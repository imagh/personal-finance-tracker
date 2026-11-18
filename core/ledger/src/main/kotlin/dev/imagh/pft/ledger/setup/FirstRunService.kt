package dev.imagh.pft.ledger.setup

import dev.imagh.pft.ledger.category.CategoryService
import dev.imagh.pft.ledger.identity.Identity
import dev.imagh.pft.ledger.identity.IdentityService
import dev.imagh.pft.ledger.tx.TransactionRunner

/**
 * First-run setup in one step: the identity (user, device, current-device setting) **and** the
 * default categories, in a single transaction, so a failure part-way leaves neither a user without
 * categories nor categories without a user (INT-4).
 *
 * It exists because [IdentityService] cannot seed categories itself: the audited write path every
 * category write uses needs the identity service, so the identity service calling a category
 * service would be a construction cycle (plan D6). A plain coordinating class with one caller (the
 * setup screen), not an abstraction.
 *
 * @param runner the transaction both parts run in (both nest inside it).
 * @param identity creates the identity.
 * @param categories seeds the defaults, attributed to the identity just created.
 */
class FirstRunService(
    private val runner: TransactionRunner,
    private val identity: IdentityService,
    private val categories: CategoryService,
) {
    /**
     * Creates the user named [userName] and a device labelled [deviceLabel], then seeds the default
     * categories, all or nothing.
     *
     * @return the new identity.
     * @throws dev.imagh.pft.ledger.validation.LedgerError.BlankName / `NameTooLong` for an invalid name or label.
     * @throws dev.imagh.pft.ledger.validation.LedgerError.AlreadyInitialised if this device already has an
     *   identity (nothing is seeded).
     */
    suspend fun initialise(
        userName: String,
        deviceLabel: String,
    ): Identity =
        runner.inTransaction {
            val created = identity.initialise(userName, deviceLabel)
            categories.seedDefaults()
            created
        }
}
