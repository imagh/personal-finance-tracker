package dev.imagh.pft.app.data.identity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Table `user_profile`: a household member. A persistence DTO mapped one-to-one to the core
 * `UserProfile` (plan §4); core never sees it.
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "display_name")
    val displayName: String,
)

/**
 * Table `device`: one installation of the app, owned by a user. The foreign key is `RESTRICT`, so
 * a user who still has devices can never be removed (and nothing in M1a removes users at all).
 */
@Entity(
    tableName = "device",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("user_id")],
)
data class DeviceEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "label")
    val label: String,
)
