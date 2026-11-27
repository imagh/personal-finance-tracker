package dev.imagh.pft.app.testing

import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.SystemCategory
import dev.imagh.pft.ledger.entity.Auditable
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.identity.Device
import dev.imagh.pft.ledger.identity.UserProfile
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.ledger.settings.AppSetting
import dev.imagh.pft.ledger.tag.Tag
import dev.imagh.pft.ledger.transaction.Direction
import dev.imagh.pft.ledger.transaction.LedgerTransaction
import dev.imagh.pft.ledger.transaction.TransactionLine
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.TxnSource
import dev.imagh.pft.ledger.transaction.TxnState
import dev.imagh.pft.money.Paise
import java.time.Instant

/**
 * One table in the audit-columns check: the table, a **fully populated** sample entity of the
 * model stored in it, and the reviewed list of its columns that are deliberately not audited.
 *
 * @param nonAudited columns that are not in `auditFields()` on purpose (the table's own key when it
 *   is not `id`, a denormalised sort key, a join table's second key...). Each needs a reason at the
 *   registration site, and a reviewer approves every new entry.
 * @param keysStoredIn audited keys that have no column in [table] because the value is held in another
 *   table, as audited key to that table's name (a line's `tag_ids` is held in `transaction_line_tag`).
 *   [AuditColumnsCheck] accepts each only when that table exists and is in
 *   [AuditedTables.notEntityTables], and the sample audits the key.
 */
class AuditedTable(
    val table: String,
    val sample: Auditable,
    val nonAudited: Set<String> = emptySet(),
    val keysStoredIn: Map<String, String> = emptyMap(),
)

/**
 * The registry the audit-columns check runs over. **Every slice that adds an entity table adds a
 * line here** (B1: `account`, and so on), with a fully populated sample; the check fails for a table
 * that is neither registered nor in [notEntityTables].
 */
object AuditedTables {
    private val at: Instant = Instant.parse("2026-11-01T10:00:00Z")
    private val meta =
        EntityMeta(SampleId("s-1"), at, at, UserId("user-1"), DeviceId("device-1"))

    /** Every entity table, with a fully populated sample. */
    val all: List<AuditedTable> =
        listOf(
            AuditedTable("user_profile", UserProfile(UserId("user-1"), "Akash")),
            AuditedTable("device", Device(DeviceId("device-1"), UserId("user-1"), "Pixel 8")),
            // `key` is the setting's identifier (its auditId), not an audited value; `updated_at` is
            // a meta column and the audit entry's own timestamp covers it.
            AuditedTable("app_setting", AppSetting("auto_lock_seconds", "60", at), nonAudited = setOf("key")),
            // `currency` is always INR and not part of the model (plan D4): the mapper writes it and fails
            // closed on anything else, so there is no value to audit.
            AuditedTable(
                "account",
                Account(
                    EntityMeta(AccountId("acc-1"), at, at, UserId("user-1"), DeviceId("device-1")),
                    "HDFC Savings",
                    AccountType.BANK,
                    Paise(-250_000),
                    "1234",
                    "HDFC",
                ),
                nonAudited = setOf("currency"),
            ),
            // Every column of `category` is audited, none allowlisted. The sample sets every optional
            // field (parent, icon, system key) so a column that were audited only when set cannot hide.
            AuditedTable(
                "category",
                Category(
                    EntityMeta(CategoryId("cat-1"), at, at, UserId("user-1"), DeviceId("device-1")),
                    parentId = CategoryId("cat-0"),
                    kind = CategoryKind.EXPENSE,
                    name = "Groceries",
                    colour = CategoryColour.CORAL,
                    iconKey = CategoryIcon.Symbol("grocery"),
                    sortOrder = 2,
                    systemKey = SystemCategory.LOAN_EMI,
                ),
            ),
            // The one business column of `tag` is its name, audited; no allowlist entry.
            AuditedTable(
                "tag",
                Tag(EntityMeta(TagId("tag-1"), at, at, UserId("user-1"), DeviceId("device-1")), "Travel"),
            ),
            // Every column of `ledger_transaction` is audited, none allowlisted. The sample sets every
            // optional field so a column that were audited only when set cannot hide. It has no category
            // or tag column: classification lives on its lines.
            AuditedTable(
                "ledger_transaction",
                LedgerTransaction(
                    EntityMeta(TransactionId("txn-1"), at, at, UserId("user-1"), DeviceId("device-1")),
                    type = TransactionType.TRANSFER,
                    direction = Direction.OUT,
                    amount = Paise(123_450),
                    occurredAt = at,
                    accountId = AccountId("acc-1"),
                    title = "Chai Point",
                    description = "With friends",
                    state = TxnState.UNVERIFIED,
                    source = TxnSource.SMS,
                    bankRef = "UTR123456",
                    sourceMessageId = "msg-9",
                    accountFragment = "4321",
                    transferGroupId = "group-1",
                ),
            ),
            // `tag_ids` has no column: a line's tags are rows of `transaction_line_tag`, a listed non-entity
            // table, and the audit trail records them as the sorted ids joined with `,` (IT §4).
            AuditedTable(
                "transaction_line",
                TransactionLine(
                    EntityMeta(TransactionLineId("line-1"), at, at, UserId("user-1"), DeviceId("device-1")),
                    transactionId = TransactionId("txn-1"),
                    position = 1,
                    amount = Paise(50_000),
                    categoryId = CategoryId("cat-1"),
                    note = "Snacks",
                    tagIds = setOf(TagId("tag-b"), TagId("tag-a")),
                ),
                keysStoredIn = mapOf("tag_ids" to "transaction_line_tag"),
            ),
            AuditedTable("sample_thing", SampleThing(meta, "Chai", Paise(2_500), "hot")),
        )

    /**
     * Tables that are not audited entities, with the reason: `audit_entry` is the audit trail itself
     * (immutable, so nothing audits it); `room_master_table` is Room's own identity-hash row;
     * `android_metadata` and `sqlite_sequence` are SQLite's; `transaction_line_tag` is the join table of a
     * line's tags: it has no meta columns and no row of its own to audit, and its content is audited as the
     * line's `tag_ids` (see [AuditedTable.keysStoredIn]).
     *
     * The full-text indexes (slice C D6) are derived, unaudited tables: `transaction_fts` and
     * `transaction_line_fts` hold only a searchable copy of text that `ledger_transaction` and
     * `transaction_line` already store and audit, with no meta columns and no row of their own. FTS4 also
     * creates a shadow table per index for its segments: `_segments` (the index blocks), `_segdir` (their
     * directory), `_docsize` (document sizes) and `_stat` (statistics). They are listed one by one, with no
     * name pattern, so a table named like one cannot slip through unaccounted.
     */
    val notEntityTables: Set<String> =
        setOf(
            "audit_entry",
            "room_master_table",
            "android_metadata",
            "sqlite_sequence",
            "transaction_line_tag",
            "transaction_fts",
            "transaction_fts_segments",
            "transaction_fts_segdir",
            "transaction_fts_docsize",
            "transaction_fts_stat",
            "transaction_line_fts",
            "transaction_line_fts_segments",
            "transaction_line_fts_segdir",
            "transaction_line_fts_docsize",
            "transaction_line_fts_stat",
        )

    /**
     * Room's eight full-text content-sync triggers (slice C D6): before update, before delete, after update and
     * after insert, on each of the two content tables. They sit in `sqlite_master` beside the audit triggers, so a
     * test that lists **every** trigger expects `AuditTriggers.TRIGGER_NAMES + ftsSyncTriggers`. The instrumented
     * tests keep their own copy (`FtsSyncTriggers`): `androidTest` cannot see this source set.
     */
    val ftsSyncTriggers: Set<String> =
        listOf("transaction_fts", "transaction_line_fts")
            .flatMap { index ->
                listOf("BEFORE_UPDATE", "BEFORE_DELETE", "AFTER_UPDATE", "AFTER_INSERT")
                    .map { "room_fts_content_sync_${index}_$it" }
            }.toSet()
}
