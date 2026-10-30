# Graph Report - pft-wt-a4b  (2026-10-30)

## Corpus Check
- 229 files · ~159,788 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 122 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 2722 nodes · 7198 edges · 119 communities (68 shown, 51 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 933 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `cb299fa3`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- How to run and test
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- ManifestHardeningTest
- hooks
- test_import_symbols.py
- AuditedWriterTest.kt
- DbKeyManagerJvmTest
- Paise
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
- How to run and test
- CollapsingHeaderScaffold.kt
- EncryptedDatabaseTest.kt
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- CatalogSection
- PftTypographyTest
- AuditEntry
- DatabaseFactoryTest.kt
- AppSetting
- TestClock
- Project README
- UserProfile
- PftMotion.kt
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- LedgerEntity
- CLAUDE.md Development Constitution
- AuditEntryMapperTest
- HostedComposeRule
- KeyUnavailableScreenTest.kt
- DesignCatalogTest.kt
- CategoryGlyph
- RoomAuditColumnsTest
- LedgerEntity
- MainActivity
- DbKeyManagerTest.kt
- MoneyText
- Personal Finance Tracker PRD
- AuditEntryEntity
- redact_text
- PftTypography.kt
- PftTheme.kt
- SampleThing.kt
- Pseudo
- GroupedSection.kt
- Public interface
- Collecting and Redacting Sample Messages Guide
- BundledFontTest
- DbKeyUnavailable
- _Text
- BottomActionBarTest.kt
- test_redact.py
- BottomActionBar.kt
- HeaderPolicy
- DesignCatalog.kt
- MoneyTextSamples.kt
- ThemePrefs
- AuditTriggersTest
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- HeaderSlot
- HeaderPhase
- MigrationTestSupport
- Loans and EMIs (LN-1..LN-5)
- PreMigrationBackupTest
- DesignCatalogTest.kt
- ContentScreenshotTest
- PreMigrationBackup
- MigrationTest
- DbKeyManagerTest.kt
- StatusChipTest.kt
- DbKeyManager
- PressScaleTest.kt
- CategoryGlyph.kt
- column
- DatabaseStartupTest.kt
- RoomIdentityRepositoryTest.kt
- Migrations.kt
- DbKeyManagerWiringTest.kt
- asserttrue
- BundledSymbolsLicenceTest
- .of
- GroupedSectionScope
- CategoryPalette.kt
- InMemoryLedgerStore
- PftTheme.kt
- PaiseFormatTest.kt
- Security, privacy and integrity requirements (SEC/INT)
- DevContent
- DatabaseFactoryTest.kt
- assertequals
- CollapsingHeaderScaffold
- AndroidKeystoreKek.kt
- EncryptedDatabaseTest.kt

## God Nodes (most connected - your core abstractions)
1. `PftSymbol` - 113 edges
2. `Paise` - 90 edges
3. `ThemePrefs` - 80 edges
4. `CollapsingHeaderScaffoldTest` - 70 edges
5. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 70 edges
6. `How to run and test` - 59 edges
7. `UserId` - 57 edges
8. `runSuspending()` - 54 edges
9. `3.4 Reuse by layer (owner decision, 2026-10-28)` - 51 edges
10. `MoneyText()` - 50 edges

## Surprising Connections (you probably didn't know these)
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `3.4 Header glow (UI-13, DS6d)` --references--> `HeaderPolicy`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/CollapsingHeaderScaffold.kt
- ``StatusChip`` --references--> `StatusChip()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/StatusChip.kt
- `Planned: premium look (DS6)` --references--> `BrandColors`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/BrandColors.kt
- `3.2 Theming architecture (no new abstraction)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt

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

## Communities (119 total, 51 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "How to run and test"
Cohesion: 0.05
Nodes (9): How to run and test, AuditDaoTest, FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAppSettingsRepositoryTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomTransactionRunnerTest (+1 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "AuditedWriterTest.kt"
Cohesion: 0.12
Nodes (8): AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository, InMemoryLedgerStore

### Community 9 - "DbKeyManagerJvmTest"
Cohesion: 0.17
Nodes (11): Behaviours worth knowing, AuditDiff, AuditedWriter, AuditSource, MANUAL, RECURRING, SYSTEM, EntityRepository (+3 more)

### Community 10 - "Paise"
Cohesion: 0.05
Nodes (18): MoneySize, Hero, Large, layoutResult(), MoneyTextTest, shownText(), Spec, MoneyToneTest (+10 more)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.23
Nodes (6): ColorContrastTest, Theme, DARK, LIGHT, Parsing grammar, Public interface

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.18
Nodes (20): Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense, Income (+12 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 27 - "PressScaleTest.kt"
Cohesion: 0.02
Nodes (94): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+86 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.09
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "HeaderScreenshotTest"
Cohesion: 0.14
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 31 - "How to run and test"
Cohesion: 0.06
Nodes (24): How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement (+16 more)

### Community 32 - "CollapsingHeaderScaffold.kt"
Cohesion: 0.09
Nodes (12): CollapsingHeader(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle, Dashboard, Standard (+4 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.25
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "CatalogSection"
Cohesion: 0.18
Nodes (11): CatalogSection, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, GLYPHS, MONEY, MOTION (+3 more)

### Community 37 - "PftTypographyTest"
Cohesion: 0.09
Nodes (4): displayFamily(), pftMaterialTypography(), PftTypographyTest, Resolved

### Community 38 - "AuditEntry"
Cohesion: 0.07
Nodes (31): `data` (Room persistence, A4a), MetaColumns, MetaMapper, RoomIdentityRepository, MetaMapperTest, AuditedTable, AuditedTables, TestDatabase (+23 more)

### Community 40 - "AppSetting"
Cohesion: 0.14
Nodes (7): Emoji, GlyphSource, isEmojiValue(), isSymbolKey(), Letter, Symbol, GlyphSourceTest

### Community 41 - "TestClock"
Cohesion: 0.09
Nodes (11): Data it owns, EpochMillis, EpochMillisTest, NameBasedId, clockAt(), Uuid7Test, TestClock, Conventions (+3 more)

### Community 42 - "Project README"
Cohesion: 0.14
Nodes (13): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer (+5 more)

### Community 43 - "UserProfile"
Cohesion: 0.18
Nodes (3): IdentityRepository, UserProfile, InMemoryIdentityRepository

### Community 45 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.22
Nodes (6): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), Row, CategoryGlyphScreenshotTest

### Community 46 - "LedgerEntity"
Cohesion: 0.13
Nodes (15): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), MoneyColors, PftShapes, PftSpacing, pftType(), PftTypography, Theming, 3.1 Visual direction: "Cashew, but premium" (+7 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.10
Nodes (24): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+16 more)

### Community 48 - "AuditEntryMapperTest"
Cohesion: 0.15
Nodes (7): RoomAppSettingsRepository, Auditable, AppSetting, AppSettingsRepository, Thing, InMemoryAppSettingsRepository, The core/app boundary

### Community 49 - "HostedComposeRule"
Cohesion: 0.22
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 50 - "KeyUnavailableScreenTest.kt"
Cohesion: 0.05
Nodes (27): RoomAuditLog, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, AuditLog, SettingKeys (+19 more)

### Community 51 - "DesignCatalogTest.kt"
Cohesion: 0.06
Nodes (3): MainActivityStartupTest, KeyUnavailableScreenTest, DesignCatalogTest

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "LedgerEntity"
Cohesion: 0.07
Nodes (20): EntityMeta, LedgerEntity, AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind, LARGE (+12 more)

### Community 55 - "MainActivity"
Cohesion: 0.08
Nodes (34): `:app` — Android application, Configuration, Debug and release source sets, Dependencies, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose, Responsibilities and boundaries (+26 more)

### Community 57 - "MoneyText"
Cohesion: 0.11
Nodes (16): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), moneySign(), MoneyText() (+8 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.08
Nodes (32): Personal Finance Tracker PRD, Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Automatic capture from Email (E-0..E-5) (+24 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 61 - "PftTypography.kt"
Cohesion: 0.18
Nodes (3): PftSymbolTest, The curated list, download()

### Community 63 - "SampleThing.kt"
Cohesion: 0.21
Nodes (6): SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 65 - "GroupedSection.kt"
Cohesion: 0.20
Nodes (8): GroupedRow, GroupedSection(), groupedSectionColor(), GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, GroupedSectionSamples(), `GroupedSection`

### Community 66 - "Public interface"
Cohesion: 0.09
Nodes (18): RoomTransactionRunner, RoomTestKit, Configuration, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, AuditRecorder, Identity (+10 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 69 - "DbKeyUnavailable"
Cohesion: 0.11
Nodes (14): DatabaseFactory, DatabaseStartup, KeyUnavailable, Ready, PftApplication, DbKeyUnavailable, Reason, KEK_MISSING (+6 more)

### Community 73 - "test_redact.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 76 - "DesignCatalog.kt"
Cohesion: 0.10
Nodes (9): ChoiceRow(), DesignCatalog(), SectionContent(), GroupedSectionPreview(), StatusChipPreview(), StatusChipSamples(), KeyUnavailableScreen(), TokenSheet() (+1 more)

### Community 77 - "MoneyTextSamples.kt"
Cohesion: 0.24
Nodes (4): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples()

### Community 78 - "ThemePrefs"
Cohesion: 0.12
Nodes (10): DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, ThemeMode, DARK, LIGHT, SYSTEM (+2 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.15
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 82 - "UserProfileEntity"
Cohesion: 0.22
Nodes (3): IdentityDao, DeviceEntity, UserProfileEntity

### Community 83 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 84 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 85 - "MigrationTestSupport"
Cohesion: 0.10
Nodes (5): AuditTriggers, MigrationTestSupport, MigrationTest, The audit trail, Audit triggers

### Community 86 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.16
Nodes (3): PftDatabase, AppSettingDao, AppSettingEntity

### Community 91 - "PreMigrationBackup"
Cohesion: 0.13
Nodes (13): PreMigrationBackup, PreMigrationBackupFailed, Corruption handling, Fail closed, Persistence and security, Pre-migration backup (INT-5), SQLCipher, The key and the KEK (+5 more)

### Community 94 - "StatusChipTest.kt"
Cohesion: 0.15
Nodes (5): ChipColors, StatusChip(), StatusChipTest, roles(), surfaceRoles()

### Community 95 - "DbKeyManager"
Cohesion: 0.20
Nodes (4): DbKeyManager, KekSource, Decoded, WrappedKeyFormat

### Community 97 - "CategoryGlyph.kt"
Cohesion: 0.13
Nodes (4): fixedSp(), GlyphSize, Chip, Hero

### Community 98 - "column"
Cohesion: 0.32
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 106 - "GroupedSectionScope"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 107 - "CategoryPalette.kt"
Cohesion: 0.52
Nodes (3): CategoryColors, CategoryPalette, pair()

### Community 109 - "PftTheme.kt"
Cohesion: 0.08
Nodes (7): MotionSection(), SpringTrack(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), resolveColorScheme(), DevContent()

### Community 111 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 114 - "DatabaseFactoryTest.kt"
Cohesion: 0.12
Nodes (9): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`(), `dynamic dark resolves to the system dynamic dark scheme`() (+1 more)

### Community 115 - "assertequals"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 118 - "CollapsingHeaderScaffold"
Cohesion: 0.12
Nodes (27): `ui/common` (shared components, DS3, DS4 and DS5), BottomActionBar(), CollapsingHeaderScaffold(), PeriodStepper(), EmptyState(), Build-time trust decisions, Colour, Content primitives (DS5) (+19 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.09
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

## Knowledge Gaps
- **184 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+179 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 566 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **51 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `ManifestHardeningTest`, `Paise`, `ColorContrastTest`, `NavTransitionTest.kt`, `PressScaleTest.kt`, `CollapsingHeaderScaffoldTest`, `HeaderScreenshotTest`, `TokenSheetScreenshotTest.kt`, `EncryptedDatabaseTest.kt`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `AuditEntry`, `DatabaseFactoryTest.kt`, `AppSetting`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `DesignCatalogTest.kt`, `CategoryGlyph`, `RoomAuditColumnsTest`, `MainActivity`, `DbKeyManagerTest.kt`, `MoneyText`, `AuditEntryEntity`, `PftTypography.kt`, `PftTheme.kt`, `SampleThing.kt`, `Public interface`, `BottomActionBarTest.kt`, `ThemePrefs`, `AuditTriggersTest`, `expandedHeaderHeight`, `MigrationTestSupport`, `Loans and EMIs (LN-1..LN-5)`, `PreMigrationBackupTest`, `DesignCatalogTest.kt`, `ContentScreenshotTest`, `DbKeyManagerTest.kt`, `StatusChipTest.kt`, `PressScaleTest.kt`, `column`, `DatabaseStartupTest.kt`, `RoomIdentityRepositoryTest.kt`, `DbKeyManagerWiringTest.kt`, `asserttrue`, `BundledSymbolsLicenceTest`, `.of`?**
  _High betweenness centrality (0.291) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `PftSymbol` (e.g. with `How to run and test` and ``ui/common` (shared components, DS3, DS4 and DS5)`) actually correct?**
  _`PftSymbol` has 13 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _184 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Collecting and Redacting Sample Messages Guide`, `Automatic capture from SMS (S-1..S-13)`, `Paise`, ``build-logic` — convention plugins`, `CLAUDE.md Development Constitution`, `KeyUnavailableScreenTest.kt`, `assertequals`, `Personal Finance Tracker PRD`?**
  _High betweenness centrality (0.110) - this node is a cross-community bridge._
- **Are the 14 inferred relationships involving `Paise` (e.g. with ``ui/common` (shared components, DS3, DS4 and DS5)` and `Behaviours worth knowing`) actually correct?**
  _`Paise` has 14 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._