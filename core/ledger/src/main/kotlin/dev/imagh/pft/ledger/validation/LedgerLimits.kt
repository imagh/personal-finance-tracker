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

    /** Most lines (items) on one transaction (IT §3.6). */
    const val MAX_LINES_PER_TRANSACTION = 50

    /** Longest note on a transaction line, in characters: the same bound as a title (IT §3.6). */
    const val MAX_LINE_NOTE_LENGTH = 200

    /** Longest bank reference (UTR, UPI or cheque number) on a transaction, in characters (slice C D2). */
    const val MAX_BANK_REF_LENGTH = 64

    /**
     * Most entries one search returns (slice C D5). The cap applies to entries after a transfer's two legs
     * are folded into one, and never splits a pair.
     */
    const val MAX_SEARCH_RESULTS = 500

    /** Longest app-setting key, in characters. */
    const val MAX_SETTING_KEY_LENGTH = 64

    /** Longest app-setting value, in characters. */
    const val MAX_SETTING_VALUE_LENGTH = 256

    /** Largest amount on a single transaction: Rs 1e11, the same bound the amount parser enforces. */
    val MAX_AMOUNT: Paise = Paise.MAX_AMOUNT
}
