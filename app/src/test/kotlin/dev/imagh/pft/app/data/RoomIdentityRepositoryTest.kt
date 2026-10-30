package dev.imagh.pft.app.data

import android.database.sqlite.SQLiteConstraintException
import dev.imagh.pft.app.data.identity.RoomIdentityRepository
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.identity.Device
import dev.imagh.pft.ledger.identity.UserProfile
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** [RoomIdentityRepository] against a real in-memory database. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomIdentityRepositoryTest {
    private val database = TestDatabase.create()
    private val repository = RoomIdentityRepository(database.identityDao())
    private val akash = UserProfile(UserId("user-1"), "Akash")
    private val priya = UserProfile(UserId("user-2"), "Priya")

    @After
    fun closeDatabase() = database.close()

    @Test
    fun `a stored user is found by id with every field`() =
        runSuspending {
            repository.insertUser(akash)

            repository.findUser(akash.id) shouldBe akash
        }

    @Test
    fun `an unknown user is null`() =
        runSuspending {
            repository.findUser(UserId("missing")) shouldBe null
        }

    @Test
    fun `users are listed oldest first`() =
        runSuspending {
            // Inserted out of id order, so this fails if the list is sorted by id.
            repository.insertUser(priya)
            repository.insertUser(akash)

            repository.users() shouldBe listOf(priya, akash)
        }

    @Test
    fun `inserting a user twice fails instead of replacing`() =
        runSuspending {
            repository.insertUser(akash)

            shouldThrow<SQLiteConstraintException> { repository.insertUser(akash.copy(displayName = "Other")) }

            repository.findUser(akash.id) shouldBe akash
        }

    @Test
    fun `a stored device is found by id`() =
        runSuspending {
            val pixel = Device(DeviceId("device-1"), akash.id, "Pixel 8")
            repository.insertUser(akash)
            repository.insertDevice(pixel)

            repository.findDevice(pixel.id) shouldBe pixel
            repository.findDevice(DeviceId("missing")) shouldBe null
        }

    @Test
    fun `a device for an unknown user is rejected`() =
        runSuspending {
            shouldThrow<SQLiteConstraintException> {
                repository.insertDevice(Device(DeviceId("device-1"), UserId("nobody"), "Pixel 8"))
            }
        }
}
