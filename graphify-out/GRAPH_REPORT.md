# Graph Report - personal-finance-tracker  (2026-11-13)

## Corpus Check
- 308 files · ~246,452 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3799 nodes · 11293 edges · 158 communities (87 shown, 71 thin omitted)
- Extraction: 82% EXTRACTED · 18% INFERRED · 0% AMBIGUOUS · INFERRED: 1978 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `34b30fb0`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- AuditedWriterTest.kt
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- shouldbe
- hooks
- test_import_symbols.py
- DesignCatalog.kt
- ThemeResolutionTest
- Paise
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Catalog.kt
- detekt.yml
- MainActivity
- PftSymbol
- CollapsingHeaderScaffoldTest
- SampleThing.kt
- AppServices
- How to run and test
- CollapsingHeaderScaffold.kt
- GroupedSectionColorsTest
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- HeaderGlowScaffoldTest
- PftTypographyTest
- HeaderIconButton
- PftShapesTest.kt
- CategoryGlyph
- LaunchViewModelTest.kt
- Project README
- Plan: Itemised transactions (one payment, many categories)
- PftMotion.kt
- HeaderTypePairingTest
- MoneyColors
- CLAUDE.md Development Constitution
- LedgerError
- CollapsingHeaderScaffoldTest.kt
- PressScaleTest.kt
- FieldChange
- PftThemeModeTest
- RoomAuditColumnsTest
- EntityMeta
- ContentScreenshotTest
- DbKeyManagerTest.kt
- MoneyText
- Personal Finance Tracker PRD
- UserId
- redact_text
- Slice A — Foundation (`feat/m1a-foundation`)
- NavTransitionTest
- 6.1 Slices
- Pseudo
- GroupedSectionSamples
- 2. Current state
- SetupFlowTest.kt
- PftTypography.kt
- AuditTriggersTest
- _Text
- MainActivityOpeningTest.kt
- import_symbols.py
- runSuspending
- WindowThemeTest
- Previews, the token sheet and screenshot goldens
- 7. Test plan
- PftSymbolTest
- 13. Deviations from the plan, by slice
- CollapsingHeaderScaffold
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- EditFormScaffoldTest
- NameFieldTest
- MigrationTestSupport
- FieldState
- PreMigrationBackupTest
- TabularFiguresTest
- TokenSheetScreenshotTest.kt
- ThemePaletteTest
- BottomActionBarTest
- Appearance: colour palettes, dynamic colour, background and type
- GroupedSectionTest
- PftTheme
- DbKeyManager
- SetupScreenshotTest.kt
- StatusChip
- Data protection: encrypted storage and the "can't open your data" screen
- DatabaseStartupTest.kt
- MoneyToneTest
- Migrations.kt
- PftSurfacesTest
- AppServicesTest
- Icons and emoji (DS3, UI-6)
- HostedComposeRule
- parse_svg
- HomeScreenTest.kt
- BundledFontTest
- NameField
- ImportRunTest
- EditFormScaffold
- InMemoryKekSource
- AuditColumnsCheck
- DataUnavailableScreenTest
- PaletteSwatchesTest.kt
- LogEventShapeTest
- AppContainer
- SetupScreenTest
- Loans and EMIs (LN-1..LN-5)
- PreMigrationBackup
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- 6. Implementation steps (TDD-ordered)
- 6. Implementation steps (TDD-ordered)
- FormScreenshotTest.kt
- SetupRouteTest
- PftSpacingTest
- PaletteScreenshotTest.kt
- .check
- BottomActionBarSurfaceTest.kt
- PaiseParser
- expandedHeaderHeight
- ThemePrefs
- StatusChipTest.kt
- ManifestHardeningTest
- LedgerLimits
- CategoryPaletteTest
- Conventions
- HeaderSlot
- AppSetting
- Collecting and Redacting Sample Messages Guide
- BottomActionBar
- FontBudgetTest
- SafeLogTest
- MoneyTextTest.kt
- 3. Design & decision
- Security, privacy and integrity requirements (SEC/INT)
- DatabaseFactory
- debug/kotlin/dev/imagh/pft/app/DevEntry.kt
- LogEventTest
- GlyphSize
- ThemePrefsTest
- FormSamples.kt
- HeaderPhase
- Immutable Audit Trail

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 190 edges
2. `ThemePrefs` - 159 edges
3. `PftSymbol` - 127 edges
4. `How to run and test` - 101 edges
5. `Paise` - 96 edges
6. `HostedComposeRule` - 94 edges
7. `CollapsingHeaderScaffoldTest` - 83 edges
8. `runSuspending()` - 74 edges
9. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 71 edges
10. `PftDatabase` - 59 edges

## Surprising Connections (you probably didn't know these)
- `3.5 Fonts: bundling, licensing, custom import` --references--> `SafeLog`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/log/SafeLog.kt
- `PR breakdown (incremental — required)` --references--> `GlyphSource`  [INFERRED]
  docs/plans/2026-11-10-m1a-slice-b-ledger-setup.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/GlyphSource.kt
- `13. Deviations from the plan, by slice` --references--> `asHeroTop()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderGlow.kt
- `10. Risks & mitigations` --references--> `MoneyText()`  [INFERRED]
  docs/plans/2026-11-10-itemised-transactions.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/MoneyText.kt
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

## Communities (158 total, 71 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "AuditedWriterTest.kt"
Cohesion: 0.14
Nodes (7): AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "shouldbe"
Cohesion: 0.05
Nodes (13): EpochMillisTest, How to run and test, AuditLog, AuditDiffTest, Thing, EntityMetaTest, IdentityServiceTest, IdsTest (+5 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "DesignCatalog.kt"
Cohesion: 0.06
Nodes (13): AccentCheckSection(), MotionSection(), SpringTrack(), fixedSp(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), GroupedSectionColors (+5 more)

### Community 9 - "ThemeResolutionTest"
Cohesion: 0.10
Nodes (5): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemeResolutionTest, Surfaces and window colour (DS6c, UI-12, UI-18)

### Community 10 - "Paise"
Cohesion: 0.11
Nodes (7): MoneySize, Hero, Large, MoneyTextTest, Spec, Paise, Slice D — Budgets (`feat/m1a-budgets`)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.09
Nodes (16): AccentHueTest, Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone() (+8 more)

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.10
Nodes (31): GroupedSection(), GroupedSectionDefaults, moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER (+23 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "MainActivity"
Cohesion: 0.12
Nodes (14): `:app` — Android application, Data it owns, Debug and release source sets, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Responsibilities and boundaries, `ui/nav` and `ui/home` (A5f), Window theme (+6 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.09
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "SampleThing.kt"
Cohesion: 0.06
Nodes (17): `data` (Room persistence, A4a), BaseDao, EntityMapper, MetaColumns, MetaMapper, RoomEntityRepository, MetaMapperTest, RoomEntityRepositoryTest (+9 more)

### Community 30 - "AppServices"
Cohesion: 0.06
Nodes (50): `di` and startup (A5b), `log` (safe logging, A5a), Purpose, `ui/setup` and the UiState convention (A5e), AppServices, AppStart, KeyUnavailable, Ready (+42 more)

### Community 31 - "How to run and test"
Cohesion: 0.07
Nodes (10): How to run and test, DatabaseStartup, Ready, PftDatabase, TestDatabase, HomeViewModelTest, LaunchRoutingTest, ReleaseHasNoCatalogueTest (+2 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.18
Nodes (14): Row, TokenSheet(), TokenSheetPreview(), TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface() (+6 more)

### Community 40 - "CategoryGlyph"
Cohesion: 0.09
Nodes (9): CategoryGlyph(), Emoji, GlyphSource, isEmojiValue(), isSymbolKey(), Letter, Symbol, CategoryGlyphTest (+1 more)

### Community 42 - "Project README"
Cohesion: 0.22
Nodes (6): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Code Structure Map, core/shared Shared Packages, Project README, Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "Plan: Itemised transactions (one payment, many categories)"
Cohesion: 0.22
Nodes (9): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5. Flows, 8. Security considerations, 9. Documentation updates (all required surfaces) (+1 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.09
Nodes (6): iosSpring(), PftMotion, pressScale(), PftMotionTest, 7. Test plan, 11. Rollout & verification

### Community 46 - "MoneyColors"
Cohesion: 0.12
Nodes (7): ChipColors, MoneyColors, PftShapes, PftSpacing, ChipColorsTest, Theming, 3.3 Tokens

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "LedgerError"
Cohesion: 0.14
Nodes (21): `ui/common` (shared components, DS3, DS4, DS5 and A5d), FieldError, Required, TooLong, toFieldError(), AmountNotPositive, AmountTooLarge, Archived (+13 more)

### Community 49 - "CollapsingHeaderScaffoldTest.kt"
Cohesion: 0.10
Nodes (3): assertShownInFull(), isShownInFull(), layout()

### Community 51 - "FieldChange"
Cohesion: 0.11
Nodes (5): AuditEntryEntity, AuditEntryMapper, AuditDaoTest, AuditEntryMapperTest, FieldChange

### Community 54 - "EntityMeta"
Cohesion: 0.06
Nodes (23): EntityMeta, LedgerEntity, EntityId, AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind (+15 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.21
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "MoneyText"
Cohesion: 0.09
Nodes (21): Dependencies, atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText() (+13 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.11
Nodes (22): Personal Finance Tracker PRD, Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope, Idempotent ingestion (X-2) (+14 more)

### Community 59 - "UserId"
Cohesion: 0.06
Nodes (29): RoomIdentityRepository, RoomIdentityRepositoryTest, AuditedTables, AuditDiff, AuditAction, ARCHIVE, CREATE, DELETE (+21 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 61 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.13
Nodes (5): RoomAuditLog, awaitMain(), FailsFirstAppend, SetupViewModelTest, Slice A — Foundation (`feat/m1a-foundation`)

### Community 63 - "6.1 Slices"
Cohesion: 0.07
Nodes (38): sansFamily(), colors(), ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, paletteFor() (+30 more)

### Community 65 - "GroupedSectionSamples"
Cohesion: 0.36
Nodes (4): GroupedRow, GroupedSectionScope, GroupedSectionPreview(), GroupedSectionSamples()

### Community 66 - "2. Current state"
Cohesion: 0.07
Nodes (40): Behaviours worth knowing, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, AuditedWriter, AuditSource, MANUAL, RECURRING (+32 more)

### Community 68 - "PftTypography.kt"
Cohesion: 0.17
Nodes (12): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansWeights(), DisplayFont, BUNDLED_SERIF (+4 more)

### Community 72 - "MainActivityOpeningTest.kt"
Cohesion: 0.12
Nodes (3): MainActivityOpeningTest, SlowOpeningApplication, MainActivityStartupTest

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 74 - "runSuspending"
Cohesion: 0.05
Nodes (9): FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAppSettingsRepositoryTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomTransactionRunnerTest, AppContainerTest, runSuspending() (+1 more)

### Community 75 - "WindowThemeTest"
Cohesion: 0.12
Nodes (3): SyncWindowBackground(), WindowBackgroundTest, WindowThemeTest

### Community 76 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.07
Nodes (26): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+18 more)

### Community 77 - "7. Test plan"
Cohesion: 0.15
Nodes (9): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 3.4 Header glow (UI-13, DS6d), 3.8 Abstraction bar (+1 more)

### Community 79 - "13. Deviations from the plan, by slice"
Cohesion: 0.18
Nodes (8): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, MidnightPalette, PftSurfaces, Colour, 13. Deviations from the plan, by slice

### Community 80 - "CollapsingHeaderScaffold"
Cohesion: 0.25
Nodes (15): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle, Dashboard (+7 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.15
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 82 - "UserProfileEntity"
Cohesion: 0.07
Nodes (6): AuditDao, IdentityDao, DeviceEntity, UserProfileEntity, AppSettingDao, AppSettingEntity

### Community 85 - "MigrationTestSupport"
Cohesion: 0.06
Nodes (8): AuditTriggers, MigrationTestSupport, RegisterHostActivity, Statement, BundledSymbolsLicenceTest, MigrationTest, Audit triggers, Step B3b — Categories Room, v3, seeding wired into Setup

### Community 86 - "FieldState"
Cohesion: 0.29
Nodes (3): FieldCheck, FieldState, FieldStateTest

### Community 87 - "PreMigrationBackupTest"
Cohesion: 0.05
Nodes (7): Configuration, PreMigrationBackupTest, LoggingScanTest, DbKeyManagerWiringTest, SourceScan, Violation, ThemeLiteralScanTest

### Community 92 - "Appearance: colour palettes, dynamic colour, background and type"
Cohesion: 0.18
Nodes (5): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you

### Community 94 - "PftTheme"
Cohesion: 0.16
Nodes (5): ChoiceRow(), DesignCatalog(), PftTheme(), DesignCatalogTest, Step 11 — `NameField` and `EditFormScaffold` (A5d)

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 97 - "StatusChip"
Cohesion: 0.24
Nodes (3): StatusChip(), StatusChipPreview(), StatusChipSamples()

### Community 98 - "Data protection: encrypted storage and the "can't open your data" screen"
Cohesion: 0.22
Nodes (7): Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen, What it does for you, Behaviour details, First run and profiles, What it does for you

### Community 104 - "Icons and emoji (DS3, UI-6)"
Cohesion: 0.27
Nodes (7): CategoryColors, CategoryPalette, pair(), Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 109 - "NameField"
Cohesion: 0.31
Nodes (3): errorMessage(), NameField(), 3.3 Abstraction check

### Community 111 - "EditFormScaffold"
Cohesion: 0.09
Nodes (11): EditFormScaffold(), HomeRoute(), HomeScreen(), PftNavHost(), SetupRoute(), SetupScreen(), Where it lives, 3.5 Design system (owner decision, 2026-10-29) (+3 more)

### Community 112 - "InMemoryKekSource"
Cohesion: 0.13
Nodes (3): AndroidKeystoreKek, KekSource, InMemoryKekSource

### Community 113 - "AuditColumnsCheck"
Cohesion: 0.44
Nodes (3): AuditColumnsCheck, AuditedTable, Step C1c — Room v4 for transactions and lines

### Community 114 - "DataUnavailableScreenTest"
Cohesion: 0.15
Nodes (8): Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING, DataUnavailableScreenTest

### Community 117 - "AppContainer"
Cohesion: 0.10
Nodes (11): AppContainer, PftRoot(), RoomTestKit, PftRootTest, DevRouteTest, SettingKeys, Call flow example: first-run Setup, Startup (+3 more)

### Community 119 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5), M1a Core ledger (+2 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.23
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.20
Nodes (10): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Step B2b — Account create and edit, Step B2c — `PftBottomSheet` and `AccountPicker`, Step B4a — Categories list, Step B4b — Category create/edit (name, parent, colour, symbol) and `CategoryPicker`, Step B4c — Emoji icons, Step B5a — Tags core and Room, v4 (+2 more)

### Community 124 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.22
Nodes (9): 6. Implementation steps (TDD-ordered), Later-slice deltas (no new PRs; each rides its slice's PR), PR breakdown (incremental — required), Step C2 — Transfers (delta to the plan's C2), Step C3 — Queries, search, balances (delta to the plan's C3), Step C4 — List and plain add/edit UI (delta to the plan's C4), Step C4b — Items editor (new), Step C5 — Search, filters and detail with history (delta to the plan's C5) (+1 more)

### Community 130 - "BottomActionBarSurfaceTest.kt"
Cohesion: 0.18
Nodes (5): IvoryPalette, BackgroundStyle, FLAT, TONAL, BottomActionBarSurfaceTest

### Community 133 - "ThemePrefs"
Cohesion: 0.21
Nodes (3): EmptyState(), ThemePrefs, EmptyStateTest

### Community 136 - "ManifestHardeningTest"
Cohesion: 0.08
Nodes (5): ManifestHardeningTest, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest

### Community 137 - "LedgerLimits"
Cohesion: 0.06
Nodes (28): Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, Purpose, LedgerLimits, 10. Risks & mitigations, 11. Rollout & verification (+20 more)

### Community 139 - "Conventions"
Cohesion: 0.14
Nodes (6): NameBasedId, Conventions, Data model, The audit trail, Version 1 (A4a), Version table

### Community 140 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 141 - "AppSetting"
Cohesion: 0.09
Nodes (14): EpochMillis, RoomTransactionRunner, RoomAppSettingsRepository, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), AppSetting, AppSettingsRepository, TransactionRunner, InMemoryAppSettingsRepository (+6 more)

### Community 142 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 143 - "BottomActionBar"
Cohesion: 0.11
Nodes (13): BottomActionBar(), BottomActionBarPreview(), BottomActionBarSamples(), Adding a token, Build-time trust decisions, Design system, Motion (UI-10), One-hand layout (UI-7) (+5 more)

### Community 147 - "3. Design & decision"
Cohesion: 0.29
Nodes (7): 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins"), 3. Design & decision

### Community 148 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 149 - "DatabaseFactory"
Cohesion: 0.06
Nodes (21): `security` and the encrypted open (A4b), DatabaseFactory, PreMigrationBackupFailed, DbKeyUnavailable, DbOpened, LogValue, render(), DbKeyUnavailable (+13 more)

### Community 150 - "debug/kotlin/dev/imagh/pft/app/DevEntry.kt"
Cohesion: 0.21
Nodes (3): DevHeaderActions(), devRoutes(), DevHeaderActions()

### Community 152 - "GlyphSize"
Cohesion: 0.50
Nodes (3): GlyphSize, Chip, Hero

### Community 155 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

## Knowledge Gaps
- **198 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+193 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 623 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **71 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `PaletteScreenshotTest.kt`, `.check`, `BottomActionBarSurfaceTest.kt`, `expandedHeaderHeight`, `ThemePrefs`, `StatusChipTest.kt`, `ManifestHardeningTest`, `ThemeResolutionTest`, `Paise`, `CategoryPaletteTest`, `ColorContrastTest`, `SafeLogTest`, `LogEventTest`, `ThemePrefsTest`, `MainActivity`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `SampleThing.kt`, `AppServices`, `GroupedSectionColorsTest`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `PftShapesTest.kt`, `CategoryGlyph`, `LaunchViewModelTest.kt`, `PftMotion.kt`, `MoneyColors`, `PressScaleTest.kt`, `FieldChange`, `PftThemeModeTest`, `RoomAuditColumnsTest`, `ContentScreenshotTest`, `DbKeyManagerTest.kt`, `MoneyText`, `UserId`, `Slice A — Foundation (`feat/m1a-foundation`)`, `NavTransitionTest`, `2. Current state`, `SetupFlowTest.kt`, `AuditTriggersTest`, `MainActivityOpeningTest.kt`, `runSuspending`, `WindowThemeTest`, `Previews, the token sheet and screenshot goldens`, `PftSymbolTest`, `EditFormScaffoldTest`, `NameFieldTest`, `MigrationTestSupport`, `FieldState`, `PreMigrationBackupTest`, `TokenSheetScreenshotTest.kt`, `ThemePaletteTest`, `BottomActionBarTest`, `GroupedSectionTest`, `PftTheme`, `SetupScreenshotTest.kt`, `DatabaseStartupTest.kt`, `MoneyToneTest`, `PftSurfacesTest`, `AppServicesTest`, `HomeScreenTest.kt`, `BundledFontTest`, `DataUnavailableScreenTest`, `LogEventShapeTest`, `AppContainer`, `SetupScreenTest`, `FormScreenshotTest.kt`, `SetupRouteTest`, `PftSpacingTest`?**
  _High betweenness centrality (0.245) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _198 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `PaletteScreenshotTest.kt`, `BottomActionBarSurfaceTest.kt`, `ThemePrefs`, `StatusChipTest.kt`, `DesignCatalog.kt`, `ThemeResolutionTest`, `Paise`, `BottomActionBar`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `MoneyTextTest.kt`, `3. Design & decision`, `FormSamples.kt`, `MainActivity`, `CollapsingHeaderScaffoldTest`, `AppServices`, `How to run and test`, `CollapsingHeaderScaffold.kt`, `TokenSheetSections.kt`, `HeaderGlowScaffoldTest`, `PftTypographyTest`, `HeaderIconButton`, `PftShapesTest.kt`, `CategoryGlyph`, `PftMotion.kt`, `HeaderTypePairingTest`, `MoneyColors`, `CollapsingHeaderScaffoldTest.kt`, `PressScaleTest.kt`, `PftThemeModeTest`, `ContentScreenshotTest`, `MoneyText`, `6.1 Slices`, `GroupedSectionSamples`, `2. Current state`, `SetupFlowTest.kt`, `PftTypography.kt`, `WindowThemeTest`, `Previews, the token sheet and screenshot goldens`, `7. Test plan`, `13. Deviations from the plan, by slice`, `CollapsingHeaderScaffold`, `Plan: M1a design system and visual direction (lands before A5)`, `EditFormScaffoldTest`, `NameFieldTest`, `TokenSheetScreenshotTest.kt`, `BottomActionBarTest`, `GroupedSectionTest`, `SetupScreenshotTest.kt`, `StatusChip`, `Icons and emoji (DS3, UI-6)`, `HostedComposeRule`, `HomeScreenTest.kt`, `NameField`, `EditFormScaffold`, `DataUnavailableScreenTest`, `PaletteSwatchesTest.kt`, `AppContainer`, `SetupScreenTest`, `FormScreenshotTest.kt`, `SetupRouteTest`, `PftSpacingTest`?**
  _High betweenness centrality (0.090) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._