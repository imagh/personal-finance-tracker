# Graph Report - personal-finance-tracker  (2026-10-30)

## Corpus Check
- 231 files · ~162,196 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 122 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 2748 nodes · 7269 edges · 134 communities (73 shown, 61 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 950 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `4b6f1304`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- How to run and test
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- ManifestHardeningTest
- hooks
- test_import_symbols.py
- InMemoryEntityRepository
- MoneyTextTest.kt
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- MoneyTone
- Catalog.kt
- detekt.yml
- NavTransitionTest.kt
- PftSymbol
- CollapsingHeaderScaffoldTest
- Previews, the token sheet and screenshot goldens
- TokenSheetScreenshotTest.kt
- Uuid7Test.kt
- CollapsingHeaderScaffold.kt
- BottomActionBarTest.kt
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- CatalogSection
- PftTypographyTest
- UserId
- PftShapesTest.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- TestClock
- Project README
- Auditable
- PftMotion.kt
- CategoryGlyphScreenshotTest
- PftTypography.kt
- CLAUDE.md Development Constitution
- AppSetting
- HostedComposeRule
- shouldbe
- DesignCatalogTest.kt
- BottomActionBar
- RoomAuditColumnsTest
- EntityMeta
- MainActivity
- DbKeyManagerTest
- MoneyText
- Personal Finance Tracker PRD
- AuditEntryMapperTest.kt
- redact_text
- PftSymbolTest
- PftSpacingTest
- SampleThing.kt
- Pseudo
- GroupedSection.kt
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- Collecting and Redacting Sample Messages Guide
- AuditEntryEntity
- DbKeyUnavailable
- _Text
- PftThemeModeTest
- import_symbols.py
- BottomActionBar.kt
- HeaderPolicy
- DesignCatalog.kt
- ThemePrefs
- AuditTriggersTest
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- HeaderSlot
- HeaderPhase
- MigrationTestSupport
- AppSettingEntity
- PreMigrationBackupTest
- SampleId
- ContentScreenshotTest
- Slice A — Foundation (`feat/m1a-foundation`)
- RoomEntityRepository
- GroupedSectionTest.kt
- StatusChipTest.kt
- DbKeyManager
- PressScaleTest.kt
- Paise
- CollapsingHeaderScaffold
- DatabaseStartupTest.kt
- AccentHueTest
- Migrations.kt
- DbKeyManagerWiringTest.kt
- CategoryPaletteTest
- BundledSymbolsLicenceTest
- BundledFontTest
- parse_svg
- CategoryPalette.kt
- InMemoryEntityStore
- PftTheme.kt
- ImportRunTest
- .format
- AndroidKeystoreKek.kt
- KeyUnavailableScreenTest.kt
- DatabaseFactoryTest.kt
- Layered Modular Architecture (core vs app)
- Loans and EMIs (LN-1..LN-5)
- ColorContrastTest.kt
- Design system
- BaseDao
- MainActivityStartupTest.kt
- EncryptedDatabaseTest.kt
- ThemeResolutionTest.kt
- PftDatabase
- `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)
- MoneyColors
- ThemeLiteralScanTest
- PaiseParser
- Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...)
- 6.1 Slices
- MoneySize
- `:core:ledger` — the ledger foundations
- Immutable Audit Trail

## God Nodes (most connected - your core abstractions)
1. `PftSymbol` - 113 edges
2. `Paise` - 90 edges
3. `ThemePrefs` - 82 edges
4. `CollapsingHeaderScaffoldTest` - 70 edges
5. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 69 edges
6. `How to run and test` - 59 edges
7. `UserId` - 57 edges
8. `runSuspending()` - 54 edges
9. `3.4 Reuse by layer (owner decision, 2026-10-28)` - 51 edges
10. `MoneyText()` - 50 edges

## Surprising Connections (you probably didn't know these)
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `3.4 Header glow (UI-13, DS6d)` --references--> `HeaderPolicy`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/CollapsingHeaderScaffold.kt
- ``StatusChip`` --references--> `StatusChip()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/StatusChip.kt
- `Adding an icon` --references--> `PftSymbol`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftSymbol.kt
- `9. Documentation updates (all required surfaces)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt

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

## Communities (134 total, 61 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "How to run and test"
Cohesion: 0.05
Nodes (9): How to run and test, FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAppSettingsRepositoryTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomIdentityRepositoryTest, RoomTransactionRunnerTest (+1 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "ManifestHardeningTest"
Cohesion: 0.08
Nodes (8): ManifestHardeningTest, WindowThemeTest, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "InMemoryEntityRepository"
Cohesion: 0.16
Nodes (6): Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 9 - "MoneyTextTest.kt"
Cohesion: 0.10
Nodes (5): layoutResult(), shownText(), assertShownInFull(), isShownInFull(), layout()

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.26
Nodes (4): ColorContrastTest, Theme, DARK, LIGHT

### Community 16 - "MoneyTone"
Cohesion: 0.16
Nodes (20): moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense (+12 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 29 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.14
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 31 - "Uuid7Test.kt"
Cohesion: 0.05
Nodes (19): IdentityServiceTest, NameBasedIdTest, clockAt(), Uuid7Test, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest (+11 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "CatalogSection"
Cohesion: 0.18
Nodes (11): CatalogSection, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, GLYPHS, MONEY, MOTION (+3 more)

### Community 38 - "UserId"
Cohesion: 0.09
Nodes (24): AuditDaoTest, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE, UNARCHIVE (+16 more)

### Community 40 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.08
Nodes (10): CategoryGlyph(), Emoji, GlyphSource, isEmojiValue(), isSymbolKey(), Letter, Symbol, CategoryGlyphTest (+2 more)

### Community 41 - "TestClock"
Cohesion: 0.10
Nodes (10): Data it owns, EpochMillis, EpochMillisTest, NameBasedId, TestClock, Conventions, Data model, The audit trail (+2 more)

### Community 42 - "Project README"
Cohesion: 0.23
Nodes (7): Code Structure Map, core/shared Shared Packages, Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen, What it does for you, Project README

### Community 43 - "Auditable"
Cohesion: 0.11
Nodes (9): RoomIdentityRepository, AuditedTable, AuditedTables, Auditable, Device, IdentityRepository, UserProfile, Thing (+1 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.09
Nodes (6): iosSpring(), PftMotion, pressScale(), PftMotionTest, Principles, 7. Test plan

### Community 45 - "CategoryGlyphScreenshotTest"
Cohesion: 0.21
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 46 - "PftTypography.kt"
Cohesion: 0.12
Nodes (10): displayFamily(), pftMaterialTypography(), pftType(), PftTypography, DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT (+2 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "AppSetting"
Cohesion: 0.17
Nodes (6): RoomAuditLog, RoomAppSettingsRepository, AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository, The core/app boundary

### Community 49 - "HostedComposeRule"
Cohesion: 0.22
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 50 - "shouldbe"
Cohesion: 0.07
Nodes (21): TestDatabase, How to run and test, AuditLog, SettingKeys, AlreadyInitialised, AmountNotPositive, AmountTooLarge, Archived (+13 more)

### Community 52 - "BottomActionBar"
Cohesion: 0.11
Nodes (19): BottomActionBar(), BottomBarAction, GroupedSection(), 3.1 Modules (Gradle), 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3.5 Design system (owner decision, 2026-10-29), 3. Design & decision, 6.2 Appearance slice: base `feat/m1a-appearance` off `main`, after A-final (Q4) (+11 more)

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.06
Nodes (25): `data` (Room persistence, A4a), MetaColumns, MetaMapper, MetaMapperTest, EntityMeta, LedgerEntity, EntityId, AuditFieldsCheckTest (+17 more)

### Community 55 - "MainActivity"
Cohesion: 0.14
Nodes (19): `:app` — Android application, Configuration, Debug and release source sets, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose, Responsibilities and boundaries, Window theme (+11 more)

### Community 57 - "MoneyText"
Cohesion: 0.17
Nodes (9): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText(), RollingDigits() (+1 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.09
Nodes (28): Personal Finance Tracker PRD, Audit trail (INT-2), Balance observations (L-10), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5) (+20 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 61 - "PftSymbolTest"
Cohesion: 0.22
Nodes (5): PftSymbolTest, Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 63 - "SampleThing.kt"
Cohesion: 0.21
Nodes (6): SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 65 - "GroupedSection.kt"
Cohesion: 0.15
Nodes (6): GroupedRow, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, GroupedSectionPreview(), GroupedSectionSamples()

### Community 66 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.08
Nodes (25): RoomTransactionRunner, RoomTestKit, Behaviours worth knowing, Configuration, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, AuditDiff (+17 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 69 - "DbKeyUnavailable"
Cohesion: 0.11
Nodes (14): DatabaseFactory, DatabaseStartup, KeyUnavailable, Ready, PftApplication, DbKeyUnavailable, Reason, KEK_MISSING (+6 more)

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 76 - "DesignCatalog.kt"
Cohesion: 0.08
Nodes (10): ChoiceRow(), DesignCatalog(), SectionContent(), DevContent(), StatusChipPreview(), StatusChipSamples(), KeyUnavailableScreen(), TokenSheet() (+2 more)

### Community 78 - "ThemePrefs"
Cohesion: 0.09
Nodes (17): EmptyState(), PftSpacing, ThemeMode, DARK, LIGHT, SYSTEM, ThemePrefs, EmptyStateTest (+9 more)

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.14
Nodes (14): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header (+6 more)

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

### Community 90 - "ContentScreenshotTest"
Cohesion: 0.21
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), ContentScreenshotTest

### Community 91 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.16
Nodes (12): `security` and the encrypted open (A4b), PreMigrationBackup, PreMigrationBackupFailed, Pre-migration backup (INT-5), What logs may contain, 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice A — Foundation (`feat/m1a-foundation`) (+4 more)

### Community 95 - "DbKeyManager"
Cohesion: 0.16
Nodes (11): DbKeyManager, Decoded, WrappedKeyFormat, Corruption handling, Fail closed, Persistence and security, SQLCipher, The key and the KEK (+3 more)

### Community 97 - "Paise"
Cohesion: 0.20
Nodes (3): MoneyToneTest, Paise, Slice D — Budgets (`feat/m1a-budgets`)

### Community 98 - "CollapsingHeaderScaffold"
Cohesion: 0.23
Nodes (16): `ui/common` (shared components, DS3, DS4 and DS5), CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle (+8 more)

### Community 100 - "AccentHueTest"
Cohesion: 0.20
Nodes (5): AccentHueTest, hueDistance(), labHue(), roles(), surfaceRoles()

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 107 - "CategoryPalette.kt"
Cohesion: 0.43
Nodes (3): CategoryColors, CategoryPalette, pair()

### Community 109 - "PftTheme.kt"
Cohesion: 0.06
Nodes (9): MotionSection(), SpringTrack(), fixedSp(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), GlyphSize, Chip (+1 more)

### Community 111 - ".format"
Cohesion: 0.16
Nodes (11): Dependencies, Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface (+3 more)

### Community 114 - "DatabaseFactoryTest.kt"
Cohesion: 0.26
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), factory(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 116 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5), M1a Core ledger (+2 more)

### Community 117 - "ColorContrastTest.kt"
Cohesion: 0.18
Nodes (6): groupedSectionColor(), Content primitives (DS5), `EmptyState`, `GroupedSection`, `StatusChip`, 3.1 Palettes (DS6a, DS6b)

### Community 118 - "Design system"
Cohesion: 0.17
Nodes (11): Adding a token, Build-time trust decisions, Colour, Design system, Motion (UI-10), One-hand layout (UI-7), Planned: premium look (DS6), Shapes (+3 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "ThemeResolutionTest.kt"
Cohesion: 0.24
Nodes (3): `default prefs turn dynamic colour off`(), `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`()

### Community 125 - "`ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)"
Cohesion: 0.33
Nodes (4): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), GraphitePalette, PaletteColors, 13. Deviations from the plan, by slice

### Community 129 - "Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...)"
Cohesion: 0.32
Nodes (3): Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), InMemoryAuditLog, InMemoryLedgerStore

### Community 130 - "6.1 Slices"
Cohesion: 0.29
Nodes (5): resolveColorScheme(), 6.1 Slices, 6.2 PR breakdown (incremental — required), 6.3 What AP1 must add (DS §6.2 is annotated to point here), 6. Implementation steps (TDD-ordered)

### Community 131 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 132 - "`:core:ledger` — the ledger foundations"
Cohesion: 0.40
Nodes (4): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, Purpose

## Knowledge Gaps
- **184 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+179 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 569 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **61 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `ManifestHardeningTest`, `MoneyTextTest`, `ColorContrastTest`, `NavTransitionTest.kt`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `Previews, the token sheet and screenshot goldens`, `TokenSheetScreenshotTest.kt`, `BottomActionBarTest.kt`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `UserId`, `PftShapesTest.kt`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `PftMotion.kt`, `CategoryGlyphScreenshotTest`, `DesignCatalogTest.kt`, `RoomAuditColumnsTest`, `EntityMeta`, `MainActivity`, `DbKeyManagerTest`, `MoneyText`, `AuditEntryMapperTest.kt`, `PftSymbolTest`, `PftSpacingTest`, `SampleThing.kt`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `PftThemeModeTest`, `ThemePrefs`, `AuditTriggersTest`, `expandedHeaderHeight`, `MigrationTestSupport`, `PreMigrationBackupTest`, `SampleId`, `ContentScreenshotTest`, `GroupedSectionTest.kt`, `StatusChipTest.kt`, `PressScaleTest.kt`, `Paise`, `DatabaseStartupTest.kt`, `DbKeyManagerWiringTest.kt`, `CategoryPaletteTest`, `BundledSymbolsLicenceTest`, `BundledFontTest`, `.format`, `KeyUnavailableScreenTest.kt`, `MainActivityStartupTest.kt`, `PftDatabase`?**
  _High betweenness centrality (0.289) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `PftSymbol` (e.g. with `How to run and test` and ``ui/common` (shared components, DS3, DS4 and DS5)`) actually correct?**
  _`PftSymbol` has 13 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _184 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Collecting and Redacting Sample Messages Guide`, ``:core:ledger` — the ledger foundations`, `Automatic capture from SMS (S-1..S-13)`, ``build-logic` — convention plugins`, `.format`, `CLAUDE.md Development Constitution`, `Layered Modular Architecture (core vs app)`, `Personal Finance Tracker PRD`?**
  _High betweenness centrality (0.102) - this node is a cross-community bridge._
- **Are the 14 inferred relationships involving `Paise` (e.g. with ``ui/common` (shared components, DS3, DS4 and DS5)` and `Behaviours worth knowing`) actually correct?**
  _`Paise` has 14 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._