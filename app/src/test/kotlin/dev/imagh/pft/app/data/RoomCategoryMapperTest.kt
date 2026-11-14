package dev.imagh.pft.app.data

import dev.imagh.pft.app.data.category.CategoryEntity
import dev.imagh.pft.app.data.category.CategoryMapper
import dev.imagh.pft.app.data.common.MetaColumns
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.SystemCategory
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.Test
import java.time.Instant

/**
 * [CategoryMapper]: the stored shape of a category. `colour` is stored as the palette **key**
 * (`coral`) and read with `CategoryColour.fromStored`; a stored colour or icon is untrusted on read
 * (a corrupt row, or a future sync), so a bad one falls back (slate, no icon) and never crashes. An
 * unknown `kind` or `system_key` fails closed like `account.type` does (B1c), naming the column and
 * never the stored value (CLAUDE.md §3.7).
 */
class RoomCategoryMapperTest {
    private val at = Instant.parse("2026-11-01T10:00:00.123Z")
    private val category =
        Category(
            meta = EntityMeta(CategoryId("cat-2"), at, at, UserId("user-1"), DeviceId("device-1")),
            parentId = CategoryId("cat-1"),
            kind = CategoryKind.EXPENSE,
            name = "Groceries",
            colour = CategoryColour.CORAL,
            iconKey = CategoryIcon.Symbol("grocery"),
            sortOrder = 3,
            systemKey = null,
        )

    private fun row() =
        CategoryEntity(
            meta = MetaColumns("cat-2", at.toEpochMilli(), at.toEpochMilli(), "user-1", "device-1", null, null),
            parentId = "cat-1",
            kind = "EXPENSE",
            name = "Groceries",
            colour = "coral",
            iconKey = "sym:grocery",
            sortOrder = 3,
            systemKey = null,
        )

    @Test
    fun `a category maps to a row with the documented columns, the colour as its key`() {
        CategoryMapper.toRow(category) shouldBe row()
    }

    @Test
    fun `a row maps back to the same category`() {
        CategoryMapper.toModel(row()) shouldBe category
    }

    @Test
    fun `every colour is stored as its key and read back as itself`() {
        CategoryColour.entries.forEach { colour ->
            val stored = CategoryMapper.toRow(category.copy(colour = colour))

            stored.colour shouldBe colour.key
            CategoryMapper.toModel(stored).colour shouldBe colour
        }
    }

    @Test
    fun `optional columns map to and from null and an emoji icon round-trips`() {
        val bare = category.copy(parentId = null, iconKey = null, systemKey = null)
        val stored = CategoryMapper.toRow(bare)

        stored.parentId shouldBe null
        stored.iconKey shouldBe null
        stored.systemKey shouldBe null
        CategoryMapper.toModel(stored) shouldBe bare

        val emoji = category.copy(iconKey = CategoryIcon.Emoji("🛒"))
        CategoryMapper.toRow(emoji).iconKey shouldBe "emoji:🛒"
        CategoryMapper.toModel(CategoryMapper.toRow(emoji)) shouldBe emoji
    }

    @Test
    fun `the kind and the system key round-trip`() {
        val income = category.copy(kind = CategoryKind.INCOME, parentId = null)
        val system = category.copy(parentId = null, systemKey = SystemCategory.LOAN_EMI)

        CategoryMapper.toRow(income).kind shouldBe "INCOME"
        CategoryMapper.toModel(CategoryMapper.toRow(income)) shouldBe income
        CategoryMapper.toRow(system).systemKey shouldBe "LOAN_EMI"
        CategoryMapper.toModel(CategoryMapper.toRow(system)) shouldBe system
    }

    @Test
    fun `an unknown, wrong-case or empty stored colour reads as slate`() {
        listOf("purple", "Coral", "CORAL", "").forEach { stored ->
            CategoryMapper.toModel(row().copy(colour = stored)).colour shouldBe CategoryColour.SLATE
        }
    }

    @Test
    fun `a stored icon that fails the grammar reads as no icon, never a crash`() {
        listOf("sym:Grocery", "sym:", "emoji:", "emoji:a b", "grocery", "").forEach { stored ->
            CategoryMapper.toModel(row().copy(iconKey = stored)).iconKey shouldBe null
        }
    }

    @Test
    fun `a row with an unknown kind fails on read without echoing the stored text`() {
        val failure = shouldThrow<IllegalStateException> { CategoryMapper.toModel(row().copy(kind = "TRANSFER")) }

        failure.message.orEmpty() shouldContain "kind"
        failure.message.orEmpty() shouldNotContain "TRANSFER"
    }

    @Test
    fun `a row with an unknown system key fails on read without echoing the stored text`() {
        val failure =
            shouldThrow<IllegalStateException> { CategoryMapper.toModel(row().copy(systemKey = "SAVINGS_POT")) }

        failure.message.orEmpty() shouldContain "system_key"
        failure.message.orEmpty() shouldNotContain "SAVINGS_POT"
    }
}
