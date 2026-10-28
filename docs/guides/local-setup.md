# Local setup (your machine)

All builds, tests and Claude sessions run locally. This is slice **S0**: tooling only, no application code yet. Do these once per machine.

## 1. Tools

| Tool | Why | Notes |
|---|---|---|
| Git | version control | |
| JDK | Gradle and Android builds | Eclipse Temurin 17, latest 17.0.x patch. AGP 8.7 needs JDK 17. See §2. |
| Android SDK | compile, install | Command-line tools plus exactly three packages: `platform-tools`, `platforms;android-35`, `build-tools;34.0.0`. Lives in `~/Library/Android/sdk`. See §2. |
| Android Studio | optional: Compose previews, profiler, emulator | Current stable; not pinned (it is a tool, not part of the build). Point it at the same SDK and JDK as the terminal. See §2. |
| `adb` | install the APK, optional SMS export | From the SDK's `platform-tools` |
| Python 3.9+ | `tools/redact` | Standard library only |
| `pre-commit` and `gitleaks` | local gates and secret scan | `brew install pre-commit gitleaks` (or `pip install pre-commit` and gitleaks from its release page) |
| `uv` | installs graphify in isolation | |
| Claude Code | the agents | Pro plan login |

Pinned versions (AGP, Gradle, Kotlin, Compose, Room, SQLCipher, SDK levels) and the reasons for them are in `docs/TECHNICAL.md` §7.1. The rule is about two years old for support, newer only for security.

**Do not install** the NDK, CMake, extra platforms or build-tools, `sources`, system images, Google Play images, or the emulator (unless you have no physical phone), and no second JDK manager such as SDKMAN.

## 2. Android toolchain (macOS)

1. **JDK.** `brew install --cask temurin@17`, then check with `/usr/libexec/java_home -v 17`.
2. **Environment.** Add to `~/.zshrc`, then `source ~/.zshrc`:
   ```bash
   export JAVA_HOME="$(/usr/libexec/java_home -v 17)"
   export ANDROID_HOME="$HOME/Library/Android/sdk"
   export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"
   ```
   `ANDROID_SDK_ROOT` is deprecated; `ANDROID_HOME` is enough.
3. **Get `sdkmanager`** (one of):
   - **Android Studio:** Settings → Languages & Frameworks → Android SDK. Set the location to `~/Library/Android/sdk`, then on the SDK Tools tab tick only *Android SDK Command-line Tools (latest)* and *Android SDK Platform-Tools*. Untick Emulator, NDK and CMake.
   - **Official zip:** download *Command line tools only* for Mac from <https://developer.android.com/studio>, check `shasum -a 256` against the value on that page, then:
     ```bash
     mkdir -p ~/Library/Android/sdk/cmdline-tools
     unzip -q ~/Downloads/commandlinetools-mac-*_latest.zip -d ~/Library/Android/sdk/cmdline-tools
     mv ~/Library/Android/sdk/cmdline-tools/cmdline-tools ~/Library/Android/sdk/cmdline-tools/latest
     ```
   Avoid the Homebrew `android-commandlinetools` cask: it installs outside the SDK folder and every command then needs `--sdk_root`.
4. **SDK packages and licences.**
   ```bash
   sdkmanager "platform-tools" "platforms;android-35" "build-tools;34.0.0"
   yes | sdkmanager --licenses
   sdkmanager --list_installed     # expect only these plus cmdline-tools
   ```
   If Studio added others (newer build-tools, `sources;android-35`), remove them with `sdkmanager --uninstall "<package>"`.
5. **Stop Gradle downloading JDKs.** Add to `~/.gradle/gradle.properties`:
   ```properties
   org.gradle.java.installations.auto-download=false
   ```
6. **Android Studio** (if you use it):
   - *SDK:* Settings → Languages & Frameworks → Android SDK → location `~/Library/Android/sdk`.
   - *Gradle:* Settings → Build, Execution, Deployment → Build Tools → Gradle. Leave **Distribution** on **Wrapper** (putting a JDK path there fails with "Gradle location is incorrect"). Set **Gradle JVM criteria → Version** to **17** and **Vendor** to Eclipse Adoptium. This writes `gradle/gradle-daemon-jvm.properties`, which is committed so every machine and the terminal use the same JDK. For a default before the project exists, use File → New Projects Setup → Settings for New Projects.
   - Ignore prompts to upgrade AGP; upgrades are deliberate (TECHNICAL.md §7.1).
7. **Verify.** `java -version` shows 17.0.x Temurin, `sdkmanager --version` and `adb version` run. Once the Gradle project exists, `./gradlew --version` shows Gradle 8.10.2 on JVM 17, matching the JDK in Studio's Build window.

## 3. Clone and install the git hooks

```bash
git clone https://github.com/imagh/personal-finance-tracker && cd personal-finance-tracker
pre-commit install && pre-commit install --hook-type pre-push
python3 -m unittest discover -s tools/redact      # sanity check, should pass
```

Never commit with `--no-verify`. The Gradle hooks skip themselves until the Gradle project exists.

## 4. Secrets: environment only (docs/TECHNICAL.md §3)

Nothing secret goes in the repo. Put values in your shell profile or `~/.gradle/gradle.properties`:

| Variable (proposed names) | Used for |
|---|---|
| `ANDROID_KEYSTORE_PATH`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD` | Release signing (keystore lives outside the repo, e.g. `~/.android/`) |
| `GOOGLE_OAUTH_CLIENT_ID` | Google sign-in for Gmail read-only and Drive appData |

Debug builds need none of these. The `.gitignore` and the agents' deny-list already block `.env*`, `local.properties`, keystores and your private exports.

## 5. graphify (token saver, docs/TECHNICAL.md §2)

Do this once the first code exists; an empty repo has nothing to graph. Commands are from upstream's README as of 2026-10-04, so check them again.

1. **Vet first.** The PyPI name is `graphifyy` (temporary). Confirm the PyPI page links to the upstream GitHub repo, pick a version, and **pin it**.
2. `uv tool install graphifyy==<pinned version>`
3. In the repo: `graphify install --project`, then `graphify claude install`. This edits `CLAUDE.md` and adds a hook. **Merge, don't overwrite**, and read the hook before accepting it.
4. Build the graph in code-only mode (no LLM tokens), then `graphify hook install` to keep it fresh.
5. Commit `graphify-out/graph.json` and `GRAPH_REPORT.md`; keep `graph.html` local.

## 6. Using the agents

| Command | Does | Model |
|---|---|---|
| `/architect <task>` | writes a TDD-ordered plan to `docs/plans/` | Opus, high |
| `/dev <plan>` | implements under TDD, then auto-reviews | Sonnet, high |
| `/review-changes` | reviews the current diff | Sonnet, high |

Before every PR also run `/security-review` (docs/TECHNICAL.md §5). Agents open PRs; **you** merge.

## 7. Sample messages (when you are ready)

See [collecting-sample-messages.md](collecting-sample-messages.md). Your bank list and redacted samples are still open for planning and are needed before M1b.
