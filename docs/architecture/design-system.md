# Design system

Status: **DS1 (colour, `PftTheme`, window themes), DS2 (type, shape, spacing, bundled serif,
screenshot harness, debug catalogue), DS4 (motion tokens, `CollapsingHeaderScaffold`,
`BottomActionBar`) and DS5 (`MoneyText`, `StatusChip`, `GroupedSection`, `EmptyState`) are built.**
Icons and the category palette (DS3) are specified in
`docs/plans/2026-10-29-m1a-design-system.md` and are added to this page by their slice.

The plan is the spec; this page records what is built and how to extend it.

## Principles

"Cashew, but premium" (plan §3.1):

| Principle | In practice |
|---|---|
| Quiet surfaces, one accent | Neutral tonal surfaces. The dynamic accent colours interactive chrome only; money is never drawn in the accent |
| Type carries hierarchy, not boxes | Large figures, generous whitespace, inset grouped "islands" instead of shadowed cards |
| Colour where it signals | Money green/red only on amounts, meters and status, always with a sign or a word |
| Reach first | Content starts about 38% down; primary actions at the bottom (`CollapsingHeaderScaffold`, `BottomActionBar`) |
| Continuity | One spring vocabulary (`PftMotion`); shared-element transitions arrive with C4 |

Rules, enforced in review (detekt has no type resolution, so it cannot check them):

- Do not wrap an M3 component just to restyle it. Style comes from the theme.
- A component goes in `ui/common` only when two or more named consumers exist.
- No raw colour (`Color(0x...)`) or raw design `dp`/`sp` outside `ui/theme`. Layout arithmetic is
  the exception. Detekt's `MagicNumber` is relaxed for `**/ui/theme/**` for this reason.
- Experimental Compose APIs are opted into in one file each.

## Theming

`PftTheme(prefs: ThemePrefs = ThemePrefs.Default) { ... }` in `ui/theme` is the only theme entry
point. It resolves dark or light from `ThemePrefs.mode`, picks the colour scheme (dynamic Material
You, or the committed `BrandColors`), applies `MaterialTheme(colorScheme, typography, shapes)` and
provides the extended tokens through `staticCompositionLocalOf`. Read them through the `PftTheme`
object:

| Accessor | Type | Provided how |
|---|---|---|
| `MaterialTheme.colorScheme` | M3 `ColorScheme` | dynamic or `BrandColors` |
| `MaterialTheme.typography` | M3 `Typography` | every role in the sans text family |
| `MaterialTheme.shapes` | M3 `Shapes` | 6/10/16/24/32dp |
| `PftTheme.money` | `MoneyColors` | fixed green/red/amber, never derived from the accent |
| `PftTheme.type` | `PftTypography` | the three amount styles |
| `PftTheme.shapes` | `PftShapes` | `glyph` (the category badge) |
| `PftTheme.spacing` | `PftSpacing` | the 4dp-grid scale |
| `PftTheme.motion` | `PftMotion` | four springs, navigation transitions, press scale; a constant, the same in every theme |

`ThemePrefs` is a plain data class (`mode`, `dynamicColor`, `displayFont`). Until the appearance
settings (AP1) the app always passes the defaults. Tokens are static code constants; per-user
choices will live in the database (CLAUDE.md §3.5).

## Tokens

### Typography (UI-2, UI-3, owner decision Q10)

The serif (Newsreader) sets **only** `amountHero`. Every M3 role, titles included, is the sans text
family (`FontFamily.Default`, the device's system font). `amountLarge` and `amountRow` are sans.

| Role | Family | Size / line (sp) | Weight | Features |
|---|---|---|---|---|
| `PftTheme.type.amountHero` | display font (Newsreader by default) | 48/60, +0.01em | 400 | `tnum`, `lnum` |
| `headlineLarge` | text | 32/40 | 600 | |
| `titleLarge` | text | 22/28 | 500 | |
| other M3 `display*`, `headline*`, `title*`, `body*`, `label*` | text | M3 defaults | M3 | |
| `PftTheme.type.amountLarge` | text | 32/40 | 500 | `tnum`, `lnum` |
| `PftTheme.type.amountRow` | text | 16/24 | 500 | `tnum` |

`ThemePrefs.displayFont` chooses the hero's family: `BUNDLED_SERIF` (default), `SYSTEM_SERIF` or
`TEXT` ("same as text"). The hero spacing (1.25x line height, +0.01em) comes from the owner (Q1)
and is a named constant in `PftTypography.kt`, to be tuned on the debug catalogue on the phone
before goldens are re-recorded. Newsreader is a variable font; the hero is pinned to weight 400
and the 48sp optical size (`opsz` axis). A custom font without `tnum` falls back to proportional
digits; that is accepted.

### Shapes

| Shape | Radius |
|---|---|
| extraSmall / small / medium / large / extraLarge | 6 / 10 / 16 / 24 / 32dp |
| `PftTheme.shapes.glyph` | 30% of the badge side |

### Spacing

`PftTheme.spacing`: `xxs` 2, `xs` 4, `sm` 8, `md` 12, `lg` 16, `gutter` 20 (screen edge), `xl` 24,
`xxl` 32, `huge` 48 dp.

### Colour

See `BrandColors` and `MoneyColors` (KDoc) and plan §3.3. `ColorContrastTest` pins every pair at
WCAG 4.5:1. Never draw money text on `surfaceVariant`:
dark `negative` on it is 4.23:1. `ColorContrastTest` pins both halves of that: the sweep excludes
`surfaceVariant` on purpose (a test asserts the ratio is still below 4.5, so the exclusion is not
dropped by accident), and the backgrounds DS5 draws money on (`groupedSectionColor`, every
`chipColors` pill) are asserted to be swept surface roles and never `surfaceVariant`.

### Motion (UI-10)

Springs are defined the SwiftUI way, as a perceptual `(duration, bounce)` pair, and converted
exactly to Compose by `iosSpring(durationMillis, bounce)` (mass 1):
`stiffness = (2π / duration in seconds)²`, `dampingRatio = 1 − bounce`. It rejects a bounce
outside `[0, 1)` (NaN included) and a duration of 0 or less with `IllegalArgumentException`.

| Token (`PftTheme.motion.*`) | (duration, bounce) | Stiffness, ζ | Used for |
|---|---|---|---|
| `snappy` | 0.30 s, 0.10 | ≈ 438.6, 0.90 | press feedback, chips, keypad digits, toggles |
| `smooth` | 0.45 s, 0 | ≈ 194.9, 1.00 | screen transitions, header snap, progress fill, rolling digits |
| `gentle` | 0.60 s, 0 | ≈ 109.7, 1.00 | container transforms (FAB to add screen, row to detail) |
| `bouncy` | 0.50 s, 0.25 | ≈ 157.9, 0.75 | rare celebration only (goal reached) |

- The tokens are `SpringSpec<Float>`: that fits `animate*AsState`, `graphicsLayer` and M3's snap spec.
  For another type, build the same spring with `iosSpring<T>(duration, bounce)`.
- **Navigation transitions** (`navEnter`, `navExit`, `navPopEnter`, `navPopExit`) are plain
  `EnterTransition`/`ExitTransition` values: a push slides the incoming screen in from the end by
  25% of the width with a fade, and the outgoing one out toward the start; a pop is the reverse. A5
  wires them into the NavHost; predictive back is verified there.
- **Press feedback:** `Modifier.pressScale()` scales to 0.97 (`PftMotion.PRESSED_SCALE`) with `snappy`
  while a pointer is down, and back on release or cancel. Put it before `clickable` on rows, cards,
  glyph tiles and keypad keys; it only observes pointers, so the ripple and the tap are untouched
  and a drag that turns into a scroll releases the press.
- **Reduced motion:** Compose honours the system animator-duration scale, so animations jump to
  their end state. Rule: no logic waits on an animation to finish (a delete commits on the state
  change, not at the end of a collapse).
- **Judging motion:** only on a release-like build. Debug Compose builds are much slower, so a debug
  jank verdict is meaningless (plan §10, §11).

### One-hand layout (UI-7)

`CollapsingHeaderScaffold` (`ui/common`) is the frame for every screen: a header that collapses to a
64dp bar so content starts about 38% down, within thumb reach, plus `BottomActionBar` for the
primary action.

- **Heights.** Expanded is `clamp(0.38 x window height, 200dp, 360dp)`; collapsed is a 64dp bar
  (both plus the status-bar inset). A window shorter than 560dp (landscape, split screen) has no
  expanded state: the header starts and stays collapsed. A list that fits keeps it expanded.
- **Mechanics.** M3's `TopAppBarState` and `exitUntilCollapsedScrollBehavior(snapAnimationSpec =
  PftTheme.motion.smooth)`; the scaffold sets `heightOffsetLimit` and lays out by
  `collapsedFraction`. There is no custom nested-scroll code. Snap-to-nearer-end on release and
  fling come from M3. The scaffold owns the `LazyColumn` (it needs its scroll state for `canScroll`).
  Layout reads the scroll state in the layout phase and only the presence of parts (large title,
  bar title, lower band) is composition state, so a scroll frame does no recomposition of the header itself. (M3's `Scaffold` still hands the list a
  new content padding as the header height changes, as in every M3 collapsing bar; whether that costs
  frames is for the release-build jank check, deferred to C4.)
- **Header layout rule (owner decision Q10).** All titles are sans; the serif is the hero number
  only. The title is read-only (no click action), horizontally centred, in the upper band, with an
  optional one-line subtitle under it.

  | Style | Title | Lower band (thumb zone) |
  |---|---|---|
  | `Dashboard` | `titleLarge`, top edge about 12% down the expanded header; at least 24dp clear above the hero | the hero (display font), its caption, and the ‹ › period stepper at the end of the caption row |
  | `Standard` | `headlineLarge`, centred in the middle of the expanded area | none (a hero is ignored) |

  The top row holds only secondary `actions` (back, search, filter, overflow); add actions go in the
  FAB. On collapse the title cross-fades and scales into the 64dp bar's `titleLarge` title, and the
  subtitle and lower band fade out (three times faster than the collapse). While fully expanded
  only the large title exists in the tree, and while fully collapsed only the bar title, so
  assistive technology never reads the title twice.
- **Period.** `period: HeaderPeriod(onPrevious, onNext)` (Dashboard only) draws the ‹ › stepper
  ("Previous period" / "Next period" for TalkBack) and makes a horizontal swipe of more than 48dp on
  the *expanded* header step the period: toward the end of the line is previous. Vertical drags stay
  with the scroll. C4's `PeriodSwitcher` adds the directional slide on top of this.
- **Insets.** Edge-to-edge with `WindowInsets.safeDrawing`. `bottomBar` (nullable) sits in
  `imePadding()` and `BottomActionBar` pads by `safeDrawing` at the bottom and sides, so Save rides
  above the keyboard. With **no** bottom bar the list and the FAB are padded by the bottom inset:
  M3's `Scaffold` does that only when the bottom-bar slot emits nothing, so the slot is left empty,
  not filled with a wrapper. The scaffold reads raw `safeDrawing`: use **one per screen, at the
  root**, never under another Scaffold or layout that consumes insets.
- **Dashboard slots go together.** `hero`, `heroCaption` and `period` make up the lower band; without
  a `hero` the caption and stepper are not drawn and the swipe is inert. Below 560dp of window height
  (landscape, split screen) the header is always collapsed, so the Dashboard hero is **hidden** there,
  per the plan; the screen's figure must also be reachable in the list if it matters in landscape.
- **`BottomActionBar`.** One full-width primary `Button`, or an outlined secondary plus the primary
  on the end side. At least 56dp tall (the touch-target floor is 48dp). Built from `BottomBarAction(label, enabled, onClick)`.
- **Font scale.** Tested at 2.0x on the 393x851dp reference window with a subtitle: the title and
  subtitle end 67dp above the hero (Android's non-linear font scaling grows the 48sp hero only to
  about 56dp). A long hero shrinks to fit (DS5's `MoneyText`, below): `CollapsingHeaderScaffoldTest`
  proves the longest possible figure (`Paise.MAX_AMOUNT`) fits inside the header at 2.0x on 393dp and
  320dp windows, with the title still 24dp above it. On a short expandable window (about 560dp) at 2.0x
  the gap is not asserted.
- **Tests and insets.** Robolectric does express insets: the tests dispatch navigation-bar and IME
  insets to the Compose host with `ViewCompat.dispatchApplyWindowInsets` (`CollapsingHeaderScaffoldTest`).
  Status-bar and cutout behaviour on a real device is still an emulator check. The scaffold tests
  run on native graphics (`@GraphicsMode(NATIVE)`): the legacy mode measures every glyph about a
  pixel wide, which hides text wrapping.

## Content primitives (DS5)

All in `ui/common`. Each has a `*Samples.kt` file (fake data) that feeds its Studio preview, its
page in the debug catalogue and its goldens, so they cannot drift.

### `MoneyText`

`MoneyText(amount: Paise, semantic: MoneySemantic, modifier, size: MoneySize = Row, animate = false)`
is the only way an amount is drawn (plan §5.2).

- **Format.** `PaiseFormat.format` from `:core:money` returns a sign-free `₹1,23,456.78`. `MoneyText`
  prepends the sign (`moneySign`) and never reformats; there is no float anywhere in the path.
  U+2212 (a true minus, not a hyphen) for an expense or a negative balance, `+` for income, nothing for
  a transfer, a budget figure or a non-negative balance.
- **Negative amounts.** Transaction amounts are positive magnitudes and the direction is the
  semantic, so a negative `Paise` is rejected with `IllegalArgumentException` for every semantic except
  `Balance`. A wrong sign would misreport the money, so it fails closed instead of drawing it.
- **Tone** (`moneyTone(semantic, amount, inRow)` → `MoneyTone` → colour):

  | Semantic | Tone |
  |---|---|
  | `Income` | Positive (green), in totals and in rows |
  | `Expense` | Negative (red) in totals (`Hero`, `Large`); Neutral in rows (`Row`), owner decision Q2 |
  | `Transfer` | Neutral |
  | `Balance` | Neutral, except Negative when the amount is below zero (Q3) |
  | `Budget(UNDER / AT_THRESHOLD / OVER)` | Positive / Warning / Negative |

  Neutral is `onSurface`; the others are `PftTheme.money` tokens, never the accent. `BudgetState` is
  the UI's own three-value enum until the budget module (D1) exists; D4 maps core's `BudgetProgress`
  onto it.
- **Spoken.** The semantics description is "Expense, ₹250.00" (direction word first, from string
  resources: Income, Transfer, Balance, Negative balance, Under budget, Budget near its limit, Over
  budget), so colour is never the only cue.
- **Sizes.** `Hero` is `amountHero`, `Large` is `amountLarge`, `Row` is `amountRow`.
- **Hero shrink-to-fit.** The hero is one line at the largest of `HeroSizeSteps` (48, 40, 34, 28, 24,
  20, 16, 12sp; the plan's four plus four more) whose width fits the width it is given, measured with
  a `TextMeasurer` (so it tracks font scale and density) inside `BoxWithConstraints`. The line height
  scales with the size and the optical size stays at the 48sp display cut. The steps go below the plan's
  28sp because the plan's example amount (Rs 1e10) is shorter than the real limit `Paise.MAX_AMOUNT`
  (Rs 1e11, `₹1,00,00,00,00,000.00`): at 200% font scale that needs 16sp on 393dp and 12sp on 320dp
  (Android scales sp to dp non-linearly, so 12sp is still about 24dp tall there). If even 12sp does not
  fit (an extreme figure on a very narrow window) the text wraps rather than being cut off, because a
  truncated amount would misreport the money. The line break may fall right after the sign.
- **Rolling digits** (`animate = true`). Each character is its own `AnimatedContent` slot, keyed from
  the end of the string so the units digit keeps its column when the length changes; a rising digit
  slides in from below, a falling one from above, on `PftMotion.smooth` (`smoothOffset` for the slide).
  Assistive technology sees one text and one description on the container, and the text is correct as
  soon as the state changes, not after the animation. A custom font without `tnum` may shift slightly.
- **Never on `surfaceVariant`** (see Colour).

### `StatusChip`

`StatusChip(label, tone: MoneyTone)`: a pill with a required, non-blank label (so colour is never the
only cue). Colours (`chipColors`): the money container pairs for Positive, Negative and Warning
(`onXContainer` on `xContainer`, 4.5:1 pinned), and `surfaceContainerHighest` with `onSurface` for
Neutral. `labelMedium`, `extraLarge` shape, one merged semantics node. Not interactive.

### `GroupedSection`

`GroupedSection { row(onClick?) { ... } }`: the replacement for cards. Inset by the 20dp gutter,
`large` shape, filled with `surfaceContainer` (`groupedSectionColor`, never `surfaceVariant`), one
physical-pixel `outlineVariant` divider between rows, inset 16dp from the start (N rows give N-1
dividers). A row with `onClick` is clickable and at least 56dp tall. A rows lambda rather than a
composable slot lets the section own the dividers. M3 `ListItem` paints its own `surface` container,
so rows use `GroupedSectionDefaults.listItemColors()` (transparent). `Dp.Hairline` is not used for the
divider because it lays out at zero height, which cannot be measured and relies on an unclipped
canvas stroke.

### `EmptyState`

`EmptyState(message, glyph?, actionLabel?, onAction?)`: centred glyph slot (DS3's `CategoryGlyph` or a
symbol goes here), a `bodyLarge` message in `onSurfaceVariant`, and an optional tonal button. The label
and callback must be given together. It sits in a list's content; it does not fill the screen itself.

## Previews, the token sheet and screenshot goldens

`ui/theme/TokenSheet.kt` shows every token on one sheet (type scale, amount styles, shapes,
spacing ruler, money tokens on surfaces) with fake figures only. It is the **single source** for
three consumers, so they cannot drift:

- the Studio preview (`@PreviewLightDark`),
- the debug-only design catalogue (`src/debug/.../DesignCatalog.kt`, shown by `DevContent()`),
  which adds toggles for light/dark, hero font and font scale,
- the Roborazzi goldens (`TokenSheetScreenshotTest`).

Component goldens (DS4) come from `HeaderScreenshotTest`, using the same `StandardHeaderSample`,
`DashboardHeaderSample` and `BottomActionBarSamples` as the previews and the catalogue:
`header-<standard|dashboard>-<expanded|collapsed>-<light|dark>.png` and
`bottom-action-bar-<light|dark>.png`, full-screen 393x851dp (about 90 to 110 KB each; the collapsed
ones are captured after a real swipe). The debug catalogue has a page for each, plus a motion page.

Content goldens (DS5) come from `ContentScreenshotTest`, captured by the bounds of the `*Samples`
composable on `surface` (393dp wide): `money-text-<light|dark>[-2x].png` (the 2x ones at font scale
2.0), `status-chip-`, `grouped-section-` and `empty-state-<light|dark>.png`, 19 to 170 KB each. The
Dashboard header sample uses a `MoneyText` hero, so its two expanded goldens show the money red.

The sheet is split into `TokenSheetPages`, each short enough for one 393x851dp screen at 200% font
scale, so each page is one golden. Goldens are in `app/src/test/screenshots/`:
`token-sheet-<page>-<light|dark>-<1x|2x>.png`.

Screenshot rules:

- Native graphics (`@GraphicsMode(NATIVE)`), qualifiers `w393dp-h851dp-xxhdpi`, light and `+night`,
  font scale 1.0 and 2.0, brand colours (dynamic off) so goldens are deterministic.
- `roborazzi.test.verify=true` in `gradle.properties`: `./gradlew check` verifies against the
  committed goldens on both unit-test variants (debug and release).
- Record deliberately: `./gradlew :app:recordRoborazziDebug`, then review every changed PNG in the
  PR. Re-record whenever a token changes on purpose; a failing verify with no intended change is a
  regression.
- **Compare threshold:** none is configured, so Roborazzi 1.36.0's defaults apply. A pixel counts as
  changed only if its colour distance exceeds `maxDistance = 0.007` (differ's `SimpleImageComparator`,
  `RoborazziOptions.CompareOptions.DefaultImageComparator`), and the result must have **zero**
  changed pixels (`ThresholdValidator(0F)`, i.e. a `changeThreshold` of 0%). Source:
  `include-build/roborazzi-core/src/commonJvmMain/kotlin/com/github/takahirom/roborazzi/RoborazziOptions.kt`
  and `ThresholdValidator.kt` at tag 1.36.0. The plan's risk row (§10) asked for a small non-zero
  threshold; it was not needed on this machine (goldens verified identically on the debug and
  release variants), so it is left strict. If goldens flake across machines, pass
  `RoborazziOptions(compareOptions = CompareOptions(changeThreshold = 0.01f))` to `captureRoboImage`
  (1% of pixels) rather than re-recording blindly.
- Goldens use **fake data only**; reviewers reject real names, amounts or account numbers.
- Goldens are a review aid. Correctness is carried by the behaviour tests (`PftTypographyTest`,
  `PftShapesTest`, `PftSpacingTest`, `ColorContrastTest`, `CollapsingHeaderScaffoldTest`,
  `BottomActionBarTest`, `MoneyTextTest`, `StatusChipTest`, `GroupedSectionTest`, `EmptyStateTest`), not by pixels.
  Native-graphics output can differ across OS or Robolectric versions; record on one machine and
  re-record on upgrades.
- **Fit is asserted with line metrics, not `hasVisualOverflow`.** In Compose 1.7 `TextLayoutResult.hasVisualOverflow`
  is true for any wrap-content `Text` narrower than its maximum width and false when a `maxLines = 1`
  text is really cut off, so tests use `isShownInFull()` (`TextLayoutSupport.kt`: one line, whole text,
  not wider than its box).

## Third-party assets

| Asset | Source | Version / commit | Licence | Size | SHA-256 | Shipped as |
|---|---|---|---|---|---|---|
| Newsreader variable font `Newsreader[opsz,wght].ttf` (Production Type) | https://github.com/google/fonts/tree/8b0a1d0f5983c89bc2b93f1b5fb55f9e252744b5/ofl/newsreader (google/fonts `ofl/newsreader`; upstream project https://github.com/productiontype/Newsreader) | Version 1.003 per google/fonts commit `db2573d`; file unchanged at google/fonts commit `8b0a1d0` (2026-03-12) | SIL OFL 1.1, no Reserved Font Name | 451,664 B | `8a08d13f8a6c0d51be379a60af84f945f65369a67e509ee3c3bdcc421254d7c1` | `app/src/main/res/font/newsreader.ttf`, unmodified |
| Newsreader licence text `OFL.txt` | same directory, same commit | | | 4,394 B upstream | upstream `fdfad38143ec470553cae82a1e45320bdd1b9ec70415d37bd0171051d8a4ded8` | `app/src/main/assets/licenses/newsreader-OFL.txt` (shipped sha256 `9a1ec58d31446303d5c3aac2e31ddecdb0e9ec285d5008747e929096d03f0b60`: the text is identical to upstream apart from trailing whitespace trimmed and a final newline added by the repo's pre-commit hooks) |

`BundledFontTest` pins the font's size and SHA-256 and checks that the OFL text and the line
"Copyright 2020 The Newsreader Project Authors (http://github.com/productiontype/Newsreader)" ship.
OFL section 2 requires the licence to accompany every copy; AP1 adds an in-app licences viewer.
No Reserved Font Name is declared, so subsetting is allowed later if APK size matters; it is not
done, so provenance stays checkable by hash. To update the font, download from google/fonts into a
scratch directory, verify, and change the commit, size and hash here and in `BundledFontTest`
together.

## Build-time trust decisions

The Roborazzi Gradle plugin (1.36.0) puts a set of artifacts on the **buildscript classpath** that
nothing else in the build uses: `org.webjars:material-design-icons`, `org.webjars:materializecss`
and `org.webjars:webjars-locator-lite` (for its HTML report), `org.jspecify:jspecify`,
`kotlinx-serialization` (core and json), `kotlinx-io` (core and bytestring), `com.dropbox.differ`,
plus `junit` and `hamcrest-core`. They are locked in `buildscript-gradle.lockfile` and sha256-verified
in `gradle/verification-metadata.xml` (trust on first use), and they run only in the build, never in
the APK. Accepting them is a conscious decision made with the Roborazzi addition; the verification
file was not pruned. Re-vet them whenever Roborazzi is upgraded.

## Adding a token

1. Write the failing test (value in `PftTypographyTest`/`PftShapesTest`/`PftSpacingTest`, contrast
   in `ColorContrastTest`).
2. Add it under `ui/theme` with KDoc, and expose it through `PftTheme` if it is not an M3 token.
3. Add it to the token sheet, run `./gradlew :app:recordRoborazziDebug` and review the PNG diff.
4. Update this page and `app/README.md`.
