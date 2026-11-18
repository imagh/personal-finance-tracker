package dev.imagh.pft.app.ui.categories

import android.os.Looper
import dev.imagh.pft.app.data.audit.RoomAuditLog
import dev.imagh.pft.app.log.EntityAction
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.DefaultCategories
import dev.imagh.pft.ledger.ids.CategoryId
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
 * [CategoriesViewModel]'s delete and undo, the refused delete of a parent, and what a failure does:
 * every action is one audited transaction, so a failed one leaves nothing behind, shows a fixed
 * message and logs one enum-only event (SEC-5).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CategoriesActionsTest {
    private val database = TestDatabase.create()
    private val audit = FailsOnDemand(RoomAuditLog(database.auditDao()))
    private val kit = RoomTestKit(database, auditLog = audit)

    @After
    fun closeDatabase() = database.close()

    /** The real audit log until [failure] is set; then every append throws, so a write fails and rolls back. */
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

    private fun <T> blocking(block: suspend () -> T): T = runBlocking { block() }

    private fun withDefaults() =
        blocking {
            kit.initialise()
            kit.categories.seedDefaults()
        }

    private fun id(seedKey: String) = DefaultCategories.idOf(seedKey)

    private fun viewModel(): CategoriesViewModel {
        val vm = CategoriesViewModel(kit.categories)
        awaitMain { vm.state.value.loaded }
        return vm
    }

    private fun CategoriesViewModel.names() = state.value.active.map { it.category.name }

    private fun trail(id: CategoryId) = blocking { kit.trailOf("category", id.value) }.map { it.action }

    private fun failWith(failure: Throwable = IllegalStateException("secret detail Akash")) {
        audit.failure = failure
    }

    private fun scalar(sql: String): Long =
        database.openHelper.readableDatabase.query(sql).use { cursor ->
            cursor.moveToFirst()
            cursor.getLong(0)
        }

    private fun categoryRows(): List<String> =
        database.openHelper.readableDatabase.query("SELECT * FROM category ORDER BY id").use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add((0 until cursor.columnCount).joinToString("|") { cursor.getString(it).orEmpty() })
                }
            }
        }

    private fun logLines() = ShadowLog.getLogs().map { it.msg }

    @Test
    fun `delete removes a sub-category, audits a DELETE and offers an undo of it`() {
        withDefaults()
        val vm = viewModel()

        vm.choose(id("food_delivery"), CategoryAction.DELETE)
        awaitMain { vm.state.value.undo != null }

        vm.state.value.undo shouldBe id("food_delivery")
        vm.names().take(5) shouldBe listOf("Food & dining", "Groceries", "Eating out", "Transport", "Fuel")
        trail(id("food_delivery")) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE)
        vm.state.value.notice shouldBe null
    }

    @Test
    fun `undo restores the deleted category in its place, audits a RESTORE and clears the offer`() {
        withDefaults()
        val vm = viewModel()
        vm.choose(id("shopping"), CategoryAction.DELETE)
        awaitMain { vm.state.value.undo != null }

        vm.undo()
        awaitMain { vm.state.value.undo == null }

        vm.names().indexOf("Shopping") shouldBe 8
        trail(id("shopping")) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE, AuditAction.RESTORE)
    }

    @Test
    fun `undo with no offer does nothing, and dismissing the offer keeps the delete`() {
        withDefaults()
        val vm = viewModel()
        val auditRows = scalar("SELECT COUNT(*) FROM audit_entry")
        vm.undo()
        repeat(5) { shadowOf(Looper.getMainLooper()).idle() }
        scalar("SELECT COUNT(*) FROM audit_entry") shouldBe auditRows

        vm.choose(id("shopping"), CategoryAction.DELETE)
        awaitMain { vm.state.value.undo != null }
        vm.dismissUndo()

        vm.state.value.undo shouldBe null
        vm.names().contains("Shopping") shouldBe false
        trail(id("shopping")) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE)
    }

    @Test
    fun `a second delete replaces the offer, so only the latest delete can be undone`() {
        withDefaults()
        val vm = viewModel()
        vm.choose(id("shopping"), CategoryAction.DELETE)
        awaitMain { vm.state.value.undo == id("shopping") }

        vm.choose(id("gifts"), CategoryAction.DELETE)
        awaitMain { vm.state.value.undo == id("gifts") }
        vm.undo()
        awaitMain { vm.state.value.undo == null }

        vm.names().contains("Gifts & donations") shouldBe true
        vm.names().contains("Shopping") shouldBe false
    }

    @Test
    fun `deleting a parent with sub-categories is refused with the fixed notice and writes nothing`() {
        withDefaults()
        val vm = viewModel()
        val rows = categoryRows()
        val auditRows = scalar("SELECT COUNT(*) FROM audit_entry")
        ShadowLog.clear()

        vm.choose(id("food"), CategoryAction.DELETE)
        awaitMain { vm.state.value.notice == CategoriesNotice.DeleteBlocked }

        categoryRows() shouldBe rows
        scalar("SELECT COUNT(*) FROM audit_entry") shouldBe auditRows
        vm.state.value.undo shouldBe null
        vm.state.value.notice shouldBe CategoriesNotice.DeleteBlocked
        vm.names().take(2) shouldBe listOf("Food & dining", "Groceries")
        // A refused delete is an answer, not a failure: nothing is logged.
        logLines() shouldBe emptyList()

        vm.noticeShown()
        vm.state.value.notice shouldBe null
    }

    @Test
    fun `a parent can be deleted once its sub-categories are, including an archived one`() {
        withDefaults()
        blocking { kit.categories.archive(id("food_groceries")) }
        val vm = viewModel()
        listOf("food_groceries", "food_eating_out", "food_delivery").forEach { key ->
            vm.choose(id(key), CategoryAction.DELETE)
            awaitMain { vm.state.value.undo == id(key) }
        }

        vm.choose(id("food"), CategoryAction.DELETE)
        awaitMain { vm.state.value.undo == id("food") }

        vm.state.value.notice shouldBe null
        vm.names().first() shouldBe "Transport"
        trail(id("food")) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE)
    }

    @Test
    fun `a failing audit on archive leaves the list unchanged and logs only the kind and the action`() {
        withDefaults()
        val vm = viewModel()
        val rows = categoryRows()
        failWith()
        ShadowLog.clear()

        vm.choose(id("shopping"), CategoryAction.ARCHIVE)
        awaitMain { vm.state.value.notice != null }

        audit.failedAppends shouldBe 1
        vm.state.value.notice shouldBe CategoriesNotice.Failed(EntityAction.ARCHIVE)
        vm.state.value.archived shouldBe emptyList()
        vm.names().contains("Shopping") shouldBe true
        categoryRows() shouldBe rows
        logLines() shouldBe listOf("entity_action_failed kind=CATEGORY action=ARCHIVE")

        vm.noticeShown()
        vm.state.value.notice shouldBe null
    }

    @Test
    fun `a failing move reports REORDER, writes nothing and logs it`() {
        withDefaults()
        val vm = viewModel()
        val rows = categoryRows()
        failWith()
        ShadowLog.clear()

        vm.choose(id("food"), CategoryAction.MOVE_DOWN)
        awaitMain { vm.state.value.notice != null }

        vm.state.value.notice shouldBe CategoriesNotice.Failed(EntityAction.REORDER)
        vm.names().first() shouldBe "Food & dining"
        categoryRows() shouldBe rows
        logLines() shouldBe listOf("entity_action_failed kind=CATEGORY action=REORDER")
    }

    @Test
    fun `a failing unarchive and delete report their own action`() {
        withDefaults()
        blocking { kit.categories.archive(id("shopping")) }
        val vm = viewModel()
        failWith()
        ShadowLog.clear()

        vm.choose(id("shopping"), CategoryAction.UNARCHIVE)
        awaitMain { vm.state.value.notice == CategoriesNotice.Failed(EntityAction.UNARCHIVE) }
        vm.noticeShown()
        vm.choose(id("gifts"), CategoryAction.DELETE)
        awaitMain { vm.state.value.notice == CategoriesNotice.Failed(EntityAction.DELETE) }

        vm.state.value.undo shouldBe null
        logLines() shouldBe
            listOf(
                "entity_action_failed kind=CATEGORY action=UNARCHIVE",
                "entity_action_failed kind=CATEGORY action=DELETE",
            )
    }

    @Test
    fun `a failing undo reports RESTORE, leaves the category deleted and withdraws the offer`() {
        withDefaults()
        val vm = viewModel()
        vm.choose(id("shopping"), CategoryAction.DELETE)
        awaitMain { vm.state.value.undo != null }
        failWith()
        ShadowLog.clear()

        vm.undo()
        awaitMain { vm.state.value.notice != null }

        vm.state.value.notice shouldBe CategoriesNotice.Failed(EntityAction.RESTORE)
        vm.state.value.undo shouldBe null
        vm.names().contains("Shopping") shouldBe false
        logLines() shouldBe listOf("entity_action_failed kind=CATEGORY action=RESTORE")
    }

    @Test
    fun `an action on a category that is gone is a failure too, logged, and the list is refreshed`() {
        withDefaults()
        val vm = viewModel()
        blocking { kit.categories.delete(id("shopping")) }
        ShadowLog.clear()

        vm.choose(id("shopping"), CategoryAction.ARCHIVE)
        awaitMain { vm.state.value.notice != null }

        vm.state.value.notice shouldBe CategoriesNotice.Failed(EntityAction.ARCHIVE)
        vm.names().contains("Shopping") shouldBe false
        logLines() shouldBe listOf("entity_action_failed kind=CATEGORY action=ARCHIVE")
    }

    @Test
    fun `a move of a category that is gone does nothing, with no failure`() {
        withDefaults()
        val vm = viewModel()
        blocking { kit.categories.delete(id("shopping")) }
        ShadowLog.clear()

        vm.choose(id("shopping"), CategoryAction.MOVE_UP)
        vm.reload()
        awaitMain { !vm.names().contains("Shopping") }

        vm.state.value.notice shouldBe null
        logLines() shouldBe emptyList()
    }

    @Test
    fun `a cancellation is not reported as a failure`() {
        withDefaults()
        val vm = viewModel()
        failWith(CancellationException("cancelled"))
        ShadowLog.clear()

        vm.choose(id("shopping"), CategoryAction.ARCHIVE)
        awaitMain { audit.failedAppends >= 1 }
        repeat(10) {
            shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(10)
        }

        vm.state.value.notice shouldBe null
        logLines() shouldBe emptyList()
    }

    @Test
    fun `two rapid actions both complete and leave a consistent list`() {
        withDefaults()
        val vm = viewModel()

        vm.choose(id("shopping"), CategoryAction.ARCHIVE)
        vm.choose(id("gifts"), CategoryAction.ARCHIVE)
        awaitMain { vm.state.value.archived.size == 2 }

        vm.state.value.archived
            .map { it.category.name } shouldBe listOf("Shopping", "Gifts & donations")
        vm.state.value.kind shouldBe CategoryKind.EXPENSE
    }
}
