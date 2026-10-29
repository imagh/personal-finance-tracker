# Graph Report - personal-finance-tracker  (2026-10-30)

## Corpus Check
- 131 files · ~93,214 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 29 file(s) not represented in the graph (top: .xml 10, .lockfile 8, (none) 4)

## Summary
- 1411 nodes · 3284 edges · 86 communities (53 shown, 33 thin omitted)
- Extraction: 87% EXTRACTED · 13% INFERRED · 0% AMBIGUOUS · INFERRED: 425 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `eaac19f7`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- Loans and EMIs (LN-1..LN-5)
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- WindowThemeTest.kt
- hooks
- RedactTests
- AuditFieldsCheck.kt
- PftTheme.kt
- Plan: M1a — Core ledger (manual-entry household tracker)
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- BrandColors
- Catalog.kt
- detekt.yml
- NavTransitionTest.kt
- Public interface
- CollapsingHeaderScaffoldTest
- HeaderScreenshotTest
- Paise
- Uuid7Test.kt
- CollapsingHeaderScaffold.kt
- Security, privacy and integrity requirements (SEC/INT)
- ColorSchemeTestSupport.kt
- TokenSheetSections.kt
- DesignCatalog.kt
- PftTypographyTest
- UserId
- PftShapesTest.kt
- SettingsService
- instant
- Project README
- UserProfile
- PftMotion.kt
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- MoneyColors
- CLAUDE.md Development Constitution
- IdentityService.kt
- LedgerError
- BottomActionBarTest.kt
- AuditedWriterTest.kt
- PressScaleTest.kt
- EntityMeta
- How to run and test
- CollapsingHeaderScaffold
- 3. Design & decision
- Personal Finance Tracker PRD
- InMemoryEntityStore
- redact_text
- InMemoryEntityRepository
- Architecture overview
- 6. Implementation steps (TDD-ordered)
- Pseudo
- assertequals
- Uuid7
- Collecting and Redacting Sample Messages Guide
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- IdentityServiceTest.kt
- _Text
- .onCreate
- main
- BottomActionBar.kt
- HeaderPolicy
- MotionSection.kt
- modifier
- ThemePrefs
- ManifestHardeningTest
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- HeaderSamples.kt
- HeaderSlot
- HeaderPhase
- DevContent

## God Nodes (most connected - your core abstractions)
1. `CollapsingHeaderScaffoldTest` - 64 edges
2. `Public interface` - 44 edges
3. `EntityMeta` - 37 edges
4. `ThemePrefs` - 34 edges
5. `UserId` - 34 edges
6. `3.4 Reuse by layer (owner decision, 2026-10-28)` - 34 edges
7. `Paise` - 31 edges
8. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 31 edges
9. `HostedComposeRule` - 29 edges
10. `DeviceId` - 28 edges

## Surprising Connections (you probably didn't know these)
- `9. Documentation updates (all required surfaces)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt
- `Security posture of the shell` --references--> `ManifestHardeningTest`  [INFERRED]
  docs/architecture/overview.md → app/src/test/kotlin/dev/imagh/pft/app/ManifestHardeningTest.kt
- `3.6 Icons and emoji (UI-6)` --references--> `ManifestHardeningTest`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/test/kotlin/dev/imagh/pft/app/ManifestHardeningTest.kt
- `8. Security considerations` --references--> `ManifestHardeningTest`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/test/kotlin/dev/imagh/pft/app/ManifestHardeningTest.kt
- `Third-party assets` --references--> `BundledFontTest`  [INFERRED]
  docs/architecture/design-system.md → app/src/test/kotlin/dev/imagh/pft/app/ui/theme/BundledFontTest.kt

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

## Communities (86 total, 33 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (8): _day(), _email_record(), html_to_text(), read_adb(), read_any(), read_eml(), read_mbox(), read_sms_xml()

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.19
Nodes (12): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5) (+4 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.25
Nodes (4): Personal Finance Tracker Learnings, graphify tool, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 9 - "PftTheme.kt"
Cohesion: 0.07
Nodes (5): resolveColorScheme(), RegisterHostActivity, Statement, `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`()

### Community 10 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.15
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 3.1 Modules (Gradle), 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3.5 Design system (owner decision, 2026-10-29) (+5 more)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.09
Nodes (6): BundledFontTest, ColorContrastTest, Theme, DARK, LIGHT, NameBasedId

### Community 16 - "BrandColors"
Cohesion: 0.17
Nodes (8): BrandColors, Colour, Motion (UI-10), One-hand layout (UI-7), Shapes, Spacing, Tokens, Typography (UI-2, UI-3, owner decision Q10)

### Community 18 - "detekt.yml"
Cohesion: 0.20
Nodes (9): complexity, LongParameterList, ignoreAnnotated, ignoreDefaultParameters, excludes, naming, FunctionNaming, style (+1 more)

### Community 26 - "NavTransitionTest.kt"
Cohesion: 0.08
Nodes (3): NavTransitionTest, PftThemeModeTest, TokenSheetScreenshotTest

### Community 27 - "Public interface"
Cohesion: 0.27
Nodes (5): How an entity step composes the blocks, Public interface, Identity, IdentityService, Validation

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.08
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "HeaderScreenshotTest"
Cohesion: 0.18
Nodes (5): DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 30 - "Paise"
Cohesion: 0.09
Nodes (14): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+6 more)

### Community 31 - "Uuid7Test.kt"
Cohesion: 0.08
Nodes (8): NameBasedIdTest, clockAt(), Uuid7Test, TestClock, How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest

### Community 33 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.20
Nodes (11): TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle(), ShapesAndSpacing() (+3 more)

### Community 36 - "DesignCatalog.kt"
Cohesion: 0.12
Nodes (11): CatalogSection, BOTTOM_BAR, DASHBOARD_HEADER, MOTION, STANDARD_HEADER, TOKENS, ChoiceRow(), DesignCatalog() (+3 more)

### Community 37 - "PftTypographyTest"
Cohesion: 0.10
Nodes (5): displayFamily(), pftMaterialTypography(), pftType(), PftTypographyTest, Resolved

### Community 38 - "UserId"
Cohesion: 0.11
Nodes (25): FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE, UNARCHIVE, UPDATE (+17 more)

### Community 40 - "SettingsService"
Cohesion: 0.12
Nodes (14): Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, Purpose, Responsibilities & boundaries, Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), AuditLog (+6 more)

### Community 42 - "Project README"
Cohesion: 0.22
Nodes (6): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Code Structure Map, core/shared Shared Packages, Project README, Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "UserProfile"
Cohesion: 0.18
Nodes (4): Device, IdentityRepository, UserProfile, InMemoryIdentityRepository

### Community 44 - "PftMotion.kt"
Cohesion: 0.11
Nodes (3): PftMotion, pressScale(), 3.7 Component inventory (UI-9)

### Community 45 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.20
Nodes (9): Behaviours worth knowing, AuditedWriter, AuditRecorder, EntityRepository, IdentityCorrupt, LedgerTestKit, 3.4 Reuse by layer (owner decision, 2026-10-28), Slice B — Accounts, categories, tags (`feat/m1a-ledger-setup`) (+1 more)

### Community 46 - "MoneyColors"
Cohesion: 0.14
Nodes (10): `ui/theme` (design-system tokens, DS1, DS2, DS4), MoneyColors, PftTypography, Adding a token, Build-time trust decisions, Design system, Principles, Theming (+2 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.10
Nodes (24): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+16 more)

### Community 48 - "IdentityService.kt"
Cohesion: 0.27
Nodes (4): AuditDiff, AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository

### Community 50 - "LedgerError"
Cohesion: 0.09
Nodes (17): Auditable, AlreadyInitialised, AmountNotPositive, AmountTooLarge, Archived, BlankName, Deleted, InvalidSettingKey (+9 more)

### Community 52 - "AuditedWriterTest.kt"
Cohesion: 0.21
Nodes (6): AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note, NoteId

### Community 54 - "EntityMeta"
Cohesion: 0.09
Nodes (18): EntityMeta, LedgerEntity, AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind, LARGE (+10 more)

### Community 55 - "How to run and test"
Cohesion: 0.17
Nodes (15): `:app` — Android application, Configuration, Data it owns, Debug and release source sets, Dependencies, How to run and test, Public interface, Purpose (+7 more)

### Community 56 - "CollapsingHeaderScaffold"
Cohesion: 0.26
Nodes (14): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle, Dashboard (+6 more)

### Community 57 - "3. Design & decision"
Cohesion: 0.22
Nodes (8): PftSpacing, 3.1 Visual direction: "Cashew, but premium", 3.3 Tokens, 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins"), 3. Design & decision

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.10
Nodes (24): Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Personal Finance Tracker PRD, Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Delivery constraints (small vertical slices, Claude Pro limits), Automatic capture from Email (E-0..E-5) (+16 more)

### Community 60 - "redact_text"
Cohesion: 0.27
Nodes (6): mask_run(), process(), redact_text(), amt(), hold(), review_flags()

### Community 62 - "Architecture overview"
Cohesion: 0.29
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 63 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.29
Nodes (7): 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice H — App lock, diagnostics, release (`feat/m1a-hardening`)

### Community 65 - "assertequals"
Cohesion: 0.24
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 68 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.29
Nodes (3): HostedComposeRule, PftSpacingTest, 6.1 Design-system slice: PRs into `feat/m1a-foundation`

### Community 69 - "IdentityServiceTest.kt"
Cohesion: 0.16
Nodes (6): How to run and test, EntityMetaTest, IdentityServiceTest, IdsTest, SettingsServiceTest, 7. Test plan

### Community 72 - ".onCreate"
Cohesion: 0.29
Nodes (5): 5.1 Launch and theme resolution, 5.3 Collapsing header, 5.4 Custom font import (AP2), 5.5 Mockups (wireframes; the styled version is in the HTML file), 5. Flows

### Community 73 - "main"
Cohesion: 0.20
Nodes (3): PR breakdown (incremental — required), main(), read_jsonl()

### Community 74 - "BottomActionBar.kt"
Cohesion: 0.18
Nodes (3): BottomActionBar(), BottomActionBarPreview(), BottomActionBarSamples()

### Community 78 - "ThemePrefs"
Cohesion: 0.24
Nodes (9): DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, ThemeMode, DARK, LIGHT, SYSTEM (+1 more)

### Community 79 - "ManifestHardeningTest"
Cohesion: 0.33
Nodes (3): Manifest security posture (SEC-2, SEC-3, SEC-6), Responsibilities and boundaries, ManifestHardeningTest

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.20
Nodes (8): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 8. Security considerations, 9. Documentation updates (all required surfaces), Plan: M1a design system and visual direction (lands before A5)

### Community 83 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 84 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

## Knowledge Gaps
- **70 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+65 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 388 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **33 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `6.1 Design-system slice: PRs into `feat/m1a-foundation`` connect `6.1 Design-system slice: PRs into `feat/m1a-foundation`` to `WindowThemeTest.kt`, `ColorContrastTest`, `BrandColors`, `NavTransitionTest.kt`, `CollapsingHeaderScaffoldTest`, `Paise`, `DesignCatalog.kt`, `PftTypographyTest`, `PftShapesTest.kt`, `SettingsService`, `PftMotion.kt`, `MoneyColors`, `BottomActionBarTest.kt`, `PressScaleTest.kt`, `How to run and test`, `CollapsingHeaderScaffold`, `assertequals`, `main`, `BottomActionBar.kt`, `ThemePrefs`, `ManifestHardeningTest`?**
  _High betweenness centrality (0.196) - this node is a cross-community bridge._
- **Are the 5 inferred relationships involving `CollapsingHeaderScaffoldTest` (e.g. with `How to run and test` and `One-hand layout (UI-7)`) actually correct?**
  _`CollapsingHeaderScaffoldTest` has 5 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _70 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.14761904761904762 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Collecting and Redacting Sample Messages Guide`, `Automatic capture from SMS (S-1..S-13)`, `SettingsService`, ``build-logic` — convention plugins`, `CLAUDE.md Development Constitution`, `Paise`, `Personal Finance Tracker PRD`, `Architecture overview`?**
  _High betweenness centrality (0.155) - this node is a cross-community bridge._
- **Are the 40 inferred relationships involving `Public interface` (e.g. with `AuditDiff` and `FieldChange`) actually correct?**
  _`Public interface` has 40 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._