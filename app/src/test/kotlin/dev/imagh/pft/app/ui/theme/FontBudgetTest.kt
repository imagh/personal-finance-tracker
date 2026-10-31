package dev.imagh.pft.app.ui.theme

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.reflect.Modifier

/**
 * The size budget for bundled fonts (UI-17, premium-look plan §3.7): all of `res/font` together
 * stay within [FONT_BUDGET_BYTES]. Every `R.font.*` resource is counted, so a font added later
 * (Plus Jakarta Sans in DS6i) is covered without editing this test. A font over the pre-commit
 * hook's 500 KB limit would also need its own path-scoped exception, backed by [BundledFontTest]'s exact size and hash.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class FontBudgetTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    /** Resource name to size in bytes, for every `R.font.*` field. */
    private fun fontSizes(): Map<String, Int> =
        R.font::class.java.declaredFields
            .filter { Modifier.isStatic(it.modifiers) && it.type == Int::class.javaPrimitiveType }
            .associate { field ->
                field.name to context.resources.openRawResource(field.getInt(null)).use { it.readBytes().size }
            }

    @Test
    fun `the bundled fonts together stay within the 3 MiB budget`() {
        val sizes = fontSizes()
        val total = sizes.values.sum()
        assertTrue("res/font is $total B over $sizes, budget $FONT_BUDGET_BYTES B", total <= FONT_BUDGET_BYTES)
    }

    @Test
    fun `the budget counts both bundled fonts`() {
        val sizes = fontSizes()
        assertEquals(setOf("newsreader", "figtree"), sizes.keys.intersect(setOf("newsreader", "figtree")))
        assertTrue("every font file is non-empty", sizes.values.all { it > 0 })
    }

    private companion object {
        /** 3 MiB for all of `res/font` (owner-confirmed 2026-10-30, plan §3.7). */
        const val FONT_BUDGET_BYTES = 3 * 1024 * 1024
    }
}
