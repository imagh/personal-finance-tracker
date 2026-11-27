package dev.imagh.pft.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Corner shapes. The five M3 sizes are applied to `MaterialTheme.shapes` by `PftTheme`, so M3
 * components pick them up with no restyling: extraSmall 6dp, small 10dp, medium 16dp, large 24dp
 * (large panels), extraLarge 32dp (sheets). Read the Cobalt tokens below through `PftTheme.shapes`:
 * [card] 22dp, [island] 18dp, [quickAction] 17dp and the full-pill [tabBar].
 */
object PftShapes {
    /** The M3 shape scale handed to `MaterialTheme`. */
    internal val material =
        Shapes(
            extraSmall = RoundedCornerShape(6.dp),
            small = RoundedCornerShape(10.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(24.dp),
            extraLarge = RoundedCornerShape(32.dp),
        )

    /**
     * The soft-square badge behind a category glyph: corners are 30% of the badge's side, so the
     * 40dp row badge, the 56dp detail badge and the 28dp chip badge all look alike.
     */
    val glyph: Shape = RoundedCornerShape(percent = 30)

    /** The balance card (Cobalt, plan D3): 22dp corners. */
    val card: Shape = RoundedCornerShape(22.dp)

    /** A grouped island of rows (`GroupedSection`, day groups): 18dp corners. */
    val island: Shape = RoundedCornerShape(18.dp)

    /** A quick-action tile on Home: 17dp corners. */
    val quickAction: Shape = RoundedCornerShape(17.dp)

    /** The floating tab bar: a full pill, corners at half the bar's shorter side. */
    val tabBar: Shape = RoundedCornerShape(percent = 50)
}
