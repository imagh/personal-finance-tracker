"""A Claude Code PreToolUse hook that keeps agents within the usage budget (CLAUDE.md §8).

It refuses two kinds of tool call, with a message that says what to do instead:

- **A whole Read of a big text file** (more than MAX_WHOLE_READ_BYTES with no offset or limit). Files such
  as the learnings log, the design plan or `app/README.md` cost tens of thousands of tokens, and every
  later call re-sends them. Read the lines you need (find them with `grep -n` or `graphify query`), or
  read `docs/learnings/DIGEST.md` instead of the learnings log. Images and PDFs are exempt.
- **A foreground Gradle run that runs tests** (`check`, `test`, `test*UnitTest`, Roborazzi record, verify
  or compare). A Robolectric run can hang on `AppNotIdleException` for hours; run it in the background
  and poll its log, or use `tools/agent/check-retry.sh`.

Wiring: `.claude/settings.json` runs `python3 -I tools/agent/guard.py` for Read and Bash. The hook reads
the tool call as JSON on stdin; exit code 2 blocks the call and shows stderr to the agent, 0 allows it.
This is a usage guard, not a security control, so unreadable input **fails open** (allowed): a bug here
must never block every tool call. Python 3.9 compatible (the system python on the owner's Mac).
"""
import json
import re
import sys
from pathlib import Path
from typing import Optional

MAX_WHOLE_READ_BYTES = 48_000
"""Bigger than this, a text file is read in parts. The largest docs an agent needs whole (CLAUDE.md,
TECHNICAL.md, code-structure.md) are 17-26 KB; the ones this stops are 74-160 KB."""

EXEMPT_SUFFIXES = {".png", ".jpg", ".jpeg", ".gif", ".webp", ".pdf"}

TEST_TASK = re.compile(
    r"^(?::?[\w-]+:)*(check|test|test\w*UnitTest|(?:record|verify|compare)Roborazzi\w*|connected\w*AndroidTest)$"
)
"""A Gradle task path that runs tests: `check`, `:app:check`, `test`, `:core:ledger:test`,
`testDebugUnitTest`, `recordRoborazziDebug`. Not `cleanTestDebugUnitTest` or `ktlintCheck`."""

GRADLE_CALL = re.compile(r"(?:^|[\s;&|(])(?:\./)?gradlew\b((?:[<>]&|&>|[^;&|\n])*)(&&|\|\||&|;|\||\n|$)")
"""One `gradlew` invocation: its arguments (redirections such as `2>&1` included), and the operator that
ends it (a lone `&` backgrounds it)."""

READ_MESSAGE = (
    "{name} is {kb} KB, too big to read whole (the usage budget, CLAUDE.md §8). Find the lines you need "
    "with `grep -n` or `graphify query`, then Read with offset/limit. For the learnings log, read "
    "docs/learnings/DIGEST.md instead."
)

BASH_MESSAGE = (
    "Run Gradle tests and check in the background, never in the foreground: a Robolectric run can hang on "
    "AppNotIdleException. Use run_in_background, or `./gradlew ... > /tmp/pft-briefs/<name>.log 2>&1 &` and "
    "poll the log every 20-30 s. For a full check, run `bash tools/agent/check-retry.sh <worktree> <tag>` "
    "in the background."
)


def decide(payload) -> Optional[str]:
    """Why this tool call is refused, or None to allow it. Never raises on odd input."""
    if not isinstance(payload, dict) or not isinstance(payload.get("tool_input"), dict):
        return None
    tool_input = payload["tool_input"]
    if payload.get("tool_name") == "Read":
        return _whole_read_of_big_file(tool_input)
    if payload.get("tool_name") == "Bash":
        return _foreground_gradle_tests(tool_input)
    return None


def _whole_read_of_big_file(tool_input: dict) -> Optional[str]:
    path = tool_input.get("file_path")
    if not isinstance(path, str) or "offset" in tool_input or "limit" in tool_input:
        return None
    file = Path(path)
    if file.suffix.lower() in EXEMPT_SUFFIXES or not file.is_file():
        return None
    size = file.stat().st_size
    if size <= MAX_WHOLE_READ_BYTES:
        return None
    return READ_MESSAGE.format(name=file.name, kb=size // 1000)


def _foreground_gradle_tests(tool_input: dict) -> Optional[str]:
    command = tool_input.get("command")
    if not isinstance(command, str) or tool_input.get("run_in_background") is True:
        return None
    for match in GRADLE_CALL.finditer(command):
        backgrounded = match.group(2) == "&"
        if not backgrounded and _runs_tests(match.group(1).split()):
            return BASH_MESSAGE
    return None


def _runs_tests(arguments: list) -> bool:
    skip_next = False
    for argument in arguments:
        if skip_next:
            skip_next = False
        elif argument in ("--tests", "-x", "--exclude-task"):
            skip_next = True
        elif not argument.startswith("-") and not argument.startswith(">") and TEST_TASK.match(argument):
            return True
    return False


def main(stdin=sys.stdin, stderr=sys.stderr) -> int:
    """Hook entry point: 2 (with the reason on stderr) blocks the call, 0 allows it."""
    try:
        payload = json.load(stdin)
    except ValueError:
        return 0
    reason = decide(payload)
    if reason is None:
        return 0
    stderr.write(reason + "\n")
    return 2


if __name__ == "__main__":
    sys.exit(main())
