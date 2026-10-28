# Graph Report - personal-finance-tracker  (2026-10-28)

## Corpus Check
- Corpus is ~19,125 words - fits in a single context window. You may not need a graph.

## Summary
- 223 nodes · 340 edges · 14 communities (8 shown, 6 thin omitted)
- Extraction: 94% EXTRACTED · 6% INFERRED · 0% AMBIGUOUS · INFERRED: 22 edges (avg confidence: 0.86)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- HTML Text Extraction
- Redaction Tests
- Pseudonymisation
- Redaction Tool Imports
- Pre-commit Hook Config
- Agent Workflow & PR Process
- Code Structure Map
- Data portability (D-1..D-3)
- PRD & Plan Template
- Architecture, Stack & graphify
- Learnings & Toolchain
- Ledger, Discovery & Budgets
- Sample Message Collection

## God Nodes (most connected - your core abstractions)
1. `CLAUDE.md Development Constitution` - 17 edges
2. `Personal Finance Tracker PRD` - 16 edges
3. `RedactTests` - 12 edges
4. `code-reviewer Agent` - 11 edges
5. `read_any()` - 9 edges
6. `hooks` - 9 edges
7. `Local Setup Guide` - 9 edges
8. `redact_text()` - 8 edges
9. `Definition of Done` - 8 edges
10. `senior-full-stack-dev Agent` - 8 edges

## Surprising Connections (you probably didn't know these)
- `PR Checklist (TDD, Docs, Security, Architecture, Sizing)` --semantically_similar_to--> `Definition of Done`  [INFERRED] [semantically similar]
  .github/pull_request_template.md → CLAUDE.md
- `App-specific Security Checklist` --conceptually_related_to--> `Domain Safety Invariants`  [INFERRED]
  docs/TECHNICAL.md → CLAUDE.md
- `Definition of Done` --references--> `R4 - Security review before every PR`  [EXTRACTED]
  CLAUDE.md → docs/TECHNICAL.md
- `/architect Command` --references--> `Development Workflow (Plan, Implement, Document, Review, Learn, PR)`  [EXTRACTED]
  .claude/commands/architect.md → CLAUDE.md
- `senior-full-stack-dev Agent` --references--> `Pre-commit Quality Gates (secret scan, ktlint, detekt, gradlew check)`  [EXTRACTED]
  .claude/agents/senior-full-stack-dev.md → CLAUDE.md

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

## Communities (14 total, 6 thin omitted)

### Community 0 - "Redaction Tool Imports"
Cohesion: 0.09
Nodes (16): _day(), _email_record(), html_to_text(), main(), mask_run(), process(), read_adb(), read_any() (+8 more)

### Community 6 - "Pre-commit Hook Config"
Cohesion: 0.15
Nodes (12): always_run, entry, files, id, language, name, pass_filenames, stages (+4 more)

### Community 1 - "Agent Workflow & PR Process"
Cohesion: 0.10
Nodes (24): Architect Plan (docs/plans/YYYY-MM-DD-slug.md), Compact Review Report (approve/minor/changes requested), PR Checklist (TDD, Docs, Security, Architecture, Sizing), PR Security Review Section (TECHNICAL.md §5), Android Surface and Multi-user Restrictions, Immutable Audit Trail, >=90% Coverage Floor on core, Definition of Done (+16 more)

### Community 2 - "PRD & Plan Template"
Cohesion: 0.07
Nodes (32): Required Documentation Updates Section, TDD-Ordered Implementation Steps, Audit trail (INT-2), Balance observations (L-10), Cashew (existing expense app), Client-Generated Time-Sortable IDs (UUIDv7/ULID), Client-side encrypted sync with passphrase-derived key (Y-3), Delivery constraints (small vertical slices, Claude Pro limits) (+24 more)

### Community 3 - "Architecture, Stack & graphify"
Cohesion: 0.10
Nodes (21): app/ Thin Android Layer, core/<module> (money, ledger, parsing, discovery, matching, recurring, budget, sync), graphify Usage Rules, Tech Stack (Kotlin, Compose, Room+SQLCipher, Gradle), Agent Slash Commands, Android Toolchain Setup (macOS), Emulator and Primary Phone Policy, Pre-commit Git Hooks Install (+13 more)

### Community 4 - "Learnings & Toolchain"
Cohesion: 0.11
Nodes (14): graphify tool, Room 2.7.1 and sqlcipher-android 4.7.2, Decisions log, Parse log (S-11), Read-only SMS and email access (S-10), Sender alias registry (S-13), Automatic capture from SMS (S-1..S-13), Generic Bank-Agnostic Parser (+6 more)

### Community 5 - "Ledger, Discovery & Budgets"
Cohesion: 0.19
Nodes (12): Automatic discovery of accounts, cards and loans (AD-1..AD-8), Budgets (B-1..B-5), Core ledger (L-1..L-10), Cross-source de-duplication (X-3, X-4), Entity merge and split (AD-7), Goals (G-1..G-3), Entity identity key (issuer + type + masked id, deterministic ID), Loans and EMIs (LN-1..LN-5) (+4 more)

### Community 8 - "Sample Message Collection"
Cohesion: 0.43
Nodes (6): Email Export (.eml / Google Takeout mbox), fixtures/ Reviewed Redacted Samples, Local Redaction Procedure, SMS Export (SMS Backup & Restore / adb), Per-Bank Template Overrides, Collecting and Redacting Sample Messages Guide

## Knowledge Gaps
- **34 isolated node(s):** `Compact Review Report (approve/minor/changes requested)`, `Android Surface and Multi-user Restrictions`, `>=90% Coverage Floor on core`, `Documentation Taxonomy`, `Base Feature Branch Topology` (+29 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 89 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **6 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Collecting and Redacting Sample Messages Guide` connect `Sample Message Collection` to `PRD & Plan Template`, `Architecture, Stack & graphify`?**
  _High betweenness centrality (0.393) - this node is a cross-community bridge._
- **What connects `Compact Review Report (approve/minor/changes requested)`, `Android Surface and Multi-user Restrictions`, `>=90% Coverage Floor on core` to the rest of the system?**
  _34 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Redaction Tool Imports` be split into smaller, more focused modules?**
  _Cohesion score 0.08717948717948718 - nodes in this community are weakly interconnected._
- **Why does `Local Redaction Procedure` connect `Sample Message Collection` to `Redaction Tool Imports`?**
  _High betweenness centrality (0.371) - this node is a cross-community bridge._
- **Should `Agent Workflow & PR Process` be split into smaller, more focused modules?**
  _Cohesion score 0.10317460317460317 - nodes in this community are weakly interconnected._
- **Why does `Project README` connect `Architecture, Stack & graphify` to `Sample Message Collection`, `Agent Workflow & PR Process`, `PRD & Plan Template`, `Learnings & Toolchain`?**
  _High betweenness centrality (0.346) - this node is a cross-community bridge._
- **Should `PRD & Plan Template` be split into smaller, more focused modules?**
  _Cohesion score 0.07226890756302522 - nodes in this community are weakly interconnected._