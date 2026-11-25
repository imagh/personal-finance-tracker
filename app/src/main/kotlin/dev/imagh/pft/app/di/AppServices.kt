package dev.imagh.pft.app.di

import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.data.RoomTransactionRunner
import dev.imagh.pft.app.data.account.RoomAccountRepository
import dev.imagh.pft.app.data.audit.RoomAuditLog
import dev.imagh.pft.app.data.category.RoomCategoryRepository
import dev.imagh.pft.app.data.identity.RoomIdentityRepository
import dev.imagh.pft.app.data.settings.RoomAppSettingsRepository
import dev.imagh.pft.app.data.tag.RoomTagRepository
import dev.imagh.pft.app.data.transaction.RoomTransactionLineRepository
import dev.imagh.pft.app.data.transaction.RoomTransactionRepository
import dev.imagh.pft.ledger.account.AccountService
import dev.imagh.pft.ledger.audit.AuditLog
import dev.imagh.pft.ledger.audit.AuditRecorder
import dev.imagh.pft.ledger.audit.AuditedWriter
import dev.imagh.pft.ledger.category.CategoryService
import dev.imagh.pft.ledger.identity.IdentityService
import dev.imagh.pft.ledger.ids.Uuid7
import dev.imagh.pft.ledger.settings.SettingsService
import dev.imagh.pft.ledger.setup.FirstRunService
import dev.imagh.pft.ledger.tag.TagService
import dev.imagh.pft.ledger.transaction.TransactionLines
import dev.imagh.pft.ledger.transaction.TransactionService
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

    /** Accounts. */
    val accountRepository = RoomAccountRepository(database.accountDao())

    /** Categories. */
    val categoryRepository = RoomCategoryRepository(database.categoryDao())

    /** Tags. */
    val tagRepository = RoomTagRepository(database.tagDao())

    /** Transactions (payment facts). */
    val transactionRepository = RoomTransactionRepository(database.transactionDao())

    /** Transaction lines (items) with their tags, written in one Room transaction each. */
    val transactionLineRepository = RoomTransactionLineRepository(database.transactionLineDao(), runner)

    /** Builds audit entries into the audit log. */
    val recorder = AuditRecorder(auditLog, ids, clock)

    /** First-run setup and current identity. */
    val identity = IdentityService(runner, identityRepository, settingsRepository, recorder, ids, clock)

    /** Audited runtime settings. */
    val settings = SettingsService(runner, settingsRepository, identity, recorder, clock)

    /** The audited write path every service uses. */
    val writer = AuditedWriter(runner, identity, recorder, clock)

    /** Accounts: create, edit, archive, delete, restore and the lists. */
    val accounts = AccountService(runner, writer, accountRepository, transactionRepository, ids)

    /** Categories: create, edit, archive, delete, restore, reorder, the pickable list and the default seeding. */
    val categories = CategoryService(runner, writer, categoryRepository, transactionLineRepository, ids)

    /** Tags: create, rename, archive, delete, restore, the lists and the picker list. */
    val tags = TagService(runner, writer, tagRepository, transactionLineRepository, ids)

    /** First-run setup: the identity and the default categories in one transaction. */
    val firstRun = FirstRunService(runner, identity, categories)

    /**
     * Transactions: create, edit, delete, restore, mark verified and read, each with its lines (items).
     * The only writer of transactions and lines that the app's other layers call, and it enforces the sum
     * invariant; the repositories above are wiring, not for the UI.
     */
    val transactions =
        TransactionService(
            runner,
            writer,
            transactionRepository,
            TransactionLines(writer, transactionLineRepository, categoryRepository, tagRepository, ids),
            accountRepository,
            ids,
        )
}
