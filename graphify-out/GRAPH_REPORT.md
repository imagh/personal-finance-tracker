# Graph Report - personal-finance-tracker  (2026-10-31)

## Corpus Check
- 252 files · ~179,941 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 122 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 2997 nodes · 8214 edges · 138 communities (81 shown, 57 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 1152 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `ecea536e`
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
- ThemePrefs
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- MoneyTone
- Catalog.kt
- detekt.yml
- PftThemeModeTest
- PftSymbol
- CollapsingHeaderScaffoldTest
- HeaderIconButtonTest.kt
- TokenSheetScreenshotTest.kt
- InMemoryEntityStore
- CollapsingHeaderScaffold.kt
- Auditable
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- CatalogSection
- PftTypographyTest
- RoomAuditedWriterIntegrationTest.kt
- PftShapesTest.kt
- CategoryGlyph
- MoneyText
- Project README
- UserProfile
- PftMotion.kt
- CategoryGlyphScreenshotTest
- PftTypography.kt
- CLAUDE.md Development Constitution
- Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...)
- HostedComposeRule
- WindowBackgroundTest.kt
- KeyUnavailableScreenTest.kt
- assertequals
- RoomAuditColumnsTest
- EntityMeta
- MainActivity
- DbKeyManagerTest.kt
- MoneyText.kt
- Personal Finance Tracker PRD
- UserId
- redact_text
- PftSymbolTest
- MoneyTextTest.kt
- SampleThing.kt
- Pseudo
- EmptyStateSamples.kt
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- Collecting and Redacting Sample Messages Guide
- AuditEntryEntity
- DatabaseFactory
- _Text
- Slice A — Foundation (`feat/m1a-foundation`)
- import_symbols.py
- SampleId
- HeaderPolicy
- DesignCatalog.kt
- GlyphSourceTest
- NavTransitionTest.kt
- AuditTriggersTest
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- HeaderSlot
- HeaderPhase
- MigrationTestSupport
- AppSettingEntity
- PreMigrationBackupTest
- 6.1 Slices
- 7. Test plan
- ContentScreenshotTest
- PreMigrationBackup
- RoomEntityRepository
- GroupedSectionTest.kt
- PftTheme
- DbKeyManager
- PressScaleTest.kt
- BottomActionBar.kt
- CollapsingHeaderScaffold
- DatabaseStartupTest.kt
- Paise
- Migrations.kt
- BottomActionBarSurfaceTest.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- PaletteSamples.kt
- PaiseParserTest.kt
- parse_svg
- MoneyColors
- HeaderStyle
- MotionSection.kt
- ImportRunTest
- MetaMapperTest
- AndroidKeystoreKek.kt
- DbKeyUnavailable
- BundledFontTest
- Layered Modular Architecture (core vs app)
- Loans and EMIs (LN-1..LN-5)
- PftSpacing
- PaletteColors
- BaseDao
- PaletteScreenshotTest.kt
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- DisplayFont
- How to run and test
- ManifestHardeningTest
- asserttrue
- 13. Deviations from the plan, by slice
- Plan: M1a — Core ledger (manual-entry household tracker)
- PftSurfacesTest
- PaiseParser
- StatusChip
- ThemePrefsTest
- 3. Design & decision
- Architecture overview
- Immutable Audit Trail
- PaiseFormatTest.kt

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 147 edges
2. `PftSymbol` - 120 edges
3. `ThemePrefs` - 115 edges
4. `Paise` - 90 edges
5. `CollapsingHeaderScaffoldTest` - 81 edges
6. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 72 edges
7. `How to run and test` - 70 edges
8. `HostedComposeRule` - 58 edges
9. `UserId` - 57 edges
10. `runSuspending()` - 54 edges

## Surprising Connections (you probably didn't know these)
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `3.4 Header glow (UI-13, DS6d)` --references--> `HeaderPolicy`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/CollapsingHeaderScaffold.kt
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

## Communities (138 total, 57 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "runSuspending"
Cohesion: 0.06
Nodes (7): FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAppSettingsRepositoryTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomTransactionRunnerTest, runSuspending()

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
Cohesion: 0.10
Nodes (13): GroupedRow, GroupedSection(), GroupedSectionColors, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, GroupedSectionPreview(), GroupedSectionSamples() (+5 more)

### Community 9 - "ThemePrefs"
Cohesion: 0.10
Nodes (6): dynamicPaletteColors(), resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest, Surfaces and window colour (DS6c, UI-12, UI-18)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.08
Nodes (14): AccentHueTest, Case, ColorContrastTest, Theme, DARK, LIGHT, greyAtTone(), hueDistance() (+6 more)

### Community 16 - "MoneyTone"
Cohesion: 0.09
Nodes (28): MoneySize, Hero, Large, Balance, Budget, BudgetState, AT_THRESHOLD, OVER (+20 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.07
Nodes (4): BottomActionBar(), BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "HeaderIconButtonTest.kt"
Cohesion: 0.05
Nodes (25): HeaderIconButton(), DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderIconButtonTest, HeaderScreenshotTest, PftSpacingTest (+17 more)

### Community 33 - "Auditable"
Cohesion: 0.13
Nodes (11): `data` (Room persistence, A4a), RoomAppSettingsRepository, AuditedTable, AuditedTables, Auditable, AppSetting, AppSettingsRepository, SettingKeys (+3 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.16
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "CatalogSection"
Cohesion: 0.15
Nodes (13): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, GLYPHS, MONEY (+5 more)

### Community 38 - "RoomAuditedWriterIntegrationTest.kt"
Cohesion: 0.09
Nodes (11): Data it owns, EpochMillis, EpochMillisTest, TestDatabase, NameBasedId, TestClock, Conventions, Data model (+3 more)

### Community 40 - "CategoryGlyph"
Cohesion: 0.05
Nodes (7): CategoryGlyph(), EmptyState(), MainActivityStartupTest, BottomActionBarTest, CategoryGlyphTest, EmptyStateTest, PaletteSwatchesTest

### Community 41 - "MoneyText"
Cohesion: 0.12
Nodes (17): Dependencies, `ui/common` (shared components, DS3, DS4 and DS5), moneySign(), MoneyText(), Row, Configuration, `:core:money` — integer-paise money, Data it owns (+9 more)

### Community 42 - "Project README"
Cohesion: 0.23
Nodes (7): Code Structure Map, core/shared Shared Packages, Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen, What it does for you, Project README

### Community 43 - "UserProfile"
Cohesion: 0.10
Nodes (6): RoomIdentityRepository, RoomIdentityRepositoryTest, Device, IdentityRepository, UserProfile, InMemoryIdentityRepository

### Community 44 - "PftMotion.kt"
Cohesion: 0.10
Nodes (4): PftMotion, pressScale(), Principles, 3.7 Component inventory (UI-9)

### Community 45 - "CategoryGlyphScreenshotTest"
Cohesion: 0.23
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 46 - "PftTypography.kt"
Cohesion: 0.15
Nodes (6): displayFamily(), pftMaterialTypography(), pftType(), PftTypography, Theming, 2. Current state

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...)"
Cohesion: 0.13
Nodes (6): Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), InMemoryAuditLog, InMemoryLedgerStore, AuditFieldsCheck, haveCompleteAuditFields(), shouldHaveCompleteAuditFields()

### Community 49 - "HostedComposeRule"
Cohesion: 0.24
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 52 - "assertequals"
Cohesion: 0.11
Nodes (8): colors(), ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, ChipColorsTest, CategoryPaletteTest

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.06
Nodes (24): EntityMeta, LedgerEntity, EntityId, Fixture, Leaky, LeakyRepository, Note, NoteId (+16 more)

### Community 55 - "MainActivity"
Cohesion: 0.21
Nodes (13): `:app` — Android application, Configuration, Debug and release source sets, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose, Responsibilities and boundaries, `security` and the encrypted open (A4b) (+5 more)

### Community 57 - "MoneyText.kt"
Cohesion: 0.16
Nodes (8): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), RollingDigits(), toneColor()

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.09
Nodes (28): Personal Finance Tracker PRD, Audit trail (INT-2), Balance observations (L-10), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5) (+20 more)

### Community 59 - "UserId"
Cohesion: 0.07
Nodes (28): AuditEntryMapper, RoomAuditLog, AuditDaoTest, AuditEntryMapperTest, FieldChange, AuditAction, ARCHIVE, CREATE (+20 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 61 - "PftSymbolTest"
Cohesion: 0.17
Nodes (5): PftSymbolTest, Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 62 - "MoneyTextTest.kt"
Cohesion: 0.12
Nodes (5): layoutResult(), shownText(), assertShownInFull(), isShownInFull(), layout()

### Community 63 - "SampleThing.kt"
Cohesion: 0.21
Nodes (6): SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 65 - "EmptyStateSamples.kt"
Cohesion: 0.73
Nodes (3): EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder()

### Community 66 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.06
Nodes (29): RoomTransactionRunner, RoomTestKit, Behaviours worth knowing, Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How an entity step composes the blocks (+21 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 69 - "DatabaseFactory"
Cohesion: 0.19
Nodes (6): DatabaseFactory, DatabaseStartup, KeyUnavailable, Ready, PftApplication, open()

### Community 72 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.06
Nodes (23): How to run and test, AlreadyInitialised, AmountNotPositive, AmountTooLarge, Archived, BlankName, Deleted, InvalidSettingKey (+15 more)

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 75 - "HeaderPolicy"
Cohesion: 0.22
Nodes (3): HeaderPolicy, Parts, 5. Flows

### Community 76 - "DesignCatalog.kt"
Cohesion: 0.09
Nodes (8): ChoiceRow(), DesignCatalog(), SectionContent(), DevContent(), StatusChipPreview(), StatusChipSamples(), TokenSheet(), TokenSheetPreview()

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
Nodes (4): AuditTriggers, MigrationTestSupport, MigrationTest, The audit trail

### Community 87 - "PreMigrationBackupTest"
Cohesion: 0.06
Nodes (3): PftDatabaseTest, PreMigrationBackupTest, DbKeyManagerWiringTest

### Community 88 - "6.1 Slices"
Cohesion: 0.18
Nodes (9): BackgroundStyle, FLAT, TONAL, PftSurfaces, Case, GroupedSectionColorsTest, 6.1 Slices, 6.2 PR breakdown (incremental — required) (+1 more)

### Community 89 - "7. Test plan"
Cohesion: 0.30
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 90 - "ContentScreenshotTest"
Cohesion: 0.22
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 91 - "PreMigrationBackup"
Cohesion: 0.23
Nodes (3): PreMigrationBackup, factory(), Handoff

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 98 - "CollapsingHeaderScaffold"
Cohesion: 0.29
Nodes (13): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderTitle(), LowerBand() (+5 more)

### Community 100 - "Paise"
Cohesion: 0.20
Nodes (3): MoneyToneTest, Paise, Slice D — Budgets (`feat/m1a-budgets`)

### Community 103 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.29
Nodes (6): Emoji, GlyphSource, isSymbolKey(), Letter, Symbol, 6.1 Design-system slice: PRs into `feat/m1a-foundation`

### Community 104 - "PaletteSamples.kt"
Cohesion: 0.42
Nodes (4): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 105 - "PaiseParserTest.kt"
Cohesion: 0.20
Nodes (4): How to run and test, PaiseParserTest, PaiseTest, 7. Test plan

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 107 - "MoneyColors"
Cohesion: 0.19
Nodes (5): ChipColors, CategoryColors, CategoryPalette, pair(), MoneyColors

### Community 108 - "HeaderStyle"
Cohesion: 0.67
Nodes (3): HeaderStyle, Dashboard, Standard

### Community 109 - "MotionSection.kt"
Cohesion: 0.06
Nodes (8): AccentCheckSection(), MotionSection(), SpringTrack(), fixedSp(), GlyphSize, Chip, Hero, DevContent()

### Community 111 - "MetaMapperTest"
Cohesion: 0.31
Nodes (3): MetaColumns, MetaMapper, MetaMapperTest

### Community 113 - "DbKeyUnavailable"
Cohesion: 0.07
Nodes (27): PreMigrationBackupFailed, DbKeyUnavailable, Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED (+19 more)

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 116 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5), M1a Core ledger (+2 more)

### Community 117 - "PftSpacing"
Cohesion: 0.20
Nodes (9): PftSpacing, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens, 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins") (+1 more)

### Community 118 - "PaletteColors"
Cohesion: 0.24
Nodes (6): AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, Colour

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.14
Nodes (5): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "DisplayFont"
Cohesion: 0.18
Nodes (10): DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, paletteFor(), ThemeMode, DARK, LIGHT (+2 more)

### Community 127 - "13. Deviations from the plan, by slice"
Cohesion: 0.14
Nodes (12): toneOf(), ThemeLiteralScanTest, Violation, WindowFlashToneTest, 11. Rollout & verification, 12. Open questions, 13. Deviations from the plan, by slice, 1. Requirement (+4 more)

### Community 128 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.17
Nodes (10): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5. Flows, 8. Security considerations (+2 more)

### Community 134 - "3. Design & decision"
Cohesion: 0.29
Nodes (7): 3.1 Palettes (DS6a, DS6b), 3.4 Header glow (UI-13, DS6d), 3.5 Icon-only header actions (UI-14, DS6e; needs DS3), 3.6 Type pairing and optical size per role (UI-15, DS6g), 3.7 Bundled sans fonts (UI-16, UI-17; DS6f spike done, then DS6h, DS6i), 3.8 Abstraction bar, 3. Design & decision

### Community 135 - "Architecture overview"
Cohesion: 0.33
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

## Knowledge Gaps
- **192 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+187 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 581 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **57 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `runSuspending`, `PftSurfacesTest`, `StatusChip`, `ThemePrefsTest`, `WindowThemeTest`, `ThemePrefs`, `MoneyTextTest`, `ColorContrastTest`, `PftThemeModeTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `HeaderIconButtonTest.kt`, `TokenSheetScreenshotTest.kt`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `PftShapesTest.kt`, `CategoryGlyph`, `MoneyText`, `UserProfile`, `CategoryGlyphScreenshotTest`, `WindowBackgroundTest.kt`, `KeyUnavailableScreenTest.kt`, `assertequals`, `RoomAuditColumnsTest`, `MainActivity`, `DbKeyManagerTest.kt`, `UserId`, `PftSymbolTest`, `SampleThing.kt`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `SampleId`, `GlyphSourceTest`, `NavTransitionTest.kt`, `AuditTriggersTest`, `expandedHeaderHeight`, `MigrationTestSupport`, `PreMigrationBackupTest`, `6.1 Slices`, `7. Test plan`, `ContentScreenshotTest`, `GroupedSectionTest.kt`, `PftTheme`, `PressScaleTest.kt`, `DatabaseStartupTest.kt`, `Paise`, `BottomActionBarSurfaceTest.kt`, `MetaMapperTest`, `BundledFontTest`, `PaletteScreenshotTest.kt`, `ManifestHardeningTest`, `asserttrue`, `13. Deviations from the plan, by slice`?**
  _High betweenness centrality (0.259) - this node is a cross-community bridge._
- **Are the 46 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 46 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _192 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `Collecting and Redacting Sample Messages Guide`, `Automatic capture from SMS (S-1..S-13)`, `MoneyText`, ``build-logic` — convention plugins`, `CLAUDE.md Development Constitution`, `Layered Modular Architecture (core vs app)`, `Personal Finance Tracker PRD`?**
  _High betweenness centrality (0.107) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `PftSymbol` (e.g. with `How to run and test` and ``ui/common` (shared components, DS3, DS4 and DS5)`) actually correct?**
  _`PftSymbol` has 13 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._