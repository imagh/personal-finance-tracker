package dev.imagh.pft.app.ui.common

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import dev.imagh.pft.app.ui.theme.MoneyColors

/** The colours of a [StatusChip]: the pill [container] and the label [content] drawn on it. */
internal data class ChipColors(
    val container: Color,
    val content: Color,
)

/**
 * The pill colours for [tone]: the money container tokens for positive, negative and warning, and
 * a neutral surface container for [MoneyTone.Neutral]. A pure function so the contrast test can
 * sweep it without a composition. Never `surfaceVariant`: see `MoneyColors`.
 */
internal fun chipColors(
    tone: MoneyTone,
    money: MoneyColors,
    scheme: ColorScheme,
): ChipColors =
    when (tone) {
        MoneyTone.Neutral -> ChipColors(scheme.surfaceContainerHighest, scheme.onSurface)
        MoneyTone.Positive -> ChipColors(money.positiveContainer, money.onPositiveContainer)
        MoneyTone.Negative -> ChipColors(money.negativeContainer, money.onNegativeContainer)
        MoneyTone.Warning -> ChipColors(money.warningContainer, money.onWarningContainer)
    }
