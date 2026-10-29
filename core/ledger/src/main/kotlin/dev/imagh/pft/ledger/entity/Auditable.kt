package dev.imagh.pft.ledger.entity

/**
 * Something whose creation and changes are written to the audit trail.
 *
 * The audit diff works on [auditFields] generically, so there is no reflection and no
 * kotlin-reflect: each model lists the fields it wants audited, as text.
 *
 * Implemented by every ledger entity (through [LedgerEntity]) and by the identity and settings
 * records, which carry no [EntityMeta].
 */
interface Auditable {
    /** The kind of record, recorded on each audit row: a stable lower-case name such as `account`. */
    val auditType: String

    /** The record's id as text. */
    val auditId: String

    /**
     * The audited fields as `column name -> value as text` (`null` for an unset optional field).
     *
     * Amounts as paise text, enums by name, instants as ISO text. Leave out [EntityMeta] fields:
     * the lifecycle (create, delete, archive) is recorded as the audit *action*. Keys are the
     * snake_case column names so the audit trail reads like the schema.
     */
    fun auditFields(): Map<String, String?>
}
