package dev.imagh.pft.app.ui.theme

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.security.MessageDigest

/**
 * The bundled fonts and their licences: the display serif (design plan §3.5) and the default sans
 * Figtree (premium-look plan §3.7). The font ships unmodified, so its
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

    @Test
    fun `the Figtree font resource loads`() {
        assertNotNull(context.resources.getFont(R.font.figtree))
    }

    @Test
    fun `the shipped Figtree is the unmodified upstream variable file`() {
        val bytes = context.resources.openRawResource(R.font.figtree).use { it.readBytes() }
        assertEquals(FIGTREE_BYTES, bytes.size)
        assertEquals(FIGTREE_SHA256, sha256(bytes))
    }

    @Test
    fun `the OFL text and the Figtree copyright ship with the font`() {
        val text = context.assets.open("licenses/figtree-OFL.txt").use { String(it.readBytes(), Charsets.UTF_8) }
        assertTrue(text.contains("SIL OPEN FONT LICENSE Version 1.1"))
        assertTrue(
            text.contains("Copyright 2022 The Figtree Project Authors (https://github.com/erikdkennedy/figtree)"),
        )
        assertFalse("Figtree declares no Reserved Font Name", text.contains("with Reserved Font Name"))
    }

    private companion object {
        // Google Fonts ofl/newsreader at google/fonts commit 8b0a1d0f5983c89bc2b93f1b5fb55f9e252744b5.
        const val NEWSREADER_BYTES = 451_664
        const val NEWSREADER_SHA256 = "8a08d13f8a6c0d51be379a60af84f945f65369a67e509ee3c3bdcc421254d7c1"

        // Google Fonts ofl/figtree/Figtree[wght].ttf at google/fonts commit
        // 6c1dbe27dcf7d817d4b62cec033ba77fa8e2a5ec (DS6f record, premium-look plan §3.7).
        const val FIGTREE_BYTES = 62_712
        const val FIGTREE_SHA256 = "26ad3db9b31ff7dde67a91ff515d022d2f495cd506590699cf264f0bfe6fb714"
    }
}
