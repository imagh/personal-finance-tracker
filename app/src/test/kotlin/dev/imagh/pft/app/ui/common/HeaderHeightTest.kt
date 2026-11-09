package dev.imagh.pft.app.ui.common

import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** The header height rule of plan §3.4: `clamp(0.38 x window height, 200dp, 360dp)`, collapsed when short. Pure JVM. */
class HeaderHeightTest {
    @Test
    fun `a tall phone gets 38 percent of the window height`() {
        assertEquals(323.38f, expandedHeaderHeight(851.dp).value, 0.01f)
    }

    @Test
    fun `a very tall window is capped at 360dp`() {
        assertEquals(360f, expandedHeaderHeight(1200.dp).value, 0f)
        assertEquals(360f, expandedHeaderHeight(947.37.dp).value, 0.01f)
    }

    @Test
    fun `the shortest window that can expand gets about 213dp, above the 200dp floor`() {
        assertEquals(212.8f, expandedHeaderHeight(560.dp).value, 0.01f)
    }

    @Test
    fun `a window shorter than 560dp has no expanded state, so its height is the collapsed bar`() {
        assertEquals(COLLAPSED_HEADER_HEIGHT, expandedHeaderHeight(559.dp))
        assertEquals(COLLAPSED_HEADER_HEIGHT, expandedHeaderHeight(0.dp))
    }

    @Test
    fun `the collapsed bar is 64dp`() {
        assertEquals(64f, COLLAPSED_HEADER_HEIGHT.value, 0f)
    }
}
