package dev.imagh.pft.app.di

import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.data.RoomTransactionRunner
import dev.imagh.pft.app.data.audit.RoomAuditLog
import dev.imagh.pft.app.data.identity.RoomIdentityRepository
import dev.imagh.pft.app.data.settings.RoomAppSettingsRepository
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.AuditRecorder
import dev.imagh.pft.ledger.audit.AuditedWriter
import dev.imagh.pft.ledger.identity.IdentityService
import dev.imagh.pft.ledger.ids.Uuid7
import dev.imagh.pft.ledger.settings.SettingsService
import java.time.Clock

/**
 * The production object graph: the core services wired over an **open** [PftDatabase]. It is what
 * [AppContainer] hands out once the database has opened, and what the test kit (`RoomTestKit`)
 * builds too, so tests exercise the production wiring rather than a copy of it.
 *
 * It is a class of its own, apart from [AppContainer], because the services cannot exist until the
 * database is open: the container owns *when*, this owns *what*. Constructing it opens nothing and
 * does no I/O.
 *
 * @param database an open database.
 * @param clock the clock core sees. The app passes `EpochMillis.clock(...)`, so every instant core
 *   produces is a whole millisecond and storable (the A4a criterion); a sub-millisecond clock makes
 *   the first write fail loudly.
 * @param auditLog where audit entries go; the Room audit trail of [database] by default. A test passes
 *   a wrapper that fails, to prove a failed audit write rolls the change back.
 */
class AppServices(
    val database: PftDatabase,
    clock: Clock,
    auditLog: AuditLog = RoomAuditLog(database.auditDao()),
) {
    /** Id generator on the clock. */
    val ids = Uuid7(clock)

    /** The Room transaction runner. */
    val runner = RoomTransactionRunner(database)

    /** Users and devices. */
    val identityRepository = RoomIdentityRepository(database.identityDao())

    /** App settings. */
    val settingsRepository = RoomAppSettingsRepository(database.appSettingDao())

    /** Builds audit entries into the audit log. */
    val recorder = AuditRecorder(auditLog, ids, clock)

    /** First-run setup and current identity. */
    val identity = IdentityService(runner, identityRepository, settingsRepository, recorder, ids, clock)

    /** Audited runtime settings. */
    val settings = SettingsService(runner, settingsRepository, identity, recorder, clock)

    /** The audited write path every service uses. */
    val writer = AuditedWriter(runner, identity, recorder, clock)
}
