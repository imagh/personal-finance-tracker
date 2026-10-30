# Graph Report - personal-finance-tracker  (2026-10-30)

## Corpus Check
- 245 files · ~171,482 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 122 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 2893 nodes · 7843 edges · 144 communities (74 shown, 70 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 1082 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b82aaf7f`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- runSuspending
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- ManifestHardeningTest
- hooks
- test_import_symbols.py
- Slice A — Foundation (`feat/m1a-foundation`)
- MoneyTextTest.kt
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- MoneyText
- Catalog.kt
- detekt.yml
- PftThemeModeTest
- PftSymbol
- .show
- Previews, the token sheet and screenshot goldens
- TokenSheetScreenshotTest.kt
- InMemoryEntityStore
- CollapsingHeaderScaffold.kt
- GroupedSection.kt
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- CatalogSection
- PftTypographyTest
- TestClock
- PftShapesTest.kt
- CategoryGlyph
- GlyphSourceTest
- Project README
- UserProfile
- PftMotion.kt
- CategoryGlyphScreenshotTest
- PftTypography.kt
- CLAUDE.md Development Constitution
- CollapsingHeaderScaffoldTest
- HostedComposeRule
- PftTheme
- CollapsingHeaderScaffold
- RoomAuditColumnsTest
- EntityMeta
- .onCreate
- DbKeyManagerTest.kt
- MoneyText.kt
- Personal Finance Tracker PRD
- UserId
- redact_text
- PftSymbolTest
- PftSpacingTest
- SampleThing.kt
- Pseudo
- ThemeResolutionTest
- IdentityService
- Collecting and Redacting Sample Messages Guide
- AuditEntryMapperTest
- _Text
- modifier
- import_symbols.py
- RoomEntityRepositoryTest.kt
- HeaderPolicy
- DesignCatalog.kt
- IdentityRoomIntegrationTest
- ThemePrefs
- PftDatabaseTest
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- PftDatabase
- HeaderSlot
- HeaderPhase
- MigrationTestSupport
- Auditable
- PreMigrationBackupTest
- How to run and test
- WindowThemeTest
- ContentScreenshotTest
- PreMigrationBackup
- AuditTriggersTest
- GroupedSectionTest.kt
- StatusChipTest.kt
- DbKeyManager
- PressScaleTest.kt
- Paise
- 13. Deviations from the plan, by slice
- DatabaseFactory
- RoomTransactionRunnerTest
- Migrations.kt
- DbKeyManagerWiringTest.kt
- assertequals
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- PaletteSamples.kt
- parse_svg
- 7. Test plan
- HeaderStyle
- MotionSection.kt
- ImportRunTest
- BottomActionBar.kt
- AndroidKeystoreKek.kt
- DbKeyUnavailable
- Layered Modular Architecture (core vs app)
- KeyUnavailableScreenTest.kt
- AuditFieldsCheck.kt
- PftSpacing
- RoomEntityRepository
- Loans and EMIs (LN-1..LN-5)
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- PaletteScreenshotTest.kt
- onnodewithtag
- HeaderIconButtonTest.kt
- 6.1 Slices
- PaiseParser
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- ThemeLiteralScanTest
- MoneyTextSamples.kt
- Appearance: colour palettes and dynamic colour
- EmptyStateSamples.kt
- .of
- TokenSheet
- Architecture overview
- 6. Implementation steps (TDD-ordered)
- MoneySize
- Plan: M1a — Core ledger (manual-entry household tracker)
- AuditLog
- Immutable Audit Trail

## God Nodes (most connected - your core abstractions)
1. `PftTheme()` - 138 edges
2. `PftSymbol` - 120 edges
3. `ThemePrefs` - 98 edges
4. `Paise` - 90 edges
5. `CollapsingHeaderScaffoldTest` - 77 edges
6. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 72 edges
7. `How to run and test` - 65 edges
8. `UserId` - 57 edges
9. `runSuspending()` - 54 edges
10. `HostedComposeRule` - 54 edges

## Surprising Connections (you probably didn't know these)
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `3.4 Header glow (UI-13, DS6d)` --references--> `HeaderPolicy`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/CollapsingHeaderScaffold.kt
- `6.2 PR breakdown (incremental — required)` --references--> `HeaderIconButton()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderIconButton.kt
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

## Communities (144 total, 70 thin omitted)

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

### Community 5 - "ManifestHardeningTest"
Cohesion: 0.25
Nodes (3): Manifest security posture (SEC-2, SEC-3, SEC-6), Responsibilities and boundaries, ManifestHardeningTest

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.06
Nodes (23): `:core:ledger` — the ledger foundations, Dependencies, How to run and test, Purpose, AlreadyInitialised, AmountNotPositive, AmountTooLarge, Archived (+15 more)

### Community 9 - "MoneyTextTest.kt"
Cohesion: 0.10
Nodes (5): layoutResult(), shownText(), assertShownInFull(), isShownInFull(), layout()

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.06
Nodes (19): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, AccentHueTest, Case (+11 more)

### Community 16 - "MoneyText"
Cohesion: 0.12
Nodes (27): directionLabel(), moneySign(), MoneyText(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER (+19 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 29 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.19
Nodes (5): DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 33 - "GroupedSection.kt"
Cohesion: 0.12
Nodes (8): GroupedRow, groupedSectionColor(), GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, GroupedSectionPreview(), GroupedSectionSamples(), 3.1 Palettes (DS6a, DS6b)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "CatalogSection"
Cohesion: 0.15
Nodes (13): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, GLYPHS, MONEY (+5 more)

### Community 38 - "TestClock"
Cohesion: 0.07
Nodes (8): NameBasedIdTest, clockAt(), Uuid7Test, TestClock, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest

### Community 40 - "CategoryGlyph"
Cohesion: 0.06
Nodes (15): CategoryGlyph(), fixedSp(), CategoryColors, CategoryPalette, pair(), GlyphSize, Chip, Hero (+7 more)

### Community 42 - "Project README"
Cohesion: 0.23
Nodes (7): Code Structure Map, core/shared Shared Packages, Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen, What it does for you, Project README

### Community 43 - "UserProfile"
Cohesion: 0.10
Nodes (6): RoomIdentityRepository, RoomIdentityRepositoryTest, Device, IdentityRepository, UserProfile, InMemoryIdentityRepository

### Community 46 - "PftTypography.kt"
Cohesion: 0.16
Nodes (9): displayFamily(), pftMaterialTypography(), pftType(), PftTypography, DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT (+1 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 49 - "HostedComposeRule"
Cohesion: 0.22
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 52 - "CollapsingHeaderScaffold"
Cohesion: 0.09
Nodes (40): Debug and release source sets, Public interface, `ui/common` (shared components, DS3, DS4 and DS5), Window theme, label(), MainActivity, BottomActionBar(), BottomBarAction (+32 more)

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.06
Nodes (25): EntityMeta, LedgerEntity, EntityId, AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note (+17 more)

### Community 57 - "MoneyText.kt"
Cohesion: 0.17
Nodes (7): atSize(), Figure(), fitHero(), HeroFit, HeroText(), RollingDigits(), toneColor()

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.09
Nodes (28): Personal Finance Tracker PRD, Audit trail (INT-2), Balance observations (L-10), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5) (+20 more)

### Community 59 - "UserId"
Cohesion: 0.07
Nodes (25): RoomAuditLog, AuditDaoTest, TestDatabase, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE (+17 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 63 - "SampleThing.kt"
Cohesion: 0.16
Nodes (7): EntityMapper, SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 66 - "IdentityService"
Cohesion: 0.09
Nodes (18): `data` (Room persistence, A4a), RoomTransactionRunner, RoomAppSettingsRepository, RoomTestKit, Configuration, Responsibilities & boundaries, AuditRecorder, Identity (+10 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 76 - "DesignCatalog.kt"
Cohesion: 0.13
Nodes (7): ChoiceRow(), DesignCatalog(), SectionContent(), DevContent(), CategoryGlyphPreview(), CategoryGlyphSamples(), SampleRow()

### Community 78 - "ThemePrefs"
Cohesion: 0.25
Nodes (3): EmptyState(), ThemePrefs, EmptyStateTest

### Community 79 - "PftDatabaseTest"
Cohesion: 0.08
Nodes (4): PftDatabaseTest, BundledFontTest, BundledSymbolsLicenceTest, Third-party assets

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.15
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 82 - "PftDatabase"
Cohesion: 0.07
Nodes (8): AuditDao, AuditEntryEntity, IdentityDao, DeviceEntity, UserProfileEntity, PftDatabase, AppSettingDao, AppSettingEntity

### Community 83 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 84 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 85 - "MigrationTestSupport"
Cohesion: 0.10
Nodes (4): AuditTriggers, MigrationTestSupport, MigrationTest, Audit triggers

### Community 86 - "Auditable"
Cohesion: 0.11
Nodes (8): AuditedTable, AuditedTables, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), Auditable, AppSetting, AuditDiffTest, Thing, InMemoryAppSettingsRepository

### Community 90 - "ContentScreenshotTest"
Cohesion: 0.23
Nodes (3): StatusChipPreview(), StatusChipSamples(), ContentScreenshotTest

### Community 94 - "StatusChipTest.kt"
Cohesion: 0.16
Nodes (3): ChipColors, MoneyColors, StatusChipTest

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 98 - "13. Deviations from the plan, by slice"
Cohesion: 0.31
Nodes (11): CollapsingHeader(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderTitle(), LowerBand(), PeriodStepper() (+3 more)

### Community 99 - "DatabaseFactory"
Cohesion: 0.10
Nodes (12): `:app` — Android application, Configuration, Data it owns, Dependencies, Purpose, DatabaseFactory, DatabaseStartup, KeyUnavailable (+4 more)

### Community 104 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.27
Nodes (6): Emoji, GlyphSource, isSymbolKey(), Letter, Symbol, 6.1 Design-system slice: PRs into `feat/m1a-foundation`

### Community 105 - "PaletteSamples.kt"
Cohesion: 0.25
Nodes (4): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 107 - "7. Test plan"
Cohesion: 0.30
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 108 - "HeaderStyle"
Cohesion: 0.67
Nodes (3): HeaderStyle, Dashboard, Standard

### Community 109 - "MotionSection.kt"
Cohesion: 0.07
Nodes (3): AccentCheckSection(), MotionSection(), SpringTrack()

### Community 113 - "DbKeyUnavailable"
Cohesion: 0.09
Nodes (22): `security` and the encrypted open (A4b), PreMigrationBackupFailed, DbKeyUnavailable, Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO (+14 more)

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 117 - "AuditFieldsCheck.kt"
Cohesion: 0.22
Nodes (3): AuditFieldsCheck, haveCompleteAuditFields(), shouldHaveCompleteAuditFields()

### Community 118 - "PftSpacing"
Cohesion: 0.16
Nodes (10): PftShapes, PftSpacing, Theming, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6) (+2 more)

### Community 120 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5), M1a Core ledger (+2 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.19
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 127 - "6.1 Slices"
Cohesion: 0.08
Nodes (24): colors(), ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, paletteFor(), ThemeMode (+16 more)

### Community 128 - "PaiseParser"
Cohesion: 0.16
Nodes (8): Configuration, `:core:money` — integer-paise money, Data it owns, Parsing grammar, Public interface, Purpose, Responsibilities & boundaries, PaiseParser

### Community 129 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.12
Nodes (16): Behaviours worth knowing, How an entity step composes the blocks, Public interface, AuditDiff, AuditedWriter, AuditSource, MANUAL, RECURRING (+8 more)

### Community 130 - "ThemeLiteralScanTest"
Cohesion: 0.38
Nodes (3): ThemeLiteralScanTest, Violation, Planned: premium look (DS6)

### Community 132 - "MoneyTextSamples.kt"
Cohesion: 0.67
Nodes (4): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples()

### Community 133 - "Appearance: colour palettes and dynamic colour"
Cohesion: 0.25
Nodes (5): Appearance: colour palettes and dynamic colour, Behind the scenes, Defaults, How the accent is kept from looking like a status, What it does for you

### Community 134 - "EmptyStateSamples.kt"
Cohesion: 0.73
Nodes (3): EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder()

### Community 135 - ".of"
Cohesion: 0.14
Nodes (10): EpochMillis, MetaColumns, MetaMapper, EpochMillisTest, MetaMapperTest, Conventions, Data model, The audit trail (+2 more)

### Community 137 - "Architecture overview"
Cohesion: 0.33
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 138 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.33
Nodes (6): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`)

### Community 139 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 140 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.13
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 3.1 Modules (Gradle), 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3. Design & decision (+5 more)

## Knowledge Gaps
- **190 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+185 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 577 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **70 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `runSuspending`, `ManifestHardeningTest`, `.of`, `MoneyTextTest`, `ColorContrastTest`, `MoneyText`, `PftThemeModeTest`, `PftSymbol`, `Previews, the token sheet and screenshot goldens`, `TokenSheetScreenshotTest.kt`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `PftShapesTest.kt`, `CategoryGlyph`, `GlyphSourceTest`, `UserProfile`, `CategoryGlyphScreenshotTest`, `CollapsingHeaderScaffoldTest`, `PftTheme`, `CollapsingHeaderScaffold`, `RoomAuditColumnsTest`, `DbKeyManagerTest.kt`, `UserId`, `PftSymbolTest`, `PftSpacingTest`, `SampleThing.kt`, `ThemeResolutionTest`, `IdentityService`, `AuditEntryMapperTest`, `RoomEntityRepositoryTest.kt`, `IdentityRoomIntegrationTest`, `ThemePrefs`, `PftDatabaseTest`, `expandedHeaderHeight`, `PftDatabase`, `MigrationTestSupport`, `PreMigrationBackupTest`, `WindowThemeTest`, `ContentScreenshotTest`, `AuditTriggersTest`, `GroupedSectionTest.kt`, `StatusChipTest.kt`, `PressScaleTest.kt`, `Paise`, `DatabaseFactory`, `RoomTransactionRunnerTest`, `DbKeyManagerWiringTest.kt`, `assertequals`, `7. Test plan`, `KeyUnavailableScreenTest.kt`, `PaletteScreenshotTest.kt`, `6.1 Slices`?**
  _High betweenness centrality (0.270) - this node is a cross-community bridge._
- **Are the 41 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 41 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _190 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `PaiseParser`, `docs/TECHNICAL.md`, `Collecting and Redacting Sample Messages Guide`, `Automatic capture from SMS (S-1..S-13)`, `Slice A — Foundation (`feat/m1a-foundation`)`, ``build-logic` — convention plugins`, `CLAUDE.md Development Constitution`, `Layered Modular Architecture (core vs app)`, `Personal Finance Tracker PRD`?**
  _High betweenness centrality (0.128) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `PftSymbol` (e.g. with `How to run and test` and ``ui/common` (shared components, DS3, DS4 and DS5)`) actually correct?**
  _`PftSymbol` has 13 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._