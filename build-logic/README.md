# `build-logic` — convention plugins

## Purpose

An included Gradle build (`pluginManagement { includeBuild("build-logic") }`) that holds the
shared build configuration, so no module copies it (CLAUDE.md §3.4, plan §3.4).

## Responsibilities and boundaries

Two plugins, each with at least two consumers:

| Plugin id | Applied by | Provides |
|---|---|---|
| `pft.kotlin-core` | every `core/<module>` | Kotlin/JVM 17 toolchain, JUnit 5 + Kotest (runner, assertions, property), `pft.lint`, and the **JaCoCo gate** described below |
| `pft.lint` | `pft.kotlin-core`, `:app` and the root project (for `build-logic` and the build scripts) | ktlint (engine 1.4.1) and detekt 1.23.7, both configured from root files |

### How the build scripts and build-logic are linted

`build-logic` is an included build, so root tasks do not reach it by themselves. The root project
applies `pft.lint` too, and the root `build.gradle.kts` adds the extra paths: ktlint's
`kotlinScriptAdditionalPaths` covers `build-logic/**/*.kt(s)`, root `*.kts` are covered by ktlint's
script tasks, and detekt's `source` lists `build-logic/src`, the build-logic and root
`build.gradle.kts`/`settings.gradle.kts`, and the module build files. So the pre-commit command
`./gradlew ktlintFormat detekt` (run from the root) lints all of it. A 130-character line in
`build.gradle.kts` or `settings.gradle.kts` fails detekt, and a magic number in build-logic fails
detekt. New module build files must be added to the detekt `source` list in the root
`build.gradle.kts` (ktlint finds them through each module's own script task).

### The coverage gate (CLAUDE.md Rule A)

`./gradlew check` fails a core module when:

1. line coverage is below **90%** (`jacocoTestCoverageVerification`, hardcoded in the plugin so a
   module cannot lower it), or
2. the module has production sources but **no tests**, or its tests produced no coverage data
   (`requireTestsForProductionCode`). JaCoCo itself *skips* verification when there is no
   execution data, so without this guard a module with zero tests would pass. The guard keys off the
   test **sources** (any `.kt`/`.java` under `src/test`), not off `build/jacoco/test.exec`, because
   a leftover exec file from an earlier run must not let a module with deleted tests pass.

Experiments run locally (not committed):

| Setup | Result |
|---|---|
| class with 1 of 3 functions tested | floor fails: `lines covered ratio is 0.14` |
| same class, no tests, clean build | `requireTestsForProductionCode` fails |
| all functions tested | `check` passes |
| tests deleted, a leftover **real** `build/jacoco/test.exec` kept, `check` WITHOUT `clean`, guard keyed on the exec file | **passed (fail open)** |
| same state, guard keyed on test sources | `requireTestsForProductionCode` fails |

(Gradle itself removes the previous exec file when the `test` task becomes `NO-SOURCE`, so the
fail-open case needed the file restored by hand; the source-based guard does not depend on that.)

**Follow-up:** these experiments are manual. A Gradle TestKit test in `build-logic` that replays
the rows above would make the Rule A gate itself regression-tested; planned after A1.

## Public interface

Plugin ids above. Version pins are read from the root `gradle/libs.versions.toml` through the
`libs` catalog (build-logic's `settings.gradle.kts` imports it), so there is one place to pin.
A core module that must share fakes with other modules applies Gradle's `java-test-fixtures`
itself (`:core:ledger` does); `pft.lint` already lints `src/testFixtures/kotlin`, and the JaCoCo
gate counts only `src/main`, so fixtures do not need their own tests to meet the floor (they are
exercised by the module's tests).

Kotlin, ktlint and detekt plugin artifacts are `compileOnly` here and loaded once by the root build
(`alias(...) apply false`); bundling them would load the Kotlin plugin twice in different
classloaders.

## Data it owns

None.

## Configuration

Static, in code: JVM 17, coverage floor 0.90 line ratio. Style rules: root `.editorconfig`
(ktlint) and `config/detekt/detekt.yml` (detekt, on top of the defaults). Everything else is
convention, not configuration.

## Dependencies

`kotlin-gradle-plugin` 2.1.0, `ktlint-gradle` 12.1.2, `detekt-gradle-plugin` 1.23.7 (all
`compileOnly`). Built with Gradle 8.10.2's embedded Kotlin 1.9 via `kotlin-dsl`, which is why the
plugins are Kotlin classes and not precompiled `.gradle.kts` scripts (those need the plugins on the
runtime classpath, which would double-load them).

## How to run and test

The plugins are exercised by every build. Common commands from the repo root:

```bash
./gradlew ktlintFormat detekt     # what the pre-commit hook runs
./gradlew check                   # what the pre-push hook runs (tests + coverage floor + lint)
./gradlew :core:money:check       # one module
```

### Refreshing pins (deliberate upgrades only)

Dependencies are pinned in `gradle/libs.versions.toml`, locked in `gradle.lockfile` files
(root, `app/`, `core/*/`, `build-logic/`, and `buildscript-gradle.lockfile`) and checksum-verified
in `gradle/verification-metadata.xml` (sha256). After changing a version, or adding a module or
dependency:

```bash
./gradlew buildEnvironment resolveAndLockAll --write-locks --refresh-dependencies
./gradlew -p build-logic dependencies --write-locks      # then: rm -r build-logic/gradle
./gradlew --write-verification-metadata sha256 --refresh-dependencies --rerun-tasks --no-build-cache \
    buildEnvironment resolveAndLockAll ktlintFormat detekt check assembleDebug assembleRelease
```

**Do not delete `verification-metadata.xml` first.** The write command appends entries for
artifacts it has not seen and keeps every existing checksum, so a changed checksum for an old
artifact still fails the build instead of being silently re-trusted. `--refresh-dependencies`
matters: on a fresh machine Gradle also downloads the metadata of versions that lost conflict
resolution, and those files must be in the file too (a warm cache hides them).

Review the diff of every lockfile and of `verification-metadata.xml` before committing. A new
entry is a trust-on-first-use decision ("Generated by Gradle"): vet the dependency first
(CLAUDE.md Rule C).

**Pruning entries nothing resolves any more.** Copy the repo (without `.git`) to a scratch
directory, delete that copy's `verification-metadata.xml`, and run the write command above with an
empty `GRADLE_USER_HOME`. That produces the minimal set a fresh clone needs. Remove from the real
file only the entries missing from the minimal set, and check that every shared entry has the same
sha256 (a difference means an artifact changed upstream: stop and investigate). Do not prune by
guessing: for example the BouncyCastle 1.77 and 1.78.1 entries look stale but are required, because
AGP's tooling configurations still resolve them.

### Vulnerability scan (not part of `check`)

```bash
export NVD_API_KEY=...            # from your shell profile (Keychain); never commit it
export OSSINDEX_USERNAME=...      # Sonatype OSS Index account email
export OSSINDEX_TOKEN=...         # OSS Index API token, from the Keychain; never commit it
./gradlew dependencyCheckAggregate
```

OWASP dependency-check 11.1.1 scans all modules from the root and fails on CVSS >= 7. It is slow
and needs the NVD feed, so it runs in the PR security step (`docs/TECHNICAL.md` §5), not in
`check`. Sonatype OSS Index stopped answering anonymous requests, so the OSS Index analyzer needs
an account: without credentials it errors on every artifact and the analysis fails. If any of the
three variables is missing, the task fails immediately and names the ones that are missing.

**What the gate covers.** The default run fails only on findings in what ships: the app's
`releaseRuntimeClasspath` and each core module's `runtimeClasspath`. Build and test tooling (the
Kotlin compiler, ktlint, detekt, AGP's device-test platform) never reaches the APK and carries
dozens of old CVEs we can't fix without a toolchain bump, so gating on it would keep the scan
permanently red. Run `./gradlew dependencyCheckAggregate -PscanAll` in the PR security step too:
it scans every configuration **report-only**, so tooling findings stay visible. Record anything
new or notable from it in the PR.

**Suppressions** live in `config/dependency-check/suppressions.xml`. Each one needs a written
reason and an `until` expiry date, so it lapses and comes back for review. The current one
(CVE-2026-53914, Kotlin compiler build-cache deserialization) expires 2027-01-31; until Kotlin is
>= 2.4.20 we use **no remote or shared Gradle build cache**.
The first run downloads the NVD database (about 1 hour; about 240 MB under
`~/.gradle/dependency-check-data/`); later runs only fetch updates.
