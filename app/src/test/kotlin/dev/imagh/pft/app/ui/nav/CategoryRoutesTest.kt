package dev.imagh.pft.app.ui.nav

import dev.imagh.pft.ledger.category.CategoryKind
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The category form's route string (plan D13): only ids and enum names go in it, each encoded, and an
 * argument that is not given is left out, so a route for a new category carries just its kind.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CategoryRoutesTest {
    @Test
    fun `with no arguments the route is just the form's path`() {
        Routes.categoryEdit() shouldBe "categories/edit"
    }

    @Test
    fun `a new category of a kind carries only its kind`() {
        Routes.categoryEdit(kind = CategoryKind.INCOME) shouldBe "categories/edit?kind=INCOME"
    }

    @Test
    fun `editing carries only the id`() {
        Routes.categoryEdit(id = "abc-123") shouldBe "categories/edit?id=abc-123"
    }

    @Test
    fun `a new sub-category carries its parent and its kind`() {
        Routes.categoryEdit(parent = "p1", kind = CategoryKind.EXPENSE) shouldBe
            "categories/edit?parent=p1&kind=EXPENSE"
    }

    @Test
    fun `an argument is encoded, so nothing in it can add or end an argument`() {
        Routes.categoryEdit(id = "a b&kind=INCOME#x") shouldBe "categories/edit?id=a%20b%26kind%3DINCOME%23x"
    }

    @Test
    fun `the route pattern names the three arguments`() {
        Routes.CATEGORY_EDIT shouldBe "categories/edit?id={id}&parent={parent}&kind={kind}"
    }
}
