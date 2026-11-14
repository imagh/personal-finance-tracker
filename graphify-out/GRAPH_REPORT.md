# Graph Report - personal-finance-tracker  (2026-11-14)

## Corpus Check
- 318 files · ~241,186 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3833 nodes · 11277 edges · 150 communities (85 shown, 65 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 1810 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `275a833e`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- LogEvent
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- How to run and test
- hooks
- test_import_symbols.py
- AuditEntryMapperTest
- ThemePrefs
- Paise
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
- AppContainer
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
- Public interface
- PftMotion.kt
- HeaderTypePairingTest.kt
- PreMigrationBackup
- CLAUDE.md Development Constitution
- LaunchRoutingTest.kt
- CategoryGlyphScreenshotTest
- AuditedWriter
- CollapsingHeaderScaffold
- getvalue
- RoomAuditColumnsTest
- EntityMeta
- ContentScreenshotTest
- DbKeyManagerTest
- MoneyText
- Personal Finance Tracker PRD
- UserId
- redact_text
- SetupViewModelTest
- ThemePaletteTest
- 13. Deviations from the plan, by slice
- Pseudo
- GroupedSection.kt
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- SetupFlowTest.kt
- MoneyColors
- PftDatabaseTest
- _Text
- How to run and test
- import_symbols.py
- runSuspending
- WindowBackgroundTest.kt
- Previews, the token sheet and screenshot goldens
- 7. Test plan
- PftSymbolTest
- ReleaseHasNoCatalogueTest.kt
- Plan: M1a design system and visual direction (lands before A5)
- AuditEntryEntity
- InMemoryKekSource
- SetupViewModel.kt
- MigrationTestSupport
- AuditTriggersTest
- PreMigrationBackupTest
- TabularFiguresTest
- TokenSheetScreenshotTest.kt
- PftSurfacesTest
- Collecting and Redacting Sample Messages Guide
- StatusChipTest.kt
- GroupedSectionTest.kt
- PftTheme
- DbKeyManager
- Architecture overview
- Plan: M1a — Core ledger (manual-entry household tracker)
- DatabaseStartupTest.kt
- MoneyToneTest
- Migrations.kt
- AppSettingEntity
- MotionSection.kt
- AuditFieldsCheck.kt
- parse_svg
- BundledFontTest
- BottomActionBarTest.kt
- ImportRunTest
- SetupScreen.kt
- AndroidKeystoreKek
- CatalogSection
- DataUnavailableScreenTest
- HostedComposeRule
- LogEventShapeTest
- PftRootTest.kt
- FieldState
- Loans and EMIs (LN-1..LN-5)
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- shouldbe
- WindowThemeTest
- FormScreenshotTest.kt
- modifier
- PaiseParser
- EmptyStateTest.kt
- ManifestHardeningTest
- SetupRouteTest.kt
- .of
- TestClock
- BottomActionBar.kt
- FontBudgetTest
- SafeLogTest
- MoneyTextTest.kt
- .fromIconKey
- DatabaseFactory
- LogEventTest
- ThemePrefsTest
- SetupScreenTest.kt
- EditFormScaffoldTest.kt
- Appearance: colour palettes, dynamic colour, background and type
- HomeScreenTest.kt
- BundledSymbolsLicenceTest
- Security, privacy and integrity requirements (SEC/INT)
- assertisdisplayed
- CategoryPalette
- CategoryPaletteTest
- Icons and emoji (DS3, UI-6)
- Immutable Audit Trail

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 192 edges
2. `ThemePrefs` - 161 edges
3. `PftSymbol` - 125 edges
4. `Paise` - 102 edges
5. `How to run and test` - 102 edges
6. `HostedComposeRule` - 94 edges
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

## Communities (150 total, 65 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "LogEvent"
Cohesion: 0.08
Nodes (19): `log` (safe logging, A5a), `security` and the encrypted open (A4b), DatabaseStartup, Ready, PreMigrationBackupFailed, DbKeyUnavailable, DbOpened, IdentityCorrupt (+11 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "How to run and test"
Cohesion: 0.10
Nodes (11): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, AuditDiffTest, Thing, AuditedWriterTest (+3 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 9 - "ThemePrefs"
Cohesion: 0.11
Nodes (5): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest

### Community 10 - "Paise"
Cohesion: 0.14
Nodes (3): MoneyTextTest, Spec, Paise

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.07
Nodes (18): AccentHueTest, Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone() (+10 more)

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.16
Nodes (23): moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense (+15 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "DesignCatalog.kt"
Cohesion: 0.05
Nodes (10): Debug and release source sets, AccentCheckSection(), ChoiceRow(), SectionContent(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), GroupedSectionPreview() (+2 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.08
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "SampleThing.kt"
Cohesion: 0.08
Nodes (11): BaseDao, EntityMapper, RoomEntityRepository, RoomEntityRepositoryTest, SampleDatabase, SampleId, SampleThing, SampleThingDao (+3 more)

### Community 30 - "AppContainer"
Cohesion: 0.08
Nodes (51): `di` and startup (A5b), Purpose, AppContainer, AppServices, AppStart, KeyUnavailable, Ready, SafeLog (+43 more)

### Community 31 - "AppServicesTest"
Cohesion: 0.16
Nodes (3): Data it owns, AppServicesTest, HomeViewModelTest

### Community 32 - "CollapsingHeaderScaffold.kt"
Cohesion: 0.05
Nodes (29): CollapsingHeader(), expandedHeaderHeight(), HeaderChildren(), HeaderContent, HeaderPeriod, HeaderPhase, Collapsed, Expanded (+21 more)

### Community 33 - "6.1 Slices"
Cohesion: 0.10
Nodes (20): colors(), ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, paletteFor(), SansFont (+12 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.25
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 37 - "PftTypographyTest"
Cohesion: 0.15
Nodes (3): sansFamily(), PftTypographyTest, Resolved

### Community 38 - "CategoryGlyph"
Cohesion: 0.08
Nodes (8): CategoryGlyph(), fixedSp(), HeaderIconButton(), GlyphSize, Chip, Hero, CategoryGlyphTest, HeaderIconButtonTest

### Community 42 - "Project README"
Cohesion: 0.22
Nodes (6): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Code Structure Map, core/shared Shared Packages, Project README, Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "Public interface"
Cohesion: 0.06
Nodes (38): Required, Behaviours worth knowing, How an entity step composes the blocks, Public interface, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), Account, AccountDraft, AccountLists (+30 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.10
Nodes (4): iosSpring(), pressScale(), PftMotionTest, 7. Test plan

### Community 45 - "HeaderTypePairingTest.kt"
Cohesion: 0.17
Nodes (4): HeaderTypePairingTest, assertShownInFull(), isShownInFull(), layout()

### Community 46 - "PreMigrationBackup"
Cohesion: 0.26
Nodes (3): PreMigrationBackup, factory(), Pre-migration backup (INT-5)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 49 - "CategoryGlyphScreenshotTest"
Cohesion: 0.23
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 50 - "AuditedWriter"
Cohesion: 0.16
Nodes (4): AuditedWriter, EntityRepository, InMemoryEntityRepository, 2. Current state

### Community 51 - "CollapsingHeaderScaffold"
Cohesion: 0.10
Nodes (44): Public interface, `ui/common` (shared components, DS3, DS4, DS5 and A5d), `ui/nav` and `ui/home` (A5f), `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), Window theme, MainActivity, BottomActionBar(), CollapsingHeaderScaffold() (+36 more)

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.09
Nodes (14): Probe, RoomAuditColumnsTest, AuditColumnsCheck, AuditedTable, 6. Implementation steps (TDD-ordered), Later-slice deltas (no new PRs; each rides its slice's PR), PR breakdown (incremental — required), Step C1b — Itemise: multi-line edit and reconciliation (core) (+6 more)

### Community 54 - "EntityMeta"
Cohesion: 0.06
Nodes (25): EntityMeta, LedgerEntity, EntityId, Fixture, Leaky, LeakyRepository, Note, NoteId (+17 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.22
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "MoneyText"
Cohesion: 0.07
Nodes (28): `:app` — Android application, Dependencies, Manifest security posture (SEC-2, SEC-3, SEC-6), Responsibilities and boundaries, MoneySize, Hero, Large, atSize() (+20 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.11
Nodes (22): Personal Finance Tracker PRD, Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope, Idempotent ingestion (X-2) (+14 more)

### Community 59 - "UserId"
Cohesion: 0.08
Nodes (14): `data` (Room persistence, A4a), MetaColumns, MetaMapper, RoomIdentityRepository, MetaMapperTest, RoomIdentityRepositoryTest, AuditedTables, TestDatabase (+6 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 61 - "SetupViewModelTest"
Cohesion: 0.17
Nodes (3): awaitMain(), FailsFirstAppend, SetupViewModelTest

### Community 63 - "13. Deviations from the plan, by slice"
Cohesion: 0.11
Nodes (18): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), sansWeights(), DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF (+10 more)

### Community 65 - "GroupedSection.kt"
Cohesion: 0.18
Nodes (4): GroupedRow, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags

### Community 66 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.06
Nodes (23): RoomTransactionRunner, RoomAppSettingsRepository, RoomAppSettingsRepositoryTest, Configuration, Responsibilities & boundaries, AuditDiff, AuditRecorder, Auditable (+15 more)

### Community 68 - "MoneyColors"
Cohesion: 0.29
Nodes (3): MoneyColors, PftSpacing, 3.3 Tokens

### Community 72 - "How to run and test"
Cohesion: 0.07
Nodes (6): How to run and test, PftDatabase, MainActivityOpeningTest, SlowOpeningApplication, MainActivityStartupTest, DevRouteTest

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 74 - "runSuspending"
Cohesion: 0.05
Nodes (8): FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomTransactionRunnerTest, AppContainerTest, runSuspending(), 3.3 Dependencies (one pins-only PR, A5c)

### Community 76 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.16
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 77 - "7. Test plan"
Cohesion: 0.12
Nodes (10): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 1. Requirement, 3.4 Header glow (UI-13, DS6d) (+2 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.11
Nodes (19): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6) (+11 more)

### Community 82 - "AuditEntryEntity"
Cohesion: 0.11
Nodes (5): AuditDao, AuditEntryEntity, IdentityDao, DeviceEntity, UserProfileEntity

### Community 84 - "SetupViewModel.kt"
Cohesion: 0.12
Nodes (6): `ui/setup` and the UiState convention (A5e), HomeUiState, HomeViewModel, SetupUiState, SetupViewModel, Step 12 — `SetupViewModel` (A5e)

### Community 85 - "MigrationTestSupport"
Cohesion: 0.10
Nodes (5): AuditTriggers, MigrationTestSupport, MigrationTest, The audit trail, Audit triggers

### Community 87 - "PreMigrationBackupTest"
Cohesion: 0.05
Nodes (9): Configuration, PreMigrationBackupTest, LoggingScanTest, DbKeyManagerWiringTest, SourceScan, Violation, ThemeLiteralScanTest, Planned: premium look (DS6) (+1 more)

### Community 91 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 93 - "GroupedSectionTest.kt"
Cohesion: 0.05
Nodes (16): GroupedSectionColors, AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, BackgroundStyle, FLAT (+8 more)

### Community 94 - "PftTheme"
Cohesion: 0.15
Nodes (4): DesignCatalog(), PftTheme(), DesignCatalogTest, Step 11 — `NameField` and `EditFormScaffold` (A5d)

### Community 95 - "DbKeyManager"
Cohesion: 0.24
Nodes (4): keys(), DbKeyManager, Decoded, WrappedKeyFormat

### Community 96 - "Architecture overview"
Cohesion: 0.33
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 98 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.11
Nodes (19): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 3.1 Modules (Gradle), 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3. Design & decision (+11 more)

### Community 104 - "MotionSection.kt"
Cohesion: 0.14
Nodes (6): MotionSection(), SpringTrack(), PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 105 - "AuditFieldsCheck.kt"
Cohesion: 0.08
Nodes (19): AuditFieldsCheck, haveCompleteAuditFields(), shouldHaveCompleteAuditFields(), 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 3.1 The model: every transaction has one or more lines (+11 more)

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 111 - "SetupScreen.kt"
Cohesion: 0.07
Nodes (9): message(), NameField(), HomeRoute(), HomeScreen(), Routes, SetupRoute(), SetupScreen(), Where it lives (+1 more)

### Community 113 - "CatalogSection"
Cohesion: 0.14
Nodes (14): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, FORMS, GLYPHS (+6 more)

### Community 114 - "DataUnavailableScreenTest"
Cohesion: 0.15
Nodes (8): Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING, DataUnavailableScreenTest

### Community 115 - "HostedComposeRule"
Cohesion: 0.22
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 118 - "FieldState"
Cohesion: 0.10
Nodes (5): FieldCheck, FieldState, FieldStateTest, SetupScreenshotTest, InMemoryEntityStore

### Community 119 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5), M1a Core ledger (+2 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.12
Nodes (9): after(), cleanUp(), header(), keyCopy(), loadNativeLibraryAndClean(), putSetting(), settingDirectly(), theDatabaseRunsInWriteAheadLogMode() (+1 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.17
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), open(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "shouldbe"
Cohesion: 0.06
Nodes (32): RoomAuditLog, EpochMillis, AuditDaoTest, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE (+24 more)

### Community 125 - "FormScreenshotTest.kt"
Cohesion: 0.21
Nodes (3): EditFormPreview(), EditFormSample(), FormScreenshotTest

### Community 128 - "modifier"
Cohesion: 0.21
Nodes (3): DevHeaderActions(), devRoutes(), PaletteScreenshotTest

### Community 139 - ".of"
Cohesion: 0.22
Nodes (6): EpochMillisTest, NameBasedId, Conventions, Data model, Version 1 (A4a), Version table

### Community 142 - "TestClock"
Cohesion: 0.09
Nodes (7): clockAt(), Uuid7Test, TestClock, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest

### Community 143 - "BottomActionBar.kt"
Cohesion: 0.14
Nodes (4): BottomActionBarPreview(), BottomActionBarSamples(), StatusChipPreview(), StatusChipSamples()

### Community 144 - "FontBudgetTest"
Cohesion: 0.21
Nodes (6): FontBudgetTest, 10. Risks & mitigations, 11. Rollout & verification, 4. Data model & schema, 9. Documentation updates (all required surfaces), Plan: M1a premium look (DS6): curated palettes, tonal surfaces, header glow, icon-only header actions, type pairing, bundled sans

### Community 148 - ".fromIconKey"
Cohesion: 0.27
Nodes (5): Emoji, GlyphSource, isSymbolKey(), Letter, Symbol

### Community 149 - "DatabaseFactory"
Cohesion: 0.14
Nodes (11): backup(), open(), DatabaseFactory, Corruption handling, Fail closed, Persistence and security, SQLCipher, The key and the KEK (+3 more)

### Community 164 - "Appearance: colour palettes, dynamic colour, background and type"
Cohesion: 0.10
Nodes (12): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you, Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen (+4 more)

### Community 168 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 172 - "CategoryPalette"
Cohesion: 0.52
Nodes (3): CategoryColors, CategoryPalette, pair()

### Community 176 - "Icons and emoji (DS3, UI-6)"
Cohesion: 0.50
Nodes (4): Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

## Knowledge Gaps
- **203 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+198 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 626 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **65 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `modifier`, `EmptyStateTest.kt`, `AuditEntryMapperTest`, `ManifestHardeningTest`, `Paise`, `SetupRouteTest.kt`, `ThemePrefs`, `ColorContrastTest`, `SafeLogTest`, `LogEventTest`, `ThemePrefsTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `SampleThing.kt`, `AppContainer`, `AppServicesTest`, `CollapsingHeaderScaffold.kt`, `6.1 Slices`, `DbKeyManagerJvmTest`, `EditFormScaffoldTest.kt`, `SetupScreenTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `HomeScreenTest.kt`, `GlyphSourceTest`, `assertisdisplayed`, `LaunchViewModelTest.kt`, `BundledSymbolsLicenceTest`, `PftMotion.kt`, `PftShapesTest.kt`, `CategoryPaletteTest`, `Public interface`, `LaunchRoutingTest.kt`, `CategoryGlyphScreenshotTest`, `CollapsingHeaderScaffold`, `getvalue`, `RoomAuditColumnsTest`, `HeaderGlowScaffoldTest.kt`, `ContentScreenshotTest`, `DbKeyManagerTest`, `MoneyText`, `UserId`, `SetupViewModelTest`, `ThemePaletteTest`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `SetupFlowTest.kt`, `runSuspending`, `WindowBackgroundTest.kt`, `Previews, the token sheet and screenshot goldens`, `PftSymbolTest`, `ReleaseHasNoCatalogueTest.kt`, `MigrationTestSupport`, `AuditTriggersTest`, `PreMigrationBackupTest`, `TokenSheetScreenshotTest.kt`, `PftSurfacesTest`, `StatusChipTest.kt`, `GroupedSectionTest.kt`, `PftTheme`, `DatabaseStartupTest.kt`, `MoneyToneTest`, `BundledFontTest`, `BottomActionBarTest.kt`, `DataUnavailableScreenTest`, `LogEventShapeTest`, `PftRootTest.kt`, `FieldState`, `shouldbe`, `WindowThemeTest`, `FormScreenshotTest.kt`?**
  _High betweenness centrality (0.268) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _203 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `modifier`, `EmptyStateTest.kt`, `ThemePrefs`, `Paise`, `SetupRouteTest.kt`, `ColorContrastTest`, `BottomActionBar.kt`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `MoneyTextTest.kt`, `DesignCatalog.kt`, `CollapsingHeaderScaffoldTest`, `AppContainer`, `SetupScreenTest.kt`, `CollapsingHeaderScaffold.kt`, `6.1 Slices`, `EditFormScaffoldTest.kt`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `CategoryGlyph`, `HomeScreenTest.kt`, `PftShapesTest.kt`, `assertisdisplayed`, `CategoryPalette`, `HeaderTypePairingTest.kt`, `LaunchRoutingTest.kt`, `CategoryGlyphScreenshotTest`, `CollapsingHeaderScaffold`, `getvalue`, `ContentScreenshotTest`, `MoneyText`, `13. Deviations from the plan, by slice`, `GroupedSection.kt`, `SetupFlowTest.kt`, `MoneyColors`, `How to run and test`, `WindowBackgroundTest.kt`, `Previews, the token sheet and screenshot goldens`, `7. Test plan`, `ReleaseHasNoCatalogueTest.kt`, `Plan: M1a design system and visual direction (lands before A5)`, `TokenSheetScreenshotTest.kt`, `StatusChipTest.kt`, `GroupedSectionTest.kt`, `Architecture overview`, `MotionSection.kt`, `BottomActionBarTest.kt`, `SetupScreen.kt`, `DataUnavailableScreenTest`, `PftRootTest.kt`, `FieldState`, `FormScreenshotTest.kt`?**
  _High betweenness centrality (0.117) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._