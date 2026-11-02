# Graph Report - personal-finance-tracker  (2026-11-02)

## Corpus Check
- 306 files · ~227,382 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3734 nodes · 10990 edges · 162 communities (95 shown, 67 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 1741 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `ef645f48`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- AuditedWriterTest.kt
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- TestClock
- hooks
- test_import_symbols.py
- AccentCheckSection.kt
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
- RoomEntityRepositoryTest.kt
- 9. Documentation updates (all required surfaces)
- How to run and test
- CollapsingHeaderScaffold.kt
- BackgroundStyle
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- HeaderGlowScaffoldTest.kt
- PftTypographyTest
- CategoryGlyph
- PftShapesTest.kt
- GlyphSourceTest
- AppContainerTest.kt
- Project README
- PftTypographyTest.kt
- PftMotion.kt
- HeaderTypePairingTest.kt
- `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)
- CLAUDE.md Development Constitution
- HeaderIconButtonTest.kt
- CategoryGlyphScreenshotTest
- SetupScreenshotTest.kt
- AuditEntryMapperTest
- PftThemeModeTest
- RoomAuditColumnsTest
- EntityMeta
- ContentScreenshotTest
- DbKeyManagerTest.kt
- MoneyText.kt
- Collecting and Redacting Sample Messages Guide
- UserId
- redact_text
- SetupViewModelTest
- asserttrue
- TypePairing
- Pseudo
- GroupedSection.kt
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- SetupFlowTest.kt
- r
- _Text
- MainActivityOpeningTest.kt
- import_symbols.py
- runSuspending
- 13. Deviations from the plan, by slice
- Previews, the token sheet and screenshot goldens
- SetupScreenTest.kt
- PftSymbolTest
- modifier
- SampleThing.kt
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- AuditEntryEntity
- SetupViewModel.kt
- MigrationTestSupport
- TransactionRunner
- PreMigrationBackupTest
- TabularFiguresTest
- StatusChip
- ThemePaletteTest
- LedgerError
- CollapsingHeaderScaffold
- GroupedSectionTest.kt
- PftTheme
- DbKeyManager
- ThemePalette
- DevRouteTest.kt
- Plan: M1a — Core ledger (manual-entry household tracker)
- DatabaseStartupTest.kt
- Paise
- Migrations.kt
- ThemeLiteralScanTest
- PftDatabase.kt
- MotionSection.kt
- How to run and test
- parse_svg
- RoomEntityRepository
- BundledFontTest
- HostedComposeRule
- ImportRunTest
- PftNavHost
- InMemoryKekSource
- Personal Finance Tracker PRD
- assertisdisplayed
- Layered Modular Architecture (core vs app)
- LogEvent
- AppContainer
- FieldState
- Loans and EMIs (LN-1..LN-5)
- BundledSymbolsLicenceTest
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- shouldbe
- WindowThemeTest
- `di` and startup (A5b)
- CategoryGlyph.kt
- 7. Test plan
- SetupViewModel
- 7. Test plan
- BottomActionBarSurfaceTest.kt
- PaiseParser
- IdentityRoomIntegrationTest
- EmptyState
- assertnotequals
- BaseDao
- ManifestHardeningTest
- 6. Implementation steps (TDD-ordered)
- LaunchRoutingTest.kt
- Conventions
- PftSurfacesTest
- AppSetting
- RedactTests
- BottomActionBar.kt
- FontBudgetTest
- SafeLogTest
- MoneyTextTest.kt
- PftSpacing
- .fromIconKey
- DatabaseFactory
- HomeScreenTest.kt
- assertequals
- DbKeyManagerWiringTest.kt
- ThemePrefsTest
- NameField
- .check
- CategoryPalette
- MoneySize
- parse_manifest
- StatusChipSamples.kt
- Appearance: colour palettes, dynamic colour, background and type
- Immutable Audit Trail

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 190 edges
2. `ThemePrefs` - 159 edges
3. `PftSymbol` - 125 edges
4. `How to run and test` - 101 edges
5. `HostedComposeRule` - 92 edges
6. `Paise` - 92 edges
7. `CollapsingHeaderScaffoldTest` - 82 edges
8. `runSuspending()` - 74 edges
9. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 71 edges
10. `PftDatabase` - 57 edges

## Surprising Connections (you probably didn't know these)
- `Step 1 — `LogEvent` rendering (A5a)` --references--> `LogEvent`  [INFERRED]
  docs/plans/2026-11-01-m1a-a5-first-run-setup.md → app/src/main/kotlin/dev/imagh/pft/app/log/LogEvent.kt
- `Step 2 — `LogEvent` can hold no text (A5a)` --references--> `LogEvent`  [INFERRED]
  docs/plans/2026-11-01-m1a-a5-first-run-setup.md → app/src/main/kotlin/dev/imagh/pft/app/log/LogEvent.kt
- `3.5 Fonts: bundling, licensing, custom import` --references--> `SafeLog`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/log/SafeLog.kt
- `13. Deviations from the plan, by slice` --references--> `asHeroTop()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderGlow.kt
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

## Communities (162 total, 67 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "AuditedWriterTest.kt"
Cohesion: 0.15
Nodes (7): AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "TestClock"
Cohesion: 0.06
Nodes (9): EpochMillisTest, clockAt(), Uuid7Test, TestClock, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest (+1 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "AccentCheckSection.kt"
Cohesion: 0.07
Nodes (5): AccentCheckSection(), AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples()

### Community 9 - "ThemePrefs"
Cohesion: 0.11
Nodes (6): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest, 6.1 Slices

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.07
Nodes (16): AccentHueTest, Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone() (+8 more)

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.11
Nodes (32): `:app` — Android application, Configuration, Dependencies, Responsibilities and boundaries, directionLabel(), moneySign(), MoneyText(), Balance (+24 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "DesignCatalog.kt"
Cohesion: 0.08
Nodes (19): Debug and release source sets, CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS (+11 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.08
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "RoomEntityRepositoryTest.kt"
Cohesion: 0.23
Nodes (3): RoomEntityRepositoryTest, SampleId, SampleThingRepository

### Community 30 - "9. Documentation updates (all required surfaces)"
Cohesion: 0.14
Nodes (22): Purpose, SafeLog, LaunchViewModel, DataUnavailableScreen(), IdentityCorrupt, Fail closed, Slice H — App lock, diagnostics, release (`feat/m1a-hardening`), 10. Risks & mitigations (+14 more)

### Community 31 - "How to run and test"
Cohesion: 0.09
Nodes (11): Data it owns, How to run and test, EpochMillis, PftDatabase, AppServices, Ready, AppServicesTest, TestDatabase (+3 more)

### Community 32 - "CollapsingHeaderScaffold.kt"
Cohesion: 0.05
Nodes (29): CollapsingHeader(), expandedHeaderHeight(), HeaderChildren(), HeaderContent, HeaderPeriod, HeaderPhase, Collapsed, Expanded (+21 more)

### Community 33 - "BackgroundStyle"
Cohesion: 0.22
Nodes (6): BackgroundStyle, FLAT, TONAL, PftSurfaces, Case, GroupedSectionColorsTest

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "HeaderGlowScaffoldTest.kt"
Cohesion: 0.06
Nodes (4): HeaderGlowScaffoldTest, PaletteScreenshotTest, PaletteSwatchesTest, PressScaleTest

### Community 41 - "AppContainerTest.kt"
Cohesion: 0.09
Nodes (3): DatabaseStartup, Ready, LaunchViewModelTest

### Community 42 - "Project README"
Cohesion: 0.21
Nodes (9): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer (+1 more)

### Community 43 - "PftTypographyTest.kt"
Cohesion: 0.10
Nodes (8): displayFamily(), newsreader(), pftType(), PftTypography, sansWeights(), Resolved, Theming, 2. Current state

### Community 44 - "PftMotion.kt"
Cohesion: 0.11
Nodes (3): PftMotion, pressScale(), 11. Rollout & verification

### Community 46 - "`ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)"
Cohesion: 0.22
Nodes (7): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, Colour

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 49 - "CategoryGlyphScreenshotTest"
Cohesion: 0.23
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 52 - "PftThemeModeTest"
Cohesion: 0.09
Nodes (3): NavTransitionTest, PftThemeModeTest, TokenSheetScreenshotTest

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.05
Nodes (26): `data` (Room persistence, A4a), MetaColumns, MetaMapper, MetaMapperTest, EntityMeta, LedgerEntity, EntityId, AuditFieldsCheckTest (+18 more)

### Community 57 - "MoneyText.kt"
Cohesion: 0.17
Nodes (7): atSize(), Figure(), fitHero(), HeroFit, HeroText(), RollingDigits(), toneColor()

### Community 58 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.14
Nodes (17): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope (+9 more)

### Community 59 - "UserId"
Cohesion: 0.09
Nodes (10): RoomIdentityRepository, RoomIdentityRepositoryTest, AuditedTable, AuditedTables, Auditable, Device, IdentityRepository, UserProfile (+2 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 61 - "SetupViewModelTest"
Cohesion: 0.15
Nodes (3): awaitMain(), FailsFirstAppend, SetupViewModelTest

### Community 62 - "asserttrue"
Cohesion: 0.15
Nodes (3): assertShownInFull(), isShownInFull(), layout()

### Community 63 - "TypePairing"
Cohesion: 0.10
Nodes (24): pftMaterialTypography(), DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, SansFont, FIGTREE, PLUS_JAKARTA_SANS (+16 more)

### Community 65 - "GroupedSection.kt"
Cohesion: 0.13
Nodes (7): GroupedRow, GroupedSectionColors, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, GroupedSectionPreview(), GroupedSectionSamples()

### Community 66 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.08
Nodes (26): Behaviours worth knowing, Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How an entity step composes the blocks, Public interface, Purpose (+18 more)

### Community 69 - "r"
Cohesion: 0.09
Nodes (12): `security` and the encrypted open (A4b), DbKeyUnavailable, Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED (+4 more)

### Community 73 - "import_symbols.py"
Cohesion: 0.16
Nodes (6): The curated list, download(), main(), parse_hashes(), to_vector_drawable(), UnsafeSvg

### Community 74 - "runSuspending"
Cohesion: 0.06
Nodes (7): RoomAppSettingsRepositoryTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomTransactionRunnerTest, AppContainerTest, runSuspending(), 3.3 Dependencies (one pins-only PR, A5c)

### Community 75 - "13. Deviations from the plan, by slice"
Cohesion: 0.13
Nodes (6): Window theme, SyncWindowBackground(), toneOf(), WindowBackgroundTest, WindowFlashToneTest, 13. Deviations from the plan, by slice

### Community 76 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.20
Nodes (5): DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 77 - "SetupScreenTest.kt"
Cohesion: 0.06
Nodes (5): HeaderTags, BottomActionBarTest, EditFormScaffoldTest, FormScreenshotTest, SetupScreenTest

### Community 79 - "modifier"
Cohesion: 0.14
Nodes (6): DevHeaderActions(), devRoutes(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), DashboardHeaderPreview()

### Community 80 - "SampleThing.kt"
Cohesion: 0.24
Nodes (5): SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.15
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 82 - "UserProfileEntity"
Cohesion: 0.22
Nodes (3): IdentityDao, DeviceEntity, UserProfileEntity

### Community 85 - "MigrationTestSupport"
Cohesion: 0.06
Nodes (7): AuditTriggers, PftDatabaseTest, MigrationTestSupport, Statement, MigrationTest, The audit trail, Audit triggers

### Community 86 - "TransactionRunner"
Cohesion: 0.13
Nodes (10): RoomTransactionRunner, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), TransactionRunner, InMemoryAuditLog, InMemoryLedgerStore, The core/app boundary, 3.1 Modules (Gradle), 3.2 Key decisions (+2 more)

### Community 89 - "StatusChip"
Cohesion: 0.14
Nodes (5): ChipColors, StatusChip(), MoneyColors, StatusChipTest, 3.1 Palettes (DS6a, DS6b)

### Community 91 - "LedgerError"
Cohesion: 0.13
Nodes (18): `ui/common` (shared components, DS3, DS4, DS5 and A5d), FieldError, Required, TooLong, toFieldError(), AmountNotPositive, AmountTooLarge, Archived (+10 more)

### Community 92 - "CollapsingHeaderScaffold"
Cohesion: 0.09
Nodes (36): Manifest security posture (SEC-2, SEC-3, SEC-6), MainActivity, BottomActionBar(), CollapsingHeaderScaffold(), EditFormScaffold(), GroupedSection(), Build-time trust decisions, Content primitives (DS5) (+28 more)

### Community 94 - "PftTheme"
Cohesion: 0.21
Nodes (4): DesignCatalog(), PftTheme(), DesignCatalogTest, Step 11 — `NameField` and `EditFormScaffold` (A5d)

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 96 - "ThemePalette"
Cohesion: 0.17
Nodes (8): colors(), ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, paletteFor(), ChipColorsTest

### Community 98 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.10
Nodes (17): Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen, What it does for you, Behaviour details, First run and profiles, What it does for you, 10. Risks & mitigations (+9 more)

### Community 102 - "ThemeLiteralScanTest"
Cohesion: 0.16
Nodes (5): LoggingScanTest, SourceScan, Violation, ThemeLiteralScanTest, Planned: premium look (DS6)

### Community 104 - "MotionSection.kt"
Cohesion: 0.14
Nodes (7): MotionSection(), SpringTrack(), PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch(), PftShapes

### Community 105 - "How to run and test"
Cohesion: 0.11
Nodes (8): How to run and test, AuditDiffTest, Thing, EntityMetaTest, IdentityServiceTest, IdsTest, NameBasedIdTest, SettingsServiceTest

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 108 - "BundledFontTest"
Cohesion: 0.07
Nodes (4): LogEventShapeTest, TextBearing, NameFieldTest, BundledFontTest

### Community 109 - "HostedComposeRule"
Cohesion: 0.14
Nodes (3): HostedComposeRule, RegisterHostActivity, ReleaseHasNoCatalogueTest

### Community 111 - "PftNavHost"
Cohesion: 0.12
Nodes (9): `ui/nav` and `ui/home` (A5f), HomeRoute(), HomeScreen(), PftNavHost(), Routes, SetupRoute(), SetupScreen(), Where it lives (+1 more)

### Community 112 - "InMemoryKekSource"
Cohesion: 0.13
Nodes (3): AndroidKeystoreKek, KekSource, InMemoryKekSource

### Community 113 - "Personal Finance Tracker PRD"
Cohesion: 0.22
Nodes (11): Personal Finance Tracker PRD, Audit trail (INT-2), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Milestone 3 (insights), Non-functional requirements, Security, privacy and integrity requirements (SEC/INT) (+3 more)

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 116 - "LogEvent"
Cohesion: 0.20
Nodes (9): `log` (safe logging, A5a), DbKeyUnavailable, DbOpened, IdentityCorrupt, LogEvent, LogValue, render(), SetupCompleted (+1 more)

### Community 117 - "AppContainer"
Cohesion: 0.11
Nodes (12): Public interface, AppContainer, PftApplication, PftRoot(), SlowOpeningApplication, RoomTestKit, PftRootTest, Call flow example: first-run Setup (+4 more)

### Community 118 - "FieldState"
Cohesion: 0.27
Nodes (3): FieldCheck, FieldState, FieldStateTest

### Community 119 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.14
Nodes (16): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID) (+8 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.14
Nodes (8): PreMigrationBackup, `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "shouldbe"
Cohesion: 0.08
Nodes (25): RoomAuditLog, AuditDaoTest, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE (+17 more)

### Community 125 - "`di` and startup (A5b)"
Cohesion: 0.22
Nodes (10): `di` and startup (A5b), AppStart, KeyUnavailable, IdentityCorrupt, KeyUnavailable, LaunchState, Opening, Ready (+2 more)

### Community 126 - "CategoryGlyph.kt"
Cohesion: 0.19
Nodes (6): fixedSp(), GlyphSize, Chip, Hero, Adding an icon, Icons and emoji (DS3, UI-6)

### Community 127 - "7. Test plan"
Cohesion: 0.14
Nodes (9): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 3.4 Header glow (UI-13, DS6d), 3.8 Abstraction bar (+1 more)

### Community 128 - "SetupViewModel"
Cohesion: 0.20
Nodes (6): `ui/setup` and the UiState convention (A5e), SetupUiState, SetupViewModel, AlreadyInitialised, 5. Flows, Step 12 — `SetupViewModel` (A5e)

### Community 129 - "7. Test plan"
Cohesion: 0.30
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 131 - "PaiseParser"
Cohesion: 0.16
Nodes (8): Configuration, `:core:money` — integer-paise money, Data it owns, Parsing grammar, Public interface, Purpose, Responsibilities & boundaries, PaiseParser

### Community 132 - "IdentityRoomIntegrationTest"
Cohesion: 0.20
Nodes (3): FailingOnNthAppend, IdentityRoomIntegrationTest, Step 5 — `AppServices`: the production object graph (A5b)

### Community 137 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.29
Nodes (7): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice I — Cashew import (`feat/m1a-cashew-import`): tracking stub

### Community 139 - "Conventions"
Cohesion: 0.33
Nodes (5): NameBasedId, Conventions, Data model, Version 1 (A4a), Version table

### Community 141 - "AppSetting"
Cohesion: 0.26
Nodes (4): RoomAppSettingsRepository, AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository

### Community 147 - "PftSpacing"
Cohesion: 0.22
Nodes (8): PftSpacing, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins"), 3. Design & decision

### Community 148 - ".fromIconKey"
Cohesion: 0.27
Nodes (5): Emoji, GlyphSource, isSymbolKey(), Letter, Symbol

### Community 149 - "DatabaseFactory"
Cohesion: 0.12
Nodes (12): DatabaseFactory, PreMigrationBackupFailed, open(), Corruption handling, Persistence and security, Pre-migration backup (INT-5), SQLCipher, The key and the KEK (+4 more)

### Community 154 - "NameField"
Cohesion: 0.25
Nodes (4): EditFormPreview(), EditFormSample(), errorMessage(), NameField()

### Community 156 - "CategoryPalette"
Cohesion: 0.43
Nodes (3): CategoryColors, CategoryPalette, pair()

### Community 157 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 160 - "Appearance: colour palettes, dynamic colour, background and type"
Cohesion: 0.40
Nodes (5): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you

## Knowledge Gaps
- **198 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+193 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 603 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **67 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `7. Test plan`, `BottomActionBarSurfaceTest.kt`, `IdentityRoomIntegrationTest`, `EmptyState`, `assertnotequals`, `ManifestHardeningTest`, `ThemePrefs`, `MoneyTextTest`, `LaunchRoutingTest.kt`, `PftSurfacesTest`, `ColorContrastTest`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `SafeLogTest`, `HomeScreenTest.kt`, `assertequals`, `DbKeyManagerWiringTest.kt`, `ThemePrefsTest`, `PftSymbol`, `.check`, `RoomEntityRepositoryTest.kt`, `9. Documentation updates (all required surfaces)`, `CollapsingHeaderScaffoldTest`, `CollapsingHeaderScaffold.kt`, `BackgroundStyle`, `DbKeyManagerJvmTest`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `PftShapesTest.kt`, `GlyphSourceTest`, `AppContainerTest.kt`, `CategoryGlyphScreenshotTest`, `SetupScreenshotTest.kt`, `AuditEntryMapperTest`, `PftThemeModeTest`, `RoomAuditColumnsTest`, `EntityMeta`, `ContentScreenshotTest`, `DbKeyManagerTest.kt`, `UserId`, `SetupViewModelTest`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `SetupFlowTest.kt`, `MainActivityOpeningTest.kt`, `runSuspending`, `13. Deviations from the plan, by slice`, `Previews, the token sheet and screenshot goldens`, `SetupScreenTest.kt`, `PftSymbolTest`, `SampleThing.kt`, `MigrationTestSupport`, `PreMigrationBackupTest`, `StatusChip`, `ThemePaletteTest`, `CollapsingHeaderScaffold`, `GroupedSectionTest.kt`, `PftTheme`, `ThemePalette`, `DevRouteTest.kt`, `DatabaseStartupTest.kt`, `Paise`, `ThemeLiteralScanTest`, `BundledFontTest`, `HostedComposeRule`, `assertisdisplayed`, `AppContainer`, `FieldState`, `BundledSymbolsLicenceTest`, `shouldbe`, `WindowThemeTest`?**
  _High betweenness centrality (0.268) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _198 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `BottomActionBarSurfaceTest.kt`, `EmptyState`, `AccentCheckSection.kt`, `ThemePrefs`, `MoneyTextTest`, `LaunchRoutingTest.kt`, `ColorContrastTest`, `BottomActionBar.kt`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `MoneyTextTest.kt`, `PftSpacing`, `HomeScreenTest.kt`, `NameField`, `DesignCatalog.kt`, `CategoryPalette`, `CollapsingHeaderScaffoldTest`, `9. Documentation updates (all required surfaces)`, `StatusChipSamples.kt`, `CollapsingHeaderScaffold.kt`, `BackgroundStyle`, `How to run and test`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `PftShapesTest.kt`, `Project README`, `PftTypographyTest.kt`, `PftMotion.kt`, `HeaderTypePairingTest.kt`, ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`, `HeaderIconButtonTest.kt`, `CategoryGlyphScreenshotTest`, `SetupScreenshotTest.kt`, `PftThemeModeTest`, `ContentScreenshotTest`, `MoneyText.kt`, `asserttrue`, `TypePairing`, `GroupedSection.kt`, `SetupFlowTest.kt`, `13. Deviations from the plan, by slice`, `Previews, the token sheet and screenshot goldens`, `SetupScreenTest.kt`, `modifier`, `Plan: M1a design system and visual direction (lands before A5)`, `StatusChip`, `CollapsingHeaderScaffold`, `GroupedSectionTest.kt`, `DevRouteTest.kt`, `MotionSection.kt`, `BundledFontTest`, `HostedComposeRule`, `assertisdisplayed`, `AppContainer`, `CategoryGlyph.kt`, `7. Test plan`?**
  _High betweenness centrality (0.099) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `TestClock` be split into smaller, more focused modules?**
  _Cohesion score 0.06363636363636363 - nodes in this community are weakly interconnected._