package dev.imagh.pft.app.ui.theme

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The Material Symbols vector drawables and their licence (design plan §3.6). Apache-2.0 section 4
 * requires the licence to accompany redistributed copies, so the full text and the attribution
 * (source repository and pinned commit) must ship as an asset.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BundledSymbolsLicenceTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    private val text =
        context.assets.open("licenses/material-symbols-APACHE-2.0.txt").use { String(it.readBytes(), Charsets.UTF_8) }

    @Test
    fun `the Apache 2_0 text ships with the icons`() {
        assertTrue(text.contains("Apache License"))
        assertTrue(text.contains("Version 2.0, January 2004"))
        assertTrue(text.contains("TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION"))
        assertTrue(text.contains("END OF TERMS AND CONDITIONS"))
    }

    @Test
    fun `the attribution names the source and the pinned commit`() {
        assertTrue(text.contains("Material Symbols"))
        assertTrue(text.contains("Google LLC"))
        assertTrue(text.contains("https://github.com/google/material-design-icons"))
        assertTrue(text.contains("10ecc14790612ca8fd164e3f4ffe9c16fc50d5b7"))
    }
}
