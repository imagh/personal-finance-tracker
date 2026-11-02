package dev.imagh.pft.app.data

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import dev.imagh.pft.ledger.tx.TransactionRunner

/**
 * The Room implementation of core's [TransactionRunner] (INT-4): [inTransaction] is
 * [withTransaction] on the database, so every DAO call in the block shares one SQLite transaction
 * and the write, its audit entry and anything else in the block commit together or not at all.
 *
 * Blocks nest. Room (like SQLite on Android) rolls the *outermost* transaction back when any
 * nested block fails, even if an outer block catches the exception, which is exactly the
 * semantics of core's in-memory fake; `RoomTransactionRunnerTest` pins it.
 */
class RoomTransactionRunner(
    private val database: RoomDatabase,
) : TransactionRunner {
    override suspend fun <T> inTransaction(block: suspend () -> T): T = database.withTransaction { block() }
}
