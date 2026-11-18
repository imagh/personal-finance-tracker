package dev.imagh.pft.ledger.transaction

/**
 * Which way the money moved for the transaction's account. Derived from the type for an expense
 * ([OUT]) or an income ([IN]); each transfer leg stores its own.
 */
enum class Direction {
    /** Money came into the account. */
    IN,

    /** Money left the account. */
    OUT,
}
