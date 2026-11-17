package dev.imagh.pft.app.ui.categories

import dev.imagh.pft.app.log.SafeLog
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.ids.CategoryId
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLog

/**
 * The emoji icon in [CategoryEditViewModel] (plan B4c) over real in-memory Room: a quick-pick, a typed or
 * pasted emoji judged by [EmojiInputValidator], what a rejected paste keeps and clears, and what is saved
 * and audited. Native graphics, so the validator uses the platform's real `Paint.hasGlyph`. The failure
 * shape and everything else of the form is in `CategoryEditViewModelTest`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35])
class CategoryEditEmojiViewModelTest {
    private val database = TestDatabase.create()
    private val kit = RoomTestKit(database)

    @After
    fun closeDatabase() = database.close()

    private fun <T> blocking(block: suspend () -> T): T = runBlocking { block() }

    private fun ensureIdentity() {
        blocking { if (kit.identity.currentIdentity() == null) kit.initialise() }
    }

    private fun seed(
        name: String,
        icon: CategoryIcon?,
    ): Category {
        ensureIdentity()
        return blocking {
            kit.categories.create(null, CategoryKind.EXPENSE, CategoryDraft(name, CategoryColour.CORAL, icon))
        }
    }

    private fun stored(): List<Category> =
        blocking { CategoryKind.entries.flatMap { kit.categories.lists(it).let { l -> l.active + l.archived } } }

    private fun trail(id: CategoryId) = blocking { kit.trailOf("category", id.value) }.map { it.action }

    private fun createForm(): CategoryEditViewModel {
        ensureIdentity()
        val vm = CategoryEditViewModel(kit.categories, null, null, null)
        awaitMain { vm.state.value.loaded }
        return vm
    }

    private fun editForm(id: String): CategoryEditViewModel {
        ensureIdentity()
        val vm = CategoryEditViewModel(kit.categories, id, null, null)
        awaitMain { vm.state.value.loaded }
        return vm
    }

    private fun CategoryEditViewModel.saveAndWait() {
        submit()
        awaitMain { state.value.saved }
    }

    // --- Emoji icons (B4c): the quick-pick, a typed or pasted emoji, and what a rejected paste leaves alone

    private fun storedIconKeys(): List<String?> =
        database.openHelper.readableDatabase.query("SELECT icon_key FROM category ORDER BY name").use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(if (cursor.isNull(0)) null else cursor.getString(0))
            }
        }

    @Test
    fun `a quick-pick emoji becomes the icon, and saving stores emoji text with a CREATE audit`() {
        val vm = createForm()
        vm.onNameChange("Pets")
        vm.onIconChange(CategoryIcon.Emoji("🐶"))

        vm.state.value.icon shouldBe CategoryIcon.Emoji("🐶")
        vm.saveAndWait()

        val category = stored().single()
        category.iconKey shouldBe CategoryIcon.Emoji("🐶")
        storedIconKeys() shouldBe listOf("emoji:🐶")
        trail(category.meta.id) shouldBe listOf(AuditAction.CREATE)
    }

    @Test
    fun `a pasted emoji that passes becomes the icon and clears an earlier rejection`() {
        val vm = createForm()
        vm.onEmojiInput("not an emoji")
        vm.state.value.emojiRejected shouldBe true

        vm.onEmojiInput("🛒")

        vm.state.value.icon shouldBe CategoryIcon.Emoji("🛒")
        vm.state.value.emojiRejected shouldBe false
    }

    @Test
    fun `a rejected paste shows the error and keeps the previous symbol, with nothing else about it kept`() {
        val vm = createForm()
        vm.onIconChange(CategoryIcon.Symbol("pets"))
        val before = vm.state.value

        vm.onEmojiInput("secret text, not an emoji")

        vm.state.value shouldBe before.copy(emojiRejected = true)
        vm.state.value
            .toString()
            .contains("secret") shouldBe false
    }

    @Test
    fun `a rejected paste keeps a previous emoji, and so does a rejected paste on an edit form`() {
        val vm = createForm()
        vm.onEmojiInput("🐶")
        vm.onEmojiInput("🛒🛒")
        vm.state.value.icon shouldBe CategoryIcon.Emoji("🐶")
        vm.state.value.emojiRejected shouldBe true

        val pets = seed("Pets", CategoryIcon.Emoji("🛒"))
        val edit = editForm(pets.meta.id.value)
        edit.onEmojiInput("x")
        edit.saveAndWait()
        stored().single().iconKey shouldBe CategoryIcon.Emoji("🛒")
    }

    @Test
    fun `every kind of bad paste is rejected and leaves the icon as it was`() {
        val vm = createForm()
        vm.onIconChange(CategoryIcon.Symbol("pets"))
        listOf(
            "",
            " ",
            "a",
            "Pets",
            "🛒 ",
            " 🛒",
            "🛒\u200B",
            "🛒🛒",
            "\uD83D",
            "🛒".repeat(100_000),
        ).forEach { paste ->
            vm.onEmojiInput(paste)

            vm.state.value.icon shouldBe CategoryIcon.Symbol("pets")
            vm.state.value.emojiRejected shouldBe true
            vm.onIconChange(CategoryIcon.Symbol("pets"))
        }
    }

    @Test
    fun `choosing a symbol or a quick-pick emoji clears the rejection, and a rejection does not block saving`() {
        val vm = createForm()
        vm.onNameChange("Pets")
        vm.onEmojiInput("zzz")
        vm.onIconChange(CategoryIcon.Symbol("pets"))
        vm.state.value.emojiRejected shouldBe false
        vm.onEmojiInput("zzz")
        vm.onIconChange(CategoryIcon.Emoji("🐶"))
        vm.state.value.emojiRejected shouldBe false

        vm.onIconChange(CategoryIcon.Symbol("pets"))
        vm.onEmojiInput("zzz")
        vm.saveAndWait()

        stored().single().iconKey shouldBe CategoryIcon.Symbol("pets")
    }

    @Test
    fun `choosing a symbol replaces an emoji and pasting an emoji replaces a symbol`() {
        val vm = createForm()
        vm.onNameChange("Pets")
        vm.onEmojiInput("🐶")
        vm.state.value.icon shouldBe CategoryIcon.Emoji("🐶")
        vm.onIconChange(CategoryIcon.Symbol("pets"))
        vm.state.value.icon shouldBe CategoryIcon.Symbol("pets")
        vm.onEmojiInput("🛒")
        vm.state.value.icon shouldBe CategoryIcon.Emoji("🛒")

        vm.saveAndWait()

        storedIconKeys() shouldBe listOf("emoji:🛒")
    }

    @Test
    fun `editing a category with an emoji changes it to another emoji with an UPDATE audit`() {
        val pets = seed("Pets", CategoryIcon.Emoji("🐶"))
        val vm = editForm(pets.meta.id.value)
        vm.onEmojiInput("🛒")

        vm.saveAndWait()

        stored().single().iconKey shouldBe CategoryIcon.Emoji("🛒")
        trail(pets.meta.id) shouldBe listOf(AuditAction.CREATE, AuditAction.UPDATE)
    }

    @Test
    fun `a rejected paste is not logged anywhere`() {
        val vm = createForm()
        ShadowLog.clear()

        vm.onEmojiInput("secret text, not an emoji")

        ShadowLog.getLogs().filter { it.tag == SafeLog.TAG } shouldBe emptyList()
        ShadowLog.getLogs().any { it.msg.contains("secret") } shouldBe false
    }
}
