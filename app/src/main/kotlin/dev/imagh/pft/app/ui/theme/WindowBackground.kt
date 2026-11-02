package dev.imagh.pft.app.ui.theme

import android.graphics.drawable.ColorDrawable
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb

/**
 * Keeps [window]'s background equal to the canvas the user's theme is drawing (UI-18).
 *
 * Before Compose draws its first frame the window paints the colour from resources
 * (`pft_window_background`, Graphite's canvas), which cannot know the user's palette; every palette
 * stays within a few tones of it (`WindowFlashToneTest`). From the first composition on, this makes
 * the window colour exact, so what shows through predictive back, IME gaps and configuration
 * changes is the canvas, not the resource colour. It runs again whenever the canvas changes
 * (palette, mode, background style or dynamic colour).
 *
 * Call it once, inside [PftTheme], from the activity: `PftTheme(prefs) { SyncWindowBackground(window) ... }`.
 * It touches only the window's background drawable, never its flags (`FLAG_SECURE` is set earlier,
 * in `MainActivity.onCreate`).
 */
@Composable
internal fun SyncWindowBackground(window: Window) {
    val canvas = PftTheme.surfaces.canvas.toArgb()
    SideEffect { window.setBackgroundDrawable(ColorDrawable(canvas)) }
}
