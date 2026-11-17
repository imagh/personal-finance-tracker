package dev.imagh.pft.app.ui.common

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import java.time.Instant

private fun sampleCategory(
    id: String,
    name: String,
    colour: CategoryColour,
    icon: CategoryIcon?,
    parent: String? = null,
) = Category(
    meta = EntityMeta(CategoryId(id), Instant.EPOCH, Instant.EPOCH, UserId("sample"), DeviceId("sample")),
    parentId = parent?.let(::CategoryId),
    kind = CategoryKind.EXPENSE,
    name = name,
    colour = colour,
    iconKey = icon,
    sortOrder = 0,
    systemKey = null,
)

/**
 * Fake categories in the order `CategoryService.pickable` gives them: Food with two sub-categories,
 * Transport with one, a "Misc" under each of Fun and Pets (the same name twice, told apart by "Under ..."),
 * a long name that wraps, an emoji icon and a category with no icon.
 */
private val SAMPLE_CATEGORIES =
    listOf(
        sampleCategory("food", "Food & dining", CategoryColour.CORAL, CategoryIcon.Symbol("restaurant")),
        sampleCategory("groceries", "Groceries", CategoryColour.CORAL, CategoryIcon.Symbol("grocery"), "food"),
        sampleCategory("eating", "Eating out", CategoryColour.CORAL, CategoryIcon.Symbol("local_cafe"), "food"),
        sampleCategory("transport", "Transport", CategoryColour.SKY, CategoryIcon.Symbol("directions_car")),
        sampleCategory("fuel", "Fuel", CategoryColour.SKY, CategoryIcon.Symbol("local_gas_station"), "transport"),
        sampleCategory("fun", "Fun", CategoryColour.VIOLET, CategoryIcon.Symbol("movie")),
        sampleCategory("fun-misc", "Misc", CategoryColour.VIOLET, null, "fun"),
        sampleCategory("pets", "Pets", CategoryColour.AMBER, CategoryIcon.Emoji("🐶")),
        sampleCategory("pets-misc", "Misc", CategoryColour.AMBER, null, "pets"),
        sampleCategory("repairs", "Maintenance & repairs", CategoryColour.OLIVE, CategoryIcon.Symbol("handyman")),
    )

/**
 * A `CategoryPicker` over fake categories with "Groceries" selected. The single source for the preview
 * and the golden. Release builds drop it (R8; see `verifyReleaseShrink`).
 */
@Composable
internal fun CategoryPickerSample() {
    CategoryPicker(SAMPLE_CATEGORIES, selected = CategoryId("groceries"), onPick = {}, onDismiss = {})
}

/** A `CategoryPicker` as the category form's parent field opens it: top-level categories only, "Transport" selected. */
@Composable
internal fun CategoryParentPickerSample() {
    CategoryPicker(
        SAMPLE_CATEGORIES,
        selected = CategoryId("transport"),
        onPick = {},
        onDismiss = {},
        topLevelOnly = true,
        title = stringResource(R.string.category_parent_picker_title),
    )
}

@PreviewLightDark
@Composable
internal fun CategoryPickerPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { CategoryPickerSample() } }
}

@PreviewLightDark
@Composable
internal fun CategoryParentPickerPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) {
        Surface(color = PftTheme.surfaces.canvas) { CategoryParentPickerSample() }
    }
}
