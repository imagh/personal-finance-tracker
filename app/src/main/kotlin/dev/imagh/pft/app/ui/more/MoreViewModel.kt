package dev.imagh.pft.app.ui.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.ledger.identity.IdentityService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * What the More tab's header shows: who is using this phone, so the owner can see attribution
 * (the line Home used to show; K8's greeting reads the name again for Home).
 *
 * @property userName the household member's name, once [loaded].
 * @property deviceLabel this phone's name, once [loaded].
 * @property loaded false until the stored identity has been read; the screen shows no subtitle until
 *   then, so it never flashes an empty "  · ".
 */
data class MoreUiState(
    val userName: String = "",
    val deviceLabel: String = "",
    val loaded: Boolean = false,
)

/**
 * Reads who is using this phone ([IdentityService.requireIdentity]) for the More tab's subtitle. More
 * is only reachable once an identity exists (the launch route and Setup guarantee it), so a missing or
 * inconsistent identity here is a bug, not a user state: it is not caught, which ends the current work
 * loudly (the same fail-closed stance as `LaunchViewModel`) instead of showing a
 * made-up name. The names are shown on screen only and never logged.
 */
class MoreViewModel(
    private val identity: IdentityService,
) : ViewModel() {
    private val mutableState = MutableStateFlow(MoreUiState())

    /** The screen's state; [MoreUiState.loaded] is false until the identity has been read. */
    val state: StateFlow<MoreUiState> = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            val current = identity.requireIdentity()
            mutableState.value =
                MoreUiState(userName = current.user.displayName, deviceLabel = current.device.label, loaded = true)
        }
    }

    companion object {
        /** A factory that builds the ViewModel over [services] (there is no DI framework). */
        fun factory(services: AppServices): ViewModelProvider.Factory =
            viewModelFactory { initializer { MoreViewModel(services.identity) } }
    }
}
