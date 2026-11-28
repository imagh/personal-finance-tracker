package dev.imagh.pft.ledger.transaction

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Reading a transfer back by its group id ([TransferService.get], slice C C4b): what the edit form opens a
 * stored transfer from. Live only, both legs with their lines, and nothing written.
 */
class TransferReadTest :
    FunSpec({
        /** A started fixture with a Rs 500 transfer HDFC to SBI tagged Travel. */
        suspend fun made(): Pair<TransactionFixture, Transfer> {
            val f = TransactionFixture().start()
            val b = f.newAccount("SBI")
            val draft = f.transferDraft(f.account.meta.id, b.meta.id, tagIds = setOf(f.travel.meta.id))
            return f to f.transfers.create(draft)
        }

        test("a stored transfer reads back as its two legs, each with its one line, and writes nothing") {
            val (f, made) = made()
            val before = f.trail.size

            val read = f.transfers.get(made.groupId)

            read shouldBe made
            f.trail.size shouldBe before
        }

        test("an edited transfer reads back as it is now, not as it was made") {
            val (f, made) = made()
            val edited =
                f.transfers.edit(
                    made.groupId,
                    f.transferDraft(
                        made.into.transaction.accountId,
                        made.out.transaction.accountId,
                        amount = 70_000,
                        title = "Back",
                    ),
                )

            f.transfers.get(made.groupId) shouldBe edited
        }

        test("an unknown group id is null") {
            val (f, _) = made()

            f.transfers.get("no-such-group") shouldBe null
        }

        test("a deleted transfer is null, and live again after a restore") {
            val (f, made) = made()
            f.service.delete(made.out.transaction.meta.id)

            f.transfers.get(made.groupId) shouldBe null

            f.service.restore(made.out.transaction.meta.id)
            f.transfers.get(made.groupId)?.groupId shouldBe made.groupId
        }

        test("a group that is not one OUT and one IN leg fails closed instead of reading half a transfer") {
            val (f, made) = made()
            f.kit.writer.delete(f.kit.transactionRepository, made.into.transaction.meta.id)

            // One live leg left: neither null (the transfer exists) nor a pair.
            val failure = runCatching { f.transfers.get(made.groupId) }.exceptionOrNull()

            (failure is IllegalStateException) shouldBe true
        }
    })
