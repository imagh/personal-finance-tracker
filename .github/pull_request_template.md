<!--
Pull request. Fill every section. PRs that skip the checklist or leave sections blank will be
sent back. Keep it small and single-purpose (see CLAUDE.md §6 → Commit & PR sizing) — if this
diff is doing more than one thing, split it before requesting review.
-->

## Task

- **What this PR does:** <!-- one line -->
- **Plan:** <!-- link to docs/plans/YYYY-MM-DD-<slug>.md -->
- **Acceptance criteria addressed:** <!-- e.g. AC1.3 -->
- **Part of:** <!-- base feature branch if this is a slice PR; else "standalone" -->

## Description of changes

<!-- What changed, at a level a reviewer can follow. Bullet the notable files/modules. -->

## Reason for changes

<!-- Why this exists — the problem/need it solves. Link the plan/spec. -->

## Open questions

<!-- Decisions you need from the reviewer. "None" if there are none. -->

---

## Checklist (all must be checked before requesting review)

### TDD & test quality
- [ ] Tests were written **first** (Red → Green → Refactor) for every new behaviour.
- [ ] Unit tests cover logic/transformations; integration tests cover flows.
- [ ] Tests are **meaningful** — assert real behaviour/outcomes (returned values, persisted
      rows, emitted events, responses), cover happy + failure/edge cases, not assertion-free
      or mock-asserting or coverage-gaming.
- [ ] Integration tests use **real collaborators** (real ephemeral datastore, actual
      controller → service → dao → db); only true external boundaries are mocked.
- [ ] Every acceptance criterion above is covered by a passing test.
- [ ] Full suite green with ≥ the coverage floor; linters/type-checks pass.

### Documentation
- [ ] Inline: docstrings on public modules/classes/functions; comments on non-obvious logic.
- [ ] Module README(s) created/updated for every touched module (all sections).
- [ ] Feature (product) doc created/updated (`docs/features/`).
- [ ] Architecture docs updated where a component/boundary changed (`docs/architecture/`).
- [ ] Code-structure updated for any new/moved module (`docs/architecture/code-structure.md`).
- [ ] Learnings appended (`docs/learnings/README.md`).

### Security
- [ ] No secrets/keys/tokens hardcoded, committed, or logged; loaded from typed settings.
- [ ] External inputs (payloads, params, model output) validated at the boundary; no
      `eval`/exec/shell-interpolation of untrusted data.
- [ ] Parameterised queries / ORM only; least-privilege data access.
- [ ] No secrets/PII leaked in responses, logs, or errors; authz/isolation intact.
- [ ] No new known-vulnerable or unvetted dependency (ran the vuln scan).

### Architecture & quality
- [ ] Module layering respected (controller/handler → service → dao → db; logic in services,
      no data-access in services, no logic in daos).
- [ ] Reused helpers live in a shared package — not duplicated.
- [ ] Config stored by variability: env for static/secret, DB for runtime/per-user.
- [ ] Abstraction added only at real swap boundaries; no interface/factory around a single
      implementation; no premature optimisation; no dead code.

### Sizing & review
- [ ] This PR is **small, single-purpose, and incrementally reviewable** (§6) — oversized or
      mixed-purpose work was split, not shipped whole. Commits are small and atomic.
- [ ] The `code-reviewer` report was produced and its blockers/required actions addressed.
- [ ] A human reviewer will approve and merge (agents never merge).

## Security review (docs/TECHNICAL.md §5)
- [ ] `/security-review` and `/review-changes` run on this branch; every High/Medium finding fixed (Low: fixed or explained below).
- [ ] App-specific checklist in `docs/TECHNICAL.md` §5 passed (no secrets or real data, permissions/scopes unchanged or approved, no sensitive logging, SMS/email read-only).
- Summary of findings and resolution:
