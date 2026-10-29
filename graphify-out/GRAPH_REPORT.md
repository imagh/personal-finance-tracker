# Graph Report - personal-finance-tracker  (2026-10-29)

## Corpus Check
- 116 files · ~78,212 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 29 file(s) not represented in the graph (top: .xml 10, .lockfile 8, (none) 4)

## Summary
- 1015 nodes · 2093 edges · 71 communities (42 shown, 29 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 295 edges (avg confidence: 0.94)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `1b803c43`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- Loans and EMIs (LN-1..LN-5)
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- ManifestHardeningTest
- hooks
- RedactTests
- MoneyColors
- ThemeResolutionTest.kt
- Plan: M1a — Core ledger (manual-entry household tracker)
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- WindowThemeTest.kt
- Catalog.kt
- detekt.yml
- HostedComposeRule
- DesignCatalogTest.kt
- Sync via Google Drive app-data (Y-1..Y-8)
- MainActivity.kt
- Plan: M1a design system and visual direction (lands before A5)
- Uuid7Test.kt
- Automatic capture from Email (E-0..E-5)
- Personal Finance Tracker PRD
- ColorSchemeTestSupport.kt
- TokenSheetSections.kt
- Delivery constraints (small vertical slices, Claude Pro limits)
- PftTypographyTest
- UserId
- PftShapesTest.kt
- Public interface
- AuditedWriterTest.kt
- `:core:money` — integer-paise money
- Auditable
- Research: Indian Bank SMS Formats
- AuditedWriter
- DesignCatalog.kt
- LedgerError
- AppSetting
- EntityMeta
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- TransactionRunner
- Note
- PftTheme.kt
- ThemePrefs
- redact_text
- How to run and test
- PaiseParser
- Paise
- InMemoryEntityStore
- TokenSheet
- TokenSheetPage.kt
- Architecture overview
- Pseudo
- _Text
- PftShapes.kt
- `:core:ledger` — the ledger foundations
- 5. Flows
- 3. Design & decision

## God Nodes (most connected - your core abstractions)
1. `Public interface` - 43 edges
2. `UserId` - 32 edges
3. `PftTypographyTest` - 26 edges
4. `DeviceId` - 26 edges
5. `IdentityService` - 25 edges
6. `Paise` - 25 edges
7. `AuditSource` - 24 edges
8. `LedgerError` - 24 edges
9. `AuditEntry` - 23 edges
10. `AuditedWriter` - 23 edges

## Surprising Connections (you probably didn't know these)
- `3.5 Design system (owner decision, 2026-10-29)` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `3.2 Theming architecture (no new abstraction)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt
- `9. Documentation updates (all required surfaces)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt
- `Security posture of the shell` --references--> `ManifestHardeningTest`  [INFERRED]
  docs/architecture/overview.md → app/src/test/kotlin/dev/imagh/pft/app/ManifestHardeningTest.kt
- `3.6 Icons and emoji (UI-6)` --references--> `ManifestHardeningTest`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/test/kotlin/dev/imagh/pft/app/ManifestHardeningTest.kt

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

## Communities (71 total, 29 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.14
Nodes (9): _day(), _email_record(), html_to_text(), read_adb(), read_any(), read_eml(), read_jsonl(), read_mbox() (+1 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.05
Nodes (52): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+44 more)

### Community 2 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Balance observations (L-10), Budgets (B-1..B-5), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Goals (G-1..G-3), Loans and EMIs (LN-1..LN-5), M1a Core ledger, Milestone 2 (shared entities, P2P sync, balances from email) (+2 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.25
Nodes (5): Personal Finance Tracker Learnings, graphify tool, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Toolchain pins (AGP 8.7.3, Gradle 8.10.2, Kotlin 2.1.0, minSdk 33)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.47
Nodes (3): Read-only SMS and email access (S-10), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "MoneyColors"
Cohesion: 0.08
Nodes (20): `ui/theme` (design-system tokens, DS1–DS2), BrandColors, MoneyColors, PftSpacing, PftTypography, Colour, Shapes, Spacing (+12 more)

### Community 9 - "ThemeResolutionTest.kt"
Cohesion: 0.08
Nodes (4): BundledFontTest, `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`(), NameBasedId

### Community 10 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.12
Nodes (15): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 6. Implementation steps (TDD-ordered), 8. Security considerations (+7 more)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.10
Nodes (11): ColorContrastTest, Theme, DARK, LIGHT, PftSpacingTest, Adding a token, Build-time trust decisions, Design system (+3 more)

### Community 18 - "detekt.yml"
Cohesion: 0.29
Nodes (6): ignoreAnnotated, excludes, naming, FunctionNaming, style, MagicNumber

### Community 26 - "HostedComposeRule"
Cohesion: 0.08
Nodes (5): HostedComposeRule, RegisterHostActivity, Statement, PftThemeModeTest, TokenSheetScreenshotTest

### Community 28 - "Sync via Google Drive app-data (Y-1..Y-8)"
Cohesion: 0.25
Nodes (7): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Client-side encrypted sync with passphrase-derived key (Y-3), Entity merge and split (AD-7), Entity identity key (issuer + type + masked id, deterministic ID), Operation-log based sync (Y-2), Sync via Google Drive app-data (Y-1..Y-8), Sync behind an interface (Y-7)

### Community 30 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.16
Nodes (11): PaiseFormat, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 7. Test plan (+3 more)

### Community 31 - "Uuid7Test.kt"
Cohesion: 0.07
Nodes (9): NameBasedIdTest, clockAt(), Uuid7Test, TestClock, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest (+1 more)

### Community 32 - "Automatic capture from Email (E-0..E-5)"
Cohesion: 0.20
Nodes (11): Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope, Idempotent ingestion (X-2), M1 Phasing (M1a/M1b/M1c), M1b SMS capture, M1c Email capture and sync, Open questions (Q1 banks, Q2 email senders) (+3 more)

### Community 33 - "Personal Finance Tracker PRD"
Cohesion: 0.22
Nodes (11): Personal Finance Tracker PRD, Audit trail (INT-2), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Milestone 3 (insights), Non-functional requirements, Security, privacy and integrity requirements (SEC/INT) (+3 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.22
Nodes (10): AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle(), ShapesAndSpacing(), ShapesSection(), SpacingSection() (+2 more)

### Community 36 - "Delivery constraints (small vertical slices, Claude Pro limits)"
Cohesion: 0.33
Nodes (4): Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 37 - "PftTypographyTest"
Cohesion: 0.10
Nodes (5): displayFamily(), pftMaterialTypography(), pftType(), PftTypographyTest, Resolved

### Community 38 - "UserId"
Cohesion: 0.13
Nodes (23): FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE, UNARCHIVE, UPDATE (+15 more)

### Community 40 - "Public interface"
Cohesion: 0.14
Nodes (13): Configuration, How an entity step composes the blocks, Public interface, AuditRecorder, Identity, IdentityService, Uuid7, SettingsService (+5 more)

### Community 41 - "AuditedWriterTest.kt"
Cohesion: 0.11
Nodes (10): How to run and test, AuditDiffTest, Thing, AuditedWriterTest, EntityMetaTest, IdentityServiceTest, IdsTest, SettingsServiceTest (+2 more)

### Community 42 - "`:core:money` — integer-paise money"
Cohesion: 0.20
Nodes (9): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+1 more)

### Community 43 - "Auditable"
Cohesion: 0.14
Nodes (5): Auditable, Device, IdentityRepository, UserProfile, InMemoryIdentityRepository

### Community 44 - "Research: Indian Bank SMS Formats"
Cohesion: 0.25
Nodes (8): Parse log (S-11), Sender alias registry (S-13), Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser, Public Bank SMS Header Registry (gist, Dec 2022), Open-Source Indian SMS Parsers (hisab-kitab, FinTrack, ...), Indian SMS Sender Header Format (prefix-HEADER-S), SMS Spoofing and Phishing

### Community 45 - "AuditedWriter"
Cohesion: 0.22
Nodes (7): Behaviours worth knowing, AuditDiff, AuditedWriter, EntityRepository, 3.4 Reuse by layer (owner decision, 2026-10-28), Slice B — Accounts, categories, tags (`feat/m1a-ledger-setup`), Slice C — Transactions (`feat/m1a-transactions`)

### Community 46 - "DesignCatalog.kt"
Cohesion: 0.17
Nodes (3): ChoiceRow(), DesignCatalog(), DevContent()

### Community 47 - "LedgerError"
Cohesion: 0.13
Nodes (13): AlreadyInitialised, AmountNotPositive, AmountTooLarge, Archived, BlankName, Deleted, IdentityCorrupt, InvalidSettingKey (+5 more)

### Community 48 - "AppSetting"
Cohesion: 0.22
Nodes (5): Responsibilities & boundaries, AppSetting, AppSettingsRepository, SettingKeys, InMemoryAppSettingsRepository

### Community 49 - "EntityMeta"
Cohesion: 0.26
Nodes (4): EntityMeta, LedgerEntity, EntityId, Widget

### Community 50 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.24
Nodes (12): Debug and release source sets, Public interface, Window theme, MainActivity, 5. Flows, PR breakdown (incremental — required), 6.1 Design-system slice: PRs into `feat/m1a-foundation`, 6.2 Appearance slice: base `feat/m1a-appearance` off `main`, after A-final (Q4) (+4 more)

### Community 51 - "TransactionRunner"
Cohesion: 0.21
Nodes (6): Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), AuditLog, TransactionRunner, InMemoryAuditLog, InMemoryLedgerStore, The core/app boundary

### Community 52 - "Note"
Cohesion: 0.19
Nodes (4): Fixture, Note, NoteId, InMemoryEntityRepository

### Community 54 - "ThemePrefs"
Cohesion: 0.24
Nodes (9): DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, ThemeMode, DARK, LIGHT, SYSTEM (+1 more)

### Community 55 - "redact_text"
Cohesion: 0.27
Nodes (6): mask_run(), process(), redact_text(), amt(), hold(), review_flags()

### Community 56 - "How to run and test"
Cohesion: 0.22
Nodes (8): `:app` — Android application, Configuration, Data it owns, Dependencies, How to run and test, Manifest security posture (SEC-2, SEC-3, SEC-6), Purpose, Responsibilities and boundaries

### Community 62 - "Architecture overview"
Cohesion: 0.29
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 66 - "`:core:ledger` — the ledger foundations"
Cohesion: 0.40
Nodes (4): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, Purpose

### Community 67 - "5. Flows"
Cohesion: 0.40
Nodes (5): 5.2 Rendering an amount, 5.3 Collapsing header, 5.4 Custom font import (AP2), 5.5 Mockups (wireframes; the styled version is in the HTML file), 5. Flows

### Community 69 - "3. Design & decision"
Cohesion: 0.50
Nodes (4): 3.1 Modules (Gradle), 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3.5 Design system (owner decision, 2026-10-29), 3. Design & decision

## Knowledge Gaps
- **53 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `BUNDLED_SERIF`, `SYSTEM_SERIF` (+48 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 301 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **29 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Project README` connect `docs/TECHNICAL.md` to `Personal Finance Tracker PRD`, ``:core:ledger` — the ledger foundations`, ``:core:money` — integer-paise money`, ``build-logic` — convention plugins`, `Research: Indian Bank SMS Formats`, `How to run and test`, `Architecture overview`?**
  _High betweenness centrality (0.236) - this node is a cross-community bridge._
- **Are the 39 inferred relationships involving `Public interface` (e.g. with `AuditDiff` and `FieldChange`) actually correct?**
  _`Public interface` has 39 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _53 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1422924901185771 - nodes in this community are weakly interconnected._
- **Why does `6.1 Design-system slice: PRs into `feat/m1a-foundation`` connect `6.1 Design-system slice: PRs into `feat/m1a-foundation`` to `PftShapes.kt`, `ManifestHardeningTest`, `PftTypographyTest`, `PftShapesTest.kt`, `MoneyColors`, `ThemeResolutionTest.kt`, `Public interface`, `DesignCatalog.kt`, `ColorContrastTest`, `WindowThemeTest.kt`, `ThemePrefs`, `HostedComposeRule`, `DesignCatalogTest.kt`, `TokenSheet`, `Plan: M1a design system and visual direction (lands before A5)`?**
  _High betweenness centrality (0.226) - this node is a cross-community bridge._
- **Are the 5 inferred relationships involving `PftTypographyTest` (e.g. with `How to run and test` and `Adding a token`) actually correct?**
  _`PftTypographyTest` has 5 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.05093167701863354 - nodes in this community are weakly interconnected._