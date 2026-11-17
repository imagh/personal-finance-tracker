package dev.imagh.pft.app.ui.common

import dev.imagh.pft.app.log.EntityKind
import dev.imagh.pft.app.log.SafeLog
import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

/**
 * [launchSave], the save shape every form shares (plan D12): how a write's end is classified (saved, the
 * record vanished, failed), that only a failure is logged and only as an enum-only event, that a ledger
 * rule a form wants to show on a field can be taken out of the classification, and that a cancellation is
 * never swallowed or reported.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LaunchSaveTest {
    private val outcomes = mutableListOf<SaveOutcome>()

    /** Runs [write] through [launchSave] to the end and returns the app's log lines it wrote. */
    private fun run(
        refuses: (LedgerError) -> Boolean = { false },
        write: suspend () -> Unit,
    ): List<String> {
        ShadowLog.clear()
        runBlocking { launchSave(EntityKind.CATEGORY, { outcomes += it }, refuses, write).join() }
        return ShadowLog.getLogs().filter { it.tag == SafeLog.TAG }.map { it.msg }
    }

    @Test
    fun `a write that returns is saved, and nothing is logged`() {
        run { }.shouldBe(emptyList())

        outcomes shouldBe listOf(SaveOutcome.SAVED)
    }

    @Test
    fun `a record that is gone or deleted is vanished, a stale screen that is not logged`() {
        run { throw LedgerError.NotFound("category") } shouldBe emptyList()
        run { throw LedgerError.Deleted("category") } shouldBe emptyList()

        outcomes shouldBe listOf(SaveOutcome.VANISHED, SaveOutcome.VANISHED)
    }

    @Test
    fun `any other failure is failed, and logs one enum-only event without the exception's text`() {
        val lines = run { throw IllegalStateException("secret detail Pets Vet 1234") }

        outcomes shouldBe listOf(SaveOutcome.FAILED)
        lines shouldBe listOf("entity_save_failed kind=CATEGORY")
    }

    @Test
    fun `a ledger rule nobody took out of the classification is a failure like any other`() {
        val lines = run { throw LedgerError.BlankName() }

        outcomes shouldBe listOf(SaveOutcome.FAILED)
        lines shouldBe listOf("entity_save_failed kind=CATEGORY")
    }

    @Test
    fun `a ledger rule the form takes is neither saved, vanished nor failed, and is not logged`() {
        val seen = mutableListOf<LedgerError>()
        val lines =
            run(refuses = {
                seen += it
                it is LedgerError.DuplicateName
            }) { throw LedgerError.DuplicateName("category") }

        outcomes shouldBe emptyList()
        seen.size shouldBe 1
        lines shouldBe emptyList()
    }

    @Test
    fun `a rule the form declines is a failure`() {
        val lines = run(refuses = { false }) { throw LedgerError.DuplicateName("category") }

        outcomes shouldBe listOf(SaveOutcome.FAILED)
        lines shouldBe listOf("entity_save_failed kind=CATEGORY")
    }

    @Test
    fun `a cancellation is rethrown, so the job is cancelled and nothing is reported or logged`() {
        ShadowLog.clear()
        val job =
            runBlocking { launchSave(EntityKind.CATEGORY, { outcomes += it }) { throw CancellationException("stop") } }
        runBlocking { job.join() }

        job.isCancelled shouldBe true
        outcomes shouldBe emptyList()
        ShadowLog.getLogs().filter { it.tag == SafeLog.TAG } shouldBe emptyList()
    }
}
