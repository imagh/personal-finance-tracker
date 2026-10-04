# Technical Implementation Rules

| | |
|---|---|
| **Status** | Draft v0.2 — builds are local |
| **Applies to** | Everyone who writes code here: the owner, and Claude sessions and agents |
| **Companion docs** | [PRD](PRD.md), [message collection guide](guides/collecting-sample-messages.md) |

**Legend.** **[Owner rule]** is stated by the owner and non-negotiable. **[Proposed]** is my recommendation and needs confirmation. **[Verify]** means I could only confirm it from web search summaries, so check it at install time.

**Precedence.** Once the starter kit is adopted (rule 3), its `CLAUDE.md` constitution governs process. If it conflicts with this document, the stricter rule wins and the conflict gets fixed in whichever file is wrong.

---

## 1. The owner's rules

| # | Rule | Section |
|---|---|---|
| R1 | Use **graphify** to reduce token usage | [2](#2-r1--graphify-for-token-efficiency) |
| R2 | **No secrets in the repo.** They are read from the installed environment | [3](#3-r2--no-secrets-in-the-repo) |
| R3 | Set up agents and rules with **`imagh/claude-project-starter-kit`** | [4](#4-r3--claude-project-starter-kit) |
| R4 | **Security review before every PR** | [5](#5-r4--security-review-before-every-pr) |

---

## 2. R1 — graphify for token efficiency

Graphify turns the repo into a queryable knowledge graph so a session reads the relevant subgraph instead of re-reading files. **[Owner rule]**

Commands below are from the project's README as of 2026-10-04 **[Verify]**: repo `safishamsi/graphify` (now under the Graphify-Labs org).

```bash
uv tool install graphifyy          # PyPI name is temporarily "graphifyy" (double y)
graphify install --project         # register the skill for this project only
graphify claude install            # writes a CLAUDE.md section + a PreToolUse hook
graphify hook install              # rebuild on commit / branch checkout
```

Rules:
1. **Vet before installing.** The PyPI name is a stopgap, which is typosquat territory. Confirm the PyPI page links to the upstream GitHub repo, then **pin an exact version** and re-vet on every upgrade. Install it in the cloud environment's setup script, not per session.
2. **Code-only mode by default** (`--code-only` / default AST mode): parsed locally with tree-sitter, zero LLM tokens. Never use `--mode deep` without the owner's approval. Docs, PDFs and images are sent to the model for extraction, which costs tokens and exposes content.
3. **Keep personal data out of the graph's reach.** Real exports and unreviewed redaction output live outside the repo tree (and are git-ignored). Only reviewed fixtures live in `fixtures/`.
4. **Query before reading.** In any session: `/graphify query`, `explain` or `path` first, then open only the files the graph points to. Use Grep for exact strings. Don't `cat` whole directories to "get oriented".
5. **Keep the graph fresh.** `graphify hook install` rebuilds on commit; after `git pull`, run `graphify update .`. Stale graphs are worse than none, so a PR that restructures modules regenerates the graph in the same PR.
6. **Commit** `graphify-out/graph.json` and `GRAPH_REPORT.md` so fresh cloud clones start with a graph; `graph.html` stays local. **[Proposed]** Reviewers skim generated diffs. Revisit if `graph.json` gets large or noisy.
7. **Merge, don't clobber.** `graphify claude install` edits `CLAUDE.md` and adds a hook. Merge it into the starter kit's `CLAUDE.md` and `.claude/settings.json`, and read the hook before accepting it. Hooks execute commands.

## 3. R2 — no secrets in the repo

Secret values come from the installed environment and never from files in the repo. **[Owner rule]**

| Secret | Where it lives | How code gets it |
|---|---|---|
| Release signing keystore and passwords | Outside the repo (e.g. `~/.android/`), passwords in env | Gradle reads `ANDROID_KEYSTORE_PATH`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD` from env or `~/.gradle/gradle.properties`. Release builds fail closed if missing. Debug builds use the debug key. |
| Google OAuth client ID(s) | Env or `local.properties` (git-ignored) | Injected into `BuildConfig` at build time. Android OAuth clients have no client secret, so never embed a web client secret. |
| Cloud/CI tokens | Cloud environment secrets / GitHub Actions secrets | Provided as env vars. Never pasted in chat or PRs. |
| **Runtime user secrets** (Gmail/Drive tokens, DB key, sync key/passphrase) | Android Keystore-backed storage on the device | Never in source, logs, backups or sync payloads. |

Rules:
1. Commit only `*.example` templates (`local.properties.example`, `.env.example`) listing variable **names** only.
2. `.gitignore` already blocks `.env*`, `local.properties`, keystores, `google-services.json`, `client_secret*.json`, `secrets/`, `credentials/`.
3. Layered enforcement: `.gitignore`, a pre-commit secret scan (gitleaks), the starter kit's `.claude/settings.json` deny-list (agents can't read `.env*`, keys, `.git/`), GitHub secret scanning before each PR, and a CI secret scan.
4. No secrets or personal data in logs, test fixtures, docs, commit messages or PR text.
5. **If a secret leaks: rotate it first**, then clean history. Rewriting history doesn't un-leak.

## 4. R3 — Claude project starter kit

Source: `imagh/claude-project-starter-kit` v1.0.0. It provides a development constitution (`CLAUDE.md`: strict TDD, mandatory docs, built-in security, small atomic commits and PRs), three role agents (**architect**, **senior-full-stack-dev**, **code-reviewer**), slash commands (`/architect`, `/dev`, `/review-changes`), a PR template, a plan template, a guardrail `.claude/settings.json` (denies secret paths, blocks `gh pr merge`, blocks `--no-verify` and `git add -f`), and a `.pre-commit-config.yaml`.

**Adoption is the first slice (S0) of M1a**, as its own small PR: `chore: adopt development constitution + role agents`. The kit is an interview, not a drop-in, so S0 starts with the owner's answers to the questions below. Its `SETUP.md` runs the full interview.

Proposed answers (the owner confirms or changes each):

| Kit placeholder | Proposed value |
|---|---|
| Project | Personal Finance Tracker — private, offline-first household expense tracker for Android |
| Phasing | M1 (a/b/c) → M2 → M3, see PRD §6 |
| Language / UI | Kotlin, Jetpack Compose |
| Backend / frontend | N/A (no server) |
| Database | SQLite via Room + SQLCipher |
| Other infra | WorkManager; Google Drive appData and Gmail read-only via Google APIs |
| Build | Local machine (Android Studio + Gradle), owner's Claude Pro plan |
| Test framework | JUnit 5 + Kotest for the pure `core` modules; Robolectric where needed. Instrumented tests run locally only |
| Coverage floor | 90% on `core` modules; no floor on thin UI code |
| Lint/format | ktlint + detekt; pre-commit runs gitleaks |
| Dependency vetting | Gradle dependency locking/verification + OWASP dependency-check |
| Domain safety rule | Integer-paise money; audit trail on every change; SMS/email are never modified; no financial content in logs; deterministic parsers |
| Architecture (§3.1) | Layered Android modules (see §6), not the kit's backend layering |
| Doc taxonomy | Kit default under `docs/` |
| Review/merge human | The owner. Agents never merge |

**Model policy [Confirmed by the owner, 2026-10-04]:**

| Agent | Model | Effort |
|---|---|---|
| architect | Opus | high |
| senior-full-stack-dev (also does all parser/template work) | Sonnet | high |
| code-reviewer | Sonnet | high *(the owner did not specify the reviewer; set to match the developer, to confirm)* |

The kit's default (`xhigh` everywhere, reviewer on the session model) was replaced. A spawner may bump a run to Opus for a genuinely hard step (novel pattern, crypto, subtle concurrency) and must say so.

## 5. R4 — security review before every PR

No PR is opened until a security review of the branch diff is done and recorded. **[Owner rule]**

1. Run the **`/security-review`** skill on the branch, and the kit's **`/review-changes`** (code-reviewer).
2. Fix every High and Medium finding. Low findings are fixed or listed in the PR with a reason. The owner decides on anything accepted.
3. The PR body contains a **Security review** section: what was run, the result, and the checklist below. The kit's PR template gets a matching checkbox (S0).
4. CI blocks the merge on: secret scan, dependency vulnerability scan, lint, tests.
5. Agents open and update PRs but **never merge** (enforced by the kit's deny-list).
6. Repeat before every release APK.

**App-specific checklist** (any "no" blocks the PR):
- [ ] No secrets, tokens, keys, real account numbers or real messages in the diff, including tests and fixtures.
- [ ] New permissions or OAuth scopes: none, or justified and approved. Allowed scopes are only `gmail.readonly` and `drive.appdata`.
- [ ] Manifest: `allowBackup=false` (or encrypted), nothing `exported` without need, SMS receiver permission-protected, cleartext traffic off.
- [ ] No logging of amounts, payees, message text, account identifiers or tokens.
- [ ] SMS/email content treated as untrusted input (validated, never executed, never trusted for sender identity).
- [ ] No code path deletes or modifies SMS/email.
- [ ] DB access via Room/parameterised queries only; schema change has a migration and a migration test.
- [ ] Money uses integer paise end to end. No floating point.
- [ ] Anything leaving the device is encrypted client-side first, and contains no raw message text.
- [ ] New dependencies: vetted, pinned, minimal, licence checked. No analytics or crash SDKs.
- [ ] Network calls only to the allowed Google endpoints.
- [ ] Sensitive screens use `FLAG_SECURE` and respect app lock.

## 6. Proposed engineering rules

**Architecture.** Android app, Kotlin, Jetpack Compose, Room + SQLCipher, WorkManager.
- **Pure-Kotlin `core` modules with no Android imports**: money, ledger, parsing, entity resolution (account/loan/card discovery), matching and de-duplication, recurring engine, budget math, sync merge. These carry the business risk and run on a plain JVM, so they are fast to test, cheap in tokens, and buildable in the cloud environment without the Android SDK.
- Android modules (UI, SMS receiver, Gmail/Drive clients, Keystore, DB wiring) stay thin and depend on `core`.
- Interfaces only at real swap boundaries (e.g. the sync transport that M2 replaces), as the kit's abstraction bar says.

**Testing.** TDD per the kit. Parsers are template + fixture tests. Property-based tests for money and sync merge. Instrumented and emulator tests run locally, not in the cloud.

**Data and privacy engineering.** Parsers, matching and categorisation are on-device and deterministic. A restrictive network security config allows only the Google API hosts. No analytics, ads or crash SDKs. Fixtures are reviewed redacted output only, under `fixtures/` (tool: `tools/redact`).

**Migrations.** Versioned Room migrations with exported schemas committed, migration tests, and an automatic pre-migration backup.

**Git and PRs.** Small atomic commits, small incremental PRs, one slice per PR. Never force-push or bypass hooks (`--no-verify`). Feature branches only. The owner merges.

**Definition of done (per slice).** Tests written first and passing; docs updated; graph refreshed; security review recorded; PR within the size cap.

## 7. Build environment **[Confirmed]**

Everything is built and tested **locally on the owner's machine**; see [local setup guide](guides/local-setup.md). Cloud sessions are not used for builds. (Finding from 2026-10-04, kept for context: the cloud environment's network policy blocked `dl.google.com`, which is where the Android SDK is downloaded from. It no longer matters.) The pure-Kotlin `core` split still pays off: those modules test in seconds on a plain JVM, without an emulator, which keeps sessions short and cheap.

## 8. Token and cost discipline

Budget: the owner's **Claude Pro plan** usage limits (building locally). Rules: one slice per session; query the graph first (R1); no broad exploration or "audit everything" prompts; no subagents unless the plan calls for them; deterministic code and tests over long debugging; write research findings into `docs/research/` once. PRD §16 has the slice plan.
