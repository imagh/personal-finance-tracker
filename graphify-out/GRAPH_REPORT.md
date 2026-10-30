# Graph Report - personal-finance-tracker  (2026-10-30)

## Corpus Check
- 171 files · ~106,469 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 30 file(s) not represented in the graph (top: .xml 10, .lockfile 8, (none) 4)

## Summary
- 1791 nodes · 4458 edges · 103 communities (55 shown, 48 thin omitted)
- Extraction: 88% EXTRACTED · 12% INFERRED · 0% AMBIGUOUS · INFERRED: 513 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `2f62a8ee`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- runSuspending
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- WindowThemeTest.kt
- hooks
- RedactTests
- LedgerEntity
- BundledFontTest.kt
- Plan: M1a — Core ledger (manual-entry household tracker)
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- BottomActionBar
- Catalog.kt
- detekt.yml
- HostedComposeRule
- Public interface
- CollapsingHeaderScaffoldTest
- HeaderScreenshotTest
- Paise
- Uuid7Test.kt
- CollapsingHeaderScaffold.kt
- Security, privacy and integrity requirements (SEC/INT)
- ColorSchemeTestSupport.kt
- TokenSheetSections.kt
- DesignCatalog.kt
- PftTypographyTest
- AuditEntry
- PftShapesTest.kt
- TransactionRunner
- TestClock
- Project README
- UserProfile
- PftMotion.kt
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- 3.7 Component inventory (UI-9)
- CLAUDE.md Development Constitution
- AppSetting
- ManifestHardeningTest
- LedgerError
- PressScaleTest.kt
- Leaky
- RoomAuditColumnsTest
- AuditFieldsCheckTest.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- CollapsingHeaderScaffold
- UserId
- Personal Finance Tracker PRD
- AuditEntryMapperTest.kt
- redact_text
- InMemoryEntityRepository
- Architecture overview
- SampleThing.kt
- Pseudo
- assertequals
- IdentityService
- Collecting and Redacting Sample Messages Guide
- PftDatabase
- RoomAuditedWriterIntegrationTest.kt
- _Text
- 5. Flows
- BottomActionBar.kt
- HeaderPolicy
- MotionSection.kt
- HeaderSamples.kt
- ThemePrefs
- AuditTriggersTest
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- HeaderSlot
- HeaderPhase
- MigrationTestSupport
- AppSettingEntity
- AuditDaoTest.kt
- Auditable
- MigrationTest
- Conventions
- installAuditTriggers
- RoomEntityRepository
- ThemeResolutionTest.kt
- PftTheme.kt
- `:core:money` — integer-paise money
- PaiseParser
- BaseDao
- TokenSheet
- PaiseFormat
- BrandColors
- Migrations.kt
- Immutable Audit Trail

## God Nodes (most connected - your core abstractions)
1. `CollapsingHeaderScaffoldTest` - 64 edges
2. `UserId` - 57 edges
3. `runSuspending()` - 51 edges
4. `EntityMeta` - 50 edges
5. `DeviceId` - 48 edges
6. `3.4 Reuse by layer (owner decision, 2026-10-28)` - 47 edges
7. `Public interface` - 42 edges
8. `Paise` - 41 edges
9. `AuditEntry` - 38 edges
10. `How to run and test` - 36 edges

## Surprising Connections (you probably didn't know these)
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `9. Documentation updates (all required surfaces)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt
- `Slice H — App lock, diagnostics, release (`feat/m1a-hardening`)` --references--> `TokenSheet()`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/TokenSheet.kt
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

## Communities (103 total, 48 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.14
Nodes (9): _day(), _email_record(), html_to_text(), read_adb(), read_any(), read_eml(), read_jsonl(), read_mbox() (+1 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.15
Nodes (16): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+8 more)

### Community 2 - "runSuspending"
Cohesion: 0.06
Nodes (9): How to run and test, RoomEntityRepositoryTest, FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomTransactionRunnerTest, runSuspending() (+1 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.14
Nodes (9): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits) (+1 more)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "LedgerEntity"
Cohesion: 0.23
Nodes (3): LedgerEntity, AuditFieldsCheck, haveCompleteAuditFields()

### Community 10 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.20
Nodes (10): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5. Flows, 8. Security considerations (+2 more)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.13
Nodes (5): ColorContrastTest, Theme, DARK, LIGHT, PftSpacingTest

### Community 16 - "BottomActionBar"
Cohesion: 0.09
Nodes (20): BottomActionBar(), BottomBarAction, Adding a token, Build-time trust decisions, Colour, Design system, Motion (UI-10), One-hand layout (UI-7) (+12 more)

### Community 18 - "detekt.yml"
Cohesion: 0.20
Nodes (9): complexity, LongParameterList, ignoreAnnotated, ignoreDefaultParameters, excludes, naming, FunctionNaming, style (+1 more)

### Community 26 - "HostedComposeRule"
Cohesion: 0.07
Nodes (6): HostedComposeRule, RegisterHostActivity, Statement, NavTransitionTest, PftThemeModeTest, TokenSheetScreenshotTest

### Community 27 - "Public interface"
Cohesion: 0.13
Nodes (12): Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How an entity step composes the blocks, Public interface, Purpose, Responsibilities & boundaries (+4 more)

### Community 29 - "HeaderScreenshotTest"
Cohesion: 0.18
Nodes (5): DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 31 - "Uuid7Test.kt"
Cohesion: 0.10
Nodes (7): NameBasedIdTest, clockAt(), Uuid7Test, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest

### Community 33 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.20
Nodes (11): TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle(), ShapesAndSpacing() (+3 more)

### Community 36 - "DesignCatalog.kt"
Cohesion: 0.15
Nodes (10): CatalogSection, BOTTOM_BAR, DASHBOARD_HEADER, MOTION, STANDARD_HEADER, TOKENS, ChoiceRow(), DesignCatalog() (+2 more)

### Community 37 - "PftTypographyTest"
Cohesion: 0.09
Nodes (5): displayFamily(), pftMaterialTypography(), pftType(), PftTypographyTest, Resolved

### Community 38 - "AuditEntry"
Cohesion: 0.12
Nodes (22): FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE, UNARCHIVE, UPDATE (+14 more)

### Community 40 - "TransactionRunner"
Cohesion: 0.12
Nodes (5): RoomTransactionRunner, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), TransactionRunner, InMemoryEntityStore, InMemoryLedgerStore

### Community 42 - "Project README"
Cohesion: 0.21
Nodes (6): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Code Structure Map, core/shared Shared Packages, Project README, Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "UserProfile"
Cohesion: 0.10
Nodes (6): RoomIdentityRepository, RoomIdentityRepositoryTest, Device, IdentityRepository, UserProfile, InMemoryIdentityRepository

### Community 45 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.19
Nodes (8): Behaviours worth knowing, AuditedWriter, EntityRepository, IdentityCorrupt, shouldHaveCompleteAuditFields(), 3.4 Reuse by layer (owner decision, 2026-10-28), Slice B — Accounts, categories, tags (`feat/m1a-ledger-setup`), Slice C — Transactions (`feat/m1a-transactions`)

### Community 46 - "3.7 Component inventory (UI-9)"
Cohesion: 0.11
Nodes (14): `ui/theme` (design-system tokens, DS1, DS2, DS4), MoneyColors, pressScale(), PftShapes, PftSpacing, PftTypography, Theming, 3.1 Visual direction: "Cashew, but premium" (+6 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "AppSetting"
Cohesion: 0.13
Nodes (9): `data` (Room persistence, A4a), AuditTriggers, MetaMapper, RoomAppSettingsRepository, RoomAppSettingsRepositoryTest, AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository (+1 more)

### Community 50 - "LedgerError"
Cohesion: 0.09
Nodes (21): RoomAuditLog, AlreadyInitialised, AmountNotPositive, AmountTooLarge, Archived, BlankName, Deleted, InvalidSettingKey (+13 more)

### Community 51 - "PressScaleTest.kt"
Cohesion: 0.06
Nodes (3): BottomActionBarTest, PressScaleTest, DesignCatalogTest

### Community 52 - "Leaky"
Cohesion: 0.24
Nodes (4): Leaky, LeakyRepository, Note, NoteId

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (4): Probe, RoomAuditColumnsTest, AuditColumnsCheck, AuditedTable

### Community 54 - "AuditFieldsCheckTest.kt"
Cohesion: 0.09
Nodes (14): AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind, LARGE, SMALL, MisMapped (+6 more)

### Community 55 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.13
Nodes (19): `:app` — Android application, Configuration, Data it owns, Debug and release source sets, Dependencies, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose (+11 more)

### Community 56 - "CollapsingHeaderScaffold"
Cohesion: 0.26
Nodes (14): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle, Dashboard (+6 more)

### Community 57 - "UserId"
Cohesion: 0.16
Nodes (9): MetaMapperTest, AuditedTables, EntityMeta, DeviceId, EntityId, UserId, EntityMetaTest, ValidationTest (+1 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.08
Nodes (32): Personal Finance Tracker PRD, Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Automatic capture from Email (E-0..E-5) (+24 more)

### Community 60 - "redact_text"
Cohesion: 0.27
Nodes (6): mask_run(), process(), redact_text(), amt(), hold(), review_flags()

### Community 62 - "Architecture overview"
Cohesion: 0.33
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 63 - "SampleThing.kt"
Cohesion: 0.18
Nodes (7): MetaColumns, SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 65 - "assertequals"
Cohesion: 0.24
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 66 - "IdentityService"
Cohesion: 0.19
Nodes (7): RoomTestKit, AuditRecorder, IdentityService, Uuid7, SettingsService, LedgerTestKit, The audited write path

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 68 - "PftDatabase"
Cohesion: 0.18
Nodes (3): AuditDao, AuditEntryEntity, PftDatabase

### Community 69 - "RoomAuditedWriterIntegrationTest.kt"
Cohesion: 0.12
Nodes (8): TestDatabase, How to run and test, AuditLog, AuditedWriterTest, IdentityServiceTest, IdsTest, SettingsServiceTest, 7. Test plan

### Community 72 - "5. Flows"
Cohesion: 0.50
Nodes (4): 5.3 Collapsing header, 5.4 Custom font import (AP2), 5.5 Mockups (wireframes; the styled version is in the HTML file), 5. Flows

### Community 78 - "ThemePrefs"
Cohesion: 0.24
Nodes (9): DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, ThemeMode, DARK, LIGHT, SYSTEM (+1 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.20
Nodes (8): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 8. Security considerations, 9. Documentation updates (all required surfaces), Plan: M1a design system and visual direction (lands before A5)

### Community 82 - "UserProfileEntity"
Cohesion: 0.24
Nodes (3): IdentityDao, DeviceEntity, UserProfileEntity

### Community 83 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 84 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 88 - "Auditable"
Cohesion: 0.21
Nodes (5): AuditDiff, Auditable, SettingKeys, AuditDiffTest, Thing

### Community 90 - "Conventions"
Cohesion: 0.18
Nodes (6): NameBasedId, Conventions, Data model, The audit trail, Version 1 (A4a), Version table

### Community 95 - "`:core:money` — integer-paise money"
Cohesion: 0.22
Nodes (8): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose

### Community 99 - "PaiseFormat"
Cohesion: 0.33
Nodes (4): Responsibilities & boundaries, PaiseFormat, 2. Current state, 5.2 Rendering an amount

## Knowledge Gaps
- **70 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+65 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 409 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **48 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `runSuspending` to `WindowThemeTest.kt`, `BundledFontTest.kt`, `ColorContrastTest`, `HostedComposeRule`, `CollapsingHeaderScaffoldTest`, `HeaderScreenshotTest`, `PftTypographyTest`, `PftShapesTest.kt`, `UserProfile`, `AppSetting`, `PressScaleTest.kt`, `RoomAuditColumnsTest`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `UserId`, `AuditEntryMapperTest.kt`, `SampleThing.kt`, `assertequals`, `IdentityService`, `PftDatabase`, `AuditTriggersTest`, `expandedHeaderHeight`, `MigrationTestSupport`, `AuditDaoTest.kt`, `MigrationTest`?**
  _High betweenness centrality (0.281) - this node is a cross-community bridge._
- **Are the 5 inferred relationships involving `CollapsingHeaderScaffoldTest` (e.g. with `How to run and test` and `One-hand layout (UI-7)`) actually correct?**
  _`CollapsingHeaderScaffoldTest` has 5 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _70 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1422924901185771 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Collecting and Redacting Sample Messages Guide`, `Automatic capture from SMS (S-1..S-13)`, ``build-logic` — convention plugins`, `CLAUDE.md Development Constitution`, `Personal Finance Tracker PRD`, `Public interface`, ``:core:money` — integer-paise money`?**
  _High betweenness centrality (0.173) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `UserId` (e.g. with `Public interface` and `3.4 Reuse by layer (owner decision, 2026-10-28)`) actually correct?**
  _`UserId` has 2 INFERRED edges - model-reasoned connections that need verification._
- **Should `runSuspending` be split into smaller, more focused modules?**
  _Cohesion score 0.05877167205406994 - nodes in this community are weakly interconnected._