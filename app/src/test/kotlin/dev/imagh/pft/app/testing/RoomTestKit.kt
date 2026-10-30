package dev.imagh.pft.app.testing

import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.data.RoomTransactionRunner
import dev.imagh.pft.app.data.audit.AuditEntryMapper
import dev.imagh.pft.app.data.audit.RoomAuditLog
import dev.imagh.pft.app.data.identity.RoomIdentityRepository
import dev.imagh.pft.app.data.settings.RoomAppSettingsRepository
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.AuditRecorder
import dev.imagh.pft.ledger.audit.AuditedWriter
import dev.imagh.pft.ledger.fakes.TestClock
import dev.imagh.pft.ledger.identity.Identity
import dev.imagh.pft.ledger.identity.IdentityService
import dev.imagh.pft.ledger.ids.Uuid7
import dev.imagh.pft.ledger.settings.SettingsService

/**
 * The core object graph wired over a real [PftDatabase], the Room twin of core's `LedgerTestKit`
 * (which wires the in-memory fakes). An integration test starts with
 * `RoomTestKit(TestDatabase.create())` and drives the real services through Room.
 *
 * @param auditLog where audit entries go; defaults to the real [RoomAuditLog]. Pass a wrapper that
 *   fails to prove a failed audit write rolls the change back.
 */
class RoomTestKit(
    val database: PftDatabase,
    val auditLog: AuditLog = RoomAuditLog(database.auditDao()),
) {
    /** The clock behind every timestamp and id; advance it to test ordering. */
    val clock = TestClock()

    /** Id generator on [clock]. */
    val ids = Uuid7(clock)

    /** The Room transaction runner. */
    val runner = RoomTransactionRunner(database)

    /** Users and devices. */
    val identityRepository = RoomIdentityRepository(database.identityDao())

    /** App settings. */
    val settingsRepository = RoomAppSettingsRepository(database.appSettingDao())

    /** Builds audit entries into [auditLog]. */
    val recorder = AuditRecorder(auditLog, ids, clock)

    /** First-run setup and current identity. */
    val identity = IdentityService(runner, identityRepository, settingsRepository, recorder, ids, clock)

    /** Audited runtime settings. */
    val settings = SettingsService(runner, settingsRepository, identity, recorder, clock)

    /** The audited write path every service uses. */
    val writer = AuditedWriter(runner, identity, recorder, clock)

    /** Performs first-run setup as `Akash` on `Pixel` and returns that identity. */
    suspend fun initialise(): Identity = identity.initialise("Akash", "Pixel")

    /** The audit trail of one record, read back through the DAO and mapper. */
    suspend fun trailOf(
        entityType: String,
        entityId: String,
    ): List<AuditEntry> = database.auditDao().forEntity(entityType, entityId).map(AuditEntryMapper::toModel)
}
