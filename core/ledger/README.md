# `:core:ledger` — the ledger foundations

## Purpose

The core of the household ledger: client-generated ids, who-is-using-this-device (identity), the
audit trail and the one audited write path, atomic transactions, shared validation, and runtime
settings. This is the **foundation** (plan step A3); accounts, categories, tags, transactions,
loans and goals are added to this module by later slices, composed from the building blocks
below. **Accounts** are the first entity built on them (slice B1b, package `account`), **categories**
the second (slice B3a, package `category`, with first-run seeding in `setup`) and **tags** the third
(slice B5a, package `tag`). **Transactions** and their lines (items) are the fourth (slices C1a and
C1b, package `transaction`). Pure Kotlin/JVM, no
Android imports, no third-party runtime dependency (only `:core:money`).

## Responsibilities & boundaries

- **Owns:** UUIDv7 and name-based ids; typed id classes; `EntityMeta` (attribution and lifecycle
  columns); the audit model, diff and the `AuditedWriter`; the `TransactionRunner` contract;
  `LedgerError` and `Validation`; users, devices and first-run setup; audited app settings;
  accounts (`AccountService`); categories (`CategoryService`, the default set and the `icon_key`
  grammar); tags (`TagService`); transactions and their lines (`TransactionService`, `Breakdown`,
  `TransactionLines`); first-run setup (`FirstRunService`); and the repository interfaces for all of these.
- **Does not own:** persistence (Room implements the interfaces in `app`, built in A4a; SQLCipher follows in A4b),
  UI, logging, serialisation of audit `changes` (`app` does that with `org.json`), money
  representation (`:core:money`). Budget and recurring logic live in `:core:budget` and
  `:core:recurring`, which depend on this module.
- **Layering:** UI -> ViewModel -> service -> repository -> db. Services contain business rules;
  repositories hold none. The only interfaces here are the swap boundaries `core` and `app` meet
  at: `EntityRepository`, `IdentityRepository`, `AppSettingsRepository`, `AccountRepository`,
  `CategoryRepository`, `TagRepository`, `TransactionRepository`, `TransactionLineRepository`, `AuditLog` and `TransactionRunner` (CLAUDE.md abstraction bar). Everything else is concrete.
- **Untrusted input:** `Validation`, `IdentityService`, `SettingsService`, `TransactionDraft.validated()`
  and `Breakdown.of` are the boundary for user-typed text and amounts. They trim, bound and type it before anything is stored.

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
| `Validation`, `LedgerLimits`, `LedgerError` | `validation` | `requireName`, `requireOptionalName` (blank -> null), `requireAmount` (positive), `requireBalance` (signed, zero allowed, bounded by `MAX_AMOUNT` either way), `requireLive`, `nameKey` (the one "same name ignoring case" comparison, shared by categories and tags); the static limits; the sealed error vocabulary (add new cases here) |
| `Uuid7`, `NameBasedId`, `UserId`/`DeviceId`/`AuditEntryId`/`AccountId`/`CategoryId`/`TagId`, `EntityId` | `ids` | time-sortable ids, deterministic UUIDv5 ids (`NameBasedId.of("recurring-txn", ruleId, dueDate)`), typed ids (each slice adds its own, implementing `EntityId`) |
| `IdentityService`, `Identity`, `UserProfile`, `Device`, `IdentityRepository` | `identity` | `initialise` / `initialiseForExistingUser` (first run), `currentIdentity()`, `requireIdentity()`, `users()` |
| `SettingsService`, `AppSetting`, `AppSettingsRepository`, `SettingKeys` | `settings` | audited runtime settings: `get(key)`, `set(key, value)` |
| `AccountService`, `Account`, `AccountType`, `AccountDraft`, `AccountLists`, `AccountRules`, `AccountRepository` | `account` | accounts (PRD L-4): `create(draft)`, `edit(id, draft)`, `archive`/`unarchive`, `delete`/`restore`, `active()`, `all()`, `lists()` (active and archived, each sorted by name ignoring case, then by creation order); `AccountRules.requireMaskedIdentifier(text)` is public so a form can check the last four with core's rule. `AccountRepository` adds `allLive()` to `EntityRepository` |
| `CategoryService`, `Category`, `CategoryLists`, `CategoryKind`, `CategoryDraft`, `CategoryColour`, `CategoryIcon`, `SystemCategory`, `DefaultCategories`, `DefaultCategory`, `CategoryRepository` | `category` | categories (PRD L-2): `create(parentId, kind, draft)`, `edit(id, draft)` (name, colour, icon only), `reorder(parentId, kind, orderedIds)`, `archive`/`unarchive`, `delete`/`restore`, `pickable(kind)`, `lists(kind)` (for a list screen: `CategoryLists(active, archived)`, B4a), `seedDefaults()`. `CategoryColour` is the 12 permanent palette keys (`fromStored`: unknown becomes slate); `CategoryIcon` is `Symbol(key)` / `Emoji(text)` with `toStored()`, `parse(stored)` and `isValid`, the one home of the `icon_key` grammar (the app's `GlyphSource` delegates to it). `CategoryRepository` adds `allLive()` and `bySystemKey(key)` to `EntityRepository`. `CategoryTree` (sorting, sibling checks, picker order) is internal: plain functions over `allLive()`, unit-tested without a repository |
| `TagService`, `Tag`, `TagLists`, `TagRepository` | `tag` | tags (PRD L-3): `create(name)`, `edit(id, name)` (rename), `archive`/`unarchive`, `delete`/`restore`, `lists()` (active and archived, each sorted by name ignoring case, then by id), `pickable()` (the active list). `TagRepository` adds `allLive()` to `EntityRepository`. The service takes a `TransactionRunner` for the check-then-write name rule |
| `TransactionService`, `LedgerTransaction`, `TransactionLine`, `TransactionType`, `Direction`, `TxnState`, `TxnSource`, `TransactionDraft`, `LineDraft`, `Breakdown`, `TransactionWithLines`, `TransactionLines`, `TransferService`, `TransferDraft`, `Transfer`, `TransactionRepository`, `TransactionLineRepository` | `transaction` | transactions (PRD L-1, L-5, L-6, L-9, L-11), slices C1a, C1b and C2: `create(draft)`, `edit(id, draft)`, `delete`/`restore` (the transaction only; its lines stay; a transfer leg acts on **both legs**), `markVerified(id)` (refused on a transfer leg), `get(id): TransactionWithLines?` (live only, lines by position, then id). A transaction holds the payment facts and **one or more lines** hold the classification (category, tags, note). `Breakdown.of(total, drafts)` is the pure check of a split (count, amounts, notes, distinct ids, exact sum). `TransactionLines(writer, lines, categories, tags, ids)` is the line logic the transaction and transfer services share (create, reconcile, the reference checks and the sum re-check): its constructor is public so `app` can wire it, its operations are `internal`. `TransactionLineRepository` adds `liveLinesOf(txnId)` (no order promised), `isCategoryInUse(id)` and `isTagInUse(id)` (a live line of a live transaction uses it); `TransactionRepository` adds `isAccountInUse(id)` (a live transaction is on it) and `legsOfGroup(groupId)` (every transaction of a transfer group, tombstones included, no order promised). `TransferService.create(TransferDraft): Transfer` and `edit(groupId, TransferDraft): Transfer` write a transfer (C2, see Behaviours); delete and restore of a transfer go through `TransactionService`. `AccountService`, `CategoryService` and `TagService` take these repositories to refuse a delete with `LedgerError.InUse(what)`. Create and edit take 1 to 50 lines (itemising, C1b) |
| `FirstRunService` | `setup` | `initialise(userName, deviceLabel)`: the identity (`IdentityService.initialise`) **and** `CategoryService.seedDefaults()` in one transaction. A plain coordinating class; `IdentityService` stays unaware of categories because the writer needs the identity service (a construction cycle otherwise) |

### Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...)

Consume with `testImplementation(testFixtures(project(":core:ledger")))`. Package
`dev.imagh.pft.ledger.fakes` and `...matchers`:

| Fixture | Use |
|---|---|
| `InMemoryLedgerStore` | The fake `TransactionRunner` and home of every fake table. `inTransaction` snapshots all stores when the outermost block starts and **restores them if any block at any depth throws**, even when an outer block swallows it, so atomicity is testable in core. `newStore(idOf)` registers a new table |
| `InMemoryEntityStore<T, Id>` | A keyed, insertion-ordered table with primary-key behaviour (duplicate insert and missing update fail) |
| `InMemoryEntityRepository<T, Id>` | `EntityRepository` over a store; extend it with an entity's own queries (`protected val rows`) |
| `InMemoryAuditLog`, `InMemoryIdentityRepository`, `InMemoryAppSettingsRepository` | the fakes for this module's boundaries |
| `InMemoryAccountRepository` | `AccountRepository` over `InMemoryEntityRepository` (adds `allLive()`, which returns rows newest first on purpose, so no test depends on an order the contract does not promise); `all()` returns every row, tombstones included, in insertion order |
| `InMemoryCategoryRepository` | `CategoryRepository` over `InMemoryEntityRepository` (adds `allLive()`, newest first on purpose, and `bySystemKey`) |
| `InMemoryTagRepository` | `TagRepository` over `InMemoryEntityRepository` (adds `allLive()`, newest first on purpose) |
| `InMemoryTransactionRepository`, `InMemoryTransactionLineRepository` | the transaction stores over `InMemoryEntityRepository`; `liveLinesOf` and `legsOfGroup` return newest first on purpose; the line store joins to the transaction store for the in-use queries, as the SQL does |
| `LedgerTestKit` | the whole graph wired over the fakes: `store`, `clock`, `ids`, `identity`, `settings`, `writer`, `accountRepository`, `accounts` (an `AccountService`), `categoryRepository`, `categories` (a `CategoryService`), `tagRepository`, `tags` (a `TagService`), `transactionRepository`, `transactionLineRepository`, `transactions` (a `TransactionService`), `transfers` (a `TransferService`), `firstRun` (a `FirstRunService`), `initialise()` (identity only, no defaults); `writerWith(log)` / `identityServiceWith(log)` inject a failing audit log |
| `TestClock` | a hand-moved `java.time.Clock` |
| `AuditFieldsCheck`, `shouldHaveCompleteAuditFields()` | Asserts an entity's `auditFields()` is complete (see above); `InMemoryEntityRepository` runs it on every write, with no opt-out |
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
  olds, ARCHIVE `archived_at: null -> <ISO instant>`, UNARCHIVE the reverse.
- **`auditFields()` must be complete.** The diff reads `auditFields()`, so **every persisted
  business field must be listed there, under its snake_case name** (`openingBalance` ->
  `opening_balance`), and nothing else. It is enforced twice:
  - *In tests:* `InMemoryEntityRepository` runs `AuditFieldsCheck` on every insert and update, with
    no opt-out. It uses plain Java reflection over the entity's `declaredFields`; names must match
    both ways, and the audited value must equal the field's value for text, whole numbers,
    booleans, enums, dates, `Paise` and ids. A misspelt or missing key fails any service test that
    writes the entity. It also fails if the repository's `entityType` differs from the entity's
    `auditType`.
  - *At runtime:* `AuditedWriter.update` throws `IllegalStateException` (nothing written) when the
    entity changed but no listed field did.

  Rules that follow from how the check works:
  - **Reserved names.** `meta`, `auditType` and `auditId` are exempt, so they cannot be business
    properties of an entity.
  - **Final data classes only.** Only the concrete class's `declaredFields` are read, so an entity
    must be a final data class. A delegated or lazy property (`by lazy`) creates a hidden field and
    is flagged.
  - **No non-persisted fields.** Derive such a value in a getter (`val x get() = ...`), which has
    no backing field. If a field ever needs redacted auditing, add a per-field exemption to the
    check (named, reviewed, documented here); never an opt-out for a whole entity or repository.
  - **Collections and composite values are checked by name only.** Flatten them to text in
    `auditFields()` (for example the sorted tag ids joined with `,`).
  - **Call the matcher in each entity's own test**, on a fully populated instance (every optional
    field set): `entity.shouldHaveCompleteAuditFields()`. A fake write only checks the instance it
    is given, and a null field is trivially consistent.
  - Typed ids and `Paise` need nothing special.
- **Accounts** (`AccountService`): the name is trimmed and 1 to 100 characters; names need not be
  unique. The opening balance is a **signed** `Paise` bounded by `LedgerLimits.MAX_AMOUNT` either
  way (`requireBalance`), so an overdrawn account or a card with money owed is representable; which
  sign the UI asks for by type is the UI's choice, core does not tie them. The last four digits are
  null when blank, otherwise exactly four ASCII `0-9` (not `Char.isDigit()`, which accepts other
  scripts' digits), else `InvalidMaskedIdentifier`. The issuer is optional (blank -> null, same 100
  limit). The currency is always INR and is not on the model: the persistence layer writes it and
  refuses anything else. There is no stored balance (INT-6). All checks run before anything is
  written, so a refused call leaves no row and no audit entry. `edit` audits only the changed
  fields, and an identical edit writes nothing. Archive and delete are idempotent. `delete` is refused
  with `InUse("account")` while a **live transaction** is on the account (archived or not; a transfer
  leg counts; a deleted transaction does not), and the check and the delete run in one
  `runner.inTransaction`, so a transaction saved meanwhile cannot slip between them (C1d, D4).
- **Categories** (`CategoryService`, plan D5-D7, D10). Two levels only, and a sub-category has the
  kind of its parent:
  - *Hierarchy.* A sub-category's parent must exist, be live (`NotFound` / `Deleted` / `Archived`),
    be top-level (`DepthExceeded`) and have the same kind (`KindMismatch`).
  - *Kind and parent are fixed at creation.* `edit` takes name, colour and icon only (the icon can be
    cleared). To "move" a category, delete and re-create it.
  - *Names* are trimmed, 1 to 100 characters and unique among the live siblings (same parent and
    kind), compared by `trim()` then `lowercase(Locale.ROOT)` (Unicode-aware, unlike SQLite
    `NOCASE`): `DuplicateName("category")`, which carries the kind of record and never the name.
    Archived siblings count, deleted ones do not. The check and the write run in one transaction;
    renaming a category to a case variant of its own name is allowed; `restore` re-checks and
    refuses a clash.
  - *Icons.* `create` and `edit` refuse an icon outside the grammar with `InvalidIcon`, so an invalid
    icon can never be stored. Grammar: a symbol key is `[a-z0-9_]{1,40}`; an emoji is 1 to 16
    UTF-16 units of well-formed text with no whitespace, control or invisible format characters
    (zero-width space, bidi overrides), except the zero-width joiner and tag characters. Whether a
    symbol key exists in the app's registry and whether an emoji renders are the app's checks.
    `CategoryIcon.parse` bounds the length before it copies or scans, and never throws.
  - *Order.* A new category goes last among its live siblings (`max + 1`, or 0). `reorder` takes
    the full order and requires it to be exactly the live siblings of that parent and kind
    (archived ones included, each once), else `InvalidOrder` and nothing is written. It hands the
    siblings' **existing `sort_order` values out again in the new order**, so a category that keeps
    its place keeps its value and writes nothing, even when a deleted sibling left a gap; each
    moved one gets an audited UPDATE, all in one transaction. **Ties are broken by the reorder:** two
    siblings can hold the same value (a deleted category restored after a new one took its place;
    M2 sync), so the values handed out are made strictly increasing (a tied value moves up to one
    past the one before it), which leaves every non-tied value, gaps included, where it was.
  - *Archive hides.* `pickable(kind)` returns the categories to offer: not archived, and not under an
    archived parent. **Depth-first order:** each top-level category in `sort_order`, immediately
    followed by its own pickable children in `sort_order`; ties are broken by id, so the order never
    depends on what the repository returned. Unarchiving a parent brings its non-archived children
    back. Archive does not touch the children's own state.
  - *Lists (B4a).* `lists(kind)` returns what a list screen shows: `CategoryLists(active, archived)`,
    each depth-first (every top-level category in `sort_order`, then its own children). A category is in
    `archived` when it is archived or its parent is (both are hidden from pickers); nothing deleted is in
    either. `CategoryLists.siblingsOf(parentId)` gives the live categories under one parent from both
    lists in display order (`sort_order`, then id): exactly the set `reorder` needs, archived ones
    included, so a caller can swap two neighbours without knowing the tie-break.
  - *Delete and restore.* Delete is a tombstone, refused with `HasChildren` while the category has
    live children (archived ones count; there is no cascade). `restore` of a sub-category whose
    parent is deleted gives `Deleted("category")`. Delete is also refused with `InUse("category")`
    while a **live line of a live transaction** uses the category (C1d, D4); archive it instead. A
    deleted transaction keeps its lines as they were, so the check joins to the transaction.
  - *System category.* `LOAN_EMI` (`systemKey != null`) can be renamed, recoloured and re-iconed but
    `archive` and `delete` give `SystemCategoryProtected`, nothing written.
  - *Audit.* `auditFields()` uses the column names (`parent_id`, `kind`, `name`, `colour`,
    `icon_key`, `sort_order`, `system_key`); the model property for the icon is named `iconKey` for
    that reason (`AuditFieldsCheck` matches property and key names). Enums, `colour` included, are
    audited by enum name (`CORAL`); the icon by `toStored()`.
- **Tags** (`TagService`, plan D10): a tag is a name (trimmed, 1 to 100 characters) and nothing else.
  - *Names* are unique among the **live** tags, compared by `Validation.nameKey` (`trim()` then
    `lowercase(Locale.ROOT)`, the same function categories use for their siblings):
    `DuplicateName("tag")`, which carries the kind of record and never the name. Archived tags
    count (an archived "Travel" still blocks "travel"); deleted ones do not. The check and the write
    run in one transaction (`create`, and `edit` inside the writer's); renaming a tag to a case
    variant of its own name is allowed; `restore` re-checks and refuses a clash, leaving the tag
    deleted. There is no database index behind this (Room cannot declare a partial or `NOCASE`
    unique index), so this rule is the only guard.
  - *Lifecycle.* Archive hides a tag from `pickable()` but keeps it in `lists().archived`; delete is a
    tombstone, `restore` undoes it, and each call is idempotent and audited. Delete is refused with
    `InUse("tag")` while a live line of a live transaction carries the tag (C1d, D4); archive it
    instead, which keeps it on the lines that have it.
  - *Lists.* `lists()` and `pickable()` sort by `nameKey`, then by id, so tags that compare equal (two
    devices' tags after a sync, a restore that raced a create) never swap places with the repository's
    order. `auditFields()` is `name`.
- **Transactions** (`TransactionService`, slice C D2/D3, IT §3.1 to §3.3).
  - *The sum invariant* (hardcoded, CLAUDE.md §3.8): every live transaction has at least one live
    line, and its live lines' amounts add up **exactly** to its amount. `ledger_transaction` holds no
    category and no tags; they live on `transaction_line`. `TransactionService` and `TransferService` are the only writers of
    both (through `TransactionLines`), and every create and edit passes `Breakdown.of` before anything is written: 1 to 50 lines
    (`TooManyLines(50)`), each amount positive and within the limit, notes trimmed and at most 200
    characters (`NameTooLong(200)`), no line id twice (`DuplicateLine`), and the exact sum with
    `Paise.plus` (`BreakdownMismatch`, also for an empty list). The transaction's own amount is
    checked first, so a bad amount is reported as such and not as a mismatch.
  - *Manual entries* are `MANUAL` and `VERIFIED` (L-6); `direction` follows the type (expense OUT,
    income IN). Title, description and bank reference are optional: trimmed, blank stored as null,
    at most 200, 2000 and 64 characters. A `TRANSFER` draft, or an edit of a stored transfer leg, is
    refused with `IsTransfer` (a transfer is created and edited with `TransferService`), and so is
    `markVerified` on a leg. `delete` and `restore` of a leg act on both legs (see *Transfers* below).
  - *References are checked only where they change* (IT §3.3 rule 3): the account and a line's
    category must be live when set or changed, an added tag must be live; what a transaction keeps
    may have been archived since, so editing an old transaction's title never fails. The category's
    kind must match the type **always** (`KindMismatch`), so expense ↔ income works only with a
    matching category. A refused call writes no row and no audit entry (the transaction and its lines are
    written in one `runner.inTransaction`).
  - *Several lines, reconciled (C1b; IT §3.3 rules 4 to 8).* `create` and `edit` take 1 to 50 lines
    (`TransactionLines`). On **create** every line is new (a draft line must name no id,
    `NotFound("transaction_line")`) and gets the id `NameBasedId.of("transaction-line", txnId,
    position)` for position 1..n, so the same transaction derives the same lines on every device.
    On **edit** the drafts are matched to the live lines **by id**: a draft with the id of a live line
    of this transaction updates it (a change-free one writes nothing), a draft with no id adds a line
    (a random time-ordered `Uuid7`), and a live line no draft names is tombstoned (audited DELETE; its
    row stays, so its id can never be used again). **Positions are renumbered 1..n in draft order**,
    so a reordering changes only the `position` fields. A draft id that is another transaction's
    line, a tombstoned line or unknown is `NotFound("transaction_line")`: a line can never be
    re-parented or brought back through a draft. References are checked as above on every line: the
    kind always, liveness only for a new line, a changed category or an added tag. Itemising changes
    lines only: the payment facts of an auto-captured transaction (`amount`, `account_id`,
    `occurred_at`, `bank_ref`, `source_message_id`, `source`, `state`) are untouched and no
    `ledger_transaction` entry is written. Changing the amount needs lines that add up to the new
    amount in the same edit (`BreakdownMismatch` otherwise).
  - *Defence in depth.* Before `create` or `edit` returns, `TransactionLines` reads the live lines
    back; if they do not add up to the amount it throws `IllegalStateException` (fixed text, no
    amounts), which rolls back the whole `runner.inTransaction`: the transaction, its lines and the
    audit entries. `Breakdown` has already checked the drafts, so this only fires on a bug in the
    reconciliation or a store that misbehaves; since the sum must match exactly, tests inject a store that
    drops an insert and one that stores an extra line.
  - *Edit* audits only the changed fields of each row and keeps the state, source and message facts;
    a no-op edit writes nothing. *Delete and restore* act on the transaction row only: its lines stay,
    hidden with it, so restore brings back exactly the lines it had. Because the in-use check counts
    live transactions only, a deleted one may point at something deleted since, so `restore` first
    checks that its account, and the category and tags of its **live** lines, are not deleted
    (`Deleted("account" | "category" | "tag")`, nothing written; archived ones are fine, like a
    sub-category restored under a deleted parent). `get` reads the transaction and
    its lines in one `runner.inTransaction`. `markVerified` audits
    `state: UNVERIFIED -> VERIFIED`; on a verified one it is a no-op.
  - *Transfers* (C2; IT §3.3 rule 2, PRD L-5). `TransferService` writes money moved between two of the
    user's accounts as **two linked transactions**: an OUT leg on the source and an IN leg on the target,
    both `type = TRANSFER`, sharing a `transfer_group_id` (a `Uuid7`), with the same amount, time, title
    and description, `MANUAL` and `VERIFIED`. A card bill payment is a transfer from a bank account to a
    credit card. Each leg has **exactly one line**, uncategorised, carrying the whole amount and the
    transfer's tags (the same on both); a category, or other than one line, is
    `TransferCannotBeItemised` (the rule is in `TransactionLines`, so it holds on create and reconcile).
    The same account on both sides is `SameAccount`; the amount, texts, accounts and tags are checked as
    for any transaction. *Create* gives four CREATE entries (two transactions, two lines). *Edit*
    (`edit(groupId, draft)`) updates both legs and both lines (same line ids, same group, same
    directions), keeps the line-sum invariant on each, audits only what changed, and refuses a deleted
    transfer with `Deleted` as an edit of a deleted transaction does; a group that is not exactly one OUT
    and one IN leg, or a leg without exactly one live line, fails closed with `IllegalStateException`.
    **Every write of a pair is one transaction**: both legs, both lines and every audit entry, or nothing.
    `TransactionService.delete` and `restore` find the other leg by `transfer_group_id` and act on both in
    one `runner.inTransaction` (a DELETE or RESTORE entry for each leg; lines stay); a restore runs the
    in-use checks for both legs, so a deleted account on either side refuses the whole restore. A
    transfer leg with no group id is refused with `IsTransfer` (its other leg cannot be found; fail
    closed). `TransactionService.edit` and `markVerified` on a leg stay `IsTransfer`; verifying a transfer
    is revisited with the M1b captures (a manual transfer is verified when made). Balances and totals (C3a)
    must count a transfer as neither income nor expense.
  - *Audit keys.* `ledger_transaction`: `type`, `direction`, `amount`, `occurred_at`, `account_id`,
    `title`, `description`, `state`, `source`, `bank_ref`, `source_message_id`, `account_fragment`,
    `transfer_group_id` (no `recurring_rule_id`: E3 adds it, Q8). `transaction_line`:
    `transaction_id`, `position`, `amount`, `category_id`, `note`, `tag_ids` (the ids sorted by their
    text and joined with `,`; null when there are none).
- **Default categories** (`DefaultCategories.LIST`, `CategoryService.seedDefaults()`): the 31
  starter categories of plan §4 (27 expense, with sub-categories, including the Loan EMI system
  category, and 4 income). They are only a starting set: users create their own through `create`.
  Each has a permanent `seedKey`; its id is `CategoryId(NameBasedId.of("default_category", seedKey))`,
  so every device seeds the same ids (M2 sync will not see two "Food"). Seeding is audited with
  source `SYSTEM`, attributed to the current user and device, parents before children, `sort_order`
  following the table order within siblings. It skips any id that already exists, **tombstoned ones
  included**, so a default the user deleted is not re-created and a second call writes and audits
  nothing. It runs once, inside `FirstRunService`; installs set up before categories existed get no
  defaults (plan Q4).
- **First run** (`FirstRunService.initialise`): the user, device, current-device setting and their 3
  audit rows, then the 31 defaults and their audit rows, in one transaction; if any append fails
  (the tests fail the 10th, the 7th category), nothing at all is written. A second call gives
  `AlreadyInitialised` and seeds nothing.
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

Defines (and `app` persists, since A4a) these tables; they are Room schema v1 (details in `docs/architecture/data-model.md`):

| Table | Columns |
|---|---|
| `user_profile` | `id`, `display_name` |
| `device` | `id`, `user_id`, `label`; this device is named by `app_setting('current_device_id')` |
| `audit_entry` | `id`, `entity_type`, `entity_id`, `action`, `changes_json`, `user_id`, `device_id`, `source`, `at`; insert-only, immutability enforced by DB triggers |
| `app_setting` | `key`, `value`, `updated_at` |

`account` (schema v2) is defined here and persisted by `app`:
`name`, `type`, `opening_balance` (signed paise), `masked_identifier`, `issuer`, plus the currency
column the persistence layer owns.

`category` (schema v3, persisted by `app` in slice B3b; the model is defined here): `parent_id`
(null for top level), `kind` (enum name), `name`, `colour` (a `CategoryColour` key), `icon_key`
(`sym:<key>` or `emoji:<text>`, null for none), `sort_order`, `system_key` (null for a user category).

`tag` (schema v4, persisted by `app` in slice B5a; the model is defined here): `name`.

`ledger_transaction` and `transaction_line` (the models are defined here since C1a; `app` persists
them since C1c, schema v5): the audit keys above are their columns, except that a line's `tag_ids` is
not a column but the rows of the `transaction_line_tag` join table (a non-entity table; the audit check's
`keysStoredIn`, `docs/architecture/data-model.md`).

Entity tables added by later slices share the `EntityMeta` columns (`id`, `created_at`,
`updated_at`, `created_by_user_id`, `created_on_device_id`, `deleted_at`, plus archive state).

## Configuration

Per CLAUDE.md §3.5, chosen by how the value varies:

- **Static limits -> typed code, `LedgerLimits`** (not user-tunable): `MAX_NAME_LENGTH = 100`
  (accounts, categories, tags, user and device names), `MAX_TITLE_LENGTH = 200`,
  `MAX_DESCRIPTION_LENGTH = 2000`, `MAX_AMOUNT = Paise.MAX_AMOUNT` (Rs 1e11),
  `MAX_LINES_PER_TRANSACTION = 50` (items are per purpose, not per product), `MAX_LINE_NOTE_LENGTH = 200`,
  `MAX_BANK_REF_LENGTH = 64`,
  `MAX_SETTING_KEY_LENGTH = 64`, `MAX_SETTING_VALUE_LENGTH = 256`. The hardcoded invariants
  (integer paise, audit on every write, soft delete, the transaction sum invariant) cannot be switched
  off.
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
  `suspend` contracts need only the language feature. Slice C keeps it so (no `Flow` in core,
  slice C Q1); revisit when background inserts exist (E4 or M1b).
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
`ValidationTest`, `validation/CategoryErrorsTest`, `account/AccountServiceTest`, `account/AccountRulesTest`,
`category/CategoryIconTest` (the `icon_key` grammar, exhaustively), `category/CategoryColourTest`,
`category/CategoryTreeTest`, `category/CategoryServiceTest` (create, edit), `category/CategoryLifecycleTest`
(archive, delete, restore), `category/CategoryOrderTest` (reorder, `pickable`), `category/CategoryListsTest` (`lists`, `siblingsOf`),
`category/DefaultCategoriesTest` (the exact default table, ids, SYSTEM source, no re-seeding),
`tag/TagServiceTest` (create, rename, the name rule), `tag/TagLifecycleTest` (archive, delete, restore, the lists and `pickable`),
`transaction/LedgerTransactionTest` and `TransactionLineTest` (audit keys, `tag_ids`), `transaction/BreakdownTest`
(every split rule, plus partition property tests), `transaction/TransactionServiceTest` (create: pinned line id,
validation, references), `transaction/TransactionEditTest` (exact audit diffs, no-op, type change, rule 3),
`transaction/TransactionItemisationTest` (several lines, reconciliation by id, foreign and tombstoned ids, positions, auto-captured facts, the fail-closed sum re-check over a faulty store),
`transaction/TransactionLifecycleTest` (delete, restore, `markVerified`, `get` order), `transaction/TransactionRestoreReferencesTest` (restore refused after a reference was deleted),
`account/AccountInUseTest`, `category/CategoryInUseTest`, `tag/TagInUseTest` (the in-use refusals), `validation/TransactionErrorsTest`,
`setup/FirstRunServiceTest` (identity plus defaults, all-or-nothing), `IdentityServiceTest`, `SettingsServiceTest`, `AuditedWriterTest` (including
rollback of the write and its audit entry), and `fakes/` tests that pin the fake runner's
rollback semantics and the matcher. Services are tested against the in-memory fakes; the same
contracts are re-tested against real Room in `app` (A4a: `RoomTransactionRunnerTest`, `IdentityRoomIntegrationTest`,
`RoomAuditedWriterIntegrationTest`).
