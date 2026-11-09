# Local setup (your machine)

All builds, tests and Claude sessions run locally. Do these once per machine. The Gradle project exists since slice **A1**; §9 shows the day-to-day commands.

## 1. Tools

| Tool | Why | Notes |
|---|---|---|
| Git | version control | |
| JDK | Gradle and Android builds | Eclipse Temurin 17, latest 17.0.x patch. AGP 8.7 needs JDK 17. See §2. |
| Android SDK | compile, install | Command-line tools plus exactly three packages: `platform-tools`, `platforms;android-35`, `build-tools;34.0.0`, and the emulator with one system image (§3). Lives in `~/Library/Android/sdk`. See §2. |
| Android Studio | optional: Compose previews, profiler, emulator | Current stable; not pinned (it is a tool, not part of the build). Point it at the same SDK and JDK as the terminal. See §2. |
| `adb` | drive the emulator, fake SMS, optional SMS export | From the SDK's `platform-tools`. Never enabled on the primary phone (see §3). |
| Python 3.9+ | `tools/redact` | Standard library only |
| `pre-commit` and `gitleaks` | local gates and secret scan | `brew install pre-commit gitleaks` (or `pip install pre-commit` and gitleaks from its release page) |
| `uv` | installs graphify in isolation | |
| Claude Code | the agents | Pro plan login |

Pinned versions (AGP, Gradle, Kotlin, Compose, Room, SQLCipher, SDK levels) and the reasons for them are in `docs/TECHNICAL.md` §7.1. The rule is about two years old for support, newer only for security.

**Do not install** the NDK, CMake, extra platforms or build-tools, `sources`, Google Play images, or any system image beyond the one in §3, and no second JDK manager such as SDKMAN.

## 2. Android toolchain (macOS)

1. **JDK.** `brew install --cask temurin@17`, then check with `/usr/libexec/java_home -v 17`.
2. **Environment.** Add to `~/.zshrc`, then `source ~/.zshrc`:
   ```bash
   export JAVA_HOME="$(/usr/libexec/java_home -v 17)"
   export ANDROID_HOME="$HOME/Library/Android/sdk"
   export PATH="$ANDROID_HOME/emulator:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"
   setopt interactivecomments   # zsh: lets you paste commands that end in "# comment"
   ```
   `ANDROID_SDK_ROOT` is deprecated; `ANDROID_HOME` is enough. Without `interactivecomments`, zsh passes a trailing `# ...` to the command as arguments (e.g. `adb` just prints its usage).
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

## 3. Emulator and phone

The primary phone runs payment apps, so it **never** gets Developer options or USB debugging (many payment apps refuse to run with them on). Development and testing use the emulator; the phone only gets sideloaded release builds.

**Emulator** (one image only):

```bash
sdkmanager "emulator" "system-images;android-35;google_apis;arm64-v8a"
avdmanager create avd -n pft35 -k "system-images;android-35;google_apis;arm64-v8a" -d pixel_7
emulator -avd pft35 -gpu host > /tmp/pft35-emulator.log 2>&1 &
adb devices                      # expect: emulator-5554  device (may say offline while booting)
adb emu sms send 12345 "Test"    # appears in the emulator's Messages app
```

- `-gpu host` renders on the Mac's GPU; without it API 35 falls back to slow software rendering. Drop the flag if the screen glitches.
- Answer **no** if `avdmanager` asks about a custom hardware profile; `-d pixel_7` avoids the question.
- Use the Google APIs image, not a Google Play image. To check the minSdk floor later, add `system-images;android-33;google_apis;arm64-v8a`.
- `adb emu sms send <sender> "<text>"` is how SMS capture (M1b) is tested end to end. Send only reviewed, redacted text from `fixtures/`, never raw messages.

**Primary phone** (no USB, no Developer options):

1. Install only **release** builds signed with the release keystore (§5). Android accepts an update only if it is signed with the same key; changing keys means uninstalling, which deletes the encrypted database. So never put a debug build on the phone, and create the keystore before the first install.
2. Copy the APK to the phone (Google Drive or file transfer), open it in Files, allow *Install unknown apps* for Files once, install, then turn that permission off again.
3. Android 13+ can block sensitive permissions for sideloaded apps ("restricted settings"). If a permission is greyed out, use App info → ⋮ → *Allow restricted settings*. Whether this covers SMS permissions on the phone's Android version is **unverified**; check it when M1b starts.

## 4. Clone and install the git hooks

```bash
git clone https://github.com/imagh/personal-finance-tracker && cd personal-finance-tracker
pre-commit install && pre-commit install --hook-type pre-push
python3 -m unittest discover -s tools/redact      # sanity check, should pass
```

Never commit with `--no-verify`. The commit hook runs `./gradlew --quiet ktlintFormat detekt`, the push hook runs `./gradlew check` (tests, coverage floor, lint); the first run downloads Gradle 8.10.2 and the dependencies, so allow a few minutes. Both hooks configure `:app`, so the shell that runs `git commit` and `git push` needs `ANDROID_HOME` set (§2).

## 5. Secrets: environment only (docs/TECHNICAL.md §3)

Nothing secret goes in the repo. Put values in your shell profile or `~/.gradle/gradle.properties`:

| Variable (proposed names) | Used for |
|---|---|
| `ANDROID_KEYSTORE_PATH`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD` | Release signing (keystore lives outside the repo, e.g. `~/.android/`) |
| `GOOGLE_OAUTH_CLIENT_ID` | Google sign-in for Gmail read-only and Drive appData |
| `NVD_API_KEY` | OWASP dependency-check (`./gradlew dependencyCheckAggregate`). Request a free key at <https://nvd.nist.gov/developers/request-an-api-key> and export it in your shell profile. The scan refuses to start without it. It is not needed for `check` |
| `OSSINDEX_USERNAME`, `OSSINDEX_TOKEN` | Sonatype OSS Index analyzer in the same scan (anonymous access was withdrawn). Create a free account at <https://ossindex.sonatype.org>, take the API token from your account settings, and export both in your shell profile; keep the token in the Keychain (`security add-generic-password -a "$USER" -s OSSINDEX_TOKEN -w`, read back with `security find-generic-password -a "$USER" -s OSSINDEX_TOKEN -w`). The scan refuses to start without them |

Debug builds need none of these. The `.gitignore` and the agents' deny-list already block `.env*`, `local.properties`, keystores and your private exports.

## 6. graphify (token saver, docs/TECHNICAL.md §2)

The repo already carries the graph (`graphify-out/graph.json`, `GRAPH_REPORT.md`), the `CLAUDE.md` section and the PreToolUse hook in `.claude/settings.json`. Each machine only needs the tool:

1. `uv tool install graphifyy==0.9.97` (pinned and vetted in TECHNICAL.md §2). It must land in `~/.local/bin`, which is where the committed hook calls it.
2. Check: `~/.local/bin/graphify --version` prints `graphifyy v0.9.97`.
3. Optional, once Kotlin code exists: `graphify hook install` adds post-commit/checkout/merge/rewrite hooks that rebuild the code graph. They sit beside the pre-commit hooks without replacing them.
4. After `git pull`, `graphify update .` refreshes code nodes for free. Doc nodes need `/graphify . --update` in Claude, which costs tokens; run it when docs change materially.

To upgrade: vet the new version, re-read the hook, then update the pin here and in TECHNICAL.md §2.

## 7. Using the agents

| Command | Does | Model |
|---|---|---|
| `/architect <task>` | writes a TDD-ordered plan to `docs/plans/` | Opus, high |
| `/dev <plan>` | implements under TDD, then auto-reviews | Sonnet, high |
| `/review-changes` | reviews the current diff | Sonnet, high |

Before every PR also run `/security-review` (docs/TECHNICAL.md §5). Agents open PRs; **you** merge.

## 8. Sample messages (when you are ready)

See [collecting-sample-messages.md](collecting-sample-messages.md). Your bank list and redacted samples are still open for planning and are needed before M1b.

## 9. Build, test and the emulator

With `JAVA_HOME` and `ANDROID_HOME` set (§2), from the repo root:

```bash
./gradlew ktlintFormat detekt         # format and lint (what the commit hook runs)
./gradlew check                       # everything the push hook runs: JVM + Robolectric tests, lint, 90% coverage floor on core
./gradlew :app:testDebugUnitTest      # just the Robolectric tests
./gradlew :app:assembleDebug          # debug APK in app/build/outputs/apk/debug/
./gradlew dependencyCheckAggregate    # PR security step; needs NVD_API_KEY + OSSINDEX_* (§5), slow on first run
./gradlew dependencyCheckAggregate -PscanAll  # same, report-only over build/test tooling
```

Robolectric downloads its Android 15 (`android-all`) jar from Maven Central the first time the app
tests run and caches it under `~/.m2`; later runs are offline-capable.

**Emulator (`pft35`, from §3).** Unit and Robolectric tests need no device. Instrumented tests
(`src/androidTest`: the Android Keystore and SQLCipher, slice A4b) are not part of `check`; run them
locally with the emulator up:

```bash
emulator -avd pft35 -no-window -no-audio -gpu host > /tmp/pft35-emulator.log 2>&1 &
adb wait-for-device; adb shell getprop sys.boot_completed       # wait until it prints 1
adb devices                                                     # expect: emulator-5554  device
ANDROID_SERIAL=emulator-5554 ./gradlew :app:connectedDebugAndroidTest
```

**If the snapshot boot hangs** (`adb devices` stays `offline`), cold-boot instead:
`emulator -avd pft35 -no-window -no-audio -no-snapshot-load -no-snapshot-save -gpu host`, then wait
for `sys.boot_completed` = 1 (about 15 s). The tests use their own database name, Keystore alias and
folder, so they never touch the app's real data; stop the emulator with `adb emu kill`.

To try the app by hand: `./gradlew :app:installDebug`, then launch "Personal Finance Tracker". It
shows a blank window for now (slice A1), and screenshots are deliberately blocked by `FLAG_SECURE`.
The first start creates the encrypted database and its key. In a debug build you can look at the
files with `adb shell run-as dev.imagh.pft ls -la databases no_backup`; deleting
`no_backup/pft_db_key.bin` and restarting shows the "Your data can't be opened" screen
(`KEY_FILE_MISSING`) and leaves the database untouched.

**Dev devices that ran a build before A4b** (A4a created a plaintext `pft.db` and no key file) show
`KEY_FILE_MISSING` with the A4b build, because the app never replaces an existing database. Uninstall
the app, or clear its data (`adb shell pm clear dev.imagh.pft`), before installing A4b. This only affects
development emulators and phones; there are no real users with an older database.

**Changing dependencies.** Versions are pinned in `gradle/libs.versions.toml` and checked against
lockfiles and `gradle/verification-metadata.xml`, so a new or upgraded dependency fails the build
until you refresh them deliberately. The exact commands are in `build-logic/README.md`.
