# Graph Report - personal-finance-tracker  (2026-10-31)

## Corpus Check
- 255 files · ~185,896 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 122 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3075 nodes · 8500 edges · 142 communities (77 shown, 65 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 1202 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `1695c70f`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- runSuspending
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- WindowThemeTest
- hooks
- test_import_symbols.py
- GroupedSection.kt
- ThemeResolutionTest
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
- Previews, the token sheet and screenshot goldens
- TokenSheetScreenshotTest.kt
- InMemoryEntityStore
- CollapsingHeaderScaffold.kt
- HeaderGlowScaffoldTest.kt
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- MotionSection.kt
- PftTypographyTest
- TestClock
- PftShapesTest.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- DatabaseFactory
- Project README
- UserId
- PftMotion.kt
- CategoryGlyphScreenshotTest
- ThemePalette
- CLAUDE.md Development Constitution
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- HostedComposeRule
- WindowBackgroundTest.kt
- HeaderIconButtonTest.kt
- AuditEntryEntity
- RoomAuditColumnsTest
- EntityMeta
- PaiseParserTest.kt
- DbKeyManagerTest.kt
- MoneyText
- Personal Finance Tracker PRD
- DeviceId
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
- Slice A — Foundation (`feat/m1a-foundation`)
- import_symbols.py
- RoomEntityRepositoryTest.kt
- HeaderPolicy
- DesignCatalog.kt
- PftSpacingTest
- NavTransitionTest.kt
- AuditTriggersTest
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- HeaderSlot
- HeaderPhase
- MigrationTestSupport
- ThemeLiteralScanTest
- PreMigrationBackupTest
- BackgroundStyle
- 7. Test plan
- ContentScreenshotTest
- PreMigrationBackup
- IdentityRoomIntegrationTest
- GroupedSectionTest.kt
- PftTheme
- DbKeyManager
- ThemePrefsTest
- BottomActionBar.kt
- CollapsingHeaderScaffold
- DatabaseStartupTest.kt
- MoneyToneTest
- Migrations.kt
- One-hand layout (UI-7)
- HeaderStyle
- PaletteSamples.kt
- RoomTransactionRunnerTest
- parse_svg
- MoneyColors
- AuditDaoTest.kt
- ImportRunTest
- PaiseFormatTest.kt
- AndroidKeystoreKek.kt
- ThemePaletteTest
- BundledFontTest
- Layered Modular Architecture (core vs app)
- RoomAppSettingsRepositoryTest.kt
- PftSpacing
- PaletteColors
- PaletteScreenshotTest.kt
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- Leaky
- How to run and test
- ManifestHardeningTest
- applicationprovider
- MainActivity
- 3.5 Design system (owner decision, 2026-10-29)
- PftSurfacesTest
- PaiseParser
- StatusChip
- ThemePrefs
- 7. Test plan
- PaletteSwatchesTest.kt
- MainActivityStartupTest.kt
- TextLayoutSupport.kt
- CategoryPaletteTest
- Appearance: colour palettes, dynamic colour and background
- column
- Security, privacy and integrity requirements (SEC/INT)

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 153 edges
2. `ThemePrefs` - 123 edges
3. `PftSymbol` - 120 edges
4. `Paise` - 90 edges
5. `CollapsingHeaderScaffoldTest` - 82 edges
6. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 71 edges
7. `How to run and test` - 70 edges
8. `HostedComposeRule` - 60 edges
9. `UserId` - 57 edges
10. `runSuspending()` - 54 edges

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

## Communities (142 total, 65 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "runSuspending"
Cohesion: 0.22
Nodes (3): RoomAuditedWriterIntegrationTest, AuditLog, runSuspending()

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
Cohesion: 0.11
Nodes (6): GroupedRow, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, GroupedSectionPreview(), GroupedSectionSamples()

### Community 9 - "ThemeResolutionTest"
Cohesion: 0.10
Nodes (6): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest, Surfaces and window colour (DS6c, UI-12, UI-18)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.08
Nodes (16): AccentHueTest, Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone() (+8 more)

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.09
Nodes (30): GroupedSection(), MoneySize, Hero, Large, Balance, Budget, BudgetState, AT_THRESHOLD (+22 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.08
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.16
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "MotionSection.kt"
Cohesion: 0.07
Nodes (6): AccentCheckSection(), MotionSection(), SpringTrack(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder()

### Community 37 - "PftTypographyTest"
Cohesion: 0.09
Nodes (6): pftMaterialTypography(), pftType(), PftTypography, PftTypographyTest, Resolved, Theming

### Community 38 - "TestClock"
Cohesion: 0.11
Nodes (10): Data it owns, EpochMillis, EpochMillisTest, NameBasedId, TestClock, Conventions, Data model, The audit trail (+2 more)

### Community 40 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.06
Nodes (21): CategoryGlyph(), fixedSp(), Emoji, GlyphSource, isEmojiValue(), isSymbolKey(), Letter, Symbol (+13 more)

### Community 41 - "DatabaseFactory"
Cohesion: 0.19
Nodes (6): DatabaseFactory, DatabaseStartup, KeyUnavailable, Ready, PftApplication, open()

### Community 42 - "Project README"
Cohesion: 0.14
Nodes (13): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer (+5 more)

### Community 43 - "UserId"
Cohesion: 0.07
Nodes (17): `data` (Room persistence, A4a), MetaColumns, MetaMapper, RoomIdentityRepository, MetaMapperTest, RoomIdentityRepositoryTest, AuditedTables, EntityMeta (+9 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.10
Nodes (4): PftMotion, pressScale(), Principles, 3.7 Component inventory (UI-9)

### Community 45 - "CategoryGlyphScreenshotTest"
Cohesion: 0.23
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 46 - "ThemePalette"
Cohesion: 0.11
Nodes (18): displayFamily(), colors(), ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, DisplayFont (+10 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.10
Nodes (24): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+16 more)

### Community 48 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.17
Nodes (6): AuditSource, MANUAL, RECURRING, SYSTEM, EntityRepository, Slice B — Accounts, categories, tags (`feat/m1a-ledger-setup`)

### Community 49 - "HostedComposeRule"
Cohesion: 0.24
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (4): Probe, RoomAuditColumnsTest, AuditColumnsCheck, AuditedTable

### Community 54 - "EntityMeta"
Cohesion: 0.06
Nodes (19): LedgerEntity, EntityId, AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind, LARGE (+11 more)

### Community 55 - "PaiseParserTest.kt"
Cohesion: 0.20
Nodes (4): How to run and test, PaiseParserTest, PaiseTest, 7. Test plan

### Community 57 - "MoneyText"
Cohesion: 0.09
Nodes (24): `ui/common` (shared components, DS3, DS4 and DS5), atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), moneySign() (+16 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.08
Nodes (32): Personal Finance Tracker PRD, Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Automatic capture from Email (E-0..E-5) (+24 more)

### Community 59 - "DeviceId"
Cohesion: 0.08
Nodes (22): AuditEntryMapper, AuditEntryMapperTest, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), FieldChange, AuditAction, ARCHIVE, CREATE, DELETE (+14 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 63 - "SampleThing.kt"
Cohesion: 0.13
Nodes (6): BaseDao, EntityMapper, SampleDatabase, SampleThingDao, SampleThingEntity, SampleThingMapper

### Community 66 - "Public interface"
Cohesion: 0.07
Nodes (30): RoomTransactionRunner, RoomTestKit, Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How an entity step composes the blocks, Public interface (+22 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 69 - "DbKeyUnavailable"
Cohesion: 0.10
Nodes (12): DbKeyUnavailable, Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING (+4 more)

### Community 72 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.06
Nodes (24): Behaviours worth knowing, How to run and test, AlreadyInitialised, AmountNotPositive, AmountTooLarge, Archived, BlankName, Deleted (+16 more)

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 74 - "RoomEntityRepositoryTest.kt"
Cohesion: 0.16
Nodes (5): RoomEntityRepository, RoomEntityRepositoryTest, SampleId, SampleThing, SampleThingRepository

### Community 76 - "DesignCatalog.kt"
Cohesion: 0.09
Nodes (17): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, GLYPHS, MONEY (+9 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.13
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 82 - "UserProfileEntity"
Cohesion: 0.20
Nodes (3): IdentityDao, DeviceEntity, UserProfileEntity

### Community 83 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 84 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 85 - "MigrationTestSupport"
Cohesion: 0.07
Nodes (5): AuditTriggers, AuditTriggersTest, MigrationTestSupport, MigrationTest, Audit triggers

### Community 88 - "BackgroundStyle"
Cohesion: 0.17
Nodes (10): GroupedSectionColors, BackgroundStyle, FLAT, TONAL, PftSurfaces, Case, GroupedSectionColorsTest, 6.1 Slices (+2 more)

### Community 89 - "7. Test plan"
Cohesion: 0.30
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 90 - "ContentScreenshotTest"
Cohesion: 0.21
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 91 - "PreMigrationBackup"
Cohesion: 0.13
Nodes (12): PreMigrationBackup, PreMigrationBackupFailed, factory(), Corruption handling, Persistence and security, Pre-migration backup (INT-5), SQLCipher, The key and the KEK (+4 more)

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 97 - "BottomActionBar.kt"
Cohesion: 0.20
Nodes (3): BottomActionBar(), BottomActionBarPreview(), BottomActionBarSamples()

### Community 98 - "CollapsingHeaderScaffold"
Cohesion: 0.32
Nodes (12): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderTitle(), LowerBand() (+4 more)

### Community 102 - "One-hand layout (UI-7)"
Cohesion: 0.33
Nodes (6): Motion (UI-10), One-hand layout (UI-7), Shapes, Spacing, Tokens, Typography (UI-2, UI-3, owner decision Q10)

### Community 103 - "HeaderStyle"
Cohesion: 0.67
Nodes (3): HeaderStyle, Dashboard, Standard

### Community 104 - "PaletteSamples.kt"
Cohesion: 0.25
Nodes (5): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch(), PftShapes

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 107 - "MoneyColors"
Cohesion: 0.19
Nodes (3): ChipColors, MoneyColors, ChipColorsTest

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 116 - "RoomAppSettingsRepositoryTest.kt"
Cohesion: 0.15
Nodes (4): RoomAppSettingsRepository, RoomAppSettingsRepositoryTest, AppSetting, InMemoryAppSettingsRepository

### Community 117 - "PftSpacing"
Cohesion: 0.20
Nodes (9): PftSpacing, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens, 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins") (+1 more)

### Community 118 - "PaletteColors"
Cohesion: 0.22
Nodes (7): AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, Colour, 13. Deviations from the plan, by slice

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.13
Nodes (6): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "Leaky"
Cohesion: 0.16
Nodes (6): Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 124 - "How to run and test"
Cohesion: 0.24
Nodes (3): How to run and test, PftDatabase, TestDatabase

### Community 127 - "MainActivity"
Cohesion: 0.13
Nodes (21): `:app` — Android application, Configuration, Debug and release source sets, Dependencies, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose, Responsibilities and boundaries (+13 more)

### Community 128 - "3.5 Design system (owner decision, 2026-10-29)"
Cohesion: 0.12
Nodes (16): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5. Flows, 6. Implementation steps (TDD-ordered) (+8 more)

### Community 132 - "StatusChip"
Cohesion: 0.15
Nodes (10): StatusChip(), StatusChipTest, Adding a token, Build-time trust decisions, Content primitives (DS5), Design system, `EmptyState`, `GroupedSection` (+2 more)

### Community 134 - "7. Test plan"
Cohesion: 0.13
Nodes (10): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 1. Requirement, 3.4 Header glow (UI-13, DS6d) (+2 more)

### Community 140 - "TextLayoutSupport.kt"
Cohesion: 0.38
Nodes (3): assertShownInFull(), isShownInFull(), layout()

### Community 142 - "Appearance: colour palettes, dynamic colour and background"
Cohesion: 0.14
Nodes (11): Appearance: colour palettes, dynamic colour and background, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions (+3 more)

### Community 144 - "column"
Cohesion: 0.17
Nodes (4): StatusChipPreview(), StatusChipSamples(), TokenSheet(), TokenSheetPreview()

### Community 145 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

## Knowledge Gaps
- **192 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+187 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 583 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **65 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `runSuspending`, `PftSurfacesTest`, `StatusChip`, `ThemePrefs`, `WindowThemeTest`, `ThemeResolutionTest`, `MainActivityStartupTest.kt`, `Paise`, `CategoryPaletteTest`, `ColorContrastTest`, `PftThemeModeTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `Previews, the token sheet and screenshot goldens`, `TokenSheetScreenshotTest.kt`, `HeaderGlowScaffoldTest.kt`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `PftShapesTest.kt`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `UserId`, `CategoryGlyphScreenshotTest`, `WindowBackgroundTest.kt`, `RoomAuditColumnsTest`, `DbKeyManagerTest.kt`, `MoneyText`, `DeviceId`, `PftSymbolTest`, `Public interface`, `BottomActionBarTest.kt`, `DbKeyUnavailable`, `RoomEntityRepositoryTest.kt`, `PftSpacingTest`, `NavTransitionTest.kt`, `expandedHeaderHeight`, `MigrationTestSupport`, `PreMigrationBackupTest`, `BackgroundStyle`, `7. Test plan`, `ContentScreenshotTest`, `IdentityRoomIntegrationTest`, `GroupedSectionTest.kt`, `PftTheme`, `ThemePrefsTest`, `DatabaseStartupTest.kt`, `MoneyToneTest`, `RoomTransactionRunnerTest`, `MoneyColors`, `AuditDaoTest.kt`, `ThemePaletteTest`, `BundledFontTest`, `RoomAppSettingsRepositoryTest.kt`, `PaletteScreenshotTest.kt`, `ManifestHardeningTest`, `applicationprovider`, `MainActivity`?**
  _High betweenness centrality (0.270) - this node is a cross-community bridge._
- **Are the 48 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 48 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _192 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Public interface`, `Collecting and Redacting Sample Messages Guide`, `Automatic capture from SMS (S-1..S-13)`, ``build-logic` — convention plugins`, `CLAUDE.md Development Constitution`, `Layered Modular Architecture (core vs app)`, `MoneyText`, `Personal Finance Tracker PRD`?**
  _High betweenness centrality (0.114) - this node is a cross-community bridge._
- **Are the 37 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 37 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._