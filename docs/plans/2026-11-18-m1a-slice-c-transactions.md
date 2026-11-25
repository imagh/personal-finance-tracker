# Plan: M1a slice C — Transactions (reviewed and consolidated)

| Field       | Value |
|-------------|-------|
| Date        | 2026-11-18 |
| Author      | architect |
| Status      | Approved (2026-11-18; every §12 default accepted). Amended 2026-11-25 (Cobalt, tab bar, Transactions tab): amendment approved 2026-11-25 |
| Related ACs | PRD L-1 (amended), L-3, L-5, L-6, L-7, L-8, L-9, L-11, X-1 (manual bank ref), INT-1..INT-6, SEC-5. Prepares B-1..B-3 (budgets read item portions in slice D) |
| Plan owner  | akashg (repo owner) |

Foundation: slices A and B are merged to `main` (`3a5f3ee`, PR #73). The database is at **v4** (`app/src/main/kotlin/dev/imagh/pft/app/data/PftDatabase.kt:51`).

**Why this plan exists.** Slice C was specified in two places:
- the M1a plan's base steps C1..C5 ([`2026-10-28-m1a-core-ledger.md`](2026-10-28-m1a-core-ledger.md) §6 "Slice C");
- the itemised-transactions amendment, which supersedes C1 and gives deltas to C2..C5 ([`2026-11-10-itemised-transactions.md`](2026-11-10-itemised-transactions.md) §6).

Both were written before slice B was built. This plan reviews them against the code as it stands after B and makes **one** document an agent can be briefed from by section.
- **Still binding, cited here, not repeated:** the itemised plan's design (§3.1 to §3.8, §4, and owner answers Q1 to Q6) and the M1a decisions (§3.2).
- **Replaced by this plan:** the step text and the PR table for slice C in both earlier plans.
- **Where they conflict, this plan wins.** §2.2 lists every change and why.

Abbreviations: **M1a** = the M1a plan, **IT** = the itemised plan, **B** = [`2026-11-10-m1a-slice-b-ledger-setup.md`](2026-11-10-m1a-slice-b-ledger-setup.md).

## Amendment 2026-11-25 — Cobalt, a tab bar and a Transactions tab

**Why.** The owner approved the Cobalt redesign and asked for a tab bar now, with a Cashew-style Transactions tab (owner answers of 2026-11-25). The design lives in [`2026-11-25-m1a-cobalt-redesign.md`](2026-11-25-m1a-cobalt-redesign.md) (**CR**); this amendment schedules the slice C parts on top of it. Where they conflict, this amendment wins over the original text below; edited places are marked "(amended 2026-11-25)".

**What changes.**
1. **Q2 reversed.** The tab bar ships in CR K7 (Home · Accounts · More on `main`); C4a inserts **Transactions** at position 2. Budgets joins at position 3 in slice D (CR D8).
2. **Home becomes a dashboard (new C4d).** Greeting header (CR K8), `BalanceCard` with this month's spend and In/Out, quick actions Expense/Income/Transfer (Budgets hidden until D4), and "Recent transactions" (latest 5, day headers without totals) with "See all" → Transactions tab. The month list moves off Home.
3. **Transactions tab (C4a, rewritten).** `Standard` header "Transactions"; a horizontal **month strip** (selected month underlined, the year under each January and under the first month shown); a **totals banner** (expense ▼, income ▲, = net, for the month under the current filter); the list in `DayGroup`s with day totals (`MoneySemantic.Net`); `TransactionRow` with category tile, title, tag/account mini-chips, amount; FAB "+" (C4b). Swipe on the list still steps months.
4. **Search, filters and sort split into three PRs** (C5b search, C5c filters, C5d sort). The header actions appear with the PR that builds them: Search (C5b), overflow with Filters (C5c) and Sort (C5d). The list settings sheet (totals banner mode, pinned account filters) is **deferred** to roadmap R-7: it is not trivial (a persisted setting with audit) and nothing in M1a needs it.
5. **Core gaps in `TransactionFilter`/`TransactionQueries`** (new C3c, and C3b):

   | Cashew feature | Gap today (D5) | Where |
   |---|---|---|
   | Sort by date, date created, date updated, amount, title, asc/desc | no sort parameter (newest first only) | **C3c**: `TransactionSort(key: OCCURRED, CREATED, UPDATED, AMOUNT, TITLE; descending)` on `month` and `search`; ties broken by id; SQL `ORDER BY` from a fixed `CASE` on a bound key, never concatenated |
   | Person chips (who recorded it) | no field | **C3c**: `createdBy: Set<UserId>` over `created_by_user_id` (meta, already stored). UI shows the section only with ≥ 2 known users (R-1/M1c) |
   | Title contains / notes contains | one `text` over title, description and notes | **C3b**: `titleText` (FTS column `title`) and `noteText` (description + item notes), each through `SearchText.toMatchQuery` with a core-built column prefix; word-prefix semantics like search |
   | Home "Recent" | no read | **C3c**: `recent(limit: Int): List<LedgerEntry>` (live, newest by `occurredAt`, transfers folded) |
   | Month strip range | no read | **C3c**: `entryMonthSpan(zone): MonthSpan?` (first and last month with a live entry); the strip spans `min(first, now)..max(last, now)` |
   | Category tiles, amount range, Income/Expense, account chips | covered (`categoryIds`, `amountMin/Max`, `types`, `accountIds`) | C5c UI only |
   | Upcoming, subscription, repeating; paid / not paid / skipped | — | slice E (recurring, E3/E4) |
   | Budget chip | — | slice D (D4) |
   | Loan chip | — | slice F |
   | Goal chip | — | slice G |
   | State chips (verified/unverified) | covered (`states`) | UI hidden in M1a (every manual entry is verified); shown from M1b |

6. **Sequencing.** C1d → C2 → C3a → C3c → C3b proceed now (core/data). CR K1–K8 land on `main`; then **C-sync** merges `main` into `feat/m1a-transactions` (T0) before C4a, so `TransactionRow`, `DayGroup`, the tab bar and the money rules (CR D5: expenses neutral everywhere, `Net` for day totals and the banner's net) exist before any transaction screen is built.

**Owner answers recorded (2026-11-25):** Cobalt default with the other palettes kept; tab bar now; Transactions tab modelled on Cashew; Home becomes a dashboard; the architect sequences; restyle vs replace by effort (CR D3).

---

## 1. Requirement

**Need.** The owner wants to run the household's month on manual entry (PRD §6.1 exit criterion). Accounts, categories and tags exist (slice B). Slice C adds the transactions themselves:
- expenses, income and transfers;
- each transaction broken into one or more items (L-1, L-11);
- a month view with totals;
- search and filters (L-7);
- a detail screen with the audit history (INT-2).

**Done means:**
1. **Model.** A transaction holds the payment facts. One or more items (`transaction_line`) hold amount, optional category, tags and note. Live items always sum exactly to the amount, and a save that breaks this is refused with nothing written (IT §3.1, §3.3).
2. **Write rules.**
   - Every create, edit, itemise, delete, restore and verify is audited and attributed (L-8).
   - Delete is a tombstone. A transfer is two legs written, edited and deleted together (L-5).
   - A transfer never counts as income or expense.
3. **In use.** An account, category or tag that a live transaction or item uses cannot be deleted; the user archives it instead (D4, Q4).
4. **Home** shows the selected month:
   - one row per transaction (an itemised one reads "3 items"; a transfer is one row);
   - the month's expense, income and net figures;
   - ‹ › and a swipe to step months, in the device's time zone.
5. **Add and edit** (D8). The user can:
   - add or edit an expense, income or transfer: amount, account (and destination account), date, title, description, optional bank reference;
   - pick the single item's category and tags;
   - **split into items** and fill the remainder, where Save works only when the remainder is ₹0.00 (C4c).
6. **Detail** (C5a) shows:
   - the facts and the items;
   - who created the transaction, on which device, and its source;
   - a history that merges the transaction's entries and its items' entries.

   From the detail the user can edit, delete (with Undo) and mark an unverified transaction verified.
7. **Search and filter** (C5b search, C5c filters, C5d sort; amended 2026-11-25, which also adds sort, a person filter and title/notes text):
   - by text over title, description and item notes;
   - by date range, account, category (a parent includes its children), tag, amount range, state and type;
   - category and tag filters total only the matching items.
8. **Accounts list** shows each account's derived balance: opening balance + Σ IN − Σ OUT over live rows. No balance is stored (INT-6).
9. **Gates.** `./gradlew check` is green with ≥ 90% coverage on `core`. Migrations v4→v5→v6 pass on the JVM and on the encrypted file on `pft35`. Nothing logs an amount, name, title or note.

**Out of scope.**
- **To other slices:** budgets and `spendingLinesBetween` (slice D, D5 and Q7); recurring and `recurring_rule_id` (slice E, Q8); loans and goals (F, G); Cashew import (I).
- **To later milestones:** SMS and email capture (M1b); sync (M1c).
- **Not in M1a:** discount lines and invoice-to-items (roadmap R-4); statistics (roadmap R-5); payee autocomplete; per-item loan or goal links.
- **Deferred to a later slice:** a time-of-day picker (Q6); the Transactions list settings sheet (roadmap R-7, amendment item 4). (Amended 2026-11-25: the tab bar is no longer deferred; it ships in CR K7.)

## 2. Current state

### 2.1 What C plugs into (reuse as is)

**Core write path**
- `AuditedWriter`:
  - `create` (`core/ledger/src/main/kotlin/dev/imagh/pft/ledger/audit/AuditedWriter.kt:49-63`) and `update` with no-op detection (`:77-99`);
  - tombstone `delete` (`:107-114`) and `restore`;
  - it nests in an outer `runner.inTransaction` (`:34-35`), so a transaction, its lines and both transfer legs commit together.
- `LedgerEntity`, `EntityMeta` and `EntityRepository` (`entity/`).
- `AuditSource { MANUAL, SYSTEM, RECURRING }` (`audit/AuditEntry.kt:30-39`).

**Validation, ids and money**
- `Validation`:
  - `requireName` (`validation/Validation.kt:22-30`), `requireOptionalName` (`:68-71`), `requireAmount` (`:38-45`), `requireLive` (`:89-96`), `nameKey` (`:79`).
  - `requireName` already covers "a name or title" and throws `NameTooLong(max)`. The app maps that to `FieldError.TooLong(max)` (`app/.../ui/common/LedgerErrorMapping.kt:16`). So IT's planned `optionalText`/`TextTooLong` would duplicate them (§2.2, item 4).
- `LedgerLimits.MAX_TITLE_LENGTH = 200`, `MAX_DESCRIPTION_LENGTH = 2_000`, `MAX_AMOUNT` (`validation/LedgerLimits.kt:16,19,28`).
- `LedgerError` is sealed, and its messages carry no content (`validation/LedgerError.kt:16-98`). `DuplicateName(what)` (`:92`) is the pattern for one parameterised error.
- Typed ids: `AccountId`, `CategoryId`, `TagId` (`ids/Ids.kt:46-74`). `NameBasedId.of(kind, vararg parts: String)` (`ids/NameBasedId.kt:30`).
- `Paise.plus`/`minus` are exact (`core/money/.../Paise.kt:17-20`). `Paise.ZERO`.

**Services in this slice gets an in-use check (C1d)**
- `AccountService(writer, repository, ids)`, `delete` (`account/AccountService.kt:82`). Its KDoc says "no in use check … until slice C" (`:24`).
- `CategoryService.delete` (`category/CategoryService.kt:124`, KDoc `:33`).
- `TagService.delete` (`tag/TagService.kt:94`, KDoc `:26`).
- `TagService.lists()` and `CategoryService.pickable(kind)` feed the pickers.

**Core test fixtures**
- `InMemoryEntityRepository` runs `AuditFieldsCheck` on every write (`core/ledger/src/testFixtures/.../fakes/InMemoryEntityRepository.kt:19-50`).
- `InMemoryLedgerStore` rolls back the whole outermost block.
- `LedgerTestKit` wires every service (`fakes/LedgerTestKit.kt:22-80`), plus `shouldHaveAudited`.
- **Core has no coroutines dependency** (`core/ledger/build.gradle.kts`: only `:core:money`).

**Room**
- `PftDatabase` is at v4 with seven entities (`app/.../data/PftDatabase.kt:35-51`). `Migrations.MIGRATION_1_2..3_4` hold SQL copied from the exported JSON (`data/Migrations.kt:26-80`).
- Category's foreign key is Room's default `ON DELETE NO ACTION` (`Migrations.kt:62-63`), not IT's `RESTRICT`. Rows are never hard-deleted, so the two behave the same here; C1c keeps the default.
- `RoomEntityRepository(dao, entityType, mapper)` (`data/common/RoomEntityRepository.kt:17`), `BaseDao`, `MetaColumns`, `EpochMillis`.
- `AuditDao.forEntity(type, id)` exists, oldest first (`data/audit/AuditDao.kt:17-22`). C3a builds `AuditQueries` on the same table and index.
- `AppServices` is the production graph (`di/AppServices.kt:39-88`). `RoomTestKit` delegates to it.

**App test support**
- `AuditedTables.all` (`app/src/test/.../testing/AuditedTables.kt:49`). Its `notEntityTables` is a fixed set (`:98`).
- `AuditColumnsCheck.unaccountedTables` (`AuditColumnsCheck.kt:80`) fails on any table not listed there. **FTS4 creates shadow tables** (`<name>_segments`, `_segdir`, `_docsize`, `_stat`), so C3b must account for them (D6).
- `MigrationTestSupport` checks that the audit triggers survive (`MigrationTestSupport.kt:92-101`). It checks only that triggers are *missing*, so Room's FTS content-sync triggers do not trip it. (Its KDoc says "exactly the triggers"; C3b corrects that wording.)
- `SampleDatabase` must list every new entity, or KSP fails (`testing/SampleThing.kt:83-96`; digest "Data, Room and migrations").

**UI blocks to reuse**

| Block | Where | Use in C |
|---|---|---|
| `CollapsingHeaderScaffold` | `ui/common/CollapsingHeaderScaffold.kt:195-207` | `HeaderStyle.Dashboard`, `hero`, `heroCaption`, `period`, `snackbarHost` |
| `HeaderPeriod(onPrevious, onNext)` | same file, `:79-92` | The ‹ › stepper and the swipe are **already built**. The design plan's `PeriodSwitcher` (design plan `:348`) reduces to the directional slide (C6) |
| `EditFormScaffold`, `launchSave`, `SaveOutcome` | `ui/common/EditFormScaffold.kt`, `ui/common/LaunchSave.kt:43` | add/edit form and its save |
| `AmountField` + `FieldState.checkAmount()` | `ui/common/AmountField.kt:32,63` | transaction amount, item amounts |
| `AccountPicker`, `CategoryPicker`, `TagPicker` | `ui/common/AccountPicker.kt:61`, `CategoryPicker.kt:76`, `TagPicker.kt:77` | `TagPicker` takes `TagLists` and keeps an archived tag that is already selected |
| `PftBottomSheet` | `ui/common/PftBottomSheet.kt:50` | sheets |
| `UndoDeleteSnackbar` | `ui/common/UndoDeleteSnackbar.kt:36` | delete with Undo |
| `StatusChip` | `ui/common/StatusChip.kt:29` | Unverified state |
| `MoneyText` | `ui/common/MoneyText.kt:85` | throws on a negative amount except for `Balance` (`MoneyTone.kt:10-21`) |
| `GroupedSection` | `ui/common/GroupedSection.kt:112` | detail and history sections |
| `EmptyState`, `CategoryIcon?.toGlyphSource` | `ui/common/CategoryIconGlyph.kt:16` | empty month, row glyphs |
| `EntityRow` | `ui/common/EntityRow.kt` | plain data; no amount slot yet |

- **Symbols.** `PftSymbol` has `SEARCH`, `FILTER`, `CALENDAR`, `RECEIPT_LONG` (`ui/theme/PftSymbol.kt:34,35,46,96`). It has **no transfer symbol** (`swap_horiz`) and **no `backspace`**. Import both through `tools/material-symbols` (C4a, C6).
- **Navigation.** Routes are strings with id arguments (`ui/nav/Routes.kt`). More is reached from Home's header (`ui/home/HomeScreen.kt:32-47`).
- **Cross-pop results.** "No longer exists" crosses a pop via `savedStateHandle` (`ui/nav/CategoryRoutes.kt:29-65`).
- **Reload on resume.** `LifecycleResumeEffect` reloads a list on resume (`ui/tags/TagsScreen.kt:91-118`).
- **List notices.** `CategoriesViewModel` has a one-off notice for answers such as `HasChildren` (`ui/categories/CategoriesViewModel.kt:26,55,139`). The accounts and tags lists only have `failedAction`.
- **Logging.** `LogEvent.EntitySaveFailed(kind)` and `EntityActionFailed(kind, action)`, with `EntityKind { ACCOUNT, CATEGORY, TAG }` (`log/LogEvent.kt:69-100`).
- **Stale KDoc.** It names C4 as where the `NavigationBar` arrives: `HomeScreen.kt:22-27`, `MoreScreen.kt:28`, `PftNavHost.kt:29`, `HomeViewModel.kt:34`.

### 2.2 Review findings: what changes against M1a and IT

| # | Earlier text | Finding against the code | This plan |
|---|---|---|---|
| 1 | IT C1a puts `AccountService.delete` → `AccountInUse` in core-only C1a | Changing `AccountService`'s constructor breaks `AppServices` (`di/AppServices.kt:78`) until a Room `TransactionRepository` exists (C1c). The category and tag in-use checks promised by B (`B:31`, `:133`; `TagService.kt:26`) are in neither plan | New step **C1d** after C1c: one `InUse(what)` for account, category and tag, plus the list notices (D4) |
| 2 | IT C1a is "pure core" | Every new `LedgerError` breaks app compilation until `LedgerErrorMapping.kt:13-37` maps it (exhaustive `when`) | C1a and C1b map their new errors in the same PR (one app file plus strings) |
| 3 | IT: "extend `TransactionService`" for transfers (C2) | detekt `TooManyFunctions` counts private functions too, from 11 per class (digest "Build"). Lifecycle + itemising + transfers + their helpers would exceed it | `TransactionService` + `TransferService` (C2), sharing one internal `TransactionLines` (D3) |
| 4 | IT C1a: new `Validation.optionalText` + `LedgerError.TextTooLong` | `requireOptionalName`/`requireName` already trim, null a blank and throw `NameTooLong(max)`, which the UI already maps | Reuse them for title, description and note. No new helper or error |
| 5 | M1a §4: `recurring_rule_id NULL` on `ledger_transaction` in v5 | Its table arrives in v8 (E3). Room cannot declare a foreign key to a missing entity, so the column would be unchecked for three versions | E3 adds it with `ALTER TABLE`, as F2 and G1 already do (Q8) |
| 6 | IT C3: `spendingLinesBetween` built in C3 | Nothing in slice C reads it; D2 is its first consumer | Built in D2/D3 to IT's spec (Q7). C3a keeps the per-line filtered totals that C needs |
| 7 | IT C3 Green: `MIGRATION_4_5` | Stale after the B-final renumbering (it is v5→v6) | C3b uses `MIGRATION_5_6`; P0 fixes the IT line |
| 8 | M1a C3: one FTS "content table" | External-content FTS4 joins on the content table's implicit `rowid`. Our ids are TEXT, so the rowid is not an alias, and `VACUUM`/`VACUUM INTO` may renumber it. The shadow tables also trip `unaccountedTables` | D6: external content plus a `rebuild` rule, a test, and the shadow tables registered |
| 9 | M1a C4 is one PR (Home, form, keypad, pickers, transfer, delete/undo, shared elements); M1a C5 is one PR (search, detail, history) | Each is several sessions' work under CLAUDE.md §8 ("one session per PR") | C4 → **C4a** (Home list) + **C4b** (plain form). IT's items editor (IT "C4b") becomes **C4c**. C5 → **C5a** (detail) + **C5b** (search). New **C6** for the keypad and motion (Q5, Q9) |
| 10 | M1a C3 is one PR (month, filters, balances, FTS, v6) | FTS is the only schema change and the only untrusted text that reaches SQL | **C3a** (reads, no schema) + **C3b** (FTS, v6) |
| 11 | M1a §5: the list observes a `Flow`; M1a `:130` adds coroutines to core "with the first Flow query (slice C)" | Only this UI writes transactions in M1a (recurring catch-up arrives in E4, on foreground). B's reload-on-resume pattern covers it | No `Flow`, no new core dependency (D5, Q1) |
| 12 | Design plan: the C4 `NavigationBar` has Home · Budgets · Upcoming · More | Budgets (D4) and Upcoming (E4) do not exist in C, which leaves two tabs | Defer the bar to the first slice with a third tab; More stays in Home's header (Q2). **Superseded 2026-11-25:** tabs Home · Transactions · Accounts · More now (CR D8) |
| 13 | M1a C4 test: "Delete gives an Undo snackbar" on the list | The design puts Delete on the detail screen (design plan `:548-564`, `BottomActionBar(Edit, Delete)`) | Delete and Undo arrive with the detail screen (C5a) |
| 14 | Title "≤ 200" (M1a §3.2 decision 8) | No required/optional decision anywhere. M1b captures may have no payee | Title optional; the row falls back to the category (Q3) |

## 3. Design & decision

The model, invariant, write rules, reads, UI, limits and abstraction stance are **IT §3.1 to §3.8**. The decisions below add to them or change them, and each step cites them.

**D1 — Scope of this plan.**
- It replaces the slice C step text and PR table in M1a §6 and IT §6.
- It keeps IT's design and its approved answers.
- Owner-facing ids change as in §2.2 items 9 and 10. The mapping is in §6's PR table.

**D2 — The model (IT §3.1 to §3.3, with these changes).**
- **`LedgerTransaction(meta, type, direction, amount, occurredAt, accountId, title: String?, description: String?, state, source, bankRef: String?, sourceMessageId: String?, accountFragment: String?, transferGroupId: String?)`.**
  - `direction` is derived (EXPENSE→OUT, INCOME→IN; a transfer leg stores its own).
  - All fields are audited.
  - There is no `recurring_rule_id` (Q8).
- **`TransactionLine(meta, transactionId, position, amount, categoryId: CategoryId?, note: String?, tagIds: Set<TagId>)`.**
  - It audits `tag_ids` as the sorted ids joined with `,` (IT §4).
- **Text.**
  - The title uses `requireOptionalName(text, MAX_TITLE_LENGTH)` (Q3).
  - The description uses `requireOptionalName(text, MAX_DESCRIPTION_LENGTH)`.
  - A note uses `requireOptionalName(text, MAX_LINE_NOTE_LENGTH)`.
  - The bank reference uses `requireOptionalName(text, MAX_BANK_REF_LENGTH = 64)`. This is a new static limit (CLAUDE.md §3.5: static, so a typed constant).
- **New limits.** `MAX_LINES_PER_TRANSACTION = 50` and `MAX_LINE_NOTE_LENGTH = 200` (IT §3.6). `MAX_SEARCH_RESULTS = 500` arrives in C3a.
- **Manual entries.** `create` makes `source = MANUAL` and `state = VERIFIED` (L-6). Other sources get their own entry points in their slices: E2 (recurring), M1b (SMS and email), I1 (import).
- **Type changes on edit.**
  - Expense ↔ income is allowed when every line's category then matches the new kind (otherwise `KindMismatch`).
  - Transfer ↔ non-transfer is refused (M1a decision 6): delete and re-create.
- **Schema rules (as built in B).** No `CHECK` constraint: amount > 0 and position > 0 are enforced in core and the mappers. Foreign keys use Room's default action, like `category`.

**D3 — Services: two concrete services and one shared internal class (IT §3.8 amended).**
- **`TransactionService`** (C1a, C1b): `create(draft)`, `edit(id, draft)`, `delete(id)`, `restore(id)`, `markVerified(id)`, `get(id): TransactionWithLines?`.
  - `delete` and `restore` act on **both legs** when the transaction is a transfer leg.
  - `edit` of a transfer leg is refused with `LedgerError.IsTransfer`; the caller uses `TransferService`.
- **`TransferService`** (C2): `create(TransferDraft)` and `edit(groupId, TransferDraft)`. A transfer draft has from, to, amount, occurredAt, title, description and tagIds. There are no categories and exactly one line per leg (IT §3.3 rule 2).
- **`internal class TransactionLines(writer, lines, categories, tags, ids)`** (C1b) holds:
  - the reconciliation (IT §3.3 rule 4);
  - `requireBalanced` (rule 5);
  - the "validate references only where they change" checks (rule 3);
  - the deterministic first-line ids (rule 8).

  Both services use it. It is a concrete class with two named consumers, so it meets M1a §3.4.
- `TransactionDraft.validated()` and `Breakdown.of` are pure, as `CategoryDraft.validated()` is in B.
- No line service and no generic child-entity helper.

**D4 — In-use refusals (C1d; Q4).**
- **The error.** One `LedgerError.InUse(what: String)`, like `DuplicateName(what)`. The message names the kind only.
- **When each delete is refused:**
  - `AccountService.delete`, while a live transaction uses the account;
  - `CategoryService.delete`, while a live line **of a live transaction** uses the category (`HasChildren` already guards parents);
  - `TagService.delete`, the same, over `transaction_line_tag`.
- **What stays allowed.** Archive (it never deletes history, L-2).
- **Restore.** Because in-use counts only live transactions, a tombstoned transaction may point at something deleted since. `TransactionService.restore` therefore refuses with `Deleted(what)` when its account, or any live line's category or tag, is tombstoned. Archived is fine. This mirrors `CategoryService.restore` refusing under a deleted parent.
- **Queries.** `TransactionRepository.isAccountInUse(id)`, `TransactionLineRepository.isCategoryInUse(id)` and `isTagInUse(id)`.
- **List screens.** `InUse` is an *answer*: a fixed notice ("In use by transactions. Archive it instead.") and nothing logged, as `HasChildren` is in categories. Accounts and tags gain the categories-style notice.

**D5 — Reads (C3a).** `ledger/query/TransactionQueries` is a new core interface, the persistence boundary, implemented in `app/data/transaction/`.
- **`month(month: YearMonth, zone: ZoneId, filter: TransactionFilter): MonthPage`.**
  - `MonthPage` holds `entries: List<LedgerEntry>`, newest first, and `totals: Totals(expense, income)`.
  - Net is derived.
- **`search(filter, zone): SearchPage`.** At most `MAX_SEARCH_RESULTS`, with `truncated: Boolean`.
- **Balances.** `accountBalances(): Map<AccountId, Paise>` and `accountBalance(id)`, over live rows. A credit card goes negative with spend.
- **`LedgerEntry`** is `Single(transaction, lines)` or `Transfer(out, in)`. The fold is a pure core function: with no account filter, two legs are one entry; with an account filter, the leg on that account is a `Single`.
- **`TransactionFilter`** (validated in core, `TransactionFilterTest`):
  - `from`/`to: LocalDate?` with from ≤ to;
  - `accountIds`, `categoryIds` (a parent expands to its children, in core via `CategoryTree`), `tagIds`;
  - `amountMin`/`amountMax: Paise?` with min ≤ max;
  - `states`, `types`, `text: String?` (C3b).
  - (Amended 2026-11-25) `titleText: String?`, `noteText: String?` (C3b); `createdBy: Set<UserId>` (C3c). `month` and `search` take a `TransactionSort` (C3c, default `OCCURRED` descending). New reads `recent(limit)` and `entryMonthSpan(zone)` (C3c). See the amendment's gap table.
- **Totals.** With a category or tag filter, totals are the Σ of the *matching lines* (IT §3.4). Without one, they are the Σ of transaction amounts by type. Transfers are never in totals (L-5).
- **Time zone.**
  - Core takes a `ZoneId`. The app passes `ZoneId.systemDefault()` through a new `AppServices` parameter.
  - Tests pin `Asia/Kolkata`.
  - Config by variability: it comes from the environment and is not hardcoded.
- **Query shape.** Lines for a window are read by a join on the same window predicate, not an `IN (:ids)` list, so there is no variable-count limit.
- **`AuditQueries.forTransaction(id)`** is a new core interface, built here on the existing `audit_entry (entity_type, entity_id)` index. It returns the transaction's entries and all its lines' entries (tombstoned ones included), oldest first.
- No `Flow`. No `spendingLinesBetween` (Q7).

**D6 — Full-text search (C3b, v6).**
- **Tables.** Two Room `@Fts4(contentEntity = …, tokenizer = unicode61)` tables: `transaction_fts` (title, description) and `transaction_line_fts` (note). Room installs the content-sync triggers. Search joins on `rowid` and filters tombstones on the content tables.
- **Query text.**
  - Pure core `SearchText.toMatchQuery(raw: String): String?` keeps letters and digits only, splits on everything else, caps the tokens (8) and their length (32).
  - It emits `"tok"*` per token joined by spaces (implicit AND), or null when nothing is left.
  - The result is bound as a parameter, never concatenated (M1a §8).
- **Rowid hazard.** Anything that runs `VACUUM`, `VACUUM INTO` or an export must then run `INSERT INTO <fts>(<fts>) VALUES('rebuild')` on both tables.
  - `RoomFtsIntegrationTest.rebuildAfterVacuum` pins that a rebuild restores matches.
  - `data-model.md` records the rule for D-2/D-3.
- **`[Verify]` in Red.**
  - Room 2.7.1 accepts `contentEntity` on an entity whose primary key is TEXT `id`.
  - Robolectric's SQLite has FTS4 and `unicode61`.

  If either fails: a self-content FTS4 table with `notIndexed = ["transaction_id"]`, written by the repositories in the same transaction (no triggers). Record the outcome in learnings.
- **Test support.** The shadow tables join `AuditedTables.notEntityTables` by an explicit list (no pattern), with the reason. Both FTS tables are listed there too: they are derived, unaudited indexes.

**D7 — Navigation and screens (amended 2026-11-25).**
- **Tabs** (CR D8): Home · Transactions · Accounts · More after C4a; each tab keeps its own back stack; the bar hides on forms, detail and search.
- **Transactions tab** (C4a) is the month list: a `Standard` header "Transactions", the month strip, the totals banner, one `DayGroup` per day (day total as `Net`), loaded on start and on resume (B's D2 pattern). The ‹ › `HeaderPeriod` stepper is replaced by the strip; swipe still steps months.
- **Home** (C4d) is the dashboard (CR D9): greeting, balance card, quick actions, recent 5, "See all" → Transactions tab.
- **Row taps.** A row tap opens the edit form (C4b). From C5a, it opens the detail.
- **Header actions on the Transactions tab:** Search (C5b) and an overflow menu with Filters (C5c) and Sort (C5d), each added by the PR that builds it (no dead controls). More is a tab, not a Home header action.
- **New routes** (direct children of the `MAIN` graph, so any tab can push them):
  - `TRANSACTION_EDIT = "transactions/edit?id={id}&split={split}&type={type}"` (no id = create; `split=true` opens the items section; `type` presets EXPENSE, INCOME or TRANSFER for a create, from the quick actions; an unknown `type` reads as EXPENSE).
  - `TRANSACTION_DETAIL = "transactions/detail?id={id}"` and `SEARCH = "search"`.
  - Ids and `type` are untrusted text, validated in the ViewModel. An unknown id gives "no longer exists" across the pop.
- **Delete.** It lives on the detail (C5a). The detail pops with a `deleted` id in the previous entry's `savedStateHandle`, and the screen it returns to (Transactions tab, Home or search) offers `UndoDeleteSnackbar`. Undo calls `restore`.
- **Filter and sort state** lives in `TransactionsViewModel`'s `SavedStateHandle` (survives rotation and tab switches through `saveState`, not an app restart). Not persisted, so no setting and no audit entry.

**D8 — The add/edit form (C4b).**
- **One ViewModel.** `TransactionEditViewModel` serves add, edit and itemise. One Save makes the whole write atomic (IT §3.5).
- **Fields.**
  - A type segmented control (Expense | Income | Transfer) on create. On edit, Expense | Income only for a non-transfer, and none for a transfer.
  - Amount (`AmountField`, Q5), account (`AccountPicker`), destination account for a transfer, date (`DateField`), title, description, bank reference.
  - The single item's category (`CategoryPicker` of the matching kind) and tags (`TagPicker`).
- **Switching expense ↔ income** clears a category of the other kind, with a polite live-region notice.
- **`DateField`.**
  - It is a new shared block in `ui/common`, wrapping M3 `DatePicker` in its dialog. Named consumers: C4b, D budget anchor, E start date, F loan start, G target date.
  - It holds the one `@OptIn` if M3 1.3.1 still marks the API experimental `[Verify]`.
  - The date is shown in the device zone.
  - A new transaction takes the current time of day. An edit keeps the stored time of day when the date changes (Q6).
- **Saving.** Through `launchSave(EntityKind.TRANSACTION, …)`. `EntityKind.TRANSACTION` is new in C4b, the first emitter (digest "Logging").
- **Untouched fields.** An untouched archived category or tag stays as stored (IT §3.3 rule 3, and `TagPicker` already keeps it).

**D9 — Items editor (C4c).** As IT §3.5 and IT C4b:
- `ItemsSection.kt` is local and the items state lives in `TransactionEditViewModel`;
- "Remaining" or "Over by" is reported as `Over(magnitude)`, never a negative `Paise`;
- "Fill remaining" sets an item to the remainder; Add is disabled at 50 items;
- removing down to one item returns to the plain form.

**D10 — Detail and history (C5a).**
- **`AuditHistoryList`** is a new shared block in `ui/common` (consumers: C5a, then F, G, D and E details). It renders already resolved `HistoryEntry(title, changes: List<String>, byline)` in a `GroupedSection`.
- **`TransactionHistoryMapper`** is local to `ui/transactions` and turns audit rows into labels:
  - an amount change reads "Amount ₹200.00 → ₹250.00";
  - an item change reads "Item 2 · Category Groceries → Stationery";
  - a removed item reads "Item 3 removed".
  - Names are resolved with `get(id)`, so archived and tombstoned names still resolve. User and device names come through `IdentityService`, which gains `device(id)` (one read).
- **Actions.** Edit, Delete, and "Mark verified" when the state is UNVERIFIED (L-6).

**D11 — Logging.** No new event shape: `EntitySaveFailed(TRANSACTION)` and `EntityActionFailed(TRANSACTION, DELETE | RESTORE | VERIFY)`. `EntityAction.VERIFY` is new in C5a. No title, note, amount or id in any line. Each new enum value gets a `LogEventShapeTest` sample.

**D12 — Abstraction check.**
- **New interfaces.** Only `TransactionRepository`, `TransactionLineRepository`, `TransactionQueries` and `AuditQueries`, all at the mandated core↔app persistence boundary.
- **New shared blocks.** Each has two or more consumers named in M1a:
  - `TransactionRow` (C4a list, C4d Home recent, C5b results, D4, F3); built in CR K5 with `DayGroup` (amended 2026-11-25);
  - `DateField` (C4b, D, E, F, G);
  - `AuditHistoryList` (C5a, D, E, F, G);
  - `EntityRow.trailing: RowAmount?` (accounts balance now, loans and goals later).
- **Local only.** `ItemsSection`, `AmountKeypad`, `TransactionHistoryMapper`.
- **Rejected.** A `Flow` layer, a generic child-entity writer, a query builder, a money-input abstraction.

**D13 — Schema versions.** C1c is v5 and C3b is v6. C1d, C2 and C3a change no schema.

## 4. Data model & schema

IT §4 is binding, renumbered (v5/v6), with D2's changes:

| Version (PR) | Table | Columns (plus `MetaColumns`) | Indexes and notes |
|---|---|---|---|
| v5 (C1c) | `ledger_transaction` | `type`, `direction`, `amount INTEGER NOT NULL`, `occurred_at INTEGER NOT NULL`, `account_id TEXT NOT NULL` FK→`account`, `title`, `description`, `state`, `source`, `bank_ref`, `source_message_id`, `account_fragment`, `transfer_group_id` (nullable where optional) | `(occurred_at)`, `(account_id, occurred_at)`, `(transfer_group_id)`. **No** `category_id`, no `recurring_rule_id` (D2, Q8), no `CHECK` |
| v5 (C1c) | `transaction_line` | `transaction_id TEXT NOT NULL` FK→`ledger_transaction`, `position INTEGER NOT NULL`, `amount INTEGER NOT NULL`, `category_id TEXT` FK→`category`, `note TEXT` | `(transaction_id)`, `(category_id)`. Rules in core and the mapper |
| v5 (C1c) | `transaction_line_tag` | `line_id` FK→`transaction_line`, `tag_id` FK→`tag`; PK `(line_id, tag_id)` | `(tag_id)`. A non-entity table; audited as `transaction_line.tag_ids` through `AuditedTable.keysStoredIn` (IT C1c) |
| v6 (C3b) | `transaction_fts`, `transaction_line_fts` | FTS4 external content over `title`, `description` / `note`, `unicode61` | Room's sync triggers; shadow tables registered as non-entity tables (D6) |

- **Migration SQL** is copied from the exported `5.json`/`6.json`, never written from memory. Room writes `@Embedded` columns last (digest).
- **Mappers fail closed** on an unknown stored `type`, `direction`, `state` or `source`, and on a non-positive amount or position. The message names the column, never the value.
- **Mismatch count.** `TransactionLineDao.breakdownMismatchCount()` counts live transactions with no live line or with Σ live lines ≠ amount (IT §3.4). It is asserted 0 in every Room transaction test.
- **Canonical schema.** The core models are canonical, and Room entities map one-to-one (CLAUDE.md §3.3).

## 5. Flows

- **Add an expense (C4b).**
  1. The Transactions tab FAB, or a Home quick action with its `type` preset (amended 2026-11-25) → `TransactionEditScreen` → `TransactionEditViewModel.save()`.
  2. Each amount goes through `checkAmount()` (`PaiseParser`).
  3. `TransactionService.create(draft)`:
     - runs `draft.validated()` and `Breakdown.of`;
     - checks the account is live, and the categories (kind) and tags are live;
     - then `runner.inTransaction { writer.create(txn); lines.createAll(NameBasedId("transaction-line", txnId, "1")…); lines.requireBalanced(txnId) }`.
  4. This runs in Room `withTransaction` on the SQLCipher `pft.db`.
  5. Back on the Transactions tab (or Home), `LifecycleResumeEffect` reloads `TransactionQueries.month(…)` (and `recent(5)` on Home).
- **Itemise later.** This is IT §5, through the detail's "Split into items" → `transactions/edit?id=…&split=true`.
- **Transfer (C2, C4b).** `TransferService.create`:
  - writes the OUT leg on `from` and the IN leg on `to`, with one `transfer_group_id`;
  - gives each leg one uncategorised line with the same tags;
  - writes four CREATE audit entries in one transaction.
- **Delete with Undo (C5a).**
  1. Detail → Delete → `TransactionService.delete` (both legs for a transfer).
  2. The detail pops with the deleted id; the screen it returns to shows Undo.
  3. Undo calls `restore`, which refuses with `Deleted(what)` if a reference has been deleted meanwhile (D4).
- **Search (C5b).** Search screen → `SearchViewModel` → `TransactionQueries.search(filter with SearchText.toMatchQuery(text))` → `TransactionQueryDao`, one parameterised query per shape → results as `TransactionRow`s.

## 6. Implementation steps (TDD-ordered)

**Conventions** (as M1a §6 and B §6):
- Core tests are Kotest FunSpec under `core/ledger/src/test/kotlin/dev/imagh/pft/ledger/<pkg>/`. Fakes go in `core/ledger/src/testFixtures/.../fakes/`.
- App JVM tests are Robolectric under `app/src/test/kotlin/dev/imagh/pft/app/`, with `HostedComposeRule` and `awaitMain`.
- Migration tests go in `app/src/testDebug/.../data/MigrationTest.kt`. Instrumented tests run on `pft35`.

**Every step:**
- **Start.** Read `docs/learnings/DIGEST.md` and run `graphify query` before reading source.
- **Tests.** Run Gradle in the background with `tools/agent/check-retry.sh`. Run `./gradlew :core:ledger:check` before a core commit.
- **Proof.** Mutation-check each guard (digest "Test quality").
- **Wrap-up:**
  - add new `*Samples.kt` prefixes to `mustBeRemoved` (`app/build.gradle.kts:170`);
  - write a learnings entry;
  - run `graphify update .` when structure changed.
- **Schema PRs.** Every schema-version PR (C1c, C3b) also runs `EncryptedMigrationTest` and `EncryptedDatabaseTest` together on `pft35` (digest "Data, Room and migrations").

### Step P0 — Record this plan (docs only)
- **Edits:**
  - this file;
  - a pointer block under M1a §6 "Slice C" and in its PR table row;
  - a pointer at the top of IT §6 and its PR table;
  - the stale `MIGRATION_4_5` fixed in IT C3 Green (§2.2 item 7);
  - a learnings entry for the review findings (§2.2).
- **Tier:** T0.

### Step C1a — Transaction and line model, plain lifecycle (core). Uses D2, D3; IT §3.1 to §3.3
- **Red:**
  - `transaction/LedgerTransactionTest` and `TransactionLineTest`: fully populated instances pass `shouldHaveCompleteAuditFields()`, and `tag_ids` is sorted and joined with `,`.
  - `transaction/BreakdownTest`: IT C1a's cases, with notes over 200 characters → `NameTooLong(200)` (§2.2 item 4), and the partition property test.
  - `transaction/TransactionServiceTest`:
    - `create` with one line stores the transaction and line 1 with `NameBasedId.of("transaction-line", txnId.value, "1")`, both audited (CREATE) and attributed;
    - the transaction is MANUAL and VERIFIED (L-6);
    - a blank title is stored as null (Q3);
    - an INCOME category on an expense line → `KindMismatch`;
    - an archived or deleted account, category or tag on create → `Archived`/`Deleted` (no row, no audit);
    - amount 0 → `AmountNotPositive`; above the limit → `AmountTooLarge`;
    - the bank ref is stored (X-1);
    - a one-line `edit` of title or category gives exactly the changed audit fields, and a no-op edit writes nothing;
    - expense → income with a matching category is OK, with a mismatched one → `KindMismatch`;
    - `delete` tombstones the transaction only and `restore` brings back the same lines;
    - `markVerified` on an UNVERIFIED row (seeded through the fake) is audited, and on a VERIFIED one is a no-op.
- **Green:**
  - in `core/ledger/.../transaction/`: `LedgerTransaction`, `TransactionLine`, `TransactionType`, `Direction`, `TxnState`, `TxnSource`, `Breakdown`, `TransactionDraft` (+ `validated()`), `LineDraft`, `TransactionWithLines`, `TransactionRepository`, `TransactionLineRepository` (`liveLinesOf`, `allLineIdsOf`), `TransactionService`;
  - `TransactionId` and `TransactionLineId` in `ids/Ids.kt`;
  - `LedgerError`: `BreakdownMismatch`, `TooManyLines(max)`, `DuplicateLine`, `IsTransfer`;
  - `LedgerLimits`: `MAX_LINES_PER_TRANSACTION`, `MAX_LINE_NOTE_LENGTH`, `MAX_BANK_REF_LENGTH`;
  - testFixtures: `InMemoryTransactionRepository` and `InMemoryTransactionLineRepository`, wired into `LedgerTestKit`;
  - app: map the four new errors in `LedgerErrorMapping.kt` (`BreakdownMismatch` and `TooManyLines` are form-level, the others null) so that the app compiles (§2.2 item 2).
- **Refactor:** `core/ledger/README.md` (public interface, the invariant, limits); `docs/architecture/code-structure.md` (`ledger/transaction/`).
- **Tier:** T3. It defines the hardcoded sum invariant (CLAUDE.md §3.8) and the model every later slice reads.

### Step C1b — Itemisation and reconciliation (core). Uses D3; IT §3.3 rules 3 to 8
- **Red:** `transaction/TransactionItemisationTest` holds every IT C1b case, unchanged:
  - itemise; remove an item; mismatch with rollback;
  - amount rules; foreign or tombstoned line ids;
  - archived references only where they change;
  - income; auto-captured payment facts byte-for-byte unchanged;
  - fail closed through a fake that drops one insert.
- **Green:** `internal class TransactionLines` (reconcile, `requireBalanced`, reference checks), used by `TransactionService.edit`.
- **Refactor:** README "Behaviours" (the reconciliation rules).
- **Tier:** T3. It is a novel algorithm on the integrity invariant.

### Step C1c — Room v5 for transactions and lines. Uses D2, D13, §4; IT C1c
- **Red:**
  - `data/RoomTransactionIntegrationTest`:
    - create and itemise through the services over Room;
    - a foreign key failure on line 3's tag leaves rows and audit exactly as before (INT-4);
    - `breakdownMismatchCount() == 0` after each scenario, and 1 after a planted raw-SQL line;
    - the mapper refuses a stored amount ≤ 0, and an unknown `type` names the column.
  - `RoomAuditColumnsTest`, with both tables registered. It first fails on `tag_ids`.
  - `AuditColumnsCheckTest`: `keysStoredIn` passes only for an existing non-entity table, and a misspelt table fails.
  - `MigrationTest.migrate4To5`: v4 holding an account, a category and a tag migrates; the new tables exist and are empty; existing rows are intact; the triggers survive.
- **Green:**
  - in `app/.../data/transaction/`: `LedgerTransactionEntity`, `TransactionLineEntity`, `TransactionLineTagEntity`, `TransactionDao`, `TransactionLineDao`, `RoomTransactionRepository`, `RoomTransactionLineRepository`, and the mappers;
  - `MIGRATION_4_5`, `VERSION = 5`, `SampleDatabase` entities;
  - `AppServices.transactions`;
  - test support: `AuditedTable.keysStoredIn`, and `transaction_line_tag` in `notEntityTables` with its reason.
- **Refactor:** `app/README.md` (`data/transaction`); `data-model.md` (a real v5 section, the `keysStoredIn` convention, why the invariant is not a trigger); `overview.md` (one line: classification lives on lines).
- **Tier:** T3. Schema and migration.

### Step C1d — In-use refusals. Uses D4; Q4
- **Red:**
  - Core:
    - `AccountServiceInUseTest`: delete with a live transaction → `InUse("account")`, with no tombstone and no audit; with only a tombstoned transaction, delete succeeds.
    - The same for a category (a line of a live transaction) and for a tag.
    - `TransactionServiceTest.restore` refuses `Deleted("account")` after its account was deleted, and the same for a line's category and tag. An archived reference restores fine.
  - Room: `RoomInUseIntegrationTest`, the same over Room, with no tombstone leaking.
  - UI:
    - `AccountsViewModelTest`, `CategoriesViewModelTest`, `TagsViewModelTest`: `InUse` shows the fixed notice and logs nothing (a `ShadowLog` filter on `SafeLog.TAG`);
    - another failure still logs `EntityActionFailed`.
- **Green:**
  - `LedgerError.InUse(what)` and its mapping;
  - the three repository queries (core interface, fake, DAO);
  - the services gain the transaction repositories as constructor parameters (`AppServices`, `LedgerTestKit`);
  - an `InUse` notice in the accounts and tags list ViewModels, copying `CategoriesNotice`;
  - strings.
- **Refactor:**
  - remove the "until slice C" KDoc (`AccountService.kt:24`, `CategoryService.kt:33`, `TagService.kt:26`, `Tag.kt:10`);
  - `docs/features/accounts.md` and `categories-and-tags.md` ("in use: archive instead").
- **Tier:** T2, bumped to T3 by the `data/` path rule.

### Step C2 — Transfers. Uses D3; IT C2; M1a C2
- **Red:** `transaction/TransferServiceTest` (fakes):
  - `create(a → b, ₹500)` gives two live legs with one group, OUT on a and IN on b, with equal amount and time, one uncategorised line each with the same tags, and four CREATE entries;
  - `from == to` → `SameAccount`; a category on a leg → `TransferCannotBeItemised`;
  - editing the amount updates both legs and both lines (the invariant holds on each);
  - deleting either leg through `TransactionService.delete` tombstones both; restore brings both back;
  - `TransactionService.edit` on a leg → `IsTransfer`;
  - a card bill payment is a BANK → CREDIT_CARD transfer.

  Room: `RoomTransferIntegrationTest`. A failure on the second leg's line leaves zero rows and zero audit entries.
- **Green:** `TransferService`, `TransferDraft`, `LedgerError.SameAccount` and `TransferCannotBeItemised` (plus mapping), `AppServices.transfers`.
- **Refactor:** README "Behaviours" (transfers).
- **Tier:** T3. Atomic multi-row writes on the invariant.

### Step C3a — Reads: month, filters, totals, balances, audit history. Uses D5; IT §3.4
- **Red:**
  - Core:
    - `query/TransactionFilterTest`: min ≤ max; from ≤ to; a parent category expands to its children.
    - `query/LedgerEntryFoldTest`: two legs fold into one `Transfer` without an account filter; with one, the leg on that account is a `Single`; a lone leg (data damage) stays a `Single`, never dropped.
  - `data/TransactionQueriesIntegrationTest` (Room, zone `Asia/Kolkata`):
    - the month window includes 2026-10-31T23:30 IST and excludes 2026-11-01T00:10 IST;
    - the account, category (parent includes children, matched on any live line), tag, amount, state and type filters each work;
    - a category filter on Groceries returns the ₹2,000 itemised transaction once, with a total of ₹1,200;
    - tombstoned transactions and lines are excluded;
    - totals exclude transfers;
    - `accountBalance` = opening + Σ IN − Σ OUT and is unchanged by itemising; a credit card goes negative;
    - search is capped at `MAX_SEARCH_RESULTS` with `truncated`.
  - `data/AuditQueriesIntegrationTest`: `forTransaction` returns the transaction's and its lines' entries, removed lines included, oldest first, and none from another transaction.
- **Green:**
  - `ledger/query/{TransactionFilter, TransactionQueries, MonthPage, SearchPage, LedgerEntry, Totals}`;
  - `ledger/audit/AuditQueries`;
  - fakes;
  - `app/.../data/transaction/TransactionQueryDao` plus `RoomTransactionQueries`, and `RoomAuditQueries` on `AuditDao`;
  - `AppServices(zone = ZoneId.systemDefault())`.
- **Refactor:** README (reads); `code-structure.md` (`ledger/query/`).
- **Tier:** T3. Money totals over a new query layer; `data/` path.

### Step C3c — Sort, person filter, recent, month span (new 2026-11-25). Uses D5 (amended); amendment item 5
Kept out of C3a so C3a stays one reviewable read layer; no schema change.
- **Red:**
  - Core: `query/TransactionSortTest`: the default is `OCCURRED` descending; `TransactionFilterTest` gains `createdBy` (empty = everyone).
  - `data/TransactionQueriesIntegrationTest` (Room, `Asia/Kolkata`):
    - each sort key orders a seeded month as expected in both directions, ties broken by id (seed colliding values with chosen ids so a naive order differs; digest "Test quality");
    - `TITLE` sorts case-insensitively with no-title entries last; `AMOUNT` sorts a transfer by its amount;
    - `search` applies the sort before the `MAX_SEARCH_RESULTS` cap (the 500 kept are the first 500 in that order);
    - `createdBy` returns only that user's rows, and totals follow the filter;
    - `recent(5)` returns the newest five live entries across months, transfers folded, tombstones excluded; `recent` with no data is empty;
    - `entryMonthSpan` is null with no live rows and spans a back-dated and a future-dated entry otherwise.
  - Prove the sort is not concatenated: an integration test passes every enum value; a `LoggingScanTest`-style source check is not needed because the key is an enum mapped to a bound integer.
- **Green:** `ledger/query/{TransactionSort, MonthSpan}`, `TransactionFilter.createdBy`, the `sort` parameter, `recent`, `entryMonthSpan`; DAO `ORDER BY CASE :sortKey …` with bound values; fakes.
- **Refactor:** `core/ledger/README.md` (reads); `data-model.md` (no index added; the month window index still serves these reads at household scale, recorded).
- **Tier:** T3. Queries on money, `data/` path.

### Step C3b — Full-text search (v6). Uses D6
- **(Amended 2026-11-25)** Also `TransactionFilter.titleText` and `noteText`: `SearchText.toMatchQuery(raw, column)` prefixes each quoted token with a core-chosen column (`title:` on `transaction_fts`; `description:` on `transaction_fts` OR `note:` on `transaction_line_fts` for notes). Extra Red: `SearchTextTest` — a raw `title:` typed by the user is a literal token, never a column filter; `RoomFtsIntegrationTest` — `titleText "swi"` matches the title "Swiggy" but not a note "swiss"; `noteText "notebook"` matches an item note and a description, not a title.
- **Red:**
  - `query/SearchTextTest` (pure):
    - `swi` becomes `"swi"*`;
    - `"a" OR b* NEAR -c` keeps only `a`, `OR`, `b`, `NEAR` and `c` as quoted literal tokens;
    - empty, punctuation-only or whitespace-only input → null;
    - a 10-token input keeps 8;
    - a 100-character token is cut to 32;
    - Devanagari letters are kept.
  - `data/RoomFtsIntegrationTest`:
    - "swiggy" matches the title "Swiggy order";
    - "notebook" matches an item note;
    - a tombstoned transaction or line does not match;
    - an edited title re-indexes;
    - `rebuildAfterVacuum`;
    - hostile input (`'`, `"`, `*`, `NEAR(`, a NUL character) neither throws nor matches everything.
  - `MigrationTest.migrate5To6` backfills both indexes for rows written at v5.
  - Instrumented `EncryptedFtsTest` on `pft35`: the same match on SQLCipher.
- **Green:**
  - the FTS entities, the `MIGRATION_5_6` SQL from `6.json` (including `rebuild` statements for the backfill), `VERSION = 6`;
  - the search predicate in `TransactionQueryDao`;
  - shadow tables in `notEntityTables`;
  - the `MigrationTestSupport` KDoc wording fixed.
- **Security:** record in the PR the bundled SQLite version and the reachability of the FTS CVEs (M1a C3 follow-up). FTS4 only, a bound and sanitised parameter.
- **Refactor:** `data-model.md` (v6, the rowid/rebuild rule); `persistence-and-security.md` (the FTS input rule).
- **Tier:** T3. Schema, and untrusted text reaching SQL.

### Step C4a — Transactions tab: month strip, totals banner, day list (rewritten 2026-11-25). Uses D5, D7, D12; CR D8, K5
Builds on CR's `TransactionRow`, `DayGroup`, `PftTabBar` and money rules (after C-sync). Account balances moved to C4d.
- **Red:**
  - `ui/transactions/TransactionRowMapperTest`:
    - one line → its category glyph and name, "category · account" meta, tag mini-chips;
    - three lines → `RECEIPT_LONG` and "3 items";
    - a transfer → the transfer glyph and "HDFC → Card", with no sign;
    - a blank title falls back to the category name, then the type label (Q3);
    - amounts are always non-negative `Paise` with the type's semantic (DS5 contract); expenses neutral (CR D5);
    - UNVERIFIED adds the `StatusChip` label.
  - `ui/transactions/MonthStripTest` (pure + Compose): the months span `min(first, now)..max(last, now)` from `entryMonthSpan`, or just this month with no data; the selected month is underlined and `selected` in semantics; January and the first month carry the year; tapping a month selects it; the selected month is scrolled into view.
  - `ui/transactions/TransactionsViewModelTest`:
    - loads this month in the zone; selecting a month or swiping changes it; next is allowed past today;
    - the banner totals equal the seeded sums and net = income − expense (`Net`, may be negative);
    - day totals are the net of each complete day.
  - `ui/nav/TransactionsTabFlowTest` (real in-memory Room through `PftNavHost`):
    - the bar shows Home · Transactions · Accounts · More; Transactions shows seeded rows under day headers with signed amounts and day totals;
    - the banner shows expense ▼, income ▲ and = net with words for TalkBack;
    - the empty month shows `EmptyState`;
    - switching to Accounts and back keeps the selected month (tab `saveState`).
  - Goldens: the Transactions tab with fake data, light and dark; `tab-bar-*` re-recorded for the fourth tab.
- **Green:**
  - `ui/transactions/{TransactionsScreen, TransactionsViewModel, TransactionRowMapper, MonthStrip, TotalsBanner}`;
  - the `transactions_tab` graph and its `Tabs` entry at position 2;
  - the `swap_horiz` symbol through `tools/material-symbols` if `PftSymbol.TRANSFER` is not it;
  - the "C4 NavigationBar" KDoc lines reworded (§2.1).
- **Refactor:** `app/README.md` (`ui/transactions`); `code-structure.md`; `docs/features/transactions.md` created with "The Transactions tab". Measure the release APK.
- **Tier:** T2. First read UI on the shared row; money totals displayed.

### Step C4b — Add and edit form (plain, one item). Uses D7, D8
- **(Amended 2026-11-25)** The FAB lives on the Transactions tab (above the tab bar's clearance). The route takes `type` (D7): extra Red in `TransactionEditViewModelTest` — `type=INCOME` opens in Income mode, `TRANSFER` in Transfer mode, an unknown or missing value in Expense mode; `type` is ignored on edit. Flow assertions below read "the Transactions tab row" for "the Home row".
- **Red:**
  - `ui/transactions/TransactionEditViewModelTest`:
    - each field is checked with core's rule;
    - a bad amount → `InvalidAmount` and nothing is saved;
    - transfer mode needs a different destination;
    - switching expense to income clears an expense category and shows the notice;
    - a second Save while saving is ignored (mutation-proved);
    - the failure path keeps the typed text and logs `EntitySaveFailed(TRANSACTION)` only;
    - an unknown id → not found;
    - the edit keeps the time of day (Q6).
  - `ui/common/DateFieldTest`: the picked date round-trips in a non-UTC zone, and there is a 48dp target.
  - `ui/nav/TransactionsFlowTest`:
    - FAB → enter ₹250, pick an account, category and two tags → Save;
    - the Home row shows −₹250.00 and the totals update;
    - tapping the row opens edit; changing the amount updates the row;
    - Transfer mode with a destination saves one row and leaves the totals unchanged;
    - an archived tag already on the item survives a title edit.
- **Green:**
  - `ui/transactions/{TransactionEditScreen, TransactionEditViewModel, TransactionEditActions}`;
  - `ui/common/DateField.kt`;
  - routes in `ui/nav/TransactionRoutes.kt`;
  - `EntityKind.TRANSACTION` with its sample;
  - strings.
- **Refactor:** `docs/features/transactions.md`, created (add, edit, transfer, what is fixed); `app/README.md`.
- **Tier:** T2. Untrusted input on a form, and a new shared block.

### Step C4d — Home dashboard and account balances (new 2026-11-25). Uses D5, D7; CR D6, D9, K6, K8
Lands after C4b so the quick actions open a real form; before C4c (independent of it).
- **Red:**
  - `ui/home/HomeViewModelTest`: loads this month's totals in the zone and `recent(5)`; reloads on resume; the identity still loads first (no flash).
  - `ui/home/HomeFlowTest` (real in-memory Room through `PftNavHost`):
    - the balance card shows this month's expense total as the hero and In/Out as words + amounts; no budget meter (slice D);
    - quick actions are exactly Expense, Income, Transfer (no Budgets); each opens the form in that mode; saving returns to Home with the card and recent list updated;
    - "Recent transactions" shows the newest five under day headers **without** day totals (CR D6), and "See all" selects the Transactions tab on this month;
    - with no transactions, the card shows ₹0.00 and the recent list shows `EmptyState` with an Add action.
  - `ui/accounts/AccountRowMapperTest`: a trailing `MoneyText(Balance)`, negative (red) for a card in use; `AccountsFlowTest`: the Accounts tab shows balances.
  - Goldens: Home with fake data (light, dark); the accounts list with balances. Re-record only what changed.
- **Green:** `ui/home/{HomeScreen, HomeViewModel}` composing CR's `GreetingHeader`, `BalanceCard`, `QuickActionRow` and `DayGroup`/`TransactionRow`; `EntityRow.trailing: RowAmount?`; `HomeUiState` with totals and recent entries.
- **Refactor:** `app/README.md` (`ui/home`, `EntityRow.trailing`); `docs/features/transactions.md` "Home"; `docs/features/accounts.md` (balances).
- **Tier:** T2. Money on the tinted card and a derived balance shown to the user.

### Step C4c — Items editor. Uses D9; IT C4b
- **Red:** IT C4b's `TransactionItemsFlowTest`, `ItemsEditorStateTest` and golden, unchanged, on the C4b form. Accessibility:
  - each item's remove button is labelled "Remove item 2";
  - the Remaining line is a polite live region;
  - the targets are 48dp.
- **Green:** `ui/transactions/ItemsSection.kt` and the items state in `TransactionEditViewModel`.
- **Refactor:** the "Items" section of `docs/features/transactions.md`.
- **Tier:** T2. Tricky state and accessibility.

### Step C5a — Detail, history, delete with Undo, mark verified. Uses D7, D10, D11
- **Red:**
  - `ui/transactions/TransactionHistoryMapperTest`:
    - an amount change reads "Amount ₹200.00 → ₹250.00";
    - an item change reads "Item 2 · Category Groceries → Stationery";
    - a removed item reads "Item 3 removed";
    - a tag ids diff becomes names;
    - a tombstoned category's name still resolves;
    - no raw id is ever shown;
    - the byline reads "by Akash on Pixel".
  - `TransactionDetailViewModelTest`:
    - delete pops with the id;
    - restore refused with `Deleted` shows its notice;
    - verify is audited, and is offered only when UNVERIFIED.
  - `ui/nav/TransactionDetailFlowTest`:
    - row → detail shows the facts, source and bank ref;
    - an Items section appears only when there is more than one line;
    - "Split into items" opens the editor;
    - Delete → Home shows Undo → Undo restores the row with its items;
    - letting Undo time out keeps it deleted (clock advanced).
- **Green:**
  - `ui/transactions/{TransactionDetailScreen, TransactionDetailViewModel, TransactionHistoryMapper}`;
  - `ui/common/AuditHistoryList.kt`;
  - `IdentityService.device(id)`;
  - `EntityAction.VERIFY`;
  - the row tap now opens the detail.
- **Refactor:** "Detail and history" in the feature doc; `app/README.md`.
- **Tier:** T2. A new shared block; read-mostly.

### Step C5b — Search (amended 2026-11-25: filters and sort split out). Uses D5, D6, D7
- **Red:**
  - `ui/search/SearchViewModelTest`:
    - typing debounces (300 ms on a test clock);
    - an empty query shows nothing (not every row);
    - `truncated` shows "Showing the first 500".
  - `ui/nav/SearchFlowTest`:
    - the Transactions tab's Search action opens search (the tab bar hides);
    - "notebook" finds the itemised transaction; results are `DayGroup`s of `TransactionRow`s across months;
    - a result opens its detail; Back returns to search, then to the Transactions tab.
- **Green:** `ui/search/{SearchScreen, SearchViewModel}`; the `SEARCH` route; a `HeaderIconButton(SEARCH)` on the Transactions tab.
- **Refactor:** the "Search" section of the feature doc.
- **Tier:** T2. Untrusted text, sanitised in core.

### Step C5c — Filters sheet and active-filter chips (new 2026-11-25). Uses D5 (amended), D7
Modelled on Cashew's filter sheet; applies to the Transactions tab's month list **and** its banner.
- **Red:**
  - `ui/transactions/FilterStateTest` (pure): builds a valid `TransactionFilter` from the sheet; "All" categories clears `categoryIds`; amount range in whole rupees maps to `Paise` (`amountMin` ≤ `amountMax`); blank title/notes text → null; Clear resets everything; the chip row lists one chip per active filter, and removing one removes only it.
  - `ui/transactions/FilterSheetTest`:
    - category tiles row ("All" + top-level categories, `CategoryGlyph` cells, `selectable(role = Checkbox)` in one `selectableGroup`; a parent includes its children in core);
    - amount `RangeSlider` from ₹0 to the month's largest amount rounded up to ₹100, values shown as `MoneyText` ([Verify] `RangeSlider` is stable in the pinned M3; else the one `@OptIn` lives here);
    - type chips Expense / Income / Transfer; account chips (active accounts); person chips only when `IdentityService.users()` has ≥ 2 users (positive and negative control);
    - "Title contains" and "Notes contains" fields (untrusted text, length-capped by `SearchText`);
    - Clear and Apply; Apply closes the sheet (`PftBottomSheet` `close` lambda).
  - `ui/nav/TransactionsFilterFlowTest`: a Groceries filter shows the matching rows and a banner expense of ₹1,200 for the ₹2,000 itemised transaction (category totals count matching lines); the chips row appears with an X that clears all; switching tabs and back keeps the filter.
- **Green:** `ui/transactions/{FilterSheet, FilterState, ActiveFilterChips}`; the overflow menu (`HeaderIconButton(OVERFLOW)` + `DropdownMenu`) with "Filters".
- **Out of M1a (Cashew chips):** upcoming/subscription/repeating and paid/not paid/skipped (slice E), budget (D), loan (F), goal (G), state (M1b) — amendment item 5.
- **Refactor:** "Filters" in the feature doc.
- **Tier:** T2. Untrusted text and a multi-part form state.

### Step C5d — Sort sheet (new 2026-11-25). Uses D5 (amended), D7
- **Red:**
  - `ui/transactions/SortSheetTest`: keys Date, Date added, Date updated, Amount, Title as single-choice (`Role.RadioButton` in a `selectableGroup`); an ascending/descending segmented control; Clear restores Date ↓; Apply closes.
  - `TransactionsViewModelTest`: a non-date sort shows a flat list without day groups (day grouping only means something under a date sort); totals are unchanged by sort.
  - `TransactionsFilterFlowTest` (extend): Amount ↑ orders the month's rows by amount; the choice survives a tab switch.
- **Green:** `ui/transactions/SortSheet.kt`; "Sort" in the overflow menu.
- **Refactor:** "Sort" in the feature doc.
- **Tier:** T1. Follows the filter sheet's pattern; the ordering is core's (C3c).

### Step C6 — Everyday polish: keypad and motion. Q5; design plan C4/C5
- **Red:**
  - `ui/transactions/AmountKeypadTest`:
    - the keys produce text that `PaiseParser` accepts;
    - a third decimal digit and over-long input are refused;
    - backspace works;
    - each key has a label and a 48dp target.
  - The add form uses the keypad for the top-level amount: the flow tests stay green, and the soft keyboard does not open for the amount.
  - Motion:
    - FAB → add and row → detail use `SharedElements`;
    - `animateItem` on insert and delete;
    - reduced motion skips them (assertion via `PftMotion`).
  - Run the release-build jank check on `pft35` (design-system plan §11) and record it in learnings.
- **Green:** `ui/transactions/AmountKeypad.kt`, `ui/nav/SharedElements.kt` (the one `SharedTransitionLayout` opt-in), and the `backspace` symbol.
- **Tier:** T2. An experimental API, and a measurement.

### Step C-final — Integrate
- **Edits:**
  - root `README.md` status ("daily manual entry");
  - the M1a "As built (C)" note;
  - check the feature doc against the app on the emulator;
  - `graphify update .`.
- **Tier:** T0.

### PR breakdown (incremental — required)

**Target branches.**
- P0 is one docs branch → `main`.
- Then cut the base `feat/m1a-transactions` from `main`. Dev branches are `feat/m1a-transactions-<slug>` (a ref cannot be a branch and a directory; digest "Git").
- Each slice PR targets the base. C-final merges the base → `main`.

| PR | Scope | Target | Depends on | Tier (CLAUDE.md §8) | Why it's independently reviewable | Was (M1a / IT) |
|----|-------|--------|------------|------|-----|-----|
| P0 | This plan, pointers, IT fix, learnings | `main` | — | T0 docs | the decision to review | — |
| C1a | model, `Breakdown`, plain lifecycle (core) | base | P0 | T3 invariant | pure core, one-line transactions | IT C1a (minus in-use) |
| C1b | itemisation, `TransactionLines` | base | C1a | T3 algorithm | one rule set | IT C1b |
| C1c | Room v5 | base | C1b | T3 schema | adapter and migration only | IT C1c |
| C1d | in-use refusals + list notices | base | C1c | T3 (T2 + `data/` bump) | one rule across three entities | new (§2.2 item 1) |
| C2 | transfers | base | C1c (independent of C1d) | T3 atomic pairs | one behaviour | M1a/IT C2 |
| C3a | reads, totals, balances, `AuditQueries` | base | C2 | T3 queries | read side, no schema | M1a/IT C3 part |
| C3c | sort, person filter, `recent`, month span (new 2026-11-25) | base | C3a | T3 queries | read additions, no schema | new |
| C3b | FTS, v6; title/notes scoped text (amended) | base | C3c | T3 schema + untrusted input | one search mechanism | M1a/IT C3 part |
| C-sync | merge `main` (Cobalt, CR K-final) into the base | base | C3b, CR K-final | T0 merge | no new code | new |
| C4a | Transactions tab: month strip, banner, day list (rewritten) | base | C-sync | T2 shared row, money totals | first read UI | M1a C4 part |
| C4b | add/edit form (plain), `DateField`, `type` preset | base | C4a, C2 | T2 forms | one form | M1a C4 part |
| C4d | Home dashboard, account balances (new) | base | C4b, C3c | T2 money on the card | one screen + one row field | new |
| C4c | items editor | base | C4b (after C4d in order) | T2 state/a11y | one section | IT C4b |
| C5a | detail, history, delete/Undo, verify | base | C4c, C3a | T2 shared history | one screen | M1a C5 part |
| C5b | search (amended: filters, sort split out) | base | C5a, C3b | T2 untrusted text | one screen | M1a C5 part |
| C5c | filters sheet + active chips (new) | base | C5b | T2 untrusted text, form state | one sheet | new |
| C5d | sort sheet (new) | base | C5c | T1 follows C5c | one sheet | new |
| C6 | keypad and motion | base | C5d | T2 experimental API | polish only, revertable | M1a C4/C5 part |
| C-final | slice C → `main` | `main` | C1a–C6 (with C3c, C-sync, C4d, C5c, C5d) | T0 merge | one capability | as planned |

**Tier for the next session.**
- P0 is T0, so a Sonnet orchestrator suffices.
- C1a to C3b are T3: owner, start those sessions on Opus.
- The owner can start daily entry once C4b merges into the base. It reaches `main` at C-final.
- (Amended 2026-11-25) C3c is T3 (Opus). C-sync is T0. C4a, C4d, C5c are T2; C5d is T1.

## 7. Test plan

**Unit (core, Kotest, ≥ 90%):**
- `BreakdownTest` (property);
- `LedgerTransactionTest`, `TransactionLineTest`;
- `TransactionServiceTest`, `TransactionItemisationTest`, `TransferServiceTest`;
- the in-use tests;
- `TransactionFilterTest`, `LedgerEntryFoldTest`, `SearchTextTest`.

All assert stored rows and audit entries through the fakes' reads, not mock calls.

**Integration (Robolectric, real in-memory Room):**
- `RoomTransactionIntegrationTest`, `RoomTransferIntegrationTest`, `RoomInUseIntegrationTest`;
- `TransactionQueriesIntegrationTest`, `AuditQueriesIntegrationTest`, `RoomFtsIntegrationTest`;
- `RoomAuditColumnsTest`, `AuditColumnsCheckTest`;
- `MigrationTest.migrate4To5` and `migrate5To6`, plus the chain;
- the ViewModel and flow tests named in C4a to C5d (C3c's integration tests run with C3a's).

**Instrumented (`pft35`):** `EncryptedMigrationTest` (to v5, then v6), `EncryptedDatabaseTest`, `EncryptedFtsTest`.

**Edge and failure cases:**
- one paisa off, an empty item list, 51 items, a zero amount, an amount over the limit;
- a foreign or tombstoned line id;
- an archived reference that is untouched vs one that changed;
- a kind mismatch; the same account on both sides; a category on a transfer;
- a foreign key failure on the last line; a failure on the second leg;
- delete and restore with a deleted reference;
- the IST month edges; a future date;
- hostile search text; the truncation cap;
- an over-allocated editor (no negative `MoneyText`);
- a no-op edit (no audit row).

**AC → test map**

| AC | Tests |
|---|---|
| L-1, L-11 | `BreakdownTest`, `TransactionServiceTest`, `TransactionItemisationTest`, `TransactionItemsFlowTest`, `TransactionDetailFlowTest` |
| L-3 | `TransactionLineTest`, `TransactionQueriesIntegrationTest` (tag filter), `TransactionsFlowTest` |
| L-5 | `TransferServiceTest`, `RoomTransferIntegrationTest`, totals in `TransactionQueriesIntegrationTest` |
| L-6 | `TransactionServiceTest` (manual = VERIFIED, `markVerified`), `TransactionDetailViewModelTest` |
| L-7 | `TransactionQueriesIntegrationTest`, `RoomFtsIntegrationTest`, `SearchFlowTest` |
| L-8 | attribution asserts in the service tests; history byline |
| L-9 | `account_id NOT NULL`; `accountBalance` tests |
| X-1 (manual) | bank ref stored (`TransactionServiceTest`), shown (`TransactionDetailFlowTest`) |
| INT-2, INT-3 | `shouldHaveAudited` throughout; delete/restore and removed-item tests; `AuditQueriesIntegrationTest` |
| INT-4 | rollback tests in C1b, C1c, C2 |
| INT-5 | `migrate4To5`, `migrate5To6`, `EncryptedMigrationTest` |
| INT-6 | balances and totals computed from rows; `breakdownMismatchCount` |
| SEC-5 | the `ShadowLog` assertions in C1d and C4b; `LogEventShapeTest` |

## 8. Security considerations

- **Untrusted input:**
  - every typed field is checked in the ViewModel with core's rule, then again in core (amount grammar, lengths, live references only where they change);
  - route ids are untrusted text;
  - search text passes through `SearchText.toMatchQuery` and is then bound.
- **Queries:** Room DAOs only, all parameterised; `IN` lists are bound; no `@RawQuery`; FTS `MATCH` input is a bound parameter.
- **Known risk to record in C3b:** the bundled SQLite (3.49.1 in sqlcipher-android 4.7.2) and the FTS CVE reachability. The H3 bump stays planned.
- **Integrity (CLAUDE.md §3.8):**
  - the sum invariant is hardcoded and fails closed (refusal, or rollback after write);
  - itemising never changes the payment facts;
  - in-use refusals stop a delete from orphaning history;
  - restore cannot revive a reference to a deleted record.
- **Logs:** enums only (D11). No title, note, amount, account name or id.
- **No new dependency, permission, exported component or scope.** `FLAG_SECURE` already covers every screen.

Run `/security-review` before every PR (TECHNICAL.md §5), and in particular record it for C1c, C3b and C4b.

## 9. Documentation updates (all required surfaces)

| Surface | What | PR |
|---|---|---|
| This plan; M1a §6 and PR-table pointers; IT §6 pointer and the `MIGRATION_4_5` fix | new / pointers | P0 |
| Inline KDoc | every public type and function above; reword the "C4 NavigationBar" and "until slice C" KDoc | each PR (C1d, C4a) |
| `core/ledger/README.md` | interface, invariant, reconciliation, transfers, in-use, reads, limits | C1a, C1b, C1d, C2, C3a, C3b |
| `app/README.md` | `data/transaction`, FTS, `ui/home`, `ui/transactions`, `ui/search`, new shared blocks | C1c, C3b, C4a–C6 (incl. C4d, C5c, C5d) |
| `docs/architecture/data-model.md` | v5, `keysStoredIn`, mismatch count, v6, the FTS rebuild rule | C1c, C3b |
| `docs/architecture/code-structure.md` | `ledger/transaction`, `ledger/query`, `data/transaction`, `ui/transactions`, `ui/search`, shared blocks | C1a, C1c, C3a, C3c, C4a, C4b, C4d, C5a, C5b, C5c |
| `docs/architecture/overview.md` | classification lives on lines | C1c |
| `docs/architecture/persistence-and-security.md` | FTS input rule | C3b |
| `docs/features/transactions.md` | created: add/edit/transfer (C4b); Items (C4c); detail and history, delete, verify (C5a); search (C5b); the Transactions tab (C4a); Home (C4d); filters (C5c); sort (C5d) | C4a–C5d |
| `docs/features/accounts.md`, `categories-and-tags.md` | in use → archive; account balance | C1d, C4d |
| `docs/learnings/README.md` (+ `DIGEST.md` for standing rules) | each PR | all |
| Root `README.md` | status | C-final |

## 10. Risks & mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| A reconciliation bug leaves lines that do not sum | wrong totals | `Breakdown`, the post-write `requireBalanced`, the mismatch count in every Room test |
| `TooManyFunctions` forces a mid-PR redesign | stalled C1b/C2 | D3 split decided up front |
| Room rejects `contentEntity` on a TEXT-id table, or Robolectric lacks FTS4 | C3b stalls | the D6 fallback, decided in Red; one instrumented test on SQLCipher |
| A future `VACUUM` or export silently breaks search | wrong search results | D6 rebuild rule, its test, `data-model.md` |
| Month boundaries are wrong across zones | rows in the wrong month | zone passed in; IST edge tests; future-date test |
| The Home list is slow at 50k rows | sluggish UI | month window, indexes, a window join for lines; measured in H3 |
| The slice grows past one session per PR | usage limits hit | 13 small PRs, each stating its tier and decisions by section |
| `MoneyText` throws on a negative value | crash | mappers pass magnitudes; mapper tests (C4a, C4c, C5a) |

## 11. Rollout & verification

**Per PR:**
- `bash tools/agent/check-retry.sh <worktree> <tag>` (background) is green;
- C1c and C3b also run the encrypted tests on `pft35`;
- C4a, C4b and C6 measure the release APK by entry (digest "APK size").

**End to end on the emulator (`./gradlew installDebug` on `pft35`, after C5d, with fake data). Amended 2026-11-25: read "Home" in steps 2–3 as the Transactions tab (rows, banner) and Home's balance card; after step 6 also apply Sort Amount ↑ and a Title-contains filter:**
1. Create two accounts (HDFC bank, a credit card) on top of the seeded categories.
2. Add a ₹540 Food › Delivery expense with two tags. Home shows −₹540.00 under Today, and the hero shows ₹540.00 spent.
3. Add ₹1,20,000 salary income. The caption shows income and net.
4. Add a ₹12,000 transfer HDFC → card. It is one neutral row and the totals do not change. The accounts list shows HDFC down by ₹12,000 and the card's balance up.
5. Add ₹2,000 on the card, then from its detail split it into Groceries ₹1,200, Stationery ₹300 (note "notebook") and Utilities ₹500. The row shows "3 items".
6. Search "notebook" and find it. Filter by Groceries: the total is ₹1,200.
7. Open the detail. The history shows the item lines "by <name> on <device>".
8. Delete the transaction, Undo it, and it returns with its three items. Delete it again and let Undo lapse.
9. Try to delete the Groceries category and the HDFC account. Each is refused with "archive instead".
10. Step to last month (empty state) and back.

**Inspect:**
- `adb logcat` for the app's pid shows no amount, title or note;
- `run-as … head -c 16 databases/pft.db` is not the SQLite header.

**Rollback:**
- Each PR reverts on its own inside the base before C-final. Migrations are forward-only after C-final.
- Reverting C4c leaves every stored transaction valid, because a plain one is one line.

## 12. Open questions

Each has a recommended default that applies unless the owner says otherwise. **Answered 2026-11-18:** the owner accepted every default.

| # | Question | Default |
|---|---|---|
| Q1 | Keep Home and search on load-on-demand (reload on resume and after own actions) and leave `Flow` and kotlinx-coroutines out of core in slice C? (M1a `:130` expected coroutines here; §2.2 item 11) | **Yes.** Revisit in E4 or M1b, when background inserts exist |
| Q2 | Defer the bottom `NavigationBar` until a third tab exists (Budgets, D4), keeping More and adding Search in Home's header? | ~~**Yes.** A two-tab bar is chrome without choice~~ **Reversed 2026-11-25:** tab bar now (Home · Transactions · Accounts · More), CR D8; Search moves to the Transactions tab header |
| Q3 | Is a transaction's title optional? (Blank is stored as none, and the row shows the category, then the type) | **Optional.** M1b captures may have no payee |
| Q4 | Refuse deleting an account, category or tag that a live transaction or item uses ("archive it instead"), and refuse restoring a transaction whose account, category or tag was deleted since? | **Yes to both** |
| Q5 | Ship the add form first with the existing `AmountField`, and add the `AmountKeypad` and shared-element motion in a separate polish PR (C6) after search? | **Yes.** It keeps C4b one session and lets you judge the keypad on the emulator |
| Q6 | No time-of-day picker in M1a: a new entry takes the current time, and changing the date keeps the time? | **Yes** |
| Q7 | Move `spendingLinesBetween` from C3 to budgets (D2/D3), its first consumer? | **Yes** |
| Q8 | Drop `recurring_rule_id` from v5 and add it in E3 with `ALTER TABLE`, as F2 and G1 do for their links? | **Yes** |
| Q9 | Accept the split into 13 PRs with the new ids (C1d, C3a/C3b, C4a/C4b/C4c, C5a/C5b, C6), where IT's "C4b items editor" becomes C4c? | **Yes** |
