# Graph Report - personal-finance-tracker  (2026-11-02)

## Corpus Check
- 288 files · ~216,176 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3517 nodes · 10116 edges · 161 communities (85 shown, 76 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 1613 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `a97c44e0`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- AuditedWriterTest.kt
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- Slice A — Foundation (`feat/m1a-foundation`)
- hooks
- test_import_symbols.py
- DesignCatalog.kt
- ThemePrefs
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- MoneySemantic
- Catalog.kt
- detekt.yml
- NavTransitionTest.kt
- PftSymbol
- CollapsingHeaderScaffoldTest
- SampleId
- AppServices
- LedgerError
- CollapsingHeaderScaffold.kt
- Previews, the token sheet and screenshot goldens
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- HeaderGlowScaffoldTest.kt
- PftTypographyTest
- PftTypography.kt
- PftShapesTest.kt
- CategoryGlyph
- LaunchViewModelTest.kt
- Project README
- 6.1 Slices
- PftMotion.kt
- HeaderTypePairingTest
- `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)
- CLAUDE.md Development Constitution
- HeaderIconButtonTest.kt
- FieldState
- PftThemeModeTest
- AuditEntryEntity
- RoomAuditColumnsTest
- EntityMeta
- ContentScreenshotTest
- DbKeyManagerTest.kt
- MoneyText
- Personal Finance Tracker PRD
- UserId
- redact_text
- 7. Test plan
- MoneyTextTest.kt
- SampleThing.kt
- Pseudo
- PftDatabase.kt
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- Collecting and Redacting Sample Messages Guide
- TokenSheetScreenshotTest.kt
- MainActivityOpeningTest.kt
- _Text
- CollapsingHeaderScaffold
- import_symbols.py
- runSuspending
- HeaderPolicy
- CatalogSection
- 7. Test plan
- HostedComposeRule
- PftDatabaseTest
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- HeaderSlot
- HeaderPhase
- MigrationTestSupport
- StatusChip
- PreMigrationBackupTest
- TabularFiguresTest
- PressScaleTest.kt
- NameFieldTest
- PreMigrationBackup
- AuditFieldsCheck.kt
- GroupedSectionTest.kt
- PftTheme
- DbKeyManager
- PftSymbolTest
- BottomActionBar.kt
- Composable
- DatabaseStartupTest.kt
- Paise
- Migrations.kt
- DbKeyUnavailable
- BottomActionBarTest
- PaletteSamples.kt
- SafeLogTest
- parse_svg
- SectionContent
- BundledFontTest
- applicationprovider
- ImportRunTest
- SafeLog
- AndroidKeystoreKek.kt
- DbKeyManagerWiringTest.kt
- HostedComposeRule
- Layered Modular Architecture (core vs app)
- ThemeLiteralScanTest
- AppContainer
- ThemePaletteTest
- .format
- modifier
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- AuditEntry
- AppServicesTest
- Icons and emoji (DS3, UI-6)
- ThemePrefsTest
- Density
- Plan: M1a — Core ledger (manual-entry household tracker)
- CategoryPaletteTest
- LogEventShapeTest
- PaiseParser
- ThemePalette
- EmptyState
- How to run and test
- FormScreenshotTest.kt
- ManifestHardeningTest
- CollapsingHeaderScaffoldTest.kt
- MigrationTest
- AppSetting
- PftSurfacesTest
- PftSpacingTest
- EditFormScaffoldTest
- TestClock
- FontBudgetTest
- RoomEntityRepository
- PaletteSwatchesTest.kt
- BottomActionBarSurfaceTest.kt
- 6. Implementation steps (TDD-ordered)
- DatabaseFactory
- BundledSymbolsLicenceTest
- BaseDao
- AuditTriggers
- MoneyTextSamples
- NameField
- TokenSheet
- Security, privacy and integrity requirements (SEC/INT)
- MoneySize
- HeaderStyle

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 175 edges
2. `ThemePrefs` - 145 edges
3. `PftSymbol` - 124 edges
4. `Paise` - 92 edges
5. `How to run and test` - 88 edges
6. `CollapsingHeaderScaffoldTest` - 82 edges
7. `HostedComposeRule` - 77 edges
8. `runSuspending()` - 74 edges
9. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 71 edges
10. `UserId` - 57 edges

## Surprising Connections (you probably didn't know these)
- `3.5 Fonts: bundling, licensing, custom import` --references--> `SafeLog`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/log/SafeLog.kt
- `13. Deviations from the plan, by slice` --references--> `asHeroTop()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderGlow.kt
- `Adding an icon` --references--> `PftSymbol`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftSymbol.kt
- `UI layer` --references--> `PftTheme()`  [INFERRED]
  docs/architecture/overview.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftTheme.kt
- `1. Requirement` --references--> `PftTheme()`  [INFERRED]
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

## Communities (161 total, 76 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "AuditedWriterTest.kt"
Cohesion: 0.12
Nodes (8): AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository, InMemoryLedgerStore

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.04
Nodes (20): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, AuditDiffTest, Thing, EntityMetaTest (+12 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "DesignCatalog.kt"
Cohesion: 0.07
Nodes (9): MotionSection(), SpringTrack(), fixedSp(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), GlyphSize, Chip (+1 more)

### Community 9 - "ThemePrefs"
Cohesion: 0.11
Nodes (5): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.09
Nodes (16): AccentHueTest, Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone() (+8 more)

### Community 16 - "MoneySemantic"
Cohesion: 0.17
Nodes (22): moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense (+14 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.09
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "SampleId"
Cohesion: 0.25
Nodes (3): RoomEntityRepositoryTest, SampleId, SampleThingRepository

### Community 30 - "AppServices"
Cohesion: 0.09
Nodes (21): `di` and startup (A5b), AppServices, AppStart, KeyUnavailable, Ready, IdentityCorrupt, KeyUnavailable, LaunchState (+13 more)

### Community 31 - "LedgerError"
Cohesion: 0.13
Nodes (18): `ui/common` (shared components, DS3, DS4, DS5 and A5d), FieldError, Required, TooLong, toFieldError(), AmountNotPositive, AmountTooLarge, Archived (+10 more)

### Community 33 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.18
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.23
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 38 - "PftTypography.kt"
Cohesion: 0.17
Nodes (11): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansWeights(), DisplayFont, BUNDLED_SERIF (+3 more)

### Community 40 - "CategoryGlyph"
Cohesion: 0.06
Nodes (14): CategoryGlyph(), CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), Emoji, GlyphSource, isEmojiValue() (+6 more)

### Community 42 - "Project README"
Cohesion: 0.14
Nodes (13): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer (+5 more)

### Community 43 - "6.1 Slices"
Cohesion: 0.11
Nodes (24): PftSurfaces, sansFamily(), paletteFor(), SansFont, FIGTREE, PLUS_JAKARTA_SANS, SYSTEM, ThemeMode (+16 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.11
Nodes (3): PftMotion, pressScale(), 11. Rollout & verification

### Community 46 - "`ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)"
Cohesion: 0.10
Nodes (16): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), ChipColors, AuberginePalette, GraphitePalette, PaletteColors, MidnightPalette, MoneyColors, PftSpacing (+8 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.10
Nodes (24): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+16 more)

### Community 50 - "FieldState"
Cohesion: 0.26
Nodes (3): FieldCheck, FieldState, FieldStateTest

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.06
Nodes (23): `data` (Room persistence, A4a), MetaColumns, MetaMapper, MetaMapperTest, EntityMeta, LedgerEntity, EntityId, AuditFieldsCheckTest (+15 more)

### Community 57 - "MoneyText"
Cohesion: 0.17
Nodes (9): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText(), RollingDigits() (+1 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.08
Nodes (32): Personal Finance Tracker PRD, Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Automatic capture from Email (E-0..E-5) (+24 more)

### Community 59 - "UserId"
Cohesion: 0.09
Nodes (10): RoomIdentityRepository, RoomIdentityRepositoryTest, AuditedTable, AuditedTables, Auditable, Device, IdentityRepository, UserProfile (+2 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 61 - "7. Test plan"
Cohesion: 0.30
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 62 - "MoneyTextTest.kt"
Cohesion: 0.17
Nodes (3): layoutResult(), shownText(), StatusChipTest

### Community 63 - "SampleThing.kt"
Cohesion: 0.22
Nodes (5): SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper

### Community 66 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.07
Nodes (32): RoomTransactionRunner, Behaviours worth knowing, Configuration, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, AuditDiff, AuditedWriter (+24 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 69 - "MainActivityOpeningTest.kt"
Cohesion: 0.13
Nodes (4): MainActivityOpeningTest, SlowOpeningApplication, MainActivityStartupTest, 10. Risks & mitigations

### Community 72 - "CollapsingHeaderScaffold"
Cohesion: 0.10
Nodes (29): Manifest security posture (SEC-2, SEC-3, SEC-6), Responsibilities and boundaries, MainActivity, BottomActionBar(), CollapsingHeaderScaffold(), EditFormScaffold(), GroupedSection(), SyncWindowBackground() (+21 more)

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 74 - "runSuspending"
Cohesion: 0.06
Nodes (8): FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomTransactionRunnerTest, AppContainerTest, runSuspending(), 3.3 Dependencies (one pins-only PR, A5c)

### Community 76 - "CatalogSection"
Cohesion: 0.14
Nodes (14): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+6 more)

### Community 77 - "7. Test plan"
Cohesion: 0.24
Nodes (6): LoggingScanTest, SourceScan, Violation, AlreadyInitialised, 7. Test plan, Step 3 — `SafeLog` writes the line, and nothing else can log (A5a)

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

### Community 86 - "StatusChip"
Cohesion: 0.11
Nodes (12): GroupedSectionDefaults, GroupedSectionPreview(), GroupedSectionSamples(), StatusChip(), Adding a token, Build-time trust decisions, Content primitives (DS5), Design system (+4 more)

### Community 91 - "PreMigrationBackup"
Cohesion: 0.18
Nodes (11): PreMigrationBackupFailed, Corruption handling, Persistence and security, Pre-migration backup (INT-5), SQLCipher, The key and the KEK, The key array and SQLCipher (a trap), The pieces (+3 more)

### Community 92 - "AuditFieldsCheck.kt"
Cohesion: 0.20
Nodes (3): AuditFieldsCheck, haveCompleteAuditFields(), shouldHaveCompleteAuditFields()

### Community 93 - "GroupedSectionTest.kt"
Cohesion: 0.15
Nodes (3): GroupedRow, GroupedSectionScope, GroupedSectionTest

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 98 - "Composable"
Cohesion: 0.27
Nodes (11): CollapsingHeader(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderTitle(), LowerBand(), PeriodStepper() (+3 more)

### Community 99 - "DatabaseStartupTest.kt"
Cohesion: 0.19
Nodes (4): DatabaseStartup, KeyUnavailable, Ready, DatabaseStartupTest

### Community 102 - "DbKeyUnavailable"
Cohesion: 0.15
Nodes (8): DbKeyUnavailable, Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING

### Community 104 - "PaletteSamples.kt"
Cohesion: 0.18
Nodes (6): GroupedSectionColors, GroupedSectionTags, PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 107 - "SectionContent"
Cohesion: 0.26
Nodes (6): AccentCheckSection(), SectionContent(), EditFormPreview(), EditFormSample(), StatusChipPreview(), StatusChipSamples()

### Community 111 - "SafeLog"
Cohesion: 0.08
Nodes (29): Purpose, `security` and the encrypted open (A4b), DbKeyUnavailable, DbOpened, IdentityCorrupt, LogEvent, LogValue, render() (+21 more)

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 116 - "ThemeLiteralScanTest"
Cohesion: 0.23
Nodes (7): ThemeLiteralScanTest, 10. Risks & mitigations, 11. Rollout & verification, 1. Requirement, 4. Data model & schema, 9. Documentation updates (all required surfaces), Plan: M1a premium look (DS6): curated palettes, tonal surfaces, header glow, icon-only header actions, type pairing, bundled sans

### Community 117 - "AppContainer"
Cohesion: 0.10
Nodes (11): `:app` — Android application, Configuration, Data it owns, `log` (safe logging, A5a), Public interface, Window theme, AppContainer, PftApplication (+3 more)

### Community 119 - ".format"
Cohesion: 0.16
Nodes (11): Dependencies, Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface (+3 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.14
Nodes (8): PreMigrationBackup, `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "AuditEntry"
Cohesion: 0.07
Nodes (25): AuditEntryMapper, RoomAuditLog, AuditDaoTest, AuditEntryMapperTest, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), FieldChange, AuditAction, ARCHIVE (+17 more)

### Community 125 - "Icons and emoji (DS3, UI-6)"
Cohesion: 0.24
Nodes (7): CategoryColors, CategoryPalette, pair(), Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 127 - "Density"
Cohesion: 0.15
Nodes (9): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 3.4 Header glow (UI-13, DS6d), 3.8 Abstraction bar (+1 more)

### Community 128 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.11
Nodes (13): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions (+5 more)

### Community 132 - "ThemePalette"
Cohesion: 0.19
Nodes (7): colors(), ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, ChipColorsTest

### Community 134 - "How to run and test"
Cohesion: 0.15
Nodes (4): How to run and test, PftDatabase, TestDatabase, PftRootReadyTest

### Community 136 - "ManifestHardeningTest"
Cohesion: 0.06
Nodes (4): ManifestHardeningTest, WindowBackgroundTest, WindowThemeTest, 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`)

### Community 137 - "CollapsingHeaderScaffoldTest.kt"
Cohesion: 0.09
Nodes (3): assertShownInFull(), isShownInFull(), layout()

### Community 139 - "AppSetting"
Cohesion: 0.14
Nodes (5): EpochMillis, RoomAppSettingsRepository, RoomAppSettingsRepositoryTest, AppSetting, InMemoryAppSettingsRepository

### Community 140 - "PftSurfacesTest"
Cohesion: 0.11
Nodes (4): Case, GroupedSectionColorsTest, PftSurfacesTest, InMemoryEntityStore

### Community 143 - "TestClock"
Cohesion: 0.20
Nodes (7): EpochMillisTest, NameBasedId, Conventions, Data model, The audit trail, Version 1 (A4a), Version table

### Community 147 - "BottomActionBarSurfaceTest.kt"
Cohesion: 0.15
Nodes (6): IvoryPalette, BackgroundStyle, FLAT, TONAL, DevContent(), BottomActionBarSurfaceTest

### Community 148 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.29
Nodes (7): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice I — Cashew import (`feat/m1a-cashew-import`): tracking stub

### Community 153 - "MoneyTextSamples"
Cohesion: 0.53
Nodes (4): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples()

### Community 154 - "NameField"
Cohesion: 0.13
Nodes (9): Debug and release source sets, ChoiceRow(), DesignCatalog(), DevContent(), errorMessage(), NameField(), 2. Current state, Step 11 — `NameField` and `EditFormScaffold` (A5d) (+1 more)

### Community 156 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 157 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 160 - "HeaderStyle"
Cohesion: 0.67
Nodes (3): HeaderStyle, Dashboard, Standard

## Knowledge Gaps
- **198 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+193 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 602 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **76 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `CategoryPaletteTest`, `LogEventShapeTest`, `ThemePalette`, `EmptyState`, `FormScreenshotTest.kt`, `ManifestHardeningTest`, `ThemePrefs`, `MoneyTextTest`, `AppSetting`, `PftSurfacesTest`, `PftSpacingTest`, `EditFormScaffoldTest`, `ColorContrastTest`, `MigrationTest`, `BottomActionBarSurfaceTest.kt`, `BundledSymbolsLicenceTest`, `NavTransitionTest.kt`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `SampleId`, `AppServices`, `Previews, the token sheet and screenshot goldens`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `PftShapesTest.kt`, `CategoryGlyph`, `LaunchViewModelTest.kt`, `FieldState`, `PftThemeModeTest`, `RoomAuditColumnsTest`, `EntityMeta`, `ContentScreenshotTest`, `DbKeyManagerTest.kt`, `MoneyText`, `UserId`, `7. Test plan`, `MoneyTextTest.kt`, `SampleThing.kt`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `TokenSheetScreenshotTest.kt`, `MainActivityOpeningTest.kt`, `CollapsingHeaderScaffold`, `runSuspending`, `7. Test plan`, `PftDatabaseTest`, `expandedHeaderHeight`, `MigrationTestSupport`, `PreMigrationBackupTest`, `PressScaleTest.kt`, `NameFieldTest`, `GroupedSectionTest.kt`, `PftTheme`, `PftSymbolTest`, `DatabaseStartupTest.kt`, `Paise`, `BottomActionBarTest`, `SafeLogTest`, `BundledFontTest`, `DbKeyManagerWiringTest.kt`, `HostedComposeRule`, `AppContainer`, `ThemePaletteTest`, `.format`, `modifier`, `AuditEntry`, `AppServicesTest`, `ThemePrefsTest`?**
  _High betweenness centrality (0.292) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _198 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Collecting and Redacting Sample Messages Guide`, `Automatic capture from SMS (S-1..S-13)`, `Slice A — Foundation (`feat/m1a-foundation`)`, ``build-logic` — convention plugins`, `CLAUDE.md Development Constitution`, `Layered Modular Architecture (core vs app)`, `.format`, `Personal Finance Tracker PRD`?**
  _High betweenness centrality (0.110) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._