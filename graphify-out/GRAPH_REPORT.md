# Graph Report - personal-finance-tracker  (2026-11-14)

## Corpus Check
- 326 files · ~245,813 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3908 nodes · 11586 edges · 172 communities (96 shown, 76 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 1836 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `11c920a1`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- LogEvent
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- AuditedWriterTest.kt
- hooks
- test_import_symbols.py
- FieldChange
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
- SampleThing.kt
- `di` and startup (A5b)
- AppServicesTest
- CollapsingHeaderScaffold.kt
- 6.1 Slices
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- HeaderGlowScaffoldTest.kt
- PftTypographyTest
- CategoryGlyph
- PftShapesTest.kt
- GlyphSourceTest
- LaunchViewModelTest.kt
- Project README
- Account
- PftMotion.kt
- HeaderTypePairingTest.kt
- PreMigrationBackup
- CLAUDE.md Development Constitution
- AppContainer
- CategoryGlyphScreenshotTest
- AuditedWriter
- Design system
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
- ThemePaletteTest
- PftTypography.kt
- Pseudo
- GroupedSection.kt
- Public interface
- SetupFlowTest.kt
- MoneyColors
- PftDatabaseTest
- _Text
- MainActivityOpeningTest.kt
- import_symbols.py
- AppContainerTest.kt
- MotionSection.kt
- Previews, the token sheet and screenshot goldens
- 7. Test plan
- PftSymbolTest
- ReleaseHasNoCatalogueTest.kt
- `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- RoomAccountIntegrationTest
- SetupViewModel.kt
- MigrationTestSupport
- RoomAuditedWriterIntegrationTest
- PreMigrationBackupTest
- TabularFiguresTest
- TokenSheetScreenshotTest.kt
- HeaderIconButtonTest.kt
- NavTransitionTest.kt
- StatusChip
- GroupedSectionTest.kt
- PftTheme
- DbKeyManager
- MainActivity
- Plan: M1a — Core ledger (manual-entry household tracker)
- DatabaseStartupTest.kt
- Paise
- RoomEntityRepositoryTest.kt
- IdentityRoomIntegrationTest
- PftDatabase.kt
- PaletteSamples.kt
- Plan: Itemised transactions (one payment, many categories)
- parse_svg
- NameFieldTest.kt
- BundledFontTest
- BottomActionBarTest.kt
- ImportRunTest
- PftRoot
- AndroidKeystoreKek
- CatalogSection
- DataUnavailableScreenTest
- HostedComposeRule
- LogEventShapeTest
- `ui/common` (shared components, DS3, DS4, DS5 and A5d)
- FieldState
- Loans and EMIs (LN-1..LN-5)
- MigrationTest
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- Slice A — Foundation (`feat/m1a-foundation`)
- AppSetting
- PftTheme.kt
- RoomTransactionRunnerTest
- LoggingScanTest
- PaletteScreenshotTest.kt
- runSuspending
- SetupScreenshotTest.kt
- PaiseParser
- AccountDao
- EmptyState
- AuditEntryEntity
- 7. Test plan
- ManifestHardeningTest
- DevRouteTest.kt
- How to run and test
- .of
- DbKeyManagerWiringTest.kt
- PftSpacingTest
- TestClock
- BottomActionBar
- r
- SafeLogTest
- MoneyTextTest.kt
- .fromIconKey
- DatabaseFactory
- PaletteSwatchesTest.kt
- assertequals
- AuditTriggers
- ThemePrefsTest
- 6. Implementation steps (TDD-ordered)
- Appearance: colour palettes, dynamic colour, background and type
- 6. Implementation steps (TDD-ordered)
- Layered Modular Architecture (core vs app)
- SetupScreenTest.kt
- 3. Design & decision
- MoneySize
- 13. Deviations from the plan, by slice
- EditFormScaffoldTest.kt
- Data protection: encrypted storage and the "can't open your data" screen
- AuditLog
- HomeScreenTest.kt
- BundledSymbolsLicenceTest
- Personal Finance Tracker PRD
- FieldErrorMessageTest.kt
- Icons and emoji (DS3, UI-6)
- Immutable Audit Trail

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 192 edges
2. `ThemePrefs` - 161 edges
3. `PftSymbol` - 125 edges
4. `Paise` - 112 edges
5. `How to run and test` - 107 edges
6. `HostedComposeRule` - 94 edges
7. `runSuspending()` - 85 edges
8. `CollapsingHeaderScaffoldTest` - 82 edges
9. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 71 edges
10. `PftDatabase` - 61 edges

## Surprising Connections (you probably didn't know these)
- `Step 1 — `LogEvent` rendering (A5a)` --references--> `LogEvent`  [INFERRED]
  docs/plans/2026-11-01-m1a-a5-first-run-setup.md → app/src/main/kotlin/dev/imagh/pft/app/log/LogEvent.kt
- `Step 2 — `LogEvent` can hold no text (A5a)` --references--> `LogEvent`  [INFERRED]
  docs/plans/2026-11-01-m1a-a5-first-run-setup.md → app/src/main/kotlin/dev/imagh/pft/app/log/LogEvent.kt
- `3.5 Fonts: bundling, licensing, custom import` --references--> `SafeLog`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/log/SafeLog.kt
- `Step 10 — `FieldState` and `FieldError` (A5d)` --references--> `Required`  [INFERRED]
  docs/plans/2026-11-01-m1a-a5-first-run-setup.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/FieldError.kt
- `13. Deviations from the plan, by slice` --references--> `asHeroTop()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderGlow.kt

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

## Communities (172 total, 76 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "LogEvent"
Cohesion: 0.09
Nodes (14): `log` (safe logging, A5a), DatabaseStartup, KeyUnavailable, Ready, DbKeyUnavailable, DbOpened, IdentityCorrupt, LogEvent (+6 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "AuditedWriterTest.kt"
Cohesion: 0.14
Nodes (7): AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "FieldChange"
Cohesion: 0.14
Nodes (4): AuditEntryMapper, AuditDaoTest, AuditEntryMapperTest, FieldChange

### Community 9 - "ThemePrefs"
Cohesion: 0.15
Nodes (4): resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.13
Nodes (11): Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone(), roles() (+3 more)

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.12
Nodes (34): GroupedSection(), directionLabel(), moneySign(), MoneyText(), Balance, Budget, BudgetState, AT_THRESHOLD (+26 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "DesignCatalog.kt"
Cohesion: 0.07
Nodes (3): ChoiceRow(), TokenSheet(), TokenSheetPreview()

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.08
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "SampleThing.kt"
Cohesion: 0.21
Nodes (6): SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 30 - "`di` and startup (A5b)"
Cohesion: 0.11
Nodes (26): `di` and startup (A5b), AppStart, KeyUnavailable, IdentityCorrupt, KeyUnavailable, LaunchState, LaunchViewModel, Opening (+18 more)

### Community 32 - "CollapsingHeaderScaffold.kt"
Cohesion: 0.05
Nodes (31): CollapsingHeader(), CollapsingHeaderScaffold(), expandedHeaderHeight(), HeaderChildren(), HeaderContent, HeaderPeriod, HeaderPhase, Collapsed (+23 more)

### Community 33 - "6.1 Slices"
Cohesion: 0.06
Nodes (36): GroupedSectionColors, BackgroundStyle, FLAT, TONAL, PftSurfaces, colors(), ThemePalette, AUBERGINE (+28 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.25
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 37 - "PftTypographyTest"
Cohesion: 0.11
Nodes (3): sansFamily(), PftTypographyTest, Resolved

### Community 42 - "Project README"
Cohesion: 0.21
Nodes (9): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer (+1 more)

### Community 43 - "Account"
Cohesion: 0.06
Nodes (22): AccountEntity, AccountMapper, RoomAccountRepository, EntityMapper, RoomAccountMapperTest, AuditedTable, AuditedTables, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...) (+14 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.11
Nodes (3): PftMotion, pressScale(), 11. Rollout & verification

### Community 45 - "HeaderTypePairingTest.kt"
Cohesion: 0.16
Nodes (4): HeaderTypePairingTest, assertShownInFull(), isShownInFull(), layout()

### Community 46 - "PreMigrationBackup"
Cohesion: 0.15
Nodes (11): PreMigrationBackup, PreMigrationBackupFailed, Corruption handling, Persistence and security, Pre-migration backup (INT-5), SQLCipher, The key and the KEK, The key array and SQLCipher (a trap) (+3 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "AppContainer"
Cohesion: 0.08
Nodes (8): AppContainer, RoomTestKit, LaunchRoutingTest, PftRootTest, Call flow example: first-run Setup, Startup, 7. Test plan, Step 14 — `PftNavHost`, Home placeholder, debug route, predictive back (A5f)

### Community 49 - "CategoryGlyphScreenshotTest"
Cohesion: 0.21
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 50 - "AuditedWriter"
Cohesion: 0.13
Nodes (9): RoomEntityRepository, Responsibilities & boundaries, AuditedWriter, AuditSource, MANUAL, RECURRING, SYSTEM, EntityRepository (+1 more)

### Community 51 - "Design system"
Cohesion: 0.10
Nodes (18): FontBudgetTest, Adding a token, Build-time trust decisions, Design system, Motion (UI-10), Planned: premium look (DS6), Principles, Shapes (+10 more)

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.06
Nodes (23): `data` (Room persistence, A4a), MetaColumns, MetaMapper, MetaMapperTest, EntityMeta, LedgerEntity, Complete, HasDerived (+15 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.21
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "MoneyText.kt"
Cohesion: 0.18
Nodes (7): atSize(), Figure(), fitHero(), HeroFit, HeroText(), RollingDigits(), toneColor()

### Community 58 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.14
Nodes (17): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope (+9 more)

### Community 59 - "UserId"
Cohesion: 0.06
Nodes (20): EpochMillis, RoomIdentityRepository, RoomIdentityRepositoryTest, TestDatabase, AuditEntry, Device, IdentityRepository, UserProfile (+12 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 61 - "SetupViewModelTest"
Cohesion: 0.16
Nodes (3): awaitMain(), FailsFirstAppend, SetupViewModelTest

### Community 62 - "ThemePaletteTest"
Cohesion: 0.11
Nodes (4): CategoryPaletteTest, PftSurfacesTest, Pins, ThemePaletteTest

### Community 63 - "PftTypography.kt"
Cohesion: 0.14
Nodes (12): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansWeights(), DisplayFont, BUNDLED_SERIF (+4 more)

### Community 65 - "GroupedSection.kt"
Cohesion: 0.11
Nodes (6): GroupedRow, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, GroupedSectionPreview(), GroupedSectionSamples()

### Community 66 - "Public interface"
Cohesion: 0.05
Nodes (42): RoomTransactionRunner, Behaviours worth knowing, Configuration, How an entity step composes the blocks, Public interface, AccountRules, AuditDiff, AuditRecorder (+34 more)

### Community 68 - "MoneyColors"
Cohesion: 0.16
Nodes (5): ChipColors, MoneyColors, PftSpacing, ChipColorsTest, 3.3 Tokens

### Community 72 - "MainActivityOpeningTest.kt"
Cohesion: 0.13
Nodes (3): MainActivityOpeningTest, SlowOpeningApplication, MainActivityStartupTest

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 75 - "MotionSection.kt"
Cohesion: 0.06
Nodes (13): AccentCheckSection(), SectionContent(), DevHeaderActions(), devRoutes(), MotionSection(), SpringTrack(), fixedSp(), EmptyStatePreview() (+5 more)

### Community 76 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.16
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 77 - "7. Test plan"
Cohesion: 0.13
Nodes (10): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 1. Requirement, 3.4 Header glow (UI-13, DS6d) (+2 more)

### Community 80 - "`ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)"
Cohesion: 0.15
Nodes (10): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, AccentHueTest, hueDistance() (+2 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.15
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 82 - "UserProfileEntity"
Cohesion: 0.22
Nodes (3): IdentityDao, DeviceEntity, UserProfileEntity

### Community 84 - "SetupViewModel.kt"
Cohesion: 0.10
Nodes (8): `ui/setup` and the UiState convention (A5e), HomeUiState, HomeViewModel, SetupUiState, SetupViewModel, AlreadyInitialised, Step 12 — `SetupViewModel` (A5e), Step 4 — The database open is logged (A5a)

### Community 94 - "PftTheme"
Cohesion: 0.14
Nodes (3): DesignCatalog(), PftTheme(), DesignCatalogTest

### Community 95 - "DbKeyManager"
Cohesion: 0.26
Nodes (4): DbKeyManager, Decoded, WrappedKeyFormat, Migrations on the encrypted file (`EncryptedMigrationTest`)

### Community 96 - "MainActivity"
Cohesion: 0.15
Nodes (17): `:app` — Android application, Configuration, Data it owns, Debug and release source sets, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Responsibilities and boundaries, Window theme (+9 more)

### Community 98 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.25
Nodes (8): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 9. Documentation updates (all required surfaces), Plan: M1a — Core ledger (manual-entry household tracker)

### Community 104 - "PaletteSamples.kt"
Cohesion: 0.25
Nodes (5): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch(), PftShapes

### Community 105 - "Plan: Itemised transactions (one payment, many categories)"
Cohesion: 0.22
Nodes (9): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5. Flows, 8. Security considerations, 9. Documentation updates (all required surfaces) (+1 more)

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 111 - "PftRoot"
Cohesion: 0.08
Nodes (27): Purpose, `security` and the encrypted open (A4b), `ui/nav` and `ui/home` (A5f), SafeLog, EditFormScaffold(), NameField(), HomeRoute(), HomeScreen() (+19 more)

### Community 113 - "CatalogSection"
Cohesion: 0.14
Nodes (14): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+6 more)

### Community 114 - "DataUnavailableScreenTest"
Cohesion: 0.15
Nodes (8): Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING, DataUnavailableScreenTest

### Community 115 - "HostedComposeRule"
Cohesion: 0.22
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 117 - "`ui/common` (shared components, DS3, DS4, DS5 and A5d)"
Cohesion: 0.14
Nodes (8): `ui/common` (shared components, DS3, DS4, DS5 and A5d), FieldError, InvalidLastFour, Required, TooLong, toFieldError(), Row, InMemoryEntityStore

### Community 118 - "FieldState"
Cohesion: 0.24
Nodes (3): FieldCheck, FieldState, FieldStateTest

### Community 119 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.14
Nodes (16): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID) (+8 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.10
Nodes (13): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+5 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.19
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.06
Nodes (27): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, AuditAction, ARCHIVE, CREATE (+19 more)

### Community 124 - "AppSetting"
Cohesion: 0.20
Nodes (6): RoomAuditLog, RoomAppSettingsRepository, AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository, The core/app boundary

### Community 125 - "PftTheme.kt"
Cohesion: 0.12
Nodes (4): EditFormPreview(), EditFormSample(), dynamicPaletteColors(), FormScreenshotTest

### Community 127 - "LoggingScanTest"
Cohesion: 0.24
Nodes (4): LoggingScanTest, SourceScan, Violation, Step 3 — `SafeLog` writes the line, and nothing else can log (A5a)

### Community 129 - "runSuspending"
Cohesion: 0.27
Nodes (3): RoomAppSettingsRepositoryTest, runSuspending(), 3.3 Dependencies (one pins-only PR, A5c)

### Community 131 - "PaiseParser"
Cohesion: 0.16
Nodes (8): Configuration, `:core:money` — integer-paise money, Data it owns, Parsing grammar, Public interface, Purpose, Responsibilities & boundaries, PaiseParser

### Community 135 - "7. Test plan"
Cohesion: 0.32
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 136 - "ManifestHardeningTest"
Cohesion: 0.05
Nodes (11): SyncWindowBackground(), ManifestHardeningTest, WindowBackgroundTest, WindowThemeTest, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`, 3.5 Fonts: bundling, licensing, custom import (+3 more)

### Community 138 - "How to run and test"
Cohesion: 0.14
Nodes (5): How to run and test, PftDatabase, AppServices, HomeViewModelTest, SetupRouteTest

### Community 139 - ".of"
Cohesion: 0.20
Nodes (7): EpochMillisTest, NameBasedId, Conventions, Data model, Version 1 (A4a), Version 2 (B1c), Version table

### Community 142 - "TestClock"
Cohesion: 0.08
Nodes (8): clockAt(), Uuid7Test, TestClock, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest, 7. Test plan

### Community 143 - "BottomActionBar"
Cohesion: 0.16
Nodes (5): BottomActionBar(), BottomActionBarPreview(), BottomActionBarSamples(), StatusChipPreview(), StatusChipSamples()

### Community 146 - "MoneyTextTest.kt"
Cohesion: 0.12
Nodes (4): Dependencies, layoutResult(), shownText(), PaiseFormat

### Community 148 - ".fromIconKey"
Cohesion: 0.27
Nodes (5): Emoji, GlyphSource, isSymbolKey(), Letter, Symbol

### Community 149 - "DatabaseFactory"
Cohesion: 0.12
Nodes (5): EncryptedMigrationTest, DatabaseFactory, open(), AccountServiceTest, Slice B — Accounts, categories, tags (`feat/m1a-ledger-setup`)

### Community 152 - "AuditTriggers"
Cohesion: 0.33
Nodes (3): AuditTriggers, The audit trail, Audit triggers

### Community 154 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.22
Nodes (9): 6. Implementation steps (TDD-ordered), Later-slice deltas (no new PRs; each rides its slice's PR), PR breakdown (incremental — required), Step C2 — Transfers (delta to the plan's C2), Step C3 — Queries, search, balances (delta to the plan's C3), Step C4 — List and plain add/edit UI (delta to the plan's C4), Step C4b — Items editor (new), Step C5 — Search, filters and detail with history (delta to the plan's C5) (+1 more)

### Community 155 - "Appearance: colour palettes, dynamic colour, background and type"
Cohesion: 0.25
Nodes (5): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you

### Community 156 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.29
Nodes (7): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice I — Cashew import (`feat/m1a-cashew-import`): tracking stub

### Community 157 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 159 - "3. Design & decision"
Cohesion: 0.33
Nodes (6): 3.1 The model: every transaction has one or more lines, 3.4 Reads, 3.6 Configuration (CLAUDE.md §3.5), 3.7 Future items recorded, not built, 3.8 Abstraction bar, 3. Design & decision

### Community 160 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 161 - "13. Deviations from the plan, by slice"
Cohesion: 0.19
Nodes (4): toneOf(), ThemeLiteralScanTest, WindowFlashToneTest, 13. Deviations from the plan, by slice

### Community 164 - "Data protection: encrypted storage and the "can't open your data" screen"
Cohesion: 0.22
Nodes (7): Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen, What it does for you, Behaviour details, First run and profiles, What it does for you

### Community 168 - "Personal Finance Tracker PRD"
Cohesion: 0.22
Nodes (11): Personal Finance Tracker PRD, Audit trail (INT-2), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Milestone 3 (insights), Non-functional requirements, Security, privacy and integrity requirements (SEC/INT) (+3 more)

### Community 172 - "Icons and emoji (DS3, UI-6)"
Cohesion: 0.24
Nodes (7): CategoryColors, CategoryPalette, pair(), Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

## Knowledge Gaps
- **203 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+198 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 625 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **76 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `FieldChange`, `ThemePrefs`, `MoneyTextTest`, `ColorContrastTest`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `SampleThing.kt`, ``di` and startup (A5b)`, `AppServicesTest`, `CollapsingHeaderScaffold.kt`, `6.1 Slices`, `DbKeyManagerJvmTest`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `PftShapesTest.kt`, `GlyphSourceTest`, `LaunchViewModelTest.kt`, `Account`, `AppContainer`, `CategoryGlyphScreenshotTest`, `PftThemeModeTest`, `RoomAuditColumnsTest`, `EntityMeta`, `ContentScreenshotTest`, `DbKeyManagerTest.kt`, `UserId`, `SetupViewModelTest`, `ThemePaletteTest`, `Public interface`, `SetupFlowTest.kt`, `MoneyColors`, `MainActivityOpeningTest.kt`, `AppContainerTest.kt`, `Previews, the token sheet and screenshot goldens`, `PftSymbolTest`, `ReleaseHasNoCatalogueTest.kt`, ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`, `RoomAccountIntegrationTest`, `MigrationTestSupport`, `RoomAuditedWriterIntegrationTest`, `PreMigrationBackupTest`, `TokenSheetScreenshotTest.kt`, `NavTransitionTest.kt`, `StatusChip`, `GroupedSectionTest.kt`, `PftTheme`, `MainActivity`, `DatabaseStartupTest.kt`, `Paise`, `RoomEntityRepositoryTest.kt`, `IdentityRoomIntegrationTest`, `NameFieldTest.kt`, `BundledFontTest`, `BottomActionBarTest.kt`, `PftRoot`, `DataUnavailableScreenTest`, `LogEventShapeTest`, ``ui/common` (shared components, DS3, DS4, DS5 and A5d)`, `FieldState`, `MigrationTest`, `AppSetting`, `PftTheme.kt`, `RoomTransactionRunnerTest`, `LoggingScanTest`, `PaletteScreenshotTest.kt`, `runSuspending`, `SetupScreenshotTest.kt`, `EmptyState`, `7. Test plan`, `ManifestHardeningTest`, `DevRouteTest.kt`, `DbKeyManagerWiringTest.kt`, `PftSpacingTest`, `SafeLogTest`, `MoneyTextTest.kt`, `applicationprovider`, `DatabaseFactory`, `assertequals`, `ThemePrefsTest`, `SetupScreenTest.kt`, `13. Deviations from the plan, by slice`, `EditFormScaffoldTest.kt`, `HomeScreenTest.kt`, `BundledSymbolsLicenceTest`, `FieldErrorMessageTest.kt`?**
  _High betweenness centrality (0.258) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _203 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `PaletteScreenshotTest.kt`, `SetupScreenshotTest.kt`, `EmptyState`, `ManifestHardeningTest`, `ThemePrefs`, `MoneyTextTest`, `How to run and test`, `DevRouteTest.kt`, `PftSpacingTest`, `BottomActionBar`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `MoneyTextTest.kt`, `PaletteSwatchesTest.kt`, `DesignCatalog.kt`, `CollapsingHeaderScaffoldTest`, `SetupScreenTest.kt`, `CollapsingHeaderScaffold.kt`, `6.1 Slices`, `EditFormScaffoldTest.kt`, `13. Deviations from the plan, by slice`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `HomeScreenTest.kt`, `PftShapesTest.kt`, `FieldErrorMessageTest.kt`, `Project README`, `Icons and emoji (DS3, UI-6)`, `PftMotion.kt`, `HeaderTypePairingTest.kt`, `AppContainer`, `CategoryGlyphScreenshotTest`, `Design system`, `PftThemeModeTest`, `ContentScreenshotTest`, `MoneyText.kt`, `PftTypography.kt`, `GroupedSection.kt`, `SetupFlowTest.kt`, `MoneyColors`, `MotionSection.kt`, `Previews, the token sheet and screenshot goldens`, `7. Test plan`, `ReleaseHasNoCatalogueTest.kt`, ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`, `Plan: M1a design system and visual direction (lands before A5)`, `TokenSheetScreenshotTest.kt`, `HeaderIconButtonTest.kt`, `StatusChip`, `GroupedSectionTest.kt`, `MainActivity`, `PaletteSamples.kt`, `NameFieldTest.kt`, `BottomActionBarTest.kt`, `PftRoot`, `DataUnavailableScreenTest`, `PftTheme.kt`?**
  _High betweenness centrality (0.094) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._