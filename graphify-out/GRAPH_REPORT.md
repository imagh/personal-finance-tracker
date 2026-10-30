# Graph Report - pft-wt-ds3  (2026-10-30)

## Corpus Check
- 207 files · ~143,710 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 122 file(s) not represented in the graph (top: .xml 102, .lockfile 8, (none) 4)

## Summary
- 2403 nodes · 6344 edges · 125 communities (67 shown, 58 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 856 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `a772b89b`
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
- RedactTests
- InMemoryEntityRepository
- PressScaleTest.kt
- MoneyTextTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Catalog.kt
- detekt.yml
- NavTransitionTest.kt
- PftSymbol
- CollapsingHeaderScaffoldTest
- Previews, the token sheet and screenshot goldens
- TokenSheetScreenshotTest.kt
- PaiseParserTest.kt
- CollapsingHeaderScaffold.kt
- Security, privacy and integrity requirements (SEC/INT)
- ColorSchemeTestSupport.kt
- TokenSheetSections.kt
- DesignCatalog.kt
- PftTypographyTest
- UserId
- PftShapesTest.kt
- GlyphSourceTest
- RoomAuditedWriterIntegrationTest.kt
- Project README
- DeviceId
- PftMotion.kt
- CategoryGlyphScreenshotTest
- 3. Design & decision
- CLAUDE.md Development Constitution
- AppSetting
- HostedComposeRule
- Slice A — Foundation (`feat/m1a-foundation`)
- BottomActionBarTest.kt
- CategoryGlyph
- RoomAuditColumnsTest
- EntityMeta
- MainActivity
- CollapsingHeaderScaffold
- MoneyText
- Personal Finance Tracker PRD
- AuditEntryMapperTest
- redact_text
- PftSymbolTest
- PftSpacingTest
- SampleThing.kt
- Pseudo
- GroupedSection.kt
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- Collecting and Redacting Sample Messages Guide
- AuditEntryEntity
- _Text
- PftThemeModeTest
- import_symbols.py
- BottomActionBar.kt
- HeaderPolicy
- PftTheme.kt
- CategoryGlyph.kt
- EmptyStateTest.kt
- AuditTriggersTest
- dp
- Plan: M1a design system and visual direction (lands before A5)
- UserProfileEntity
- HeaderSlot
- HeaderPhase
- MigrationTestSupport
- AppSettingEntity
- PftTypography.kt
- SampleId
- MoneyTextTest.kt
- ContentScreenshotTest
- How to run and test
- RoomEntityRepository
- GroupedSectionTest
- ThemePrefs
- Plan: M1a — Core ledger (manual-entry household tracker)
- Paise
- `data` (Room persistence, A4a)
- 7. Test plan
- .format
- BrandColors
- Migrations.kt
- Immutable Audit Trail
- Loans and EMIs (LN-1..LN-5)
- WindowThemeTest
- BundledFontTest
- parse_svg
- PaiseParser
- InMemoryEntityStore
- modifier
- Architecture overview
- MoneySize
- BundledSymbolsLicenceTest
- MoneyColors
- ThemeResolutionTest.kt
- Design system
- Icons and emoji (DS3, UI-6)
- CategoryPaletteTest
- 3.9 Scope: app-wide
- BaseDao
- parse_manifest
- ChipColors
- UnsafeSvg

## God Nodes (most connected - your core abstractions)
1. `PftSymbol` - 113 edges
2. `Paise` - 90 edges
3. `ThemePrefs` - 78 edges
4. `CollapsingHeaderScaffoldTest` - 70 edges
5. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 69 edges
6. `UserId` - 57 edges
7. `runSuspending()` - 54 edges
8. `How to run and test` - 52 edges
9. `MoneyText()` - 50 edges
10. `EntityMeta` - 50 edges

## Surprising Connections (you probably didn't know these)
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `3.4 Header glow (UI-13, DS6d)` --references--> `HeaderPolicy`  [INFERRED]
  docs/plans/2026-10-30-m1a-premium-look.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/CollapsingHeaderScaffold.kt
- ``StatusChip`` --references--> `StatusChip()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/StatusChip.kt
- `Planned: premium look (DS6)` --references--> `BrandColors`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/BrandColors.kt
- `Adding an icon` --references--> `PftSymbol`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/PftSymbol.kt

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

## Communities (125 total, 58 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (12): _day(), _email_record(), html_to_text(), main(), process(), read_adb(), read_any(), read_eml() (+4 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.16
Nodes (15): Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup, Secrets via Environment Variables (+7 more)

### Community 2 - "runSuspending"
Cohesion: 0.06
Nodes (7): FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAppSettingsRepositoryTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomTransactionRunnerTest, runSuspending()

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.14
Nodes (9): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits) (+1 more)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "InMemoryEntityRepository"
Cohesion: 0.09
Nodes (7): Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), InMemoryAuditLog, InMemoryEntityRepository, InMemoryLedgerStore, AuditFieldsCheck, haveCompleteAuditFields(), shouldHaveCompleteAuditFields()

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.21
Nodes (4): ColorContrastTest, Theme, DARK, LIGHT

### Community 16 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.19
Nodes (19): Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense, Income (+11 more)

### Community 18 - "detekt.yml"
Cohesion: 0.15
Nodes (12): complexity, LongParameterList, active, imports, ignoreAnnotated, ignoreDefaultParameters, excludes, naming (+4 more)

### Community 27 - "PftSymbol"
Cohesion: 0.02
Nodes (93): PftSymbol, ACCOUNT_BALANCE, ACCOUNT_BALANCE_WALLET, ADD, BACK, BAKERY_DINING, BEACH_ACCESS, BED (+85 more)

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.09
Nodes (5): BottomActionBar(), BottomBarAction, CollapsingHeaderScaffoldTest, Steps, One-hand layout (UI-7)

### Community 29 - "Previews, the token sheet and screenshot goldens"
Cohesion: 0.14
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 31 - "PaiseParserTest.kt"
Cohesion: 0.17
Nodes (4): How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest

### Community 33 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "DesignCatalog.kt"
Cohesion: 0.09
Nodes (19): CatalogSection, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, GLYPHS, MONEY, MOTION (+11 more)

### Community 38 - "UserId"
Cohesion: 0.08
Nodes (26): AuditDaoTest, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE, UNARCHIVE (+18 more)

### Community 40 - "GlyphSourceTest"
Cohesion: 0.15
Nodes (7): Emoji, GlyphSource, isEmojiValue(), isSymbolKey(), Letter, Symbol, GlyphSourceTest

### Community 41 - "RoomAuditedWriterIntegrationTest.kt"
Cohesion: 0.08
Nodes (8): Data it owns, EpochMillis, EpochMillisTest, TestDatabase, NameBasedId, clockAt(), TestClock, Conventions

### Community 42 - "Project README"
Cohesion: 0.21
Nodes (6): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Code Structure Map, core/shared Shared Packages, Project README, Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "DeviceId"
Cohesion: 0.09
Nodes (10): RoomIdentityRepository, RoomIdentityRepositoryTest, AuditedTable, AuditedTables, Auditable, Device, IdentityRepository, UserProfile (+2 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.10
Nodes (3): PftMotion, pressScale(), Principles

### Community 45 - "CategoryGlyphScreenshotTest"
Cohesion: 0.24
Nodes (5): CategoryGlyphPreview(), CategoryGlyphSamples(), CategoryGlyphSymbolSamples(), SampleRow(), CategoryGlyphScreenshotTest

### Community 46 - "3. Design & decision"
Cohesion: 0.20
Nodes (9): PftSpacing, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens, 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins") (+1 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.11
Nodes (24): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+16 more)

### Community 48 - "AppSetting"
Cohesion: 0.14
Nodes (7): RoomAuditLog, RoomTransactionRunner, RoomAppSettingsRepository, AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository, The core/app boundary

### Community 49 - "HostedComposeRule"
Cohesion: 0.20
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 50 - "Slice A — Foundation (`feat/m1a-foundation`)"
Cohesion: 0.06
Nodes (26): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How to run and test, Purpose, AlreadyInitialised, AmountNotPositive, AmountTooLarge (+18 more)

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (3): Probe, RoomAuditColumnsTest, AuditColumnsCheck

### Community 54 - "EntityMeta"
Cohesion: 0.07
Nodes (24): EntityMeta, LedgerEntity, EntityId, AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note (+16 more)

### Community 55 - "MainActivity"
Cohesion: 0.20
Nodes (13): `:app` — Android application, Configuration, Debug and release source sets, Dependencies, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose, Responsibilities and boundaries (+5 more)

### Community 56 - "CollapsingHeaderScaffold"
Cohesion: 0.26
Nodes (14): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle, Dashboard (+6 more)

### Community 57 - "MoneyText"
Cohesion: 0.15
Nodes (13): `ui/common` (shared components, DS3, DS4 and DS5), atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), moneySign() (+5 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.11
Nodes (22): Personal Finance Tracker PRD, Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope, Idempotent ingestion (X-2) (+14 more)

### Community 59 - "AuditEntryMapperTest"
Cohesion: 0.17
Nodes (6): AuditEntryMapper, AuditEntryMapperTest, Data model, The audit trail, Version 1 (A4a), Version table

### Community 60 - "redact_text"
Cohesion: 0.33
Nodes (4): mask_run(), redact_text(), amt(), hold()

### Community 63 - "SampleThing.kt"
Cohesion: 0.21
Nodes (6): SampleDatabase, SampleThing, SampleThingDao, SampleThingEntity, SampleThingMapper, SampleThingRepository

### Community 65 - "GroupedSection.kt"
Cohesion: 0.13
Nodes (14): GroupedRow, GroupedSection(), groupedSectionColor(), GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, Content primitives (DS5), `EmptyState` (+6 more)

### Community 66 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.08
Nodes (24): RoomTestKit, Behaviours worth knowing, Configuration, How an entity step composes the blocks, Public interface, Responsibilities & boundaries, AuditDiff, AuditedWriter (+16 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 73 - "import_symbols.py"
Cohesion: 0.15
Nodes (4): download(), main(), to_vector_drawable(), DrawableTest

### Community 76 - "PftTheme.kt"
Cohesion: 0.10
Nodes (5): MotionSection(), SpringTrack(), EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder()

### Community 77 - "CategoryGlyph.kt"
Cohesion: 0.14
Nodes (4): fixedSp(), GlyphSize, Chip, Hero

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.12
Nodes (14): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5.1 Launch and theme resolution, 5.3 Collapsing header (+6 more)

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
Cohesion: 0.11
Nodes (3): AuditTriggers, MigrationTestSupport, MigrationTest

### Community 87 - "PftTypography.kt"
Cohesion: 0.16
Nodes (4): pftMaterialTypography(), pftType(), PftTypography, Theming

### Community 89 - "MoneyTextTest.kt"
Cohesion: 0.12
Nodes (5): layoutResult(), shownText(), assertShownInFull(), isShownInFull(), layout()

### Community 94 - "ThemePrefs"
Cohesion: 0.13
Nodes (10): GroupedSectionPreview(), GroupedSectionSamples(), StatusChip(), ThemeMode, DARK, LIGHT, SYSTEM, ThemePrefs (+2 more)

### Community 95 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.10
Nodes (21): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 3.1 Modules (Gradle), 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3.5 Design system (owner decision, 2026-10-29) (+13 more)

### Community 97 - "`data` (Room persistence, A4a)"
Cohesion: 0.31
Nodes (4): `data` (Room persistence, A4a), MetaColumns, MetaMapper, MetaMapperTest

### Community 98 - "7. Test plan"
Cohesion: 0.32
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 99 - ".format"
Cohesion: 0.18
Nodes (10): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+2 more)

### Community 100 - "BrandColors"
Cohesion: 0.11
Nodes (15): `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4), BrandColors, resolveColorScheme(), 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema (+7 more)

### Community 103 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5), M1a Core ledger (+2 more)

### Community 106 - "parse_svg"
Cohesion: 0.35
Nodes (3): parse_svg(), ParseSvgTest, svg()

### Community 109 - "modifier"
Cohesion: 0.18
Nodes (5): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples(), DevContent()

### Community 110 - "Architecture overview"
Cohesion: 0.33
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 111 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 113 - "MoneyColors"
Cohesion: 0.24
Nodes (7): MoneyColors, displayFamily(), DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, 2. Current state

### Community 115 - "Design system"
Cohesion: 0.18
Nodes (10): Adding a token, Build-time trust decisions, Colour, Design system, Motion (UI-10), Planned: premium look (DS6), Shapes, Spacing (+2 more)

### Community 116 - "Icons and emoji (DS3, UI-6)"
Cohesion: 0.31
Nodes (6): CategoryColors, CategoryPalette, pair(), Adding an icon, Icons and emoji (DS3, UI-6), The curated list

### Community 118 - "3.9 Scope: app-wide"
Cohesion: 0.25
Nodes (8): 3.2 Surfaces: tonal or flat (DS6c), 3.4 Header glow (UI-13, DS6d), 3.5 Icon-only header actions (UI-14, DS6e; needs DS3), 3.6 Type pairing and optical size per role (UI-15, DS6g), 3.7 Bundled sans fonts (UI-16, UI-17; DS6f spike, then DS6h, DS6i), 3.8 Abstraction bar, 3.9 Scope: app-wide, 3. Design & decision

## Knowledge Gaps
- **178 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+173 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 528 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **58 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `How to run and test` to `runSuspending`, `ManifestHardeningTest`, `PressScaleTest.kt`, `MoneyTextTest`, `ColorContrastTest`, `NavTransitionTest.kt`, `PftSymbol`, `CollapsingHeaderScaffoldTest`, `Previews, the token sheet and screenshot goldens`, `TokenSheetScreenshotTest.kt`, `PftTypographyTest`, `UserId`, `PftShapesTest.kt`, `GlyphSourceTest`, `DeviceId`, `CategoryGlyphScreenshotTest`, `BottomActionBarTest.kt`, `CategoryGlyph`, `RoomAuditColumnsTest`, `MainActivity`, `MoneyText`, `AuditEntryMapperTest`, `PftSymbolTest`, `PftSpacingTest`, `SampleThing.kt`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `PftThemeModeTest`, `EmptyStateTest.kt`, `AuditTriggersTest`, `dp`, `MigrationTestSupport`, `SampleId`, `ContentScreenshotTest`, `GroupedSectionTest`, `ThemePrefs`, `Paise`, ``data` (Room persistence, A4a)`, `7. Test plan`, `.format`, `WindowThemeTest`, `BundledFontTest`, `BundledSymbolsLicenceTest`, `CategoryPaletteTest`?**
  _High betweenness centrality (0.264) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `PftSymbol` (e.g. with `How to run and test` and ``ui/common` (shared components, DS3, DS4 and DS5)`) actually correct?**
  _`PftSymbol` has 13 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _178 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `runSuspending` be split into smaller, more focused modules?**
  _Cohesion score 0.06288448393711552 - nodes in this community are weakly interconnected._
- **Why does `Paise` connect `Paise` to `runSuspending`, `InMemoryEntityRepository`, `MoneyTextTest`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `CollapsingHeaderScaffoldTest`, `Previews, the token sheet and screenshot goldens`, `RoomAuditedWriterIntegrationTest.kt`, `DeviceId`, `Slice A — Foundation (`feat/m1a-foundation`)`, `EntityMeta`, `MoneyText`, `SampleThing.kt`, `3.4 Reuse by layer (owner decision, 2026-10-28)`, `SampleId`, `MoneyTextTest.kt`, `ThemePrefs`, `Plan: M1a — Core ledger (manual-entry household tracker)`, `.format`, `PaiseParser`, `modifier`, `MoneySize`?**
  _High betweenness centrality (0.140) - this node is a cross-community bridge._
- **Are the 14 inferred relationships involving `Paise` (e.g. with ``ui/common` (shared components, DS3, DS4 and DS5)` and `Behaviours worth knowing`) actually correct?**
  _`Paise` has 14 INFERRED edges - model-reasoned connections that need verification._
- **Should `Personal Finance Tracker Learnings` be split into smaller, more focused modules?**
  _Cohesion score 0.14285714285714285 - nodes in this community are weakly interconnected._