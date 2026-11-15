package dev.imagh.pft.app.ui.accounts

import android.os.Looper
import dev.imagh.pft.app.data.audit.RoomAuditLog
import dev.imagh.pft.app.log.EntityAction
import dev.imagh.pft.app.log.EntityKind
import dev.imagh.pft.app.log.LogEvent
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.money.Paise
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
 * [AccountsViewModel] over real in-memory Room through the production wiring ([RoomTestKit]): what the
 * list shows, what each action changes and audits, the undo offer, and what a failure does. The
 * ViewModel runs on the main dispatcher, so tests wait with [awaitMain].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AccountsViewModelTest {
    private val database = TestDatabase.create()
    private val audit = FailsOnDemand(RoomAuditLog(database.auditDao()))
    private val kit = RoomTestKit(database, auditLog = audit)

    @After
    fun closeDatabase() = database.close()

    private fun <T> blocking(block: suspend () -> T): T = runBlocking { block() }

    /** Sets up the identity and three accounts whose names sort unambiguously: Axis Card, HDFC Savings, Wallet. */
    private fun seed(): Map<String, AccountId> {
        blocking { kit.initialise() }
        return listOf(
            "Wallet" to AccountType.WALLET,
            "HDFC Savings" to AccountType.BANK,
            "Axis Card" to AccountType.CREDIT_CARD,
        ).associate { (name, type) ->
            name to blocking { kit.accounts.create(AccountDraft(name, type, Paise(0))) }.meta.id
        }
    }

    private fun viewModel(): AccountsViewModel {
        val vm = AccountsViewModel(kit.accounts)
        awaitMain { vm.state.value.loaded }
        return vm
    }

    private fun AccountsViewModel.names(list: (AccountsUiState) -> List<Account>) = list(state.value).map { it.name }

    private fun auditRowCount(): Long =
        database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM audit_entry").use { cursor ->
            cursor.moveToFirst()
            cursor.getLong(0)
        }

    private fun trail(id: AccountId) = blocking { kit.trailOf("account", id.value) }.map { it.action }

    /**
     * The real audit log until a test sets [failure]; then every append throws it, so any write that
     * reaches the audit log fails and Room rolls it back. The audit log is the real swap boundary of
     * the write path, so this injects the failure without a seam of its own.
     */
    private class FailsOnDemand(
        private val real: AuditLog,
    ) : AuditLog {
        var failure: Throwable? = null
        var failedAppends = 0
            private set

        override suspend fun append(entry: AuditEntry) {
            failure?.let {
                failedAppends++
                throw it
            }
            real.append(entry)
        }
    }

    private fun failWith(failure: Throwable = IllegalStateException("secret detail Akash")) {
        audit.failure = failure
    }

    @Test
    fun `it loads the seeded accounts split into active and archived, each sorted by name`() {
        val ids = seed()
        blocking { kit.accounts.archive(ids.getValue("Wallet")) }

        val vm = viewModel()

        vm.names { it.active } shouldBe listOf("Axis Card", "HDFC Savings")
        vm.names { it.archived } shouldBe listOf("Wallet")
        vm.state.value.undo shouldBe null
        vm.state.value.failedAction shouldBe null
    }

    @Test
    fun `with no accounts it is loaded and both lists are empty`() {
        blocking { kit.initialise() }

        val vm = viewModel()

        vm.state.value.loaded shouldBe true
        vm.state.value.active shouldBe emptyList()
        vm.state.value.archived shouldBe emptyList()
    }

    @Test
    fun `a deleted account is in neither list`() {
        val ids = seed()
        blocking { kit.accounts.delete(ids.getValue("Wallet")) }

        val vm = viewModel()

        vm.names { it.active } shouldBe listOf("Axis Card", "HDFC Savings")
        vm.names { it.archived } shouldBe emptyList()
    }

    @Test
    fun `archive moves the account to archived and audits an ARCHIVE`() {
        val ids = seed()
        val vm = viewModel()
        ShadowLog.clear()

        vm.archive(ids.getValue("HDFC Savings"))
        awaitMain {
            vm.state.value.archived
                .isNotEmpty()
        }

        vm.names { it.active } shouldBe listOf("Axis Card", "Wallet")
        vm.names { it.archived } shouldBe listOf("HDFC Savings")
        trail(ids.getValue("HDFC Savings")) shouldBe listOf(AuditAction.CREATE, AuditAction.ARCHIVE)
        vm.state.value.undo shouldBe null
        ShadowLog.getLogs().map { it.msg } shouldBe emptyList()
    }

    @Test
    fun `unarchive brings the account back to active and audits an UNARCHIVE`() {
        val ids = seed()
        blocking { kit.accounts.archive(ids.getValue("Wallet")) }
        val vm = viewModel()

        vm.unarchive(ids.getValue("Wallet"))
        awaitMain {
            vm.state.value.archived
                .isEmpty()
        }

        vm.names { it.active } shouldBe listOf("Axis Card", "HDFC Savings", "Wallet")
        trail(ids.getValue("Wallet")) shouldBe listOf(AuditAction.CREATE, AuditAction.ARCHIVE, AuditAction.UNARCHIVE)
    }

    @Test
    fun `delete removes the account, audits a DELETE and offers an undo of that account`() {
        val ids = seed()
        val vm = viewModel()

        vm.delete(ids.getValue("Axis Card"))
        awaitMain { vm.state.value.undo != null }

        vm.state.value.undo shouldBe ids.getValue("Axis Card")
        vm.names { it.active } shouldBe listOf("HDFC Savings", "Wallet")
        trail(ids.getValue("Axis Card")) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE)
    }

    @Test
    fun `undo restores the deleted account, audits a RESTORE and clears the offer`() {
        val ids = seed()
        val vm = viewModel()
        vm.delete(ids.getValue("Axis Card"))
        awaitMain { vm.state.value.undo != null }

        vm.undo()
        awaitMain { vm.state.value.undo == null }

        vm.names { it.active } shouldBe listOf("Axis Card", "HDFC Savings", "Wallet")
        trail(ids.getValue("Axis Card")) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE, AuditAction.RESTORE)
    }

    @Test
    fun `undo with no offer does nothing`() {
        seed()
        val vm = viewModel()
        val auditRows = auditRowCount()

        vm.undo()
        repeat(5) { shadowOf(Looper.getMainLooper()).idle() }

        vm.names { it.active } shouldBe listOf("Axis Card", "HDFC Savings", "Wallet")
        auditRowCount() shouldBe auditRows
    }

    @Test
    fun `dismissing the offer keeps the account deleted`() {
        val ids = seed()
        val vm = viewModel()
        vm.delete(ids.getValue("Axis Card"))
        awaitMain { vm.state.value.undo != null }

        vm.dismissUndo()

        vm.state.value.undo shouldBe null
        vm.names { it.active } shouldBe listOf("HDFC Savings", "Wallet")
        trail(ids.getValue("Axis Card")) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE)
    }

    @Test
    fun `a second delete replaces the offer, so only the latest delete can be undone`() {
        val ids = seed()
        val vm = viewModel()
        vm.delete(ids.getValue("Axis Card"))
        awaitMain { vm.state.value.undo == ids.getValue("Axis Card") }

        vm.delete(ids.getValue("Wallet"))
        awaitMain { vm.state.value.undo == ids.getValue("Wallet") }
        vm.undo()
        awaitMain { vm.state.value.undo == null }

        vm.names { it.active } shouldBe listOf("HDFC Savings", "Wallet")
    }

    @Test
    fun `reload picks up an account added elsewhere`() {
        seed()
        val vm = viewModel()
        blocking { kit.accounts.create(AccountDraft("Zeta", AccountType.CASH, Paise(0))) }

        vm.reload()
        awaitMain { vm.state.value.active.size == 4 }

        vm.names { it.active } shouldBe listOf("Axis Card", "HDFC Savings", "Wallet", "Zeta")
    }

    @Test
    fun `an action on an account that was deleted meanwhile just refreshes the list, with no error and no log`() {
        val ids = seed()
        val vm = viewModel()
        blocking { kit.accounts.delete(ids.getValue("Wallet")) }
        ShadowLog.clear()

        vm.archive(ids.getValue("Wallet"))
        awaitMain { vm.state.value.active.size == 2 }

        vm.names { it.active } shouldBe listOf("Axis Card", "HDFC Savings")
        vm.state.value.failedAction shouldBe null
        ShadowLog.getLogs().map { it.msg } shouldBe emptyList()
    }

    @Test
    fun `a failing audit on archive leaves the list unchanged, sets the failure and logs only the enums`() {
        val ids = seed()
        val vm = viewModel()
        failWith()
        ShadowLog.clear()

        vm.archive(ids.getValue("HDFC Savings"))
        awaitMain { vm.state.value.failedAction != null }

        audit.failedAppends shouldBe 1
        vm.state.value.failedAction shouldBe EntityAction.ARCHIVE
        vm.names { it.active } shouldBe listOf("Axis Card", "HDFC Savings", "Wallet")
        vm.names { it.archived } shouldBe emptyList()
        // The write rolled back: nothing is archived in the database either.
        blocking { kit.accounts.lists() }.archived shouldBe emptyList()
        trail(ids.getValue("HDFC Savings")) shouldBe listOf(AuditAction.CREATE)
        ShadowLog.getLogs().map { it.msg } shouldBe
            listOf(LogEvent.EntityActionFailed(EntityKind.ACCOUNT, EntityAction.ARCHIVE).line())
        ShadowLog.getLogs().single().msg shouldBe "entity_action_failed kind=ACCOUNT action=ARCHIVE"
    }

    @Test
    fun `a failing unarchive reports UNARCHIVE`() {
        val ids = seed()
        blocking { kit.accounts.archive(ids.getValue("Wallet")) }
        val vm = viewModel()
        failWith()

        vm.unarchive(ids.getValue("Wallet"))
        awaitMain { vm.state.value.failedAction != null }

        vm.state.value.failedAction shouldBe EntityAction.UNARCHIVE
        vm.names { it.archived } shouldBe listOf("Wallet")
    }

    @Test
    fun `a failing delete reports DELETE, keeps the account and offers no undo`() {
        val ids = seed()
        val vm = viewModel()
        failWith()
        ShadowLog.clear()

        vm.delete(ids.getValue("Wallet"))
        awaitMain { vm.state.value.failedAction != null }

        vm.state.value.failedAction shouldBe EntityAction.DELETE
        vm.state.value.undo shouldBe null
        vm.names { it.active } shouldBe listOf("Axis Card", "HDFC Savings", "Wallet")
        ShadowLog.getLogs().single().msg shouldBe "entity_action_failed kind=ACCOUNT action=DELETE"
    }

    @Test
    fun `a failing undo reports RESTORE, leaves the account deleted and withdraws the offer`() {
        val ids = seed()
        val vm = viewModel()
        vm.delete(ids.getValue("Wallet"))
        awaitMain { vm.state.value.undo != null }
        failWith()
        ShadowLog.clear()

        vm.undo()
        awaitMain { vm.state.value.failedAction != null }

        vm.state.value.failedAction shouldBe EntityAction.RESTORE
        vm.state.value.undo shouldBe null
        vm.names { it.active } shouldBe listOf("Axis Card", "HDFC Savings")
        ShadowLog.getLogs().single().msg shouldBe "entity_action_failed kind=ACCOUNT action=RESTORE"
    }

    @Test
    fun `the failure is cleared once it has been shown`() {
        val ids = seed()
        val vm = viewModel()
        failWith()
        vm.archive(ids.getValue("Wallet"))
        awaitMain { vm.state.value.failedAction != null }

        vm.failureShown()

        vm.state.value.failedAction shouldBe null
    }

    @Test
    fun `a cancellation is not reported as a failure`() {
        val ids = seed()
        val vm = viewModel()
        failWith(CancellationException("cancelled"))
        ShadowLog.clear()

        vm.archive(ids.getValue("Wallet"))
        awaitMain { audit.failedAppends >= 1 }
        repeat(10) {
            shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(10)
        }

        vm.state.value.failedAction shouldBe null
        ShadowLog.getLogs().map { it.msg } shouldBe emptyList()
    }

    @Test
    fun `two rapid actions on different accounts both complete and leave a consistent list`() {
        val ids = seed()
        val vm = viewModel()

        vm.archive(ids.getValue("Wallet"))
        vm.archive(ids.getValue("Axis Card"))
        awaitMain { vm.state.value.archived.size == 2 }

        vm.names { it.active } shouldBe listOf("HDFC Savings")
        vm.names { it.archived } shouldBe listOf("Axis Card", "Wallet")
    }
}
