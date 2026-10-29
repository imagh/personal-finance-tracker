package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.tx.TransactionRunner

/**
 * The in-memory persistence for core tests: it is the fake [TransactionRunner] and the home of
 * every fake table, so atomicity is testable in core without Room (plan §3.2 decision 14).
 *
 * [inTransaction] snapshots every store created through [newStore] when the outermost block
 * starts and puts them all back if the block throws, which is how a service test can assert that
 * a failure leaves neither the write nor its audit entry behind. Not thread-safe; tests are
 * sequential.
 *
 * Later slices add their fake repositories by creating their tables with [newStore].
 */
class InMemoryLedgerStore : TransactionRunner {
    private val stores = mutableListOf<InMemoryEntityStore<*, *>>()
    private var depth = 0

    /** The audit trail, rolled back together with everything else. */
    val auditLog = InMemoryAuditLog(this)

    /** A new table that takes part in transactions. [idOf] extracts a row's primary key. */
    fun <T, Id : Any> newStore(idOf: (T) -> Id): InMemoryEntityStore<T, Id> =
        InMemoryEntityStore<T, Id>(idOf).also { stores += it }

    override suspend fun <T> inTransaction(block: suspend () -> T): T {
        val restore = if (depth == 0) stores.map { it.snapshot() } else emptyList()
        depth++
        var completed = false
        try {
            val result = block()
            completed = true
            return result
        } finally {
            depth--
            if (!completed && depth == 0) restore.forEach { it() }
        }
    }
}
