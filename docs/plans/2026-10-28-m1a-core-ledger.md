# Plan: M1a — Core ledger (manual-entry household tracker)

| Field       | Value |
|-------------|-------|
| Date        | 2026-10-28 |
| Author      | architect |
| Status      | Approved (2026-10-28; Q1–Q7 defaults accepted, plus §3.4 reuse by layer and the Q4 app-ID rule) |
| Related ACs | PRD L-1..L-9, B-1..B-4, R-1..R-3, R-5, LN-1..LN-5 (manual parts), G-1, G-2, X-1 (manual parts), SEC-1, SEC-2, SEC-3, SEC-5, SEC-6, INT-1..INT-6, NFR responsiveness/offline |
| Plan owner  | akashg (repo owner) |

Foundation: S0 is merged (constitution, agents, hooks, graphify, toolchain pins: `docs/TECHNICAL.md` §7.1). There is **no Gradle project and no Kotlin code yet**; `.pre-commit-config.yaml:4` already expects the Gradle project to arrive in "slice M1a-1".

---

## 1. Requirement

**Need.** The owner tracks household money in Cashew. M1a replaces Cashew's everyday ledger with a private, encrypted, offline Android app so the household can *run a full month on manual entry only* (PRD §6.1 exit criterion). SMS/email capture (M1b/M1c) build on this ledger, so its schema must already carry per-user attribution, source/reference fields, tombstones and client-generated IDs.

**Done means:** on a release-signed APK installed on the owner's phone, both users can (after picking a profile on first run) create and manage accounts, two-level categories, tags, expense/income/transfer transactions, budgets, recurring items and subscriptions, EMI loans and goals; search and filter transactions; see budget progress and loan/goal progress derived from transactions; every create/edit/delete is in an immutable audit trail; the database is SQLCipher-encrypted with a Keystore-wrapped key; the app is behind a biometric/device-credential lock with `FLAG_SECURE`; the manifest requests no runtime permissions and no `INTERNET`; `./gradlew check` is green with ≥ 90% coverage on `core`.

**Out of scope (later phases):** SMS/email parsing, discovery (AD-*), review inbox, notifications (S-5), matching and de-dup (X-2..X-6, R-4), balance observations from messages (L-10), Drive sync and the op-log (Y-*), export/backup/restore UI (D-2, D-3), Cashew import (D-1), shared budgets/goals and splits (B-5, G-3), WorkManager, network access of any kind, insights.

## 2. Current state

- **Code:** none. Repo holds `docs/`, `tools/redact/` (Python, `tools/redact/redact.py`), `.claude/` agents and settings, `.github/pull_request_template.md`, `.pre-commit-config.yaml`, `graphify-out/`.
- **Gates already wired:** `.pre-commit-config.yaml:39-47` runs `./gradlew ktlintFormat detekt` on commit and `:49-56` runs `./gradlew check` on push, both skipping until `./gradlew` exists. So slice A1 must provide exactly the tasks `ktlintFormat`, `detekt` and `check` (with the coverage floor inside `check`).
- **Pinned toolchain:** `docs/TECHNICAL.md:154-167` (AGP 8.7.3, Gradle 8.10.2 with checksum, Kotlin 2.1.0, Compose BOM 2024.10.01, KSP 2.1.0-1.0.29, Room 2.7.1, `sqlcipher-android` 4.7.2, minSdk 33, target/compile 35, JDK 17 via `gradle/gradle-daemon-jvm.properties`). `docs/TECHNICAL.md:169` notes that Room 2.7.1 plus SQLCipher 4.7.2 is untested together; the first encrypted-DB test (A4b) confirms it.
- **Secrets:** release signing from env (`docs/TECHNICAL.md:56`, `docs/guides/local-setup.md` §5). M1a needs no OAuth.
- **Reuse:** there is nothing in-repo to reuse yet. From the platform: `java.time` (minSdk 33, plain JVM in core), `java.security.SecureRandom`, Android Keystore, `org.json` (built into Android, for audit-diff serialisation in `app`), Gradle's built-in `jacoco` plugin (no extra coverage dependency).
- **Conflict with the brief to flag:** PRD §16 item 8 orders "app lock + encryption" **last**. This plan moves **encryption to the persistence foundation (A4b)**, because retrofitting SQLCipher onto a plaintext DB needs a `sqlcipher_export` migration, and plaintext financial data would otherwise exist on the emulator or phone in the meantime. App lock stays late (slice H).

## 3. Design & decision

### 3.1 Modules (Gradle)

| Module | Kind | Owns |
|---|---|---|
| `:core:money` | Kotlin/JVM | `Paise` value type, parsing from user text, Indian formatting, overflow-safe arithmetic |
| `:core:ledger` | Kotlin/JVM | IDs (UUIDv7), identity (users/devices), audit model and diffing, `TransactionRunner`, accounts, categories, tags, transactions/transfers, queries/search filters, loans, goals, app settings; all repository interfaces for these |
| `:core:budget` | Kotlin/JVM | period math, budget scope matching, progress, budget revisions, `BudgetRepository` |
| `:core:recurring` | Kotlin/JVM | occurrence engine, rules and subscriptions, catch-up processing, `RecurringRepository` |
| `:app` | Android | Compose UI, ViewModels, manual DI container, Room/SQLCipher implementations of every repository, Keystore DB-key wrapping, app lock, safe logging, diagnostics |
| `build-logic/` | included build | one convention plugin `pft.kotlin-core` (JVM 17, Kotest/JUnit 5, JaCoCo 90% gate, ktlint, detekt) applied by the four core modules |

Dependency direction: `money ← ledger ← budget`, `ledger ← recurring`, `app → all core`. **No `core/shared` package yet.** IDs and audit live in `ledger`, and `budget`/`recurring` already depend on `ledger`, so no second consumer outside that chain exists. Promote to `core/shared` the day a module that must not depend on `ledger` needs them (likely parsing in M1b).

Loans and goals stay inside `ledger`. They are small ledger entities whose only logic is "sum linked transactions", and CLAUDE.md §3.1 does not list them as separate modules.

### 3.2 Key decisions

1. **Money**: `@JvmInline value class Paise(val value: Long)`. Amounts on transactions are always **positive**, and the sign comes from `direction` (OUT/IN). Arithmetic uses `Math.addExact`/`subtractExact` (an overflow throws, it never wraps). User input is parsed from text with a strict grammar (digits, optional Indian/Western grouping commas, at most 2 decimals, no exponent, no sign). Floating point is never used, and the reviewer rejects `Double`/`Float` anywhere in money paths (INT-1).
2. **IDs**: UUIDv7 (RFC 9562), written by hand (about 25 lines, `SecureRandom`) because Kotlin 2.1's `kotlin.uuid` cannot generate v7. Stored as 36-char text. Entity IDs are typed value classes (`AccountId`, `TransactionId`, …) so they cannot be mixed up. **Recurring-generated transactions get a deterministic name-based ID** from `(ruleId, dueDate)` so two devices cannot create divergent duplicates once M1c sync lands (X-2 spirit).
3. **Atomicity and audit (INT-2, INT-4)**: core defines `interface TransactionRunner { suspend fun <T> inTransaction(block: suspend () -> T): T }` and `interface AuditLog { suspend fun append(entry: AuditEntry) }`. Every mutating core service method does `runner.inTransaction { repo.write(...); audit.append(diff) }`. The Room implementation uses `withTransaction`. **Immutability is enforced in the DB**: `BEFORE UPDATE` / `BEFORE DELETE` triggers on `audit_entry` `RAISE(ABORT)`, and the DAO exposes only insert and select. An `AuditEntry` holds entity type, entity id, action (CREATE/UPDATE/DELETE/RESTORE/ARCHIVE/UNARCHIVE), field-level `changes: Map<String, FieldChange(old, new)>` as strings, userId, deviceId, source (MANUAL/SYSTEM/RECURRING) and timestamp. `app` serialises `changes` with `org.json`, so core stays dependency-free.
4. **Soft delete (INT-3)**: every user entity has `deleted_at`. Queries exclude tombstones. `restore` is audited. Hard delete never happens.
5. **Derived totals only (INT-6)**: account balance (= opening balance + Σ signed transactions), budget spent, loan paid and goal progress are always computed by query or pure function. No running-total columns.
6. **Single-entry ledger (L-9)**: every transaction row has exactly one `account_id`. A transfer is **two rows** (OUT leg, IN leg) sharing `transfer_group_id`, created, edited and deleted atomically together. A transaction cannot change between transfer and non-transfer in an edit; the user deletes it and creates a new one (keeps invariants simple).
7. **Loans (LN-*)**: `Loan` is its **own entity**, not an account. An EMI is an EXPENSE on the paying account with `loan_id` set and the system "Loan EMI" category as default (LN-1). Paid = Σ linked EMIs. Remaining payable = `emi × (tenure − count)` when tenure is known, otherwise "unknown". No amortisation maths in M1a. Card-EMI conversion (LN-5): the original purchase gets `neutralised_by_loan_id`, which is audited and excludes it from spending queries. **(Open question Q1.)**
8. **Configuration by variability (§3.5)**: static limits (title ≤ 200 chars, description ≤ 2 000, max amount ₹1e11) live in a typed `LedgerLimits` object documented in the ledger README. Per-user/runtime values (budget alert threshold %, upcoming-lookahead days, auto-lock timeout) are in the DB (`app_setting` table or per-budget column), and every change is audited.
9. **Persistence**: Room 2.7.1 + `SupportOpenHelperFactory` (sqlcipher-android 4.7.2). The DB key is 32 random bytes, wrapped with an AES-256-GCM Keystore key (`pft_db_kek`) and stored in `noBackupFilesDir`. The key is **not** user-auth-bound, because M1b's SMS receiver must write while the app is locked. App lock is a UI gate; at-rest protection comes from SQLCipher plus the hardware-backed KEK. If the key is lost or invalidated the app **fails closed** with an error screen and never creates a fresh DB over existing data. Exported schemas go in `app/schemas/`. Every schema bump has a `Migration` plus a `MigrationTestHelper` test. Before any migration runs, a **pre-migration copy** of the (still encrypted) DB file is made (INT-5), keeping the last 3.
10. **Search (L-7)**: an FTS4 table over `title`/`description`, plus a single parameterised DAO query with nullable or flagged filter params (`(:noAccounts OR account_id IN (:accountIds))`). No `@RawQuery` and no string-built SQL. The transaction list is **month-windowed** (like Cashew), and search results are capped (limit in `LedgerLimits`), so there is no Paging dependency.
11. **Recurring without WorkManager**: catch-up processing runs when the app comes to the foreground and after unlock. AUTO rules create the due transactions dated at their due date (`source=RECURRING`, `state=UNVERIFIED`). CONFIRM rules surface PENDING occurrences as upcoming or overdue (R-3, R-5). WorkManager is deferred to M1b, where SMS capture needs it anyway (YAGNI).
12. **DI**: a hand-written `AppContainer` built in `Application`. No Hilt/Koin (abstraction bar).
13. **Abstraction bar**: the only *swap-boundary* interfaces are the repository interfaces, `TransactionRunner` and `AuditLog`, which are the mandated core↔app persistence boundary (CLAUDE.md §3.1); there are no other swap boundaries in M1a (no network, no providers). Services, calculators, `Uuid7`, `DbKeyManager`, `AppLockPolicy` and `SafeLog` are concrete. Shared building blocks that remove duplication across the entities this plan already names are listed in §3.4; nothing else is abstracted.
14. **Tests**: core uses Kotest (FunSpec plus property tests for money and period maths) on JUnit 5, with hand-written in-memory fakes published once from `:core:ledger` via `java-test-fixtures` and reused by budget/recurring tests (shared, not copied). `app` integration uses Robolectric (JUnit 4 runner via the JUnit Vintage engine, because Robolectric does not run on Jupiter) with a real in-memory Room DB through ViewModel → service → Room → SQLite. Instrumented tests run on the `pft35` emulator only for SQLCipher and Keystore.

### 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`)

Late-2024 line per `docs/TECHNICAL.md` §7.1; exact patch **[Verify]** at install, and move up if the OWASP scan flags a version.

| Dependency | Pin (proposed) | Why |
|---|---|---|
| kotlinx-coroutines-core / -test | 1.9.0 | suspend services and Flow in core; test dispatcher |
| JUnit Jupiter + Vintage engine | 5.11.3 | test platform; Vintage runs Robolectric's JUnit 4 tests |
| Kotest runner/assertions/property | 5.9.1 | core test style and property tests |
| Robolectric | 4.14.1 | JVM integration tests of Room/ViewModels/Compose (SDK 35 support) |
| androidx.room runtime/ktx/compiler/testing | 2.7.1 | already decided |
| net.zetetic:sqlcipher-android | 4.7.2 | already decided |
| androidx.activity-compose | 1.9.3 | Compose host |
| androidx.lifecycle viewmodel-compose / runtime-compose / process | 2.8.7 | ViewModels, `collectAsStateWithLifecycle`, foreground detection for lock and catch-up |
| androidx.navigation-compose | 2.8.4 | screen navigation |
| androidx.biometric | 1.1.0 | app lock (SEC-2) |
| Compose BOM ui-test-junit4 | via BOM 2024.10.01 | a few UI flow tests. `ui-test-manifest` is **not** used (DS1: it adds an exported activity); see the note below |
| ktlint Gradle plugin (`org.jlleitschuh.gradle.ktlint`) | 12.1.2 | provides `ktlintFormat` that the hook calls |
| detekt plugin | 1.23.7 | lint (plain `detekt` task; no type resolution) |
| OWASP dependency-check plugin | 11.1.1 | vulnerability scan; `NVD_API_KEY` from env, never committed |

Rejected: Paging, WorkManager, Hilt, kotlinx-serialization, Turbine, androidx.security-crypto (deprecated), Kover (built-in JaCoCo suffices), material-icons-extended (large). Icons are curated Material Symbols drawables instead, and `material-icons-core` is banned too (design-system plan §3.6).

Note (2026-10-29): Material 3 resolves to **1.3.1** through the Compose BOM, not 1.3.0. The design-system plan adds Roborazzi 1.36.0 for screenshot tests (its §3.8).

Note (DS1, 2026-10-29): `ui-test-manifest` is dropped because it adds an exported `ComponentActivity` to the debug manifest, which `ManifestHardeningTest` forbids. **A5 and every later Compose JVM test use `HostedComposeRule`** (`app/src/test/kotlin/dev/imagh/pft/app/ui/HostedComposeRule.kt`), which registers the host activity with Robolectric, and never `createComposeRule()` (it fails on the release unit-test task). Instrumented tests (A4b) need their own answer.

### 3.4 Reuse by layer (owner decision, 2026-10-28)

Code is abstract *at its own layer* so it is reused, not copied: one shared building block per layer instead of the same code repeated for every entity. The plan already names 8+ entities (account, category, tag, transaction, budget, recurring rule, loan, goal) that share the same lifecycle, common columns, audit flow and screen shapes. Their consumers are known now, so building these pieces up front meets the abstraction bar rather than breaking it.

**Rules:**
- Extract a shared piece only when this plan already names **at least two concrete consumers**. Otherwise write the concrete thing.
- Prefer composition and plain functions to inheritance, with **at most one level** of generic base class.
- Services stay concrete per entity, because their rules differ. There is **no** generic `CrudService<T>`.
- The only interfaces with a single implementation remain the mandated core↔app repository boundary.
- A piece needed across core modules moves to `core/shared` as soon as a module that must not depend on `ledger` needs it (likely parsing in M1b).

| Layer | Shared building block | Consumers named in this plan | Built in |
|---|---|---|---|
| core (`:core:ledger`) | `EntityMeta` data class (id, created/updated, createdBy user and device, deletedAt, archivedAt), composed into every model | every entity | A3 |
| core | `Auditable` interface: `auditFields(): Map<String, String?>`. `AuditDiff` works on it generically, so no reflection or kotlin-reflect | about 10 models | A3 |
| core | `AuditedWriter` (concrete): create, update, delete, restore and archive wrapped in `runner.inTransaction`, with attribution from identity, the diff and the audit append; a no-op update writes nothing | every service | A3 |
| core | `EntityRepository<T, Id>` (get, getLive, insert, update), which each entity's repository extends with its own queries | every repository | A3 |
| core | `LedgerError` sealed hierarchy and validation helpers (`requireName(text, max)`, `requireAmount(paise, max)`, `requireLive(ref)`) | every service | A3 |
| core test fixtures | generic `InMemoryEntityStore<T, Id>` backing every fake repository, and a Kotest matcher `shouldHaveAudited(...)` | all core tests | A3 |
| app data | `@Embedded MetaColumns` with one `MetaColumns ↔ EntityMeta` mapper; generic `BaseDao<E>` (insert, update); `RoomEntityRepository<T, E, Id>` base taking the DAO and a mapper | every Room repository | A4a |
| app tests | `TestDatabase` factory (Robolectric) and `MigrationTestSupport` for every `migrateNToN+1` test and the chain test | all app tests | A4a |
| app UI (`ui/common/`) | `UiState` convention, `FormState` field validation, `EditFormScaffold`, `NameField` | all edit screens | A5 |
| app UI | `EntityListScreen` (active and archived sections, FAB, empty state), `UndoDeleteSnackbar`, `AmountField` (wraps `Paise.parse` and shows the error), `AccountPicker` (`MoneyText` moved to design-system DS5) | accounts, categories, tags, transactions, budgets, recurring, loans, goals | B2 |
| app UI | `CategoryPicker` (B4), `TagPicker` (B5), `DateField` (C4) | transactions, budgets, recurring, loans | B4, B5, C4 |
| app UI | `AuditHistoryList`: a generic history view for every detail screen, not only transactions | transaction, loan, goal, budget and recurring detail | C5 |

From B1 on, each entity step **composes** `EntityMeta`, `AuditedWriter`, `EntityRepository`/`RoomEntityRepository`, `EntityListScreen` and `EditFormScaffold`, so the step contains only that entity's own rules.

**As built (A3, 2026-10-29).** A4a and B1 build on these details, which the step text above did not fix:
- **`LedgerEntity<Id, Self>`** (`entity/`, F-bounded: `meta` + `withMeta`) is what an entity implements, on top of `Auditable` (`auditType`, `auditId`, `auditFields()`). `AuditedWriter` needs it to stamp the meta (id, timestamps, attribution, deleted/archived state) itself, so a caller cannot forge them. `UserProfile`, `Device` and `AppSetting` are `Auditable` only (no `EntityMeta`).
- **`EntityRepository<T, Id>`** also declares `val entityType: String` (the entity's `auditType`), used to name the kind in `LedgerError.NotFound`. A4a's `RoomEntityRepository` must supply it.
- **`AuditRecorder`** (`audit/`, not in the step text) builds and appends one audit entry (id, time, attribution). `AuditedWriter`, `IdentityService` and `SettingsService` share it, which also breaks the writer/identity construction cycle.
- **`AuditedWriter`:** `create(repo, id) { meta -> entity }`, `update(repo, id) { old -> new }`, `delete`, `restore`, `archive`, `unarchive`. Lifecycle calls are idempotent (repeating one is a no-op that audits nothing). ARCHIVE and UNARCHIVE audit the `archived_at` change (`null -> ISO instant` and back); DELETE records a field snapshot with null news, RESTORE the fields with null olds. A no-op update writes nothing. **Every persisted business field must be in `auditFields()`, under its snake_case name, and nothing else** (`openingBalance` -> `opening_balance`). Enforced by `AuditFieldsCheck` (testFixtures, plain Java reflection, no kotlin-reflect), which `InMemoryEntityRepository` runs on every insert and update: **B1..G entity tests get this for free** just by writing through their fake repository (extend `InMemoryEntityRepository`, do not bypass it); entity tests may also call `shouldHaveCompleteAuditFields()`. A field that is not persisted is not allowed on an entity (use a getter). At runtime, an update that changes the entity but no listed field throws `IllegalStateException` and writes nothing. **A4a:** the fake check does not see Room columns, so add a Room-side test that each Room entity's columns match its model's `auditFields()` keys (one generic assertion in `MetaMapper`/`TestDatabase` support) so a new column cannot be added without being audited.
- **`IdentityService`** also has `initialiseForExistingUser(userId, deviceLabel)` and `requireIdentity()`. A `current_device_id` setting whose device or user is missing throws `LedgerError.IdentityCorrupt` (fail closed, never "uninitialised"). `SettingsService` refuses the reserved `current_device_id` key; setting values go into the audit trail in clear text, so it is for non-sensitive values only.
- **`NameBasedId.of(kind, vararg parts)`** is UUIDv5 over `"<utf8 length>:<text>"` for the kind and each part, in a fixed app namespace. Its output is pinned by tests, so the encoding must never change. `Uuid7` takes a `java.time.Clock` and is strictly increasing within a process.
- **Typed ids:** only `UserId`, `DeviceId` and `AuditEntryId` exist, all implementing `EntityId`. Each slice adds its own.
- **Test fixtures:** `InMemoryLedgerStore` (the fake runner: a failure at any nesting depth restores the whole outermost block, like Room), `InMemoryEntityStore`, `InMemoryEntityRepository(ledger, entityType)`, `LedgerTestKit`, `TestClock`, `shouldHaveAudited`/`shouldNotHaveAudited`.
- **No coroutines library yet.** The `suspend` contracts need only the language feature; add `kotlinx-coroutines` with the first `Flow`-returning query (slice C), not before.

### 3.5 Design system (owner decision, 2026-10-29)

Every screen is built on the design system in [`2026-10-29-m1a-design-system.md`](2026-10-29-m1a-design-system.md): `PftTheme` tokens (colour, type, shape, spacing, motion), the one-hand `CollapsingHeaderScaffold` and `BottomActionBar`, `MoneyText`/`MoneyTone`, `CategoryGlyph` and the other shared components. Its PRs DS1–DS5 land in `feat/m1a-foundation` before A5. Appearance settings and custom-font import (AP1, AP2) get their own slice after A-final. How each M1a step consumes it is listed below (the design plan's §6.3, applied here):

| M1a step | Change |
|---|---|
| **A5** | **Navigation:** `PftNavHost` uses `PftMotion` nav transitions, and the manifest sets `enableOnBackInvokedCallback="true"`. `MainActivity` swaps `DevContent()` for the nav host, and the catalogue becomes a debug-only route. **Forms:** `EditFormScaffold` = `CollapsingHeaderScaffold(Standard)` + `BottomActionBar(Save)` + `imePadding`, and `SetupScreen` is its first consumer. `SetupFlowTest` also asserts that Continue is in the bottom bar. **Lint:** `SafeLog` uses a detekt `ForbiddenImport` of `android.util.Log`, not `ForbiddenMethodCall`, which needs type resolution and would silently do nothing. |
| **B2** | `EntityListScreen` = `CollapsingHeaderScaffold` + M3 `ListItem` + `EmptyState`. `MoneyText` comes from DS5. **New here:** `PftBottomSheet` (first consumer `AccountPicker`). The account type badge is a `CategoryGlyph` with a symbol. |
| **B3** | `CategoryColour` enum and `CategoryIcon` in core, with validation tests in `CategoryServiceTest`. `DefaultCategories` assigns symbols and colours. |
| **B4** | Category edit has a symbol grid (`PftSymbol`), an emoji tab (quick-pick plus keyboard, with `EmojiInputValidator` and its test) and palette swatches. `CategoryPicker` is a `PftBottomSheet` grid of `CategoryGlyph`. A shared `CategoryIcon → GlyphSource` mapper goes in `ui/common` (consumers: C4, D4, E4). |
| **C4** | Home uses `HeaderStyle.Dashboard` with a hero `MoneyText(animate = true)`. **New here:** `PeriodSwitcher` and `TransactionRow` (shared), plus `AmountKeypad` (local). FAB → add uses a container transform via `SharedElements`; list insert and delete animate; unverified rows show the `StatusChip`. |
| **C5** | List → detail shared elements (glyph and amount). The detail is built on `GroupedSection` + `AuditHistoryList` + `BottomActionBar(Edit, Delete)`. |
| **D4** | **New here:** `ProgressMeter` (shared). Its test asserts that unverified spend is drawn with a distinct pattern (screenshot) and labelled in semantics (B-3). **Two screens** (design plan §12 Q11): the Budgets tab is a `Standard`-header list of budget cards, and the budget detail page uses the `Dashboard` header (step D4). |
| **E4 / F3 / G2** | E4 uses `StatusChip` for Overdue/Paused/Cancelled. F3 and G2 use `ProgressMeter`. G2 completion uses the `bouncy` spring, which reduced motion skips. |
| **H1 / H3** | H1: the lock screen uses `PftTheme`, and the timeout row joins AP1's `SettingsScreen`. H3: motion is judged on a minified release build, and jank numbers go in learnings. |

## 4. Data model & schema

Room DB `pft.db`, starting at v1. Each slice adds tables through its own migration (v1→v2→…), each with a migration test. Common columns on user entities: `id TEXT PK`, `created_at`, `updated_at`, `created_by_user_id`, `created_on_device_id`, `deleted_at NULL`. All money columns are `INTEGER` paise.

| Version (slice) | Table | Key columns / constraints |
|---|---|---|
| v1 (A4a) | `user_profile` | id, display_name |
| v1 | `device` | id, user_id FK, label; the current device is pointed to by `app_setting('current_device_id')` |
| v1 | `audit_entry` | id, entity_type, entity_id, action, changes_json, user_id, device_id, source, at; index (entity_type, entity_id); **UPDATE/DELETE triggers abort** |
| v1 | `app_setting` | key PK, value, updated_at (changes audited) |
| v2 (B1) | `account` | name, type (BANK/CREDIT_CARD/CASH/WALLET), currency CHECK = 'INR', masked_identifier NULL, issuer NULL, opening_balance INTEGER (signed), archived |
| v3 (B3) | `category` | parent_id NULL FK self, kind (EXPENSE/INCOME), name, colour, icon_key, sort_order, archived, system_key NULL UNIQUE (e.g. `LOAN_EMI`) |
| v3 (B5) | `tag`, `transaction_tag` | tag: name (UNIQUE NOCASE among live rows), archived. The join table is created in C1 |
| v4 (C1) | `ledger_transaction` | type (EXPENSE/INCOME/TRANSFER), direction (OUT/IN), amount INTEGER CHECK > 0, occurred_at (epoch ms UTC), account_id FK, category_id NULL FK, title, description, state (UNVERIFIED/VERIFIED), source (MANUAL/SMS/EMAIL/IMPORT/RECURRING), bank_ref NULL, source_message_id NULL, account_fragment NULL, transfer_group_id NULL, recurring_rule_id NULL; indexes on (occurred_at), (account_id, occurred_at), (category_id), (transfer_group_id) |
| v4 (C1) | `transaction_tag` | (transaction_id, tag_id) PK |
| v5 (C3) | `transaction_fts` | FTS4 content table over title, description |
| v6 (D3) | `budget`, `budget_scope`, `budget_revision` | budget: name, period_type (WEEKLY/MONTHLY/CUSTOM), anchor_date, custom_end NULL, alert_threshold_pct, archived; scope rows: (budget_id, category_id or tag_id); revision: (budget_id, effective_from, limit_amount) |
| v7 (E3) | `recurring_rule`, `recurring_occurrence` | rule: kind (RECURRING/SUBSCRIPTION/EMI), template fields (type, amount, account_id, to_account_id NULL, category_id, title, tag ids via `recurring_rule_tag`), frequency (DAILY/WEEKLY/MONTHLY/YEARLY), interval, start_date, end_date NULL, max_count NULL, mode (AUTO/CONFIRM), status (ACTIVE/PAUSED/CANCELLED), provider NULL, loan_id NULL; occurrence: (rule_id, due_date) **UNIQUE**, state (PENDING/CREATED/SKIPPED), transaction_id NULL |
| v8 (F2) | `loan`; ALTER `ledger_transaction` ADD `loan_id`, `neutralised_by_loan_id` | loan: lender, account_fragment NULL, emi_amount, start_date, principal NULL, interest_rate_bps NULL (integer basis points, no float), tenure_months NULL, status |
| v9 (G1) | `goal`; ALTER `ledger_transaction` ADD `goal_id` | goal: name, target_amount, target_date NULL, archived |

Canonical schema (§3.3): the core models are the canonical shape. Room entities in `app` are persistence DTOs mapped one-to-one, and core never sees them.

## 5. Flows

**Manual expense (representative).** `AddTransactionScreen` → `AddTransactionViewModel.save()` → `Paise.parse(text)` (rejects bad input at the UI boundary) → `TransactionService.createExpense(cmd)` validates (account live, category of kind EXPENSE and live, amount > 0 and ≤ limit, title length) → `runner.inTransaction { transactionRepo.insert(txn); tagRepo.setTags(id, tags); audit.append(CREATE diff) }` → Room `withTransaction` → SQLCipher DB. The list screen observes `transactionQueries.month(yearMonth, filter): Flow<List<…>>` and updates.

**Transfer.** `TransactionService.createTransfer(from, to, amount, at)` creates two rows with one `transfer_group_id` (OUT on `from`, IN on `to`) and two audit entries in one DB transaction. Budgets and income/expense totals exclude TRANSFER (L-5).

**Budget progress.** `BudgetsViewModel` → `BudgetService.progress(budgetId, today)` → `PeriodCalculator.periodContaining(budget, today, zone)` → `TransactionQueries.spendingBetween(range)` (EXPENSE, live, not neutralised) → `BudgetCalculator.progress(budget, revisionLimit, txns, categoryTree)` → spent, verified and unverified split, remaining, %, `overThreshold`.

**Recurring catch-up.** `ProcessLifecycleOwner` ON_START (after unlock) → `RecurringService.catchUp(today)` → for each ACTIVE rule, `OccurrenceEngine.dueDates(rule, lastProcessed..today)` → for each date without an occurrence row: AUTO → `inTransaction { insert txn(deterministic id); insert occurrence CREATED; audit }`; CONFIRM → insert PENDING. The UNIQUE(rule_id, due_date) constraint makes re-runs idempotent.

**First run.** No `current_device_id` setting → `SetupScreen` → `IdentityService.initialise(userName or pick existing, deviceLabel)` → creates the user (if new), the device and the setting, then seeds default categories (source SYSTEM), all audited in one transaction.

**App start.** `PftApplication.onCreate` → `DbKeyManager.loadOrCreateKey()` → `PreMigrationBackup.copyIfUpgrading()` → open Room with SQLCipher factory → `AppContainer`. `MainActivity` sets `FLAG_SECURE` and wraps the nav host in `LockGate`.

## 6. Implementation steps (TDD-ordered)

Conventions: package root `dev.imagh.pft` (**Open question Q4**). Core tests: `core/<m>/src/test/kotlin/dev/imagh/pft/<m>/…Test.kt` (Kotest). Fakes: `core/ledger/src/testFixtures/kotlin/dev/imagh/pft/ledger/fakes/`. App JVM tests: `app/src/test/kotlin/dev/imagh/pft/app/…` (Robolectric). Instrumented: `app/src/androidTest/kotlin/…` (emulator `pft35`). Every step ends with a green `./gradlew check`.

### Slice A — Foundation (`feat/m1a-foundation`)

**A1 — Gradle skeleton and gates** (infra; the "tests" are the gates themselves plus two app-hardening tests)
- **Red:** `app/src/test/.../ManifestHardeningTest` (Robolectric) asserts that `ApplicationInfo.FLAG_ALLOW_BACKUP` is unset, that the merged manifest requests no permissions, and that the launched `MainActivity` window has `FLAG_SECURE`. Fails because the project does not exist.
- **Green:** `settings.gradle.kts` (`:app`, `:core:money`; other core modules are added in the slice that needs them), `gradle/libs.versions.toml`, the wrapper 8.10.2 with `distributionSha256Sum`, `gradle/gradle-daemon-jvm.properties`, `build-logic/` with `pft.kotlin-core` (JVM 17 toolchain, `useJUnitPlatform()`, Kotest, JaCoCo with `jacocoTestCoverageVerification` minimum 0.90 line coverage wired into `check`, ktlint, detekt), and `:app` (AGP 8.7.3, Compose BOM, minSdk 33/target 35, `allowBackup=false`, `dataExtractionRules` excluding everything, empty `MainActivity` setting `FLAG_SECURE`). Add dependency locking (`gradle.lockfile`s), `gradle/verification-metadata.xml` (sha256) and the OWASP plugin (`dependencyCheckAggregate`, failing on CVSS ≥ 7; `NVD_API_KEY` from env).
- **Refactor:** confirm the pre-commit hook runs `ktlintFormat detekt` and the pre-push hook runs `check`. Prove the floor fails closed by temporarily dropping a test locally (not committed).

**A2 — `core/money`**
- **Red:** `PaiseTest`: `parse("1,23,456.78") == Paise(12345678)`, `parse("12") == Paise(1200)`, `parse("0.5") == Paise(50)`; rejects `"1.234"`, `"-5"`, `"1e3"`, `""`, `"12..3"`, `" "`, overlong input. `format(Paise(12345678)) == "₹1,23,456.78"`. Addition overflow throws `ArithmeticException`. Property tests: `parse(format(x)) == x` for x in 0..limit; `a + b - b == a`.
- **Green:** `core/money/src/main/kotlin/dev/imagh/pft/money/Paise.kt`, `PaiseParser.kt`, `PaiseFormat.kt`.
- **Refactor:** README; KDoc.

**A3 — `core/ledger` foundations: IDs, identity, audit, runner**
- **Red:** `Uuid7Test`: version nibble = 7, variant = 0b10, IDs from increasing millis sort lexicographically, 10k IDs are unique. `NameBasedIdTest`: the same `(ruleId, date)` gives the same ID and different inputs give different IDs. `AuditDiffTest`: `diff(old, new)` lists only changed fields with string old/new values, CREATE has null olds, DELETE has null news, and an identical object gives no diff (no audit row for no-op edits). `IdentityServiceTest` (with fakes): `initialise("Akash","Pixel")` persists a user, a device and the current-device setting and appends 3 audit entries; calling it again throws `AlreadyInitialised`; a blank name is rejected; `currentIdentity()` returns null before initialisation.
  `AuditedWriterTest` (§3.4): a create is audited with the current user and device; a no-op update writes no audit row; an exception inside the block rolls back both the write and the audit entry. `ValidationTest`: `requireName` rejects blank and over-limit text, `requireAmount` rejects zero and over-limit, `requireLive` rejects archived and deleted references, each with its `LedgerError`.
- **Green:** `ids/Uuid7.kt`, `ids/NameBasedId.kt`, `ids/Ids.kt` (typed value classes), `entity/{EntityMeta,Auditable,EntityRepository}.kt`, `audit/AuditEntry.kt`, `audit/AuditDiff.kt` (works on `Auditable`), `audit/AuditLog.kt` (interface), `audit/AuditedWriter.kt`, `tx/TransactionRunner.kt` (interface), `validation/{LedgerError,Validation}.kt`, `identity/{UserProfile,Device,IdentityRepository,IdentityService}.kt`, `settings/{AppSettingsRepository,SettingsService}.kt`, plus testFixtures `InMemoryEntityStore<T, Id>`, `InMemoryLedgerStore` (fake runner snapshots and restores state on exception, so atomicity is testable in core) and the `shouldHaveAudited` matcher.
- **Refactor:** README for ledger (initial).

**A4a — Room DB v1 and core↔Room adapters (plaintext, in-memory in tests)**
- **Red:** `app/src/test/.../data/AuditDaoTest`: inserting then reading by entity returns the entry with the `changes` map round-tripped; a raw `UPDATE audit_entry …` or `DELETE` throws `SQLiteConstraintException` (trigger). `RoomTransactionRunnerTest`: a block that writes a user then throws leaves zero rows (INT-4). `IdentityRoomIntegrationTest`: `IdentityService` over Room repositories persists and returns the identity. `RoomAuditedWriterIntegrationTest`: `AuditedWriter` over Room writes the row and its audit entry together, and a failure leaves neither.
- **Green:** `app/.../data/PftDatabase.kt` (v1, `exportSchema=true`, `room.schemaLocation=app/schemas`), the shared `data/common/{MetaColumns,MetaMapper,BaseDao,RoomEntityRepository}.kt` (§3.4), entities, DAOs, `RoomAuditLog`, `RoomTransactionRunner`, `RoomIdentityRepository`, `RoomAppSettingsRepository`, mappers. Test support: `TestDatabase` and `MigrationTestSupport` under `app/src/test/.../testing/`. Room does not model triggers, so they are created by `RoomDatabase.Callback.onCreate` (one `AuditTriggers.SQL` constant). Because `audit_entry` exists from v1, every later DB already has them; a `MigrationTestHelper` test asserts the triggers survive each migration (documented in the app README).
- **Refactor:** none (the shared audit path is `AuditedWriter` from A3).

**A4b — SQLCipher, key management, pre-migration backup**
- **Red (instrumented, emulator):** `EncryptedDatabaseTest`: after writing a row, the DB file's first 16 bytes are **not** `"SQLite format 3\0"`; opening the file with a wrong key fails; reopening with the managed key reads the row (this proves the Room 2.7.1 + SQLCipher 4.7.2 pairing). `DbKeyManagerTest`: the first call creates and persists a wrapped key, the second returns identical bytes, and a corrupted wrapped blob throws `DbKeyUnavailable` (fail closed, no new key generated over an existing DB). **Red (Robolectric):** `PreMigrationBackupTest`: when the stored last-opened version < the current version, the DB file is copied to `backups/pre-migration-v{n}.db` and only the newest 3 are kept; when versions are equal, nothing is copied.
- **Green:** `app/.../security/DbKeyManager.kt` (Keystore AES-GCM KEK, wrapped key in `noBackupFilesDir`, key bytes zeroed after use), `app/.../data/DatabaseFactory.kt` (`System.loadLibrary("sqlcipher")`, `SupportOpenHelperFactory`), `app/.../data/PreMigrationBackup.kt`, `PftApplication` wiring, and a `KeyUnavailableScreen`.
- **Refactor:** none. Record the pairing result in learnings.

**A5 — Safe logging and first-run profile (L-8)**
- **Red:** `SafeLogTest` (JVM, app): `SafeLog.event(LogEvent.DbOpened(schemaVersion=1, millis=12))` produces `"db_opened schema=1 ms=12"`. `LogEvent` is a sealed hierarchy whose fields are only ints, enums and durations, so amounts, names and strings are unrepresentable (compile-time guard). `SetupFlowTest` (Robolectric + Compose): on a fresh DB the setup screen shows; entering a name and device label and tapping Continue lands on Home, and the DB holds a user, a device and the current-device setting. `SetupViewModelTest`: a blank name gives a validation error and nothing is persisted.
- **Green:** `app/.../log/{SafeLog,LogEvent}.kt`, `app/.../di/AppContainer.kt`, `app/.../ui/common/{UiState,FormState,EditFormScaffold,NameField}.kt` (§3.4; setup is the first consumer), `app/.../ui/setup/{SetupScreen,SetupViewModel}.kt`, `app/.../ui/nav/PftNavHost.kt` (start destination depends on identity).
- **Refactor:** none.

### Slice B — Accounts, categories, tags (`feat/m1a-ledger-setup`)

**B1 — Accounts core + Room (v2)**
- **Red:** `AccountServiceTest`: create (name required ≤ limit, type, opening balance signed, currency fixed INR, optional last-4 must be 4 digits) gives an audited CREATE. Rename/edit gives an UPDATE with a field diff. Archive hides the account from `activeAccounts()` while `allAccounts()` still includes it. `delete` of an account with live transactions → `AccountInUse` (C1 adds that test once transactions exist; here delete succeeds and is a tombstone). `restore` reverses a delete. `RoomAccountIntegrationTest` covers the same through Room. `MigrationTest.migrate1To2` (MigrationTestHelper) creates v1 with a user row, migrates, and shows the `account` table exists with the user row intact.
- **Green:** `core/ledger/.../account/{Account,AccountType,AccountRepository,AccountService}.kt`; `app/.../data/account/*`; `Migrations.kt` `MIGRATION_1_2`.

**B2 — Accounts UI**
- **Red:** `AccountsFlowTest` (Robolectric Compose): add a "HDFC Savings" BANK account and it appears in the list; archive it and it moves to the Archived section; invalid last-4 shows an error.
- **Green:** `ui/common/{EntityListScreen,UndoDeleteSnackbar,AmountField,MoneyText,AccountPicker}.kt` (§3.4; accounts are the first consumer, the opening balance uses `AmountField`), then `ui/accounts/{AccountsScreen,AccountEditScreen,AccountsViewModel}.kt` built from them; bottom-nav "More" entry.

**B3 — Categories core + Room (v3, includes `tag`)**
- **Red:** `CategoryServiceTest`: create a top-level category and a sub-category; creating a sub-category of a sub-category → `DepthExceeded`; a sub-category's kind must equal its parent's; rename/recolour/re-icon are audited; `reorder(parent, orderedIds)` rewrites `sort_order` for siblings only; archive keeps history and removes it from `pickable()`; archiving a parent hides its children from `pickable()`; the `LOAN_EMI` system category can be renamed but not archived or deleted. `DefaultCategoriesTest`: `seedDefaults()` is idempotent and creates the documented list, including `LOAN_EMI`. Room integration plus `migrate2To3`.
- **Green:** `core/ledger/.../category/*`, `DefaultCategories.kt`; called from `IdentityService.initialise` (update the A5 test to assert seeded categories); `app/.../data/category/*`; `MIGRATION_2_3`.

**B4 — Categories UI**
- **Red:** `CategoriesFlowTest`: create "Food" › "Groceries", reorder, archive; the archived category is absent from the picker used by the transaction form (picker composable test).
- **Green:** `ui/categories/*`, a reusable `CategoryPicker` composable (used later by transactions, budgets and recurring).

**B5 — Tags core + Room + UI**
- **Red:** `TagServiceTest`: names are trimmed and unique case-insensitively among live tags (`"Travel"` vs `"travel"` → `DuplicateTag`); rename/archive are audited. Room integration (the tag table already exists at v3). `TagsFlowTest` for create/rename/archive.
- **Green:** `core/ledger/.../tag/*`, `app/.../data/tag/*`, `ui/tags/*`, `TagPicker` composable.

### Slice C — Transactions (`feat/m1a-transactions`)

**C1 — Expense/income core + Room (v4)**
- **Red:** `TransactionServiceTest`: `createExpense` produces `type=EXPENSE, direction=OUT, state=VERIFIED, source=MANUAL`, attributed to the current user and device (L-6, L-8), and audited. Validation rejects an archived/deleted account, an INCOME category on an expense, an archived category, amount 0, and amount > limit. Tags are set many-to-many (L-3), and an optional `bank_ref` is stored (X-1). `edit` diffs only changed fields, including tags; a no-op edit writes no audit row. `delete` sets a tombstone and `restore` restores it. `markVerified` flips state and is audited. `AccountService.delete` of an account with live transactions → `AccountInUse`. Room integration, including a failure mid-`create` (bad tag id) leaving neither a transaction nor an audit row. `migrate3To4`.
- **Green:** `core/ledger/.../transaction/{LedgerTransaction,TransactionType,Direction,TxnState,TxnSource,TransactionRepository,TransactionService}.kt`; `app/.../data/transaction/*`; `MIGRATION_3_4`.

**C2 — Transfers (L-5)**
- **Red:** `TransferTest`: `createTransfer(a, b, ₹500)` gives two live rows with the same group id (OUT on a, IN on b), equal amount and time, and two audit entries. `from == to` → `SameAccount`. Editing the amount updates both legs. Deleting one leg tombstones both. Card bill payment = a transfer from BANK to CREDIT_CARD. A Room test shows that a failure on the second leg leaves zero rows.
- **Green:** extend `TransactionService`; no schema change.

**C3 — Queries, search, balances (L-7, v5)**
- **Red:** `TransactionQueriesIntegrationTest` (Room): `month(2026-10, filter)` returns only that month in Asia/Kolkata (a row at 2026-10-31T23:30 IST is included; 2026-11-01T00:10 IST is excluded). Filters cover account(s), category (the parent includes its sub-categories), tag, amount min/max, state, type, and the text query (FTS: "swiggy" matches title "Swiggy order"). Tombstones are excluded. Search results are capped at the limit. `accountBalance(id)` = opening + Σ IN − Σ OUT over live rows, and is correct for a credit card (spend makes it negative). `spendingBetween(range)` returns EXPENSE only (no transfers, no income). Core `TransactionFilterTest` covers filter validation (min ≤ max, range order). `migrate4To5` backfills FTS for existing rows.
- **Green:** `core/ledger/.../query/{TransactionFilter,TransactionQueries}.kt` (interface implemented in app), `app/.../data/transaction/TransactionQueryDao.kt` (single parameterised query), FTS entity, `MIGRATION_4_5`.

**C4 — Transaction list and add/edit UI**
- **Red:** `TransactionsFlowTest`: on Home, tap +, enter ₹250, pick account, category and tags, then save; the row shows in this month's list with "₹250.00", and month totals (expense/income, excluding transfers) update. Switching to Transfer shows a destination-account field, and saving shows paired legs. Editing changes the amount. Delete gives an Undo snackbar that restores the row. Bad amount text shows an inline error and saves nothing.
- **Green:** `ui/transactions/{TransactionListScreen,TransactionEditScreen,TransactionsViewModel,TransactionEditViewModel}.kt`, month switcher, state chip (Verified/Unverified badge, L-6).

**C5 — Search/filter UI and transaction detail with history**
- **Red:** `SearchFlowTest`: a filter by tag plus text returns the expected rows. `TransactionDetailTest`: detail shows fields, source and bank ref, and an audit history list ("Amount ₹200.00 → ₹250.00 by Akash on Pixel"; history is formatted in the UI from the audit rows).
- **Green:** `ui/search/*`, `ui/transactions/TransactionDetailScreen.kt`, `AuditQueries` read API in core plus Room, and the generic `ui/common/AuditHistoryList.kt` (§3.4), which the loan, goal, budget and recurring detail screens reuse later.

### Slice D — Budgets (`feat/m1a-budgets`)

Adds `:core:budget` to settings.

**D1 — Period maths (pure)**
- **Red:** `PeriodCalculatorTest`: MONTHLY with anchor on the 1st → calendar months; anchor on the 31st → periods start on the 31st or the month's last day (Feb 28/29). Weekly with a Monday anchor gives Mon–Sun. CUSTOM is a single range, and dates outside it → none. `previousPeriods(n)` is contiguous and non-overlapping. Property: for any date ≥ anchor, exactly one period contains it.
- **Green:** `core/budget/.../{BudgetPeriod,PeriodCalculator}.kt`.

**D2 — Scope matching, progress, service**
- **Red:** `BudgetCalculatorTest`: an overall budget counts all EXPENSE rows (including Loan EMI, LN-1) and no transfers or income. A category scope includes its sub-categories. A tag scope matches any tag. A mixed scope is the union (an OR, documented). `spent` splits into verified and unverified (B-3). Remaining can be negative. Percentage is integer maths (basis points; no float). `overThreshold` fires at ≥ the threshold. `BudgetServiceTest`: create/edit/archive are audited; editing the limit adds a revision effective from the current period start, so last month's history keeps the old limit (B-4); `history(n)` returns per-period progress.
  - **For the budget detail page (design plan Q11):**
    - `DailyPaceTest` (pure): pace = remaining ÷ days left in the period, counting today, in integer paise rounded **down** (it never suggests more than is left). On the last day it equals remaining. Remaining ≤ 0 gives no pace (the UI shows "Over"). A past period gives no pace. Day counts use the period's local dates, so a DST change does not shift them.
    - `BudgetServiceTest`: `transactions(budgetId, period)` returns exactly the rows the calculator counts (same scope and type rules), newest first, so their sum equals `spent` for that period, for current and past periods alike.
- **Green:** `core/budget/.../{Budget,BudgetScope,BudgetRevision,BudgetCalculator,BudgetProgress,DailyPace,BudgetRepository,BudgetService}.kt`; consumes `TransactionQueries.spendingBetween` and the category tree from `:core:ledger`. `DailyPace` is a plain function next to `BudgetCalculator`, and `BudgetProgress` carries its result.

**D3 — Room (v6)**
- **Red:** `RoomBudgetIntegrationTest`: create a budget and add expenses through `TransactionService`; `progress()` reflects them, and a deleted transaction drops out. `migrate5To6`.
- **Green:** `app/.../data/budget/*`, `MIGRATION_5_6`.

**D4 — Budgets UI: list and detail** (layout per design plan §3.4 and §5.5, owner decision Q11)
- **Red:** `BudgetsFlowTest` (through the nav host, real in-memory Room):
  - **List:** create "Groceries ₹8,000 monthly"; adding a ₹6,500 groceries expense shows the Groceries card at 81% with a threshold warning at 80%, and unverified spend is shown with a distinct marker. Each active budget has one card; an archived budget is not listed. The FAB opens the create form.
  - **List → detail:** tapping the card opens the budget detail, whose title is the budget name and whose subtitle is the current period.
  - **Detail:** the hero shows spent of limit; the facts show the period, what it covers and the daily pace; this period's transactions are listed (the ₹6,500 expense, and a tap opens its transaction detail).
  - **Previous period:** ‹ (and a horizontal header swipe) moves to the previous period, which shows that period's spent, limit (B-4: the old limit after an edit) and transactions; › returns to the current one, and › is disabled on the current period.
  - **Edit and archive:** Edit from the detail changes the limit, and the detail and list show the new limit for the current period only. Archive removes the card from the list and keeps the budget's history (audited, per B2's archive pattern).
- **Green:** `ui/budgets/{BudgetsListScreen,BudgetCard,BudgetDetailScreen,BudgetEditScreen,BudgetsViewModel,BudgetDetailViewModel}.kt`. `BudgetCard` stays local (one consumer).

### Slice E — Recurring and subscriptions (`feat/m1a-recurring`)

Adds `:core:recurring`.

**E1 — Occurrence engine (pure, R-1)**
- **Red:** `OccurrenceEngineTest`: daily/weekly/monthly/yearly with interval n; monthly on the 31st clamps per month but keeps the 31st anchor (Jan 31 → Feb 28 → Mar 31); yearly on Feb 29 → Feb 28 in non-leap years; `end_date` inclusive; `max_count` stops after n; a range before the start is empty. Property: dates are strictly increasing and within [start, end].
- **Green:** `core/recurring/.../{Frequency,RecurrenceRule,OccurrenceEngine}.kt`.

**E2 — Rules, subscriptions, catch-up service (R-2, R-3, R-5)**
- **Red:** `RecurringServiceTest` (fakes): rule CRUD is audited; SUBSCRIPTION requires a provider and exposes `nextDue`; pause/resume/cancel are audited and a paused rule generates nothing. `catchUp(today)`: AUTO creates one UNVERIFIED `source=RECURRING` transaction per missed due date, dated on its due date, with a deterministic id; running it twice creates nothing new (idempotent). CONFIRM creates PENDING occurrences; `confirm(occ)` creates a VERIFIED transaction and `skip(occ)` marks SKIPPED (audited, never silent). `overdue(today)` lists PENDING with due < today (R-5), and `upcoming(today, lookaheadDays)` reads lookahead from settings (R-3). Transfer templates create both legs.
- **Green:** `core/recurring/.../{RecurringRule,RecurringKind,Occurrence,RecurringRepository,RecurringService}.kt`.

**E3 — Room (v7)**
- **Red:** `RoomRecurringIntegrationTest`: catch-up over Room is idempotent, and a concurrent second `catchUp` cannot insert a duplicate (UNIQUE constraint holds, and the service treats the conflict as already-processed). A failure creating the transaction leaves no occurrence row. `migrate6To7`.
- **Green:** `app/.../data/recurring/*`, `MIGRATION_6_7`.

**E4 — UI and trigger**
- **Red:** `RecurringFlowTest`: create a monthly CONFIRM rent rule starting last month; after catch-up, the Overdue section shows it, and Confirm creates the transaction. Subscription list shows provider, next due and status. `CatchUpTriggerTest`: the lifecycle ON_START observer invokes catch-up once per foreground (the result is asserted as rows created, not as a call count).
- **Green:** `ui/recurring/*`, `app/.../lifecycle/ForegroundCatchUp.kt` (via `ProcessLifecycleOwner`).

### Slice F — Loans and EMIs (`feat/m1a-loans`)

**F1 — Core**
- **Red:** `LoanServiceTest`: create a loan (lender required, EMI > 0, optional principal, rate in bps, tenure) as an audited CREATE; `withSchedule=true` also creates an EMI-kind CONFIRM monthly `RecurringRule` linked to the loan with category LOAN_EMI (LN-4). `linkEmi(txn, loan)` requires an EXPENSE and is audited. `progress(loan)` = paid (Σ linked live EMIs), count, and remaining payable (`emi × (tenure − count)`, or unknown). `markConverted(purchaseTxn, loan)` sets `neutralised_by_loan_id`, is audited, and `TransactionQueries.spendingBetween` then excludes it (LN-5; this updates the C3 test). An EMI confirmed from the recurring rule is auto-linked to the loan.
- **Green:** `core/ledger/.../loan/{Loan,LoanRepository,LoanService,LoanProgress}.kt`. `LoanService` takes `RecurringService`? **No**: that would make ledger depend on recurring. Instead `LoanScheduleService` lives in `:core:recurring` (which already depends on ledger) and composes `LoanService` and `RecurringService`.

**F2 — Room (v8)**: `RoomLoanIntegrationTest` (loan plus linked EMIs give correct progress; a neutralised purchase is absent from budget spent end to end), `migrate7To8`.

**F3 — UI**: `LoansFlowTest`: create a loan with schedule, confirm an EMI, and the detail shows paid/remaining and the linked EMIs; in the transaction detail, "Converted to EMI" links a purchase.

### Slice G — Goals (`feat/m1a-goals`)

**G1 — Core + Room (v9)**
- **Red:** `GoalServiceTest`: create (target > 0, optional date) is audited; `linkContribution(txn, goal)` (for a transfer, only the IN leg carries the link, so it counts once); progress = Σ linked live amounts, percentage in bps, and complete when ≥ target; unlinking and transaction deletion reduce progress. Room integration and `migrate8To9`.
- **Green:** `core/ledger/.../goal/*`, `app/.../data/goal/*`, `MIGRATION_8_9`.

**G2 — UI**: `GoalsFlowTest`: create "Emergency fund ₹1,00,000", link a transfer, and the progress bar shows 10%.

### Slice H — App lock, diagnostics, release (`feat/m1a-hardening`)

**H1 — App lock (SEC-2)**
- **Red:** `AppLockPolicyTest` (JVM, app): locked at cold start; unlocked after success; re-locks when background time ≥ timeout (timeout read from the `app_setting`, default 60 s, audited on change); stays unlocked below the timeout; a device with no secure credential → `Unavailable` (fail closed, the UI shows "Set a screen lock"). `LockGateTest` (Robolectric Compose): content is not composed while locked.
- **Green:** `app/.../security/{AppLockPolicy,LockGate,BiometricAuthenticator}.kt` (BiometricPrompt `BIOMETRIC_STRONG or DEVICE_CREDENTIAL`), and a settings screen for the timeout. Catch-up (E4) runs only after unlock.

**H2 — Diagnostics screen (§3.7)**
- **Red:** `DiagnosticsViewModelTest`: shows schema version, row counts per table, last catch-up time, app version and DB encryption = on, and the rendered text contains no amounts or names (asserted against seeded data containing a known payee and amount).
- **Green:** `ui/diagnostics/*`, `DiagnosticsQueries`.

**H3 — Release build and M1a acceptance**
- **Red:** a `./gradlew assembleRelease` without the signing env vars **fails** with a clear message (fail closed; checked by running it, documented in the PR). `ReleaseSmokeTest` (instrumented, on a release-like `benchmark`/minified build if feasible, otherwise a manual checklist): the app launches, the encrypted DB opens and the R8 keep rules for SQLCipher hold.
- **Green:** `app/build.gradle.kts` `signingConfigs.release` from `ANDROID_KEYSTORE_*`; R8 rules (`-keep class net.zetetic.database.** { *; }`); `local.properties.example` lists names only.
- **Refactor:** run the 50k-row responsiveness check on the emulator (§11) and record the numbers in learnings.

### PR breakdown (incremental — required)

**Branching.** M1a is too large for one base branch: a single `feat/m1a` → `main` PR at the end would hand the owner a months-long diff. Each **slice** is its own feature with a base branch off `main`, small slice PRs into it, and one base→`main` PR when the slice is green. `main` therefore gains one finished, reviewed capability at a time (PRD §16 "small vertical slices"). **(Open question Q2.)**

| PR | Scope (steps) | Target | Depends on | Why independently reviewable |
|----|---------------|--------|------------|------------------------------|
| A1 | Gradle skeleton, gates, manifest hardening | `feat/m1a-foundation` | — | build infrastructure only |
| A2 | `core/money` | `feat/m1a-foundation` | A1 | one pure value type |
| A3 | ledger IDs, audit model, runner, identity service | `feat/m1a-foundation` | A2 | pure core contracts plus fakes |
| DS1–DS5 | design system (see the design-system plan's PR table) | `feat/m1a-foundation` | A1 (DS5 also needs A2) | one design concern each; A5 depends on DS5 |
| A4a | Room v1, audit triggers, Room runner/repos | `feat/m1a-foundation` | A3 | persistence adapter only |
| A4b | SQLCipher, Keystore key, pre-migration backup | `feat/m1a-foundation` | A4a | security-critical, isolated for focused review |
| A5 | SafeLog, AppContainer, first-run setup UI (on the design system) | `feat/m1a-foundation` | A4b, DS5 | first user-visible flow |
| A-final | foundation | `main` | A1–A5 | encrypted app with a profile |
| B1 / B2 | accounts core+Room / accounts UI | `feat/m1a-ledger-setup` | A-final | one entity, then its screen |
| B3 / B4 | categories core+Room / categories UI | `feat/m1a-ledger-setup` | B1 | hierarchy rules isolated from UI |
| B5 | tags (core+Room+UI; small) | `feat/m1a-ledger-setup` | B3 | small entity |
| B-final | ledger setup | `main` | B1–B5 | |
| C1, C2, C3, C4, C5 | expense/income; transfers; queries+FTS+balances; list+edit UI; search+detail UI | `feat/m1a-transactions` | B-final, sequential | each is one behaviour |
| C-final | transactions | `main` | C1–C5 | **Owner can start daily manual entry here** |
| D1, D2, D3, D4 | period maths; calculator+service; Room; UI | `feat/m1a-budgets` | C-final | |
| E1, E2, E3, E4 | engine; service; Room; UI+trigger | `feat/m1a-recurring` | C-final (parallel with D allowed) | |
| F1, F2, F3 | loan core (+schedule via recurring); Room; UI | `feat/m1a-loans` | E-final | needs recurring for LN-4 |
| G1, G2 | goals core+Room; UI | `feat/m1a-goals` | C-final | |
| H1, H2, H3 | app lock; diagnostics; release signing + acceptance | `feat/m1a-hardening` | all above | |
| each X-final | slice → `main` | `main` | its slice PRs | one coherent capability |

About 32 slice PRs and 8 integration PRs, each sized for one session (TECHNICAL.md §8). Every PR carries its own docs, learnings entry and security-review section, and refreshes the graph (`graphify update .`) when structure changes.

## 7. Test plan

**Unit (core, Kotest on JUnit 5, ≥ 90% line coverage enforced by JaCoCo in `check`):** money parsing, formatting and arithmetic (with property tests); UUIDv7 and name-based IDs; audit diff; every service's validation, state transitions and audit emission against the in-memory fakes (the fake runner rolls back on exception); period maths and the occurrence engine (property tests); budget, loan and goal calculators.

**Integration (app, Robolectric, real in-memory Room/SQLite):** each repository through its service; atomicity (failure mid-transaction → no rows, no audit); audit immutability triggers; FTS and filter queries; IST month boundaries; recurring idempotency under UNIQUE; every migration `n→n+1` and an `1→latest` chain test with `MigrationTestHelper` against `app/schemas/`; ViewModel → service → Room flows; Compose flow tests for each screen's core path.

**Instrumented (emulator `pft35`, local only):** SQLCipher at rest (file header, wrong key fails, correct key reads), Keystore wrap/unwrap, release R8 smoke.

**Edge/failure cases:** bad amount strings, zero or over-limit amounts, archived or deleted references, transfer to the same account, category depth/kind violations, duplicate tag names, month-end/leap-year recurrences, a paused rule, catch-up re-runs, lost DB key (fail closed), no device credential (fail closed), a no-op edit (no audit row).

**AC → test map**

| AC | Tests |
|---|---|
| L-1, L-3 | `TransactionServiceTest`, `TransactionsFlowTest`, `TagServiceTest` |
| L-2 | `CategoryServiceTest`, `CategoriesFlowTest` |
| L-4 | `AccountServiceTest` |
| L-5 | `TransferTest`, `TransactionQueriesIntegrationTest.spendingExcludesTransfers`, `BudgetCalculatorTest` |
| L-6 | `TransactionServiceTest.manualIsVerified/markVerified`, `RecurringServiceTest` (auto → UNVERIFIED) |
| L-7 | `TransactionQueriesIntegrationTest`, `SearchFlowTest` |
| L-8 | `IdentityServiceTest`, `SetupFlowTest`, attribution asserts in `TransactionServiceTest` |
| L-9 | schema (account_id NOT NULL), `accountBalance` tests |
| X-1 (manual) | `TransactionServiceTest.storesBankRefAndSource`, `TransactionDetailTest` |
| B-1..B-4 | `PeriodCalculatorTest`, `BudgetCalculatorTest`, `DailyPaceTest`, `BudgetServiceTest`, `BudgetsFlowTest` (list, detail, past periods, edit, archive) |
| R-1, R-2, R-3, R-5 | `OccurrenceEngineTest`, `RecurringServiceTest`, `RecurringFlowTest` |
| LN-1..LN-5 (manual) | `LoanServiceTest`, `RoomLoanIntegrationTest`, `LoansFlowTest` |
| G-1, G-2 | `GoalServiceTest`, `GoalsFlowTest` |
| SEC-1 | `EncryptedDatabaseTest`, `DbKeyManagerTest` |
| SEC-2 | `AppLockPolicyTest`, `LockGateTest`, `ManifestHardeningTest` (FLAG_SECURE) |
| SEC-3, SEC-6 | `ManifestHardeningTest` (no permissions, backup off) |
| SEC-5 | `SafeLogTest`, `DiagnosticsViewModelTest` |
| INT-1 | `PaiseTest` (property), detekt rule forbidding `Double`/`Float` in `core/money`/`ledger` money paths (custom `ForbiddenImport`/type config) |
| INT-2 | audit assertions in every service test, `AuditDaoTest` |
| INT-3 | delete/restore tests per entity |
| INT-4 | `RoomTransactionRunnerTest`, atomicity tests in C1, C2, E3 |
| INT-5 | `MigrationTest` chain, `PreMigrationBackupTest` |
| INT-6 | balance, budget, loan and goal tests computed from rows, with deletion changing the result |

## 8. Security considerations

- **Inputs:** all user text is validated at the ViewModel/service boundary (amount grammar, lengths, referenced IDs live). No SMS/email in M1a.
- **Queries:** Room DAOs only, every query parameterised; FTS `MATCH` input is passed as a bound parameter and stripped of FTS operators to avoid query-syntax errors (not injection, but crash-proofing). No `@RawQuery`.
- **Secrets:** the DB key is generated on device, wrapped by Keystore and kept in `noBackupFilesDir`; it is never logged and its bytes are zeroed after use. Signing comes from env; `NVD_API_KEY` from env. `.gitignore` already blocks keystores and `local.properties`.
- **Manifest:** no permissions except `USE_BIOMETRIC`; no `INTERNET` in M1a (strongest possible "nothing leaves the device"); `allowBackup=false` plus `dataExtractionRules` excluding all; only the launcher activity is exported; `FLAG_SECURE` on the single activity covers every screen and recents.
- **Logs:** only through `SafeLog` with typed, non-sensitive `LogEvent`s. A detekt `ForbiddenImport` rule bans `android.util.Log` outside `SafeLog` (`ForbiddenMethodCall` needs type resolution, which our detekt setup doesn't run), and `println` is banned in production code.
- **Fail closed:** missing or invalid DB key → error screen, no new DB; no device credential → app stays locked; release build without a signing env → build fails.
- **Audit immutability:** DB triggers plus an insert-only DAO.
- **Dependencies:** listed in §3.3, pinned, locked, checksum-verified, OWASP scan in the PR checklist; no analytics or crash SDKs.
- Run `/security-review` and `/review-changes` before every PR (TECHNICAL.md §5), especially A4b and H1.
- **Reuse check (§3.4), added to every review:** "Does this PR re-implement something a shared building block already does?" and "Does this PR add an abstraction with fewer than two named consumers?" Either one blocks the PR.

## 9. Documentation updates (all required surfaces)

| Surface | Created/updated in |
|---|---|
| Inline KDoc on every public class/function | every PR |
| `core/money/README.md` | A2 |
| `core/ledger/README.md` (sections per Rule B; config: `LedgerLimits`, DB settings) | A3, then each B/C/F/G PR |
| `core/budget/README.md` | D1, updated D2/D3 |
| `core/recurring/README.md` | E1, updated E2/E3/F1 |
| `app/README.md` (layers, DI container, DB/migrations, key management, test types and how to run instrumented tests) | A1, updated in every app PR |
| `build-logic/README.md` | A1 |
| `docs/architecture/overview.md` (modules, dependency direction, call flow, the core↔app boundary interfaces) | A1, refined A3 |
| `docs/architecture/data-model.md` (tables/ER and the version table from §4) | A4a, each migration PR |
| `docs/architecture/persistence-and-security.md` (SQLCipher, Keystore KEK, why the key is not auth-bound, pre-migration backup, audit triggers, app-lock model) | A4b, H1 |
| `docs/architecture/code-structure.md` (fill the module and shared-package tables, plus a "Shared building blocks" table per layer from §3.4) | A1, A3, A4a, and each PR adding a module, package or shared block (A5, B2, C5, D1, E1) |
| `core/ledger/README.md` and `app/README.md` "Public interface" sections list their reusable building blocks | A3, A4a, A5, B2, C5 |
| `docs/features/first-run-and-profiles.md` | A5 |
| `docs/features/accounts.md`, `categories-and-tags.md` (includes the default category list) | B2, B4/B5 |
| `docs/features/transactions.md` (expense/income/transfer, states, search, history) | C4/C5 |
| `docs/features/budgets.md` (periods, anchor, scope union rule, unverified marker, history, the list and detail screens, daily pace) | D4 |
| `docs/features/recurring-and-subscriptions.md` (AUTO vs CONFIRM, catch-up-on-open, overdue) | E4 |
| `docs/features/loans-and-emis.md`, `goals.md` | F3, G2 |
| `docs/features/security-and-app-lock.md`, `diagnostics.md` | H1, H2 |
| `docs/learnings/README.md` | every PR (notably: Room+SQLCipher pairing, Robolectric on JUnit Vintage, FTS on Robolectric, deterministic recurring IDs) |
| Root `README.md` (status, build/test commands, module map) | A1, A-final, C-final, H3 |
| `docs/guides/local-setup.md` (`NVD_API_KEY`, running `connectedAndroidTest` on `pft35`) | A1, A4b |
| `.pre-commit-config.yaml` comment about "slice M1a-1" | A1 (now real) |
| PRD §16 note recording the encryption-first reorder (decision-log entry) | A-final, if the owner approves |

## 10. Risks & mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Room 2.7.1 + SQLCipher 4.7.2 pairing untested (`TECHNICAL.md:169`) | foundation blocked | A4b proves it first on the emulator; fall back to Room 2.6.1 (previous decision line) only with owner approval |
| Robolectric's SQLite lacks FTS4 or behaves differently from SQLCipher's SQLite | false-green search tests | C3 includes one instrumented FTS test on `pft35`; Robolectric tests skip FTS if unsupported (documented in learnings) |
| Keystore key invalidated (OS reset, OEM bug) → DB unreadable | data loss | fail closed, never overwrite; pre-migration copies; D-3 backup/restore prioritised right after M1a (flag to owner) |
| Detekt 1.23.7 / Kotest 5.9.1 against Kotlin 2.1.0 | gate failures | [Verify] in A1; use plain `detekt` (no type resolution); bump within the late-2024 line if needed |
| OWASP scan needs an NVD API key and is slow | push hook friction | run `dependencyCheckAggregate` in the PR security step, not in `check`; key from env |
| Schema churn across 9 migrations | migration bugs | one migration per PR, each tested, plus a chain test 1→latest |
| Responsiveness at 50k transactions (NFR ~200 ms) | sluggish lists | month-windowed list, indexes from C1, measured in H3 on the emulator with seeded data; optimise only if measured slow |
| Scope or cost overrun on the Pro plan | stalled milestone | one PR per session, stop points per slice, slices D, E and G independently shippable after C |
| Pinned Kotlin 2.1.0 is affected by CVE-2026-53914 (compiler build-cache deserialization; fixed in 2.4.20), and the old AGP device-test tooling carries ~60 grpc/netty/protobuf CVEs | build-machine exposure only; nothing ships | scan gates only shipped classpaths, `-PscanAll` reports tooling; Kotlin CVE suppressed until 2027-01-31 with no remote build cache; **follow-up architect plan for a toolchain bump** (Kotlin ≥ 2.4.20 with a matching AGP/Gradle) before that date |
| applicationId or release signing key change | a sideloaded app with a new ID or key is a new install, so all data is lost | **blocked** until a full export/import of all data exists and its round-trip tests pass (see Q4) |
| Shared building blocks grow into a framework | abstraction hell, hard-to-follow call paths | §3.4 rules: two named consumers, composition first, one level of generic base, no generic service; enforced in review |

## 11. Rollout & verification

Per slice: `./gradlew check` (unit + Robolectric + coverage gate + ktlint + detekt), `./gradlew connectedDebugAndroidTest` on `pft35` for instrumented tests, then `./gradlew installDebug` on the emulator and exercise the slice's flow by hand per its feature doc.

**M1a acceptance (H3):**
1. Fresh emulator install → set up profile → lock prompt → seed accounts and categories.
2. Enter a realistic month (expenses, income, a card bill-payment transfer, an EMI from a loan schedule, a goal contribution, a subscription on AUTO and rent on CONFIRM); check budget percentages, loan paid/remaining, goal progress and account balances against a hand-computed sheet.
3. Inspect: `adb shell run-as dev.imagh.pft head -c 16 databases/pft.db` is not the SQLite header; the app is absent from the recents preview; diagnostics show no amounts.
4. Seed 50k transactions via a debug-only seeder and time the list/add screens.
5. Build a release APK with env signing and sideload it to the phone (TECHNICAL.md decision 31). Owner runs one real month: the exit criterion.

**Rollback:** each slice is a separate base→main merge and can be reverted as a unit. Schema migrations are forward-only. Rollback on a device with data means restoring the pre-migration copy, which is documented in `persistence-and-security.md`.

## 12. Open questions

**Resolved 2026-10-28:** the owner accepted every default below.

**App-ID rule (Q4, hard rule).** `dev.imagh.pft` is permanent. Before the applicationId changes (and, for the same reason, before the release signing key is rotated), first build and verify a **full export and import of ALL data**: every table, including the audit trail, users and devices, settings, recurring occurrences, and archived or tombstoned rows. It needs a round-trip test proving the exported → imported database equals the original, and the export file must be encrypted. Export from the old app, import into the new one, check the result, and only then retire the old app. So the later export and backup work (D-2, D-3) must cover *all* data, not a CSV subset.

| # | Question | Default (accepted) |
|---|---|---|
| Q1 | Loans as a **separate entity** (EMIs are expenses on the paying account, linked by `loan_id`) rather than an account of type LOAN as PRD L-9's wording suggests? | Separate entity; no LOAN account type in M1a |
| Q2 | Branching: **one base branch per slice** (8 slice→main merges), or one `feat/m1a` base with a single final PR? | Per-slice base branches |
| Q3 | Approve moving **encryption into the foundation** (A4b) instead of last (PRD §16)? | Yes, encryption first |
| Q4 | Package / applicationId. It is **permanent for sideload updates**: changing it later means reinstalling, which wipes the DB. | `dev.imagh.pft` |
| Q5 | Include **opening balance + computed account balance** in M1a (useful for a real month, L-4 says balance *from email* is M2)? | Include |
| Q6 | Recurring AUTO items: created as **UNVERIFIED** (consistent with "auto-logged") rather than VERIFIED? | UNVERIFIED |
| Q7 | Budget scope with both categories and tags: **union (OR)**? | Union |
