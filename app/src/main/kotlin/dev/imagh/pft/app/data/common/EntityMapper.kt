package dev.imagh.pft.app.data.common

/**
 * Maps one core model [T] to its Room row [E] and back. Each entity has one (an object); the
 * shared [RoomEntityRepository] and the entity's own queries both use it.
 */
interface EntityMapper<T, E> {
    /** The row for [model]. */
    fun toRow(model: T): E

    /** The model stored in [row]. */
    fun toModel(row: E): T
}
