package dev.imagh.pft.app.ui.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.HostedComposeRule
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.TimeZone

/**
 * `DateField` (slice C C4b): a date shown in the device's locale that opens the M3 date picker. The picker
 * counts a date as that day's midnight in UTC whatever the device zone, so the field must convert the same
 * way: a date has to come back as the day that was picked in zones on both sides of UTC, and a stored date
 * has to be the day the dialog opens on.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w393dp-h1400dp-xxhdpi")
class DateFieldTest {
    @get:Rule
    val compose = HostedComposeRule()

    private val defaultZone = TimeZone.getDefault()
    private var date by mutableStateOf(LocalDate.of(2026, 11, 15))
    private val picked = mutableListOf<LocalDate>()

    @After
    fun restoreZone() = TimeZone.setDefault(defaultZone)

    private fun showField() {
        compose.setContent {
            PftTheme(ThemePrefs(dynamicColor = false)) {
                DateField(
                    label = "Date",
                    date = date,
                    onDateChange = {
                        picked += it
                        date = it
                    },
                )
            }
        }
        compose.waitForIdle()
    }

    private fun openDialog() {
        compose.onNodeWithContentDescription("Date, Nov 15, 2026").performClick()
        compose.waitForIdle()
    }

    // --- The conversion the picker needs

    @Test
    fun `a date is the picker's UTC midnight of that day, not the device zone's`() {
        listOf("Asia/Kolkata", "Pacific/Kiritimati", "Pacific/Pago_Pago", "America/Los_Angeles").forEach { zone ->
            TimeZone.setDefault(TimeZone.getTimeZone(zone))

            LocalDate.of(2026, 11, 15).toPickerMillis() shouldBe
                Instant.parse("2026-11-15T00:00:00Z").toEpochMilli()
        }
    }

    @Test
    fun `the picker's millis come back as the same day in zones either side of UTC, at the month and year edges`() {
        val days = listOf(LocalDate.of(2026, 11, 15), LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31))
        listOf("Asia/Kolkata", "Pacific/Kiritimati", "Pacific/Pago_Pago", "America/Los_Angeles").forEach { zone ->
            TimeZone.setDefault(TimeZone.getTimeZone(zone))

            days.forEach { it.toPickerMillis().toPickerDate() shouldBe it }
        }
    }

    @Test
    fun `any instant of the picker's day reads as that day`() {
        TimeZone.setDefault(TimeZone.getTimeZone("Pacific/Pago_Pago"))
        val midnight =
            LocalDate
                .of(2026, 11, 15)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()

        (midnight + MILLIS_BEFORE_NEXT_DAY).toPickerDate() shouldBe LocalDate.of(2026, 11, 15)
        (midnight + MILLIS_BEFORE_NEXT_DAY + 1).toPickerDate() shouldBe LocalDate.of(2026, 11, 16)
    }

    // --- The field

    @Test
    fun `the field says its label and the date, once, as a button`() {
        showField()

        val node = compose.onNodeWithContentDescription("Date, Nov 15, 2026").fetchSemanticsNode()
        node.config.getOrNull(SemanticsProperties.Role) shouldBe Role.Button
        compose.onAllNodesWithText("Nov 15, 2026").fetchSemanticsNodes().size shouldBe 0
    }

    @Test
    fun `the field is at least 48dp tall`() {
        showField()

        val bounds = compose.onNodeWithContentDescription("Date, Nov 15, 2026").fetchSemanticsNode().touchBoundsInRoot
        with(compose.density) { bounds.height.toDp() shouldBeGreaterThanOrEqualTo MIN_TARGET }
    }

    @Test
    fun `the dialog opens on the stored day in a zone behind UTC`() = assertOpensOnStoredDay("America/Los_Angeles")

    @Test
    fun `the dialog opens on the stored day in a zone ahead of UTC`() = assertOpensOnStoredDay("Pacific/Kiritimati")

    private fun assertOpensOnStoredDay(zone: String) {
        TimeZone.setDefault(TimeZone.getTimeZone(zone))
        showField()

        openDialog()

        compose.onAllNodes(isSelected() and hasText("Sunday, November 15, 2026")).fetchSemanticsNodes().size shouldBe 1
    }

    @Test
    fun `picking a day reports exactly that date, once, in a zone ahead of UTC`() = assertPicksNov20("Asia/Kolkata")

    @Test
    fun `picking a day reports that date, once, in a zone behind UTC`() = assertPicksNov20("America/Los_Angeles")

    private fun assertPicksNov20(zone: String) {
        TimeZone.setDefault(TimeZone.getTimeZone(zone))
        showField()
        openDialog()

        compose.onNode(hasText("Friday, November 20, 2026") and hasClickAction()).performClick()
        compose.onNodeWithText("OK").performClick()
        compose.waitForIdle()

        picked shouldBe listOf(LocalDate.of(2026, 11, 20))
    }

    @Test
    fun `cancelling the dialog reports nothing`() {
        showField()
        openDialog()

        compose.onNode(hasText("Friday, November 20, 2026") and hasClickAction()).performClick()
        compose.onNodeWithText("Cancel").performClick()
        compose.waitForIdle()

        picked shouldBe emptyList()
    }

    private companion object {
        /** The last millisecond of a UTC day. */
        const val MILLIS_BEFORE_NEXT_DAY = 24L * 60 * 60 * 1000 - 1

        val MIN_TARGET = 48.dp
    }
}
