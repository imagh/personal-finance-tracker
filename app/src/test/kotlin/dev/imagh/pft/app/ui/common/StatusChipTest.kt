package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.MoneyColors
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * `StatusChip` (plan §6.1 DS5, B-3): a labelled pill, so colour is never the only cue. The label
 * is the cue; the tone only picks the pill's colours.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class StatusChipTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val labels =
        mapOf(
            MoneyTone.Neutral to "Paused",
            MoneyTone.Positive to "Under",
            MoneyTone.Warning to "Unverified",
            MoneyTone.Negative to "Overdue",
        )

    @Test
    fun `every tone renders its text label`() {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                Column { labels.forEach { (tone, label) -> StatusChip(label, tone) } }
            }
        }
        labels.values.forEach { compose.onNodeWithText(it).assertIsDisplayed() }
    }

    @Test
    fun `the label is shown exactly as given`() {
        compose.setContent {
            PftTheme(
                ThemePrefs(dynamicColor = false),
            ) { StatusChip("80%", MoneyTone.Warning, Modifier.testTag("chip")) }
        }
        compose.onNodeWithTag("chip").assertTextEquals("80%")
    }

    @Test
    fun `a blank label is rejected, since a pill with no word would leave colour as the only cue`() {
        val failure =
            runCatching {
                compose.setContent {
                    PftTheme(
                        ThemePrefs(dynamicColor = false),
                    ) { StatusChip("   ", MoneyTone.Negative) }
                }
            }.exceptionOrNull()
        assertTrue("$failure", failure is IllegalArgumentException)
    }

    @Test
    fun `the label is drawn in the tone's on-container colour`() {
        lateinit var expected: Map<MoneyTone, ChipColors>
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                expected = MoneyTone.entries.associateWith { chipColors(it, PftTheme.money, MaterialTheme.colorScheme) }
                Column {
                    MoneyTone.entries.forEach { tone ->
                        StatusChip(labels.getValue(tone), tone, Modifier.testTag("chip-$tone"))
                    }
                }
            }
        }
        MoneyTone.entries.forEach { tone ->
            val label =
                compose
                    .onNodeWithTag("chip-$tone")
                    .layout()
                    .layoutInput.style.color
            assertEquals("$tone label", expected.getValue(tone).content, label)
        }
    }

    @Test
    fun `the money tones use the money container tokens and neutral uses a surface container`() {
        lateinit var money: MoneyColors
        lateinit var scheme: ColorScheme
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                money = PftTheme.money
                scheme = MaterialTheme.colorScheme
            }
        }
        val pairs =
            mapOf(
                MoneyTone.Positive to ChipColors(money.positiveContainer, money.onPositiveContainer),
                MoneyTone.Negative to ChipColors(money.negativeContainer, money.onNegativeContainer),
                MoneyTone.Warning to ChipColors(money.warningContainer, money.onWarningContainer),
                MoneyTone.Neutral to ChipColors(scheme.surfaceContainerHighest, scheme.onSurface),
            )
        pairs.forEach { (tone, colors) -> assertEquals("$tone", colors, chipColors(tone, money, scheme)) }
    }
}
