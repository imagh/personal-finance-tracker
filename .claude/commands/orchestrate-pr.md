---
description: Build the next PR of a plan by delegation, review it yourself, and open the PR (one PR per session)
---

You orchestrate one PR of an approved plan: delegate the build, review it yourself, run `check`, and
open the PR. Then stop: **one PR per session** (CLAUDE.md §8). Arguments: the PR id from the plan's PR
table (e.g. `B5b`) and anything the owner adds. If none is given, take the first unmerged PR in the table.

## 1. Orient (cheaply)
- `git fetch origin`. Find the plan and its PR table (`grep -n` for the table; read only it, the PR's §6
  step, the decisions that step cites, the conventions paragraph and the §12 owner answers, by section).
- State comes from git, not memory: `gh pr list --state all --base <base>` shows what has merged; there must
  be no open PR for this step and no stray worktree (`git worktree list`).
- Read the PR's **tier** and apply CLAUDE.md §8: it decides the developer's model and effort, the review,
  and whether this session's model fits. If the tier needs Opus and this session isn't, say so before you
  start. For **T0**, do the work yourself and skip to step 4.

## 2. Delegate the build
- Create the worktree: `git -C <repo> worktree add -b <base>-<slug> <worktrees>/<slug>/personal-finance-tracker origin/<base>`,
  then `git branch --unset-upstream` in it. Never commit or check out in the root checkout.
- Spawn **one** `senior-full-stack-dev` with the tier's `model` and `effort`. Never run two developers at
  once. Brief it with only:
  - the worktree path;
  - the plan step and decision **section names** to read (not their text, unless short);
  - decisions already made that the plan doesn't record;
  - this PR's traps (from the step, `DIGEST.md` and earlier reviews);
  - what to stop and ask about (re-recording an existing golden, the emulator, a plan gap).

  Its agent file already carries the reading budget, commands, hang rule, evidence rules and self-review
  list, so don't repeat them.
- If it stops on a usage limit: wait for the reset, then check `git status`, `git diff` and the
  `/tmp/pft-briefs/check-*.log` files in its worktree, and resume **the same agent** with the exact state.
  To test whether the limit has lifted, spawn a tiny Sonnet `general-purpose` agent that replies "OK".

## 3. Review it yourself (unless the tier says Opus `code-reviewer`)
- Read the diff and the tests against the plan's Red list. Go through every item under "Findings that
  recur" in `.claude/agents/code-reviewer.md`. **Check the code, not the report.**
- Read the `_compare.png` of any changed existing golden before approving a re-record; view new goldens.
- Send fixes to **the same agent** (`SendMessage`), as numbered blocker, should-fix and nit items, each
  saying exactly what to change. Escalate the step to Opus if the same item is missed twice (§8).

## 4. Verify, then ask
- Clear the test outputs (`cleanTestDebugUnitTest cleanTestReleaseUnitTest :core:<m>:cleanTest`), then run
  `bash tools/agent/check-retry.sh <worktree> <tag> --no-build-cache` in the background, so every test
  really runs. Take the counts from the JUnit XML.
- Report to the owner:
  - what was built;
  - findings and how each was resolved;
  - the counts;
  - the APK delta if measured;
  - the proposed commit split.

  **Ask before committing.**

## 5. On the owner's go-ahead
- Make small, atomic commits in the order:
  1. events and validators;
  2. core;
  3. shared blocks;
  4. the feature;
  5. navigation;
  6. docs;
  7. graph refresh.

  Each commit has an imperative subject and a why-body. Before splitting, grep each commit's files
  (including `*Samples.kt`) for references to later commits.
- Check the middle commits in a temporary detached worktree:
  `bash tools/agent/check-commits.sh <tmp-worktree> <tag> <sha>…`, in the background. Remove the temporary
  worktree afterwards. The last code commit has the same code as HEAD.
- Security review: `/security-review` reads the root checkout, so review
  `git diff origin/<base>...HEAD` yourself against `docs/TECHNICAL.md` §5.
- Run the push in the background (the pre-push hook runs `check`).
- Open the PR into `<base>` with `.github/pull_request_template.md`, with every box ticked or explained. Say
  that you reviewed the diff in place of the `code-reviewer` agent (when you did), and list what was fixed
  and what was accepted. **Never merge.**
- Then tell the owner the PR link and **end the session**. Name the next PR and its tier, and the model to
  start the next session on.

## After the owner merges (start of the next session, or when told)
- Remove the merged PR's worktree and local branch, after confirming nothing is uncommitted or unpushed.
- `git branch -d` compares against the root's `main` and refuses, so confirm with
  `git merge-base --is-ancestor <branch> origin/<base>`, then use `git branch -D`.

$ARGUMENTS
