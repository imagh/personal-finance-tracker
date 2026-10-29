package dev.imagh.pft.ledger.identity

import dev.imagh.pft.ledger.entity.Auditable
import dev.imagh.pft.ledger.ids.UserId

/**
 * A member of the household (the app has two users). Every record is attributed to one
 * (`createdByUserId`, audit `userId`). Created on first run; M1a has no way to edit or remove one.
 */
data class UserProfile(
    val id: UserId,
    val displayName: String,
) : Auditable {
    override val auditType: String get() = "user_profile"
    override val auditId: String get() = id.value

    override fun auditFields(): Map<String, String?> = mapOf("display_name" to displayName)
}
