package dev.imagh.pft.app.data.identity

import dev.imagh.pft.ledger.identity.Device
import dev.imagh.pft.ledger.identity.IdentityRepository
import dev.imagh.pft.ledger.identity.UserProfile
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId

/** The Room implementation of core's [IdentityRepository]: maps between core models and rows. */
class RoomIdentityRepository(
    private val dao: IdentityDao,
) : IdentityRepository {
    override suspend fun users(): List<UserProfile> = dao.users().map { it.toModel() }

    override suspend fun findUser(id: UserId): UserProfile? = dao.user(id.value)?.toModel()

    override suspend fun findDevice(id: DeviceId): Device? = dao.device(id.value)?.toModel()

    override suspend fun insertUser(user: UserProfile) =
        dao.insertUser(UserProfileEntity(user.id.value, user.displayName))

    override suspend fun insertDevice(device: Device) =
        dao.insertDevice(DeviceEntity(device.id.value, device.userId.value, device.label))

    private fun UserProfileEntity.toModel() = UserProfile(UserId(id), displayName)

    private fun DeviceEntity.toModel() = Device(DeviceId(id), UserId(userId), label)
}
