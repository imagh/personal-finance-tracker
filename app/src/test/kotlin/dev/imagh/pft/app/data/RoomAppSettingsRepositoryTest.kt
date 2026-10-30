package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.settings.RoomAppSettingsRepository
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.settings.AppSetting
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant

/** [RoomAppSettingsRepository] against a real in-memory database. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomAppSettingsRepositoryTest {
    private val database = TestDatabase.create()
    private val repository = RoomAppSettingsRepository(database.appSettingDao())
    private val at = Instant.parse("2026-11-01T10:00:00Z")

    @After
    fun closeDatabase() = database.close()

    @Test
    fun `a stored setting is read back with its value and time`() =
        runSuspending {
            val setting = AppSetting("auto_lock_seconds", "60", at)

            repository.put(setting)

            repository.get("auto_lock_seconds") shouldBe setting
        }

    @Test
    fun `an unknown key is null`() =
        runSuspending {
            repository.get("missing") shouldBe null
        }

    @Test
    fun `putting an existing key replaces its value and time`() =
        runSuspending {
            repository.put(AppSetting("auto_lock_seconds", "60", at))
            val later = AppSetting("auto_lock_seconds", "120", at.plusSeconds(5))

            repository.put(later)

            repository.get("auto_lock_seconds") shouldBe later
        }

    @Test
    fun `settings are independent per key`() =
        runSuspending {
            repository.put(AppSetting("a", "1", at))
            repository.put(AppSetting("b", "2", at))

            repository.get("a")?.value shouldBe "1"
            repository.get("b")?.value shouldBe "2"
        }
}
