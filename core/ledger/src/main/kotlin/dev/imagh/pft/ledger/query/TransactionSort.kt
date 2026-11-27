package dev.imagh.pft.ledger.query

/**
 * The order of a month list or a search (slice C C3c). [Key] is what entries are ordered by and
 * [descending] the direction. **Every order ends with the same fixed tie-breaks, ascending in both
 * directions** (the transfer group id, then the transaction id), so the order is always total.
 *
 * @property key what to order by; default [Key.OCCURRED].
 * @property descending true for newest, largest or last-in-the-alphabet first; default true.
 */
data class TransactionSort(
    val key: Key = Key.OCCURRED,
    val descending: Boolean = true,
) {
    /** What an order compares. Persisted nowhere; the data layer maps each to a fixed SQL fragment. */
    enum class Key {
        /** When the payment happened (`occurredAt`). */
        OCCURRED,

        /**
         * When the record was created. A transfer is ranked by the **first** creation of its two legs, the
         * same value for both and in both directions.
         */
        CREATED,

        /**
         * When the record was last changed. A transfer is ranked by the **last** change of its two legs
         * (editing one side moves one leg only), the same value for both and in both directions.
         */
        UPDATED,

        /** The transaction's amount: an itemised transaction by its total, a transfer by its amount. */
        AMOUNT,

        /** The title, ignoring ASCII case; entries without a title come last in both directions. */
        TITLE,
    }

    companion object {
        /** Newest payment first: what the month list showed before sort existed. */
        val DEFAULT = TransactionSort()
    }
}
