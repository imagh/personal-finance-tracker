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
 * What Home's greeting header shows. The phone's name is not here: it is the More tab's attribution
 * line (plan D8).
 *
 * @property userName the household member's name, once [loaded].
 * @property loaded false until the stored identity has been read; the header draws nothing until then,
 *   so it never flashes a greeting with no name.
 */
data class HomeUiState(
    val userName: String = "",
    val loaded: Boolean = false,
)

/**
 * Reads who is using this phone ([IdentityService.requireIdentity]) for Home's greeting. Home is only
 * reachable once an identity exists, so a missing identity is a bug, not a user state: it is not caught
 * (the same fail-closed stance as `MoreViewModel`). The name is shown on screen only and never logged.
 */
class HomeViewModel(
    private val identity: IdentityService,
) : ViewModel() {
    private val mutableState = MutableStateFlow(HomeUiState())

    /** The screen's state; [HomeUiState.loaded] is false until the identity has been read. */
    val state: StateFlow<HomeUiState> = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            mutableState.value = HomeUiState(userName = identity.requireIdentity().user.displayName, loaded = true)
        }
    }

    companion object {
        /** A factory that builds the ViewModel over [services] (there is no DI framework). */
        fun factory(services: AppServices): ViewModelProvider.Factory =
            viewModelFactory { initializer { HomeViewModel(services.identity) } }
    }
}
