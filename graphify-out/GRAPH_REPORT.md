# Graph Report - personal-finance-tracker  (2026-10-30)

## Corpus Check
- 118 files · ~83,380 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 30 file(s) not represented in the graph (top: .xml 10, .lockfile 8, (none) 4)

## Summary
- 1074 nodes · 2300 edges · 77 communities (47 shown, 30 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 332 edges (avg confidence: 0.94)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b0675028`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- Loans and EMIs (LN-1..LN-5)
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- WindowThemeTest.kt
- hooks
- RedactTests
- AuditFieldsCheck.kt
- PftTheme.kt
- Plan: M1a — Core ledger (manual-entry household tracker)
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- MoneyColors
- Catalog.kt
- detekt.yml
- TokenSheetScreenshotTest.kt
- Public interface
- Sync via Google Drive app-data (Y-1..Y-8)
- Paise
- Uuid7Test.kt
- Automatic capture from Email (E-0..E-5)
- Security, privacy and integrity requirements (SEC/INT)
- ColorSchemeTestSupport.kt
- TokenSheetSections.kt
- Architect Plan Template
- PftTypographyTest
- AuditSource
- HostedComposeRule
- IdentityService
- Project README
- UserId
- Research: Indian Bank SMS Formats
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- PftTypography.kt
- CLAUDE.md Development Constitution
- Auditable
- ManifestHardeningTest
- LedgerError
- DesignCatalogTest.kt
- Leaky
- Definition of Done
- WidgetId
- AuditFieldsCheckTest.kt
- 3. Design & decision
- Personal Finance Tracker PRD
- InMemoryEntityStore
- redact_text
- InMemoryEntityRepository
- Architecture overview
- 6. Implementation steps (TDD-ordered)
- Pseudo
- MainActivity
- EntityMeta
- Collecting and Redacting Sample Messages Guide
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Omits
- _Text
- .onCreate
- 3. Design & decision
- Rule A: Mandatory TDD (Red-Green-Refactor)
- Rule B: Mandatory Documentation

## God Nodes (most connected - your core abstractions)
1. `Public interface` - 44 edges
2. `EntityMeta` - 37 edges
3. `UserId` - 34 edges
4. `3.4 Reuse by layer (owner decision, 2026-10-28)` - 34 edges
5. `Paise` - 31 edges
6. `DeviceId` - 28 edges
7. `LedgerEntity` - 27 edges
8. `PftTypographyTest` - 26 edges
9. `IdentityService` - 26 edges
10. `AuditSource` - 24 edges

## Surprising Connections (you probably didn't know these)
- `3.5 Design system (owner decision, 2026-10-29)` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `3.2 Theming architecture (no new abstraction)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt
- `9. Documentation updates (all required surfaces)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt
- `Slice H — App lock, diagnostics, release (`feat/m1a-hardening`)` --references--> `TokenSheet()`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/TokenSheet.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Owner rules R1-R4** — docs_technical_r1_graphify, docs_technical_r2_no_secrets, docs_technical_r3_starter_kit, docs_technical_r4_security_review [EXTRACTED 0.95]
- **Three non-negotiable process rules (TDD, Docs, Security)** — claude_rule_a_tdd, claude_rule_b_documentation, claude_rule_c_security [EXTRACTED 0.95]
- **Layered SMS Parser Strategy** — docs_research_indian_bank_sms_header_registry_gist, docs_research_indian_bank_sms_generic_parser, docs_research_indian_bank_sms_per_bank_overrides, docs_prd_parse_log [EXTRACTED 1.00]
- **Idempotent ingestion and de-duplication across sources** — docs_prd_idempotent_ingestion, docs_prd_cross_source_dedup, docs_prd_identity_key, docs_prd_reconciliation [INFERRED 0.80]
- **Encrypted offline-first sync stack** — docs_prd_op_log_sync, docs_prd_client_side_encryption, docs_prd_sync_interface, docs_prd_client_ids [INFERRED 0.80]
- **Secrets handling across constitution, technical rules and setup guide** — docs_technical_r2_no_secrets, docs_guides_local_setup_secrets_env [INFERRED 0.85]
- **Untrusted message handling (templates, unverified flag, read-only)** — docs_prd_deterministic_parsing, docs_prd_untrusted_messages, docs_prd_unverified_state, docs_prd_read_only_messages [INFERRED 0.85]

## Communities (77 total, 30 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.14
Nodes (9): _day(), _email_record(), html_to_text(), read_adb(), read_any(), read_eml(), read_jsonl(), read_mbox() (+1 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.16
Nodes (14): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup, Secrets via Environment Variables (+6 more)

### Community 2 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.23
Nodes (11): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5) (+3 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.20
Nodes (5): Personal Finance Tracker Learnings, graphify tool, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Transfers (two linked legs, not income/expense)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.32
Nodes (5): Budgets (B-1..B-5), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "AuditFieldsCheck.kt"
Cohesion: 0.22
Nodes (3): AuditFieldsCheck, haveCompleteAuditFields(), shouldHaveCompleteAuditFields()

### Community 9 - "PftTheme.kt"
Cohesion: 0.06
Nodes (5): resolveColorScheme(), BundledFontTest, `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`(), NameBasedId

### Community 10 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.17
Nodes (10): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5. Flows, 8. Security considerations (+2 more)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.21
Nodes (4): ColorContrastTest, Theme, DARK, LIGHT

### Community 16 - "MoneyColors"
Cohesion: 0.12
Nodes (12): `ui/theme` (design-system tokens, DS1–DS2), BrandColors, MoneyColors, PftSpacing, Colour, Shapes, Spacing, Theming (+4 more)

### Community 18 - "detekt.yml"
Cohesion: 0.29
Nodes (6): ignoreAnnotated, excludes, naming, FunctionNaming, style, MagicNumber

### Community 27 - "Public interface"
Cohesion: 0.23
Nodes (4): How an entity step composes the blocks, Public interface, Identity, Validation

### Community 28 - "Sync via Google Drive app-data (Y-1..Y-8)"
Cohesion: 0.40
Nodes (4): Client-side encrypted sync with passphrase-derived key (Y-3), Operation-log based sync (Y-2), Sync via Google Drive app-data (Y-1..Y-8), Sync behind an interface (Y-7)

### Community 30 - "Paise"
Cohesion: 0.06
Nodes (28): AmountTooLarge, Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface (+20 more)

### Community 31 - "Uuid7Test.kt"
Cohesion: 0.05
Nodes (19): Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, EntityMetaTest, IdentityServiceTest (+11 more)

### Community 32 - "Automatic capture from Email (E-0..E-5)"
Cohesion: 0.29
Nodes (7): Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope, Idempotent ingestion (X-2), Transaction reference and reconciliation (X-1..X-6), Risks and dependencies, Two Google identities per device (shared sync account, personal Gmail)

### Community 33 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.05
Nodes (28): ChoiceRow(), DesignCatalog(), DevContent(), PftShapes, DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT (+20 more)

### Community 36 - "Architect Plan Template"
Cohesion: 0.50
Nodes (3): Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps

### Community 38 - "AuditSource"
Cohesion: 0.12
Nodes (21): FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE, UNARCHIVE, UPDATE (+13 more)

### Community 39 - "HostedComposeRule"
Cohesion: 0.07
Nodes (11): HostedComposeRule, RegisterHostActivity, Statement, PftShapesTest, PftSpacingTest, Adding a token, Build-time trust decisions, Design system (+3 more)

### Community 40 - "IdentityService"
Cohesion: 0.13
Nodes (14): Responsibilities & boundaries, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), AuditLog, AuditRecorder, IdentityService, Uuid7, SettingsService, TransactionRunner (+6 more)

### Community 42 - "Project README"
Cohesion: 0.22
Nodes (6): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Code Structure Map, core/shared Shared Packages, Project README, Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "UserId"
Cohesion: 0.12
Nodes (8): Device, IdentityRepository, UserProfile, DeviceId, EntityId, UserId, ValidationTest, InMemoryIdentityRepository

### Community 44 - "Research: Indian Bank SMS Formats"
Cohesion: 0.29
Nodes (7): Parse log (S-11), Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser, Public Bank SMS Header Registry (gist, Dec 2022), Open-Source Indian SMS Parsers (hisab-kitab, FinTrack, ...), Indian SMS Sender Header Format (prefix-HEADER-S), SMS Spoofing and Phishing

### Community 45 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.23
Nodes (8): Behaviours worth knowing, AuditDiff, AuditedWriter, EntityRepository, IdentityCorrupt, 3.4 Reuse by layer (owner decision, 2026-10-28), Slice B — Accounts, categories, tags (`feat/m1a-ledger-setup`), Slice C — Transactions (`feat/m1a-transactions`)

### Community 46 - "PftTypography.kt"
Cohesion: 0.15
Nodes (5): displayFamily(), pftMaterialTypography(), pftType(), PftTypography, Resolved

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.22
Nodes (15): architect Agent, code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command, Pull Request Template (+7 more)

### Community 48 - "Auditable"
Cohesion: 0.15
Nodes (7): Auditable, AppSetting, AppSettingsRepository, SettingKeys, AuditDiffTest, Thing, InMemoryAppSettingsRepository

### Community 50 - "LedgerError"
Cohesion: 0.14
Nodes (13): AlreadyInitialised, AmountNotPositive, Archived, BlankName, Deleted, InvalidSettingKey, LedgerError, NameTooLong (+5 more)

### Community 52 - "Leaky"
Cohesion: 0.24
Nodes (5): Fixture, Leaky, LeakyRepository, Note, NoteId

### Community 53 - "Definition of Done"
Cohesion: 0.15
Nodes (10): Architect Plan (docs/plans/YYYY-MM-DD-slug.md), PR Checklist (TDD, Docs, Security, Architecture, Sizing), Android Surface and Multi-user Restrictions, Immutable Audit Trail, Definition of Done, Money as Integer Paise, Untrusted Input Validation (SMS/email/LLM output), Agent Slash Commands (+2 more)

### Community 54 - "WidgetId"
Cohesion: 0.17
Nodes (5): HasDerived, HasScratch, MisMapped, Misspelt, WidgetId

### Community 56 - "AuditFieldsCheckTest.kt"
Cohesion: 0.22
Nodes (5): AuditFieldsCheckTest, Complete, Kind, LARGE, SMALL

### Community 57 - "3. Design & decision"
Cohesion: 0.29
Nodes (7): 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins"), 3. Design & decision

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.22
Nodes (10): Personal Finance Tracker PRD, Cashew (existing expense app), Delivery constraints (small vertical slices, Claude Pro limits), M1 Phasing (M1a/M1b/M1c), M1b SMS capture, M1c Email capture and sync, Milestone 3 (insights), Non-functional requirements (+2 more)

### Community 60 - "redact_text"
Cohesion: 0.27
Nodes (6): mask_run(), process(), redact_text(), amt(), hold(), review_flags()

### Community 62 - "Architecture overview"
Cohesion: 0.29
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 63 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.29
Nodes (7): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice H — App lock, diagnostics, release (`feat/m1a-hardening`)

### Community 65 - "MainActivity"
Cohesion: 0.25
Nodes (11): `:app` — Android application, Configuration, Data it owns, Debug and release source sets, Dependencies, How to run and test, Public interface, Purpose (+3 more)

### Community 66 - "EntityMeta"
Cohesion: 0.22
Nodes (6): EntityMeta, LedgerEntity, WrongCase, WrongValue, Widget, The audited write path

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 68 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.47
Nodes (5): 6.1 Design-system slice: PRs into `feat/m1a-foundation`, 6.2 Appearance slice: base `feat/m1a-appearance` off `main`, after A-final (Q4), 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), main()

### Community 72 - ".onCreate"
Cohesion: 0.40
Nodes (3): Manifest security posture (SEC-2, SEC-3, SEC-6), Responsibilities and boundaries, 5.1 Launch and theme resolution

### Community 74 - "3. Design & decision"
Cohesion: 0.50
Nodes (4): 3.1 Modules (Gradle), 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3.5 Design system (owner decision, 2026-10-29), 3. Design & decision

## Knowledge Gaps
- **54 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `BUNDLED_SERIF`, `SYSTEM_SERIF` (+49 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 312 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **30 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Collecting and Redacting Sample Messages Guide`, ``build-logic` — convention plugins`, `Research: Indian Bank SMS Formats`, `CLAUDE.md Development Constitution`, `Definition of Done`, `Paise`, `Personal Finance Tracker PRD`, `Architecture overview`, `Uuid7Test.kt`?**
  _High betweenness centrality (0.217) - this node is a cross-community bridge._
- **Are the 40 inferred relationships involving `Public interface` (e.g. with `AuditDiff` and `FieldChange`) actually correct?**
  _`Public interface` has 40 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _54 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1422924901185771 - nodes in this community are weakly interconnected._
- **Why does `6.1 Design-system slice: PRs into `feat/m1a-foundation`` connect `6.1 Design-system slice: PRs into `feat/m1a-foundation`` to `MainActivity`, `TokenSheetSections.kt`, `PftTypographyTest`, `WindowThemeTest.kt`, `HostedComposeRule`, `IdentityService`, `PftTheme.kt`, `PftTypography.kt`, `MoneyColors`, `ManifestHardeningTest`, `DesignCatalogTest.kt`, `TokenSheetScreenshotTest.kt`, `Paise`?**
  _High betweenness centrality (0.179) - this node is a cross-community bridge._
- **Are the 4 inferred relationships involving `EntityMeta` (e.g. with `Data it owns` and `Responsibilities & boundaries`) actually correct?**
  _`EntityMeta` has 4 INFERRED edges - model-reasoned connections that need verification._
- **Should `hooks` be split into smaller, more focused modules?**
  _Cohesion score 0.14285714285714285 - nodes in this community are weakly interconnected._