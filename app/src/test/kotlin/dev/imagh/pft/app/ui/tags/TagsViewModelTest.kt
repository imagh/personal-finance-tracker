package dev.imagh.pft.app.ui.tags

import androidx.lifecycle.viewModelScope
import dev.imagh.pft.app.data.audit.RoomAuditLog
import dev.imagh.pft.app.log.EntityAction
import dev.imagh.pft.app.log.SafeLog
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.ledger.account.AccountDraft
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.tag.Tag
import dev.imagh.pft.ledger.transaction.LineDraft
import dev.imagh.pft.ledger.transaction.TransactionDraft
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.money.Paise
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import java.time.Instant

/**
 * [TagsViewModel] over real in-memory Room through the production wiring ([RoomTestKit]): what the list
 * shows, what each action changes and audits, the undo offer, and what a failure does. The ViewModel runs
 * on the main dispatcher, so tests wait with [awaitMain]; a swallowed cancellation is pinned by joining the
 * ViewModel's own jobs, never by sleeping.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TagsViewModelTest {
    private val database = TestDatabase.create()
    private val audit = FailsOnDemand(RoomAuditLog(database.auditDao()))
    private val kit = RoomTestKit(database, auditLog = audit)

    @After
    fun closeDatabase() = database.close()

    private fun <T> blocking(block: suspend () -> T): T = runBlocking { block() }

    /**
     * Sets up the identity and three tags. Their names sort differently with and without ignoring case
     * (Bills, groceries, Travel versus Bills, Travel, groceries) and not like their creation order.
     */
    private fun seed(): Map<String, TagId> {
        blocking { kit.initialise() }
        return listOf("Travel", "groceries", "Bills").associateWith { blocking { kit.tags.create(it) }.meta.id }
    }

    private fun viewModel(): TagsViewModel {
        val vm = TagsViewModel(kit.tags)
        awaitMain { vm.state.value.loaded }
        return vm
    }

    private fun TagsViewModel.names(list: (TagsUiState) -> List<Tag>) = list(state.value).map { it.name }

    /** A transaction whose only line carries tag [id]. */
    private fun carryTag(id: TagId) =
        blocking {
            val account = kit.accounts.create(AccountDraft("Cash", AccountType.CASH, Paise(0)))
            kit.transactions.create(
                TransactionDraft(
                    TransactionType.EXPENSE,
                    Paise(1_000),
                    Instant.parse("2026-11-20T10:00:00Z"),
                    account.meta.id,
                    lines = listOf(LineDraft(null, Paise(1_000), null, setOf(id))),
                ),
            )
        }

    private fun trail(id: TagId) = blocking { kit.trailOf("tag", id.value) }.map { it.action }

    private fun logged() = ShadowLog.getLogs().filter { it.tag == SafeLog.TAG }.map { it.msg }

    /** The real audit log until a test sets [failure]; then every append throws it, so a write rolls back. */
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

    private fun failWith(failure: Throwable = IllegalStateException("secret detail Travel")) {
        audit.failure = failure
    }

    @Test
    fun `it loads the tags split into active and archived, each sorted by name ignoring case`() {
        val ids = seed()
        blocking { kit.tags.archive(ids.getValue("Travel")) }

        val vm = viewModel()

        vm.names { it.active } shouldBe listOf("Bills", "groceries")
        vm.names { it.archived } shouldBe listOf("Travel")
        vm.state.value.undo shouldBe null
        vm.state.value.failedAction shouldBe null
    }

    @Test
    fun `active tags sort by name ignoring case, not by code point or creation order`() {
        seed()

        viewModel().names { it.active } shouldBe listOf("Bills", "groceries", "Travel")
    }

    @Test
    fun `with no tags it is loaded and both lists are empty`() {
        blocking { kit.initialise() }

        val vm = viewModel()

        vm.state.value.loaded shouldBe true
        vm.state.value.active shouldBe emptyList()
        vm.state.value.archived shouldBe emptyList()
    }

    @Test
    fun `a deleted tag is in neither list`() {
        val ids = seed()
        blocking { kit.tags.delete(ids.getValue("Travel")) }

        val vm = viewModel()

        vm.names { it.active } shouldBe listOf("Bills", "groceries")
        vm.names { it.archived } shouldBe emptyList()
    }

    @Test
    fun `archive moves the tag to archived and audits an ARCHIVE, logging nothing`() {
        val ids = seed()
        val vm = viewModel()
        ShadowLog.clear()

        vm.archive(ids.getValue("groceries"))
        awaitMain {
            vm.state.value.archived
                .isNotEmpty()
        }

        vm.names { it.active } shouldBe listOf("Bills", "Travel")
        vm.names { it.archived } shouldBe listOf("groceries")
        trail(ids.getValue("groceries")) shouldBe listOf(AuditAction.CREATE, AuditAction.ARCHIVE)
        vm.state.value.undo shouldBe null
        logged() shouldBe emptyList()
    }

    @Test
    fun `unarchive brings the tag back to active and audits an UNARCHIVE`() {
        val ids = seed()
        blocking { kit.tags.archive(ids.getValue("Travel")) }
        val vm = viewModel()

        vm.unarchive(ids.getValue("Travel"))
        awaitMain {
            vm.state.value.archived
                .isEmpty()
        }

        vm.names { it.active } shouldBe listOf("Bills", "groceries", "Travel")
        trail(ids.getValue("Travel")) shouldBe listOf(AuditAction.CREATE, AuditAction.ARCHIVE, AuditAction.UNARCHIVE)
    }

    @Test
    fun `delete removes the tag, audits a DELETE and offers an undo of that tag`() {
        val ids = seed()
        val vm = viewModel()

        vm.delete(ids.getValue("Travel"))
        awaitMain { vm.state.value.undo != null }

        vm.state.value.undo shouldBe ids.getValue("Travel")
        vm.names { it.active } shouldBe listOf("Bills", "groceries")
        trail(ids.getValue("Travel")) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE)
    }

    @Test
    fun `undo restores the deleted tag, audits a RESTORE and clears the offer`() {
        val ids = seed()
        val vm = viewModel()
        vm.delete(ids.getValue("Travel"))
        awaitMain { vm.state.value.undo != null }

        vm.undo()
        awaitMain { vm.state.value.undo == null }

        vm.names { it.active } shouldBe listOf("Bills", "groceries", "Travel")
        trail(ids.getValue("Travel")) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE, AuditAction.RESTORE)
    }

    @Test
    fun `dismissing the offer keeps the tag deleted`() {
        val ids = seed()
        val vm = viewModel()
        vm.delete(ids.getValue("Travel"))
        awaitMain { vm.state.value.undo != null }

        vm.dismissUndo()

        vm.state.value.undo shouldBe null
        vm.names { it.active } shouldBe listOf("Bills", "groceries")
        trail(ids.getValue("Travel")) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE)
    }

    @Test
    fun `a second delete replaces the offer, so only the latest delete can be undone`() {
        val ids = seed()
        val vm = viewModel()
        vm.delete(ids.getValue("Travel"))
        awaitMain { vm.state.value.undo == ids.getValue("Travel") }

        vm.delete(ids.getValue("Bills"))
        awaitMain { vm.state.value.undo == ids.getValue("Bills") }
        vm.undo()
        awaitMain { vm.state.value.undo == null }

        vm.names { it.active } shouldBe listOf("Bills", "groceries")
    }

    @Test
    fun `an undo whose name was taken meanwhile leaves the tag deleted, ends the offer and logs nothing`() {
        val ids = seed()
        val vm = viewModel()
        vm.delete(ids.getValue("Travel"))
        awaitMain { vm.state.value.undo != null }
        blocking { kit.tags.create("travel") }
        ShadowLog.clear()

        vm.undo()
        awaitMain { vm.state.value.undo == null }

        vm.names { it.active } shouldBe listOf("Bills", "groceries", "travel")
        trail(ids.getValue("Travel")) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE)
        vm.state.value.failedAction shouldBe null
        logged() shouldBe emptyList()
    }

    @Test
    fun `reload picks up a tag added elsewhere`() {
        seed()
        val vm = viewModel()
        blocking { kit.tags.create("Zeta") }

        vm.reload()
        awaitMain { vm.state.value.active.size == 4 }

        vm.names { it.active } shouldBe listOf("Bills", "groceries", "Travel", "Zeta")
    }

    @Test
    fun `an action on a tag that was deleted meanwhile just refreshes the list, with no error and no log`() {
        val ids = seed()
        val vm = viewModel()
        blocking { kit.tags.delete(ids.getValue("Travel")) }
        ShadowLog.clear()

        vm.archive(ids.getValue("Travel"))
        awaitMain { vm.state.value.active.size == 2 }

        vm.names { it.active } shouldBe listOf("Bills", "groceries")
        vm.state.value.failedAction shouldBe null
        logged() shouldBe emptyList()
    }

    @Test
    fun `deleting a tag that a transaction carries shows the in-use notice, writes nothing and logs nothing`() {
        val ids = seed()
        carryTag(ids.getValue("Travel"))
        val vm = viewModel()
        ShadowLog.clear()

        vm.delete(ids.getValue("Travel"))
        awaitMain { vm.state.value.inUse }

        vm.state.value.undo shouldBe null
        vm.state.value.failedAction shouldBe null
        vm.names { it.active } shouldBe listOf("Bills", "groceries", "Travel")
        trail(ids.getValue("Travel")) shouldBe listOf(AuditAction.CREATE)
        // A refused delete is an answer, not a failure: nothing of ours is logged.
        logged() shouldBe emptyList()

        vm.inUseShown()
        vm.state.value.inUse shouldBe false
    }

    @Test
    fun `a tag no transaction carries is still deleted while another is in use, with no notice`() {
        val ids = seed()
        carryTag(ids.getValue("Travel"))
        val vm = viewModel()

        vm.delete(ids.getValue("Bills"))
        awaitMain { vm.state.value.undo != null }

        vm.state.value.inUse shouldBe false
        vm.names { it.active } shouldBe listOf("groceries", "Travel")
    }

    @Test
    fun `a failing audit on archive leaves the list unchanged, sets the failure and logs only the enums`() {
        val ids = seed()
        val vm = viewModel()
        failWith()
        ShadowLog.clear()

        vm.archive(ids.getValue("groceries"))
        awaitMain { vm.state.value.failedAction != null }

        audit.failedAppends shouldBe 1
        vm.state.value.failedAction shouldBe EntityAction.ARCHIVE
        vm.names { it.active } shouldBe listOf("Bills", "groceries", "Travel")
        vm.names { it.archived } shouldBe emptyList()
        blocking { kit.tags.lists() }.archived shouldBe emptyList()
        trail(ids.getValue("groceries")) shouldBe listOf(AuditAction.CREATE)
        logged() shouldBe listOf("entity_action_failed kind=TAG action=ARCHIVE")
    }

    @Test
    fun `a failing unarchive reports UNARCHIVE`() {
        val ids = seed()
        blocking { kit.tags.archive(ids.getValue("Travel")) }
        val vm = viewModel()
        failWith()
        ShadowLog.clear()

        vm.unarchive(ids.getValue("Travel"))
        awaitMain { vm.state.value.failedAction != null }

        vm.state.value.failedAction shouldBe EntityAction.UNARCHIVE
        vm.names { it.archived } shouldBe listOf("Travel")
        logged() shouldBe listOf("entity_action_failed kind=TAG action=UNARCHIVE")
    }

    @Test
    fun `a failing delete reports DELETE, keeps the tag and offers no undo`() {
        val ids = seed()
        val vm = viewModel()
        failWith()
        ShadowLog.clear()

        vm.delete(ids.getValue("Travel"))
        awaitMain { vm.state.value.failedAction != null }

        vm.state.value.failedAction shouldBe EntityAction.DELETE
        vm.state.value.undo shouldBe null
        vm.names { it.active } shouldBe listOf("Bills", "groceries", "Travel")
        logged() shouldBe listOf("entity_action_failed kind=TAG action=DELETE")
        // Another failure is not the in-use answer.
        vm.state.value.inUse shouldBe false
    }

    @Test
    fun `a failing undo reports RESTORE, leaves the tag deleted and withdraws the offer`() {
        val ids = seed()
        val vm = viewModel()
        vm.delete(ids.getValue("Travel"))
        awaitMain { vm.state.value.undo != null }
        failWith()
        ShadowLog.clear()

        vm.undo()
        awaitMain { vm.state.value.failedAction != null }

        vm.state.value.failedAction shouldBe EntityAction.RESTORE
        vm.state.value.undo shouldBe null
        vm.names { it.active } shouldBe listOf("Bills", "groceries")
        logged() shouldBe listOf("entity_action_failed kind=TAG action=RESTORE")
    }

    @Test
    fun `the failure is cleared once it has been shown`() {
        val ids = seed()
        val vm = viewModel()
        failWith()
        vm.archive(ids.getValue("Travel"))
        awaitMain { vm.state.value.failedAction != null }

        vm.failureShown()

        vm.state.value.failedAction shouldBe null
    }

    @Test
    fun `a cancellation is not reported as a failure and logs nothing`() {
        val ids = seed()
        val vm = viewModel()
        failWith(CancellationException("cancelled"))
        ShadowLog.clear()

        vm.archive(ids.getValue("Travel"))
        awaitMain { audit.failedAppends >= 1 }
        // Wait for the ViewModel's own jobs to end, not for a time to pass.
        runBlocking {
            vm.viewModelScope.coroutineContext[Job]!!
                .children
                .toList()
                .forEach { it.join() }
        }

        vm.state.value.failedAction shouldBe null
        logged() shouldBe emptyList()
    }

    @Test
    fun `two rapid actions on different tags both complete and leave a consistent list`() {
        val ids = seed()
        val vm = viewModel()

        vm.archive(ids.getValue("Travel"))
        vm.archive(ids.getValue("Bills"))
        awaitMain { vm.state.value.archived.size == 2 }

        vm.names { it.active } shouldBe listOf("groceries")
        vm.names { it.archived } shouldBe listOf("Bills", "Travel")
    }
}
