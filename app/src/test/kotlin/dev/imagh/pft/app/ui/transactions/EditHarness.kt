package dev.imagh.pft.app.ui.transactions

import dev.imagh.pft.app.data.QueryWorld
import dev.imagh.pft.app.data.QueryWorld.Companion.ist
import dev.imagh.pft.app.data.audit.RoomAuditLog
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.transaction.TransactionWithLines
import kotlinx.coroutines.runBlocking
import java.time.Clock

/**
 * The world every transaction-form ViewModel test starts from: real in-memory Room through the production
 * wiring ([RoomTestKit]), the household of [QueryWorld] (accounts HDFC, HDFC Card and SBI; Food with
 * Groceries and Dining, Travel, Salary; tags Trip and Gift), and a fixed clock: 2026-11-15 10:00 in
 * `Asia/Kolkata`. [failure] makes every audit append throw, so any write fails and Room rolls it back.
 */
internal class EditHarness {
    val database = TestDatabase.create()
    private val audit = FailsOnDemand(RoomAuditLog(database.auditDao()))
    val kit = RoomTestKit(database, auditLog = audit)
    val world = QueryWorld(kit)
    val now: Clock = Clock.fixed(ist(2026, 11, 15, 10), RoomTestKit.ZONE)

    /** The audit log wrapper: real until [failure] is set. */
    private class FailsOnDemand(
        private val real: AuditLog,
    ) : AuditLog {
        var failure: Throwable? = null

        override suspend fun append(entry: AuditEntry) {
            failure?.let { throw it }
            real.append(entry)
        }
    }

    /** Every audit append throws [failure] from now on (null: back to normal). */
    var failure: Throwable?
        get() = audit.failure
        set(value) {
            audit.failure = value
        }

    fun <T> blocking(block: suspend () -> T): T = runBlocking { block() }

    fun start(): EditHarness {
        blocking { world.start() }
        return this
    }

    fun close() = database.close()

    /** Opens the form with [id] and [type] as a route would pass them; waits for the load unless [wait] is false. */
    fun open(
        id: String? = null,
        type: String? = null,
        wait: Boolean = true,
        clock: Clock = now,
    ): TransactionEditViewModel {
        val vm =
            TransactionEditViewModel(
                EditStore(kit.transactions, kit.transfers),
                EditLookups(kit.accounts, kit.categories, kit.tags),
                { clock },
                TransactionEditRequest(id, type),
            )
        if (wait) awaitMain { vm.state.value.loaded || vm.state.value.notFound }
        return vm
    }

    /** The ids of every stored transaction row, tombstones included. */
    fun storedIds(): List<String> =
        database.openHelper.readableDatabase.query("SELECT id FROM ledger_transaction ORDER BY id").use { cursor ->
            buildList { while (cursor.moveToNext()) add(cursor.getString(0)) }
        }

    fun auditRows(): Long =
        database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM audit_entry").use { cursor ->
            cursor.moveToFirst()
            cursor.getLong(0)
        }

    /** The one live transaction with its lines. */
    fun only(): TransactionWithLines = checkNotNull(get(storedIds().single()))

    /** Waits for [vm]'s save to end one way or the other (a refused Save never starts, so it returns at once). */
    fun awaitSaveEnd(vm: TransactionEditViewModel) = awaitMain { vm.state.value.saved || !vm.state.value.saving }

    fun get(id: String): TransactionWithLines? = blocking { kit.transactions.get(TransactionId(id)) }
}
