# Plan: Itemised transactions (one payment, many categories)

| Field       | Value |
|-------------|-------|
| Date        | 2026-11-10 |
| Author      | architect |
| Status      | Approved (2026-11-10; Q1–Q4 and Q6 defaults accepted; Q5 changed: statistics before invoice → items, §12) |
| Related ACs | PRD L-1 (amended), new L-11; L-3, L-7, B-1..B-3 (budgets count item portions), X-1/X-2 (unchanged: one message is still one transaction), INT-2, INT-3, INT-4, INT-6. Notes two future items: invoice-to-items capture (new roadmap R-4) and statistics (new roadmap R-5) |
| Plan owner  | akashg (repo owner) |

Foundation: slice A is merged (`main` at `75580d6`). This plan **amends the approved M1a plan** ([`2026-10-28-m1a-core-ledger.md`](2026-10-28-m1a-core-ledger.md)) before slice C is built. **Slice B (accounts, categories, tags) is not affected and can start now.** The only thing that must merge before slice C starts is PR P0 below, which is docs only.

---

## 1. Requirement

**Need.** One real payment often covers several purposes. For example, one ₹2,000 supermarket card payment might be ₹1,200 of groceries, ₹300 of stationery and ₹500 of household utilities. Today's M1a design gives every transaction exactly one category and one set of tags (M1a plan §4, `docs/plans/2026-10-28-m1a-core-ledger.md:170-171`). To record that payment correctly, the owner would have to split it into three transactions. That breaks the link to the bank's single debit, and with it reconciliation (X-1, X-3), idempotent capture (X-2) and the account balance trail. The owner wants to keep **one transaction per real payment** and then **break it down into items**, each with its own amount, category or sub-category, tags and note. This must work whether the transaction was entered by hand or (from M1b) auto-captured from SMS or email, and it must be possible both at creation and later.

**Done means:**
- Any transaction can be itemised into 1 to `MAX_LINES_PER_TRANSACTION` items. Each item has an amount (> 0), an optional category or sub-category, tags and an optional note. Item amounts **always sum exactly to the transaction amount**, in integer paise. A save that breaks this is refused and nothing is written.
- A transaction that is not itemised is simply a transaction with **one** item. That is the same data shape, not a separate mode, so every consumer reads one model.
- Itemising never changes the payment facts (amount, account, time, bank reference, source, state). For an auto-captured transaction those stay exactly as captured; only the classification is edited. Every item create, edit and removal is audited and attributed to a user and device. A removed item is tombstoned, never hard-deleted.
- Category and tag views count **each item's portion**:
  - A category filter or a category budget counts ₹1,200 of the example payment to Groceries, not ₹2,000.
  - An overall budget still counts ₹2,000, once.
  - Text search also matches item notes.
- The list shows an itemised transaction as one row ("3 items"). The detail screen shows the items and a history that includes item changes.

**Out of scope:**
- **Invoice image/PDF → items (future; roadmap R-4).** This plan only records it and the constraints it must respect (§3.7).
- **Statistics and graphs (future; roadmap R-5).** This plan only records it (§3.7). The data shape chosen here, one item per classified amount, is the fact table those statistics will read.
- Negative items (discount or refund lines inside an invoice). The user nets a discount into the other items.
- Per-item links to loans or goals. These links stay on the whole transaction (LN-5 neutralises the whole purchase; G-2 links the whole contribution).
- Recurring templates with several items. A template still has one category and one set of tags, so it generates a one-item transaction.
- M2's "split a transaction across shared entities and users" (PRD §14, G-3, `docs/PRD.md:169`, `:337`). That is a different feature. The word **"split" is deliberately not used** for this one; it is "items" in the UI and "lines" in code.

## 2. Current state

- **No transaction code exists yet.** `core/ledger` holds only the foundations (`ids/`, `identity/`, `audit/`, `entity/`, `settings/`, `tx/`, `validation/`), and the database is at v1 (`app/schemas/.../1.json`). Transactions are slice C (plan lines 279-300). So this is a **design change before build, not a retrofit**: the item table enters the schema in C1's own migration (v4), and there is no data migration.
- **What the approved design says today, and what this plan changes** (line numbers are the M1a plan as of `75580d6`, before P0 added its amendment blocks):

  | Plan location | What it says today |
  |---|---|
  | `2026-10-28-m1a-core-ledger.md:170` | `ledger_transaction.category_id NULL FK` |
  | `:171` | `transaction_tag (transaction_id, tag_id)` |
  | `:172` | FTS over title and description |
  | `:186` | `BudgetCalculator` over transactions |
  | `:282` | C1 validates "an INCOME category on an expense" per transaction |
  | `:290` | C3 filters "category (the parent includes its sub-categories), tag" on the transaction |
  | `:311-314` | D2 sums transactions and requires that `transactions(budgetId, period)` sums to `spent` |

  Each of these moves to the item level (§3, §6).
- **Building blocks reused unchanged:**
  - `AuditedWriter` (`core/ledger/src/main/kotlin/dev/imagh/pft/ledger/audit/AuditedWriter.kt:49-63` create, `:77-99` update with no-op detection, `:107-114` tombstone delete). It nests inside an outer `runner.inTransaction` (`:34-35`), so the transaction and all its items commit or roll back together (INT-4).
  - `LedgerEntity`/`EntityMeta` (`entity/LedgerEntity.kt:14-22`, `entity/EntityMeta.kt:23-40`): per-item attribution and tombstone.
  - `EntityRepository` (`entity/EntityRepository.kt:12-31`), extended per entity with its own queries.
  - `Validation.requireAmount`/`requireLive` (`validation/Validation.kt:37-61`).
  - `NameBasedId.of(kind, vararg parts)` (`ids/NameBasedId.kt:30`) for deterministic item ids.
  - `Paise` exact arithmetic (`core/money/.../Paise.kt:17-20`).
  - `InMemoryEntityRepository`, `InMemoryLedgerStore` (rollback at any depth) and `shouldHaveAudited` (`core/ledger/README.md:53-62`).
  - `RoomEntityRepository` (`app/src/main/kotlin/dev/imagh/pft/app/data/common/RoomEntityRepository.kt:17-33`).
  - UI blocks planned in §3.4 (`AmountField` B2, `CategoryPicker` B4, `TagPicker` B5, `GroupedSection`, `MoneyText`, `AuditHistoryList` C5).
  - The `RECEIPT_LONG` symbol already bundled (`app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftSymbol.kt:96`) for the "several items" glyph.
- **Collections in the audit trail:** a set of tag ids is flattened to one text key in `auditFields()` and checked by name only (`core/ledger/README.md:106-107`). So a line's `tag_ids` works with `AuditFieldsCheck` as is.
- **A gap this plan must close (it would have hit C1 anyway):** the Room-side check compares columns and audit keys **in both directions**. An audited key with no column fails with "auditFields() key '…' has no column" (`app/src/test/kotlin/dev/imagh/pft/app/testing/AuditColumnsCheck.kt:49-57`). A join table that is not registered also fails (`unaccountedTables`, `AuditColumnsCheck.kt:79-83`, unless it is in `AuditedTables.notEntityTables`, `AuditedTables.kt:48-54`). Tags stored in a join table therefore need a small, reviewed extension to `AuditedTable` (step C1c).
- **Slice B is unaffected.** B1 to B5 (`2026-10-28-m1a-core-ledger.md:256-277`) never read or write transaction rows. The only transaction-coupled items in slice B are the `transaction_tag` mention in the v3 row (`:169`, "The join table is created in C1") and B1's deferred `AccountInUse` test (`:259`), which C1 adds and which this plan keeps as is. So slice B can proceed in parallel with P0.
- **PRD ID clash to avoid.** The PRD uses `R-` for two lists: the §6.2 roadmap items R-1..R-3 (`docs/PRD.md:93-95`) and the recurring ACs R-1..R-5 (`docs/PRD.md:145-149`). The existing convention is to write "roadmap R-n" (decisions 32 and 33, `docs/PRD.md:320-321`). The two new roadmap rows are R-4 and R-5, which also exist as recurring ACs, so **this plan and every doc edit always write "roadmap R-4" / "roadmap R-5"**, never a bare id (open question Q6 covers whether to rename the roadmap prefix instead).
- **Limits** are typed constants (`validation/LedgerLimits.kt:11-29`). Errors are one sealed vocabulary with no user content (`validation/LedgerError.kt:16-69`).

## 3. Design & decision

### 3.1 The model: every transaction has one or more lines

A transaction holds the **payment facts**. Its **lines** hold the **classification**.

| | Owns |
|---|---|
| `LedgerTransaction` (table `ledger_transaction`) | type, direction, amount, occurred_at, account, title (payee/merchant), description, state, source, bank_ref, source_message_id, account_fragment, transfer_group_id, recurring_rule_id (later loan_id, neutralised_by_loan_id, goal_id). **No category and no tags.** |
| `TransactionLine` (table `transaction_line`) | transaction_id, position (1-based display order), amount (> 0), category_id (NULL = uncategorised), note (optional), tag ids (join table `transaction_line_tag`) |

**The invariant (hardcoded, CLAUDE.md §3.8):** every live transaction has at least one live line, and the live lines' amounts sum exactly to the transaction's amount. This includes each transfer leg (one line, category NULL). A plain transaction is one line whose amount equals the transaction's.

**Why "always lines" rather than "an optional breakdown on top of a transaction-level category":**
- **One canonical schema** (CLAUDE.md §3.3). Classification lives in exactly one place. With an optional breakdown, every consumer (budgets, filters, search, stats, import, sync) would need two code paths: "use the transaction's category unless it has lines". Itemising would also have to move the category off the transaction, and it would be unclear what the transaction's own category means once lines exist.
- **A single, checkable invariant**, with no "unless itemised" exceptions. One query can count violations (§4), and tests and diagnostics use it.
- **Cost:** one extra row per transaction and one join in classification queries. That is negligible at the NFR scale of 50k transactions (`docs/PRD.md:258`), and covered by indexes and measured in H3.

### 3.2 Lines are ledger entities, written only by `TransactionService`

`TransactionLine` is a `LedgerEntity<TransactionLineId, TransactionLine>` with its own `EntityMeta`. That gives it the following, all from existing blocks with **no new write path**:
- per-user and per-device attribution: the spouse adding items is recorded as the spouse;
- a tombstone on removal (INT-3: removed items stay restorable and visible in history);
- its own field-level audit entries through `AuditedWriter`.

Its repository `TransactionLineRepository : EntityRepository<…>` is the mandated core↔app persistence boundary, so it is not a new kind of abstraction. **There is no line service.** Only `TransactionService` writes lines, inside the same `runner.inTransaction` as the transaction. A line can never be created, moved or edited on its own in a way that skips the sum check.

Rejected alternative: lines as a value list inside the transaction, flattened into its audit fields. It would hard-delete child rows on every edit (against INT-3). It would need an escaped text encoding or a change to `AuditFieldsCheck` for indexed keys, and the history UI would have to decode that encoding. It would also give items no attribution.

### 3.3 The write rules (`TransactionService`)

The service takes one command shape for create and edit: `TransactionDraft(…payment facts…, lines: List<LineDraft>)`, with `LineDraft(id: TransactionLineId?, amount: Paise, categoryId: CategoryId?, tagIds: Set<TagId>, note: String?)`. The ViewModel builds the single line for a plain transaction. Core does not special-case it.

1. **`Breakdown.of(total, drafts)`**, a pure value type that validates the list:
   - count is 1..`MAX_LINES_PER_TRANSACTION`, otherwise `TooManyLines(max)`; an empty list fails the sum check;
   - each amount passes `Validation.requireAmount`;
   - notes are trimmed, blank becomes null, and a note is at most `MAX_LINE_NOTE_LENGTH`;
   - no duplicate line ids;
   - Σ amounts == total, computed with `Paise.plus` (exact), otherwise `BreakdownMismatch`.
   No overflow is reachable: 50 × 1e13 is far below `Long.MAX_VALUE`, and `addExact` guards anyway.
2. **Kind rules:**
   - a line's category must match the transaction type (EXPENSE category on an expense, INCOME on income);
   - transfer legs have exactly one line, with category NULL (`TransferCannotBeItemised`);
   - tags are allowed on every type, including transfers. A transfer's tags are written to both legs' lines, as C2 already does for amounts.
3. **References are validated only where they change.** A new line, a changed category or a newly added tag must be live (`Validation.requireLive`). A line that is untouched keeps its now-archived category. So editing only the title of an old transaction never fails because a category was archived since. This applies to the C1 rule at plan line 282.
4. **Edit is a reconciliation in one transaction**, matching lines by id:
   - a draft with an existing id → `writer.update` (a no-op writes nothing);
   - a draft without an id → `writer.create`;
   - a live line missing from the draft → `writer.delete` (tombstone);
   - positions are renumbered in draft order.
   A draft id that belongs to another transaction, or is tombstoned, fails with `NotFound("transaction_line")`. This prevents re-parenting another transaction's line.
5. **Defence in depth.** Before the transaction block returns, the service re-reads the live lines. If their sum ≠ amount, it fails closed with `IllegalStateException`, which rolls back the transaction, its lines and the audit rows. This guards against a reconciliation bug.
6. **The amount can change only together with lines that match it.** Correcting a mis-captured amount on an itemised transaction therefore makes the user re-balance the items. Auto-captured payment facts are never changed by itemising, and the test asserts they are byte-for-byte unchanged.
7. **Delete and restore act on the transaction only.** Its lines stay as they are and are hidden by the join on the parent's `deleted_at`. Restore brings back exactly the lines it had, never the ones removed earlier by editing.
8. **Deterministic ids for lines created with the transaction:** `NameBasedId.of("transaction-line", txnId, position)`. Lines added by a later edit get `Uuid7`. A recurring-generated transaction (deterministic id, plan decision 2, line 52) or an SMS/email transaction (X-2) therefore gets the same line id on every device, so re-processing and later sync cannot create a second line and break the sum.

### 3.4 Reads

- **List and detail:** each transaction comes with its live lines (two parameterised queries: transactions in the window, then lines for those ids). There is no `@Relation`, because it cannot filter tombstoned children.
- **Classification queries use lines.**
  - A category filter matches a transaction if **any** live line has the category or one of its children; the same applies to a tag filter.
  - The **filtered total** is the sum of the matching lines, not of the whole transactions. Filtering by Groceries shows the ₹2,000 row with a total of ₹1,200.
  - With no category or tag filter, totals are Σ transaction amounts, as before.
- **`spendingLinesBetween(range): List<SpendingLine>`** replaces the planned `spendingBetween`. It returns live lines of live EXPENSE transactions that are not neutralised, each with `(transactionId, occurredAt, amount, categoryId, tagIds, state)`. Budgets read it today; statistics (roadmap R-5) will read it later.
- **Budget scope union (plan Q7) is applied per line.** A line matching both the category and the tag scope counts once.
- **Search:** a second FTS4 external-content table over `transaction_line.note`. A transaction matches if its title/description match or any live line's note does.
- **`breakdownMismatchCount(): Int`** counts live transactions with no live line or with Σ live lines ≠ amount. Every Room integration test asserts it is 0, and H2 diagnostics shows it as a number (no amounts).

### 3.5 UI

- **Add/edit form (C4):** looks as already designed. Its Category and Tags fields edit the single line. A **"Split into items"** row opens the items section in the same form, so there is one ViewModel and one Save, which keeps it atomic.
- **Items section (new step C4b):** one row per item (`AmountField`, `CategoryPicker`, note, `TagPicker`), "Add item", and remove.
  - A live **Remaining / Over by** indicator is computed in the ViewModel with `Paise` (no float). "Over by" is a non-negative magnitude with its own semantic, because `MoneyText` throws on a negative value (DS5 contract, plan line 296).
  - "Fill remaining" on an item sets its amount to the remainder.
  - Save is enabled only when the remainder is ₹0.00.
  - Removing down to one item returns to the plain form.
- **Detail (C5):** an "Items" `GroupedSection` appears when there is more than one line. Opening "Split into items" from the detail is how an auto-captured transaction is itemised later. History (`AuditHistoryList`) merges the transaction's and its lines' audit entries in time order, labelling line entries "Item 2 · Amount ₹300.00 → ₹350.00", including removed items.
- **List row:** one line shows its category glyph. Several lines show the `RECEIPT_LONG` glyph with "N items".

### 3.6 Configuration (CLAUDE.md §3.5)

Both new limits are static and not user-tunable, so they are typed constants in `LedgerLimits`, documented in the ledger README:
- `MAX_LINES_PER_TRANSACTION = 50`. Items are per purpose, not per product SKU; raise it when roadmap R-4 needs per-SKU lines.
- `MAX_LINE_NOTE_LENGTH = 200`, the same bound as a title.

Nothing goes in the database or the environment.

### 3.7 Future items recorded, not built

- **Roadmap R-4: invoice image/PDF → proposed items.** It lands after statistics (roadmap R-5) ships (owner decision Q5, 2026-11-10), with its own architect plan. Constraints it must meet:
  - it only **proposes** items, and the user confirms them; nothing is auto-applied (CLAUDE.md §3.8, "uncertain cases surface for review");
  - the file is untrusted input and stays on the device;
  - recognising text in a photo needs an on-device OCR model, which goes beyond PRD decision 4 ("no on-device model", `docs/PRD.md:293`, made for SMS/email) and the PRD non-goal "receipt OCR" (`docs/PRD.md:34`), so the owner must decide on it explicitly in that plan;
  - receiving a shared file needs an exported share-target activity, which Rule C requires to be justified;
  - it will need negative (discount) lines and probably a higher `MAX_LINES_PER_TRANSACTION`.
  The line model here is the target shape, so roadmap R-4 needs no schema change for the lines themselves.
- **Roadmap R-5: statistics and graphs.** Stats for categories, sub-categories, tags, accounts, budgets, loans, goals and users, over interval types (week, month, quarter, year, custom). It lands in M3 (insights, PRD §15), with its own plan. Constraints it must meet:
  - figures are derived on read from transactions and `SpendingLine` (INT-6), with no stored aggregates;
  - the chart approach (Compose `Canvas` vs a charting dependency) must be vetted against §2 of the constitution;
  - nothing reaches logs.

### 3.8 Abstraction bar

**No new abstraction.** `TransactionLineRepository` is the persistence boundary that every entity already has. `Breakdown` is a concrete value type. The reconciliation is a private function of `TransactionService`. There is no line service, no generic "child entity" helper and no strategy for the two FTS tables. The one test-support extension (`AuditedTable.keysStoredIn`) is a field on an existing class, and two consumers are already named for it: `transaction_line.tag_ids` now, and `recurring_rule.tag_ids` in E3.

## 4. Data model & schema

These are amendments to the planned v4/v5. **No change to the current v1 database and no data migration**, because v4 does not exist yet.

| Version (slice) | Table | Change vs the M1a plan §4 |
|---|---|---|
| v4 (C1) | `ledger_transaction` | **Drop** `category_id` and its `(category_id)` index. Everything else as planned |
| v4 (C1) | `transaction_line` (**new**) | meta columns (`id`, `created_at`, `updated_at`, `created_by_user_id`, `created_on_device_id`, `deleted_at`, `archived_at`); `transaction_id TEXT NOT NULL` FK → `ledger_transaction.id` ON DELETE RESTRICT; `position INTEGER NOT NULL CHECK (position > 0)`; `amount INTEGER NOT NULL CHECK (amount > 0)`; `category_id TEXT NULL` FK → `category.id` RESTRICT; `note TEXT NULL`. Indexes `(transaction_id)`, `(category_id)` |
| v4 (C1) | `transaction_line_tag` (**replaces** `transaction_tag`) | `(line_id, tag_id)` PK, FKs RESTRICT, index `(tag_id)`. A join table: listed in `AuditedTables` as a non-entity table with its reason; its content is audited as `transaction_line.tag_ids` |
| v5 (C3) | `transaction_fts` | as planned (title, description) |
| v5 (C3) | `transaction_line_fts` (**new**) | FTS4 external content over `transaction_line.note` |

- **Audit keys:**
  - `ledger_transaction` audits every column above, with no `category_id` and no `tag_ids`.
  - `transaction_line` audits `transaction_id`, `position`, `amount`, `category_id`, `note` and `tag_ids` (sorted ids joined with `,`).
  - `archived_at` is unused for lines; the meta column exists for uniformity.
- **Why the invariant is not a DB trigger:** an edit is several statements (update line 1, insert lines 2 and 3). SQLite has no deferred CHECK across rows, and an immediate trigger would fire on the intermediate states. So the invariant is enforced in core (§3.3, rules 1 and 5), and `breakdownMismatchCount()` makes any violation visible.
- **Join rows are replaced, not tombstoned.** When a line's tags change, `RoomTransactionLineRepository.update` deletes and re-inserts that line's `transaction_line_tag` rows inside the same Room transaction. Join rows are not entities. The record of the change is the line's audited `tag_ids` diff (old and new id lists), which satisfies INT-2/INT-3 as the original `transaction_tag` design did. No entity row is ever hard-deleted.
- **No partial unique index on `(transaction_id, position)`:** Room cannot declare a partial index, and positions are display order only.
- **Canonical schema:** the core models above are the canonical shape, and Room entities map them one-to-one (CLAUDE.md §3.3).

## 5. Flows

**Manual itemised expense.**
1. `TransactionEditScreen` → `TransactionEditViewModel.save()`: `Paise.parse` on each amount field at the UI boundary.
2. `TransactionService.create(draft)`: `Breakdown.of(amount, lines)`, then check the account is live, the categories are live and of the right kind, and the tags are live.
3. `runner.inTransaction { writer.create(transactions, txnId); for each line: writer.create(lines, NameBasedId("transaction-line", txnId, pos)); tag rows; invariant re-check }`.
4. Room `withTransaction` → SQLCipher DB. Audit: one `ledger_transaction` CREATE and N `transaction_line` CREATE entries.

**Itemise later (manual or auto-captured).**
1. Detail → "Split into items" → the edit form's items section, pre-filled with the one existing line.
2. Save → `TransactionService.edit(id, draft)`, with payment facts unchanged:
   - the transaction gets no UPDATE (no-op);
   - line 1 gets an UPDATE (amount, category);
   - lines 2..N get CREATE entries;
   - the invariant is re-checked.
3. The transaction keeps its id, bank_ref, source_message_id, source and state, so M1b de-duplication and matching are unaffected.

**Budget progress (D2).** `BudgetService.progress` → `TransactionQueries.spendingLinesBetween(period)` → `BudgetCalculator` sums the lines in scope. The verified/unverified split comes from each line's transaction state, so B-3 is unchanged. `transactions(budgetId, period)` returns `(transaction, inScopeAmount)` pairs whose amounts sum to `spent`.

**Category filter (C3).** Filter by Groceries → `EXISTS (live line with category in {Groceries + children})` on live transactions. The row shows the whole transaction, and the total sums only the matching lines.

## 6. Implementation steps (TDD-ordered)

Conventions are those of the M1a plan (line 196): core tests use Kotest under `core/ledger/src/test/kotlin/dev/imagh/pft/ledger/…`, fakes live in `core/ledger/src/testFixtures/…/fakes/`, app tests use Robolectric under `app/src/test/kotlin/dev/imagh/pft/app/…`, and migration tests go in `app/src/testDebug/…/data/MigrationTest.kt`. Every step ends with `./gradlew check` green. **Steps C1a..C5 replace the M1a plan's C1..C5 step text.** Steps marked "delta" add to an existing step in a later slice and ride in that slice's PR.

### Step P0 — Record the decision (docs only, this plan's PR)
- **Red/Green:** none (docs). The checks are review plus a link check by reading.
- **Edits:**
  - **This file.**
  - **`docs/PRD.md`:**
    - L-1 reworded (a transaction has amount, date/time, type, account, title, description and **one or more items**, each with amount, category/sub-category, tags and note, summing to the amount);
    - new **L-11** (itemise at creation or later, manual or auto-captured; the sum rule; payment facts untouched; budgets and filters count item portions);
    - §6.2 roadmap rows **R-4** (invoice → proposed items, after roadmap R-5, own plan, OCR decision vs decision 4) and **R-5** (statistics and graphs over entities and interval types, M3, own plan);
    - §15 M3 placeholder: one line pointing to roadmap R-5;
    - §2 non-goal "receipt OCR" annotated "for M1; see roadmap R-4";
    - §12 decision **35** (itemised transactions; lines always), written once the owner approves.
  - **`docs/plans/2026-10-28-m1a-core-ledger.md`:**
    - an "Amendment (2026-11-10, itemised transactions)" block under §4 and under slice C, pointing here;
    - the §4 rows updated: v3 (`:169`, the join table is now `transaction_line_tag`, still created in C1), v4 (`:170-171`) and v5 (`:172`);
    - the §5 flows that name the category check, `tagRepo.setTags` or `spendingBetween` (`:182`, `:186`) pointed at §5 of this plan;
    - an amendment block under the slice C heading saying §6 of this plan supersedes C1 and adds C4b. The C2..C5 step text (`:285-300`) stays as the base, because §6's C2..C5 entries are deltas to it; where they conflict, this plan wins;
    - deltas noted on D2 (`:311-315`), D3, E2, F1, G1, I1 and H2;
    - the PR table row for slice C (`:413`) updated to C1a, C1b, C1c, C2, C3, C4, C4b, C5, and the INT-4 test mapping (`:459`) gaining the C1b/C1c rollback tests.
  - **`docs/architecture/data-model.md`:** the version table rows 4 and 5 (still "planned").
  - **`docs/learnings/README.md`:** one entry.

### Step C1a — Transaction and line model, plain create/edit/delete (core)
- **Red:**
  - `transaction/TransactionLineTest`:
    - a fully populated `TransactionLine` passes `shouldHaveCompleteAuditFields()`;
    - `tag_ids` is the sorted ids joined by `,`.
    The same check runs on `LedgerTransaction` in `transaction/LedgerTransactionTest`.
  - `transaction/BreakdownTest` (pure):
    - one line equal to the total is valid;
    - Σ ≠ total → `BreakdownMismatch`, and an empty list → `BreakdownMismatch`;
    - 51 lines → `TooManyLines(50)`;
    - a zero line → `AmountNotPositive`, a line over the limit → `AmountTooLarge`;
    - a note is trimmed, blank becomes null, and 201 characters → `TextTooLong(200)`;
    - a duplicate line id → `DuplicateLine`;
    - property test: any partition of a total into 1..50 positive parts is valid, and moving 1 paisa out of any part is not.
  - `validation/ValidationTest`: a new `optionalText(text, max)` returns trimmed text or null and throws `TextTooLong`. It has two consumers: the transaction description and the line note.
  - `transaction/TransactionServiceTest`: the original C1 cases (plan line 282), re-expressed on lines:
    - `createExpense` with one line stores the transaction and one line whose id is `NameBasedId.of("transaction-line", txnId, "1")`; both are audited (`shouldHaveAudited("ledger_transaction", …, CREATE)` and `("transaction_line", …, CREATE)`) and attributed to the current user and device;
    - an INCOME category on an expense line → kind mismatch; an archived category or tag on a new line → `Archived`;
    - a no-op edit writes no audit row;
    - delete tombstones the transaction only, its lines remain live, and restore returns it with the same lines;
    - `markVerified` is audited;
    - `AccountService.delete` with live transactions → `AccountInUse`.
- **Green:**
  - in `core/ledger/src/main/kotlin/dev/imagh/pft/ledger/transaction/`: `LedgerTransaction`, `TransactionLine`, `TransactionType`, `Direction`, `TxnState`, `TxnSource`, `Breakdown`, `TransactionDraft`, `LineDraft`, `TransactionRepository`, `TransactionLineRepository` (`liveLinesOf(txnId)`, `allLineIdsOf(txnId)`), `TransactionService`;
  - `ids/Ids.kt`: `TransactionId`, `TransactionLineId`;
  - `LedgerError`: `BreakdownMismatch`, `TooManyLines`, `DuplicateLine`, `TextTooLong`, `TransferCannotBeItemised` (none carries amounts or names);
  - `LedgerLimits`: the two limits;
  - testFixtures: `InMemoryTransactionRepository` and `InMemoryTransactionLineRepository`, both extending `InMemoryEntityRepository` (they are shared, because budget and recurring tests will use them).
- **Refactor:** `core/ledger/README.md` (public interface, the invariant under "Behaviours worth knowing", configuration).

### Step C1b — Itemise: multi-line edit and reconciliation (core)
- **Red:** `transaction/TransactionItemisationTest`, over the fakes:
  - **Itemise:** a ₹2,000 single-line expense becomes 1,200 Groceries (existing id kept) + 300 Stationery + 500 Utilities. Expect exactly one `transaction_line` UPDATE, two `transaction_line` CREATEs (`Uuid7` ids), no `ledger_transaction` entry, and live lines summing to 2,000.
  - **Remove an item:** it is tombstoned (DELETE audited), the other positions become 1..n, and it still appears in `allLineIdsOf`.
  - **Mismatch:** Σ ≠ amount → `BreakdownMismatch`, and the store and audit log are unchanged (the rollback of `InMemoryLedgerStore`).
  - **Amount rules:** a changed amount with unchanged lines → `BreakdownMismatch`; a changed amount with re-balanced lines → OK, with the transaction UPDATE showing the `amount` diff.
  - **Foreign line ids:** a draft line id from another transaction → `NotFound("transaction_line")`, nothing written. A tombstoned line id → the same.
  - **Archived references:** an untouched line on a since-archived category: a title edit succeeds. Changing a line to an archived category → `Archived("category")`.
  - **Income:** itemising with INCOME categories is OK; an EXPENSE category on an income line → kind mismatch.
  - **Auto-captured:** a transaction with `source = SMS`, `state = UNVERIFIED` and a bank_ref, once itemised, keeps `amount`, `account_id`, `occurred_at`, `bank_ref`, `source_message_id`, `source` and `state` exactly. Line entries are audited with `AuditSource.MANUAL`.
  - **Defence in depth:** the invariant check fails closed. A test-only `TransactionLineRepository` fake that drops one insert makes `edit` throw `IllegalStateException` and roll everything back.
- **Green:** in `TransactionService`, `edit(id, draft)` plus a private `reconcileLines(...)` and `requireBalanced(txnId)`.
- **Refactor:** none expected. The README "Behaviours" section gets the reconciliation rules.

### Step C1c — Room v4 for transactions and lines
- **Red:**
  - `app/src/test/.../data/RoomTransactionIntegrationTest`:
    - create and itemise through `TransactionService` over Room;
    - a bad tag id on the third line (FK failure) leaves the transaction, its lines and the audit trail exactly as before (INT-4);
    - `breakdownMismatchCount() == 0` after every scenario, and it is 1 after a raw-SQL line insert planted by the test.
  - `RoomAuditColumnsTest` (existing) with `ledger_transaction` and `transaction_line` registered in `AuditedTables` (fully populated samples). It first fails on `tag_ids` having no column.
  - `AuditColumnsCheckTest`, extended:
    - a key declared stored in another table passes only if that table exists and is listed as a non-entity table;
    - a misspelt table fails.
  - `MigrationTest.migrate3To4` (testDebug): v3 with a category and a tag migrates; the new tables exist, empty; existing rows are intact; the triggers survive.
- **Green:**
  - in `app/src/main/kotlin/dev/imagh/pft/app/data/transaction/`: `LedgerTransactionEntity`, `TransactionLineEntity`, `TransactionLineTagEntity`, `TransactionDao`, `TransactionLineDao` (including the mismatch-count query), `RoomTransactionRepository`, `RoomTransactionLineRepository` (on `RoomEntityRepository`; tags written in the same Room transaction), and the mappers;
  - `Migrations.kt` `MIGRATION_3_4`;
  - `AppServices` wiring;
  - test support: `AuditedTable` gains `keysStoredIn: Map<String, String>` (audited key → the join table holding it), checked by `AuditColumnsCheck`; `transaction_line_tag` is added as a non-entity table with its reason.
- **Refactor:** `app/README.md`, `docs/architecture/data-model.md` (a real v4 section, plus the `keysStoredIn` convention), `docs/architecture/code-structure.md` (`data/transaction/`).

### Step C2 — Transfers (delta to the plan's C2)
- **Red:** in `TransferTest`, each leg has exactly one line, with category NULL and the same tags. Editing the amount updates both transactions and both lines (the invariant holds on both). Supplying two lines for a leg → `TransferCannotBeItemised`; a category on a transfer line → `TransferCannotBeItemised`. A Room test shows that a failure on the second leg's line leaves zero rows.
- **Green:** extend `TransactionService.createTransfer`/`edit`.

### Step C3 — Queries, search, balances (delta to the plan's C3)
- **Red:** `TransactionQueriesIntegrationTest` adds:
  - a category filter on Groceries returns the itemised ₹2,000 transaction once, with a filtered total of ₹1,200; the parent category Food includes a Groceries child line;
  - a tag filter matches on line tags;
  - a tombstoned line does not match, and neither does any line of a tombstoned transaction;
  - "notebook" matches a line note through `transaction_line_fts`;
  - `spendingLinesBetween` returns the three lines (not one transaction), excludes income, transfers, deleted transactions and deleted lines, and Σ equals the month's expense total;
  - `accountBalance` is unchanged by itemising.
  `migrate4To5` backfills both FTS tables. The existing instrumented FTS check (plan risk table) also covers line notes.
- **Green:** in `core/ledger/.../query/`, `TransactionFilter`, `TransactionQueries` (`month`, `search`, `spendingLinesBetween`, `SpendingLine`); `TransactionQueryDao`, the FTS entities and `MIGRATION_4_5` in `app`.

### Step C4 — List and plain add/edit UI (delta to the plan's C4)
- **Red:**
  - `TransactionsFlowTest`, as planned: the plain form's category and tags round-trip through the single line.
  - `TransactionRowMapperTest`: one line → the category glyph and name; three lines → `RECEIPT_LONG` and "3 items"; the amount is always the non-negative transaction amount with its direction semantic (DS5).
- **Green:** as planned (plan line 296).

### Step C4b — Items editor (new)
- **Red:**
  - `TransactionItemsFlowTest` (Robolectric Compose, real in-memory Room):
    - open a saved ₹2,000 expense, tap "Split into items", add Groceries 1,200, Stationery 300 and Utilities 500;
    - the remaining indicator reads ₹2,000.00 → ₹800.00 → ₹500.00 → ₹0.00, Save is disabled until ₹0.00, and after save the list row shows "3 items";
    - reopening shows the three items; removing one shows "Remaining ₹500.00" with Save disabled; "Fill remaining" on an item restores ₹0.00;
    - the same flow works on the Add form for a new transaction.
  - `ItemsEditorStateTest` (ViewModel, pure state):
    - remaining is computed with `Paise` and over-allocation is reported as `Over(magnitude)`, never a negative `Paise`;
    - Add is disabled at 50 items;
    - bad amount text on one item shows an inline error and blocks Save;
    - collapsing to one item restores the plain form with that item's category and tags.
  - A Roborazzi golden of the items section with fake data.
- **Green:** in `app/src/main/kotlin/dev/imagh/pft/app/ui/transactions/`, a local `ItemsSection.kt` plus the items state in `TransactionEditViewModel`, reusing `AmountField`, `CategoryPicker`, `TagPicker`, `GroupedSection` and `BottomActionBar`.
- **Refactor:** extract nothing to `ui/common` (one consumer).

### Step C5 — Search, filters and detail with history (delta to the plan's C5)
- **Red:**
  - `TransactionDetailTest`: an itemised transaction shows an Items section with three rows, and a plain one shows none.
  - The history lists the transaction's and its lines' entries in time order: "Item 2 · Amount ₹300.00 → ₹350.00", and a removed item as "Item 3 removed".
  - `AuditQueriesIntegrationTest`: `forTransaction(id)` returns the entries for the transaction and all its lines, tombstoned ones included, and no entries for other transactions.
  - `SearchFlowTest`: a note-text search finds the itemised transaction.
- **Green:** `AuditQueries.forTransaction` in core and Room (one parameterised query over `audit_entry` `(entity_type, entity_id)`, using the existing index, `data-model.md:38`), plus the detail screen and history labels.

### Later-slice deltas (no new PRs; each rides its slice's PR)
- **D2:** in `BudgetCalculatorTest`:
  - a category budget counts only the in-scope line of an itemised transaction;
  - the overall budget counts the whole amount once;
  - a line matching both the category and the tag scope counts once;
  - verified and unverified are split by the transaction's state.
  In `BudgetServiceTest`, `transactions(budgetId, period)` returns `(transaction, inScopeAmount)` pairs whose sum equals `spent`. **D3:** `RoomBudgetIntegrationTest` with one itemised expense.
- **E2/E3:**
  - AUTO and CONFIRM transactions have one line from the template, whose id is derived from the deterministic transaction id;
  - running catch-up twice creates no extra lines;
  - `recurring_rule.tag_ids` uses `keysStoredIn`.
- **F1:** an EMI is one LOAN_EMI line. `markConverted` on an itemised purchase removes **all** its lines from `spendingLinesBetween` (LN-5).
- **G1:** an itemised transaction linked to a goal counts its amount once.
- **I1:** each imported transaction gets one line, unless the I0 spike finds itemised data in Cashew's export.
- **H2:** `DiagnosticsViewModelTest`: diagnostics shows the breakdown mismatch count (a number only), and the rendered text still holds no amounts.
- **Recorded for later plans (not M1a):**
  - **M1b:** a captured message creates one line (category from the S-6 proposal, or NULL), with the deterministic line id.
  - **M1c:** the sync merge must run the invariant per transaction after a merge and send any violation to review. It must never be auto-fixed, and the losing values are kept (Y-5).

### PR breakdown (incremental — required)

**Target branches.**
- P0 is a small docs change: one branch `docs/itemised-transactions-plan` → `main`.
- The build PRs land in slice C's planned base branch `feat/m1a-transactions` (dev branches `feat/m1a-transactions-c1a-…`; slice branches cannot nest under the base name, see learnings 2026-10-28).
- C-final (`feat/m1a-transactions` → `main`) is unchanged.

| PR | Scope (steps) | Target | Depends on | Why it's independently reviewable |
|----|---------------|--------|------------|-----------------------------------|
| P0 | This plan; PRD L-1/L-11, roadmap R-4, roadmap R-5, §15, §2 note, decision 35; M1a plan amendments; data-model planned rows; learnings | `main` | — (parallel with slice B) | Docs only: the decision the owner approves |
| C1a | Transaction + line model, `Breakdown`, plain create/edit/delete (core + fakes) | `feat/m1a-transactions` | B-final, P0 | Pure core; one behaviour (one-line transactions) |
| C1b | Multi-line itemisation and reconciliation (core) | `feat/m1a-transactions` | C1a | The new rule set, isolated for a careful review |
| C1c | Room v4, `keysStoredIn` test support, `migrate3To4` | `feat/m1a-transactions` | C1b | Persistence adapter and migration only |
| C2 | Transfers (one line per leg) | `feat/m1a-transactions` | C1c | One behaviour |
| C3 | Queries over lines, both FTS tables, `spendingLinesBetween`, v5 | `feat/m1a-transactions` | C2 | Read side only |
| C4 | List and plain add/edit UI | `feat/m1a-transactions` | C3 | First UI; no items editor yet |
| C4b | Items editor UI | `feat/m1a-transactions` | C4 | One screen section |
| C5 | Search UI, detail with items and merged history | `feat/m1a-transactions` | C4b | Read-only UI |
| C-final | Transactions | `main` | C1a–C5 | One coherent capability, as planned |

The deltas to D, E, F, G, I and H ride in those slices' existing PRs and add no new PRs. Slice C grows from 5 PRs to 8. Each stays one-session sized: C1 is split three ways because it now carries two entities, a reconciliation and a migration.

## 7. Test plan

**Unit (core, Kotest; ≥ 90% line coverage):**
- `BreakdownTest`: rules and a property test over partitions;
- `TransactionLineTest`, `LedgerTransactionTest`: audit-field completeness;
- `ValidationTest`: `optionalText`;
- `TransactionServiceTest`: plain lifecycle;
- `TransactionItemisationTest`: reconciliation, atomic rollback, the foreign-id refusal, archived references, payment facts preserved;
- `TransferTest`;
- `BudgetCalculatorTest` (D2): per-line scope.
All assert stored rows and audit entries through the fakes' public reads, never mock calls.

**Integration (app, Robolectric, real in-memory Room):**
- `RoomTransactionIntegrationTest`: atomicity on a FK failure, and the mismatch count;
- `RoomAuditColumnsTest` and `AuditColumnsCheckTest`: `keysStoredIn`;
- `MigrationTest.migrate3To4` and `migrate4To5`, plus the chain test;
- `TransactionQueriesIntegrationTest`: per-line filters, filtered totals, line-note FTS, `spendingLinesBetween`;
- `AuditQueriesIntegrationTest`;
- `TransactionItemsFlowTest`, `TransactionsFlowTest`, `TransactionDetailTest`, `SearchFlowTest`: ViewModel → service → Room;
- `RoomBudgetIntegrationTest` (D3).

**Instrumented (`pft35`):** the planned FTS check, extended to `transaction_line_fts`.

**Edge and failure cases:**
- an empty item list, one paisa off, a zero item, 51 items;
- a duplicate or foreign line id, a tombstoned line id;
- an archived category on a new vs an untouched line;
- an income/expense kind mismatch on a line; a transfer with two lines or a category;
- an amount change without a re-balance;
- a FK failure on the last line;
- delete and restore of an itemised transaction;
- an over-allocated editor (no negative `MoneyText`);
- a no-op edit (no audit rows).

**AC → test map**

| AC | Tests |
|---|---|
| L-1 (amended), L-11 | `BreakdownTest`, `TransactionServiceTest`, `TransactionItemisationTest`, `TransactionItemsFlowTest`, `TransactionDetailTest` |
| L-3 (tags per item) | `TransactionLineTest`, `TransactionQueriesIntegrationTest` (tag filter) |
| L-7 (filters/search over items) | `TransactionQueriesIntegrationTest`, `SearchFlowTest` |
| B-1..B-3 (item portions) | `BudgetCalculatorTest`, `BudgetServiceTest`, `RoomBudgetIntegrationTest` |
| X-1/X-2, "never silently altered" | `TransactionItemisationTest` (payment facts preserved), deterministic line id in `TransactionServiceTest` and E2 |
| INT-2 (audit) | `shouldHaveAudited` in every service test; `AuditQueriesIntegrationTest` |
| INT-3 (tombstones) | removed-item and delete/restore tests |
| INT-4 (atomic) | the rollback tests in C1b and `RoomTransactionIntegrationTest` |
| INT-6 (derived) | `breakdownMismatchCount`, `spendingLinesBetween` sums, the balance-unchanged test |

## 8. Security considerations

- **Untrusted input:** every item field is user-typed text validated at the boundary:
  - amounts by `Paise.parse` in the ViewModel and `requireAmount` in core;
  - notes by `optionalText` with a length bound;
  - category and tag ids by `requireLive` where they change;
  - line ids by ownership (a foreign id is refused, so no cross-transaction tampering);
  - the item count is bounded by `MAX_LINES_PER_TRANSACTION` (no unbounded write from one save).
- **Queries:** Room DAOs only, all parameterised. The category/tag `IN (:ids)` lists are bound parameters. The FTS `MATCH` on line notes is a bound parameter stripped of FTS operators, like the planned title search. The C3 follow-up about the bundled SQLite 3.49.1 FTS CVEs (plan line 292) now covers **two** FTS tables, and its reachability note in the PR security review must cover both.
- **Logs:** no new `LogEvent` carries amounts or notes. The diagnostics mismatch count is an integer. `LedgerError` additions hold no amounts, names or notes (only limits).
- **Integrity (CLAUDE.md §3.8):**
  - the sum invariant is hardcoded, with no config switch;
  - it fails closed (refused save, or rollback on the post-write check);
  - itemising cannot change auto-captured payment facts;
  - every change is in the immutable audit trail.
- **Attribution:** each line carries `created_by_user_id` and `created_on_device_id`.
- **Dependencies, permissions, scopes:** no new dependency, permission, exported component or scope. Roadmap R-4 (future) will need an exported share target and possibly an OCR model; both are flagged in §3.7 for their own review.

## 9. Documentation updates (all required surfaces)

| Surface | What | PR |
|---|---|---|
| This plan | new | P0 |
| `docs/PRD.md` | L-1 amended, L-11, §6.2 roadmap R-4 and roadmap R-5, §15 pointer, §2 non-goal note, §12 decision 35 | P0 |
| `docs/plans/2026-10-28-m1a-core-ledger.md` | amendment blocks (§4, slice C, deltas on D2/D3/E2/F1/G1/I1/H2), PR table | P0 |
| `docs/architecture/data-model.md` | planned v4/v5 rows (P0); the real v4 section with `transaction_line`, `transaction_line_tag` and the `keysStoredIn` convention (C1c); v5 (C3) | P0, C1c, C3 |
| Inline KDoc | every public class/function: `Breakdown`, `TransactionLine`, `TransactionService.edit` (the reconciliation rules), `SpendingLine`, `AuditQueries.forTransaction`, `AuditedTable.keysStoredIn` | each build PR |
| `core/ledger/README.md` | public interface (transactions, lines, queries), "Behaviours" (the invariant, reconciliation, deterministic line ids), configuration (two new limits), data owned | C1a, C1b, C3 |
| `app/README.md` | `data/transaction/`, the join table, FTS tables, test support | C1c, C3, C4b |
| `docs/architecture/code-structure.md` | `ledger/transaction/`, `ledger/query/`, `app/data/transaction/`, `ui/transactions/` | C1a, C1c, C3, C4 |
| `docs/architecture/overview.md` | one line under the canonical-schema/boundary notes: classification lives on transaction lines | C1c |
| `docs/features/transactions.md` | created in C4 as planned, with an **"Items"** section: what it is for, how to itemise (new or later, including auto-captured), the remaining indicator, what stays fixed, how budgets/filters/search count items, history | C4, C4b, C5 |
| `docs/features/budgets.md` | how itemised transactions count (portion per scope, once overall) | D4 |
| `docs/learnings/README.md` | P0: the model decision; C1c: `keysStoredIn` and why the invariant is not a trigger; others as found | every PR |
| Root `README.md` | status line at C-final, as planned | C-final |

## 10. Risks & mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| A reconciliation bug leaves lines that do not sum | wrong budgets and categories | `Breakdown` validation, a post-write `requireBalanced` that rolls back, `breakdownMismatchCount` asserted in every Room test and shown in H2 |
| Sync (M1c) merges two devices' line edits into an unbalanced transaction | silent wrong totals | deterministic ids for lines created with a transaction; the M1c plan must re-check the invariant per merged transaction and route violations to review (recorded in the M1a plan amendment and PRD L-11) |
| Query cost of the extra join at 50k transactions | slow lists or budgets | indexes on `transaction_line(transaction_id)` and `(category_id)`; the list query reads lines only for the visible window; measured in H3 |
| Renumbered positions make history noisy | harder-to-read history | history labels use the line's position at the time of the entry; reorder-only edits are rare (there is no drag-to-reorder in C4b) |
| "Items" confused with M2 "split across users" | design drift | the naming rule in §1; PRD L-11 cross-references G-3 as distinct |
| The two-way audit-column check blocks join-table keys | C1c stalls | the `keysStoredIn` extension, with its own tests, is part of C1c |
| `MoneyText` throws on a negative "remaining" | crash in the editor | the editor state reports `Over(magnitude)`; `ItemsEditorStateTest` covers it |
| Scope creep (per-item loan/goal, discount lines, recurring items) | slice C overruns | listed out of scope; roadmap R-4 owns discount lines |

## 11. Rollout & verification

- **P0:** the owner reviews and approves the PR (decision 35 is written on approval). Slice B continues meanwhile.
- **Each build PR:** `./gradlew check` (unit, Robolectric, coverage gate, ktlint, detekt). C1c and C3 also run `./gradlew connectedDebugAndroidTest` on `pft35` (migrations on the encrypted file, FTS).
- **End to end on the emulator (`./gradlew installDebug`, after C5):**
  1. Add a ₹2,000 card expense with one category, then from the detail screen split it into Groceries ₹1,200, Stationery ₹300 and Utilities ₹500.
  2. Confirm the list shows one "3 items" row, and the month expense total rises by ₹2,000, not ₹6,000.
  3. Filter by Groceries: the total shows ₹1,200. Search "notebook" (an item note) and find it.
  4. Open the detail: three items, and history lines for each item.
  5. Edit the amount to ₹2,100 without re-balancing: Save stays disabled. Re-balance it: it saves.
  6. Delete and undo: it returns with its three items.
  7. Check the account balance moved by the payment amount only.
- **After D4:** a Groceries budget shows ₹1,200 from that payment, and an overall budget shows ₹2,000.
- **After H2:** diagnostics shows "breakdown mismatches: 0".
- **Rollback:** each PR reverts independently inside `feat/m1a-transactions` before C-final. After C-final, migrations are forward-only. A plain transaction is just one line, so hiding the items editor (reverting C4b) leaves every stored transaction valid.

## 12. Open questions

Each has a default that applies if the owner does not answer. **Answered 2026-11-10:** the owner accepted every default except Q5, recorded in its row.

| # | Question | Default |
|---|---|---|
| Q1 | **Model:** every transaction always has 1..N lines carrying the category, tags and note (recommended, §3.1), or keep a transaction-level category and add optional lines only when itemised? | Lines always |
| Q2 | **Tags live only on lines**, so a plain transaction's tags are its one line's tags, and an itemised one is tagged per item; or also keep whole-transaction tags (for example "Goa trip" on every item at once)? The second adds a union rule to every tag query. | Lines only. If tagging each item proves tedious, a later UI-only "apply to all items" action writes the same tags to every line, with no schema change |
| Q3 | **Items editor placement:** a section inside the add/edit form (one Save, atomic), or its own screen; and do you want an HTML mockup before C4b, as the design-system plans had? | A section in the form; no separate mockup; screenshots in the C4b PR |
| Q4 | **Limits and naming:** at most 50 items per transaction and 200 characters per item note? The item's free-text field is stored as `note` (so it is never confused with the transaction's own `description`) and labelled "Note" in the UI. | Yes, as stated |
| Q5 | **Roadmap placement:** invoice → items (roadmap R-4) after M1 with its own plan, which must settle OCR vs PRD decision 4; statistics and graphs (roadmap R-5) in M3. Pull either earlier? | As stated. **Owner (2026-11-10): statistics comes before invoice → items.** Statistics stays in M3; invoice → items follows it, with its own plan |
| Q6 | **Roadmap ID prefix:** the new roadmap rows R-4/R-5 share ids with the recurring ACs R-4/R-5 (§2). Keep the "roadmap R-n" qualifier convention (P0 does this), or rename the roadmap prefix (for example `RM-1..RM-5`) everywhere it is used? A rename touches §6.2, decisions 32/33, D-1 and older plans, so it would be **its own small docs PR**, not part of P0. | Keep the qualifier; no rename |
