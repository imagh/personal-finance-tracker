# Graph Report - pft-wt-ds5  (2026-10-30)

## Corpus Check
- 149 files · ~104,969 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 30 file(s) not represented in the graph (top: .xml 10, .lockfile 8, (none) 4)

## Summary
- 1664 nodes · 4246 edges · 92 communities (53 shown, 39 thin omitted)
- Extraction: 86% EXTRACTED · 14% INFERRED · 0% AMBIGUOUS · INFERRED: 579 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `cfc4aa5a`
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
- ColorSchemeTestSupport.kt
- TokenSheetSections.kt
- DesignCatalog.kt
- PftTypographyTest
- UserId
- PftShapesTest.kt
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
- DesignCatalogTest.kt
- Architecture overview
- GroupedSection.kt
- Pseudo
- ThemePrefs
- ContentScreenshotTest
- Collecting and Redacting Sample Messages Guide
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Paise
- _Text
- StatusChipTest.kt
- main
- BottomActionBar.kt
- HeaderPolicy
- PftTheme.kt
- modifier
- DisplayFont
- expandedHeaderHeight
- Plan: M1a design system and visual direction (lands before A5)
- WindowThemeTest.kt
- HeaderSlot
- HeaderPhase
- HostedComposeRule
- GroupedSectionSamples
- PaiseParser
- MoneyTextSamples.kt
- StatusChipSamples.kt
- MoneySize
- Immutable Audit Trail

## God Nodes (most connected - your core abstractions)
1. `Paise` - 76 edges
2. `ThemePrefs` - 68 edges
3. `CollapsingHeaderScaffoldTest` - 68 edges
4. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 52 edges
5. `MoneyText()` - 46 edges
6. `MoneyTextTest` - 46 edges
7. `Public interface` - 44 edges
8. `HostedComposeRule` - 39 edges
9. `EntityMeta` - 37 edges
10. `3.4 Reuse by layer (owner decision, 2026-10-28)` - 35 edges

## Surprising Connections (you probably didn't know these)
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- ``EmptyState`` --references--> `EmptyState()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/EmptyState.kt
- ``StatusChip`` --references--> `StatusChip()`  [INFERRED]
  docs/architecture/design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/common/StatusChip.kt
- `3.2 Theming architecture (no new abstraction)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt
- `Slice H — App lock, diagnostics, release (`feat/m1a-hardening`)` --references--> `TokenSheet()`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/TokenSheet.kt

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

## Communities (92 total, 39 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.15
Nodes (8): _day(), _email_record(), html_to_text(), read_adb(), read_any(), read_eml(), read_mbox(), read_sms_xml()

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.14
Nodes (17): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+9 more)

### Community 2 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5), M1a Core ledger (+2 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "AuditFieldsCheck.kt"
Cohesion: 0.18
Nodes (3): AuditFieldsCheck, haveCompleteAuditFields(), shouldHaveCompleteAuditFields()

### Community 9 - "ThemeResolutionTest.kt"
Cohesion: 0.08
Nodes (5): BundledFontTest, `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`(), NameBasedId, Third-party assets

### Community 10 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.11
Nodes (17): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5. Flows, 6. Implementation steps (TDD-ordered) (+9 more)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.17
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.12
Nodes (6): ColorContrastTest, Theme, DARK, LIGHT, PftSpacingTest, Adding a token

### Community 16 - "BottomActionBar"
Cohesion: 0.12
Nodes (16): BottomActionBar(), Build-time trust decisions, Colour, Content primitives (DS5), Design system, `EmptyState`, Motion (UI-10), One-hand layout (UI-7) (+8 more)

### Community 18 - "detekt.yml"
Cohesion: 0.20
Nodes (9): complexity, LongParameterList, ignoreAnnotated, ignoreDefaultParameters, excludes, naming, FunctionNaming, style (+1 more)

### Community 26 - "NavTransitionTest.kt"
Cohesion: 0.09
Nodes (3): NavTransitionTest, PftThemeModeTest, TokenSheetScreenshotTest

### Community 28 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.08
Nodes (6): BottomBarAction, CollapsingHeaderScaffoldTest, Steps, assertShownInFull(), isShownInFull(), layout()

### Community 29 - "HeaderScreenshotTest"
Cohesion: 0.13
Nodes (6): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), HeaderScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 30 - ".format"
Cohesion: 0.16
Nodes (11): Dependencies, Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface (+3 more)

### Community 31 - "LedgerError"
Cohesion: 0.05
Nodes (28): How to run and test, AlreadyInitialised, AmountNotPositive, AmountTooLarge, Archived, BlankName, Deleted, InvalidSettingKey (+20 more)

### Community 33 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.24
Nodes (12): Row, TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle() (+4 more)

### Community 36 - "DesignCatalog.kt"
Cohesion: 0.11
Nodes (14): CatalogSection, BOTTOM_BAR, CHIPS, DASHBOARD_HEADER, EMPTY, MONEY, MOTION, SECTIONS (+6 more)

### Community 37 - "PftTypographyTest"
Cohesion: 0.08
Nodes (9): `ui/theme` (design-system tokens, DS1, DS2, DS4), PftSpacing, displayFamily(), pftMaterialTypography(), pftType(), PftTypography, PftTypographyTest, Resolved (+1 more)

### Community 38 - "UserId"
Cohesion: 0.16
Nodes (19): FieldChange, AuditAction, ARCHIVE, CREATE, DELETE, RESTORE, UNARCHIVE, UPDATE (+11 more)

### Community 40 - "Public interface"
Cohesion: 0.07
Nodes (28): Configuration, `:core:ledger` — the ledger foundations, Data it owns, Dependencies, How an entity step composes the blocks, Public interface, Purpose, Responsibilities & boundaries (+20 more)

### Community 42 - "Project README"
Cohesion: 0.20
Nodes (6): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Code Structure Map, core/shared Shared Packages, Project README, Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "Auditable"
Cohesion: 0.12
Nodes (6): Auditable, Device, IdentityRepository, UserProfile, Thing, InMemoryIdentityRepository

### Community 44 - "PftMotion.kt"
Cohesion: 0.08
Nodes (5): iosSpring(), PftMotion, pressScale(), PftMotionTest, 7. Test plan

### Community 45 - "3.4 Reuse by layer (owner decision, 2026-10-28)"
Cohesion: 0.18
Nodes (12): Behaviours worth knowing, AuditDiff, AuditedWriter, AuditSource, MANUAL, RECURRING, SYSTEM, EntityRepository (+4 more)

### Community 47 - "CLAUDE.md Development Constitution"
Cohesion: 0.12
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 48 - "MoneyText"
Cohesion: 0.15
Nodes (9): atSize(), directionLabel(), Figure(), fitHero(), HeroFit, HeroText(), MoneyText(), RollingDigits() (+1 more)

### Community 52 - "AuditedWriterTest.kt"
Cohesion: 0.15
Nodes (7): AuditedWriterTest, Fixture, Leaky, LeakyRepository, Note, NoteId, InMemoryEntityRepository

### Community 54 - "EntityMeta"
Cohesion: 0.09
Nodes (18): EntityMeta, LedgerEntity, EntityId, AuditFieldsCheckTest, Complete, HasDerived, HasScratch, Kind (+10 more)

### Community 55 - "How to run and test"
Cohesion: 0.19
Nodes (12): `:app` — Android application, Configuration, Data it owns, Debug and release source sets, How to run and test, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose (+4 more)

### Community 56 - "CollapsingHeaderScaffold"
Cohesion: 0.28
Nodes (13): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle, Dashboard (+5 more)

### Community 57 - "3. Design & decision"
Cohesion: 0.29
Nodes (7): 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens, 3.5 Fonts: bundling, licensing, custom import, 3.6 Icons and emoji (UI-6), 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins"), 3. Design & decision

### Community 58 - "Personal Finance Tracker PRD"
Cohesion: 0.11
Nodes (22): Personal Finance Tracker PRD, Balance observations (L-10), Cashew (existing expense app), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope, Idempotent ingestion (X-2) (+14 more)

### Community 60 - "redact_text"
Cohesion: 0.27
Nodes (6): mask_run(), process(), redact_text(), amt(), hold(), review_flags()

### Community 62 - "Architecture overview"
Cohesion: 0.29
Nodes (6): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, UI layer

### Community 63 - "GroupedSection.kt"
Cohesion: 0.16
Nodes (9): `ui/common` (shared components, DS4 and DS5), GroupedRow, GroupedSection(), groupedSectionColor(), GroupedSectionDefaults, GroupedSectionScope, GroupedSectionTags, Row (+1 more)

### Community 65 - "ThemePrefs"
Cohesion: 0.23
Nodes (3): EmptyState(), ThemePrefs, EmptyStateTest

### Community 67 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 68 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.12
Nodes (26): moneySign(), Balance, Budget, BudgetState, AT_THRESHOLD, OVER, UNDER, Expense (+18 more)

### Community 73 - "main"
Cohesion: 0.20
Nodes (3): 6.2 Appearance slice: base `feat/m1a-appearance` off `main`, after A-final (Q4), main(), read_jsonl()

### Community 76 - "PftTheme.kt"
Cohesion: 0.10
Nodes (3): MotionSection(), SpringTrack(), resolveColorScheme()

### Community 77 - "modifier"
Cohesion: 0.18
Nodes (4): EmptyStatePreview(), EmptyStateSamples(), GlyphPlaceholder(), DevContent()

### Community 78 - "DisplayFont"
Cohesion: 0.20
Nodes (8): DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, ThemeMode, DARK, LIGHT, SYSTEM

### Community 81 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.15
Nodes (13): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 5.3 Collapsing header, 5.4 Custom font import (AP2) (+5 more)

### Community 83 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 84 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 85 - "HostedComposeRule"
Cohesion: 0.20
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 86 - "GroupedSectionSamples"
Cohesion: 0.28
Nodes (4): GroupedSectionPreview(), GroupedSectionSamples(), TokenSheet(), TokenSheetPreview()

### Community 88 - "MoneyTextSamples.kt"
Cohesion: 0.67
Nodes (4): AmountRow(), Caption(), MoneyTextPreview(), MoneyTextSamples()

### Community 90 - "MoneySize"
Cohesion: 0.40
Nodes (4): MoneySize, Hero, Large, Spec

## Knowledge Gaps
- **83 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+78 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 396 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **39 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `6.1 Design-system slice: PRs into `feat/m1a-foundation`` connect `6.1 Design-system slice: PRs into `feat/m1a-foundation`` to `WindowThemeTest`, `ThemeResolutionTest.kt`, `ColorContrastTest`, `BottomActionBar`, `NavTransitionTest.kt`, `MoneyTextTest`, `CollapsingHeaderScaffoldTest`, `.format`, `DesignCatalog.kt`, `PftTypographyTest`, `PftShapesTest.kt`, `Public interface`, `PftMotion.kt`, `MoneyColors`, `MoneyText`, `ManifestHardeningTest`, `GroupedSectionTest.kt`, `BottomActionBarTest.kt`, `PressScaleTest.kt`, `How to run and test`, `CollapsingHeaderScaffold`, `DesignCatalogTest.kt`, `GroupedSection.kt`, `ThemePrefs`, `ContentScreenshotTest`, `Paise`, `StatusChipTest.kt`, `main`, `modifier`, `WindowThemeTest.kt`, `HostedComposeRule`, `GroupedSectionSamples`, `MoneyTextSamples.kt`, `StatusChipSamples.kt`?**
  _High betweenness centrality (0.170) - this node is a cross-community bridge._
- **Are the 9 inferred relationships involving `Paise` (e.g. with `Behaviours worth knowing` and `Dependencies`) actually correct?**
  _`Paise` has 9 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _83 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.14761904761904762 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Collecting and Redacting Sample Messages Guide`, `Automatic capture from SMS (S-1..S-13)`, `Public interface`, `CLAUDE.md Development Constitution`, `.format`, `Personal Finance Tracker PRD`, `Architecture overview`?**
  _High betweenness centrality (0.158) - this node is a cross-community bridge._
- **Are the 11 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `TokenSheetPreview()`) actually correct?**
  _`ThemePrefs` has 11 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._