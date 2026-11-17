---
description: Implement an approved plan (senior-full-stack-dev, strict TDD), then auto-review
---

Implement the following plan/task, then review it — you are the orchestrator, delegating to
the two subagents (do not implement or review yourself):

1. Delegate to the **`senior-full-stack-dev`** subagent to implement it step by step under
   strict TDD (Red → Green → Refactor), keeping the whole suite green and updating every
   required documentation surface.
2. When it finishes, **automatically** delegate to the **`code-reviewer`** subagent to review
   the resulting diff, and surface its compact report to the user.

Per CLAUDE.md §8, the developer and reviewer run on the model and effort of the work's tier (T1 Sonnet
`medium`, T2 Sonnet `high`, T3 Opus `high`; the plan's PR table names the tier); a user's explicit model
choice wins, and you escalate to Opus if a step is missed twice — say so if you do. Do not
commit, push, or merge unless the user asks; keep commits small and single-purpose (§6). To run a whole
PR with your own review in place of the reviewer agent, use `/orchestrate-pr` instead.

$ARGUMENTS
