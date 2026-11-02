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
import dev.imagh.pft.ledger.identity.IdentityService
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.ledger.validation.Validation
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
 */
data class SetupUiState(
    val name: FieldState = FieldState(),
    val deviceLabel: FieldState = FieldState(),
    val saving: Boolean = false,
    val done: Boolean = false,
)

/**
 * First-run setup: collects the user's name and this phone's name and saves them as the identity.
 *
 * - **Validation is core's.** Both fields go through [FieldState.check] with
 *   [Validation.requireName] and core's [LedgerLimits.MAX_NAME_LENGTH]; this class has no rule of its
 *   own, and `IdentityService.initialise` validates again. Nothing is saved unless both are valid.
 * - **One save.** [submit] sets [SetupUiState.saving] before it launches the save and ignores calls
 *   while saving or done, so a double tap creates one user and three audit rows. It must be called
 *   on the main thread (the UI does), which is what makes the check-then-set atomic.
 * - **Already set up is done.** [LedgerError.AlreadyInitialised] (an identity exists, for example
 *   after a race) is treated as success, because an identity exists either way.
 * - **Logging.** One `setup_completed` event when this call saved the identity; it carries nothing.
 *
 * Any other failure is not caught: it ends the app's current work loudly rather than being hidden
 * (the same rule as `LaunchViewModel`).
 *
 * @param identity first-run setup.
 * @param defaultDeviceLabel the suggested phone name (the device model), trimmed and cut to the
 *   name limit for the prefill only; the user can edit it.
 */
class SetupViewModel(
    private val identity: IdentityService,
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

    /** The name field was edited to [text]; its error clears. */
    fun onNameChange(text: String) = mutableState.update { it.copy(name = it.name.edit(text)) }

    /** The phone-name field was edited to [text]; its error clears. */
    fun onDeviceLabelChange(text: String) = mutableState.update { it.copy(deviceLabel = it.deviceLabel.edit(text)) }

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
            mutableState.value = current.copy(name = name.state, deviceLabel = label.state)
            return
        }
        mutableState.value = current.copy(name = name.state, deviceLabel = label.state, saving = true)
        viewModelScope.launch {
            try {
                identity.initialise(validName, validLabel)
                SafeLog.event(LogEvent.SetupCompleted)
            } catch (_: LedgerError.AlreadyInitialised) {
                // An identity exists already: that is what setup is for, so it is done.
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
            viewModelFactory { initializer { SetupViewModel(services.identity, defaultDeviceLabel) } }
    }
}
