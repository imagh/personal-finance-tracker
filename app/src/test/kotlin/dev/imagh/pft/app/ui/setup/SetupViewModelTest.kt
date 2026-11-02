package dev.imagh.pft.app.ui.setup

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.data.common.EpochMillis
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.app.ui.common.FieldError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

/**
 * [SetupViewModel] against a real Room database through the production wiring ([AppServices]): what
 * it refuses, what it persists, and what it logs. The ViewModel runs on the main dispatcher, so the
 * tests wait with [awaitMain], which idles the Robolectric main looper.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SetupViewModelTest {
    private val database: PftDatabase = TestDatabase.create()
    private val services = AppServices(database, EpochMillis.clock())
    private val store = ViewModelStore()

    @After
    fun tearDown() {
        store.clear()
        database.close()
    }

    private fun viewModel(defaultDeviceLabel: String = "Pixel 8"): SetupViewModel =
        ViewModelProvider(store, SetupViewModel.factory(services, defaultDeviceLabel))[SetupViewModel::class.java]

    private fun <T> blocking(block: suspend () -> T): T = runBlocking { block() }

    private fun count(table: String): String =
        database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM $table").use { cursor ->
            cursor.moveToFirst()
            cursor.getString(0)
        }

    @Test
    fun `a blank name is Required on the name field, saves nothing and is not done`() {
        val vm = viewModel()
        vm.onNameChange("   ")

        vm.submit()
        awaitMain { !vm.state.value.saving }

        val state = vm.state.value
        state.name.error shouldBe FieldError.Required
        state.deviceLabel.error shouldBe null
        state.done shouldBe false
        blocking { services.identity.users() } shouldBe emptyList()
        count("audit_entry") shouldBe "0"
    }

    @Test
    fun `a blank phone name is Required on the phone field only`() {
        val vm = viewModel()
        vm.onNameChange("Akash")
        vm.onDeviceLabelChange("  ")

        vm.submit()
        awaitMain { !vm.state.value.saving }

        val state = vm.state.value
        state.deviceLabel.error shouldBe FieldError.Required
        state.name.error shouldBe null
        state.done shouldBe false
        blocking { services.identity.users() } shouldBe emptyList()
        count("audit_entry") shouldBe "0"
    }

    @Test
    fun `a name over the limit is TooLong with the core limit`() {
        val vm = viewModel()
        vm.onNameChange("a".repeat(101))

        vm.submit()
        awaitMain { !vm.state.value.saving }

        vm.state.value.name.error shouldBe FieldError.TooLong(100)
        vm.state.value.name.text shouldBe "a".repeat(101)
    }

    @Test
    fun `a valid submit is done, saves the trimmed user and device and logs setup_completed`() {
        val vm = viewModel()
        vm.onNameChange("  Akash ")
        vm.onDeviceLabelChange(" Pixel 8  ")
        ShadowLog.clear()

        vm.submit()
        awaitMain { vm.state.value.done }

        val identity = blocking { services.identity.currentIdentity() }
        identity?.user?.displayName shouldBe "Akash"
        identity?.device?.label shouldBe "Pixel 8"
        identity?.device?.userId shouldBe identity?.user?.id
        vm.state.value.name.error shouldBe null
        vm.state.value.deviceLabel.error shouldBe null
        ShadowLog.getLogs().map { it.msg } shouldBe listOf("setup_completed")
    }

    @Test
    fun `two rapid submits create one user and three audit rows, with no error`() {
        val vm = viewModel()
        vm.onNameChange("Akash")

        vm.submit()
        vm.submit()
        awaitMain { vm.state.value.done }

        blocking { services.identity.users() }.size shouldBe 1
        count("audit_entry") shouldBe "3"
        vm.state.value.name.error shouldBe null
        vm.state.value.deviceLabel.error shouldBe null
    }

    @Test
    fun `while saving, Continue is disabled and a second submit is ignored even if the text changed`() {
        val vm = viewModel()
        vm.onNameChange("Akash")

        vm.submit()
        vm.state.value.saving shouldBe true
        vm.onNameChange("")
        vm.submit()
        awaitMain { vm.state.value.done }

        vm.state.value.name.error shouldBe null
        blocking { services.identity.users() }.map { it.displayName } shouldBe listOf("Akash")
    }

    @Test
    fun `submitting again once done changes nothing`() {
        val vm = viewModel()
        vm.onNameChange("Akash")
        vm.submit()
        awaitMain { vm.state.value.done }

        vm.submit()
        awaitMain { !vm.state.value.saving }

        count("audit_entry") shouldBe "3"
        vm.state.value.done shouldBe true
    }

    @Test
    fun `an identity that already exists is treated as done, not as an error`() {
        blocking { services.identity.initialise("Akash", "Pixel") }
        val vm = viewModel()
        vm.onNameChange("Someone")

        vm.submit()
        awaitMain { vm.state.value.done }

        vm.state.value.name.error shouldBe null
        blocking { services.identity.users() }.map { it.displayName } shouldBe listOf("Akash")
        count("audit_entry") shouldBe "3"
    }

    @Test
    fun `the phone name is prefilled from the default, trimmed and cut to the limit`() {
        // Built directly: a ViewModelStore hands back its first instance for the same class.
        SetupViewModel(services.identity, "  Pixel 8 ")
            .state.value.deviceLabel.text shouldBe "Pixel 8"
        SetupViewModel(services.identity, "x".repeat(150))
            .state.value.deviceLabel.text shouldBe "x".repeat(100)
    }

    @Test
    fun `typing clears that field's error and leaves the other one`() {
        val vm = viewModel()
        vm.onDeviceLabelChange("")
        vm.submit()
        awaitMain { !vm.state.value.saving }
        vm.state.value.name.error shouldBe FieldError.Required
        vm.state.value.deviceLabel.error shouldBe FieldError.Required

        vm.onNameChange("A")

        vm.state.value.name.error shouldBe null
        vm.state.value.deviceLabel.error shouldBe FieldError.Required
    }
}
