# Graph Report - personal-finance-tracker  (2026-10-30)

## Corpus Check
- 245 files · ~171,558 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 122 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 2894 nodes · 7839 edges · 133 communities (72 shown, 61 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 1077 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `a927386b`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- How to run and test
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- AuditedWriterTest.kt
- hooks
- test_import_symbols.py
- Slice A — Foundation (`feat/m1a-foundation`)
- DatabaseStartupTest.kt
- Paise
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Catalog.kt
- detekt.yml
- PftThemeModeTest
- PftSymbol
- CollapsingHeaderScaffoldTest
- Previews, the token sheet and screenshot goldens
- TokenSheetScreenshotTest.kt
- InMemoryEntityStore
- CollapsingHeaderScaffold.kt
- GroupedSection.kt
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- CatalogSection
- PftTypographyTest
- TestClock
- PftShapesTest.kt
- CategoryGlyph
- BottomActionBarTest.kt
- Project README
- UserProfile
- PftMotion.kt
- CategoryGlyphScreenshotTest
- PftTypography.kt
- CLAUDE.md Development Constitution
- How to run and test
- HostedComposeRule
- AuditEntryEntity
- PftTheme
- `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)
- RoomAuditColumnsTest
- EntityMeta
- MainActivity
- DbKeyManagerTest.kt
- MoneyText.kt
- Personal Finance Tracker PRD
- UserId
- redact_text
- AccentHueTest
- PftSpacingTest
- SampleThing.kt
- Pseudo
- ThemeResolutionTest
- IdentityService
- Collecting and Redacting Sample Messages Guide
- AuditEntryMapperTest
- AppSettingEntity
- _Text
- modifier
- import_symbols.py
- RoomEntityRepositoryTest.kt
- HeaderPolicy
- DesignCatalog.kt
- CategoryGlyphSamples.kt
- ThemePrefs
- PftDatabaseTest
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- HeaderSlot
- HeaderPhase
- MigrationTestSupport
- Auditable
- PreMigrationBackupTest
- ThemePaletteTest
- ContentScreenshotTest
- PreMigrationBackup
- AuditTriggersTest
- GroupedSectionTest.kt
- MoneyColors
- DbKeyManager
- PressScaleTest.kt
- MotionSection.kt
- 13. Deviations from the plan, by slice
- DatabaseFactory
- MainActivityStartupTest.kt
- Migrations.kt
- RoomEntityRepository
- CategoryPaletteTest
- PftDatabase
- PaletteSamples.kt
- parse_svg
- 7. Test plan
- HeaderStyle
- MotionSection.kt
- ImportRunTest
- BottomActionBar.kt
- AndroidKeystoreKek.kt
- DbKeyUnavailable
- Layered Modular Architecture (core vs app)
- KeyUnavailableScreenTest.kt
- AuditFieldsCheck.kt
- RoomEntityRepository
- Reason
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- PaletteScreenshotTest.kt
- onnodewithtag
- HeaderIconButtonTest.kt
- Security, privacy and integrity requirements (SEC/INT)
- ThemePalette
- DevContent
- HeaderIconButton
- MoneyTextSamples.kt
- Appearance: colour palettes and dynamic colour
- ManifestHardeningTest

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 138 edges
2. `PftSymbol` - 120 edges
3. `ThemePrefs` - 98 edges
4. `Paise` - 90 edges
5. `CollapsingHeaderScaffoldTest` - 77 edges
6. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 72 edges
7. `How to run and test` - 65 edges
8. `UserId` - 57 edges
9. `runSuspending()` - 54 edges
10. `HostedComposeRule` - 54 edges

## Surprising Connections (you probably didn't know these)
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `3.4 Header glow (UI-13, DS6d)` --references--> `HeaderPolicy`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/CollapsingHeaderScaffold.kt
- `UI layer` --references--> `PftTheme()`  [INFERRED]
  docs/architecture/overview.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftTheme.kt
- `1. Requirement` --references--> `PftTheme()`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftTheme.kt
- `5.4 Custom font import (AP2)` --references--> `PftTheme()`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftTheme.kt

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

## Communities (133 total, 61 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "How to run and test"
Cohesion: 0.05
Nodes (9): How to run and test, FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAppSettingsRepositoryTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomIdentityRepositoryTest, RoomTransactionRunnerTest (+1 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "AuditedWriterTest.kt"
Cohesion: 0.14
Nodes (7): AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.10
Nodes (15): RoomAuditLog, RoomTransactionRunner, AlreadyInitialised, AmountNotPositive, AmountTooLarge, Archived, BlankName, Deleted (+7 more)

### Community 10 - "Paise"
Cohesion: 0.05
Nodes (18): MoneySize, Hero, Large, layoutResult(), MoneyTextTest, shownText(), Spec, MoneyToneTest (+10 more)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.20
Nodes (7): Case, ColorContrastTest, Theme, DARK, LIGHT, Parsing grammar, Public interface

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.08
Nodes (54): `ui/common` (shared components, DS3, DS4 and DS5), BottomActionBar(), CollapsingHeaderScaffold(), GroupedSection(), GroupedSectionDefaults, GroupedSectionSamples(), moneySign(), MoneyText() (+46 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (94): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+86 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.08
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.15
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 33 - "GroupedSection.kt"
Cohesion: 0.09
Nodes (8): fixedSp(), GroupedRow, groupedSectionColor(), GroupedSectionScope, GroupedSectionTags, GlyphSize, Chip, Hero

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "CatalogSection"
Cohesion: 0.15
Nodes (13): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, GLYPHS, MONEY (+5 more)

### Community 38 - "TestClock"
Cohesion: 0.06
Nodes (12): EpochMillisTest, NameBasedId, clockAt(), Uuid7Test, TestClock, How to run and test, PaiseFormatTest, PaiseParserTest (+4 more)

### Community 40 - "CategoryGlyph"
Cohesion: 0.08
Nodes (9): CategoryGlyph(), Emoji, GlyphSource, isEmojiValue(), isSymbolKey(), Letter, Symbol, CategoryGlyphTest (+1 more)

### Community 42 - "Project README"
Cohesion: 0.14
Nodes (13): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer (+5 more)

### Community 43 - "UserProfile"
Cohesion: 0.15
Nodes (5): RoomIdentityRepository, Device, IdentityRepository, UserProfile, InMemoryIdentityRepository

### Community 46 - "PftTypography.kt"
Cohesion: 0.16
Nodes (9): displayFamily(), pftMaterialTypography(), pftType(), PftTypography, DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT (+1 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.10
Nodes (24): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+16 more)

### Community 48 - "How to run and test"
Cohesion: 0.11
Nodes (10): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, EntityMetaTest, IdsTest, NameBasedIdTest (+2 more)

### Community 49 - "HostedComposeRule"
Cohesion: 0.22
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 51 - "PftTheme"
Cohesion: 0.15
Nodes (3): PftTheme(), StatusChipTest, DesignCatalogTest

### Community 52 - "`ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)"
Cohesion: 0.17
Nodes (12): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, Colour, Motion (UI-10) (+4 more)

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.07
Nodes (26): `data` (Room persistence, A4a), MetaColumns, MetaMapper, MetaMapperTest, AuditedTable, AuditedTables, EntityMeta, LedgerEntity (+18 more)

### Community 55 - "MainActivity"
Cohesion: 0.20
Nodes (13): `:app` — Android application, Configuration, Debug and release source sets, Dependencies, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose, Responsibilities and boundaries (+5 more)

### Community 57 - "MoneyText.kt"
Cohesion: 0.17
Nodes (8): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), RollingDigits(), toneColor()

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.08
Nodes (32): Personal Finance Tracker PRD, Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Automatic capture from Email (E-0..E-5) (+24 more)

### Community 59 - "UserId"
Cohesion: 0.07
Nodes (27): AuditDaoTest, TestDatabase, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE (+19 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 61 - "AccentHueTest"
Cohesion: 0.24
Nodes (5): AccentHueTest, hueDistance(), labHue(), roles(), surfaceRoles()

### Community 63 - "SampleThing.kt"
Cohesion: 0.21
Nodes (6): SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 65 - "ThemeResolutionTest"
Cohesion: 0.08
Nodes (11): resolveColorScheme(), paletteFor(), ThemeMode, DARK, LIGHT, SYSTEM, ThemePrefs, ThemePrefsTest (+3 more)

### Community 66 - "IdentityService"
Cohesion: 0.08
Nodes (24): RoomTestKit, Behaviours worth knowing, Configuration, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, AuditDiff, AuditedWriter (+16 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 68 - "AuditEntryMapperTest"
Cohesion: 0.20
Nodes (4): AuditEntryMapper, AuditEntryMapperTest, The audit trail, Version 1 (A4a)

### Community 72 - "modifier"
Cohesion: 0.18
Nodes (4): EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), DevContent()

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 76 - "DesignCatalog.kt"
Cohesion: 0.11
Nodes (6): ChoiceRow(), DesignCatalog(), SectionContent(), GroupedSectionPreview(), TokenSheet(), TokenSheetPreview()

### Community 77 - "CategoryGlyphSamples.kt"
Cohesion: 0.31
Nodes (6): CategoryGlyphPreview(), CategoryGlyphSamples(), SampleRow(), Row, StatusChipPreview(), StatusChipSamples()

### Community 79 - "PftDatabaseTest"
Cohesion: 0.05
Nodes (7): PftDatabaseTest, BundledFontTest, BundledSymbolsLicenceTest, PftSymbolTest, The curated list, Third-party assets, download()

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.09
Nodes (21): PftSpacing, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens (+13 more)

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
Nodes (4): AuditTriggers, MigrationTestSupport, MigrationTest, Audit triggers

### Community 86 - "Auditable"
Cohesion: 0.12
Nodes (8): EpochMillis, RoomAppSettingsRepository, Auditable, AppSetting, AppSettingsRepository, AuditDiffTest, Thing, InMemoryAppSettingsRepository

### Community 94 - "MoneyColors"
Cohesion: 0.19
Nodes (5): ChipColors, CategoryColors, CategoryPalette, pair(), MoneyColors

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 98 - "13. Deviations from the plan, by slice"
Cohesion: 0.38
Nodes (7): CollapsingHeader(), HeaderChildren(), HeaderContent, headerSizes(), HeaderTitle(), LowerBand(), periodSwipe()

### Community 99 - "DatabaseFactory"
Cohesion: 0.18
Nodes (7): Data it owns, DatabaseFactory, DatabaseStartup, KeyUnavailable, Ready, PftApplication, open()

### Community 105 - "PaletteSamples.kt"
Cohesion: 0.29
Nodes (4): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 108 - "HeaderStyle"
Cohesion: 0.67
Nodes (3): HeaderStyle, Dashboard, Standard

### Community 113 - "DbKeyUnavailable"
Cohesion: 0.14
Nodes (15): `security` and the encrypted open (A4b), PreMigrationBackupFailed, DbKeyUnavailable, KeyUnavailableScreen(), Corruption handling, Fail closed, Persistence and security, Pre-migration backup (INT-5) (+7 more)

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 117 - "AuditFieldsCheck.kt"
Cohesion: 0.13
Nodes (6): Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), InMemoryAuditLog, InMemoryLedgerStore, AuditFieldsCheck, haveCompleteAuditFields(), shouldHaveCompleteAuditFields()

### Community 120 - "Reason"
Cohesion: 0.25
Nodes (7): Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.13
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 126 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 127 - "ThemePalette"
Cohesion: 0.12
Nodes (14): colors(), ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, ChipColorsTest, 10. Risks & mitigations (+6 more)

### Community 130 - "HeaderIconButton"
Cohesion: 0.13
Nodes (18): HeaderPeriod, PeriodStepper(), HeaderIconButton(), ThemeLiteralScanTest, Violation, Icons and emoji (DS3, UI-6), Planned: premium look (DS6), 11. Rollout & verification (+10 more)

### Community 132 - "MoneyTextSamples.kt"
Cohesion: 0.67
Nodes (4): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples()

### Community 133 - "Appearance: colour palettes and dynamic colour"
Cohesion: 0.25
Nodes (5): Appearance: colour palettes and dynamic colour, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you

### Community 140 - "ManifestHardeningTest"
Cohesion: 0.05
Nodes (21): ManifestHardeningTest, WindowThemeTest, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 3.1 Modules (Gradle) (+13 more)

## Knowledge Gaps
- **190 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+185 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 578 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **61 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `DatabaseStartupTest.kt`, `Paise`, `ManifestHardeningTest`, `ColorContrastTest`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `PftThemeModeTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `Previews, the token sheet and screenshot goldens`, `TokenSheetScreenshotTest.kt`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `PftShapesTest.kt`, `CategoryGlyph`, `BottomActionBarTest.kt`, `CategoryGlyphScreenshotTest`, `PftTheme`, `RoomAuditColumnsTest`, `EntityMeta`, `MainActivity`, `DbKeyManagerTest.kt`, `UserId`, `AccentHueTest`, `PftSpacingTest`, `SampleThing.kt`, `ThemeResolutionTest`, `IdentityService`, `AuditEntryMapperTest`, `RoomEntityRepositoryTest.kt`, `ThemePrefs`, `PftDatabaseTest`, `expandedHeaderHeight`, `MigrationTestSupport`, `PreMigrationBackupTest`, `ThemePaletteTest`, `ContentScreenshotTest`, `AuditTriggersTest`, `GroupedSectionTest.kt`, `PressScaleTest.kt`, `MainActivityStartupTest.kt`, `CategoryPaletteTest`, `PftDatabase`, `7. Test plan`, `KeyUnavailableScreenTest.kt`, `PaletteScreenshotTest.kt`, `ThemePalette`?**
  _High betweenness centrality (0.262) - this node is a cross-community bridge._
- **Are the 41 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 41 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _190 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Collecting and Redacting Sample Messages Guide`, `Automatic capture from SMS (S-1..S-13)`, `Paise`, ``build-logic` — convention plugins`, `CLAUDE.md Development Constitution`, `How to run and test`, `Layered Modular Architecture (core vs app)`, `Personal Finance Tracker PRD`?**
  _High betweenness centrality (0.130) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `PftSymbol` (e.g. with `How to run and test` and ``ui/common` (shared components, DS3, DS4 and DS5)`) actually correct?**
  _`PftSymbol` has 13 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._