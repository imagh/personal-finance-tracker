package dev.imagh.pft.app.ui.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.log.EntityKind
import dev.imagh.pft.app.ui.common.FieldState
import dev.imagh.pft.app.ui.common.SaveOutcome
import dev.imagh.pft.app.ui.common.launchSave
import dev.imagh.pft.app.ui.common.toFieldError
import dev.imagh.pft.ledger.tag.Tag
import dev.imagh.pft.ledger.tag.TagService
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.ledger.validation.Validation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Everything the tag form shows.
 *
 * @property editing true when an existing tag is being edited, false when one is being created.
 * @property loaded false until the tag being edited has been read, so the screen draws no field and no
 *   "not found" before the answer is known. Always true when creating.
 * @property notFound true when the tag asked for does not exist, was deleted, or the id was not an id at
 *   all (the id is an untrusted navigation argument). The route then leaves the screen.
 * @property name the tag's name, as typed, with its error (a duplicate shows here).
 * @property saving true from a valid Save until the save finishes; the screen disables Save.
 * @property saved true once the tag is saved; the route then leaves the screen. A one-shot outcome held
 *   as state, not a channel.
 * @property saveFailed true after an unexpected failure to save: the screen shows a fixed "try again"
 *   message. It is cleared by editing the name or by submitting again; the typed text is kept.
 */
data class TagEditUiState(
    val editing: Boolean = false,
    val loaded: Boolean = true,
    val notFound: Boolean = false,
    val name: FieldState = FieldState(),
    val saving: Boolean = false,
    val saved: Boolean = false,
    val saveFailed: Boolean = false,
)

/**
 * Creates or renames one tag (plan D10, D12). A tag is only a name, so this is the smallest of the entity
 * forms, with the same save shape as the others ([launchSave]).
 *
 * - **Validation is core's.** The name goes through `FieldState.check` with `Validation.requireName`; an
 *   invalid name shows its error under the field at once and nothing is saved. [TagService] validates
 *   again on save.
 * - **A duplicate is an answer.** A name another live tag already has (ignoring case and surrounding
 *   spaces; archived tags count, deleted ones do not) is refused by [TagService] inside its write
 *   transaction. `launchSave` hands that [LedgerError.DuplicateName] to this form first, which shows
 *   "Already in use" under the name field, keeps the typed text and logs nothing. Core does the check, so
 *   two saves cannot both pass it.
 * - **Edit mode** reads the tag on construction (active or archived) and prefills the name. An unknown,
 *   deleted or malformed id ends in [TagEditUiState.notFound]: the id is compared as text against the
 *   stored ids and never trusted to be one. A rename that changes nothing writes no audit entry.
 * - **One save.** [submit] sets [TagEditUiState.saving] before it launches the save and ignores calls
 *   while saving, once saved, before an edit has loaded, and after not-found. It must be called on the
 *   main thread (the UI does), which makes the check-then-set atomic.
 * - **A vanished tag** (deleted after the form loaded) ends in [TagEditUiState.notFound] with nothing
 *   logged: a stale screen, not an error.
 * - **An unexpected failure** saves nothing, resets [TagEditUiState.saving], sets
 *   [TagEditUiState.saveFailed] (a fixed message, never exception text), keeps the typed text and logs one
 *   enum-only `entity_save_failed` event. Retrying is safe. A cancellation is rethrown, never reported.
 * - **Reading** the tag is not caught: a corrupt row fails loudly, as on the list.
 *
 * @param tags the tags service.
 * @param tagId the id of the tag to edit as it arrived (untrusted text), or null to create.
 */
class TagEditViewModel(
    private val tags: TagService,
    tagId: String?,
) : ViewModel() {
    private val mutableState =
        MutableStateFlow(TagEditUiState(editing = tagId != null, loaded = tagId == null))

    /** The screen's state. */
    val state: StateFlow<TagEditUiState> = mutableState.asStateFlow()

    /** The tag being edited, once read; null when creating. Main thread only. */
    private var original: Tag? = null

    init {
        if (tagId != null) {
            viewModelScope.launch { load(tagId) }
        }
    }

    /** The name field was edited to [text]; its error, and any save-failed message, clears. */
    fun onNameChange(text: String) = mutableState.update { it.copy(name = it.name.edit(text), saveFailed = false) }

    /**
     * Save was tapped: validates the name and, if it is valid, saves. The error appears on the name
     * field; nothing is saved then. Ignored while saving, once saved, before an edit has loaded, and when
     * the tag is not found.
     */
    fun submit() {
        val current = mutableState.value
        if (current.busy || current.closed) return
        val name = current.name.check { Validation.requireName(it, LedgerLimits.MAX_NAME_LENGTH) }
        val validName = name.value
        if (validName == null) {
            mutableState.value = current.copy(name = name.state, saveFailed = false)
            return
        }
        mutableState.value = current.copy(name = name.state, saving = true, saveFailed = false)
        val target = original
        viewModelScope.launchSave(EntityKind.TAG, ::finished, ::showRefusal) {
            if (target == null) tags.create(validName) else tags.edit(target.meta.id, validName)
        }
    }

    /** Reads the tag asked for and prefills the form, or ends in not found. */
    private suspend fun load(rawId: String) {
        val lists = tags.lists()
        // Compared as text, never turned into an id first: the argument is untrusted.
        val tag = (lists.active + lists.archived).firstOrNull { it.meta.id.value == rawId }
        if (tag == null) {
            mutableState.update { it.copy(loaded = true, notFound = true) }
            return
        }
        original = tag
        mutableState.update { it.copy(loaded = true, name = FieldState(tag.name)) }
    }

    /** The save ended as [outcome] (see [launchSave]); the failure has been logged by now. */
    private fun finished(outcome: SaveOutcome) =
        mutableState.update {
            when (outcome) {
                SaveOutcome.SAVED -> it.copy(saving = false, saved = true)
                // The tag vanished since the form loaded: a stale screen, not an error.
                SaveOutcome.VANISHED -> it.copy(saving = false, notFound = true)
                SaveOutcome.FAILED -> it.copy(saving = false, saveFailed = true)
            }
        }

    /**
     * A rule about the name (a duplicate, say) is shown under the name field instead of as a failure.
     * Returns false for any other rule: the form already checked those, so they cannot happen, and are an
     * unexpected failure.
     */
    private fun showRefusal(rule: LedgerError): Boolean {
        val error = rule.toFieldError() ?: return false
        mutableState.update { it.copy(name = it.name.copy(error = error), saving = false) }
        return true
    }

    companion object {
        /**
         * A factory that builds the ViewModel over [services] (there is no DI framework).
         *
         * @param tagId the tag to edit as it arrived (untrusted), or null to create.
         */
        fun factory(
            services: AppServices,
            tagId: String?,
        ): ViewModelProvider.Factory = viewModelFactory { initializer { TagEditViewModel(services.tags, tagId) } }
    }
}

/** The form cannot take a Save right now: a save is running, or an edit has not loaded yet. */
private val TagEditUiState.busy: Boolean get() = saving || !loaded

/** The form's work is over: it saved, or the tag is gone. */
private val TagEditUiState.closed: Boolean get() = saved || notFound
