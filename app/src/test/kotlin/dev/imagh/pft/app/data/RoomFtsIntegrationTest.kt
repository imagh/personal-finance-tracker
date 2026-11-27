package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.QueryWorld.Companion.ist
import dev.imagh.pft.app.data.QueryWorld.Companion.rs
import dev.imagh.pft.app.data.transaction.TransactionFts
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.query.LedgerEntry
import dev.imagh.pft.ledger.query.TransactionFilter
import dev.imagh.pft.ledger.transaction.LineDraft
import dev.imagh.pft.ledger.transaction.TransactionDraft
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TransactionWithLines
import dev.imagh.pft.ledger.transaction.TransferDraft
import dev.imagh.pft.money.Paise
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.YearMonth

/** A string built from code points: ktlint rewrites non-ASCII and invisible literals, which would change the test. */
private fun cps(vararg codePoints: Int) = String(codePoints, 0, codePoints.size)

/** "किराना" (kirana, a grocery): consonants with the vowel signs i (U+093F) and aa (U+093E), which are marks. */
private val kirana = cps(0x0915, 0x093F, 0x0930, 0x093E, 0x0928, 0x093E)

/** "किताब" (kitab, a book): shares its first consonant and vowel sign with [kirana]. */
private val kitab = cps(0x0915, 0x093F, 0x0924, 0x093E, 0x092C)

/** "कुरान" (Quran): same consonants k, r, n as [kirana] but other vowel signs (u, aa). */
private val kuran = cps(0x0915, 0x0941, 0x0930, 0x093E, 0x0928)

/** "न" (na): the last consonant of [kirana]; a word is not found by a letter that follows a vowel sign. */
private val na = cps(0x0928)

/** "विद्या" (vidya, knowledge): a conjunct, consonants joined by a virama (U+094D). */
private val vidya = cps(0x0935, 0x093F, 0x0926, 0x094D, 0x092F, 0x093E)

/** "या" (yaa): the end of [vidya], after its virama. */
private val yaa = cps(0x092F, 0x093E)

/** "रा" (raa): a syllable from the middle of [kirana]. */
private val raa = cps(0x0930, 0x093E)

/**
 * Full-text search through the real stack (slice C C3b, D6): the Room FTS4 indexes, their sync triggers and
 * the `text`, `titleText` and `noteText` predicates of the queries, over in-memory SQLite. Rows are written by
 * the real services so the triggers, not the test, keep the index.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomFtsIntegrationTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)
    private val w = QueryWorld(kit)
    private val november = YearMonth.of(2026, 11)

    @After
    fun closeDatabase() = database.close()

    /** One expense of Rs 100 on [day] of November, with the given texts; [note] is its single item's note. */
    private suspend fun spend(
        day: Int,
        title: String?,
        description: String? = null,
        note: String? = null,
        paise: Long = rs(100),
    ): TransactionWithLines =
        kit.transactions.create(
            TransactionDraft(
                TransactionType.EXPENSE,
                Paise(paise),
                ist(2026, 11, day),
                w.bank.meta.id,
                title = title,
                description = description,
                lines = listOf(LineDraft(amount = Paise(paise), categoryId = null, note = note)),
            ),
        )

    private suspend fun search(filter: TransactionFilter) = kit.transactionQueries.search(filter, kit.zone).entries

    private suspend fun titles(filter: TransactionFilter) = search(filter).titles()

    private fun List<LedgerEntry>.titles(): List<String?> =
        map {
            when (it) {
                is LedgerEntry.Single -> it.transaction.title
                is LedgerEntry.Transfer -> it.outgoing.title
            }
        }

    @Test
    fun `a word matches the start of a title, in any case`() =
        runSuspending {
            w.start()
            spend(1, "Swiggy order")
            spend(2, "Rent")

            titles(TransactionFilter(text = "swi")) shouldContainExactly listOf("Swiggy order")
            titles(TransactionFilter(text = "SWIGGY")) shouldContainExactly listOf("Swiggy order")
            titles(TransactionFilter(text = "wiggy")).shouldBeEmpty()
        }

    @Test
    fun `an item note is found`() =
        runSuspending {
            w.start()
            spend(1, "Stationery", note = "Notebook for school")
            spend(2, "Other", note = "Pens")

            titles(TransactionFilter(text = "notebook")) shouldContainExactly listOf("Stationery")
        }

    @Test
    fun `the description is found`() =
        runSuspending {
            w.start()
            spend(1, "Dinner", description = "Birthday at the terrace")
            spend(2, "Lunch", description = "Office")

            titles(TransactionFilter(text = "terrace")) shouldContainExactly listOf("Dinner")
        }

    @Test
    fun `all words must match, in any order`() =
        runSuspending {
            w.start()
            spend(1, "Swiggy order")
            spend(2, "Swiggy")
            spend(3, "Order from Amazon")

            titles(TransactionFilter(text = "swiggy order")) shouldContainExactly listOf("Swiggy order")
            titles(TransactionFilter(text = "order swiggy")) shouldContainExactly listOf("Swiggy order")
        }

    @Test
    fun `each word may match in a different place of its scope`() =
        runSuspending {
            w.start()
            spend(1, "Swiggy", note = "biryani")
            spend(2, "Swiggy", description = "lunch", note = "biryani")
            spend(3, "Swiggy", note = "dal")
            spend(4, "Zomato", description = "biryani")

            titles(TransactionFilter(text = "swiggy biryani")) shouldContainExactly listOf("Swiggy", "Swiggy")
            titles(TransactionFilter(text = "biryani swiggy")) shouldContainExactly listOf("Swiggy", "Swiggy")
            titles(TransactionFilter(text = "swiggy lunch biryani")) shouldContainExactly listOf("Swiggy")
            titles(TransactionFilter(text = "zomato biryani")) shouldContainExactly listOf("Zomato")
            titles(TransactionFilter(text = "swiggy zomato")).shouldBeEmpty()
        }

    @Test
    fun `a title only search does not take a word from an item note`() =
        runSuspending {
            w.start()
            spend(1, "Swiggy", note = "biryani")

            titles(TransactionFilter(titleText = "swiggy biryani")).shouldBeEmpty()
            titles(TransactionFilter(titleText = "swiggy")) shouldContainExactly listOf("Swiggy")
        }

    @Test
    fun `a notes search takes each word from the description or an item note, never the title`() =
        runSuspending {
            w.start()
            spend(1, "Rent", description = "monthly", note = "flat")
            spend(2, "monthly", note = "flat")

            titles(TransactionFilter(noteText = "monthly flat")) shouldContainExactly listOf("Rent")
        }

    @Test
    fun `a title only search skips the description and the notes`() =
        runSuspending {
            w.start()
            spend(1, "Rent", description = "monthly rent")
            spend(2, "Other", description = "rent")
            spend(3, "Third", note = "rent")

            titles(TransactionFilter(titleText = "rent")) shouldContainExactly listOf("Rent")
            titles(TransactionFilter(text = "rent")) shouldContainExactly listOf("Third", "Other", "Rent")
        }

    @Test
    fun `a notes search is the description or an item note, not the title`() =
        runSuspending {
            w.start()
            spend(1, "Rent", description = "monthly")
            spend(2, "Other", description = "rent")
            spend(3, "Third", note = "rent")
            spend(4, "rent")

            titles(TransactionFilter(noteText = "rent")) shouldContainExactly listOf("Third", "Other")
        }

    @Test
    fun `the text filters are ANDed with each other`() =
        runSuspending {
            w.start()
            spend(1, "Swiggy", description = "lunch", note = "dal")
            spend(2, "Swiggy", description = "dinner", note = "dal")
            spend(3, "Zomato", description = "lunch", note = "dal")

            titles(TransactionFilter(titleText = "swiggy", noteText = "lunch")) shouldContainExactly listOf("Swiggy")
            titles(TransactionFilter(text = "dal", titleText = "zomato", noteText = "lunch")) shouldContainExactly
                listOf("Zomato")
            titles(TransactionFilter(titleText = "zomato", noteText = "dinner")).shouldBeEmpty()
        }

    @Test
    fun `text is ANDed with the other filters and the month totals follow it`() =
        runSuspending {
            w.start()
            spend(1, "Swiggy", paise = rs(100))
            spend(2, "Swiggy", paise = rs(250))
            spend(3, "Zomato", paise = rs(900))
            kit.transactions.create(
                TransactionDraft(
                    TransactionType.EXPENSE,
                    Paise(rs(40)),
                    ist(2026, 11, 4),
                    w.card.meta.id,
                    title = "Swiggy",
                    lines = listOf(LineDraft(amount = Paise(rs(40)), categoryId = null)),
                ),
            )

            val page =
                kit.transactionQueries.month(
                    november,
                    kit.zone,
                    TransactionFilter(text = "swiggy", accountIds = setOf(w.bank.meta.id)),
                )

            page.entries.titles() shouldContainExactly listOf("Swiggy", "Swiggy")
            page.totals.expense shouldBe Paise(rs(350))
        }

    @Test
    fun `a transfer is one entry when its text matches, never cut`() =
        runSuspending {
            w.start()
            kit.transfers.create(
                TransferDraft(
                    w.bank.meta.id,
                    w.savings.meta.id,
                    Paise(rs(500)),
                    ist(2026, 11, 5),
                    title = "Rent to savings",
                ),
            )
            spend(6, "Other")

            val entries = search(TransactionFilter(text = "rent"))

            entries.size shouldBe 1
            val transfer = entries.single() as LedgerEntry.Transfer
            transfer.outgoing.title shouldBe "Rent to savings"
            transfer.incoming.title shouldBe "Rent to savings"
            kit.transactionQueries
                .month(november, kit.zone, TransactionFilter(text = "rent"))
                .entries
                .size shouldBe 1
        }

    @Test
    fun `a tombstoned transaction does not match, and does again when restored`() =
        runSuspending {
            w.start()
            val gone = spend(1, "Swiggy order", note = "notebook")
            spend(2, "Swiggy lunch")

            kit.transactions.delete(gone.transaction.meta.id)

            titles(TransactionFilter(text = "swiggy")) shouldContainExactly listOf("Swiggy lunch")
            titles(TransactionFilter(text = "notebook")).shouldBeEmpty()
            titles(TransactionFilter(titleText = "swiggy")) shouldContainExactly listOf("Swiggy lunch")
            titles(TransactionFilter(noteText = "notebook")).shouldBeEmpty()

            kit.transactions.restore(gone.transaction.meta.id)

            titles(TransactionFilter(text = "notebook")) shouldContainExactly listOf("Swiggy order")
        }

    @Test
    fun `a tombstoned line's note does not match while its transaction stays`() =
        runSuspending {
            w.start()
            val two =
                kit.transactions.create(
                    TransactionDraft(
                        TransactionType.EXPENSE,
                        Paise(rs(100)),
                        ist(2026, 11, 1),
                        w.bank.meta.id,
                        title = "Shopping",
                        lines =
                            listOf(
                                LineDraft(amount = Paise(rs(60)), categoryId = null, note = "bread"),
                                LineDraft(amount = Paise(rs(40)), categoryId = null, note = "notebook"),
                            ),
                    ),
                )
            titles(TransactionFilter(text = "notebook")) shouldContainExactly listOf("Shopping")
            val bread = two.lines.single { it.note == "bread" }

            kit.transactions.edit(
                two.transaction.meta.id,
                TransactionDraft(
                    TransactionType.EXPENSE,
                    Paise(rs(100)),
                    ist(2026, 11, 1),
                    w.bank.meta.id,
                    title = "Shopping",
                    lines =
                        listOf(
                            LineDraft(id = bread.meta.id, amount = Paise(rs(100)), categoryId = null, note = "bread"),
                        ),
                ),
            )

            titles(TransactionFilter(text = "notebook")).shouldBeEmpty()
            titles(TransactionFilter(noteText = "notebook")).shouldBeEmpty()
            titles(TransactionFilter(text = "bread")) shouldContainExactly listOf("Shopping")
        }

    @Test
    fun `an edited title and note are re-indexed`() =
        runSuspending {
            w.start()
            val saved = spend(1, "Swiggy order", note = "notebook")
            val line = saved.lines.single()

            kit.transactions.edit(
                saved.transaction.meta.id,
                TransactionDraft(
                    TransactionType.EXPENSE,
                    Paise(rs(100)),
                    ist(2026, 11, 1),
                    w.bank.meta.id,
                    title = "Zomato order",
                    lines =
                        listOf(
                            LineDraft(id = line.meta.id, amount = Paise(rs(100)), categoryId = null, note = "pencil"),
                        ),
                ),
            )

            titles(TransactionFilter(text = "swiggy")).shouldBeEmpty()
            titles(TransactionFilter(text = "notebook")).shouldBeEmpty()
            titles(TransactionFilter(text = "zomato")) shouldContainExactly listOf("Zomato order")
            titles(TransactionFilter(text = "pencil")) shouldContainExactly listOf("Zomato order")
        }

    @Test
    fun `a Devanagari word with vowel signs matches whole and by prefix, and another word does not`() =
        runSuspending {
            w.start()
            spend(1, kirana)
            spend(2, kitab)
            spend(3, "Rent", note = kirana)

            titles(TransactionFilter(titleText = kirana)) shouldContainExactly listOf(kirana)
            titles(TransactionFilter(titleText = kirana.substring(0, 4))) shouldContainExactly listOf(kirana)
            titles(TransactionFilter(text = kirana)) shouldContainExactly listOf("Rent", kirana)
            titles(TransactionFilter(text = kitab)) shouldContainExactly listOf(kitab)
        }

    @Test
    fun `a Devanagari word is one token, so other vowel signs and a mid-word syllable do not match`() =
        runSuspending {
            w.start()
            spend(1, kirana)
            spend(2, "Rent", note = kirana)
            spend(3, vidya)

            // A line's note is indexed by the other table, which has the same tokenizer.
            titles(TransactionFilter(noteText = kuran)).shouldBeEmpty()
            titles(TransactionFilter(noteText = raa)).shouldBeEmpty()
            titles(TransactionFilter(noteText = kirana)) shouldContainExactly listOf("Rent")
            // A vowel sign and a virama are part of the word: its later letters do not find it on their own.
            titles(TransactionFilter(titleText = na)).shouldBeEmpty()
            titles(TransactionFilter(titleText = yaa)).shouldBeEmpty()
            titles(TransactionFilter(titleText = vidya)) shouldContainExactly listOf(vidya)
            titles(TransactionFilter(titleText = kuran)).shouldBeEmpty()
            titles(TransactionFilter(titleText = raa)).shouldBeEmpty()
            titles(TransactionFilter(text = kuran)).shouldBeEmpty()
            titles(TransactionFilter(titleText = cps(0x0915, 0x093F, 0x0930, 0x093E))) shouldContainExactly
                listOf(kirana)
        }

    @Test
    fun `hostile text neither throws nor matches everything`() =
        runSuspending {
            w.start()
            spend(1, "secret", description = "plain")
            spend(2, "other", description = "secret")
            spend(3, "it's", note = "x*y")
            val nul = cps(0)
            val hostile =
                listOf(
                    "'",
                    "\"",
                    "*",
                    "NEAR(",
                    nul,
                    "title:",
                    "\\",
                    "\"a\" OR \"b",
                    "a OR b",
                    "(",
                    ")",
                    "^",
                    "{",
                    "}",
                    "NEAR/3",
                )

            for (text in hostile) {
                titles(TransactionFilter(text = text)).size shouldBe 0
                titles(TransactionFilter(titleText = text)).size shouldBe 0
                titles(TransactionFilter(noteText = text)).size shouldBe 0
            }
            // A typed column filter is two literal words, not a filter on the title.
            titles(TransactionFilter(text = "title:secret")).shouldBeEmpty()
            titles(TransactionFilter(text = "description:secret")).shouldBeEmpty()
            // Typed operators are words: "x*y" is the words x and y, and "OR" matches no title here.
            titles(TransactionFilter(text = "x*y")) shouldContainExactly listOf("it's")
            titles(TransactionFilter(text = "secret OR plain")).shouldBeEmpty()
        }

    @Test
    fun `text that holds no word searches for nothing, never everything`() =
        runSuspending {
            w.start()
            spend(1, "Swiggy")
            spend(2, "Rent")

            titles(TransactionFilter()) shouldContainExactly listOf("Rent", "Swiggy")
            titles(TransactionFilter(text = null)) shouldContainExactly listOf("Rent", "Swiggy")
            for (text in listOf("", "   ", "***", "\"\"", "()")) {
                titles(TransactionFilter(text = text)).shouldBeEmpty()
                titles(TransactionFilter(titleText = text)).shouldBeEmpty()
                titles(TransactionFilter(noteText = text)).shouldBeEmpty()
            }
            val page = kit.transactionQueries.month(november, kit.zone, TransactionFilter(text = "***"))
            page.entries.shouldBeEmpty()
            page.totals.expense shouldBe Paise(0)
        }

    @Test
    fun `an empty text filter with a real one still searches for nothing`() =
        runSuspending {
            w.start()
            spend(1, "Swiggy")

            titles(TransactionFilter(text = "swiggy", noteText = "***")).shouldBeEmpty()
        }

    @Test
    fun `a rebuild after VACUUM keeps every match`() =
        runSuspending {
            w.start()
            spend(1, "Swiggy order", note = "notebook")
            val dropped = spend(2, "Dropped", note = "scrapped")
            spend(3, "Rent")
            indexRows("transaction_fts", "\"dropped*\"") shouldBe 1
            indexRows("transaction_line_fts", "\"scrapped*\"") shouldBe 1
            removeForGood(dropped.transaction.meta.id)

            // Read from the indexes themselves: a search joins to the content tables, which no longer hold the
            // row, so it would hide an entry the delete triggers left behind.
            indexRows("transaction_fts", "\"dropped*\"") shouldBe 0
            indexRows("transaction_line_fts", "\"scrapped*\"") shouldBe 0

            vacuumAndRebuild()

            titles(TransactionFilter(text = "swiggy")) shouldContainExactly listOf("Swiggy order")
            titles(TransactionFilter(text = "notebook")) shouldContainExactly listOf("Swiggy order")
            titles(TransactionFilter(text = "rent")) shouldContainExactly listOf("Rent")
            titles(TransactionFilter(text = "dropped")).shouldBeEmpty()
            indexRows("transaction_fts", "\"dropped*\"") shouldBe 0
        }

    @Test
    fun `a rebuild restores an index that no longer matches its content`() =
        runSuspending {
            w.start()
            spend(1, "Swiggy order", note = "notebook")
            val db = database.openHelper.writableDatabase
            db.execSQL("DELETE FROM transaction_fts")
            db.execSQL("DELETE FROM transaction_line_fts")
            titles(TransactionFilter(text = "swiggy")).shouldBeEmpty()
            titles(TransactionFilter(text = "notebook")).shouldBeEmpty()

            TransactionFts.REBUILD.forEach(db::execSQL)

            titles(TransactionFilter(text = "swiggy")) shouldContainExactly listOf("Swiggy order")
            titles(TransactionFilter(text = "notebook")) shouldContainExactly listOf("Swiggy order")
        }

    /** Removes a transaction and its lines by SQL, as nothing in the app does, so a rowid gap exists to vacuum. */
    private fun removeForGood(id: TransactionId) {
        val db = database.openHelper.writableDatabase
        db.execSQL("DELETE FROM transaction_line WHERE transaction_id = ?", arrayOf(id.value))
        db.execSQL("DELETE FROM ledger_transaction WHERE id = ?", arrayOf(id.value))
    }

    /** How many rows [index] holds for [query], read from the index alone (no join to its content table). */
    private fun indexRows(
        index: String,
        query: String,
    ): Int =
        database.openHelper.writableDatabase
            .query("SELECT COUNT(*) FROM $index WHERE $index MATCH ?", arrayOf(query))
            .use {
                it.moveToFirst()
                it.getInt(0)
            }

    private fun vacuumAndRebuild() {
        val db = database.openHelper.writableDatabase
        db.execSQL("VACUUM")
        TransactionFts.REBUILD.forEach(db::execSQL)
    }
}
