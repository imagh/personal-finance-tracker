package dev.imagh.pft.app

import android.view.WindowManager
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.app.data.DatabaseStartup
import dev.imagh.pft.app.di.AppContainer
import dev.imagh.pft.app.security.DbKeyUnavailable
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * The activity does not wait for the database (NFR responsiveness, the A4b follow-up): with an
 * opener that is held open, the activity still starts, composes its first frame, is `FLAG_SECURE`,
 * and draws no text (the Opening state). Only when the opener finishes does the screen appear.
 *
 * If the open ran on the main thread, the activity could not even be created before the opener was
 * released, and the opener only releases when this test does, so the rule would time out.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [35], qualifiers = "w393dp-h851dp-xxhdpi", application = SlowOpeningApplication::class)
class MainActivityOpeningTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    private val app get() = ApplicationProvider.getApplicationContext<SlowOpeningApplication>()

    @Test
    fun `the first composition happens before the open finishes, and shows no text`() {
        app.opening.await(10, TimeUnit.SECONDS) shouldBe true
        compose.waitForIdle()

        app.finished shouldBe false
        compose.activity.window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE shouldBe
            WindowManager.LayoutParams.FLAG_SECURE
        compose.onAllNodesWithText("Your data can't be opened").fetchSemanticsNodes().size shouldBe 0
        compose.onRoot().printToString() shouldNotContain "Text"

        app.release.countDown()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText("Code: KEK_MISSING").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Code: KEK_MISSING").assertIsDisplayed()
        app.finished shouldBe true
    }
}

/** An application whose container's open waits for [release]; stands in for a slow first open. */
class SlowOpeningApplication : PftApplication() {
    /** Counted down when the opener has started. */
    val opening = CountDownLatch(1)

    /** The test counts this down to let the opener finish. */
    val release = CountDownLatch(1)

    @Volatile
    var finished = false

    override val container: AppContainer by lazy {
        AppContainer(openDatabase = {
            opening.countDown()
            release.await(10, TimeUnit.SECONDS)
            finished = true
            DatabaseStartup.KeyUnavailable(DbKeyUnavailable.Reason.KEK_MISSING)
        })
    }
}
