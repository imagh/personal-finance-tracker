package dev.imagh.pft.app

import androidx.compose.foundation.layout.Arrangement
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
import dev.imagh.pft.app.ui.theme.DisplayFont
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemeMode
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.app.ui.theme.TokenSheet

/**
 * The debug design catalogue: every design token on one scrollable sheet, with live toggles for
 * light/dark, the hero font and the font scale, so the tokens can be checked on a device or the
 * emulator (design plan §3.7, §11). It uses [TokenSheet], the same composable the previews and
 * the screenshot goldens use. Debug builds only; the release twin of `DevContent` is empty.
 *
 * Each toggle row is a set of single-choice chips. Dynamic colour is on, as in the real app, so
 * the accent follows the wallpaper while the money colours stay fixed.
 */
@Composable
internal fun DesignCatalog() {
    var mode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
    var displayFont by rememberSaveable { mutableStateOf(DisplayFont.BUNDLED_SERIF) }
    var fontScale by rememberSaveable { mutableStateOf(1f) }
    val density = LocalDensity.current

    CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
        PftTheme(ThemePrefs(mode = mode, displayFont = displayFont)) {
            Surface(Modifier.fillMaxSize()) {
                Column(Modifier.statusBarsPadding().verticalScroll(rememberScrollState())) {
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
                    TokenSheet()
                }
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
