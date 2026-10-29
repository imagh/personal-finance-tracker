package dev.imagh.pft.ledger.validation

import dev.imagh.pft.money.Paise

/**
 * The ledger's static limits (CLAUDE.md §3.5: static values live in typed code, not the DB).
 *
 * They are not user-tunable. Runtime-varying values (budget alert threshold, auto-lock timeout,
 * ...) are app settings in the database instead; see `SettingsService`.
 */
object LedgerLimits {
    /** Longest name of an account, category, tag, user or device, in characters. */
    const val MAX_NAME_LENGTH = 100

    /** Longest transaction title, in characters (plan §3.2 decision 8). */
    const val MAX_TITLE_LENGTH = 200

    /** Longest transaction description, in characters (plan §3.2 decision 8). */
    const val MAX_DESCRIPTION_LENGTH = 2_000

    /** Largest amount on a single transaction: Rs 1e11, the same bound the amount parser enforces. */
    val MAX_AMOUNT: Paise = Paise.MAX_AMOUNT
}
