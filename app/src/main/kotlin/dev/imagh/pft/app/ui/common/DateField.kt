package dev.imagh.pft.app.ui.common

import android.text.format.DateFormat
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.theme.PftSymbol
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/** The date skeleton the app shows a day in ("Nov 15, 2026"), turned into the locale's own pattern. */
private const val DATE_SKELETON = "yMMMd"

/**
 * This date as the M3 date picker counts it: **midnight UTC of that day**, whatever the device zone. The
 * picker reads and writes its selection as UTC milliseconds, so using the device zone here would open the
 * dialog on the wrong day (and return the wrong one) wherever the zone is not UTC.
 */
internal fun LocalDate.toPickerMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

/** The day the M3 date picker's [this] milliseconds are in, read as UTC (see [toPickerMillis]). */
internal fun Long.toPickerDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

/** The locale's short date format ("Nov 15, 2026"), rebuilt when the locale changes. */
@Composable
internal fun rememberDateFormatter(): DateTimeFormatter {
    val locale = LocalConfiguration.current.locales[0]
    return remember(locale) {
        DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, DATE_SKELETON), locale)
    }
}

/**
 * A form field that shows a date and opens the M3 date picker in a dialog when tapped. A new shared block
 * (slice C D8); named consumers: the transaction form, and later the budget anchor, a recurring start date,
 * a loan start and a goal's target date. It holds the one opt-in to M3's experimental date picker.
 *
 * - **A date, not an instant.** [date] is a calendar day with no zone; the picker's UTC-midnight convention
 *   is converted on the way in and out ([toPickerMillis], [toPickerDate]), so the day shown, the day the
 *   dialog opens on and the day reported back are always the same in any device zone. What time of day an
 *   entry has is the caller's decision.
 * - **Choosing.** OK reports the picked day through [onDateChange] once and closes the dialog; Cancel, Back
 *   or a tap outside closes it and reports nothing. OK with no day selected (the user cleared it) reports
 *   nothing.
 * - **TalkBack.** One button that says the label and the date together (see [SelectField]); the dialog is
 *   the platform's picker. The field is a 48dp target at least.
 * - **Logging.** Nothing: a date is user data.
 *
 * @param label the field's name, for example "Date".
 * @param date the chosen day.
 * @param onDateChange called with the day the user picked and confirmed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    var open by rememberSaveable { mutableStateOf(false) }
    val shown = date.format(rememberDateFormatter())
    SelectField(
        label = label,
        value = shown,
        spoken = "$label, $shown",
        trailing = PftSymbol.CALENDAR,
        onClick = { open = true },
        modifier = modifier,
    )
    if (open) {
        val state = rememberDatePickerState(initialSelectedDateMillis = date.toPickerMillis())
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        open = false
                        state.selectedDateMillis?.let { onDateChange(it.toPickerDate()) }
                    },
                ) { Text(stringResource(R.string.date_picker_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { open = false }) { Text(stringResource(R.string.date_picker_cancel)) }
            },
        ) { DatePicker(state) }
    }
}
