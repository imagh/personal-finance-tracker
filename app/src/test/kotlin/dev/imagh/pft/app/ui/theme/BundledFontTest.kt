package dev.imagh.pft.app.ui.theme

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.security.MessageDigest

/**
 * The bundled display serif and its licence (design plan §3.5). The font ships unmodified, so its
 * SHA-256 pins the provenance recorded in `docs/architecture/design-system.md`, and the SIL OFL
 * text must travel with every copy (OFL §2).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BundledFontTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    private fun sha256(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

    @Test
    fun `the Newsreader font resource loads`() {
        assertNotNull(context.resources.getFont(R.font.newsreader))
    }

    @Test
    fun `the shipped font is the unmodified upstream file`() {
        val bytes = context.resources.openRawResource(R.font.newsreader).use { it.readBytes() }
        assertEquals(NEWSREADER_BYTES, bytes.size)
        assertEquals(NEWSREADER_SHA256, sha256(bytes))
    }

    @Test
    fun `the OFL text and the Newsreader copyright ship with the font`() {
        val text = context.assets.open("licenses/newsreader-OFL.txt").use { String(it.readBytes(), Charsets.UTF_8) }
        assertTrue(text.contains("SIL OPEN FONT LICENSE Version 1.1"))
        assertTrue(
            text.contains(
                "Copyright 2020 The Newsreader Project Authors (http://github.com/productiontype/Newsreader)",
            ),
        )
    }

    private companion object {
        // Google Fonts ofl/newsreader at google/fonts commit 8b0a1d0f5983c89bc2b93f1b5fb55f9e252744b5.
        const val NEWSREADER_BYTES = 451_664
        const val NEWSREADER_SHA256 = "8a08d13f8a6c0d51be379a60af84f945f65369a67e509ee3c3bdcc421254d7c1"
    }
}
