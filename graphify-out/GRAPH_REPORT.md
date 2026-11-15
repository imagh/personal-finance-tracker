# Graph Report - personal-finance-tracker  (2026-11-14)

## Corpus Check
- 377 files · ~277,994 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 4396 nodes · 13457 edges · 201 communities (94 shown, 107 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 2009 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `8f5e321c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- DbKeyUnavailable
- Personal Finance Tracker Learnings
- Collecting and Redacting Sample Messages Guide
- MoneyTextTest.kt
- hooks
- test_import_symbols.py
- AuditEntryMapperTest
- ThemePrefs
- MoneyTextTest
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
- AppServicesTest
- SafeLogTest
- AccountsViewModel.kt
- GroupedSection.kt
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- HeaderGlowScaffoldTest.kt
- PftTypographyTest
- CategoryGlyph
- PaletteSamples.kt
- GlyphSourceTest
- LaunchViewModelTest.kt
- Layered Modular Architecture (core vs app)
- AccountId
- PftMotion.kt
- HeaderTypePairingTest.kt
- PreMigrationBackup
- CLAUDE.md Development Constitution
- PftRootTest.kt
- CategoryGlyphScreenshotTest
- EntityRepository
- FontBudgetTest
- PftThemeModeTest
- RoomAuditColumnsTest
- EntityMeta
- ContentScreenshotTest
- DbKeyManagerTest.kt
- MoneyText
- Loans and EMIs (LN-1..LN-5)
- UserProfile
- redact_text
- AccountsViewModelTest
- ThemePaletteTest
- 13. Deviations from the plan, by slice
- Pseudo
- EntityListScreenTest.kt
- Public interface
- DesignCatalog.kt
- SetupScreenTest.kt
- SetupScreenTest.kt
- _Text
- PftDatabaseTest
- import_symbols.py
- AppContainerTest.kt
- RoomCategoryIntegrationTest
- Previews, the token sheet and screenshot goldens
- asserttrue
- PftSymbolTest
- ReleaseHasNoCatalogueTest.kt
- assertequals
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- RoomAccountIntegrationTest
- How to run and test
- MigrationTest
- RoomTestKit
- PreMigrationBackupTest
- TabularFiguresTest
- LedgerError
- HeaderIconButton
- NavTransitionTest.kt
- StatusChipTest.kt
- GroupedSectionTest
- PftTheme
- DbKeyManager
- GroupedSection
- LedgerLimits
- DbKeyUnavailable
- Paise
- SampleThing.kt
- runSuspending
- PftDatabase.kt
- SetupFlowTest.kt
- TestClock
- parse_svg
- onnodewithtext
- BundledFontTest
- BottomActionBarTest.kt
- ImportRunTest
- FieldState
- InMemoryKekSource
- CatalogSection
- localdensity
- HostedComposeRule.kt
- LogEventShapeTest
- Category
- DatabaseStartupTest.kt
- 2. Current state
- EntityListSamples.kt
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- shouldbe
- AccountsRouteTest.kt
- MigrationTestSupport
- RoomTransactionRunnerTest
- awaitMain
- FormScreenshotTest.kt
- AppSetting
- EntityListScreenshotTest.kt
- PaiseParser
- AccountDao
- EmptyStateTest.kt
- AuditEntryEntity
- 7. Test plan
- ManifestHardeningTest
- DevRouteTest.kt
- CategoryIcon
- `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)
- DbKeyManagerWiringTest.kt
- UndoDeleteSnackbarTest.kt
- Uuid7Test.kt
- ColorSchemeTestSupport.kt
- FieldStateTest.kt
- BottomActionBar.kt
- Paise
- CategoryEntity
- MoneySize
- AuditTriggers
- PftSurfacesTest
- HomeViewModelTest.kt
- SetupRouteTest.kt
- RoomCategoryForeignKeyTest.kt
- DatabaseFactory
- BottomActionBarSurfaceTest.kt
- LogEventTest
- `ui/more` and `ui/accounts` (B2a)
- PaletteScreenshotTest.kt
- FieldStateTest
- LoggingScanTest
- EditFormScaffoldTest.kt
- AccountRowMapperTest
- Project README
- AccountsFlowTest.kt
- HomeScreenTest.kt
- BundledSymbolsLicenceTest
- .append
- FieldErrorMessageTest.kt
- MoreScreenTest.kt
- PaletteSwatchesTest.kt
- CategoryPalette
- Accounts
- Slice A — Foundation (`feat/m1a-foundation`)
- InMemoryEntityRepository
- .clock
- AccountsScreen.kt
- CollapsingHeaderScaffold
- Immutable Audit Trail
- rowscope
- Design system
- HeaderPolicy
- LaunchRoutingTest.kt
- AccountsScreenTest.kt
- AuditDaoTest.kt
- CategoryColourTest.kt
- CategoryErrorsTest.kt
- PftSpacingTest
- ThemePrefsTest
- Appearance: colour palettes, dynamic colour, background and type
- Personal Finance Tracker PRD
- PftDatabase
- HeaderSlot
- expandedHeaderHeight
- .check
- EntityListSamples.kt
- .fromIconKey
- .append

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 212 edges
2. `ThemePrefs` - 179 edges
3. `PftSymbol` - 134 edges
4. `Paise` - 121 edges
5. `How to run and test` - 112 edges
6. `runSuspending()` - 109 edges
7. `HostedComposeRule` - 108 edges
8. `CollapsingHeaderScaffoldTest` - 83 edges
9. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 72 edges
10. `EntityMeta` - 70 edges

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

## Communities (201 total, 107 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "DbKeyUnavailable"
Cohesion: 0.12
Nodes (17): `log` (safe logging, A5a), DbKeyUnavailable, DbOpened, EntityAction, ARCHIVE, DELETE, RESTORE, UNARCHIVE (+9 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.17
Nodes (13): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Parse log (S-11), Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+5 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 9 - "ThemePrefs"
Cohesion: 0.09
Nodes (6): EditFormPreview(), dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.19
Nodes (8): Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone(), sweptTones()

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.16
Nodes (22): moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense (+14 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "EntityListScreen.kt"
Cohesion: 0.06
Nodes (10): DevHeaderActions(), devRoutes(), MotionSection(), SpringTrack(), fixedSp(), MoreScreen(), GlyphSize, Chip (+2 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest.kt"
Cohesion: 0.08
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "AppServicesTest"
Cohesion: 0.16
Nodes (3): How to run and test, AppServicesTest, CategoryIconTest

### Community 33 - "GroupedSection.kt"
Cohesion: 0.13
Nodes (7): GroupedRow, GroupedSectionColors, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, GroupedSectionPreview(), GroupedSectionSamples()

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "HeaderGlowScaffoldTest.kt"
Cohesion: 0.06
Nodes (4): HeaderGlowScaffoldTest, SetupScreenshotTest, PftShapesTest, PressScaleTest

### Community 39 - "PaletteSamples.kt"
Cohesion: 0.42
Nodes (4): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 42 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "AccountId"
Cohesion: 0.09
Nodes (16): `data` (Room persistence, A4a), AccountEntity, AccountMapper, RoomAccountRepository, AccountsUiState, RoomAccountMapperTest, Account, AccountRepository (+8 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.12
Nodes (3): PftMotion, pressScale(), 11. Rollout & verification

### Community 46 - "PreMigrationBackup"
Cohesion: 0.11
Nodes (15): `security` and the encrypted open (A4b), PreMigrationBackup, PreMigrationBackupFailed, DataUnavailableCause, IdentityCorrupt, KeyUnavailable, Corruption handling, Persistence and security (+7 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "PftRootTest.kt"
Cohesion: 0.07
Nodes (4): MainActivityOpeningTest, MainActivityStartupTest, PftRootTest, DataUnavailableScreenTest

### Community 49 - "CategoryGlyphScreenshotTest"
Cohesion: 0.23
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.13
Nodes (5): Probe, RoomAuditColumnsTest, AuditColumnsCheck, AuditedTable, Step C1c — Room v4 for transactions and lines

### Community 54 - "EntityMeta"
Cohesion: 0.07
Nodes (20): EntityMeta, LedgerEntity, AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind, LARGE (+12 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.21
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "MoneyText"
Cohesion: 0.15
Nodes (13): `:app` — Android application, Configuration, Dependencies, Responsibilities and boundaries, atSize(), directionLabel(), Figure(), fitHero() (+5 more)

### Community 58 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.08
Nodes (29): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Budgets (B-1..B-5), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Automatic capture from Email (E-0..E-5), Entity merge and split (AD-7), Unverified Gmail OAuth client risk (E-5) (+21 more)

### Community 59 - "UserProfile"
Cohesion: 0.07
Nodes (15): RoomIdentityRepository, RoomIdentityRepositoryTest, AuditedTables, Device, IdentityRepository, UserProfile, AuditEntryId, DeviceId (+7 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 63 - "13. Deviations from the plan, by slice"
Cohesion: 0.08
Nodes (35): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansWeights(), DisplayFont, BUNDLED_SERIF (+27 more)

### Community 66 - "Public interface"
Cohesion: 0.07
Nodes (31): RoomAppSettingsRepository, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), AccountLists, AccountService, AuditDiff (+23 more)

### Community 67 - "DesignCatalog.kt"
Cohesion: 0.07
Nodes (9): EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), ThemeMode, DARK, LIGHT, SYSTEM, TokenSheet() (+1 more)

### Community 68 - "SetupScreenTest.kt"
Cohesion: 0.09
Nodes (4): HeaderTags, assertShownInFull(), isShownInFull(), layout()

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 76 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.16
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 77 - "asserttrue"
Cohesion: 0.14
Nodes (9): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 3.4 Header glow (UI-13, DS6d), 3.8 Abstraction bar (+1 more)

### Community 80 - "assertequals"
Cohesion: 0.06
Nodes (22): ChipColors, MoneyColors, PftSpacing, BackgroundStyle, FLAT, TONAL, PftSurfaces, colors() (+14 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.15
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 82 - "UserProfileEntity"
Cohesion: 0.21
Nodes (4): IdentityDao, DeviceEntity, UserProfileEntity, SampleDatabase

### Community 84 - "How to run and test"
Cohesion: 0.08
Nodes (48): `di` and startup (A5b), Purpose, AppContainer, AppServices, AppStart, KeyUnavailable, Ready, SafeLog (+40 more)

### Community 89 - "LedgerError"
Cohesion: 0.10
Nodes (33): `ui/common` (shared components, DS3, DS4, DS5 and A5d), Duplicate, FieldError, InvalidLastFour, Required, TooLong, toFieldError(), Behaviours worth knowing (+25 more)

### Community 92 - "StatusChipTest.kt"
Cohesion: 0.21
Nodes (6): StatusChip(), StatusChipTest, Content primitives (DS5), `EmptyState`, `GroupedSection`, `StatusChip`

### Community 94 - "PftTheme"
Cohesion: 0.19
Nodes (4): ChoiceRow(), DesignCatalog(), PftTheme(), DesignCatalogTest

### Community 95 - "DbKeyManager"
Cohesion: 0.26
Nodes (4): DbKeyManager, Decoded, WrappedKeyFormat, Migrations on the encrypted file (`EncryptedMigrationTest`)

### Community 96 - "GroupedSection"
Cohesion: 0.17
Nodes (18): Debug and release source sets, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, `ui/nav` and `ui/home` (A5f), Window theme, MainActivity, BottomActionBar(), GroupedSection() (+10 more)

### Community 98 - "LedgerLimits"
Cohesion: 0.09
Nodes (21): Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, Purpose, validated(), LedgerLimits, 10. Risks & mitigations (+13 more)

### Community 99 - "DbKeyUnavailable"
Cohesion: 0.18
Nodes (8): DbKeyUnavailable, Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING

### Community 100 - "Paise"
Cohesion: 0.16
Nodes (4): MoneyToneTest, Paise, Slice D — Budgets (`feat/m1a-budgets`), 3.5 UI

### Community 101 - "SampleThing.kt"
Cohesion: 0.11
Nodes (8): RoomEntityRepository, RoomEntityRepositoryTest, SampleId, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 102 - "runSuspending"
Cohesion: 0.18
Nodes (4): FailingOnNthAppend, IdentityRoomIntegrationTest, runSuspending(), 3.3 Dependencies (one pins-only PR, A5c)

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 111 - "FieldState"
Cohesion: 0.06
Nodes (18): `ui/setup` and the UiState convention (A5e), EditFormScaffold(), message(), FieldCheck, FieldState, EditFormSample(), NameField(), HomeRoute() (+10 more)

### Community 113 - "CatalogSection"
Cohesion: 0.10
Nodes (18): AccentCheckSection(), CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS (+10 more)

### Community 117 - "Category"
Cohesion: 0.06
Nodes (32): RoomCategoryRepository, Category, CategoryColour, AMBER, BLUE, CORAL, EMERALD, INDIGO (+24 more)

### Community 118 - "DatabaseStartupTest.kt"
Cohesion: 0.19
Nodes (4): DatabaseStartup, KeyUnavailable, Ready, DatabaseStartupTest

### Community 119 - "2. Current state"
Cohesion: 0.08
Nodes (25): AuditFieldsCheck, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 3.1 The model: every transaction has one or more lines, 3.2 Lines are ledger entities, written only by `TransactionService` (+17 more)

### Community 120 - "EntityListSamples.kt"
Cohesion: 0.26
Nodes (10): toRow(), sampleAction(), EntityListScreen(), EntityRowContent(), EntitySection(), RowMenu(), EntityRow, RowGlyph (+2 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.09
Nodes (13): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+5 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.23
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "shouldbe"
Cohesion: 0.06
Nodes (36): TestDatabase, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE, UNARCHIVE (+28 more)

### Community 124 - "AccountsRouteTest.kt"
Cohesion: 0.14
Nodes (3): RoomAuditLog, AccountsRouteTest, FailsOnDemand

### Community 127 - "awaitMain"
Cohesion: 0.27
Nodes (3): awaitMain(), FailsOnAppend, SetupViewModelTest

### Community 135 - "7. Test plan"
Cohesion: 0.30
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 136 - "ManifestHardeningTest"
Cohesion: 0.05
Nodes (12): SyncWindowBackground(), ManifestHardeningTest, WindowBackgroundTest, WindowThemeTest, 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold` (+4 more)

### Community 138 - "CategoryIcon"
Cohesion: 0.10
Nodes (9): CategoryMapper, EntityMapper, MetaColumns, MetaMapper, MetaMapperTest, RoomCategoryMapperTest, CategoryIcon, Emoji (+1 more)

### Community 139 - "`ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)"
Cohesion: 0.24
Nodes (7): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, Colour

### Community 142 - "Uuid7Test.kt"
Cohesion: 0.09
Nodes (9): How to run and test, IdsTest, NameBasedIdTest, clockAt(), Uuid7Test, How to run and test, PaiseFormatTest, PaiseParserTest (+1 more)

### Community 143 - "ColorSchemeTestSupport.kt"
Cohesion: 0.13
Nodes (8): AccentHueTest, hueDistance(), labHue(), roles(), surfaceRoles(), toneOf(), washTop(), WindowFlashToneTest

### Community 146 - "Paise"
Cohesion: 0.18
Nodes (10): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+2 more)

### Community 147 - "CategoryEntity"
Cohesion: 0.16
Nodes (3): CategoryDao, CategoryEntity, BaseDao

### Community 148 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 149 - "AuditTriggers"
Cohesion: 0.39
Nodes (3): AuditTriggers, The audit trail, Audit triggers

### Community 153 - "RoomCategoryForeignKeyTest.kt"
Cohesion: 0.10
Nodes (9): RoomCategoryForeignKeyTest, NameBasedId, idOf(), Conventions, Data model, Version 1 (A4a), Version 2 (B1c), Version 3 (B3b) (+1 more)

### Community 154 - "DatabaseFactory"
Cohesion: 0.13
Nodes (3): EncryptedMigrationTest, DatabaseFactory, open()

### Community 161 - "LoggingScanTest"
Cohesion: 0.29
Nodes (3): LoggingScanTest, SourceScan, Violation

### Community 163 - "AccountRowMapperTest"
Cohesion: 0.33
Nodes (3): AccountBadge, AccountRowMapper, AccountRowMapperTest

### Community 164 - "Project README"
Cohesion: 0.11
Nodes (15): Code Structure Map, core/shared Shared Packages, Categories and tags, Rules the app already enforces, Tags, The categories you start with, What a category is, Data protection: encrypted storage and the "can't open your data" screen (+7 more)

### Community 172 - "CategoryPalette"
Cohesion: 0.27
Nodes (7): CategoryColors, CategoryPalette, pair(), Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 173 - "Accounts"
Cohesion: 0.22
Nodes (8): Accounts, Archive or delete?, Configuration, If something goes wrong, Privacy, The accounts list, Undo, What an account is

### Community 174 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.09
Nodes (17): RoomTransactionRunner, AuditDiffTest, Thing, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape (+9 more)

### Community 175 - "InMemoryEntityRepository"
Cohesion: 0.13
Nodes (6): Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 176 - ".clock"
Cohesion: 0.40
Nodes (3): Data it owns, EpochMillis, EpochMillisTest

### Community 177 - "AccountsScreen.kt"
Cohesion: 0.20
Nodes (5): AccountsRoute(), AccountsScreen(), failureRes(), typeLabelRes(), UndoDeleteSnackbar()

### Community 178 - "CollapsingHeaderScaffold"
Cohesion: 0.25
Nodes (15): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle, Dashboard (+7 more)

### Community 181 - "Design system"
Cohesion: 0.16
Nodes (11): ThemeLiteralScanTest, Adding a token, Build-time trust decisions, Design system, Motion (UI-10), Planned: premium look (DS6), Principles, Shapes (+3 more)

### Community 191 - "Appearance: colour palettes, dynamic colour, background and type"
Cohesion: 0.18
Nodes (5): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you

### Community 192 - "Personal Finance Tracker PRD"
Cohesion: 0.16
Nodes (14): Personal Finance Tracker PRD, Audit trail (INT-2), Balance observations (L-10), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Milestone 2 (shared entities, P2P sync, balances from email), Milestone 3 (insights) (+6 more)

### Community 194 - "HeaderSlot"
Cohesion: 0.16
Nodes (11): HeaderPhase, Collapsed, Expanded, Moving, HeaderSlot, Actions, Caption, Hero (+3 more)

### Community 198 - "EntityListSamples.kt"
Cohesion: 0.50
Nodes (5): EntityListEmptyPreview(), EntityListEmptySample(), EntityListSample(), glyph(), sampleRow()

### Community 199 - ".fromIconKey"
Cohesion: 0.39
Nodes (4): Emoji, GlyphSource, Letter, Symbol

## Knowledge Gaps
- **223 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+218 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 673 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **107 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `AppServicesTest` to `AuditEntryMapperTest`, `ThemePrefs`, `MoneyTextTest`, `ColorContrastTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest.kt`, `SafeLogTest`, `DbKeyManagerJvmTest`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `GlyphSourceTest`, `LaunchViewModelTest.kt`, `AccountId`, `PftRootTest.kt`, `CategoryGlyphScreenshotTest`, `PftThemeModeTest`, `RoomAuditColumnsTest`, `ContentScreenshotTest`, `DbKeyManagerTest.kt`, `MoneyText`, `UserProfile`, `ThemePaletteTest`, `Public interface`, `SetupScreenTest.kt`, `AppContainerTest.kt`, `RoomCategoryIntegrationTest`, `Previews, the token sheet and screenshot goldens`, `PftSymbolTest`, `ReleaseHasNoCatalogueTest.kt`, `assertequals`, `RoomAccountIntegrationTest`, `How to run and test`, `MigrationTest`, `RoomTestKit`, `PreMigrationBackupTest`, `NavTransitionTest.kt`, `StatusChipTest.kt`, `GroupedSectionTest`, `PftTheme`, `GroupedSection`, `Paise`, `SampleThing.kt`, `runSuspending`, `SetupFlowTest.kt`, `onnodewithtext`, `BundledFontTest`, `BottomActionBarTest.kt`, `localdensity`, `LogEventShapeTest`, `DatabaseStartupTest.kt`, `AccountsRouteTest.kt`, `MigrationTestSupport`, `RoomTransactionRunnerTest`, `awaitMain`, `FormScreenshotTest.kt`, `AppSetting`, `EmptyStateTest.kt`, `7. Test plan`, `ManifestHardeningTest`, `DevRouteTest.kt`, `CategoryIcon`, `DbKeyManagerWiringTest.kt`, `ColorSchemeTestSupport.kt`, `FieldStateTest.kt`, `Paise`, `PftSurfacesTest`, `HomeViewModelTest.kt`, `SetupRouteTest.kt`, `RoomCategoryForeignKeyTest.kt`, `DatabaseFactory`, `BottomActionBarSurfaceTest.kt`, `LogEventTest`, `PaletteScreenshotTest.kt`, `FieldStateTest`, `LoggingScanTest`, `EditFormScaffoldTest.kt`, `HomeScreenTest.kt`, `BundledSymbolsLicenceTest`, `FieldErrorMessageTest.kt`, `LaunchRoutingTest.kt`, `AuditDaoTest.kt`, `PftSpacingTest`, `ThemePrefsTest`, `PftDatabase`, `expandedHeaderHeight`, `.check`?**
  _High betweenness centrality (0.221) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _223 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `FormScreenshotTest.kt`, `EntityListScreenshotTest.kt`, `EmptyStateTest.kt`, `MoneyTextTest.kt`, `ManifestHardeningTest`, `ThemePrefs`, `MoneyTextTest`, ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`, `DevRouteTest.kt`, `UndoDeleteSnackbarTest.kt`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `BottomActionBar.kt`, `SetupRouteTest.kt`, `EntityListScreen.kt`, `BottomActionBarSurfaceTest.kt`, `CollapsingHeaderScaffoldTest.kt`, `PaletteScreenshotTest.kt`, `CollapsingHeaderScaffold.kt`, `GroupedSection.kt`, `EditFormScaffoldTest.kt`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `PaletteSamples.kt`, `HomeScreenTest.kt`, `FieldErrorMessageTest.kt`, `MoreScreenTest.kt`, `AccountsFlowTest.kt`, `CategoryPalette`, `PftMotion.kt`, `HeaderTypePairingTest.kt`, `PaletteSwatchesTest.kt`, `PftRootTest.kt`, `CategoryGlyphScreenshotTest`, `Slice A — Foundation (`feat/m1a-foundation`)`, `CollapsingHeaderScaffold`, `PftThemeModeTest`, `Design system`, `ContentScreenshotTest`, `AccountsScreenTest.kt`, `MoneyText`, `GroupedSectionTest.kt`, `LaunchRoutingTest.kt`, `PftSpacingTest`, `13. Deviations from the plan, by slice`, `EntityListScreenTest.kt`, `DesignCatalog.kt`, `SetupScreenTest.kt`, `SetupScreenTest.kt`, `EntityListSamples.kt`, `Previews, the token sheet and screenshot goldens`, `asserttrue`, `ReleaseHasNoCatalogueTest.kt`, `assertequals`, `Plan: M1a design system and visual direction (lands before A5)`, `How to run and test`, `HeaderIconButton`, `StatusChipTest.kt`, `GroupedSectionTest`, `GroupedSection`, `SetupFlowTest.kt`, `onnodewithtext`, `BottomActionBarTest.kt`, `FieldState`, `CatalogSection`, `localdensity`, `AccountsRouteTest.kt`?**
  _High betweenness centrality (0.104) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._