package dev.imagh.pft.app.ui.home

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
 * Everything the Home placeholder shows.
 *
 * @property userName the household member's name, once [loaded].
 * @property deviceLabel this phone's name, once [loaded].
 * @property loaded false until the stored identity has been read; the screen shows no subtitle until
 *   then, so it never flashes an empty "  · ".
 */
data class HomeUiState(
    val userName: String = "",
    val deviceLabel: String = "",
    val loaded: Boolean = false,
)

/**
 * Reads who is using this phone ([IdentityService.requireIdentity]) so Home can show it. Home is
 * only reachable once an identity exists (the launch route and Setup guarantee it), so a missing or
 * inconsistent identity here is a bug, not a user state: it is not caught, which ends the current
 * work loudly (the same fail-closed stance as `LaunchViewModel`) instead of showing a made-up name.
 * C4 replaces this ViewModel with the real Home.
 */
class HomeViewModel(
    private val identity: IdentityService,
) : ViewModel() {
    private val mutableState = MutableStateFlow(HomeUiState())

    /** The screen's state; [HomeUiState.loaded] is false until the identity has been read. */
    val state: StateFlow<HomeUiState> = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            val current = identity.requireIdentity()
            mutableState.value =
                HomeUiState(userName = current.user.displayName, deviceLabel = current.device.label, loaded = true)
        }
    }

    companion object {
        /** A factory that builds the ViewModel over [services] (there is no DI framework). */
        fun factory(services: AppServices): ViewModelProvider.Factory =
            viewModelFactory { initializer { HomeViewModel(services.identity) } }
    }
}
