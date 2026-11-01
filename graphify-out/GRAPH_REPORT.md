# Graph Report - personal-finance-tracker  (2026-11-02)

## Corpus Check
- 278 files · ~212,287 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3423 nodes · 9764 edges · 156 communities (77 shown, 79 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 1535 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `1b205ebb`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- RoomAuditedWriterIntegrationTest
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- shouldbe
- hooks
- test_import_symbols.py
- GroupedSection.kt
- ThemePrefs
- Paise
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
- RoomEntityRepositoryTest.kt
- AppContainer
- InMemoryEntityStore
- CollapsingHeaderScaffold.kt
- Previews, the token sheet and screenshot goldens
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- HeaderGlowScaffoldTest.kt
- PftTypographyTest
- .clock
- PftShapesTest.kt
- CategoryGlyph
- HeaderTypePairingTest.kt
- Project README
- 6.1 Slices
- PftMotion.kt
- CategoryGlyphScreenshotTest
- 13. Deviations from the plan, by slice
- CLAUDE.md Development Constitution
- PftTypography.kt
- HostedComposeRule
- render
- AuditEntryMapperTest
- AuditEntryEntity
- RoomAuditColumnsTest
- EntityMeta
- ContentScreenshotTest
- DbKeyManagerTest
- MoneyText
- Personal Finance Tracker PRD
- UserId
- redact_text
- NavTransitionTest.kt
- MoneyTextTest.kt
- SampleThing.kt
- Pseudo
- AppSettingEntity
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- Collecting and Redacting Sample Messages Guide
- TokenSheetScreenshotTest.kt
- MainActivityStartupTest.kt
- _Text
- BottomActionBar
- import_symbols.py
- AppContainerTest.kt
- HeaderPolicy
- DesignCatalog.kt
- PressScaleTest.kt
- PftDatabaseTest
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- HeaderSlot
- HeaderPhase
- MigrationTestSupport
- MotionSection.kt
- PreMigrationBackupTest
- TabularFiguresTest
- runSuspending
- GlyphSourceTest
- PreMigrationBackup
- PaiseParserTest.kt
- GroupedSectionTest.kt
- PftTheme
- DbKeyManager
- PftSymbolTest
- BottomActionBar.kt
- CollapsingHeaderScaffold
- DatabaseStartupTest.kt
- MoneyToneTest
- Migrations.kt
- DbKeyUnavailable
- CatalogSection
- PaletteSamples.kt
- SafeLogTest
- parse_svg
- ThemePalette
- BundledFontTest
- DesignCatalogTest.kt
- ImportRunTest
- LogEventShapeTest
- InMemoryKekSource
- AuditTriggersTest
- DataUnavailableScreenTest
- Layered Modular Architecture (core vs app)
- RoomTransactionRunnerTest
- PftRootTest.kt
- ThemePaletteTest
- .format
- PaletteScreenshotTest.kt
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...)
- How to run and test
- WindowThemeTest
- 3.9 Scope: app-wide
- 7. Test plan
- Plan: M1a — Core ledger (manual-entry household tracker)
- CategoryPaletteTest
- .fromIconKey
- PaiseParser
- StatusChip
- EmptyState
- PftRootReadyTest.kt
- LogEventTest
- ManifestHardeningTest
- PftDatabase
- WindowBackgroundTest.kt
- RoomAppSettingsRepositoryTest.kt
- PftSurfacesTest
- PftSpacingTest
- PaletteSwatchesTest.kt
- AuditTriggers
- FontBudgetTest
- MigrationTest
- Security, privacy and integrity requirements (SEC/INT)
- .onCreate
- MoneySize
- Icons and emoji (DS3, UI-6)
- applicationprovider
- RoomEntityRepository
- DatabaseFactory
- MainActivityOpeningTest.kt

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 163 edges
2. `ThemePrefs` - 136 edges
3. `PftSymbol` - 123 edges
4. `Paise` - 90 edges
5. `How to run and test` - 83 edges
6. `CollapsingHeaderScaffoldTest` - 82 edges
7. `runSuspending()` - 74 edges
8. `HostedComposeRule` - 71 edges
9. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 71 edges
10. `UserId` - 57 edges

## Surprising Connections (you probably didn't know these)
- `3.5 Fonts: bundling, licensing, custom import` --references--> `SafeLog`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/log/SafeLog.kt
- `13. Deviations from the plan, by slice` --references--> `asHeroTop()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderGlow.kt
- ``StatusChip`` --references--> `StatusChip()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/StatusChip.kt
- `Adding an icon` --references--> `PftSymbol`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftSymbol.kt
- `UI layer` --references--> `PftTheme()`  [INFERRED]
  docs/architecture/overview.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftTheme.kt

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

## Communities (156 total, 79 thin omitted)

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

### Community 5 - "shouldbe"
Cohesion: 0.06
Nodes (15): EpochMillis, How to run and test, AuditLog, SettingKeys, AuditDiffTest, Thing, EntityMetaTest, IdentityServiceTest (+7 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "GroupedSection.kt"
Cohesion: 0.18
Nodes (4): GroupedRow, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags

### Community 9 - "ThemePrefs"
Cohesion: 0.10
Nodes (5): resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemePrefsTest, ThemeResolutionTest

### Community 10 - "Paise"
Cohesion: 0.13
Nodes (3): MoneyTextTest, AmountTooLarge, Paise

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.09
Nodes (16): AccentHueTest, Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone() (+8 more)

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.15
Nodes (24): moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense (+16 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 29 - "RoomEntityRepositoryTest.kt"
Cohesion: 0.22
Nodes (4): RoomEntityRepositoryTest, SampleId, SampleThing, SampleThingRepository

### Community 30 - "AppContainer"
Cohesion: 0.04
Nodes (71): Debug and release source sets, `di` and startup (A5b), `log` (safe logging, A5a), Public interface, Purpose, Window theme, AppContainer, AppServices (+63 more)

### Community 33 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.14
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.16
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 38 - ".clock"
Cohesion: 0.15
Nodes (7): Data it owns, MetaColumns, MetaMapper, EpochMillisTest, MetaMapperTest, NameBasedId, Conventions

### Community 40 - "CategoryGlyph"
Cohesion: 0.07
Nodes (5): CategoryGlyph(), HeaderIconButton(), BottomActionBarTest, CategoryGlyphTest, HeaderIconButtonTest

### Community 42 - "Project README"
Cohesion: 0.14
Nodes (13): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer (+5 more)

### Community 43 - "6.1 Slices"
Cohesion: 0.12
Nodes (21): sansFamily(), paletteFor(), SansFont, FIGTREE, PLUS_JAKARTA_SANS, SYSTEM, ThemeMode, DARK (+13 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.09
Nodes (6): iosSpring(), PftMotion, pressScale(), PftMotionTest, 3.7 Component inventory (UI-9), 11. Rollout & verification

### Community 46 - "13. Deviations from the plan, by slice"
Cohesion: 0.11
Nodes (11): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), ChipColors, AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, MoneyColors (+3 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.10
Nodes (24): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+16 more)

### Community 48 - "PftTypography.kt"
Cohesion: 0.14
Nodes (13): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansWeights(), DisplayFont, BUNDLED_SERIF (+5 more)

### Community 49 - "HostedComposeRule"
Cohesion: 0.24
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 50 - "render"
Cohesion: 0.31
Nodes (4): DbKeyUnavailable, DbOpened, LogValue, render()

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.05
Nodes (28): EntityMeta, LedgerEntity, EntityId, AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note (+20 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.21
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "MoneyText"
Cohesion: 0.18
Nodes (9): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText(), RollingDigits() (+1 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.08
Nodes (32): Personal Finance Tracker PRD, Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Automatic capture from Email (E-0..E-5) (+24 more)

### Community 59 - "UserId"
Cohesion: 0.05
Nodes (30): RoomAuditLog, RoomIdentityRepository, AuditDaoTest, RoomIdentityRepositoryTest, AuditedTable, AuditedTables, FieldChange, AuditAction (+22 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 63 - "SampleThing.kt"
Cohesion: 0.16
Nodes (5): EntityMapper, SampleDatabase, SampleThingDao, SampleThingEntity, SampleThingMapper

### Community 66 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.05
Nodes (38): `data` (Room persistence, A4a), RoomTransactionRunner, RoomAppSettingsRepository, Behaviours worth knowing, Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies (+30 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 72 - "BottomActionBar"
Cohesion: 0.10
Nodes (24): `ui/common` (shared components, DS3, DS4 and DS5), BottomActionBar(), BottomBarAction, GroupedSection(), Adding a token, Build-time trust decisions, Content primitives (DS5), Design system (+16 more)

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 76 - "DesignCatalog.kt"
Cohesion: 0.07
Nodes (14): ChoiceRow(), DesignCatalog(), SectionContent(), DevContent(), CategoryGlyphPreview(), CategoryGlyphSamples(), SampleRow(), GroupedSectionPreview() (+6 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.09
Nodes (22): PftSpacing, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens (+14 more)

### Community 82 - "UserProfileEntity"
Cohesion: 0.22
Nodes (3): IdentityDao, DeviceEntity, UserProfileEntity

### Community 83 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 84 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 86 - "MotionSection.kt"
Cohesion: 0.07
Nodes (11): AccentCheckSection(), MotionSection(), SpringTrack(), fixedSp(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), GlyphSize (+3 more)

### Community 87 - "PreMigrationBackupTest"
Cohesion: 0.05
Nodes (9): `:app` — Android application, Configuration, Dependencies, PreMigrationBackupTest, LoggingScanTest, DbKeyManagerWiringTest, SourceScan, Violation (+1 more)

### Community 89 - "runSuspending"
Cohesion: 0.18
Nodes (4): FailingOnNthAppend, IdentityRoomIntegrationTest, runSuspending(), 3.3 Dependencies (one pins-only PR, A5c)

### Community 91 - "PreMigrationBackup"
Cohesion: 0.10
Nodes (17): `security` and the encrypted open (A4b), PreMigrationBackup, PreMigrationBackupFailed, DataUnavailableCause, IdentityCorrupt, KeyUnavailable, factory(), Corruption handling (+9 more)

### Community 92 - "PaiseParserTest.kt"
Cohesion: 0.16
Nodes (5): How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest, 7. Test plan

### Community 94 - "PftTheme"
Cohesion: 0.13
Nodes (3): dynamicPaletteColors(), PftTheme(), DesignCatalogTest

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 98 - "CollapsingHeaderScaffold"
Cohesion: 0.26
Nodes (14): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle, Dashboard (+6 more)

### Community 99 - "DatabaseStartupTest.kt"
Cohesion: 0.17
Nodes (4): DatabaseStartup, KeyUnavailable, Ready, DatabaseStartupTest

### Community 102 - "DbKeyUnavailable"
Cohesion: 0.18
Nodes (8): DbKeyUnavailable, Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING

### Community 103 - "CatalogSection"
Cohesion: 0.15
Nodes (13): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, GLYPHS, MONEY (+5 more)

### Community 104 - "PaletteSamples.kt"
Cohesion: 0.42
Nodes (4): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 107 - "ThemePalette"
Cohesion: 0.13
Nodes (13): GroupedSectionColors, BackgroundStyle, FLAT, TONAL, PftSurfaces, colors(), ThemePalette, AUBERGINE (+5 more)

### Community 109 - "DesignCatalogTest.kt"
Cohesion: 0.11
Nodes (3): assertShownInFull(), isShownInFull(), layout()

### Community 112 - "InMemoryKekSource"
Cohesion: 0.14
Nodes (3): AndroidKeystoreKek, KekSource, InMemoryKekSource

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 119 - ".format"
Cohesion: 0.16
Nodes (11): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+3 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.19
Nodes (6): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...)"
Cohesion: 0.32
Nodes (3): Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), InMemoryAuditLog, InMemoryLedgerStore

### Community 126 - "3.9 Scope: app-wide"
Cohesion: 0.33
Nodes (6): 3.1 Palettes (DS6a, DS6b), 3.2 Surfaces: tonal or flat (DS6c), 3.3 Window and first frame (UI-18, DS6c), 3.5 Icon-only header actions (UI-14, DS6e; needs DS3), 3.9 Scope: app-wide, 3. Design & decision

### Community 127 - "7. Test plan"
Cohesion: 0.12
Nodes (10): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 1. Requirement, 3.4 Header glow (UI-13, DS6d) (+2 more)

### Community 128 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.08
Nodes (20): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions (+12 more)

### Community 130 - ".fromIconKey"
Cohesion: 0.25
Nodes (5): Emoji, GlyphSource, isSymbolKey(), Letter, Symbol

### Community 136 - "ManifestHardeningTest"
Cohesion: 0.13
Nodes (3): Manifest security posture (SEC-2, SEC-3, SEC-6), Responsibilities and boundaries, ManifestHardeningTest

### Community 143 - "AuditTriggers"
Cohesion: 0.23
Nodes (6): AuditTriggers, Data model, The audit trail, Version 1 (A4a), Version table, Audit triggers

### Community 144 - "FontBudgetTest"
Cohesion: 0.21
Nodes (6): FontBudgetTest, 10. Risks & mitigations, 11. Rollout & verification, 4. Data model & schema, 9. Documentation updates (all required surfaces), Plan: M1a premium look (DS6): curated palettes, tonal surfaces, header glow, icon-only header actions, type pairing, bundled sans

### Community 146 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 147 - ".onCreate"
Cohesion: 0.25
Nodes (3): SyncWindowBackground(), 5. Flows, 8. Security considerations

### Community 148 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 149 - "Icons and emoji (DS3, UI-6)"
Cohesion: 0.27
Nodes (7): CategoryColors, CategoryPalette, pair(), Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

## Knowledge Gaps
- **197 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+192 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 600 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **79 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `CategoryPaletteTest`, `RoomAuditedWriterIntegrationTest`, `StatusChip`, `EmptyState`, `PftRootReadyTest.kt`, `LogEventTest`, `ManifestHardeningTest`, `PftDatabase`, `Paise`, `RoomAppSettingsRepositoryTest.kt`, `PftSurfacesTest`, `PftSpacingTest`, `ColorContrastTest`, `ThemePrefs`, `WindowBackgroundTest.kt`, `MigrationTest`, `applicationprovider`, `MainActivityOpeningTest.kt`, `PftThemeModeTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `RoomEntityRepositoryTest.kt`, `AppContainer`, `Previews, the token sheet and screenshot goldens`, `DbKeyManagerJvmTest`, `HeaderGlowScaffoldTest.kt`, `PftTypographyTest`, `.clock`, `PftShapesTest.kt`, `CategoryGlyph`, `PftMotion.kt`, `CategoryGlyphScreenshotTest`, `13. Deviations from the plan, by slice`, `AuditEntryMapperTest`, `RoomAuditColumnsTest`, `ContentScreenshotTest`, `DbKeyManagerTest`, `MoneyText`, `UserId`, `NavTransitionTest.kt`, `SampleThing.kt`, `TokenSheetScreenshotTest.kt`, `MainActivityStartupTest.kt`, `AppContainerTest.kt`, `PressScaleTest.kt`, `expandedHeaderHeight`, `MigrationTestSupport`, `PreMigrationBackupTest`, `runSuspending`, `GlyphSourceTest`, `GroupedSectionTest.kt`, `PftTheme`, `PftSymbolTest`, `DatabaseStartupTest.kt`, `MoneyToneTest`, `SafeLogTest`, `ThemePalette`, `BundledFontTest`, `LogEventShapeTest`, `AuditTriggersTest`, `DataUnavailableScreenTest`, `RoomTransactionRunnerTest`, `PftRootTest.kt`, `ThemePaletteTest`, `.format`, `PaletteScreenshotTest.kt`, `WindowThemeTest`?**
  _High betweenness centrality (0.257) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _197 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `PftSymbol` connect `PftSymbol` to `CollapsingHeaderScaffold.kt`, `Previews, the token sheet and screenshot goldens`, `.fromIconKey`, `PftSymbolTest`, `BottomActionBar`, `CategoryGlyph`, `DesignCatalog.kt`, `CollapsingHeaderScaffoldTest`, `13. Deviations from the plan, by slice`, `assertequals`, `PftMotion.kt`, `Plan: M1a design system and visual direction (lands before A5)`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `Icons and emoji (DS3, UI-6)`, `MotionSection.kt`, `AppContainer`, `How to run and test`, `3.9 Scope: app-wide`?**
  _High betweenness centrality (0.095) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._