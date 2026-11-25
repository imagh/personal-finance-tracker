# Data model

The Room database `pft.db`: tables, conventions and the version table. Status: **schema v5 (plan
slice C1c)**. Each later slice adds its tables through its own migration and a migration test and
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
registered in `AuditedTables` (test support) or the same test fails. Current allowlist (unchanged by `category` and `tag`):
`app_setting.key` (the setting's identifier, not an audited value) and `account.currency` (always
`INR`, not part of the model; the mapper writes it and refuses anything else on read).

**`keysStoredIn`: an audited value that is not a column.** A line's tags are rows of the join table
`transaction_line_tag`, so the line audits `tag_ids` (the sorted ids joined with `,`) with no `tag_ids`
column on `transaction_line`. The registry entry says so: `AuditedTable(..., keysStoredIn = mapOf("tag_ids"
to "transaction_line_tag"))` (audited key to the table that holds it). `AuditColumnsCheck` then accepts the
missing column only when **the holder table exists in the database and is listed in
`AuditedTables.notEntityTables`** (with its reason), and the sample audits the key; a misspelt table, an
unlisted table or a stale entry each fails. Join tables are not ledger entities: no meta columns, no
`AuditedTables.all` entry, and their rows are replaced rather than tombstoned (the audited diff of the
owning entity is the record). E3's `recurring_rule.tag_ids` will use the same convention.

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

## Version 4 (B5a)

| Table | Columns | Notes |
|---|---|---|
| `tag` | shared columns, `name` | A free-form label for the items of a transaction (PRD L-3). A name and nothing else: no colour, icon or order. No foreign keys and **no index** (tens of rows). The link from an item to its tags is `transaction_line_tag` (v5) |

- **Name uniqueness is a core rule, not a table rule.** The plan says "unique among live tags, ignoring
  case". SQL could only say that with a partial unique index (`WHERE deleted_at IS NULL`) or a `NOCASE`
  collation (ASCII only, so `Éclair` and `éCLAIR` would differ); Room can declare neither on an `@Entity`,
  so there is none (see the version 3 note above). `TagService` compares `Validation.nameKey` (trimmed,
  lower-cased with `Locale.ROOT`) over the live tags inside the write transaction, archived tags count,
  deleted ones do not, and `restore` checks again. The migrated table and a fresh one are identical, and
  the table itself would accept two tags with the same name.
- **Every column is audited; the allowlist did not grow** (`tag` is registered in `AuditedTables` with a
  fully populated sample and no allowlist entry).
- **Migration `3 -> 4`** is the single `CREATE TABLE` copied from the exported `schemas/.../4.json`;
  `MigrationTest.migrate3To4` starts from a v3 database with an account row and a (top-level) category
  row, validates the result against that schema, and checks the columns, that no index or key was added,
  the surviving rows and the audit triggers. Nothing is seeded: tags start empty.

## Version 5 (C1c)

| Table | Columns | Notes |
|---|---|---|
| `ledger_transaction` | shared columns, `type`, `direction`, `amount` (positive paise), `occurred_at` (epoch ms), `account_id` (FK -> `account.id`), `title` NULL, `description` NULL, `state`, `source`, `bank_ref` NULL, `source_message_id` NULL, `account_fragment` NULL, `transfer_group_id` NULL | One payment fact (PRD L-1, L-9): what moved, how much, when, on which account. **No `category_id`, no tag column and no `recurring_rule_id`**: classification lives on lines. `type`, `direction`, `state` and `source` are enum names. Indexes `index_ledger_transaction_occurred_at`, `..._account_id_occurred_at` (also serves the foreign key) and `..._transfer_group_id` |
| `transaction_line` | shared columns, `transaction_id` (FK -> `ledger_transaction.id`), `position` (from 1, display order only), `amount` (positive paise), `category_id` NULL (FK -> `category.id`), `note` NULL | One item of a transaction (PRD L-11): its own category, tags and note. `archived_at` is never set for a line. Indexes on `transaction_id` and `category_id` |
| `transaction_line_tag` | `line_id` (FK -> `transaction_line.id`), `tag_id` (FK -> `tag.id`); primary key `(line_id, tag_id)` | Which tags a line carries. **A join table, not an entity** (see `keysStoredIn` above): audited as the line's `tag_ids`, rows replaced when the tag set changes. Index on `tag_id` (the in-use check of C1d) |

- **No `CHECK` and no `ON DELETE` action, as for `category`.** Room cannot declare a `CHECK` on an
  `@Entity`, so `amount > 0` and `position > 0` would exist only in migrated databases; the plans' `CHECK`
  wording is the rule, not the schema. Core validates before writing and the mappers **fail closed** on an
  unknown `type`, `direction`, `state` or `source` and on a non-positive amount or position (the message
  names the column, never the value). Foreign keys use Room's default action: rows are tombstoned, never
  removed, and SQLite enforces the keys on both engines. There is no unique index on
  `(transaction_id, position)` either: Room cannot declare a partial one, and positions are display order.
- **Why the line-sum invariant is not a trigger.** The invariant (a live transaction has at least one live
  line, and its live lines add up to its amount, CLAUDE.md §3.8) holds between operations, not between
  statements: an edit updates line 1, then inserts lines 2 and 3, and SQLite has no deferred cross-row
  `CHECK`, so an immediate trigger would fire on a correct intermediate state and refuse legitimate edits.
  Core enforces it instead: `Breakdown` before anything is written, and a re-read of the stored lines
  before commit that fails closed (and rolls the transaction back) on a mismatch. A database-level check
  stays visible through `TransactionLineDao.breakdownMismatchCount()`: live transactions with no live line
  or with live lines that do not sum to the amount (a tombstoned transaction is not counted; a tombstoned
  line does not count towards the sum). It is 0 whenever only `TransactionService` has written, and the
  Room integration tests assert that after every scenario and that a raw-SQL line planted by the test
  makes it 1.
- **A line and its tags are one write.** `RoomTransactionLineRepository` writes the line row and its
  `transaction_line_tag` rows in one Room transaction (it nests inside the service's), and `update`
  replaces the tag set. A tag id with no row fails the foreign key and rolls back the whole service
  transaction: the transaction row, every line and every audit entry (INT-4).
- **Every column is audited; the allowlist did not grow.** `ledger_transaction` and `transaction_line` are
  registered with fully populated samples and no allowlist entry; `transaction_line.tag_ids` uses
  `keysStoredIn`, and `transaction_line_tag` is a listed non-entity table.
- **Migration `4 -> 5`** is the three `CREATE TABLE` statements and their five indexes copied from the
  exported `schemas/.../5.json`, in parent-first order. `MigrationTest.migrate4To5` starts from a v4
  database with an account, a category and a tag (parents seeded before children), validates the result
  against that schema, and checks the columns, that the new tables are empty, the indexes and keys, the
  surviving rows and the audit triggers. Nothing is seeded.

## Version table

| Version | Slice | Adds |
|---|---|---|
| 1 | A4a | `user_profile`, `device`, `app_setting`, `audit_entry` and the audit triggers |
| 2 | B1c | `account` (built) |
| 3 | B3b | `category` (built) |
| 4 | B5a | `tag` (built) |
| 5 | C1c | `ledger_transaction`, `transaction_line`, `transaction_line_tag` (built) |
| 6 | C3 | `transaction_fts`, `transaction_line_fts` |
| 7 | D3 | `budget`, `budget_scope`, `budget_revision` |
| 8 | E3 | `recurring_rule`, `recurring_occurrence` |
| 9 | F2 | `loan`; `ledger_transaction.loan_id`, `neutralised_by_loan_id` |
| 10 | G1 | `goal`; `ledger_transaction.goal_id` |

`tag` has its own migration (v4, slice B5a; plan 2026-11-10 D3), so every later slice is one version higher than the M1a plan first numbered it (C1 is v5 … G1 is v10); the M1a plan and the itemised-transactions plan were renumbered to match in the B-final PR. Only the rows up to `transaction_line_tag` (v5) are built; the rest are planned (M1a plan §4). Versions 5 and 6 follow the itemised-transactions amendment (`docs/plans/2026-11-10-itemised-transactions.md` §4): category and tags live on transaction lines, whose amounts always sum to the transaction amount. Those plans still write `CHECK (amount > 0)` and `CHECK (position > 0)` for the new tables: per the rule above, C1 enforces them in core and the mappers instead (version 5 above).
