# Personal Finance Tracker — Development Constitution

This file is the source of truth for **how** we build Personal Finance Tracker. It is loaded into
every Claude Code session. Read it before writing code, planning, or reviewing. Rules here
are non-negotiable unless the user explicitly overrides them for a task.

Product spec: `docs/PRD.md`. Engineering rules: `docs/TECHNICAL.md`; read it alongside this file, and where they differ the stricter rule wins. The phase plan is PRD §6.

---

## 1. What we are building

Personal Finance Tracker is a private, offline-first Android app that tracks a household's expenses, budgets, loans and goals, and logs transactions automatically from SMS and email. It is for personal use by two people, where accuracy, integrity, privacy and security outrank convenience. Data lives in local SQLite (SQLCipher-encrypted), messages are parsed on-device with deterministic templates, and nothing leaves the device unencrypted. Requirements are in `docs/PRD.md`; engineering rules are in `docs/TECHNICAL.md`.
It is built as three milestones: M1 (single-household tracker, delivered as M1a core ledger, M1b SMS capture, M1c email capture and sync), M2 (shared entities, serverless P2P sync, balances from email) and M3 (insights). Currently M1a.

## 2. Tech stack

| Concern            | Choice                     |
|--------------------|----------------------------|
| Language           | Kotlin (Android)       |
| App framework      | N/A (no server; Android app with WorkManager)      |
| Database           | SQLite 3 via Room, encrypted with SQLCipher               |
| Other infra        | Google Drive appData (encrypted sync) and Gmail read-only API; no other network services            |
| Frontend           | Jetpack Compose         |
| Packaging / infra  | Gradle; sideloaded APK built locally       |
| Testing            | JUnit 5 + Kotest (`./gradlew check`)         |

Do not introduce a new framework, database, or heavy dependency without an architect plan
that justifies it against these defaults.

## 3. Architecture rules (cross-cutting)

> **Universal vs. project-specific.** The *principles* in this section are universal and hold
> for any codebase: clear module boundaries with no cross-layer reaching, one canonical schema
> at boundaries, shared code in packages (never copied), config-by-variability, observability,
> and simplicity over abstraction. The *concrete shapes* — the exact layer names, whether it's
> a monolith or services, the transport, the packaging — are a **default (a layered modular
> monolith) that you adapt at setup to this project's real architecture**. Rules marked
> "adapt" below were reconciled with the existing structure during setup; keep the principle,
> match the shape to the repo.

1. **Modular, layered architecture.** Two kinds of module (details in `docs/TECHNICAL.md` §6):
   - **`core/<module>`**: pure Kotlin/JVM with **no Android imports** (money, ledger, parsing, discovery, matching, recurring, budget, sync). Each owns its models, services and tests. Business logic lives here, and `core` defines the repository interfaces.
   - **`app/`**: the thin Android layer: Compose UI, SMS receiver, WorkManager, Gmail/Drive clients, Keystore, and the Room/SQLCipher implementations of the repository interfaces. `app` depends on `core`, never the reverse.
   The call flow is **UI (Compose) → ViewModel → core service → repository (Room dao) → db**, each layer calling only the one below. No UI reaching into a dao, no data access in a core service, no business logic in a dao or a composable.
2. **Abstraction first (at service boundaries).** Every external dependency (data source,
   third-party API, provider, model, broker) sits behind an abstract interface. Swapping a
   provider must require only a new implementation class — never a change to core logic.
3. **One canonical internal schema.** Data crossing a module boundary is normalised to a
   shared, versioned schema before entering the pipeline — not passed around in raw
   source-specific shapes.
4. **Shared code lives in packages, not copies.** Common helpers, utilities, and cross-cutting
   functions are never copy-pasted between modules. They live in versioned, reusable packages
   under `core/shared/`. The moment a helper is needed by a second module, promote it to a
   shared package rather than duplicating it.
5. **Configuration-driven — env or DB by variability.** Thresholds, params, and limits are
   never hardcoded. Choose the store by how the value varies: **static, environment-specific,
   or secret → env config** (typed settings); **runtime-varying, per-user, or operationally
   tuned → the database** (with an audit trail on change). Pick deliberately and document it
   in the module README.
6. **Service-to-service transport.** N/A: there is no server and there are no internal services.
7. **Observability from day one, without leaking.** Components emit structured local logs and a local-only diagnostics screen. Logs **never** contain amounts, payees, message text, account identifiers or tokens. Every significant change is still auditable from the DB audit trail.
8. **Domain safety & correctness invariants.**
   Money is stored as integer paise, never floating point. Every create/edit/delete is written to an audit trail. SMS and email are read-only inputs and are never modified or deleted. Message content and financial data never appear in logs and never leave the device unencrypted. Messages are untrusted input. Parsing is deterministic and on-device. Auto-logged transactions are never silently dropped, merged or altered; uncertain cases surface for review. These invariants are **hardcoded and cannot be disabled by config or
   by model output**; every action that touches them is written to an immutable audit trail.
9. **Simplicity over abstraction — do not over-engineer.** The default is simple, clean,
   readable, concrete code. Modular and extensible, but never abstraction hell.

### The abstraction bar (read this before adding an interface)

Abstraction earns its place at **service boundaries we genuinely swap** — external providers,
data sources, models, brokers. That is where interfaces are mandatory.

Everywhere else, prefer the concrete thing:

- **Do not** add an interface/abstract base/factory/wrapper for a class, function, or module
  that has exactly one implementation and no imminent second one on the roadmap.
- **YAGNI.** Build for the requirement in front of you, not a hypothetical future. Introduce
  indirection when the second implementation actually arrives, not before.
- **No premature optimisation.** Write the clear version first; optimise only what a
  measurement shows is too slow.
- Plain functions and concrete classes beat layers of indirection. A reader should follow a
  call path without chasing through five abstractions.

Both the architect (in plans) and the developer (in code) apply this bar. Any added
abstraction must justify itself against this rule.

## 4. The three non-negotiable process rules

### Rule A — TDD is mandatory
No production code is written before a failing test that specifies it. Follow
**Red → Green → Refactor**:

1. **Red** — write the smallest test that expresses the next behaviour; run it; watch it fail
   for the right reason.
2. **Green** — write the minimum code to make it pass.
3. **Refactor** — clean up with the test staying green.

- **Two test levels, both required:** **unit** (pure logic/transformations, fast, no I/O) and
  **integration** (flows across layers/modules, controller → service → dao → db, adapters
  against real or faked externals). Most non-trivial changes need both.
- Every product acceptance criterion maps to at least one automated test.

**Coverage is a floor, not the goal — ≥ 90% on `core` modules** (thin Android glue is exempt from the floor, not from tests). The push hook fails below it.
But **90% of meaningless tests is a failure, not a pass.** Never write a test
just to move the number.

**What "meaningful" means:**
- **Assert observable behaviour/outcomes** through public interfaces — the returned value, the
  persisted row, the emitted event, the HTTP response — not that a line ran or a mock was
  called.
- **Integration tests exercise real collaborators** — prefer a real ephemeral datastore (e.g.
  testcontainers) over mocking the dao; test the actual path. Mock only true external
  boundaries, and assert on the resulting behaviour, not on the mock.
- **Cover the paths that matter:** happy path, **failure and edge cases** (bad input, outage,
  boundary values, empty/oversized payloads), and every acceptance criterion.
- **Test behaviour, not implementation** — tests should survive a behaviour-preserving refactor.

**Forbidden (the reviewer rejects these):** assertion-free "smoke" tests counted as coverage;
`assert True` / `assert x is not None` as the only assertion; asserting a mock was called
instead of the real effect; over-mocking that verifies nothing real; tests that merely restate
the implementation.

### Rule B — Documentation is mandatory
Every change that adds or alters behaviour updates docs **in the same unit of work**:

| Surface | Where | When |
|---|---|---|
| **Inline** | Docstrings & comments in the code | Always — every public module, class, function |
| **Module README** | `README.md` in each module dir | Every module — created with it, updated on change |
| **Feature (product) doc** | `docs/features/<feature>.md` | Every feature — what it does for the user + behaviour |
| **Architecture docs** | `docs/architecture/` | New component, boundary, or design decision |
| **Code-structure** | `docs/architecture/code-structure.md` | New/moved module or package |
| **Learnings** | `docs/learnings/README.md` | End of every session/task |
| **Project README** | root `README.md` | Setup, run, test, module map — kept current |

A module or feature without its docs is **incomplete**. Docs are reviewed like code: the
`code-reviewer` treats a missing/stale README, product doc, or docstring as a blocker. Each
module README states, in order: Purpose · Responsibilities & boundaries · Public interface ·
Data it owns · Configuration · Dependencies · How to run & test.

### Rule C — Security is built in
Every change is threat-aware and introduces no security issues. When in doubt, **fail closed.**

- **Secrets & credentials.** Never hardcode, commit, or log secrets/keys/tokens — load from
  env / secret store via typed settings. Least-privilege credentials; keep any
  sensitive/production credentials separate.
- **Protected paths.** Never read, print, copy, or write secret/credential material — `.env*`,
  `secrets/`, key/certificate files, and `.git/` internals. Defence is layered: `.gitignore`,
  the pre-commit secret scan, and the `.claude/settings.json` deny-list. Never bypass via Bash
  (`cat`/`echo`/redirection) or `git add -f`. Commit only `*.example` templates.
- **Untrusted input (all external data is untrusted).** Validate and type every external input
  at the boundary — request params, ingested payloads, and **model/LLM outputs** — before use.
  Never `eval`/exec or shell-interpolate untrusted data. Treat any content flowing into an LLM
  as a **prompt-injection** vector; model output must never directly trigger a
  privileged/irreversible action without passing the hardcoded safety checks.
- **Data store.** Parameterised queries / ORM only — never build queries by string
  concatenation. Least-privilege DB roles. Every schema change via migration.
- **Android surface & multi-user.** No exported component, permission or OAuth scope without justification (allowed scopes: `gmail.readonly`, `drive.appdata`); `allowBackup` off; app lock and `FLAG_SECURE` on sensitive screens; SMS/email content is untrusted input; never leak internals, secrets or PII in logs or errors; every record carries per-user attribution.
- **Dependencies.** Pin versions; vet before adding; keep the footprint minimal. Run a
  vulnerability scan (e.g. `Gradle (dependency locking/verification) + OWASP dependency-check` audit) as part of the suite — a known-vuln
  dependency is a blocker.

Apply security proportionately (the abstraction bar still holds) — but never skip the boundary
checks above.

## 5. Documentation taxonomy

```
README.md                  # PROJECT readme: setup, run, test, module map
docs/
  PRD.md                   # product requirements (source of truth for what to build)
  TECHNICAL.md             # engineering rules (graphify, secrets, security review, ...)
  research/                # one-off research notes (e.g. bank SMS formats)
  guides/                  # how-tos (local setup, collecting sample messages)
  architecture/            # system design notes, boundaries, code-structure.md
  features/                # one PRODUCT doc per feature: what/why, behaviour, config
  plans/                   # architect output: one dated plan per task (_TEMPLATE.md)
  learnings/               # running ledger of decisions & gotchas for future sessions
app/, core/<module>/       # code; each module has its own README.md next to it
```

## 6. Workflow for any new development

1. **Plan** — invoke the architect (`/architect` or the `architect` sub-agent). It produces a
   documented plan in `docs/plans/` following `docs/plans/_TEMPLATE.md`, including a
   TDD-ordered step list, the documentation updates required, and an **incremental PR
   breakdown** — how the work splits into a sequence of small, independently reviewable PRs
   (per *Commit & PR sizing* below). The architect designs the work to ship in small
   increments **from the outset**; sizing is a planning-time concern, not bolted on after.
2. **Implement** — follow the plan, TDD each step (Red → Green → Refactor). Use `/dev`.
3. **Document** — update every documentation surface as you go.
4. **Review** — the `code-reviewer` fires automatically after the developer (end of the `/dev`
   flow) and produces a compact report. Address its blockers before merge. Run it manually
   anytime with `/review-changes`.
5. **Capture learnings** — append to `docs/learnings/README.md` before finishing.
6. **Open a PR** using `.github/pull_request_template.md`; complete every checklist item.
   **Agents open PRs but never merge them.** Every PR needs **≥1 approving human review**;
   a human (the repo owner) reviews, approves, and merges. Agents are blocked from
   `gh pr merge` via `.claude/settings.json`; branch protection should require the approval
   server-side too.

### Commit & PR sizing (hard rule)

Small, incremental units are **non-negotiable** — they make review careful instead of
overwhelmed, let a reviewer build understanding gradually, and keep history bisectable and
revertable. This binds both the developer (commits) and whoever opens the PR.

- **Commits are small and atomic.** Each commit is **one logical, self-contained change** — a
  single concern (one behaviour, one fix, one refactor, one rename), of a size a reviewer can
  hold in their head. It leaves the working tree green (compiles, suite passes) and is
  **independently revertable**. Never bundle unrelated changes into one commit — split them.
  Imperative subject line; the body says **why**. Prefer a sequence of small commits over one
  sprawling one.
- **PRs are small, single-purpose, and incremental.** A PR does **one thing** and stays small
  enough to review in a single sitting — never hand a reviewer an overwhelming diff. Decompose
  large work into a **sequence of small, incremental PRs**, each landing a coherent slice and
  building on the last, so reviewers accumulate context gradually. If a branch is outgrowing a
  single reviewable PR, **stop and split it**. Unrelated changes (a config tweak, a drive-by
  cleanup) get their own PR rather than riding a feature branch.
- **Branching for larger features.** A small change is one short branch off the default branch
  → one PR to it. A **larger feature** uses a **base feature branch** off the default branch as
  its integration point: cut **short development branches off that base branch**, and open each
  slice's small PR **into the base feature branch** (not the default branch) — that is where the
  gradual, slice-by-slice review happens. When the feature is complete and green on the base
  branch, raise **one PR from the base feature branch → the default branch**. Topology:
  `main` ← `feat/<feature>` (base) ← `feat/<feature>/<slice>` (short dev branches). Each PR
  still needs its ≥1 approving human review; agents never merge.
- **Rule of thumb (not a hard line count):** if you cannot summarise a commit in one line or a
  PR in a few, it is doing too much — split it. Keep each unit independently reviewable and,
  ideally, independently revertable.

## 7. Definition of Done

A unit of work is done only when **all** of these hold:

- [ ] A plan exists in `docs/plans/` (for anything beyond a trivial fix).
- [ ] Tests were written first and now pass; acceptance criteria are covered.
- [ ] The full test suite is green; linters/type-checks pass.
- [ ] Public code has docstrings; non-obvious logic has comments.
- [ ] Every touched module has an up-to-date `README.md`; each feature has its
      `docs/features/` product doc; architecture / code-structure docs updated.
- [ ] Learnings appended to `docs/learnings/README.md`.
- [ ] No security issue introduced (Rule C): secrets safe, inputs validated, no injection,
      parameterised queries, no vulnerable deps.
- [ ] Domain safety invariants (§3.8) intact and not bypassable.
- [ ] Security review (`docs/TECHNICAL.md` §5) run and recorded in the PR; graphify graph refreshed if structure changed.
- [ ] New external dependency (if any) sits behind an abstract interface — and no gratuitous
      abstraction was added elsewhere (the abstraction bar holds).
- [ ] Commits are small and atomic, and the PR is small, single-purpose, and incrementally
      reviewable (§6 → *Commit & PR sizing*) — oversized work was split, not shipped whole.
- [ ] The `code-reviewer` report was produced and its blockers/required actions resolved.
- [ ] The PR follows `.github/pull_request_template.md` with every checklist item complete.

## 8. Model policy for role agents

Owner decision (2026-10-04). Builds run locally on the owner's Claude Pro plan, so cost discipline matters. Models and effort are pinned in each agent's frontmatter (the `effort` field overrides session effort per the [sub-agents docs](https://code.claude.com/docs/en/sub-agents)).

- **architect**: `model: opus`, `effort: high`.
- **senior-full-stack-dev**: `model: sonnet`, `effort: high`. Also does all parser/template work.
- **code-reviewer**: `model: sonnet`, `effort: high`. *(The owner did not specify the reviewer; set to match the developer. To confirm.)*
- **Escape hatch**: for a genuinely hard step (a novel pattern, crypto, tricky concurrency, a subtle algorithm) the spawner may bump a run to Opus. Say so when doing it.
- A user's explicit model choice on any invocation **always wins**.

Rationale in one line: **Opus plans, Sonnet builds, Sonnet reviews**, with strict TDD as the guard against rework.

## 9. Conventions

- Absolute imports within a module; modules communicate through defined interfaces, not by
  reaching into each other's internals.
- Config via environment + typed settings; secrets never committed.
- Migrations for every schema change (no manual DB edits).
- Small, atomic, single-purpose commits and small, incremental PRs — see the hard rule in
  §6 → *Commit & PR sizing*; the working tree stays green.
- **Local quality gates via pre-commit** (`.pre-commit-config.yaml`): secret scanning +
  `ktlint + detekt (via Gradle)` on **commit**; the full `JUnit 5 + Kotest (`./gradlew check`)` suite on **push**. Install
  once with `pre-commit install && pre-commit install --hook-type pre-push`. Do not skip with
  `--no-verify`. These gates complement, but do not replace, TDD and the `code-reviewer`.
