# `:app` — Android application

## Purpose

The thin Android layer of Personal Finance Tracker: Compose UI, the single `MainActivity`, and (in
later slices) the Room/SQLCipher implementations of the `core` repository interfaces, Keystore key
handling, app lock and safe logging. It contains no business logic; that lives in `core/<module>`
(CLAUDE.md §3.1).

Status: **A1 skeleton plus DS1 theming.** The app launches an empty, `FLAG_SECURE`, edge-to-edge
window inside `PftTheme`. Screens, persistence and DI arrive in slices A4a–A5 (plan
`docs/plans/2026-10-28-m1a-core-ledger.md`); the rest of the design system in DS2–DS5 (plan
`docs/plans/2026-10-29-m1a-design-system.md`).

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
| Screen capture | `FLAG_SECURE` set in `MainActivity.onCreate` before `enableEdgeToEdge()` and any content | No screenshots, screen recording or recents preview |

Two library-contributed manifest entries are removed with `tools:node="remove"` because they would
break the table above: `androidx.profileinstaller.ProfileInstallReceiver` (an exported receiver)
and the `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` permission from `androidx.core` (only needed
below API 33; minSdk is 33). If a library upgrade adds a new entry, `ManifestHardeningTest` fails.

`androidx.compose.ui:ui-test-manifest` is deliberately not a dependency: it adds an exported
`ComponentActivity` to the debug manifest. Compose tests use `HostedComposeRule`, which registers
that activity with Robolectric instead. `ManifestHardeningTest` runs against both variants.

## Public interface

Entry point: `dev.imagh.pft.app.MainActivity`. Reusable building blocks (plan §3.4) are listed here
as they land (`data/common`, `ui/common`).

### `ui/theme` (design-system tokens, DS1)

| Symbol | What it is |
|---|---|
| `PftTheme(prefs: ThemePrefs = ThemePrefs.Default) { ... }` | Wraps the UI. Resolves dark/light from `prefs.mode`, picks the colour scheme, applies `MaterialTheme` and provides the extended tokens |
| `PftTheme.money` | `MoneyColors` for the current theme (read inside `PftTheme`). Type, shape, spacing and motion accessors join in DS2 and DS4 |
| `ThemePrefs(mode: ThemeMode, dynamicColor: Boolean)`, `ThemePrefs.Default` | The user's appearance. `Default` is `SYSTEM` + dynamic colour on |
| `ThemeMode { SYSTEM, LIGHT, DARK }` | `SYSTEM` follows the system night mode |
| `resolveColorScheme(dark, dynamic, context)` | Dynamic (Material You) scheme when `dynamic`, else `BrandColors` |
| `BrandColors.light` / `.dark` | The fallback scheme: seed ink indigo `#4A55B8`, generated offline (tonal spot), committed as constants. Used when dynamic colour is off and in screenshot tests |
| `MoneyColors`, `moneyColors(dark)` | Fixed green/red/amber tokens for amounts and status. **Never derived from the accent**; the same in the dynamic and the brand scheme. Positive balances are neutral by owner decision (the `MoneyTone` mapping lands in DS5) |

Rules: never draw money text on `surfaceVariant`; use `background`, `surface` or a
`surfaceContainer*` step (dark `negative` on `surfaceVariant` is 4.23:1, below 4.5). Do not wrap an M3 component just to restyle it (style comes from the theme); raw colour
values and design `dp`/`sp` live only in `ui/theme` (detekt's `MagicNumber` is relaxed there for
that reason). Colour is never the only cue: every amount carries a sign and every status a word.

### Window theme

`res/values/themes.xml` and `res/values-night/themes.xml` (`Theme.Pft`) paint the brand surface
behind the first frame (`pft_window_background`, which must equal `BrandColors.*.surface`), keep
the system bars transparent and colour the launch splash, so a dark launch does not flash a light
window. `MainActivity` then calls `enableEdgeToEdge()` (after the `FLAG_SECURE` line). The window
colour is the brand surface even when dynamic colour is on, because the dynamic palette is not
known before Compose runs; the difference is a few tones of a near-neutral.

**Known limitation, AP1 must address:** plain `enableEdgeToEdge()`, the window theme
(`values`/`values-night`) and the splash follow the *system* night mode. Once AP1 lets the user
force `DARK` on a light system (or the reverse), the status/navigation bar icons and the
pre-Compose window colour would be wrong. AP1 fixes this with `UiModeManager.setApplicationNightMode`
(design plan §5.1) and/or passing explicit `SystemBarStyle`s to `enableEdgeToEdge()`.

### Debug and release source sets

`DevContent()` is what `MainActivity` shows until A5 brings the navigation host. `src/debug` is
where the design catalogue grows from DS2; `src/release` holds an empty twin, so nothing from the
catalogue ships.

## Data it owns

None yet. The encrypted Room database (`pft.db`) arrives in A4a/A4b.

## Configuration

Static, build-time: `applicationId`/`namespace` `dev.imagh.pft` (**permanent**: changing it or the
release signing key installs as a new app with an empty database; plan §12 Q4), minSdk 33,
target/compile SDK 35, JDK 17. Pinned versions are in `gradle/libs.versions.toml`.
Runtime appearance (`ThemePrefs`: mode, dynamic colour) is a plain value that defaults to
`SYSTEM` + dynamic on; the settings screen and its `app_setting` storage arrive in AP1, so today
nothing reads or writes a setting. Colours, like all design tokens, are static code constants.

## Dependencies

Compose BOM 2024.10.01 (ui 1.7.5, material3 1.3.1), `androidx.activity:activity-compose` 1.9.3.
Test only: JUnit 5 Jupiter, JUnit Vintage + JUnit 4 (to run Robolectric), Robolectric 4.14.1,
`androidx.test:core`, `androidx.compose.ui:ui-test-junit4` (via the BOM).
No network, analytics or crash SDKs.

## How to run and test

```bash
./gradlew :app:testDebugUnitTest      # Robolectric JVM tests (no emulator needed)
./gradlew :app:assembleDebug          # debug APK (debug key)
./gradlew :app:lint                   # Android lint (also part of `check`)
```

Design-token tests: `ColorContrastTest` (JUnit 5, WCAG contrast of every colour pair, light and dark),
`ThemeResolutionTest`, `PftThemeModeTest` (needs the `+night` qualifier) and `WindowThemeTest` (all
Robolectric).

Robolectric tests use the JUnit 4 runner and execute on the JUnit Vintage engine, because
Robolectric does not run on Jupiter. Robolectric downloads its `android-all` SDK 35 jar from Maven
Central on first use (cached in `~/.m2`); this download is outside Gradle's dependency
verification (see `docs/learnings/README.md`). Instrumented tests (emulator `pft35`) arrive with
A4b.
