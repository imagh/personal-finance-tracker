package dev.imagh.pft.app.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import dev.imagh.pft.app.ui.theme.PftTheme

/**
 * A small labelled pill for a state: "Unverified", "Overdue", "Paused", "Over", "80%". The label
 * is required and is the cue: the tone only picks the colours, so a red/green colour-blind user
 * still reads the state (WCAG 1.4.1, plan §3.3, B-3). The pairs are the money container tokens
 * (4.5:1 pinned by `ColorContrastTest`), or a neutral surface container.
 *
 * It is not interactive and has no touch target; wrap it in the row that is. Named consumers:
 * transaction rows (Unverified), the transaction and budget detail screens, and recurring and
 * loan rows (Overdue, Paused, Cancelled).
 *
 * @param label the state in words; must not be blank.
 * @param tone the colour family: [MoneyTone.Warning] for Unverified or near-limit,
 *   [MoneyTone.Negative] for Overdue or Over, [MoneyTone.Positive] for Under, [MoneyTone.Neutral]
 *   for Paused or Cancelled.
 * @throws IllegalArgumentException if [label] is blank.
 */
@Composable
fun StatusChip(
    label: String,
    tone: MoneyTone,
    modifier: Modifier = Modifier,
) {
    require(label.isNotBlank()) { "a status chip needs a label: colour alone is not a cue" }
    val colors = chipColors(tone, PftTheme.money, MaterialTheme.colorScheme)
    val spacing = PftTheme.spacing
    // One merged node, so the pill reads as a single "Overdue" to assistive technology.
    Surface(
        modifier.semantics(mergeDescendants = true) {
        },
        shape = MaterialTheme.shapes.extraLarge,
        color = colors.container,
    ) {
        Text(
            label,
            Modifier.padding(horizontal = spacing.sm, vertical = spacing.xs),
            color = colors.content,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
