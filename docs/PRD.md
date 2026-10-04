# Personal Finance Tracker — Product Requirements Document

| | |
|---|---|
| **Status** | Draft v0.2 — owner answers round 2 folded in |
| **Owner** | akashg.sde@gmail.com |
| **Scope of this version** | Milestone 1 in detail; Milestones 2 and 3 as direction only |
| **Source** | Handwritten milestone notes + Q&A (see [Decisions log](#12-decisions-log)) |

**Legend.** Items marked **[Confirmed]** were decided by the owner. Items marked **[Proposed]** are my recommendation and need confirmation. Nothing marked Proposed should be treated as agreed. Open questions are collected in [section 13](#13-open-questions).

---

## 1. Problem

The owner tracks personal and family expenses in Cashew (Android) and shares a Google account with their spouse for sync. Two problems:

1. **Capturing transactions is slow.** Every transaction is typed by hand.
2. **Month-end reconciliation is laborious.** Matching the app against bank, card and UPI records takes significant effort, and errors are hard to spot.

## 2. Goals and non-goals

### Goals
- Make capturing a transaction near-zero effort by logging it automatically from SMS and email.
- Make reconciliation cheap by storing the bank's own transaction reference on every transaction, so records can be matched and de-duplicated deterministically.
- Match Cashew's everyday functionality (budgets, recurring transactions, subscriptions, loans, goals, category hierarchy) closely enough to replace it.
- Rank **accuracy, integrity, privacy and security** above convenience in every trade-off.

### Non-goals (for M1)
- Publishing on the Play Store or supporting users beyond the owner's household.
- Server-side components operated by the owner (M2 is explicitly serverless).
- Insights and analytics beyond basic budget progress (M3).
- Bank API / Account Aggregator integrations, receipt OCR. **[Proposed]**
- Multi-currency. INR only. **[Confirmed]**
- Importing Cashew data. Deferred to a later phase by the owner. **[Confirmed]**
- Person-to-person lending/borrowing as a loan type. **[Confirmed for now: loans means EMI loans owed to banks and card issuers]**

## 3. Users

| User | Description |
|---|---|
| **Owner** | Primary user, Android phone. |
| **Spouse** | Second user, own Android phone. In M1 they share the owner's Google account for sync (as today). In M2 each uses their own email. |

Household scale only: two users, a handful of devices, thousands to low tens of thousands of transactions per year.

## 4. Principles

These drive trade-offs and are testable requirements in section 8.

1. **Accuracy over convenience.** An auto-logged transaction is never silently dropped, merged or altered. Uncertain cases are surfaced to the user.
2. **Integrity.** Money is stored exactly (no floating point). Every change is traceable. Sync can't lose or duplicate transactions.
3. **Privacy.** Financial data and message content stay on the owner's devices by default. No third-party analytics, ads or crash reporters that receive financial data. Parsing is on-device.
4. **Security.** Least privilege on every permission and OAuth scope. Data is protected at rest and in transit, and sync data is encrypted before it leaves the device. **[Proposed]**
5. **Replaceable parts.** The sync transport in M1 (Google Drive) is a stopgap that M2 replaces with P2P, so sync sits behind a clean interface.

## 5. Platform and technology

| Area | Decision | Status |
|---|---|---|
| Platform | Android | **[Confirmed]** |
| Local database | SQLite 3 | **[Confirmed]** |
| Distribution | Sideloaded APK for personal use (Play Store policy restricts SMS permissions) | **[Proposed]** |
| Language/UI | Native Kotlin, Jetpack Compose, Room | **[Proposed]** |
| Encryption at rest | SQLCipher (SQLite-compatible), key in Android Keystore, biometric/PIN app lock | **[Confirmed]** |
| Money representation | Integer minor units (paise), one currency per account | **[Proposed]** |
| IDs | Client-generated, globally unique, time-sortable (UUIDv7/ULID) so devices can create records offline without collisions | **[Proposed]** |

## 6. Milestone overview

| Milestone | Theme | Detail level here |
|---|---|---|
| **M1** | Single-household tracker: core ledger, SMS + email auto-capture, Drive-based sync | Full |
| **M2** | Shared entities, serverless P2P sync, balances/outstanding from email, per-user email | Direction |
| **M3** | Insights (requirements open) | Placeholder |

### 6.1 Phasing inside M1 **[Confirmed]**

M1 is large. Delivering it in three increments gives a usable app early and de-risks the hardest parts one at a time.

| Phase | Contents | Exit criterion |
|---|---|---|
| **M1a — Core ledger** | Accounts, categories/sub-categories, tags, transactions, budgets, recurring/subscriptions, loans, goals, local encrypted DB, manual entry | Owner can run a full month on the app with manual entry only |
| **M1b — SMS capture** | SMS parsing, review inbox, notifications, transaction-reference matching | Owner's bank/UPI/card SMS log automatically and de-duplicate correctly |
| **M1c — Email capture and sync** | Gmail read-only ingestion, Drive app-data sync across devices | Spouse's phone and owner's phone converge to the same ledger |

---

## 7. Milestone 1 — Functional requirements

### 7.1 Core ledger

| ID | Requirement |
|---|---|
| L-1 | A transaction has: amount, date/time, type (expense / income / transfer), account, category, optional sub-category, zero or more tags, title, description. |
| L-2 | Categories form a two-level hierarchy (category → sub-category). Users can create, rename, recolour/re-icon, reorder and archive both. Archiving never deletes history. |
| L-3 | Tags are free-form, user-defined, many-to-many with transactions. |
| L-4 | Accounts represent the method/account used (bank account, credit card, cash, wallet/UPI). In M1 an account has a name, type, currency and optional identifiers used for matching (e.g. last 4 digits). Balance tracking from email is M2. |
| L-5 | Transfers between accounts (including credit-card bill payments) are a first-class type and are **not** counted as income or expense. **[Proposed — Q7]** |
| L-6 | A transaction's lifecycle state is visible: `unverified` (auto-logged, not yet reviewed) or `verified` (created manually or confirmed). **[Confirmed]** |
| L-7 | Full-text search and filtering by date range, account, category, tag, amount, state. |
| L-8 | Every transaction records who created it (user profile) and on which device. In M1 both users share one Google account, so the user profile is chosen per device on first run. **[Confirmed]** |

### 7.2 Budgets

| ID | Requirement |
|---|---|
| B-1 | Create budgets by period (weekly, monthly, custom range) and scope (overall, or one or more categories / sub-categories / tags). |
| B-2 | Show spent / remaining / percentage for the current period; flag when exceeding a threshold. |
| B-3 | Unverified auto-logged transactions **count** towards budgets and are visibly marked as such. **[Confirmed]** |
| B-4 | Budget history is retained per period. |
| B-5 | (Sharing a budget between users is M2.) |

### 7.3 Recurring transactions and subscriptions

| ID | Requirement |
|---|---|
| R-1 | Schedule a repeating transaction (daily / weekly / monthly / yearly / custom interval) with start date and optional end date or count. |
| R-2 | A *subscription* is a recurring expense that additionally tracks provider, billing cycle, next due date and active/paused/cancelled status. |
| R-3 | Upcoming items are listed ahead of time. The user chooses per item: create automatically on the due date, or prompt for confirmation. **[Proposed]** |
| R-4 | When an SMS/email transaction arrives that matches an upcoming recurring item (amount, payee, window), link it to that item instead of creating a duplicate. |
| R-5 | Missed or overdue occurrences are surfaced, never silently skipped. |

### 7.4 Loans

**Scope for now [Confirmed]:** EMI loans owed to banks and credit-card companies. Person-to-person lending is out of scope for now.

| ID | Requirement |
|---|---|
| LN-1 | A loan is a liability with: lender (bank / card issuer), principal, interest rate (optional), tenure, EMI amount, start date, and linked account. Outstanding and paid amounts are derived from linked transactions. |
| LN-2 | EMI schedule feeds the recurring engine (R-1) so each instalment appears as an upcoming item. |
| LN-3 | An incoming SMS/email EMI debit is matched to the loan's schedule (amount, lender, window) and linked, not double-counted (see R-4, X-3). |
| LN-4 | Credit-card EMI conversions are modelled as loans against the card issuer. Tracking the card's total outstanding balance from email is M2. |

### 7.5 Goals

| ID | Requirement |
|---|---|
| G-1 | A goal has a name, target amount, optional target date, and progress derived from linked contributions. |
| G-2 | Transactions can be linked to a goal as contributions. |
| G-3 | (Splitting a transaction across shared goals/budgets is M2.) |

### 7.6 Automatic capture — SMS

| ID | Requirement |
|---|---|
| S-1 | With the user's permission, read incoming SMS and parse transaction messages from configured senders (banks, cards, UPI apps). |
| S-2 | Parsing uses **deterministic, on-device templates only**. No message content leaves the device. **[Confirmed]** |
| S-3 | A parsed message creates a transaction in `unverified` state with extracted amount, direction, date, account (by last-4 / sender), payee/merchant, and bank reference. |
| S-4 | Messages that look financial but can't be parsed go to an "unparsed" queue for manual action — never ignored. |
| S-5 | Each new auto-logged transaction triggers a notification prompting the user to check category/tags/title. Notifications are batchable and don't expose full amounts on the lock screen unless the user opts in. **[Proposed]** |
| S-6 | Auto-categorisation is rule-based and learnt from the user's past choices (e.g. merchant → category). It only proposes; the user confirms. **[Proposed]** |
| S-7 | Parsers are data-driven (one template per sender/format) so new formats can be added without a rewrite, and each template has unit tests built from redacted real messages. |
| S-8 | Reading existing historical SMS (backfill) is user-initiated and idempotent. |
| S-9 | OTP and other non-financial messages are never stored. |
| S-10 | **Read-only.** The app only reads SMS and email. It never deletes, edits, sends or marks them. **[Confirmed]** |
| S-11 | **Parse log.** When a message can't be parsed, or the user flags a transaction as wrongly parsed ("Report parse error"), the app keeps an entry with the source message text and parser version, in the encrypted local DB, never synced. Messages that parse successfully are not stored: only the message ID and parsed fields are kept, and they can be re-read from the phone or mailbox if re-parsing is needed. **[Confirmed; extraction of this log is a later feature]** |
| S-12 | **Messages are untrusted input.** Sender headers can be spoofed and scam messages mimic bank alerts. Reject messages containing links or phishing wording, require a plausible sender header, and rely on the `unverified` flag so a forged alert is never treated as fact. **[Proposed]** |
| S-13 | Sender matching strips the operator prefix (e.g. `VM-`, `AD-`) and suffix (e.g. `-S`) and matches the remaining header against an alias registry per bank. See `docs/research/indian-bank-sms.md`. |

### 7.7 Automatic capture — Email

| ID | Requirement |
|---|---|
| E-1 | Read transaction alerts/statements from Gmail using the **read-only Gmail API scope** (`gmail.readonly`) via OAuth. No send/modify/delete scope. **[Confirmed]** |
| E-2 | Only messages from configured senders / matching configured filters are fetched and parsed; the app does not index the whole mailbox. **[Proposed]** |
| E-3 | Parsing is on-device and deterministic (same rules as S-2), producing `unverified` transactions with the same notification flow. |
| E-4 | OAuth refresh tokens are stored in the Android Keystore-backed encrypted storage. Revoking access in the Google account stops ingestion with a clear in-app error. |
| E-5 | **Known risk:** an OAuth client for a sensitive/restricted scope that isn't Google-verified shows a warning screen and may have limits (including token lifetime depending on publishing status). This must be validated early — see section 11. |

### 7.8 Transaction reference and reconciliation

The note: *"link txn ref into data for better reconciliation."*

| ID | Requirement |
|---|---|
| X-1 | Every transaction stores its **source** (manual / SMS / email / import / recurring) and, where available, the **bank reference** (e.g. UPI reference/UTR/RRN, card authorisation code, NEFT/IMPS ref), the **originating message ID**, and an identifying account fragment. |
| X-2 | **Idempotent ingestion:** a given source message always maps to the same transaction (deterministic ID derived from source + message ID). Re-scanning SMS/email, or two devices reading the same mailbox, must not create duplicates. |
| X-3 | **Cross-source de-duplication:** the same real-world payment often arrives as an SMS *and* an email *and* possibly a manual entry. Matching on bank reference is exact. Absent a reference, fuzzy match (same account, same amount, close timestamp, similar payee) produces a **"possible duplicate"** suggestion. |
| X-4 | The app never auto-deletes or auto-merges on a fuzzy match. Exact-reference matches may auto-link the two sources to one transaction while retaining both source records. **[Proposed]** |
| X-5 | A transaction shows all its linked source records so the user can see *why* it exists. |
| X-6 | Month-end reconciliation view: list of `unverified` transactions, possible duplicates, unparsed messages, and transactions with no matching source evidence. Goal: reconciling a month means clearing one short list, not re-comparing everything. **[Proposed]** |

### 7.9 Sync (M1: through Google)

The note: *"Sync across devices through gmail."* Interpreted as Google-account-based sync via the **Drive app-data folder**. **[Confirmed]**

| ID | Requirement |
|---|---|
| Y-1 | Devices sync through a hidden app-data folder in Google Drive using the non-sensitive `drive.appdata` scope. |
| Y-2 | Sync is **operation-log based**: each device appends immutable change records (create/update/delete-as-tombstone) with a device ID and a hybrid logical timestamp. Devices merge logs deterministically. **[Proposed]** |
| Y-3 | Sync payloads are **encrypted client-side** before upload, using a key that never leaves the user's devices. Google can't read the ledger. **[Confirmed]** How devices obtain the shared key is open (Q8). |
| Y-4 | **Raw SMS/email text is never synced**; only parsed transaction data and the source references needed for de-duplication (X-2). **[Proposed]** |
| Y-5 | Conflicts on the same field are resolved deterministically and visibly (e.g. last-writer-wins by logical clock, with the losing value retained in history). No silent data loss. |
| Y-6 | Sync works offline-first: the app is fully usable without a network and converges when connectivity returns. |
| Y-7 | The sync layer sits behind an interface so M2 can swap Drive for P2P without changing the data model. |
| Y-8 | A manual "verify ledger" check (e.g. a hash/count comparison across devices) shows whether devices agree. **[Proposed]** |

### 7.10 Data portability

| ID | Requirement |
|---|---|
| D-1 | Import from Cashew's export. **Deferred to a later phase by the owner.** The schema should not make it hard. |
| D-2 | Full export to open formats (CSV and JSON), including the audit trail. |
| D-3 | Encrypted local backup and restore. |

---

## 8. Security, privacy and integrity requirements

| ID | Requirement | Status |
|---|---|---|
| SEC-1 | Database encrypted at rest (SQLCipher), key held in Android Keystore. | Confirmed |
| SEC-2 | App lock with biometric / device credential; auto-lock after a configurable timeout; screen content hidden in recents. | Confirmed |
| SEC-3 | Permissions requested only when needed (SMS at first enable, notifications, network). No contacts, location, or storage-wide access. | Proposed |
| SEC-4 | OAuth scopes limited to `gmail.readonly` and `drive.appdata`. | Confirmed |
| SEC-5 | No analytics/crash SDKs that transmit financial content. Logs never contain amounts, payees, message text or tokens. | Proposed |
| SEC-6 | Local backups and sync data are encrypted; Android auto-backup of app data is disabled unless encrypted. | Proposed |
| INT-1 | Amounts stored as integers in minor units. No floating point in money math. | Proposed |
| INT-2 | Every create/edit/delete is recorded in an audit trail (what changed, when, by which device/user, from which source). Normal edits are allowed; the audit trail makes them traceable. | Confirmed |
| INT-3 | Deletion is a soft delete (tombstone) that syncs and is recoverable. | Proposed |
| INT-4 | Database changes are transactional; a crash mid-write can't leave partial transactions. | Proposed |
| INT-5 | Migrations are versioned and tested, with an automatic pre-migration backup. | Proposed |
| INT-6 | Derived totals (balances, budget spent, goal progress) are computed from transactions and can always be re-derived. No hand-maintained running totals that can drift. | Proposed |

## 9. Non-functional requirements

- **Offline-first.** All core features work without network.
- **Responsiveness.** Common screens (add transaction, list, budget view) respond in under ~200 ms on a mid-range device at 50k transactions. **[Proposed]**
- **Reliability of capture.** SMS ingestion must survive process death, reboot and battery optimisation (broadcast receiver plus WorkManager). Email polling runs on a schedule suited to the platform's background limits.
- **Testability.** Parsers, matching/de-dup, recurring engine, budget math and sync merge are pure, heavily unit-tested logic.
- **Observability without leakage.** Local-only diagnostics screen (parser hit/miss counts, last sync, last email fetch), no financial content.

## 10. Success metrics (personal)

| Metric | Target |
|---|---|
| Transactions captured automatically (SMS + email) vs. typed manually | ≥ 80% **[Proposed]** |
| Auto-parsed transactions with correct amount/direction/account | ≥ 99% on the template set; the remainder land in the unparsed queue, not in the ledger wrongly |
| Duplicate transactions in the ledger after a month | 0 unresolved |
| Time to reconcile a month | Under ~15 minutes **[Proposed — owner to set the target]** |
| Devices in agreement after sync | 100%, verified by the ledger check (Y-8) |

## 11. Risks and dependencies

| Risk | Impact | Mitigation |
|---|---|---|
| **Gmail OAuth for an unverified personal app** (warning screen; limits on token lifetime and users depending on publishing status) | Email ingestion could stop periodically | Validate in a spike *before* committing to M1c; document the exact setup; fall back to re-auth UX; revisit IMAP only if unavoidable |
| **SMS permissions and Play Store policy** | App can't be distributed through Play | Sideload only (decision above) |
| **Android background limits / OEM battery killers** | Missed SMS or email | Receiver + WorkManager, in-app health indicator, "ingestion last ran" display, user guidance |
| **Parser brittleness** (banks change formats) | Missed or wrong transactions | Data-driven templates, tests from real samples, unparsed queue, parser-version recorded on each transaction |
| **Shared Google account in M1** | Both phones may read the same mailbox and double-log | Idempotent ingestion (X-2) |
| **Sync conflicts / corruption** | Divergent ledgers | Op-log design, deterministic merge, ledger check, encrypted backups |
| **Scope size of M1** | Slow time to first value | Phase M1a/b/c (6.1) |
| **Lost/stolen phone** | Exposure of financial data | Encryption at rest, app lock, ability to revoke Google access |

## 12. Decisions log

| # | Decision | Date |
|---|---|---|
| 1 | Android app; local SQLite 3 as the database | from notes |
| 2 | M1 sync uses the **Google Drive app-data folder** (not Gmail as transport) | 2026-10-04 |
| 3 | Email is read with the **Gmail API, `gmail.readonly` OAuth** | 2026-10-04 |
| 4 | SMS/email parsing is **deterministic templates only, on-device** — no cloud LLM, no on-device model | 2026-10-04 |
| 5 | Auto-logged transactions are **counted immediately and flagged `unverified`** until reviewed | 2026-10-04 |
| 7 | Loans = EMI loans owed to banks and card issuers, for now | 2026-10-04 |
| 8 | INR only | 2026-10-04 |
| 9 | Normal edits with full audit trail (not append-only) | 2026-10-04 |
| 10 | Cashew import deferred | 2026-10-04 |
| 11 | App is read-only on SMS/email; failed or wrong parses go to a local log; extracting the log is a later feature | 2026-10-04 |
| 12 | Per-user attribution (created by user and device) in M1 | 2026-10-04 |
| 13 | SQLCipher, app lock, client-side-encrypted sync, and the M1a/b/c phasing are approved | 2026-10-04 |
| 14 | Build effort must stay within the owner's Claude token limit (see section 16) | 2026-10-04 |
| 6 | M2 sync is serverless (P2P is a "maybe"); each user uses their own email in M2 | from notes |

## 13. Open questions

Answered items are in the decisions log. Each remaining question has a proposed default that applies if not answered.

| # | Question | Proposed default |
|---|---|---|
| Q1 | Which banks, credit cards and UPI apps do **you and your spouse** actually use? A parser for every Indian bank isn't feasible or needed (see `docs/research/indian-bank-sms.md`). The templates are built for these first. Redacted sample SMS/emails for each would speed this up. | Generic parser plus overrides for your banks only |
| Q2 | Which email senders carry alerts and statements for those accounts? | Filter by a sender list you configure |
| Q3 | Do transfers and credit-card bill payments need to be first-class (excluded from income/expense)? | Yes |
| Q4 | Single-entry (Cashew-style) or double-entry bookkeeping? | Single-entry with first-class transfers; revisit before M2 balances |
| Q5 | "Credit-card companies" in loans: do you mean card EMI conversions, or the card's outstanding dues as a loan? | EMI conversions only; card dues are M2 |
| Q6 | The parse log (S-11) holds raw failed messages, which contain account numbers and balances. Is encrypted-local-only OK, with the option to mask digits when extracted later? | Yes |
| Q7 | Which email account is read in M1: the shared Google account, or each person's own? M2 says each user uses their own. | Each device reads its own configured mailbox; ingestion is idempotent (X-2) |
| Q8 | How should two devices share the sync encryption key? Options: passphrase entered on each device (recoverable), or QR pairing from the first device. | Passphrase-derived key (Argon2id) |
| Q9 | What is your actual Claude token limit (plan, per-window or monthly)? It decides how many work sessions M1a can use. | Assume a small budget; plan M1a in compact, test-first slices (section 16) |

## 14. Milestone 2 — direction (not yet specified)

From the notes:
- Split transactions and attach them to **shared entities** (budget, goal, subscription, etc.) across users.
- **Sync without a centralised server** (P2P is a maybe).
- **Track balance and outstanding** of bank accounts, credit cards and loans, derived from **email**.
- **Each user uses their own email**, yet the household ledger still syncs.

Implications to keep in mind while building M1:
- The sync interface (Y-7) and operation-log data model must already assume multiple writers with distinct identities.
- Per-user identity (Q12) should exist in the schema from M1.
- Balances derived from email statements will need an "account balance checkpoint" concept that the ledger can reconcile against, so keep accounts as full entities from M1a.

## 15. Milestone 3 — placeholder

Insights; requirements deliberately open. To be specified after M1/M2 usage shows what questions the owner actually asks of the data.

## 16. Delivery constraints — token budget **[Confirmed constraint, approach Proposed]**

The owner's budget for building this is bounded by their Claude token limit. Approach:

1. **Build in small vertical slices**, one per session, each ending with passing tests and a commit. M1a is the first target. No M1b/M1c work starts until M1a is accepted.
2. **Keep the repo cheap to read.** A short `CLAUDE.md` holds architecture, conventions and commands, so a session needn't re-explore the codebase. Docs stay short and link instead of repeat.
3. **Prefer deterministic, test-driven code** for parsers, matching, recurring, budget math and sync merge. Failures get caught by tests rather than long debugging.
4. **Data-driven parsers.** Adding a bank format means adding a template and a fixture, not new code paths.
5. **No broad "audit everything" sessions** and no unnecessary subagents. Research is done once and written into `docs/research/`.
6. **Stop points.** Every slice has a definition of done in advance. If a slice would exceed its budget, it is cut down, not extended.
7. Suggested slice order for M1a: schema + migrations + audit trail → accounts/categories/tags → transactions UI → budgets → recurring/subscriptions → loans → goals → app lock + encryption.
