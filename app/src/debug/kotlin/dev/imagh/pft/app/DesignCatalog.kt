package dev.imagh.pft.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.common.BottomActionBarSamples
import dev.imagh.pft.app.ui.common.DashboardHeaderSample
import dev.imagh.pft.app.ui.common.MoneyTextSamples
import dev.imagh.pft.app.ui.common.StandardHeaderSample
import dev.imagh.pft.app.ui.common.StatusChipSamples
import dev.imagh.pft.app.ui.theme.DisplayFont
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemeMode
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.app.ui.theme.TokenSheet

/**
 * The debug design catalogue: every design token on one scrollable sheet, plus the motion demo and
 * the one-hand layout components (both header styles and `BottomActionBar`) and the content
 * primitives (`MoneyText`, `StatusChip`, `GroupedSection`, `EmptyState`), with live toggles for
 * light/dark, the hero font and the font scale, so they can be checked on a device or the
 * emulator (design plan §3.7, §11). It uses the same sample composables the previews and the
 * screenshot goldens use. Debug builds only; the release twin of `DevContent` is empty.
 *
 * Each toggle row is a set of single-choice chips. Dynamic colour is on, as in the real app, so
 * the accent follows the wallpaper while the money colours stay fixed.
 */
@Composable
internal fun DesignCatalog() {
    var mode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
    var displayFont by rememberSaveable { mutableStateOf(DisplayFont.BUNDLED_SERIF) }
    var fontScale by rememberSaveable { mutableStateOf(1f) }
    var section by rememberSaveable { mutableStateOf(CatalogSection.TOKENS) }
    val density = LocalDensity.current

    CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
        PftTheme(ThemePrefs(mode = mode, displayFont = displayFont)) {
            Surface(Modifier.fillMaxSize()) {
                Column(Modifier.statusBarsPadding()) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        ChoiceRow(
                            options =
                                listOf(
                                    "System" to ThemeMode.SYSTEM,
                                    "Light" to ThemeMode.LIGHT,
                                    "Dark" to ThemeMode.DARK,
                                ),
                            selected = mode,
                            onSelect = { mode = it },
                        )
                        ChoiceRow(
                            options =
                                listOf(
                                    "Bundled serif" to DisplayFont.BUNDLED_SERIF,
                                    "System serif" to DisplayFont.SYSTEM_SERIF,
                                    "Same as text" to DisplayFont.TEXT,
                                ),
                            selected = displayFont,
                            onSelect = { displayFont = it },
                        )
                        ChoiceRow(
                            options = listOf("Font 1.0" to 1f, "Font 1.3" to 1.3f, "Font 2.0" to 2f),
                            selected = fontScale,
                            onSelect = { fontScale = it },
                        )
                    }
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        CatalogSection.entries.chunked(SECTIONS_PER_ROW).forEach { row ->
                            ChoiceRow(options = row.map { it.label to it }, selected = section, onSelect = {
                                section =
                                    it
                            })
                        }
                    }
                    Box(Modifier.weight(1f)) { SectionContent(section) }
                }
            }
        }
    }
}

private const val SECTIONS_PER_ROW = 3

/** The catalogue's pages: the token sheet, then the motion, layout and content components. */
private enum class CatalogSection(
    val label: String,
) {
    TOKENS("Tokens"),
    MOTION("Motion"),
    BOTTOM_BAR("Bottom bar"),
    STANDARD_HEADER("Standard header"),
    DASHBOARD_HEADER("Dashboard header"),
    MONEY("Money"),
    CHIPS("Chips"),
}

/**
 * One page of the catalogue. The header pages are the real scaffold (it owns its own list, so it
 * cannot sit inside a scrolling column); the others scroll.
 */
@Composable
private fun SectionContent(section: CatalogSection) {
    when (section) {
        CatalogSection.STANDARD_HEADER -> StandardHeaderSample()
        CatalogSection.DASHBOARD_HEADER -> DashboardHeaderSample()
        else ->
            Column(Modifier.verticalScroll(rememberScrollState())) {
                when (section) {
                    CatalogSection.MOTION -> MotionSection()
                    CatalogSection.BOTTOM_BAR -> BottomActionBarSamples()
                    CatalogSection.MONEY -> Box(Modifier.padding(16.dp)) { MoneyTextSamples() }
                    CatalogSection.CHIPS -> Box(Modifier.padding(16.dp)) { StatusChipSamples() }
                    else -> TokenSheet()
                }
            }
    }
}

@Composable
private fun <T> ChoiceRow(
    options: List<Pair<String, T>>,
    selected: T,
    onSelect: (T) -> Unit,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (label, value) ->
            FilterChip(selected = selected == value, onClick = { onSelect(value) }, label = { Text(label) })
        }
    }
}
