package dev.imagh.pft.ledger.ids

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch

class NameBasedIdTest :
    FunSpec({
        test("the same inputs always give the same ID") {
            val a = NameBasedId.of("recurring-txn", "rule-1", "2026-11-05")
            val b = NameBasedId.of("recurring-txn", "rule-1", "2026-11-05")
            a shouldBe b
        }

        test("different parts give different IDs") {
            val base = NameBasedId.of("recurring-txn", "rule-1", "2026-11-05")
            NameBasedId.of("recurring-txn", "rule-2", "2026-11-05") shouldNotBe base
            NameBasedId.of("recurring-txn", "rule-1", "2026-11-06") shouldNotBe base
        }

        test("the same parts under a different kind give different IDs") {
            NameBasedId.of("kind-a", "x") shouldNotBe NameBasedId.of("kind-b", "x")
        }

        test("how the parts are split matters: ab|c is not a|bc") {
            NameBasedId.of("k", "ab", "c") shouldNotBe NameBasedId.of("k", "a", "bc")
            NameBasedId.of("k", "a:b") shouldNotBe NameBasedId.of("k", "a", "b")
            NameBasedId.of("k") shouldNotBe NameBasedId.of("k", "")
        }

        test("is a version 5, variant 10 UUID in the canonical text form") {
            val id = NameBasedId.of("recurring-txn", "rule-1", "2026-11-05")
            id shouldMatch Regex("[0-9a-f]{8}-[0-9a-f]{4}-5[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")
        }

        test("uuid5 reproduces the RFC 9562 reference vector") {
            val dnsNamespace = "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
            NameBasedId.uuid5(dnsNamespace, "www.example.com") shouldBe "2ed6657d-e927-568b-95e1-2665a8aea6a2"
        }

        test("IDs are pinned, so a refactor cannot change IDs devices have already stored") {
            // Expected values were computed independently (Python uuid.uuid5 over the documented
            // "<utf8 length>:<text>" encoding in the app namespace). Two devices, and a later sync,
            // must derive the very same ID for the same rule and date.
            NameBasedId.of("recurring-txn", "rule-1", "2026-11-05") shouldBe "15a3e5ae-16fe-53c1-862b-9b4a9798294b"
            NameBasedId.of("k", "a") shouldBe "ecc20ed5-cfd2-5c29-a156-9d3f69181cf9"
        }

        test("rejects a namespace that is not a UUID") {
            io.kotest.assertions.throwables.shouldThrow<IllegalArgumentException> {
                NameBasedId.uuid5("not-a-uuid", "x")
            }
        }
    })
