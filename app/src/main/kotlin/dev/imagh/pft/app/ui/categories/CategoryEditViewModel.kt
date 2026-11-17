package dev.imagh.pft.app.ui.categories

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
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.category.CategoryIcon
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.CategoryService
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.ledger.validation.Validation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Everything the category form shows.
 *
 * @property editing true when an existing category is being edited, false when one is being created.
 * @property loaded false until what the form needs has been read (the category being edited, or the
 *   parent choices when creating), so the screen draws no fields and no "not found" before the answer
 *   is known.
 * @property notFound true when the category asked for does not exist, was deleted, or an argument of the
 *   route was not valid (the arguments are untrusted navigation input). The route then leaves the screen.
 * @property kind expense or income. Fixed: chosen by the list's switch when creating, and the category's
 *   own when editing; it is shown, never edited.
 * @property name the category's name, as typed, with its error.
 * @property colour the chosen colour; a new category starts as slate, the neutral one.
 * @property icon the chosen symbol, or null for none (the name's first letter is drawn). When editing, a
 *   category's emoji icon is kept as it is until a symbol is chosen.
 * @property parent the top-level category this one is (or will be) under, or null for a top-level
 *   category. Chosen when creating; shown but not editable when editing.
 * @property parentOptions the live top-level categories of [kind] a new category can go under; empty when
 *   editing (the parent cannot change). In `pickable` order.
 * @property saving true from a valid Save until the save finishes; the screen disables Save.
 * @property saved true once the category is saved; the route then leaves the screen.
 * @property saveFailed true after an unexpected failure to save: the screen shows a fixed "try again"
 *   message. It is cleared by editing anything or by submitting again; the typed text is kept.
 */
data class CategoryEditUiState(
    val editing: Boolean = false,
    val loaded: Boolean = false,
    val notFound: Boolean = false,
    val kind: CategoryKind = CategoryKind.EXPENSE,
    val name: FieldState = FieldState(),
    val colour: CategoryColour = CategoryColour.SLATE,
    val icon: CategoryIcon? = null,
    val parent: Category? = null,
    val parentOptions: List<Category> = emptyList(),
    val saving: Boolean = false,
    val saved: Boolean = false,
    val saveFailed: Boolean = false,
)

/**
 * The route arguments of the category form exactly as they arrived: **untrusted text** (ids and an enum
 * name), compared against stored ids and enum names and never trusted to be one.
 *
 * @property categoryId the category to edit, or null to create.
 * @property parentId when creating, the parent of a new sub-category, or null for a top-level category.
 * @property kindName when creating, the new category's kind as an enum name, or null for Expense.
 */
data class CategoryEditRequest(
    val categoryId: String? = null,
    val parentId: String? = null,
    val kindName: String? = null,
)

/**
 * Creates or edits one category (plan D5, D7, D12, D13, Q5, Q7). Its save shape is [launchSave], shared
 * with the account form, so the forms behave alike; the one addition is that a rule about the name (a
 * duplicate) is shown under the name field, which `launchSave` hands to [showRefusal] first.
 *
 * - **Validation is core's.** The name goes through `FieldState.check` with `Validation.requireName`;
 *   a name another live sibling already has (ignoring case) is refused by [CategoryService] inside the
 *   write transaction and shown under the name field ([dev.imagh.pft.app.ui.common.FieldError.Duplicate]).
 * - **Create.** The route's `kind` (an enum name; none means Expense) and optional `parent` (an id) are
 *   untrusted text: an unknown kind, or a parent that is not one of the live top-level categories of that
 *   kind that [CategoryService.pickable] offers, ends in [CategoryEditUiState.notFound]. Those categories
 *   are also [CategoryEditUiState.parentOptions], the list the form's parent picker shows; [onParentChange]
 *   takes one of them or null (top level). A sub-category has its parent's kind, which is the form's.
 * - **Edit.** The category is found by comparing the route's `id` as text with every live category's id
 *   (never turned into an id first) in both kinds, archived ones included, so an archived category can be
 *   edited. Name, colour and symbol are editable. **Kind and parent are fixed once created** (plan D7,
 *   owner Q7): [onParentChange] does nothing when editing, and the draft carries neither. A category with
 *   an emoji icon keeps it unless a symbol is chosen (the emoji tab arrives in B4c). An edit that changes
 *   nothing writes no audit entry (the core service sees identical values).
 * - **One save.** [submit] sets [CategoryEditUiState.saving] before it launches the save and ignores
 *   calls while saving, once saved, before the form has loaded, or after not-found. It must be called on
 *   the main thread (the UI does), which makes the check-then-set atomic.
 * - **A vanished category** (deleted after the form loaded) ends in [CategoryEditUiState.notFound] with
 *   nothing logged: a stale screen, not an error.
 * - **An unexpected failure** (anything else, not a cancellation) is caught: saving is reset,
 *   [CategoryEditUiState.saveFailed] is set (a fixed message, never exception text), the typed text is
 *   kept, and one enum-only `entity_save_failed` event is logged. Retrying is safe: the write is one
 *   audited transaction. A cancellation is rethrown, never reported.
 * - **Reading** is not caught: a corrupt row fails loudly, as on the list.
 *
 * @param categories the categories service.
 * @param categoryId the id of the category to edit as it arrived (untrusted text), or null to create.
 * @param parentId when creating, the id of the parent as it arrived (untrusted text), or null.
 * @param kindName when creating, the kind's enum name as it arrived (untrusted text), or null for Expense.
 */
class CategoryEditViewModel(
    private val categories: CategoryService,
    categoryId: String?,
    parentId: String?,
    kindName: String?,
) : ViewModel() {
    private val mutableState = MutableStateFlow(CategoryEditUiState(editing = categoryId != null))

    /** The screen's state. */
    val state: StateFlow<CategoryEditUiState> = mutableState.asStateFlow()

    /** The category being edited, once read; null when creating. Main thread only. */
    private var original: Category? = null

    init {
        viewModelScope.launch {
            if (categoryId != null) loadForEdit(categoryId) else loadForCreate(parentId, kindName)
        }
    }

    /** The name field was edited to [text]; its error, and any save-failed message, clears. */
    fun onNameChange(text: String) = mutableState.update { it.copy(name = it.name.edit(text), saveFailed = false) }

    /** A colour was chosen; any save-failed message clears. */
    fun onColourChange(colour: CategoryColour) = mutableState.update { it.copy(colour = colour, saveFailed = false) }

    /** A symbol was chosen, or null for none; any save-failed message clears. */
    fun onIconChange(icon: CategoryIcon?) = mutableState.update { it.copy(icon = icon, saveFailed = false) }

    /**
     * A parent was chosen, or null for top level. Only one of [CategoryEditUiState.parentOptions] is
     * accepted (anything else is ignored), and nothing changes when editing: the parent is fixed once
     * the category exists.
     */
    fun onParentChange(id: CategoryId?) =
        mutableState.update { form ->
            val chosen = form.parentOptions.firstOrNull { it.meta.id == id }
            when {
                form.editing -> form
                id == null -> form.copy(parent = null, saveFailed = false)
                chosen != null -> form.copy(parent = chosen, saveFailed = false)
                else -> form
            }
        }

    /**
     * Save was tapped: validates the name and, if it is valid, saves. The error appears on the name
     * field; nothing is saved then. Ignored while saving, once saved, before the form has loaded, and
     * when the category is not found.
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
        val draft = CategoryDraft(validName, current.colour, current.icon)
        mutableState.value = current.copy(name = name.state, saving = true, saveFailed = false)
        save(draft, current.parent?.meta?.id, current.kind)
    }

    /** Reads the category asked for and prefills the form, or ends in not found. */
    private suspend fun loadForEdit(rawId: String) {
        val everything =
            CategoryKind.entries.flatMap { kind -> categories.lists(kind).let { it.active + it.archived } }
        // Compared as text, never turned into an id first: the argument is untrusted.
        val category = everything.firstOrNull { it.meta.id.value == rawId }
        if (category == null) {
            mutableState.update { it.copy(loaded = true, notFound = true) }
            return
        }
        original = category
        mutableState.update {
            it.copy(
                loaded = true,
                kind = category.kind,
                name = FieldState(category.name),
                colour = category.colour,
                icon = category.iconKey,
                parent = category.parentId?.let { id -> everything.firstOrNull { c -> c.meta.id == id } },
            )
        }
    }

    /** Reads the parent choices for the kind asked for and applies the parent asked for, or ends in not found. */
    private suspend fun loadForCreate(
        rawParent: String?,
        rawKind: String?,
    ) {
        val kind = kindOf(rawKind)
        val options = kind?.let { categories.pickable(it) }.orEmpty().filter { it.parentId == null }
        val parent = rawParent?.let { raw -> options.firstOrNull { it.meta.id.value == raw } }
        val valid = kind != null && (rawParent == null || parent != null)
        mutableState.update {
            it.copy(
                loaded = true,
                notFound = !valid,
                kind = kind ?: it.kind,
                parentOptions = options,
                parent = parent,
            )
        }
    }

    private fun save(
        draft: CategoryDraft,
        parentId: CategoryId?,
        kind: CategoryKind,
    ) = viewModelScope.launchSave(EntityKind.CATEGORY, ::finished, ::showRefusal) {
        val target = original
        if (target == null) categories.create(parentId, kind, draft) else categories.edit(target.meta.id, draft)
    }

    /** The save ended as [outcome] (see [launchSave]); the failure has been logged by now. */
    private fun finished(outcome: SaveOutcome) =
        mutableState.update {
            when (outcome) {
                SaveOutcome.SAVED -> it.copy(saving = false, saved = true)
                // The category vanished since the form loaded: a stale screen, not an error.
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
         * @param request the route arguments as they arrived (untrusted).
         */
        fun factory(
            services: AppServices,
            request: CategoryEditRequest,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    CategoryEditViewModel(services.categories, request.categoryId, request.parentId, request.kindName)
                }
            }
    }
}

/** The form cannot take a Save right now: a save is running, or nothing has loaded yet. */
private val CategoryEditUiState.busy: Boolean get() = saving || !loaded

/** The form's work is over: it saved, or the category is gone. */
private val CategoryEditUiState.closed: Boolean get() = saved || notFound

/** The kind the route's untrusted `kind` argument names: Expense when it is absent, null when it names no kind. */
private fun kindOf(raw: String?): CategoryKind? =
    if (raw == null) CategoryKind.EXPENSE else CategoryKind.entries.firstOrNull { it.name == raw }
