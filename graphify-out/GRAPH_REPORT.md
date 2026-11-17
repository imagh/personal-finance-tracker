# Graph Report - personal-finance-tracker  (2026-11-16)

## Corpus Check
- 450 files · ~329,927 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 5311 nodes · 17188 edges · 206 communities (97 shown, 109 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 2241 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `f42975c9`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- MotionSection.kt
- CategoryEditViewModelTest
- Automatic capture from SMS (S-1..S-13)
- RoomTagIntegrationTest
- hooks
- test_import_symbols.py
- Tag
- RoomEntityRepository
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ThemePrefs
- PftKotlinCorePlugin.kt
- CategoriesActionsTest
- Catalog.kt
- detekt.yml
- AccountEditScreen.kt
- PftSymbol
- CollapsingHeaderScaffoldTest.kt
- PftBottomSheetTest.kt
- CategoryEntity
- CategoriesViewModel.kt
- getvalue
- CollapsingHeaderScaffold
- DbKeyManagerJvmTest
- ColorContrastTest
- CategoryEditScreenTest.kt
- PftTypographyTest
- CategoryGlyph
- PreMigrationBackupTest
- AccountPickerTest.kt
- LaunchViewModelTest.kt
- Layered Modular Architecture (core vs app)
- AccountId
- PftMotion.kt
- HeaderTypePairingTest.kt
- ThemePrefs
- CLAUDE.md Development Constitution
- HeaderGlowScaffoldTest.kt
- CollapsingHeaderScaffold.kt
- awaitMain
- FontBudgetTest
- materialtheme
- RoomAuditColumnsTest
- EntityMeta
- localdensity
- DbKeyManagerTest
- MoneyText
- Personal Finance Tracker PRD
- UserId
- redact_text
- AccountEditViewModelTest
- assertisdisplayed
- PftTypography.kt
- Pseudo
- EntityListScreenTest.kt
- Public interface
- 6.1 Slices
- DesignCatalog.kt
- CategoryPickerTest.kt
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
- Plan: M1a design system and visual direction (lands before A5)
- RoomCategoryMapperTest
- RoomAccountIntegrationTest
- AppServices
- MigrationTest
- RoomAuditedWriterIntegrationTest
- box
- TabularFiguresTest
- LedgerError
- LogEvent
- AccountsViewModelTest
- GroupedSection.kt
- CategoriesFlowTest
- PftTheme
- DatabaseFactory
- onnodewithtext
- CategoriesViewModelTest
- AccountEditScreenTest.kt
- PftRootTest.kt
- Paise
- SampleThing.kt
- AppSettingEntity
- SetupFlowTest.kt
- AccountEditViewModel.kt
- parse_svg
- SetupScreenTest.kt
- BundledFontTest
- BottomActionBarTest.kt
- ImportRunTest
- AuditEntryMapperTest
- InMemoryKekSource
- CatalogSection
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Collecting and Redacting Sample Messages Guide
- InMemoryEntityRepository
- Category
- DatabaseStartupTest.kt
- SymbolGridTest.kt
- WindowThemeTest
- EncryptedDatabaseTest.kt
- IdentityRoomIntegrationTest
- shouldbe
- AccountsRouteTest.kt
- SampleId
- RoomTransactionRunnerTest
- AccountType
- CategoriesRouteTest.kt
- runSuspending
- ColourSwatchesTest.kt
- Where it lives
- How to run and test
- modifier
- AuditDao
- Personal Finance Tracker Learnings
- WindowBackgroundTest.kt
- DevRouteTest.kt
- AccountEntity
- BaseDao
- DbKeyManagerWiringTest.kt
- UndoDeleteSnackbarTest.kt
- TestClock
- hastext
- MainActivityOpeningTest.kt
- PftSymbolCategoriesTest
- .append
- How to run and test
- Immutable Audit Trail
- HomeViewModelTest.kt
- SetupRouteTest.kt
- ThemePalette
- DbKeyManager
- GlyphSourceTest
- LogEventTest
- PftSurfacesTest
- HeaderIconButton
- PftSymbolTest
- setvalue
- EditFormScaffoldTest.kt
- Project README
- AccountsFlowTest.kt
- performclick
- BundledSymbolsLicenceTest
- AppServicesTest
- mutablestateof
- PaiseParser
- CategoryRowMapperTest
- Accounts
- Plan: M1a — Core ledger (manual-entry household tracker)
- TokenSheetSections.kt
- .append
- .append
- AccountsScreenTest.kt
- testtag
- FieldState
- CategoryColourTest.kt
- CategoryErrorsTest.kt
- CategoryPickerScreenshotTest.kt
- AccountEditScreenshotTest.kt
- MoreScreenTest.kt
- UserProfileEntity
- CategoriesScreenshotTest.kt
- PaletteScreenshotTest.kt
- PaiseParserTest.kt
- LoggingScanTest
- onnodewithtag
- SetupScreenshotTest.kt
- .check
- ManifestHardeningTest
- AccountPickerScreenshotTest
- LaunchSaveTest
- .append
- Slice A — Foundation (`feat/m1a-foundation`)
- StatusChipTest.kt
- .event
- FormScreenshotTest.kt
- CategoryIconGlyphTest
- Icons and emoji (DS3, UI-6)
- PaletteSamples.kt
- ThemeLiteralScanTest
- Categories and tags
- Security, privacy and integrity requirements (SEC/INT)
- SpokenName.kt

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 260 edges
2. `ThemePrefs` - 219 edges
3. `Paise` - 149 edges
4. `PftSymbol` - 144 edges
5. `HostedComposeRule` - 140 edges
6. `runSuspending()` - 125 edges
7. `awaitMain()` - 116 edges
8. `How to run and test` - 113 edges
9. `Category` - 89 edges
10. `CategoryId` - 88 edges

## Surprising Connections (you probably didn't know these)
- `3.5 Fonts: bundling, licensing, custom import` --references--> `SafeLog`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/log/SafeLog.kt
- `The category picker` --references--> `CategoryPicker()`  [INFERRED]
  docs/features/categories-and-tags.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/CategoryPicker.kt
- `13. Deviations from the plan, by slice` --references--> `asHeroTop()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderGlow.kt
- `10. Risks & mitigations` --references--> `MoneyText()`  [INFERRED]
  docs/plans/2026-11-10-itemised-transactions.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/MoneyText.kt
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

## Communities (206 total, 109 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "MotionSection.kt"
Cohesion: 0.04
Nodes (14): AccentCheckSection(), MotionSection(), SpringTrack(), ColourSwatches(), SymbolGrid(), AccountPickerRow(), fixedSp(), CategoryCell() (+6 more)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "Tag"
Cohesion: 0.10
Nodes (13): RoomTagRepository, TagEntity, TagMapper, TagId, Tag, TagLists, TagRepository, isNameTaken() (+5 more)

### Community 10 - "MoneyTextTest"
Cohesion: 0.10
Nodes (5): layoutResult(), MoneyTextTest, shownText(), Spec, Paise

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ThemePrefs"
Cohesion: 0.10
Nodes (4): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemeResolutionTest

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "AccountEditScreen.kt"
Cohesion: 0.07
Nodes (7): AccountEditRoute(), AccountEditScreen(), amountLabelRes(), LastFourField(), TypeSelector(), GroupedSectionPreview(), NameField()

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (94): Symbol, PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS (+86 more)

### Community 28 - "CollapsingHeaderScaffoldTest.kt"
Cohesion: 0.08
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 31 - "CategoriesViewModel.kt"
Cohesion: 0.05
Nodes (16): EntityAction, ARCHIVE, DELETE, REORDER, RESTORE, UNARCHIVE, CategoriesNotice, CategoriesViewModel (+8 more)

### Community 32 - "getvalue"
Cohesion: 0.05
Nodes (31): AccountsActions, AccountsNavigation, AccountsRoute(), AccountsScreen(), typeLabelRes(), CategoriesActions, CategoriesNavigation, actionLabelRes() (+23 more)

### Community 33 - "CollapsingHeaderScaffold"
Cohesion: 0.11
Nodes (46): `ui/more` and `ui/accounts` (B2a, B2b), `ui/nav` and `ui/home` (A5f), MainActivity, AccountPicker(), AmountField(), BottomActionBar(), CategoryPicker(), CollapsingHeaderScaffold() (+38 more)

### Community 35 - "ColorContrastTest"
Cohesion: 0.09
Nodes (13): Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone(), roles() (+5 more)

### Community 37 - "PftTypographyTest"
Cohesion: 0.11
Nodes (3): sansFamily(), PftTypographyTest, Resolved

### Community 40 - "AccountPickerTest.kt"
Cohesion: 0.19
Nodes (12): `a row also shows the issuer and last four digits that tell similar accounts apart`(), `a second tap while the sheet is closing picks nothing more`(), `a selection that is not among the offered accounts marks nothing`(), AccountPickerTest, `Back closes the picker without picking`(), badgeLabels(), `lists only the active accounts, sorted by name ignoring case, each with its type badge`(), rowNames() (+4 more)

### Community 42 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "AccountId"
Cohesion: 0.11
Nodes (10): RoomAccountRepository, AccountsUiState, AccountsViewModel, Account, AccountDraft, AccountLists, AccountRepository, AccountService (+2 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.09
Nodes (5): iosSpring(), PftMotion, pressScale(), PftMotionTest, 7. Test plan

### Community 45 - "HeaderTypePairingTest.kt"
Cohesion: 0.11
Nodes (4): HeaderTypePairingTest, assertShownInFull(), isShownInFull(), layout()

### Community 46 - "ThemePrefs"
Cohesion: 0.14
Nodes (3): ThemePrefs, EmptyStateTest, ThemePrefsTest

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "HeaderGlowScaffoldTest.kt"
Cohesion: 0.07
Nodes (3): BottomActionBarSurfaceTest, HeaderGlowScaffoldTest, PressScaleTest

### Community 49 - "CollapsingHeaderScaffold.kt"
Cohesion: 0.05
Nodes (29): CollapsingHeader(), expandedHeaderHeight(), HeaderChildren(), HeaderContent, HeaderPeriod, HeaderPhase, Collapsed, Expanded (+21 more)

### Community 50 - "awaitMain"
Cohesion: 0.20
Nodes (3): awaitMain(), FailsOnAppend, SetupViewModelTest

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.05
Nodes (34): MetaColumns, sampleCategory(), MetaMapperTest, AuditedTable, AuditedTables, EntityMeta, LedgerEntity, DeviceId (+26 more)

### Community 55 - "localdensity"
Cohesion: 0.22
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "MoneyText"
Cohesion: 0.09
Nodes (25): Dependencies, MoneySize, Hero, Large, atSize(), directionLabel(), Figure(), fitHero() (+17 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.08
Nodes (32): Personal Finance Tracker PRD, Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Automatic capture from Email (E-0..E-5) (+24 more)

### Community 59 - "UserId"
Cohesion: 0.10
Nodes (6): RoomIdentityRepository, RoomIdentityRepositoryTest, Device, IdentityRepository, UserProfile, InMemoryIdentityRepository

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 62 - "assertisdisplayed"
Cohesion: 0.10
Nodes (9): Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING, CategoryEditScreenshotTest (+1 more)

### Community 63 - "PftTypography.kt"
Cohesion: 0.08
Nodes (33): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), sansWeights(), DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF (+25 more)

### Community 65 - "EntityListScreenTest.kt"
Cohesion: 0.09
Nodes (10): AccountBadge, AccountRowMapper, toRow(), toRow(), sampleAction(), EntityRow, RowGlyph, RowAction (+2 more)

### Community 66 - "Public interface"
Cohesion: 0.05
Nodes (37): RoomTransactionRunner, RoomAppSettingsRepository, Configuration, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), AuditDiff (+29 more)

### Community 67 - "6.1 Slices"
Cohesion: 0.19
Nodes (7): GroupedSectionColors, BackgroundStyle, FLAT, TONAL, PftSurfaces, Case, GroupedSectionColorsTest

### Community 68 - "DesignCatalog.kt"
Cohesion: 0.11
Nodes (8): ChoiceRow(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), StatusChipPreview(), StatusChipSamples(), TokenSheet(), TokenSheetPreview()

### Community 69 - "CategoryPickerTest.kt"
Cohesion: 0.21
Nodes (15): `a cell speaks its name once, and its badge says nothing`(), `a second tap while the sheet is closing picks nothing more`(), `a selection that is not offered marks nothing`(), `a sub-category says whose it is, so two with the same name can be told apart`(), `Back closes the picker without picking`(), CategoryPickerTest, cellNames(), `cells are one radio group, so TalkBack presents them as a set of choices`() (+7 more)

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 76 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.15
Nodes (8): SectionContent(), BottomActionBarSamples(), DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 77 - "7. Test plan"
Cohesion: 0.13
Nodes (10): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 1. Requirement, 3.4 Header glow (UI-13, DS6d) (+2 more)

### Community 80 - "assertequals"
Cohesion: 0.09
Nodes (9): AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, AccentHueTest, hueDistance(), labHue() (+1 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.07
Nodes (24): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions (+16 more)

### Community 84 - "AppServices"
Cohesion: 0.07
Nodes (51): `:app` — Android application, Data it owns, Debug and release source sets, `di` and startup (A5b), Public interface, Purpose, AppContainer, AppServices (+43 more)

### Community 85 - "MigrationTest"
Cohesion: 0.07
Nodes (7): AuditTriggers, MigrationTestSupport, RegisterHostActivity, Statement, MigrationTest, The audit trail, Audit triggers

### Community 87 - "box"
Cohesion: 0.24
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 89 - "LedgerError"
Cohesion: 0.07
Nodes (37): `ui/categories` (B4a, B4b), `ui/common` (shared components, DS3, DS4, DS5 and A5d), `ui/setup` and the UiState convention (A5e), Duplicate, FieldError, InvalidAmount, InvalidLastFour, Required (+29 more)

### Community 90 - "LogEvent"
Cohesion: 0.07
Nodes (17): `log` (safe logging, A5a), DbKeyUnavailable, DbOpened, EntityActionFailed, EntitySaveFailed, IdentityCorrupt, LogEvent, LogValue (+9 more)

### Community 92 - "GroupedSection.kt"
Cohesion: 0.16
Nodes (6): GroupedRow, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, MoreRow(), MoreScreen()

### Community 94 - "PftTheme"
Cohesion: 0.13
Nodes (5): DesignCatalog(), BottomActionBarPreview(), PftTheme(), DesignCatalogTest, Step 11 — `NameField` and `EditFormScaffold` (A5d)

### Community 96 - "onnodewithtext"
Cohesion: 0.20
Nodes (6): EntityListEmptyPreview(), EntityListEmptySample(), EntityListSample(), glyph(), sampleRow(), EntityListScreenshotTest

### Community 101 - "SampleThing.kt"
Cohesion: 0.26
Nodes (4): SampleDatabase, SampleThingDao, SampleThingEntity, SampleThingMapper

### Community 105 - "AccountEditViewModel.kt"
Cohesion: 0.12
Nodes (7): AccountEditUiState, AccountEditViewModel, SaveOutcome, FAILED, SAVED, VANISHED, AccountRules

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 113 - "CatalogSection"
Cohesion: 0.14
Nodes (14): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+6 more)

### Community 114 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.10
Nodes (26): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), Letter, Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER (+18 more)

### Community 115 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 116 - "InMemoryEntityRepository"
Cohesion: 0.14
Nodes (6): Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 117 - "Category"
Cohesion: 0.04
Nodes (51): `data` (Room persistence, A4a), CategoryMapper, RoomCategoryRepository, MetaMapper, archivedSamples(), CategoriesPreview(), sampleCategoriesState(), sampleCategory() (+43 more)

### Community 118 - "DatabaseStartupTest.kt"
Cohesion: 0.16
Nodes (4): DatabaseStartup, KeyUnavailable, Ready, DatabaseStartupTest

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.09
Nodes (13): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+5 more)

### Community 123 - "shouldbe"
Cohesion: 0.06
Nodes (36): RoomAuditLog, EpochMillis, AuditDaoTest, TestDatabase, FieldChange, AuditAction, ARCHIVE, CREATE (+28 more)

### Community 125 - "SampleId"
Cohesion: 0.22
Nodes (4): RoomEntityRepositoryTest, SampleId, SampleThing, SampleThingRepository

### Community 127 - "AccountType"
Cohesion: 0.18
Nodes (10): AccountPickerEmptyPreview(), AccountPickerEmptySample(), AccountPickerSample(), sampleAccount(), AccountType, BANK, CASH, CREDIT_CARD (+2 more)

### Community 129 - "runSuspending"
Cohesion: 0.21
Nodes (3): RoomAppSettingsRepositoryTest, runSuspending(), 3.3 Dependencies (one pins-only PR, A5c)

### Community 132 - "How to run and test"
Cohesion: 0.05
Nodes (34): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, AuditDiffTest, Thing, IdsTest (+26 more)

### Community 133 - "modifier"
Cohesion: 0.19
Nodes (3): DevHeaderActions(), devRoutes(), DevHeaderActions()

### Community 135 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 136 - "WindowBackgroundTest.kt"
Cohesion: 0.11
Nodes (7): Window theme, SyncWindowBackground(), toneOf(), WindowBackgroundTest, WindowFlashToneTest, 5. Flows, 8. Security considerations

### Community 138 - "AccountEntity"
Cohesion: 0.13
Nodes (6): AccountDao, AccountEntity, AccountMapper, EntityMapper, RoomAccountMapperTest, Version 2 (B1c)

### Community 142 - "TestClock"
Cohesion: 0.09
Nodes (10): EpochMillisTest, NameBasedId, idOf(), clockAt(), Uuid7Test, TestClock, Conventions, Data model (+2 more)

### Community 148 - "How to run and test"
Cohesion: 0.06
Nodes (6): How to run and test, PftDatabase, RoomCategoryForeignKeyTest, LaunchRoutingTest, ReleaseHasNoCatalogueTest, CategoryIconTest

### Community 153 - "ThemePalette"
Cohesion: 0.12
Nodes (10): colors(), ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, paletteFor(), ChipColorsTest (+2 more)

### Community 154 - "DbKeyManager"
Cohesion: 0.10
Nodes (13): DatabaseFactory, DbKeyManager, Decoded, WrappedKeyFormat, `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory() (+5 more)

### Community 159 - "HeaderIconButton"
Cohesion: 0.19
Nodes (5): HeaderIconButton(), HeaderIconButtonTest, Planned: premium look (DS6), 6.2 PR breakdown (incremental — required), 9. Documentation updates (all required surfaces)

### Community 161 - "PftSymbolTest"
Cohesion: 0.19
Nodes (6): toGlyphSource(), PftSymbolTest, Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 164 - "Project README"
Cohesion: 0.15
Nodes (10): Code Structure Map, core/shared Shared Packages, Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen, What it does for you, Behaviour details, First run and profiles (+2 more)

### Community 165 - "AccountsFlowTest.kt"
Cohesion: 0.07
Nodes (3): AmountFieldTest, NameFieldTest, AccountsFlowTest

### Community 172 - "CategoryRowMapperTest"
Cohesion: 0.22
Nodes (9): CategoryAction, ARCHIVE, DELETE, MOVE_DOWN, MOVE_UP, UNARCHIVE, CategoryItem, toItems() (+1 more)

### Community 173 - "Accounts"
Cohesion: 0.14
Nodes (13): Accounts, Adding and editing an account, Archive or delete?, Choosing an account (the account picker), Configuration, If saving fails, If something goes wrong, If the account is gone (+5 more)

### Community 174 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.08
Nodes (22): AccountServiceTest, TagServiceTest, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 3.1 Modules (Gradle) (+14 more)

### Community 175 - "TokenSheetSections.kt"
Cohesion: 0.17
Nodes (15): GlyphSize, Chip, Hero, Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers() (+7 more)

### Community 186 - "FieldState"
Cohesion: 0.24
Nodes (3): FieldCheck, FieldState, FieldStateTest

### Community 195 - "UserProfileEntity"
Cohesion: 0.24
Nodes (3): IdentityDao, DeviceEntity, UserProfileEntity

### Community 199 - "PaiseParserTest.kt"
Cohesion: 0.17
Nodes (4): How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest

### Community 200 - "LoggingScanTest"
Cohesion: 0.26
Nodes (4): Configuration, LoggingScanTest, SourceScan, Violation

### Community 206 - "ManifestHardeningTest"
Cohesion: 0.09
Nodes (9): Manifest security posture (SEC-2, SEC-3, SEC-6), Responsibilities and boundaries, ManifestHardeningTest, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape (+1 more)

### Community 208 - "LaunchSaveTest"
Cohesion: 0.17
Nodes (5): EntityKind, ACCOUNT, CATEGORY, launchSave(), LaunchSaveTest

### Community 211 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.09
Nodes (17): `security` and the encrypted open (A4b), PreMigrationBackup, PreMigrationBackupFailed, DataUnavailableCause, IdentityCorrupt, KeyUnavailable, NameBasedIdTest, Corruption handling (+9 more)

### Community 217 - "Icons and emoji (DS3, UI-6)"
Cohesion: 0.52
Nodes (3): CategoryColors, CategoryPalette, pair()

### Community 218 - "PaletteSamples.kt"
Cohesion: 0.50
Nodes (4): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 221 - "Categories and tags"
Cohesion: 0.18
Nodes (10): CategoryListsTest, Categories and tags, Creating and editing a category, Rules the app already enforces, Tags, The categories list, The categories you start with, The category picker (+2 more)

### Community 224 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

## Knowledge Gaps
- **233 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+228 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 694 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **109 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `RoomTagIntegrationTest`, `MoneyTextTest`, `ThemePrefs`, `PftSymbol`, `CollapsingHeaderScaffoldTest.kt`, `PftBottomSheetTest.kt`, `CollapsingHeaderScaffold`, `DbKeyManagerJvmTest`, `ColorContrastTest`, `PftTypographyTest`, `CategoryGlyph`, `PreMigrationBackupTest`, `LaunchViewModelTest.kt`, `AccountId`, `PftMotion.kt`, `ThemePrefs`, `HeaderGlowScaffoldTest.kt`, `CollapsingHeaderScaffold.kt`, `awaitMain`, `materialtheme`, `RoomAuditColumnsTest`, `EntityMeta`, `localdensity`, `DbKeyManagerTest`, `MoneyText`, `UserId`, `assertisdisplayed`, `Public interface`, `6.1 Slices`, `AuditTriggersTest`, `AppContainerTest.kt`, `RoomCategoryIntegrationTest`, `Previews, the token sheet and screenshot goldens`, `GroupedSectionTest.kt`, `assertequals`, `RoomCategoryMapperTest`, `RoomAccountIntegrationTest`, `AppServices`, `MigrationTest`, `RoomAuditedWriterIntegrationTest`, `box`, `LogEvent`, `PftTheme`, `DatabaseFactory`, `PftRootTest.kt`, `Paise`, `SetupFlowTest.kt`, `SetupScreenTest.kt`, `BundledFontTest`, `BottomActionBarTest.kt`, `AuditEntryMapperTest`, `DatabaseStartupTest.kt`, `WindowThemeTest`, `IdentityRoomIntegrationTest`, `shouldbe`, `SampleId`, `RoomTransactionRunnerTest`, `runSuspending`, `WindowBackgroundTest.kt`, `DevRouteTest.kt`, `AccountEntity`, `DbKeyManagerWiringTest.kt`, `MainActivityOpeningTest.kt`, `HomeViewModelTest.kt`, `SetupRouteTest.kt`, `ThemePalette`, `DbKeyManager`, `GlyphSourceTest`, `LogEventTest`, `PftSurfacesTest`, `PftSymbolTest`, `setvalue`, `EditFormScaffoldTest.kt`, `AccountsFlowTest.kt`, `performclick`, `BundledSymbolsLicenceTest`, `AppServicesTest`, `mutablestateof`, `testtag`, `FieldState`, `PaletteScreenshotTest.kt`, `LoggingScanTest`, `SetupScreenshotTest.kt`, `.check`, `ManifestHardeningTest`, `StatusChipTest.kt`, `.event`, `FormScreenshotTest.kt`?**
  _High betweenness centrality (0.176) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _233 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `CategoriesRouteTest.kt`, `MotionSection.kt`, `ColourSwatchesTest.kt`, `WindowBackgroundTest.kt`, `DevRouteTest.kt`, `MoneyTextTest`, `UndoDeleteSnackbarTest.kt`, `ThemePrefs`, `How to run and test`, `SetupRouteTest.kt`, `AccountEditScreen.kt`, `CollapsingHeaderScaffoldTest.kt`, `PftBottomSheetTest.kt`, `HeaderIconButton`, `getvalue`, `CollapsingHeaderScaffold`, `EditFormScaffoldTest.kt`, `CategoryEditScreenTest.kt`, `PftTypographyTest`, `AccountsFlowTest.kt`, `CategoryGlyph`, `AccountPickerTest.kt`, `mutablestateof`, `performclick`, `ColorContrastTest`, `PftMotion.kt`, `HeaderTypePairingTest.kt`, `ThemePrefs`, `HeaderGlowScaffoldTest.kt`, `CollapsingHeaderScaffold.kt`, `materialtheme`, `localdensity`, `AccountsScreenTest.kt`, `MoneyText`, `testtag`, `CategoryPickerScreenshotTest.kt`, `assertisdisplayed`, `PftTypography.kt`, `AccountEditScreenshotTest.kt`, `EntityListScreenTest.kt`, `MoreScreenTest.kt`, `6.1 Slices`, `DesignCatalog.kt`, `CategoriesScreenshotTest.kt`, `CategoryPickerTest.kt`, `PaletteScreenshotTest.kt`, `onnodewithtag`, `SetupScreenshotTest.kt`, `Previews, the token sheet and screenshot goldens`, `7. Test plan`, `GroupedSectionTest.kt`, `CategoriesScreenTest.kt`, `AccountPickerScreenshotTest`, `assertequals`, `ManifestHardeningTest`, `Plan: M1a design system and visual direction (lands before A5)`, `AppServices`, `StatusChipTest.kt`, `FormScreenshotTest.kt`, `box`, `Icons and emoji (DS3, UI-6)`, `PaletteSamples.kt`, `GroupedSection.kt`, `CategoriesFlowTest`, `onnodewithtext`, `AccountEditScreenTest.kt`, `PftRootTest.kt`, `SetupFlowTest.kt`, `SetupScreenTest.kt`, `BottomActionBarTest.kt`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `Category`, `SymbolGridTest.kt`, `AccountsRouteTest.kt`, `AccountType`?**
  _High betweenness centrality (0.122) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._