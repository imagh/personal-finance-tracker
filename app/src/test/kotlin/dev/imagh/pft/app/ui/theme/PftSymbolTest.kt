package dev.imagh.pft.app.ui.theme

import android.graphics.drawable.VectorDrawable
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * `PftSymbol` is the icon registry whose keys are persisted in `category.icon_key`, so the keys
 * are permanent (plan §3.6): the pinned list `pft-symbol-keys.txt` turns a rename or a removal
 * into a failing test.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PftSymbolTest {
    private val context = ApplicationProvider.getApplicationContext<android.app.Application>()
    private val grammar = Regex("[a-z0-9_]{1,40}")

    private fun pinnedKeys(): List<String> =
        checkNotNull(PftSymbolTest::class.java.getResourceAsStream("/pft-symbol-keys.txt")) {
            "pft-symbol-keys.txt is missing from the test resources"
        }.bufferedReader().readLines().map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith("#") }

    @Test
    fun `keys are unique and match the key grammar`() {
        val keys = PftSymbol.entries.map { it.key }
        val duplicates = keys.groupBy { it }.filterValues { it.size > 1 }.keys
        assertTrue("duplicate keys: $duplicates", duplicates.isEmpty())
        val bad = keys.filterNot { grammar.matches(it) }
        assertTrue("keys outside [a-z0-9_]{1,40}: $bad", bad.isEmpty())
    }

    @Test
    fun `every pinned key still exists, because persisted keys are permanent`() {
        val missing = pinnedKeys().filter { PftSymbol.fromKey(it) == null }
        assertTrue("persisted keys were renamed or removed: $missing", missing.isEmpty())
    }

    @Test
    fun `every key in the registry is pinned, so adding one is a deliberate edit of the list`() {
        val unpinned = PftSymbol.entries.map { it.key } - pinnedKeys().toSet()
        assertTrue("add these to pft-symbol-keys.txt: $unpinned", unpinned.isEmpty())
    }

    @Test
    fun `every symbol resolves to a vector drawable that inflates`() {
        for (symbol in PftSymbol.entries) {
            val drawable = context.resources.getDrawable(symbol.res, context.theme)
            assertTrue("${symbol.key} is ${drawable::class.java}", drawable is VectorDrawable)
            assertTrue("${symbol.key} has no size", drawable.intrinsicWidth > 0 && drawable.intrinsicHeight > 0)
        }
    }

    @Test
    fun `every symbol drawable in the resources is registered`() {
        val declared =
            R.drawable::class.java.fields
                .map { it.name }
                .filter { it.startsWith("sym_") }
                .map { it.removePrefix("sym_") }
                .toSet()
        assertEquals(declared, PftSymbol.entries.map { it.key }.toSet())
    }

    @Test
    fun `each symbol points at the drawable named after its key`() {
        for (symbol in PftSymbol.entries) {
            val expected = R.drawable::class.java.getField("sym_${symbol.key}").getInt(null)
            assertEquals(symbol.key, expected, symbol.res)
        }
    }

    @Test
    fun `fromKey finds a known key`() {
        assertSame(PftSymbol.BACK, PftSymbol.fromKey("back"))
        assertSame(PftSymbol.LOCAL_CAFE, PftSymbol.fromKey("local_cafe"))
    }

    @Test
    fun `fromKey is null for an unknown, empty, mis-cased or padded key`() {
        listOf("nope", "", "BACK", "Back", " back", "back ", "sym:back", "../back", "back\n").forEach {
            assertNull("'$it'", PftSymbol.fromKey(it))
        }
    }

    @Test
    fun `the chrome symbols the header actions depend on exist under their agreed keys`() {
        listOf("back", "search", "filter", "overflow", "chevron_left", "chevron_right").forEach {
            assertNotNull(it, PftSymbol.fromKey(it))
        }
    }
}
