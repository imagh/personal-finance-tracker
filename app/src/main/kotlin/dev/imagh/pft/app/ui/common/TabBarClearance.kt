package dev.imagh.pft.app.ui.common

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp

/**
 * How much of the bottom of the screen the floating tab bar covers, above the system bars' inset
 * (which `CollapsingHeaderScaffold` already pads by). The navigation host sets it while the bar is
 * showing; a tab-root list adds it as bottom space so its last row and its add button clear the bar
 * (`EntityListScreen`). Zero by default, which is right for every screen the bar is not drawn over:
 * forms, Setup, and a list rendered on its own.
 */
val LocalTabBarClearance = compositionLocalOf { 0.dp }
