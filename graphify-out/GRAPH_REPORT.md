# Graph Report - personal-finance-tracker  (2026-11-02)

## Corpus Check
- 296 files · ~220,263 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3614 nodes · 10505 edges · 162 communities (84 shown, 78 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 1663 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `22c75f02`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- InMemoryEntityRepository
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- Slice A — Foundation (`feat/m1a-foundation`)
- hooks
- test_import_symbols.py
- MotionSection.kt
- ThemePrefs
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Catalog.kt
- detekt.yml
- PftThemeModeTest
- PftSymbol
- CollapsingHeaderScaffoldTest
- SampleThing.kt
- How to run and test
- LedgerError
- CollapsingHeaderScaffold.kt
- ThemePalette
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- HeaderGlowScaffoldTest.kt
- PftTypographyTest
- PftTypographyTest.kt
- PftShapesTest.kt
- CategoryGlyph
- LaunchViewModelTest.kt
- Project README
- 13. Deviations from the plan, by slice
- PftMotion.kt
- HeaderTypePairingTest.kt
- `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)
- CLAUDE.md Development Constitution
- HeaderIconButtonTest.kt
- FieldState
- AuditEntryMapperTest.kt
- AuditDao
- RoomAuditColumnsTest
- EntityMeta
- ContentScreenshotTest
- DbKeyManagerTest
- MoneyText.kt
- Personal Finance Tracker PRD
- UserId
- redact_text
- SetupViewModelTest
- MoneyTextTest.kt
- RoomAuditedWriterIntegrationTest
- Pseudo
- AppSettingEntity
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- Collecting and Redacting Sample Messages Guide
- TokenSheetScreenshotTest.kt
- MainActivityOpeningTest.kt
- _Text
- HostedComposeRule
- import_symbols.py
- AppContainerTest.kt
- WindowBackgroundTest.kt
- DesignCatalog.kt
- LoggingScanTest
- AuditTriggersTest
- SetupScreenTest.kt
- 7. Test plan
- UserProfileEntity
- ColorSchemeTestSupport.kt
- runSuspending
- MigrationTestSupport
- GroupedSection.kt
- PreMigrationBackupTest
- TabularFiguresTest
- StatusChip
- NameFieldTest.kt
- Persistence and security
- Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...)
- GroupedSectionTest.kt
- PftTheme
- DbKeyManager
- PftSymbolTest
- BottomActionBar.kt
- Design system
- DatabaseStartupTest.kt
- Paise
- Migrations.kt
- ThemeResolutionTest.kt
- BottomActionBarTest.kt
- PaletteSamples.kt
- SafeLogTest
- parse_svg
- RoomTransactionRunnerTest
- BundledFontTest
- WindowThemeTest
- ImportRunTest
- LogEvent
- InMemoryKekSource
- DbKeyManagerWiringTest.kt
- DataUnavailableScreenTest
- Layered Modular Architecture (core vs app)
- ThemeLiteralScanTest
- AppContainer
- ThemePaletteTest
- Loans and EMIs (LN-1..LN-5)
- CategoryGlyphScreenshotTest
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- shouldbe
- AppServicesTest
- PreMigrationBackup
- ThemePrefsTest
- 7. Test plan
- Plan: M1a — Core ledger (manual-entry household tracker)
- assertnotequals
- LogEventShapeTest
- PaiseParser
- MoneyColors
- EmptyState
- PftRootReadyTest.kt
- PftSpacing
- ManifestHardeningTest
- DesignCatalogTest.kt
- SetupRouteTest.kt
- RoomAppSettingsRepositoryTest.kt
- PftSurfacesTest
- PftSpacingTest
- EditFormScaffoldTest.kt
- .clock
- FontBudgetTest
- RedactTests
- assertisdisplayed
- modifier
- 6. Implementation steps (TDD-ordered)
- DatabaseFactory
- BundledSymbolsLicenceTest
- LogEventTest
- AccentHueTest
- text
- EditFormScaffold
- parse_manifest
- NameBasedIdTest.kt
- MoneySize
- AuditLog
- Immutable Audit Trail

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 182 edges
2. `ThemePrefs` - 151 edges
3. `PftSymbol` - 124 edges
4. `How to run and test` - 93 edges
5. `Paise` - 92 edges
6. `HostedComposeRule` - 83 edges
7. `CollapsingHeaderScaffoldTest` - 82 edges
8. `runSuspending()` - 74 edges
9. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 71 edges
10. `UserId` - 57 edges

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

## Communities (162 total, 78 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "InMemoryEntityRepository"
Cohesion: 0.16
Nodes (6): Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.05
Nodes (21): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, AuditDiffTest, Thing, AuditedWriterTest (+13 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "MotionSection.kt"
Cohesion: 0.08
Nodes (6): AccentCheckSection(), MotionSection(), SpringTrack(), pressScale(), PftShapes, 3.7 Component inventory (UI-9)

### Community 9 - "ThemePrefs"
Cohesion: 0.21
Nodes (5): resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest, 6.1 Slices

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.19
Nodes (8): Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone(), washTop()

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.14
Nodes (28): directionLabel(), moneySign(), MoneyText(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER (+20 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.06
Nodes (6): BottomActionBar(), BottomBarAction, BottomActionBarSurfaceTest, CollapsingHeaderScaffoldTest, Steps, One-hand layout (UI-7)

### Community 29 - "SampleThing.kt"
Cohesion: 0.06
Nodes (15): `data` (Room persistence, A4a), BaseDao, EntityMapper, MetaColumns, MetaMapper, RoomEntityRepository, MetaMapperTest, RoomEntityRepositoryTest (+7 more)

### Community 30 - "How to run and test"
Cohesion: 0.10
Nodes (25): `di` and startup (A5b), How to run and test, Purpose, PftDatabase, AppServices, AppStart, KeyUnavailable, Ready (+17 more)

### Community 31 - "LedgerError"
Cohesion: 0.08
Nodes (31): `ui/common` (shared components, DS3, DS4, DS5 and A5d), SafeLog, FieldError, Required, TooLong, toFieldError(), Identity, AlreadyInitialised (+23 more)

### Community 32 - "CollapsingHeaderScaffold.kt"
Cohesion: 0.05
Nodes (30): CollapsingHeader(), CollapsingHeaderScaffold(), expandedHeaderHeight(), HeaderChildren(), HeaderContent, HeaderPeriod, HeaderPhase, Collapsed (+22 more)

### Community 33 - "ThemePalette"
Cohesion: 0.12
Nodes (14): GroupedSectionColors, BackgroundStyle, FLAT, TONAL, PftSurfaces, colors(), ThemePalette, AUBERGINE (+6 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "HeaderGlowScaffoldTest.kt"
Cohesion: 0.06
Nodes (8): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderGlowScaffoldTest, HeaderScreenshotTest, PressScaleTest, Previews, the token sheet and screenshot goldens

### Community 37 - "PftTypographyTest"
Cohesion: 0.15
Nodes (3): sansFamily(), PftTypographyTest, Resolved

### Community 38 - "PftTypographyTest.kt"
Cohesion: 0.11
Nodes (4): displayFamily(), newsreader(), PftTypography, sansWeights()

### Community 40 - "CategoryGlyph"
Cohesion: 0.06
Nodes (18): CategoryGlyph(), fixedSp(), Emoji, GlyphSource, isEmojiValue(), isSymbolKey(), Letter, Symbol (+10 more)

### Community 42 - "Project README"
Cohesion: 0.23
Nodes (7): Code Structure Map, core/shared Shared Packages, Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen, What it does for you, Project README

### Community 43 - "13. Deviations from the plan, by slice"
Cohesion: 0.08
Nodes (36): pftMaterialTypography(), pftType(), DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, SansFont, FIGTREE (+28 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.09
Nodes (5): iosSpring(), PftMotion, PftMotionTest, Principles, 11. Rollout & verification

### Community 46 - "`ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)"
Cohesion: 0.24
Nodes (7): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, Colour

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 50 - "FieldState"
Cohesion: 0.15
Nodes (4): FieldCheck, FieldState, FieldStateTest, InMemoryEntityStore

### Community 51 - "AuditEntryMapperTest.kt"
Cohesion: 0.17
Nodes (3): AuditEntryEntity, AuditEntryMapper, AuditEntryMapperTest

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.09
Nodes (18): EntityMeta, LedgerEntity, EntityId, AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind (+10 more)

### Community 57 - "MoneyText.kt"
Cohesion: 0.20
Nodes (7): atSize(), Figure(), fitHero(), HeroFit, HeroText(), RollingDigits(), toneColor()

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.09
Nodes (28): Personal Finance Tracker PRD, Audit trail (INT-2), Balance observations (L-10), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5) (+20 more)

### Community 59 - "UserId"
Cohesion: 0.10
Nodes (11): RoomIdentityRepository, RoomIdentityRepositoryTest, AuditedTable, AuditedTables, Device, IdentityRepository, UserProfile, DeviceId (+3 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 66 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.06
Nodes (28): RoomTransactionRunner, RoomAppSettingsRepository, Behaviours worth knowing, Configuration, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, AuditDiff (+20 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 68 - "TokenSheetScreenshotTest.kt"
Cohesion: 0.08
Nodes (4): FormScreenshotTest, SetupScreenshotTest, PaletteScreenshotTest, TokenSheetScreenshotTest

### Community 69 - "MainActivityOpeningTest.kt"
Cohesion: 0.12
Nodes (4): MainActivityOpeningTest, SlowOpeningApplication, MainActivityStartupTest, 10. Risks & mitigations

### Community 72 - "HostedComposeRule"
Cohesion: 0.10
Nodes (20): Debug and release source sets, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, MainActivity, PftApplication, HostedComposeRule, RegisterHostActivity, Surfaces and window colour (DS6c, UI-12, UI-18) (+12 more)

### Community 73 - "import_symbols.py"
Cohesion: 0.16
Nodes (6): The curated list, download(), main(), parse_hashes(), to_vector_drawable(), UnsafeSvg

### Community 75 - "WindowBackgroundTest.kt"
Cohesion: 0.16
Nodes (3): Window theme, SyncWindowBackground(), WindowBackgroundTest

### Community 76 - "DesignCatalog.kt"
Cohesion: 0.08
Nodes (20): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+12 more)

### Community 81 - "7. Test plan"
Cohesion: 0.10
Nodes (20): `:app` — Android application, Configuration, Dependencies, Responsibilities and boundaries, PaiseFormat, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions (+12 more)

### Community 82 - "UserProfileEntity"
Cohesion: 0.20
Nodes (3): IdentityDao, DeviceEntity, UserProfileEntity

### Community 83 - "ColorSchemeTestSupport.kt"
Cohesion: 0.15
Nodes (5): roles(), surfaceRoles(), sweptTones(), toneOf(), WindowFlashToneTest

### Community 84 - "runSuspending"
Cohesion: 0.20
Nodes (3): FailingOnNthAppend, IdentityRoomIntegrationTest, runSuspending()

### Community 85 - "MigrationTestSupport"
Cohesion: 0.08
Nodes (6): AuditTriggers, MigrationTestSupport, Statement, MigrationTest, The audit trail, Audit triggers

### Community 86 - "GroupedSection.kt"
Cohesion: 0.13
Nodes (11): GroupedRow, GroupedSection(), GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, GroupedSectionPreview(), GroupedSectionSamples(), Content primitives (DS5) (+3 more)

### Community 91 - "Persistence and security"
Cohesion: 0.18
Nodes (11): PreMigrationBackupFailed, Corruption handling, Persistence and security, Pre-migration backup (INT-5), SQLCipher, The key and the KEK, The key array and SQLCipher (a trap), The pieces (+3 more)

### Community 92 - "Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...)"
Cohesion: 0.13
Nodes (6): Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), InMemoryAuditLog, InMemoryLedgerStore, AuditFieldsCheck, haveCompleteAuditFields(), shouldHaveCompleteAuditFields()

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 97 - "BottomActionBar.kt"
Cohesion: 0.14
Nodes (4): BottomActionBarPreview(), BottomActionBarSamples(), StatusChipPreview(), StatusChipSamples()

### Community 98 - "Design system"
Cohesion: 0.12
Nodes (14): Adding a token, Build-time trust decisions, Design system, Motion (UI-10), Planned: premium look (DS6), Shapes, Spacing, Third-party assets (+6 more)

### Community 104 - "PaletteSamples.kt"
Cohesion: 0.42
Nodes (4): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 111 - "LogEvent"
Cohesion: 0.09
Nodes (12): `log` (safe logging, A5a), `ui/setup` and the UiState convention (A5e), DbKeyUnavailable, DbOpened, IdentityCorrupt, LogEvent, LogValue, render() (+4 more)

### Community 112 - "InMemoryKekSource"
Cohesion: 0.14
Nodes (3): AndroidKeystoreKek, KekSource, InMemoryKekSource

### Community 114 - "DataUnavailableScreenTest"
Cohesion: 0.11
Nodes (9): DbKeyUnavailable, Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING (+1 more)

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 116 - "ThemeLiteralScanTest"
Cohesion: 0.28
Nodes (4): SourceScan, Violation, ThemeLiteralScanTest, Step 3 — `SafeLog` writes the line, and nothing else can log (A5a)

### Community 119 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5), M1a Core ledger (+2 more)

### Community 120 - "CategoryGlyphScreenshotTest"
Cohesion: 0.23
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.10
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.21
Nodes (8): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), open(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "shouldbe"
Cohesion: 0.07
Nodes (26): RoomAuditLog, AuditDaoTest, TestDatabase, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE (+18 more)

### Community 127 - "7. Test plan"
Cohesion: 0.12
Nodes (11): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, Theming, 1. Requirement (+3 more)

### Community 128 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.12
Nodes (14): Behaviour details, First run and profiles, Not yet (A5f), What it does for you, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement (+6 more)

### Community 131 - "PaiseParser"
Cohesion: 0.16
Nodes (8): Configuration, `:core:money` — integer-paise money, Data it owns, Parsing grammar, Public interface, Purpose, Responsibilities & boundaries, PaiseParser

### Community 132 - "MoneyColors"
Cohesion: 0.21
Nodes (3): ChipColors, MoneyColors, ChipColorsTest

### Community 134 - "PftRootReadyTest.kt"
Cohesion: 0.13
Nodes (8): `security` and the encrypted open (A4b), DatabaseStartup, KeyUnavailable, Ready, DataUnavailableCause, IdentityCorrupt, KeyUnavailable, PftRootReadyTest

### Community 135 - "PftSpacing"
Cohesion: 0.20
Nodes (9): PftSpacing, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens, 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins") (+1 more)

### Community 136 - "ManifestHardeningTest"
Cohesion: 0.10
Nodes (7): ManifestHardeningTest, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 137 - "DesignCatalogTest.kt"
Cohesion: 0.13
Nodes (4): HeaderTags, assertShownInFull(), isShownInFull(), layout()

### Community 143 - ".clock"
Cohesion: 0.18
Nodes (8): Data it owns, EpochMillis, EpochMillisTest, NameBasedId, Conventions, Data model, Version 1 (A4a), Version table

### Community 147 - "modifier"
Cohesion: 0.18
Nodes (4): EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), DevContent()

### Community 148 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.29
Nodes (7): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice I — Cashew import (`feat/m1a-cashew-import`): tracking stub

### Community 152 - "AccentHueTest"
Cohesion: 0.43
Nodes (3): AccentHueTest, hueDistance(), labHue()

### Community 153 - "text"
Cohesion: 0.11
Nodes (4): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples()

### Community 154 - "EditFormScaffold"
Cohesion: 0.10
Nodes (11): EditFormScaffold(), EditFormPreview(), EditFormSample(), errorMessage(), NameField(), PftRoot(), SetupRoute(), SetupScreen() (+3 more)

### Community 157 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

## Knowledge Gaps
- **198 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+193 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 595 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **78 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `assertnotequals`, `LogEventShapeTest`, `MoneyColors`, `EmptyState`, `PftRootReadyTest.kt`, `ManifestHardeningTest`, `ThemePrefs`, `MoneyTextTest`, `RoomAppSettingsRepositoryTest.kt`, `SetupRouteTest.kt`, `PftSpacingTest`, `EditFormScaffoldTest.kt`, `ColorContrastTest`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `PftSurfacesTest`, `BundledSymbolsLicenceTest`, `LogEventTest`, `AccentHueTest`, `PftThemeModeTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `SampleThing.kt`, `CollapsingHeaderScaffold.kt`, `ThemePalette`, `DbKeyManagerJvmTest`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `PftShapesTest.kt`, `CategoryGlyph`, `LaunchViewModelTest.kt`, `PftMotion.kt`, `FieldState`, `AuditEntryMapperTest.kt`, `RoomAuditColumnsTest`, `ContentScreenshotTest`, `DbKeyManagerTest`, `UserId`, `SetupViewModelTest`, `RoomAuditedWriterIntegrationTest`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `TokenSheetScreenshotTest.kt`, `MainActivityOpeningTest.kt`, `HostedComposeRule`, `AppContainerTest.kt`, `WindowBackgroundTest.kt`, `LoggingScanTest`, `AuditTriggersTest`, `SetupScreenTest.kt`, `7. Test plan`, `ColorSchemeTestSupport.kt`, `runSuspending`, `MigrationTestSupport`, `PreMigrationBackupTest`, `StatusChip`, `NameFieldTest.kt`, `GroupedSectionTest.kt`, `PftTheme`, `PftSymbolTest`, `DatabaseStartupTest.kt`, `Paise`, `BottomActionBarTest.kt`, `SafeLogTest`, `RoomTransactionRunnerTest`, `BundledFontTest`, `WindowThemeTest`, `DbKeyManagerWiringTest.kt`, `DataUnavailableScreenTest`, `AppContainer`, `ThemePaletteTest`, `CategoryGlyphScreenshotTest`, `shouldbe`, `AppServicesTest`, `ThemePrefsTest`?**
  _High betweenness centrality (0.242) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _198 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `MoneyColors`, `EmptyState`, `PftRootReadyTest.kt`, `PftSpacing`, `MotionSection.kt`, `ThemePrefs`, `MoneyTextTest`, `SetupRouteTest.kt`, `DesignCatalogTest.kt`, `PftSpacingTest`, `EditFormScaffoldTest.kt`, `ManifestHardeningTest`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `assertisdisplayed`, `modifier`, `text`, `EditFormScaffold`, `PftThemeModeTest`, `CollapsingHeaderScaffoldTest`, `How to run and test`, `CollapsingHeaderScaffold.kt`, `ThemePalette`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `PftTypographyTest.kt`, `PftShapesTest.kt`, `CategoryGlyph`, `13. Deviations from the plan, by slice`, `PftMotion.kt`, `HeaderTypePairingTest.kt`, ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`, `HeaderIconButtonTest.kt`, `ContentScreenshotTest`, `MoneyText.kt`, `MoneyTextTest.kt`, `TokenSheetScreenshotTest.kt`, `HostedComposeRule`, `WindowBackgroundTest.kt`, `DesignCatalog.kt`, `SetupScreenTest.kt`, `7. Test plan`, `GroupedSection.kt`, `StatusChip`, `NameFieldTest.kt`, `GroupedSectionTest.kt`, `BottomActionBar.kt`, `Design system`, `ThemeResolutionTest.kt`, `BottomActionBarTest.kt`, `PaletteSamples.kt`, `DataUnavailableScreenTest`, `AppContainer`, `CategoryGlyphScreenshotTest`, `7. Test plan`?**
  _High betweenness centrality (0.094) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `Slice A — Foundation (`feat/m1a-foundation`)` be split into smaller, more focused modules?**
  _Cohesion score 0.045372050816696916 - nodes in this community are weakly interconnected._