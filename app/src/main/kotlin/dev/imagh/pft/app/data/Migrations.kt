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
@Suppress("MagicNumber") // The version numbers of `Migration(from, to)` are the point of this file.
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

    /**
     * 2 to 3 (slice B3): adds the `category` table and its two indexes. The statements are Room's own
     * for `CategoryEntity`, copied from the exported `schemas/.../3.json` with the table name filled in,
     * so a migrated database and a fresh one are identical (`MigrationTest` validates it against that
     * schema): the self-referencing foreign key on `parent_id`, the index Room requires for it, and the
     * unique index on `system_key` (SQLite allows many NULLs, so only system categories take part).
     *
     * No `CHECK` constraint and no partial index, as in 1 to 2: Room cannot declare either on an
     * entity (plan §2), so name uniqueness among live siblings and the two-level rule live in
     * `CategoryService`. No default categories are inserted here: first-run setup seeds them (plan D6),
     * and a database that already has an identity does not get them (owner decision Q4).
     */
    val MIGRATION_2_3: Migration =
        object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `category` (`parent_id` TEXT, `kind` TEXT NOT NULL, " +
                        "`name` TEXT NOT NULL, `colour` TEXT NOT NULL, `icon_key` TEXT, " +
                        "`sort_order` INTEGER NOT NULL, `system_key` TEXT, `id` TEXT NOT NULL, " +
                        "`created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, " +
                        "`created_by_user_id` TEXT NOT NULL, `created_on_device_id` TEXT NOT NULL, " +
                        "`deleted_at` INTEGER, `archived_at` INTEGER, PRIMARY KEY(`id`), " +
                        "FOREIGN KEY(`parent_id`) REFERENCES `category`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE NO ACTION )",
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_category_parent_id` ON `category` (`parent_id`)")
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_category_system_key` ON `category` (`system_key`)",
                )
            }
        }

    /** All migrations for `Room.databaseBuilder(...).addMigrations(*Migrations.ALL.toTypedArray())`. */
    val ALL: List<Migration> = listOf(MIGRATION_1_2, MIGRATION_2_3)
}
