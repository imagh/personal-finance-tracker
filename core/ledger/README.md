# `:core:ledger` — the ledger foundations

## Purpose

The core of the household ledger: client-generated ids, who-is-using-this-device (identity), the
audit trail and the one audited write path, atomic transactions, shared validation, and runtime
settings. This is the **foundation** (plan step A3); accounts, categories, tags, transactions,
loans and goals are added to this module by later slices, composed from the building blocks
below. Pure Kotlin/JVM, no Android imports, no third-party runtime dependency (only
`:core:money`).

## Responsibilities & boundaries

- **Owns:** UUIDv7 and name-based ids; typed id classes; `EntityMeta` (attribution and lifecycle
  columns); the audit model, diff and the `AuditedWriter`; the `TransactionRunner` contract;
  `LedgerError` and `Validation`; users, devices and first-run setup; audited app settings; and
  the repository interfaces for all of these.
- **Does not own:** persistence (Room/SQLCipher implement the interfaces in `app`, step A4a),
  UI, logging, serialisation of audit `changes` (`app` does that with `org.json`), money
  representation (`:core:money`). Budget and recurring logic live in `:core:budget` and
  `:core:recurring`, which depend on this module.
- **Layering:** UI -> ViewModel -> service -> repository -> db. Services contain business rules;
  repositories hold none. The only interfaces here are the swap boundaries `core` and `app` meet
  at: `EntityRepository`, `IdentityRepository`, `AppSettingsRepository`, `AuditLog` and
  `TransactionRunner` (CLAUDE.md abstraction bar). Everything else is concrete.
- **Untrusted input:** `Validation`, `IdentityService` and `SettingsService` are the boundary for
  user-typed text. They trim, bound and type it before anything is stored.

## Public interface

Package root `dev.imagh.pft.ledger`. The **reusable building blocks** (plan §3.4) that every
entity step composes instead of re-implementing:

| Building block | Package | What it gives an entity step |
|---|---|---|
| `EntityMeta<Id>` | `entity` | id, `createdAt`/`updatedAt`, `createdByUserId`/`createdOnDeviceId`, `deletedAt` (tombstone), `archivedAt`; `isDeleted`/`isArchived`/`isActive`. Composed as `meta` into every model |
| `LedgerEntity<Id, Self>` | `entity` | `meta` + `withMeta(...)`; implemented by each entity data class so `AuditedWriter` can stamp it |
| `Auditable` | `entity` | `auditType`, `auditId`, `auditFields(): Map<String, String?>`; the diff works on it, no reflection |
| `EntityRepository<T, Id>` | `entity` | `entityType` (the entity's `auditType`, used to name it in `NotFound`), `get` (tombstones included), `getLive` (tombstones excluded; archived included), `insert`, `update`; each entity's repository extends it with its own queries |
| `AuditedWriter` | `audit` | `create`, `update`, `delete`, `restore`, `archive`, `unarchive`: one transaction, attribution from the current identity, the diff and the audit entry; a no-op writes nothing |
| `AuditDiff`, `AuditEntry`, `FieldChange`, `AuditAction`, `AuditSource`, `AuditLog`, `AuditRecorder` | `audit` | the immutable trail: `diff(old, new)` of field text, the entry model, the insert-only `AuditLog` boundary, and the single place an entry is built |
| `TransactionRunner` | `tx` | `inTransaction { ... }`: atomic, nestable; Room's `withTransaction` in `app` |
| `Validation`, `LedgerLimits`, `LedgerError` | `validation` | `requireName`, `requireAmount`, `requireLive`; the static limits; the sealed error vocabulary (add new cases here) |
| `Uuid7`, `NameBasedId`, `UserId`/`DeviceId`/`AuditEntryId`, `EntityId` | `ids` | time-sortable ids, deterministic UUIDv5 ids (`NameBasedId.of("recurring-txn", ruleId, dueDate)`), typed ids (each slice adds its own, implementing `EntityId`) |
| `IdentityService`, `Identity`, `UserProfile`, `Device`, `IdentityRepository` | `identity` | `initialise` / `initialiseForExistingUser` (first run), `currentIdentity()`, `requireIdentity()`, `users()` |
| `SettingsService`, `AppSetting`, `AppSettingsRepository`, `SettingKeys` | `settings` | audited runtime settings: `get(key)`, `set(key, value)` |

### Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...)

Consume with `testImplementation(testFixtures(project(":core:ledger")))`. Package
`dev.imagh.pft.ledger.fakes` and `...matchers`:

| Fixture | Use |
|---|---|
| `InMemoryLedgerStore` | The fake `TransactionRunner` and home of every fake table. `inTransaction` snapshots all stores when the outermost block starts and **restores them if any block at any depth throws**, even when an outer block swallows it, so atomicity is testable in core. `newStore(idOf)` registers a new table |
| `InMemoryEntityStore<T, Id>` | A keyed, insertion-ordered table with primary-key behaviour (duplicate insert and missing update fail) |
| `InMemoryEntityRepository<T, Id>` | `EntityRepository` over a store; extend it with an entity's own queries (`protected val rows`) |
| `InMemoryAuditLog`, `InMemoryIdentityRepository`, `InMemoryAppSettingsRepository` | the fakes for this module's boundaries |
| `LedgerTestKit` | the whole graph wired over the fakes: `store`, `clock`, `ids`, `identity`, `settings`, `writer`, `initialise()`; `writerWith(log)` / `identityServiceWith(log)` inject a failing audit log |
| `TestClock` | a hand-moved `java.time.Clock` |
| `shouldHaveAudited` / `shouldNotHaveAudited` | Kotest matchers over an audit trail: `kit.store.auditLog.entries.shouldHaveAudited("account", id, AuditAction.CREATE, userId = ...)` |

### How an entity step composes the blocks

```kotlin
data class Account(override val meta: EntityMeta<AccountId>, val name: String) :
    LedgerEntity<AccountId, Account> {
    override val auditType = "account"
    override fun auditFields() = mapOf("name" to name)   // list EVERY persisted business field
    override fun withMeta(meta: EntityMeta<AccountId>) = copy(meta = meta)
}

// in AccountService: only the account's own rules
val name = Validation.requireName(cmd.name, LedgerLimits.MAX_NAME_LENGTH)
writer.create(accounts, AccountId(ids.next())) { Account(it, name) }
```

### Behaviours worth knowing

- **Audit trail:** every create, edit, delete, restore, archive and setting change appends an
  `AuditEntry` in the **same transaction** as the change. `changes` is `field -> (old, new)` as
  text. CREATE has null olds, DELETE a snapshot with null news, RESTORE the fields with null
  olds, ARCHIVE/UNARCHIVE empty changes. The diff reads `auditFields()`, so **every persisted
  business field must be listed there**. `AuditedWriter.update` enforces it where it can: an edit
  that changes the entity but no listed field throws `IllegalStateException` (nothing written),
  never a silent drop. An edit that changes a listed and an unlisted field together cannot be
  detected, so the unlisted one would go unaudited: check `auditFields()` in review.
- **Soft delete only (INT-3):** `delete` sets `deletedAt`; there is no hard delete. `restore` is
  audited. Lifecycle calls are idempotent: repeating one is a no-op that audits nothing.
- **The writer owns the meta:** id, timestamps, attribution and lifecycle state are set by
  `AuditedWriter`; a builder or update lambda cannot forge them.
- **Fail closed:** no identity -> `LedgerError.NotInitialised`; a current-device setting without
  its device or user -> `LedgerError.IdentityCorrupt` (never silently "new install").
- **Errors never carry user content** (names, amounts), only a kind or a limit, so they are safe
  in logs and diagnostics.
- **IDs:** `Uuid7` is strictly increasing within a process (12-bit counter, backwards-clock safe).
  `NameBasedId` is UUIDv5 over a length-prefixed encoding; its output is pinned by tests because
  devices will store these ids.

## Data it owns

Defines (and `app` persists, from step A4a) these tables; they are created in Room v1:

| Table | Columns |
|---|---|
| `user_profile` | `id`, `display_name` |
| `device` | `id`, `user_id`, `label`; this device is named by `app_setting('current_device_id')` |
| `audit_entry` | `id`, `entity_type`, `entity_id`, `action`, `changes_json`, `user_id`, `device_id`, `source`, `at`; insert-only, immutability enforced by DB triggers |
| `app_setting` | `key`, `value`, `updated_at` |

Entity tables added by later slices share the `EntityMeta` columns (`id`, `created_at`,
`updated_at`, `created_by_user_id`, `created_on_device_id`, `deleted_at`, plus archive state).

## Configuration

Per CLAUDE.md §3.5, chosen by how the value varies:

- **Static limits -> typed code, `LedgerLimits`** (not user-tunable): `MAX_NAME_LENGTH = 100`
  (accounts, categories, tags, user and device names), `MAX_TITLE_LENGTH = 200`,
  `MAX_DESCRIPTION_LENGTH = 2000`, `MAX_AMOUNT = Paise.MAX_AMOUNT` (Rs 1e11),
  `MAX_SETTING_KEY_LENGTH = 64`, `MAX_SETTING_VALUE_LENGTH = 256`. The hardcoded invariants
  (integer paise, audit on every write, soft delete) cannot be switched off.
- **Runtime-varying values -> the database (`app_setting`), via `SettingsService`**, every change
  audited: for example the budget alert threshold, upcoming-lookahead days and auto-lock timeout.
  Keys are lower-case identifiers (`[a-z][a-z0-9_]*`); `current_device_id` is reserved for
  first-run setup and cannot be written through `SettingsService`.
  **Values are written, in clear text, into the audit trail** (old and new). Use `SettingsService`
  only for non-sensitive tuning values. A future sensitive setting (a PIN, a token, anything
  secret) must not go through it; give it its own store with its own handling.
- Environment config: none.

## Dependencies

- `:core:money` (`api`: `Paise` is part of the validation API).
- JDK only at runtime (`java.time`, `java.security`); Kotlin stdlib. No coroutines library: the
  `suspend` contracts need only the language feature; `kotlinx-coroutines` arrives with the first
  `Flow`-returning query (slice C).
- Tests: JUnit 5 and Kotest (runner, assertions, property) from the `pft.kotlin-core` convention
  plugin; fixtures add `kotest-assertions-core` as an `api` dependency for the matchers.
- Depended on by: `:app` (and later `:core:budget`, `:core:recurring`).

## How to run and test

```bash
./gradlew :core:ledger:check      # tests + ktlint + detekt + the 90% JaCoCo line-coverage gate
./gradlew :core:ledger:test       # tests only
```

Tests (Kotest on JUnit 5, `core/ledger/src/test/kotlin/dev/imagh/pft/ledger/`): `Uuid7Test`,
`NameBasedIdTest` (RFC 9562 vector and pinned ids), `IdsTest`, `AuditDiffTest`, `EntityMetaTest`,
`ValidationTest`, `IdentityServiceTest`, `SettingsServiceTest`, `AuditedWriterTest` (including
rollback of the write and its audit entry), and `fakes/` tests that pin the fake runner's
rollback semantics and the matcher. Services are tested against the in-memory fakes; the same
contracts are re-tested against real Room in `app` (step A4a).
