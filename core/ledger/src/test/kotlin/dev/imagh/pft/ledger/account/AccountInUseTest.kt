package dev.imagh.pft.ledger.account

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.transaction.TransactionFixture
import dev.imagh.pft.ledger.transaction.TransactionType
import dev.imagh.pft.ledger.transaction.shouldRefuseInUse
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/** An account a live transaction is on cannot be deleted: archive it instead (slice C C1d, D4, Q4; PRD L-2, L-4). */
class AccountInUseTest :
    FunSpec({
        context("delete") {
            test("is refused while a live transaction is on the account: no tombstone and no audit entry") {
                val f = TransactionFixture().start()
                f.service.create(f.draft())

                val error = f.shouldRefuseInUse("account") { f.kit.accounts.delete(f.account.meta.id) }

                error.message shouldBe "This account is in use by transactions"
                f.kit.accountRepository
                    .all()
                    .single()
                    .meta.isDeleted shouldBe false
            }

            test("is refused for an archived account too, and archiving is still allowed while in use") {
                val f = TransactionFixture().start()
                f.service.create(f.draft())

                f.kit.accounts
                    .archive(f.account.meta.id)
                    .meta.isArchived shouldBe true

                f.shouldRefuseInUse("account") { f.kit.accounts.delete(f.account.meta.id) }
            }

            test("is refused while a transfer leg is on the account") {
                val f = TransactionFixture().start()
                f.seed(type = TransactionType.TRANSFER)

                f.shouldRefuseInUse("account") { f.kit.accounts.delete(f.account.meta.id) }
            }

            test("succeeds once the only transaction is deleted, and is refused again when it is restored") {
                val f = TransactionFixture().start()
                val saved = f.service.create(f.draft())
                f.service.delete(saved.transaction.meta.id)

                f.kit.accounts
                    .delete(f.account.meta.id)
                    .meta.isDeleted shouldBe true
                f.trail.shouldHaveAudited("account", f.account.meta.id.value, AuditAction.DELETE)

                f.kit.accounts.restore(f.account.meta.id)
                f.service.restore(saved.transaction.meta.id)

                f.shouldRefuseInUse("account") { f.kit.accounts.delete(f.account.meta.id) }
            }

            test("counts only transactions on this account: another account without any is deleted") {
                val f = TransactionFixture().start()
                f.service.create(f.draft())
                val spare = f.kit.accounts.create(AccountDraft("Spare", AccountType.CASH, f.account.openingBalance))

                f.kit.accounts
                    .delete(spare.meta.id)
                    .meta.isDeleted shouldBe true

                f.kit.accountRepository
                    .all()
                    .single { it.meta.id == f.account.meta.id }
                    .meta.isDeleted shouldBe false
            }
        }
    })
