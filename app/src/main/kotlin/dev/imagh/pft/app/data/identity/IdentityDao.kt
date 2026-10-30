package dev.imagh.pft.app.data.identity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Plain reads and inserts for `user_profile` and `device`. No logic and no update or delete: M1a
 * never edits or removes a user or device. A duplicate id aborts (Room's default conflict
 * strategy), it never replaces.
 */
@Dao
interface IdentityDao {
    /** All users in insertion order (`rowid`), so oldest first. */
    @Query("SELECT * FROM user_profile ORDER BY rowid")
    suspend fun users(): List<UserProfileEntity>

    /** The user with [id], or null. */
    @Query("SELECT * FROM user_profile WHERE id = :id")
    suspend fun user(id: String): UserProfileEntity?

    /** The device with [id], or null. */
    @Query("SELECT * FROM device WHERE id = :id")
    suspend fun device(id: String): DeviceEntity?

    /** Inserts [user]; fails if the id exists. */
    @Insert
    suspend fun insertUser(user: UserProfileEntity)

    /** Inserts [device]; fails if the id exists or its user does not. */
    @Insert
    suspend fun insertDevice(device: DeviceEntity)
}
