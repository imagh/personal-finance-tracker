# Graph Report - personal-finance-tracker  (2026-10-31)

## Corpus Check
- 258 files · ~196,148 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 124 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 3177 nodes · 8920 edges · 148 communities (79 shown, 69 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 1278 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `99b15e52`
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
- GroupedSection
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
- Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...)
- CollapsingHeaderScaffold.kt
- HeaderGlowScaffoldTest.kt
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- MoneyTextSamples.kt
- PftTypographyTest
- .of
- PftShapesTest.kt
- CategoryGlyph
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
- HeaderTypePairingTest.kt
- AuditEntryEntity
- RoomAuditColumnsTest
- LedgerEntity
- ContentScreenshotTest
- DbKeyManagerTest.kt
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
- GroupedSection.kt
- _Text
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- import_symbols.py
- RoomEntityRepository
- HeaderPolicy
- DesignCatalog.kt
- TokenSheetScreenshotTest.kt
- 7. Test plan
- BundledFontTest
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
- GlyphSourceTest
- DbKeyUnavailable
- Slice A — Foundation (`feat/m1a-foundation`)
- GroupedSectionTest.kt
- PftTheme
- DbKeyManager
- ThemePrefsTest
- BottomActionBar.kt
- CollapsingHeaderScaffold
- DatabaseFactory
- MoneyToneTest
- Migrations.kt
- KeyUnavailableScreenTest.kt
- PressScaleTest.kt
- PaletteSamples.kt
- AuditTriggersTest
- parse_svg
- BackgroundStyle
- ThemeLiteralScanTest
- ImportRunTest
- PaiseParserTest.kt
- AndroidKeystoreKek.kt
- ThemePaletteTest
- AuditDaoTest.kt
- Layered Modular Architecture (core vs app)
- AppSetting
- 3. Design & decision
- 13. Deviations from the plan, by slice
- BottomActionBarSurfaceTest.kt
- PaletteScreenshotTest.kt
- EncryptedDatabaseTest.kt
- DatabaseFactoryTest.kt
- Leaky
- PftDatabase
- ManifestHardeningTest
- modifier
- 7. Test plan
- Plan: M1a — Core ledger (manual-entry household tracker)
- assertequals
- .fromIconKey
- PaiseParser
- StatusChip
- EmptyStateTest.kt
- 6.1 Slices
- DbKeyManagerWiringTest.kt
- MainActivityStartupTest.kt
- WindowBackgroundTest.kt
- PreMigrationBackup
- BaseDao
- PftSurfacesTest
- PftSpacingTest
- onnodewithtag
- PftTheme.kt
- FontBudgetTest
- TokenSheet
- Security, privacy and integrity requirements (SEC/INT)
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
10. `PftTypographyTest` - 56 edges

## Surprising Connections (you probably didn't know these)
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
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

## Communities (148 total, 69 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "How to run and test"
Cohesion: 0.06
Nodes (9): How to run and test, RoomEntityRepositoryTest, FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomTransactionRunnerTest, runSuspending() (+1 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "GroupedSection"
Cohesion: 0.13
Nodes (15): `ui/common` (shared components, DS3, DS4 and DS5), GroupedRow, GroupedSection(), GroupedSectionScope, GroupedSectionPreview(), GroupedSectionSamples(), Content primitives (DS5), `EmptyState` (+7 more)

### Community 9 - "ThemePrefs"
Cohesion: 0.14
Nodes (4): resolveColorScheme(), resolvePaletteColors(), ThemePrefs, ThemeResolutionTest

### Community 10 - "Paise"
Cohesion: 0.14
Nodes (3): MoneyTextTest, Paise, Slice D — Budgets (`feat/m1a-budgets`)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.08
Nodes (16): AccentHueTest, Case, ColorContrastTest, Theme, DARK, LIGHT, contrast(), greyAtTone() (+8 more)

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.16
Nodes (23): moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense (+15 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.08
Nodes (4): BottomActionBar(), BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.17
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 31 - "Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...)"
Cohesion: 0.14
Nodes (4): Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), InMemoryAuditLog, InMemoryEntityStore, InMemoryLedgerStore

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "MoneyTextSamples.kt"
Cohesion: 0.67
Nodes (4): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples()

### Community 37 - "PftTypographyTest"
Cohesion: 0.10
Nodes (3): sansFamily(), PftTypographyTest, Resolved

### Community 38 - ".of"
Cohesion: 0.16
Nodes (9): Data it owns, EpochMillis, EpochMillisTest, NameBasedId, Conventions, Data model, The audit trail, Version 1 (A4a) (+1 more)

### Community 40 - "CategoryGlyph"
Cohesion: 0.06
Nodes (25): CategoryGlyph(), HeaderIconButton(), CategoryColors, CategoryPalette, pair(), CategoryGlyphTest, HeaderIconButtonTest, Adding a token (+17 more)

### Community 42 - "Project README"
Cohesion: 0.14
Nodes (13): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer (+5 more)

### Community 43 - "UserId"
Cohesion: 0.07
Nodes (18): `data` (Room persistence, A4a), MetaColumns, MetaMapper, RoomIdentityRepository, MetaMapperTest, RoomIdentityRepositoryTest, AuditedTables, TestDatabase (+10 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.11
Nodes (4): PftMotion, pressScale(), Principles, 3.7 Component inventory (UI-9)

### Community 45 - "CategoryGlyphScreenshotTest"
Cohesion: 0.23
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 46 - "MoneyColors"
Cohesion: 0.13
Nodes (6): ChipColors, MoneyColors, PftSpacing, colors(), ChipColorsTest, 3.3 Tokens

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.10
Nodes (24): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+16 more)

### Community 48 - "PftTypography.kt"
Cohesion: 0.17
Nodes (12): displayFamily(), newsreader(), pftMaterialTypography(), pftType(), PftTypography, sansWeights(), DisplayFont, BUNDLED_SERIF (+4 more)

### Community 49 - "HostedComposeRule"
Cohesion: 0.24
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 50 - "MainActivity"
Cohesion: 0.19
Nodes (11): Debug and release source sets, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, `security` and the encrypted open (A4b), Window theme, MainActivity, SyncWindowBackground(), 6.2 Appearance slice: base `feat/m1a-appearance` off `main`, after A-final (Q4) (+3 more)

### Community 51 - "HeaderTypePairingTest.kt"
Cohesion: 0.17
Nodes (4): HeaderTypePairingTest, assertShownInFull(), isShownInFull(), layout()

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (4): Probe, RoomAuditColumnsTest, AuditColumnsCheck, AuditedTable

### Community 54 - "LedgerEntity"
Cohesion: 0.06
Nodes (18): LedgerEntity, AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind, LARGE, SMALL (+10 more)

### Community 57 - "MoneyText"
Cohesion: 0.15
Nodes (13): `:app` — Android application, Configuration, Dependencies, Responsibilities and boundaries, atSize(), directionLabel(), Figure(), fitHero() (+5 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.08
Nodes (32): Personal Finance Tracker PRD, Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Automatic capture from Email (E-0..E-5) (+24 more)

### Community 59 - "AuditEntry"
Cohesion: 0.08
Nodes (25): FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE, UNARCHIVE, UPDATE (+17 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 63 - "SampleThing.kt"
Cohesion: 0.21
Nodes (6): SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 66 - "Public interface"
Cohesion: 0.08
Nodes (22): RoomTransactionRunner, RoomTestKit, Configuration, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, AuditDiff, AuditRecorder (+14 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 69 - "GroupedSection.kt"
Cohesion: 0.10
Nodes (6): fixedSp(), GroupedSectionDefaults, GroupedSectionTags, GlyphSize, Chip, Hero

### Community 72 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.14
Nodes (8): Behaviours worth knowing, AuditedWriter, EntityRepository, IdentityCorrupt, InMemoryEntityRepository, 3.4 Reuse by layer (owner decision, 2026-10-28), Slice B — Accounts, categories, tags (`feat/m1a-ledger-setup`), Slice C — Transactions (`feat/m1a-transactions`)

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 76 - "DesignCatalog.kt"
Cohesion: 0.09
Nodes (17): CatalogSection, ACCENT, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, GLYPHS, MONEY (+9 more)

### Community 78 - "7. Test plan"
Cohesion: 0.30
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 79 - "BundledFontTest"
Cohesion: 0.07
Nodes (3): PftDatabaseTest, BundledFontTest, BundledSymbolsLicenceTest

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.14
Nodes (14): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header (+6 more)

### Community 82 - "UserProfileEntity"
Cohesion: 0.19
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
Cohesion: 0.07
Nodes (3): AccentCheckSection(), MotionSection(), SpringTrack()

### Community 89 - ".format"
Cohesion: 0.18
Nodes (10): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+2 more)

### Community 91 - "DbKeyUnavailable"
Cohesion: 0.13
Nodes (14): PreMigrationBackupFailed, DbKeyUnavailable, KeyUnavailableScreen(), Corruption handling, Fail closed, Persistence and security, Pre-migration backup (INT-5), SQLCipher (+6 more)

### Community 92 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.04
Nodes (26): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, AlreadyInitialised, AmountNotPositive, AmountTooLarge (+18 more)

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 97 - "BottomActionBar.kt"
Cohesion: 0.16
Nodes (4): BottomActionBarPreview(), BottomActionBarSamples(), StatusChipPreview(), StatusChipSamples()

### Community 98 - "CollapsingHeaderScaffold"
Cohesion: 0.25
Nodes (15): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle, Dashboard (+7 more)

### Community 99 - "DatabaseFactory"
Cohesion: 0.12
Nodes (7): Purpose, DatabaseFactory, DatabaseStartup, KeyUnavailable, Ready, PftApplication, DatabaseStartupTest

### Community 102 - "KeyUnavailableScreenTest.kt"
Cohesion: 0.15
Nodes (8): Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING, KeyUnavailableScreenTest

### Community 104 - "PaletteSamples.kt"
Cohesion: 0.42
Nodes (4): PaletteColumn(), PaletteSwatches(), PaletteSwatchesPreview(), Swatch()

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 107 - "BackgroundStyle"
Cohesion: 0.18
Nodes (8): GroupedSectionColors, BackgroundStyle, FLAT, TONAL, PftSurfaces, Case, GroupedSectionColorsTest, 6.2 PR breakdown (incremental — required)

### Community 111 - "PaiseParserTest.kt"
Cohesion: 0.16
Nodes (5): How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest, 7. Test plan

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 116 - "AppSetting"
Cohesion: 0.14
Nodes (4): RoomAppSettingsRepository, RoomAppSettingsRepositoryTest, AppSetting, InMemoryAppSettingsRepository

### Community 117 - "3. Design & decision"
Cohesion: 0.29
Nodes (7): 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins"), 3. Design & decision

### Community 118 - "13. Deviations from the plan, by slice"
Cohesion: 0.22
Nodes (8): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), AuberginePalette, GraphitePalette, PaletteColors, IvoryPalette, MidnightPalette, Colour, 13. Deviations from the plan, by slice

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "DatabaseFactoryTest.kt"
Cohesion: 0.16
Nodes (7): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), open(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 123 - "Leaky"
Cohesion: 0.24
Nodes (5): Fixture, Leaky, LeakyRepository, Note, NoteId

### Community 126 - "modifier"
Cohesion: 0.18
Nodes (4): EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), DevContent()

### Community 127 - "7. Test plan"
Cohesion: 0.12
Nodes (11): asHeroTop(), glowAlpha(), glowEnd(), glowStrength(), headerGlow(), HeaderGlowTest, Theming, 1. Requirement (+3 more)

### Community 128 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.12
Nodes (15): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5. Flows, 6. Implementation steps (TDD-ordered) (+7 more)

### Community 130 - ".fromIconKey"
Cohesion: 0.25
Nodes (5): Emoji, GlyphSource, isSymbolKey(), Letter, Symbol

### Community 134 - "6.1 Slices"
Cohesion: 0.08
Nodes (33): ThemePalette, AUBERGINE, GRAPHITE, IVORY, MIDNIGHT, paletteFor(), SansFont, FIGTREE (+25 more)

### Community 146 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 148 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

## Knowledge Gaps
- **197 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+192 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 584 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **69 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `assertequals`, `StatusChip`, `EmptyStateTest.kt`, `WindowThemeTest`, `DbKeyManagerWiringTest.kt`, `MainActivityStartupTest.kt`, `ThemePrefs`, `Paise`, `WindowBackgroundTest.kt`, `PftSurfacesTest`, `PftSpacingTest`, `ColorContrastTest`, `PftThemeModeTest`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `Previews, the token sheet and screenshot goldens`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `PftShapesTest.kt`, `CategoryGlyph`, `AuditEntryMapperTest.kt`, `UserId`, `CategoryGlyphScreenshotTest`, `MoneyColors`, `MainActivity`, `RoomAuditColumnsTest`, `ContentScreenshotTest`, `DbKeyManagerTest.kt`, `MoneyText`, `PftSymbolTest`, `SampleThing.kt`, `Public interface`, `BottomActionBarTest.kt`, `TokenSheetScreenshotTest.kt`, `7. Test plan`, `BundledFontTest`, `expandedHeaderHeight`, `MigrationTestSupport`, `PreMigrationBackupTest`, `.format`, `GlyphSourceTest`, `GroupedSectionTest.kt`, `PftTheme`, `ThemePrefsTest`, `DatabaseFactory`, `MoneyToneTest`, `KeyUnavailableScreenTest.kt`, `PressScaleTest.kt`, `AuditTriggersTest`, `BackgroundStyle`, `ThemePaletteTest`, `AuditDaoTest.kt`, `AppSetting`, `BottomActionBarSurfaceTest.kt`, `PaletteScreenshotTest.kt`, `PftDatabase`, `ManifestHardeningTest`?**
  _High betweenness centrality (0.248) - this node is a cross-community bridge._
- **Are the 49 inferred relationships involving `PftTheme()` (e.g. with `Purpose` and ``ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)`) actually correct?**
  _`PftTheme()` has 49 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _197 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Collecting and Redacting Sample Messages Guide`, `Automatic capture from SMS (S-1..S-13)`, ``build-logic` — convention plugins`, `CLAUDE.md Development Constitution`, `Layered Modular Architecture (core vs app)`, `.format`, `Personal Finance Tracker PRD`, `Slice A — Foundation (`feat/m1a-foundation`)`?**
  _High betweenness centrality (0.096) - this node is a cross-community bridge._
- **Are the 38 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `PaletteSwatchesPreview()`) actually correct?**
  _`ThemePrefs` has 38 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._