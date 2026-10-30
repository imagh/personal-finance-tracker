package dev.imagh.pft.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
}

internal val DisplayAndHeadline: List<Pair<String, (Typography) -> TextStyle>> =
    listOf(
        "displayLarge" to { t -> t.displayLarge },
        "displayMedium" to { t -> t.displayMedium },
        "displaySmall" to { t -> t.displaySmall },
        "headlineLarge" to { t -> t.headlineLarge },
        "headlineMedium" to { t -> t.headlineMedium },
        "headlineSmall" to { t -> t.headlineSmall },
    )

internal val TitleBodyLabel: List<Pair<String, (Typography) -> TextStyle>> =
    listOf(
        "titleLarge" to { t -> t.titleLarge },
        "titleMedium" to { t -> t.titleMedium },
        "titleSmall" to { t -> t.titleSmall },
        "bodyLarge" to { t -> t.bodyLarge },
        "bodyMedium" to { t -> t.bodyMedium },
        "bodySmall" to { t -> t.bodySmall },
        "labelLarge" to { t -> t.labelLarge },
        "labelMedium" to { t -> t.labelMedium },
        "labelSmall" to { t -> t.labelSmall },
    )

@Composable
internal fun TypeScale(
    title: String,
    roles: List<Pair<String, (Typography) -> TextStyle>>,
) {
    val type = MaterialTheme.typography
    Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.xs)) {
        SectionTitle(title)
        roles.forEach { (name, style) -> Text(name, style = style(type), color = MaterialTheme.colorScheme.onSurface) }
    }
}

@Composable
internal fun AmountStylesSection() {
    val type = PftTheme.type
    val onSurface = MaterialTheme.colorScheme.onSurface
    Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.sm)) {
        SectionTitle("Amount styles (tabular figures)")
        Text("amountHero", style = MaterialTheme.typography.labelSmall, color = onSurface)
        Text("₹82,450", style = type.amountHero, color = onSurface)
        Text("amountLarge", style = MaterialTheme.typography.labelSmall, color = onSurface)
        Text("₹82,450.00", style = type.amountLarge, color = onSurface)
        Text("amountRow: digits line up", style = MaterialTheme.typography.labelSmall, color = onSurface)
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            listOf("₹1,111.11", "₹8,888.88", "₹10,000.00").forEach {
                Text(it, style = type.amountRow, color = onSurface, textAlign = TextAlign.End)
            }
        }
    }
}

@Composable
private fun ShapesSection() {
    val shapes = MaterialTheme.shapes
    val named: List<Pair<String, Shape>> =
        listOf(
            "xs 6" to shapes.extraSmall,
            "sm 10" to shapes.small,
            "md 16" to shapes.medium,
            "lg 24" to shapes.large,
            "xl 32" to shapes.extraLarge,
            "glyph 30%" to PftTheme.shapes.glyph,
        )
    Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.sm)) {
        SectionTitle("Shapes (dp)")
        named.chunked(SHAPES_PER_ROW).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(PftTheme.spacing.lg)) {
                row.forEach { (label, shape) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(48.dp).background(MaterialTheme.colorScheme.secondaryContainer, shape))
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}

private const val SHAPES_PER_ROW = 3

@Composable
internal fun ShapesAndSpacing() {
    Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.xl)) {
        ShapesSection()
        SpacingSection()
    }
}

@Composable
private fun SpacingSection() {
    val s = PftTheme.spacing
    val steps: List<Pair<String, Dp>> =
        listOf(
            "xxs" to s.xxs,
            "xs" to s.xs,
            "sm" to s.sm,
            "md" to s.md,
            "lg" to s.lg,
            "gutter" to s.gutter,
            "xl" to s.xl,
            "xxl" to s.xxl,
            "huge" to s.huge,
        )
    Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.xs)) {
        SectionTitle("Spacing (dp)")
        steps.forEach { (name, size) ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(s.sm)) {
                Box(Modifier.width(size).height(12.dp).background(MaterialTheme.colorScheme.primary))
                Text(
                    "$name ${size.value.toInt()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
internal fun MoneyTokensSection() {
    val scheme = MaterialTheme.colorScheme
    val surfaces: List<Pair<String, Color>> =
        listOf(
            "surface" to scheme.surface,
            "canvas" to PftTheme.surfaces.canvas,
            "island" to PftTheme.surfaces.island,
            "surfaceContainerHighest" to scheme.surfaceContainerHighest,
        )
    Column(verticalArrangement = Arrangement.spacedBy(PftTheme.spacing.sm)) {
        SectionTitle("Money tokens on surfaces")
        surfaces.forEach { (name, color) -> MoneyOnSurface(name, color) }
        MoneyContainers()
    }
}

@Composable
private fun MoneyOnSurface(
    name: String,
    color: Color,
) {
    val money = PftTheme.money
    val amount = PftTheme.type.amountRow
    Surface(color = color, shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(PftTheme.spacing.md)) {
            Text(name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
            Text("+₹1,20,000.00", style = amount, color = money.positive)
            Text("−₹48,250.40", style = amount, color = money.negative)
            Text("80% of budget", style = amount, color = money.warning)
        }
    }
}

@Composable
private fun MoneyContainers() {
    val money = PftTheme.money
    val pills: List<Triple<String, Color, Color>> =
        listOf(
            Triple("Under", money.positiveContainer, money.onPositiveContainer),
            Triple("Over", money.negativeContainer, money.onNegativeContainer),
            Triple("Alert", money.warningContainer, money.onWarningContainer),
        )
    Row(horizontalArrangement = Arrangement.spacedBy(PftTheme.spacing.sm)) {
        pills.forEach { (label, container, content) ->
            Surface(color = container, shape = MaterialTheme.shapes.small) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    color = content,
                    modifier = Modifier.padding(horizontal = PftTheme.spacing.md, vertical = PftTheme.spacing.xs),
                )
            }
        }
    }
}
