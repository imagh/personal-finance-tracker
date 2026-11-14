# Data model

The Room database `pft.db`: tables, conventions and the version table. Status: **schema v3 (plan
slice B3b)**. Each later slice adds its tables through its own migration and a migration test and
updates this file in the same PR. The full table plan is in
`docs/plans/2026-10-28-m1a-core-ledger.md` §4; the encryption layer (SQLCipher, Keystore key) is
A4b and is described in [persistence-and-security.md](persistence-and-security.md).

Core models are the canonical shape. The Room entities in `app/.../data/` are persistence DTOs that
are mapped one-to-one to them, and core never sees them (CLAUDE.md §3.3).

## Conventions

| Topic | Rule |
|---|---|
| Money | `INTEGER` paise, never floating point. Amounts on transactions are positive; the sign comes from `direction` |
| Ids | 36-character UUID text (`Uuid7` or `NameBasedId`), primary key `id TEXT` |
| Time | epoch **milliseconds** UTC, `INTEGER`, stored exactly: `EpochMillis.of` refuses a finer instant instead of rounding it (so a stored entity always equals the one written, and `updated_at` equals the audit `at`). The app builds core's `Clock` with `EpochMillis.clock()` (A5) |
| Column names | explicit snake_case `@ColumnInfo(name = ...)`, equal to the keys of the model's `auditFields()` |
| Shared columns | `id`, `created_at`, `updated_at`, `created_by_user_id`, `created_on_device_id`, `deleted_at NULL`, `archived_at NULL` on every ledger entity table (`MetaColumns`, `@Embedded`). Entities declare `primaryKeys = ["id"]` themselves (Room does not count a key inside `@Embedded`) |
| Delete | soft delete only (`deleted_at`), hard delete never happens (INT-3) |
| Queries | Room DAOs only, every query parameterised, no `@RawQuery` |
| Schema changes | a `Migration` plus a `MigrationTestSupport` test per version; exported JSON in `app/schemas/<db class>/<version>.json` is committed |

Every column must be audited or named in an explicit, reviewed allowlist. The test
`RoomAuditColumnsTest` enforces it by comparing `PRAGMA table_info` (minus the shared columns and the
allowlist) with the keys of a fully populated sample's `auditFields()`. A new table must be
registered in `AuditedTables` (test support) or the same test fails. Current allowlist (unchanged by `category`):
`app_setting.key` (the setting's identifier, not an audited value) and `account.currency` (always
`INR`, not part of the model; the mapper writes it and refuses anything else on read).

## Version 1 (A4a)

| Table | Columns | Notes |
|---|---|---|
| `user_profile` | `id` PK, `display_name` | A household member. Not a `LedgerEntity` (no shared columns, no soft delete): M1a never edits or removes a user |
| `device` | `id` PK, `user_id` FK -> `user_profile.id` (RESTRICT), `label` | One installation. Which device is *this* one is `app_setting('current_device_id')` |
| `app_setting` | `key` PK, `value`, `updated_at` | Runtime-varying values; changes are audited by `SettingsService` and the value appears in clear text in the trail, so non-sensitive values only |
| `audit_entry` | `id` PK, `entity_type`, `entity_id`, `action`, `changes_json`, `user_id`, `device_id`, `source`, `at`; index on (`entity_type`, `entity_id`) | The immutable audit trail (below) |

### The audit trail

`changes_json` is `{"<field>":{"old":<text or null>,"new":<text or null>}, ...}` in field order
(`AuditEntryMapper`, `org.json`). Unset sides are JSON `null`, which is different from the text
`"null"`. The shape is a contract between devices once sync exists, so it changes only with a versioned
migration. A row that cannot be parsed fails closed with a message that holds no row content.

`audit_entry` is **append-only in the database**. `AuditTriggers` installs two triggers (Room does not
model triggers) through a `RoomDatabase.Callback` that `PftDatabase.create` attaches, on **every open**
(a same-named trigger is dropped and re-created first): `audit_entry_no_update` and
`audit_entry_no_delete`, each `BEFORE ... ON audit_entry` with `RAISE(ABORT, 'audit_entry is
append-only')`, so any UPDATE or DELETE, from any code, fails with `SQLiteConstraintException`. The
DAO has only insert and select as well. The table has **no foreign keys** on purpose: the trail
must stay writable and intact whatever happens to the rows it describes. Limits: `DROP TABLE` and an
outside tool holding the key are not stopped; the encrypted file (A4b) is the protection against
others.

`PftDatabase.create` (`internal` to the app module) is the only way a database is built, so no caller can forget the triggers, and
a database that lost or had its triggers replaced (an old file, a bad migration, tampering) is repaired
on the next open. The exported schema JSON has no triggers, so `MigrationTestSupport` installs them when it
builds an old-version database and, after every migration, fails unless all triggers exist and still
refuse to update or delete a seeded audit row. A migration that rebuilds `audit_entry` must re-create
them.

## Version 2 (B1c)

| Table | Columns | Notes |
|---|---|---|
| `account` | shared columns (below), `name`, `type` (the `AccountType` name), `currency` (always `INR`), `opening_balance` (signed paise), `masked_identifier` NULL (last four digits), `issuer` NULL | A place money is kept (PRD L-4). No stored balance (INT-6): the balance is the opening balance plus the account's transactions, computed in slice C. No foreign keys and no index (tens of rows) |

- **`currency` fails closed.** `AccountMapper` always writes `INR` and throws on read for any other
  value, naming the column and never the value. It is outside `auditFields()` because it is not on the
  core model; the audit-columns test carries it as a reviewed allowlist entry.
- **No `CHECK` constraints and no partial indexes, anywhere.** Room cannot declare either on an
  `@Entity`, so one added only in a migration would exist in upgraded databases and not in fresh
  installs. Invariants that SQL would enforce (INR, the name rules, balance bounds) live in core
  services and mappers instead (plan 2026-11-10 §2, learnings 2026-11-10).
- **Migration `1 -> 2`** is one `CREATE TABLE IF NOT EXISTS` copied verbatim from Room's exported
  `schemas/.../2.json` (table name filled in), so an upgraded table is identical to a fresh one;
  `MigrationTest.migrate1To2` validates it against that schema and checks the audit triggers survived.
  Room writes the embedded shared columns **last** in the statement; the order is not part of the
  contract.
- Two instrumented tests run it on the encrypted file (`EncryptedMigrationTest`, see
  [persistence-and-security.md](persistence-and-security.md)).

## Version 3 (B3b)

| Table | Columns | Notes |
|---|---|---|
| `category` | shared columns, `parent_id` NULL (FK -> `category.id`), `kind` (the `CategoryKind` name), `name`, `colour` (the palette **key**, `coral`), `icon_key` NULL (`sym:<key>` or `emoji:<text>`), `sort_order` (INTEGER), `system_key` NULL (the `SystemCategory` name) | A group for the items of a transaction (PRD L-2), two levels. Indexes: `index_category_parent_id` on `parent_id` (Room requires one for the key) and **unique** `index_category_system_key` on `system_key` (SQLite allows many NULLs, so only system categories take part: at most one `LOAN_EMI` row, tombstones included) |

- **Every column is audited; the allowlist did not grow.** The audit trail names `colour` by its enum
  name (`CORAL`, like `kind` and `system_key`) while the column holds the key (`coral`): the audit-columns
  test compares column names only, and the owner confirmed the difference.
- **Foreign key `parent_id -> category(id)`, no `ON DELETE` action** (rows are tombstoned, never
  removed). **Result of the plan's [Verify]: SQLite enforces it.** Room turns `PRAGMA foreign_keys` on, so
  an insert whose `parent_id` does not exist fails with `SQLiteConstraintException`.
  `RoomCategoryForeignKeyTest` pins it on the framework SQLite (Robolectric) and
  `EncryptedDatabaseTest.foreignKeysAreEnforcedOnTheEncryptedDatabase` on SQLCipher (`pft35`). The
  guard is still two layers: `CategoryService` validates the parent (live, top-level, same kind) in the
  write transaction, and the database refuses a dangling reference whatever the service does. Because
  the key is enforced, **a migration or a later table rebuild that inserts a child before its parent
  fails**; insert parents first.
- **No `CHECK` constraints and no partial indexes, and why.** Room creates a fresh database's tables
  from the `@Entity` classes and cannot declare a `CHECK` or a `WHERE` index there, so one written only
  into a migration would exist in upgraded databases and not in fresh installs: the two would silently
  enforce different rules. So the rules SQL would have enforced live in core (`currency = 'INR'`, the name
  rules, balance bounds, two levels, kind and parent fixed, and **name uniqueness among live siblings**,
  checked case-insensitively inside the write transaction), and the schema holds only what Room can
  declare on both paths: types, `NOT NULL`, the foreign key, plain and unique indexes.
- **No default categories in the migration.** First-run setup creates them (`FirstRunService`, one
  transaction with the identity). A database that already has an identity when it migrates to v3 stays
  without them (owner decision Q4: no launch-time seeding; only dev installs existed).
- **Migration `2 -> 3`** is the `CREATE TABLE` and two `CREATE INDEX` statements copied from the
  exported `schemas/.../3.json`; `MigrationTest.migrate2To3` starts from a v2 database with an account
  row, validates the result against that schema, and checks the columns, both indexes (and which is
  unique), the key, the account row and the audit triggers. A second test inserts into the migrated table
  and gets the dangling-parent and second-system-key refusals.

## Version table

| Version | Slice | Adds |
|---|---|---|
| 1 | A4a | `user_profile`, `device`, `app_setting`, `audit_entry` and the audit triggers |
| 2 | B1c | `account` (built) |
| 3 | B3b | `category` (built) |
| 4 | C1 | `ledger_transaction`, `transaction_line`, `transaction_line_tag` |
| 5 | C3 | `transaction_fts`, `transaction_line_fts` |
| 6 | D3 | `budget`, `budget_scope`, `budget_revision` |
| 7 | E3 | `recurring_rule`, `recurring_occurrence` |
| 8 | F2 | `loan`; `ledger_transaction.loan_id`, `neutralised_by_loan_id` |
| 9 | G1 | `goal`; `ledger_transaction.goal_id` |

`tag` moves out of version 3 into its own migration (v4, slice B5a; plan 2026-11-10 D3), so every row below it shifts by one (C1 becomes v5); the B-final PR renumbers this table and the plans that quote it. Versions 4 onwards are planned (plan §4), not built. Versions 4 and 5 follow the itemised-transactions amendment (`docs/plans/2026-11-10-itemised-transactions.md` §4): category and tags live on transaction lines, whose amounts always sum to the transaction amount.
