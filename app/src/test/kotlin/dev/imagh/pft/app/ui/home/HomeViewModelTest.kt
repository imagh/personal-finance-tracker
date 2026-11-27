package dev.imagh.pft.app.ui.home

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.data.common.EpochMillis
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** [HomeViewModel] against a real Room database through the production wiring: the stored user's name. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class HomeViewModelTest {
    private val database: PftDatabase = TestDatabase.create()
    private val services = AppServices(database, EpochMillis.clock())
    private val store = ViewModelStore()

    @After
    fun tearDown() {
        store.clear()
        database.close()
    }

    private fun viewModel(): HomeViewModel =
        ViewModelProvider(store, HomeViewModel.factory(services))[HomeViewModel::class.java]

    @Test
    fun `it starts not loaded, with no name`() {
        runBlocking { services.identity.initialise("Akash", "Pixel 8") }

        val before = viewModel().state.value

        before.loaded shouldBe false
        before.userName shouldBe ""
    }

    @Test
    fun `the state carries the stored, trimmed user name once loaded`() {
        runBlocking { services.identity.initialise("  Akash ", "Pixel 8") }
        val vm = viewModel()

        awaitMain { vm.state.value.loaded }

        vm.state.value.userName shouldBe "Akash"
    }
}
