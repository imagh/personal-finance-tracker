# Graph Report - pft-wt-a4b  (2026-10-30)

## Corpus Check
- 215 files · ~146,891 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 30 file(s) not represented in the graph (top: .xml 10, .lockfile 8, (none) 4)

## Summary
- 2413 nodes · 6461 edges · 122 communities (70 shown, 52 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 833 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `2f055f80`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- runSuspending
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- ManifestHardeningTest
- hooks
- RedactTests
- PreMigrationBackupTest
- DbKeyManagerJvmTest
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Catalog.kt
- detekt.yml
- NavTransitionTest.kt
- PressScaleTest.kt
- CollapsingHeaderScaffoldTest
- HeaderScreenshotTest
- TokenSheetScreenshotTest.kt
- shouldbe
- CollapsingHeaderScaffold.kt
- EncryptedDatabaseTest.kt
- ColorSchemeTestSupport.kt
- TokenSheetSections.kt
- DesignCatalog.kt
- PftTypographyTest
- AuditEntry
- DatabaseFactoryTest.kt
- AppSetting
- PftShapesTest.kt
- Project README
- UserId
- PftMotion.kt
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- 3. Design & decision
- CLAUDE.md Development Constitution
- How to run and test
- HostedComposeRule
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- GroupedSectionTest.kt
- AuditedWriterTest.kt
- RoomAuditColumnsTest
- LedgerEntity
- MainActivity
- CollapsingHeaderScaffold
- MoneyText
- Personal Finance Tracker PRD
- RoomEntityRepositoryTest.kt
- redact_text
- PftTypography.kt
- PftTheme.kt
- SampleThing.kt
- Pseudo
- GroupedSection.kt
- Public interface
- Collecting and Redacting Sample Messages Guide
- IdentityRoomIntegrationTest
- PreMigrationBackup
- _Text
- BottomActionBarTest.kt
- BottomActionBar.kt
- HeaderPolicy
- PftTheme.kt
- text
- ThemePrefs
- AuditTriggersTest
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- PftDatabase
- HeaderSlot
- HeaderPhase
- MigrationTestSupport
- RoomTransactionRunnerTest
- PftThemeModeTest
- DesignCatalogTest.kt
- MoneyTextTest.kt
- ContentScreenshotTest
- RoomIdentityRepositoryTest.kt
- Security, privacy and integrity requirements (SEC/INT)
- DbKeyManagerTest.kt
- StatusChipTest.kt
- Plan: M1a — Core ledger (manual-entry household tracker)
- MoneyToneTest
- HeaderStyle
- TokenSheet
- .format
- BrandColors
- Migrations.kt
- AuditLog
- TestClock
- PaiseParser
- InMemoryLedgerStore
- MoneyTextSamples.kt
- MoneySize
- Design system
- DatabaseFactory
- DbKeyManager
- assertequals
- modifier
- DatabaseStartupTest.kt
- AndroidKeystoreKek.kt
- DbKeyManagerWiringTest.kt
- 3.9 Scope: app-wide
- Reason
- main
- BaseDao
- Layered Modular Architecture (core vs app)

## God Nodes (most connected - your core abstractions)
1. `Paise` - 90 edges
2. `ThemePrefs` - 74 edges
3. `CollapsingHeaderScaffoldTest` - 70 edges
4. `UserId` - 57 edges
5. `runSuspending()` - 54 edges
6. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 54 edges
7. `How to run and test` - 51 edges
8. `3.4 Reuse by layer (owner decision, 2026-10-28)` - 51 edges
9. `MoneyText()` - 50 edges
10. `EntityMeta` - 50 edges

## Surprising Connections (you probably didn't know these)
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `3.4 Header glow (UI-13, DS6d)` --references--> `HeaderPolicy`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/CollapsingHeaderScaffold.kt
- `3.5 Icon-only header actions (UI-14, DS6e; needs DS3)` --references--> `PeriodStepper()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/CollapsingHeaderScaffold.kt
- ``EmptyState`` --references--> `EmptyState()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/EmptyState.kt
- ``StatusChip`` --references--> `StatusChip()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/StatusChip.kt

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

## Communities (122 total, 52 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.14
Nodes (9): _day(), _email_record(), html_to_text(), read_adb(), read_any(), read_eml(), read_jsonl(), read_mbox() (+1 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "ManifestHardeningTest"
Cohesion: 0.06
Nodes (9): ManifestHardeningTest, WindowThemeTest, clockAt(), Uuid7Test, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest (+1 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.13
Nodes (5): ColorContrastTest, Theme, DARK, LIGHT, PftSpacingTest

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.17
Nodes (21): moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense (+13 more)

### Community 18 - "detekt.yml"
Cohesion: 0.20
Nodes (9): complexity, LongParameterList, ignoreAnnotated, ignoreDefaultParameters, excludes, naming, FunctionNaming, style (+1 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.09
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "HeaderScreenshotTest"
Cohesion: 0.14
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 31 - "shouldbe"
Cohesion: 0.07
Nodes (12): EpochMillis, How to run and test, AuditLog, SettingKeys, AuditDiffTest, Thing, EntityMetaTest, IdentityServiceTest (+4 more)

### Community 33 - "EncryptedDatabaseTest.kt"
Cohesion: 0.10
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.25
Nodes (11): TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle(), ShapesAndSpacing() (+3 more)

### Community 36 - "DesignCatalog.kt"
Cohesion: 0.11
Nodes (16): CatalogSection, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, MONEY, MOTION, SECTIONS (+8 more)

### Community 38 - "AuditEntry"
Cohesion: 0.06
Nodes (23): AuditEntryMapper, RoomAuditLog, AuditDaoTest, AuditEntryMapperTest, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), FieldChange, AuditAction, ARCHIVE (+15 more)

### Community 39 - "DatabaseFactoryTest.kt"
Cohesion: 0.13
Nodes (8): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`(), `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`()

### Community 40 - "AppSetting"
Cohesion: 0.25
Nodes (5): RoomAppSettingsRepository, AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository, The core/app boundary

### Community 42 - "Project README"
Cohesion: 0.14
Nodes (13): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer (+5 more)

### Community 43 - "UserId"
Cohesion: 0.13
Nodes (9): RoomIdentityRepository, AuditedTables, Auditable, Device, IdentityRepository, UserProfile, DeviceId, UserId (+1 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.09
Nodes (6): PftMotion, pressScale(), 3.1 Modules (Gradle), 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3.5 Design system (owner decision, 2026-10-29), 3. Design & decision

### Community 45 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.13
Nodes (12): Behaviours worth knowing, AuditDiff, AuditedWriter, AuditSource, MANUAL, RECURRING, SYSTEM, EntityRepository (+4 more)

### Community 46 - "3. Design & decision"
Cohesion: 0.12
Nodes (12): `ui/theme` (design-system tokens, DS1, DS2, DS4), ChipColors, MoneyColors, PftSpacing, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens, 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold` (+4 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.10
Nodes (24): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+16 more)

### Community 48 - "How to run and test"
Cohesion: 0.19
Nodes (3): How to run and test, RoomAppSettingsRepositoryTest, TestDatabase

### Community 49 - "HostedComposeRule"
Cohesion: 0.22
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 50 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.14
Nodes (12): AlreadyInitialised, AmountNotPositive, AmountTooLarge, Archived, BlankName, Deleted, InvalidSettingKey, LedgerError (+4 more)

### Community 52 - "AuditedWriterTest.kt"
Cohesion: 0.21
Nodes (6): AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note, NoteId

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (4): Probe, RoomAuditColumnsTest, AuditColumnsCheck, AuditedTable

### Community 54 - "LedgerEntity"
Cohesion: 0.06
Nodes (25): `data` (Room persistence, A4a), MetaColumns, MetaMapper, MetaMapperTest, EntityMeta, LedgerEntity, EntityId, AuditFieldsCheckTest (+17 more)

### Community 55 - "MainActivity"
Cohesion: 0.19
Nodes (14): `:app` — Android application, Configuration, Debug and release source sets, Dependencies, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose, Responsibilities and boundaries (+6 more)

### Community 56 - "CollapsingHeaderScaffold"
Cohesion: 0.35
Nodes (11): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderTitle(), LowerBand() (+3 more)

### Community 57 - "MoneyText"
Cohesion: 0.16
Nodes (9): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText(), RollingDigits() (+1 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.08
Nodes (32): Personal Finance Tracker PRD, Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Automatic capture from Email (E-0..E-5) (+24 more)

### Community 60 - "redact_text"
Cohesion: 0.27
Nodes (6): mask_run(), process(), redact_text(), amt(), hold(), review_flags()

### Community 61 - "PftTypography.kt"
Cohesion: 0.16
Nodes (10): displayFamily(), pftMaterialTypography(), pftType(), PftTypography, DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT (+2 more)

### Community 63 - "SampleThing.kt"
Cohesion: 0.17
Nodes (7): EntityMapper, SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 65 - "GroupedSection.kt"
Cohesion: 0.12
Nodes (14): `ui/common` (shared components, DS4 and DS5), GroupedRow, GroupedSection(), groupedSectionColor(), GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, Row (+6 more)

### Community 66 - "Public interface"
Cohesion: 0.09
Nodes (21): RoomTransactionRunner, RoomTestKit, Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How an entity step composes the blocks, Public interface (+13 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 69 - "PreMigrationBackup"
Cohesion: 0.13
Nodes (13): PreMigrationBackup, PreMigrationBackupFailed, factory(), Corruption handling, Persistence and security, Pre-migration backup (INT-5), SQLCipher, The key and the KEK (+5 more)

### Community 74 - "BottomActionBar.kt"
Cohesion: 0.20
Nodes (3): BottomActionBar(), BottomActionBarPreview(), BottomActionBarSamples()

### Community 78 - "ThemePrefs"
Cohesion: 0.15
Nodes (8): EmptyState(), ThemeMode, DARK, LIGHT, SYSTEM, ThemePrefs, EmptyStateTest, 3.7 Component inventory (UI-9)

### Community 79 - "AuditTriggersTest"
Cohesion: 0.08
Nodes (3): AuditTriggersTest, PftDatabaseTest, BundledFontTest

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.15
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 82 - "PftDatabase"
Cohesion: 0.07
Nodes (8): AuditDao, AuditEntryEntity, IdentityDao, DeviceEntity, UserProfileEntity, PftDatabase, AppSettingDao, AppSettingEntity

### Community 83 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 84 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 85 - "MigrationTestSupport"
Cohesion: 0.10
Nodes (4): AuditTriggers, MigrationTestSupport, MigrationTest, Audit triggers

### Community 88 - "DesignCatalogTest.kt"
Cohesion: 0.07
Nodes (3): MainActivityStartupTest, KeyUnavailableScreenTest, DesignCatalogTest

### Community 89 - "MoneyTextTest.kt"
Cohesion: 0.11
Nodes (5): layoutResult(), shownText(), assertShownInFull(), isShownInFull(), layout()

### Community 92 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 95 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.17
Nodes (10): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5. Flows, 8. Security considerations (+2 more)

### Community 96 - "MoneyToneTest"
Cohesion: 0.19
Nodes (3): MoneyToneTest, Paise, Slice D — Budgets (`feat/m1a-budgets`)

### Community 97 - "HeaderStyle"
Cohesion: 0.67
Nodes (3): HeaderStyle, Dashboard, Standard

### Community 99 - ".format"
Cohesion: 0.16
Nodes (11): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+3 more)

### Community 100 - "BrandColors"
Cohesion: 0.10
Nodes (14): BrandColors, resolveColorScheme(), Planned: premium look (DS6), 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 6.1 Slices (+6 more)

### Community 105 - "TestClock"
Cohesion: 0.09
Nodes (9): Data it owns, EpochMillisTest, NameBasedId, TestClock, Conventions, Data model, The audit trail, Version 1 (A4a) (+1 more)

### Community 109 - "MoneyTextSamples.kt"
Cohesion: 0.67
Nodes (4): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples()

### Community 111 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 112 - "Design system"
Cohesion: 0.15
Nodes (12): Adding a token, Build-time trust decisions, Colour, Design system, Motion (UI-10), One-hand layout (UI-7), Principles, Shapes (+4 more)

### Community 113 - "DatabaseFactory"
Cohesion: 0.21
Nodes (6): DatabaseFactory, DatabaseStartup, KeyUnavailable, Ready, PftApplication, open()

### Community 114 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 115 - "assertequals"
Cohesion: 0.32
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 116 - "modifier"
Cohesion: 0.18
Nodes (4): EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), DevContent()

### Community 125 - "3.9 Scope: app-wide"
Cohesion: 0.25
Nodes (8): 3.2 Surfaces: tonal or flat (DS6c), 3.4 Header glow (UI-13, DS6d), 3.5 Icon-only header actions (UI-14, DS6e; needs DS3), 3.6 Type pairing and optical size per role (UI-15, DS6g), 3.7 Bundled sans fonts (UI-16, UI-17; DS6f spike, then DS6h, DS6i), 3.8 Abstraction bar, 3.9 Scope: app-wide, 3. Design & decision

### Community 126 - "Reason"
Cohesion: 0.18
Nodes (8): DbKeyUnavailable, Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING

### Community 127 - "main"
Cohesion: 0.20
Nodes (10): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice H — App lock, diagnostics, release (`feat/m1a-hardening`), 6.2 Appearance slice: base `feat/m1a-appearance` off `main`, after A-final (Q4), 6. Implementation steps (TDD-ordered) (+2 more)

### Community 129 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

## Knowledge Gaps
- **89 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+84 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 456 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **52 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `runSuspending`, `ManifestHardeningTest`, `PreMigrationBackupTest`, `DbKeyManagerJvmTest`, `MoneyTextTest`, `ColorContrastTest`, `NavTransitionTest.kt`, `PressScaleTest.kt`, `CollapsingHeaderScaffoldTest`, `HeaderScreenshotTest`, `TokenSheetScreenshotTest.kt`, `PftTypographyTest`, `AuditEntry`, `PftShapesTest.kt`, `GroupedSectionTest.kt`, `RoomAuditColumnsTest`, `LedgerEntity`, `MainActivity`, `MoneyText`, `RoomEntityRepositoryTest.kt`, `SampleThing.kt`, `Public interface`, `IdentityRoomIntegrationTest`, `BottomActionBarTest.kt`, `ThemePrefs`, `AuditTriggersTest`, `expandedHeaderHeight`, `PftDatabase`, `MigrationTestSupport`, `RoomTransactionRunnerTest`, `PftThemeModeTest`, `DesignCatalogTest.kt`, `ContentScreenshotTest`, `RoomIdentityRepositoryTest.kt`, `DbKeyManagerTest.kt`, `StatusChipTest.kt`, `MoneyToneTest`, `.format`, `assertequals`, `DatabaseStartupTest.kt`, `DbKeyManagerWiringTest.kt`?**
  _High betweenness centrality (0.214) - this node is a cross-community bridge._
- **Are the 14 inferred relationships involving `Paise` (e.g. with ``ui/common` (shared components, DS4 and DS5)` and `Behaviours worth knowing`) actually correct?**
  _`Paise` has 14 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _89 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1422924901185771 - nodes in this community are weakly interconnected._
- **Why does `Paise` connect `MoneyToneTest` to `runSuspending`, `MoneyTextTest`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `CollapsingHeaderScaffoldTest`, `HeaderScreenshotTest`, `shouldbe`, `UserId`, `PftMotion.kt`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `LedgerEntity`, `MoneyText`, `RoomEntityRepositoryTest.kt`, `SampleThing.kt`, `GroupedSection.kt`, `Public interface`, `text`, `MoneyTextTest.kt`, `.format`, `PaiseParser`, `MoneyTextSamples.kt`, `MoneySize`?**
  _High betweenness centrality (0.139) - this node is a cross-community bridge._
- **Are the 15 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `TokenSheetPreview()`) actually correct?**
  _`ThemePrefs` has 15 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._