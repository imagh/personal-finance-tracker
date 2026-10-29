package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.identity.Device
import dev.imagh.pft.ledger.identity.IdentityRepository
import dev.imagh.pft.ledger.identity.UserProfile
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId

/** The in-memory [IdentityRepository]; part of [ledger]'s transactions. */
class InMemoryIdentityRepository(
    ledger: InMemoryLedgerStore,
) : IdentityRepository {
    private val userRows = ledger.newStore<UserProfile, UserId> { it.id }
    private val deviceRows = ledger.newStore<Device, DeviceId> { it.id }

    override suspend fun users(): List<UserProfile> = userRows.all()

    override suspend fun findUser(id: UserId): UserProfile? = userRows.get(id)

    override suspend fun findDevice(id: DeviceId): Device? = deviceRows.get(id)

    override suspend fun insertUser(user: UserProfile) = userRows.insert(user)

    override suspend fun insertDevice(device: Device) = deviceRows.insert(device)
}
