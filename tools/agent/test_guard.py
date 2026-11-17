"""Tests for guard.py: the PreToolUse hook that blocks whole reads of big files and foreground Gradle test runs."""
import io
import json
import sys
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import guard  # noqa: E402


def read(path, **extra):
    return {"tool_name": "Read", "tool_input": dict({"file_path": str(path)}, **extra)}


def bash(command, background=None):
    tool_input = {"command": command}
    if background is not None:
        tool_input["run_in_background"] = background
    return {"tool_name": "Bash", "tool_input": tool_input}


class ReadRuleTest(unittest.TestCase):
    def setUp(self):
        self.dir = tempfile.TemporaryDirectory()
        self.root = Path(self.dir.name)
        self.big = self.root / "README.md"
        self.big.write_bytes(b"x" * (guard.MAX_WHOLE_READ_BYTES + 1))
        self.small = self.root / "small.md"
        self.small.write_bytes(b"x" * guard.MAX_WHOLE_READ_BYTES)

    def tearDown(self):
        self.dir.cleanup()

    def test_refuses_a_whole_read_of_a_big_file_and_says_what_to_do_instead(self):
        reason = guard.decide(read(self.big))
        self.assertIn("README.md", reason)
        self.assertIn("offset/limit", reason)
        self.assertIn("DIGEST.md", reason)

    def test_allows_a_file_at_the_limit(self):
        self.assertIsNone(guard.decide(read(self.small)))

    def test_allows_a_big_file_read_in_part(self):
        self.assertIsNone(guard.decide(read(self.big, offset=100, limit=50)))
        self.assertIsNone(guard.decide(read(self.big, limit=50)))
        self.assertIsNone(guard.decide(read(self.big, offset=10)))

    def test_allows_big_images_and_pdfs(self):
        for name in ("golden_compare.png", "photo.jpg", "spec.pdf", "shot.webp"):
            path = self.root / name
            path.write_bytes(b"x" * (guard.MAX_WHOLE_READ_BYTES * 3))
            self.assertIsNone(guard.decide(read(path)), name)

    def test_allows_a_path_that_does_not_exist(self):
        self.assertIsNone(guard.decide(read(self.root / "missing.md")))


class BashRuleTest(unittest.TestCase):
    def assert_refused(self, command):
        reason = guard.decide(bash(command))
        self.assertIsNotNone(reason, command)
        self.assertIn("background", reason)
        self.assertIn("check-retry.sh", reason)

    def assert_allowed(self, command, background=None):
        self.assertIsNone(guard.decide(bash(command, background)), command)

    def test_refuses_a_foreground_check_or_test_run(self):
        self.assert_refused("./gradlew check")
        self.assert_refused("./gradlew :app:testDebugUnitTest --tests '*EmojiTabTest*'")
        self.assert_refused('export PATH="/opt/homebrew/bin:$PATH"; cd /w && ./gradlew -q :app:check > log 2>&1')
        self.assert_refused("./gradlew test")
        self.assert_refused("./gradlew :core:ledger:test")
        self.assert_refused("./gradlew :app:recordRoborazziDebug --tests '*Shot*'")
        self.assert_refused("./gradlew ktlintCheck && ./gradlew verifyRoborazziDebug")

    def test_allows_a_backgrounded_run(self):
        self.assert_allowed("./gradlew check > /tmp/x.log 2>&1 &")
        self.assert_allowed("./gradlew :app:testDebugUnitTest > /tmp/x.log 2>&1 & echo started")
        self.assert_allowed("./gradlew check", background=True)

    def test_a_backgrounded_run_does_not_excuse_a_later_foreground_one(self):
        self.assert_refused("./gradlew assembleDebug > a.log 2>&1 & ./gradlew check")

    def test_allows_gradle_tasks_that_run_no_tests(self):
        self.assert_allowed("./gradlew -q cleanTestDebugUnitTest cleanTestReleaseUnitTest :core:ledger:cleanTest")
        self.assert_allowed("./gradlew ktlintFormat detekt")
        self.assert_allowed("./gradlew assembleRelease")
        self.assert_allowed("./gradlew :app:testClasses compileDebugUnitTestKotlin")
        self.assert_allowed("./gradlew :app:dependencies --configuration releaseRuntimeClasspath")

    def test_a_tests_filter_value_is_not_a_task(self):
        self.assert_allowed("./gradlew assembleDebug --tests check")

    def test_allows_commands_that_only_mention_gradle(self):
        self.assert_allowed("grep -n 'gradlew check' .pre-commit-config.yaml")
        self.assert_allowed("git log --oneline -3")


class OtherInputTest(unittest.TestCase):
    def test_other_tools_and_malformed_input_are_allowed(self):
        self.assertIsNone(guard.decide({"tool_name": "Edit", "tool_input": {"file_path": "x"}}))
        self.assertIsNone(guard.decide({}))
        self.assertIsNone(guard.decide({"tool_name": "Bash", "tool_input": "not a dict"}))
        self.assertIsNone(guard.decide({"tool_name": "Read", "tool_input": {"file_path": 7}}))


class MainTest(unittest.TestCase):
    def run_main(self, stdin_text):
        err = io.StringIO()
        code = guard.main(io.StringIO(stdin_text), err)
        return code, err.getvalue()

    def test_blocks_with_exit_code_2_and_the_reason_on_stderr(self):
        code, err = self.run_main(json.dumps(bash("./gradlew check")))
        self.assertEqual(2, code)
        self.assertIn("check-retry.sh", err)

    def test_allows_with_exit_code_0_and_no_output(self):
        self.assertEqual((0, ""), self.run_main(json.dumps(bash("ls"))))

    def test_unreadable_input_fails_open(self):
        self.assertEqual((0, ""), self.run_main("not json"))


if __name__ == "__main__":
    unittest.main()
