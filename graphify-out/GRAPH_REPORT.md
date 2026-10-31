# Graph Report - personal-finance-tracker  (2026-10-31)

## Corpus Check
- 256 files · ~189,683 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 122 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3122 nodes · 8712 edges · 149 communities (80 shown, 69 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 1221 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `bd828983`
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
- InMemoryEntityStore
- CollapsingHeaderScaffold.kt
- HeaderGlowScaffoldTest.kt
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- MotionSection.kt
- PftTypographyTest
- TestClock
- PftShapesTest.kt
- CategoryGlyph
- DatabaseFactory
- Project README
- UserProfile
- PftMotion.kt
- CategoryGlyphSamples.kt
- ThemePalette
- CLAUDE.md Development Constitution
- GlyphSourceTest
- HostedComposeRule
- WindowBackgroundTest.kt
- HeaderIconButtonTest.kt
- AuditEntryEntity
- RoomAuditColumnsTest
- LedgerEntity
- PaiseParserTest.kt
- DbKeyManagerTest.kt
- MoneyText.kt
- Personal Finance Tracker PRD
- AuditEntry
- redact_text
- PftSymbolTest
- MoneyTextTest.kt
- SampleThing.kt
- Pseudo
- AppSettingEntity
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- Collecting and Redacting Sample Messages Guide
- BottomActionBarTest.kt
- DbKeyUnavailable
- _Text
- LedgerError
- import_symbols.py
- RoomEntityRepository
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
- CategoryGlyph.kt
- PreMigrationBackupTest
- GroupedSectionColorsTest
- ColorSchemeTestSupport.kt
- MoneyTextSamples.kt
- Slice A — Foundation (`feat/m1a-foundation`)
- UserId
- GroupedSectionTest.kt
- PftTheme
- DbKeyManager
- ThemePrefsTest
- BottomActionBar.kt
- PeriodStepper
- DatabaseStartupTest.kt
- MoneyToneTest
- Migrations.kt
- KeyUnavailableScreenTest.kt
- HeaderStyle
- PaletteSamples.kt
- .fromIconKey
- parse_svg
- BackgroundStyle
- BottomActionBarSurfaceTest.kt
- ImportRunTest
- PaiseFormatTest.kt
- AndroidKeystoreKek.kt
- ThemePaletteTest
- MainActivityStartupTest.kt
- Layered Modular Architecture (core vs app)
- AppSetting
- MoneyColors
- 13. Deviations from the plan, by slice
- CatalogSection
- PaletteScreenshotTest.kt
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- AuditedWriterTest.kt
- PftDatabase
- ManifestHardeningTest
- modifier
- CollapsingHeaderScaffold
- Plan: M1a — Core ledger (manual-entry household tracker)
- PftSurfacesTest
- PaiseParser
- StatusChipTest.kt
- EmptyStateTest.kt
- 7. Test plan
- DbKeyManagerWiringTest.kt
- PaletteSwatchesTest.kt
- Auditable
- WindowFlashToneTest
- BaseDao
- Uuid7Test.kt
- Plan: M1a premium look (DS6): curated palettes, tonal surfaces, header glow, icon-only header actions, type pairing, bundled sans
- StatusChipSamples.kt
- text
- Security, privacy and integrity requirements (SEC/INT)
- CategoryPaletteTest
- NameBasedIdTest.kt
- MoneySize

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 155 edges
2. `ThemePrefs` - 129 edges
3. `PftSymbol` - 120 edges
4. `Paise` - 90 edges
5. `CollapsingHeaderScaffoldTest` - 82 edges
6. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 71 edges
7. `How to run and test` - 70 edges
8. `HostedComposeRule` - 62 edges
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

## Communities (149 total, 69 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "How to run and test"
Cohesion: 0.05
Nodes (10): How to run and test, AuditDaoTest, RoomEntityRepositoryTest, FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomTransactionRunnerTest (+2 more)

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
Cohesion: 0.16
Nodes (5): GroupedRow, GroupedSectionColors, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags

### Community 9 - "ThemePrefs"
Cohesion: 0.10
Nodes (6): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest, 6.1 Slices

### Community 10 - "Paise"
Cohesion: 0.15
Nodes (3): MoneyTextTest, Spec, Paise

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.21
Nodes (6): Case, ColorContrastTest, Theme, DARK, LIGHT, sweptTones()

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.10
Nodes (34): `:app` — Android application, Configuration, Dependencies, Manifest security posture (SEC-2, SEC-3, SEC-6), Responsibilities and boundaries, directionLabel(), moneySign(), MoneyText() (+26 more)

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
Cohesion: 0.06
Nodes (10): CategoryGlyphSymbolSamples(), DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), CategoryGlyphScreenshotTest, ContentScreenshotTest, HeaderScreenshotTest (+2 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "MotionSection.kt"
Cohesion: 0.10
Nodes (3): AccentCheckSection(), MotionSection(), SpringTrack()

### Community 37 - "PftTypographyTest"
Cohesion: 0.06
Nodes (16): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF (+8 more)

### Community 38 - "TestClock"
Cohesion: 0.09
Nodes (11): Data it owns, EpochMillis, EpochMillisTest, NameBasedId, clockAt(), TestClock, Conventions, Data model (+3 more)

### Community 42 - "Project README"
Cohesion: 0.14
Nodes (13): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer (+5 more)

### Community 43 - "UserProfile"
Cohesion: 0.10
Nodes (6): RoomIdentityRepository, RoomIdentityRepositoryTest, Device, IdentityRepository, UserProfile, InMemoryIdentityRepository

### Community 44 - "PftMotion.kt"
Cohesion: 0.09
Nodes (5): iosSpring(), PftMotion, pressScale(), PftMotionTest, 7. Test plan

### Community 45 - "CategoryGlyphSamples.kt"
Cohesion: 0.73
Nodes (3): CategoryGlyphPreview(), CategoryGlyphSamples(), SampleRow()

### Community 46 - "ThemePalette"
Cohesion: 0.12
Nodes (12): colors(), ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, paletteFor(), ThemeMode (+4 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.10
Nodes (24): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+16 more)

### Community 49 - "HostedComposeRule"
Cohesion: 0.24
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 50 - "WindowBackgroundTest.kt"
Cohesion: 0.15
Nodes (4): SyncWindowBackground(), WindowBackgroundTest, 5. Flows, 8. Security considerations

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "LedgerEntity"
Cohesion: 0.06
Nodes (18): LedgerEntity, EntityId, Complete, HasDerived, HasScratch, Kind, LARGE, SMALL (+10 more)

### Community 55 - "PaiseParserTest.kt"
Cohesion: 0.21
Nodes (4): How to run and test, PaiseParserTest, PaiseTest, 7. Test plan

### Community 57 - "MoneyText.kt"
Cohesion: 0.17
Nodes (7): atSize(), Figure(), fitHero(), HeroFit, HeroText(), RollingDigits(), toneColor()

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.08
Nodes (32): Personal Finance Tracker PRD, Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Automatic capture from Email (E-0..E-5) (+24 more)

### Community 59 - "AuditEntry"
Cohesion: 0.08
Nodes (22): AuditEntryMapper, AuditEntryMapperTest, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), FieldChange, AuditAction, ARCHIVE, CREATE, DELETE (+14 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 61 - "PftSymbolTest"
Cohesion: 0.15
Nodes (5): PftSymbolTest, Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 62 - "MoneyTextTest.kt"
Cohesion: 0.11
Nodes (5): layoutResult(), shownText(), assertShownInFull(), isShownInFull(), layout()

### Community 63 - "SampleThing.kt"
Cohesion: 0.21
Nodes (6): SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 66 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.07
Nodes (29): RoomTransactionRunner, RoomTestKit, Behaviours worth knowing, Configuration, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, AuditDiff (+21 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 69 - "DbKeyUnavailable"
Cohesion: 0.09
Nodes (21): Purpose, `security` and the encrypted open (A4b), DatabaseStartup, KeyUnavailable, PftApplication, DbKeyUnavailable, Reason, KEK_MISSING (+13 more)

### Community 72 - "LedgerError"
Cohesion: 0.07
Nodes (21): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, SettingKeys, AlreadyInitialised, AmountNotPositive (+13 more)

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 76 - "DesignCatalog.kt"
Cohesion: 0.16
Nodes (6): ChoiceRow(), DesignCatalog(), SectionContent(), DevContent(), TokenSheet(), TokenSheetPreview()

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.11
Nodes (19): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6) (+11 more)

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
Cohesion: 0.10
Nodes (4): AuditTriggers, MigrationTestSupport, MigrationTest, Audit triggers

### Community 86 - "CategoryGlyph.kt"
Cohesion: 0.13
Nodes (4): fixedSp(), GlyphSize, Chip, Hero

### Community 89 - "ColorSchemeTestSupport.kt"
Cohesion: 0.17
Nodes (8): AccentHueTest, contrast(), greyAtTone(), hueDistance(), labHue(), roles(), surfaceRoles(), washTop()

### Community 90 - "MoneyTextSamples.kt"
Cohesion: 0.67
Nodes (4): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples()

### Community 91 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.15
Nodes (13): PreMigrationBackup, PreMigrationBackupFailed, factory(), Corruption handling, Persistence and security, Pre-migration backup (INT-5), SQLCipher, The key and the KEK (+5 more)

### Community 92 - "UserId"
Cohesion: 0.11
Nodes (13): `data` (Room persistence, A4a), MetaColumns, MetaMapper, MetaMapperTest, AuditedTable, AuditedTables, TestDatabase, EntityMeta (+5 more)

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 98 - "PeriodStepper"
Cohesion: 0.33
Nodes (10): CollapsingHeader(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderTitle(), LowerBand(), PeriodStepper() (+2 more)

### Community 103 - "HeaderStyle"
Cohesion: 0.67
Nodes (3): HeaderStyle, Dashboard, Standard

### Community 104 - "PaletteSamples.kt"
Cohesion: 0.42
Nodes (4): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 105 - ".fromIconKey"
Cohesion: 0.25
Nodes (5): Emoji, GlyphSource, isSymbolKey(), Letter, Symbol

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 107 - "BackgroundStyle"
Cohesion: 0.17
Nodes (10): CategoryColors, CategoryPalette, pair(), BackgroundStyle, FLAT, TONAL, PftSurfaces, 6.2 PR breakdown (incremental — required) (+2 more)

### Community 114 - "MainActivityStartupTest.kt"
Cohesion: 0.08
Nodes (4): MainActivityStartupTest, BundledFontTest, BundledSymbolsLicenceTest, Third-party assets

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 116 - "AppSetting"
Cohesion: 0.13
Nodes (7): RoomAuditLog, RoomAppSettingsRepository, RoomAppSettingsRepositoryTest, AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository, The core/app boundary

### Community 117 - "MoneyColors"
Cohesion: 0.27
Nodes (5): MoneyColors, PftShapes, PftSpacing, Theming, 3.3 Tokens

### Community 118 - "13. Deviations from the plan, by slice"
Cohesion: 0.14
Nodes (11): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, ThemeLiteralScanTest, Violation (+3 more)

### Community 119 - "CatalogSection"
Cohesion: 0.15
Nodes (13): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, GLYPHS, MONEY (+5 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.16
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), open(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "AuditedWriterTest.kt"
Cohesion: 0.12
Nodes (8): AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository, InMemoryLedgerStore

### Community 127 - "CollapsingHeaderScaffold"
Cohesion: 0.10
Nodes (40): Debug and release source sets, Public interface, `ui/common` (shared components, DS3, DS4 and DS5), Window theme, MainActivity, BottomActionBar(), CollapsingHeaderScaffold(), GroupedSection() (+32 more)

### Community 128 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.17
Nodes (10): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5. Flows, 8. Security considerations (+2 more)

### Community 131 - "PaiseParser"
Cohesion: 0.16
Nodes (8): Configuration, `:core:money` — integer-paise money, Data it owns, Parsing grammar, Public interface, Purpose, Responsibilities & boundaries, PaiseParser

### Community 133 - "EmptyStateTest.kt"
Cohesion: 0.23
Nodes (3): EmptyState(), EmptyStateTest, `EmptyState`

### Community 134 - "7. Test plan"
Cohesion: 0.13
Nodes (10): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 1. Requirement, 3.4 Header glow (UI-13, DS6d) (+2 more)

### Community 137 - "Auditable"
Cohesion: 0.24
Nodes (3): Auditable, AuditDiffTest, Thing

### Community 142 - "Plan: M1a premium look (DS6): curated palettes, tonal surfaces, header glow, icon-only header actions, type pairing, bundled sans"
Cohesion: 0.14
Nodes (11): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions (+3 more)

### Community 144 - "text"
Cohesion: 0.13
Nodes (4): EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), GroupedSectionPreview()

### Community 145 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 148 - "MoneySize"
Cohesion: 0.50
Nodes (3): MoneySize, Hero, Large

## Knowledge Gaps
- **194 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+189 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 581 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **69 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `PftSurfacesTest`, `StatusChipTest.kt`, `EmptyStateTest.kt`, `WindowThemeTest`, `DbKeyManagerWiringTest.kt`, `ThemePrefs`, `Paise`, `WindowFlashToneTest`, `ColorContrastTest`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `CategoryPaletteTest`, `PftThemeModeTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `Previews, the token sheet and screenshot goldens`, `HeaderGlowScaffoldTest.kt`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `PftShapesTest.kt`, `CategoryGlyph`, `UserProfile`, `PftMotion.kt`, `ThemePalette`, `GlyphSourceTest`, `WindowBackgroundTest.kt`, `RoomAuditColumnsTest`, `DbKeyManagerTest.kt`, `AuditEntry`, `PftSymbolTest`, `SampleThing.kt`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `BottomActionBarTest.kt`, `PftSpacingTest`, `NavTransitionTest.kt`, `AuditTriggersTest`, `expandedHeaderHeight`, `MigrationTestSupport`, `PreMigrationBackupTest`, `GroupedSectionColorsTest`, `ColorSchemeTestSupport.kt`, `UserId`, `GroupedSectionTest.kt`, `PftTheme`, `ThemePrefsTest`, `DatabaseStartupTest.kt`, `MoneyToneTest`, `KeyUnavailableScreenTest.kt`, `BottomActionBarSurfaceTest.kt`, `ThemePaletteTest`, `MainActivityStartupTest.kt`, `AppSetting`, `PaletteScreenshotTest.kt`, `PftDatabase`, `ManifestHardeningTest`, `CollapsingHeaderScaffold`?**
  _High betweenness centrality (0.251) - this node is a cross-community bridge._
- **Are the 47 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 47 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _194 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `PftTheme()` connect `PftTheme` to `StatusChipTest.kt`, `EmptyStateTest.kt`, `7. Test plan`, `GroupedSection.kt`, `ThemePrefs`, `Paise`, `PaletteSwatchesTest.kt`, `StatusChipSamples.kt`, `text`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `PftThemeModeTest`, `CollapsingHeaderScaffoldTest`, `Previews, the token sheet and screenshot goldens`, `CollapsingHeaderScaffold.kt`, `HeaderGlowScaffoldTest.kt`, `MotionSection.kt`, `PftTypographyTest`, `PftShapesTest.kt`, `CategoryGlyph`, `Project README`, `PftMotion.kt`, `CategoryGlyphSamples.kt`, `WindowBackgroundTest.kt`, `HeaderIconButtonTest.kt`, `MoneyText.kt`, `MoneyTextTest.kt`, `BottomActionBarTest.kt`, `DbKeyUnavailable`, `DesignCatalog.kt`, `PftSpacingTest`, `Plan: M1a design system and visual direction (lands before A5)`, `CategoryGlyph.kt`, `MoneyTextSamples.kt`, `GroupedSectionTest.kt`, `BottomActionBar.kt`, `KeyUnavailableScreenTest.kt`, `PaletteSamples.kt`, `BackgroundStyle`, `BottomActionBarSurfaceTest.kt`, `MoneyColors`, `13. Deviations from the plan, by slice`, `PaletteScreenshotTest.kt`, `modifier`, `CollapsingHeaderScaffold`?**
  _High betweenness centrality (0.100) - this node is a cross-community bridge._
- **Are the 37 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 37 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._