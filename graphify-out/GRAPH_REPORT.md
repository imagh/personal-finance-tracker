# Graph Report - personal-finance-tracker  (2026-10-30)

## Corpus Check
- 118 files · ~81,534 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 29 file(s) not represented in the graph (top: .xml 10, .lockfile 8, (none) 4)

## Summary
- 1065 nodes · 2264 edges · 60 communities (41 shown, 19 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 330 edges (avg confidence: 0.94)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `c51dd626`
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
- LedgerEntity
- BundledFontTest.kt
- Plan: M1a — Core ledger (manual-entry household tracker)
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- BrandColors
- Catalog.kt
- detekt.yml
- HostedComposeRule
- Public interface
- Sync via Google Drive app-data (Y-1..Y-8)
- Paise
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
- Project README
- Auditable
- Research: Indian Bank SMS Formats
- AuditedWriter
- PftTypography.kt
- CLAUDE.md Development Constitution
- AppSetting
- Definition of Done
- LedgerError
- Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...)
- Note
- Domain Safety Invariants
- ThemePrefs
- MoneyColors
- InMemoryEntityStore
- EntityMeta
- Collecting and Redacting Sample Messages Guide

## God Nodes (most connected - your core abstractions)
1. `Public interface` - 44 edges
2. `EntityMeta` - 35 edges
3. `UserId` - 34 edges
4. `3.4 Reuse by layer (owner decision, 2026-10-28)` - 34 edges
5. `Paise` - 29 edges
6. `DeviceId` - 28 edges
7. `PftTypographyTest` - 26 edges
8. `LedgerEntity` - 26 edges
9. `IdentityService` - 26 edges
10. `AuditSource` - 24 edges

## Surprising Connections (you probably didn't know these)
- `3.5 Design system (owner decision, 2026-10-29)` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
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

## Communities (60 total, 19 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.05
Nodes (27): Debug and release source sets, Public interface, Window theme, MainActivity, 6.1 Design-system slice: PRs into `feat/m1a-foundation`, 6.2 Appearance slice: base `feat/m1a-appearance` off `main`, after A-final (Q4), 6.3 Amendments to M1a steps (consumption; this plan edits M1a when approved), 6. Implementation steps (TDD-ordered) (+19 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.15
Nodes (16): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+8 more)

### Community 2 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Balance observations (L-10), Budgets (B-1..B-5), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Goals (G-1..G-3), Loans and EMIs (LN-1..LN-5), M1a Core ledger, Milestone 2 (shared entities, P2P sync, balances from email) (+2 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.25
Nodes (5): Personal Finance Tracker Learnings, graphify tool, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Toolchain pins (AGP 8.7.3, Gradle 8.10.2, Kotlin 2.1.0, minSdk 33)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.47
Nodes (3): Read-only SMS and email access (S-10), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state

### Community 5 - "ManifestHardeningTest"
Cohesion: 0.06
Nodes (7): Manifest security posture (SEC-2, SEC-3, SEC-6), Responsibilities and boundaries, ManifestHardeningTest, `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`(), WindowThemeTest, 5.1 Launch and theme resolution

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "LedgerEntity"
Cohesion: 0.07
Nodes (16): LedgerEntity, AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind, LARGE, SMALL (+8 more)

### Community 10 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.09
Nodes (21): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 3.1 Modules (Gradle), 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3.5 Design system (owner decision, 2026-10-29) (+13 more)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.10
Nodes (11): ColorContrastTest, Theme, DARK, LIGHT, PftSpacingTest, Adding a token, Build-time trust decisions, Design system (+3 more)

### Community 16 - "BrandColors"
Cohesion: 0.18
Nodes (7): `ui/theme` (design-system tokens, DS1–DS2), BrandColors, Colour, Shapes, Spacing, Tokens, Typography (UI-2, UI-3, owner decision Q10)

### Community 18 - "detekt.yml"
Cohesion: 0.29
Nodes (6): ignoreAnnotated, excludes, naming, FunctionNaming, style, MagicNumber

### Community 26 - "HostedComposeRule"
Cohesion: 0.05
Nodes (12): `:app` — Android application, Configuration, Data it owns, Dependencies, How to run and test, Purpose, HostedComposeRule, RegisterHostActivity (+4 more)

### Community 27 - "Public interface"
Cohesion: 0.23
Nodes (4): How an entity step composes the blocks, Public interface, Identity, Validation

### Community 28 - "Sync via Google Drive app-data (Y-1..Y-8)"
Cohesion: 0.25
Nodes (7): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Client-side encrypted sync with passphrase-derived key (Y-3), Entity merge and split (AD-7), Entity identity key (issuer + type + masked id, deterministic ID), Operation-log based sync (Y-2), Sync via Google Drive app-data (Y-1..Y-8), Sync behind an interface (Y-7)

### Community 30 - "Paise"
Cohesion: 0.06
Nodes (28): AmountTooLarge, Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface (+20 more)

### Community 31 - "Uuid7Test.kt"
Cohesion: 0.08
Nodes (9): NameBasedIdTest, clockAt(), Uuid7Test, TestClock, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest (+1 more)

### Community 32 - "Automatic capture from Email (E-0..E-5)"
Cohesion: 0.20
Nodes (11): Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope, Idempotent ingestion (X-2), M1 Phasing (M1a/M1b/M1c), M1b SMS capture, M1c Email capture and sync, Open questions (Q1 banks, Q2 email senders) (+3 more)

### Community 33 - "Personal Finance Tracker PRD"
Cohesion: 0.22
Nodes (11): Personal Finance Tracker PRD, Audit trail (INT-2), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Milestone 3 (insights), Non-functional requirements, Security, privacy and integrity requirements (SEC/INT) (+3 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.05
Nodes (19): ChoiceRow(), DesignCatalog(), DevContent(), resolveColorScheme(), TokenSheet(), TokenSheetPreview(), TokenSheetPage, TokenSheetPageFrame() (+11 more)

### Community 36 - "Delivery constraints (small vertical slices, Claude Pro limits)"
Cohesion: 0.33
Nodes (4): Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 38 - "UserId"
Cohesion: 0.14
Nodes (24): FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE, UNARCHIVE, UPDATE (+16 more)

### Community 40 - "Public interface"
Cohesion: 0.16
Nodes (15): Configuration, Responsibilities & boundaries, AuditDiff, AuditedWriter, AuditLog, AuditRecorder, IdentityService, Uuid7 (+7 more)

### Community 41 - "AuditedWriterTest.kt"
Cohesion: 0.10
Nodes (11): How to run and test, SettingKeys, AlreadyInitialised, AuditDiffTest, AuditedWriterTest, EntityMetaTest, IdentityServiceTest, IdsTest (+3 more)

### Community 42 - "Project README"
Cohesion: 0.22
Nodes (6): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Code Structure Map, core/shared Shared Packages, Project README, Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "Auditable"
Cohesion: 0.12
Nodes (6): Auditable, Device, IdentityRepository, UserProfile, Thing, InMemoryIdentityRepository

### Community 44 - "Research: Indian Bank SMS Formats"
Cohesion: 0.25
Nodes (8): Parse log (S-11), Sender alias registry (S-13), Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser, Public Bank SMS Header Registry (gist, Dec 2022), Open-Source Indian SMS Parsers (hisab-kitab, FinTrack, ...), Indian SMS Sender Header Format (prefix-HEADER-S), SMS Spoofing and Phishing

### Community 45 - "AuditedWriter"
Cohesion: 0.17
Nodes (6): Behaviours worth knowing, EntityRepository, IdentityCorrupt, NotInitialised, Slice B — Accounts, categories, tags (`feat/m1a-ledger-setup`), Slice C — Transactions (`feat/m1a-transactions`)

### Community 46 - "PftTypography.kt"
Cohesion: 0.24
Nodes (4): displayFamily(), pftMaterialTypography(), pftType(), PftTypography

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.19
Nodes (10): code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /dev Command (implement then auto-review), CLAUDE.md Development Constitution, >=90% Coverage Floor on core, Documentation Taxonomy, Milestones M1a/M1b/M1c, M2, M3 (+2 more)

### Community 48 - "AppSetting"
Cohesion: 0.16
Nodes (10): AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape (+2 more)

### Community 49 - "Definition of Done"
Cohesion: 0.29
Nodes (10): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), /architect Command, /review-changes Command, Pull Request Template, PR Checklist (TDD, Docs, Security, Architecture, Sizing), PR Security Review Section (TECHNICAL.md §5), Definition of Done (+2 more)

### Community 50 - "LedgerError"
Cohesion: 0.20
Nodes (9): AmountNotPositive, Archived, BlankName, Deleted, InvalidSettingKey, LedgerError, NameTooLong, NotFound (+1 more)

### Community 51 - "Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...)"
Cohesion: 0.38
Nodes (3): Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), InMemoryAuditLog, InMemoryLedgerStore

### Community 52 - "Note"
Cohesion: 0.16
Nodes (6): Fixture, Leaky, leakyRepository(), Note, NoteId, InMemoryEntityRepository

### Community 53 - "Domain Safety Invariants"
Cohesion: 0.22
Nodes (4): Android Surface and Multi-user Restrictions, Immutable Audit Trail, Money as Integer Paise, Untrusted Input Validation (SMS/email/LLM output)

### Community 54 - "ThemePrefs"
Cohesion: 0.21
Nodes (10): DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, ThemeMode, DARK, LIGHT, SYSTEM (+2 more)

### Community 57 - "MoneyColors"
Cohesion: 0.16
Nodes (11): MoneyColors, PftSpacing, Theming, 3.1 Visual direction: "Cashew, but premium", 3.3 Tokens, 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6) (+3 more)

### Community 66 - "EntityMeta"
Cohesion: 0.25
Nodes (6): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, Purpose, EntityMeta, Widget

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

## Knowledge Gaps
- **54 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `BUNDLED_SERIF`, `SYSTEM_SERIF` (+49 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 311 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **19 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `EntityMeta`, `Collecting and Redacting Sample Messages Guide`, `Personal Finance Tracker PRD`, ``build-logic` — convention plugins`, `Research: Indian Bank SMS Formats`, `CLAUDE.md Development Constitution`, `AppSetting`, `Domain Safety Invariants`, `Paise`?**
  _High betweenness centrality (0.212) - this node is a cross-community bridge._
- **Are the 40 inferred relationships involving `Public interface` (e.g. with `AuditDiff` and `FieldChange`) actually correct?**
  _`Public interface` has 40 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _54 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.05288207297726071 - nodes in this community are weakly interconnected._
- **Why does `6.1 Design-system slice: PRs into `feat/m1a-foundation`` connect `redact.py` to `TokenSheetSections.kt`, `ManifestHardeningTest`, `PftTypographyTest`, `PftShapesTest.kt`, `Public interface`, `BundledFontTest.kt`, `PftTypography.kt`, `ColorContrastTest`, `BrandColors`, `ThemePrefs`, `MoneyColors`, `HostedComposeRule`, `Paise`?**
  _High betweenness centrality (0.198) - this node is a cross-community bridge._
- **Are the 4 inferred relationships involving `EntityMeta` (e.g. with `Data it owns` and `Responsibilities & boundaries`) actually correct?**
  _`EntityMeta` has 4 INFERRED edges - model-reasoned connections that need verification._
- **Should `ManifestHardeningTest` be split into smaller, more focused modules?**
  _Cohesion score 0.05974025974025974 - nodes in this community are weakly interconnected._