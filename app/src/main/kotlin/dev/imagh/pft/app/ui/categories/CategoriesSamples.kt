package dev.imagh.pft.app.ui.categories

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.CategoryLists
import dev.imagh.pft.ledger.category.SystemCategory
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import java.time.Instant

private val SAMPLE_TIME = Instant.parse("2026-01-01T00:00:00Z")

private fun sample(
    id: String,
    name: String,
    colour: CategoryColour,
    icon: CategoryIcon?,
    parent: String? = null,
    sortOrder: Int = 0,
    archived: Boolean = false,
    system: SystemCategory? = null,
) = Category(
    meta =
        EntityMeta(
            id = CategoryId(id),
            createdAt = SAMPLE_TIME,
            updatedAt = SAMPLE_TIME,
            createdByUserId = UserId("sample-user"),
            createdOnDeviceId = DeviceId("sample-device"),
            archivedAt = if (archived) SAMPLE_TIME else null,
        ),
    parentId = parent?.let(::CategoryId),
    kind = CategoryKind.EXPENSE,
    name = name,
    colour = colour,
    iconKey = icon,
    sortOrder = sortOrder,
    systemKey = system,
)

/**
 * The state of the categories list with fake data: a parent with two sub-categories, a category with
 * an emoji icon, one with no icon, the system "Loan EMI" (no Archive or Delete), and two archived
 * rows, one of them a sub-category of an active parent. The rows' menus come from the real rules
 * ([toItems]), so the sample cannot drift from them. The single source for the preview, the goldens
 * and `CategoriesScreenTest`; release builds drop it (R8; see `verifyReleaseShrink`).
 */
internal fun sampleCategoriesState(): CategoriesUiState {
    val lists =
        CategoryLists(
            active =
                listOf(
                    sample("food", "Food & dining", CategoryColour.CORAL, CategoryIcon.Symbol("restaurant")),
                    sample("groceries", "Groceries", CategoryColour.CORAL, CategoryIcon.Symbol("grocery"), "food"),
                    sample("cafe", "Eating out", CategoryColour.CORAL, CategoryIcon.Symbol("local_cafe"), "food", 1),
                    sample(
                        "transport",
                        "Transport",
                        CategoryColour.SKY,
                        CategoryIcon.Symbol("directions_car"),
                        sortOrder = 1,
                    ),
                    sample("pets", "Pets", CategoryColour.AMBER, CategoryIcon.Emoji("🐶"), sortOrder = 2),
                    sample("misc", "Miscellaneous", CategoryColour.OLIVE, null, sortOrder = 3),
                    sample(
                        "loan",
                        "Loan EMI",
                        CategoryColour.SLATE,
                        CategoryIcon.Symbol("account_balance"),
                        sortOrder = 5,
                        system = SystemCategory.LOAN_EMI,
                    ),
                ),
            archived =
                listOf(
                    sample(
                        "delivery",
                        "Food delivery",
                        CategoryColour.CORAL,
                        CategoryIcon.Symbol("fastfood"),
                        "food",
                        2,
                        true,
                    ),
                    sample(
                        "shopping",
                        "Shopping",
                        CategoryColour.MAGENTA,
                        CategoryIcon.Symbol("shopping_bag"),
                        sortOrder = 4,
                        archived = true,
                    ),
                ),
        )
    val (active, archived) = lists.toItems()
    return CategoriesUiState(kind = CategoryKind.EXPENSE, active = active, archived = archived, loaded = true)
}

/** The categories list over [sampleCategoriesState]. Same use as that function. */
@Composable
internal fun CategoriesSample() {
    val snackbars = remember { SnackbarHostState() }
    CategoriesScreen(
        state = sampleCategoriesState(),
        actions = CategoriesActions(choose = { _, _ -> }, selectKind = {}),
        snackbars = snackbars,
        onBack = {},
    )
}

@PreviewLightDark
@Composable
internal fun CategoriesPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { CategoriesSample() } }
}
