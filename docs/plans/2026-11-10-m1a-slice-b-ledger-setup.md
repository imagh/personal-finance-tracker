# Plan: M1a slice B — Accounts, categories and tags

| Field       | Value |
|-------------|-------|
| Date        | 2026-11-10 |
| Author      | architect |
| Status      | Proposed: awaiting owner review (Q1 accepted 2026-11-10; Q2–Q7 open, §12). B1a, which no question touches, is in review |
| Related ACs | PRD L-2 (two-level categories: create, rename, recolour/re-icon, reorder, archive), L-3 (tags; the link to transaction items is C1), L-4 (accounts: name, type, currency, optional identifiers), L-9 (one account per transaction; this slice supplies the accounts), L-8 (attribution on every record), INT-2, INT-3, INT-4, INT-5 (first real migration and its backup), INT-6 (no stored balance), SEC-5 (nothing logged), UI-6 (category icons and colours). L-1 and L-11 (itemised transactions) are slice C; this slice only supplies the entities and pickers each item uses |
| Plan owner  | akashg (repo owner) |

Foundation: this is **slice B** of the approved M1a plan, [`2026-10-28-m1a-core-ledger.md`](2026-10-28-m1a-core-ledger.md) (abbreviated **M1a** below): §6 "Slice B" (`:256-277`), the schema rows for v2/v3 (`:167-169`), the shared blocks assigned to B2/B4/B5 (`:115-116`), the A3/A4a/A4b/A5 "As built" notes (`:121-139`, `:228-235`, `:244-254`), and the B1 follow-ups (`:232`, `:261`). It also follows the design-system amendments to B2–B4 ([`2026-10-29-m1a-design-system.md`](2026-10-29-m1a-design-system.md) §3.6, §3.7, §4 and the §6.3 table `:806-808`; the DS3 as-built note `:671-672`).

Slice A is merged to `main` (`75580d6`). The itemised-transactions plan (`docs/plans/2026-11-10-itemised-transactions.md`, merged in #52 at `34b30fb`, PRD decision 35) states that slice B is not affected by it (its §2). The touch points are the schema version C1 gets (D3, Q1), the join-table name, the C1 "in use" checks, and the CHECK constraints it specifies on `transaction_line` (§2, §10).

---

## 1. Requirement

**Need.** Every transaction (slice C) must belong to exactly one account (L-9) and has one or more items, each of which may carry a category or sub-category and tags (L-1, L-2, L-3, L-11). Before the owner can enter a single transaction, the app needs those three reference entities, their rules, their screens, and pickers that slice C can drop into its add-transaction form and its per-item editor (C4b). This slice also lands the first real schema migration, which is the first time the pre-migration backup (INT-5) and the audit-trigger survival check run for real.

**Done means:**
1. **Accounts.** From Home → More → Accounts, a user can add an account (name, type Bank / Credit card / Cash / Wallet, optional last 4 digits, optional issuer, opening balance), edit it, archive and unarchive it, delete it with an Undo, and see active and archived accounts in separate sections. The currency is always INR. Each change is one audited, attributed write; a delete is a tombstone.
2. **Categories.** From More → Categories, a user sees the seeded defaults, switches between Expense and Income, creates top-level categories and sub-categories (never deeper), renames, recolours and re-icons them (symbol or one emoji), moves them up and down among their siblings, archives (hiding children from pickers) and deletes with an Undo. The system "Loan EMI" category can be renamed but never archived or deleted.
3. **Tags.** From More → Tags, a user creates, renames, archives and deletes tags; names are unique ignoring case.
4. **Defaults.** A fresh first-run setup creates the user, device, current-device setting **and** the documented default categories in one transaction.
5. **Pickers** for slice C exist and are tested: `AccountPicker`, `CategoryPicker` (also used by the category form to choose a parent), `TagPicker`, all as `PftBottomSheet`s; none offers an archived or deleted entry.
6. **Schema.** The database migrates v1 → v2 (`account`) → v3 (`category`) → v4 (`tag`), each with a JVM migration test, and the instrumented test migrates an encrypted v1 file to the current version and finds `backups/pre-migration-v1.db`. The A4b backup orphan (`-wal` left behind when the `.db` copy fails) is fixed.
7. Nothing in this slice logs a name, an identifier or an amount; `./gradlew check` is green with ≥ 90% coverage on `core`; every documentation surface in §9 is updated.

**Out of scope:**
- Account balances (opening + Σ transactions) and the "account in use" / "category in use" delete refusals: they need transactions and their items (C1, C3); the category and tag checks will query `transaction_line` and `transaction_line_tag`. B1 deletes are plain tombstones, as M1a `:259` says.
- Moving a category to another parent, or changing its kind, after creation (D7).
- Drag-and-drop reordering (D8: Move up / Move down).
- The bottom `NavigationBar` (C4). B2 reaches More from a Home header button (D9).
- Tag colours or icons; item–tag links (C1 creates `transaction_line_tag`, which replaced the planned `transaction_tag` in the itemised plan).
- Account reordering, account icons beyond the per-type badge, multi-currency.
- Re-seeding defaults on installs set up before this slice (dev emulators only; see Q4).
- Cashew import mapping onto these entities (slice I).

## 2. Current state

**Core write path (reuse as is).**
- `AuditedWriter` (`core/ledger/src/main/kotlin/dev/imagh/pft/ledger/audit/AuditedWriter.kt:37-218`): `create(repo, id, source) { meta -> entity }`, `update(repo, id) { old -> new }`, `delete`, `restore`, `archive`, `unarchive`, all idempotent and audited; an update that changes nothing writes nothing (`:88-93`). It stamps the meta itself, so services only state their rules.
- `LedgerEntity<Id, Self>` (`entity/LedgerEntity.kt:14-22`), `EntityMeta` with `isDeleted`/`isArchived`/`isActive` (`entity/EntityMeta.kt:23-40`), `EntityRepository<T, Id>` with `entityType`, `get`, `getLive`, `insert`, `update` (`entity/EntityRepository.kt:12-31`).
- `Validation.requireName` / `requireAmount` / `requireLive` (`validation/Validation.kt:21-61`); `LedgerLimits.MAX_NAME_LENGTH = 100`, `MAX_AMOUNT` (`validation/LedgerLimits.kt:13,28`). `requireAmount` rejects zero and negatives, so it **cannot** validate a signed opening balance (D4).
- `LedgerError` is sealed and its messages carry no user content (`validation/LedgerError.kt:16-69`). "Slices add their own cases here" (`:9-10`).
- `NameBasedId.of(kind, vararg parts)` (UUIDv5, output pinned by tests; M1a `:127`) for deterministic default-category ids (D6).
- Typed ids: only `UserId`, `DeviceId`, `AuditEntryId` exist (`ids/Ids.kt:17-44`). "Each slice adds its own" (M1a `:128`).
- `IdentityService.initialise` writes user, device, setting and three audit rows in one `inTransaction` (`identity/IdentityService.kt:73-85`). `AuditedWriter` needs `IdentityService` (`AuditedWriter.kt:39`), so `IdentityService` cannot call a category service without a construction cycle (D6).

**Core test fixtures (reuse).** `InMemoryEntityRepository(ledger, entityType)` runs `AuditFieldsCheck` on every write (`core/ledger/src/testFixtures/.../fakes/InMemoryEntityRepository.kt:19-50`); `InMemoryLedgerStore` is the rolling-back fake runner (`InMemoryLedgerStore.kt:17-57`); `LedgerTestKit` wires everything and says "later slices … add their fake repository with `kit.store.newStore(...)`" (`LedgerTestKit.kt:11-17`); `shouldHaveAudited`; `TestClock`.

**Room (reuse).**
- `PftDatabase` v1 with four entities (`app/src/main/kotlin/dev/imagh/pft/app/data/PftDatabase.kt:28-41`); `Migrations.ALL` is empty (`data/Migrations.kt:16`).
- `MetaColumns` (`data/common/MetaColumns.kt:15-30`, entities declare `@Entity(primaryKeys = ["id"])` themselves), `MetaMapper`, `BaseDao<E>` (`insert`, `update(): Int`, overridable `byId`, `data/common/BaseDao.kt:12-23`), `EntityMapper<T, E>`, `RoomEntityRepository(dao, entityType, mapper)` (`data/common/RoomEntityRepository.kt:17-37`), `EpochMillis`.
- `AppServices` is the production object graph (`di/AppServices.kt:32-60`); `RoomTestKit` delegates to it (`app/src/test/.../testing/RoomTestKit.kt:25-68`).

**App test support (reuse and extend).**
- `AuditedTables.all` registry, "every slice that adds an entity table adds a line here" (`testing/AuditedTables.kt:27-46`); `RoomAuditColumnsTest` pins the allowlist to `{"app_setting": {"key"}}` (`data/RoomAuditColumnsTest.kt:65-70`). M1a `:125` names `account.currency` as a planned allowlist entry.
- `MigrationTestSupport.createV1` / `migrateTo` (`testing/MigrationTestSupport.kt:44-71`) only builds a **v1** database; `MigrationTest` checks that every step has one migration (`src/testDebug/.../data/MigrationTest.kt:46-51`). Schemas are debug assets and migration tests live in `src/testDebug` (M1a `:137`).
- `SampleThing`/`SampleDatabase` exist only so the shared blocks had a real entity before B1 (M1a `:135`).
- `HostedComposeRule`, `awaitMain`, `FlowTestSupport`, `SourceScan`.

**Backup.** `PreMigrationBackup.backUpIfOlder` copies `-wal` first, then `.db` (`data/PreMigrationBackup.kt:85-89`); if the `.db` copy throws, the `pre-migration-vN.db-wal` copy stays, and `prune()` only lists `.db` names (`:166-182`), so the orphan is never removed (the M1a `:261` follow-up).

**UI (reuse).**
- `EditFormScaffold` (`ui/common/EditFormScaffold.kt:34-56`), `FieldState`/`FieldCheck` (`FieldState.kt:16-52`), `FieldError { Required, TooLong }` ("B1 and later slices add their own cases", `FieldError.kt:7-17`), `LedgerError.toFieldError()` with an exhaustive `when` and no `else` (`LedgerErrorMapping.kt:13-28`): **every new `LedgerError` case breaks the app's compilation until it is mapped**, so a core PR that adds one must update this file in the same PR.
- `NameField`, `EmptyState(message, glyph, actionLabel, onAction)`, `GroupedSection`, `HeaderIconButton`, `MoneyText`, `StatusChip`.
- `CollapsingHeaderScaffold(... bottomBar, floatingActionButton, content)` wraps M3 `Scaffold` but has **no snackbar slot** (`CollapsingHeaderScaffold.kt:191-202`), which `UndoDeleteSnackbar` needs.
- `CategoryGlyph(source, name, colourKey, size, onClick)`, the only way a category/account badge is drawn; its KDoc names "the account type badge (B2)" as a consumer. `GlyphSource.fromIconKey(iconKey, name)` already parses the `sym:` / `emoji:` grammar (`GlyphSource.kt:66-69`, `isSymbolKey` `:115-117`, `isEmojiValue` `:127-140`). Core's `CategoryIcon` (B3) must enforce the same grammar at write time (design plan `:671-672`), so the grammar moves to core and the app parser delegates to it (D5), rather than living in two copies.
- `CategoryPalette.keys`: the 12 permanent colour names, `slate` the fallback (`ui/theme/CategoryPalette.kt:37-55`). `PftSymbol` has the category symbols the defaults need (`ui/theme/PftSymbol.kt:62-138`).
- Navigation: string routes `SETUP`, `HOME`, "routes carry no arguments yet" (`ui/nav/Routes.kt:3-15`); `PftNavHost` passes `actions = { DevHeaderActions(...) }` to Home (`ui/nav/PftNavHost.kt:57-59`). Home's placeholder says accounts and transactions "arrive in the next update" (`ui/home/HomeScreen.kt:39`, `R.string.home_placeholder`).
- `SetupViewModel` calls `identity.initialise` and has the save shape every form should copy (catch `AlreadyInitialised`, rethrow cancellation, broad catch → `saveFailed` + field-less `LogEvent`) (`ui/setup/SetupViewModel.kt:104-124`; learnings 2026-11-02 "How to apply").

**Conflicts with the approved text to flag.**
- M1a §4 puts `tag` in v3 with `category`, and its B5 says "the tag table already exists at v3". Room validates a migrated schema against `@Entity` classes, so a v3 `tag` table means B3 must ship a `Tag` model, entity, DAO and audit registration two PRs before tags are built. This plan gives `tag` its **own** migration v4 in B5 (D3), which shifts C1 to v5. Since #52, three merged docs assume tag in v3 and C1 in v4: the amended M1a §4 table, `data-model.md`'s version table, and the itemised plan (§4 rows, C1c `migrate3To4`). B-final renumbers them (§9). Owner question Q1.
- M1a §4 specifies `currency CHECK = 'INR'` and `tag.name UNIQUE NOCASE among live rows`. **Room cannot declare a CHECK constraint or a partial (`WHERE`) index on an `@Entity`**, so either would exist only in migrated databases and not in fresh installs (Room creates fresh tables from the entity). This plan holds both invariants in code instead (D4, D10). The same trap applies to C1: the itemised plan §4 specifies `transaction_line.position CHECK > 0` and `amount CHECK > 0`, and M1a §4 `ledger_transaction.amount CHECK > 0`. Noted for C1 in §10.
- M1a B2 says "bottom-nav 'More' entry", but A5 moved the `NavigationBar` to C4 (A5 plan §1 out of scope). B2 adds a More screen reached from Home's header (D9); C4 moves it into the nav bar.
- M1a B5 names a `DuplicateTag` error. This plan uses one `LedgerError.DuplicateName(what)` for tags and sibling categories (D10).

## 3. Design & decision

### 3.1 Layers

```
ui/more/MoreScreen ── Accounts / Categories / Tags rows
ui/accounts/   AccountsScreen + AccountsViewModel        AccountEditScreen + AccountEditViewModel
ui/categories/ CategoriesScreen + CategoriesViewModel    CategoryEditScreen + CategoryEditViewModel
ui/tags/       TagsScreen + TagsViewModel                TagEditScreen + TagEditViewModel
      │ (ViewModel → core service only)
core/ledger  account/AccountService   category/CategoryService   tag/TagService   setup/FirstRunService
      │ (AuditedWriter + the entity's repository interface)
app/data     account/ category/ tag/  RoomAccountRepository …  (RoomEntityRepository + own queries)
      │
pft.db       account (v2)  category (v3)  tag (v4)
```

Each entity composes the A3/A4a blocks (`EntityMeta`, `LedgerEntity`, `AuditedWriter`, `EntityRepository`/`RoomEntityRepository`, `MetaColumns`, `BaseDao`) and the A5d form blocks, so each step contains only that entity's rules (M1a `:119`).

### 3.2 Key decisions

**D1 — Three concrete services, no generic CRUD.** `AccountService`, `CategoryService`, `TagService` are concrete (M1a §3.4: "There is no generic `CrudService<T>`"). Each takes `AuditedWriter`, its repository and `Uuid7`. The only interfaces added are the three repository interfaces (`AccountRepository`, `CategoryRepository`, `TagRepository`, each `: EntityRepository<T, Id>` plus its own queries), which are the mandated core↔app persistence boundary (CLAUDE.md §3.1). No other abstraction.

**D2 — Lists are read on demand; no `Flow` in core yet.** Services return snapshots (`suspend fun lists(): AccountLists`). A list ViewModel loads in `init`, after each of its own actions, and when its screen resumes (`LifecycleResumeEffect`, lifecycle-runtime-compose 2.8.7, already a dependency), which covers returning from the edit screen. These entities change only through this UI, so nothing needs to observe the database. This keeps M1a's decision that kotlinx-coroutines enters core "with the first `Flow`-returning query (slice C)" (M1a `:130`), and adds no dependency. Owner question Q2.

**D3 — One migration per entity: v2 `account` (B1c), v3 `category` (B3b), v4 `tag` (B5a).** Each PR then carries its own entity, migration, schema JSON, migration test and audit registration, and nothing ships a table before its model exists. Cost: C1 becomes v5 (and the later versions shift by one). B-final renumbers the merged docs that assume C1 = v4: the M1a §4 version table, `data-model.md`'s version table, and the itemised plan's §4 rows and C1c `migrate3To4` (which becomes `migrate4To5`, from "v4 with a category and a tag"). Owner question Q1.

**D4 — Account.**
- **Model** `Account(meta, name, type: AccountType, openingBalance: Paise, maskedIdentifier: String?, issuer: String?)`; `AccountType { BANK, CREDIT_CARD, CASH, WALLET }`. `auditFields()`: `name`, `type`, `opening_balance`, `masked_identifier`, `issuer`.
- **Currency** is not on the model. The `account.currency` column exists (L-4, future multi-currency) and the mapper always writes `"INR"`; a row with any other value **fails closed on read** (`IllegalStateException`, message names the column only), because only this mapper writes the table in M1a and a different value means corruption or an unsupported future sync. The column is in the reviewed non-audited allowlist (M1a `:125` planned exactly this).
- **Opening balance** is signed `Paise` (M1a §4, decision 5: balance = opening + Σ IN − Σ OUT). Core accepts any value with `|x| ≤ LedgerLimits.MAX_AMOUNT` via a new `Validation.requireBalance(paise)` (zero allowed; `AmountTooLarge` beyond). The UI decides the sign from the type (Q3): Bank, Cash, Wallet ask "Current balance" (stored as is); Credit card asks "Amount owed" (stored negated). Core does not tie the sign to the type, so an overdrawn bank account or an imported card in credit stays representable.
- **Last 4 digits** (`masked_identifier`): blank becomes null; otherwise exactly four ASCII digits `0-9` (not `Char.isDigit()`, which accepts Devanagari and other digits) → else `LedgerError.InvalidMaskedIdentifier`. Exposed as `AccountRules.requireMaskedIdentifier(text): String?` so the ViewModel validates the field with core's rule (A5 D5 pattern).
- **Issuer**: blank becomes null; otherwise `requireName(text, MAX_NAME_LENGTH)`. Exposed as `Validation.requireOptionalName(text, max): String?` (also used by nothing else yet, but it is the optional twin of `requireName`, so it lives beside it).
- **Names are not unique** across accounts (two cards from one bank are normal; the last 4 digits tell them apart).
- **Queries**: `AccountRepository.allLive(): List<Account>` (not tombstoned). The service sorts and splits: `active()` (live, not archived, by name ignoring case), `all()` (live, archived included), `lists(): AccountLists(active, archived)`.
- **Delete** is a tombstone with `restore`; no in-use check until C1 (M1a `:259`; kept by the itemised plan's C1a as `AccountInUse`).

**D5 — Category icon and colour live in core; the app parser delegates.**
- `enum class CategoryColour(val key: String)` with the 12 permanent names (`rose` … `slate`); `fromStored(text)` maps unknown or null to `SLATE` (design plan §4: fail-safe on read). A B3a test pins the 12 names; a B4a app test asserts `CategoryColour.entries.map { it.key } == CategoryPalette.keys`, so the two lists cannot drift.
- `sealed interface CategoryIcon { Symbol(key); Emoji(text) }` with `toStored()` (`sym:<key>` / `emoji:<text>`) and `parse(stored): CategoryIcon?`. The grammar (`[a-z0-9_]{1,40}` keys; emoji 1–16 UTF-16 units, well-formed, no whitespace/control/invisible format characters except ZWJ and tag characters) **moves from `GlyphSource.kt:115-140` into core** (`category/CategoryIcon.kt`), and `GlyphSource.fromIconKey` calls `CategoryIcon.parse` (B4a refactor, `GlyphSourceTest` stays green). One grammar, two consumers, no copy (CLAUDE.md §3.4). Whether a symbol key *exists* stays the app's job (`PftSymbol.fromKey`); whether an emoji *renders* stays `EmojiInputValidator`'s (B4c).
- The icon is optional (`null` → letter fallback).

**D6 — Default categories are seeded in the first-run transaction, with deterministic ids.**
- `DefaultCategories.LIST` (core, `category/DefaultCategories.kt`): the table in §4, each entry with a stable `seedKey`, kind, symbol key, colour, optional parent `seedKey`, and `systemKey` for `LOAN_EMI`.
- `CategoryService.seedDefaults()` creates each entry with id `CategoryId(NameBasedId.of("default_category", seedKey))` and `AuditSource.SYSTEM`, skipping any id that already exists, **tombstoned included** (so a default the user deleted is not resurrected). Re-running it is a no-op. Deterministic ids also mean two phones seed the *same* ids, so M2 sync will not see two "Food" categories.
- **Where it runs:** a new concrete `setup/FirstRunService(runner, identity, categories)` with `initialise(userName, deviceLabel) = runner.inTransaction { identity.initialise(...); categories.seedDefaults() }`. Both run in one transaction (nesting is supported by both runners), so a failure leaves no user without categories. `IdentityService` stays unaware of categories, which avoids the `AuditedWriter ↔ IdentityService` construction cycle. `SetupViewModel` switches from `services.identity.initialise` to `services.firstRun.initialise` (B3b). This is a coordinating service with one caller, not an abstraction.

**D7 — Category rules.**
- Two levels: a sub-category's parent must be a live, **top-level** category (`requireLive`; parent with a parent → `DepthExceeded`), and of the **same kind** (`KindMismatch`).
- **Kind and parent are fixed at creation.** The edit command carries name, colour and icon only. Changing kind would invalidate C's expense/income links, and moving between parents is not in L-2. (Delete and re-create instead; same stance as M1a decision 6 for transfers.)
- `sortOrder`: a new category goes last among its live siblings (`max + 1`). `reorder(parentId: CategoryId?, kind, orderedIds)` requires `orderedIds` to be exactly the live sibling ids (archived included) of that parent and kind, else `InvalidOrder`; it rewrites `sort_order` through `AuditedWriter.update`, so each moved row gets an audited UPDATE and unmoved rows write nothing.
- **Archive**: archived categories, and the children of an archived parent, are absent from `pickable(kind)`. Unarchiving the parent brings its non-archived children back.
- **Delete**: refused with `HasChildren` while the category has live children (the user deletes the children first; there is no cascade). `restore` of a sub-category whose parent is tombstoned → `Deleted("category")`. C1 adds "in use by a live transaction item" to delete (a query on `transaction_line.category_id`).
- **System category** (`system_key = LOAN_EMI`): `archive` and `delete` → `SystemCategoryProtected`; rename/recolour/re-icon allowed (M1a `:268`).
- **Names**: `requireName(MAX_NAME_LENGTH)`; unique among live siblings (same parent and kind), ignoring case → `DuplicateName("category")` (D10, Q5).
- **Queries** on `CategoryRepository`: `allLive(): List<Category>` (the whole tree is small: tens of rows), `bySystemKey(key)`. The tree, sorting, pickability and sibling checks are core functions over that list (`CategoryTree`), so they are unit-tested without Room.

**D8 — Reorder is "Move up / Move down" in a row menu.** No drag-and-drop gesture code or library. Accessible by default, testable with plain clicks, and enough for tens of categories. A drag handle can come later without a core change (`reorder` already takes the full order).

**D9 — More screen as the B2 entry point.** `ui/more/MoreScreen` (`Routes.MORE`) is a `CollapsingHeaderScaffold` with one `GroupedSection`: Accounts, Categories, Tags (rows added as each screen lands). Home gets a `HeaderIconButton(PftSymbol.OVERFLOW, "More")` (header actions are secondary navigation, allowed by the header rule). C4 replaces the header button with the `NavigationBar` "More" tab and reuses the same screen. Home's placeholder copy changes to point at More.

**D10 — Name uniqueness is enforced in core, inside the write transaction.** Tags: unique among live (not tombstoned, archived included) tags; categories: among live siblings. Comparison is `trim()` then `lowercase(Locale.ROOT)` (Unicode-aware, unlike SQLite `NOCASE`, which folds ASCII only). Check and insert run in one `runner.inTransaction`, and Room's transactions serialise writers, so two saves cannot both pass. Restoring or unarchiving does not bypass it: `restore` re-checks and refuses `DuplicateName`. No partial unique index (see the §2 conflicts).

**D11 — Lists: one shared screen, row actions in a menu, Undo in a snackbar.**
- `ui/common/EntityListScreen` (M1a §3.4, built in B2a): `CollapsingHeaderScaffold(Standard)` with Back, a FAB (`PftSymbol.ADD`, labelled), an optional header item slot (the categories kind switch), the **active** rows in a `GroupedSection`, a collapsible **"Archived (n)"** section, `EmptyState` when there are no rows at all, and a snackbar host. A row is `EntityRow(key, title, supporting, glyph, indent, onClick, actions: List<RowAction>)`; `actions` render as a trailing overflow `IconButton` + `DropdownMenu` (Archive, Unarchive, Delete, Move up, Move down). Consumers: accounts, categories, tags in this slice; loans and goals later (M1a `:115`).
- **`CollapsingHeaderScaffold` gains a `snackbarHost` slot** (passes through to the M3 `Scaffold`); default empty, so no existing screen or golden changes.
- `UndoDeleteSnackbar`: shows "<Kind> deleted" with **Undo**; Undo calls the ViewModel's `restore(id)`. The list ViewModel owns delete and undo, so no result has to travel across navigation.
- Edit screens edit fields only (Save in the bottom bar). Archive and delete live on the list row.

**D12 — Forms follow the A5 save shape; extract it once there are two copies.** `AccountEditViewModel` (B2b) copies `SetupViewModel`'s shape: per-field `FieldState.check` with core's rules, a `saving` guard, `LedgerError` → field errors (`toFieldError()`) or a form-level message, cancellation rethrown, any other exception → `saveFailed` + `LogEvent.EntitySaveFailed(kind)`. When B4b writes the second copy, the Refactor step extracts the common part into one plain function in `ui/common` (`launchSave`, no base class) if the two copies are the same; otherwise it stays concrete. No speculative helper in B2b.

**D13 — Navigation routes with an id.** `Routes`: `MORE`, `ACCOUNTS`, `ACCOUNT_EDIT = "accounts/edit?id={id}"` (no id = create), and the same for categories (`?id={id}&parent={parent}&kind={kind}`) and tags. Arguments are ids and enum names only (never names or amounts). The ViewModel treats them as untrusted: a malformed or unknown id shows the list again with a "no longer exists" snackbar, never a crash.

**D14 — Logging.** Events are added by the PR that first emits them (A5a rule): `LogEvent.EntitySaveFailed(kind: EntityKind)` and `LogEvent.EntityActionFailed(kind: EntityKind, action: EntityAction)` with enums only (`EntityKind { ACCOUNT, CATEGORY, TAG }`, `EntityAction { ARCHIVE, UNARCHIVE, DELETE, RESTORE, REORDER }`). Enum entries are added as each kind arrives. Never a name, an id, last-4 digits or an amount.

**D15 — `SampleThing` stays.** `RoomEntityRepositoryTest` and `RoomAuditedWriterIntegrationTest` test the shared blocks generically through it; rewriting them on `Account` would couple shared-block tests to one entity's rules. M1a `:135` allows keeping it.

### 3.3 Abstraction check

Added interfaces: `AccountRepository`, `CategoryRepository`, `TagRepository` (mandated persistence boundary). Added shared pieces, each with consumers named here: `EntityListScreen`/`EntityRow`/`RowAction` (3 consumers in this slice), `UndoDeleteSnackbar` (3), `AmountField` (account opening balance; C4 amount, C4b item amounts, D budget limit, F loan EMI), `PftBottomSheet` (3 pickers + C4 date), `AccountPicker`/`CategoryPicker`/`TagPicker` (C4, D, E; `CategoryPicker` and `TagPicker` also per item in C4b; `CategoryPicker` also the B4b parent field), `CategoryIcon` grammar (core writes, app reads), the `snackbarHost` slot (lists, C4), `FieldError.message()` (`NameField`, `AmountField`). Rejected: a generic list ViewModel, a generic edit ViewModel, a `CrudService`, a `Flow` layer, a picker base class.

## 4. Data model & schema

| Version (PR) | Table | Columns (all with `MetaColumns`: `id` PK, `created_at`, `updated_at`, `created_by_user_id`, `created_on_device_id`, `deleted_at`, `archived_at`) | Indexes / notes |
|---|---|---|---|
| v2 (B1c) | `account` | `name TEXT NOT NULL`, `type TEXT NOT NULL` (enum name), `currency TEXT NOT NULL` (always `INR`, non-audited allowlist), `opening_balance INTEGER NOT NULL` (signed paise), `masked_identifier TEXT NULL`, `issuer TEXT NULL` | none needed (tens of rows) |
| v3 (B3b) | `category` | `parent_id TEXT NULL` FK → `category(id)`, `kind TEXT NOT NULL`, `name TEXT NOT NULL`, `colour TEXT NOT NULL`, `icon_key TEXT NULL`, `sort_order INTEGER NOT NULL`, `system_key TEXT NULL` | index `(parent_id)` (Room requires one for the FK); unique index `(system_key)` (SQLite allows many NULLs) |
| v4 (B5a) | `tag` | `name TEXT NOT NULL` | none |

- Every column has an explicit snake_case `@ColumnInfo(name = ...)` (the audit-columns check relies on it, M1a `:125`).
- Each migration is SQL copied from Room's generated `CREATE TABLE` in the exported schema JSON (so the migrated and fresh tables are identical; `runMigrationsAndValidate` proves it). No CHECK constraints and no partial indexes (§2 conflict).
- **Foreign key enforcement [Verify in B3b]:** a test inserts a `category` row whose `parent_id` does not exist through the DAO. If Room has `PRAGMA foreign_keys` on, it must fail; if it is off, the test pins that fact and the core parent check is the guard (recorded in learnings and `data-model.md`). No `onDelete` action: rows are never hard-deleted.
- **Audit registration** (`AuditedTables.all`): `account` with `nonAudited = setOf("currency")`; `category` and `tag` with none. `RoomAuditColumnsTest`'s pinned allowlist becomes `{"app_setting": {"key"}, "account": {"currency"}}`.
- **Canonical schema:** the core models are canonical; Room entities are one-to-one DTOs mapped by an `EntityMapper` object each.

**Default categories (proposed; Q4).** Symbols are existing `PftSymbol` keys; colours are `CategoryPalette` keys. `seedKey` is permanent (it feeds the id).

| seedKey | Kind | Name | Parent | Symbol | Colour |
|---|---|---|---|---|---|
| `food` | EXPENSE | Food & dining | — | `restaurant` | coral |
| `food_groceries` | EXPENSE | Groceries | food | `grocery` | coral |
| `food_eating_out` | EXPENSE | Eating out | food | `local_cafe` | coral |
| `food_delivery` | EXPENSE | Food delivery | food | `fastfood` | coral |
| `transport` | EXPENSE | Transport | — | `directions_car` | sky |
| `transport_fuel` | EXPENSE | Fuel | transport | `local_gas_station` | sky |
| `transport_cab` | EXPENSE | Cab & auto | transport | `local_taxi` | sky |
| `transport_public` | EXPENSE | Public transport | transport | `train` | sky |
| `shopping` | EXPENSE | Shopping | — | `shopping_bag` | magenta |
| `bills` | EXPENSE | Bills & utilities | — | `receipt_long` | amber |
| `bills_electricity` | EXPENSE | Electricity | bills | `bolt` | amber |
| `bills_water` | EXPENSE | Water | bills | `water_drop` | amber |
| `bills_internet_mobile` | EXPENSE | Internet & mobile | bills | `wifi` | amber |
| `bills_subscriptions` | EXPENSE | Subscriptions | bills | `subscriptions` | amber |
| `home` | EXPENSE | Home | — | `home` | olive |
| `home_rent` | EXPENSE | Rent | home | `bed` | olive |
| `home_maintenance` | EXPENSE | Maintenance & repairs | home | `handyman` | olive |
| `home_help` | EXPENSE | Household help | home | `cleaning_services` | olive |
| `health` | EXPENSE | Health | — | `local_hospital` | rose |
| `health_medicines` | EXPENSE | Medicines | health | `medication` | rose |
| `health_fitness` | EXPENSE | Fitness | health | `fitness_center` | rose |
| `entertainment` | EXPENSE | Entertainment | — | `movie` | violet |
| `travel` | EXPENSE | Travel | — | `flight` | teal |
| `education` | EXPENSE | Education | — | `school` | indigo |
| `personal_care` | EXPENSE | Personal care | — | `spa` | emerald |
| `gifts` | EXPENSE | Gifts & donations | — | `redeem` | magenta |
| `loan_emi` | EXPENSE | Loan EMI (`system_key = LOAN_EMI`) | — | `account_balance` | slate |
| `income_salary` | INCOME | Salary | — | `work` | emerald |
| `income_interest` | INCOME | Interest | — | `savings` | teal |
| `income_refunds` | INCOME | Refunds & cashback | — | `payments` | sky |
| `income_other` | INCOME | Other income | — | `trending_up` | slate |

Account type badges (B2, a `CategoryGlyph` with a symbol, colour fixed per type): Bank `account_balance`/blue, Credit card `credit_card`/violet, Cash `payments`/emerald, Wallet `account_balance_wallet`/teal.

## 5. Flows

**Create an account.** More → Accounts → FAB → `AccountEditScreen` → `AccountEditViewModel.submit()` checks each field with core's rules (`Validation.requireName`, `AccountRules.requireMaskedIdentifier`, `Validation.requireOptionalName`, `AmountField`'s `PaiseParser.parse` then `Validation.requireBalance` with the sign from the type) → `AccountService.create(AccountDraft)` validates again → `AuditedWriter.create(accountRepo, AccountId(ids.next()))` → `runner.inTransaction { RoomAccountRepository.insert; AuditRecorder.record(CREATE) }` → Room `withTransaction` → SQLCipher `pft.db`. Back on the list, `LifecycleResumeEffect` reloads `AccountService.lists()`.

**Delete with Undo.** Row menu → Delete → `AccountsViewModel.delete(id)` → `AccountService.delete` → `AuditedWriter.delete` (tombstone + DELETE audit) → the ViewModel reloads and shows `UndoDeleteSnackbar`; Undo → `AccountService.restore` (RESTORE audit). A failure shows "Couldn't delete. Try again." and logs `EntityActionFailed(ACCOUNT, DELETE)`.

**First run with defaults.** Setup → `SetupViewModel.submit()` → `FirstRunService.initialise(name, label)` → `runner.inTransaction { IdentityService.initialise(...) /* user, device, setting, 3 audits */; CategoryService.seedDefaults() /* 31 categories, 31 audits, source SYSTEM, attributed to the new identity */ }` → Home.

**Reorder a category.** Categories (Expense) → row menu → Move up → `CategoriesViewModel.moveUp(id)` computes the sibling order from its state → `CategoryService.reorder(parentId, kind, newOrder)` → `CategoryTree` validates the set → `AuditedWriter.update` per row whose `sort_order` changed (two rows for a swap), one transaction.

**Pick a category (C4, tested in B4b).** `CategoryPicker(kind, onPick)` → `PftBottomSheet` → `CategoryService.pickable(kind)` (live, not archived, parent not archived; top-level then children in `sort_order`) → grid of `CategoryGlyph`s.

**Upgrade.** App start → `DatabaseFactory.open()` → key → `PreMigrationBackup.backUpIfOlder()` copies the encrypted v1 file to `backups/pre-migration-v1.db` → `PftDatabase.create` runs `MIGRATION_1_2..3_4` → `recordOpened()`.

## 6. Implementation steps (TDD-ordered)

Conventions (unchanged from M1a §6): core tests `core/ledger/src/test/kotlin/dev/imagh/pft/ledger/<pkg>/…Test.kt` (Kotest FunSpec), fakes in `core/ledger/src/testFixtures/kotlin/dev/imagh/pft/ledger/fakes/`, app JVM tests under `app/src/test/kotlin/dev/imagh/pft/app/…` (Robolectric, `HostedComposeRule`, `awaitMain`), migration tests in `app/src/testDebug/…/data/MigrationTest.kt`, instrumented in `app/src/androidTest/…` on `pft35`. Every step ends with a green `./gradlew check`. New `*Samples.kt` files are added to `verifyReleaseShrink`'s `mustBeRemoved` (learnings 2026-10-30). Each PR re-measures the release APK when it makes new screens reachable.

### Step B1a — Backup orphan fix (A4b follow-up)
- **Red:** `app/src/test/.../data/PreMigrationBackupTest`: `a failed database copy leaves no log copy behind` — a `-wal` with content, and a `.db` copy that fails (a directory where the temp file must go, or a read-only backup dir after the log is written); assert `backUpIfOlder()` throws `PreMigrationBackupFailed` **and** `backups/` holds neither `pre-migration-v1.db` nor `pre-migration-v1.db-wal`. A second test: `prune removes an orphaned log copy` — seed `pre-migration-v0.db-wal` with no `.db`, run a successful backup, assert the orphan is gone.
- **Green:** in `PreMigrationBackup.backUpIfOlder`, delete `walCopy` when the `.db` copy fails; in `prune()`, also delete `*.db-wal` files whose `.db` is absent.
- **Refactor:** none. KDoc "Consistency (WAL)" paragraph updated.

### Step B1b — Accounts core
- **Red:** `account/AccountServiceTest` (with `LedgerTestKit`):
  - `create` stores name (trimmed), type, opening balance (positive, zero and negative), last 4 and issuer; INR is implied; the result is audited CREATE with the current user and device (`shouldHaveAudited`).
  - Rejections, nothing written and nothing audited: blank or over-100 name (`BlankName`, `NameTooLong`); last 4 of `"123"`, `"12345"`, `"12a4"`, `"١٢٣٤"` (Arabic-Indic digits), `"१२३४"` (Devanagari) → `InvalidMaskedIdentifier`; issuer over 100 → `NameTooLong`; `|opening| > MAX_AMOUNT` → `AmountTooLarge`; before setup → `NotInitialised`.
  - Blank last 4 and blank issuer are stored as null.
  - `edit` audits an UPDATE listing only changed fields; an identical edit writes nothing.
  - `archive` removes it from `active()` and `lists().active`, keeps it in `all()` and `lists().archived`; `unarchive` reverses; both audited; repeating either is a no-op.
  - `delete` tombstones (absent from every list), `restore` brings it back, both audited; `edit` of a deleted account → `Deleted`; an unknown id → `NotFound`.
  - `active()` is sorted by name ignoring case.
  - A failure inside the transaction (an `AuditLog` that throws, via `kit.writerWith`) leaves no account.
  - `ValidationTest`: `requireBalance` accepts `-MAX`, `0`, `MAX` and rejects one paisa beyond either end; `requireOptionalName` returns null for blank and the trimmed text otherwise.
- **Green:** `core/ledger/src/main/kotlin/dev/imagh/pft/ledger/account/{Account,AccountType,AccountDraft,AccountLists,AccountRepository,AccountRules,AccountService}.kt`; `ids/Ids.kt` adds `AccountId`; `validation/LedgerError.kt` adds `InvalidMaskedIdentifier`; `validation/Validation.kt` adds `requireBalance`, `requireOptionalName`. Test fixtures: `fakes/InMemoryAccountRepository.kt` (extends `InMemoryEntityRepository`), `LedgerTestKit.accounts` (+ `accountRepository`). **App, same PR (compile):** `ui/common/LedgerErrorMapping.kt` maps `InvalidMaskedIdentifier` → new `FieldError.InvalidLastFour`, and `FieldError.kt` adds it. `NameField`'s private `errorMessage` is an exhaustive `when` over `FieldError` (`NameField.kt:59-63`), so it gains the branch and its string; because `AmountField` (B2b) needs the same rendering, promote it here to an internal `@Composable fun FieldError.message(): String` in `ui/common/FieldError.kt` (two consumers: `NameField`, `AmountField`), covered by a `FieldErrorMessageTest`.
- **Refactor:** `core/ledger/README.md` (public interface, behaviours, `LedgerLimits` reuse).

### Step B1c — Accounts Room, v2, encrypted migration
- **Red:**
  - `app/src/test/.../data/RoomAccountIntegrationTest` (`RoomTestKit`): create/edit/archive/delete/restore through `AccountService` over Room round-trip every field (negative balance, nulls), each with its audit entry read back by `trailOf("account", id)`; a failing `AuditLog` leaves no row (INT-4); `lists()` excludes tombstones.
  - `RoomAccountMapperTest`: a row with `currency = 'USD'` fails on read with a message that names the column and not the value.
  - `RoomAuditColumnsTest`: `account` registered in `AuditedTables` with a fully populated `Account`; the pinned allowlist now includes `"account" to setOf("currency")`. (Fails first because the table is unregistered.)
  - `MigrationTest.migrate1To2`: `createV1` seeds a user row; after `migrateTo(2, MIGRATION_1_2)` the `account` table exists with the expected columns, the user row is intact and the audit triggers survive (built into `migrateTo`). The step-count test now expects `[1 to 2]`.
  - Instrumented `app/src/androidTest/.../data/EncryptedMigrationTest`: build a **v1** database file keyed with the managed key (`MigrationTestHelper(instrumentation, PftDatabase::class.java, emptyList(), SupportOpenHelperFactory(<own copy of the key>))`; never the array `useKey` zero-fills, learnings 2026-10-30 item 2), write `pft_schema_version` = 1, insert a user row; then `DatabaseFactory.forApp(context).open()`; assert `backups/pre-migration-v1.db` exists and does not start with `"SQLite format 3\0"`, the database is at `PftDatabase.VERSION`, the user row survived and an account can be written. It targets `PftDatabase.VERSION`, so B3b and B5a re-run it without edits.
- **Green:** `app/src/main/kotlin/dev/imagh/pft/app/data/account/{AccountEntity,AccountDao,AccountMapper,RoomAccountRepository}.kt`; `PftDatabase` adds the entity, `accountDao()`, `VERSION = 2`; `Migrations.MIGRATION_1_2` + `ALL`; exported `app/schemas/.../2.json`; `AppServices.accountRepository` + `accounts` (`AccountService`); `RoomTestKit` exposes them.
- **Refactor:** none. Docs: `data-model.md` v2, `app/README.md` data section, learnings (the CHECK-constraint finding, the encrypted-migration recipe).

### Step B2a — More screen and the accounts list
- **Red:**
  - `ui/common/EntityListScreenTest`: active rows render in order; archived rows sit under a collapsed "Archived (n)" header that expands on tap; no rows at all shows the `EmptyState` message and its action; the FAB has the add label; each row's overflow menu lists exactly its `actions` and invokes the chosen one; an `indent` row is inset (bounds). Goldens via a `EntityListSamples.kt` (light/dark, empty, with archived).
  - `CollapsingHeaderScaffoldTest`: a snackbar shown through the new `snackbarHost` slot is displayed above the bottom inset; existing goldens unchanged (the slot defaults to nothing).
  - `ui/accounts/AccountsViewModelTest` (Room, `awaitMain`): loads seeded accounts split active/archived; `archive`, `unarchive`, `delete` change the lists and are audited; `delete` sets an undo offer and `undo()` restores; an `AuditLog` that throws on archive leaves the list unchanged, sets the "couldn't" message and logs `entity_action_failed kind=ACCOUNT action=ARCHIVE` (asserted via `LogEvent.line()`, not logcat).
  - `ui/nav/AccountsFlowTest` (through `PftRoot`/nav host, real in-memory Room): Home → More → Accounts shows the empty state; with a seeded "HDFC Savings" BANK account, archive moves it to Archived; delete shows the Undo snackbar and Undo brings it back; Back returns to More, then Home.
  - `HomeScreenTest`: the More header button exists with its TalkBack label; the placeholder copy is the new one.
  - `LogEventShapeTest` gains the two new events.
- **Green:** `ui/common/{EntityListScreen,EntityRow,RowAction,UndoDeleteSnackbar,EntityListSamples}.kt`; `CollapsingHeaderScaffold` `snackbarHost` parameter; `ui/more/MoreScreen.kt`; `ui/accounts/{AccountsScreen,AccountsViewModel,AccountRowMapper}.kt` (the type → badge symbol/colour mapping, D4); `ui/nav/Routes` `MORE`, `ACCOUNTS`; `PftNavHost` routes + Home's More action; `log/LogEvent` `EntitySaveFailed`, `EntityActionFailed`, `EntityKind`, `EntityAction`; strings.
- **Refactor:** none. Docs: `docs/features/accounts.md` (created: list, archive, delete/undo), `app/README.md` (`ui/common` additions, `ui/more`, `ui/accounts`), `code-structure.md`, design-system architecture doc (snackbar slot), release APK size in learnings.

### Step B2b — Account create and edit
- **Red:**
  - `ui/common/AmountFieldTest`: `"1,23,456.78"` gives `Paise(12345678)`; `"-5"`, `"1.234"`, `"abc"` show `FieldError.InvalidAmount` and give null; the text is never altered; the field uses a decimal keyboard.
  - `ui/accounts/AccountEditViewModelTest` (Room): a valid create saves once (double submit → one row, one audit; the guard test edits a field to invalid between submits, as A5e learned); every invalid field shows its own error at once (name, last 4, issuer, amount) and nothing is saved; Credit card + "5,000" stores `-500000`, Bank + "5,000" stores `500000`, and switching the type after typing flips the stored sign, not the text; edit mode prefills from the stored account (a negative card balance shows "5,000.00" under "Amount owed"); a no-op edit writes no audit row; an unknown or deleted id ends in `notFound` state; an `AuditLog` failure keeps the text, resets `saving`, sets `saveFailed`, logs `entity_save_failed kind=ACCOUNT`.
  - `AccountsFlowTest` extends: FAB → fill "HDFC Savings", Bank, "1234", "HDFC", "10,000" → Save → the list shows it; an invalid last 4 shows the error under that field and stays on the form; tap the row → edit → rename → the list shows the new name.
  - Screenshot goldens for the form (empty, with errors, credit card).
- **Green:** `ui/common/AmountField.kt` (wraps `PaiseParser.parse`; magnitude only); `FieldError.InvalidAmount`; `ui/accounts/{AccountEditScreen,AccountEditViewModel}.kt` on `EditFormScaffold` + `NameField` + M3 segmented buttons for the type; `Routes.ACCOUNT_EDIT` with the optional `id` argument.
- **Refactor:** none (the save-shape extraction waits for B4b, D12). Docs: `accounts.md` (create, edit, the "amount owed" rule, last 4 rules), learnings.

### Step B2c — `PftBottomSheet` and `AccountPicker`
- **Red:** `ui/common/AccountPickerTest`: with three seeded accounts (one archived, one deleted) the sheet lists only the active one plus the third, sorted by name, each with its type badge; tapping one calls `onPick(id)` and dismisses; the current selection is marked (semantics `selected`); no accounts shows an empty state. `PftBottomSheetTest`: Back dismisses; the sheet uses the theme's sheet shape.
- **Green:** `ui/common/PftBottomSheet.kt` (the one `@OptIn(ExperimentalMaterial3Api)` for `ModalBottomSheet`, design plan `:131`), `ui/common/AccountPicker.kt` (stateless: takes the `List<Account>`; the consumer's ViewModel loads `AccountService.active()`; no ViewModel inside a shared component).
- **Refactor:** none. Docs: `app/README.md` `ui/common`, `code-structure.md`, debug catalogue page for the sheet (optional).

### Step B3a — Categories core
- **Red:**
  - `category/CategoryIconTest`: `parse("sym:grocery")` → `Symbol("grocery")`; `parse("emoji:🛒")` → `Emoji`; rejects (null) `"sym:Grocery"`, `"sym:"`, 41-char keys, `"emoji:"`, 17-unit emoji, `"emoji:a b"`, a zero-width space, a bidi override, a lone surrogate (built from code points, learnings DS3 item 2); accepts a ZWJ family and the England subdivision flag; `toStored` round-trips. (These are the cases `GlyphSourceTest` already pins, moved.)
  - `category/CategoryColourTest`: the 12 keys in order, permanent; `fromStored("teal")`, `fromStored("TEAL")` → `SLATE`, `fromStored(null)` → `SLATE`.
  - `category/CategoryServiceTest`:
    - create top-level and sub-category; sub of a sub → `DepthExceeded`; income sub under an expense parent → `KindMismatch`; under an archived parent → `Archived`, under a deleted one → `Deleted`;
    - name rules and `DuplicateName` among live siblings ignoring case ("Groceries" vs "groceries" under Food), while the same name under another parent or kind is fine, and a tombstoned sibling does not block;
    - a stored icon that fails the grammar → `InvalidIcon`;
    - `edit` (rename, recolour, re-icon, clear icon) audits only the changed fields;
    - new categories go last; `reorder` with the exact sibling set rewrites `sort_order` with one UPDATE per moved row and none for unmoved rows; a missing, extra or foreign id → `InvalidOrder`, nothing written;
    - `pickable(kind)` excludes archived categories and the children of an archived parent, and orders top-level then children by `sort_order`; unarchiving the parent restores the children;
    - `delete` of a parent with a live child → `HasChildren`; after the child is deleted, it succeeds; `restore` of a child whose parent is deleted → `Deleted`; `restore` into a sibling-name clash → `DuplicateName`;
    - the `LOAN_EMI` category: rename works; archive and delete → `SystemCategoryProtected`.
  - `category/DefaultCategoriesTest`: `seedDefaults()` creates exactly the §4 list (names, kinds, parents, icons, colours, one `LOAN_EMI`), with ids `NameBasedId.of("default_category", seedKey)`, audited with source SYSTEM; a second call creates nothing and audits nothing; a deleted default is not re-created; the seed keys are unique and every parent key exists.
  - `setup/FirstRunServiceTest`: `initialise` creates the identity and the defaults; an `AuditLog` that fails on the 10th append leaves no user, no device, no setting and no category (one transaction); a second call → `AlreadyInitialised` and seeds nothing.
- **Green:** `core/ledger/.../category/{Category,CategoryKind,CategoryColour,CategoryIcon,SystemCategory,CategoryDraft,CategoryTree,CategoryRepository,CategoryService,DefaultCategories}.kt`; `ids` `CategoryId`; `LedgerError` `DepthExceeded`, `KindMismatch`, `HasChildren`, `InvalidOrder`, `InvalidIcon`, `DuplicateName(what)`, `SystemCategoryProtected`; `setup/FirstRunService.kt`; fakes `InMemoryCategoryRepository`, `LedgerTestKit.categories` + `firstRun`. **App, same PR (compile):** `LedgerErrorMapping` maps `DuplicateName` → `FieldError.Duplicate` (new), the rest → null (form-level).
- **Refactor:** `core/ledger/README.md`.

### Step B3b — Categories Room, v3, seeding wired into Setup
- **Red:**
  - `RoomCategoryIntegrationTest`: the service rules that touch storage (create, sub-category, reorder, archive/pickable, delete/restore, `bySystemKey`) through Room, each audited; a failing audit append leaves nothing.
  - `RoomCategoryForeignKeyTest` [Verify]: a DAO insert with a missing `parent_id` fails if Room enforces foreign keys; otherwise the test is written to pin "not enforced" and the plan's learnings note says the service is the guard.
  - `RoomAuditColumnsTest`: `category` registered.
  - `MigrationTestSupport` generalised: `createAt(version, seed)` (with `createV1` kept as `createAt(1, …)`), because `migrate2To3` must start from v2 with an account row.
  - `MigrationTest.migrate2To3`: v2 with an account row → v3; `category` exists with its indexes, the account row is intact; chain test and step-count test updated.
  - `ui/setup/SetupViewModelTest` and `ui/nav/SetupFlowTest` (updated): after Continue the database holds the user, device, setting **and** the 31 defaults (count and the `LOAN_EMI` row); the "failed audit" test now proves no category exists either.
  - `EncryptedMigrationTest` re-run on `pft35` (no edit: it targets the current version).
- **Green:** `app/.../data/category/{CategoryEntity,CategoryDao,CategoryMapper,RoomCategoryRepository}.kt` (the mapper uses `CategoryColour.fromStored` and `CategoryIcon.parse`; a stored icon that fails to parse reads as null, never a crash); `VERSION = 3`, `MIGRATION_2_3`, schema `3.json`; `AppServices.categories`, `firstRun`; `SetupViewModel` uses `FirstRunService`; `RoomTestKit`.
- **Refactor:** none. Docs: `data-model.md` v3, `docs/features/first-run-and-profiles.md` (setup also creates the default categories), `docs/features/categories-and-tags.md` (created: the default list), learnings (FK result).

### Step B4a — Categories list
- **Red:**
  - `ui/categories/CategoriesViewModelTest` (Room): Expense is shown first; the Income switch shows only income categories; rows are parents then their children (indented) in `sort_order`; `moveUp`/`moveDown` swap with the neighbouring sibling (the first row has no Move up, the last no Move down); archive, delete with undo; delete of a parent with children shows "Delete its sub-categories first" (fixed text) and writes nothing; `LOAN_EMI`'s menu has no Archive or Delete; an action failure logs `entity_action_failed kind=CATEGORY …`.
  - `ui/common/GlyphSourceTest` stays green after `fromIconKey` delegates to `CategoryIcon.parse` (refactor safety net); add `CategoryColourPaletteTest`: `CategoryColour.entries.map { it.key } == CategoryPalette.keys`.
  - `ui/nav/CategoriesFlowTest`: More → Categories shows "Food & dining" with "Groceries" under it; Move down on "Food & dining" puts it after "Transport"; archive "Shopping" moves it to Archived; Income shows "Salary".
  - Goldens: list light/dark, with an archived section and an emoji icon.
- **Green:** `ui/categories/{CategoriesScreen,CategoriesViewModel,CategoryRowMapper}.kt`; the `CategoryIcon → GlyphSource` mapper in `ui/common/CategoryIconGlyph.kt` (consumers: this list, `CategoryPicker`, C4 rows, D4, E4; design plan `:808`); `GlyphSource.fromIconKey` delegates to core; More gets the Categories row.
- **Refactor:** remove the now-dead grammar helpers from `GlyphSource.kt`. Docs: `categories-and-tags.md` (list, reorder, archive rules), `app/README.md`, `code-structure.md`.

### Step B4b — Category create/edit (name, parent, colour, symbol) and `CategoryPicker`
- **Red:**
  - `ui/categories/CategoryEditViewModelTest`: create a top-level Expense category with a symbol and colour; create a sub-category with a parent chosen from the picker (only live top-level categories of the same kind are offered); `DuplicateName` shows under the name field; edit prefills and changes name/colour/icon; kind and parent are shown but not editable when editing; failure shape as B2b.
  - `ui/common/CategoryPickerTest`: offers `pickable(kind)` only (an archived category and the children of an archived parent are absent: M1a `:272`); `topLevelOnly` hides children; tapping a glyph calls `onPick` and dismisses; selection is announced.
  - `CategoriesFlowTest` extends: FAB → "Food" exists, so create "Pets" with `pets` and amber → appears; create "Vet" under "Pets" → shows indented.
  - Goldens: symbol grid, swatches.
- **Green:** `ui/categories/{CategoryEditScreen,CategoryEditViewModel,SymbolGrid,ColourSwatches}.kt`; `ui/common/CategoryPicker.kt` (stateless over a list; the consumer's ViewModel loads it); routes with `id`, `parent`, `kind` arguments.
- **Refactor (D12):** if `AccountEditViewModel` and `CategoryEditViewModel` share the same save shape, extract `ui/common/launchSave(...)` (a plain function) and use it in both, plus `SetupViewModel` if it fits without changing its tests; otherwise leave them concrete and say why in the PR.

### Step B4c — Emoji icons
- **Red:** `ui/categories/EmojiInputValidatorTest` (`@GraphicsMode(NATIVE)` [Verify `Paint.hasGlyph` under Robolectric; if it cannot answer, the render check is injected as a function and tested with the real `Paint` on `pft35`]): one emoji accepted; a ZWJ family accepted; a flag accepted; two emoji rejected; a letter rejected; a zero-width space rejected; `"🛒 "` (trailing space) rejected (design plan `:808`). `CategoryEditViewModelTest`: the emoji tab's quick-pick sets `CategoryIcon.Emoji`; a rejected paste shows the error and keeps the previous icon.
- **Green:** `ui/categories/{EmojiInputValidator,EmojiTab}.kt` (ICU `BreakIterator`, `UCharacter.hasBinaryProperty(EXTENDED_PICTOGRAPHIC)`, `Paint.hasGlyph`); the static quick-pick list of about 24 finance emoji.
- **Refactor:** none. Docs: `categories-and-tags.md` emoji rules (from design plan §3.6).

### Step B5a — Tags core and Room, v4
- **Red:**
  - `tag/TagServiceTest`: create trims; `"Travel"` then `"travel"` or `" TRAVEL "` → `DuplicateName("tag")`; an archived "Travel" still blocks "travel"; a deleted one does not; rename to another live tag's name → `DuplicateName`, rename to a case change of its own name is allowed; archive/unarchive/delete/restore audited; restore into a clash → `DuplicateName`; `lists()` sorted ignoring case; `pickable()` excludes archived.
  - `RoomTagIntegrationTest`, `RoomAuditColumnsTest` (`tag` registered), `MigrationTest.migrate3To4` (v3 with an account and a category row → v4), chain/step tests; `EncryptedMigrationTest` re-run.
- **Green:** `core/ledger/.../tag/{Tag,TagRepository,TagService,TagLists}.kt`, `TagId`, fakes `InMemoryTagRepository`, `LedgerTestKit.tags`; `app/.../data/tag/*`, `VERSION = 4`, `MIGRATION_3_4`, `4.json`, `AppServices.tags`.
- **Refactor:** none. Docs: `core/ledger/README.md`, `data-model.md` v4.

### Step B5b — Tags UI and `TagPicker`
- **Red:** `ui/tags/TagsViewModelTest`, `TagEditViewModelTest` (duplicate shows under the name field); `ui/common/TagPickerTest` (multi-select: toggling marks rows `selected`, Done returns the chosen ids, archived tags absent, an already-selected archived tag is still shown as selected so an edit in C4 cannot silently drop it); `ui/nav/TagsFlowTest` (create "Travel", rename to "Trips", archive).
- **Green:** `ui/tags/{TagsScreen,TagsViewModel,TagEditScreen,TagEditViewModel}.kt`, `ui/common/TagPicker.kt`, More gets the Tags row.
- **Refactor:** use `launchSave` if B4b extracted it. Docs: `categories-and-tags.md` (tags), `app/README.md`.

### Step B-final — Integration docs
- No code. Update the M1a plan with an "As built (B)" note (versions v2–v4, C1 now v5; the CHECK/partial-index finding for C1), the M1a §4 table, `data-model.md`'s planned version rows (C1 v5 … G1 v10), the itemised plan's §4 version rows and C1c migration test name, and the CHECK finding added to the itemised plan's §4 and §10, root `README.md` status, `docs/architecture/overview.md` if the module picture changed (it does not: no new module), `graphify update .`.

### PR breakdown (incremental — required)

**Target branch.** A larger feature: base branch **`feat/m1a-ledger-setup`** off `main` (M1a `:409-412`). Slice branches are named `feat/m1a-ledger-setup-<slug>` (a ref cannot nest under the base name; learnings 2026-10-28) and each PR targets the base branch. B-final merges the base branch into `main`. One worktree per PR branch (CLAUDE.md §6).

| PR | Scope (steps) | Target | Depends on | Why it's independently reviewable |
|----|---------------|--------|------------|-----------------------------------|
| B1a | Backup orphan fix | `feat/m1a-ledger-setup` | — | one bug in one class, its own tests |
| B1b | Accounts core (+ error mapping) | `feat/m1a-ledger-setup` | — | pure core rules against fakes |
| B1c | Accounts Room, v2, encrypted migration test | `feat/m1a-ledger-setup` | B1a, B1b | persistence and the first real migration, isolated for a careful review |
| B2a | More screen, `EntityListScreen`, snackbar slot, accounts list | `feat/m1a-ledger-setup` | B1c | first list screen and the shared list block |
| B2b | `AmountField`, account create/edit | `feat/m1a-ledger-setup` | B2a | one form |
| B2c | `PftBottomSheet`, `AccountPicker` | `feat/m1a-ledger-setup` | B2a | one shared picker, no consumer changes |
| B3a | Categories core, defaults, `FirstRunService` | `feat/m1a-ledger-setup` | B1b | hierarchy rules, pure core |
| B3b | Categories Room, v3, seeding wired into Setup | `feat/m1a-ledger-setup` | B1c, B3a | persistence + one changed first-run path |
| B4a | Categories list (kind switch, reorder, archive), icon mapper, `GlyphSource` refactor | `feat/m1a-ledger-setup` | B2a, B3b | list behaviour only |
| B4b | Category form (name, parent, colour, symbol), `CategoryPicker`, save-shape refactor | `feat/m1a-ledger-setup` | B2b, B2c, B4a | one form and one picker |
| B4c | Emoji tab, `EmojiInputValidator` | `feat/m1a-ledger-setup` | B4b | one untrusted-input validator |
| B5a | Tags core + Room, v4 | `feat/m1a-ledger-setup` | B3b | small entity, last migration of the slice |
| B5b | Tags UI, `TagPicker` | `feat/m1a-ledger-setup` | B2c, B4b, B5a | small screen set and one picker |
| B-final | Docs; `feat/m1a-ledger-setup` → `main` | `main` | B1a–B5b | one coherent capability: the ledger's reference data |

B1a, B1b and B3a can be developed in parallel (separate worktrees); everything with a schema bump is sequential (B1c → B3b → B5a) because each migration builds on the previous version. Each PR carries its docs, its learnings entry, its security-review section and a `graphify update .`.

## 7. Test plan

**Unit (core, Kotest, ≥ 90% line coverage):** every service rule above against `LedgerTestKit` fakes, which also run `AuditFieldsCheck` on each write; `CategoryIcon` grammar (moved from `GlyphSourceTest`), `CategoryColour`, `CategoryTree` (sorting, pickability, sibling sets), `DefaultCategories` integrity, `Validation.requireBalance`/`requireOptionalName`, `AccountRules`; atomicity via a failing `AuditLog`.

**Integration (app, Robolectric, real in-memory Room):** one `Room<Entity>IntegrationTest` per entity through its real service; `RoomAuditColumnsTest` for each new table; `MigrationTest` 1→2, 2→3, 3→4, the chain and the step count, each proving the audit triggers survived; ViewModel tests over `RoomTestKit`; nav flow tests through `PftRoot` for Accounts, Categories, Tags and the updated Setup; component tests for `EntityListScreen`, `AmountField`, the pickers, `EmojiInputValidator`; Roborazzi goldens for new screens and components (fake data only).

**Instrumented (`pft35`, not in `check`):** `EncryptedMigrationTest` (v1 encrypted → current, backup exists and is encrypted) run in B1c, B3b and B5a; `EncryptedDatabaseTest` unchanged and re-run once in B1c.

**Edge and failure cases:** blank/over-limit names; non-ASCII digits in last 4; ±`MAX_AMOUNT` and one paisa beyond; zero opening balance; type switch flipping the sign; depth 3; kind mismatch; parent archived/deleted; duplicate names with case and whitespace variants; restore into a clash; reorder with a stale set; delete parent with children; restore child of a deleted parent; system category protection; corrupt stored `colour`/`icon_key`/`currency`; unknown route id; double submit; audit failure mid-write; seeding twice; first-run failure leaving nothing.

**AC → test map**

| AC | Tests |
|---|---|
| L-2 | `CategoryServiceTest`, `CategoryTreeTest`, `RoomCategoryIntegrationTest`, `CategoriesViewModelTest`, `CategoriesFlowTest`, `CategoryEditViewModelTest`, `CategoryPickerTest` |
| L-3 (tags only) | `TagServiceTest`, `RoomTagIntegrationTest`, `TagsFlowTest`, `TagPickerTest` |
| L-4 | `AccountServiceTest`, `RoomAccountIntegrationTest`, `RoomAccountMapperTest` (INR), `AccountEditViewModelTest`, `AccountsFlowTest` |
| L-8 | attribution asserts (`shouldHaveAudited`) in every service test; `FirstRunServiceTest` |
| L-9 (supply side) | `AccountPickerTest` (only active accounts offered) |
| INT-2 | audit assertions in every service, Room integration and ViewModel test; `RoomAuditColumnsTest` |
| INT-3 | delete/restore tests per entity; Undo flow tests |
| INT-4 | failing-`AuditLog` tests per entity and in `FirstRunServiceTest`/`SetupViewModelTest` |
| INT-5 | `MigrationTest` (each step, chain), `PreMigrationBackupTest` (orphan), `EncryptedMigrationTest` |
| INT-6 | no balance column exists (`RoomAuditColumnsTest` column set); balance derivation is C3 |
| SEC-5 | `LogEventShapeTest` (new events: enums only), `LoggingScanTest` (unchanged, still scans new code) |
| UI-6 | `CategoryIconTest`, `CategoryColourPaletteTest`, `EmojiInputValidatorTest`, `GlyphSourceTest`, goldens |

## 8. Security considerations

- **Untrusted input:** every name, issuer, last-4 and emoji is user text, validated in core (lengths, grammar, ASCII digits) and again per field in the ViewModel with the same core functions. Amount text goes through the bounded hand-written `PaiseParser` (no regex backtracking, no `Long` overflow). Emoji text is bounded to 16 UTF-16 units and stripped of invisible/bidi characters in core, then checked as one renderable grapheme in the app. Stored `colour`, `icon_key` and `currency` are treated as untrusted on read (future M2 sync): unknown colour → slate, bad icon → letter fallback, non-INR currency → fail closed.
- **Navigation arguments** are ids and enum names only, never names or amounts; a bad id ends in "no longer exists", never a crash or a stack trace on screen.
- **Queries:** Room DAOs with bound parameters only; no `@RawQuery`; migrations are fixed SQL with no interpolation. No CHECK constraints or partial indexes (schema parity between fresh and migrated databases).
- **Logs:** only `SafeLog` + enum-only `LogEvent`s; `LedgerError` messages name the kind and the limit, never the name, last 4 or amount (the account identifier is explicitly on the SEC-5 "never log" list). The audit trail stores these values in the encrypted database, as for every other field.
- **Fail closed:** corrupt `currency` throws on read; a first-run failure rolls back the identity and the defaults together; a failed backup still stops the database from opening (unchanged), and the orphan fix only removes our own partial copy.
- **Manifest/permissions:** none added. `FLAG_SECURE` already covers every screen (single activity). No new exported component. `EmojiCompatInitializer` stays removed (no downloadable emoji font).
- **Dependencies:** none added (no coroutines-core in core, no picker or drag library; ICU and `Paint` are platform APIs).
- **Review:** `/security-review` and `/review-changes` per PR (TECHNICAL.md §5), with B1c (migration + backup + encrypted test) and B4c (untrusted emoji input) called out for focused review.

## 9. Documentation updates (all required surfaces)

| Surface | Change | PR |
|---|---|---|
| Inline KDoc | every new public class/function; `PreMigrationBackup` WAL paragraph; `GlyphSource` (delegates the grammar); `CollapsingHeaderScaffold` (`snackbarHost`) | each PR |
| `core/ledger/README.md` | public interface (account, category, tag, setup packages; new `Validation` helpers; `LedgerError` cases), behaviours (uniqueness, depth, kind/parent fixed, system category, seeding ids), data owned, test fixtures (new fakes, `LedgerTestKit` members) | B1b, B3a, B5a |
| `app/README.md` | `data` (three new tables, migrations, encrypted migration test), `ui/common` (EntityListScreen, UndoDeleteSnackbar, AmountField, PftBottomSheet, pickers, CategoryIconGlyph, `launchSave` if extracted), `ui/more`, `ui/accounts`, `ui/categories`, `ui/tags`, routes with arguments, logging events | B1c, B2a–c, B3b, B4a–c, B5a–b |
| `docs/features/accounts.md` (new) | what accounts are, types and badges, opening balance and "amount owed", last 4 rules, archive vs delete, Undo | B2a, B2b, B2c |
| `docs/features/categories-and-tags.md` (new) | default list (§4), two levels, kind and parent fixed, reorder, archive hides children, Loan EMI protection, icons (symbols, emoji rules, quick-pick), colours, tags uniqueness | B3b, B4a–c, B5b |
| `docs/features/first-run-and-profiles.md` | setup also creates the default categories in the same step | B3b |
| `docs/architecture/data-model.md` | v2, v3, v4 tables and the version table; "no CHECK constraints or partial indexes, and why"; FK enforcement result; planned rows renumbered (C1 v5 … G1 v10) | B1c, B3b, B5a, B-final |
| `docs/architecture/persistence-and-security.md` | the first real migration and its backup, the orphan fix, `EncryptedMigrationTest` | B1a, B1c |
| `docs/architecture/design-system.md` | `snackbarHost` slot, `EntityListScreen`, `PftBottomSheet`, pickers, emoji input | B2a, B2c, B4b–c |
| `docs/architecture/code-structure.md` | core `account/`, `category/`, `tag/`, `setup/`; app `data/account|category|tag`, `ui/more|accounts|categories|tags`, new `ui/common` blocks; "Shared building blocks" table | B1b, B1c, B2a–c, B3a–b, B4a–b, B5a–b |
| `docs/architecture/overview.md` | the call flow example gets a real entity (account) | B2b |
| `docs/plans/2026-10-28-m1a-core-ledger.md` | "As built (B)" note; §4 version table (tag v4, C1 v5…); CHECK/partial-index finding for C1 | B-final |
| `docs/plans/2026-11-10-itemised-transactions.md` | §4 version rows (C1 v5, C3 v6) and C1c `migrate4To5`; CHECK/partial-index finding on `transaction_line` in §4 and §10 | B-final |
| `docs/learnings/README.md` | each PR's entry (expected: CHECK/partial index, FK pragma, encrypted migration recipe, `LifecycleResumeEffect` in Robolectric, `Paint.hasGlyph` under Robolectric, APK size deltas) | every PR |
| Root `README.md` | status: slice B complete | B-final |
| `graphify-out/` | `graphify update .` | every code PR |

## 10. Risks & mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| A migration's SQL differs from Room's generated `CREATE TABLE` | validation fails on upgrade, or worse, passes with a different shape on fresh installs | copy the SQL from the exported schema JSON; `runMigrationsAndValidate` in every migration test; chain test |
| CHECK constraints or partial indexes in a migration only | fresh and upgraded databases enforce different rules silently | none used (§2, D4, D10); invariants held in core; flagged for C1's `ledger_transaction.amount > 0` and the itemised plan's `transaction_line.position > 0` / `amount > 0` (decide in C1c: core-only, which the line-sum invariant already is, or a callback-installed trigger like the audit triggers) |
| First real migration on an encrypted file breaks or skips the backup | data loss on the phone at the first upgrade | `EncryptedMigrationTest` on `pft35` before B1c merges; manual upgrade from the A-final build on the emulator (§11) |
| Room does not enforce foreign keys | a dangling `parent_id` could be written by a bug | the service validates the parent in the same transaction; B3b pins the actual behaviour in a test |
| `Paint.hasGlyph` or ICU behaves differently under Robolectric | false-green or false-red emoji tests | `@GraphicsMode(NATIVE)`; inject the render check if needed and prove it on `pft35` |
| `LifecycleResumeEffect` reload not firing in nav tests | stale list after editing | flow tests assert the edited name appears after Back; fall back to reloading on `NavBackStackEntry` lifecycle if needed |
| Default list does not match the household's habits | immediate clean-up work for the owner | Q4: owner reviews the list in the plan; seed keys are permanent, names are editable after seeding |
| Seeding 31 audited rows makes first run slow | sluggish Continue | one transaction on the IO dispatcher; measure on the emulator in B3b (expect well under 200 ms) |
| Shared list block grows options for every entity | abstraction creep | `EntityRow`/`RowAction` stay plain data; entity-specific logic stays in each ViewModel's row mapper; reviewer's reuse check (M1a §8) |
| Slice runs long on the Pro plan | stalled slice | 13 small PRs, each one session; B1a/B1b/B3a parallelisable; stop points after B2c (accounts complete) and B4c (categories complete) |

## 11. Rollout & verification

Per PR: `./gradlew check` (unit, Robolectric, goldens, coverage gate, ktlint, detekt, `verifyReleaseShrink`); `./gradlew :app:connectedDebugAndroidTest` on `pft35` for B1c, B3b and B5a; `graphify update .`.

End to end on the emulator (`pft35`, cold-booted per learnings), at B5b and again on the base branch before B-final:
1. **Upgrade path:** install the A-final build from `main`, complete Setup, then `./gradlew installDebug` from the base branch. The app opens on Home; `adb shell run-as dev.imagh.pft ls no_backup/backups` lists `pre-migration-v1.db`; `head -c 16` of it and of `databases/pft.db` is not the SQLite header. (Defaults are not seeded on this pre-B install: Q4.)
2. **Fresh path:** `adb uninstall dev.imagh.pft`, install, Setup → Home → More → Categories shows the defaults.
3. **Accounts:** add "HDFC Savings" (Bank, 1234, HDFC, ₹10,000) and "Amex" (Credit card, owed ₹5,000); edit, archive, unarchive, delete and Undo; check the Archived section.
4. **Categories:** add "Pets" with a symbol and "Vet" under it with an emoji; Move up/down; archive "Pets" and confirm "Vet" disappears from the parent picker; try to archive "Loan EMI" (no menu entry).
5. **Tags:** add "Travel", try "travel" (error), rename, archive.
6. **Logs:** `adb logcat --pid=$(pidof dev.imagh.pft)` during all of the above contains no account name, last 4, issuer, category or tag name, or amount (learnings A5f item 10).
7. Release build: `assembleRelease`, re-measure the APK, and run the release smoke from learnings (2026-10-30) once at B-final.

**Rollback:** each PR reverts on the base branch; after B-final, the merge commit reverts slice B as a unit. Migrations are forward-only: a device that already migrated restores `pre-migration-vN.db` (procedure in `persistence-and-security.md`).

## 12. Open questions

| # | Question | Default (recommended) |
|---|---|---|
| Q1 | Give `tag` its own migration (v4, in B5a) instead of sharing v3 with `category`? C1 then becomes v5 and later versions shift by one. Since #52 this renumbers three merged docs at B-final (M1a §4, `data-model.md`, the itemised plan's §4 and C1c). The alternative is tags core before B3b with one v3 for both tables, which keeps every downstream number. | **Yes, own migration** (re-confirmed 2026-11-10 after #52). Each table still lands with its model; the renumbering is a one-off docs edit. |
| Q2 | Keep list screens on load-on-demand (reload on resume) and leave `Flow`/coroutines-core out of core until slice C? | **Yes.** No dependency change in B; C introduces `Flow` where background inserts (M1b) make it necessary. |
| Q3 | Opening balance sign from the type: Credit card asks "Amount owed" and stores it negative; other types ask "Current balance" and store it positive. Overdrawn bank accounts cannot be entered in the UI (core still allows it). | **Yes.** Simple and matches how each account is usually thought of; a sign toggle can come later. |
| Q4 | Approve the default category list in §4 (31 categories, 1 system). And: should installs set up **before** this slice get the defaults (a one-time idempotent seed at launch), or not? Only dev emulators exist (no phone install before H3 signing). | **List as proposed (edit in review); no seeding for pre-B installs.** |
| Q5 | Category names unique among live siblings (same parent and kind), ignoring case, like tags? Not in PRD or M1a; prevents two identical picker entries and helps Cashew import match by name. | **Yes.** |
| Q6 | Reorder by Move up / Move down in the row menu (no drag and drop) for M1a? | **Yes.** |
| Q7 | Kind and parent fixed after a category is created (edit name, colour and icon only)? | **Yes.** |
