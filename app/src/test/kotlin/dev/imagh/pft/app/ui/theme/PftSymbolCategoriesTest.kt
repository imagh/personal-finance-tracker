package dev.imagh.pft.app.ui.theme

import dev.imagh.pft.ledger.category.DefaultCategories
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

/**
 * [CATEGORY_SYMBOLS], the explicit list of symbols a category can have. Membership is deliberate and fails
 * closed: a symbol added to the registry later is not offered as a category icon until it is added to the
 * list, and these tests make sure every registry symbol has been classified, one way or the other.
 */
class PftSymbolCategoriesTest {
    /**
     * The interface symbols, which must never be offered as a category icon. Spelled out here on purpose: a
     * new registry symbol is in neither this list nor [CATEGORY_SYMBOLS], so the classification test below
     * fails until the author says which kind it is.
     */
    private val interfaceSymbols =
        listOf(
            PftSymbol.BACK,
            PftSymbol.FORWARD,
            PftSymbol.ADD,
            PftSymbol.CLOSE,
            PftSymbol.CHECK,
            PftSymbol.SEARCH,
            PftSymbol.FILTER,
            PftSymbol.OVERFLOW,
            PftSymbol.CHEVRON_LEFT,
            PftSymbol.CHEVRON_RIGHT,
            PftSymbol.EXPAND_MORE,
            PftSymbol.EXPAND_LESS,
            PftSymbol.EDIT,
            PftSymbol.DELETE,
            PftSymbol.UNDO,
            PftSymbol.LOCK,
            PftSymbol.SETTINGS,
            PftSymbol.CALENDAR,
            PftSymbol.INFO,
            PftSymbol.WARNING,
            PftSymbol.ERROR,
            PftSymbol.VISIBILITY,
            PftSymbol.VISIBILITY_OFF,
            PftSymbol.DOWNLOAD,
            PftSymbol.UPLOAD,
            PftSymbol.TUNE,
            PftSymbol.SORT,
            PftSymbol.TRANSFER,
            PftSymbol.TAG,
            PftSymbol.HISTORY,
            PftSymbol.CHECK_CIRCLE,
        )

    @Test
    fun `the list has no duplicates`() {
        CATEGORY_SYMBOLS.groupBy { it }.filterValues { it.size > 1 }.keys shouldBe emptySet()
    }

    @Test
    fun `the list holds every default category's symbol`() {
        val missing =
            DefaultCategories.LIST.map { it.symbolKey }.filter { key ->
                CATEGORY_SYMBOLS.none { it.key == key }
            }
        missing shouldBe emptyList()
    }

    @Test
    fun `the list holds no interface symbol`() {
        CATEGORY_SYMBOLS.filter { it in interfaceSymbols } shouldBe emptyList()
    }

    @Test
    fun `every registry symbol is classified as a category or an interface symbol`() {
        val unclassified = PftSymbol.entries - CATEGORY_SYMBOLS.toSet() - interfaceSymbols.toSet()
        unclassified shouldBe emptyList()
    }
}
