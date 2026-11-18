package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.transaction.LedgerTransaction
import dev.imagh.pft.ledger.transaction.TransactionRepository

/** The in-memory [TransactionRepository]: the generic fake, shared with the budget and recurring tests. */
class InMemoryTransactionRepository(
    ledger: InMemoryLedgerStore,
) : InMemoryEntityRepository<LedgerTransaction, TransactionId>(ledger, "ledger_transaction"),
    TransactionRepository
