package dev.imagh.pft.app.ui.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.ComposeContentTestRule

/**
 * The window of an open `PftBottomSheet` drawn into a bitmap, for the sheet's shape test and its
 * goldens. `captureToImage` waits for a draw callback that a dialog window never gets under
 * Robolectric (it times out after 2 s), so the compose view that owns the sheet is drawn directly.
 * The sheet is opaque; the scrim around it is black at about 32% alpha, so [background] (an ARGB
 * colour, transparent by default) is painted first to stand in for the screen behind the sheet.
 *
 * @param containing text that is on screen only inside the sheet (its title), used to pick the
 *   sheet's window out of the app's and the sheet's two roots.
 * @param background what to paint under the window before drawing it.
 */
internal fun ComposeContentTestRule.drawSheetWindow(
    containing: String,
    background: Int = android.graphics.Color.TRANSPARENT,
): Bitmap {
    val root = onAllNodes(isRoot() and hasAnyDescendant(hasText(containing))).fetchSemanticsNodes().single()
    val view = root.root as View
    return Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888).also {
        val canvas = Canvas(it)
        canvas.drawColor(background)
        view.draw(canvas)
    }
}
