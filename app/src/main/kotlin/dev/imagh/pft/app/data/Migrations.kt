package dev.imagh.pft.app.data

import androidx.room.migration.Migration

/**
 * Every schema migration of [PftDatabase], in version order. Each slice that bumps
 * [PftDatabase.VERSION] adds its `MIGRATION_n_n+1` here and to [ALL] (plan §4), together with a
 * `migrateNToN+1` test; `MigrationTest` fails if a step is missing or out of order, and checks after
 * every migration that the audit trail and its triggers survived.
 *
 * Migrations are forward-only and use SQL only (no core code, which may change after the
 * migration shipped). A migration that rebuilds `audit_entry` must re-create the audit triggers.
 */
object Migrations {
    /** All migrations for `Room.databaseBuilder(...).addMigrations(*Migrations.ALL.toTypedArray())`. */
    val ALL: List<Migration> = emptyList()
}
