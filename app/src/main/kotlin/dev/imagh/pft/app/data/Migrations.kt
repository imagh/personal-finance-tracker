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
 *
 * Room's full-text content-sync triggers are different: Room drops them before any migration runs
 * (`onPreMigrate`) and recreates them afterwards (`onPostMigrate`), so in production it is `onPostMigrate`, not a
 * migration, that gives a migrated database its sync triggers. A migration that **writes or rebuilds a content
 * table** (`ledger_transaction`, `transaction_line`) must still end with the two `rebuild` statements
 * (`TransactionFts.REBUILD`), because no trigger runs for rows it writes while the triggers are gone.
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

    /**
     * 3 to 4 (slice B5): adds the `tag` table. The statement is Room's own `CREATE TABLE` for `TagEntity`,
     * copied from the exported `schemas/.../4.json` with the table name filled in, so a migrated database
     * and a fresh one have the identical table (`MigrationTest` validates it against that schema).
     *
     * No index, no `CHECK` constraint and no partial or `NOCASE` unique index, as in the earlier steps: Room
     * cannot declare those on an entity (plan §2), so "unique among live tags, ignoring case" lives in
     * `TagService`, in the write transaction. Nothing is seeded: tags start empty.
     */
    val MIGRATION_3_4: Migration =
        object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `tag` (`name` TEXT NOT NULL, `id` TEXT NOT NULL, " +
                        "`created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, " +
                        "`created_by_user_id` TEXT NOT NULL, `created_on_device_id` TEXT NOT NULL, " +
                        "`deleted_at` INTEGER, `archived_at` INTEGER, PRIMARY KEY(`id`))",
                )
            }
        }

    /**
     * 4 to 5 (slice C1c): adds `ledger_transaction`, `transaction_line` and `transaction_line_tag`, with
     * their indexes and foreign keys, in that order (a child after its parent). The statements are Room's
     * own for the three entities, copied from the exported `schemas/.../5.json` with the table names
     * filled in, so a migrated database and a fresh one are identical (`MigrationTest` validates it
     * against that schema). Embedded meta columns come last because Room lists them there.
     *
     * No `CHECK` constraint, no partial or unique index on `(transaction_id, position)` and no
     * `ON DELETE` action: Room cannot declare a `CHECK` or a partial index on an entity (plan §2), and rows
     * are tombstoned, never removed. The amount and position rules live in core and in the mappers; the
     * "live lines add up to the amount" invariant is checked by core and counted by
     * `TransactionLineDao.breakdownMismatchCount`, not enforced by a trigger (data-model.md). Nothing is
     * seeded: the tables start empty, and no old row changes.
     */
    val MIGRATION_4_5: Migration =
        object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `ledger_transaction` (`type` TEXT NOT NULL, " +
                        "`direction` TEXT NOT NULL, `amount` INTEGER NOT NULL, `occurred_at` INTEGER NOT NULL, " +
                        "`account_id` TEXT NOT NULL, `title` TEXT, `description` TEXT, `state` TEXT NOT NULL, " +
                        "`source` TEXT NOT NULL, `bank_ref` TEXT, `source_message_id` TEXT, " +
                        "`account_fragment` TEXT, `transfer_group_id` TEXT, `id` TEXT NOT NULL, " +
                        "`created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, " +
                        "`created_by_user_id` TEXT NOT NULL, `created_on_device_id` TEXT NOT NULL, " +
                        "`deleted_at` INTEGER, `archived_at` INTEGER, PRIMARY KEY(`id`), " +
                        "FOREIGN KEY(`account_id`) REFERENCES `account`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE NO ACTION )",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_ledger_transaction_occurred_at` " +
                        "ON `ledger_transaction` (`occurred_at`)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_ledger_transaction_account_id_occurred_at` " +
                        "ON `ledger_transaction` (`account_id`, `occurred_at`)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_ledger_transaction_transfer_group_id` " +
                        "ON `ledger_transaction` (`transfer_group_id`)",
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `transaction_line` (`transaction_id` TEXT NOT NULL, " +
                        "`position` INTEGER NOT NULL, `amount` INTEGER NOT NULL, `category_id` TEXT, " +
                        "`note` TEXT, `id` TEXT NOT NULL, `created_at` INTEGER NOT NULL, " +
                        "`updated_at` INTEGER NOT NULL, `created_by_user_id` TEXT NOT NULL, " +
                        "`created_on_device_id` TEXT NOT NULL, `deleted_at` INTEGER, `archived_at` INTEGER, " +
                        "PRIMARY KEY(`id`), FOREIGN KEY(`transaction_id`) REFERENCES `ledger_transaction`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE NO ACTION , FOREIGN KEY(`category_id`) " +
                        "REFERENCES `category`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_transaction_line_transaction_id` " +
                        "ON `transaction_line` (`transaction_id`)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_transaction_line_category_id` " +
                        "ON `transaction_line` (`category_id`)",
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `transaction_line_tag` (`line_id` TEXT NOT NULL, " +
                        "`tag_id` TEXT NOT NULL, PRIMARY KEY(`line_id`, `tag_id`), " +
                        "FOREIGN KEY(`line_id`) REFERENCES `transaction_line`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE NO ACTION , FOREIGN KEY(`tag_id`) " +
                        "REFERENCES `tag`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_transaction_line_tag_tag_id` " +
                        "ON `transaction_line_tag` (`tag_id`)",
                )
            }
        }

    /**
     * The Devanagari combining marks the version 6 full-text tables treat as word characters (`tokenchars=`),
     * copied from `6.json`. A frozen copy of `TransactionFts.DEVANAGARI_MARKS` as it was at version 6: a migration
     * never changes after it ships, whatever the current tokenizer is.
     */
    private const val FTS_TOKENCHARS_V6: String =
        "\u0900\u0901\u0902\u0903" +
            "\u093A\u093B\u093C\u093E\u093F\u0940\u0941\u0942\u0943\u0944\u0945" +
            "\u0946\u0947\u0948\u0949\u094A\u094B\u094C\u094D\u094E\u094F" +
            "\u0951\u0952\u0953\u0954\u0955\u0956\u0957" +
            "\u0962\u0963"

    /**
     * 5 to 6 (slice C3b): adds the full-text indexes `transaction_fts` (title, description) and
     * `transaction_line_fts` (note). Both are external-content FTS4 tables with the `unicode61` tokenizer,
     * and each comes with Room's four content-sync triggers (before and after update, before delete, after
     * insert). The statements are Room's own, copied from the exported `schemas/.../6.json` with the table
     * names filled in, so a migrated database and a fresh one are identical. In production Room
     * drops the content-sync triggers before a migration and recreates them after it (`onPostMigrate`), so
     * the copies here are not what protects a user's database: they keep a migrated database identical to a fresh
     * one and serve `MigrationTestHelper`, which runs no callbacks. `MigrationTest` checks them by writing through
     * them and by comparing them with a fresh database's.
     *
     * An external-content index starts empty, so the last two statements are `rebuild`,
     * which fills each index from the rows that already exist (the same statements as
     * `TransactionFts.REBUILD`, spelled out because a migration is frozen). No old row changes, and the new tables are
     * derived, unaudited indexes (data-model.md).
     */
    val MIGRATION_5_6: Migration =
        object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE VIRTUAL TABLE IF NOT EXISTS `transaction_fts` USING FTS4(`title` TEXT, " +
                        "`description` TEXT, tokenize=unicode61 `tokenchars=$FTS_TOKENCHARS_V6`, " +
                        "content=`ledger_transaction`)",
                )
                db.execSQL(
                    "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_transaction_fts_BEFORE_UPDATE BEFORE " +
                        "UPDATE ON `ledger_transaction` BEGIN DELETE FROM `transaction_fts` WHERE " +
                        "`docid`=OLD.`rowid`; END",
                )
                db.execSQL(
                    "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_transaction_fts_BEFORE_DELETE BEFORE " +
                        "DELETE ON `ledger_transaction` BEGIN DELETE FROM `transaction_fts` WHERE " +
                        "`docid`=OLD.`rowid`; END",
                )
                db.execSQL(
                    "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_transaction_fts_AFTER_UPDATE AFTER " +
                        "UPDATE ON `ledger_transaction` BEGIN INSERT INTO `transaction_fts`(`docid`, `title`, " +
                        "`description`) VALUES (NEW.`rowid`, NEW.`title`, NEW.`description`); END",
                )
                db.execSQL(
                    "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_transaction_fts_AFTER_INSERT AFTER " +
                        "INSERT ON `ledger_transaction` BEGIN INSERT INTO `transaction_fts`(`docid`, `title`, " +
                        "`description`) VALUES (NEW.`rowid`, NEW.`title`, NEW.`description`); END",
                )
                db.execSQL(
                    "CREATE VIRTUAL TABLE IF NOT EXISTS `transaction_line_fts` USING FTS4(`note` TEXT, " +
                        "tokenize=unicode61 `tokenchars=$FTS_TOKENCHARS_V6`, content=`transaction_line`)",
                )
                db.execSQL(
                    "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_transaction_line_fts_BEFORE_UPDATE " +
                        "BEFORE UPDATE ON `transaction_line` BEGIN DELETE FROM `transaction_line_fts` WHERE " +
                        "`docid`=OLD.`rowid`; END",
                )
                db.execSQL(
                    "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_transaction_line_fts_BEFORE_DELETE " +
                        "BEFORE DELETE ON `transaction_line` BEGIN DELETE FROM `transaction_line_fts` WHERE " +
                        "`docid`=OLD.`rowid`; END",
                )
                db.execSQL(
                    "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_transaction_line_fts_AFTER_UPDATE " +
                        "AFTER UPDATE ON `transaction_line` BEGIN INSERT INTO `transaction_line_fts`(`docid`, " +
                        "`note`) VALUES (NEW.`rowid`, NEW.`note`); END",
                )
                db.execSQL(
                    "CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_transaction_line_fts_AFTER_INSERT " +
                        "AFTER INSERT ON `transaction_line` BEGIN INSERT INTO `transaction_line_fts`(`docid`, " +
                        "`note`) VALUES (NEW.`rowid`, NEW.`note`); END",
                )
                // Fill both indexes from the rows that already exist. Spelled out, not shared with
                // TransactionFts.REBUILD: a migration is a frozen snapshot of what ran at 5 to 6.
                db.execSQL("INSERT INTO `transaction_fts`(`transaction_fts`) VALUES('rebuild')")
                db.execSQL("INSERT INTO `transaction_line_fts`(`transaction_line_fts`) VALUES('rebuild')")
            }
        }

    /** All migrations for `Room.databaseBuilder(...).addMigrations(*Migrations.ALL.toTypedArray())`. */
    val ALL: List<Migration> = listOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
}
