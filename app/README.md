# `:app` — Android application

## Purpose

The thin Android layer of Personal Finance Tracker: Compose UI, the single `MainActivity`, and (in
later slices) the Room/SQLCipher implementations of the `core` repository interfaces, Keystore key
handling, app lock and safe logging. It contains no business logic; that lives in `core/<module>`
(CLAUDE.md §3.1).

Status: **A1 skeleton plus DS1, DS2, DS4 and DS5 (theming, motion, one-hand layout, content primitives).** The app launches a `FLAG_SECURE`, edge-to-edge
window inside `PftTheme` (a debug build shows the design catalogue; release shows an empty surface).
Screens, persistence and DI arrive in slices A4a–A5 (plan
`docs/plans/2026-10-28-m1a-core-ledger.md`); the rest of the design system (icons, DS3) later (plan
`docs/plans/2026-10-29-m1a-design-system.md`; what is built: `docs/architecture/design-system.md`).

## Responsibilities and boundaries

- Owns everything that needs the Android SDK. Depends on `core`, never the reverse (today `:core:money`, for `MoneyText`).
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
that activity with Robolectric instead. Likewise `ui-tooling` (debug only) registers an exported
`androidx.compose.ui.tooling.PreviewActivity`; `src/debug/AndroidManifest.xml` removes it
(`tools:node="remove"`), so Studio's "run preview on device" is unavailable but static previews and
screenshot tests work. `ManifestHardeningTest` runs against both variants.

## Public interface

Entry point: `dev.imagh.pft.app.MainActivity`. Reusable building blocks (plan §3.4) are listed here
as they land (`data/common`, `ui/common`).

### `ui/theme` (design-system tokens, DS1, DS2, DS4)

| Symbol | What it is |
|---|---|
| `PftTheme(prefs: ThemePrefs = ThemePrefs.Default) { ... }` | Wraps the UI. Resolves dark/light from `prefs.mode`, picks the colour scheme, applies `MaterialTheme` and provides the extended tokens |
| `PftTheme.money` | `MoneyColors` for the current theme (read inside `PftTheme`) |
| `PftTheme.type` | `PftTypography`: `amountHero` (48/60, display font, `tnum`+`lnum`), `amountLarge` (32/40, sans), `amountRow` (16/24, sans, `tnum`) |
| `PftTheme.shapes`, `MaterialTheme.shapes` | `PftShapes.glyph` (category badge, 30% corners); M3 shapes are 6/10/16/24/32dp |
| `PftTheme.spacing` | `PftSpacing`: `xxs` 2, `xs` 4, `sm` 8, `md` 12, `lg` 16, `gutter` 20, `xl` 24, `xxl` 32, `huge` 48 dp |
| `MaterialTheme.typography` | The M3 scale, **every role in the sans text family** (`FontFamily.Default`); `headlineLarge` 32/40 semibold and `titleLarge` 22/28 medium are the header titles. |
| `PftTheme.motion` | `PftMotion`: springs `snappy` (0.30 s, bounce 0.10), `smooth` (0.45 s, 0), `gentle` (0.60 s, 0), `bouncy` (0.50 s, 0.25) as `SpringSpec<Float>`; `navEnter`/`navExit`/`navPopEnter`/`navPopExit` (25% slide + fade on `smooth`); `PRESSED_SCALE` 0.97; `smoothOffset` is `smooth` over `IntOffset` for slide transitions (rolling digits). A constant, not a CompositionLocal |
| `iosSpring<T>(durationMillis, bounce)` | Converts a SwiftUI-style spring to a `SpringSpec<T>` (`stiffness = (2π/duration)²`, `dampingRatio = 1 − bounce`); throws `IllegalArgumentException` for bounce outside `[0, 1)` or duration ≤ 0 |
| `Modifier.pressScale()` | Scales to 0.97 with `snappy` while pressed; observes pointers only (does not consume). Put it before `clickable` |
| `ThemePrefs(mode: ThemeMode, dynamicColor: Boolean, displayFont: DisplayFont)`, `ThemePrefs.Default` | The user's appearance. `Default` is `SYSTEM`, dynamic colour on, bundled serif |
| `DisplayFont { BUNDLED_SERIF, SYSTEM_SERIF, TEXT }` | Font of the hero number only; the serif is never used for titles (owner decision Q10) |
| `ThemeMode { SYSTEM, LIGHT, DARK }` | `SYSTEM` follows the system night mode |
| `resolveColorScheme(dark, dynamic, context)` | Dynamic (Material You) scheme when `dynamic`, else `BrandColors` |
| `BrandColors.light` / `.dark` | The fallback scheme: seed ink indigo `#4A55B8`, generated offline (tonal spot), committed as constants. Used when dynamic colour is off and in screenshot tests |
| `MoneyColors`, `moneyColors(dark)` | Fixed green/red/amber tokens for amounts and status. **Never derived from the accent**; the same in the dynamic and the brand scheme. Positive balances are neutral by owner decision (`MoneyTone` in `ui/common` maps meaning to these tokens) |

The bundled serif is Newsreader (`res/font/newsreader.ttf`, OFL-1.1, unmodified upstream; licence in
`assets/licenses/newsreader-OFL.txt`; provenance and hash in `docs/architecture/design-system.md`).
`TokenSheet` (internal) shows every token; it feeds the Studio preview, the debug catalogue and the
screenshot goldens.

Rules: never draw money text on `surfaceVariant`; use `background`, `surface` or a
`surfaceContainer*` step (dark `negative` on `surfaceVariant` is 4.23:1, below 4.5). Do not wrap an M3 component just to restyle it (style comes from the theme); raw colour
values and design `dp`/`sp` live only in `ui/theme` (detekt's `MagicNumber` is relaxed there for
that reason). Colour is never the only cue: every amount carries a sign and every status a word.

### `ui/common` (shared components, DS4 and DS5)

| Symbol | What it is |
|---|---|
| `CollapsingHeaderScaffold(title, style, subtitle, hero, heroCaption, period, actions, bottomBar, floatingActionButton) { LazyListScope }` | The screen frame: M3 `TopAppBarState` + `exitUntilCollapsedScrollBehavior` (snap on `PftTheme.motion.smooth`), expanded height `clamp(0.38 x window, 200dp, 360dp)`, 64dp collapsed bar, collapsed-only under 560dp of window height, expanded while the list fits. Owns the `LazyColumn`. Edge-to-edge (`safeDrawing`); `bottomBar` is `imePadding()`-aware |
| `HeaderStyle { Dashboard, Standard }` | `Dashboard`: `titleLarge` title about 12% down plus the hero, caption and period stepper in the lower (thumb) band. `Standard`: `headlineLarge` title centred in the middle, no hero. All titles are sans, centred and read-only (Q10) |
| `HeaderPeriod(onPrevious, onNext)` | Dashboard only: draws the ‹ › stepper and makes a sideways swipe of more than 48dp on the expanded header step the period |
| `BottomActionBar(primary, modifier, secondary)`, `BottomBarAction(label, enabled, onClick)` | One full-width primary button, or an outlined secondary plus the primary on the end side; at least 56dp tall; pads by `safeDrawing` (so it clears the IME and navigation bar) |
| `MoneyText(amount: Paise, semantic: MoneySemantic, modifier, size: MoneySize = Row, animate = false)` | One amount: `PaiseFormat.format` (sign-free, from `:core:money`) with a U+2212 or `+` prepended here, never reformatted. Tone from `moneyTone` mapped to a `PftTheme.money` token (neutral is `onSurface`); the spoken description names the direction first ("Expense, ₹250.00"). A negative amount is rejected (`IllegalArgumentException`) for every semantic except `Balance`. `size = Hero` steps the figure down through `HeroSizeSteps` (48, 40, 34, 28, 24, 20, 16, 12sp) to the largest that fits one line; if even 12sp does not fit it wraps rather than cutting digits. `animate` rolls the digits (`PftMotion.smooth`) |
| `MoneySemantic { Income, Expense, Transfer, Balance, Budget(state) }`, `BudgetState { UNDER, AT_THRESHOLD, OVER }`, `MoneySize { Hero, Large, Row }` | What an amount means and which `PftTheme.type.amount*` style it uses. `Row` is the only size where an expense is neutral (Q2); `Balance` is the only semantic that may be negative |
| `MoneyTone { Neutral, Positive, Negative, Warning }`, `moneyTone(semantic, amount, inRow)` | The pure mapping: income Positive; expense Negative in totals and Neutral in rows (Q2); balance Neutral unless negative (Q3); budget Positive, Warning or Negative by state |
| `StatusChip(label, tone: MoneyTone, modifier)` | A labelled pill (Unverified, Overdue, Paused, Over, 80%). The label is required (blank is rejected): colour is never the only cue. Colours are the money container tokens, or `surfaceContainerHighest` for Neutral (`chipColors`) |
| `GroupedSection(modifier) { row(onClick?) { ... } }`, `GroupedSectionDefaults.listItemColors()` | An inset (20dp gutter), rounded (`large`) island of rows on `surfaceContainer`, with a one-pixel divider, inset 16dp from the start, between rows only (N rows, N-1 dividers). A row with `onClick` is clickable and at least 56dp tall. Give M3 `ListItem`s `listItemColors()` (transparent container) |
| `EmptyState(message, modifier, glyph, actionLabel, onAction)` | A centred glyph slot, message and optional tonal-button action; the label and callback must be given together |

The hero slot is set in `PftTheme.type.amountHero` by default (the serif is the number only). Actions
go in the top row (back, search, filter, overflow; secondary only), add actions in the FAB. Both
components are shared (named consumers in `docs/architecture/code-structure.md`); do not wrap them
to restyle. `HeaderSamples.kt` holds the fake-data samples that feed the previews, the debug
catalogue and `HeaderScreenshotTest`; DS5 adds one file per component (`MoneyTextSamples.kt`,
`StatusChipSamples.kt`, `GroupedSectionSamples.kt`, `EmptyStateSamples.kt`) for the same three
consumers and `ContentScreenshotTest`. Each `*Samples` file is listed in `mustBeRemoved` of
`verifyReleaseShrink`, so R8 is shown to drop it from the release APK.

Money text never sits on `surfaceVariant`: `GroupedSection` and `StatusChip` use `surfaceContainer`
steps, and `ColorContrastTest` sweeps both and pins that `surfaceVariant` stays excluded.

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

`DevContent()` is what `MainActivity` shows until A5 brings the navigation host. In `src/debug` it
shows `DesignCatalog`: the token sheet, a motion page (press target, one spring track per token), the bottom bar, both header styles and a page each for money text, chips, sections and the empty state, with toggles for light/dark, hero font and font scale (use it
to check tokens and components on the emulator or phone; it grows with each DS slice). `src/release` holds an empty
twin, so nothing from the catalogue ships in `DevContent`. `TokenSheet` itself lives in `main`
(`ui/theme`) so the screenshot tests can render it on both unit-test variants; it is unreferenced by
release code and R8 drops it (checked by `verifyReleaseShrink`, below).

## Data it owns

None yet. The encrypted Room database (`pft.db`) arrives in A4a/A4b.

## Configuration

Static, build-time: `applicationId`/`namespace` `dev.imagh.pft` (**permanent**: changing it or the
release signing key installs as a new app with an empty database; plan §12 Q4), minSdk 33,
target/compile SDK 35, JDK 17. Pinned versions are in `gradle/libs.versions.toml`.
Runtime appearance (`ThemePrefs`: mode, dynamic colour, display font) is a plain value that defaults to
`SYSTEM` + dynamic on + bundled serif; the settings screen and its `app_setting` storage arrive in AP1, so today
nothing reads or writes a setting. Colours, like all design tokens, are static code constants.

## Dependencies

Compose BOM 2024.10.01 (ui 1.7.5, material3 1.3.1), `androidx.activity:activity-compose` 1.9.3.
`androidx.compose.ui:ui-tooling-preview` (annotations only, via the BOM; the only DS2 addition to
the release runtime classpath), `androidx.compose.ui:ui-tooling` (debug only).
Project: `:core:money` (`implementation`, DS5): `MoneyText` formats through `PaiseFormat`. It has no
external dependencies, so the lockfiles and verification metadata did not change.
Test only: JUnit 5 Jupiter, JUnit Vintage + JUnit 4 (to run Robolectric), Robolectric 4.14.1,
`androidx.test:core`, `androidx.compose.ui:ui-test-junit4` (via the BOM), Roborazzi 1.36.0
(`roborazzi`, `roborazzi-compose`, `roborazzi-junit-rule` and its Gradle plugin; screenshot tests).
No network, analytics or crash SDKs.

## How to run and test

```bash
./gradlew :app:testDebugUnitTest      # Robolectric JVM tests (no emulator needed)
./gradlew :app:assembleDebug          # debug APK (debug key)
./gradlew :app:lint                   # Android lint (also part of `check`)
./gradlew :app:recordRoborazziDebug   # re-record the screenshot goldens (deliberate visual changes only)
./gradlew :app:verifyRoborazziDebug   # compare against the goldens (what `check` does via the unit tests)
./gradlew :app:assembleRelease        # minified (R8) release APK, UNSIGNED: no release signing until H3
./gradlew :app:verifyReleaseShrink    # R8 guard (part of `check`): MainActivity kept, preview code removed
```

**Release build (R8).** `buildTypes.release` has `isMinifyEnabled` and `isShrinkResources` on
(owner decision 2026-10-30, so sample and preview code does not ship), with
`proguard-android-optimize.txt` plus `app/proguard-rules.pro`. That file has no keep rules yet: the
release runtime classpath is Compose and activity-compose, which carry their own consumer rules,
and AGP keeps the manifest's `MainActivity`. Add a rule only for something R8 reports or a smoke
test shows broken, with a comment saying why; the SQLCipher/Room rules come with those
dependencies (A4a, H3). There is deliberately **no release `signingConfig`** (H3 adds it from
`ANDROID_KEYSTORE_*` and must fail closed), so `assembleRelease` yields `app-release-unsigned.apk`.
The release APK is about 1.4 MB with R8 (18.3 MB without).

`verifyReleaseShrink` (registered in `app/build.gradle.kts`, wired into `check`) runs
`assembleRelease`, reads `build/outputs/mapping/release/mapping.txt` and fails with a clear message
if minification is off, `dev.imagh.pft.app.MainActivity` is gone, or any
`dev.imagh.pft.app.ui.theme.TokenSheet*` class is still present. When a slice adds preview or sample
code, add its compiled class prefix to `mustBeRemoved`. To smoke-test a release build on `pft35`,
sign a copy of the APK with the debug keystore, outside the build (recipe in `docs/learnings`).

**Screenshot goldens.** `TokenSheetScreenshotTest` (Roborazzi, `@GraphicsMode(NATIVE)`, qualifiers
`w393dp-h851dp-xxhdpi`, light and `+night`, font scale 1.0 and 2.0, brand colours) renders the token
sheet page by page. The goldens are committed in `app/src/test/screenshots/` (fake data only);
`roborazzi.test.verify=true` in `gradle.properties` makes `./gradlew check` compare against them,
on both the debug and release unit-test variants. After an intended visual change run
`:app:recordRoborazziDebug`, review every changed PNG and commit them with the code. Failing actual
and diff images land in `app/build/outputs/roborazzi/`.

Design-token tests: `ColorContrastTest` (JUnit 5, WCAG contrast of every colour pair, light and dark),
`ThemeResolutionTest`, `PftThemeModeTest` (needs the `+night` qualifier), `WindowThemeTest`,
`PftTypographyTest`, `PftShapesTest`, `PftSpacingTest`, `BundledFontTest` (font hash and OFL text) and
`TokenSheetScreenshotTest` (all Robolectric). DS4 adds `PftMotionTest` and `HeaderHeightTest` (JUnit 5, pure),
`NavTransitionTest`, `PressScaleTest`, `CollapsingHeaderScaffoldTest` (real nested scroll, clock stepped by hand),
`BottomActionBarTest` and `HeaderScreenshotTest` (goldens for both header styles, expanded and collapsed, and the bar); `DesignCatalogTest` lives in `src/testDebug` because
the catalogue is debug-only. DS5 adds `MoneyToneTest` (JUnit 5, pure), `MoneyTextTest` (real `PaiseFormat`:
signs, spoken direction, tone colours, hero shrink at the real maximum `Paise.MAX_AMOUNT` including 200% font
scale on 320dp, rolling digits), `StatusChipTest`, `GroupedSectionTest`, `EmptyStateTest` and
`ContentScreenshotTest` (goldens for the four components, `MoneyText` also at 200%). Text fit is asserted with
line metrics (`TextLayoutSupport.kt`), not `hasVisualOverflow`, which is unreliable in Compose 1.7.

Robolectric tests use the JUnit 4 runner and execute on the JUnit Vintage engine, because
Robolectric does not run on Jupiter. Robolectric downloads its `android-all` SDK 35 jar from Maven
Central on first use (cached in `~/.m2`); this download is outside Gradle's dependency
verification (see `docs/learnings/README.md`). Instrumented tests (emulator `pft35`) arrive with
A4b.
