# `:app` — Android application

## Purpose

The thin Android layer of Personal Finance Tracker: Compose UI, the single `MainActivity`, and (in
later slices) the Room/SQLCipher implementations of the `core` repository interfaces, Keystore key
handling, app lock and safe logging. It contains no business logic; that lives in `core/<module>`
(CLAUDE.md §3.1).

Status: **A1 skeleton.** The app launches an empty, `FLAG_SECURE` window. Screens, persistence and
DI arrive in slices A4a–A5 (plan `docs/plans/2026-10-28-m1a-core-ledger.md`).

## Responsibilities and boundaries

- Owns everything that needs the Android SDK. Depends on `core`, never the reverse.
- Call flow: UI (Compose) → ViewModel → core service → repository (Room dao) → db. Each layer calls
  only the one below.
- Owns the **manifest security posture** (below), which is pinned by `ManifestHardeningTest`.

### Manifest security posture (SEC-2, SEC-3, SEC-6)

| Property | Setting | Why |
|---|---|---|
| Backup | `allowBackup="false"` plus `dataExtractionRules` excluding all nine domains from cloud backup and device transfer | Financial data must not reach third-party storage |
| Permissions | none (no `INTERNET`) | "Nothing leaves the device". `USE_BIOMETRIC` arrives with the app lock (H1) |
| Cleartext traffic | `usesCleartextTraffic="false"` | Defence in depth; there is no network code |
| Exported components | only `MainActivity` (launcher) | Smallest attack surface |
| Screen capture | `FLAG_SECURE` set in `MainActivity.onCreate` before any content | No screenshots, screen recording or recents preview |

Two library-contributed manifest entries are removed with `tools:node="remove"` because they would
break the table above: `androidx.profileinstaller.ProfileInstallReceiver` (an exported receiver)
and the `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` permission from `androidx.core` (only needed
below API 33; minSdk is 33). If a library upgrade adds a new entry, `ManifestHardeningTest` fails.

## Public interface

None yet. Entry point: `dev.imagh.pft.app.MainActivity`. Reusable building blocks (plan §3.4) will
be listed here as they land (`data/common`, `ui/common`).

## Data it owns

None yet. The encrypted Room database (`pft.db`) arrives in A4a/A4b.

## Configuration

Static, build-time: `applicationId`/`namespace` `dev.imagh.pft` (**permanent**: changing it or the
release signing key installs as a new app with an empty database; plan §12 Q4), minSdk 33,
target/compile SDK 35, JDK 17. Pinned versions are in `gradle/libs.versions.toml`.
No runtime configuration yet.

## Dependencies

Compose BOM 2024.10.01 (ui, material3), `androidx.activity:activity-compose` 1.9.3. Test only:
JUnit 5 Jupiter, JUnit Vintage + JUnit 4 (to run Robolectric), Robolectric 4.14.1, `androidx.test:core`.
No network, analytics or crash SDKs.

## How to run and test

```bash
./gradlew :app:testDebugUnitTest      # Robolectric JVM tests (no emulator needed)
./gradlew :app:assembleDebug          # debug APK (debug key)
./gradlew :app:lint                   # Android lint (also part of `check`)
```

Robolectric tests use the JUnit 4 runner and execute on the JUnit Vintage engine, because
Robolectric does not run on Jupiter. Robolectric downloads its `android-all` SDK 35 jar from Maven
Central on first use (cached in `~/.m2`); this download is outside Gradle's dependency
verification (see `docs/learnings/README.md`). Instrumented tests (emulator `pft35`) arrive with
A4b.
