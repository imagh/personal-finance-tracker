package dev.imagh.pft.app.data.audit

import dev.imagh.pft.app.data.common.EpochMillis
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.ids.AuditEntryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import org.json.JSONException
import org.json.JSONObject
import java.time.Instant

/**
 * Maps a core [AuditEntry] to its [AuditEntryEntity] row and back.
 *
 * `changes_json` is `{"<field>":{"old":<text or null>,"new":<text or null>}, ...}` in field order,
 * serialised with the platform's `org.json` so core stays dependency-free. Unset sides are JSON
 * `null`, which is different from the text `"null"`. The shape is a contract: rows written by one
 * device are read by the other once sync exists, so change it only with a versioned migration.
 * Time goes through [EpochMillis]: epoch milliseconds, exactly; a finer instant is refused.
 *
 * Reading fails closed: a row that cannot be parsed throws an [IllegalStateException] whose message
 * holds no row content (an audit row may contain names and amounts, CLAUDE.md §3.7).
 */
object AuditEntryMapper {
    private const val OLD = "old"
    private const val NEW = "new"

    /** The row for [entry]. */
    fun toEntity(entry: AuditEntry): AuditEntryEntity =
        AuditEntryEntity(
            id = entry.id.value,
            entityType = entry.entityType,
            entityId = entry.entityId,
            action = entry.action.name,
            changesJson = encode(entry.changes),
            userId = entry.userId.value,
            deviceId = entry.deviceId.value,
            source = entry.source.name,
            at = EpochMillis.of(entry.at),
        )

    /**
     * The entry stored in [row].
     *
     * @throws IllegalStateException if the row is not a valid audit entry.
     */
    fun toModel(row: AuditEntryEntity): AuditEntry =
        try {
            AuditEntry(
                id = AuditEntryId(row.id),
                entityType = row.entityType,
                entityId = row.entityId,
                action = AuditAction.valueOf(row.action),
                changes = decode(row.changesJson),
                userId = UserId(row.userId),
                deviceId = DeviceId(row.deviceId),
                source = AuditSource.valueOf(row.source),
                at = Instant.ofEpochMilli(row.at),
            )
        } catch (_: JSONException) {
            throw unreadable()
        } catch (_: IllegalArgumentException) {
            throw unreadable()
        }

    private fun unreadable() = IllegalStateException("An audit_entry row is unreadable")

    private fun encode(changes: Map<String, FieldChange>): String {
        val root = JSONObject()
        for ((field, change) in changes) {
            val pair = JSONObject()
            pair.put(OLD, change.old ?: JSONObject.NULL)
            pair.put(NEW, change.new ?: JSONObject.NULL)
            root.put(field, pair)
        }
        return root.toString()
    }

    private fun decode(json: String): Map<String, FieldChange> {
        val root = JSONObject(json)
        val changes = LinkedHashMap<String, FieldChange>()
        for (field in root.keys()) {
            val pair = root.getJSONObject(field)
            changes[field] = FieldChange(old = pair.textOrNull(OLD), new = pair.textOrNull(NEW))
        }
        return changes
    }

    /** The text under [name], null for JSON null; anything else (a number, an object) is invalid. */
    private fun JSONObject.textOrNull(name: String): String? =
        when (val value = get(name)) {
            JSONObject.NULL -> null
            is String -> value
            else -> throw JSONException("not text")
        }
}
