package dev.imagh.pft.ledger.tx

/**
 * Runs a block of repository writes as one atomic unit (INT-4): all of it commits, or none of it.
 *
 * A swap boundary like the repositories: core defines it, `app` implements it with Room's
 * `withTransaction`, and core tests use the in-memory fake that snapshots and restores its state
 * on failure. Every mutating service call, including its audit entry, runs inside one block, so a
 * failure can never leave a change without its audit row or the reverse.
 */
interface TransactionRunner {
    /**
     * Runs [block] atomically and returns its result. If [block] throws, every write it made is
     * rolled back and the exception propagates. Blocks may be nested; a failure anywhere rolls the
     * whole outermost block back.
     */
    suspend fun <T> inTransaction(block: suspend () -> T): T
}
