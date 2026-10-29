package dev.imagh.pft.ledger.ids

/**
 * An entity's identifier: a UUID in 36-character text form (UUIDv7 from [Uuid7], or a
 * deterministic one from [NameBasedId]).
 *
 * Every entity has its own `@JvmInline` id type so ids of different entities cannot be mixed up
 * at compile time (an `AccountId` cannot be passed where a `UserId` is expected). Slices add
 * their own id types next to the ones the foundation needs (`AccountId`, `TransactionId`, ...).
 */
interface EntityId {
    /** The id as stored: UUID text. */
    val value: String
}

/** Identifies a [dev.imagh.pft.ledger.identity.UserProfile] (a household member). */
@JvmInline
value class UserId(
    override val value: String,
) : EntityId {
    init {
        require(value.isNotBlank()) { "UserId must not be blank" }
    }
}

/** Identifies a [dev.imagh.pft.ledger.identity.Device] (one installation of the app). */
@JvmInline
value class DeviceId(
    override val value: String,
) : EntityId {
    init {
        require(value.isNotBlank()) { "DeviceId must not be blank" }
    }
}

/** Identifies one row of the audit trail. */
@JvmInline
value class AuditEntryId(
    override val value: String,
) : EntityId {
    init {
        require(value.isNotBlank()) { "AuditEntryId must not be blank" }
    }
}
