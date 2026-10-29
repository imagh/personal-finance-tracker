# Design system

Status: **DS1 (colour, `PftTheme`, window themes) and DS2 (type, shape, spacing, bundled serif,
screenshot harness, debug catalogue) are built.** Motion and the one-hand layout (DS4), icons and
the category palette (DS3) and the content primitives (DS5) are specified in
`docs/plans/2026-10-29-m1a-design-system.md` and are added to this page by their slices.

The plan is the spec; this page records what is built and how to extend it.

## Principles

"Cashew, but premium" (plan §3.1):

| Principle | In practice |
|---|---|
| Quiet surfaces, one accent | Neutral tonal surfaces. The dynamic accent colours interactive chrome only; money is never drawn in the accent |
| Type carries hierarchy, not boxes | Large figures, generous whitespace, inset grouped "islands" instead of shadowed cards |
| Colour where it signals | Money green/red only on amounts, meters and status, always with a sign or a word |
| Reach first | Content starts about 38% down; primary actions at the bottom (DS4) |
| Continuity | Springs and shared-element transitions (DS4) |

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
WCAG 4.5:1. Never draw money text on `surfaceVariant`.

## Previews, the token sheet and screenshot goldens

`ui/theme/TokenSheet.kt` shows every token on one sheet (type scale, amount styles, shapes,
spacing ruler, money tokens on surfaces) with fake figures only. It is the **single source** for
three consumers, so they cannot drift:

- the Studio preview (`@PreviewLightDark`),
- the debug-only design catalogue (`src/debug/.../DesignCatalog.kt`, shown by `DevContent()`),
  which adds toggles for light/dark, hero font and font scale,
- the Roborazzi goldens (`TokenSheetScreenshotTest`).

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
- Goldens use **fake data only**; reviewers reject real names, amounts or account numbers.
- Goldens are a review aid. Correctness is carried by the behaviour tests (`PftTypographyTest`,
  `PftShapesTest`, `PftSpacingTest`, `ColorContrastTest`), not by pixels. Native-graphics output
  can differ across OS or Robolectric versions; record on one machine and re-record on upgrades.

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

## Adding a token

1. Write the failing test (value in `PftTypographyTest`/`PftShapesTest`/`PftSpacingTest`, contrast
   in `ColorContrastTest`).
2. Add it under `ui/theme` with KDoc, and expose it through `PftTheme` if it is not an M3 token.
3. Add it to the token sheet, run `./gradlew :app:recordRoborazziDebug` and review the PNG diff.
4. Update this page and `app/README.md`.
