# Graph Report - personal-finance-tracker  (2026-11-15)

## Corpus Check
- 400 files · ~295,569 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 4675 nodes · 14566 edges · 186 communities (98 shown, 88 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 2071 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `35e1a863`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- Tag
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- RoomTagIntegrationTest
- hooks
- test_import_symbols.py
- AuditEntryMapperTest
- ThemeResolutionTest
- Paise
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Catalog.kt
- detekt.yml
- EntityListScreen.kt
- PftSymbol
- CollapsingHeaderScaffoldTest.kt
- MotionSection.kt
- CategoryEntity
- AccountEditViewModel.kt
- AccountsScreen.kt
- CollapsingHeaderScaffold
- DbKeyManagerJvmTest
- DesignCatalog.kt
- HeaderGlowScaffoldTest.kt
- PftTypographyTest
- CategoryGlyph
- Loans and EMIs (LN-1..LN-5)
- assertequals
- LaunchViewModelTest.kt
- Layered Modular Architecture (core vs app)
- AccountType
- PftMotion.kt
- HeaderTypePairingTest.kt
- assertisdisplayed
- CLAUDE.md Development Constitution
- How to run and test
- CollapsingHeaderScaffold.kt
- awaitMain
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
- AccountEditViewModelTest
- LogEvent
- PftTypography.kt
- Pseudo
- EntityListScreenTest.kt
- Public interface
- ThemePalette
- MoneyTextTest.kt
- ManifestHardeningTest
- _Text
- PreMigrationBackupTest
- import_symbols.py
- AppContainerTest.kt
- RoomCategoryIntegrationTest
- modifier
- 7. Test plan
- GroupedSectionTest.kt
- ReleaseHasNoCatalogueTest.kt
- `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)
- Plan: M1a design system and visual direction (lands before A5)
- AccountEditScreen.kt
- RoomAccountIntegrationTest
- AppServices
- MigrationTest
- RoomAuditedWriterIntegrationTest
- CategoryGlyphScreenshotTest
- TabularFiguresTest
- LedgerError
- HeaderIconButtonTest.kt
- AccountsViewModelTest
- StatusChipTest.kt
- EncryptedMigrationTest
- PftTheme
- DbKeyManager
- onnodewithtext
- InMemoryEntityRepository
- AccountEditScreenTest.kt
- DataUnavailableScreenTest
- MoneyToneTest
- SampleThing.kt
- IdentityRoomIntegrationTest
- AppSettingEntity
- SetupFlowTest.kt
- 13. Deviations from the plan, by slice
- parse_svg
- SetupScreenTest.kt
- BundledFontTest
- BottomActionBarTest.kt
- ImportRunTest
- r
- InMemoryKekSource
- CatalogSection
- .check
- Collecting and Redacting Sample Messages Guide
- LogEventShapeTest
- Category
- DatabaseStartupTest.kt
- Plan: Itemised transactions (one payment, many categories)
- WindowThemeTest
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- shouldbe
- AccountsRouteTest.kt
- PftSymbolTest
- RoomTransactionRunnerTest
- NameFieldTest.kt
- FormScreenshotTest.kt
- runSuspending
- .append
- How to run and test
- AuditFieldsCheck.kt
- `ui/more` and `ui/accounts` (B2a, B2b)
- AuditEntryEntity
- FieldState
- WindowBackgroundTest.kt
- DevRouteTest.kt
- .clock
- AccountsViewModelTest.kt
- DbKeyManagerWiringTest.kt
- UndoDeleteSnackbarTest.kt
- How to run and test
- EntityListSamples.kt
- SetupScreenshotTest.kt
- MoneyColors
- surface
- .format
- 7. Test plan
- SafeLogTest
- rowscope
- .clock
- SetupRouteTest.kt
- ThemePaletteTest
- PftDatabase
- AmountFieldTest
- assertequals
- PftSpacingTest
- PftSurfacesTest
- AppContainer
- ThemePrefsTest
- ThemeLiteralScanTest
- EditFormScaffoldTest.kt
- AccountRowMapperTest
- Project README
- AccountsFlowTest.kt
- HomeScreenTest.kt
- BundledSymbolsLicenceTest
- AppServicesTest
- setvalue
- LaunchRoutingTest.kt
- PaiseParser
- Icons and emoji (DS3, UI-6)
- Accounts
- SetupScreen.kt
- 6. Implementation steps (TDD-ordered)
- MoneySize
- .append
- .append
- Immutable Audit Trail
- .append
- HostedComposeRule
- AccountsScreenTest.kt
- CategoryColourTest.kt
- CategoryErrorsTest.kt
- Security, privacy and integrity requirements (SEC/INT)

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 221 edges
2. `ThemePrefs` - 186 edges
3. `Paise` - 145 edges
4. `PftSymbol` - 134 edges
5. `runSuspending()` - 125 edges
6. `HostedComposeRule` - 114 edges
7. `How to run and test` - 113 edges
8. `CollapsingHeaderScaffoldTest` - 83 edges
9. `EntityMeta` - 75 edges
10. `Public interface` - 74 edges

## Surprising Connections (you probably didn't know these)
- `3.5 Fonts: bundling, licensing, custom import` --references--> `SafeLog`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/log/SafeLog.kt
- `13. Deviations from the plan, by slice` --references--> `asHeroTop()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderGlow.kt
- `10. Risks & mitigations` --references--> `MoneyText()`  [INFERRED]
  docs/plans/2026-11-10-itemised-transactions.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/MoneyText.kt
- ``StatusChip`` --references--> `StatusChip()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/StatusChip.kt
- `Motion (UI-10)` --references--> `PftNavHost()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/nav/PftNavHost.kt

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

## Communities (186 total, 88 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "Tag"
Cohesion: 0.07
Nodes (16): `data` (Room persistence, A4a), EntityMapper, MetaColumns, MetaMapper, RoomTagRepository, TagEntity, TagMapper, MetaMapperTest (+8 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.25
Nodes (4): Personal Finance Tracker Learnings, graphify tool, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "AuditEntryMapperTest"
Cohesion: 0.17
Nodes (3): AuditEntryEntity, AuditEntryMapper, AuditEntryMapperTest

### Community 9 - "ThemeResolutionTest"
Cohesion: 0.10
Nodes (7): EditFormPreview(), EditFormSample(), dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.11
Nodes (12): Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone(), roles() (+4 more)

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.16
Nodes (22): moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense (+14 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "EntityListScreen.kt"
Cohesion: 0.07
Nodes (13): AccentCheckSection(), EntityListScreen(), EntityRowContent(), EntitySection(), RowMenu(), GroupedRow, GroupedSectionDefaults, GroupedSectionScope (+5 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest.kt"
Cohesion: 0.07
Nodes (6): BottomBarAction, CollapsingHeaderScaffoldTest, Steps, assertShownInFull(), isShownInFull(), layout()

### Community 29 - "MotionSection.kt"
Cohesion: 0.08
Nodes (10): MotionSection(), SpringTrack(), fixedSp(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), GlyphSize, Chip (+2 more)

### Community 30 - "CategoryEntity"
Cohesion: 0.07
Nodes (9): AccountDao, AccountEntity, CategoryDao, CategoryEntity, CategoryMapper, BaseDao, TagDao, RoomAccountMapperTest (+1 more)

### Community 31 - "AccountEditViewModel.kt"
Cohesion: 0.08
Nodes (12): SafeLog, SaveOutcome, FAILED, SAVED, VANISHED, HomeUiState, HomeViewModel, SetupUiState (+4 more)

### Community 32 - "AccountsScreen.kt"
Cohesion: 0.13
Nodes (8): AccountsActions, AccountsNavigation, AccountsRoute(), AccountsScreen(), failureRes(), typeLabelRes(), UndoDeleteSnackbar(), accountRoutes()

### Community 33 - "CollapsingHeaderScaffold"
Cohesion: 0.04
Nodes (48): Manifest security posture (SEC-2, SEC-3, SEC-6), MainActivity, BottomActionBar(), BottomActionBarPreview(), BottomActionBarSamples(), CollapsingHeaderScaffold(), EditFormScaffold(), GroupedSection() (+40 more)

### Community 35 - "DesignCatalog.kt"
Cohesion: 0.07
Nodes (18): Row, PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch(), TokenSheet(), TokenSheetPreview(), TokenSheetPage (+10 more)

### Community 38 - "CategoryGlyph"
Cohesion: 0.10
Nodes (7): CategoryGlyph(), Emoji, GlyphSource, Letter, Symbol, CategoryGlyphTest, GlyphSourceTest

### Community 39 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.19
Nodes (12): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5) (+4 more)

### Community 42 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "AccountType"
Cohesion: 0.13
Nodes (8): AccountEditUiState, AccountEditViewModel, AccountType, BANK, CASH, CREDIT_CARD, WALLET, Fixture

### Community 44 - "PftMotion.kt"
Cohesion: 0.10
Nodes (4): `ui/nav` and `ui/home` (A5f), Routes, PftMotion, pressScale()

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "How to run and test"
Cohesion: 0.12
Nodes (3): MainActivityOpeningTest, SlowOpeningApplication, MainActivityStartupTest

### Community 49 - "CollapsingHeaderScaffold.kt"
Cohesion: 0.05
Nodes (27): CollapsingHeader(), expandedHeaderHeight(), HeaderChildren(), HeaderContent, HeaderPeriod, HeaderPhase, Collapsed, Expanded (+19 more)

### Community 50 - "awaitMain"
Cohesion: 0.20
Nodes (3): awaitMain(), FailsOnAppend, SetupViewModelTest

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.05
Nodes (35): AccountMapper, RoomAccountRepository, AccountsUiState, AuditedTable, AuditedTables, Account, AccountLists, AccountRepository (+27 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.16
Nodes (7): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), StatusChipPreview(), StatusChipSamples(), ContentScreenshotTest

### Community 57 - "MoneyText"
Cohesion: 0.18
Nodes (9): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText(), RollingDigits() (+1 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.10
Nodes (24): Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Personal Finance Tracker PRD, Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Delivery constraints (small vertical slices, Claude Pro limits), Automatic capture from Email (E-0..E-5) (+16 more)

### Community 59 - "UserId"
Cohesion: 0.10
Nodes (6): RoomIdentityRepository, RoomIdentityRepositoryTest, Device, IdentityRepository, UserProfile, InMemoryIdentityRepository

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 62 - "LogEvent"
Cohesion: 0.11
Nodes (20): `log` (safe logging, A5a), DbKeyUnavailable, DbOpened, EntityAction, ARCHIVE, DELETE, RESTORE, UNARCHIVE (+12 more)

### Community 63 - "PftTypography.kt"
Cohesion: 0.15
Nodes (15): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansWeights(), DisplayFont, BUNDLED_SERIF (+7 more)

### Community 66 - "Public interface"
Cohesion: 0.05
Nodes (39): RoomTransactionRunner, RoomAppSettingsRepository, AppServices, Configuration, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...) (+31 more)

### Community 67 - "ThemePalette"
Cohesion: 0.09
Nodes (21): GroupedSectionColors, BackgroundStyle, FLAT, TONAL, PftSurfaces, ThemePalette, AUBERGINE, GRAPHITE (+13 more)

### Community 69 - "ManifestHardeningTest"
Cohesion: 0.05
Nodes (16): ManifestHardeningTest, clockAt(), Uuid7Test, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest, Architecture overview (+8 more)

### Community 72 - "PreMigrationBackupTest"
Cohesion: 0.05
Nodes (3): AuditTriggersTest, PftDatabaseTest, PreMigrationBackupTest

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 76 - "modifier"
Cohesion: 0.15
Nodes (7): SectionContent(), DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 77 - "7. Test plan"
Cohesion: 0.11
Nodes (11): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), colors(), HeaderGlowTest, 1. Requirement (+3 more)

### Community 78 - "GroupedSectionTest.kt"
Cohesion: 0.05
Nodes (5): BottomActionBarSurfaceTest, GroupedSectionTest, NavTransitionTest, PaletteScreenshotTest, PaletteSwatchesTest

### Community 79 - "ReleaseHasNoCatalogueTest.kt"
Cohesion: 0.20
Nodes (4): Debug and release source sets, Public interface, Window theme, ReleaseHasNoCatalogueTest

### Community 80 - "`ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)"
Cohesion: 0.15
Nodes (10): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, AccentHueTest, hueDistance() (+2 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.15
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 82 - "AccountEditScreen.kt"
Cohesion: 0.11
Nodes (7): AccountEditRoute(), AccountEditScreen(), amountLabelRes(), LastFourField(), TypeSelector(), AmountField(), NameField()

### Community 84 - "AppServices"
Cohesion: 0.08
Nodes (36): `di` and startup (A5b), Purpose, AppStart, KeyUnavailable, Ready, IdentityCorrupt, KeyUnavailable, LaunchState (+28 more)

### Community 85 - "MigrationTest"
Cohesion: 0.08
Nodes (4): AuditTriggers, MigrationTestSupport, MigrationTest, Audit triggers

### Community 87 - "CategoryGlyphScreenshotTest"
Cohesion: 0.22
Nodes (6): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), Row, CategoryGlyphScreenshotTest

### Community 89 - "LedgerError"
Cohesion: 0.09
Nodes (37): `ui/common` (shared components, DS3, DS4, DS5 and A5d), `ui/setup` and the UiState convention (A5e), Duplicate, FieldError, InvalidAmount, InvalidLastFour, Required, TooLong (+29 more)

### Community 90 - "HeaderIconButtonTest.kt"
Cohesion: 0.16
Nodes (4): DevHeaderActions(), devRoutes(), HeaderIconButton(), HeaderIconButtonTest

### Community 94 - "PftTheme"
Cohesion: 0.12
Nodes (6): ChoiceRow(), DesignCatalog(), PftTheme(), DesignCatalogTest, 3.9 Scope: app-wide, Step 11 — `NameField` and `EditFormScaffold` (A5d)

### Community 95 - "DbKeyManager"
Cohesion: 0.26
Nodes (4): DbKeyManager, Decoded, WrappedKeyFormat, Migrations on the encrypted file (`EncryptedMigrationTest`)

### Community 97 - "InMemoryEntityRepository"
Cohesion: 0.13
Nodes (6): Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 98 - "AccountEditScreenTest.kt"
Cohesion: 0.18
Nodes (3): AccountEditActions, HeaderTags, AccountEditScreenTest

### Community 99 - "DataUnavailableScreenTest"
Cohesion: 0.14
Nodes (8): Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING, DataUnavailableScreenTest

### Community 100 - "MoneyToneTest"
Cohesion: 0.20
Nodes (3): MoneyToneTest, Paise, 3.5 UI

### Community 101 - "SampleThing.kt"
Cohesion: 0.10
Nodes (9): RoomEntityRepository, RoomEntityRepositoryTest, SampleDatabase, SampleId, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper (+1 more)

### Community 105 - "13. Deviations from the plan, by slice"
Cohesion: 0.18
Nodes (12): sansFamily(), SansFont, FIGTREE, PLUS_JAKARTA_SANS, SYSTEM, toneOf(), WindowFlashToneTest, Third-party assets (+4 more)

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 111 - "r"
Cohesion: 0.11
Nodes (7): DbKeyUnavailable, message(), HomeRoute(), HomeScreen(), DataUnavailableCause, IdentityCorrupt, KeyUnavailable

### Community 112 - "InMemoryKekSource"
Cohesion: 0.13
Nodes (3): AndroidKeystoreKek, KekSource, InMemoryKekSource

### Community 113 - "CatalogSection"
Cohesion: 0.14
Nodes (14): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+6 more)

### Community 114 - ".check"
Cohesion: 0.11
Nodes (3): NameBasedId, idOf(), InMemoryEntityStore

### Community 115 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 117 - "Category"
Cohesion: 0.04
Nodes (37): RoomCategoryRepository, RoomCategoryForeignKeyTest, Category, CategoryColour, AMBER, BLUE, CORAL, EMERALD (+29 more)

### Community 119 - "Plan: Itemised transactions (one payment, many categories)"
Cohesion: 0.07
Nodes (23): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions (+15 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.10
Nodes (13): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+5 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.21
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "shouldbe"
Cohesion: 0.06
Nodes (40): AuditDaoTest, TestDatabase, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE (+32 more)

### Community 125 - "PftSymbolTest"
Cohesion: 0.17
Nodes (5): PftSymbolTest, Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 129 - "runSuspending"
Cohesion: 0.16
Nodes (3): RoomAppSettingsRepositoryTest, AppSetting, InMemoryAppSettingsRepository

### Community 131 - "How to run and test"
Cohesion: 0.11
Nodes (6): How to run and test, IdentityDao, DeviceEntity, UserProfileEntity, PftDatabase, CategoryIconTest

### Community 132 - "AuditFieldsCheck.kt"
Cohesion: 0.13
Nodes (9): AuditFieldsCheck, haveCompleteAuditFields(), 3.1 The model: every transaction has one or more lines, 3.2 Lines are ledger entities, written only by `TransactionService`, 3.4 Reads, 3.6 Configuration (CLAUDE.md §3.5), 3.7 Future items recorded, not built, 3.8 Abstraction bar (+1 more)

### Community 135 - "FieldState"
Cohesion: 0.21
Nodes (4): checkAmount(), FieldCheck, FieldState, FieldStateTest

### Community 138 - ".clock"
Cohesion: 0.18
Nodes (9): Data it owns, EpochMillis, EpochMillisTest, Conventions, Data model, The audit trail, Version 1 (A4a), Version 2 (B1c) (+1 more)

### Community 142 - "How to run and test"
Cohesion: 0.09
Nodes (12): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, AuditDiffTest, Thing, AuditedWriterTest (+4 more)

### Community 143 - "EntityListSamples.kt"
Cohesion: 0.24
Nodes (10): toRow(), EntityListEmptyPreview(), EntityListEmptySample(), EntityListSample(), glyph(), sampleAction(), sampleRow(), EntityRow (+2 more)

### Community 145 - "MoneyColors"
Cohesion: 0.21
Nodes (3): ChipColors, MoneyColors, ChipColorsTest

### Community 147 - ".format"
Cohesion: 0.16
Nodes (11): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+3 more)

### Community 148 - "7. Test plan"
Cohesion: 0.32
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 154 - "PftDatabase"
Cohesion: 0.10
Nodes (15): `security` and the encrypted open (A4b), DatabaseFactory, PreMigrationBackup, PreMigrationBackupFailed, open(), Corruption handling, Persistence and security, Pre-migration backup (INT-5) (+7 more)

### Community 159 - "AppContainer"
Cohesion: 0.10
Nodes (8): DatabaseStartup, Ready, AppContainer, RoomTestKit, PftRootTest, Call flow example: create an account (B2b), Call flow example: first-run Setup, Startup

### Community 161 - "ThemeLiteralScanTest"
Cohesion: 0.13
Nodes (9): `:app` — Android application, Configuration, Dependencies, Responsibilities and boundaries, LoggingScanTest, SourceScan, Violation, ThemeLiteralScanTest (+1 more)

### Community 163 - "AccountRowMapperTest"
Cohesion: 0.33
Nodes (3): AccountBadge, AccountRowMapper, AccountRowMapperTest

### Community 164 - "Project README"
Cohesion: 0.11
Nodes (15): Code Structure Map, core/shared Shared Packages, Categories and tags, Rules the app already enforces, Tags, The categories you start with, What a category is, Data protection: encrypted storage and the "can't open your data" screen (+7 more)

### Community 172 - "Icons and emoji (DS3, UI-6)"
Cohesion: 0.43
Nodes (3): CategoryColors, CategoryPalette, pair()

### Community 173 - "Accounts"
Cohesion: 0.15
Nodes (12): Accounts, Adding and editing an account, Archive or delete?, Configuration, If saving fails, If something goes wrong, If the account is gone, Privacy (+4 more)

### Community 174 - "SetupScreen.kt"
Cohesion: 0.17
Nodes (12): SetupRoute(), SetupScreen(), 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema (+4 more)

### Community 175 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.29
Nodes (7): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice I — Cashew import (`feat/m1a-cashew-import`): tracking stub

### Community 176 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 183 - "HostedComposeRule"
Cohesion: 0.17
Nodes (5): PftApplication, HostedComposeRule, RegisterHostActivity, Statement, Step 8 — `PftRoot`, the generalised fail-closed screen and `MainActivity` (A5b)

### Community 192 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

## Knowledge Gaps
- **226 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+221 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 691 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **88 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `Tag`, `RoomTagIntegrationTest`, `AuditEntryMapperTest`, `ThemeResolutionTest`, `Paise`, `ColorContrastTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest.kt`, `MotionSection.kt`, `CategoryEntity`, `CollapsingHeaderScaffold`, `DbKeyManagerJvmTest`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `assertequals`, `LaunchViewModelTest.kt`, `assertisdisplayed`, `How to run and test`, `CollapsingHeaderScaffold.kt`, `awaitMain`, `PftThemeModeTest`, `RoomAuditColumnsTest`, `EntityMeta`, `ContentScreenshotTest`, `DbKeyManagerTest`, `MoneyText`, `UserId`, `Public interface`, `ThemePalette`, `ManifestHardeningTest`, `PreMigrationBackupTest`, `AppContainerTest.kt`, `RoomCategoryIntegrationTest`, `modifier`, `GroupedSectionTest.kt`, `ReleaseHasNoCatalogueTest.kt`, ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`, `RoomAccountIntegrationTest`, `AppServices`, `MigrationTest`, `RoomAuditedWriterIntegrationTest`, `CategoryGlyphScreenshotTest`, `StatusChipTest.kt`, `EncryptedMigrationTest`, `PftTheme`, `DataUnavailableScreenTest`, `MoneyToneTest`, `SampleThing.kt`, `IdentityRoomIntegrationTest`, `SetupFlowTest.kt`, `13. Deviations from the plan, by slice`, `SetupScreenTest.kt`, `BundledFontTest`, `BottomActionBarTest.kt`, `.check`, `LogEventShapeTest`, `Category`, `DatabaseStartupTest.kt`, `WindowThemeTest`, `shouldbe`, `PftSymbolTest`, `RoomTransactionRunnerTest`, `NameFieldTest.kt`, `FormScreenshotTest.kt`, `runSuspending`, `FieldState`, `WindowBackgroundTest.kt`, `DevRouteTest.kt`, `AccountsViewModelTest.kt`, `DbKeyManagerWiringTest.kt`, `SetupScreenshotTest.kt`, `MoneyColors`, `.format`, `7. Test plan`, `SafeLogTest`, `.clock`, `SetupRouteTest.kt`, `ThemePaletteTest`, `PftDatabase`, `assertequals`, `PftSpacingTest`, `PftSurfacesTest`, `AppContainer`, `ThemePrefsTest`, `ThemeLiteralScanTest`, `EditFormScaffoldTest.kt`, `HomeScreenTest.kt`, `BundledSymbolsLicenceTest`, `AppServicesTest`, `setvalue`, `LaunchRoutingTest.kt`?**
  _High betweenness centrality (0.229) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _226 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `FormScreenshotTest.kt`, `WindowBackgroundTest.kt`, `ThemeResolutionTest`, `Paise`, `DevRouteTest.kt`, `UndoDeleteSnackbarTest.kt`, `ColorContrastTest`, `EntityListSamples.kt`, `SetupScreenshotTest.kt`, `MoneyColors`, `surface`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `SetupRouteTest.kt`, `EntityListScreen.kt`, `AmountFieldTest`, `CollapsingHeaderScaffoldTest.kt`, `MotionSection.kt`, `PftSpacingTest`, `AppContainer`, `CollapsingHeaderScaffold`, `EditFormScaffoldTest.kt`, `DesignCatalog.kt`, `HeaderGlowScaffoldTest.kt`, `AccountsFlowTest.kt`, `CategoryGlyph`, `HomeScreenTest.kt`, `PftTypographyTest`, `setvalue`, `LaunchRoutingTest.kt`, `Icons and emoji (DS3, UI-6)`, `PftMotion.kt`, `SetupScreen.kt`, `assertisdisplayed`, `HeaderTypePairingTest.kt`, `CollapsingHeaderScaffold.kt`, `PftThemeModeTest`, `ContentScreenshotTest`, `AccountsScreenTest.kt`, `MoneyText`, `PftTypography.kt`, `EntityListScreenTest.kt`, `ThemePalette`, `MoneyTextTest.kt`, `ManifestHardeningTest`, `modifier`, `7. Test plan`, `GroupedSectionTest.kt`, `ReleaseHasNoCatalogueTest.kt`, ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`, `Plan: M1a design system and visual direction (lands before A5)`, `AccountEditScreen.kt`, `AppServices`, `CategoryGlyphScreenshotTest`, `HeaderIconButtonTest.kt`, `StatusChipTest.kt`, `onnodewithtext`, `AccountEditScreenTest.kt`, `DataUnavailableScreenTest`, `SetupFlowTest.kt`, `13. Deviations from the plan, by slice`, `SetupScreenTest.kt`, `BottomActionBarTest.kt`, `AccountsRouteTest.kt`, `NameFieldTest.kt`?**
  _High betweenness centrality (0.093) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._