package dev.imagh.pft.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.imagh.pft.app.di.AppContainer
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.di.AppStart
import dev.imagh.pft.app.log.LogEvent
import dev.imagh.pft.app.log.SafeLog
import dev.imagh.pft.app.security.DbKeyUnavailable
import dev.imagh.pft.ledger.validation.LedgerError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** What the app is doing at launch, as the root composable needs to draw it. */
sealed interface LaunchState {
    /** The database is opening; nothing is drawn but the canvas. */
    data object Opening : LaunchState

    /** Fail closed: the database key cannot be produced; [reason] is a fixed code safe to show. */
    data class KeyUnavailable(
        val reason: DbKeyUnavailable.Reason,
    ) : LaunchState

    /** Fail closed: the stored identity is inconsistent. Never treated as a new install. */
    data object IdentityCorrupt : LaunchState

    /** The database is open. [hasIdentity] is false only on a fresh install (first-run setup is due). */
    data class Ready(
        val services: AppServices,
        val hasIdentity: Boolean,
    ) : LaunchState
}

/**
 * Decides what the app opens to: it waits for [AppContainer.open], and for the open database reads
 * the current identity, mapping both to a [LaunchState].
 *
 * An inconsistent identity ([LedgerError.IdentityCorrupt]) becomes [LaunchState.IdentityCorrupt]
 * (and one `identity_corrupt` log line) instead of "no identity", so a damaged database is never
 * mistaken for a new install and set up over. Anything else that goes wrong while opening is not
 * caught: it is thrown in the ViewModel's coroutine, which has no handler, so it ends the start
 * without touching data, exactly as `DatabaseStartup` documents.
 *
 * The work runs on [io] rather than the main dispatcher, so a test can wait for the state from the
 * (Robolectric) main thread without deadlocking the looper.
 */
class LaunchViewModel(
    container: AppContainer,
    io: CoroutineDispatcher,
) : ViewModel() {
    private val mutableState = MutableStateFlow<LaunchState>(LaunchState.Opening)

    /** The launch state; [LaunchState.Opening] until the database has answered. */
    val state: StateFlow<LaunchState> = mutableState.asStateFlow()

    init {
        viewModelScope.launch(io) {
            mutableState.value =
                when (val start = container.open()) {
                    is AppStart.KeyUnavailable -> LaunchState.KeyUnavailable(start.reason)
                    is AppStart.Ready -> readyOrCorrupt(start.services)
                }
        }
    }

    private suspend fun readyOrCorrupt(services: AppServices): LaunchState =
        try {
            LaunchState.Ready(services, hasIdentity = services.identity.currentIdentity() != null)
        } catch (_: LedgerError.IdentityCorrupt) {
            SafeLog.event(LogEvent.IdentityCorrupt)
            LaunchState.IdentityCorrupt
        }

    companion object {
        /** A factory that builds the ViewModel over [container] (there is no DI framework). */
        fun factory(
            container: AppContainer,
            io: CoroutineDispatcher = Dispatchers.IO,
        ): ViewModelProvider.Factory = viewModelFactory { initializer { LaunchViewModel(container, io) } }
    }
}
