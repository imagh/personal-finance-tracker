# Graph Report - personal-finance-tracker  (2026-11-14)

## Corpus Check
- 357 files · ~266,488 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 4129 nodes · 12419 edges · 194 communities (105 shown, 89 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 1932 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `2c2dd6ba`
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
- DesignCatalog.kt
- PftSymbol
- CollapsingHeaderScaffoldTest
- TestClock
- SafeLogTest
- SetupViewModel.kt
- BackgroundStyle
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- HeaderGlowScaffoldTest.kt
- PftTypographyTest
- CategoryGlyph
- PftShapesTest.kt
- GlyphSourceTest
- AppContainer
- Layered Modular Architecture (core vs app)
- Account
- PftMotion.kt
- HeaderTypePairingTest.kt
- PreMigrationBackup
- CLAUDE.md Development Constitution
- AppContainer
- CategoryGlyphScreenshotTest
- EntityRepository
- FontBudgetTest
- PftThemeModeTest
- RoomAuditColumnsTest
- LedgerEntity
- ContentScreenshotTest
- DbKeyManagerTest
- MoneyText
- Personal Finance Tracker PRD
- EntityMeta
- redact_text
- SetupViewModelTest
- ThemePaletteTest
- PftTypography.kt
- Pseudo
- GroupedSection.kt
- Public interface
- DesignCatalog.kt
- AccountEntity
- SetupScreenTest.kt
- _Text
- MainActivityOpeningTest.kt
- import_symbols.py
- AppContainerTest.kt
- RoomCategoryIntegrationTest
- Previews, the token sheet and screenshot goldens
- 7. Test plan
- PftSymbolTest
- ReleaseHasNoCatalogueTest.kt
- MoneyColors
- Plan: M1a design system and visual direction (lands before A5)
- PftDatabase.kt
- RoomAccountIntegrationTest
- FieldState
- MigrationTest
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
- CollapsingHeaderScaffold
- LedgerLimits
- DataUnavailableScreenTest
- Paise
- RoomEntityRepositoryTest.kt
- IdentityRoomIntegrationTest
- AppSettingEntity
- SetupFlowTest.kt
- How to run and test
- parse_svg
- NameFieldTest.kt
- BundledFontTest
- BottomActionBarTest
- ImportRunTest
- SetupScreen.kt
- InMemoryKekSource
- CatalogSection
- localdensity
- HostedComposeRule
- LogEventShapeTest
- CategoryColour
- DatabaseStartupTest.kt
- Loans and EMIs (LN-1..LN-5)
- InMemoryEntityRepository
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- shouldbe
- 6.1 Slices
- DesignCatalogTest.kt
- RoomTransactionRunnerTest
- SampleThing.kt
- Category
- runSuspending
- .create
- PaiseParser
- BaseDao
- EmptyState
- AuditEntryEntity
- 7. Test plan
- WindowThemeTest
- DevRouteTest.kt
- RoomCategoryMapperTest
- `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)
- DbKeyManagerWiringTest.kt
- PftSpacingTest
- Uuid7Test.kt
- HeaderPolicy
- assertequals
- 6. Implementation steps (TDD-ordered)
- .format
- CategoryEntity
- MoneySize
- DatabaseFactory
- PftSurfacesTest
- HomeViewModelTest.kt
- SetupRouteTest.kt
- RoomCategoryForeignKeyTest.kt
- EncryptedMigrationTest.kt
- Design system
- LogEventTest
- .fromIconKey
- PaletteScreenshotTest.kt
- .check
- ThemeLiteralScanTest
- EditFormScaffoldTest.kt
- 13. Deviations from the plan, by slice
- Project README
- PeriodStepper
- HomeScreenTest.kt
- BundledSymbolsLicenceTest
- .append
- FieldErrorMessageTest.kt
- 2. Current state
- `data` (Room persistence, A4a)
- Icons and emoji (DS3, UI-6)
- CategoryKind
- expandedHeaderHeight
- AuditDaoTest.kt
- AccountType
- CategoryFixture
- HeaderSlot
- Immutable Audit Trail
- rowscope
- Collecting and Redacting Sample Messages Guide
- MainActivityStartupTest.kt
- LaunchRoutingTest.kt
- CategoryIcon
- HeaderPhase
- HeaderStyle
- CategoryColourTest.kt
- CategoryErrorsTest.kt
- LoggingScanTest
- Architecture overview
- Security, privacy and integrity requirements (SEC/INT)
- AccountRulesTest.kt

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 192 edges
2. `ThemePrefs` - 161 edges
3. `PftSymbol` - 125 edges
4. `Paise` - 112 edges
5. `How to run and test` - 112 edges
6. `runSuspending()` - 109 edges
7. `HostedComposeRule` - 94 edges
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

## Communities (194 total, 89 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "LogEvent"
Cohesion: 0.12
Nodes (12): `log` (safe logging, A5a), PreMigrationBackupFailed, DbKeyUnavailable, DbOpened, LogEvent, LogValue, render(), SetupCompleted (+4 more)

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
Cohesion: 0.19
Nodes (4): AuditEntryMapper, AuditEntryMapperTest, The audit trail, Version 1 (A4a)

### Community 9 - "ThemePrefs"
Cohesion: 0.08
Nodes (7): EditFormPreview(), dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemePrefsTest, ThemeResolutionTest

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.17
Nodes (9): Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone(), sweptTones() (+1 more)

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.16
Nodes (22): moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense (+14 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "DesignCatalog.kt"
Cohesion: 0.06
Nodes (7): AccentCheckSection(), MotionSection(), SpringTrack(), fixedSp(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder()

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.08
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "TestClock"
Cohesion: 0.10
Nodes (10): `:app` — Android application, Configuration, Data it owns, Dependencies, Responsibilities and boundaries, EpochMillis, EpochMillisTest, AppServicesTest (+2 more)

### Community 33 - "BackgroundStyle"
Cohesion: 0.17
Nodes (8): GroupedSectionColors, BackgroundStyle, FLAT, TONAL, PftSurfaces, Case, GroupedSectionColorsTest, Theming

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.18
Nodes (15): GlyphSize, Chip, Hero, Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers() (+7 more)

### Community 37 - "PftTypographyTest"
Cohesion: 0.10
Nodes (5): sansFamily(), PftTypographyTest, Resolved, Third-party assets, 3.7 Bundled sans fonts (UI-16, UI-17; DS6f spike done, then DS6h, DS6i)

### Community 41 - "AppContainer"
Cohesion: 0.09
Nodes (23): `di` and startup (A5b), Purpose, `security` and the encrypted open (A4b), AppContainer, AppStart, KeyUnavailable, Ready, PftApplication (+15 more)

### Community 42 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "Account"
Cohesion: 0.18
Nodes (6): RoomAccountRepository, Account, AccountDraft, AccountRepository, AccountId, InMemoryAccountRepository

### Community 44 - "PftMotion.kt"
Cohesion: 0.11
Nodes (3): PftMotion, pressScale(), 11. Rollout & verification

### Community 46 - "PreMigrationBackup"
Cohesion: 0.17
Nodes (10): PreMigrationBackup, factory(), Corruption handling, Persistence and security, Pre-migration backup (INT-5), SQLCipher, The key and the KEK, The key array and SQLCipher (a trap) (+2 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 49 - "CategoryGlyphScreenshotTest"
Cohesion: 0.23
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 50 - "EntityRepository"
Cohesion: 0.18
Nodes (5): AuditSource, MANUAL, RECURRING, SYSTEM, EntityRepository

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "LedgerEntity"
Cohesion: 0.08
Nodes (18): MetaMapperTest, EntityMeta, LedgerEntity, AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind (+10 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.19
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "MoneyText"
Cohesion: 0.18
Nodes (9): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText(), RollingDigits() (+1 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.10
Nodes (24): Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Personal Finance Tracker PRD, Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Delivery constraints (small vertical slices, Claude Pro limits), Automatic capture from Email (E-0..E-5) (+16 more)

### Community 59 - "EntityMeta"
Cohesion: 0.07
Nodes (16): RoomIdentityRepository, RoomIdentityRepositoryTest, AuditedTable, AuditedTables, Auditable, Device, IdentityRepository, UserProfile (+8 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 61 - "SetupViewModelTest"
Cohesion: 0.27
Nodes (3): awaitMain(), FailsOnAppend, SetupViewModelTest

### Community 63 - "PftTypography.kt"
Cohesion: 0.17
Nodes (12): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansWeights(), DisplayFont, BUNDLED_SERIF (+4 more)

### Community 65 - "GroupedSection.kt"
Cohesion: 0.14
Nodes (8): GroupedRow, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, Content primitives (DS5), `EmptyState`, `GroupedSection`, `StatusChip`

### Community 66 - "Public interface"
Cohesion: 0.07
Nodes (35): RoomAuditLog, RoomTransactionRunner, RoomAppSettingsRepository, AppServices, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...) (+27 more)

### Community 67 - "DesignCatalog.kt"
Cohesion: 0.06
Nodes (14): ChoiceRow(), SectionContent(), BottomActionBarPreview(), BottomActionBarSamples(), GroupedSectionPreview(), GroupedSectionSamples(), StatusChipPreview(), StatusChipSamples() (+6 more)

### Community 68 - "AccountEntity"
Cohesion: 0.22
Nodes (4): AccountEntity, AccountMapper, MetaColumns, RoomAccountMapperTest

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 76 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.12
Nodes (8): DevHeaderActions(), devRoutes(), DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 77 - "7. Test plan"
Cohesion: 0.11
Nodes (11): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), colors(), HeaderGlowTest, 1. Requirement (+3 more)

### Community 80 - "MoneyColors"
Cohesion: 0.14
Nodes (6): ChipColors, MoneyColors, PftSpacing, ChipColorsTest, 3.3 Tokens, 3.1 Palettes (DS6a, DS6b)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.15
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 82 - "PftDatabase.kt"
Cohesion: 0.14
Nodes (4): IdentityDao, DeviceEntity, UserProfileEntity, PftDatabase

### Community 84 - "FieldState"
Cohesion: 0.07
Nodes (40): `ui/setup` and the UiState convention (A5e), IdentityCorrupt, SafeLog, EditFormScaffold(), FieldError, InvalidLastFour, FieldCheck, FieldState (+32 more)

### Community 85 - "MigrationTest"
Cohesion: 0.08
Nodes (4): AuditTriggers, MigrationTestSupport, MigrationTest, Audit triggers

### Community 87 - "PreMigrationBackupTest"
Cohesion: 0.07
Nodes (3): AuditTriggersTest, PftDatabaseTest, PreMigrationBackupTest

### Community 89 - "LedgerError"
Cohesion: 0.12
Nodes (28): `ui/common` (shared components, DS3, DS4, DS5 and A5d), Duplicate, Required, TooLong, toFieldError(), Behaviours worth knowing, AmountNotPositive, AmountTooLarge (+20 more)

### Community 90 - "HeaderIconButtonTest.kt"
Cohesion: 0.22
Nodes (3): HeaderIconButton(), HeaderIconButtonTest, 3.5 Icon-only header actions (UI-14, DS6e; needs DS3)

### Community 93 - "GroupedSectionTest.kt"
Cohesion: 0.07
Nodes (3): BottomActionBarSurfaceTest, FieldStateTest, GroupedSectionTest

### Community 94 - "PftTheme"
Cohesion: 0.22
Nodes (3): DesignCatalog(), PftTheme(), DesignCatalogTest

### Community 95 - "DbKeyManager"
Cohesion: 0.26
Nodes (4): DbKeyManager, Decoded, WrappedKeyFormat, Migrations on the encrypted file (`EncryptedMigrationTest`)

### Community 96 - "CollapsingHeaderScaffold"
Cohesion: 0.16
Nodes (25): Debug and release source sets, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, `ui/nav` and `ui/home` (A5f), Window theme, MainActivity, BottomActionBar(), CollapsingHeaderScaffold() (+17 more)

### Community 98 - "LedgerLimits"
Cohesion: 0.04
Nodes (44): Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, Purpose, LedgerLimits, 10. Risks & mitigations, 11. Rollout & verification (+36 more)

### Community 99 - "DataUnavailableScreenTest"
Cohesion: 0.15
Nodes (8): Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING, DataUnavailableScreenTest

### Community 105 - "How to run and test"
Cohesion: 0.12
Nodes (9): How to run and test, AuditDiffTest, Thing, AuditedWriterTest, IdentityServiceTest, IdsTest, SettingsServiceTest, ValidationTest (+1 more)

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 111 - "SetupScreen.kt"
Cohesion: 0.09
Nodes (7): message(), HomeRoute(), HomeScreen(), SetupRoute(), SetupScreen(), Where it lives, Step 13 — `SetupScreen` (A5e)

### Community 112 - "InMemoryKekSource"
Cohesion: 0.13
Nodes (3): AndroidKeystoreKek, KekSource, InMemoryKekSource

### Community 113 - "CatalogSection"
Cohesion: 0.14
Nodes (14): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+6 more)

### Community 114 - "localdensity"
Cohesion: 0.11
Nodes (3): FormScreenshotTest, SetupScreenshotTest, TokenSheetScreenshotTest

### Community 115 - "HostedComposeRule"
Cohesion: 0.14
Nodes (4): HostedComposeRule, RegisterHostActivity, Statement, PaletteSwatchesTest

### Community 117 - "CategoryColour"
Cohesion: 0.13
Nodes (15): CategoryColour, AMBER, BLUE, CORAL, EMERALD, INDIGO, MAGENTA, OLIVE (+7 more)

### Community 118 - "DatabaseStartupTest.kt"
Cohesion: 0.17
Nodes (4): DatabaseStartup, KeyUnavailable, Ready, DatabaseStartupTest

### Community 119 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.19
Nodes (12): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5) (+4 more)

### Community 120 - "InMemoryEntityRepository"
Cohesion: 0.13
Nodes (6): Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.09
Nodes (13): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+5 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.19
Nodes (6): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "shouldbe"
Cohesion: 0.09
Nodes (25): TestDatabase, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE, UNARCHIVE (+17 more)

### Community 124 - "6.1 Slices"
Cohesion: 0.11
Nodes (23): ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, paletteFor(), SansFont, FIGTREE (+15 more)

### Community 125 - "DesignCatalogTest.kt"
Cohesion: 0.14
Nodes (4): HeaderTags, assertShownInFull(), isShownInFull(), layout()

### Community 127 - "SampleThing.kt"
Cohesion: 0.23
Nodes (6): SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 128 - "Category"
Cohesion: 0.21
Nodes (5): RoomCategoryRepository, Category, CategoryRepository, CategoryId, InMemoryCategoryRepository

### Community 129 - "runSuspending"
Cohesion: 0.16
Nodes (5): RoomAppSettingsRepositoryTest, runSuspending(), AppSetting, InMemoryAppSettingsRepository, 3.3 Dependencies (one pins-only PR, A5c)

### Community 130 - ".create"
Cohesion: 0.18
Nodes (3): CategoryDraft, validated(), CategoryTree

### Community 135 - "7. Test plan"
Cohesion: 0.32
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 136 - "WindowThemeTest"
Cohesion: 0.05
Nodes (11): SyncWindowBackground(), ManifestHardeningTest, WindowBackgroundTest, WindowThemeTest, 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.5 Fonts: bundling, licensing, custom import (+3 more)

### Community 139 - "`ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)"
Cohesion: 0.24
Nodes (7): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, Colour

### Community 142 - "Uuid7Test.kt"
Cohesion: 0.07
Nodes (13): AccountServiceTest, CategoryServiceTest, DefaultCategoriesTest, NameBasedIdTest, clockAt(), Uuid7Test, How to run and test, PaiseFormatTest (+5 more)

### Community 145 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.29
Nodes (7): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice I — Cashew import (`feat/m1a-cashew-import`): tracking stub

### Community 146 - ".format"
Cohesion: 0.18
Nodes (10): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+2 more)

### Community 148 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 151 - "HomeViewModelTest.kt"
Cohesion: 0.17
Nodes (3): How to run and test, HomeViewModelTest, CategoryIconTest

### Community 155 - "Design system"
Cohesion: 0.09
Nodes (19): Adding a token, Build-time trust decisions, Design system, Motion (UI-10), Planned: premium look (DS6), Shapes, Spacing, Tokens (+11 more)

### Community 157 - ".fromIconKey"
Cohesion: 0.39
Nodes (4): Emoji, GlyphSource, Letter, Symbol

### Community 160 - ".check"
Cohesion: 0.18
Nodes (3): NameBasedId, idOf(), InMemoryEntityStore

### Community 161 - "ThemeLiteralScanTest"
Cohesion: 0.25
Nodes (3): SourceScan, Violation, ThemeLiteralScanTest

### Community 163 - "13. Deviations from the plan, by slice"
Cohesion: 0.13
Nodes (8): AccentHueTest, hueDistance(), labHue(), roles(), surfaceRoles(), toneOf(), WindowFlashToneTest, 13. Deviations from the plan, by slice

### Community 164 - "Project README"
Cohesion: 0.11
Nodes (15): Code Structure Map, core/shared Shared Packages, Categories and tags, Rules the app already enforces, Tags, The categories you start with, What a category is, Data protection: encrypted storage and the "can't open your data" screen (+7 more)

### Community 165 - "PeriodStepper"
Cohesion: 0.38
Nodes (10): CollapsingHeader(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderTitle(), LowerBand(), PeriodStepper() (+2 more)

### Community 170 - "2. Current state"
Cohesion: 0.21
Nodes (3): AuditFieldsCheck, haveCompleteAuditFields(), 2. Current state

### Community 171 - "`data` (Room persistence, A4a)"
Cohesion: 0.22
Nodes (4): `data` (Room persistence, A4a), EntityMapper, MetaMapper, RoomEntityRepository

### Community 172 - "Icons and emoji (DS3, UI-6)"
Cohesion: 0.27
Nodes (7): CategoryColors, CategoryPalette, pair(), Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 173 - "CategoryKind"
Cohesion: 0.21
Nodes (8): CategoryMapper, CategoryKind, EXPENSE, INCOME, SystemCategory, LOAN_EMI, Row, Version 3 (B3b)

### Community 176 - "AccountType"
Cohesion: 0.17
Nodes (9): AccountType, BANK, CASH, CREDIT_CARD, WALLET, Fixture, Data model, Version 2 (B1c) (+1 more)

### Community 177 - "CategoryFixture"
Cohesion: 0.22
Nodes (4): CategoryFixture, shouldRefuse(), order(), sortOrderOf()

### Community 178 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 181 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 184 - "CategoryIcon"
Cohesion: 0.22
Nodes (3): CategoryIcon, Emoji, Symbol

### Community 185 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 186 - "HeaderStyle"
Cohesion: 0.67
Nodes (3): HeaderStyle, Dashboard, Standard

### Community 191 - "Architecture overview"
Cohesion: 0.33
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 192 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

## Knowledge Gaps
- **218 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+213 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 653 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **89 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `HomeViewModelTest.kt` to `AuditEntryMapperTest`, `ThemePrefs`, `MoneyTextTest`, `ColorContrastTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `TestClock`, `SafeLogTest`, `BackgroundStyle`, `DbKeyManagerJvmTest`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `PftShapesTest.kt`, `GlyphSourceTest`, `AppContainer`, `AppContainer`, `CategoryGlyphScreenshotTest`, `PftThemeModeTest`, `RoomAuditColumnsTest`, `LedgerEntity`, `ContentScreenshotTest`, `DbKeyManagerTest`, `MoneyText`, `EntityMeta`, `SetupViewModelTest`, `ThemePaletteTest`, `Public interface`, `AccountEntity`, `SetupScreenTest.kt`, `MainActivityOpeningTest.kt`, `AppContainerTest.kt`, `RoomCategoryIntegrationTest`, `Previews, the token sheet and screenshot goldens`, `PftSymbolTest`, `ReleaseHasNoCatalogueTest.kt`, `MoneyColors`, `PftDatabase.kt`, `RoomAccountIntegrationTest`, `FieldState`, `MigrationTest`, `RoomAuditedWriterIntegrationTest`, `PreMigrationBackupTest`, `NavTransitionTest.kt`, `StatusChip`, `GroupedSectionTest.kt`, `PftTheme`, `CollapsingHeaderScaffold`, `DataUnavailableScreenTest`, `Paise`, `RoomEntityRepositoryTest.kt`, `IdentityRoomIntegrationTest`, `SetupFlowTest.kt`, `NameFieldTest.kt`, `BundledFontTest`, `BottomActionBarTest`, `localdensity`, `LogEventShapeTest`, `DatabaseStartupTest.kt`, `RoomTransactionRunnerTest`, `SampleThing.kt`, `runSuspending`, `EmptyState`, `7. Test plan`, `WindowThemeTest`, `DevRouteTest.kt`, `RoomCategoryMapperTest`, `DbKeyManagerWiringTest.kt`, `PftSpacingTest`, `assertequals`, `.format`, `DatabaseFactory`, `PftSurfacesTest`, `SetupRouteTest.kt`, `RoomCategoryForeignKeyTest.kt`, `EncryptedMigrationTest.kt`, `LogEventTest`, `PaletteScreenshotTest.kt`, `.check`, `EditFormScaffoldTest.kt`, `13. Deviations from the plan, by slice`, `HomeScreenTest.kt`, `BundledSymbolsLicenceTest`, `FieldErrorMessageTest.kt`, `expandedHeaderHeight`, `AuditDaoTest.kt`, `MainActivityStartupTest.kt`, `LaunchRoutingTest.kt`, `LoggingScanTest`?**
  _High betweenness centrality (0.278) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _218 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `PftSymbol` connect `PftSymbol` to `CollapsingHeaderScaffold.kt`, `CollapsingHeaderScaffold`, `HeaderIconButtonTest.kt`, `CategoryGlyph`, ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`, `Previews, the token sheet and screenshot goldens`, `Icons and emoji (DS3, UI-6)`, `PftSymbolTest`, `assertequals`, `CategoryGlyphScreenshotTest`, `Plan: M1a design system and visual direction (lands before A5)`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `FieldState`, `HomeViewModelTest.kt`, `LedgerError`, `DesignCatalog.kt`, `CollapsingHeaderScaffoldTest`, `.fromIconKey`?**
  _High betweenness centrality (0.088) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._