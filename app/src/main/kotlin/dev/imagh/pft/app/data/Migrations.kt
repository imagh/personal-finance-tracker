package dev.imagh.pft.app.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
    /**
     * 1 to 2 (slice B1): adds the `account` table. The statement is Room's own `CREATE TABLE` for
     * `AccountEntity`, copied from the exported `schemas/.../2.json` with the table name filled in, so
     * a migrated database and a fresh one have the identical table (`MigrationTest` validates it
     * against that schema). Embedded meta columns come last because Room lists them there.
     *
     * No `CHECK` constraint and no index: Room cannot declare a CHECK on an entity, so one added
     * here would exist only in migrated databases (plan §2); the INR rule lives in `AccountMapper`.
     */
    val MIGRATION_1_2: Migration =
        object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `account` (`name` TEXT NOT NULL, `type` TEXT NOT NULL, " +
                        "`currency` TEXT NOT NULL, `opening_balance` INTEGER NOT NULL, " +
                        "`masked_identifier` TEXT, `issuer` TEXT, `id` TEXT NOT NULL, " +
                        "`created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, " +
                        "`created_by_user_id` TEXT NOT NULL, `created_on_device_id` TEXT NOT NULL, " +
                        "`deleted_at` INTEGER, `archived_at` INTEGER, PRIMARY KEY(`id`))",
                )
            }
        }

    /** All migrations for `Room.databaseBuilder(...).addMigrations(*Migrations.ALL.toTypedArray())`. */
    val ALL: List<Migration> = listOf(MIGRATION_1_2)
}
