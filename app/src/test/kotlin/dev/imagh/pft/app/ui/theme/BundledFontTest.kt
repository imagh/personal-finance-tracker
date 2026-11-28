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
 * The bundled fonts and their licences: the display serif (design plan §3.5), the sans options
 * Figtree (the default) and Plus Jakarta Sans (premium-look plan §3.7), and the two Cobalt options
 * Manrope (sans) and Bricolage Grotesque (display; Cobalt plan D2, K2). Each font ships
 * unmodified, so its SHA-256 pins the provenance recorded in `docs/architecture/design-system.md`, and the SIL OFL
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

    @Test
    fun `the Plus Jakarta Sans font resource loads`() {
        assertNotNull(context.resources.getFont(R.font.plus_jakarta_sans))
    }

    @Test
    fun `the shipped Plus Jakarta Sans is the unmodified upstream variable file`() {
        val bytes = context.resources.openRawResource(R.font.plus_jakarta_sans).use { it.readBytes() }
        assertEquals(PLUS_JAKARTA_SANS_BYTES, bytes.size)
        assertEquals(PLUS_JAKARTA_SANS_SHA256, sha256(bytes))
    }

    @Test
    fun `the OFL text and the Plus Jakarta Sans copyright ship with the font`() {
        val text =
            context.assets.open("licenses/plus-jakarta-sans-OFL.txt").use { String(it.readBytes(), Charsets.UTF_8) }
        assertTrue(text.contains("SIL OPEN FONT LICENSE Version 1.1"))
        assertTrue(text.contains("2020 The Plus Jakarta Sans Project Authors"))
        assertFalse("Plus Jakarta Sans declares no Reserved Font Name", text.contains("with Reserved Font Name"))
    }

    @Test
    fun `the Manrope font resource loads`() {
        assertNotNull(context.resources.getFont(R.font.manrope))
    }

    @Test
    fun `the shipped Manrope is the unmodified upstream variable file`() {
        val bytes = context.resources.openRawResource(R.font.manrope).use { it.readBytes() }
        assertEquals(MANROPE_BYTES, bytes.size)
        assertEquals(MANROPE_SHA256, sha256(bytes))
    }

    @Test
    fun `the OFL text and the Manrope copyright ship with the font`() {
        val text = context.assets.open("licenses/manrope-OFL.txt").use { String(it.readBytes(), Charsets.UTF_8) }
        assertTrue(text.contains("SIL OPEN FONT LICENSE Version 1.1"))
        assertTrue(text.contains("Copyright 2018 The Manrope Project Authors (https://github.com/googlefonts/manrope)"))
        assertFalse("Manrope declares no Reserved Font Name", text.contains("with Reserved Font Name"))
    }

    @Test
    fun `the Bricolage Grotesque font resource loads`() {
        assertNotNull(context.resources.getFont(R.font.bricolage_grotesque))
    }

    @Test
    fun `the shipped Bricolage Grotesque is the unmodified upstream variable file`() {
        val bytes = context.resources.openRawResource(R.font.bricolage_grotesque).use { it.readBytes() }
        assertEquals(BRICOLAGE_BYTES, bytes.size)
        assertEquals(BRICOLAGE_SHA256, sha256(bytes))
    }

    @Test
    fun `the OFL text and the Bricolage Grotesque copyright ship with the font`() {
        val text =
            context.assets.open("licenses/bricolage-grotesque-OFL.txt").use { String(it.readBytes(), Charsets.UTF_8) }
        assertTrue(text.contains("SIL OPEN FONT LICENSE Version 1.1"))
        assertTrue(
            text.contains(
                "Copyright 2022 The Bricolage Grotesque Project Authors (https://github.com/ateliertriay/bricolage)",
            ),
        )
        // Checked against the upstream OFL.txt at the pinned commit: no Reserved Font Name is declared,
        // so the unmodified file may be bundled and, if size ever matters, subset (design-system.md).
        assertFalse("Bricolage Grotesque declares no Reserved Font Name", text.contains("with Reserved Font Name"))
    }

    private companion object {
        // Google Fonts ofl/newsreader at google/fonts commit 8b0a1d0f5983c89bc2b93f1b5fb55f9e252744b5.
        const val NEWSREADER_BYTES = 451_664
        const val NEWSREADER_SHA256 = "8a08d13f8a6c0d51be379a60af84f945f65369a67e509ee3c3bdcc421254d7c1"

        // Google Fonts ofl/figtree/Figtree[wght].ttf at google/fonts commit
        // 6c1dbe27dcf7d817d4b62cec033ba77fa8e2a5ec (DS6f record, premium-look plan §3.7).
        const val FIGTREE_BYTES = 62_712
        const val FIGTREE_SHA256 = "26ad3db9b31ff7dde67a91ff515d022d2f495cd506590699cf264f0bfe6fb714"

        // Google Fonts ofl/plusjakartasans/PlusJakartaSans[wght].ttf at the same pinned commit.
        const val PLUS_JAKARTA_SANS_BYTES = 176_288
        const val PLUS_JAKARTA_SANS_SHA256 = "89b3fb38aa0d275d7a731d0d817a4f1622b316b4d7fbdedcf02ee9099ff68bc8"

        // Google Fonts ofl/manrope/Manrope[wght].ttf at the same pinned commit (Cobalt plan K2).
        const val MANROPE_BYTES = 164_700
        const val MANROPE_SHA256 = "3ae11c49db0455a3cc33e37d380f20fdb8c7f8b41dc07625c177e3d87a9d6ae6"

        // Google Fonts ofl/bricolagegrotesque/BricolageGrotesque[opsz,wdth,wght].ttf at the same pinned commit.
        // Over 400 KB but under the pre-commit large-file limit (500 KB), so no hook exception is needed;
        // if this file is ever replaced by a larger one, add the path-scoped exception with this size and hash.
        const val BRICOLAGE_BYTES = 408_496
        const val BRICOLAGE_SHA256 = "413e7357809ddd12fd80a96a8a396de0e401638d4acd3cb3e37532f0472ac682"
    }
}
