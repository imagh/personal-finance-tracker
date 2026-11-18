package dev.imagh.pft.ledger.transaction

/**
 * Whether the user has confirmed a transaction (PRD L-6). Manual entries are verified when saved;
 * auto-logged ones (SMS, email, recurring) start unverified and surface for review.
 */
enum class TxnState {
    /** Logged automatically and not yet confirmed by the user. */
    UNVERIFIED,

    /** Entered or confirmed by the user. */
    VERIFIED,
}
