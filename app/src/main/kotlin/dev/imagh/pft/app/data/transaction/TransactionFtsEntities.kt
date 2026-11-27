package dev.imagh.pft.app.data.transaction

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

/**
 * Table `transaction_fts` (schema v6, slice C D6): the full-text index over a transaction's `title` and
 * `description`. An **external-content** FTS4 table: it stores only the index, and the text stays in
 * `ledger_transaction`. Room installs the triggers that keep it in step with the content table, so an edited
 * title re-indexes and a tombstone (an `UPDATE` of `deleted_at`) leaves the row indexed (search filters
 * tombstones on the content table instead).
 *
 * - It is a derived, unaudited index: no meta columns, and `AuditedTables.notEntityTables` lists it.
 * - `unicode61` folds case and accents on both the index and the query, and splits on everything that is not
 *   a letter, digit or Private Use character. **Devanagari combining marks (vowel signs and so on) are separators
 *   by default**, which would split "kirana" into single consonants, so [TransactionFts.DEVANAGARI_MARKS] are declared
 *   token characters (`tokenchars=`). Only Devanagari: other Indic scripts would need the same, plus a drop,
 *   recreate and `rebuild` migration.
 * - It joins to its content row on `rowid`, not on `id`: our ids are TEXT, so the content table's implicit
 *   rowid is not an alias of the key, and **`VACUUM`, `VACUUM INTO` and an export may renumber it**. After
 *   any of them run [TransactionFts.REBUILD] (data-model.md).
 * - Only core's `SearchText.toMatchTerms` output is ever matched, always as a bound parameter.
 */
@Fts4(
    contentEntity = LedgerTransactionEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
    tokenizerArgs = [TransactionFts.TOKENIZER_ARGS],
)
@Entity(tableName = "transaction_fts")
data class TransactionFtsEntity(
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val description: String?,
)

/**
 * Table `transaction_line_fts` (schema v6, slice C D6): the full-text index over a line's `note`, an
 * external-content FTS4 table over `transaction_line`, kept in step by Room's triggers. Same rules as
 * [TransactionFtsEntity]: derived and unaudited, joined on `rowid`, rebuilt after `VACUUM`.
 */
@Fts4(
    contentEntity = TransactionLineEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
    tokenizerArgs = [TransactionFts.TOKENIZER_ARGS],
)
@Entity(tableName = "transaction_line_fts")
data class TransactionLineFtsEntity(
    @ColumnInfo(name = "note") val note: String?,
)

/** The two full-text indexes and what maintains them. */
object TransactionFts {
    /**
     * The Devanagari combining marks, as the characters `unicode61` is told to treat as part of a word: the signs
     * U+0900 to U+0903, U+093A to U+093C, U+093E to U+094F, U+0951 to U+0957 and U+0962 to U+0963 (candrabindu,
     * anusvara, visarga, the vowel signs, the nukta, the virama, the stress and accent marks). The tokenizer takes a
     * list, not ranges. U+093D (avagraha) is left out: it is a letter, so `unicode61` already keeps it in a word.
     * Written as escapes so ktlint cannot rewrite them.
     * **Only Devanagari**: other Indic scripts need the same treatment, and a changed tokenizer is a drop,
     * recreate and `rebuild` migration.
     */
    const val DEVANAGARI_MARKS: String =
        "\u0900\u0901\u0902\u0903" +
            "\u093A\u093B\u093C\u093E\u093F\u0940\u0941\u0942\u0943\u0944\u0945" +
            "\u0946\u0947\u0948\u0949\u094A\u094B\u094C\u094D\u094E\u094F" +
            "\u0951\u0952\u0953\u0954\u0955\u0956\u0957" +
            "\u0962\u0963"

    /** The `tokenize=unicode61` argument that keeps [DEVANAGARI_MARKS] inside words. */
    const val TOKENIZER_ARGS: String = "tokenchars=" + DEVANAGARI_MARKS

    /**
     * The statements that rebuild both indexes from their content tables. Run them after anything that can
     * renumber a content table's `rowid` (`VACUUM`, `VACUUM INTO`, an export), or the index points at the wrong
     * rows. They are also how a migration backfills rows that existed before the index.
     */
    val REBUILD: List<String> =
        listOf(
            "INSERT INTO `transaction_fts`(`transaction_fts`) VALUES('rebuild')",
            "INSERT INTO `transaction_line_fts`(`transaction_line_fts`) VALUES('rebuild')",
        )
}
