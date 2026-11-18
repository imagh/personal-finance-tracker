package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.TransactionId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.ledger.matchers.shouldHaveCompleteAuditFields
import dev.imagh.pft.money.Paise
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant

/** The transaction's payment facts and their audit keys (slice C D2; IT §3.1). */
class LedgerTransactionTest :
    FunSpec({
        val at = Instant.parse("2026-11-18T10:15:30Z")

        fun populated() =
            LedgerTransaction(
                meta = EntityMeta(TransactionId("t-1"), at, at, UserId("u-1"), DeviceId("d-1")),
                type = TransactionType.TRANSFER,
                direction = Direction.IN,
                amount = Paise(12_345),
                occurredAt = Instant.parse("2026-11-17T08:00:00Z"),
                accountId = AccountId("a-1"),
                title = "Rent",
                description = "November",
                state = TxnState.UNVERIFIED,
                source = TxnSource.SMS,
                bankRef = "UTR123",
                sourceMessageId = "m-1",
                accountFragment = "1234",
                transferGroupId = "g-1",
            )

        test("a fully populated transaction lists every persisted field in auditFields()") {
            populated().shouldHaveCompleteAuditFields()
        }

        test("audits each field under its column name, as text") {
            populated().auditFields() shouldBe
                mapOf(
                    "type" to "TRANSFER",
                    "direction" to "IN",
                    "amount" to "12345",
                    "occurred_at" to "2026-11-17T08:00:00Z",
                    "account_id" to "a-1",
                    "title" to "Rent",
                    "description" to "November",
                    "state" to "UNVERIFIED",
                    "source" to "SMS",
                    "bank_ref" to "UTR123",
                    "source_message_id" to "m-1",
                    "account_fragment" to "1234",
                    "transfer_group_id" to "g-1",
                )
        }

        test("is audited as ledger_transaction under its id") {
            populated().auditType shouldBe "ledger_transaction"
            populated().auditId shouldBe "t-1"
        }
    })
