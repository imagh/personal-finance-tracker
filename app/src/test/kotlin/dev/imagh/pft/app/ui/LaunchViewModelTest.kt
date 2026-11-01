package dev.imagh.pft.app.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import dev.imagh.pft.app.data.DatabaseStartup
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.di.AppContainer
import dev.imagh.pft.app.di.AppStart
import dev.imagh.pft.app.security.DbKeyUnavailable.Reason
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.settings.AppSetting
import dev.imagh.pft.ledger.settings.SettingKeys
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import java.time.Instant
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * [LaunchViewModel] decides what the app opens to, against a real Room database: the four launch
 * states. It does its work on an IO dispatcher, so the test thread (Robolectric's main thread) can
 * wait for the state with a plain blocking wait without deadlocking the main looper.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LaunchViewModelTest {
    private val database: PftDatabase = TestDatabase.create()
    private val store = ViewModelStore()

    @After
    fun tearDown() {
        store.clear()
        database.close()
    }

    private fun readyContainer() = AppContainer({ DatabaseStartup.Ready(database) })

    private fun launch(container: AppContainer): LaunchViewModel =
        ViewModelProvider(store, LaunchViewModel.factory(container, Dispatchers.IO))[LaunchViewModel::class.java]

    private suspend fun LaunchViewModel.settled(): LaunchState =
        withTimeout(10_000) { state.first { it != LaunchState.Opening } }

    private suspend fun AppContainer.services() = (open() as AppStart.Ready).services

    @Test
    fun `it starts in Opening, before the database has answered`() {
        val gate = CountDownLatch(1)
        val vm =
            launch(
                AppContainer({
                    gate.await(10, TimeUnit.SECONDS)
                    DatabaseStartup.Ready(database)
                }),
            )

        try {
            vm.state.value shouldBe LaunchState.Opening
        } finally {
            gate.countDown()
        }
    }

    @Test
    fun `a fresh database is Ready without an identity`() =
        runSuspending {
            val container = readyContainer()
            val vm = launch(container)

            vm.settled() shouldBe LaunchState.Ready(container.services(), hasIdentity = false)
        }

    @Test
    fun `an initialised database is Ready with an identity`() =
        runSuspending {
            val container = readyContainer()
            container.services().identity.initialise("Akash", "Pixel")
            val vm = launch(container)

            val state = vm.settled()

            state shouldBe LaunchState.Ready(container.services(), hasIdentity = true)
            (state as LaunchState.Ready).services shouldBeSameInstanceAs container.services()
        }

    @Test
    fun `a current-device setting that points nowhere fails closed and logs identity_corrupt`() =
        runSuspending {
            val container = readyContainer()
            container.services().settingsRepository.put(
                AppSetting(SettingKeys.CURRENT_DEVICE_ID, "no-such-device", Instant.parse("2026-11-01T10:00:00Z")),
            )
            ShadowLog.clear()
            val vm = launch(container)

            vm.settled() shouldBe LaunchState.IdentityCorrupt

            ShadowLog.getLogs().map { it.msg } shouldBe listOf("identity_corrupt")
        }

    @Test
    fun `an unavailable key is KeyUnavailable with its reason`() =
        runSuspending {
            val vm = launch(AppContainer({ DatabaseStartup.KeyUnavailable(Reason.KEK_MISSING) }))

            vm.settled() shouldBe LaunchState.KeyUnavailable(Reason.KEK_MISSING)
        }
}
