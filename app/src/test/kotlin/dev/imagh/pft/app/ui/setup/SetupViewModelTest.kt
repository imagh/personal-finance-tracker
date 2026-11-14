package dev.imagh.pft.app.ui.setup

import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.data.audit.RoomAuditLog
import dev.imagh.pft.app.data.common.EpochMillis
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.category.DefaultCategories
import dev.imagh.pft.ledger.category.SystemCategory
import dev.imagh.pft.ledger.settings.SettingKeys
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
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

    /** The 31 defaults of plan §4: [DefaultCategories.LIST], each stored under its deterministic id. */
    private fun assertDefaultsSeeded() {
        count("category") shouldBe "31"
        val stored = blocking { services.categoryRepository.allLive() }
        val expected = DefaultCategories.LIST.map { DefaultCategories.idOf(it.seedKey) }.toSet()
        stored.map { it.meta.id }.toSet() shouldBe expected
        val emi = blocking { services.categoryRepository.bySystemKey(SystemCategory.LOAN_EMI) }
        emi?.name shouldBe "Loan EMI"
        // Seeded in the same step as the identity, so every default is attributed to the new user and device.
        val identity = blocking { services.identity.currentIdentity() }
        stored.map { it.meta.createdByUserId }.toSet() shouldBe setOf(identity?.user?.id)
        stored.map { it.meta.createdOnDeviceId }.toSet() shouldBe setOf(identity?.device?.id)
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
        count("category") shouldBe "0"
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
        assertDefaultsSeeded()
        // The user, the device and the setting (3 audit rows), then one CREATE per default.
        count("audit_entry") shouldBe "34"
    }

    @Test
    fun `two rapid submits create one user and one set of defaults, with no error`() {
        val vm = viewModel()
        vm.onNameChange("Akash")

        vm.submit()
        vm.submit()
        awaitMain { vm.state.value.done }

        blocking { services.identity.users() }.size shouldBe 1
        count("audit_entry") shouldBe "34"
        assertDefaultsSeeded()
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

        count("audit_entry") shouldBe "34"
        count("category") shouldBe "31"
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
        // Nothing is seeded for an identity that already existed (no defaults for pre-slice-B installs, Q4).
        count("category") shouldBe "0"
    }

    @Test
    fun `the phone name is prefilled from the default, trimmed and cut to the limit`() {
        // Built directly: a ViewModelStore hands back its first instance for the same class.
        SetupViewModel(services.firstRun, "  Pixel 8 ")
            .state.value.deviceLabel.text shouldBe "Pixel 8"
        SetupViewModel(services.firstRun, "x".repeat(150))
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

    // --- An unexpected save failure (A5f): the audit log is the real swap boundary of the write path,
    // so a test double of it makes the transaction fail after the user, device and setting rows were
    // written, which Room then rolls back.

    /** Throws [failure] on append number [failOn] (the first by default), then behaves like [real]. */
    private class FailsOnAppend(
        private val real: AuditLog,
        private val failure: Throwable,
        private val failOn: Int = 1,
    ) : AuditLog {
        var appends = 0
            private set

        override suspend fun append(entry: AuditEntry) {
            appends++
            if (appends == failOn) throw failure
            real.append(entry)
        }
    }

    private fun failingFirstSave(
        failure: Throwable = IllegalStateException("secret detail Akash"),
        failOn: Int = 1,
    ): Pair<SetupViewModel, FailsOnAppend> {
        val audit = FailsOnAppend(RoomAuditLog(database.auditDao()), failure, failOn)
        val failing = AppServices(database, EpochMillis.clock(), audit)
        return SetupViewModel(failing.firstRun, "Pixel 8") to audit
    }

    @Test
    fun `an unexpected save failure shows a form error, keeps the typed text and logs setup_failed only`() {
        val (vm, _) = failingFirstSave()
        vm.onNameChange("  Akash ")
        vm.onDeviceLabelChange(" Pixel 8  ")
        ShadowLog.clear()

        vm.submit()
        awaitMain { vm.state.value.saveFailed }

        val state = vm.state.value
        state.saving shouldBe false
        state.done shouldBe false
        state.name.text shouldBe "  Akash "
        state.deviceLabel.text shouldBe " Pixel 8  "
        state.name.error shouldBe null
        state.deviceLabel.error shouldBe null
        ShadowLog.getLogs().map { it.msg } shouldBe listOf("setup_failed")
        blocking { services.identity.users() } shouldBe emptyList()
        count("audit_entry") shouldBe "0"
        // The whole transaction rolled back, not only the rows before the failing audit append.
        count("device") shouldBe "0"
        blocking { services.settingsRepository.get(SettingKeys.CURRENT_DEVICE_ID) } shouldBe null
        count("category") shouldBe "0"
    }

    @Test
    fun `a failure after the identity and some defaults were written rolls all of it back, and a retry seeds once`() {
        // Append 10 is the seventh category: the user, device and setting rows and six defaults are already
        // written inside the transaction when it fails.
        val (vm, audit) = failingFirstSave(failOn = 10)
        vm.onNameChange("Akash")
        ShadowLog.clear()

        vm.submit()
        awaitMain { vm.state.value.saveFailed }

        audit.appends shouldBe 10
        vm.state.value.done shouldBe false
        ShadowLog.getLogs().map { it.msg } shouldBe listOf("setup_failed")
        blocking { services.identity.users() } shouldBe emptyList()
        count("user_profile") shouldBe "0"
        count("device") shouldBe "0"
        count("app_setting") shouldBe "0"
        count("category") shouldBe "0"
        count("audit_entry") shouldBe "0"

        vm.submit()
        awaitMain { vm.state.value.done }

        count("user_profile") shouldBe "1"
        count("audit_entry") shouldBe "34"
        assertDefaultsSeeded()
    }

    @Test
    fun `editing the name clears the save-failed error`() {
        val (vm, _) = failingFirstSave()
        vm.onNameChange("Akash")
        vm.submit()
        awaitMain { vm.state.value.saveFailed }

        vm.onNameChange("Akash K")

        vm.state.value.saveFailed shouldBe false
        vm.state.value.name.text shouldBe "Akash K"
    }

    @Test
    fun `editing the phone name clears the save-failed error`() {
        val (vm, _) = failingFirstSave()
        vm.onNameChange("Akash")
        vm.submit()
        awaitMain { vm.state.value.saveFailed }

        vm.onDeviceLabelChange("Pixel 9")

        vm.state.value.saveFailed shouldBe false
        vm.state.value.deviceLabel.text shouldBe "Pixel 9"
    }

    @Test
    fun `submitting again clears the error at once and a retry saves exactly one identity`() {
        val (vm, audit) = failingFirstSave()
        vm.onNameChange("Akash")
        vm.submit()
        awaitMain { vm.state.value.saveFailed }

        vm.submit()
        vm.state.value.saveFailed shouldBe false
        vm.state.value.saving shouldBe true
        awaitMain { vm.state.value.done }

        vm.state.value.saveFailed shouldBe false
        vm.state.value.saving shouldBe false
        // One failed append, then the identity (3) and the defaults (31) of the successful retry.
        audit.appends shouldBe 35
        blocking { services.identity.users() }.map { it.displayName } shouldBe listOf("Akash")
        count("user_profile") shouldBe "1"
        count("device") shouldBe "1"
        count("audit_entry") shouldBe "34"
        count("category") shouldBe "31"
        blocking { services.identity.currentIdentity() }?.device?.label shouldBe "Pixel 8"
    }

    @Test
    fun `a cancellation is not reported as a save failure`() {
        val (vm, audit) = failingFirstSave(CancellationException("cancelled"))
        vm.onNameChange("Akash")
        ShadowLog.clear()

        vm.submit()
        awaitMain { audit.appends >= 1 }
        repeat(10) {
            shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(10)
        }

        vm.state.value.saveFailed shouldBe false
        ShadowLog.getLogs().map { it.msg } shouldBe emptyList()
    }
}
