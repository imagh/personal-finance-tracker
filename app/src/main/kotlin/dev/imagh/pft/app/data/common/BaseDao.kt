package dev.imagh.pft.app.data.common

import androidx.room.Insert
import androidx.room.Update

/**
 * The DAO operations every ledger entity table shares (plan §3.4). An entity's DAO extends this
 * and adds its own `@Query` methods, including the override of [byId] (an `@Query` on the table).
 *
 * No logic lives here or in any DAO (CLAUDE.md §3.1): rules belong to core services.
 */
interface BaseDao<E> {
    /** Inserts [row]; fails if the id exists (never replaces). */
    @Insert
    suspend fun insert(row: E)

    /** Replaces the row with the same id; returns how many rows changed (0 when there is none). */
    @Update
    suspend fun update(row: E): Int

    /** The row with [id], tombstoned or not, or null. Each DAO overrides this with its `@Query`. */
    suspend fun byId(id: String): E?
}
