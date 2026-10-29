# Personal Finance Tracker — Learnings

A running ledger of decisions, gotchas, and non-obvious facts worth carrying into future
sessions (CLAUDE.md Rule B). Append at the end of every session/task; newest at the top. Keep
each entry short: what we learned, why it matters, and how to apply it next time.

---

<!-- Template for an entry:
### YYYY-MM-DD — <short title>
- **Learning:** <the non-obvious fact or decision>.
- **Why:** <why it matters / what it prevents>.
- **How to apply:** <what to do next time>.
-->

### 2026-10-29 — Design direction: premium Cashew, calm money colours
- **Learning:** the owner wants Cashew's look made more premium: one-hand layout (content from mid-screen), iOS-like springs, system sans-serif for daily screens and Newsreader serif (set looser than M3's defaults) for dashboards. Green and red are kept for meaning, not decoration: expense rows and positive balances are neutral, while totals, over-budget and negative balances carry colour. The accent comes from the system, with indigo `#4A55B8` as the fallback.
- **Why:** colour everywhere reads as noise. The light green and red are nearly equal in lightness, so colour alone can't carry meaning for colour-blind users.
- **How to apply:** build every screen from the design-system plan's components (`docs/plans/2026-10-29-m1a-design-system.md`). Give every amount a sign or word, not only a colour. No font or emoji downloads: the app has no network.

### 2026-10-29 — Gate the scan on what ships; report the tooling
- **Learning:** the first full scan found 64 CVEs at CVSS ≥ 7, but none were in code that ships. About 58 were grpc/netty/protobuf inside AGP's `_internal-unified-test-platform-*` configurations (emulator test tooling). One Kotlin compiler CVE (CVE-2026-53914, build-cache deserialization) was CPE-matched onto every Kotlin jar, including the shipped `kotlin-stdlib`. One was an Apache Ant task under JaCoCo.
- **Why:** gating on every configuration keeps the scan permanently red on a pinned, two-year-old toolchain, and a gate that's always red gets ignored.
- **How to apply:** the default `dependencyCheckAggregate` gates `releaseRuntimeClasspath` and `runtimeClasspath` only; `-PscanAll` reports everything without failing. Read a finding's "Referenced In Project/Scope" before acting on it. Suppressions need a reason and an `until` date. Plan the Kotlin ≥ 2.4.20 toolchain bump before 2027-01-31.

### 2026-10-29 — The vulnerability scan needs an OSS Index account, not just an NVD key
- **Learning:** dependency-check's Sonatype OSS Index analyzer is on by default, and OSS Index no longer answers anonymous requests. With only `NVD_API_KEY` set, the first run spent about 58 minutes downloading the NVD database, then every artifact errored in OSS Index and the task failed with "Analysis failed".
- **Why:** the failure only shows at the very end of the slow first run, and the per-artifact errors don't say "unauthorized".
- **How to apply:** export `OSSINDEX_USERNAME` and `OSSINDEX_TOKEN` (token in the Keychain) alongside `NVD_API_KEY`; the task now refuses to start without all three. Never paste a token into an editor tab or chat. If that happens, rotate it.

### 2026-10-29 — A1 Gradle skeleton: five gotchas that cost time
- **Learning:** (1) JaCoCo **skips** `jacocoTestCoverageVerification` when there is no execution data, so a core module with production code and *no tests* passes the 90% floor silently (fail open). `pft.kotlin-core` adds `requireTestsForProductionCode` to `check` to close it; we proved partial coverage fails (0.14 < 0.90), zero tests fails, full coverage passes. (2) The OWASP dependency-check plugin pulls `bcprov` 1.78 while AGP 8.7.3 pulls `bcpkix`/`bcutil` 1.77; the mix breaks AGP's `validateSigningDebug` ("Could not initialize class org.bouncycastle...") but not `check`, so the root build aligns every `org.bouncycastle` artifact on 1.78. (3) Libraries add manifest entries: `androidx.core` adds a `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` uses-permission and `androidx.profileinstaller` adds an **exported** `ProfileInstallReceiver`; both are removed with `tools:node="remove"` and `ManifestHardeningTest` guards the merged manifest (it was red on both before the fix). (4) Convention plugins in `build-logic` must be Kotlin classes with the Kotlin/ktlint/detekt plugins `compileOnly` and loaded by the root build (`apply false`); precompiled `.gradle.kts` plugins need them on the runtime classpath and double-load the Kotlin plugin. (5) Robolectric's `ApplicationInfo` does not expose `dataExtractionRules`; the test reads AGP's merged manifest through `com/android/tools/test_config.properties`.
- **Why:** each one produces a green build that is wrong (1, 3) or a red build far from the cause (2, 4).
- **How to apply:** keep the guard and the hardening test; after any dependency bump run `./gradlew check assembleDebug assembleRelease` (not just `check`) and re-read the merged manifest test. Robolectric downloads its `android-all` jar from Maven Central at test time, outside Gradle's checksum verification; if that becomes a concern, pre-resolve it as a verified Gradle dependency and run Robolectric offline.

### 2026-10-29 — A1 review fixes: verification metadata must be generated against a fresh cache
- **Learning:** `verification-metadata.xml` written on a warm Gradle cache was incomplete: a fresh `GRADLE_USER_HOME` (or `--refresh-dependencies`) also downloads the `.pom`/`.module` files of versions that lost conflict resolution (junit-bom 5.9.x–5.11.x, jackson poms, ktlint's sarif4k/kotlin-logging) and failed verification on 17 artifacts. Fixed by re-running `--write-verification-metadata` with `--refresh-dependencies` in append mode, then proving a clean clone passes with an empty `GRADLE_USER_HOME`. Pruning compared against a minimal set generated from scratch: 36 stale entries were removed, all 1,248 shared checksums were identical, and the BouncyCastle 1.77/1.78.1 entries turned out to be required (AGP tooling configurations still resolve them). Separately, `requireTestsForProductionCode` now keys off test sources, since a leftover `build/jacoco/test.exec` let a module with deleted tests pass without `clean` (reproduced by restoring a real exec file by hand). The build scripts and `build-logic` are now linted from the root, which immediately caught a 134-character line and an overlong description string.
- **Why:** an incomplete checksum file passes on the author's machine and breaks the first clone; a gate that trusts a build artifact can be fooled by a stale one.
- **How to apply:** never `rm` the metadata file to refresh it (it drops prior checksums); follow "Refreshing pins" in `build-logic/README.md`, and test dependency changes against an empty `GRADLE_USER_HOME`. Keep lint covering anything the pre-commit command does not reach by default.

### 2026-10-29 — Dependency pins confirmed in A1
- **Learning:** every plan §3.3 pin resolves on Maven Central/Google Maven and the ones used in A1 work together under Kotlin 2.1.0: Gradle 8.10.2, AGP 8.7.3, Kotlin and Compose plugin 2.1.0, Compose BOM 2024.10.01, activity-compose 1.9.3, JUnit 5.11.3 (BOM) with Vintage for Robolectric 4.14.1 (SDK 35, `androidx.test:core` 1.6.1, JUnit 4.13.2), Kotest 5.9.1, ktlint plugin 12.1.2 with engine 1.4.1, detekt 1.23.7 (plain task, no type resolution), JaCoCo 0.8.12, OWASP plugin 11.1.1. Not yet exercised (existence checked only): Room 2.7.1, KSP 2.1.0-1.0.29, sqlcipher-android 4.7.2, coroutines 1.9.0, lifecycle 2.8.7, navigation-compose 2.8.4, biometric 1.1.0.
- **Why:** Gradle 8.10.2's embedded Kotlin is 1.9, older than the plugins; the build-logic code compiles fine against them as `compileOnly`.
- **How to apply:** add each later library to `gradle/libs.versions.toml` in the slice that uses it and refresh locks and checksums (`build-logic/README.md`).

### 2026-10-28 — Slice branches can't nest under the base branch name
- **Learning:** git refuses `feat/m1a-foundation/a1-…` while `feat/m1a-foundation` exists, because a ref can't be both a file and a directory.
- **Why:** the `feat/<feature>/<slice>` topology in CLAUDE.md §6 cannot be created as written.
- **How to apply:** name slice branches `feat/<feature>-<slice>` (for example `feat/m1a-foundation-a1-gradle-skeleton`) and open their PRs into `feat/<feature>`.

### 2026-10-28 — App ID is permanent; build full export/import before ever changing it
- **Learning:** the applicationId `dev.imagh.pft` and the release signing key define the installed app. A sideloaded build with a different ID or key installs as a new app with an empty database.
- **Why:** changing either one without a migration path loses every transaction, the audit trail and the identities.
- **How to apply:** before changing the ID or rotating the key, first build and verify a full, encrypted export/import of all data (every table, including audit, settings and tombstones), with a round-trip equality test. Export, import, verify, and only then retire the old app. Rule recorded in `docs/plans/2026-10-28-m1a-core-ledger.md` Q4.

### 2026-10-28 — Reuse by layer: shared once two named consumers exist
- **Learning:** the owner wants code abstract at its own layer and reused, but no unnecessary abstraction. The M1a plan's §3.4 lists the shared building blocks per layer (`EntityMeta`, `AuditedWriter`, `EntityRepository`/`RoomEntityRepository`, `EntityListScreen`, `AmountField`, `AuditHistoryList`, …).
- **Why:** 8+ entities share the same lifecycle, columns, audit flow and screens. Copying that code per entity breaks the shared-code rule, but a generic framework breaks the abstraction bar.
- **How to apply:** extract a piece when the plan names two concrete consumers; prefer composition, with at most one level of generic base class and no generic service. In review, both re-implementing a shared block and adding a single-consumer abstraction block the PR.

### 2026-10-28 — graphify: commit the graph, not the machine
- **Learning:** `graphify claude install` writes the hook with an absolute path to one user's home and leaves a `settings.json.graphify-bak`. `graphify-out/` also holds a manifest with absolute paths, a cache and backups. `graphify update .` refreshes only code nodes, never docs.
- **Why:** a committed absolute path breaks the hook on the second machine, and committing the rest of `graphify-out/` leaks local paths and churns diffs.
- **How to apply:** hooks call `$HOME/.local/bin/graphify`; `.gitignore` lets through only `graph.json` and `GRAPH_REPORT.md`. Re-read the hook on every graphify upgrade. Use `/graphify . --update` when docs change.

### 2026-10-28 — Develop on the emulator; the primary phone never gets USB debugging
- **Learning:** the owner's phone runs payment apps, which often refuse to work with Developer options or USB debugging on. The emulator is the development device, and it can receive fake SMS (`adb emu sms send`), which suits M1b. On Apple Silicon, API 35 images fall back to software rendering unless started with `-gpu host`.
- **Why:** a sideloaded app can only be updated with the same signing key, and uninstalling wipes the encrypted database.
- **How to apply:** only release-signed builds go to the phone, installed from a file; create the release keystore before the first phone install. Feed the emulator only redacted, reviewed fixture text.

### 2026-10-28 — Toolchain pinned about two years old; the build tool is not the IDE
- **Learning:** the build toolchain (AGP 8.7.3, Gradle 8.10.2, Kotlin 2.1.0, JDK 17) is pinned to late 2024, but Android Studio stays current: a newer Studio opens an older AGP project fine. In current Studio the per-project JDK is the Gradle *JVM criteria* (`gradle/gradle-daemon-jvm.properties`); a JDK path in the Gradle *Distribution* field fails with "Gradle location is incorrect".
- **Why:** an old IDE misses security fixes and may not run on the current macOS; JVM criteria make Studio and `./gradlew` use the same JDK on both developers' machines.
- **How to apply:** bump the toolchain only deliberately and re-check the AGP/Kotlin/Gradle tables (TECHNICAL.md §7.1). Bend the age rule for security (JDK patches, SQLCipher, anything the vulnerability scan flags). AGP 8.7 caps compileSdk at 35.

### 2026-10-04 — EMIs are expenses; transfers and card bill payments are not
- **Learning:** an EMI payment is an expense linked to its loan entity. Transfers between own accounts and credit-card bill payments are not income/expense.
- **Why:** counts spending once and keeps the loan's outstanding derivable from linked EMIs. A card purchase converted to EMI would double count, so the original purchase is neutralised by linking it to the loan (PRD LN-5).

### 2026-10-04 — Bank SMS are untrusted input; parsing is templates only
- **Learning:** sender headers can be spoofed and scam messages mimic bank alerts. Parsing is deterministic and on-device; auto-logged transactions are flagged unverified.
- **Why:** a forged alert must never be treated as fact, and no message text may leave the device.

### 2026-10-04 — Builds run locally; keep logic in pure-Kotlin `core`
- **Learning:** the owner builds locally on a Claude Pro plan. The cloud environment also blocked `dl.google.com` (Android SDK), which no longer matters.
- **Why:** `core` modules test on a plain JVM in seconds, keeping sessions short and cheap.
