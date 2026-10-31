# Graph Report - personal-finance-tracker  (2026-10-31)

## Corpus Check
- 258 files · ~193,832 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 123 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3166 nodes · 8884 edges · 140 communities (79 shown, 61 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 1275 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `7bc051e1`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- How to run and test
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- WindowThemeTest
- hooks
- test_import_symbols.py
- GroupedSection.kt
- ThemePrefs
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- MoneySemantic
- Catalog.kt
- detekt.yml
- PftThemeModeTest
- PftSymbol
- CollapsingHeaderScaffoldTest
- HeaderTypePairingTest.kt
- InMemoryEntityStore
- CollapsingHeaderScaffold.kt
- HeaderGlowScaffoldTest.kt
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- AccentCheckSection.kt
- PftTypographyTest
- .of
- PftShapesTest.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- AuditEntryMapperTest.kt
- Project README
- UserId
- PftMotion.kt
- CategoryGlyphScreenshotTest
- MoneyColors
- CLAUDE.md Development Constitution
- PftTypography.kt
- HostedComposeRule
- MainActivity
- HeaderIconButtonTest.kt
- AuditEntryEntity
- RoomAuditColumnsTest
- EntityMeta
- ContentScreenshotTest
- DbKeyManagerTest
- MoneyText
- Personal Finance Tracker PRD
- AuditEntry
- redact_text
- PftSymbolTest
- MoneyTextTest.kt
- SampleThing.kt
- Pseudo
- AppSettingEntity
- Public interface
- Collecting and Redacting Sample Messages Guide
- BottomActionBarTest.kt
- DbKeyUnavailable
- _Text
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- import_symbols.py
- RoomEntityRepository
- HeaderPolicy
- DesignCatalog.kt
- TokenSheetScreenshotTest.kt
- 7. Test plan
- AuditTriggersTest
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- HeaderSlot
- HeaderPhase
- MigrationTestSupport
- MotionSection.kt
- PreMigrationBackupTest
- TabularFiguresTest
- .format
- Loans and EMIs (LN-1..LN-5)
- PreMigrationBackup
- Slice A — Foundation (`feat/m1a-foundation`)
- GroupedSectionTest.kt
- PftTheme
- DbKeyManager
- ThemePrefsTest
- BottomActionBar.kt
- PeriodStepper
- DatabaseFactory
- Paise
- Migrations.kt
- KeyUnavailableScreenTest.kt
- HeaderStyle
- PaletteSamples.kt
- Icons and emoji (DS3, UI-6)
- parse_svg
- BackgroundStyle
- ThemeLiteralScanTest
- ImportRunTest
- BundledSymbolsLicenceTest
- AndroidKeystoreKek.kt
- assertequals
- BundledFontTest
- Layered Modular Architecture (core vs app)
- AppSetting
- PftSpacing
- ThemePalette
- Architecture overview
- PaletteScreenshotTest.kt
- asserttrue
- DatabaseFactoryTest.kt
- AuditedWriterTest.kt
- PftDatabase
- ManifestHardeningTest
- modifier
- CollapsingHeaderScaffold
- Plan: M1a — Core ledger (manual-entry household tracker)
- Immutable Audit Trail
- PaiseParser
- StatusChipTest.kt
- EmptyStateTest.kt
- 13. Deviations from the plan, by slice
- DbKeyManagerWiringTest.kt
- MainActivityStartupTest.kt
- BaseDao
- Appearance: colour palettes, dynamic colour, background and type
- MoneySize

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 158 edges
2. `ThemePrefs` - 131 edges
3. `PftSymbol` - 120 edges
4. `Paise` - 90 edges
5. `CollapsingHeaderScaffoldTest` - 82 edges
6. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 71 edges
7. `How to run and test` - 70 edges
8. `HostedComposeRule` - 64 edges
9. `UserId` - 57 edges
10. `13. Deviations from the plan, by slice` - 56 edges

## Surprising Connections (you probably didn't know these)
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `13. Deviations from the plan, by slice` --references--> `asHeroTop()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderGlow.kt
- `Adding an icon` --references--> `PftSymbol`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftSymbol.kt
- `UI layer` --references--> `PftTheme()`  [INFERRED]
  docs/architecture/overview.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftTheme.kt
- `1. Requirement` --references--> `PftTheme()`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftTheme.kt

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

## Communities (140 total, 61 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "How to run and test"
Cohesion: 0.05
Nodes (10): How to run and test, RoomEntityRepositoryTest, FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomIdentityRepositoryTest, RoomTransactionRunnerTest (+2 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "GroupedSection.kt"
Cohesion: 0.13
Nodes (7): GroupedRow, GroupedSectionColors, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, GroupedSectionPreview(), GroupedSectionSamples()

### Community 9 - "ThemePrefs"
Cohesion: 0.10
Nodes (6): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest, 4. Data model & schema

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.08
Nodes (15): AccentHueTest, Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone() (+7 more)

### Community 16 - "MoneySemantic"
Cohesion: 0.24
Nodes (13): Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense, Income (+5 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 26 - "PftThemeModeTest"
Cohesion: 0.07
Nodes (3): NavTransitionTest, PftThemeModeTest, WindowBackgroundTest

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.08
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "HeaderTypePairingTest.kt"
Cohesion: 0.08
Nodes (10): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, HeaderTypePairingTest, assertShownInFull(), isShownInFull() (+2 more)

### Community 33 - "HeaderGlowScaffoldTest.kt"
Cohesion: 0.06
Nodes (10): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowScaffoldTest, HeaderGlowTest, PressScaleTest (+2 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "AccentCheckSection.kt"
Cohesion: 0.12
Nodes (7): AccentCheckSection(), AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), StatusChipPreview(), StatusChipSamples()

### Community 38 - ".of"
Cohesion: 0.12
Nodes (9): Data it owns, EpochMillis, EpochMillisTest, NameBasedId, Conventions, Data model, The audit trail, Version 1 (A4a) (+1 more)

### Community 40 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.09
Nodes (10): CategoryGlyph(), Emoji, GlyphSource, isEmojiValue(), isSymbolKey(), Letter, Symbol, CategoryGlyphTest (+2 more)

### Community 42 - "Project README"
Cohesion: 0.23
Nodes (7): Code Structure Map, core/shared Shared Packages, Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen, What it does for you, Project README

### Community 43 - "UserId"
Cohesion: 0.10
Nodes (11): RoomIdentityRepository, AuditedTables, Auditable, Device, IdentityRepository, UserProfile, DeviceId, EntityId (+3 more)

### Community 45 - "CategoryGlyphScreenshotTest"
Cohesion: 0.23
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 46 - "MoneyColors"
Cohesion: 0.21
Nodes (3): ChipColors, MoneyColors, ChipColorsTest

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "PftTypography.kt"
Cohesion: 0.19
Nodes (10): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF (+2 more)

### Community 49 - "HostedComposeRule"
Cohesion: 0.24
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 50 - "MainActivity"
Cohesion: 0.24
Nodes (14): `:app` — Android application, Configuration, Debug and release source sets, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose, Responsibilities and boundaries, `security` and the encrypted open (A4b) (+6 more)

### Community 51 - "HeaderIconButtonTest.kt"
Cohesion: 0.21
Nodes (3): HeaderIconButton(), HeaderIconButtonTest, 9. Documentation updates (all required surfaces)

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (4): Probe, RoomAuditColumnsTest, AuditColumnsCheck, AuditedTable

### Community 54 - "EntityMeta"
Cohesion: 0.06
Nodes (22): `data` (Room persistence, A4a), MetaColumns, MetaMapper, MetaMapperTest, SampleThing, SampleThingMapper, EntityMeta, LedgerEntity (+14 more)

### Community 57 - "MoneyText"
Cohesion: 0.17
Nodes (10): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText(), RollingDigits() (+2 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.09
Nodes (28): Personal Finance Tracker PRD, Audit trail (INT-2), Balance observations (L-10), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5) (+20 more)

### Community 59 - "AuditEntry"
Cohesion: 0.07
Nodes (22): RoomAuditLog, AuditDaoTest, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE (+14 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 63 - "SampleThing.kt"
Cohesion: 0.32
Nodes (3): SampleDatabase, SampleThingDao, SampleThingEntity

### Community 66 - "Public interface"
Cohesion: 0.07
Nodes (29): RoomTransactionRunner, RoomTestKit, Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How an entity step composes the blocks, Public interface (+21 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 69 - "DbKeyUnavailable"
Cohesion: 0.10
Nodes (16): DbKeyUnavailable, Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING (+8 more)

### Community 72 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.10
Nodes (21): Behaviours worth knowing, AuditedWriter, AuditSource, MANUAL, RECURRING, SYSTEM, EntityRepository, AmountNotPositive (+13 more)

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 76 - "DesignCatalog.kt"
Cohesion: 0.09
Nodes (19): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, GLYPHS, MONEY (+11 more)

### Community 78 - "7. Test plan"
Cohesion: 0.24
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.15
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 82 - "UserProfileEntity"
Cohesion: 0.22
Nodes (3): IdentityDao, DeviceEntity, UserProfileEntity

### Community 83 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 84 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 85 - "MigrationTestSupport"
Cohesion: 0.10
Nodes (4): AuditTriggers, MigrationTestSupport, MigrationTest, Audit triggers

### Community 86 - "MotionSection.kt"
Cohesion: 0.09
Nodes (6): MotionSection(), SpringTrack(), fixedSp(), GlyphSize, Chip, Hero

### Community 89 - ".format"
Cohesion: 0.15
Nodes (12): Dependencies, Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface (+4 more)

### Community 90 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5), M1a Core ledger (+2 more)

### Community 91 - "PreMigrationBackup"
Cohesion: 0.15
Nodes (11): PreMigrationBackup, PreMigrationBackupFailed, Corruption handling, Persistence and security, Pre-migration backup (INT-5), SQLCipher, The key and the KEK, The key array and SQLCipher (a trap) (+3 more)

### Community 92 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.05
Nodes (22): SampleThingRepository, TestDatabase, How to run and test, AlreadyInitialised, AuditDiffTest, EntityMetaTest, IdentityServiceTest, IdsTest (+14 more)

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 98 - "PeriodStepper"
Cohesion: 0.27
Nodes (11): CollapsingHeader(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderTitle(), LowerBand(), PeriodStepper() (+3 more)

### Community 99 - "DatabaseFactory"
Cohesion: 0.12
Nodes (7): DatabaseFactory, DatabaseStartup, KeyUnavailable, Ready, PftApplication, open(), DatabaseStartupTest

### Community 100 - "Paise"
Cohesion: 0.15
Nodes (6): `ui/common` (shared components, DS3, DS4 and DS5), moneySign(), MoneyToneTest, Row, Paise, `MoneyText`

### Community 103 - "HeaderStyle"
Cohesion: 0.67
Nodes (3): HeaderStyle, Dashboard, Standard

### Community 104 - "PaletteSamples.kt"
Cohesion: 0.42
Nodes (4): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 105 - "Icons and emoji (DS3, UI-6)"
Cohesion: 0.27
Nodes (7): CategoryColors, CategoryPalette, pair(), Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 107 - "BackgroundStyle"
Cohesion: 0.31
Nodes (5): BackgroundStyle, FLAT, TONAL, PftSurfaces, 6.2 PR breakdown (incremental — required)

### Community 108 - "ThemeLiteralScanTest"
Cohesion: 0.38
Nodes (3): ThemeLiteralScanTest, Violation, Planned: premium look (DS6)

### Community 113 - "assertequals"
Cohesion: 0.07
Nodes (7): Case, GroupedSectionColorsTest, CategoryPaletteTest, PftSpacingTest, PftSurfacesTest, Pins, ThemePaletteTest

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 116 - "AppSetting"
Cohesion: 0.15
Nodes (6): RoomAppSettingsRepository, RoomAppSettingsRepositoryTest, AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository, The core/app boundary

### Community 117 - "PftSpacing"
Cohesion: 0.22
Nodes (8): PftSpacing, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins"), 3. Design & decision

### Community 118 - "ThemePalette"
Cohesion: 0.13
Nodes (13): AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, colors(), ThemePalette, AUBERGINE (+5 more)

### Community 119 - "Architecture overview"
Cohesion: 0.33
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 121 - "asserttrue"
Cohesion: 0.08
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.19
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "AuditedWriterTest.kt"
Cohesion: 0.14
Nodes (7): AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 126 - "modifier"
Cohesion: 0.12
Nodes (4): EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), DevContent()

### Community 127 - "CollapsingHeaderScaffold"
Cohesion: 0.11
Nodes (36): BottomActionBar(), CollapsingHeaderScaffold(), GroupedSection(), MoneyTone, Negative, Neutral, Positive, Warning (+28 more)

### Community 128 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.17
Nodes (10): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5. Flows, 8. Security considerations (+2 more)

### Community 134 - "13. Deviations from the plan, by slice"
Cohesion: 0.11
Nodes (27): sansFamily(), paletteFor(), SansFont, FIGTREE, SYSTEM, ThemeMode, DARK, LIGHT (+19 more)

### Community 142 - "Appearance: colour palettes, dynamic colour, background and type"
Cohesion: 0.25
Nodes (5): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you

### Community 148 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

## Knowledge Gaps
- **196 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+191 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 583 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **61 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `StatusChipTest.kt`, `EmptyStateTest.kt`, `13. Deviations from the plan, by slice`, `DbKeyManagerWiringTest.kt`, `MainActivityStartupTest.kt`, `ThemePrefs`, `MoneyTextTest`, `WindowThemeTest`, `ColorContrastTest`, `PftThemeModeTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `HeaderTypePairingTest.kt`, `HeaderGlowScaffoldTest.kt`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `PftShapesTest.kt`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `AuditEntryMapperTest.kt`, `CategoryGlyphScreenshotTest`, `MoneyColors`, `MainActivity`, `RoomAuditColumnsTest`, `EntityMeta`, `ContentScreenshotTest`, `DbKeyManagerTest`, `MoneyText`, `AuditEntry`, `PftSymbolTest`, `Public interface`, `BottomActionBarTest.kt`, `TokenSheetScreenshotTest.kt`, `7. Test plan`, `AuditTriggersTest`, `expandedHeaderHeight`, `MigrationTestSupport`, `PreMigrationBackupTest`, `.format`, `GroupedSectionTest.kt`, `PftTheme`, `ThemePrefsTest`, `DatabaseFactory`, `Paise`, `KeyUnavailableScreenTest.kt`, `BundledSymbolsLicenceTest`, `assertequals`, `BundledFontTest`, `AppSetting`, `PaletteScreenshotTest.kt`, `PftDatabase`, `ManifestHardeningTest`?**
  _High betweenness centrality (0.249) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _196 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `.`a negative balance is red and a positive balance is neutral (Q3)``, `StatusChipTest.kt`, `EmptyStateTest.kt`, `13. Deviations from the plan, by slice`, `GroupedSection.kt`, `ThemePrefs`, `MoneyTextTest`, `MainActivityStartupTest.kt`, `MoneySemantic`, `PftThemeModeTest`, `CollapsingHeaderScaffoldTest`, `HeaderTypePairingTest.kt`, `CollapsingHeaderScaffold.kt`, `HeaderGlowScaffoldTest.kt`, `AccentCheckSection.kt`, `PftTypographyTest`, `PftShapesTest.kt`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `CategoryGlyphScreenshotTest`, `MoneyColors`, `PftTypography.kt`, `MainActivity`, `HeaderIconButtonTest.kt`, `ContentScreenshotTest`, `MoneyText`, `MoneyTextTest.kt`, `BottomActionBarTest.kt`, `DesignCatalog.kt`, `TokenSheetScreenshotTest.kt`, `Plan: M1a design system and visual direction (lands before A5)`, `MotionSection.kt`, `GroupedSectionTest.kt`, `BottomActionBar.kt`, `KeyUnavailableScreenTest.kt`, `PaletteSamples.kt`, `Icons and emoji (DS3, UI-6)`, `BackgroundStyle`, `assertequals`, `PftSpacing`, `ThemePalette`, `Architecture overview`, `PaletteScreenshotTest.kt`, `modifier`, `CollapsingHeaderScaffold`?**
  _High betweenness centrality (0.104) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._