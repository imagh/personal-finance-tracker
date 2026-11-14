package dev.imagh.pft.app.testing

import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.data.audit.AuditEntryMapper
import dev.imagh.pft.app.data.audit.RoomAuditLog
import dev.imagh.pft.app.di.AppServices
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.fakes.TestClock
import dev.imagh.pft.ledger.identity.Identity
import java.time.Clock

/**
 * The core object graph wired over a real [PftDatabase], the Room twin of core's `LedgerTestKit`
 * (which wires the in-memory fakes). An integration test starts with
 * `RoomTestKit(TestDatabase.create())` and drives the real services through Room.
 *
 * It delegates to [AppServices], the production wiring, so the tests exercise the same object graph
 * the app builds; it only adds the test clock and a few helpers.
 *
 * @param wrapClock wraps the test clock before the services see it, to give them a millisecond clock.
 * @param auditLog where audit entries go; defaults to the real [RoomAuditLog]. Pass a wrapper that
 *   fails to prove a failed audit write rolls the change back.
 */
class RoomTestKit(
    val database: PftDatabase,
    val auditLog: AuditLog = RoomAuditLog(database.auditDao()),
    wrapClock: (Clock) -> Clock = { it },
) {
    /** The test clock; advance or set it to test ordering and precision. */
    val clock = TestClock()

    /** The production wiring, over the clock as given or wrapped by [wrapClock] (for example `EpochMillis.clock`). */
    private val services = AppServices(database, wrapClock(clock), auditLog)

    /** Id generator on the services' clock. */
    val ids = services.ids

    /** The Room transaction runner. */
    val runner = services.runner

    /** Users and devices. */
    val identityRepository = services.identityRepository

    /** App settings. */
    val settingsRepository = services.settingsRepository

    /** Accounts. */
    val accountRepository = services.accountRepository

    /** Categories. */
    val categoryRepository = services.categoryRepository

    /** Builds audit entries into [auditLog]. */
    val recorder = services.recorder

    /** First-run setup and current identity. */
    val identity = services.identity

    /** Audited runtime settings. */
    val settings = services.settings

    /** The audited write path every service uses. */
    val writer = services.writer

    /** Accounts: create, edit, archive, delete, restore and the lists. */
    val accounts = services.accounts

    /** Categories: create, edit, archive, delete, restore, reorder, the pickable list and the default seeding. */
    val categories = services.categories

    /** Performs first-run setup as `Akash` on `Pixel` and returns that identity. */
    suspend fun initialise(): Identity = identity.initialise("Akash", "Pixel")

    /** The audit trail of one record, read back through the DAO and mapper. */
    suspend fun trailOf(
        entityType: String,
        entityId: String,
    ): List<AuditEntry> = database.auditDao().forEntity(entityType, entityId).map(AuditEntryMapper::toModel)
}
