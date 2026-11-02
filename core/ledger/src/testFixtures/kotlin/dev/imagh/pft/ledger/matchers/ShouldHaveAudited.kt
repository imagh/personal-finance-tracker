package dev.imagh.pft.ledger.matchers

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

// The long parameter lists are deliberate: every filter after `action` is optional, so a test names
// only what it cares about (`shouldHaveAudited("account", id, CREATE, userId = ...)`).

/**
 * A Kotest matcher that holds when the audit trail contains an entry for the given record and
 * action. Only the arguments you pass are compared: [changes], [userId], [deviceId] and [source]
 * are checked when non-null.
 */
@Suppress("LongParameterList")
fun haveAudited(
    entityType: String,
    entityId: String,
    action: AuditAction,
    changes: Map<String, FieldChange>? = null,
    userId: UserId? = null,
    deviceId: DeviceId? = null,
    source: AuditSource? = null,
): Matcher<Collection<AuditEntry>> =
    Matcher { trail ->
        val found =
            trail.any {
                it.entityType == entityType &&
                    it.entityId == entityId &&
                    it.action == action &&
                    (changes == null || it.changes == changes) &&
                    (userId == null || it.userId == userId) &&
                    (deviceId == null || it.deviceId == deviceId) &&
                    (source == null || it.source == source)
            }
        val wanted = "$action of $entityType $entityId"
        MatcherResult(
            found,
            { "Audit trail should contain $wanted matching the given expectations, but has: ${describe(trail)}" },
            { "Audit trail should not contain $wanted matching the given expectations" },
        )
    }

private fun describe(trail: Collection<AuditEntry>): String =
    if (trail.isEmpty()) "no entries" else trail.joinToString { "${it.action} ${it.entityType} ${it.entityId}" }

/** Asserts the trail contains the described entry (see [haveAudited]). */
@Suppress("LongParameterList")
fun Collection<AuditEntry>.shouldHaveAudited(
    entityType: String,
    entityId: String,
    action: AuditAction,
    changes: Map<String, FieldChange>? = null,
    userId: UserId? = null,
    deviceId: DeviceId? = null,
    source: AuditSource? = null,
) = this should haveAudited(entityType, entityId, action, changes, userId, deviceId, source)

/** Asserts the trail does **not** contain the described entry. */
@Suppress("LongParameterList")
fun Collection<AuditEntry>.shouldNotHaveAudited(
    entityType: String,
    entityId: String,
    action: AuditAction,
    changes: Map<String, FieldChange>? = null,
    userId: UserId? = null,
    deviceId: DeviceId? = null,
    source: AuditSource? = null,
) = this shouldNot haveAudited(entityType, entityId, action, changes, userId, deviceId, source)
