package dev.imagh.pft.app

import android.view.WindowManager
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.security.DbKeyManager
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * The whole startup path under Robolectric, which has no Android Keystore: opening the database
 * fails closed, the real `PftApplication` reports it, and `MainActivity` shows the key-unavailable
 * screen (with the reason code) instead of the app. No database or key file is created, and the
 * window stays `FLAG_SECURE` on this screen too.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class MainActivityStartupTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun `an unusable Keystore shows the key-unavailable screen and creates nothing`() {
        compose.onNodeWithText("Your data can't be opened").assertIsDisplayed()
        compose.onNodeWithText("Code: KEK_UNUSABLE").assertIsDisplayed()
        val context = ApplicationProvider.getApplicationContext<PftApplication>()
        context.getDatabasePath(PftDatabase.NAME).exists() shouldBe false
        File(context.noBackupFilesDir, DbKeyManager.KEY_FILE_NAME).exists() shouldBe false
    }

    @Test
    fun `the key-unavailable screen is FLAG_SECURE`() {
        compose.activity.window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE shouldBe
            WindowManager.LayoutParams.FLAG_SECURE
    }
}
