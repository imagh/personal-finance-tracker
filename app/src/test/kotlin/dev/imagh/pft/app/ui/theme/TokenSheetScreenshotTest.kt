package dev.imagh.pft.app.ui.theme

import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import com.github.takahirom.roborazzi.captureRoboImage
import dev.imagh.pft.app.ui.HostedComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Golden screenshots of the token sheet pages (type scale, amount styles, shapes, spacing ruler and
 * the money tokens on surfaces), light and dark at font scales 1.0 and 2.0, on a 393x851dp xxhdpi
 * screen with the committed Graphite scheme (dynamic colour off, so the goldens are deterministic).
 *
 * `./gradlew check` verifies against `src/test/screenshots` (roborazzi.test.verify=true); after a
 * deliberate visual change run `./gradlew :app:recordRoborazziDebug` and review the PNG diff.
 * Fake data only. Pixels catch visual regressions; correctness is carried by the behaviour tests.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi")
class TokenSheetScreenshotTest {
    @get:Rule
    val compose = HostedComposeRule()

    private companion object {
        const val PAGE_TAG = "token-sheet-page"
    }

    /** Captures every [TokenSheetPages] page as `token-sheet-<page>-<variant>.png`. */
    private fun captureAllPages(
        variant: String,
        fontScale: Float,
    ) {
        var page by mutableStateOf(TokenSheetPages.first())
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                PftTheme(ThemePrefs(dynamicColor = false)) {
                    // Only the page's own bounds are captured, so the PNG is as tall as its content.
                    Surface(Modifier.testTag(PAGE_TAG)) { TokenSheetPageFrame(page) }
                }
            }
        }
        TokenSheetPages.forEach {
            page = it
            compose.waitForIdle()
            compose.onNodeWithTag(PAGE_TAG).captureRoboImage("src/test/screenshots/token-sheet-${it.slug}-$variant.png")
        }
    }

    @Test
    @Config(qualifiers = "+notnight")
    fun lightFontScale1() = captureAllPages("light-1x", fontScale = 1f)

    @Test
    @Config(qualifiers = "+notnight")
    fun lightFontScale2() = captureAllPages("light-2x", fontScale = 2f)

    @Test
    @Config(qualifiers = "+night")
    fun darkFontScale1() = captureAllPages("dark-1x", fontScale = 1f)

    @Test
    @Config(qualifiers = "+night")
    fun darkFontScale2() = captureAllPages("dark-2x", fontScale = 2f)
}
