# Graph Report - personal-finance-tracker  (2026-10-29)

## Corpus Check
- 85 files · ~75,640 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 28 file(s) not represented in the graph (top: .xml 10, .lockfile 7, (none) 4)

## Summary
- 1095 nodes · 2313 edges · 76 communities (45 shown, 31 thin omitted)
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 242 edges (avg confidence: 0.92)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `a196cea0`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- docs/TECHNICAL.md
- Loans and EMIs (LN-1..LN-5)
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- ManifestHardeningTest
- hooks
- RedactTests
- 3. Design & decision
- ThemeResolutionTest.kt
- How to run and test
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- WindowThemeTest.kt
- Catalog.kt
- detekt.yml
- PftShapesTest.kt
- CollapsingHeaderScaffoldTest.kt
- Sync via Google Drive app-data (Y-1..Y-8)
- HostedComposeRule
- Paise
- PaiseParserTest.kt
- Automatic capture from Email (E-0..E-5)
- Security, privacy and integrity requirements (SEC/INT)
- ColorSchemeTestSupport.kt
- TokenSheetSections.kt
- Architect Plan Template
- PftTypographyTest
- BottomActionBar
- PftTypography.kt
- CLAUDE.md Development Constitution
- MotionSection.kt
- Project README
- Definition of Done
- Research: Indian Bank SMS Formats
- Collecting and Redacting Sample Messages Guide
- CollapsingHeaderScaffoldTest
- CollapsingHeaderScaffold.kt
- NavTransitionTest.kt
- BottomActionBarTest.kt
- PftTheme.kt
- DesignCatalog.kt
- CollapsingHeaderScaffold
- PftMotion.kt
- assertequals
- BottomActionBar.kt
- HeaderPolicy
- TokenSheetPage.kt
- dp
- ThemePrefs
- Personal Finance Tracker PRD
- Rule B: Mandatory Documentation
- expandedHeaderHeight
- redact_text
- Plan: M1a — Core ledger (manual-entry household tracker)
- 6. Implementation steps (TDD-ordered)
- Architecture overview
- HeaderSlot
- TokenSheet
- Pseudo
- BrandColors
- 3. Design & decision
- _Text
- HeaderPhase
- main

## God Nodes (most connected - your core abstractions)
1. `CollapsingHeaderScaffoldTest` - 64 edges
2. `ThemePrefs` - 34 edges
3. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 30 edges
4. `HostedComposeRule` - 29 edges
5. `PftTypographyTest` - 26 edges
6. `CollapsingHeaderScaffold()` - 22 edges
7. `HeaderScreenshotTest` - 22 edges
8. `ManifestHardeningTest` - 20 edges
9. `BottomActionBarTest` - 19 edges
10. `ColorContrastTest` - 19 edges

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

## Communities (76 total, 31 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.14
Nodes (9): _day(), _email_record(), html_to_text(), read_adb(), read_any(), read_eml(), read_jsonl(), read_mbox() (+1 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.16
Nodes (14): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup, Secrets via Environment Variables (+6 more)

### Community 2 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.23
Nodes (11): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5) (+3 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.20
Nodes (5): Personal Finance Tracker Learnings, graphify tool, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Transfers (two linked legs, not income/expense)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.32
Nodes (5): Budgets (B-1..B-5), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "3. Design & decision"
Cohesion: 0.16
Nodes (11): `ui/theme` (design-system tokens, DS1, DS2, DS4), PftSpacing, Principles, 3.1 Visual direction: "Cashew, but premium", 3.2 Theming architecture (no new abstraction), 3.3 Tokens, 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`, 3.5 Fonts: bundling, licensing, custom import (+3 more)

### Community 9 - "ThemeResolutionTest.kt"
Cohesion: 0.11
Nodes (3): BundledFontTest, `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`()

### Community 10 - "How to run and test"
Cohesion: 0.15
Nodes (15): `:app` — Android application, Configuration, Data it owns, Debug and release source sets, Dependencies, How to run and test, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface (+7 more)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.21
Nodes (4): ColorContrastTest, Theme, DARK, LIGHT

### Community 18 - "detekt.yml"
Cohesion: 0.20
Nodes (9): complexity, LongParameterList, ignoreAnnotated, ignoreDefaultParameters, excludes, naming, FunctionNaming, style (+1 more)

### Community 28 - "Sync via Google Drive app-data (Y-1..Y-8)"
Cohesion: 0.40
Nodes (4): Client-side encrypted sync with passphrase-derived key (Y-3), Operation-log based sync (Y-2), Sync via Google Drive app-data (Y-1..Y-8), Sync behind an interface (Y-7)

### Community 29 - "HostedComposeRule"
Cohesion: 0.12
Nodes (4): HostedComposeRule, RegisterHostActivity, Statement, PftSpacingTest

### Community 30 - "Paise"
Cohesion: 0.06
Nodes (26): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+18 more)

### Community 31 - "PaiseParserTest.kt"
Cohesion: 0.13
Nodes (6): How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest, 7. Test plan, Slice A — Foundation (`feat/m1a-foundation`)

### Community 32 - "Automatic capture from Email (E-0..E-5)"
Cohesion: 0.29
Nodes (7): Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope, Idempotent ingestion (X-2), Transaction reference and reconciliation (X-1..X-6), Risks and dependencies, Two Google identities per device (shared sync account, personal Gmail)

### Community 33 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.30
Nodes (9): AmountStylesSection(), MoneyContainers(), MoneyOnSurface(), MoneyTokensSection(), SectionTitle(), ShapesAndSpacing(), ShapesSection(), SpacingSection() (+1 more)

### Community 36 - "Architect Plan Template"
Cohesion: 0.50
Nodes (3): Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps

### Community 38 - "BottomActionBar"
Cohesion: 0.13
Nodes (13): MoneyColors, Adding a token, Build-time trust decisions, Colour, Design system, Motion (UI-10), One-hand layout (UI-7), Shapes (+5 more)

### Community 39 - "PftTypography.kt"
Cohesion: 0.17
Nodes (4): displayFamily(), pftMaterialTypography(), pftType(), PftTypography

### Community 40 - "CLAUDE.md Development Constitution"
Cohesion: 0.22
Nodes (15): architect Agent, code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command, Pull Request Template (+7 more)

### Community 42 - "Project README"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "Definition of Done"
Cohesion: 0.12
Nodes (11): Architect Plan (docs/plans/YYYY-MM-DD-slug.md), PR Checklist (TDD, Docs, Security, Architecture, Sizing), Android Surface and Multi-user Restrictions, Immutable Audit Trail, >=90% Coverage Floor on core, Definition of Done, Money as Integer Paise, Untrusted Input Validation (SMS/email/LLM output) (+3 more)

### Community 44 - "Research: Indian Bank SMS Formats"
Cohesion: 0.29
Nodes (7): Parse log (S-11), Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser, Public Bank SMS Header Registry (gist, Dec 2022), Open-Source Indian SMS Parsers (hisab-kitab, FinTrack, ...), Indian SMS Sender Header Format (prefix-HEADER-S), SMS Spoofing and Phishing

### Community 45 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

### Community 46 - "CollapsingHeaderScaffoldTest"
Cohesion: 0.15
Nodes (3): BottomBarAction, CollapsingHeaderScaffoldTest, Steps

### Community 48 - "NavTransitionTest.kt"
Cohesion: 0.11
Nodes (3): DevContent(), NavTransitionTest, PftThemeModeTest

### Community 50 - "PftTheme.kt"
Cohesion: 0.06
Nodes (8): DashboardHeaderPreview(), DashboardHeaderSample(), sampleRows(), StandardHeaderSample(), resolveColorScheme(), HeaderScreenshotTest, TokenSheetScreenshotTest, Previews, the token sheet and screenshot goldens

### Community 51 - "DesignCatalog.kt"
Cohesion: 0.15
Nodes (10): CatalogSection, BOTTOM_BAR, DASHBOARD_HEADER, MOTION, STANDARD_HEADER, TOKENS, ChoiceRow(), DesignCatalog() (+2 more)

### Community 52 - "CollapsingHeaderScaffold"
Cohesion: 0.25
Nodes (13): CollapsingHeader(), CollapsingHeaderScaffold(), HeaderChildren(), HeaderContent, HeaderPeriod, headerSizes(), HeaderStyle, Dashboard (+5 more)

### Community 53 - "PftMotion.kt"
Cohesion: 0.10
Nodes (7): PftMotion, pressScale(), 3.7 Component inventory (UI-9), 6.1 Design-system slice: PRs into `feat/m1a-foundation`, 6.3 Amendments to M1a steps (consumption; this plan edits M1a when approved), 6. Implementation steps (TDD-ordered), PR breakdown (incremental — required)

### Community 54 - "assertequals"
Cohesion: 0.24
Nodes (3): iosSpring(), PftMotionTest, 7. Test plan

### Community 55 - "BottomActionBar.kt"
Cohesion: 0.20
Nodes (3): BottomActionBar(), BottomActionBarPreview(), BottomActionBarSamples()

### Community 59 - "ThemePrefs"
Cohesion: 0.24
Nodes (9): DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, ThemeMode, DARK, LIGHT, SYSTEM (+1 more)

### Community 60 - "Personal Finance Tracker PRD"
Cohesion: 0.22
Nodes (10): Personal Finance Tracker PRD, Cashew (existing expense app), Delivery constraints (small vertical slices, Claude Pro limits), M1 Phasing (M1a/M1b/M1c), M1b SMS capture, M1c Email capture and sync, Milestone 3 (insights), Non-functional requirements (+2 more)

### Community 65 - "redact_text"
Cohesion: 0.27
Nodes (6): mask_run(), process(), redact_text(), amt(), hold(), review_flags()

### Community 66 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.18
Nodes (9): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 8. Security considerations, 9. Documentation updates (all required surfaces) (+1 more)

### Community 67 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.25
Nodes (8): 6. Implementation steps (TDD-ordered), Slice B — Accounts, categories, tags (`feat/m1a-ledger-setup`), Slice C — Transactions (`feat/m1a-transactions`), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`), Slice H — App lock, diagnostics, release (`feat/m1a-hardening`)

### Community 68 - "Architecture overview"
Cohesion: 0.15
Nodes (10): Code Structure Map, core/shared Shared Packages, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, The core/app boundary (+2 more)

### Community 69 - "HeaderSlot"
Cohesion: 0.29
Nodes (7): HeaderSlot, Actions, Caption, Hero, Subtitle, TitleBar, TitleLarge

### Community 73 - "3. Design & decision"
Cohesion: 0.33
Nodes (6): 3.1 Modules (Gradle), 3.2 Key decisions, 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3.4 Reuse by layer (owner decision, 2026-10-28), 3.5 Design system (owner decision, 2026-10-29), 3. Design & decision

### Community 76 - "HeaderPhase"
Cohesion: 0.50
Nodes (4): HeaderPhase, Collapsed, Expanded, Moving

### Community 77 - "main"
Cohesion: 0.50
Nodes (3): PR breakdown (incremental — required), 6.2 Appearance slice: base `feat/m1a-appearance` off `main`, after A-final (Q4), main()

## Knowledge Gaps
- **57 isolated node(s):** `TOKENS`, `MOTION`, `BOTTOM_BAR`, `STANDARD_HEADER`, `DASHBOARD_HEADER` (+52 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 322 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **31 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Project README` connect `Architecture overview` to `docs/TECHNICAL.md`, `CLAUDE.md Development Constitution`, `Project README`, ``build-logic` — convention plugins`, `Research: Indian Bank SMS Formats`, `Collecting and Redacting Sample Messages Guide`, `Definition of Done`, `Personal Finance Tracker PRD`?**
  _High betweenness centrality (0.209) - this node is a cross-community bridge._
- **Are the 5 inferred relationships involving `CollapsingHeaderScaffoldTest` (e.g. with `How to run and test` and `One-hand layout (UI-7)`) actually correct?**
  _`CollapsingHeaderScaffoldTest` has 5 INFERRED edges - model-reasoned connections that need verification._
- **What connects `TOKENS`, `MOTION`, `BOTTOM_BAR` to the rest of the system?**
  _57 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.1422924901185771 - nodes in this community are weakly interconnected._
- **Why does `6.1 Design-system slice: PRs into `feat/m1a-foundation`` connect `PftMotion.kt` to `ManifestHardeningTest`, `ThemeResolutionTest.kt`, `How to run and test`, `WindowThemeTest.kt`, `PftShapesTest.kt`, `CollapsingHeaderScaffoldTest.kt`, `HostedComposeRule`, `Paise`, `PftTypographyTest`, `BottomActionBar`, `PftTypography.kt`, `CollapsingHeaderScaffoldTest`, `NavTransitionTest.kt`, `BottomActionBarTest.kt`, `PftTheme.kt`, `DesignCatalog.kt`, `CollapsingHeaderScaffold`, `assertequals`, `BottomActionBar.kt`, `dp`, `ThemePrefs`, `TokenSheet`, `BrandColors`, `main`?**
  _High betweenness centrality (0.195) - this node is a cross-community bridge._
- **Are the 11 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `TokenSheetPreview()`) actually correct?**
  _`ThemePrefs` has 11 INFERRED edges - model-reasoned connections that need verification._
- **Should `hooks` be split into smaller, more focused modules?**
  _Cohesion score 0.14285714285714285 - nodes in this community are weakly interconnected._