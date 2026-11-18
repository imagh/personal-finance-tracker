package dev.imagh.pft.app.ui.tags

import android.os.Looper
import dev.imagh.pft.app.data.audit.RoomAuditLog
import dev.imagh.pft.app.log.SafeLog
import dev.imagh.pft.app.testing.RoomTestKit
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.awaitMain
import dev.imagh.pft.app.ui.common.FieldError
import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.tag.Tag
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

/**
 * [TagEditViewModel] over real in-memory Room through the production wiring ([RoomTestKit]): what it
 * validates and shows under the name field (a duplicate included), what it saves and audits, how edit mode
 * prefills, how untrusted navigation arguments are handled, and what a failure does. The ViewModel runs on
 * the main dispatcher, so tests wait with [awaitMain]. A cancelled save is pinned by `LaunchSaveTest`.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TagEditViewModelTest {
    private val database = TestDatabase.create()
    private val audit = FailsOnDemand(RoomAuditLog(database.auditDao()))
    private val kit = RoomTestKit(database, auditLog = audit)

    @After
    fun closeDatabase() = database.close()

    private fun <T> blocking(block: suspend () -> T): T = runBlocking { block() }

    /** The real audit log until a test sets [failure]; then every append throws it, so a write rolls back. */
    private class FailsOnDemand(
        private val real: AuditLog,
    ) : AuditLog {
        var failure: Throwable? = null
        var failedAppends = 0
            private set

        override suspend fun append(entry: AuditEntry) {
            failure?.let {
                failedAppends++
                throw it
            }
            real.append(entry)
        }
    }

    private fun auditRowCount(): Long =
        database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM audit_entry").use { cursor ->
            cursor.moveToFirst()
            cursor.getLong(0)
        }

    private fun ensureIdentity() {
        blocking { if (kit.identity.currentIdentity() == null) kit.initialise() }
    }

    private fun seed(name: String): Tag {
        ensureIdentity()
        return blocking { kit.tags.create(name) }
    }

    private fun stored(): List<Tag> = blocking { kit.tags.lists() }.let { it.active + it.archived }

    private fun trail(id: TagId) = blocking { kit.trailOf("tag", id.value) }.map { it.action }

    private fun logged() = ShadowLog.getLogs().filter { it.tag == SafeLog.TAG }.map { it.msg }

    private fun createForm(): TagEditViewModel {
        ensureIdentity()
        return TagEditViewModel(kit.tags, null)
    }

    private fun editForm(id: String): TagEditViewModel {
        ensureIdentity()
        val vm = TagEditViewModel(kit.tags, id)
        awaitMain { vm.state.value.loaded }
        return vm
    }

    private fun TagEditViewModel.saveAndWait() {
        submit()
        awaitMain { state.value.saved }
    }

    // --- Create

    @Test
    fun `a new form is loaded and empty`() {
        val state = createForm().state.value

        state.editing shouldBe false
        state.loaded shouldBe true
        state.notFound shouldBe false
        state.name.text shouldBe ""
        state.saving shouldBe false
        state.saved shouldBe false
        state.saveFailed shouldBe false
    }

    @Test
    fun `a valid create saves the trimmed name and audits a CREATE`() {
        val vm = createForm()
        vm.onNameChange("  Travel ")

        vm.saveAndWait()

        val tag = stored().single()
        tag.name shouldBe "Travel"
        trail(tag.meta.id) shouldBe listOf(AuditAction.CREATE)
        vm.state.value.saving shouldBe false
        logged() shouldBe emptyList()
    }

    @Test
    fun `a blank or over-long name shows its error under the name field and saves nothing`() {
        val vm = createForm()
        vm.onNameChange("   ")
        vm.submit()
        vm.state.value.name.error shouldBe FieldError.Required
        vm.state.value.name.text shouldBe "   "

        vm.onNameChange("n".repeat(101))
        vm.submit()

        vm.state.value.name.error shouldBe FieldError.TooLong(100)
        vm.state.value.saved shouldBe false
        vm.state.value.saving shouldBe false
        stored() shouldBe emptyList()
        auditRowCount() shouldBe 3 // the setup identity only
    }

    @Test
    fun `a duplicate name, ignoring case and spaces, shows Duplicate under the name field`() {
        seed("Travel")
        val vm = createForm()
        vm.onNameChange(" TRAVEL ")
        ShadowLog.clear()

        vm.submit()
        awaitMain { !vm.state.value.saving }

        vm.state.value.name.error shouldBe FieldError.Duplicate
        vm.state.value.name.text shouldBe " TRAVEL "
        vm.state.value.saved shouldBe false
        vm.state.value.saveFailed shouldBe false
        stored().size shouldBe 1
        logged() shouldBe emptyList()
    }

    @Test
    fun `an archived tag still blocks its name, and typing clears the Duplicate error`() {
        val travel = seed("Travel")
        blocking { kit.tags.archive(travel.meta.id) }
        val vm = createForm()
        vm.onNameChange("travel")
        vm.submit()
        awaitMain { vm.state.value.name.error != null }
        vm.state.value.name.error shouldBe FieldError.Duplicate

        vm.onNameChange("Travel 2")

        vm.state.value.name.error shouldBe null
    }

    @Test
    fun `the name of a deleted tag can be used again`() {
        val travel = seed("Travel")
        blocking { kit.tags.delete(travel.meta.id) }
        val vm = createForm()
        vm.onNameChange("Travel")

        vm.saveAndWait()

        stored().single().name shouldBe "Travel"
    }

    // --- One save

    @Test
    fun `a second submit while saving is ignored, so one tag and one audit entry are written`() {
        val vm = createForm()
        vm.onNameChange("Travel")

        vm.submit()
        vm.state.value.saving shouldBe true
        // The name is now invalid: without the guard this second submit would validate it and show Required.
        vm.onNameChange("")
        vm.submit()
        awaitMain { vm.state.value.saved }

        vm.state.value.name.error shouldBe null
        stored().single().name shouldBe "Travel"
        auditRowCount() shouldBe 4 // the identity's three and the one CREATE
    }

    @Test
    fun `submitting again once saved changes nothing`() {
        val vm = createForm()
        vm.onNameChange("Travel")
        vm.saveAndWait()

        // A different name: an unguarded second save would write a second tag, not be refused as a duplicate.
        vm.onNameChange("Trips")
        vm.submit()
        repeat(5) { shadowOf(Looper.getMainLooper()).idle() }

        stored().single().name shouldBe "Travel"
        auditRowCount() shouldBe 4
    }

    // --- Edit mode

    @Test
    fun `edit mode prefills the name, for an archived tag too`() {
        val travel = seed("Travel")
        val trips = seed("Trips")
        blocking { kit.tags.archive(trips.meta.id) }

        val live = editForm(travel.meta.id.value).state.value
        val archived = editForm(trips.meta.id.value).state.value

        live.editing shouldBe true
        live.notFound shouldBe false
        live.name.text shouldBe "Travel"
        archived.name.text shouldBe "Trips"
    }

    @Test
    fun `a rename changes the name and audits one UPDATE`() {
        val travel = seed("Travel")
        val vm = editForm(travel.meta.id.value)
        vm.onNameChange("Trips")

        vm.saveAndWait()

        stored().single().name shouldBe "Trips"
        trail(travel.meta.id) shouldBe listOf(AuditAction.CREATE, AuditAction.UPDATE)
    }

    @Test
    fun `a rename that changes nothing writes no audit entry`() {
        val travel = seed("Travel")
        val vm = editForm(travel.meta.id.value)

        vm.saveAndWait()

        trail(travel.meta.id) shouldBe listOf(AuditAction.CREATE)
    }

    @Test
    fun `a case-only rename of its own name is allowed`() {
        val travel = seed("Travel")
        val vm = editForm(travel.meta.id.value)
        vm.onNameChange("TRAVEL")

        vm.saveAndWait()

        stored().single().name shouldBe "TRAVEL"
    }

    @Test
    fun `renaming into another tag's name shows Duplicate and leaves the stored tag as it was`() {
        val travel = seed("Travel")
        seed("Trips")
        val vm = editForm(travel.meta.id.value)
        vm.onNameChange("trips")

        vm.submit()
        awaitMain { vm.state.value.name.error != null }

        vm.state.value.name.error shouldBe FieldError.Duplicate
        vm.state.value.saved shouldBe false
        vm.state.value.saving shouldBe false
        stored().map { it.name }.toSet() shouldBe setOf("Travel", "Trips")
        trail(travel.meta.id) shouldBe listOf(AuditAction.CREATE)
    }

    @Test
    fun `an id that does not exist, is deleted, or is not an id ends in not found and never crashes`() {
        val deleted = seed("Gone")
        blocking { kit.tags.delete(deleted.meta.id) }
        listOf(
            "00000000-0000-7000-8000-000000000000",
            deleted.meta.id.value,
            "",
            "   ",
            "../../etc/passwd",
            "x'); DROP TABLE tag; --",
            "a".repeat(10_000),
        ).forEach { raw ->
            val vm = editForm(raw)

            vm.state.value.notFound shouldBe true
            vm.state.value.editing shouldBe true
            vm.state.value.name.text shouldBe ""
            vm.submit()
            vm.state.value.saved shouldBe false
        }
        stored() shouldBe emptyList()
    }

    @Test
    fun `before the load finishes an edit form is neither loaded nor not found, and Save is ignored`() {
        val travel = seed("Travel")
        val vm = TagEditViewModel(kit.tags, travel.meta.id.value)

        vm.state.value.loaded shouldBe false
        vm.state.value.notFound shouldBe false
        vm.state.value.editing shouldBe true
        vm.submit()
        vm.state.value.name.error shouldBe null
        awaitMain { vm.state.value.loaded }
        vm.state.value.saved shouldBe false
        auditRowCount() shouldBe 4
    }

    @Test
    fun `a tag deleted after the form loaded ends in not found on save, with nothing logged`() {
        val travel = seed("Travel")
        val vm = editForm(travel.meta.id.value)
        blocking { kit.tags.delete(travel.meta.id) }
        vm.onNameChange("Renamed")
        ShadowLog.clear()

        vm.submit()
        awaitMain { vm.state.value.notFound }

        vm.state.value.saved shouldBe false
        vm.state.value.saving shouldBe false
        vm.state.value.saveFailed shouldBe false
        logged() shouldBe emptyList()
        trail(travel.meta.id) shouldBe listOf(AuditAction.CREATE, AuditAction.DELETE)
    }

    // --- An unexpected failure (the A5 save shape)

    @Test
    fun `a failing audit keeps the text, resets saving, sets saveFailed and logs only the enum`() {
        val vm = createForm()
        vm.onNameChange("  Travel ")
        audit.failure = IllegalStateException("secret detail Travel")
        ShadowLog.clear()

        vm.submit()
        awaitMain { vm.state.value.saveFailed }

        val state = vm.state.value
        audit.failedAppends shouldBe 1
        state.saving shouldBe false
        state.saved shouldBe false
        state.name.text shouldBe "  Travel "
        state.name.error shouldBe null
        stored() shouldBe emptyList()
        logged() shouldBe listOf("entity_save_failed kind=TAG")
    }

    @Test
    fun `after a failure the user can just retry, which saves once, and editing clears the message`() {
        val vm = createForm()
        vm.onNameChange("Travel")
        audit.failure = IllegalStateException("disk full")
        vm.submit()
        awaitMain { vm.state.value.saveFailed }
        vm.onNameChange("Travel ")
        vm.state.value.saveFailed shouldBe false
        vm.submit()
        awaitMain { vm.state.value.saveFailed }
        audit.failure = null

        vm.submit()
        awaitMain { vm.state.value.saved }

        vm.state.value.saveFailed shouldBe false
        stored().single().name shouldBe "Travel"
        auditRowCount() shouldBe 4
    }
}
