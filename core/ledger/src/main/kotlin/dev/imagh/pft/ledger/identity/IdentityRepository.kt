package dev.imagh.pft.ledger.identity

import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId

/**
 * Persistence of users and devices. Defined in core, implemented with Room in `app` (a swap
 * boundary, plan §3.2 decision 13). Which device is *this* one is an app setting, not stored here.
 */
interface IdentityRepository {
    /** All users, oldest first. */
    suspend fun users(): List<UserProfile>

    /** The user with [id], or null. */
    suspend fun findUser(id: UserId): UserProfile?

    /** The device with [id], or null. */
    suspend fun findDevice(id: DeviceId): Device?

    /** Stores a new user. */
    suspend fun insertUser(user: UserProfile)

    /** Stores a new device. */
    suspend fun insertDevice(device: Device)
}
