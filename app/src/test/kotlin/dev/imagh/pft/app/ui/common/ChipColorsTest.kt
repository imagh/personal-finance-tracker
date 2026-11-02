package dev.imagh.pft.app.ui.common

import dev.imagh.pft.app.ui.theme.MoneyColors
import dev.imagh.pft.app.ui.theme.ThemePalette
import dev.imagh.pft.app.ui.theme.colors
import dev.imagh.pft.app.ui.theme.moneyColors
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

/**
 * The pill colours of a `StatusChip` never depend on the palette for the money tones (plan §3.1
 * "Accent safety", UI-11): brass and champagne sit within a few degrees of warning amber, so the
 * only thing keeping a warning pill from reading as the accent is that its colours come from
 * `MoneyColors`, whichever palette is chosen.
 */
class ChipColorsTest {
    private val moneyTones = listOf(MoneyTone.Positive, MoneyTone.Negative, MoneyTone.Warning)

    private fun containerPair(
        tone: MoneyTone,
        money: MoneyColors,
    ): ChipColors =
        when (tone) {
            MoneyTone.Positive -> ChipColors(money.positiveContainer, money.onPositiveContainer)
            MoneyTone.Negative -> ChipColors(money.negativeContainer, money.onNegativeContainer)
            MoneyTone.Warning -> ChipColors(money.warningContainer, money.onWarningContainer)
            MoneyTone.Neutral -> error("Neutral is not a money tone")
        }

    @Test
    fun `positive, negative and warning pills are the MoneyColors container pair under every palette`() {
        for (palette in ThemePalette.entries) {
            for (dark in listOf(false, true)) {
                val money = moneyColors(dark)
                val scheme = palette.colors(dark).scheme
                for (tone in moneyTones) {
                    assertEquals(
                        containerPair(tone, money),
                        chipColors(tone, money, scheme),
                        "$palette dark=$dark $tone",
                    )
                }
            }
        }
    }

    @Test
    fun `a money pill is identical under every palette in the same mode`() {
        for (dark in listOf(false, true)) {
            val money = moneyColors(dark)
            for (tone in moneyTones) {
                val pills =
                    ThemePalette.entries
                        .map { chipColors(tone, money, it.colors(dark).scheme) }
                        .toSet()
                assertEquals(1, pills.size, "$tone pill differs between palettes (dark=$dark)")
            }
        }
    }

    @Test
    fun `a warning pill never takes the accent or its container`() {
        for (palette in ThemePalette.entries) {
            for (dark in listOf(false, true)) {
                val scheme = palette.colors(dark).scheme
                val pill = chipColors(MoneyTone.Warning, moneyColors(dark), scheme)
                listOf(scheme.primary, scheme.primaryContainer, scheme.secondaryContainer, scheme.tertiaryContainer)
                    .forEach { accent ->
                        assertNotEquals(
                            accent,
                            pill.container,
                            "$palette dark=$dark warning pill container is an accent",
                        )
                        assertNotEquals(accent, pill.content, "$palette dark=$dark warning pill label is an accent")
                    }
            }
        }
    }

    @Test
    fun `the neutral pill follows the palette's surface container`() {
        for (palette in ThemePalette.entries) {
            for (dark in listOf(false, true)) {
                val scheme = palette.colors(dark).scheme
                assertEquals(
                    ChipColors(scheme.surfaceContainerHighest, scheme.onSurface),
                    chipColors(MoneyTone.Neutral, moneyColors(dark), scheme),
                    "$palette dark=$dark",
                )
            }
        }
    }
}
