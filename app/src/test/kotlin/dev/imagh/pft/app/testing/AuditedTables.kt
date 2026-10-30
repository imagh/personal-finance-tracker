package dev.imagh.pft.app.testing

import dev.imagh.pft.ledger.entity.Auditable
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.identity.Device
import dev.imagh.pft.ledger.identity.UserProfile
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.ledger.settings.AppSetting
import dev.imagh.pft.money.Paise
import java.time.Instant

/**
 * One table in the audit-columns check: the table, a **fully populated** sample entity of the
 * model stored in it, and the reviewed list of its columns that are deliberately not audited.
 *
 * @param nonAudited columns that are not in `auditFields()` on purpose (the table's own key when it
 *   is not `id`, a denormalised sort key, a join table's second key...). Each needs a reason at the
 *   registration site, and a reviewer approves every new entry.
 */
class AuditedTable(
    val table: String,
    val sample: Auditable,
    val nonAudited: Set<String> = emptySet(),
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
            AuditedTable("sample_thing", SampleThing(meta, "Chai", Paise(2_500), "hot")),
        )

    /**
     * Tables that are not audited entities, with the reason: `audit_entry` is the audit trail itself
     * (immutable, so nothing audits it); `room_master_table` is Room's own identity-hash row;
     * `android_metadata` and `sqlite_sequence` are SQLite's.
     */
    val notEntityTables: Set<String> =
        setOf("audit_entry", "room_master_table", "android_metadata", "sqlite_sequence")
}
