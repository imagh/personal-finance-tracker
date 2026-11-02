# Graph Report - personal-finance-tracker  (2026-11-02)

## Corpus Check
- 306 files · ~225,030 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3716 nodes · 10921 edges · 150 communities (80 shown, 70 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 1736 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `eaa81414`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- InMemoryEntityRepository
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- Uuid7Test.kt
- hooks
- test_import_symbols.py
- DesignCatalog.kt
- ThemeResolutionTest
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Catalog.kt
- detekt.yml
- NavTransitionTest.kt
- PftSymbol
- CollapsingHeaderScaffoldTest
- SampleId
- Slice A — Foundation (`feat/m1a-foundation`)
- How to run and test
- CollapsingHeaderScaffold.kt
- 6.1 Slices
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- HeaderGlowScaffoldTest
- PftTypographyTest
- CategoryGlyph
- PftShapesTest.kt
- GlyphSourceTest
- LaunchViewModelTest.kt
- Project README
- PftTypography.kt
- PftMotion.kt
- HeaderTypePairingTest
- `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)
- CLAUDE.md Development Constitution
- HeaderIconButton
- PressScaleTest.kt
- FieldState
- AuditEntryMapperTest.kt
- PftThemeModeTest
- RoomAuditColumnsTest
- LedgerEntity
- ContentScreenshotTest
- DbKeyManagerTest.kt
- MoneyText
- Collecting and Redacting Sample Messages Guide
- UserId
- redact_text
- SetupFlowTest.kt
- HostedComposeRule
- 13. Deviations from the plan, by slice
- Pseudo
- BackgroundStyle
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- SetupFlowTest.kt
- TokenSheetScreenshotTest.kt
- HeaderPolicy
- _Text
- MainActivityOpeningTest.kt
- import_symbols.py
- runSuspending
- WindowThemeTest
- Previews, the token sheet and screenshot goldens
- BottomActionBarSurfaceTest.kt
- PftSymbolTest
- AuditTriggersTest
- SampleThing.kt
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- AuditEntryEntity
- AppServicesTest
- MigrationTestSupport
- DbKeyUnavailable
- PreMigrationBackupTest
- TabularFiguresTest
- ThemePrefs
- ThemePaletteTest
- PreMigrationBackup
- CollapsingHeaderScaffold
- GroupedSectionTest
- PftTheme
- DbKeyManager
- Design system
- DevRouteTest.kt
- Appearance: colour palettes, dynamic colour, background and type
- DatabaseStartupTest.kt
- MoneyToneTest
- Migrations.kt
- FormSamples.kt
- AppSettingEntity
- PaletteSamples.kt
- SafeLogTest
- parse_svg
- RoomEntityRepository
- BundledFontTest
- ReleaseHasNoCatalogueTest.kt
- ImportRunTest
- AppServices
- InMemoryKekSource
- Personal Finance Tracker PRD
- DataUnavailableScreenTest
- Layered Modular Architecture (core vs app)
- PaletteSwatchesTest.kt
- AppContainer
- FormScreenshotTest.kt
- Loans and EMIs (LN-1..LN-5)
- BundledSymbolsLicenceTest
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- shouldbe
- PaletteScreenshotTest.kt
- expandedHeaderHeight
- modifier
- 7. Test plan
- HeaderSlot
- LogEventShapeTest
- PaiseParser
- AccentHueTest
- ThemePrefs
- CategoryPaletteTest
- BaseDao
- ManifestHardeningTest
- 6. Implementation steps (TDD-ordered)
- SetupRouteTest
- AppSetting
- PftSurfacesTest
- ChipColors
- EditFormScaffoldTest
- `data` (Room persistence, A4a)
- FontBudgetTest
- DatabaseFactory
- LogEventTest
- LedgerError
- Immutable Audit Trail

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 190 edges
2. `ThemePrefs` - 159 edges
3. `PftSymbol` - 125 edges
4. `How to run and test` - 100 edges
5. `HostedComposeRule` - 92 edges
6. `Paise` - 92 edges
7. `CollapsingHeaderScaffoldTest` - 82 edges
8. `runSuspending()` - 74 edges
9. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 71 edges
10. `PftDatabase` - 57 edges

## Surprising Connections (you probably didn't know these)
- `3.5 Fonts: bundling, licensing, custom import` --references--> `SafeLog`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/log/SafeLog.kt
- `13. Deviations from the plan, by slice` --references--> `asHeroTop()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderGlow.kt
- ``StatusChip`` --references--> `StatusChip()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/StatusChip.kt
- `Motion (UI-10)` --references--> `PftNavHost()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/nav/PftNavHost.kt
- `Adding an icon` --references--> `PftSymbol`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftSymbol.kt

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

## Communities (150 total, 70 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "Uuid7Test.kt"
Cohesion: 0.06
Nodes (16): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, AuditedWriterTest, EntityMetaTest, IdsTest (+8 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "DesignCatalog.kt"
Cohesion: 0.06
Nodes (7): AccentCheckSection(), MotionSection(), SpringTrack(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), GroupedSectionTags

### Community 9 - "ThemeResolutionTest"
Cohesion: 0.11
Nodes (4): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemeResolutionTest

### Community 10 - "MoneyTextTest"
Cohesion: 0.12
Nodes (3): MoneyTextTest, Spec, Paise

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.13
Nodes (11): Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone(), roles() (+3 more)

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.10
Nodes (28): GroupedRow, GroupedSection(), GroupedSectionDefaults, GroupedSectionScope, GroupedSectionPreview(), GroupedSectionSamples(), Balance, Budget (+20 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 30 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.05
Nodes (57): `:app` — Android application, Debug and release source sets, `di` and startup (A5b), `log` (safe logging, A5a), Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose, Responsibilities and boundaries (+49 more)

### Community 31 - "How to run and test"
Cohesion: 0.13
Nodes (3): How to run and test, PftDatabase, HomeViewModelTest

### Community 32 - "CollapsingHeaderScaffold.kt"
Cohesion: 0.08
Nodes (17): CollapsingHeader(), HeaderChildren(), HeaderContent, HeaderPeriod, HeaderPhase, Collapsed, Expanded, Moving (+9 more)

### Community 33 - "6.1 Slices"
Cohesion: 0.12
Nodes (9): colors(), ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, ChipColorsTest, Case (+1 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.18
Nodes (14): Row, TokenSheet(), TokenSheetPreview(), TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface() (+6 more)

### Community 38 - "CategoryGlyph"
Cohesion: 0.12
Nodes (7): CategoryGlyph(), CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest, CategoryGlyphTest

### Community 40 - "GlyphSourceTest"
Cohesion: 0.14
Nodes (7): Emoji, GlyphSource, isEmojiValue(), isSymbolKey(), Letter, Symbol, GlyphSourceTest

### Community 42 - "Project README"
Cohesion: 0.21
Nodes (9): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer (+1 more)

### Community 43 - "PftTypography.kt"
Cohesion: 0.16
Nodes (13): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansWeights(), DisplayFont, BUNDLED_SERIF (+5 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.07
Nodes (7): iosSpring(), PftMotion, pressScale(), BottomActionBarTest, PftMotionTest, 7. Test plan, 11. Rollout & verification

### Community 46 - "`ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)"
Cohesion: 0.14
Nodes (9): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, MidnightPalette, MoneyColors, PftSpacing, Colour (+1 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "HeaderIconButton"
Cohesion: 0.09
Nodes (7): DevHeaderActions(), devRoutes(), HeaderIconButton(), HomeUiState, DevHeaderActions(), HeaderIconButtonTest, HomeScreenTest

### Community 50 - "FieldState"
Cohesion: 0.06
Nodes (7): FieldCheck, FieldState, FieldStateTest, NameFieldTest, SetupScreenshotTest, SetupScreenTest, InMemoryEntityStore

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "LedgerEntity"
Cohesion: 0.05
Nodes (27): EntityMeta, LedgerEntity, EntityId, Fixture, Leaky, LeakyRepository, Note, NoteId (+19 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.21
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "MoneyText"
Cohesion: 0.08
Nodes (25): Dependencies, MoneySize, Hero, Large, atSize(), directionLabel(), Figure(), fitHero() (+17 more)

### Community 58 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.14
Nodes (17): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope (+9 more)

### Community 59 - "UserId"
Cohesion: 0.07
Nodes (11): RoomIdentityRepository, RoomIdentityRepositoryTest, AuditedTable, AuditedTables, Auditable, Device, IdentityRepository, UserProfile (+3 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 62 - "HostedComposeRule"
Cohesion: 0.07
Nodes (7): HeaderTags, layoutResult(), shownText(), assertShownInFull(), isShownInFull(), layout(), HostedComposeRule

### Community 63 - "13. Deviations from the plan, by slice"
Cohesion: 0.12
Nodes (20): sansFamily(), paletteFor(), SansFont, FIGTREE, PLUS_JAKARTA_SANS, SYSTEM, TypePairing, EDITORIAL (+12 more)

### Community 65 - "BackgroundStyle"
Cohesion: 0.12
Nodes (17): GroupedSectionColors, BackgroundStyle, FLAT, TONAL, PftSurfaces, ThemeMode, DARK, LIGHT (+9 more)

### Community 66 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.07
Nodes (30): RoomTransactionRunner, Behaviours worth knowing, Configuration, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), AuditDiff (+22 more)

### Community 72 - "MainActivityOpeningTest.kt"
Cohesion: 0.10
Nodes (3): MainActivityOpeningTest, SlowOpeningApplication, MainActivityStartupTest

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 74 - "runSuspending"
Cohesion: 0.06
Nodes (8): FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomTransactionRunnerTest, AppContainerTest, runSuspending(), 3.3 Dependencies (one pins-only PR, A5c)

### Community 75 - "WindowThemeTest"
Cohesion: 0.10
Nodes (3): SyncWindowBackground(), WindowBackgroundTest, WindowThemeTest

### Community 76 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.07
Nodes (24): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+16 more)

### Community 80 - "SampleThing.kt"
Cohesion: 0.23
Nodes (6): SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.15
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 82 - "UserProfileEntity"
Cohesion: 0.22
Nodes (3): IdentityDao, DeviceEntity, UserProfileEntity

### Community 85 - "MigrationTestSupport"
Cohesion: 0.07
Nodes (6): AuditTriggers, MigrationTestSupport, RegisterHostActivity, Statement, MigrationTest, Audit triggers

### Community 86 - "DbKeyUnavailable"
Cohesion: 0.15
Nodes (8): DbKeyUnavailable, Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING

### Community 87 - "PreMigrationBackupTest"
Cohesion: 0.05
Nodes (7): Configuration, PreMigrationBackupTest, LoggingScanTest, DbKeyManagerWiringTest, SourceScan, Violation, ThemeLiteralScanTest

### Community 89 - "ThemePrefs"
Cohesion: 0.11
Nodes (6): StatusChip(), StatusChipPreview(), StatusChipSamples(), ThemePrefs, StatusChipTest, ThemePrefsTest

### Community 91 - "PreMigrationBackup"
Cohesion: 0.12
Nodes (13): PreMigrationBackup, PreMigrationBackupFailed, factory(), Corruption handling, Persistence and security, Pre-migration backup (INT-5), SQLCipher, The key and the KEK (+5 more)

### Community 92 - "CollapsingHeaderScaffold"
Cohesion: 0.21
Nodes (7): BottomActionBar(), BottomBarAction, CollapsingHeaderScaffold(), One-hand layout (UI-7), Principles, 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`, 3.2 Surfaces: tonal or flat (DS6c)

### Community 94 - "PftTheme"
Cohesion: 0.16
Nodes (5): ChoiceRow(), DesignCatalog(), PftTheme(), DesignCatalogTest, 3.9 Scope: app-wide

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 96 - "Design system"
Cohesion: 0.27
Nodes (7): CategoryColors, CategoryPalette, pair(), Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 98 - "Appearance: colour palettes, dynamic colour, background and type"
Cohesion: 0.07
Nodes (20): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you, Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen (+12 more)

### Community 104 - "PaletteSamples.kt"
Cohesion: 0.42
Nodes (4): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 109 - "ReleaseHasNoCatalogueTest.kt"
Cohesion: 0.16
Nodes (4): DatabaseStartup, KeyUnavailable, Ready, ReleaseHasNoCatalogueTest

### Community 111 - "AppServices"
Cohesion: 0.08
Nodes (16): `ui/nav` and `ui/home` (A5f), AppServices, HomeRoute(), HomeScreen(), HomeViewModel, PftNavHost(), Routes, PftRoot() (+8 more)

### Community 112 - "InMemoryKekSource"
Cohesion: 0.14
Nodes (3): AndroidKeystoreKek, KekSource, InMemoryKekSource

### Community 113 - "Personal Finance Tracker PRD"
Cohesion: 0.22
Nodes (11): Personal Finance Tracker PRD, Audit trail (INT-2), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Milestone 3 (insights), Non-functional requirements, Security, privacy and integrity requirements (SEC/INT) (+3 more)

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 117 - "AppContainer"
Cohesion: 0.08
Nodes (10): AppContainer, AppStart, KeyUnavailable, RoomTestKit, LaunchRoutingTest, PftRootTest, Call flow example: first-run Setup, Startup (+2 more)

### Community 119 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.14
Nodes (16): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID) (+8 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.19
Nodes (6): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "shouldbe"
Cohesion: 0.07
Nodes (29): RoomAuditLog, AuditDaoTest, TestDatabase, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE (+21 more)

### Community 126 - "modifier"
Cohesion: 0.13
Nodes (4): fixedSp(), GlyphSize, Chip, Hero

### Community 127 - "7. Test plan"
Cohesion: 0.15
Nodes (9): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 3.4 Header glow (UI-13, DS6d), 3.8 Abstraction bar (+1 more)

### Community 128 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 132 - "AccentHueTest"
Cohesion: 0.43
Nodes (3): AccentHueTest, hueDistance(), labHue()

### Community 133 - "ThemePrefs"
Cohesion: 0.12
Nodes (10): EmptyState(), EmptyStateTest, Adding a token, Build-time trust decisions, Content primitives (DS5), Design system, `EmptyState`, `GroupedSection` (+2 more)

### Community 136 - "ManifestHardeningTest"
Cohesion: 0.10
Nodes (7): ManifestHardeningTest, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins"), 3. Design & decision

### Community 137 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.29
Nodes (7): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice I — Cashew import (`feat/m1a-cashew-import`): tracking stub

### Community 139 - "AppSetting"
Cohesion: 0.08
Nodes (9): EpochMillis, RoomAppSettingsRepository, EpochMillisTest, RoomAppSettingsRepositoryTest, AppSetting, AppSettingsRepository, SettingKeys, InMemoryAppSettingsRepository (+1 more)

### Community 143 - "`data` (Room persistence, A4a)"
Cohesion: 0.16
Nodes (10): `data` (Room persistence, A4a), MetaColumns, MetaMapper, MetaMapperTest, NameBasedId, Conventions, Data model, The audit trail (+2 more)

### Community 144 - "FontBudgetTest"
Cohesion: 0.18
Nodes (8): FontBudgetTest, 10. Risks & mitigations, 11. Rollout & verification, 1. Requirement, 4. Data model & schema, 8. Security considerations, 9. Documentation updates (all required surfaces), Plan: M1a premium look (DS6): curated palettes, tonal surfaces, header glow, icon-only header actions, type pairing, bundled sans

### Community 154 - "LedgerError"
Cohesion: 0.07
Nodes (28): `ui/common` (shared components, DS3, DS4, DS5 and A5d), `ui/setup` and the UiState convention (A5e), SetupCompleted, EditFormScaffold(), FieldError, Required, TooLong, toFieldError() (+20 more)

## Knowledge Gaps
- **198 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+193 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 601 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **70 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `LogEventShapeTest`, `AccentHueTest`, `ThemePrefs`, `CategoryPaletteTest`, `ManifestHardeningTest`, `ThemeResolutionTest`, `MoneyTextTest`, `AppSetting`, `SetupRouteTest`, `PftSurfacesTest`, `EditFormScaffoldTest`, ``data` (Room persistence, A4a)`, `ColorContrastTest`, `LogEventTest`, `NavTransitionTest.kt`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `SampleId`, `Slice A — Foundation (`feat/m1a-foundation`)`, `6.1 Slices`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `CategoryGlyph`, `PftShapesTest.kt`, `GlyphSourceTest`, `LaunchViewModelTest.kt`, `PftMotion.kt`, `HeaderIconButton`, `PressScaleTest.kt`, `FieldState`, `AuditEntryMapperTest.kt`, `PftThemeModeTest`, `RoomAuditColumnsTest`, `ContentScreenshotTest`, `DbKeyManagerTest.kt`, `MoneyText`, `UserId`, `SetupFlowTest.kt`, `13. Deviations from the plan, by slice`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `SetupFlowTest.kt`, `TokenSheetScreenshotTest.kt`, `MainActivityOpeningTest.kt`, `runSuspending`, `WindowThemeTest`, `Previews, the token sheet and screenshot goldens`, `BottomActionBarSurfaceTest.kt`, `PftSymbolTest`, `AuditTriggersTest`, `SampleThing.kt`, `AppServicesTest`, `MigrationTestSupport`, `PreMigrationBackupTest`, `ThemePrefs`, `ThemePaletteTest`, `GroupedSectionTest`, `PftTheme`, `DevRouteTest.kt`, `DatabaseStartupTest.kt`, `MoneyToneTest`, `SafeLogTest`, `BundledFontTest`, `ReleaseHasNoCatalogueTest.kt`, `AppServices`, `DataUnavailableScreenTest`, `AppContainer`, `FormScreenshotTest.kt`, `BundledSymbolsLicenceTest`, `shouldbe`, `PaletteScreenshotTest.kt`, `expandedHeaderHeight`?**
  _High betweenness centrality (0.278) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _198 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `.assertIconOnlyActions`, `ThemePrefs`, `DesignCatalog.kt`, `ThemeResolutionTest`, `MoneyTextTest`, `SetupRouteTest`, `ManifestHardeningTest`, `EditFormScaffoldTest`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `FontBudgetTest`, `LedgerError`, `CollapsingHeaderScaffoldTest`, `Slice A — Foundation (`feat/m1a-foundation`)`, `CollapsingHeaderScaffold.kt`, `TokenSheetSections.kt`, `HeaderGlowScaffoldTest`, `PftTypographyTest`, `CategoryGlyph`, `PftShapesTest.kt`, `Project README`, `PftTypography.kt`, `PftMotion.kt`, `HeaderTypePairingTest`, ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`, `HeaderIconButton`, `PressScaleTest.kt`, `FieldState`, `PftThemeModeTest`, `ContentScreenshotTest`, `MoneyText`, `HostedComposeRule`, `13. Deviations from the plan, by slice`, `BackgroundStyle`, `SetupFlowTest.kt`, `TokenSheetScreenshotTest.kt`, `MainActivityOpeningTest.kt`, `WindowThemeTest`, `Previews, the token sheet and screenshot goldens`, `BottomActionBarSurfaceTest.kt`, `Plan: M1a design system and visual direction (lands before A5)`, `ThemePrefs`, `CollapsingHeaderScaffold`, `GroupedSectionTest`, `Design system`, `DevRouteTest.kt`, `FormSamples.kt`, `PaletteSamples.kt`, `ReleaseHasNoCatalogueTest.kt`, `AppServices`, `DataUnavailableScreenTest`, `PaletteSwatchesTest.kt`, `AppContainer`, `FormScreenshotTest.kt`, `PaletteScreenshotTest.kt`, `modifier`, `7. Test plan`?**
  _High betweenness centrality (0.099) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._