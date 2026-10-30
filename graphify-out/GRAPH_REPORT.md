# Graph Report - personal-finance-tracker  (2026-10-30)

## Corpus Check
- 174 files · ~108,162 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 30 file(s) not represented in the graph (top: .xml 10, .lockfile 8, (none) 4)

## Summary
- 1826 nodes · 4572 edges · 98 communities (51 shown, 47 thin omitted)
- Extraction: 89% EXTRACTED · 11% INFERRED · 0% AMBIGUOUS · INFERRED: 520 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `1796c7b2`
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
- LedgerEntity
- PressScaleTest.kt
- PftThemeModeTest
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- MoneyColors
- Catalog.kt
- detekt.yml
- NavTransitionTest.kt
- Public interface
- CollapsingHeaderScaffoldTest
- HeaderScreenshotTest
- TokenSheetScreenshotTest.kt
- Uuid7Test.kt
- CollapsingHeaderScaffold.kt
- Security, privacy and integrity requirements (SEC/INT)
- ColorSchemeTestSupport.kt
- TokenSheetSections.kt
- DesignCatalog.kt
- PftTypographyTest
- AuditEntry
- PftShapesTest.kt
- TransactionRunner
- TestClock
- Project README
- UserProfile
- PftMotion.kt
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- BottomActionBar
- CLAUDE.md Development Constitution
- AppSetting
- HostedComposeRule
- LedgerError
- PressScaleTest.kt
- Leaky
- RoomAuditColumnsTest
- AuditFieldsCheckTest.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- CollapsingHeaderScaffold
- Widget
- Personal Finance Tracker PRD
- AuditEntryMapperTest.kt
- redact_text
- InMemoryEntityRepository
- PftSpacingTest
- SampleThing.kt
- Pseudo
- assertequals
- IdentityService
- Collecting and Redacting Sample Messages Guide
- PftDatabase
- RoomAuditedWriterIntegrationTest.kt
- _Text
- UserProfileEntity
- BottomActionBar.kt
- HeaderPolicy
- MotionSection.kt
- HeaderSamples.kt
- ThemePrefs
- AuditTriggersTest
- expandedHeaderHeight
- Paise
- UserProfileEntity
- HeaderSlot
- HeaderPhase
- MigrationTestSupport
- AppSettingEntity
- AuditDaoTest.kt
- Responsibilities & boundaries
- PftDatabase
- RoomEntityRepository
- PftDatabaseTest
- PftTheme.kt
- BaseDao
- TokenSheet
- BrandColors
- Migrations.kt
- Immutable Audit Trail

## God Nodes (most connected - your core abstractions)
1. `CollapsingHeaderScaffoldTest` - 64 edges
2. `UserId` - 57 edges
3. `runSuspending()` - 54 edges
4. `EntityMeta` - 50 edges
5. `3.4 Reuse by layer (owner decision, 2026-10-28)` - 49 edges
6. `DeviceId` - 48 edges
7. `Public interface` - 42 edges
8. `Paise` - 41 edges
9. `AuditEntry` - 38 edges
10. `How to run and test` - 36 edges

## Surprising Connections (you probably didn't know these)
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `9. Documentation updates (all required surfaces)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt
- `Slice H — App lock, diagnostics, release (`feat/m1a-hardening`)` --references--> `TokenSheet()`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/TokenSheet.kt
- `3.6 Icons and emoji (UI-6)` --references--> `ManifestHardeningTest`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/test/kotlin/dev/imagh/pft/app/ManifestHardeningTest.kt
- `8. Security considerations` --references--> `ManifestHardeningTest`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/test/kotlin/dev/imagh/pft/app/ManifestHardeningTest.kt

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

## Communities (98 total, 47 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.13
Nodes (9): _day(), _email_record(), html_to_text(), read_adb(), read_any(), read_eml(), read_jsonl(), read_mbox() (+1 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.15
Nodes (16): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+8 more)

### Community 2 - "runSuspending"
Cohesion: 0.05
Nodes (13): How to run and test, RoomEntityRepositoryTest, FailingOnNthAppend, IdentityRoomIntegrationTest, RoomAuditedWriterIntegrationTest, AuditLog, RoomTransactionRunnerTest, runSuspending() (+5 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.14
Nodes (9): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits) (+1 more)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "ManifestHardeningTest"
Cohesion: 0.08
Nodes (8): ManifestHardeningTest, WindowThemeTest, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.10
Nodes (5): BundledFontTest, ColorContrastTest, Theme, DARK, LIGHT

### Community 16 - "MoneyColors"
Cohesion: 0.14
Nodes (10): `ui/theme` (design-system tokens, DS1, DS2, DS4), MoneyColors, PftTypography, Adding a token, Build-time trust decisions, Design system, Principles, Theming (+2 more)

### Community 18 - "detekt.yml"
Cohesion: 0.20
Nodes (9): complexity, LongParameterList, ignoreAnnotated, ignoreDefaultParameters, excludes, naming, FunctionNaming, style (+1 more)

### Community 27 - "Public interface"
Cohesion: 0.26
Nodes (4): How an entity step composes the blocks, Public interface, Identity, Validation

### Community 29 - "HeaderScreenshotTest"
Cohesion: 0.19
Nodes (5): DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 31 - "Uuid7Test.kt"
Cohesion: 0.05
Nodes (25): NameBasedIdTest, clockAt(), Uuid7Test, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest, 10. Risks & mitigations (+17 more)

### Community 33 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.20
Nodes (12): TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle(), ShapesAndSpacing() (+4 more)

### Community 36 - "DesignCatalog.kt"
Cohesion: 0.15
Nodes (10): CatalogSection, BOTTOM_BAR, DASHBOARD_HEADER, MOTION, STANDARD_HEADER, TOKENS, ChoiceRow(), DesignCatalog() (+2 more)

### Community 37 - "PftTypographyTest"
Cohesion: 0.09
Nodes (5): displayFamily(), pftMaterialTypography(), pftType(), PftTypographyTest, Resolved

### Community 38 - "AuditEntry"
Cohesion: 0.12
Nodes (22): AuditedTables, FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE, UNARCHIVE (+14 more)

### Community 40 - "TransactionRunner"
Cohesion: 0.12
Nodes (5): RoomTransactionRunner, TransactionRunner, InMemoryEntityStore, InMemoryLedgerStore, The core/app boundary

### Community 42 - "Project README"
Cohesion: 0.21
Nodes (6): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Code Structure Map, core/shared Shared Packages, Project README, Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "UserProfile"
Cohesion: 0.10
Nodes (6): RoomIdentityRepository, RoomIdentityRepositoryTest, Device, IdentityRepository, UserProfile, InMemoryIdentityRepository

### Community 44 - "PftMotion.kt"
Cohesion: 0.10
Nodes (8): PftMotion, pressScale(), 6.1 Design-system slice: PRs into `feat/m1a-foundation`, 6.2 Appearance slice: base `feat/m1a-appearance` off `main`, after A-final (Q4), 6.3 Amendments to M1a steps (consumption; this plan edits M1a when approved), 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), main()

### Community 45 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.19
Nodes (11): Behaviours worth knowing, AuditedWriter, AuditSource, MANUAL, RECURRING, SYSTEM, EntityRepository, IdentityCorrupt (+3 more)

### Community 46 - "BottomActionBar"
Cohesion: 0.12
Nodes (15): BottomActionBar(), BottomBarAction, PftSpacing, 3.1 Modules (Gradle), 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3.5 Design system (owner decision, 2026-10-29), 3. Design & decision, 3.1 Visual direction: "Cashew, but premium" (+7 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "AppSetting"
Cohesion: 0.12
Nodes (8): `data` (Room persistence, A4a), MetaMapper, RoomAppSettingsRepository, RoomAppSettingsRepositoryTest, AppSetting, AppSettingsRepository, SettingKeys, InMemoryAppSettingsRepository

### Community 49 - "HostedComposeRule"
Cohesion: 0.20
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 50 - "LedgerError"
Cohesion: 0.09
Nodes (16): Auditable, AlreadyInitialised, AmountNotPositive, AmountTooLarge, Archived, BlankName, Deleted, InvalidSettingKey (+8 more)

### Community 52 - "Leaky"
Cohesion: 0.21
Nodes (6): AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note, NoteId

### Community 53 - "RoomAuditColumnsTest"
Cohesion: 0.14
Nodes (4): Probe, RoomAuditColumnsTest, AuditColumnsCheck, AuditedTable

### Community 54 - "AuditFieldsCheckTest.kt"
Cohesion: 0.09
Nodes (17): MetaMapperTest, EntityMeta, LedgerEntity, AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind (+9 more)

### Community 55 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.14
Nodes (12): `:app` — Android application, Configuration, Debug and release source sets, Dependencies, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose, Responsibilities and boundaries (+4 more)

### Community 56 - "CollapsingHeaderScaffold"
Cohesion: 0.25
Nodes (13): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle, Dashboard (+5 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.08
Nodes (32): Personal Finance Tracker PRD, Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Automatic capture from Email (E-0..E-5) (+24 more)

### Community 60 - "redact_text"
Cohesion: 0.27
Nodes (6): mask_run(), process(), redact_text(), amt(), hold(), review_flags()

### Community 61 - "InMemoryEntityRepository"
Cohesion: 0.29
Nodes (3): Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), InMemoryEntityRepository, shouldHaveCompleteAuditFields()

### Community 63 - "SampleThing.kt"
Cohesion: 0.25
Nodes (4): MetaColumns, SampleDatabase, SampleThingDao, SampleThingEntity

### Community 65 - "assertequals"
Cohesion: 0.24
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 66 - "IdentityService"
Cohesion: 0.12
Nodes (12): RoomTestKit, Configuration, AuditDiff, AuditLog, AuditRecorder, IdentityService, Uuid7, SettingsService (+4 more)

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 69 - "RoomAuditedWriterIntegrationTest.kt"
Cohesion: 0.12
Nodes (7): EpochMillis, How to run and test, EntityMetaTest, IdentityServiceTest, IdsTest, SettingsServiceTest, ValidationTest

### Community 76 - "MotionSection.kt"
Cohesion: 0.15
Nodes (3): MotionSection(), SpringTrack(), DevContent()

### Community 78 - "ThemePrefs"
Cohesion: 0.24
Nodes (9): DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, ThemeMode, DARK, LIGHT, SYSTEM (+1 more)

### Community 81 - "Paise"
Cohesion: 0.06
Nodes (26): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+18 more)

### Community 83 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 84 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 85 - "MigrationTestSupport"
Cohesion: 0.07
Nodes (11): Data it owns, AuditTriggers, EpochMillisTest, MigrationTestSupport, MigrationTest, NameBasedId, Conventions, Data model (+3 more)

### Community 88 - "Responsibilities & boundaries"
Cohesion: 0.33
Nodes (5): `:core:ledger` — the ledger foundations, Data it owns, Dependencies, Purpose, Responsibilities & boundaries

### Community 93 - "PftDatabaseTest"
Cohesion: 0.12
Nodes (3): PftDatabaseTest, `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`()

### Community 100 - "BrandColors"
Cohesion: 0.17
Nodes (8): BrandColors, Colour, Motion (UI-10), One-hand layout (UI-7), Shapes, Spacing, Tokens, Typography (UI-2, UI-3, owner decision Q10)

## Knowledge Gaps
- **70 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+65 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 408 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **47 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `How to run and test` connect `runSuspending` to `ManifestHardeningTest`, `PressScaleTest.kt`, `PftThemeModeTest`, `ColorContrastTest`, `NavTransitionTest.kt`, `CollapsingHeaderScaffoldTest`, `HeaderScreenshotTest`, `TokenSheetScreenshotTest.kt`, `PftTypographyTest`, `PftShapesTest.kt`, `UserProfile`, `AppSetting`, `PressScaleTest.kt`, `RoomAuditColumnsTest`, `AuditFieldsCheckTest.kt`, `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `AuditEntryMapperTest.kt`, `PftSpacingTest`, `assertequals`, `IdentityService`, `AuditTriggersTest`, `expandedHeaderHeight`, `MigrationTestSupport`, `AuditDaoTest.kt`, `PftDatabase`?**
  _High betweenness centrality (0.256) - this node is a cross-community bridge._
- **Are the 5 inferred relationships involving `CollapsingHeaderScaffoldTest` (e.g. with `How to run and test` and `One-hand layout (UI-7)`) actually correct?**
  _`CollapsingHeaderScaffoldTest` has 5 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _70 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.13405797101449277 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Collecting and Redacting Sample Messages Guide`, `Automatic capture from SMS (S-1..S-13)`, ``build-logic` — convention plugins`, `CLAUDE.md Development Constitution`, `Paise`, `Responsibilities & boundaries`, `Personal Finance Tracker PRD`?**
  _High betweenness centrality (0.163) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `UserId` (e.g. with `Public interface` and `3.4 Reuse by layer (owner decision, 2026-10-28)`) actually correct?**
  _`UserId` has 2 INFERRED edges - model-reasoned connections that need verification._
- **Should `runSuspending` be split into smaller, more focused modules?**
  _Cohesion score 0.05482456140350877 - nodes in this community are weakly interconnected._