# Graph Report - personal-finance-tracker  (2026-10-29)

## Corpus Check
- 58 files · ~56,907 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 26 file(s) not represented in the graph (top: .xml 9, .lockfile 7, (none) 4)

## Summary
- 569 nodes · 896 edges · 36 communities (20 shown, 16 thin omitted)
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 92 edges (avg confidence: 0.93)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `b6dbf3bf`
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
- ThemePrefs
- ThemeResolutionTest.kt
- Plan: M1a — Core ledger (manual-entry household tracker)
- Project README
- Data portability (D-1..D-3)
- ColorContrastTest
- PftKotlinCorePlugin.kt
- WindowThemeTest.kt
- Catalog.kt
- detekt.yml
- PftThemeModeTest
- HostedComposeRule.kt
- Personal Finance Tracker PRD
- MainActivity
- Paise
- PaiseParserTest.kt
- Sync via Google Drive app-data (Y-1..Y-8)
- Security, privacy and integrity requirements (SEC/INT)
- ColorSchemeTestSupport.kt
- Delivery constraints (small vertical slices, Claude Pro limits)

## God Nodes (most connected - your core abstractions)
1. `ManifestHardeningTest` - 20 edges
2. `PftThemeModeTest` - 17 edges
3. `Paise` - 17 edges
4. `CLAUDE.md Development Constitution` - 17 edges
5. `Personal Finance Tracker PRD` - 17 edges
6. `ColorContrastTest` - 16 edges
7. `MainActivity` - 15 edges
8. `WindowThemeTest` - 15 edges
9. `Plan: M1a — Core ledger (manual-entry household tracker)` - 13 edges
10. `Plan: M1a design system and visual direction (lands before A5)` - 13 edges

## Surprising Connections (you probably didn't know these)
- `3.5 Design system (owner decision, 2026-10-29)` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `6.3 Amendments to M1a steps (consumption; this plan edits M1a when approved)` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `9. Documentation updates (all required surfaces)` --references--> `ThemePrefs`  [INFERRED]
  docs/plans/2026-10-29-m1a-design-system.md → app/src/main/kotlin/dev/imagh/pft/app/ui/theme/ThemePrefs.kt
- `Security posture of the shell` --references--> `ManifestHardeningTest`  [INFERRED]
  docs/architecture/overview.md → app/src/test/kotlin/dev/imagh/pft/app/ManifestHardeningTest.kt

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

## Communities (36 total, 16 thin omitted)

### Community 0 - "redact.py"
Cohesion: 0.06
Nodes (19): PR breakdown (incremental — required), _day(), _email_record(), html_to_text(), main(), mask_run(), process(), Pseudo (+11 more)

### Community 1 - "docs/TECHNICAL.md"
Cohesion: 0.07
Nodes (41): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+33 more)

### Community 2 - "Loans and EMIs (LN-1..LN-5)"
Cohesion: 0.22
Nodes (10): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5), M1a Core ledger (+2 more)

### Community 3 - "Personal Finance Tracker Learnings"
Cohesion: 0.29
Nodes (4): Personal Finance Tracker Learnings, graphify tool, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log

### Community 4 - "Automatic capture from SMS (S-1..S-13)"
Cohesion: 0.32
Nodes (5): Budgets (B-1..B-5), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state

### Community 6 - "hooks"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 8 - "ThemePrefs"
Cohesion: 0.09
Nodes (17): `ui/theme` (design-system tokens, DS1), BrandColors, MoneyColors, ThemeMode, DARK, LIGHT, SYSTEM, ThemePrefs (+9 more)

### Community 9 - "ThemeResolutionTest.kt"
Cohesion: 0.12
Nodes (3): resolveColorScheme(), `dynamic dark resolves to the system dynamic dark scheme`(), `dynamic light resolves to the system dynamic light scheme`()

### Community 10 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.08
Nodes (24): 10. Risks & mitigations, 11. Rollout & verification, 12. Open questions, 1. Requirement, 2. Current state, 3.1 Modules (Gradle), 3.2 Key decisions, 3.3 New dependencies (all pinned, vetted, recorded in `gradle/verification-metadata.xml`) (+16 more)

### Community 11 - "Project README"
Cohesion: 0.05
Nodes (38): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+30 more)

### Community 14 - "ColorContrastTest"
Cohesion: 0.21
Nodes (4): ColorContrastTest, Theme, DARK, LIGHT

### Community 18 - "detekt.yml"
Cohesion: 0.29
Nodes (6): ignoreAnnotated, excludes, naming, FunctionNaming, style, MagicNumber

### Community 27 - "HostedComposeRule.kt"
Cohesion: 0.20
Nodes (3): HostedComposeRule, RegisterHostActivity, Statement

### Community 28 - "Personal Finance Tracker PRD"
Cohesion: 0.20
Nodes (10): Personal Finance Tracker PRD, Balance observations (L-10), Cashew (existing expense app), Unverified Gmail OAuth client risk (E-5), Milestone 2 (shared entities, P2P sync, balances from email), Milestone 3 (insights), Non-functional requirements, Risks and dependencies (+2 more)

### Community 29 - "MainActivity"
Cohesion: 0.09
Nodes (15): `:app` — Android application, Configuration, Data it owns, Debug and release source sets, Dependencies, How to run and test, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface (+7 more)

### Community 30 - "Paise"
Cohesion: 0.05
Nodes (32): Configuration, `:core:money` — integer-paise money, Data it owns, Dependencies, Formatting and the sign (decision), Parsing grammar, Public interface, Purpose (+24 more)

### Community 31 - "PaiseParserTest.kt"
Cohesion: 0.13
Nodes (6): How to run and test, PaiseFormatTest, PaiseParserTest, PaiseTest, 7. Test plan, Slice A — Foundation (`feat/m1a-foundation`)

### Community 32 - "Sync via Google Drive app-data (Y-1..Y-8)"
Cohesion: 0.20
Nodes (12): Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Gmail API gmail.readonly scope, Idempotent ingestion (X-2), M1 Phasing (M1a/M1b/M1c), M1b SMS capture, M1c Email capture and sync, Operation-log based sync (Y-2) (+4 more)

### Community 33 - "Security, privacy and integrity requirements (SEC/INT)"
Cohesion: 0.40
Nodes (6): Audit trail (INT-2), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Money as integer paise, Security, privacy and integrity requirements (SEC/INT), SQLCipher encryption at rest and app lock, Platform and technology decisions (Kotlin, Compose, Room, SQLCipher, paise, UUIDv7)

### Community 36 - "Delivery constraints (small vertical slices, Claude Pro limits)"
Cohesion: 0.33
Nodes (4): Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Delivery constraints (small vertical slices, Claude Pro limits)

## Knowledge Gaps
- **38 isolated node(s):** `SYSTEM`, `LIGHT`, `DARK`, `LIGHT`, `DARK` (+33 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 212 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **16 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Project README` connect `Project README` to `docs/TECHNICAL.md`, `Personal Finance Tracker PRD`, `Paise`?**
  _High betweenness centrality (0.313) - this node is a cross-community bridge._
- **Are the 10 inferred relationships involving `ManifestHardeningTest` (e.g. with `Manifest security posture (SEC-2, SEC-3, SEC-6)` and `Responsibilities and boundaries`) actually correct?**
  _`ManifestHardeningTest` has 10 INFERRED edges - model-reasoned connections that need verification._
- **What connects `SYSTEM`, `LIGHT`, `DARK` to the rest of the system?**
  _38 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `redact.py` be split into smaller, more focused modules?**
  _Cohesion score 0.0593990216631726 - nodes in this community are weakly interconnected._
- **Why does ``:app` — Android application` connect `MainActivity` to `Project README`?**
  _High betweenness centrality (0.177) - this node is a cross-community bridge._
- **Are the 3 inferred relationships involving `PftThemeModeTest` (e.g. with `How to run and test` and `6.1 Design-system slice: PRs into `feat/m1a-foundation``) actually correct?**
  _`PftThemeModeTest` has 3 INFERRED edges - model-reasoned connections that need verification._
- **Should `docs/TECHNICAL.md` be split into smaller, more focused modules?**
  _Cohesion score 0.06599326599326599 - nodes in this community are weakly interconnected._