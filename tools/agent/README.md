# tools/agent

## Purpose
Tooling that keeps Claude Code sessions and agents within the owner's Pro-plan usage budget and safe
from the known Robolectric hang (CLAUDE.md §8).

## Responsibilities & boundaries
- `guard.py`: a PreToolUse hook (wired in `.claude/settings.json` for `Read` and `Bash`). It refuses:
  - a whole Read of a text file over 48 KB (read by section instead, or `docs/learnings/DIGEST.md` for the learnings log);
  - a foreground Gradle run that runs tests (`check`, `test`, `test*UnitTest`, Roborazzi record, verify or compare).

  Exit code 2 blocks the call and shows the reason to the agent. It is a usage guard, not a security control, so unreadable input fails open.
- `check-retry.sh <worktree> <tag> [gradle args]`: runs `./gradlew check`. At the first `AppNotIdleException` it kills only its own Gradle client and reruns, up to 3 attempts. Run it in the background and watch its `=== ` lines.
- `check-commits.sh <worktree> <tag> <sha>…`: checks each commit in turn with `check-retry.sh`, in a temporary detached worktree.
- Not part of the Gradle build, and not shipped.

## Public interface
The hook's stdin/exit-code contract (Claude Code hooks), and the two scripts' command lines above.

## Data it owns
None. The scripts write logs to `$PFT_LOG_DIR` (default `/tmp/pft-briefs`).

## Configuration
- `MAX_WHOLE_READ_BYTES` (48,000) and the exempt suffixes (images, PDF) are constants in `guard.py`. They are static, so they live in code, not in the database or env.
- `PFT_LOG_DIR` sets where the scripts write logs.

## Dependencies
Python 3.9+ standard library (the system python on the owner's Mac), and bash.

## How to run & test
`python3 -I -m unittest discover -s tools/agent`. This also runs in the pre-push hook (`tests-agent-guard`).
