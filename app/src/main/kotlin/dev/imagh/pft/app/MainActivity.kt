package dev.imagh.pft.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dev.imagh.pft.app.ui.PftRoot
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.SyncWindowBackground

/**
 * The single activity. Every screen is hosted here, so setting [WindowManager.LayoutParams.FLAG_SECURE]
 * once covers all of them: no screenshots, no screen recording, and a blank card in the recents
 * switcher (SEC-2). The flag is set before anything else, so no frame is ever capturable;
 * edge-to-edge and the content come after it. Inside the theme, [SyncWindowBackground] keeps the window
 * colour equal to the canvas (UI-18).
 *
 * What it shows is [PftRoot]'s business: it asks the process-wide [PftApplication.container] to open the
 * encrypted database off the main thread, then draws the app or, failing closed, the data-unavailable
 * screen. The activity itself does no database work, so the first frame is never blocked.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val container = (application as PftApplication).container
        setContent {
            PftTheme {
                SyncWindowBackground(window)
                Surface(modifier = Modifier.fillMaxSize(), color = PftTheme.surfaces.canvas) {
                    PftRoot(container)
                }
            }
        }
    }
}
