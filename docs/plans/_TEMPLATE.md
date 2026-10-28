# Plan: <title>

| Field       | Value |
|-------------|-------|
| Date        | YYYY-MM-DD |
| Author      | architect |
| Status      | Draft \| Approved |
| Related ACs | <acceptance criteria this satisfies> |
| Plan owner  | <human> |

<!-- Foundation this builds on (link merged plans / modules it depends on). -->

---

## 1. Requirement

*What must be true when this is done. The problem/need, the intended outcome, and a crisp
"Done means:" statement. Explicitly list what is OUT of scope.*

## 2. Current state

*What already exists that this plugs into — cite real files (`file:line`). Existing helpers,
interfaces, and patterns to reuse. Where the change lands.*

## 3. Design & decision

*The recommended approach (only the chosen one, not a survey). The key decisions and their
rationale. Every added abstraction justified against the CLAUDE.md abstraction bar — or state
"no new abstraction".*

## 4. Data model & schema

*New/changed models, tables, migrations, or the canonical schema fields touched. "No schema
change" is a valid answer — say so.*

## 5. Flows

*The user & data flow(s) through the layers (controller/handler → service → dao → db) and
across modules. A short diagram or numbered walkthrough.*

## 6. Implementation steps (TDD-ordered)

*Each step is small and verifiable. For every step: the failing test to write first (Red), the
minimal code to pass (Green), then the Refactor. Name real files.*

### Step 1 — <name>
- **Red:** `tests/...::test_...` — asserts <behaviour>.
- **Green:** implement in `<module>/...` — <what>.
- **Refactor:** <cleanup, or "none">.

### Step 2 — <name>
- **Red:** ...
- **Green:** ...
- **Refactor:** ...

### PR breakdown (incremental — required)

*How this work ships as a sequence of small, single-purpose, independently reviewable PRs
(CLAUDE.md §6 → Commit & PR sizing). Group the steps above into the smallest coherent slices a
reviewer can absorb one at a time; each PR builds on the last. A one-PR plan is only acceptable
when the whole change is genuinely small. Unrelated changes get their own PR.*

**Target branch.** If this is genuinely small, it's one branch → one PR to the default branch.
If it's a **larger feature**, name the base/integration branch (`feat/<feature>`) here; the
slice PRs below target that **base branch** (not the default branch), and a final single PR
merges the base branch → the default branch once the feature is complete.

| PR | Scope (which steps) | Target | Depends on | Why it's independently reviewable |
|----|---------------------|--------|------------|-----------------------------------|
| 1  | <steps / slice>     | `feat/<feature>` (or default branch if small) | — | <a coherent, self-contained slice> |
| 2  | <steps / slice>     | `feat/<feature>` | PR 1 | <builds on PR 1; own concern> |
| final | integrate the feature | default branch | PRs 1–N | one coherent feature PR (larger features only) |

## 7. Test plan

*The unit and integration tests, by level. What each asserts (observable outcomes). The
failure/edge cases covered. The acceptance-criterion → test mapping. Real collaborators for
integration; mocks only at true external boundaries.*

## 8. Security considerations

*Untrusted inputs and where they're validated; secrets/protected paths; parameterised queries;
new dependencies (and the vuln scan); anything that must fail closed.*

## 9. Documentation updates (all required surfaces)

*Enumerate every doc this change must add/update: inline docstrings, module README(s),
`docs/features/<feature>.md`, `docs/architecture/*`, `docs/architecture/code-structure.md`,
`docs/learnings/README.md`, and the root README if setup/run/test/module-map changed.*

## 10. Risks & mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
|      |        |            |

## 11. Rollout & verification

*How to prove it works end to end (run it / exercise the flow / run the suite). Any migration
or config steps. How to roll back.*

## 12. Open questions

*Decisions the user/architect must confirm before implementation. Write "None — no blocking
open questions" when settled.*
