# Graph Report - personal-finance-tracker  (2026-11-11)

## Corpus Check
- 307 files · ~236,909 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3769 nodes · 11087 edges · 181 communities (97 shown, 84 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 1795 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `34b30fb0`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- Leaky
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- RoomAuditedWriterIntegrationTest.kt
- hooks
- test_import_symbols.py
- MotionSection.kt
- ThemePrefs
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- 6.3 Amendments to M1a steps (consumption; this plan edits M1a when approved)
- Catalog.kt
- detekt.yml
- DesignCatalog.kt
- PftSymbol
- CollapsingHeaderScaffoldTest
- SampleThing.kt
- Slice A — Foundation (`feat/m1a-foundation`)
- AppServicesTest
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
- LedgerError
- PftMotion.kt
- HeaderTypePairingTest.kt
- 13. Deviations from the plan, by slice
- CLAUDE.md Development Constitution
- AppContainerTest.kt
- CategoryGlyphScreenshotTest
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- MainActivity
- PftThemeModeTest
- RoomAuditColumnsTest
- EntityMeta
- ContentScreenshotTest
- DbKeyManagerTest.kt
- MoneyText
- Collecting and Redacting Sample Messages Guide
- UserId
- redact_text
- SetupViewModelTest
- NavTransitionTest.kt
- PftTypography.kt
- Pseudo
- GroupedSection.kt
- IdentityService
- SetupFlowTest.kt
- MoneyColors
- PftDatabaseTest
- _Text
- MainActivityOpeningTest.kt
- import_symbols.py
- runSuspending
- WindowBackgroundTest.kt
- Previews, the token sheet and screenshot goldens
- 7. Test plan
- PftSymbolTest
- HeaderIconButtonTest.kt
- PeriodStepper
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- AuditEntryEntity
- SetupViewModel.kt
- MigrationTestSupport
- AuditTriggersTest
- PreMigrationBackupTest
- TabularFiguresTest
- getvalue
- PftSurfacesTest
- Paise
- text
- GroupedSectionTest.kt
- PftTheme
- DbKeyManager
- HeaderPolicy
- DevRouteTest.kt
- Plan: M1a — Core ledger (manual-entry household tracker)
- DatabaseStartupTest.kt
- MoneyToneTest
- Migrations.kt
- LoggingScanTest
- AppSettingEntity
- PaletteSamples.kt
- AuditFieldsCheck.kt
- parse_svg
- RoomTransactionRunnerTest
- BundledFontTest
- BottomActionBarTest.kt
- ImportRunTest
- SetupScreen.kt
- InMemoryKekSource
- CatalogSection
- DataUnavailableScreenTest
- HostedComposeRule
- LogEventShapeTest
- AppContainer
- FieldState
- Loans and EMIs (LN-1..LN-5)
- RoomEntityRepositoryTest.kt
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- shouldbe
- WindowThemeTest
- FormScreenshotTest.kt
- IdentityRoomIntegrationTest
- PftSpacingTest
- PaletteScreenshotTest.kt
- 7. Test plan
- NameFieldTest.kt
- PaiseParser
- expandedHeaderHeight
- EmptyState
- localdensity
- Persistence and security
- ManifestHardeningTest
- 6. Implementation steps (TDD-ordered)
- How to run and test
- MetaMapperTest
- HeaderSlot
- AppSetting
- PaiseParserTest.kt
- BottomActionBar.kt
- FontBudgetTest
- SafeLogTest
- MoneyTextTest.kt
- 3. Design & decision
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- DatabaseFactory
- rowscope
- LogEventTest
- CategoryGlyph.kt
- ThemePrefsTest
- setvalue
- HeaderPhase
- SetupScreenTest.kt
- MoneySize
- SetupScreenTest
- AuditDaoTest.kt
- RoomAppSettingsRepositoryTest.kt
- EditFormScaffoldTest
- `ui/common` (shared components, DS3, DS4, DS5 and A5d)
- Appearance: colour palettes, dynamic colour, background and type
- HomeScreenTest.kt
- applicationprovider
- Personal Finance Tracker PRD
- PaletteSwatchesTest.kt
- .check
- Data protection: encrypted storage and the "can't open your data" screen
- CategoryPalette
- BaseDao
- CategoryPaletteTest
- Layered Modular Architecture (core vs app)
- Icons and emoji (DS3, UI-6)
- HeaderStyle
- AuditLog
- Immutable Audit Trail

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 190 edges
2. `ThemePrefs` - 159 edges
3. `PftSymbol` - 125 edges
4. `How to run and test` - 101 edges
5. `Paise` - 95 edges
6. `HostedComposeRule` - 92 edges
7. `CollapsingHeaderScaffoldTest` - 82 edges
8. `runSuspending()` - 74 edges
9. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 71 edges
10. `PftDatabase` - 57 edges

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

## Communities (181 total, 84 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "Leaky"
Cohesion: 0.24
Nodes (5): Fixture, Leaky, LeakyRepository, Note, NoteId

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "RoomAuditedWriterIntegrationTest.kt"
Cohesion: 0.06
Nodes (13): EpochMillis, EpochMillisTest, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, EntityMetaTest (+5 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "MotionSection.kt"
Cohesion: 0.08
Nodes (8): AccentCheckSection(), DevHeaderActions(), devRoutes(), MotionSection(), SpringTrack(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder()

### Community 9 - "ThemePrefs"
Cohesion: 0.11
Nodes (5): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.07
Nodes (17): AccentHueTest, Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone() (+9 more)

### Community 16 - "6.3 Amendments to M1a steps (consumption; this plan edits M1a when approved)"
Cohesion: 0.16
Nodes (20): moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense (+12 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "DesignCatalog.kt"
Cohesion: 0.08
Nodes (8): ChoiceRow(), SectionContent(), GroupedSectionPreview(), GroupedSectionSamples(), StatusChipPreview(), StatusChipSamples(), TokenSheet(), TokenSheetPreview()

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.05
Nodes (18): BottomActionBar(), BottomBarAction, CollapsingHeaderScaffold(), BottomActionBarSurfaceTest, CollapsingHeaderScaffoldTest, Steps, Adding a token, Build-time trust decisions (+10 more)

### Community 29 - "SampleThing.kt"
Cohesion: 0.16
Nodes (7): EntityMapper, SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 30 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.06
Nodes (61): Debug and release source sets, `di` and startup (A5b), `log` (safe logging, A5a), Public interface, Purpose, `security` and the encrypted open (A4b), `ui/setup` and the UiState convention (A5e), PreMigrationBackupFailed (+53 more)

### Community 31 - "AppServicesTest"
Cohesion: 0.29
Nodes (4): `:app` — Android application, Configuration, Data it owns, AppServicesTest

### Community 33 - "6.1 Slices"
Cohesion: 0.11
Nodes (20): BackgroundStyle, FLAT, TONAL, PftSurfaces, colors(), ThemePalette, AUBERGINE, GRAPHITE (+12 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 37 - "PftTypographyTest"
Cohesion: 0.10
Nodes (4): sansFamily(), PftTypographyTest, Resolved, 3.7 Bundled sans fonts (UI-16, UI-17; DS6f spike done, then DS6h, DS6i)

### Community 42 - "Project README"
Cohesion: 0.21
Nodes (9): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer (+1 more)

### Community 43 - "LedgerError"
Cohesion: 0.07
Nodes (32): Required, AmountNotPositive, AmountTooLarge, Archived, BlankName, Deleted, InvalidSettingKey, LedgerError (+24 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.11
Nodes (3): PftMotion, pressScale(), 11. Rollout & verification

### Community 45 - "HeaderTypePairingTest.kt"
Cohesion: 0.17
Nodes (4): HeaderTypePairingTest, assertShownInFull(), isShownInFull(), layout()

### Community 46 - "13. Deviations from the plan, by slice"
Cohesion: 0.16
Nodes (9): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, ThemeLiteralScanTest, Colour (+1 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "AppContainerTest.kt"
Cohesion: 0.12
Nodes (3): DatabaseStartup, Ready, AppContainerTest

### Community 49 - "CategoryGlyphScreenshotTest"
Cohesion: 0.23
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 50 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.08
Nodes (20): RoomEntityRepository, Behaviours worth knowing, How an entity step composes the blocks, Public interface, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), AuditDiff, AuditedWriter, AuditSource (+12 more)

### Community 51 - "MainActivity"
Cohesion: 0.13
Nodes (20): Window theme, MainActivity, GroupedSection(), WindowFlashToneTest, Surfaces and window colour (DS6c, UI-12, UI-18), 3.7 Component inventory (UI-9), 6.2 Appearance slice: base `feat/m1a-appearance` off `main`, after A-final (Q4), 10. Risks & mitigations (+12 more)

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.09
Nodes (19): EntityMeta, LedgerEntity, AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind, LARGE (+11 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.22
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "MoneyText"
Cohesion: 0.18
Nodes (9): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText(), RollingDigits() (+1 more)

### Community 58 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.14
Nodes (17): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope (+9 more)

### Community 59 - "UserId"
Cohesion: 0.08
Nodes (14): `data` (Room persistence, A4a), MetaMapper, RoomIdentityRepository, RoomIdentityRepositoryTest, AuditedTable, AuditedTables, Auditable, Device (+6 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 61 - "SetupViewModelTest"
Cohesion: 0.16
Nodes (3): awaitMain(), FailsFirstAppend, SetupViewModelTest

### Community 63 - "PftTypography.kt"
Cohesion: 0.09
Nodes (22): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansWeights(), DisplayFont, BUNDLED_SERIF (+14 more)

### Community 65 - "GroupedSection.kt"
Cohesion: 0.11
Nodes (9): GroupedRow, GroupedSectionColors, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, Content primitives (DS5), `EmptyState`, `GroupedSection` (+1 more)

### Community 66 - "IdentityService"
Cohesion: 0.08
Nodes (18): RoomTransactionRunner, Configuration, Responsibilities & boundaries, AuditRecorder, Identity, IdentityService, Uuid7, SettingsService (+10 more)

### Community 68 - "MoneyColors"
Cohesion: 0.15
Nodes (5): ChipColors, MoneyColors, PftSpacing, ChipColorsTest, 3.3 Tokens

### Community 72 - "MainActivityOpeningTest.kt"
Cohesion: 0.11
Nodes (5): PftApplication, MainActivityOpeningTest, SlowOpeningApplication, MainActivityStartupTest, Step 8 — `PftRoot`, the generalised fail-closed screen and `MainActivity` (A5b)

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 74 - "runSuspending"
Cohesion: 0.19
Nodes (3): RoomAuditedWriterIntegrationTest, runSuspending(), 3.3 Dependencies (one pins-only PR, A5c)

### Community 76 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.15
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 77 - "7. Test plan"
Cohesion: 0.13
Nodes (10): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 1. Requirement, 3.4 Header glow (UI-13, DS6d) (+2 more)

### Community 79 - "HeaderIconButtonTest.kt"
Cohesion: 0.24
Nodes (3): HeaderIconButton(), HeaderIconButtonTest, 3.5 Icon-only header actions (UI-14, DS6e; needs DS3)

### Community 80 - "PeriodStepper"
Cohesion: 0.38
Nodes (10): CollapsingHeader(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderTitle(), LowerBand(), PeriodStepper() (+2 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.14
Nodes (14): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+6 more)

### Community 82 - "UserProfileEntity"
Cohesion: 0.17
Nodes (3): IdentityDao, DeviceEntity, UserProfileEntity

### Community 84 - "SetupViewModel.kt"
Cohesion: 0.11
Nodes (6): `ui/nav` and `ui/home` (A5f), HomeUiState, HomeViewModel, Routes, SetupUiState, SetupViewModel

### Community 85 - "MigrationTestSupport"
Cohesion: 0.08
Nodes (5): AuditTriggers, MigrationTestSupport, MigrationTest, The audit trail, Audit triggers

### Community 87 - "PreMigrationBackupTest"
Cohesion: 0.06
Nodes (4): PreMigrationBackup, PreMigrationBackupTest, DbKeyManagerWiringTest, Pre-migration backup (INT-5)

### Community 91 - "Paise"
Cohesion: 0.11
Nodes (10): Configuration, `:core:money` — integer-paise money, Data it owns, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose, Responsibilities & boundaries (+2 more)

### Community 94 - "PftTheme"
Cohesion: 0.13
Nodes (4): DesignCatalog(), PftTheme(), DesignCatalogTest, Step 11 — `NameField` and `EditFormScaffold` (A5d)

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 98 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.17
Nodes (11): IdentityServiceTest, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5. Flows (+3 more)

### Community 102 - "LoggingScanTest"
Cohesion: 0.27
Nodes (4): LoggingScanTest, SourceScan, Violation, Step 3 — `SafeLog` writes the line, and nothing else can log (A5a)

### Community 104 - "PaletteSamples.kt"
Cohesion: 0.25
Nodes (5): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch(), PftShapes

### Community 105 - "AuditFieldsCheck.kt"
Cohesion: 0.13
Nodes (9): AuditFieldsCheck, haveCompleteAuditFields(), 3.1 The model: every transaction has one or more lines, 3.2 Lines are ledger entities, written only by `TransactionService`, 3.4 Reads, 3.6 Configuration (CLAUDE.md §3.5), 3.7 Future items recorded, not built, 3.8 Abstraction bar (+1 more)

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 111 - "SetupScreen.kt"
Cohesion: 0.07
Nodes (11): errorMessage(), NameField(), HomeRoute(), HomeScreen(), SetupRoute(), SetupScreen(), DataUnavailableCause, IdentityCorrupt (+3 more)

### Community 112 - "InMemoryKekSource"
Cohesion: 0.13
Nodes (3): AndroidKeystoreKek, KekSource, InMemoryKekSource

### Community 113 - "CatalogSection"
Cohesion: 0.14
Nodes (14): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+6 more)

### Community 114 - "DataUnavailableScreenTest"
Cohesion: 0.15
Nodes (8): Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING, DataUnavailableScreenTest

### Community 115 - "HostedComposeRule"
Cohesion: 0.22
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 117 - "AppContainer"
Cohesion: 0.08
Nodes (10): AppContainer, PftRoot(), RoomTestKit, LaunchRoutingTest, PftRootTest, ReleaseHasNoCatalogueTest, Call flow example: first-run Setup, Startup (+2 more)

### Community 118 - "FieldState"
Cohesion: 0.26
Nodes (3): FieldCheck, FieldState, FieldStateTest

### Community 119 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.14
Nodes (16): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID) (+8 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.19
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "shouldbe"
Cohesion: 0.06
Nodes (25): AuditEntryMapper, AuditEntryMapperTest, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE (+17 more)

### Community 129 - "7. Test plan"
Cohesion: 0.32
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 135 - "Persistence and security"
Cohesion: 0.29
Nodes (7): Corruption handling, Persistence and security, SQLCipher, The key and the KEK, The key array and SQLCipher (a trap), The pieces, Wrapped-key file format, version 1

### Community 136 - "ManifestHardeningTest"
Cohesion: 0.24
Nodes (3): Manifest security posture (SEC-2, SEC-3, SEC-6), Responsibilities and boundaries, ManifestHardeningTest

### Community 137 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.29
Nodes (7): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice I — Cashew import (`feat/m1a-cashew-import`): tracking stub

### Community 138 - "How to run and test"
Cohesion: 0.09
Nodes (7): How to run and test, RoomAuditLog, PftDatabase, TestDatabase, HomeViewModelTest, SetupRouteTest, 2. Current state

### Community 139 - "MetaMapperTest"
Cohesion: 0.13
Nodes (7): MetaColumns, MetaMapperTest, NameBasedId, Conventions, Data model, Version 1 (A4a), Version table

### Community 140 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 141 - "AppSetting"
Cohesion: 0.23
Nodes (6): RoomAppSettingsRepository, AppSetting, AppSettingsRepository, SettingKeys, InMemoryAppSettingsRepository, The core/app boundary

### Community 142 - "PaiseParserTest.kt"
Cohesion: 0.17
Nodes (4): How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest

### Community 147 - "3. Design & decision"
Cohesion: 0.33
Nodes (6): 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins"), 3. Design & decision

### Community 148 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.29
Nodes (6): Emoji, GlyphSource, isSymbolKey(), Letter, Symbol, 6.1 Design-system slice: PRs into `feat/m1a-foundation`

### Community 152 - "CategoryGlyph.kt"
Cohesion: 0.17
Nodes (4): fixedSp(), GlyphSize, Chip, Hero

### Community 155 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 157 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 163 - "`ui/common` (shared components, DS3, DS4, DS5 and A5d)"
Cohesion: 0.18
Nodes (8): Dependencies, `ui/common` (shared components, DS3, DS4, DS5 and A5d), TooLong, toFieldError(), Row, Dependencies, PaiseFormat, 2. Current state

### Community 164 - "Appearance: colour palettes, dynamic colour, background and type"
Cohesion: 0.18
Nodes (5): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you

### Community 168 - "Personal Finance Tracker PRD"
Cohesion: 0.22
Nodes (11): Personal Finance Tracker PRD, Audit trail (INT-2), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Milestone 3 (insights), Non-functional requirements, Security, privacy and integrity requirements (SEC/INT) (+3 more)

### Community 171 - "Data protection: encrypted storage and the "can't open your data" screen"
Cohesion: 0.22
Nodes (7): Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen, What it does for you, Behaviour details, First run and profiles, What it does for you

### Community 172 - "CategoryPalette"
Cohesion: 0.43
Nodes (3): CategoryColors, CategoryPalette, pair()

### Community 175 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 176 - "Icons and emoji (DS3, UI-6)"
Cohesion: 0.50
Nodes (4): Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 177 - "HeaderStyle"
Cohesion: 0.67
Nodes (3): HeaderStyle, Dashboard, Standard

## Knowledge Gaps
- **198 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+193 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 616 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **84 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `ThemePrefs`, `MoneyTextTest`, `ColorContrastTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `SampleThing.kt`, `Slice A — Foundation (`feat/m1a-foundation`)`, `AppServicesTest`, `6.1 Slices`, `DbKeyManagerJvmTest`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `PftShapesTest.kt`, `GlyphSourceTest`, `LaunchViewModelTest.kt`, `AppContainerTest.kt`, `CategoryGlyphScreenshotTest`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `MainActivity`, `PftThemeModeTest`, `RoomAuditColumnsTest`, `ContentScreenshotTest`, `DbKeyManagerTest.kt`, `MoneyText`, `UserId`, `SetupViewModelTest`, `NavTransitionTest.kt`, `SetupFlowTest.kt`, `MoneyColors`, `MainActivityOpeningTest.kt`, `runSuspending`, `WindowBackgroundTest.kt`, `Previews, the token sheet and screenshot goldens`, `PftSymbolTest`, `MigrationTestSupport`, `AuditTriggersTest`, `PreMigrationBackupTest`, `getvalue`, `PftSurfacesTest`, `text`, `GroupedSectionTest.kt`, `PftTheme`, `DevRouteTest.kt`, `DatabaseStartupTest.kt`, `MoneyToneTest`, `LoggingScanTest`, `RoomTransactionRunnerTest`, `BundledFontTest`, `BottomActionBarTest.kt`, `DataUnavailableScreenTest`, `LogEventShapeTest`, `AppContainer`, `FieldState`, `RoomEntityRepositoryTest.kt`, `shouldbe`, `WindowThemeTest`, `FormScreenshotTest.kt`, `IdentityRoomIntegrationTest`, `PftSpacingTest`, `PaletteScreenshotTest.kt`, `7. Test plan`, `NameFieldTest.kt`, `expandedHeaderHeight`, `EmptyState`, `localdensity`, `ManifestHardeningTest`, `MetaMapperTest`, `SafeLogTest`, `LogEventTest`, `ThemePrefsTest`, `SetupScreenTest`, `AuditDaoTest.kt`, `RoomAppSettingsRepositoryTest.kt`, `EditFormScaffoldTest`, ``ui/common` (shared components, DS3, DS4, DS5 and A5d)`, `HomeScreenTest.kt`, `applicationprovider`, `.check`, `CategoryPaletteTest`?**
  _High betweenness centrality (0.269) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _198 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `PaletteScreenshotTest.kt`, `NameFieldTest.kt`, `EmptyState`, `localdensity`, `MotionSection.kt`, `ThemePrefs`, `MoneyTextTest`, `How to run and test`, `BottomActionBar.kt`, `6.3 Amendments to M1a steps (consumption; this plan edits M1a when approved)`, `MoneyTextTest.kt`, `3. Design & decision`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `CategoryGlyph.kt`, `setvalue`, `DesignCatalog.kt`, `CollapsingHeaderScaffoldTest`, `SetupScreenTest.kt`, `Slice A — Foundation (`feat/m1a-foundation`)`, `SetupScreenTest`, `CollapsingHeaderScaffold.kt`, `6.1 Slices`, `EditFormScaffoldTest`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `HomeScreenTest.kt`, `PftShapesTest.kt`, `PaletteSwatchesTest.kt`, `Project README`, `CategoryPalette`, `PftMotion.kt`, `13. Deviations from the plan, by slice`, `HeaderTypePairingTest.kt`, `CategoryGlyphScreenshotTest`, `MainActivity`, `PftThemeModeTest`, `ContentScreenshotTest`, `MoneyText`, `PftTypography.kt`, `GroupedSection.kt`, `SetupFlowTest.kt`, `MoneyColors`, `WindowBackgroundTest.kt`, `Previews, the token sheet and screenshot goldens`, `7. Test plan`, `HeaderIconButtonTest.kt`, `Plan: M1a design system and visual direction (lands before A5)`, `getvalue`, `Paise`, `text`, `GroupedSectionTest.kt`, `DevRouteTest.kt`, `PaletteSamples.kt`, `BottomActionBarTest.kt`, `SetupScreen.kt`, `DataUnavailableScreenTest`, `AppContainer`, `FormScreenshotTest.kt`, `PftSpacingTest`?**
  _High betweenness centrality (0.103) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._