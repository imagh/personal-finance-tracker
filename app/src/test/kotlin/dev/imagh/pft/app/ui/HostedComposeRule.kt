package dev.imagh.pft.app.ui

import android.app.Application
import android.content.ComponentName
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.robolectric.Shadows.shadowOf

/**
 * `createComposeRule()` for Robolectric tests that run on the debug **and** release unit-test tasks.
 *
 * The stock rule launches `androidx.activity.ComponentActivity`, which only `ui-test-manifest`
 * registers. That library puts an exported activity into the merged manifest (debug included),
 * which `ManifestHardeningTest` forbids, so it is not a dependency. This rule registers the host
 * activity with Robolectric's package manager before the Compose rule starts. Use it in place of
 * `createComposeRule()` in this module. Instrumented tests (A4b) will need their own answer.
 */
class HostedComposeRule private constructor(
    private val inner: ComposeContentTestRule,
) : ComposeContentTestRule by inner {
    constructor() : this(createComposeRule())

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement = RuleChain.outerRule(RegisterHostActivity).around(inner).apply(base, description)

    private object RegisterHostActivity : TestRule {
        override fun apply(
            base: Statement,
            description: Description,
        ) = object : Statement() {
            override fun evaluate() {
                val app = ApplicationProvider.getApplicationContext<Application>()
                shadowOf(app.packageManager)
                    .addActivityIfNotPresent(ComponentName(app.packageName, ComponentActivity::class.java.name))
                base.evaluate()
            }
        }
    }
}
