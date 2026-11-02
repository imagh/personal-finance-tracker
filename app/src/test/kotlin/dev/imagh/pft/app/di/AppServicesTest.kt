package dev.imagh.pft.app.di

import dev.imagh.pft.app.data.common.EpochMillis
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.fakes.TestClock
import dev.imagh.pft.ledger.settings.SettingKeys
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant

/**
 * [AppServices] is the production object graph: core services over a real Room database, on the
 * millisecond clock the app gives core (the A4a criterion: what is written equals what is read).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AppServicesTest {
    private val database = TestDatabase.create()

    /** A clock finer than a millisecond: raw, it would be refused by the Room mapping. */
    private val testClock = TestClock(Instant.parse("2026-11-01T10:00:00.123456789Z"))
    private val services = AppServices(database, EpochMillis.clock(testClock))

    @After
    fun closeDatabase() = database.close()

    @Test
    fun `first-run setup works end to end and is read back as the same identity`() =
        runSuspending {
            services.identity.currentIdentity() shouldBe null

            val created = services.identity.initialise("Akash", "Pixel")

            services.identity.currentIdentity() shouldBe created
            services.identityRepository.users() shouldBe listOf(created.user)
            services.settingsRepository.get(SettingKeys.CURRENT_DEVICE_ID)?.value shouldBe created.device.id.value
        }

    @Test
    fun `on a sub-millisecond base clock all three audit rows carry the whole-millisecond instant`() =
        runSuspending {
            services.identity.initialise("Akash", "Pixel")

            val times = auditTimes()
            times.size shouldBe 3
            times.toSet() shouldBe setOf(Instant.parse("2026-11-01T10:00:00.123Z").toEpochMilli())
        }

    @Test
    fun `the audit log defaults to the Room audit trail of the same database`() =
        runSuspending {
            val created = services.identity.initialise("Akash", "Pixel")

            database.auditDao().forEntity("user_profile", created.user.id.value).size shouldBe 1
        }

    private fun auditTimes(): List<Long> =
        database.openHelper.readableDatabase.query("SELECT at FROM audit_entry").use { cursor ->
            buildList { while (cursor.moveToNext()) add(cursor.getLong(0)) }
        }
}
