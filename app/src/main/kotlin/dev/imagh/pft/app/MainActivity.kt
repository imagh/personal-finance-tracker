package dev.imagh.pft.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

/**
 * The single activity. Every screen is hosted here, so setting [WindowManager.LayoutParams.FLAG_SECURE]
 * once covers all of them: no screenshots, no screen recording, and a blank card in the recents
 * switcher (SEC-2). The flag is set before any content so no frame is ever capturable.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {}
            }
        }
    }
}
