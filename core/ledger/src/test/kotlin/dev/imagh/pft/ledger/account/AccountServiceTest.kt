package dev.imagh.pft.ledger.account

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.fakes.LedgerTestKit
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.matchers.shouldHaveAudited
import dev.imagh.pft.ledger.matchers.shouldNotHaveAudited
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.Duration

private class Fixture {
    val kit = LedgerTestKit()
    val service = AccountService(kit.writer, kit.accountRepository, kit.ids)
    val trail get() = kit.store.auditLog.entries

    fun draft(
        name: String = "HDFC Savings",
        type: AccountType = AccountType.BANK,
        opening: Paise = Paise(1_000_000),
        lastFour: String? = "1234",
        issuer: String? = "HDFC",
    ) = AccountDraft(name, type, opening, lastFour, issuer)

    /** The stored accounts: the rows themselves, not the service's view. */
    fun stored() = kit.accountRepository.all()

    suspend fun created(
        name: String = "HDFC Savings",
        lastFour: String? = "1234",
    ): Account = service.create(draft(name = name, lastFour = lastFour))
}

class AccountServiceTest :
    FunSpec({
        context("create") {
            test("stores the trimmed name, type, opening balance, last four and issuer, audited with who and where") {
                val f = Fixture()
                val me = f.kit.initialise()

                val account =
                    f.service.create(f.draft(name = "  HDFC Savings ", lastFour = " 4321 ", issuer = " HDFC Bank "))

                f.stored() shouldBe listOf(account)
                account.name shouldBe "HDFC Savings"
                account.type shouldBe AccountType.BANK
                account.openingBalance shouldBe Paise(1_000_000)
                account.maskedIdentifier shouldBe "4321"
                account.issuer shouldBe "HDFC Bank"
                account.meta.isActive shouldBe true
                account.meta.createdByUserId shouldBe me.user.id
                account.meta.createdOnDeviceId shouldBe me.device.id
                f.trail.shouldHaveAudited(
                    "account",
                    account.meta.id.value,
                    AuditAction.CREATE,
                    changes =
                        mapOf(
                            "name" to FieldChange(null, "HDFC Savings"),
                            "type" to FieldChange(null, "BANK"),
                            "opening_balance" to FieldChange(null, "1000000"),
                            "masked_identifier" to FieldChange(null, "4321"),
                            "issuer" to FieldChange(null, "HDFC Bank"),
                        ),
                    userId = me.user.id,
                    deviceId = me.device.id,
                )
            }

            test("each type is stored as chosen") {
                val f = Fixture()
                f.kit.initialise()
                AccountType.entries.forEach { type ->
                    f.service.create(f.draft(name = type.name, type = type)).type shouldBe type
                }
            }

            test("a positive, a zero and a negative opening balance are all stored as given") {
                val f = Fixture()
                f.kit.initialise()
                listOf(Paise(250_000), Paise.ZERO, Paise(-500_000)).forEach { opening ->
                    f.service.create(f.draft(opening = opening)).openingBalance shouldBe opening
                }
                f.stored().map { it.openingBalance } shouldBe listOf(Paise(250_000), Paise.ZERO, Paise(-500_000))
            }

            test("the largest balances in both directions are accepted") {
                val f = Fixture()
                f.kit.initialise()
                f.service.create(f.draft(opening = LedgerLimits.MAX_AMOUNT))
                f.service.create(f.draft(opening = Paise(-LedgerLimits.MAX_AMOUNT.value)))
                f.stored() shouldHaveSize 2
            }

            test("blank last four and blank issuer are stored as null") {
                val f = Fixture()
                f.kit.initialise()
                val blank = f.service.create(f.draft(lastFour = "  ", issuer = "   "))
                val absent = f.service.create(f.draft(lastFour = null, issuer = null))
                listOf(blank, absent).forEach {
                    it.maskedIdentifier shouldBe null
                    it.issuer shouldBe null
                }
            }

            test("a name of exactly the limit is accepted") {
                val f = Fixture()
                f.kit.initialise()
                f.service
                    .create(f.draft(name = "a".repeat(LedgerLimits.MAX_NAME_LENGTH)))
                    .name.length shouldBe
                    LedgerLimits.MAX_NAME_LENGTH
            }

            test("two accounts may share a name") {
                val f = Fixture()
                f.kit.initialise()
                f.created("Amex")
                f.created("Amex")
                f.stored() shouldHaveSize 2
            }

            context("rejections write nothing and audit nothing") {
                suspend fun Fixture.shouldRefuse(
                    draft: AccountDraft,
                    check: (Throwable) -> Unit,
                ) {
                    val before = trail.size
                    check(shouldThrow<LedgerError> { service.create(draft) })
                    stored().shouldBeEmpty()
                    trail shouldHaveSize before
                }

                test("a blank name") {
                    val f = Fixture()
                    f.kit.initialise()
                    f.shouldRefuse(f.draft(name = "  ")) { (it is LedgerError.BlankName) shouldBe true }
                }
                test("a name over the limit") {
                    val f = Fixture()
                    f.kit.initialise()
                    f.shouldRefuse(f.draft(name = "a".repeat(LedgerLimits.MAX_NAME_LENGTH + 1))) {
                        (it as LedgerError.NameTooLong).max shouldBe LedgerLimits.MAX_NAME_LENGTH
                    }
                }
                listOf("123", "12345", "12a4", "١٢٣٤", "१२३४").forEach { bad ->
                    test("last four '$bad'") {
                        val f = Fixture()
                        f.kit.initialise()
                        f.shouldRefuse(f.draft(lastFour = bad)) {
                            (it is LedgerError.InvalidMaskedIdentifier) shouldBe true
                        }
                    }
                }
                test("an issuer over the limit") {
                    val f = Fixture()
                    f.kit.initialise()
                    f.shouldRefuse(f.draft(issuer = "i".repeat(LedgerLimits.MAX_NAME_LENGTH + 1))) {
                        (it is LedgerError.NameTooLong) shouldBe true
                    }
                }
                test("an opening balance beyond the limit in either direction") {
                    val f = Fixture()
                    f.kit.initialise()
                    val max = LedgerLimits.MAX_AMOUNT
                    f.shouldRefuse(f.draft(opening = Paise(max.value + 1))) {
                        (it as LedgerError.AmountTooLarge).max shouldBe max
                    }
                    f.shouldRefuse(f.draft(opening = Paise(-max.value - 1))) {
                        (it is LedgerError.AmountTooLarge) shouldBe true
                    }
                }
                test("before first-run setup") {
                    val f = Fixture()
                    f.shouldRefuse(f.draft()) { (it is LedgerError.NotInitialised) shouldBe true }
                }
            }

            test("a failing audit append leaves no account behind (INT-4)") {
                val f = Fixture()
                f.kit.initialise()
                val trailSize = f.trail.size
                val failing =
                    object : AuditLog {
                        override suspend fun append(entry: AuditEntry) = error("audit store unavailable")
                    }
                val broken = AccountService(f.kit.writerWith(failing), f.kit.accountRepository, f.kit.ids)

                shouldThrow<IllegalStateException> { broken.create(f.draft()) }

                f.stored().shouldBeEmpty()
                f.trail shouldHaveSize trailSize
            }
        }

        context("edit") {
            test("audits an UPDATE listing only the fields that changed") {
                val f = Fixture()
                val me = f.kit.initialise()
                val account = f.created("HDFC Savings")
                f.kit.clock.advance(Duration.ofMinutes(5))

                val edited = f.service.edit(account.meta.id, f.draft(name = "HDFC Salary", issuer = null))

                edited.name shouldBe "HDFC Salary"
                edited.issuer shouldBe null
                edited.openingBalance shouldBe account.openingBalance
                edited.meta.createdAt shouldBe account.meta.createdAt
                (edited.meta.updatedAt > account.meta.updatedAt) shouldBe true
                f.stored() shouldBe listOf(edited)
                f.trail.shouldHaveAudited(
                    "account",
                    account.meta.id.value,
                    AuditAction.UPDATE,
                    changes =
                        mapOf(
                            "name" to FieldChange("HDFC Savings", "HDFC Salary"),
                            "issuer" to FieldChange("HDFC", null),
                        ),
                    userId = me.user.id,
                )
            }

            test("can change the type, the opening balance and the last four") {
                val f = Fixture()
                f.kit.initialise()
                val account = f.created()

                val edited =
                    f.service.edit(
                        account.meta.id,
                        f.draft(type = AccountType.CREDIT_CARD, opening = Paise(-250_000), lastFour = null),
                    )

                edited.type shouldBe AccountType.CREDIT_CARD
                edited.openingBalance shouldBe Paise(-250_000)
                edited.maskedIdentifier shouldBe null
            }

            test("an identical edit writes nothing and audits nothing") {
                val f = Fixture()
                f.kit.initialise()
                val account = f.created()
                val trailSize = f.trail.size

                val same = f.service.edit(account.meta.id, f.draft())

                same shouldBe account
                f.stored() shouldBe listOf(account)
                f.trail shouldHaveSize trailSize
            }

            test("an invalid draft is refused and the account is untouched") {
                val f = Fixture()
                f.kit.initialise()
                val account = f.created()
                val trailSize = f.trail.size

                shouldThrow<LedgerError.InvalidMaskedIdentifier> {
                    f.service.edit(account.meta.id, f.draft(name = "New name", lastFour = "12"))
                }

                f.stored() shouldBe listOf(account)
                f.trail shouldHaveSize trailSize
            }

            test("a deleted account cannot be edited, and an unknown id is not found") {
                val f = Fixture()
                f.kit.initialise()
                val account = f.created()
                f.service.delete(account.meta.id)

                shouldThrow<LedgerError.Deleted> { f.service.edit(account.meta.id, f.draft(name = "Other")) }
                    .what shouldBe "account"
                shouldThrow<LedgerError.NotFound> { f.service.edit(AccountId("missing"), f.draft()) }
                    .what shouldBe "account"
            }
        }

        context("archive and unarchive") {
            test("an archived account leaves active() and lists().active, and stays in all() and lists().archived") {
                val f = Fixture()
                val me = f.kit.initialise()
                val keep = f.created("Cash")
                val archived = f.created("Old card")

                f.service.archive(archived.meta.id)

                f.service.active().map { it.meta.id } shouldBe listOf(keep.meta.id)
                f.service.all().map { it.meta.id } shouldBe listOf(keep.meta.id, archived.meta.id)
                val lists = f.service.lists()
                lists.active.map { it.meta.id } shouldBe listOf(keep.meta.id)
                lists.archived.map { it.meta.id } shouldBe listOf(archived.meta.id)
                f.trail.shouldHaveAudited(
                    "account",
                    archived.meta.id.value,
                    AuditAction.ARCHIVE,
                    userId = me.user.id,
                    deviceId = me.device.id,
                )
            }

            test("unarchive reverses it and is audited") {
                val f = Fixture()
                f.kit.initialise()
                val account = f.created()
                f.service.archive(account.meta.id)

                f.service.unarchive(account.meta.id)

                f.service
                    .lists()
                    .active
                    .map { it.meta.id } shouldBe listOf(account.meta.id)
                f.service
                    .lists()
                    .archived
                    .shouldBeEmpty()
                f.trail.shouldHaveAudited("account", account.meta.id.value, AuditAction.UNARCHIVE)
            }

            test("repeating either is a no-op that audits nothing more") {
                val f = Fixture()
                f.kit.initialise()
                val account = f.created()
                f.service.unarchive(account.meta.id) // not archived: nothing to do
                f.trail.shouldNotHaveAudited("account", account.meta.id.value, AuditAction.UNARCHIVE)
                f.service.archive(account.meta.id)
                val trailSize = f.trail.size

                f.service.archive(account.meta.id)

                f.trail shouldHaveSize trailSize
            }

            test("an archived account can still be edited") {
                val f = Fixture()
                f.kit.initialise()
                val account = f.created()
                f.service.archive(account.meta.id)

                f.service.edit(account.meta.id, f.draft(name = "Renamed")).name shouldBe "Renamed"
            }
        }

        context("delete and restore") {
            test("a deleted account is absent from every list and its row is a tombstone") {
                val f = Fixture()
                val me = f.kit.initialise()
                val gone = f.created("Gone")
                val goneArchived = f.created("Gone archived")
                f.service.archive(goneArchived.meta.id)

                f.service.delete(gone.meta.id)
                f.service.delete(goneArchived.meta.id)

                f.service.active().shouldBeEmpty()
                f.service.all().shouldBeEmpty()
                f.service
                    .lists()
                    .active
                    .shouldBeEmpty()
                f.service
                    .lists()
                    .archived
                    .shouldBeEmpty()
                f.stored() shouldHaveSize 2
                f.stored().all { it.meta.isDeleted } shouldBe true
                f.trail.shouldHaveAudited(
                    "account",
                    gone.meta.id.value,
                    AuditAction.DELETE,
                    userId = me.user.id,
                    deviceId = me.device.id,
                )
            }

            test("restore brings it back, audited, and keeps its fields") {
                val f = Fixture()
                f.kit.initialise()
                val account = f.created()
                f.service.delete(account.meta.id)

                val restored = f.service.restore(account.meta.id)

                restored.name shouldBe account.name
                restored.meta.isDeleted shouldBe false
                f.service.active().map { it.meta.id } shouldBe listOf(account.meta.id)
                f.trail.shouldHaveAudited("account", account.meta.id.value, AuditAction.RESTORE)
            }

            test("restoring an account that was archived before it was deleted returns it archived") {
                val f = Fixture()
                f.kit.initialise()
                val account = f.created()
                f.service.archive(account.meta.id)
                f.service.delete(account.meta.id)

                f.service.restore(account.meta.id)

                f.service
                    .lists()
                    .archived
                    .map { it.meta.id } shouldBe listOf(account.meta.id)
            }

            test("repeating delete or restore is a no-op") {
                val f = Fixture()
                f.kit.initialise()
                val account = f.created()
                f.service.restore(account.meta.id) // not deleted
                f.service.delete(account.meta.id)
                f.service.delete(account.meta.id)
                val trailSize = f.trail.size

                f.service.delete(account.meta.id)

                f.trail shouldHaveSize trailSize
                f.trail.count { it.action == AuditAction.DELETE } shouldBe 1
                f.trail.count { it.action == AuditAction.RESTORE } shouldBe 0
            }

            test("an unknown id is not found") {
                val f = Fixture()
                f.kit.initialise()
                val missing = AccountId("missing")
                shouldThrow<LedgerError.NotFound> { f.service.delete(missing) }.what shouldBe "account"
                shouldThrow<LedgerError.NotFound> { f.service.restore(missing) }.what shouldBe "account"
                shouldThrow<LedgerError.NotFound> { f.service.archive(missing) }.what shouldBe "account"
                shouldThrow<LedgerError.NotFound> { f.service.unarchive(missing) }.what shouldBe "account"
            }

            test("a deleted account cannot be archived or unarchived") {
                val f = Fixture()
                f.kit.initialise()
                val account = f.created()
                f.service.delete(account.meta.id)
                shouldThrow<LedgerError.Deleted> { f.service.archive(account.meta.id) }
                shouldThrow<LedgerError.Deleted> { f.service.unarchive(account.meta.id) }
            }
        }

        context("lists") {
            test("active() is sorted by name ignoring case, and every list uses the same order") {
                val f = Fixture()
                f.kit.initialise()
                listOf("zebra", "Apple", "banana", "Cherry").forEach { f.created(it) }
                f.service.archive(
                    f.service
                        .all()
                        .first { it.name == "zebra" }
                        .meta.id,
                )
                f.service.archive(
                    f.service
                        .all()
                        .first { it.name == "Apple" }
                        .meta.id,
                )

                f.service.active().map { it.name } shouldBe listOf("banana", "Cherry")
                f.service.all().map { it.name } shouldBe listOf("Apple", "banana", "Cherry", "zebra")
                f.service
                    .lists()
                    .archived
                    .map { it.name } shouldBe listOf("Apple", "zebra")
            }

            test("accounts with the same name, ignoring case, keep their creation order in every list") {
                val f = Fixture()
                f.kit.initialise()
                val first = f.created("HDFC Card")
                val second = f.created("hdfc card")
                val third = f.created("HDFC Card")
                val creationOrder = listOf(first, second, third).map { it.meta.id }

                f.service.all().map { it.meta.id } shouldBe creationOrder
                f.service.active().map { it.meta.id } shouldBe creationOrder
                f.service
                    .lists()
                    .active
                    .map { it.meta.id } shouldBe creationOrder
            }

            test("there is nothing before any account exists") {
                val f = Fixture()
                f.kit.initialise()
                f.service
                    .lists()
                    .active
                    .shouldBeEmpty()
                f.service
                    .lists()
                    .archived
                    .shouldBeEmpty()
                f.service.active().shouldBeEmpty()
                f.service.all().shouldBeEmpty()
            }
        }
    })
