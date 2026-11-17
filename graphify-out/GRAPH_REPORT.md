# Graph Report - personal-finance-tracker  (2026-11-17)

## Corpus Check
- 316 files · ~255,942 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3881 nodes · 11466 edges · 166 communities (92 shown, 74 thin omitted)
- Extraction: 82% EXTRACTED · 18% INFERRED · 0% AMBIGUOUS · INFERRED: 2034 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `43a75c23`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- AuditedWriterTest.kt
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- Slice A — Foundation (`feat/m1a-foundation`)
- hooks
- RedactTests
- DesignCatalog.kt
- ThemePrefs
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- 6.3 Amendments to M1a steps (consumption; this plan edits M1a when approved)
- Catalog.kt
- detekt.yml
- MainActivity
- PftSymbol
- CollapsingHeaderScaffoldTest
- SampleThing.kt
- AppServices
- How to run and test
- CollapsingHeaderScaffold.kt
- SetupViewModel
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- HeaderGlowScaffoldTest
- PftTypographyTest
- HeaderIconButton
- PftShapesTest.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- AppContainerTest.kt
- Project README
- Plan: Itemised transactions (one payment, many categories)
- PftMotion.kt
- HeaderTypePairingTest
- CLAUDE.md Development Constitution
- test_guard.py
- HeaderGlowScaffoldTest.kt
- AuditEntry
- PftThemeModeTest
- RoomAuditColumnsTest
- EntityMeta
- ContentScreenshotTest
- DbKeyManagerTest.kt
- MoneyText
- Personal Finance Tracker PRD
- UserId
- WindowThemeTest
- awaitMain
- NavTransitionTest
- 6.1 Slices
- Pseudo
- `ui/common` (shared components, DS3, DS4, DS5 and A5d)
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- SetupFlowTest.kt
- 13. Deviations from the plan, by slice
- PftDatabaseTest
- _Text
- MainActivityOpeningTest.kt
- import_symbols.py
- runSuspending
- WindowBackgroundTest.kt
- Previews, the token sheet and screenshot goldens
- 7. Test plan
- PftSymbolTest
- `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)
- AuditTriggersTest
- Plan: M1a design system and visual direction (lands before A5)
- AuditEntryEntity
- EditFormScaffoldTest
- NameFieldTest
- MigrationTestSupport
- .format
- PreMigrationBackupTest
- TabularFiguresTest
- TokenSheetScreenshotTest.kt
- ThemePaletteTest
- HeaderPolicy
- Plan: M1a — Core ledger (manual-entry household tracker)
- GroupedSectionTest
- PftTheme
- DbKeyManager
- LoggingScanTest
- StatusChip
- BashRuleTest
- DatabaseStartupTest.kt
- Paise
- Learnings digest
- LaunchRoutingTest.kt
- DevRouteTest.kt
- Icons and emoji (DS3, UI-6)
- HostedComposeRule
- parse_svg
- AppSettingEntity
- BundledFontTest
- NameField
- ImportRunTest
- SetupScreen.kt
- AndroidKeystoreKek.kt
- Plan: M1a premium look (DS6): curated palettes, tonal surfaces, header glow, icon-only header actions, type pairing, bundled sans
- DataUnavailableScreenTest
- PaletteSwatchesTest.kt
- LogEvent
- AppContainer
- FieldState
- Loans and EMIs (LN-1..LN-5)
- PreMigrationBackup
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- ReleaseHasNoCatalogueTest.kt
- decide
- FormScreenshotTest.kt
- DbKeyManagerWiringTest.kt
- PaletteScreenshotTest.kt
- PftTheme.kt
- BottomActionBarSurfaceTest.kt
- PaiseParser
- expandedHeaderHeight
- EmptyState
- tools/agent
- StatusChipTest.kt
- ManifestHardeningTest
- 6. Implementation steps (TDD-ordered)
- CategoryPaletteTest
- .of
- HeaderSlot
- AppSetting
- Collecting and Redacting Sample Messages Guide
- CollapsingHeaderScaffold
- FontBudgetTest
- MoneyTextTest.kt
- 3.7 Component inventory (UI-9)
- Security, privacy and integrity requirements (SEC/INT)
- DatabaseFactory
- HomeScreenTest.kt
- LogEventTest
- PaletteSamples.kt
- ThemePrefsTest
- Agent usage budget and model routing
- ThemeLiteralScanTest
- Immutable Audit Trail
- orchestrate-pr.md
- Plan: M1a slice B — Accounts, categories and tags
- TokenSheet
- Architecture overview
- MoneySize
- check-retry.sh
- check-commits.sh

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 190 edges
2. `ThemePrefs` - 159 edges
3. `PftSymbol` - 128 edges
4. `How to run and test` - 101 edges
5. `Paise` - 96 edges
6. `HostedComposeRule` - 95 edges
7. `CollapsingHeaderScaffoldTest` - 83 edges
8. `runSuspending()` - 74 edges
9. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 71 edges
10. `PftDatabase` - 59 edges

## Surprising Connections (you probably didn't know these)
- `3.5 Fonts: bundling, licensing, custom import` --references--> `SafeLog`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/log/SafeLog.kt
- `PR breakdown (incremental — required)` --references--> `GlyphSource`  [INFERRED]
  docs/plans/2026-11-10-m1a-slice-b-ledger-setup.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/GlyphSource.kt
- `13. Deviations from the plan, by slice` --references--> `asHeroTop()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderGlow.kt
- `10. Risks & mitigations` --references--> `MoneyText()`  [INFERRED]
  docs/plans/2026-11-10-itemised-transactions.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/MoneyText.kt
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

## Communities (166 total, 74 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.12
Nodes (16): _day(), _email_record(), html_to_text(), main(), mask_run(), process(), read_adb(), read_any() (+8 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "AuditedWriterTest.kt"
Cohesion: 0.14
Nodes (7): AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.04
Nodes (28): EpochMillis, RoomTransactionRunner, TestDatabase, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose (+20 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "DesignCatalog.kt"
Cohesion: 0.06
Nodes (8): AccentCheckSection(), MotionSection(), SpringTrack(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), GroupedSectionColors, GroupedSectionTags

### Community 9 - "ThemePrefs"
Cohesion: 0.17
Nodes (4): resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.09
Nodes (16): AccentHueTest, Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone() (+8 more)

### Community 16 - "6.3 Amendments to M1a steps (consumption; this plan edits M1a when approved)"
Cohesion: 0.17
Nodes (18): Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense, Income (+10 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "MainActivity"
Cohesion: 0.11
Nodes (16): `:app` — Android application, Configuration, Data it owns, Debug and release source sets, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Responsibilities and boundaries, Window theme (+8 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 29 - "SampleThing.kt"
Cohesion: 0.07
Nodes (13): BaseDao, EntityMapper, RoomEntityRepository, RoomEntityRepositoryTest, SampleDatabase, SampleId, SampleThing, SampleThingDao (+5 more)

### Community 30 - "AppServices"
Cohesion: 0.09
Nodes (36): `di` and startup (A5b), Purpose, AppServices, AppStart, KeyUnavailable, SafeLog, IdentityCorrupt, KeyUnavailable (+28 more)

### Community 31 - "How to run and test"
Cohesion: 0.08
Nodes (5): How to run and test, PftDatabase, AppServicesTest, HomeViewModelTest, SetupRouteTest

### Community 32 - "CollapsingHeaderScaffold.kt"
Cohesion: 0.08
Nodes (17): CollapsingHeader(), HeaderChildren(), HeaderContent, HeaderPeriod, HeaderPhase, Collapsed, Expanded, Moving (+9 more)

### Community 33 - "SetupViewModel"
Cohesion: 0.11
Nodes (6): `ui/setup` and the UiState convention (A5e), HomeUiState, HomeViewModel, SetupUiState, SetupViewModel, Step 12 — `SetupViewModel` (A5e)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.23
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 37 - "PftTypographyTest"
Cohesion: 0.12
Nodes (3): sansFamily(), PftTypographyTest, Resolved

### Community 40 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.07
Nodes (15): CategoryGlyph(), CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), Emoji, GlyphSource, isEmojiValue() (+7 more)

### Community 42 - "Project README"
Cohesion: 0.22
Nodes (6): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Code Structure Map, core/shared Shared Packages, Project README, Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "Plan: Itemised transactions (one payment, many categories)"
Cohesion: 0.08
Nodes (24): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 3.1 The model: every transaction has one or more lines, 3.4 Reads, 3.5 UI, 3.6 Configuration (CLAUDE.md §3.5) (+16 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.07
Nodes (6): iosSpring(), PftMotion, BottomActionBarTest, PftMotionTest, 7. Test plan, 11. Rollout & verification

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "test_guard.py"
Cohesion: 0.15
Nodes (4): _foreground_gradle_tests(), main(), _runs_tests(), _whole_read_of_big_file()

### Community 51 - "AuditEntry"
Cohesion: 0.06
Nodes (22): AuditEntryMapper, RoomAuditLog, AuditDaoTest, AuditEntryMapperTest, FieldChange, AuditAction, ARCHIVE, CREATE (+14 more)

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.10
Nodes (13): Probe, RoomAuditColumnsTest, AuditColumnsCheck, 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Step B2c — `PftBottomSheet` and `AccountPicker`, Step B3b — Categories Room, v3, seeding wired into Setup, Step B4a — Categories list (+5 more)

### Community 54 - "EntityMeta"
Cohesion: 0.06
Nodes (26): `data` (Room persistence, A4a), MetaColumns, MetaMapper, MetaMapperTest, EntityMeta, LedgerEntity, AuditFieldsCheckTest, Complete (+18 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.21
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "MoneyText"
Cohesion: 0.18
Nodes (11): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), moneySign(), MoneyText() (+3 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.11
Nodes (22): Personal Finance Tracker PRD, Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope, Idempotent ingestion (X-2) (+14 more)

### Community 59 - "UserId"
Cohesion: 0.09
Nodes (13): RoomIdentityRepository, RoomIdentityRepositoryTest, AuditedTable, AuditedTables, Auditable, Device, IdentityRepository, UserProfile (+5 more)

### Community 61 - "awaitMain"
Cohesion: 0.15
Nodes (3): awaitMain(), FailsFirstAppend, SetupViewModelTest

### Community 63 - "6.1 Slices"
Cohesion: 0.10
Nodes (23): PftSurfaces, ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, paletteFor(), SansFont (+15 more)

### Community 65 - "`ui/common` (shared components, DS3, DS4, DS5 and A5d)"
Cohesion: 0.09
Nodes (21): `ui/common` (shared components, DS3, DS4, DS5 and A5d), FieldError, Required, TooLong, GroupedRow, GroupedSection(), GroupedSectionDefaults, GroupedSectionScope (+13 more)

### Community 66 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.06
Nodes (57): Behaviours worth knowing, Configuration, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), AuditDiff, AuditedWriter (+49 more)

### Community 68 - "13. Deviations from the plan, by slice"
Cohesion: 0.14
Nodes (14): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansWeights(), DisplayFont, BUNDLED_SERIF (+6 more)

### Community 72 - "MainActivityOpeningTest.kt"
Cohesion: 0.08
Nodes (9): `security` and the encrypted open (A4b), DatabaseStartup, DbKeyUnavailable, DataUnavailableCause, IdentityCorrupt, KeyUnavailable, MainActivityOpeningTest, SlowOpeningApplication (+1 more)

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), DrawableTest, ManifestTest

### Community 74 - "runSuspending"
Cohesion: 0.05
Nodes (9): FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAppSettingsRepositoryTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomTransactionRunnerTest, AppContainerTest, runSuspending() (+1 more)

### Community 75 - "WindowBackgroundTest.kt"
Cohesion: 0.16
Nodes (3): SyncWindowBackground(), WindowBackgroundTest, 8. Security considerations

### Community 76 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.07
Nodes (24): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+16 more)

### Community 77 - "7. Test plan"
Cohesion: 0.14
Nodes (10): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 1. Requirement, 3.4 Header glow (UI-13, DS6d) (+2 more)

### Community 79 - "`ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)"
Cohesion: 0.13
Nodes (9): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, MidnightPalette, MoneyColors, colors(), ChipColorsTest (+1 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.17
Nodes (12): 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2), 5.5 Mockups (wireframes; the styled version is in the HTML file) (+4 more)

### Community 82 - "AuditEntryEntity"
Cohesion: 0.11
Nodes (5): AuditDao, AuditEntryEntity, IdentityDao, DeviceEntity, UserProfileEntity

### Community 85 - "MigrationTestSupport"
Cohesion: 0.07
Nodes (6): AuditTriggers, MigrationTestSupport, RegisterHostActivity, Statement, MigrationTest, Audit triggers

### Community 86 - ".format"
Cohesion: 0.15
Nodes (12): Dependencies, Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface (+4 more)

### Community 92 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.07
Nodes (21): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you, Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen (+13 more)

### Community 94 - "PftTheme"
Cohesion: 0.16
Nodes (5): ChoiceRow(), DesignCatalog(), PftTheme(), DesignCatalogTest, Step 11 — `NameField` and `EditFormScaffold` (A5d)

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 96 - "LoggingScanTest"
Cohesion: 0.27
Nodes (4): LoggingScanTest, SourceScan, Violation, Step 3 — `SafeLog` writes the line, and nothing else can log (A5a)

### Community 97 - "StatusChip"
Cohesion: 0.17
Nodes (4): ChipColors, StatusChip(), StatusChipPreview(), StatusChipSamples()

### Community 98 - "BashRuleTest"
Cohesion: 0.24
Nodes (3): bash(), BashRuleTest, MainTest

### Community 99 - "DatabaseStartupTest.kt"
Cohesion: 0.20
Nodes (3): KeyUnavailable, Ready, DatabaseStartupTest

### Community 101 - "Learnings digest"
Cohesion: 0.20
Nodes (8): Migrations, APK size and release shrink, Build, Gradle, detekt and ktlint, Docs and process, Git, commits and worktrees, Goldens (Roborazzi), Learnings digest, Test quality (prove tests bite)

### Community 104 - "Icons and emoji (DS3, UI-6)"
Cohesion: 0.27
Nodes (7): CategoryColors, CategoryPalette, pair(), Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 106 - "parse_svg"
Cohesion: 0.23
Nodes (4): parse_svg(), UnsafeSvg, ParseSvgTest, svg()

### Community 108 - "BundledFontTest"
Cohesion: 0.12
Nodes (3): BundledFontTest, BundledSymbolsLicenceTest, Third-party assets

### Community 109 - "NameField"
Cohesion: 0.23
Nodes (5): EditFormPreview(), EditFormSample(), errorMessage(), NameField(), 3.3 Abstraction check

### Community 111 - "SetupScreen.kt"
Cohesion: 0.13
Nodes (9): `ui/nav` and `ui/home` (A5f), HomeRoute(), HomeScreen(), Routes, SetupRoute(), SetupScreen(), Where it lives, 5. Flows (+1 more)

### Community 113 - "Plan: M1a premium look (DS6): curated palettes, tonal surfaces, header glow, icon-only header actions, type pairing, bundled sans"
Cohesion: 0.18
Nodes (12): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 3.1 Palettes (DS6a, DS6b), 3.2 Surfaces: tonal or flat (DS6c), 3.5 Icon-only header actions (UI-14, DS6e; needs DS3), 3.6 Type pairing and optical size per role (UI-15, DS6g), 3.7 Bundled sans fonts (UI-16, UI-17; DS6f spike done, then DS6h, DS6i) (+4 more)

### Community 114 - "DataUnavailableScreenTest"
Cohesion: 0.14
Nodes (8): Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING, DataUnavailableScreenTest

### Community 116 - "LogEvent"
Cohesion: 0.08
Nodes (19): `log` (safe logging, A5a), DbKeyUnavailable, DbOpened, IdentityCorrupt, LogEvent, LogValue, render(), SetupCompleted (+11 more)

### Community 117 - "AppContainer"
Cohesion: 0.23
Nodes (4): AppContainer, PftRootTest, 7. Test plan, Step 14 — `PftNavHost`, Home placeholder, debug route, predictive back (A5f)

### Community 118 - "FieldState"
Cohesion: 0.05
Nodes (9): FieldCheck, FieldState, FieldStateTest, Case, GroupedSectionColorsTest, SetupScreenshotTest, SetupScreenTest, PftSurfacesTest (+1 more)

### Community 119 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5), M1a Core ledger (+2 more)

### Community 120 - "PreMigrationBackup"
Cohesion: 0.14
Nodes (14): PreMigrationBackup, PreMigrationBackupFailed, factory(), Corruption handling, Persistence and security, Pre-migration backup (INT-5), SQLCipher, The key and the KEK (+6 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.10
Nodes (13): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+5 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.19
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), open(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 124 - "decide"
Cohesion: 0.26
Nodes (4): decide(), OtherInputTest, read(), ReadRuleTest

### Community 130 - "BottomActionBarSurfaceTest.kt"
Cohesion: 0.16
Nodes (5): IvoryPalette, BackgroundStyle, FLAT, TONAL, BottomActionBarSurfaceTest

### Community 134 - "tools/agent"
Cohesion: 0.22
Nodes (8): Configuration, Data it owns, Dependencies, How to run & test, Public interface, Purpose, Responsibilities & boundaries, tools/agent

### Community 137 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.29
Nodes (7): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice I — Cashew import (`feat/m1a-cashew-import`): tracking stub

### Community 139 - ".of"
Cohesion: 0.12
Nodes (7): EpochMillisTest, NameBasedId, Conventions, Data model, The audit trail, Version 1 (A4a), Version table

### Community 140 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 141 - "AppSetting"
Cohesion: 0.29
Nodes (4): RoomAppSettingsRepository, AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository

### Community 142 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 143 - "CollapsingHeaderScaffold"
Cohesion: 0.12
Nodes (15): BottomActionBar(), BottomBarAction, CollapsingHeaderScaffold(), EditFormScaffold(), Adding a token, Build-time trust decisions, Design system, Motion (UI-10) (+7 more)

### Community 146 - "MoneyTextTest.kt"
Cohesion: 0.13
Nodes (6): layoutResult(), shownText(), assertShownInFull(), isShownInFull(), layout(), Testing: Robolectric and Compose

### Community 147 - "3.7 Component inventory (UI-9)"
Cohesion: 0.16
Nodes (11): pressScale(), PftSpacing, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens, 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6) (+3 more)

### Community 148 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 150 - "HomeScreenTest.kt"
Cohesion: 0.14
Nodes (4): DevHeaderActions(), devRoutes(), DevHeaderActions(), HomeScreenTest

### Community 152 - "PaletteSamples.kt"
Cohesion: 0.10
Nodes (8): fixedSp(), GlyphSize, Chip, Hero, PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 154 - "Agent usage budget and model routing"
Cohesion: 0.25
Nodes (7): 1. Requirement, 2. Findings (where the usage went), 3. Design, 4. Steps (TDD-ordered), 5. Verification, 6. PR breakdown, Agent usage budget and model routing

### Community 158 - "orchestrate-pr.md"
Cohesion: 0.29
Nodes (6): 1. Orient (cheaply), 2. Delegate the build, 3. Review it yourself (unless the tier says Opus `code-reviewer`), 4. Verify, then ask, 5. On the owner's go-ahead, After the owner merges (start of the next session, or when told)

### Community 159 - "Plan: M1a slice B — Accounts, categories and tags"
Cohesion: 0.29
Nodes (7): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 3. Design & decision, 8. Security considerations, Plan: M1a slice B — Accounts, categories and tags

### Community 161 - "Architecture overview"
Cohesion: 0.33
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 162 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

## Knowledge Gaps
- **199 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+194 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 658 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **74 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `PaletteScreenshotTest.kt`, `BottomActionBarSurfaceTest.kt`, `expandedHeaderHeight`, `EmptyState`, `StatusChipTest.kt`, `ManifestHardeningTest`, `ThemePrefs`, `MoneyTextTest`, `CategoryPaletteTest`, `ColorContrastTest`, `HomeScreenTest.kt`, `LogEventTest`, `ThemePrefsTest`, `MainActivity`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `SampleThing.kt`, `AppServices`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `PftShapesTest.kt`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `AppContainerTest.kt`, `PftMotion.kt`, `HeaderGlowScaffoldTest.kt`, `AuditEntry`, `PftThemeModeTest`, `RoomAuditColumnsTest`, `EntityMeta`, `ContentScreenshotTest`, `DbKeyManagerTest.kt`, `MoneyText`, `UserId`, `WindowThemeTest`, `awaitMain`, `NavTransitionTest`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `SetupFlowTest.kt`, `MainActivityOpeningTest.kt`, `runSuspending`, `WindowBackgroundTest.kt`, `Previews, the token sheet and screenshot goldens`, `PftSymbolTest`, ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`, `AuditTriggersTest`, `EditFormScaffoldTest`, `NameFieldTest`, `MigrationTestSupport`, `.format`, `PreMigrationBackupTest`, `TokenSheetScreenshotTest.kt`, `ThemePaletteTest`, `GroupedSectionTest`, `PftTheme`, `LoggingScanTest`, `DatabaseStartupTest.kt`, `Paise`, `LaunchRoutingTest.kt`, `DevRouteTest.kt`, `BundledFontTest`, `DataUnavailableScreenTest`, `LogEvent`, `AppContainer`, `FieldState`, `ReleaseHasNoCatalogueTest.kt`, `FormScreenshotTest.kt`, `DbKeyManagerWiringTest.kt`?**
  _High betweenness centrality (0.234) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _199 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.11612903225806452 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `PaletteScreenshotTest.kt`, `PftTheme.kt`, `BottomActionBarSurfaceTest.kt`, `EmptyState`, `StatusChipTest.kt`, `DesignCatalog.kt`, `ThemePrefs`, `MoneyTextTest`, `CollapsingHeaderScaffold`, `6.3 Amendments to M1a steps (consumption; this plan edits M1a when approved)`, `.assertIconOnlyActions`, `MoneyTextTest.kt`, `3.7 Component inventory (UI-9)`, `HomeScreenTest.kt`, `PaletteSamples.kt`, `MainActivity`, `CollapsingHeaderScaffoldTest`, `AppServices`, `How to run and test`, `CollapsingHeaderScaffold.kt`, `TokenSheet`, `Architecture overview`, `HeaderGlowScaffoldTest`, `PftTypographyTest`, `HeaderIconButton`, `PftShapesTest.kt`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `PftMotion.kt`, `HeaderTypePairingTest`, `HeaderGlowScaffoldTest.kt`, `PftThemeModeTest`, `ContentScreenshotTest`, `MoneyText`, `6.1 Slices`, ``ui/common` (shared components, DS3, DS4, DS5 and A5d)`, `SetupFlowTest.kt`, `13. Deviations from the plan, by slice`, `WindowBackgroundTest.kt`, `Previews, the token sheet and screenshot goldens`, `7. Test plan`, ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`, `Plan: M1a design system and visual direction (lands before A5)`, `EditFormScaffoldTest`, `NameFieldTest`, `TokenSheetScreenshotTest.kt`, `GroupedSectionTest`, `StatusChip`, `LaunchRoutingTest.kt`, `DevRouteTest.kt`, `Icons and emoji (DS3, UI-6)`, `HostedComposeRule`, `NameField`, `SetupScreen.kt`, `DataUnavailableScreenTest`, `PaletteSwatchesTest.kt`, `AppContainer`, `FieldState`, `ReleaseHasNoCatalogueTest.kt`, `FormScreenshotTest.kt`?**
  _High betweenness centrality (0.086) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._