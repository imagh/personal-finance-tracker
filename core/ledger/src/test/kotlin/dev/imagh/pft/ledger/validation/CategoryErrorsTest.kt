package dev.imagh.pft.ledger.validation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * The error cases the categories add (plan B3a). Their messages are fixed text: an error reaches
 * logs and the diagnostics screen, so it names the kind of record, never a name the user typed.
 */
class CategoryErrorsTest :
    FunSpec({
        test("each message is fixed text") {
            LedgerError.DepthExceeded().message shouldBe "Categories can only be nested two levels"
            LedgerError.KindMismatch().message shouldBe "A sub-category must be the same kind as its parent"
            LedgerError.HasChildren().message shouldBe "Delete the sub-categories first"
            LedgerError.InvalidOrder().message shouldBe "The order does not match the current categories"
            LedgerError.InvalidIcon().message shouldBe "The icon is not valid"
            LedgerError.SystemCategoryProtected().message shouldBe "This category cannot be archived or deleted"
        }

        test("DuplicateName names the kind of record, never the name") {
            val error = LedgerError.DuplicateName("category")
            error.what shouldBe "category"
            error.message shouldBe "A category with this name already exists"
        }
    })
