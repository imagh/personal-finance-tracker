package dev.imagh.pft.app.ui.categories

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.data.common.EpochMillis
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.ids.CategoryId
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * [CategoriesRoute]'s behaviour that needs the lifecycle and the caller (plan D2 and D13): the list is
 * read again when the screen resumes (which is how a category saved on the form shows up on return),
 * the add button and a row tap reach the caller with the kind on show and the category's id, and a
 * "not found" flag left by the form shows the "no longer exists" message once before it is cleared.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class CategoriesRouteTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val database: PftDatabase = TestDatabase.create()
    private val services = AppServices(database, EpochMillis.clock())

    private val owner =
        object : LifecycleOwner {
            val registry = LifecycleRegistry(this).also { it.currentState = Lifecycle.State.RESUMED }
            override val lifecycle: Lifecycle get() = registry
        }

    @After
    fun closeDatabase() = database.close()

    private fun create(
        name: String,
        kind: CategoryKind = CategoryKind.EXPENSE,
    ): CategoryId =
        runBlocking {
            services.categories
                .create(null, kind, CategoryDraft(name, CategoryColour.AMBER))
                .meta.id
        }

    private var notFound by mutableStateOf(false)
    private var notFoundShown = 0
    private val added = mutableListOf<CategoryKind>()
    private val edited = mutableListOf<CategoryId>()

    private fun show() {
        runBlocking { services.identity.initialise("Akash", "Pixel") }
        create("Food")
        compose.setContent {
            CompositionLocalProvider(LocalLifecycleOwner provides owner) {
                PftTheme(ThemePrefs(dynamicColor = false)) {
                    CategoriesRoute(
                        services,
                        CategoriesNavigation(back = {}, add = { added += it }, edit = { edited += it }),
                        notFound = notFound,
                        onNotFoundShown = {
                            notFoundShown++
                            notFound = false
                        },
                    )
                }
            }
        }
        awaitText("Food")
    }

    private fun awaitText(text: String) =
        awaitMain {
            compose.waitForIdle()
            compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }

    private fun moveTo(state: Lifecycle.State) {
        compose.runOnUiThread { owner.registry.currentState = state }
        compose.waitForIdle()
    }

    @Test
    fun `a category added while the screen was away shows once the screen resumes, and not before`() {
        show()
        create("Zeta")

        compose.waitForIdle()
        compose.onAllNodesWithText("Zeta").fetchSemanticsNodes().size shouldBe 0

        moveTo(Lifecycle.State.STARTED)
        moveTo(Lifecycle.State.RESUMED)
        awaitText("Zeta")

        compose.onNodeWithText("Zeta").assertIsDisplayed()
        compose.onNodeWithText("Food").assertIsDisplayed()
    }

    @Test
    fun `the Add button reaches the caller with the kind on show, and a row tap with the category's id`() {
        show()
        val id =
            runBlocking { services.categories.lists(CategoryKind.EXPENSE) }
                .active
                .single()
                .meta.id

        compose.onNodeWithContentDescription("Add category").performClick()
        compose.onNodeWithText("Income").performClick()
        awaitText("No categories yet")
        compose.onNodeWithContentDescription("Add category").performClick()
        compose.onNodeWithText("Expense").performClick()
        awaitText("Food")
        compose.onNodeWithText("Food").performClick()

        added shouldBe listOf(CategoryKind.EXPENSE, CategoryKind.INCOME)
        edited shouldBe listOf(id)
    }

    @Test
    fun `a not found flag shows the no longer exists message once, then asks the caller to clear it`() {
        show()

        notFound = true
        awaitText("That category no longer exists")
        notFoundShown shouldBe 0

        // The message times out; only then is the flag cleared, so it is not shown twice.
        compose.mainClock.advanceTimeBy(11_000)
        awaitMain { notFoundShown == 1 }

        compose.onAllNodesWithText("That category no longer exists").fetchSemanticsNodes().size shouldBe 0
        notFound shouldBe false
    }
}
