# Graph Report - personal-finance-tracker  (2026-11-14)

## Corpus Check
- 348 files · ~258,219 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 4031 nodes · 11991 edges · 182 communities (98 shown, 84 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 1906 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `58269993`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- LogEvent
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- MoneyTextTest.kt
- hooks
- test_import_symbols.py
- AuditDaoTest.kt
- ThemePrefs
- MoneyTextTest.kt
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Catalog.kt
- detekt.yml
- DesignCatalog.kt
- PftSymbol
- CollapsingHeaderScaffoldTest
- TestClock
- AppServices
- AppServicesTest
- BackgroundStyle
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- HeaderGlowScaffoldTest.kt
- PftTypographyTest
- CategoryGlyph
- dp
- GlyphSourceTest
- LaunchViewModelTest.kt
- Project README
- Paise
- PftMotion.kt
- HeaderTypePairingTest.kt
- PreMigrationBackup
- CLAUDE.md Development Constitution
- AppContainer
- CategoryGlyphScreenshotTest
- AuditedWriter
- FontBudgetTest
- PftThemeModeTest
- RoomAuditColumnsTest
- EntityMeta
- ContentScreenshotTest
- DbKeyManagerTest
- MoneyText
- Personal Finance Tracker PRD
- UserId
- redact_text
- SetupViewModelTest
- ThemePaletteTest
- PftTypography.kt
- Pseudo
- GroupedSection.kt
- Public interface
- BottomActionBar.kt
- localdensity
- PftDatabaseTest
- _Text
- MainActivityOpeningTest.kt
- import_symbols.py
- AppContainerTest.kt
- modifier
- Previews, the token sheet and screenshot goldens
- 7. Test plan
- PftSymbolTest
- ReleaseHasNoCatalogueTest.kt
- 13. Deviations from the plan, by slice
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- RoomAccountIntegrationTest
- `di` and startup (A5b)
- MigrationTestSupport
- RoomAuditedWriterIntegrationTest
- PreMigrationBackupTest
- TabularFiguresTest
- LedgerError
- HeaderIconButtonTest.kt
- NavTransitionTest.kt
- StatusChip
- GroupedSectionTest.kt
- PftTheme
- DbKeyManager
- Slice A — Foundation (`feat/m1a-foundation`)
- Plan: M1a — Core ledger (manual-entry household tracker)
- WindowThemeTest
- MoneyToneTest
- SampleThing.kt
- runSuspending
- AppSettingEntity
- PaletteSamples.kt
- Plan: Itemised transactions (one payment, many categories)
- parse_svg
- NameFieldTest.kt
- BundledFontTest
- BottomActionBarTest.kt
- ImportRunTest
- AppServices
- InMemoryKekSource
- CatalogSection
- localdensity
- HostedComposeRule
- LogEventShapeTest
- AuditTriggersTest
- FieldStateTest
- Loans and EMIs (LN-1..LN-5)
- MigrationTest
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- shouldbe
- 6.1 Slices
- ManifestHardeningTest
- RoomTransactionRunnerTest
- runSuspending
- Category
- AppSetting
- assertisdisplayed
- PaiseParser
- AccountDao
- EmptyState
- AuditEntryEntity
- 7. Test plan
- WindowThemeTest
- DevRouteTest.kt
- AccentHueTest
- applicationprovider
- PftSpacingTest
- PaiseParserTest.kt
- HeaderPolicy
- 6. Implementation steps (TDD-ordered)
- .format
- PftDatabase
- MoneySize
- DatabaseFactory
- PftSurfacesTest
- AuditTriggers
- ThemePrefsTest
- 6. Implementation steps (TDD-ordered)
- Appearance: colour palettes, dynamic colour, background and type
- 7. Test plan
- CategoryIcon
- SetupScreenTest.kt
- 3. Design & decision
- ThemeLiteralScanTest
- EditFormScaffoldTest.kt
- Data protection: encrypted storage and the "can't open your data" screen
- PeriodStepper
- HomeScreenTest.kt
- BundledSymbolsLicenceTest
- Security, privacy and integrity requirements (SEC/INT)
- FieldErrorMessageTest.kt
- CategoryPalette
- expandedHeaderHeight
- MainActivityStartupTest.kt
- assertnotequals
- How to run and test
- HeaderSlot
- Immutable Audit Trail
- rowscope
- Collecting and Redacting Sample Messages Guide
- Architecture overview
- HeaderPhase
- HeaderStyle
- CategoryColourTest.kt
- CategoryErrorsTest.kt

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 192 edges
2. `ThemePrefs` - 161 edges
3. `PftSymbol` - 125 edges
4. `Paise` - 112 edges
5. `How to run and test` - 108 edges
6. `HostedComposeRule` - 94 edges
7. `runSuspending()` - 85 edges
8. `CollapsingHeaderScaffoldTest` - 82 edges
9. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 71 edges
10. `Public interface` - 69 edges

## Surprising Connections (you probably didn't know these)
- `Step 1 — `LogEvent` rendering (A5a)` --references--> `LogEvent`  [INFERRED]
  docs/plans/2026-11-01-m1a-a5-first-run-setup.md → app/src/main/kotlin/dev/imagh/pft/app/log/LogEvent.kt
- `Step 2 — `LogEvent` can hold no text (A5a)` --references--> `LogEvent`  [INFERRED]
  docs/plans/2026-11-01-m1a-a5-first-run-setup.md → app/src/main/kotlin/dev/imagh/pft/app/log/LogEvent.kt
- `3.5 Fonts: bundling, licensing, custom import` --references--> `SafeLog`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/log/SafeLog.kt
- `13. Deviations from the plan, by slice` --references--> `asHeroTop()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderGlow.kt
- `10. Risks & mitigations` --references--> `MoneyText()`  [INFERRED]
  docs/plans/2026-11-10-itemised-transactions.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/MoneyText.kt

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

## Communities (182 total, 84 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "LogEvent"
Cohesion: 0.18
Nodes (10): `log` (safe logging, A5a), DbKeyUnavailable, DbOpened, IdentityCorrupt, LogEvent, LogValue, render(), SetupCompleted (+2 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "MoneyTextTest.kt"
Cohesion: 0.12
Nodes (5): layoutResult(), shownText(), assertShownInFull(), isShownInFull(), layout()

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 9 - "ThemePrefs"
Cohesion: 0.10
Nodes (6): EditFormPreview(), dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.10
Nodes (13): Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone(), roles() (+5 more)

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.17
Nodes (22): moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense (+14 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "DesignCatalog.kt"
Cohesion: 0.06
Nodes (9): AccentCheckSection(), SectionContent(), MotionSection(), SpringTrack(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), TokenSheet() (+1 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.08
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "TestClock"
Cohesion: 0.06
Nodes (13): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, AuditedWriterTest, EntityMetaTest, IdentityServiceTest (+5 more)

### Community 30 - "AppServices"
Cohesion: 0.09
Nodes (45): `di` and startup (A5b), Purpose, AppServices, AppStart, KeyUnavailable, Ready, SafeLog, FieldError (+37 more)

### Community 31 - "AppServicesTest"
Cohesion: 0.18
Nodes (3): Data it owns, EpochMillisTest, AppServicesTest

### Community 33 - "BackgroundStyle"
Cohesion: 0.13
Nodes (13): BackgroundStyle, FLAT, TONAL, PftSurfaces, colors(), ThemePalette, AUBERGINE, GRAPHITE (+5 more)

### Community 34 - "DbKeyManagerJvmTest"
Cohesion: 0.06
Nodes (6): DatabaseStartupTest, LogEventTest, SafeLogTest, DbKeyManagerJvmTest, InMemoryKekSource, NameBasedIdTest

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "HeaderGlowScaffoldTest.kt"
Cohesion: 0.07
Nodes (3): BottomActionBarSurfaceTest, HeaderGlowScaffoldTest, PressScaleTest

### Community 37 - "PftTypographyTest"
Cohesion: 0.10
Nodes (3): sansFamily(), PftTypographyTest, Resolved

### Community 42 - "Project README"
Cohesion: 0.22
Nodes (6): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Code Structure Map, core/shared Shared Packages, Project README, Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "Paise"
Cohesion: 0.06
Nodes (25): `data` (Room persistence, A4a), AccountEntity, AccountMapper, RoomAccountRepository, EntityMapper, MetaColumns, MetaMapper, MetaMapperTest (+17 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.11
Nodes (3): PftMotion, pressScale(), 11. Rollout & verification

### Community 46 - "PreMigrationBackup"
Cohesion: 0.15
Nodes (12): `security` and the encrypted open (A4b), PreMigrationBackup, PreMigrationBackupFailed, factory(), Corruption handling, Persistence and security, Pre-migration backup (INT-5), SQLCipher (+4 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "AppContainer"
Cohesion: 0.11
Nodes (8): AppContainer, RoomTestKit, LaunchRoutingTest, PftRootTest, Call flow example: first-run Setup, Startup, 7. Test plan, Step 14 — `PftNavHost`, Home placeholder, debug route, predictive back (A5f)

### Community 49 - "CategoryGlyphScreenshotTest"
Cohesion: 0.23
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 50 - "AuditedWriter"
Cohesion: 0.08
Nodes (5): RoomEntityRepository, EntityRepository, NameBasedId, idOf(), InMemoryEntityStore

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.05
Nodes (30): EntityMeta, LedgerEntity, EntityId, Fixture, Leaky, LeakyRepository, Note, NoteId (+22 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.21
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "MoneyText"
Cohesion: 0.18
Nodes (9): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText(), RollingDigits() (+1 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.11
Nodes (22): Personal Finance Tracker PRD, Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope, Idempotent ingestion (X-2) (+14 more)

### Community 59 - "UserId"
Cohesion: 0.10
Nodes (12): RoomIdentityRepository, AuditedTable, AuditedTables, Auditable, Device, IdentityRepository, UserProfile, UserId (+4 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 61 - "SetupViewModelTest"
Cohesion: 0.06
Nodes (6): awaitMain(), HomeViewModelTest, SetupFlowTest, SetupRouteTest, FailsFirstAppend, SetupViewModelTest

### Community 63 - "PftTypography.kt"
Cohesion: 0.18
Nodes (12): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansWeights(), DisplayFont, BUNDLED_SERIF (+4 more)

### Community 65 - "GroupedSection.kt"
Cohesion: 0.13
Nodes (7): GroupedRow, GroupedSectionColors, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, GroupedSectionPreview(), GroupedSectionSamples()

### Community 66 - "Public interface"
Cohesion: 0.07
Nodes (33): RoomTransactionRunner, RoomAppSettingsRepository, Configuration, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), AccountLists (+25 more)

### Community 67 - "BottomActionBar.kt"
Cohesion: 0.14
Nodes (4): BottomActionBarPreview(), BottomActionBarSamples(), StatusChipPreview(), StatusChipSamples()

### Community 72 - "MainActivityOpeningTest.kt"
Cohesion: 0.09
Nodes (7): DatabaseStartup, KeyUnavailable, Ready, PftApplication, DbKeyUnavailable, MainActivityOpeningTest, SlowOpeningApplication

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 75 - "modifier"
Cohesion: 0.09
Nodes (6): DevHeaderActions(), devRoutes(), fixedSp(), GlyphSize, Chip, Hero

### Community 76 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.16
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 77 - "7. Test plan"
Cohesion: 0.13
Nodes (10): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 1. Requirement, 3.4 Header glow (UI-13, DS6d) (+2 more)

### Community 79 - "ReleaseHasNoCatalogueTest.kt"
Cohesion: 0.15
Nodes (6): Debug and release source sets, Public interface, `ui/nav` and `ui/home` (A5f), Window theme, Routes, ReleaseHasNoCatalogueTest

### Community 80 - "13. Deviations from the plan, by slice"
Cohesion: 0.11
Nodes (13): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, MoneyColors, PftSpacing (+5 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.15
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 82 - "UserProfileEntity"
Cohesion: 0.19
Nodes (4): IdentityDao, DeviceEntity, UserProfileEntity, SampleDatabase

### Community 84 - "`di` and startup (A5b)"
Cohesion: 0.09
Nodes (7): `ui/setup` and the UiState convention (A5e), HomeUiState, HomeViewModel, SetupUiState, SetupViewModel, AlreadyInitialised, Step 12 — `SetupViewModel` (A5e)

### Community 89 - "LedgerError"
Cohesion: 0.11
Nodes (26): `ui/common` (shared components, DS3, DS4, DS5 and A5d), Duplicate, TooLong, toFieldError(), Behaviours worth knowing, AccountRules, AmountNotPositive, AmountTooLarge (+18 more)

### Community 92 - "StatusChip"
Cohesion: 0.16
Nodes (7): ChipColors, StatusChip(), StatusChipTest, Content primitives (DS5), `EmptyState`, `GroupedSection`, `StatusChip`

### Community 94 - "PftTheme"
Cohesion: 0.13
Nodes (5): ChoiceRow(), DesignCatalog(), HeaderTags, PftTheme(), DesignCatalogTest

### Community 95 - "DbKeyManager"
Cohesion: 0.30
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 96 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.07
Nodes (44): Manifest security posture (SEC-2, SEC-3, SEC-6), Responsibilities and boundaries, MainActivity, BottomActionBar(), CollapsingHeaderScaffold(), EditFormScaffold(), EditFormSample(), GroupedSection() (+36 more)

### Community 98 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.25
Nodes (8): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 9. Documentation updates (all required surfaces), Plan: M1a — Core ledger (manual-entry household tracker)

### Community 101 - "SampleThing.kt"
Cohesion: 0.10
Nodes (8): RoomEntityRepositoryTest, SampleId, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository, Paise

### Community 104 - "PaletteSamples.kt"
Cohesion: 0.42
Nodes (4): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 105 - "Plan: Itemised transactions (one payment, many categories)"
Cohesion: 0.22
Nodes (9): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5. Flows, 8. Security considerations, 9. Documentation updates (all required surfaces) (+1 more)

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 111 - "AppServices"
Cohesion: 0.08
Nodes (13): message(), HomeRoute(), HomeScreen(), PftNavHost(), PftRoot(), SetupRoute(), SetupScreen(), DataUnavailableCause (+5 more)

### Community 113 - "CatalogSection"
Cohesion: 0.14
Nodes (14): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+6 more)

### Community 114 - "localdensity"
Cohesion: 0.08
Nodes (10): Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING, FormScreenshotTest (+2 more)

### Community 115 - "HostedComposeRule"
Cohesion: 0.14
Nodes (4): HostedComposeRule, RegisterHostActivity, Statement, PaletteScreenshotTest

### Community 119 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5), M1a Core ledger (+2 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.10
Nodes (13): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+5 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.17
Nodes (6): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "shouldbe"
Cohesion: 0.06
Nodes (32): RoomAuditLog, EpochMillis, AuditDaoTest, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE (+24 more)

### Community 124 - "6.1 Slices"
Cohesion: 0.10
Nodes (25): paletteFor(), SansFont, FIGTREE, PLUS_JAKARTA_SANS, SYSTEM, ThemeMode, DARK, LIGHT (+17 more)

### Community 127 - "runSuspending"
Cohesion: 0.27
Nodes (3): RoomIdentityRepositoryTest, runSuspending(), 3.3 Dependencies (one pins-only PR, A5c)

### Community 128 - "Category"
Cohesion: 0.05
Nodes (34): Category, CategoryColour, AMBER, BLUE, CORAL, EMERALD, INDIGO, MAGENTA (+26 more)

### Community 129 - "AppSetting"
Cohesion: 0.16
Nodes (3): RoomAppSettingsRepositoryTest, AppSetting, InMemoryAppSettingsRepository

### Community 135 - "7. Test plan"
Cohesion: 0.32
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 139 - "AccentHueTest"
Cohesion: 0.43
Nodes (3): AccentHueTest, hueDistance(), labHue()

### Community 142 - "PaiseParserTest.kt"
Cohesion: 0.17
Nodes (4): How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest

### Community 145 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.29
Nodes (7): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice I — Cashew import (`feat/m1a-cashew-import`): tracking stub

### Community 146 - ".format"
Cohesion: 0.18
Nodes (10): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+2 more)

### Community 148 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 149 - "DatabaseFactory"
Cohesion: 0.13
Nodes (5): EncryptedMigrationTest, DatabaseFactory, open(), Migrations on the encrypted file (`EncryptedMigrationTest`), The key array and SQLCipher (a trap)

### Community 152 - "AuditTriggers"
Cohesion: 0.33
Nodes (3): AuditTriggers, The audit trail, Audit triggers

### Community 154 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.25
Nodes (8): 6. Implementation steps (TDD-ordered), Later-slice deltas (no new PRs; each rides its slice's PR), PR breakdown (incremental — required), Step C2 — Transfers (delta to the plan's C2), Step C3 — Queries, search, balances (delta to the plan's C3), Step C4 — List and plain add/edit UI (delta to the plan's C4), Step C5 — Search, filters and detail with history (delta to the plan's C5), Step P0 — Record the decision (docs only, this plan's PR)

### Community 155 - "Appearance: colour palettes, dynamic colour, background and type"
Cohesion: 0.25
Nodes (5): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you

### Community 156 - "7. Test plan"
Cohesion: 0.29
Nodes (5): AccountServiceTest, CategoryServiceTest, DefaultCategoriesTest, 7. Test plan, Slice B — Accounts, categories, tags (`feat/m1a-ledger-setup`)

### Community 157 - "CategoryIcon"
Cohesion: 0.15
Nodes (7): Emoji, GlyphSource, Letter, Symbol, CategoryIcon, Emoji, Symbol

### Community 159 - "3. Design & decision"
Cohesion: 0.29
Nodes (7): 3.1 The model: every transaction has one or more lines, 3.4 Reads, 3.5 UI, 3.6 Configuration (CLAUDE.md §3.5), 3.7 Future items recorded, not built, 3.8 Abstraction bar, 3. Design & decision

### Community 161 - "ThemeLiteralScanTest"
Cohesion: 0.15
Nodes (9): `:app` — Android application, Configuration, Dependencies, LoggingScanTest, SourceScan, Violation, ThemeLiteralScanTest, Planned: premium look (DS6) (+1 more)

### Community 164 - "Data protection: encrypted storage and the "can't open your data" screen"
Cohesion: 0.22
Nodes (7): Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen, What it does for you, Behaviour details, First run and profiles, What it does for you

### Community 165 - "PeriodStepper"
Cohesion: 0.33
Nodes (11): CollapsingHeader(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderTitle(), LowerBand(), PeriodStepper() (+3 more)

### Community 168 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 172 - "CategoryPalette"
Cohesion: 0.27
Nodes (7): CategoryColors, CategoryPalette, pair(), Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 178 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 181 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 184 - "Architecture overview"
Cohesion: 0.33
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 185 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 186 - "HeaderStyle"
Cohesion: 0.67
Nodes (3): HeaderStyle, Dashboard, Standard

## Knowledge Gaps
- **218 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+213 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 650 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **84 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `AuditDaoTest.kt`, `ThemePrefs`, `MoneyTextTest.kt`, `ColorContrastTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `AppServices`, `AppServicesTest`, `BackgroundStyle`, `DbKeyManagerJvmTest`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `dp`, `GlyphSourceTest`, `LaunchViewModelTest.kt`, `Paise`, `AppContainer`, `CategoryGlyphScreenshotTest`, `AuditedWriter`, `PftThemeModeTest`, `RoomAuditColumnsTest`, `ContentScreenshotTest`, `DbKeyManagerTest`, `MoneyText`, `SetupViewModelTest`, `ThemePaletteTest`, `Public interface`, `localdensity`, `MainActivityOpeningTest.kt`, `AppContainerTest.kt`, `Previews, the token sheet and screenshot goldens`, `PftSymbolTest`, `ReleaseHasNoCatalogueTest.kt`, `13. Deviations from the plan, by slice`, `RoomAccountIntegrationTest`, `MigrationTestSupport`, `RoomAuditedWriterIntegrationTest`, `PreMigrationBackupTest`, `NavTransitionTest.kt`, `StatusChip`, `GroupedSectionTest.kt`, `PftTheme`, `Slice A — Foundation (`feat/m1a-foundation`)`, `WindowThemeTest`, `MoneyToneTest`, `SampleThing.kt`, `runSuspending`, `NameFieldTest.kt`, `BundledFontTest`, `BottomActionBarTest.kt`, `AppServices`, `localdensity`, `HostedComposeRule`, `LogEventShapeTest`, `AuditTriggersTest`, `FieldStateTest`, `MigrationTest`, `shouldbe`, `ManifestHardeningTest`, `RoomTransactionRunnerTest`, `runSuspending`, `AppSetting`, `EmptyState`, `7. Test plan`, `WindowThemeTest`, `DevRouteTest.kt`, `AccentHueTest`, `applicationprovider`, `PftSpacingTest`, `.format`, `PftDatabase`, `DatabaseFactory`, `PftSurfacesTest`, `ThemePrefsTest`, `SetupScreenTest.kt`, `ThemeLiteralScanTest`, `EditFormScaffoldTest.kt`, `HomeScreenTest.kt`, `BundledSymbolsLicenceTest`, `FieldErrorMessageTest.kt`, `expandedHeaderHeight`, `MainActivityStartupTest.kt`, `assertnotequals`?**
  _High betweenness centrality (0.245) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _218 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `assertisdisplayed`, `EmptyState`, `MoneyTextTest.kt`, `WindowThemeTest`, `ThemePrefs`, `MoneyTextTest.kt`, `DevRouteTest.kt`, `PftSpacingTest`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `DesignCatalog.kt`, `CollapsingHeaderScaffoldTest`, `AppServices`, `SetupScreenTest.kt`, `CollapsingHeaderScaffold.kt`, `BackgroundStyle`, `EditFormScaffoldTest.kt`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `dp`, `HomeScreenTest.kt`, `FieldErrorMessageTest.kt`, `CategoryPalette`, `PftMotion.kt`, `HeaderTypePairingTest.kt`, `AppContainer`, `CategoryGlyphScreenshotTest`, `PftThemeModeTest`, `ContentScreenshotTest`, `Architecture overview`, `MoneyText`, `SetupViewModelTest`, `PftTypography.kt`, `GroupedSection.kt`, `BottomActionBar.kt`, `localdensity`, `modifier`, `Previews, the token sheet and screenshot goldens`, `7. Test plan`, `ReleaseHasNoCatalogueTest.kt`, `13. Deviations from the plan, by slice`, `Plan: M1a design system and visual direction (lands before A5)`, `HeaderIconButtonTest.kt`, `StatusChip`, `GroupedSectionTest.kt`, `Slice A — Foundation (`feat/m1a-foundation`)`, `PaletteSamples.kt`, `NameFieldTest.kt`, `BottomActionBarTest.kt`, `AppServices`, `localdensity`, `HostedComposeRule`, `6.1 Slices`?**
  _High betweenness centrality (0.101) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._