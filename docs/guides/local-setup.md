# Local setup (your machine)

All builds, tests and Claude sessions run locally. This is slice **S0**: tooling only, no application code yet. Do these once per machine.

## 1. Tools

| Tool | Why | Notes |
|---|---|---|
| Git | version control | |
| JDK | Gradle and Android builds | Use the version the current Android Gradle plugin requires (JDK 17 or newer) |
| Android Studio | SDK, emulator, Gradle | Installs the Android SDK; this is where `dl.google.com` access matters, on your own network |
| `adb` | install the APK, optional SMS export | Comes with Android Studio's platform-tools |
| Python 3.9+ | `tools/redact` | Standard library only |
| `pre-commit` and `gitleaks` | local gates and secret scan | `pip install pre-commit` (or `pipx`); gitleaks from its release page or your package manager |
| `uv` | installs graphify in isolation | |
| Claude Code | the agents | Pro plan login |

## 2. Clone and install the git hooks

```bash
git clone https://github.com/imagh/personal-finance-tracker && cd personal-finance-tracker
pre-commit install && pre-commit install --hook-type pre-push
python3 -m unittest discover -s tools/redact      # sanity check, should pass
```

Never commit with `--no-verify`. The Gradle hooks skip themselves until the Gradle project exists.

## 3. Secrets: environment only (docs/TECHNICAL.md §3)

Nothing secret goes in the repo. Put values in your shell profile or `~/.gradle/gradle.properties`:

| Variable (proposed names) | Used for |
|---|---|
| `ANDROID_KEYSTORE_PATH`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD` | Release signing (keystore lives outside the repo, e.g. `~/.android/`) |
| `GOOGLE_OAUTH_CLIENT_ID` | Google sign-in for Gmail read-only and Drive appData |

Debug builds need none of these. The `.gitignore` and the agents' deny-list already block `.env*`, `local.properties`, keystores and your private exports.

## 4. graphify (token saver, docs/TECHNICAL.md §2)

Do this once the first code exists; an empty repo has nothing to graph. Commands are from upstream's README as of 2026-10-04, so check them again.

1. **Vet first.** The PyPI name is `graphifyy` (temporary). Confirm the PyPI page links to the upstream GitHub repo, pick a version, and **pin it**.
2. `uv tool install graphifyy==<pinned version>`
3. In the repo: `graphify install --project`, then `graphify claude install`. This edits `CLAUDE.md` and adds a hook. **Merge, don't overwrite**, and read the hook before accepting it.
4. Build the graph in code-only mode (no LLM tokens), then `graphify hook install` to keep it fresh.
5. Commit `graphify-out/graph.json` and `GRAPH_REPORT.md`; keep `graph.html` local.

## 5. Using the agents

| Command | Does | Model |
|---|---|---|
| `/architect <task>` | writes a TDD-ordered plan to `docs/plans/` | Opus, high |
| `/dev <plan>` | implements under TDD, then auto-reviews | Sonnet, high |
| `/review-changes` | reviews the current diff | Sonnet, high |

Before every PR also run `/security-review` (docs/TECHNICAL.md §5). Agents open PRs; **you** merge.

## 6. Sample messages (when you are ready)

See [collecting-sample-messages.md](collecting-sample-messages.md). Your bank list and redacted samples are still open for planning and are needed before M1b.
