package dev.imagh.pft.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dev.imagh.pft.app.ui.theme.PftTheme

/**
 * The single activity. Every screen is hosted here, so setting [WindowManager.LayoutParams.FLAG_SECURE]
 * once covers all of them: no screenshots, no screen recording, and a blank card in the recents
 * switcher (SEC-2). The flag is set before anything else, so no frame is ever capturable;
 * edge-to-edge and the content come after it.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            PftTheme {
                Surface(modifier = Modifier.fillMaxSize()) { DevContent() }
            }
        }
    }
}
