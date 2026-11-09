package dev.imagh.pft.ledger.fakes

/**
 * A keyed, insertion-ordered in-memory table: the one building block behind every fake
 * repository (plan §3.4). Create stores with [InMemoryLedgerStore.newStore] so that the fake
 * transaction runner snapshots and restores them.
 *
 * Behaves like a primary-keyed table: inserting an existing key, or updating a missing one,
 * fails loudly, so a test notices a service that mixes the two up.
 *
 * @param idOf extracts a row's primary key.
 */
class InMemoryEntityStore<T, Id : Any> internal constructor(
    private val idOf: (T) -> Id,
) {
    private var rows = LinkedHashMap<Id, T>()

    /** The row with [id], or null. */
    fun get(id: Id): T? = rows[id]

    /** All rows in insertion order. */
    fun all(): List<T> = rows.values.toList()

    /** Adds [entity]; fails if its key is already present. */
    fun insert(entity: T) {
        val id = idOf(entity)
        check(id !in rows) { "Duplicate primary key" }
        rows[id] = entity
    }

    /** Replaces the row with [entity]'s key; fails if there is none. */
    fun update(entity: T) {
        val id = idOf(entity)
        check(id in rows) { "No row with this primary key" }
        rows[id] = entity
    }

    /** Captures the current rows and returns a function that puts them back. */
    internal fun snapshot(): () -> Unit {
        val saved = LinkedHashMap(rows)
        return { rows = LinkedHashMap(saved) }
    }
}
