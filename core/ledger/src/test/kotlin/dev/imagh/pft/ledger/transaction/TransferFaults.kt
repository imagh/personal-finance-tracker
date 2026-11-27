package dev.imagh.pft.ledger.transaction

/**
 * Stores that fail on one chosen write, to prove a transfer is all or nothing: a failure on the **second**
 * leg, after the first leg's rows were really written. The failure is at the store boundary (what Room
 * would do on a constraint failure), not a mock assertion.
 */
internal class FailingTransactions(
    private val real: TransactionRepository,
    private val failOnInsert: Int = Int.MAX_VALUE,
    private val failOnUpdate: Int = Int.MAX_VALUE,
) : TransactionRepository by real {
    private var inserts = 0
    private var updates = 0

    /** Fails the [failOnInsert]th insert (1-based); the earlier ones are really written. */
    override suspend fun insert(entity: LedgerTransaction) {
        inserts++
        check(inserts != failOnInsert) { "store failure" }
        real.insert(entity)
    }

    /** Fails the [failOnUpdate]th update (1-based); the earlier ones are really written. */
    override suspend fun update(entity: LedgerTransaction) {
        updates++
        check(updates != failOnUpdate) { "store failure" }
        real.update(entity)
    }
}

/** A [TransactionLineRepository] whose [failOnInsert]th insert or [failOnUpdate]th update (1-based) fails. */
internal class FailingLines(
    private val real: TransactionLineRepository,
    private val failOnInsert: Int = Int.MAX_VALUE,
    private val failOnUpdate: Int = Int.MAX_VALUE,
) : TransactionLineRepository by real {
    private var inserts = 0
    private var updates = 0

    override suspend fun insert(entity: TransactionLine) {
        inserts++
        check(inserts != failOnInsert) { "store failure" }
        real.insert(entity)
    }

    override suspend fun update(entity: TransactionLine) {
        updates++
        check(updates != failOnUpdate) { "store failure" }
        real.update(entity)
    }
}
