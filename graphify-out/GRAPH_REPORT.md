# Graph Report - personal-finance-tracker  (2026-10-29)

## Corpus Check
- 66 files · ~61,332 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 28 file(s) not represented in the graph (top: .xml 10, .lockfile 7, (none) 4)

## Summary
- 714 nodes · 1254 edges · 50 communities (33 shown, 17 thin omitted)
- Extraction: 89% EXTRACTED · 11% INFERRED · 0% AMBIGUOUS · INFERRED: 133 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `eedb6ba1`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- redact.py
- CLAUDE.md Development Constitution
- Loans and EMIs (LN-1..LN-5)
- Personal Finance Tracker Learnings
- Automatic capture from SMS (S-1..S-13)
- hooks
- RedactTests
- MoneyColors
- PftTheme.kt
- Plan: M1a — Core ledger (manual-entry household tracker)
- `build-logic` — convention plugins
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- WindowThemeTest.kt
- Catalog.kt
- detekt.yml
- PftThemeModeTest
- HostedComposeRule
- Sync via Google Drive app-data (Y-1..Y-8)
- TokenSheetSections.kt
- Collecting and Redacting Sample Messages Guide
- PftTypographyTest
- Sync via Google Drive app-data (Y-1..Y-8)
- Security, privacy and integrity requirements (SEC/INT)
- ColorSchemeTestSupport.kt
- ThemePrefs
- Delivery constraints (small vertical slices, Claude Pro limits)
- TokenSheetScreenshotTest.kt
- Definition of Done
- docs/TECHNICAL.md
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
- Project README
- ManifestHardeningTest
- Plan: M1a design system and visual direction (lands before A5)
- 6. Implementation steps (TDD-ordered)
- .onCreate
- Architecture overview
- How to run and test
- 3. Design & decision
- Layered Modular Architecture (core vs app)

## God Nodes (most connected - your core abstractions)
1. `PftTypographyTest` - 26 edges
2. `ThemePrefs` - 21 edges
3. `ManifestHardeningTest` - 20 edges
4. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 20 edges
5. `HostedComposeRule` - 19 edges
6. `ColorContrastTest` - 19 edges
7. `PftThemeModeTest` - 17 edges
8. `CLAUDE.md Development Constitution` - 17 edges
9. `Personal Finance Tracker PRD` - 17 edges
10. `MainActivity` - 15 edges

## Surprising Connections (you probably didn't know these)
- `3.5 Design system (owner decision, 2026-10-29)` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `Slice A — Foundation (`feat/m1a-foundation`)` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `5.2 Rendering an amount` --references--> `MoneyColors`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/MoneyColors.kt
- `3.2 Theming architecture (no new abstraction)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt
- `9. Documentation updates (all required surfaces)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt

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

## Communities (50 total, 17 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.06
Nodes (17): _day(), _email_record(), html_to_text(), mask_run(), process(), Pseudo, read_adb(), read_any() (+9 more)

### Community 1 - "CLAUDE.md Development Constitution"
Cohesion: 0.17
Nodes (18): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+10 more)

### Community 2 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.19
Nodes (12): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5) (+4 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.25
Nodes (5): Personal Finance Tracker Learnings, graphify tool, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Toolchain pins (AGP 8.7.3, Gradle 8.10.2, Kotlin 2.1.0, minSdk 33)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.38
Nodes (4): Budgets (B-1..B-5), Read-only SMS and email access (S-10), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "MoneyColors"
Cohesion: 0.07
Nodes (21): `ui/theme` (design-system tokens, DS1–DS2), BrandColors, MoneyColors, PftShapes, PftSpacing, PftTypography, Colour, Shapes (+13 more)

### Community 9 - "PftTheme.kt"
Cohesion: 0.07
Nodes (4): resolveColorScheme(), BundledFontTest, `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`()

### Community 10 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.17
Nodes (10): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 7. Test plan, 8. Security considerations (+2 more)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.10
Nodes (11): ColorContrastTest, Theme, DARK, LIGHT, PftSpacingTest, Adding a token, Build-time trust decisions, Design system (+3 more)

### Community 18 - "detekt.yml"
Cohesion: 0.29
Nodes (6): ignoreAnnotated, excludes, naming, FunctionNaming, style, MagicNumber

### Community 27 - "HostedComposeRule"
Cohesion: 0.09
Nodes (4): HostedComposeRule, RegisterHostActivity, Statement, DesignCatalogTest

### Community 28 - "Sync via Google Drive app-data (Y-1..Y-8)"
Cohesion: 0.40
Nodes (4): Client-side encrypted sync with passphrase-derived key (Y-3), Operation-log based sync (Y-2), Sync via Google Drive app-data (Y-1..Y-8), Sync behind an interface (Y-7)

### Community 29 - "TokenSheetSections.kt"
Cohesion: 0.06
Nodes (17): ChoiceRow(), DesignCatalog(), DevContent(), TokenSheet(), TokenSheetPreview(), TokenSheetPage, TokenSheetPageFrame(), AmountStylesSection() (+9 more)

### Community 30 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.16
Nodes (14): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Parse log (S-11), Sender alias registry (S-13), Research: Indian Bank SMS Formats (+6 more)

### Community 31 - "PftTypographyTest"
Cohesion: 0.10
Nodes (5): displayFamily(), pftMaterialTypography(), pftType(), PftTypographyTest, Resolved

### Community 32 - "Sync via Google Drive app-data (Y-1..Y-8)"
Cohesion: 0.20
Nodes (11): Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope, Idempotent ingestion (X-2), M1 Phasing (M1a/M1b/M1c), M1b SMS capture, M1c Email capture and sync, Open questions (Q1 banks, Q2 email senders) (+3 more)

### Community 33 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.22
Nodes (11): Personal Finance Tracker PRD, Audit trail (INT-2), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Milestone 3 (insights), Non-functional requirements, Security, privacy and integrity requirements (SEC/INT) (+3 more)

### Community 35 - "ThemePrefs"
Cohesion: 0.13
Nodes (10): DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, ThemeMode, DARK, LIGHT, SYSTEM (+2 more)

### Community 36 - "Delivery constraints (small vertical slices, Claude Pro limits)"
Cohesion: 0.33
Nodes (4): Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 38 - "Definition of Done"
Cohesion: 0.13
Nodes (10): PR Checklist (TDD, Docs, Security, Architecture, Sizing), Android Surface and Multi-user Restrictions, Immutable Audit Trail, >=90% Coverage Floor on core, Definition of Done, Money as Integer Paise, Untrusted Input Validation (SMS/email/LLM output), Agent Slash Commands (+2 more)

### Community 39 - "docs/TECHNICAL.md"
Cohesion: 0.20
Nodes (12): Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup, Secrets via Environment Variables, Pure-Kotlin core modules (+4 more)

### Community 40 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.24
Nodes (12): Debug and release source sets, Public interface, Window theme, MainActivity, 5. Flows, PR breakdown (incremental — required), 6.1 Design-system slice: PRs into `feat/m1a-foundation`, 6.2 Appearance slice: base `feat/m1a-appearance` off `main`, after A-final (Q4) (+4 more)

### Community 41 - "Project README"
Cohesion: 0.22
Nodes (6): `:core:money` — money (stub), How to run and test, Purpose, Code Structure Map, core/shared Shared Packages, Project README

### Community 42 - "ManifestHardeningTest"
Cohesion: 0.33
Nodes (3): Manifest security posture (SEC-2, SEC-3, SEC-6), Responsibilities and boundaries, ManifestHardeningTest

### Community 43 - "Plan: M1a design system and visual direction (lands before A5)"
Cohesion: 0.20
Nodes (10): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 4. Data model & schema, 7. Test plan, 8. Security considerations (+2 more)

### Community 45 - "6. Implementation steps (TDD-ordered)"
Cohesion: 0.22
Nodes (9): 6. Implementation steps (TDD-ordered), Slice A — Foundation (`feat/m1a-foundation`), Slice B — Accounts, categories, tags (`feat/m1a-ledger-setup`), Slice C — Transactions (`feat/m1a-transactions`), Slice D — Budgets (`feat/m1a-budgets`), Slice E — Recurring and subscriptions (`feat/m1a-recurring`), Slice F — Loans and EMIs (`feat/m1a-loans`), Slice G — Goals (`feat/m1a-goals`) (+1 more)

### Community 46 - ".onCreate"
Cohesion: 0.25
Nodes (6): 5.1 Launch and theme resolution, 5.2 Rendering an amount, 5.3 Collapsing header, 5.4 Custom font import (AP2), 5.5 Mockups (wireframes; the styled version is in the HTML file), 5. Flows

### Community 47 - "Architecture overview"
Cohesion: 0.29
Nodes (7): Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape, The core/app boundary, UI layer

### Community 49 - "How to run and test"
Cohesion: 0.33
Nodes (6): `:app` — Android application, Configuration, Data it owns, Dependencies, How to run and test, Purpose

### Community 50 - "3. Design & decision"
Cohesion: 0.33
Nodes (6): 3.1 Modules (Gradle), 3.2 Key decisions, 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`), 3.4 Reuse by layer (owner decision, 2026-10-28), 3.5 Design system (owner decision, 2026-10-29), 3. Design & decision

### Community 52 - "Layered Modular Architecture (core vs app)"
Cohesion: 0.40
Nodes (3): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Planned Layout (app, core, tools/redact, fixtures)

## Knowledge Gaps
- **41 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `BUNDLED_SERIF`, `SYSTEM_SERIF` (+36 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 238 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **17 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Project README` connect `Project README` to `CLAUDE.md Development Constitution`, `Security, privacy and integrity requirements (SEC/INT)`, `Definition of Done`, `docs/TECHNICAL.md`, ``build-logic` — convention plugins`, `Layered Modular Architecture (core vs app)`, `Collecting and Redacting Sample Messages Guide`?**
  _High betweenness centrality (0.276) - this node is a cross-community bridge._
- **Are the 5 inferred relationships involving `PftTypographyTest` (e.g. with `How to run and test` and `Adding a token`) actually correct?**
  _`PftTypographyTest` has 5 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _41 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.0611764705882353 - nodes in this community are weakly interconnected._
- **Why does ``:app` — Android application` connect `How to run and test` to `6.1 Design-system slice: PRs into `feat/m1a-foundation``, `Project README`, `ManifestHardeningTest`?**
  _High betweenness centrality (0.232) - this node is a cross-community bridge._
- **Are the 11 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `TokenSheetPreview()`) actually correct?**
  _`ThemePrefs` has 11 INFERRED edges - model-reasoned connections that need verification._
- **Should `hooks` be split into smaller, more focused modules?**
  _Cohesion score 0.14285714285714285 - nodes in this community are weakly interconnected._