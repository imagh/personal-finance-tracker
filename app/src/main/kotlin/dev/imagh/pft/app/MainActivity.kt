package dev.imagh.pft.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dev.imagh.pft.app.data.DatabaseStartup
import dev.imagh.pft.app.ui.startup.KeyUnavailableScreen
import dev.imagh.pft.app.ui.theme.PftTheme

/**
 * The single activity. Every screen is hosted here, so setting [WindowManager.LayoutParams.FLAG_SECURE]
 * once covers all of them: no screenshots, no screen recording, and a blank card in the recents
 * switcher (SEC-2). The flag is set before anything else, so no frame is ever capturable;
 * edge-to-edge and the content come after it.
 *
 * What it shows depends on how opening the encrypted database ended ([PftApplication.startup]): the
 * app when it opened, [KeyUnavailableScreen] when the key cannot be produced (fail closed).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val startup = (application as PftApplication).startup
        setContent {
            PftTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (startup) {
                        is DatabaseStartup.Ready -> DevContent()
                        is DatabaseStartup.KeyUnavailable -> KeyUnavailableScreen(startup.reason)
                    }
                }
            }
        }
    }
}
