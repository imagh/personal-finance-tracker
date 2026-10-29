# Graph Report - personal-finance-tracker  (2026-10-29)

## Corpus Check
- 39 files · ~34,844 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 22 file(s) not represented in the graph (top: .lockfile 7, .xml 6, (none) 3)

## Summary
- 366 nodes · 511 edges · 24 communities (10 shown, 14 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 33 edges (avg confidence: 0.89)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `e7dc6f2a`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Redaction Tool Imports
- Agent Workflow & PR Process
- PRD & Plan Template
- Architecture, Stack & graphify
- Learnings & Toolchain
- ManifestHardeningTest.kt
- Pre-commit Hook Config
- Redaction Tests
- Pseudonymisation
- Plan: M1a — Core ledger (manual-entry household tracker)
- Project README
- Data portability (D-1..D-3)
- PftKotlinCorePlugin.kt
- `build-logic` — convention plugins
- Catalog.kt
- detekt.yml

## God Nodes (most connected - your core abstractions)
1. `CLAUDE.md Development Constitution` - 17 edges
2. `Personal Finance Tracker PRD` - 17 edges
3. `ManifestHardeningTest` - 14 edges
4. `Plan: M1a — Core ledger (manual-entry household tracker)` - 13 edges
5. `Project README` - 13 edges
6. `RedactTests` - 12 edges
7. `code-reviewer Agent` - 11 edges
8. `hooks` - 10 edges
9. `6. Implementation steps (TDD-ordered)` - 10 edges
10. `Local Setup Guide` - 10 edges

## Surprising Connections (you probably didn't know these)
- `Security posture of the shell` --references--> `ManifestHardeningTest`  [INFERRED]
  docs/architecture/overview.md → app/src/test/kotlin/dev/imagh/pft/app/ManifestHardeningTest.kt
- `PR breakdown (incremental — required)` --references--> `main()`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → tools/redact/redact.py
- `5. Flows` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `Slice A — Foundation (`feat/m1a-foundation`)` --references--> `MainActivity`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt
- `7. Test plan` --references--> `ManifestHardeningTest`  [INFERRED]
  docs/plans/2026-10-28-m1a-core-ledger.md → app/src/test/kotlin/dev/imagh/pft/app/ManifestHardeningTest.kt

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

## Communities (24 total, 14 thin omitted)

### Community 0 - "Redaction Tool Imports"
Cohesion: 0.07
Nodes (17): _day(), _email_record(), html_to_text(), main(), mask_run(), process(), read_adb(), read_any() (+9 more)

### Community 1 - "Agent Workflow & PR Process"
Cohesion: 0.11
Nodes (23): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+15 more)

### Community 2 - "PRD & Plan Template"
Cohesion: 0.07
Nodes (38): Personal Finance Tracker PRD, Audit trail (INT-2), Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Client-side encrypted sync with passphrase-derived key (Y-3), Core ledger (L-1..L-10) (+30 more)

### Community 3 - "Architecture, Stack & graphify"
Cohesion: 0.07
Nodes (26): PR Security Review Section (TECHNICAL.md §5), graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Local Setup Guide, Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install (+18 more)

### Community 4 - "Learnings & Toolchain"
Cohesion: 0.11
Nodes (18): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10) (+10 more)

### Community 6 - "Pre-commit Hook Config"
Cohesion: 0.14
Nodes (13): always_run, entry, exclude, files, id, language, name, pass_filenames (+5 more)

### Community 10 - "Plan: M1a — Core ledger (manual-entry household tracker)"
Cohesion: 0.05
Nodes (35): `:app` — Android application, Configuration, Data it owns, Dependencies, How to run and test, Manifest security posture (SEC-2, SEC-3, SEC-6), Public interface, Purpose (+27 more)

### Community 11 - "Project README"
Cohesion: 0.10
Nodes (15): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), `:core:money` — money (stub), How to run and test, Purpose, Code Structure Map, core/shared Shared Packages, Architecture overview (+7 more)

### Community 16 - "`build-logic` — convention plugins"
Cohesion: 0.15
Nodes (12): `build-logic` — convention plugins, Configuration, Data it owns, Dependencies, How the build scripts and build-logic are linted, How to run and test, Public interface, Purpose (+4 more)

### Community 18 - "detekt.yml"
Cohesion: 0.29
Nodes (6): ignoreAnnotated, excludes, naming, FunctionNaming, style, MagicNumber

## Knowledge Gaps
- **33 isolated node(s):** `Compact Review Report (approve/minor/changes requested)`, `Android Surface and Multi-user Restrictions`, `>=90% Coverage Floor on core`, `Documentation Taxonomy`, `Base Feature Branch Topology` (+28 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 167 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **14 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Project README` connect `Project README` to `Agent Workflow & PR Process`, `PRD & Plan Template`, `Architecture, Stack & graphify`, `Learnings & Toolchain`, ``build-logic` — convention plugins`?**
  _High betweenness centrality (0.352) - this node is a cross-community bridge._
- **Are the 4 inferred relationships involving `ManifestHardeningTest` (e.g. with `Manifest security posture (SEC-2, SEC-3, SEC-6)` and `Responsibilities and boundaries`) actually correct?**
  _`ManifestHardeningTest` has 4 INFERRED edges - model-reasoned connections that need verification._
- **What connects `Compact Review Report (approve/minor/changes requested)`, `Android Surface and Multi-user Restrictions`, `>=90% Coverage Floor on core` to the rest of the system?**
  _33 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Redaction Tool Imports` be split into smaller, more focused modules?**
  _Cohesion score 0.07246376811594203 - nodes in this community are weakly interconnected._
- **Why does `Collecting and Redacting Sample Messages Guide` connect `Learnings & Toolchain` to `Project README`, `PRD & Plan Template`, `Architecture, Stack & graphify`?**
  _High betweenness centrality (0.208) - this node is a cross-community bridge._
- **Should `Agent Workflow & PR Process` be split into smaller, more focused modules?**
  _Cohesion score 0.10588235294117647 - nodes in this community are weakly interconnected._
- **Why does `ManifestHardeningTest` connect `ManifestHardeningTest.kt` to `Plan: M1a — Core ledger (manual-entry household tracker)`, `Project README`?**
  _High betweenness centrality (0.206) - this node is a cross-community bridge._