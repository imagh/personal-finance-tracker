# Graph Report - personal-finance-tracker  (2026-11-01)

## Corpus Check
- 266 files · ~206,832 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3274 nodes · 9250 edges · 138 communities (80 shown, 58 thin omitted)
- Extraction: 84% EXTRACTED · 16% INFERRED · 0% AMBIGUOUS · INFERRED: 1437 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `6514f7f1`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- How to run and test
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- Slice A — Foundation (`feat/m1a-foundation`)
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
- SampleId
- LedgerError
- InMemoryEntityStore
- CollapsingHeaderScaffold.kt
- HeaderGlowScaffoldTest.kt
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- PftTypographyTest
- RoomAppSettingsRepository
- PftShapesTest.kt
- CategoryGlyph
- HeaderTypePairingTest.kt
- Project README
- UserId
- PftMotion.kt
- CategoryGlyphScreenshotTest
- MoneyColors
- CLAUDE.md Development Constitution
- 13. Deviations from the plan, by slice
- HostedComposeRule
- LogEvent
- AuditEntryMapperTest
- AuditEntryEntity
- RoomAuditColumnsTest
- EntityMeta
- ContentScreenshotTest
- DbKeyManagerTest.kt
- MoneyText.kt
- Personal Finance Tracker PRD
- DeviceId
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
- 3. Design & decision
- import_symbols.py
- HeaderPolicy
- DesignCatalog.kt
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
- asserttrue
- PreMigrationBackup
- TestClock
- GroupedSectionTest.kt
- PftTheme
- DbKeyManager
- ThemePrefsTest
- BottomActionBar
- CollapsingHeaderScaffold
- DatabaseStartupTest.kt
- MoneyToneTest
- Migrations.kt
- KeyUnavailableScreenTest.kt
- CatalogSection
- PaletteSamples.kt
- SafeLogTest
- parse_svg
- ThemePalette
- ThemeLiteralScanTest
- DesignCatalogTest.kt
- ImportRunTest
- assertequals
- AndroidKeystoreKek.kt
- Layered Modular Architecture (core vs app)
- RoomEntityRepository
- PaletteColors
- PaletteScreenshotTest.kt
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- AuditedWriterTest.kt
- WindowThemeTest
- MainActivity
- 7. Test plan
- Plan: M1a — Core ledger (manual-entry household tracker)
- CategoryPaletteTest
- .fromIconKey
- PaiseParser
- StatusChip
- EmptyState
- DbKeyManagerWiringTest.kt
- ManifestHardeningTest
- PftDatabase
- PftSpacingTest
- onnodewithtag
- FontBudgetTest
- HeaderStyle
- Security, privacy and integrity requirements (SEC/INT)
- MoneySize
- LoggingScanTest
- BaseDao
- 6. Implementation steps (TDD-ordered)

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 158 edges
2. `ThemePrefs` - 131 edges
3. `PftSymbol` - 123 edges
4. `Paise` - 90 edges
5. `CollapsingHeaderScaffoldTest` - 82 edges
6. `How to run and test` - 74 edges
7. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 71 edges
8. `HostedComposeRule` - 69 edges
9. `runSuspending()` - 57 edges
10. `UserId` - 57 edges

## Surprising Connections (you probably didn't know these)
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `3.5 Fonts: bundling, licensing, custom import` --references--> `SafeLog`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/log/SafeLog.kt
- `13. Deviations from the plan, by slice` --references--> `asHeroTop()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderGlow.kt
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

## Communities (138 total, 58 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "How to run and test"
Cohesion: 0.06
Nodes (8): How to run and test, FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAppSettingsRepositoryTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomTransactionRunnerTest, runSuspending()

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.06
Nodes (18): RoomAuditLog, RoomTransactionRunner, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, AuditDiffTest (+10 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "GroupedSection.kt"
Cohesion: 0.12
Nodes (8): GroupedRow, GroupedSectionColors, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, GroupedSectionPreview(), GroupedSectionSamples(), Surfaces and window colour (DS6c, UI-12, UI-18)

### Community 9 - "ThemePrefs"
Cohesion: 0.10
Nodes (5): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest

### Community 10 - "Paise"
Cohesion: 0.14
Nodes (3): MoneyTextTest, Spec, Paise

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.08
Nodes (16): AccentHueTest, Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone() (+8 more)

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.12
Nodes (33): Dependencies, GroupedSection(), directionLabel(), moneySign(), MoneyText(), Balance, Budget, BudgetState (+25 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 30 - "LedgerError"
Cohesion: 0.13
Nodes (26): SafeLog, AlreadyInitialised, AmountNotPositive, AmountTooLarge, Archived, BlankName, Deleted, IdentityCorrupt (+18 more)

### Community 33 - "HeaderGlowScaffoldTest.kt"
Cohesion: 0.06
Nodes (8): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderGlowScaffoldTest, HeaderScreenshotTest, PressScaleTest, Previews, the token sheet and screenshot goldens

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 38 - "RoomAppSettingsRepository"
Cohesion: 0.13
Nodes (10): Data it owns, EpochMillis, RoomAppSettingsRepository, EpochMillisTest, NameBasedId, Conventions, Data model, The audit trail (+2 more)

### Community 40 - "CategoryGlyph"
Cohesion: 0.05
Nodes (14): CategoryGlyph(), fixedSp(), HeaderIconButton(), CategoryColors, CategoryPalette, pair(), GlyphSize, Chip (+6 more)

### Community 42 - "Project README"
Cohesion: 0.14
Nodes (13): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer (+5 more)

### Community 43 - "UserId"
Cohesion: 0.10
Nodes (9): RoomIdentityRepository, RoomIdentityRepositoryTest, AuditedTable, AuditedTables, Device, IdentityRepository, UserProfile, UserId (+1 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.10
Nodes (3): PftMotion, Principles, 11. Rollout & verification

### Community 46 - "MoneyColors"
Cohesion: 0.21
Nodes (3): ChipColors, MoneyColors, ChipColorsTest

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.10
Nodes (24): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+16 more)

### Community 48 - "13. Deviations from the plan, by slice"
Cohesion: 0.08
Nodes (35): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansFamily(), sansWeights() (+27 more)

### Community 49 - "HostedComposeRule"
Cohesion: 0.24
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 50 - "LogEvent"
Cohesion: 0.13
Nodes (16): `log` (safe logging, A5a), DbKeyUnavailable, DbOpened, LogEvent, LogValue, render(), 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required) (+8 more)

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.05
Nodes (25): `data` (Room persistence, A4a), MetaColumns, MetaMapper, MetaMapperTest, EntityMeta, LedgerEntity, EntityId, AuditFieldsCheckTest (+17 more)

### Community 55 - "ContentScreenshotTest"
Cohesion: 0.22
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 57 - "MoneyText.kt"
Cohesion: 0.18
Nodes (7): atSize(), Figure(), fitHero(), HeroFit, HeroText(), RollingDigits(), toneColor()

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.08
Nodes (32): Personal Finance Tracker PRD, Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Automatic capture from Email (E-0..E-5) (+24 more)

### Community 59 - "DeviceId"
Cohesion: 0.07
Nodes (26): AuditDaoTest, TestDatabase, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE (+18 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 63 - "SampleThing.kt"
Cohesion: 0.21
Nodes (6): SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 66 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.07
Nodes (30): RoomTestKit, Behaviours worth knowing, Configuration, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, AuditDiff, AuditedWriter (+22 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 72 - "3. Design & decision"
Cohesion: 0.67
Nodes (3): 3.1 Modules (Gradle), 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3. Design & decision

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 75 - "HeaderPolicy"
Cohesion: 0.15
Nodes (7): HeaderPolicy, Parts, Motion (UI-10), One-hand layout (UI-7), Shapes, Spacing, Tokens

### Community 76 - "DesignCatalog.kt"
Cohesion: 0.09
Nodes (11): ChoiceRow(), DesignCatalog(), SectionContent(), DevContent(), CategoryGlyphPreview(), CategoryGlyphSamples(), SampleRow(), StatusChipPreview() (+3 more)

### Community 78 - "7. Test plan"
Cohesion: 0.30
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
Cohesion: 0.08
Nodes (7): AccentCheckSection(), MotionSection(), SpringTrack(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), DevContent()

### Community 90 - "asserttrue"
Cohesion: 0.05
Nodes (7): isEmojiValue(), GlyphSourceTest, BundledFontTest, BundledSymbolsLicenceTest, PftSymbolTest, The curated list, download()

### Community 91 - "PreMigrationBackup"
Cohesion: 0.13
Nodes (14): PreMigrationBackup, PreMigrationBackupFailed, factory(), Corruption handling, Fail closed, Persistence and security, Pre-migration backup (INT-5), SQLCipher (+6 more)

### Community 92 - "TestClock"
Cohesion: 0.08
Nodes (7): clockAt(), Uuid7Test, TestClock, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 97 - "BottomActionBar"
Cohesion: 0.10
Nodes (13): BottomActionBar(), BottomActionBarPreview(), BottomActionBarSamples(), BottomBarAction, pressScale(), 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold` (+5 more)

### Community 98 - "CollapsingHeaderScaffold"
Cohesion: 0.27
Nodes (14): `ui/common` (shared components, DS3, DS4 and DS5), CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderTitle() (+6 more)

### Community 99 - "DatabaseStartupTest.kt"
Cohesion: 0.11
Nodes (8): DatabaseFactory, DatabaseStartup, KeyUnavailable, Ready, PftApplication, DatabaseStartupTest, Step 6 — `AppContainer` opens once, off the caller's thread (A5b), Step 8 — `PftRoot`, the generalised fail-closed screen and `MainActivity` (A5b)

### Community 102 - "KeyUnavailableScreenTest.kt"
Cohesion: 0.12
Nodes (9): DbKeyUnavailable, Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING (+1 more)

### Community 103 - "CatalogSection"
Cohesion: 0.15
Nodes (13): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, GLYPHS, MONEY (+5 more)

### Community 104 - "PaletteSamples.kt"
Cohesion: 0.16
Nodes (4): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 107 - "ThemePalette"
Cohesion: 0.11
Nodes (15): PftSpacing, BackgroundStyle, FLAT, TONAL, PftSurfaces, colors(), ThemePalette, AUBERGINE (+7 more)

### Community 108 - "ThemeLiteralScanTest"
Cohesion: 0.28
Nodes (4): SourceScan, Violation, ThemeLiteralScanTest, Step 3 — `SafeLog` writes the line, and nothing else can log (A5a)

### Community 109 - "DesignCatalogTest.kt"
Cohesion: 0.10
Nodes (3): assertShownInFull(), isShownInFull(), layout()

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 118 - "PaletteColors"
Cohesion: 0.10
Nodes (9): AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, PftSurfacesTest, Pins, ThemePaletteTest (+1 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.17
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), open(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "AuditedWriterTest.kt"
Cohesion: 0.12
Nodes (9): Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryAuditLog, InMemoryEntityRepository (+1 more)

### Community 125 - "WindowThemeTest"
Cohesion: 0.10
Nodes (3): SyncWindowBackground(), WindowBackgroundTest, WindowThemeTest

### Community 126 - "MainActivity"
Cohesion: 0.13
Nodes (18): `:app` — Android application, Configuration, Debug and release source sets, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose, Responsibilities and boundaries, `security` and the encrypted open (A4b) (+10 more)

### Community 127 - "7. Test plan"
Cohesion: 0.12
Nodes (10): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, 1. Requirement, 3.4 Header glow (UI-13, DS6d) (+2 more)

### Community 128 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.10
Nodes (15): Appearance: colour palettes, dynamic colour, background and type, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you, 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions (+7 more)

### Community 130 - ".fromIconKey"
Cohesion: 0.27
Nodes (5): Emoji, GlyphSource, isSymbolKey(), Letter, Symbol

### Community 131 - "PaiseParser"
Cohesion: 0.16
Nodes (8): Configuration, `:core:money` — integer-paise money, Data it owns, Parsing grammar, Public interface, Purpose, Responsibilities & boundaries, PaiseParser

### Community 132 - "StatusChip"
Cohesion: 0.15
Nodes (10): StatusChip(), StatusChipTest, Adding a token, Build-time trust decisions, Content primitives (DS5), Design system, `EmptyState`, `GroupedSection` (+2 more)

### Community 144 - "FontBudgetTest"
Cohesion: 0.21
Nodes (6): FontBudgetTest, 10. Risks & mitigations, 11. Rollout & verification, 4. Data model & schema, 9. Documentation updates (all required surfaces), Plan: M1a premium look (DS6): curated palettes, tonal surfaces, header glow, icon-only header actions, type pairing, bundled sans

### Community 145 - "HeaderStyle"
Cohesion: 0.67
Nodes (3): HeaderStyle, Dashboard, Standard

### Community 146 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 148 - "MoneySize"
Cohesion: 0.50
Nodes (3): MoneySize, Hero, Large

### Community 153 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.29
Nodes (7): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice I — Cashew import (`feat/m1a-cashew-import`): tracking stub

## Knowledge Gaps
- **196 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+191 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 587 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **58 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `CategoryPaletteTest`, `StatusChip`, `EmptyState`, `DbKeyManagerWiringTest.kt`, `ManifestHardeningTest`, `PftDatabase`, `Paise`, `ThemePrefs`, `PftSpacingTest`, `ColorContrastTest`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `LoggingScanTest`, `PftThemeModeTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `SampleId`, `HeaderGlowScaffoldTest.kt`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `PftShapesTest.kt`, `CategoryGlyph`, `UserId`, `CategoryGlyphScreenshotTest`, `MoneyColors`, `AuditEntryMapperTest`, `RoomAuditColumnsTest`, `EntityMeta`, `ContentScreenshotTest`, `DbKeyManagerTest.kt`, `DeviceId`, `NavTransitionTest.kt`, `SampleThing.kt`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `TokenSheetScreenshotTest.kt`, `MainActivityStartupTest.kt`, `7. Test plan`, `AuditTriggersTest`, `expandedHeaderHeight`, `MigrationTestSupport`, `PreMigrationBackupTest`, `asserttrue`, `GroupedSectionTest.kt`, `PftTheme`, `ThemePrefsTest`, `DatabaseStartupTest.kt`, `MoneyToneTest`, `KeyUnavailableScreenTest.kt`, `SafeLogTest`, `ThemePalette`, `assertequals`, `PaletteColors`, `PaletteScreenshotTest.kt`, `WindowThemeTest`, `MainActivity`?**
  _High betweenness centrality (0.235) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _196 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Collecting and Redacting Sample Messages Guide`, `PaiseParser`, `Slice A — Foundation (`feat/m1a-foundation`)`, `Automatic capture from SMS (S-1..S-13)`, ``build-logic` — convention plugins`, `CLAUDE.md Development Constitution`, `Layered Modular Architecture (core vs app)`, `Personal Finance Tracker PRD`?**
  _High betweenness centrality (0.086) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._