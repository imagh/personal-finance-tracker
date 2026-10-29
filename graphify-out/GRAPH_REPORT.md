# Graph Report - personal-finance-tracker  (2026-10-29)

## Corpus Check
- 72 files · ~64,322 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 28 file(s) not represented in the graph (top: .xml 10, .lockfile 7, (none) 4)

## Summary
- 758 nodes · 1332 edges · 46 communities (29 shown, 17 thin omitted)
- Extraction: 89% EXTRACTED · 11% INFERRED · 0% AMBIGUOUS · INFERRED: 152 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `a0cb68ee`
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
- MoneyColors
- PftTheme.kt
- 6.1 Design-system slice: PRs into `feat/m1a-foundation`
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
- TokenSheetScreenshotTest.kt
- Paise
- PaiseParserTest.kt
- Automatic capture from Email (E-0..E-5)
- Personal Finance Tracker PRD
- ColorSchemeTestSupport.kt
- TokenSheetSections.kt
- Delivery constraints (small vertical slices, Claude Pro limits)
- PftTypographyTest
- PftSpacingTest
- PftShapesTest.kt
- CLAUDE.md Development Constitution
- Definition of Done
- Project README
- Domain Safety Invariants
- Research: Indian Bank SMS Formats
- Collecting and Redacting Sample Messages Guide

## God Nodes (most connected - your core abstractions)
1. `PftTypographyTest` - 26 edges
2. `ThemePrefs` - 21 edges
3. `6.1 Design-system slice: PRs into `feat/m1a-foundation`` - 21 edges
4. `ManifestHardeningTest` - 20 edges
5. `HostedComposeRule` - 19 edges
6. `ColorContrastTest` - 19 edges
7. `PftThemeModeTest` - 17 edges
8. `Paise` - 17 edges
9. `CLAUDE.md Development Constitution` - 17 edges
10. `Personal Finance Tracker PRD` - 17 edges

## Surprising Connections (you probably didn't know these)
- `3.2 Theming architecture (no new abstraction)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt
- `9. Documentation updates (all required surfaces)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt
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

## Communities (46 total, 17 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.06
Nodes (17): _day(), _email_record(), html_to_text(), mask_run(), process(), Pseudo, read_adb(), read_any() (+9 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.15
Nodes (16): graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup (+8 more)

### Community 2 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Balance observations (L-10), Budgets (B-1..B-5), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Goals (G-1..G-3), Loans and EMIs (LN-1..LN-5), M1a Core ledger, Milestone 2 (shared entities, P2P sync, balances from email) (+2 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.25
Nodes (5): Personal Finance Tracker Learnings, graphify tool, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Toolchain pins (AGP 8.7.3, Gradle 8.10.2, Kotlin 2.1.0, minSdk 33)

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.47
Nodes (3): Read-only SMS and email access (S-10), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state

### Community 5 - "ManifestHardeningTest"
Cohesion: 0.08
Nodes (11): Manifest security posture (SEC-2, SEC-3, SEC-6), Responsibilities and boundaries, ManifestHardeningTest, Architecture overview, Build and quality gates, Call flow, Security posture of the shell, Shape (+3 more)

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "MoneyColors"
Cohesion: 0.08
Nodes (20): `ui/theme` (design-system tokens, DS1–DS2), BrandColors, MoneyColors, PftSpacing, PftTypography, Colour, Shapes, Spacing (+12 more)

### Community 9 - "PftTheme.kt"
Cohesion: 0.07
Nodes (4): resolveColorScheme(), BundledFontTest, `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`()

### Community 10 - "6.1 Design-system slice: PRs into `feat/m1a-foundation`"
Cohesion: 0.06
Nodes (42): `:app` — Android application, Configuration, Data it owns, Debug and release source sets, Dependencies, How to run and test, Public interface, Purpose (+34 more)

### Community 11 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.21
Nodes (4): ColorContrastTest, Theme, DARK, LIGHT

### Community 18 - "detekt.yml"
Cohesion: 0.29
Nodes (6): ignoreAnnotated, excludes, naming, FunctionNaming, style, MagicNumber

### Community 27 - "HostedComposeRule"
Cohesion: 0.09
Nodes (4): HostedComposeRule, RegisterHostActivity, Statement, DesignCatalogTest

### Community 28 - "Sync via Google Drive app-data (Y-1..Y-8)"
Cohesion: 0.25
Nodes (7): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Client-side encrypted sync with passphrase-derived key (Y-3), Entity merge and split (AD-7), Entity identity key (issuer + type + masked id, deterministic ID), Operation-log based sync (Y-2), Sync via Google Drive app-data (Y-1..Y-8), Sync behind an interface (Y-7)

### Community 30 - "Paise"
Cohesion: 0.06
Nodes (27): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+19 more)

### Community 31 - "PaiseParserTest.kt"
Cohesion: 0.14
Nodes (5): How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest, 7. Test plan

### Community 32 - "Automatic capture from Email (E-0..E-5)"
Cohesion: 0.20
Nodes (11): Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope, Idempotent ingestion (X-2), M1 Phasing (M1a/M1b/M1c), M1b SMS capture, M1c Email capture and sync, Open questions (Q1 banks, Q2 email senders) (+3 more)

### Community 33 - "Personal Finance Tracker PRD"
Cohesion: 0.22
Nodes (11): Personal Finance Tracker PRD, Audit trail (INT-2), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Milestone 3 (insights), Non-functional requirements, Security, privacy and integrity requirements (SEC/INT) (+3 more)

### Community 35 - "TokenSheetSections.kt"
Cohesion: 0.06
Nodes (25): ChoiceRow(), DesignCatalog(), DevContent(), DisplayFont, BUNDLED_SERIF, SYSTEM_SERIF, TEXT, ThemeMode (+17 more)

### Community 36 - "Delivery constraints (small vertical slices, Claude Pro limits)"
Cohesion: 0.33
Nodes (4): Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 37 - "PftTypographyTest"
Cohesion: 0.10
Nodes (5): displayFamily(), pftMaterialTypography(), pftType(), PftTypographyTest, Resolved

### Community 38 - "PftSpacingTest"
Cohesion: 0.18
Nodes (7): PftSpacingTest, Adding a token, Build-time trust decisions, Design system, Previews, the token sheet and screenshot goldens, Principles, Third-party assets

### Community 40 - "CLAUDE.md Development Constitution"
Cohesion: 0.19
Nodes (10): code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /dev Command (implement then auto-review), CLAUDE.md Development Constitution, >=90% Coverage Floor on core, Documentation Taxonomy, Milestones M1a/M1b/M1c, M2, M3 (+2 more)

### Community 41 - "Definition of Done"
Cohesion: 0.29
Nodes (10): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), /architect Command, /review-changes Command, Pull Request Template, PR Checklist (TDD, Docs, Security, Architecture, Sizing), PR Security Review Section (TECHNICAL.md §5), Definition of Done (+2 more)

### Community 42 - "Project README"
Cohesion: 0.20
Nodes (6): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), Code Structure Map, core/shared Shared Packages, Project README, Planned Layout (app, core, tools/redact, fixtures)

### Community 43 - "Domain Safety Invariants"
Cohesion: 0.22
Nodes (4): Android Surface and Multi-user Restrictions, Immutable Audit Trail, Money as Integer Paise, Untrusted Input Validation (SMS/email/LLM output)

### Community 44 - "Research: Indian Bank SMS Formats"
Cohesion: 0.25
Nodes (8): Parse log (S-11), Sender alias registry (S-13), Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser, Public Bank SMS Header Registry (gist, Dec 2022), Open-Source Indian SMS Parsers (hisab-kitab, FinTrack, ...), Indian SMS Sender Header Format (prefix-HEADER-S), SMS Spoofing and Phishing

### Community 45 - "Collecting and Redacting Sample Messages Guide"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

## Knowledge Gaps
- **41 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `BUNDLED_SERIF`, `SYSTEM_SERIF` (+36 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 247 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **17 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Personal Finance Tracker PRD`, `ManifestHardeningTest`, `CLAUDE.md Development Constitution`, ``build-logic` — convention plugins`, `Research: Indian Bank SMS Formats`, `Collecting and Redacting Sample Messages Guide`, `Domain Safety Invariants`?**
  _High betweenness centrality (0.264) - this node is a cross-community bridge._
- **Are the 5 inferred relationships involving `PftTypographyTest` (e.g. with `How to run and test` and `Adding a token`) actually correct?**
  _`PftTypographyTest` has 5 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _41 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.0611764705882353 - nodes in this community are weakly interconnected._
- **Why does `6.1 Design-system slice: PRs into `feat/m1a-foundation`` connect `6.1 Design-system slice: PRs into `feat/m1a-foundation`` to `TokenSheetSections.kt`, `ManifestHardeningTest`, `PftSpacingTest`, `PftShapesTest.kt`, `MoneyColors`, `PftTheme.kt`, `PftTypographyTest`, `WindowThemeTest.kt`, `PftThemeModeTest`, `HostedComposeRule`, `TokenSheetScreenshotTest.kt`, `Paise`?**
  _High betweenness centrality (0.210) - this node is a cross-community bridge._
- **Are the 11 inferred relationships involving `ThemePrefs` (e.g. with `Configuration` and `TokenSheetPreview()`) actually correct?**
  _`ThemePrefs` has 11 INFERRED edges - model-reasoned connections that need verification._
- **Should `ManifestHardeningTest` be split into smaller, more focused modules?**
  _Cohesion score 0.08266129032258064 - nodes in this community are weakly interconnected._