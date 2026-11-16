package dev.imagh.pft.app.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.app.log.EntityAction
import dev.imagh.pft.app.log.EntityKind
import dev.imagh.pft.app.log.LogEvent
import dev.imagh.pft.app.log.SafeLog
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.category.CategoryService
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.validation.LedgerError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** A one-off message the screen shows in a snackbar and then clears with [CategoriesViewModel.noticeShown]. */
sealed interface CategoriesNotice {
    /** Delete was refused because the category still has sub-categories ("Delete its sub-categories first"). */
    data object DeleteBlocked : CategoriesNotice

    /** [action] failed unexpectedly ("Couldn't ... Try again."); the list is as it was. */
    data class Failed(
        val action: EntityAction,
    ) : CategoriesNotice
}

/**
 * Everything the categories list shows.
 *
 * @property kind the kind on show: Expense first, or Income.
 * @property active the rows in use for [kind], depth-first: each top-level category then its children.
 * @property archived archived categories, and the children of archived parents, depth-first.
 * @property loaded false until the first read for [kind] finishes, so the screen does not flash
 *   "No categories yet" or the other kind's rows.
 * @property undo the category whose delete can still be undone, or null. Only the latest delete is
 *   offered. Never shown or logged: it only identifies the offer.
 * @property notice a message not shown yet, or null.
 */
data class CategoriesUiState(
    val kind: CategoryKind = CategoryKind.EXPENSE,
    val active: List<CategoryItem> = emptyList(),
    val archived: List<CategoryItem> = emptyList(),
    val loaded: Boolean = false,
    val undo: CategoryId? = null,
    val notice: CategoriesNotice? = null,
)

/**
 * The categories list's state and actions (plan D2, D7, D8, D11): the Expense/Income switch, Move up
 * and Move down, archive, unarchive, delete and its undo.
 *
 * - **Read on demand (D2).** The lists are read from [CategoryService.lists] on construction, after
 *   every action, when the kind changes and whenever the screen asks ([reload], when it resumes).
 *   A read that throws (a corrupt row, which core refuses) is not caught: it fails loudly.
 * - **One at a time.** Actions and reads run one after another (a [Mutex]), so a read that started
 *   before an action never overwrites the list with older data.
 * - **Moving.** A move re-reads the lists, takes the neighbour among the *active* siblings, swaps the
 *   two in the full sibling order (archived siblings stay where they were) and hands that exact order
 *   to [CategoryService.reorder]. Core breaks ties in `sort_order`, so a move always shows. A move of
 *   a category that is no longer listed, or already first or last, does nothing.
 * - **Failures.** Deleting a parent that still has sub-categories ([LedgerError.HasChildren]) is an
 *   answer, not a failure: it sets [CategoriesNotice.DeleteBlocked], writes nothing and logs nothing.
 *   Any other exception (including a [LedgerError] such as a category deleted elsewhere) leaves the
 *   data as it was (every action is one audited transaction), sets [CategoriesNotice.Failed] with a
 *   fixed message, logs one enum-only `entity_action_failed` event (kind and action, never text) and
 *   refreshes the list. A cancellation is rethrown.
 * - **Undo.** A successful delete offers [CategoriesUiState.undo]; [undo] restores that category (an
 *   audited RESTORE) and clears the offer, as does [dismissUndo].
 *
 * @param categories the categories service (audited writes and the lists).
 */
class CategoriesViewModel(
    private val categories: CategoryService,
) : ViewModel() {
    private val mutableState = MutableStateFlow(CategoriesUiState())
    private val serial = Mutex()

    /** The screen's state. */
    val state: StateFlow<CategoriesUiState> = mutableState.asStateFlow()

    init {
        reload()
    }

    /** Reads the lists again (the screen calls this when it resumes, after an edit elsewhere). */
    fun reload() {
        viewModelScope.launch { serial.withLock { refresh() } }
    }

    /** Shows [kind]'s categories; nothing is listed, and nothing is "empty", until they are read. */
    fun selectKind(kind: CategoryKind) {
        viewModelScope.launch {
            serial.withLock {
                mutableState.update {
                    it.copy(
                        kind = kind,
                        active = emptyList(),
                        archived = emptyList(),
                        loaded = false,
                    )
                }
                refresh()
            }
        }
    }

    /** Does what the row menu entry [action] says to category [id]. */
    fun choose(
        id: CategoryId,
        action: CategoryAction,
    ) = when (action) {
        CategoryAction.MOVE_UP -> perform(EntityAction.REORDER) { move(id, -1) }
        CategoryAction.MOVE_DOWN -> perform(EntityAction.REORDER) { move(id, 1) }
        CategoryAction.ARCHIVE -> perform(EntityAction.ARCHIVE) { categories.archive(id) }
        CategoryAction.UNARCHIVE -> perform(EntityAction.UNARCHIVE) { categories.unarchive(id) }
        CategoryAction.DELETE -> perform(EntityAction.DELETE, offerUndoFor = id) { categories.delete(id) }
    }

    /** Restores the category whose delete is on offer; does nothing without an offer. */
    fun undo() {
        val id = mutableState.value.undo ?: return
        perform(EntityAction.RESTORE, consumesOffer = true) { categories.restore(id) }
    }

    /** The offer ended without Undo (timeout, swipe, or the screen went away): the delete stands. */
    fun dismissUndo() = mutableState.update { it.copy(undo = null) }

    /** The screen showed the [CategoriesUiState.notice]. */
    fun noticeShown() = mutableState.update { it.copy(notice = null) }

    /**
     * Reads the lists for the kind on show and publishes them in one update together with the rest of
     * what an action changed, so a screen never sees a message before the list it is about.
     *
     * @param offerUndoFor the delete to offer an undo of; null keeps whatever offer there is.
     * @param withdrawOffer true to end the current offer (an undo was just used or failed).
     * @param notice a message to show, or null for none.
     */
    private suspend fun refresh(
        offerUndoFor: CategoryId? = null,
        withdrawOffer: Boolean = false,
        notice: CategoriesNotice? = null,
    ) {
        val (active, archived) = categories.lists(mutableState.value.kind).toItems()
        mutableState.update {
            it.copy(
                active = active,
                archived = archived,
                loaded = true,
                undo = if (withdrawOffer) null else offerUndoFor ?: it.undo,
                notice = notice ?: it.notice,
            )
        }
    }

    /** Swaps [id] with its neighbour [step] places away among the active siblings (-1 up, 1 down). */
    private suspend fun move(
        id: CategoryId,
        step: Int,
    ) {
        val kind = mutableState.value.kind
        val lists = categories.lists(kind)
        val category = lists.active.firstOrNull { it.meta.id == id } ?: return
        val visible = lists.active.filter { it.parentId == category.parentId }
        val neighbour = visible.getOrNull(visible.indexOf(category) + step) ?: return
        val order = lists.siblingsOf(category.parentId).map { it.meta.id }.toMutableList()
        val from = order.indexOf(id)
        val to = order.indexOf(neighbour.meta.id)
        order[from] = neighbour.meta.id
        order[to] = id
        categories.reorder(category.parentId, kind, order)
    }

    private fun perform(
        action: EntityAction,
        offerUndoFor: CategoryId? = null,
        consumesOffer: Boolean = false,
        write: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            serial.withLock {
                var offer: CategoryId? = null
                var notice: CategoriesNotice? = null
                try {
                    write()
                    offer = offerUndoFor
                } catch (_: LedgerError.HasChildren) {
                    notice = CategoriesNotice.DeleteBlocked
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    // Deliberately broad: whatever went wrong, the user can retry, and the write is one
                    // transaction so nothing partial exists. The exception is dropped on purpose: logs and
                    // screens must not carry its text (SEC-5).
                    SafeLog.event(LogEvent.EntityActionFailed(EntityKind.CATEGORY, action))
                    notice = CategoriesNotice.Failed(action)
                }
                refresh(offer, withdrawOffer = consumesOffer, notice = notice)
            }
        }
    }

    companion object {
        /** A factory that builds the ViewModel over [services] (there is no DI framework). */
        fun factory(services: AppServices): ViewModelProvider.Factory =
            viewModelFactory { initializer { CategoriesViewModel(services.categories) } }
    }
}
