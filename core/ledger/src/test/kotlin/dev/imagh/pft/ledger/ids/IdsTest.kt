package dev.imagh.pft.ledger.ids

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IdsTest :
    FunSpec({
        test("typed ids keep the text they were given") {
            UserId("u-1").value shouldBe "u-1"
            DeviceId("d-1").value shouldBe "d-1"
            AuditEntryId("a-1").value shouldBe "a-1"
        }

        test("a blank id is rejected, whatever the type") {
            shouldThrow<IllegalArgumentException> { UserId(" ") }
            shouldThrow<IllegalArgumentException> { DeviceId("") }
            shouldThrow<IllegalArgumentException> { AuditEntryId("\t") }
        }

        test("ids of different entities are distinct types with equal text") {
            (UserId("same") as EntityId).value shouldBe (DeviceId("same") as EntityId).value
            (UserId("same") as Any == DeviceId("same") as Any) shouldBe false
        }
    })
