# Graph Report - pft-wt-ds5  (2026-10-30)

## Corpus Check
- 149 files · ~105,895 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 30 file(s) not represented in the graph (top: .xml 10, .lockfile 8, (none) 4)

## Summary
- 1676 nodes · 4291 edges · 98 communities (62 shown, 36 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 595 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `c57b34d4`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- Loans and EMIs (LN-1..LN-5)
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- WindowThemeTest
- hooks
- RedactTests
- AuditFieldsCheck.kt
- ThemeResolutionTest.kt
- Plan: M1a — Core ledger (manual-entry household tracker)
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- BottomActionBar
- Catalog.kt
- detekt.yml
- NavTransitionTest.kt
- MoneyTextTest
- CollapsingHeaderScaffoldTest
- HeaderScreenshotTest
- .format
- LedgerError
- CollapsingHeaderScaffold.kt
- Security, privacy and integrity requirements (SEC/INT)
- HeaderSamples.kt
- TokenSheetSections.kt
- DesignCatalog.kt
- PftTypographyTest
- UserId
- LedgerError
- Public interface
- TestClock
- Project README
- Auditable
- PftMotion.kt
- 3.4 Reuse by layer (owner decision, 2026-10-28)
- MoneyColors
- CLAUDE.md Development Constitution
- MoneyText
- ManifestHardeningTest
- GroupedSectionTest.kt
- StatusChip
- AuditedWriterTest.kt
- AuditFieldsCheckTest.kt
- WidgetId
- How to run and test
- CollapsingHeaderScaffold
- 3.7 Component inventory (UI-9)
- Personal Finance Tracker PRD
- InMemoryEntityStore
- redact_text
- DesignCatalogTest.kt
- Architecture overview
- Paise
- Pseudo
- ThemePrefs
- ContentScreenshotTest
- Collecting and Redacting Sample Messages Guide
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Paise
- _Text
- StatusChipTest.kt
- BottomActionBar.kt
- HeaderPolicy
- PftTheme.kt
- modifier
- DisplayFont
- MoneyTextTest.kt
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- WindowThemeTest.kt
- HeaderSlot
- HeaderPhase
- AppSetting
- TokenSheet
- PaiseParser
- MoneyTextSamples.kt
- InMemoryEntityRepository
- MoneySize
- EmptyStateSamples.kt
- Complete
- ValidationTest.kt
- PR breakdown (incremental — required)
- HasScratch
- InMemoryLedgerStore
- 3.5 Design system (owner decision, 2026-10-29)

## God Nodes (most connected - your core abstractions)
1. `Paise` - 80 edges
2. `ThemePrefs` - 68 edges
3. `CollapsingHeaderScaffoldTest` - 68 edges
4. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 54 edges
5. `MoneyText()` - 49 edges
6. `MoneyTextTest` - 48 edges
7. `Public interface` - 44 edges
8. `HostedComposeRule` - 39 edges
9. `EntityMeta` - 37 edges
10. `3.4 Reuse by layer (owner decision, 2026-10-28)` - 35 edges

## Surprising Connections (you probably didn't know these)
- ``EmptyState`` --references--> `EmptyState()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/EmptyState.kt
- `3.2 Theming architecture (no new abstraction)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt
- `Slice H — App lock, diagnostics, release (`feat/m1a-hardening`)` --references--> `TokenSheet()`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/TokenSheet.kt
- `Security posture of the shell` --references--> `ManifestHardeningTest`  [INFERRED]
  docs/architecture/overview.md → app/src/test/kotlin/dev/imagh/pft/app/ManifestHardeningTest.kt
- `3.6 Icons and emoji (UI-6)` --references--> `ManifestHardeningTest`  [INFERRED]
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

## Communities (98 total, 36 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.14
Nodes (9): _day(), _email_record(), html_to_text(), read_adb(), read_any(), read_eml(), read_jsonl(), read_mbox() (+1 more)

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

### Community 8 - "AuditFieldsCheck.kt"
Cohesion: 0.14
Nodes (5): LedgerEntity, EntityId, AuditFieldsCheck, haveCompleteAuditFields(), shouldHaveCompleteAuditFields()

### Community 9 - "ThemeResolutionTest.kt"
Cohesion: 0.07
Nodes (5): BundledFontTest, `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`(), NameBasedId, Third-party assets

### Community 10 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.12
Nodes (15): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 6. Implementation steps (TDD-ordered), 8. Security considerations (+7 more)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.09
Nodes (7): ColorContrastTest, Theme, DARK, LIGHT, PftShapesTest, PftSpacingTest, Adding a token

### Community 16 - "BottomActionBar"
Cohesion: 0.15
Nodes (13): BottomActionBar(), Build-time trust decisions, Colour, Design system, Motion (UI-10), One-hand layout (UI-7), Principles, Shapes (+5 more)

### Community 18 - "detekt.yml"
Cohesion: 0.20
Nodes (9): complexity, LongParameterList, ignoreAnnotated, ignoreDefaultParameters, excludes, naming, FunctionNaming, style (+1 more)

### Community 26 - "NavTransitionTest.kt"
Cohesion: 0.07
Nodes (6): HostedComposeRule, RegisterHostActivity, Statement, NavTransitionTest, PftThemeModeTest, TokenSheetScreenshotTest

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.09
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 29 - "HeaderScreenshotTest"
Cohesion: 0.09
Nodes (4): sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, PressScaleTest

### Community 30 - ".format"
Cohesion: 0.15
Nodes (12): Dependencies, Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface (+4 more)

### Community 31 - "LedgerError"
Cohesion: 0.07
Nodes (16): How to run and test, SettingKeys, AlreadyInitialised, AuditDiffTest, AuditedWriterTest, IdentityServiceTest, IdsTest, NameBasedIdTest (+8 more)

### Community 33 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 34 - "HeaderSamples.kt"
Cohesion: 0.20
Nodes (3): GroupedSectionPreview(), GroupedSectionSamples(), DashboardHeaderPreview()

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "DesignCatalog.kt"
Cohesion: 0.11
Nodes (14): CatalogSection, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, MONEY, MOTION, SECTIONS (+6 more)

### Community 37 - "PftTypographyTest"
Cohesion: 0.09
Nodes (7): `ui/theme` (design-system tokens, DS1, DS2, DS4), displayFamily(), pftMaterialTypography(), pftType(), PftTypography, PftTypographyTest, Resolved

### Community 38 - "UserId"
Cohesion: 0.12
Nodes (19): Test fixtures (`java-test-fixtures`, shared by `budget`, `recurring`, ...), FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE, UNARCHIVE (+11 more)

### Community 39 - "LedgerError"
Cohesion: 0.15
Nodes (11): AmountNotPositive, AmountTooLarge, Archived, BlankName, Deleted, InvalidSettingKey, LedgerError, NameTooLong (+3 more)

### Community 40 - "Public interface"
Cohesion: 0.10
Nodes (22): Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How an entity step composes the blocks, Public interface, Purpose, Responsibilities & boundaries (+14 more)

### Community 42 - "Project README"
Cohesion: 0.22
Nodes (6): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Code Structure Map, core/shared Shared Packages, Project README, Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "Auditable"
Cohesion: 0.12
Nodes (9): Auditable, Device, IdentityRepository, UserProfile, DeviceId, UserId, Thing, EntityMetaTest (+1 more)

### Community 44 - "PftMotion.kt"
Cohesion: 0.09
Nodes (5): iosSpring(), PftMotion, pressScale(), PftMotionTest, 7. Test plan

### Community 45 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.17
Nodes (11): Behaviours worth knowing, AuditedWriter, AuditSource, MANUAL, RECURRING, SYSTEM, EntityRepository, IdentityCorrupt (+3 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.10
Nodes (24): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+16 more)

### Community 48 - "MoneyText"
Cohesion: 0.13
Nodes (11): DashboardHeaderSample(), atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText() (+3 more)

### Community 50 - "GroupedSectionTest.kt"
Cohesion: 0.09
Nodes (8): GroupedRow, GroupedSection(), groupedSectionColor(), GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, GroupedSectionTest, `GroupedSection`

### Community 51 - "StatusChip"
Cohesion: 0.24
Nodes (4): StatusChip(), Content primitives (DS5), `EmptyState`, `StatusChip`

### Community 52 - "AuditedWriterTest.kt"
Cohesion: 0.25
Nodes (6): EntityMeta, Fixture, Leaky, LeakyRepository, Note, NoteId

### Community 53 - "AuditFieldsCheckTest.kt"
Cohesion: 0.25
Nodes (3): AuditFieldsCheckTest, Omits, UncheckedOmitsRepository

### Community 54 - "WidgetId"
Cohesion: 0.15
Nodes (6): HasDerived, MisMapped, Misspelt, WidgetId, WrongCase, WrongValue

### Community 55 - "How to run and test"
Cohesion: 0.17
Nodes (13): `:app` — Android application, Configuration, Data it owns, Debug and release source sets, How to run and test, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose (+5 more)

### Community 56 - "CollapsingHeaderScaffold"
Cohesion: 0.28
Nodes (13): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle, Dashboard (+5 more)

### Community 57 - "3.7 Component inventory (UI-9)"
Cohesion: 0.18
Nodes (10): PftShapes, PftSpacing, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.7 Component inventory (UI-9) (+2 more)

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.10
Nodes (24): Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Personal Finance Tracker PRD, Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Delivery constraints (small vertical slices, Claude Pro limits), Automatic capture from Email (E-0..E-5) (+16 more)

### Community 60 - "redact_text"
Cohesion: 0.27
Nodes (6): mask_run(), process(), redact_text(), amt(), hold(), review_flags()

### Community 62 - "Architecture overview"
Cohesion: 0.29
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 63 - "Paise"
Cohesion: 0.14
Nodes (6): `ui/common` (shared components, DS4 and DS5), moneySign(), Row, Paise, `MoneyText`, Slice D — Budgets (`feat/m1a-budgets`)

### Community 65 - "ThemePrefs"
Cohesion: 0.23
Nodes (3): EmptyState(), ThemePrefs, EmptyStateTest

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 68 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.20
Nodes (18): Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense, Income (+10 more)

### Community 72 - "StatusChipTest.kt"
Cohesion: 0.23
Nodes (3): StatusChipTest, roles(), surfaceRoles()

### Community 74 - "BottomActionBar.kt"
Cohesion: 0.16
Nodes (4): BottomActionBarPreview(), BottomActionBarSamples(), StatusChipPreview(), StatusChipSamples()

### Community 76 - "PftTheme.kt"
Cohesion: 0.08
Nodes (3): MotionSection(), SpringTrack(), resolveColorScheme()

### Community 78 - "DisplayFont"
Cohesion: 0.20
Nodes (8): DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, ThemeMode, DARK, LIGHT, SYSTEM

### Community 79 - "MoneyTextTest.kt"
Cohesion: 0.12
Nodes (5): layoutResult(), shownText(), assertShownInFull(), isShownInFull(), layout()

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.17
Nodes (12): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 4. Data model & schema, 5.3 Collapsing header, 5.4 Custom font import (AP2), 5.5 Mockups (wireframes; the styled version is in the HTML file) (+4 more)

### Community 83 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 84 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 85 - "AppSetting"
Cohesion: 0.36
Nodes (3): AppSetting, AppSettingsRepository, InMemoryAppSettingsRepository

### Community 88 - "MoneyTextSamples.kt"
Cohesion: 0.67
Nodes (4): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples()

### Community 90 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

### Community 91 - "EmptyStateSamples.kt"
Cohesion: 0.73
Nodes (3): EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder()

### Community 92 - "Complete"
Cohesion: 0.33
Nodes (4): Complete, Kind, LARGE, SMALL

### Community 94 - "PR breakdown (incremental — required)"
Cohesion: 0.50
Nodes (4): 6.2 Appearance slice: base `feat/m1a-appearance` off `main`, after A-final (Q4), 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required), main()

### Community 97 - "3.5 Design system (owner decision, 2026-10-29)"
Cohesion: 0.50
Nodes (4): 3.1 Modules (Gradle), 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3.5 Design system (owner decision, 2026-10-29), 3. Design & decision

## Knowledge Gaps
- **83 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+78 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 397 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **36 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `6.1 Design-system slice: PRs into `feat/m1a-foundation`` connect `6.1 Design-system slice: PRs into `feat/m1a-foundation`` to `WindowThemeTest`, `ThemeResolutionTest.kt`, `ColorContrastTest`, `BottomActionBar`, `NavTransitionTest.kt`, `MoneyTextTest`, `CollapsingHeaderScaffoldTest`, `HeaderScreenshotTest`, `.format`, `HeaderSamples.kt`, `DesignCatalog.kt`, `PftTypographyTest`, `Public interface`, `PftMotion.kt`, `MoneyColors`, `MoneyText`, `ManifestHardeningTest`, `GroupedSectionTest.kt`, `How to run and test`, `CollapsingHeaderScaffold`, `3.7 Component inventory (UI-9)`, `DesignCatalogTest.kt`, `Paise`, `ThemePrefs`, `ContentScreenshotTest`, `Paise`, `StatusChipTest.kt`, `BottomActionBar.kt`, `MoneyTextTest.kt`, `WindowThemeTest.kt`, `TokenSheet`, `MoneyTextSamples.kt`, `EmptyStateSamples.kt`, `PR breakdown (incremental — required)`?**
  _High betweenness centrality (0.196) - this node is a cross-community bridge._
- **Are the 14 inferred relationships involving `Paise` (e.g. with ``ui/common` (shared components, DS4 and DS5)` and `Behaviours worth knowing`) actually correct?**
  _`Paise` has 14 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _83 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1422924901185771 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Collecting and Redacting Sample Messages Guide`, `Automatic capture from SMS (S-1..S-13)`, `Public interface`, ``build-logic` — convention plugins`, `CLAUDE.md Development Constitution`, `.format`, `Personal Finance Tracker PRD`, `Architecture overview`?**
  _High betweenness centrality (0.171) - this node is a cross-community bridge._
- **Are the 11 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `TokenSheetPreview()`) actually correct?**
  _`ThemePrefs` has 11 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._