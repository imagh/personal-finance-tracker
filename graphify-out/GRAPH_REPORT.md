# Graph Report - personal-finance-tracker  (2026-11-15)

## Corpus Check
- 391 files · ~285,396 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 4485 nodes · 13797 edges · 171 communities (90 shown, 81 thin omitted)
- Extraction: 85% EXTRACTED · 15% INFERRED · 0% AMBIGUOUS · INFERRED: 2035 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `33d85f70`
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
- AuditEntryMapperTest.kt
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
- PftShapesTest.kt
- PressScaleTest
- SetupViewModel.kt
- CollapsingHeaderScaffold.kt
- GroupedSectionSamples
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- HeaderGlowScaffoldTest
- PftTypographyTest
- CategoryGlyph
- Loans and EMIs (LN-1..LN-5)
- GlyphSourceTest
- LaunchViewModelTest.kt
- Layered Modular Architecture (core vs app)
- Account
- PftMotion.kt
- HeaderTypePairingTest.kt
- PreMigrationBackup
- CLAUDE.md Development Constitution
- How to run and test
- CategoryGlyphScreenshotTest
- UserId
- FontBudgetTest
- PftThemeModeTest
- RoomAuditColumnsTest
- EntityMeta
- ContentScreenshotTest
- DbKeyManagerTest.kt
- MoneyText
- Personal Finance Tracker PRD
- UserProfile
- redact_text
- awaitMain
- ThemePaletteTest
- PftTypography.kt
- Pseudo
- EntityListScreenTest
- AppServices
- 6.1 Slices
- HostedComposeRule
- ManifestHardeningTest
- _Text
- MainActivityOpeningTest.kt
- import_symbols.py
- AppContainerTest.kt
- RoomCategoryIntegrationTest
- Previews, the token sheet and screenshot goldens
- 7. Test plan
- GroupedSectionTest
- ReleaseHasNoCatalogueTest.kt
- 13. Deviations from the plan, by slice
- Plan: M1a design system and visual direction (lands before A5)
- RedactTests
- RoomAccountIntegrationTest
- LogEvent
- MigrationTest
- RoomAuditedWriterIntegrationTest
- AuditTriggersTest
- TabularFiguresTest
- LedgerError
- HeaderIconButtonTest.kt
- runSuspending
- StatusChip
- EncryptedMigrationTest
- PftTheme
- DbKeyManager
- Public interface
- DataUnavailableScreenTest
- MoneyToneTest
- SampleThing.kt
- IdentityRoomIntegrationTest
- AppSettingEntity
- SetupFlowTest.kt
- parse_svg
- FieldState
- BundledFontTest
- BottomActionBarTest.kt
- ImportRunTest
- SetupScreen.kt
- AndroidKeystoreKek
- CatalogSection
- TokenSheetScreenshotTest.kt
- Collecting and Redacting Sample Messages Guide
- LogEventShapeTest
- CategoryColour
- DatabaseStartupTest.kt
- 3. Design & decision
- parse_manifest
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- shouldbe
- AccountsRouteTest.kt
- Architecture overview
- RoomTransactionRunnerTest
- NameBasedIdTest.kt
- Category
- AppSetting
- .append
- PaiseParser
- PaiseFormatTest.kt
- WindowBackgroundTest.kt
- DevRouteTest.kt
- CategoryEntity
- DbKeyUnavailable
- FormScreenshotTest.kt
- Uuid7Test.kt
- .format
- RoomEntityRepository
- r
- SetupRouteTest.kt
- RoomCategoryForeignKeyTest.kt
- PftDatabase
- Design system
- LogEventTest
- PaletteScreenshotTest.kt
- AppContainer
- .check
- EditFormScaffoldTest.kt
- 2026-11-01-m1a-a5-first-run-setup.md
- .of
- HomeScreenTest.kt
- BundledSymbolsLicenceTest
- .append
- FieldErrorMessageTest.kt
- CategoryPaletteTest
- Categories and tags
- Icons and emoji (DS3, UI-6)
- CategoryKind
- Slice A — Foundation (`feat/m1a-foundation`)
- AuditDaoTest.kt
- Immutable Audit Trail
- ThemeLiteralScanTest
- LaunchRoutingTest.kt
- AccountsScreen.kt
- CategoryColourTest.kt
- CategoryErrorsTest.kt
- Security, privacy and integrity requirements (SEC/INT)
- .fromIconKey

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 212 edges
2. `ThemePrefs` - 179 edges
3. `PftSymbol` - 134 edges
4. `runSuspending()` - 125 edges
5. `Paise` - 121 edges
6. `How to run and test` - 113 edges
7. `HostedComposeRule` - 108 edges
8. `CollapsingHeaderScaffoldTest` - 83 edges
9. `Public interface` - 74 edges
10. `EntityMeta` - 73 edges

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

## Communities (171 total, 81 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "Tag"
Cohesion: 0.13
Nodes (10): RoomTagRepository, TagId, Tag, TagLists, TagRepository, isNameTaken(), TagService, shouldRefuse() (+2 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.25
Nodes (4): Personal Finance Tracker Learnings, graphify tool, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "runSuspending"
Cohesion: 0.20
Nodes (3): RoomTagIntegrationTest, runSuspending(), 3.3 Dependencies (one pins-only PR, A5c)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "AuditEntryMapperTest.kt"
Cohesion: 0.14
Nodes (7): AuditEntryEntity, AuditEntryMapper, AuditEntryMapperTest, Data model, The audit trail, Version 1 (A4a), Version table

### Community 9 - "ThemePrefs"
Cohesion: 0.09
Nodes (6): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemePrefsTest, ThemeResolutionTest

### Community 10 - "MoneyTextTest"
Cohesion: 0.10
Nodes (8): MoneySize, Hero, Large, MoneyTextTest, Spec, Paise, Slice D — Budgets (`feat/m1a-budgets`), 3.5 UI

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.07
Nodes (18): AccentHueTest, Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone() (+10 more)

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.17
Nodes (22): moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense (+14 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "DesignCatalog.kt"
Cohesion: 0.04
Nodes (26): AccentCheckSection(), MotionSection(), SpringTrack(), BottomActionBarPreview(), BottomActionBarSamples(), fixedSp(), EmptyStatePreview(), EmptyStateSamples() (+18 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.05
Nodes (39): BottomActionBar(), BottomBarAction, CollapsingHeaderScaffold(), HeaderStyle, Dashboard, Standard, EditFormScaffold(), EntityListScreen() (+31 more)

### Community 31 - "SetupViewModel.kt"
Cohesion: 0.10
Nodes (5): `ui/more` and `ui/accounts` (B2a), AccountsViewModel, HomeUiState, HomeViewModel, AccountLists

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.17
Nodes (15): GlyphSize, Chip, Hero, Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers() (+7 more)

### Community 39 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.19
Nodes (12): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5) (+4 more)

### Community 42 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "Account"
Cohesion: 0.07
Nodes (19): `data` (Room persistence, A4a), AccountDao, AccountEntity, AccountMapper, RoomAccountRepository, AccountsUiState, RoomAccountMapperTest, AuditedTables (+11 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.09
Nodes (6): iosSpring(), PftMotion, pressScale(), PftMotionTest, 7. Test plan, 11. Rollout & verification

### Community 46 - "PreMigrationBackup"
Cohesion: 0.15
Nodes (12): `security` and the encrypted open (A4b), PreMigrationBackup, PreMigrationBackupFailed, Corruption handling, Persistence and security, Pre-migration backup (INT-5), SQLCipher, The key and the KEK (+4 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "How to run and test"
Cohesion: 0.07
Nodes (5): How to run and test, MainActivityOpeningTest, SlowOpeningApplication, MainActivityStartupTest, CategoryIconTest

### Community 49 - "CategoryGlyphScreenshotTest"
Cohesion: 0.06
Nodes (30): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CollapsingHeader(), expandedHeaderHeight(), HeaderChildren(), HeaderContent (+22 more)

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.06
Nodes (26): Probe, RoomAuditColumnsTest, AuditColumnsCheck, AuditedTable, Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status (+18 more)

### Community 54 - "EntityMeta"
Cohesion: 0.06
Nodes (26): EntityMeta, LedgerEntity, DeviceId, EntityId, UserId, cat(), CategoryTreeTest, EntityMetaTest (+18 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.21
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "MoneyText"
Cohesion: 0.14
Nodes (13): `:app` — Android application, Configuration, Dependencies, Responsibilities and boundaries, atSize(), directionLabel(), Figure(), fitHero() (+5 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.10
Nodes (24): Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Personal Finance Tracker PRD, Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Delivery constraints (small vertical slices, Claude Pro limits), Automatic capture from Email (E-0..E-5) (+16 more)

### Community 59 - "UserProfile"
Cohesion: 0.07
Nodes (9): IdentityDao, DeviceEntity, UserProfileEntity, RoomIdentityRepository, RoomIdentityRepositoryTest, Device, IdentityRepository, UserProfile (+1 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 61 - "awaitMain"
Cohesion: 0.07
Nodes (7): awaitMain(), AccountsViewModelTest, FailsOnDemand, FailsOnAppend, SetupViewModelTest, WindowThemeTest, AccountDraft

### Community 63 - "PftTypography.kt"
Cohesion: 0.17
Nodes (12): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansWeights(), DisplayFont, BUNDLED_SERIF (+4 more)

### Community 65 - "EntityListScreenTest"
Cohesion: 0.07
Nodes (15): AccountBadge, AccountRowMapper, toRow(), EntityListEmptyPreview(), EntityListEmptySample(), EntityListSample(), glyph(), sampleAction() (+7 more)

### Community 66 - "AppServices"
Cohesion: 0.11
Nodes (28): RoomTransactionRunner, AppServices, Configuration, Responsibilities & boundaries, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), AccountService, AuditDiff, AuditedWriter (+20 more)

### Community 67 - "6.1 Slices"
Cohesion: 0.08
Nodes (33): sansFamily(), ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, paletteFor(), SansFont (+25 more)

### Community 68 - "HostedComposeRule"
Cohesion: 0.06
Nodes (6): HeaderTags, IvoryPalette, layoutResult(), shownText(), layout(), HostedComposeRule

### Community 73 - "import_symbols.py"
Cohesion: 0.16
Nodes (6): The curated list, download(), main(), parse_hashes(), to_vector_drawable(), UnsafeSvg

### Community 76 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.16
Nodes (9): SectionContent(), DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, assertShownInFull(), isShownInFull() (+1 more)

### Community 77 - "7. Test plan"
Cohesion: 0.13
Nodes (11): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, Theming, 1. Requirement (+3 more)

### Community 78 - "GroupedSectionTest"
Cohesion: 0.08
Nodes (3): GroupedSectionTest, CategoryPaletteTest, PftSymbolTest

### Community 79 - "ReleaseHasNoCatalogueTest.kt"
Cohesion: 0.17
Nodes (4): RoomTestKit, ReleaseHasNoCatalogueTest, Call flow example: first-run Setup, Startup

### Community 80 - "13. Deviations from the plan, by slice"
Cohesion: 0.09
Nodes (13): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, MidnightPalette, MoneyColors, PftSpacing, PftSurfaces (+5 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.17
Nodes (12): 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2), 5.5 Mockups (wireframes; the styled version is in the HTML file) (+4 more)

### Community 84 - "LogEvent"
Cohesion: 0.05
Nodes (48): `di` and startup (A5b), `log` (safe logging, A5a), Purpose, AppStart, KeyUnavailable, DbKeyUnavailable, DbOpened, EntityAction (+40 more)

### Community 85 - "MigrationTest"
Cohesion: 0.06
Nodes (6): AuditTriggers, MigrationTestSupport, RegisterHostActivity, Statement, MigrationTest, Audit triggers

### Community 89 - "LedgerError"
Cohesion: 0.10
Nodes (35): `ui/common` (shared components, DS3, DS4, DS5 and A5d), Duplicate, FieldError, InvalidLastFour, message(), Required, TooLong, toFieldError() (+27 more)

### Community 90 - "HeaderIconButtonTest.kt"
Cohesion: 0.19
Nodes (4): HeaderIconButton(), HeaderIconButtonTest, Planned: premium look (DS6), 9. Documentation updates (all required surfaces)

### Community 92 - "StatusChip"
Cohesion: 0.17
Nodes (4): ChipColors, StatusChip(), StatusChipTest, 10. Risks & mitigations

### Community 94 - "PftTheme"
Cohesion: 0.11
Nodes (7): ChoiceRow(), DesignCatalog(), EmptyState(), PftTheme(), EmptyStateTest, DesignCatalogTest, Step 11 — `NameField` and `EditFormScaffold` (A5d)

### Community 95 - "DbKeyManager"
Cohesion: 0.26
Nodes (4): DbKeyManager, Decoded, WrappedKeyFormat, Migrations on the encrypted file (`EncryptedMigrationTest`)

### Community 98 - "Public interface"
Cohesion: 0.10
Nodes (15): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How an entity step composes the blocks, Public interface, Purpose, AccountRules, CategoryDraft (+7 more)

### Community 99 - "DataUnavailableScreenTest"
Cohesion: 0.15
Nodes (8): Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING, DataUnavailableScreenTest

### Community 101 - "SampleThing.kt"
Cohesion: 0.07
Nodes (14): EntityMapper, MetaColumns, MetaMapper, TagEntity, TagMapper, MetaMapperTest, RoomEntityRepositoryTest, SampleDatabase (+6 more)

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 107 - "FieldState"
Cohesion: 0.07
Nodes (6): FieldCheck, FieldState, FieldStateTest, NameFieldTest, SetupScreenshotTest, SetupScreenTest

### Community 111 - "SetupScreen.kt"
Cohesion: 0.09
Nodes (14): `ui/setup` and the UiState convention (A5e), NameField(), HomeRoute(), HomeScreen(), PftNavHost(), Routes, SetupRoute(), SetupScreen() (+6 more)

### Community 113 - "CatalogSection"
Cohesion: 0.14
Nodes (14): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+6 more)

### Community 115 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 117 - "CategoryColour"
Cohesion: 0.05
Nodes (38): CategoryMapper, RoomCategoryRepository, Category, CategoryColour, AMBER, BLUE, CORAL, EMERALD (+30 more)

### Community 118 - "DatabaseStartupTest.kt"
Cohesion: 0.19
Nodes (4): DatabaseStartup, KeyUnavailable, Ready, DatabaseStartupTest

### Community 119 - "3. Design & decision"
Cohesion: 0.29
Nodes (7): 3.1 The model: every transaction has one or more lines, 3.2 Lines are ledger entities, written only by `TransactionService`, 3.4 Reads, 3.6 Configuration (CLAUDE.md §3.5), 3.7 Future items recorded, not built, 3.8 Abstraction bar, 3. Design & decision

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.10
Nodes (11): after(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), putSetting(), settingDirectly() (+3 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.23
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "shouldbe"
Cohesion: 0.05
Nodes (46): RoomAuditLog, AuditDaoTest, TestDatabase, How to run and test, FieldChange, AuditAction, ARCHIVE, CREATE (+38 more)

### Community 124 - "AccountsRouteTest.kt"
Cohesion: 0.06
Nodes (8): Data it owns, EpochMillis, EpochMillisTest, AppServicesTest, AccountsRouteTest, FailsOnDemand, TestClock, Conventions

### Community 125 - "Architecture overview"
Cohesion: 0.33
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 128 - "Category"
Cohesion: 0.25
Nodes (3): EditFormPreview(), EditFormSample(), FormScreenshotTest

### Community 129 - "AppSetting"
Cohesion: 0.14
Nodes (6): RoomAppSettingsRepository, RoomAppSettingsRepositoryTest, AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository, The core/app boundary

### Community 136 - "WindowBackgroundTest.kt"
Cohesion: 0.16
Nodes (3): SyncWindowBackground(), WindowBackgroundTest, 8. Security considerations

### Community 138 - "CategoryEntity"
Cohesion: 0.17
Nodes (3): CategoryDao, CategoryEntity, RoomCategoryMapperTest

### Community 142 - "Uuid7Test.kt"
Cohesion: 0.15
Nodes (5): clockAt(), Uuid7Test, How to run and test, PaiseParserTest, PaiseTest

### Community 146 - ".format"
Cohesion: 0.18
Nodes (10): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+2 more)

### Community 147 - "RoomEntityRepository"
Cohesion: 0.14
Nodes (3): BaseDao, RoomEntityRepository, TagDao

### Community 154 - "PftDatabase"
Cohesion: 0.10
Nodes (6): backup(), open(), AuditDao, DatabaseFactory, PftDatabase, open()

### Community 155 - "Design system"
Cohesion: 0.21
Nodes (4): BackgroundStyle, FLAT, TONAL, BottomActionBarSurfaceTest

### Community 159 - "AppContainer"
Cohesion: 0.11
Nodes (5): AppContainer, PftRoot(), PftRootTest, 7. Test plan, Step 14 — `PftNavHost`, Home placeholder, debug route, predictive back (A5f)

### Community 161 - ".check"
Cohesion: 0.08
Nodes (7): LoggingScanTest, SourceScan, Violation, Case, GroupedSectionColorsTest, PftSurfacesTest, InMemoryEntityStore

### Community 164 - "2026-11-01-m1a-a5-first-run-setup.md"
Cohesion: 0.11
Nodes (15): Code Structure Map, core/shared Shared Packages, Categories and tags, Rules the app already enforces, Tags, The categories you start with, What a category is, Data protection: encrypted storage and the "can't open your data" screen (+7 more)

### Community 166 - "HomeScreenTest.kt"
Cohesion: 0.14
Nodes (4): DevHeaderActions(), devRoutes(), DevHeaderActions(), HomeScreenTest

### Community 172 - "Icons and emoji (DS3, UI-6)"
Cohesion: 0.36
Nodes (5): CategoryColors, CategoryPalette, pair(), Adding an icon, Icons and emoji (DS3, UI-6)

### Community 173 - "CategoryKind"
Cohesion: 0.22
Nodes (8): Accounts, Archive or delete?, Configuration, If something goes wrong, Privacy, The accounts list, Undo, What an account is

### Community 174 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.06
Nodes (32): Debug and release source sets, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, `ui/nav` and `ui/home` (A5f), Window theme, Ready, MainActivity, PftApplication (+24 more)

### Community 175 - "AuditDaoTest.kt"
Cohesion: 0.13
Nodes (6): Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 184 - "AccountsScreen.kt"
Cohesion: 0.12
Nodes (9): AccountsActions, AccountsRoute(), AccountsScreen(), failureRes(), typeLabelRes(), DataUnavailableCause, IdentityCorrupt, KeyUnavailable (+1 more)

### Community 192 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 199 - ".fromIconKey"
Cohesion: 0.39
Nodes (4): Emoji, GlyphSource, Letter, Symbol

## Knowledge Gaps
- **223 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+218 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 675 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **81 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `runSuspending`, `AuditEntryMapperTest.kt`, `ThemePrefs`, `MoneyTextTest`, `ColorContrastTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `PftShapesTest.kt`, `PressScaleTest`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `CategoryGlyph`, `GlyphSourceTest`, `LaunchViewModelTest.kt`, `Account`, `PftMotion.kt`, `CategoryGlyphScreenshotTest`, `PftThemeModeTest`, `RoomAuditColumnsTest`, `ContentScreenshotTest`, `DbKeyManagerTest.kt`, `MoneyText`, `UserProfile`, `awaitMain`, `ThemePaletteTest`, `AppServices`, `ManifestHardeningTest`, `MainActivityOpeningTest.kt`, `AppContainerTest.kt`, `RoomCategoryIntegrationTest`, `Previews, the token sheet and screenshot goldens`, `GroupedSectionTest`, `ReleaseHasNoCatalogueTest.kt`, `13. Deviations from the plan, by slice`, `RoomAccountIntegrationTest`, `LogEvent`, `MigrationTest`, `RoomAuditedWriterIntegrationTest`, `AuditTriggersTest`, `runSuspending`, `StatusChip`, `EncryptedMigrationTest`, `PftTheme`, `Public interface`, `DataUnavailableScreenTest`, `MoneyToneTest`, `SampleThing.kt`, `IdentityRoomIntegrationTest`, `SetupFlowTest.kt`, `FieldState`, `BundledFontTest`, `BottomActionBarTest.kt`, `TokenSheetScreenshotTest.kt`, `LogEventShapeTest`, `DatabaseStartupTest.kt`, `shouldbe`, `AccountsRouteTest.kt`, `RoomTransactionRunnerTest`, `Category`, `AppSetting`, `WindowBackgroundTest.kt`, `DevRouteTest.kt`, `CategoryEntity`, `DbKeyUnavailable`, `.format`, `r`, `SetupRouteTest.kt`, `RoomCategoryForeignKeyTest.kt`, `PftDatabase`, `Design system`, `LogEventTest`, `PaletteScreenshotTest.kt`, `AppContainer`, `.check`, `EditFormScaffoldTest.kt`, `HomeScreenTest.kt`, `BundledSymbolsLicenceTest`, `FieldErrorMessageTest.kt`, `Slice A — Foundation (`feat/m1a-foundation`)`, `LaunchRoutingTest.kt`?**
  _High betweenness centrality (0.232) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _223 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `Category`, `WindowBackgroundTest.kt`, `ThemePrefs`, `MoneyTextTest`, `DevRouteTest.kt`, `FormScreenshotTest.kt`, `ColorContrastTest`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `SetupRouteTest.kt`, `DesignCatalog.kt`, `Design system`, `CollapsingHeaderScaffoldTest`, `PftShapesTest.kt`, `PaletteScreenshotTest.kt`, `AppContainer`, `CollapsingHeaderScaffold.kt`, `GroupedSectionSamples`, `EditFormScaffoldTest.kt`, `PressScaleTest`, `HeaderGlowScaffoldTest`, `.of`, `CategoryGlyph`, `HomeScreenTest.kt`, `PftTypographyTest`, `FieldErrorMessageTest.kt`, `CategoryPaletteTest`, `Categories and tags`, `Icons and emoji (DS3, UI-6)`, `PftMotion.kt`, `Slice A — Foundation (`feat/m1a-foundation`)`, `HeaderTypePairingTest.kt`, `How to run and test`, `CategoryGlyphScreenshotTest`, `PftThemeModeTest`, `ContentScreenshotTest`, `AccountsScreen.kt`, `MoneyText`, `LaunchRoutingTest.kt`, `PftTypography.kt`, `EntityListScreenTest`, `6.1 Slices`, `HostedComposeRule`, `Previews, the token sheet and screenshot goldens`, `7. Test plan`, `GroupedSectionTest`, `ReleaseHasNoCatalogueTest.kt`, `13. Deviations from the plan, by slice`, `Plan: M1a design system and visual direction (lands before A5)`, `LogEvent`, `HeaderIconButtonTest.kt`, `StatusChip`, `DataUnavailableScreenTest`, `SetupFlowTest.kt`, `FieldState`, `BottomActionBarTest.kt`, `SetupScreen.kt`, `TokenSheetScreenshotTest.kt`, `AccountsRouteTest.kt`, `Architecture overview`?**
  _High betweenness centrality (0.116) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `Tag` be split into smaller, more focused modules?**
  _Cohesion score 0.13368983957219252 - nodes in this community are weakly interconnected._