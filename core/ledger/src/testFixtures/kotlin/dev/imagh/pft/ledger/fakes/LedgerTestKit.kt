package dev.imagh.pft.ledger.fakes

import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.AuditRecorder
import dev.imagh.pft.ledger.audit.AuditedWriter
import dev.imagh.pft.ledger.identity.Identity
import dev.imagh.pft.ledger.identity.IdentityService
import dev.imagh.pft.ledger.ids.Uuid7
import dev.imagh.pft.ledger.settings.SettingsService

/**
 * The core object graph wired over in-memory fakes, so a service test starts with one line
 * (`val kit = LedgerTestKit()`) instead of re-wiring runner, clock, ids and repositories.
 *
 * Later slices construct their service from these parts and add their fake repository with
 * `kit.store.newStore(...)`.
 */
class LedgerTestKit {
    /** Fake persistence and runner. */
    val store = InMemoryLedgerStore()

    /** The clock behind every timestamp and id; advance it to test ordering. */
    val clock = TestClock()

    /** Id generator on [clock]. */
    val ids = Uuid7(clock)

    /** Users and devices. */
    val identityRepository = InMemoryIdentityRepository(store)

    /** App settings. */
    val settingsRepository = InMemoryAppSettingsRepository(store)

    /** Builds audit entries into [InMemoryLedgerStore.auditLog]. */
    val recorder = AuditRecorder(store.auditLog, ids, clock)

    /** First-run setup and current identity. */
    val identity = IdentityService(store, identityRepository, settingsRepository, recorder, ids, clock)

    /** Audited runtime settings. */
    val settings = SettingsService(store, settingsRepository, identity, recorder, clock)

    /** The audited write path every service uses. */
    val writer = AuditedWriter(store, identity, recorder, clock)

    /** An [IdentityService] over the same fakes whose audit entries go to [log] (to inject failures). */
    fun identityServiceWith(log: AuditLog) =
        IdentityService(store, identityRepository, settingsRepository, AuditRecorder(log, ids, clock), ids, clock)

    /** Performs first-run setup as `Akash` on `Pixel` and returns that identity. */
    suspend fun initialise(): Identity = identity.initialise("Akash", "Pixel")

    /** An [AuditedWriter] over the same fakes whose audit entries go to [log] (to inject failures). */
    fun writerWith(log: AuditLog) = AuditedWriter(store, identity, AuditRecorder(log, ids, clock), clock)
}
