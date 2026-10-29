package dev.imagh.pft.ledger.audit

import dev.imagh.pft.ledger.entity.Auditable

/** One field's before and after, as text. `null` means the field was unset on that side. */
data class FieldChange(
    val old: String?,
    val new: String?,
)

/**
 * Computes the field-level changes between two versions of a record.
 *
 * Works on [Auditable.auditFields] so it needs no reflection. A field missing on one side counts
 * as `null`; a field that is `null` on both sides is not a change. The result is empty when
 * nothing changed, which is how a no-op edit avoids writing an audit row.
 */
object AuditDiff {
    /**
     * The changes from [old] to [new].
     *
     * - update (both given): only the fields whose text differs.
     * - create (`old == null`): every set field, with a `null` old value.
     * - delete (`new == null`): every set field, with a `null` new value.
     * - nothing on either side: no changes.
     */
    fun diff(
        old: Auditable?,
        new: Auditable?,
    ): Map<String, FieldChange> {
        val before = old?.auditFields().orEmpty()
        val after = new?.auditFields().orEmpty()
        val changes = LinkedHashMap<String, FieldChange>()
        for (key in before.keys + after.keys) {
            val was = before[key]
            val now = after[key]
            if (was != now) changes[key] = FieldChange(old = was, new = now)
        }
        return changes
    }
}
