# Agent usage budget and model routing

- **Date:** 2026-11-17
- **Status:** approved by the owner in session (2026-11-17)
- **Scope:** process and agent configuration only; no app code.

## 1. Requirement
Slice B kept hitting the Claude Pro plan's usage limits. B4c's developer used about 290k tokens to build and about 320k more for one fix round of 20 tool calls. The owner asked for:
- rules that every session, agent and sub-agent follows without being re-briefed;
- a way to choose the model for orchestration, development and review based on the work.

## 2. Findings (where the usage went)
1. **Reading.** Each agent read about 125k tokens of docs before writing code:
   - the learnings log (156 KB);
   - the design plan (110 KB);
   - `app/README.md` (97 KB);
   - the slice plan (74 KB).

   Every later tool call re-sends that context.
2. **Fix rounds.** Resuming a loaded agent costs about as much as the build. Most review findings repeated earlier PRs' findings.
3. **Mutation evidence on every first-pass test.** B4c produced 57 mutation logs.
4. **One long Opus orchestrating session across PRs.**
5. **The briefing rules lived in `/tmp`.** They reached agents only when pasted into a brief, and were lost on reboot.

## 3. Design
Put the rules where Claude Code loads them by itself, and enforce the costly ones with a hook:

| Rule | Where |
|---|---|
| Tier table, adjustment rules, usage budget | `CLAUDE.md` §8 (loaded by every session) |
| Reading budget, commands, hang rule, evidence scaled to risk, self-review list | `.claude/agents/senior-full-stack-dev.md` |
| Recurring review findings, narrow reading | `.claude/agents/code-reviewer.md` |
| Tier per PR, steps an agent can be handed alone, narrow reading | `.claude/agents/architect.md`, plus a Tier column in `docs/plans/_TEMPLATE.md` |
| The orchestration loop, one PR per session | `.claude/commands/orchestrate-pr.md` |
| Learnings in about 160 lines | `docs/learnings/DIGEST.md` (the log stays the full record) |
| No whole reads over 48 KB; no foreground Gradle tests | `tools/agent/guard.py`, a PreToolUse hook in `.claude/settings.json` |
| Hang-safe check scripts (moved from `/tmp`) | `tools/agent/check-retry.sh`, `check-commits.sh` |

**Model routing.** The architect gives every PR a tier, T0 to T3. The tier picks:
- the developer's model and effort;
- the reviewer;
- the model the owner starts the orchestrating session on.

The tier adjusts when a diff touches risky paths, and escalates to Opus when a Sonnet developer misses the same review item twice. The orchestrating session's model is the owner's choice at session start, so the next PR's tier is announced at the end of the previous session.

**Abstraction check (§3.3).**
- One small script with no framework.
- The hook fails open on bad input, because it is a usage guard and a bug in it must never block every tool call.
- Commands are Markdown, matching the existing `/dev` and `/architect`.

## 4. Steps (TDD-ordered)
1. **Red:** `tools/agent/test_guard.py` against a stub `guard.py`. 4 tests fail.
2. **Green:** the real guard, then a fix for `2>&1` being read as backgrounding.
3. **Mutations:** each allow rule (images, offset/limit, `--tests` values, background, missing file, fail-open, redirections, anchors) fails its own test when removed.
4. **Wiring:** `check-retry.sh` smoke-tested against a fake `gradlew` that hangs once, then the `settings.json` hook and the pre-push `tests-agent-guard`.
5. **Docs:** the agent files, the command, §8, the digest, the template, and the slice B plan's tiers for the remaining PRs.

## 5. Verification
- `python3 -I -m unittest discover -s tools/agent` passes on the system Python 3.9.
- The hook, run with real JSON, blocks `./gradlew check` and a whole read of `graph.json` (exit 2), and allows everything else.
- The next session (B5b) runs on `/orchestrate-pr`. Compare its token use with B4c's.

## 6. PR breakdown
PR 1 goes into `main`, because new sessions start from `main` and load the agent files, command and hook from there. Slice worktrees are cut from `feat/m1a-ledger-setup`, though, and need `tools/agent/` and `DIGEST.md` in their own tree. So PR 2 merges `main` into the base before B5b. That moves B-final's `main` merge earlier; B-final then only needs whatever lands on `main` after this.

| PR | Scope | Target | Tier | Why reviewable alone |
|---|---|---|---|---|
| 1 | all of the above | `main` | T1 (tooling and docs; a small tested script) | one purpose: the agent usage budget and model routing |
| 2 | merge `origin/main` into the base; resolve `docs/learnings/README.md` (both sides appended) and regenerate `graphify-out/` | `feat/m1a-ledger-setup` | T0 | a merge with no new content |
