package dev.imagh.pft.app.ui.common

import dev.imagh.pft.app.log.EntityKind
import dev.imagh.pft.app.log.LogEvent
import dev.imagh.pft.app.log.SafeLog
import dev.imagh.pft.ledger.validation.LedgerError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Runs a form's save in this scope and tells the form how it ended: the one place the save shape of the
 * entity forms (accounts, categories; plan D12) lives, so each ViewModel states only what is its own.
 *
 * - **Classification.** [write] returning is [SaveOutcome.SAVED]. A [LedgerError.NotFound] or
 *   [LedgerError.Deleted] is [SaveOutcome.VANISHED]. Any other exception, a [LedgerError] included, is
 *   [SaveOutcome.FAILED], the broad catch being deliberate: whatever went wrong (a full disk, a locked
 *   database, a bug), the user can retry, because the write is one audited transaction and a failed one
 *   leaves nothing behind. The exception is dropped on purpose: logs and screens must not carry its text
 *   (SEC-5).
 * - **Logging.** Only [SaveOutcome.FAILED] logs, one enum-only `entity_save_failed` event for [kind]
 *   (never a name, an id or an amount). A vanished record is not an error and logs nothing.
 * - **A rule the form shows itself.** A form that reports a ledger rule on a field (a duplicate name under
 *   the name field) passes [refused]: it is asked first about every [LedgerError] other than the
 *   vanished ones, and if it returns true it has shown the error itself and [onOutcome] is not called and
 *   nothing is logged. Forms with no such rule leave the default, which declines everything.
 * - **Cancellation** is rethrown, never reported: the scope is being cancelled (the ViewModel was
 *   cleared), so there is no screen left to update, and [onOutcome] is not called.
 *
 * Call it on the main thread (the ViewModel's scope); [onOutcome] and [refused] run on that scope's
 * dispatcher, where they may update the form's state.
 *
 * Setup (`SetupViewModel`) keeps its own save: it treats `AlreadyInitialised` as done, logs its own
 * event and has no vanished case, so it would need a parameter for each difference.
 *
 * @param kind which entity the form is for, for the failure event.
 * @param onOutcome receives the outcome, once.
 * @param refused true if the form handled the [LedgerError] itself.
 * @param write the audited write.
 * @return the launched job.
 */
internal fun CoroutineScope.launchSave(
    kind: EntityKind,
    onOutcome: (SaveOutcome) -> Unit,
    refused: (LedgerError) -> Boolean = { false },
    write: suspend () -> Unit,
): Job =
    launch {
        val outcome =
            try {
                write()
                SaveOutcome.SAVED
            } catch (_: LedgerError.NotFound) {
                SaveOutcome.VANISHED
            } catch (_: LedgerError.Deleted) {
                SaveOutcome.VANISHED
            } catch (rule: LedgerError) {
                if (refused(rule)) return@launch
                SaveOutcome.FAILED
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                SaveOutcome.FAILED
            }
        if (outcome == SaveOutcome.FAILED) SafeLog.event(LogEvent.EntitySaveFailed(kind))
        onOutcome(outcome)
    }
