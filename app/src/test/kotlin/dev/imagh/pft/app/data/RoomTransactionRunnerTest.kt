package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.identity.RoomIdentityRepository
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.identity.UserProfile
import dev.imagh.pft.ledger.ids.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * [RoomTransactionRunner] against a real in-memory database (INT-4). The nesting cases pin the
 * contract core's fake runner already has: a failure at any depth rolls back the **outermost**
 * block, even when an outer block catches the exception and carries on.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomTransactionRunnerTest {
    private val database = TestDatabase.create()
    private val runner = RoomTransactionRunner(database)
    private val users = RoomIdentityRepository(database.identityDao())

    @After
    fun closeDatabase() = database.close()

    private suspend fun storedUserIds() = users.users().map { it.id.value }

    private suspend fun add(id: String) = users.insertUser(UserProfile(UserId(id), "Name $id"))

    @Test
    fun `a block that completes is committed and its result returned`() =
        runSuspending {
            val result =
                runner.inTransaction {
                    add("u1")
                    "done"
                }

            result shouldBe "done"
            storedUserIds() shouldBe listOf("u1")
        }

    @Test
    fun `a block that writes then throws leaves no rows and the exception propagates`() =
        runSuspending {
            val failure =
                shouldThrow<IllegalStateException> {
                    runner.inTransaction {
                        add("u1")
                        error("boom")
                    }
                }

            failure.message shouldBe "boom"
            storedUserIds() shouldBe emptyList()
        }

    @Test
    fun `nested blocks that complete are all committed`() =
        runSuspending {
            runner.inTransaction {
                add("outer")
                runner.inTransaction { add("inner") }
            }

            storedUserIds() shouldBe listOf("outer", "inner")
        }

    @Test
    fun `an outer failure after a completed inner block rolls the inner writes back too`() =
        runSuspending {
            shouldThrow<IllegalStateException> {
                runner.inTransaction {
                    runner.inTransaction { add("inner") }
                    error("boom")
                }
            }

            storedUserIds() shouldBe emptyList()
        }

    @Test
    fun `an inner failure that the outer block swallows still rolls back the whole outer block`() =
        runSuspending {
            runner.inTransaction {
                add("outer")
                try {
                    runner.inTransaction {
                        add("inner")
                        error("boom")
                    }
                } catch (_: IllegalStateException) {
                    // Swallowed on purpose: the outer block carries on and returns normally.
                }
            }

            storedUserIds() shouldBe emptyList()
        }

    @Test
    fun `the runner is usable again after a rolled back block`() =
        runSuspending {
            shouldThrow<IllegalStateException> { runner.inTransaction { error("boom") } }

            runner.inTransaction { add("u1") }

            storedUserIds() shouldBe listOf("u1")
        }
}
