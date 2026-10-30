# Graph Report - personal-finance-tracker  (2026-10-30)

## Corpus Check
- 233 files · ~164,088 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 122 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 2777 nodes · 7393 edges · 146 communities (81 shown, 65 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 976 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `283d4b65`
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
- AuditedWriterTest.kt
- MoneyTextTest.kt
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- GroupedSection
- Catalog.kt
- detekt.yml
- NavTransitionTest.kt
- PftSymbol
- CollapsingHeaderScaffoldTest
- Previews, the token sheet and screenshot goldens
- TokenSheetScreenshotTest.kt
- Uuid7Test.kt
- CollapsingHeaderScaffold.kt
- BottomActionBarTest
- DbKeyManagerJvmTest
- TokenSheetSections.kt
- CatalogSection
- PftTypographyTest
- AuditEntry
- PftShapesTest.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- TestClock
- Project README
- UserId
- PftMotion.kt
- CategoryGlyphScreenshotTest
- PftTypography.kt
- CLAUDE.md Development Constitution
- AppSetting
- HostedComposeRule
- LedgerError
- DesignCatalogTest.kt
- 3. Design & decision
- RoomAuditColumnsTest
- EntityMeta
- MainActivity
- DbKeyManagerTest.kt
- MoneyText
- Personal Finance Tracker PRD
- AuditEntryMapperTest
- redact_text
- asserttrue
- PftSpacingTest
- SampleThing.kt
- Pseudo
- PftTheme.kt
- IdentityService
- Collecting and Redacting Sample Messages Guide
- AuditEntryEntity
- DatabaseFactory
- _Text
- RoomTestKit
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
- RoomEntityRepositoryTest.kt
- WindowThemeTest
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
- 7. Test plan
- Migrations.kt
- DbKeyManagerWiringTest.kt
- CategoryPaletteTest
- applicationprovider
- BundledFontTest
- parse_svg
- Icons and emoji (DS3, UI-6)
- How to run and test
- GroupedSection.kt
- ImportRunTest
- .format
- AndroidKeystoreKek.kt
- KeyUnavailableScreenTest.kt
- DatabaseFactoryTest.kt
- Layered Modular Architecture (core vs app)
- Loans and EMIs (LN-1..LN-5)
- RoomTransactionRunnerTest
- Design system
- BaseDao
- MainActivityStartupTest.kt
- EncryptedDatabaseTest.kt
- ThemeResolutionTest.kt
- MotionSection.kt
- PftDatabase
- HeaderIconButtonTest.kt
- MoneyColors
- 6.1 Slices
- PaiseParser
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- DisplayFont
- MoneySize
- MoneyTextSamples.kt
- Immutable Audit Trail
- 7. Test plan
- `data` (Room persistence, A4a)
- 3. Design & decision
- modifier
- CategoryGlyphSamples.kt
- Plan: M1a — Core ledger (manual-entry household tracker)
- Persistence and security
- EmptyStateSamples.kt
- Architecture overview
- AuditLog

## God Nodes (most connected - your core abstractions)
1. `PftSymbol` - 119 edges
2. `Paise` - 90 edges
3. `ThemePrefs` - 85 edges
4. `CollapsingHeaderScaffoldTest` - 77 edges
5. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 70 edges
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
- `3.8 Abstraction bar` --references--> `HeaderIconButton()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderIconButton.kt
- `6.2 PR breakdown (incremental — required)` --references--> `HeaderIconButton()`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/HeaderIconButton.kt
- ``StatusChip`` --references--> `StatusChip()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/StatusChip.kt

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

## Communities (146 total, 65 thin omitted)

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

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "AuditedWriterTest.kt"
Cohesion: 0.14
Nodes (7): AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 9 - "MoneyTextTest.kt"
Cohesion: 0.10
Nodes (5): layoutResult(), shownText(), assertShownInFull(), isShownInFull(), layout()

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.22
Nodes (4): ColorContrastTest, Theme, DARK, LIGHT

### Community 16 - "GroupedSection"
Cohesion: 0.09
Nodes (31): `ui/common` (shared components, DS3, DS4 and DS5), GroupedSection(), moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER (+23 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.08
Nodes (5): BottomActionBar(), BottomBarAction, CollapsingHeaderScaffoldTest, Steps, One-hand layout (UI-7)

### Community 29 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.20
Nodes (5): DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 31 - "Uuid7Test.kt"
Cohesion: 0.07
Nodes (8): NameBasedIdTest, clockAt(), Uuid7Test, InMemoryEntityStore, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "CatalogSection"
Cohesion: 0.18
Nodes (11): CatalogSection, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, GLYPHS, MONEY, MOTION (+3 more)

### Community 38 - "AuditEntry"
Cohesion: 0.06
Nodes (26): AuditDaoTest, TestDatabase, How to run and test, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE (+18 more)

### Community 40 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.09
Nodes (10): CategoryGlyph(), Emoji, GlyphSource, isEmojiValue(), isSymbolKey(), Letter, Symbol, CategoryGlyphTest (+2 more)

### Community 41 - "TestClock"
Cohesion: 0.14
Nodes (5): Data it owns, EpochMillis, EpochMillisTest, TestClock, Conventions

### Community 42 - "Project README"
Cohesion: 0.23
Nodes (7): Code Structure Map, core/shared Shared Packages, Data protection: encrypted storage and the "can't open your data" screen, Not built yet, The "Your data can't be opened" screen, What it does for you, Project README

### Community 43 - "UserId"
Cohesion: 0.08
Nodes (13): RoomIdentityRepository, RoomIdentityRepositoryTest, AuditedTable, AuditedTables, Auditable, Device, IdentityRepository, UserProfile (+5 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.10
Nodes (3): PftMotion, pressScale(), Principles

### Community 46 - "PftTypography.kt"
Cohesion: 0.15
Nodes (6): displayFamily(), pftMaterialTypography(), pftType(), PftTypography, Theming, 2. Current state

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "AppSetting"
Cohesion: 0.19
Nodes (6): RoomAuditLog, RoomAppSettingsRepository, AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository, The core/app boundary

### Community 49 - "HostedComposeRule"
Cohesion: 0.22
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 50 - "LedgerError"
Cohesion: 0.15
Nodes (11): AlreadyInitialised, AmountNotPositive, AmountTooLarge, Archived, BlankName, Deleted, InvalidSettingKey, LedgerError (+3 more)

### Community 52 - "3. Design & decision"
Cohesion: 0.29
Nodes (7): 3.2 Surfaces: tonal or flat (DS6c), 3.4 Header glow (UI-13, DS6d), 3.5 Icon-only header actions (UI-14, DS6e; needs DS3), 3.6 Type pairing and optical size per role (UI-15, DS6g), 3.7 Bundled sans fonts (UI-16, UI-17; DS6f spike, then DS6h, DS6i), 3.8 Abstraction bar, 3. Design & decision

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.08
Nodes (20): EntityMeta, LedgerEntity, AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind, LARGE (+12 more)

### Community 55 - "MainActivity"
Cohesion: 0.27
Nodes (11): `:app` — Android application, Configuration, Debug and release source sets, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose, Responsibilities and boundaries, Window theme (+3 more)

### Community 57 - "MoneyText"
Cohesion: 0.17
Nodes (9): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText(), RollingDigits() (+1 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.09
Nodes (28): Personal Finance Tracker PRD, Audit trail (INT-2), Balance observations (L-10), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5) (+20 more)

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 63 - "SampleThing.kt"
Cohesion: 0.32
Nodes (3): SampleDatabase, SampleThingDao, SampleThingEntity

### Community 65 - "PftTheme.kt"
Cohesion: 0.12
Nodes (4): GroupedSectionPreview(), GroupedSectionSamples(), DashboardHeaderPreview(), resolveColorScheme()

### Community 66 - "IdentityService"
Cohesion: 0.08
Nodes (19): RoomTransactionRunner, Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, Purpose, Responsibilities & boundaries, AuditRecorder (+11 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 72 - "RoomTestKit"
Cohesion: 0.14
Nodes (3): FailingOnNthAppend, IdentityRoomIntegrationTest, RoomTestKit

### Community 73 - "import_symbols.py"
Cohesion: 0.14
Nodes (6): main(), parse_hashes(), parse_manifest(), to_vector_drawable(), UnsafeSvg, ManifestTest

### Community 76 - "DesignCatalog.kt"
Cohesion: 0.11
Nodes (9): ChoiceRow(), DesignCatalog(), SectionContent(), DevContent(), StatusChipPreview(), StatusChipSamples(), KeyUnavailableScreen(), TokenSheet() (+1 more)

### Community 78 - "ThemePrefs"
Cohesion: 0.22
Nodes (4): EmptyState(), ThemePrefs, EmptyStateTest, 3.7 Component inventory (UI-9)

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
Cohesion: 0.09
Nodes (8): AuditTriggers, MigrationTestSupport, MigrationTest, Data model, The audit trail, Version 1 (A4a), Version table, Audit triggers

### Community 88 - "RoomEntityRepositoryTest.kt"
Cohesion: 0.19
Nodes (5): RoomEntityRepositoryTest, SampleId, SampleThing, SampleThingMapper, SampleThingRepository

### Community 91 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.11
Nodes (15): `security` and the encrypted open (A4b), PreMigrationBackup, PreMigrationBackupFailed, DbKeyUnavailable, factory(), Pre-migration backup (INT-5), What logs may contain, 6. Implementation steps (TDD-ordered) (+7 more)

### Community 95 - "DbKeyManager"
Cohesion: 0.28
Nodes (3): DbKeyManager, Decoded, WrappedKeyFormat

### Community 98 - "CollapsingHeaderScaffold"
Cohesion: 0.25
Nodes (15): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle, Dashboard (+7 more)

### Community 99 - "DatabaseStartupTest.kt"
Cohesion: 0.19
Nodes (5): DatabaseStartup, KeyUnavailable, Ready, PftApplication, DatabaseStartupTest

### Community 100 - "7. Test plan"
Cohesion: 0.20
Nodes (6): AccentHueTest, hueDistance(), labHue(), roles(), surfaceRoles(), 7. Test plan

### Community 105 - "BundledFontTest"
Cohesion: 0.13
Nodes (3): BundledFontTest, Third-party assets, 8. Security considerations

### Community 106 - "parse_svg"
Cohesion: 0.31
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 107 - "Icons and emoji (DS3, UI-6)"
Cohesion: 0.27
Nodes (7): CategoryColors, CategoryPalette, pair(), Adding an icon, Icons and emoji (DS3, UI-6), The curated list, download()

### Community 109 - "GroupedSection.kt"
Cohesion: 0.09
Nodes (5): fixedSp(), GroupedRow, GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags

### Community 111 - ".format"
Cohesion: 0.15
Nodes (12): Dependencies, Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface (+4 more)

### Community 113 - "KeyUnavailableScreenTest.kt"
Cohesion: 0.14
Nodes (8): Reason, KEK_MISSING, KEK_UNUSABLE, KEY_FILE_AUTH_FAILED, KEY_FILE_IO, KEY_FILE_MALFORMED, KEY_FILE_MISSING, KeyUnavailableScreenTest

### Community 114 - "DatabaseFactoryTest.kt"
Cohesion: 0.23
Nodes (6): `a failed open wipes the key it was handed, and records no version`(), clean(), cleanUp(), Handoff, `on a fresh install the key exists before the database file is created, and the database is usable`(), `the open helper gets the real key, and it is still intact once open returns`()

### Community 115 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 116 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5), M1a Core ledger (+2 more)

### Community 118 - "Design system"
Cohesion: 0.09
Nodes (19): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), groupedSectionColor(), GraphitePalette, PaletteColors, Adding a token, Build-time trust decisions, Colour, Content primitives (DS5) (+11 more)

### Community 121 - "EncryptedDatabaseTest.kt"
Cohesion: 0.11
Nodes (12): after(), backup(), cleanUp(), header(), keyCopy(), keys(), loadNativeLibraryAndClean(), open() (+4 more)

### Community 122 - "ThemeResolutionTest.kt"
Cohesion: 0.21
Nodes (3): `default prefs turn dynamic colour off`(), `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`()

### Community 123 - "MotionSection.kt"
Cohesion: 0.18
Nodes (3): MotionSection(), SpringTrack(), PftShapes

### Community 125 - "HeaderIconButtonTest.kt"
Cohesion: 0.22
Nodes (3): HeaderIconButton(), HeaderIconButtonTest, 13. Deviations from the plan, by slice

### Community 127 - "6.1 Slices"
Cohesion: 0.16
Nodes (11): ThemeLiteralScanTest, Violation, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 6.1 Slices, 6.2 PR breakdown (incremental — required) (+3 more)

### Community 129 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.10
Nodes (21): Behaviours worth knowing, How an entity step composes the blocks, Public interface, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), AuditDiff, AuditedWriter, AuditSource, MANUAL (+13 more)

### Community 130 - "DisplayFont"
Cohesion: 0.20
Nodes (9): DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, ThemeMode, DARK, LIGHT, SYSTEM (+1 more)

### Community 131 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 132 - "MoneyTextSamples.kt"
Cohesion: 0.24
Nodes (4): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples()

### Community 134 - "7. Test plan"
Cohesion: 0.32
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 135 - "`data` (Room persistence, A4a)"
Cohesion: 0.31
Nodes (4): `data` (Room persistence, A4a), MetaColumns, MetaMapper, MetaMapperTest

### Community 136 - "3. Design & decision"
Cohesion: 0.20
Nodes (9): PftSpacing, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens, 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins") (+1 more)

### Community 138 - "CategoryGlyphSamples.kt"
Cohesion: 0.31
Nodes (6): CategoryGlyphPreview(), CategoryGlyphSamples(), SampleRow(), GlyphSize, Chip, Hero

### Community 140 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.18
Nodes (9): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5. Flows, 8. Security considerations (+1 more)

### Community 141 - "Persistence and security"
Cohesion: 0.25
Nodes (8): Corruption handling, Fail closed, Persistence and security, SQLCipher, The key and the KEK, The key array and SQLCipher (a trap), The pieces, Wrapped-key file format, version 1

### Community 142 - "EmptyStateSamples.kt"
Cohesion: 0.73
Nodes (3): EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder()

### Community 143 - "Architecture overview"
Cohesion: 0.33
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

## Knowledge Gaps
- **184 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+179 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 567 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **65 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `runSuspending`, `ManifestHardeningTest`, `7. Test plan`, ``data` (Room persistence, A4a)`, `MoneyTextTest`, `ColorContrastTest`, `NavTransitionTest.kt`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `Previews, the token sheet and screenshot goldens`, `TokenSheetScreenshotTest.kt`, `BottomActionBarTest`, `DbKeyManagerJvmTest`, `PftTypographyTest`, `AuditEntry`, `PftShapesTest.kt`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `UserId`, `CategoryGlyphScreenshotTest`, `DesignCatalogTest.kt`, `RoomAuditColumnsTest`, `MainActivity`, `DbKeyManagerTest.kt`, `MoneyText`, `AuditEntryMapperTest`, `asserttrue`, `PftSpacingTest`, `RoomTestKit`, `ThemePrefs`, `AuditTriggersTest`, `expandedHeaderHeight`, `MigrationTestSupport`, `PreMigrationBackupTest`, `RoomEntityRepositoryTest.kt`, `WindowThemeTest`, `ContentScreenshotTest`, `GroupedSectionTest.kt`, `StatusChipTest.kt`, `PressScaleTest.kt`, `Paise`, `DatabaseStartupTest.kt`, `DbKeyManagerWiringTest.kt`, `CategoryPaletteTest`, `applicationprovider`, `BundledFontTest`, `.format`, `KeyUnavailableScreenTest.kt`, `RoomTransactionRunnerTest`, `MainActivityStartupTest.kt`, `PftDatabase`?**
  _High betweenness centrality (0.305) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `PftSymbol` (e.g. with `How to run and test` and ``ui/common` (shared components, DS3, DS4 and DS5)`) actually correct?**
  _`PftSymbol` has 13 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _184 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1476923076923077 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `IdentityService`, `Collecting and Redacting Sample Messages Guide`, `Automatic capture from SMS (S-1..S-13)`, ``build-logic` — convention plugins`, `.format`, `CLAUDE.md Development Constitution`, `Layered Modular Architecture (core vs app)`, `Personal Finance Tracker PRD`?**
  _High betweenness centrality (0.103) - this node is a cross-community bridge._
- **Are the 14 inferred relationships involving `Paise` (e.g. with ``ui/common` (shared components, DS3, DS4 and DS5)` and `Behaviours worth knowing`) actually correct?**
  _`Paise` has 14 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._