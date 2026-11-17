package dev.imagh.pft.app.ui.categories

import dev.imagh.pft.app.ui.common.GlyphSource
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Instant

/** The category to row mapping: title, badge, indent, parent line and menu, as plain data. Pure, no Compose. */
class CategoryRowMapperTest {
    private fun category(
        icon: CategoryIcon? = CategoryIcon.Symbol("grocery"),
        colour: CategoryColour = CategoryColour.CORAL,
    ) = Category(
        meta =
            EntityMeta(
                id = CategoryId("c1"),
                createdAt = Instant.EPOCH,
                updatedAt = Instant.EPOCH,
                createdByUserId = UserId("u1"),
                createdOnDeviceId = DeviceId("d1"),
            ),
        parentId = null,
        kind = CategoryKind.EXPENSE,
        name = "Groceries",
        colour = colour,
        iconKey = icon,
        sortOrder = 0,
        systemKey = null,
    )

    private val taken = mutableListOf<Pair<CategoryId, CategoryAction>>()
    private val opened = mutableListOf<CategoryId>()

    private fun row(
        item: CategoryItem,
        subCategoryOf: (String) -> String = { it },
    ) = item.toRow(
        actionLabel = { it.name.lowercase() },
        parentLine = subCategoryOf,
        choose = { id, action -> taken += id to action },
        edit = { opened += it },
    )

    private fun item(
        category: Category = category(),
        indent: Boolean = false,
        parentName: String? = null,
        actions: List<CategoryAction> = emptyList(),
    ) = CategoryItem(category, indent, parentName, actions)

    @Test
    fun `the row has the category's name, a decorative symbol badge in its colour and its id as key`() {
        val row = row(item())

        row.key shouldBe "c1"
        row.title shouldBe "Groceries"
        row.glyph?.source shouldBe GlyphSource.Symbol(PftSymbol.GROCERY)
        row.glyph?.colourKey shouldBe CategoryColour.CORAL.key
        row.glyph?.label shouldBe null
        row.indent shouldBe false
        row.supporting shouldBe null
    }

    @Test
    fun `an emoji icon is drawn as that emoji, and no icon as the letter`() {
        row(item(category(icon = CategoryIcon.Emoji("🛒")))).glyph?.source shouldBe GlyphSource.Emoji("🛒")
        row(item(category(icon = null))).glyph?.source shouldBe GlyphSource.Letter('G')
    }

    @Test
    fun `an indented item is an indented row`() {
        row(item(indent = true)).indent shouldBe true
    }

    @Test
    fun `a parent's name becomes the supporting line through the given wording`() {
        row(item(parentName = "Food & dining"), subCategoryOf = { "in $it" }).supporting shouldBe "in Food & dining"
    }

    @Test
    fun `the menu is the item's actions in order, labelled by the given wording, and each runs on the category`() {
        val row = row(item(actions = listOf(CategoryAction.MOVE_UP, CategoryAction.ARCHIVE, CategoryAction.DELETE)))

        row.actions.map { it.label } shouldBe listOf("move_up", "archive", "delete")
        row.actions.forEach { it.onClick() }

        taken shouldBe
            listOf(
                CategoryId("c1") to CategoryAction.MOVE_UP,
                CategoryId("c1") to CategoryAction.ARCHIVE,
                CategoryId("c1") to CategoryAction.DELETE,
            )
    }

    @Test
    fun `tapping the row opens that category for editing`() {
        val row = row(item())

        row.onClick?.invoke()

        opened shouldBe listOf(CategoryId("c1"))
    }

    @Test
    fun `an item with no actions has no menu`() {
        row(item(actions = emptyList())).actions shouldBe emptyList()
    }
}
