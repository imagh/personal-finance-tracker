# Graph Report - personal-finance-tracker  (2026-10-28)

## Corpus Check
- 21 files · ~19,111 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 205 nodes · 311 edges · 14 communities (9 shown, 5 thin omitted)
- Extraction: 93% EXTRACTED · 7% INFERRED · 0% AMBIGUOUS · INFERRED: 23 edges (avg confidence: 0.86)
- Token cost: 164,480 input · 0 output

## Community Hubs (Navigation)
- Redaction Tool Imports
- Product Requirements (PRD)
- Agent Workflow & PR Process
- Setup, Stack & graphify
- SMS Capture Safety
- Ledger, Discovery & Goals
- Learnings & Plan Template
- Pre-commit Hook Config
- Redaction Tests
- Sample Message Collection
- Pseudonymisation
- HTML Text Extraction
- Code Structure Map
- Data Portability

## God Nodes (most connected - your core abstractions)
1. `CLAUDE.md Development Constitution` - 18 edges
2. `Personal Finance Tracker PRD` - 15 edges
3. `RedactTests` - 12 edges
4. `read_any()` - 9 edges
5. `hooks` - 9 edges
6. `Local Setup Guide` - 9 edges
7. `redact_text()` - 8 edges
8. `Project README` - 8 edges
9. `Loans and EMIs (LN-1..LN-5)` - 8 edges
10. `Definition of Done` - 8 edges

## Surprising Connections (you probably didn't know these)
- `Pure-Kotlin core modules` --semantically_similar_to--> `Modular Layered Architecture (core vs app)`  [INFERRED] [semantically similar]
  docs/TECHNICAL.md → CLAUDE.md
- `PR Checklist (TDD, Docs, Security, Architecture, Sizing)` --semantically_similar_to--> `Definition of Done`  [INFERRED] [semantically similar]
  .github/pull_request_template.md → CLAUDE.md
- `Incremental PR Breakdown` --semantically_similar_to--> `Delivery constraints (small vertical slices, Claude Pro limits)`  [INFERRED] [semantically similar]
  docs/plans/_TEMPLATE.md → docs/PRD.md
- `Project README` --references--> `Collecting and Redacting Sample Messages Guide`  [EXTRACTED]
  README.md → docs/guides/collecting-sample-messages.md
- `Project README` --references--> `Personal Finance Tracker PRD`  [EXTRACTED]
  README.md → docs/PRD.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Layered SMS Parser Strategy** — docs_research_indian_bank_sms_header_registry_gist, docs_research_indian_bank_sms_generic_parser, docs_research_indian_bank_sms_per_bank_overrides, docs_prd_parse_log [EXTRACTED 1.00]
- **Untrusted message handling (templates, unverified flag, read-only)** — docs_prd_deterministic_parsing, docs_prd_untrusted_messages, docs_prd_unverified_state, docs_prd_read_only_messages [INFERRED 0.85]
- **Idempotent ingestion and de-duplication across sources** — docs_prd_idempotent_ingestion, docs_prd_cross_source_dedup, docs_prd_identity_key, docs_prd_reconciliation [INFERRED 0.80]
- **Encrypted offline-first sync stack** — docs_prd_op_log_sync, docs_prd_client_side_encryption, docs_prd_sync_interface, docs_prd_client_ids [INFERRED 0.80]
- **Three non-negotiable process rules (TDD, Docs, Security)** — claude_rule_a, claude_rule_b, claude_rule_c [EXTRACTED 0.95]
- **Owner rules R1-R4** — docs_technical_r1_graphify, docs_technical_r2_no_secrets, docs_technical_r3_starter_kit, docs_technical_r4_security_review [EXTRACTED 0.95]
- **Secrets handling across constitution, technical rules and setup guide** — claude_rule_c, docs_technical_r2_no_secrets, docs_guides_local_setup_secrets_env [INFERRED 0.85]

## Communities (14 total, 5 thin omitted)

### Community 0 - "Redaction Tool Imports"
Cohesion: 0.09
Nodes (16): _day(), _email_record(), html_to_text(), main(), mask_run(), process(), read_adb(), read_any() (+8 more)

### Community 1 - "Product Requirements (PRD)"
Cohesion: 0.09
Nodes (26): Personal Finance Tracker PRD, Audit trail (INT-2), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Client-side encrypted sync with passphrase-derived key (Y-3), Automatic capture from Email (E-0..E-5), Unverified Gmail OAuth client risk (E-5), Gmail API gmail.readonly scope (+18 more)

### Community 2 - "Agent Workflow & PR Process"
Cohesion: 0.15
Nodes (19): architect Agent, Architect Plan (docs/plans/YYYY-MM-DD-slug.md), code-reviewer Agent, Compact Review Report (approve/minor/changes requested), senior-full-stack-dev Agent, /architect Command, /dev Command (implement then auto-review), /review-changes Command (+11 more)

### Community 3 - "Setup, Stack & graphify"
Cohesion: 0.14
Nodes (16): graphify Usage Rules, Tech Stack (Kotlin, Room, SQLCipher, Compose), Local Setup Guide, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install, graphify Machine Setup, Secrets via Environment Variables (+8 more)

### Community 4 - "SMS Capture Safety"
Cohesion: 0.16
Nodes (12): Budgets (B-1..B-5), Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Unverified vs verified transaction state, Research: Indian Bank SMS Formats, Generic Bank-Agnostic Parser (+4 more)

### Community 5 - "Ledger, Discovery & Goals"
Cohesion: 0.19
Nodes (12): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Balance observations (L-10), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5) (+4 more)

### Community 6 - "Learnings & Plan Template"
Cohesion: 0.15
Nodes (8): Personal Finance Tracker Learnings, graphify tool, Architect Plan Template, Required Documentation Updates Section, TDD-Ordered Implementation Steps, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Delivery constraints (small vertical slices, Claude Pro limits)

### Community 7 - "Pre-commit Hook Config"
Cohesion: 0.15
Nodes (12): always_run, entry, files, id, language, name, pass_filenames, stages (+4 more)

### Community 9 - "Sample Message Collection"
Cohesion: 0.43
Nodes (6): Collecting and Redacting Sample Messages Guide, Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides

## Knowledge Gaps
- **29 isolated node(s):** `core/shared Shared Packages`, `Required Documentation Updates Section`, `TDD-Ordered Implementation Steps`, `Code Structure Map`, `Compact Review Report (approve/minor/changes requested)` (+24 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 78 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **5 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Collecting and Redacting Sample Messages Guide` connect `Sample Message Collection` to `Product Requirements (PRD)`, `Setup, Stack & graphify`?**
  _High betweenness centrality (0.409) - this node is a cross-community bridge._
- **What connects `core/shared Shared Packages`, `Required Documentation Updates Section`, `TDD-Ordered Implementation Steps` to the rest of the system?**
  _29 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Redaction Tool Imports` be split into smaller, more focused modules?**
  _Cohesion score 0.08717948717948718 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Setup, Stack & graphify` to `Sample Message Collection`, `Agent Workflow & PR Process`, `SMS Capture Safety`, `Product Requirements (PRD)`?**
  _High betweenness centrality (0.387) - this node is a cross-community bridge._
- **Should `Product Requirements (PRD)` be split into smaller, more focused modules?**
  _Cohesion score 0.09401709401709402 - nodes in this community are weakly interconnected._
- **Why does `Local Redaction Procedure` connect `Sample Message Collection` to `Redaction Tool Imports`?**
  _High betweenness centrality (0.386) - this node is a cross-community bridge._
- **Should `Setup, Stack & graphify` be split into smaller, more focused modules?**
  _Cohesion score 0.1368421052631579 - nodes in this community are weakly interconnected._