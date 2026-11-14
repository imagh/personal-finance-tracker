package dev.imagh.pft.app.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.log.LogEvent
import dev.imagh.pft.app.log.SafeLog
import dev.imagh.pft.app.ui.common.FieldState
import dev.imagh.pft.ledger.setup.FirstRunService
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.ledger.validation.Validation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Everything the first-run setup screen shows.
 *
 * @property name the household member's name, as typed, with its error.
 * @property deviceLabel this phone's name, as typed, with its error.
 * @property saving true from a valid Continue until the save finishes; the screen disables Continue.
 * @property done true once an identity exists (saved now, or already there); the route then leaves
 *   the screen. A one-shot outcome held as state, not a channel.
 * @property saveFailed true after an unexpected failure to save: the screen shows a fixed "try again"
 *   message. It is cleared by editing a field or by submitting again; the typed text is kept.
 */
data class SetupUiState(
    val name: FieldState = FieldState(),
    val deviceLabel: FieldState = FieldState(),
    val saving: Boolean = false,
    val done: Boolean = false,
    val saveFailed: Boolean = false,
)

/**
 * First-run setup: collects the user's name and this phone's name and saves them as the identity, together
 * with the default categories (`FirstRunService`: one transaction).
 *
 * - **Validation is core's.** Both fields go through [FieldState.check] with
 *   [Validation.requireName] and core's [LedgerLimits.MAX_NAME_LENGTH]; this class has no rule of its
 *   own, and `FirstRunService.initialise` validates again. Nothing is saved unless both are valid.
 * - **One save.** [submit] sets [SetupUiState.saving] before it launches the save and ignores calls
 *   while saving or done, so a double tap creates one user and one set of defaults. It must be called
 *   on the main thread (the UI does), which is what makes the check-then-set atomic.
 * - **Already set up is done.** [LedgerError.AlreadyInitialised] (an identity exists, for example
 *   after a race) is treated as success, because an identity exists either way.
 * - **Logging.** One `setup_completed` event when this call saved the identity; it carries nothing.
 *
 * - **An unexpected failure** (anything that is not a [LedgerError] already handled, and not a
 *   cancellation) is caught: [SetupUiState.saving] is reset, [SetupUiState.saveFailed] is set (a fixed
 *   message, never exception text), both typed fields are kept as typed, and one field-less
 *   `setup_failed` event is logged. Retrying is safe: `FirstRunService.initialise` writes the user,
 *   device, setting, default categories and audit rows in one transaction, so a failed attempt leaves
 *   nothing behind.
 *   A [CancellationException] is rethrown, so cancelling the ViewModel is never reported as a failure.
 *
 * @param firstRun first-run setup: the identity and the default categories.
 * @param defaultDeviceLabel the suggested phone name (the device model), trimmed and cut to the
 *   name limit for the prefill only; the user can edit it.
 */
class SetupViewModel(
    private val firstRun: FirstRunService,
    defaultDeviceLabel: String,
) : ViewModel() {
    private val mutableState =
        MutableStateFlow(
            SetupUiState(
                deviceLabel = FieldState(defaultDeviceLabel.trim().take(LedgerLimits.MAX_NAME_LENGTH)),
            ),
        )

    /** The screen's state. */
    val state: StateFlow<SetupUiState> = mutableState.asStateFlow()

    /** The name field was edited to [text]; its error, and any save-failed message, clears. */
    fun onNameChange(text: String) = mutableState.update { it.copy(name = it.name.edit(text), saveFailed = false) }

    /** The phone-name field was edited to [text]; its error, and any save-failed message, clears. */
    fun onDeviceLabelChange(text: String) =
        mutableState.update { it.copy(deviceLabel = it.deviceLabel.edit(text), saveFailed = false) }

    /**
     * Continue was tapped: validates both fields and, if both are valid, saves the identity. Errors
     * appear on the field(s) at fault; nothing is saved then. Ignored while saving or done.
     */
    fun submit() {
        val current = mutableState.value
        if (current.saving || current.done) return
        val name = current.name.check { Validation.requireName(it, LedgerLimits.MAX_NAME_LENGTH) }
        val label = current.deviceLabel.check { Validation.requireName(it, LedgerLimits.MAX_NAME_LENGTH) }
        val validName = name.value
        val validLabel = label.value
        if (validName == null || validLabel == null) {
            mutableState.value = current.copy(name = name.state, deviceLabel = label.state, saveFailed = false)
            return
        }
        mutableState.value =
            current.copy(name = name.state, deviceLabel = label.state, saving = true, saveFailed = false)
        viewModelScope.launch {
            try {
                firstRun.initialise(validName, validLabel)
                SafeLog.event(LogEvent.SetupCompleted)
            } catch (_: LedgerError.AlreadyInitialised) {
                // An identity exists already: that is what setup is for, so it is done.
            } catch (cancelled: CancellationException) {
                // `saving` is deliberately left as it is: the scope is being cancelled (the ViewModel is
                // cleared), so there is no screen left to update. A TimeoutCancellationException also
                // takes this path, so if a timeout is ever added to this save it needs its own handling.
                throw cancelled
            } catch (_: Exception) {
                // Deliberately broad: whatever went wrong (a full disk, a locked database, a bug), the
                // user can retry, and the save is one transaction so nothing partial exists. The
                // exception is dropped on purpose: logs and screens must not carry its text (SEC-5).
                SafeLog.event(LogEvent.SetupFailed)
                mutableState.update { it.copy(saving = false, saveFailed = true) }
                return@launch
            }
            mutableState.update { it.copy(saving = false, done = true) }
        }
    }

    companion object {
        /** A factory that builds the ViewModel over [services] (there is no DI framework). */
        fun factory(
            services: AppServices,
            defaultDeviceLabel: String,
        ): ViewModelProvider.Factory =
            viewModelFactory { initializer { SetupViewModel(services.firstRun, defaultDeviceLabel) } }
    }
}
