# Graph Report - personal-finance-tracker  (2026-11-16)

## Corpus Check
- 419 files · ~307,206 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 4913 nodes · 15511 edges · 210 communities (92 shown, 118 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 2121 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `7fdb9239`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- Tag
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- runSuspending
- hooks
- test_import_symbols.py
- CategoryColour
- ThemeResolutionTest
- Paise
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- HeaderGlowScaffoldTest.kt
- PftKotlinCorePlugin.kt
- CategoriesActionsTest
- Catalog.kt
- detekt.yml
- EntityListScreen.kt
- PftSymbol
- CollapsingHeaderScaffoldTest
- PaletteSamples.kt
- CategoryColour
- LogEvent
- CollapsingHeaderScaffold.kt
- CollapsingHeaderScaffold
- DbKeyManagerJvmTest
- DesignCatalog.kt
- PressScaleTest.kt
- PftTypographyTest
- CategoryGlyph
- PreMigrationBackupTest
- assertnotequals
- LaunchViewModelTest.kt
- Layered Modular Architecture (core vs app)
- AccountId
- PftMotion.kt
- HeaderTypePairingTest
- CategoriesFlowTest
- CLAUDE.md Development Constitution
- MainActivityStartupTest.kt
- CategoryGlyphScreenshotTest
- PftSymbolTest
- FontBudgetTest
- PftThemeModeTest
- RoomAuditColumnsTest
- EntityMeta
- ContentScreenshotTest
- DbKeyManagerTest.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Personal Finance Tracker PRD
- UserId
- redact_text
- AccountsViewModelTest
- ThemePaletteTest
- 13. Deviations from the plan, by slice
- Pseudo
- EntityListScreenTest
- Public interface
- 6.1 Slices
- SetupScreenTest.kt
- PaiseParserTest.kt
- _Text
- AuditTriggersTest
- import_symbols.py
- AppContainerTest.kt
- RoomCategoryIntegrationTest
- Previews, the token sheet and screenshot goldens
- 7. Test plan
- GroupedSectionTest.kt
- CategoriesScreenTest.kt
- assertequals
- ManifestHardeningTest
- AccountEditScreen.kt
- RoomAccountIntegrationTest
- `di` and startup (A5b)
- MigrationTest
- RoomAuditedWriterIntegrationTest
- PreMigrationBackupTest
- TabularFiguresTest
- LedgerError
- HeaderIconButton
- CategoriesActionsTest
- StatusChip
- CategoriesFlowTest
- PftTheme
- PftDatabase.kt
- CategoriesViewModelTest
- AuditDiffTest.kt
- DataUnavailableScreenTest
- MoneyToneTest
- SampleThing.kt
- RoomTestKit
- GroupedSection.kt
- SetupFlowTest.kt
- `ui/categories` (B4a)
- parse_svg
- SetupScreenTest
- BundledFontTest
- BottomActionBarTest
- ImportRunTest
- remember
- InMemoryKekSource
- CatalogSection
- .check
- Collecting and Redacting Sample Messages Guide
- StatusChip
- Category
- AppContainerTest.kt
- Appearance: colour palettes, dynamic colour, background and type
- BackgroundStyle
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- shouldbe
- TestClock
- Project README
- RoomTransactionRunnerTest
- `ui/categories` (B4a)
- PaletteScreenshotTest.kt
- AppSetting
- CategoryIcon
- How to run and test
- NameFieldTest
- InMemoryEntityRepository
- AuditTriggersTest
- AuditEntryEntity
- WindowThemeTest
- DevRouteTest.kt
- AppSetting
- TestDatabase
- DbKeyManagerWiringTest.kt
- UndoDeleteSnackbarTest
- TestClock
- RowAction
- PftMotionTest
- ThemePalette
- CategoryIcon
- Paise
- PftSpacingTest
- .onCreate
- MoneyTextTest.kt
- ThemePrefsTest
- How to run and test
- FieldState
- Slice A — Foundation (`feat/m1a-foundation`)
- PftSurfacesTest
- LogEventShapeTest
- .idOf
- CategoryIconGlyphTest
- PftRootTest.kt
- ReleaseHasNoCatalogueTest.kt
- LoggingScanTest
- EditFormScaffoldTest
- AccountsScreen.kt
- Project README
- AccountsFlowTest.kt
- HomeScreenTest.kt
- BundledSymbolsLicenceTest
- .append
- Icons and emoji (DS3, UI-6)
- CategoriesSamples.kt
- onnodewithtag
- CategoryRowMapperTest.kt
- Accounts
- Plan: M1a — Core ledger (manual-entry household tracker)
- TokenSheetSections.kt
- ThemeLiteralScanTest
- 6. Implementation steps (TDD-ordered)
- Appearance: colour palettes, dynamic colour, background and type
- Plan: Itemised transactions (one payment, many categories)
- PftNavHost.kt
- NavTransitionTest.kt
- MoneySize
- AccountsScreenTest.kt
- testtag
- CategoriesScreen.kt
- CategoryColourTest.kt
- CategoryErrorsTest.kt
- HeaderPolicy
- EntityAction
- BottomActionBarSurfaceTest.kt
- MoreScreenTest.kt
- RedactTests
- IdentityDao
- RoomCategoryForeignKeyTest.kt
- PaletteScreenshotTest.kt
- expandedHeaderHeight
- CategoryIconGlyphTest
- PaletteSwatchesTest.kt
- GlyphSource
- EntityListSamples.kt
- Where it lives
- AccountEditViewModel
- HeaderSlot
- parse_manifest
- Migrations.kt
- .append

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 231 edges
2. `ThemePrefs` - 195 edges
3. `Paise` - 145 edges
4. `PftSymbol` - 137 edges
5. `runSuspending()` - 125 edges
6. `HostedComposeRule` - 120 edges
7. `How to run and test` - 113 edges
8. `awaitMain()` - 99 edges
9. `CollapsingHeaderScaffoldTest` - 83 edges
10. `EntityMeta` - 81 edges

## Surprising Connections (you probably didn't know these)
- `3.5 Fonts: bundling, licensing, custom import` --references--> `SafeLog`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/log/SafeLog.kt
- `13. Deviations from the plan, by slice` --references--> `asHeroTop()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderGlow.kt
- `10. Risks & mitigations` --references--> `MoneyText()`  [INFERRED]
  docs/plans/2026-11-10-itemised-transactions.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/MoneyText.kt
- ``StatusChip`` --references--> `StatusChip()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/StatusChip.kt
- `11. Rollout & verification` --references--> `PftMotion`  [INFERRED]
  docs/plans/2026-11-01-m1a-a5-first-run-setup.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftMotion.kt

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

## Communities (210 total, 118 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "Tag"
Cohesion: 0.08
Nodes (17): `data` (Room persistence, A4a), CategoryMapper, EntityMapper, RoomEntityRepository, RoomTagRepository, TagEntity, TagMapper, TagId (+9 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "CategoryColour"
Cohesion: 0.08
Nodes (18): CategoryColourPaletteTest, CategoryColour, AMBER, BLUE, CORAL, EMERALD, INDIGO, MAGENTA (+10 more)

### Community 9 - "ThemeResolutionTest"
Cohesion: 0.11
Nodes (5): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "HeaderGlowScaffoldTest.kt"
Cohesion: 0.06
Nodes (19): HeaderGlowScaffoldTest, AccentHueTest, Case, ColorContrastTest, Theme, DARK, LIGHT, contrast() (+11 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "EntityListScreen.kt"
Cohesion: 0.04
Nodes (21): AccentCheckSection(), DevHeaderActions(), devRoutes(), MotionSection(), SpringTrack(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder() (+13 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (94): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+86 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.08
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "PaletteSamples.kt"
Cohesion: 0.13
Nodes (7): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch(), PftShapes, PftShapesTest, Adding a token

### Community 30 - "CategoryColour"
Cohesion: 0.08
Nodes (6): AccountDao, CategoryDao, CategoryEntity, BaseDao, TagDao, RoomCategoryMapperTest

### Community 31 - "LogEvent"
Cohesion: 0.08
Nodes (14): SaveOutcome, FAILED, SAVED, VANISHED, HomeUiState, HomeViewModel, Routes, SetupRoute() (+6 more)

### Community 32 - "CollapsingHeaderScaffold.kt"
Cohesion: 0.19
Nodes (6): AccountsActions, AccountsNavigation, AccountsRoute(), AccountsScreen(), typeLabelRes(), accountRoutes()

### Community 33 - "CollapsingHeaderScaffold"
Cohesion: 0.06
Nodes (50): Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, `ui/more` and `ui/accounts` (B2a, B2b), `ui/nav` and `ui/home` (A5f), `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), Window theme, MainActivity, AmountField() (+42 more)

### Community 35 - "DesignCatalog.kt"
Cohesion: 0.06
Nodes (12): Debug and release source sets, ChoiceRow(), BottomActionBarPreview(), CategoryGlyphPreview(), CategoryGlyphSamples(), SampleRow(), GroupedSectionPreview(), GroupedSectionSamples() (+4 more)

### Community 38 - "CategoryGlyph"
Cohesion: 0.09
Nodes (7): CategoryGlyph(), fixedSp(), GlyphSize, Chip, Hero, CategoryGlyphTest, GlyphSourceTest

### Community 42 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "AccountId"
Cohesion: 0.07
Nodes (22): AccountEntity, AccountMapper, RoomAccountRepository, AccountsUiState, RoomAccountMapperTest, Account, AccountLists, AccountRepository (+14 more)

### Community 45 - "HeaderTypePairingTest"
Cohesion: 0.12
Nodes (4): HeaderTypePairingTest, assertShownInFull(), isShownInFull(), layout()

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 49 - "CategoryGlyphScreenshotTest"
Cohesion: 0.09
Nodes (17): CollapsingHeader(), HeaderChildren(), HeaderContent, HeaderPeriod, HeaderPhase, Collapsed, Expanded, Moving (+9 more)

### Community 50 - "PftSymbolTest"
Cohesion: 0.20
Nodes (3): awaitMain(), FailsOnAppend, SetupViewModelTest

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.06
Nodes (25): EntityMeta, LedgerEntity, EntityId, Fixture, Leaky, LeakyRepository, Note, NoteId (+17 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.21
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.07
Nodes (40): `:app` — Android application, Configuration, Dependencies, Responsibilities and boundaries, atSize(), directionLabel(), Figure(), fitHero() (+32 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.07
Nodes (38): Personal Finance Tracker PRD, Audit trail (INT-2), Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10) (+30 more)

### Community 59 - "UserId"
Cohesion: 0.07
Nodes (12): RoomIdentityRepository, RoomIdentityRepositoryTest, AuditedTable, AuditedTables, Auditable, Device, IdentityRepository, UserProfile (+4 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 62 - "ThemePaletteTest"
Cohesion: 0.10
Nodes (17): `log` (safe logging, A5a), DbKeyUnavailable, DbOpened, EntityActionFailed, EntityKind, ACCOUNT, CATEGORY, EntitySaveFailed (+9 more)

### Community 63 - "13. Deviations from the plan, by slice"
Cohesion: 0.08
Nodes (32): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), sansFamily(), sansWeights(), DisplayFont, BUNDLED_SERIF (+24 more)

### Community 66 - "Public interface"
Cohesion: 0.06
Nodes (36): Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How an entity step composes the blocks, Public interface, Purpose, Responsibilities & boundaries (+28 more)

### Community 67 - "6.1 Slices"
Cohesion: 0.19
Nodes (7): GroupedSectionColors, BackgroundStyle, FLAT, TONAL, PftSurfaces, Case, GroupedSectionColorsTest

### Community 69 - "PaiseParserTest.kt"
Cohesion: 0.17
Nodes (4): How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest

### Community 73 - "import_symbols.py"
Cohesion: 0.16
Nodes (6): The curated list, download(), main(), parse_hashes(), to_vector_drawable(), UnsafeSvg

### Community 76 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.15
Nodes (8): SectionContent(), BottomActionBarSamples(), DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 77 - "7. Test plan"
Cohesion: 0.15
Nodes (8): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 3.4 Header glow (UI-13, DS6d), 3.8 Abstraction bar

### Community 80 - "assertequals"
Cohesion: 0.25
Nodes (5): AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette

### Community 81 - "ManifestHardeningTest"
Cohesion: 0.07
Nodes (25): ManifestHardeningTest, AccountServiceTest, CategoryServiceTest, TagServiceTest, 7. Test plan, Slice B — Accounts, categories, tags (`feat/m1a-ledger-setup`), 10. Risks & mitigations, 11. Rollout & verification (+17 more)

### Community 82 - "AccountEditScreen.kt"
Cohesion: 0.10
Nodes (6): AccountEditRoute(), AccountEditScreen(), amountLabelRes(), LastFourField(), TypeSelector(), message()

### Community 84 - "`di` and startup (A5b)"
Cohesion: 0.07
Nodes (48): `di` and startup (A5b), Purpose, AppContainer, AppServices, AppStart, KeyUnavailable, Ready, SafeLog (+40 more)

### Community 85 - "MigrationTest"
Cohesion: 0.08
Nodes (4): AuditTriggers, MigrationTestSupport, MigrationTest, Audit triggers

### Community 89 - "LedgerError"
Cohesion: 0.09
Nodes (35): `ui/common` (shared components, DS3, DS4, DS5 and A5d), `ui/setup` and the UiState convention (A5e), Duplicate, FieldError, InvalidAmount, InvalidLastFour, Required, TooLong (+27 more)

### Community 90 - "HeaderIconButton"
Cohesion: 0.22
Nodes (4): HeaderIconButton(), HeaderIconButtonTest, 3.5 Icon-only header actions (UI-14, DS6e; needs DS3), 9. Documentation updates (all required surfaces)

### Community 92 - "StatusChip"
Cohesion: 0.09
Nodes (11): ChipColors, StatusChip(), CategoryColors, CategoryPalette, pair(), MoneyColors, PftSpacing, StatusChipTest (+3 more)

### Community 94 - "PftTheme"
Cohesion: 0.21
Nodes (4): DesignCatalog(), PftTheme(), DesignCatalogTest, Step 11 — `NameField` and `EditFormScaffold` (A5d)

### Community 98 - "AuditDiffTest.kt"
Cohesion: 0.18
Nodes (3): AccountEditActions, HeaderTags, AccountEditScreenTest

### Community 99 - "DataUnavailableScreenTest"
Cohesion: 0.14
Nodes (8): Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING, DataUnavailableScreenTest

### Community 101 - "SampleThing.kt"
Cohesion: 0.08
Nodes (14): AuditEntryEntity, MetaColumns, MetaMapper, DeviceEntity, UserProfileEntity, MetaMapperTest, RoomEntityRepositoryTest, SampleDatabase (+6 more)

### Community 105 - "`ui/categories` (B4a)"
Cohesion: 0.15
Nodes (6): `ui/categories` (B4a), CategoriesNotice, CategoriesUiState, CategoriesViewModel, DeleteBlocked, Failed

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 113 - "CatalogSection"
Cohesion: 0.14
Nodes (14): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+6 more)

### Community 115 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 117 - "Category"
Cohesion: 0.10
Nodes (19): RoomCategoryRepository, Category, CategoryKind, EXPENSE, INCOME, CategoryLists, CategoryRepository, CategoryService (+11 more)

### Community 118 - "AppContainerTest.kt"
Cohesion: 0.08
Nodes (5): DatabaseStartup, KeyUnavailable, Ready, DatabaseStartupTest, NameBasedIdTest

### Community 119 - "Appearance: colour palettes, dynamic colour, background and type"
Cohesion: 0.40
Nodes (5): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.13
Nodes (10): after(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), putSetting(), settingDirectly() (+2 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.09
Nodes (9): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), open(), `the open helper gets the real key, and it is still intact once open returns`(), MainActivityOpeningTest (+1 more)

### Community 123 - "shouldbe"
Cohesion: 0.05
Nodes (38): AuditEntryMapper, AuditDaoTest, AuditEntryMapperTest, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE (+30 more)

### Community 129 - "AppSetting"
Cohesion: 0.22
Nodes (3): RoomAppSettingsRepositoryTest, runSuspending(), 3.3 Dependencies (one pins-only PR, A5c)

### Community 130 - "CategoryIcon"
Cohesion: 0.11
Nodes (5): CategoryDraft, CategoryIcon, Emoji, Symbol, CategoryFixture

### Community 131 - "How to run and test"
Cohesion: 0.08
Nodes (5): How to run and test, PftDatabase, LaunchRoutingTest, ReleaseHasNoCatalogueTest, CategoryIconTest

### Community 132 - "NameFieldTest"
Cohesion: 0.06
Nodes (26): AuditFieldsCheck, haveCompleteAuditFields(), 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 3.1 The model: every transaction has one or more lines, 3.2 Lines are ledger entities, written only by `TransactionService` (+18 more)

### Community 135 - "AuditEntryEntity"
Cohesion: 0.21
Nodes (4): checkAmount(), FieldCheck, FieldState, FieldStateTest

### Community 138 - "AppSetting"
Cohesion: 0.11
Nodes (10): Data it owns, EpochMillis, RoomTransactionRunner, RoomAppSettingsRepository, EpochMillisTest, AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository (+2 more)

### Community 142 - "TestClock"
Cohesion: 0.07
Nodes (11): How to run and test, AuditedWriterTest, EntityMetaTest, IdentityServiceTest, IdsTest, clockAt(), Uuid7Test, SettingsServiceTest (+3 more)

### Community 143 - "RowAction"
Cohesion: 0.44
Nodes (5): toRow(), toRow(), EntityRow, RowGlyph, RowAction

### Community 145 - "ThemePalette"
Cohesion: 0.19
Nodes (7): colors(), ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, ChipColorsTest

### Community 147 - "Paise"
Cohesion: 0.14
Nodes (5): Configuration, `:core:money` — integer-paise money, Data it owns, Purpose, Paise

### Community 150 - "MoneyTextTest.kt"
Cohesion: 0.26
Nodes (3): HomeRoute(), HomeScreen(), DevHeaderActions()

### Community 154 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.06
Nodes (22): `security` and the encrypted open (A4b), backup(), open(), EncryptedMigrationTest, DatabaseFactory, PreMigrationBackup, PreMigrationBackupFailed, DbKeyManager (+14 more)

### Community 161 - "LoggingScanTest"
Cohesion: 0.29
Nodes (3): LoggingScanTest, SourceScan, Violation

### Community 163 - "AccountsScreen.kt"
Cohesion: 0.33
Nodes (3): AccountBadge, AccountRowMapper, AccountRowMapperTest

### Community 164 - "Project README"
Cohesion: 0.09
Nodes (16): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer (+8 more)

### Community 170 - "CategoriesSamples.kt"
Cohesion: 0.20
Nodes (6): CategoriesActions, CategoriesPreview(), CategoriesSample(), sample(), sampleCategoriesState(), CategoriesScreenshotTest

### Community 172 - "CategoryRowMapperTest.kt"
Cohesion: 0.21
Nodes (9): CategoryAction, ARCHIVE, DELETE, MOVE_DOWN, MOVE_UP, UNARCHIVE, CategoryItem, toItems() (+1 more)

### Community 173 - "Accounts"
Cohesion: 0.15
Nodes (12): Accounts, Adding and editing an account, Archive or delete?, Configuration, If saving fails, If something goes wrong, If the account is gone, Privacy (+4 more)

### Community 174 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.12
Nodes (17): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5. Flows, 6. Implementation steps (TDD-ordered) (+9 more)

### Community 175 - "TokenSheetSections.kt"
Cohesion: 0.25
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 176 - "ThemeLiteralScanTest"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 183 - "MoneySize"
Cohesion: 0.22
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 186 - "CategoriesScreen.kt"
Cohesion: 0.23
Nodes (6): actionLabelRes(), CategoriesRoute(), CategoriesScreen(), kindLabelRes(), KindSwitch(), failureRes()

### Community 190 - "EntityAction"
Cohesion: 0.20
Nodes (7): EntityAction, ARCHIVE, DELETE, REORDER, RESTORE, UNARCHIVE, EntityActionTextTest

### Community 202 - "GlyphSource"
Cohesion: 0.31
Nodes (5): toGlyphSource(), Emoji, GlyphSource, Letter, Symbol

### Community 203 - "EntityListSamples.kt"
Cohesion: 0.42
Nodes (6): EntityListEmptyPreview(), EntityListEmptySample(), EntityListSample(), glyph(), sampleAction(), sampleRow()

### Community 204 - "Where it lives"
Cohesion: 0.22
Nodes (8): CategoryListsTest, Categories and tags, Rules the app already enforces, Tags, The categories list, The categories you start with, What a category is, Where it lives

### Community 206 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

## Knowledge Gaps
- **233 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+228 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 697 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **118 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `runSuspending`, `ThemeResolutionTest`, `Paise`, `HeaderGlowScaffoldTest.kt`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `PaletteSamples.kt`, `CategoryColour`, `CollapsingHeaderScaffold`, `DbKeyManagerJvmTest`, `PressScaleTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `PreMigrationBackupTest`, `assertnotequals`, `LaunchViewModelTest.kt`, `AccountId`, `CategoriesFlowTest`, `MainActivityStartupTest.kt`, `PftSymbolTest`, `PftThemeModeTest`, `RoomAuditColumnsTest`, `ContentScreenshotTest`, `DbKeyManagerTest.kt`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `UserId`, `Public interface`, `6.1 Slices`, `AuditTriggersTest`, `AppContainerTest.kt`, `RoomCategoryIntegrationTest`, `Previews, the token sheet and screenshot goldens`, `GroupedSectionTest.kt`, `ManifestHardeningTest`, `RoomAccountIntegrationTest`, ``di` and startup (A5b)`, `MigrationTest`, `RoomAuditedWriterIntegrationTest`, `PreMigrationBackupTest`, `StatusChip`, `PftTheme`, `DataUnavailableScreenTest`, `MoneyToneTest`, `SampleThing.kt`, `RoomTestKit`, `SetupFlowTest.kt`, `SetupScreenTest`, `BundledFontTest`, `BottomActionBarTest`, `.check`, `StatusChip`, `AppContainerTest.kt`, `BackgroundStyle`, `DatabaseFactoryTest.kt`, `shouldbe`, `Project README`, `RoomTransactionRunnerTest`, ``ui/categories` (B4a)`, `PaletteScreenshotTest.kt`, `AppSetting`, `AuditEntryEntity`, `WindowThemeTest`, `DevRouteTest.kt`, `TestDatabase`, `DbKeyManagerWiringTest.kt`, `PftMotionTest`, `ThemePalette`, `PftSpacingTest`, `.onCreate`, `ThemePrefsTest`, `How to run and test`, `FieldState`, `Slice A — Foundation (`feat/m1a-foundation`)`, `LogEventShapeTest`, `.idOf`, `CategoryIconGlyphTest`, `PftRootTest.kt`, `ReleaseHasNoCatalogueTest.kt`, `LoggingScanTest`, `EditFormScaffoldTest`, `HomeScreenTest.kt`, `BundledSymbolsLicenceTest`, `.append`, `Icons and emoji (DS3, UI-6)`, `NavTransitionTest.kt`, `testtag`, `BottomActionBarSurfaceTest.kt`, `RoomCategoryForeignKeyTest.kt`, `PaletteScreenshotTest.kt`, `expandedHeaderHeight`?**
  _High betweenness centrality (0.204) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _233 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `PaletteScreenshotTest.kt`, `How to run and test`, `WindowThemeTest`, `ThemeResolutionTest`, `Paise`, `DevRouteTest.kt`, `UndoDeleteSnackbarTest`, `HeaderGlowScaffoldTest.kt`, `PftMotionTest`, `CategoryIcon`, `Paise`, `How to run and test`, `EntityListScreen.kt`, `PftSurfacesTest`, `CollapsingHeaderScaffoldTest`, `PaletteSamples.kt`, `.idOf`, `PftRootTest.kt`, `CollapsingHeaderScaffold`, `EditFormScaffoldTest`, `DesignCatalog.kt`, `PressScaleTest.kt`, `AccountsFlowTest.kt`, `CategoryGlyph`, `HomeScreenTest.kt`, `PftTypographyTest`, `Icons and emoji (DS3, UI-6)`, `CategoriesSamples.kt`, `Project README`, `HeaderTypePairingTest`, `CategoriesFlowTest`, `CategoryGlyphScreenshotTest`, `PftThemeModeTest`, `ContentScreenshotTest`, `AccountsScreenTest.kt`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `CategoriesScreen.kt`, `testtag`, `13. Deviations from the plan, by slice`, `BottomActionBarSurfaceTest.kt`, `EntityListScreenTest`, `MoreScreenTest.kt`, `6.1 Slices`, `SetupScreenTest.kt`, `PaletteScreenshotTest.kt`, `PaletteSwatchesTest.kt`, `EntityListSamples.kt`, `Previews, the token sheet and screenshot goldens`, `7. Test plan`, `GroupedSectionTest.kt`, `CategoriesScreenTest.kt`, `ManifestHardeningTest`, `AccountEditScreen.kt`, ``di` and startup (A5b)`, `PreMigrationBackupTest`, `HeaderIconButton`, `StatusChip`, `CategoriesFlowTest`, `PftDatabase.kt`, `AuditDiffTest.kt`, `DataUnavailableScreenTest`, `SetupFlowTest.kt`, `SetupScreenTest`, `BottomActionBarTest`, `remember`, `TestClock`, ``ui/categories` (B4a)`?**
  _High betweenness centrality (0.104) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `Tag` be split into smaller, more focused modules?**
  _Cohesion score 0.07581453634085213 - nodes in this community are weakly interconnected._