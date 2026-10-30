# Graph Report - pft-wt-ds5  (2026-10-30)

## Corpus Check
- 192 files · ~119,557 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 30 file(s) not represented in the graph (top: .xml 10, .lockfile 8, (none) 4)

## Summary
- 2091 nodes · 5577 edges · 114 communities (64 shown, 50 thin omitted)
- Extraction: 88% EXTRACTED · 12% INFERRED · 0% AMBIGUOUS · INFERRED: 688 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `e5a9a5c6`
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
- AuditFieldsCheck.kt
- ThemeResolutionTest.kt
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Catalog.kt
- detekt.yml
- NavTransitionTest.kt
- MoneyTextTest
- CollapsingHeaderScaffoldTest
- HeaderScreenshotTest
- .format
- Uuid7Test.kt
- CollapsingHeaderScaffold.kt
- Security, privacy and integrity requirements (SEC/INT)
- HeaderSamples.kt
- TokenSheetSections.kt
- DesignCatalog.kt
- PftTypographyTest
- UserId
- LedgerError
- Public interface
- TestClock
- Project README
- Auditable
- PftMotion.kt
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- MoneyColors
- CLAUDE.md Development Constitution
- MoneyText
- ManifestHardeningTest
- GroupedSectionTest.kt
- StatusChip
- AuditedWriterTest.kt
- AuditFieldsCheckTest.kt
- WidgetId
- How to run and test
- CollapsingHeaderScaffold
- MoneyText
- Personal Finance Tracker PRD
- InMemoryEntityStore
- redact_text
- DesignCatalogTest.kt
- Architecture overview
- SampleThing.kt
- Pseudo
- GroupedSection.kt
- ContentScreenshotTest
- Collecting and Redacting Sample Messages Guide
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Paise
- _Text
- StatusChipTest.kt
- test_redact.py
- BottomActionBar.kt
- HeaderPolicy
- PftTheme.kt
- modifier
- ThemePrefs
- MoneyTextTest.kt
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- WindowThemeTest.kt
- HeaderSlot
- HeaderPhase
- AppSetting
- TokenSheet
- PaiseParser
- MoneyTextSamples.kt
- MoneyTextTest.kt
- ContentScreenshotTest
- EmptyStateSamples.kt
- Complete
- ValidationTest.kt
- PftTheme.kt
- Plan: M1a — Core ledger (manual-entry household tracker)
- Paise
- `data` (Room persistence, A4a)
- GroupedSectionSamples
- .format
- WindowThemeTest.kt
- Migrations.kt
- Immutable Audit Trail
- Loans and EMIs (LN-1..LN-5)
- WindowThemeTest
- NameBasedId
- PaiseParser
- InMemoryEntityStore
- MoneyTextSamples.kt
- Architecture overview
- MoneySize
- NameBasedIdTest.kt
- PaiseFormatTest.kt

## God Nodes (most connected - your core abstractions)
1. `Paise` - 90 edges
2. `ThemePrefs` - 68 edges
3. `CollapsingHeaderScaffoldTest` - 68 edges
4. `UserId` - 57 edges
5. `runSuspending()` - 54 edges
6. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 54 edges
7. `EntityMeta` - 50 edges
8. `3.4 Reuse by layer (owner decision, 2026-10-28)` - 50 edges
9. `MoneyText()` - 49 edges
10. `MoneyTextTest` - 48 edges

## Surprising Connections (you probably didn't know these)
- ``EmptyState`` --references--> `EmptyState()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/EmptyState.kt
- ``StatusChip`` --references--> `StatusChip()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/StatusChip.kt
- `3.2 Theming architecture (no new abstraction)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt
- `Slice H — App lock, diagnostics, release (`feat/m1a-hardening`)` --references--> `TokenSheet()`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/TokenSheet.kt
- `Security posture of the shell` --references--> `ManifestHardeningTest`  [INFERRED]
  docs/architecture/overview.md → app/src/test/kotlin/dev/imagh/pft/app/ManifestHardeningTest.kt

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

## Communities (114 total, 50 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (8): _day(), _email_record(), html_to_text(), read_adb(), read_any(), read_eml(), read_mbox(), read_sms_xml()

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.16
Nodes (15): Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup, Secrets via Environment Variables (+7 more)

### Community 2 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.05
Nodes (8): FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAppSettingsRepositoryTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomIdentityRepositoryTest, RoomTransactionRunnerTest, runSuspending()

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.14
Nodes (9): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits) (+1 more)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "AuditFieldsCheck.kt"
Cohesion: 0.22
Nodes (3): AuditFieldsCheck, haveCompleteAuditFields(), shouldHaveCompleteAuditFields()

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.08
Nodes (7): BundledFontTest, ColorContrastTest, Theme, DARK, LIGHT, `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`()

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.14
Nodes (27): `ui/common` (shared components, DS4 and DS5), GroupedSection(), moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER (+19 more)

### Community 18 - "detekt.yml"
Cohesion: 0.20
Nodes (9): complexity, LongParameterList, ignoreAnnotated, ignoreDefaultParameters, excludes, naming, FunctionNaming, style (+1 more)

### Community 27 - "MoneyTextTest"
Cohesion: 0.23
Nodes (4): How an entity step composes the blocks, Public interface, Identity, Validation

### Community 29 - "HeaderScreenshotTest"
Cohesion: 0.33
Nodes (5): DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 31 - "Uuid7Test.kt"
Cohesion: 0.14
Nodes (6): clockAt(), Uuid7Test, How to run and test, PaiseParserTest, PaiseTest, 7. Test plan

### Community 33 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "DesignCatalog.kt"
Cohesion: 0.08
Nodes (17): CatalogSection, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, MONEY, MOTION, SECTIONS (+9 more)

### Community 37 - "PftTypographyTest"
Cohesion: 0.09
Nodes (7): `ui/theme` (design-system tokens, DS1, DS2, DS4), displayFamily(), pftMaterialTypography(), pftType(), PftTypography, PftTypographyTest, Resolved

### Community 38 - "UserId"
Cohesion: 0.09
Nodes (26): EpochMillis, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE, UNARCHIVE (+18 more)

### Community 40 - "Public interface"
Cohesion: 0.21
Nodes (5): RoomTransactionRunner, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), TransactionRunner, InMemoryLedgerStore, The core/app boundary

### Community 42 - "Project README"
Cohesion: 0.21
Nodes (6): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Code Structure Map, core/shared Shared Packages, Project README, Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "Auditable"
Cohesion: 0.16
Nodes (5): RoomIdentityRepository, Device, IdentityRepository, UserProfile, InMemoryIdentityRepository

### Community 44 - "PftMotion.kt"
Cohesion: 0.08
Nodes (5): iosSpring(), PftMotion, pressScale(), PftMotionTest, 7. Test plan

### Community 45 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.22
Nodes (6): Behaviours worth knowing, AuditedWriter, EntityRepository, IdentityCorrupt, 3.4 Reuse by layer (owner decision, 2026-10-28), Slice B — Accounts, categories, tags (`feat/m1a-ledger-setup`)

### Community 46 - "MoneyColors"
Cohesion: 0.13
Nodes (11): ChipColors, MoneyColors, PftSpacing, Theming, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens, 3.5 Fonts: bundling, licensing, custom import (+3 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.11
Nodes (24): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+16 more)

### Community 48 - "MoneyText"
Cohesion: 0.21
Nodes (5): RoomAppSettingsRepository, AppSetting, AppSettingsRepository, SettingKeys, InMemoryAppSettingsRepository

### Community 49 - "ManifestHardeningTest"
Cohesion: 0.18
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 50 - "GroupedSectionTest.kt"
Cohesion: 0.09
Nodes (16): Auditable, AlreadyInitialised, AmountNotPositive, AmountTooLarge, Archived, BlankName, Deleted, InvalidSettingKey (+8 more)

### Community 51 - "StatusChip"
Cohesion: 0.06
Nodes (3): BottomActionBarTest, GroupedSectionTest, DesignCatalogTest

### Community 52 - "AuditedWriterTest.kt"
Cohesion: 0.24
Nodes (5): Fixture, Leaky, LeakyRepository, Note, NoteId

### Community 53 - "AuditFieldsCheckTest.kt"
Cohesion: 0.14
Nodes (4): Probe, RoomAuditColumnsTest, AuditColumnsCheck, AuditedTable

### Community 54 - "WidgetId"
Cohesion: 0.09
Nodes (18): EntityMeta, LedgerEntity, EntityId, AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind (+10 more)

### Community 55 - "How to run and test"
Cohesion: 0.18
Nodes (13): `:app` — Android application, Configuration, Data it owns, Debug and release source sets, Dependencies, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose (+5 more)

### Community 56 - "CollapsingHeaderScaffold"
Cohesion: 0.28
Nodes (13): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle, Dashboard (+5 more)

### Community 57 - "MoneyText"
Cohesion: 0.15
Nodes (9): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText(), RollingDigits() (+1 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.11
Nodes (22): Personal Finance Tracker PRD, Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope, Idempotent ingestion (X-2) (+14 more)

### Community 60 - "redact_text"
Cohesion: 0.27
Nodes (6): mask_run(), process(), redact_text(), amt(), hold(), review_flags()

### Community 63 - "SampleThing.kt"
Cohesion: 0.14
Nodes (7): RoomEntityRepositoryTest, SampleId, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 65 - "GroupedSection.kt"
Cohesion: 0.11
Nodes (10): GroupedRow, groupedSectionColor(), GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, Colour, Content primitives (DS5), `EmptyState` (+2 more)

### Community 66 - "ContentScreenshotTest"
Cohesion: 0.16
Nodes (11): RoomTestKit, Configuration, AuditDiff, AuditRecorder, IdentityService, Uuid7, SettingsService, LedgerLimits (+3 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 69 - "Paise"
Cohesion: 0.11
Nodes (8): AuditedTables, How to run and test, AuditedWriterTest, EntityMetaTest, IdentityServiceTest, IdsTest, SettingsServiceTest, ValidationTest

### Community 72 - "StatusChipTest.kt"
Cohesion: 0.38
Nodes (3): DeviceEntity, UserProfileEntity, SampleDatabase

### Community 74 - "BottomActionBar.kt"
Cohesion: 0.09
Nodes (16): BottomActionBar(), BottomActionBarPreview(), BottomActionBarSamples(), BottomBarAction, Adding a token, Build-time trust decisions, Design system, Motion (UI-10) (+8 more)

### Community 76 - "PftTheme.kt"
Cohesion: 0.20
Nodes (5): MotionSection(), SpringTrack(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder()

### Community 78 - "ThemePrefs"
Cohesion: 0.12
Nodes (12): EmptyState(), DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, ThemeMode, DARK, LIGHT (+4 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.13
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 83 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 84 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 85 - "AppSetting"
Cohesion: 0.08
Nodes (9): AuditTriggers, EpochMillisTest, MigrationTestSupport, MigrationTest, Conventions, Data model, The audit trail, Version 1 (A4a) (+1 more)

### Community 88 - "MoneyTextSamples.kt"
Cohesion: 0.33
Nodes (5): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, Purpose, Responsibilities & boundaries

### Community 89 - "MoneyTextTest.kt"
Cohesion: 0.12
Nodes (5): layoutResult(), shownText(), assertShownInFull(), isShownInFull(), layout()

### Community 91 - "EmptyStateSamples.kt"
Cohesion: 0.26
Nodes (3): How to run and test, PftDatabase, TestDatabase

### Community 92 - "Complete"
Cohesion: 0.14
Nodes (3): BaseDao, EntityMapper, RoomEntityRepository

### Community 94 - "PftTheme.kt"
Cohesion: 0.12
Nodes (3): StatusChip(), resolveColorScheme(), StatusChipTest

### Community 95 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.10
Nodes (20): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 3.1 Modules (Gradle), 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3.5 Design system (owner decision, 2026-10-29) (+12 more)

### Community 97 - "`data` (Room persistence, A4a)"
Cohesion: 0.29
Nodes (4): `data` (Room persistence, A4a), MetaColumns, MetaMapper, MetaMapperTest

### Community 98 - "GroupedSectionSamples"
Cohesion: 0.17
Nodes (4): GroupedSectionPreview(), GroupedSectionSamples(), TokenSheet(), TokenSheetPreview()

### Community 99 - ".format"
Cohesion: 0.18
Nodes (10): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+2 more)

### Community 103 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5), M1a Core ledger (+2 more)

### Community 109 - "MoneyTextSamples.kt"
Cohesion: 0.67
Nodes (4): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples()

### Community 110 - "Architecture overview"
Cohesion: 0.33
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 111 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

## Knowledge Gaps
- **83 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+78 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 419 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **50 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `EmptyStateSamples.kt` to `Loans and EMIs (LN-1..LN-5)`, `ThemeResolutionTest.kt`, `MoneyTextTest`, `ColorContrastTest`, `NavTransitionTest.kt`, `CollapsingHeaderScaffoldTest`, `HeaderScreenshotTest`, `.format`, `PftTypographyTest`, `LedgerError`, `PftMotion.kt`, `StatusChip`, `AuditFieldsCheckTest.kt`, `How to run and test`, `MoneyText`, `InMemoryEntityStore`, `Architecture overview`, `SampleThing.kt`, `ContentScreenshotTest`, `ThemePrefs`, `MoneyTextTest.kt`, `expandedHeaderHeight`, `AppSetting`, `PaiseParser`, `ContentScreenshotTest`, `PftTheme.kt`, `Paise`, ``data` (Room persistence, A4a)`, `.format`, `WindowThemeTest`?**
  _High betweenness centrality (0.225) - this node is a cross-community bridge._
- **Are the 14 inferred relationships involving `Paise` (e.g. with ``ui/common` (shared components, DS4 and DS5)` and `Behaviours worth knowing`) actually correct?**
  _`Paise` has 14 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _83 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.14761904761904762 - nodes in this community are weakly interconnected._
- **Why does `Paise` connect `Paise` to `Loans and EMIs (LN-1..LN-5)`, `AuditFieldsCheck.kt`, `MoneyTextTest`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `MoneyTextTest`, `CollapsingHeaderScaffoldTest`, `HeaderScreenshotTest`, `UserId`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `GroupedSectionTest.kt`, `WidgetId`, `MoneyText`, `SampleThing.kt`, `ContentScreenshotTest`, `Paise`, `modifier`, `MoneyTextSamples.kt`, `MoneyTextTest.kt`, `Plan: M1a — Core ledger (manual-entry household tracker)`, `GroupedSectionSamples`, `.format`, `PaiseParser`, `MoneyTextSamples.kt`, `MoneySize`?**
  _High betweenness centrality (0.193) - this node is a cross-community bridge._
- **Are the 11 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `TokenSheetPreview()`) actually correct?**
  _`ThemePrefs` has 11 INFERRED edges - model-reasoned connections that need verification._
- **Should `Loans and EMIs (LN-1..LN-5)` be split into smaller, more focused modules?**
  _Cohesion score 0.05225885225885226 - nodes in this community are weakly interconnected._