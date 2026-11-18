package dev.imagh.pft.ledger.account

import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AccountRulesTest :
    FunSpec({
        context("requireMaskedIdentifier") {
            test("exactly four ASCII digits are kept") {
                AccountRules.requireMaskedIdentifier("1234") shouldBe "1234"
                AccountRules.requireMaskedIdentifier("0000") shouldBe "0000"
            }
            test("blank and null become null") {
                AccountRules.requireMaskedIdentifier(null) shouldBe null
                AccountRules.requireMaskedIdentifier("") shouldBe null
                AccountRules.requireMaskedIdentifier("   ") shouldBe null
            }
            test("surrounding whitespace is trimmed before the check") {
                AccountRules.requireMaskedIdentifier(" 1234 ") shouldBe "1234"
            }
            test("anything else is rejected: wrong length, letters, inner spaces, non-ASCII digits") {
                listOf("123", "12345", "12a4", "12 34", "-123", "١٢٣٤", "१२३४", "１２３４").forEach { bad ->
                    shouldThrow<LedgerError.InvalidMaskedIdentifier> { AccountRules.requireMaskedIdentifier(bad) }
                }
            }
        }
    })
