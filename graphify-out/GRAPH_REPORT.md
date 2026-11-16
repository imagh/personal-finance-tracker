# Graph Report - personal-finance-tracker  (2026-11-16)

## Corpus Check
- 426 files · ~311,708 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 4992 nodes · 15827 edges · 201 communities (102 shown, 99 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 2156 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `5eec94a9`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- Tag
- Personal Finance Tracker Learnings
- Collecting and Redacting Sample Messages Guide
- runSuspending
- hooks
- test_import_symbols.py
- CategoryColourPaletteTest.kt
- LedgerEntity
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- HostedComposeRule
- Catalog.kt
- detekt.yml
- DesignCatalog.kt
- PftSymbol
- CollapsingHeaderScaffoldTest.kt
- CategoryGlyph.kt
- CategoryEntity
- FieldState
- CategoriesScreen.kt
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- DbKeyManagerJvmTest
- CategoryGlyphSamples
- HeaderGlowScaffoldTest.kt
- PftTypographyTest
- CategoryGlyph
- PreMigrationBackupTest
- Public interface
- LaunchViewModelTest.kt
- Layered Modular Architecture (core vs app)
- PftMotion.kt
- PftMotion.kt
- HeaderTypePairingTest.kt
- PftTheme
- CLAUDE.md Development Constitution
- HeaderGlowScaffoldTest
- awaitMain
- FontBudgetTest
- PftThemeModeTest
- RoomAuditColumnsTest
- AuditFieldsCheckTest.kt
- ContentScreenshotTest
- DbKeyManagerTest.kt
- MoneyText
- Personal Finance Tracker PRD
- UserProfile
- redact_text
- AccountEditViewModelTest
- DbKeyUnavailable
- PftTypography.kt
- Pseudo
- EntityListScreenTest.kt
- AppServices
- GroupedSectionColorsTest
- HostedComposeRule
- AccountPickerTest.kt
- _Text
- AuditTriggersTest
- import_symbols.py
- AppContainerTest.kt
- RoomCategoryIntegrationTest
- Previews, the token sheet and screenshot goldens
- 7. Test plan
- GroupedSectionTest
- AccountPickerTest.kt
- SampleId
- Plan: M1a design system and visual direction (lands before A5)
- MetaColumns
- RoomAccountIntegrationTest
- 7. Test plan
- MigrationTest
- RoomAuditedWriterIntegrationTest
- CategoryGlyphScreenshotTest
- TabularFiguresTest
- LedgerError
- instant
- AccountsViewModelTest
- GroupedSection.kt
- EncryptedMigrationTest
- DesignCatalogTest
- DatabaseFactory
- instant
- EntityMeta
- AccountEditScreenTest
- PftRootTest.kt
- MoneyToneTest
- SampleThing.kt
- IdentityRoomIntegrationTest
- RoomCategoryMapperTest
- SetupFlowTest.kt
- 13. Deviations from the plan, by slice
- parse_svg
- SetupScreenTest.kt
- BundledFontTest
- getvalue
- ImportRunTest
- AuditEntryMapperTest.kt
- InMemoryKekSource
- CatalogSection
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- TokenSheetSections.kt
- LogEventShapeTest
- Category
- DatabaseStartupTest.kt
- Plan: M1a — Core ledger (manual-entry household tracker)
- ManifestHardeningTest
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- shouldbe
- AccountsRouteTest.kt
- SampleId
- RoomTransactionRunnerTest
- NameFieldTest.kt
- Loans and EMIs (LN-1..LN-5)
- RoomAppSettingsRepositoryTest.kt
- Leaky
- PftDatabase
- HomeViewModelTest.kt
- `ui/more` and `ui/accounts` (B2a, B2b)
- AuditEntryEntity
- AuditDaoTest.kt
- WindowThemeTest
- ThemePrefsTest
- TestClock
- TagEntity
- DbKeyManagerWiringTest.kt
- UndoDeleteSnackbarTest.kt
- Uuid7Test.kt
- NavTransitionTest.kt
- PeriodStepper
- 6.1 Slices
- ReleaseHasNoCatalogueTest.kt
- .format
- LaunchRoutingTest.kt
- CategoryAction
- SetupScreen
- Collecting and Redacting Sample Messages Guide
- SetupRouteTest.kt
- ThemePaletteTest
- DbKeyManager
- AmountFieldTest
- assertequals
- PftSpacingTest
- .check
- SheetWindow.kt
- `ui/common` (shared components, DS3, DS4, DS5 and A5d)
- AccountDao
- EditFormScaffoldTest
- MainActivity
- Categories and tags
- AccountsFlowTest.kt
- assertisdisplayed
- BundledSymbolsLicenceTest
- 6. Implementation steps (TDD-ordered)
- PftBottomSheetTest.kt
- TextLayoutSupport.kt
- PaiseParser
- CategoryRowMapperTest
- Accounts
- PftShapesTest.kt
- HeaderPolicy
- CategoryColourTest.kt
- .append
- .append
- Architecture overview
- .append
- HeaderPhase
- HeaderStyle
- .append
- AccountsScreenTest.kt
- TokenSheetScreenshotTest
- CategoryColourTest.kt
- CategoryErrorsTest.kt
- HeaderPolicy
- EntityAction
- PaletteScreenshotTest.kt
- SyncWindowBackground
- expandedHeaderHeight
- UserProfileEntity
- How to run and test
- PaletteScreenshotTest.kt
- HeaderSlot
- PaletteSamples.kt
- GlyphSource
- Theme
- .append

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 240 edges
2. `ThemePrefs` - 203 edges
3. `Paise` - 149 edges
4. `PftSymbol` - 138 edges
5. `HostedComposeRule` - 126 edges
6. `runSuspending()` - 125 edges
7. `How to run and test` - 113 edges
8. `awaitMain()` - 99 edges
9. `CollapsingHeaderScaffoldTest` - 83 edges
10. `EntityMeta` - 83 edges

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

## Communities (201 total, 99 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "Tag"
Cohesion: 0.12
Nodes (7): TagMapper, TagId, Tag, TagLists, isNameTaken(), TagFixture, InMemoryTagRepository

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.17
Nodes (11): Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser, Public Bank SMS Header Registry (gist, Dec 2022) (+3 more)

### Community 5 - "runSuspending"
Cohesion: 0.18
Nodes (3): RoomTagIntegrationTest, runSuspending(), 3.3 Dependencies (one pins-only PR, A5c)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 9 - "LedgerEntity"
Cohesion: 0.07
Nodes (8): AuditedTable, EntityRepository, LedgerEntity, InMemoryEntityRepository, AuditFieldsCheck, haveCompleteAuditFields(), shouldHaveCompleteAuditFields(), 2. Current state

### Community 10 - "MoneyTextTest"
Cohesion: 0.14
Nodes (3): layoutResult(), MoneyTextTest, shownText()

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.06
Nodes (17): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), Case, ColorContrastTest, Theme, DARK, LIGHT (+9 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "DesignCatalog.kt"
Cohesion: 0.04
Nodes (9): AccentCheckSection(), MotionSection(), SpringTrack(), AccountPickerRow(), fixedSp(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder() (+1 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest.kt"
Cohesion: 0.09
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 30 - "CategoryEntity"
Cohesion: 0.16
Nodes (3): CategoryDao, CategoryEntity, BaseDao

### Community 31 - "FieldState"
Cohesion: 0.05
Nodes (28): `log` (safe logging, A5a), DbKeyUnavailable, DbOpened, EntityActionFailed, EntityKind, ACCOUNT, CATEGORY, EntitySaveFailed (+20 more)

### Community 32 - "CategoriesScreen.kt"
Cohesion: 0.11
Nodes (11): AccountsActions, AccountsNavigation, AccountsRoute(), AccountsScreen(), typeLabelRes(), actionLabelRes(), CategoriesRoute(), CategoriesScreen() (+3 more)

### Community 33 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.09
Nodes (52): Debug and release source sets, Public interface, `ui/more` and `ui/accounts` (B2a, B2b), `ui/nav` and `ui/home` (A5f), Window theme, MainActivity, AccountPicker(), AmountField() (+44 more)

### Community 35 - "CategoryGlyphSamples"
Cohesion: 0.60
Nodes (3): CategoryGlyphPreview(), CategoryGlyphSamples(), SampleRow()

### Community 37 - "PftTypographyTest"
Cohesion: 0.12
Nodes (3): sansFamily(), PftTypographyTest, Resolved

### Community 38 - "CategoryGlyph"
Cohesion: 0.09
Nodes (4): CategoryGlyph(), CategoryGlyphTest, CategoryIconGlyphTest, GlyphSourceTest

### Community 39 - "PreMigrationBackupTest"
Cohesion: 0.05
Nodes (7): Configuration, PftDatabaseTest, PreMigrationBackupTest, LoggingScanTest, SourceScan, Violation, ThemeLiteralScanTest

### Community 40 - "Public interface"
Cohesion: 0.10
Nodes (11): How an entity step composes the blocks, Public interface, AccountLists, AccountRules, AuditDiff, validated(), Auditable, Identity (+3 more)

### Community 42 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "PftMotion.kt"
Cohesion: 0.07
Nodes (21): AccountEntity, AccountMapper, EntityMapper, AccountsUiState, AccountPickerEmptyPreview(), AccountPickerEmptySample(), AccountPickerSample(), sampleAccount() (+13 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.09
Nodes (6): iosSpring(), PftMotion, pressScale(), PftMotionTest, 7. Test plan, 11. Rollout & verification

### Community 46 - "PftTheme"
Cohesion: 0.09
Nodes (15): EditFormPreview(), EditFormSample(), GroupedSectionPreview(), GroupedSectionSamples(), StatusChip(), StatusChipPreview(), StatusChipSamples(), PftTheme() (+7 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.10
Nodes (24): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+16 more)

### Community 50 - "awaitMain"
Cohesion: 0.20
Nodes (3): awaitMain(), FailsOnAppend, SetupViewModelTest

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "AuditFieldsCheckTest.kt"
Cohesion: 0.09
Nodes (14): AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind, LARGE, SMALL, MisMapped (+6 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.21
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "MoneyText"
Cohesion: 0.12
Nodes (11): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText(), RollingDigits() (+3 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.09
Nodes (26): Personal Finance Tracker PRD, Audit trail (INT-2), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope (+18 more)

### Community 59 - "UserProfile"
Cohesion: 0.07
Nodes (14): RoomIdentityRepository, RoomIdentityRepositoryTest, Device, IdentityRepository, UserProfile, AuditEntryId, DeviceId, EntityId (+6 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 62 - "DbKeyUnavailable"
Cohesion: 0.15
Nodes (8): DbKeyUnavailable, Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING

### Community 63 - "PftTypography.kt"
Cohesion: 0.13
Nodes (16): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansWeights(), DisplayFont (+8 more)

### Community 66 - "AppServices"
Cohesion: 0.09
Nodes (33): `data` (Room persistence, A4a), RoomAccountRepository, RoomEntityRepository, RoomTransactionRunner, RoomAppSettingsRepository, RoomTagRepository, AppServices, Configuration (+25 more)

### Community 68 - "HostedComposeRule"
Cohesion: 0.06
Nodes (3): HeaderTags, PftBottomSheetTest, HostedComposeRule

### Community 69 - "AccountPickerTest.kt"
Cohesion: 0.17
Nodes (12): `a row also shows the issuer and last four digits that tell similar accounts apart`(), `a second tap while the sheet is closing picks nothing more`(), `a selection that is not among the offered accounts marks nothing`(), AccountPickerTest, `Back closes the picker without picking`(), badgeLabels(), `lists only the active accounts, sorted by name ignoring case, each with its type badge`(), rowNames() (+4 more)

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 76 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.11
Nodes (9): SectionContent(), BottomActionBarPreview(), BottomActionBarSamples(), DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest (+1 more)

### Community 77 - "7. Test plan"
Cohesion: 0.15
Nodes (9): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 3.4 Header glow (UI-13, DS6d), 3.8 Abstraction bar (+1 more)

### Community 78 - "GroupedSectionTest"
Cohesion: 0.05
Nodes (11): CategoryColors, CategoryPalette, pair(), FieldStateTest, GroupedSectionTest, CategoryPaletteTest, PftSymbolTest, Adding an icon (+3 more)

### Community 80 - "SampleId"
Cohesion: 0.16
Nodes (9): AuberginePalette, GraphitePalette, PaletteColors, MidnightPalette, AccentHueTest, hueDistance(), labHue(), Colour (+1 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.11
Nodes (18): 11. Rollout & verification, 12. Open questions, 1. Requirement, 3.1 Visual direction: "Cashew, but premium", 3.3 Tokens, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins") (+10 more)

### Community 82 - "MetaColumns"
Cohesion: 0.18
Nodes (4): MetaColumns, MetaMapper, MetaMapperTest, RoomCategoryMapperTest

### Community 84 - "7. Test plan"
Cohesion: 0.07
Nodes (43): `di` and startup (A5b), Purpose, AppContainer, AppStart, KeyUnavailable, Ready, SafeLog, PftApplication (+35 more)

### Community 85 - "MigrationTest"
Cohesion: 0.06
Nodes (6): AuditTriggers, MigrationTestSupport, RegisterHostActivity, Statement, MigrationTest, The audit trail

### Community 89 - "LedgerError"
Cohesion: 0.10
Nodes (34): `ui/common` (shared components, DS3, DS4, DS5 and A5d), `ui/setup` and the UiState convention (A5e), Duplicate, FieldError, InvalidAmount, InvalidLastFour, Required, TooLong (+26 more)

### Community 90 - "instant"
Cohesion: 0.10
Nodes (5): EpochMillis, CategoryTreeTest, ValidationTest, Widget, 7. Test plan

### Community 92 - "GroupedSection.kt"
Cohesion: 0.12
Nodes (10): EntityRowContent(), EntitySection(), RowMenu(), GroupedRow, GroupedSectionColors, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags (+2 more)

### Community 94 - "DesignCatalogTest"
Cohesion: 0.06
Nodes (9): ChoiceRow(), DesignCatalog(), DevHeaderActions(), devRoutes(), HeaderIconButton(), DevHeaderActions(), HeaderIconButtonTest, DesignCatalogTest (+1 more)

### Community 95 - "DatabaseFactory"
Cohesion: 0.14
Nodes (3): EncryptedMigrationTest, DatabaseFactory, open()

### Community 98 - "AccountEditScreenTest"
Cohesion: 0.10
Nodes (8): AccountEditActions, AccountEditRoute(), AccountEditScreen(), amountLabelRes(), LastFourField(), TypeSelector(), AccountEditScreenshotTest, AccountEditScreenTest

### Community 99 - "PftRootTest.kt"
Cohesion: 0.05
Nodes (6): MainActivityOpeningTest, CategoriesScreenshotTest, FormScreenshotTest, PftRootTest, SetupScreenshotTest, DataUnavailableScreenTest

### Community 101 - "SampleThing.kt"
Cohesion: 0.21
Nodes (6): SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 105 - "13. Deviations from the plan, by slice"
Cohesion: 0.20
Nodes (5): `ui/categories` (B4a), CategoriesNotice, CategoriesViewModel, DeleteBlocked, Failed

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 112 - "InMemoryKekSource"
Cohesion: 0.13
Nodes (3): AndroidKeystoreKek, KekSource, InMemoryKekSource

### Community 113 - "CatalogSection"
Cohesion: 0.14
Nodes (14): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+6 more)

### Community 114 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.17
Nodes (20): moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense (+12 more)

### Community 115 - "TokenSheetSections.kt"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 117 - "Category"
Cohesion: 0.05
Nodes (46): CategoryMapper, RoomCategoryRepository, CategoriesActions, CategoriesPreview(), CategoriesSample(), sample(), sampleCategoriesState(), CategoriesUiState (+38 more)

### Community 118 - "DatabaseStartupTest.kt"
Cohesion: 0.19
Nodes (4): DatabaseStartup, KeyUnavailable, Ready, DatabaseStartupTest

### Community 119 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.25
Nodes (5): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you

### Community 120 - "ManifestHardeningTest"
Cohesion: 0.05
Nodes (5): MainActivityStartupTest, ManifestHardeningTest, AccountPickerScreenshotTest, WindowThemeTest, 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.09
Nodes (13): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+5 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.12
Nodes (9): PreMigrationBackup, `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`() (+1 more)

### Community 123 - "shouldbe"
Cohesion: 0.07
Nodes (30): RoomAuditLog, TestDatabase, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE (+22 more)

### Community 128 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.17
Nodes (13): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Budgets (B-1..B-5), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID) (+5 more)

### Community 129 - "RoomAppSettingsRepositoryTest.kt"
Cohesion: 0.16
Nodes (3): RoomAppSettingsRepositoryTest, AppSetting, InMemoryAppSettingsRepository

### Community 130 - "Leaky"
Cohesion: 0.21
Nodes (5): Fixture, Leaky, LeakyRepository, Note, NoteId

### Community 132 - "HomeViewModelTest.kt"
Cohesion: 0.07
Nodes (24): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 3.1 The model: every transaction has one or more lines, 3.2 Lines are ledger entities, written only by `TransactionService`, 3.4 Reads, 3.6 Configuration (CLAUDE.md §3.5) (+16 more)

### Community 138 - "TestClock"
Cohesion: 0.10
Nodes (14): `:app` — Android application, Data it owns, Dependencies, Manifest security posture (SEC-2, SEC-3, SEC-6), Responsibilities and boundaries, EpochMillisTest, NameBasedId, TestClock (+6 more)

### Community 142 - "Uuid7Test.kt"
Cohesion: 0.05
Nodes (22): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, AccountServiceTest, AuditDiffTest, Thing (+14 more)

### Community 143 - "NavTransitionTest.kt"
Cohesion: 0.23
Nodes (11): toRow(), toRow(), EntityListEmptyPreview(), EntityListEmptySample(), EntityListSample(), glyph(), sampleAction(), sampleRow() (+3 more)

### Community 144 - "PeriodStepper"
Cohesion: 0.38
Nodes (10): CollapsingHeader(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderTitle(), LowerBand(), PeriodStepper() (+2 more)

### Community 145 - "6.1 Slices"
Cohesion: 0.06
Nodes (33): ChipColors, MoneyColors, colors(), ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT (+25 more)

### Community 147 - ".format"
Cohesion: 0.18
Nodes (10): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+2 more)

### Community 149 - "CategoryAction"
Cohesion: 0.28
Nodes (8): CategoryAction, ARCHIVE, DELETE, MOVE_DOWN, MOVE_UP, UNARCHIVE, CategoryItem, toItems()

### Community 150 - "SetupScreen"
Cohesion: 0.12
Nodes (8): HomeRoute(), HomeScreen(), accountRoutes(), Routes, SetupRoute(), SetupScreen(), Where it lives, Step 13 — `SetupScreen` (A5e)

### Community 154 - "DbKeyManager"
Cohesion: 0.15
Nodes (12): DbKeyManager, Decoded, WrappedKeyFormat, Audit triggers, Corruption handling, Migrations on the encrypted file (`EncryptedMigrationTest`), Persistence and security, SQLCipher (+4 more)

### Community 163 - "MainActivity"
Cohesion: 0.33
Nodes (3): AccountBadge, AccountRowMapper, AccountRowMapperTest

### Community 164 - "Categories and tags"
Cohesion: 0.11
Nodes (16): Code Structure Map, core/shared Shared Packages, Categories and tags, Rules the app already enforces, Tags, The categories list, The categories you start with, What a category is (+8 more)

### Community 170 - "TextLayoutSupport.kt"
Cohesion: 0.47
Nodes (3): assertShownInFull(), isShownInFull(), layout()

### Community 173 - "Accounts"
Cohesion: 0.14
Nodes (13): Accounts, Adding and editing an account, Archive or delete?, Choosing an account (the account picker), Configuration, If saving fails, If something goes wrong, If the account is gone (+5 more)

### Community 174 - "PftShapesTest.kt"
Cohesion: 0.08
Nodes (23): `security` and the encrypted open (A4b), PreMigrationBackupFailed, DataUnavailableCause, IdentityCorrupt, KeyUnavailable, What logs may contain, 10. Risks & mitigations, 11. Rollout & verification (+15 more)

### Community 175 - "HeaderPolicy"
Cohesion: 0.14
Nodes (17): GlyphSize, Chip, Hero, Row, TokenSheet(), TokenSheetPreview(), TokenSheetPage, TokenSheetPageFrame() (+9 more)

### Community 176 - "CategoryColourTest.kt"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 179 - "Architecture overview"
Cohesion: 0.33
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 181 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 182 - "HeaderStyle"
Cohesion: 0.67
Nodes (3): HeaderStyle, Dashboard, Standard

### Community 190 - "EntityAction"
Cohesion: 0.20
Nodes (7): EntityAction, ARCHIVE, DELETE, REORDER, RESTORE, UNARCHIVE, EntityActionTextTest

### Community 191 - "PaletteScreenshotTest.kt"
Cohesion: 0.17
Nodes (6): IvoryPalette, BackgroundStyle, FLAT, TONAL, PftSurfaces, BottomActionBarSurfaceTest

### Community 195 - "UserProfileEntity"
Cohesion: 0.24
Nodes (3): IdentityDao, DeviceEntity, UserProfileEntity

### Community 196 - "How to run and test"
Cohesion: 0.13
Nodes (5): How to run and test, RoomCategoryForeignKeyTest, CategoryIconTest, CategoryListsTest, Where it lives

### Community 201 - "PaletteSamples.kt"
Cohesion: 0.19
Nodes (5): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch(), PaletteSwatchesTest

### Community 202 - "GlyphSource"
Cohesion: 0.31
Nodes (5): toGlyphSource(), Emoji, GlyphSource, Letter, Symbol

### Community 206 - "Theme"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

## Knowledge Gaps
- **233 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+228 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 706 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **99 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `runSuspending`, `MoneyTextTest`, `ColorContrastTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest.kt`, `CategoryGlyph.kt`, `FieldState`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `DbKeyManagerJvmTest`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `PreMigrationBackupTest`, `Public interface`, `LaunchViewModelTest.kt`, `PftMotion.kt`, `PftMotion.kt`, `PftTheme`, `awaitMain`, `PftThemeModeTest`, `RoomAuditColumnsTest`, `ContentScreenshotTest`, `DbKeyManagerTest.kt`, `MoneyText`, `UserProfile`, `AppServices`, `GroupedSectionColorsTest`, `AuditTriggersTest`, `AppContainerTest.kt`, `RoomCategoryIntegrationTest`, `Previews, the token sheet and screenshot goldens`, `GroupedSectionTest`, `SampleId`, `MetaColumns`, `RoomAccountIntegrationTest`, `7. Test plan`, `MigrationTest`, `RoomAuditedWriterIntegrationTest`, `CategoryGlyphScreenshotTest`, `DesignCatalogTest`, `DatabaseFactory`, `PftRootTest.kt`, `MoneyToneTest`, `SampleThing.kt`, `IdentityRoomIntegrationTest`, `SetupFlowTest.kt`, `SetupScreenTest.kt`, `BundledFontTest`, `getvalue`, `AuditEntryMapperTest.kt`, `LogEventShapeTest`, `DatabaseStartupTest.kt`, `ManifestHardeningTest`, `shouldbe`, `SampleId`, `RoomTransactionRunnerTest`, `NameFieldTest.kt`, `RoomAppSettingsRepositoryTest.kt`, `PftDatabase`, `AuditDaoTest.kt`, `WindowThemeTest`, `ThemePrefsTest`, `TestClock`, `DbKeyManagerWiringTest.kt`, `6.1 Slices`, `ReleaseHasNoCatalogueTest.kt`, `.format`, `LaunchRoutingTest.kt`, `Collecting and Redacting Sample Messages Guide`, `SetupRouteTest.kt`, `ThemePaletteTest`, `assertequals`, `PftSpacingTest`, `.check`, ``ui/common` (shared components, DS3, DS4, DS5 and A5d)`, `EditFormScaffoldTest`, `assertisdisplayed`, `BundledSymbolsLicenceTest`, `6. Implementation steps (TDD-ordered)`, `PftBottomSheetTest.kt`, `TokenSheetScreenshotTest`, `PaletteScreenshotTest.kt`, `PaletteScreenshotTest.kt`, `HeaderSlot`?**
  _High betweenness centrality (0.214) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _233 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `WindowThemeTest`, `ThemePrefsTest`, `MoneyTextTest`, `UndoDeleteSnackbarTest.kt`, `ColorContrastTest`, `NavTransitionTest.kt`, `6.1 Slices`, `ReleaseHasNoCatalogueTest.kt`, `LaunchRoutingTest.kt`, `SetupRouteTest.kt`, `DesignCatalog.kt`, `AmountFieldTest`, `CollapsingHeaderScaffoldTest.kt`, `CategoryGlyph.kt`, `PftSpacingTest`, `CategoriesScreen.kt`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `EditFormScaffoldTest`, `CategoryGlyphSamples`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `assertisdisplayed`, `AccountsFlowTest.kt`, `PftBottomSheetTest.kt`, `PftMotion.kt`, `PftMotion.kt`, `HeaderTypePairingTest.kt`, `HeaderPolicy`, `HeaderGlowScaffoldTest`, `CollapsingHeaderScaffold.kt`, `Architecture overview`, `PftThemeModeTest`, `ContentScreenshotTest`, `AccountsScreenTest.kt`, `MoneyText`, `TokenSheetScreenshotTest`, `PftTypography.kt`, `PaletteScreenshotTest.kt`, `EntityListScreenTest.kt`, `expandedHeaderHeight`, `HostedComposeRule`, `AccountPickerTest.kt`, `PaletteScreenshotTest.kt`, `PaletteSamples.kt`, `Previews, the token sheet and screenshot goldens`, `7. Test plan`, `GroupedSectionTest`, `AccountPickerTest.kt`, `SampleId`, `Plan: M1a design system and visual direction (lands before A5)`, `7. Test plan`, `CategoryGlyphScreenshotTest`, `GroupedSection.kt`, `EncryptedMigrationTest`, `DesignCatalogTest`, `instant`, `AccountEditScreenTest`, `PftRootTest.kt`, `SetupFlowTest.kt`, `SetupScreenTest.kt`, `getvalue`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `Category`, `ManifestHardeningTest`, `AccountsRouteTest.kt`, `NameFieldTest.kt`?**
  _High betweenness centrality (0.108) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._