# Plan: M1a premium look (DS6): curated palettes, tonal surfaces, header glow, icon-only header actions, type pairing, bundled sans

| Field       | Value |
|-------------|-------|
| Date        | 2026-10-30 |
| Author      | architect |
| Status      | Draft. Every owner decision from 2026-10-30 is folded in (§1); awaiting approval of this document. **DS6f spike results (2026-10-30) are folded into §3.6–§3.7, §6.1, §12 and §13**: Google Sans Flex and DM Sans are out of the candidate set |
| Related ACs | UI-1, UI-2 (revised), UI-3 (revised), UI-4, UI-5, UI-7, UI-8 (revised); new UI-11..UI-18 (§1); SEC-2, SEC-3 |
| Plan owner  | akashg (repo owner) |

Foundation: `docs/plans/2026-10-29-m1a-design-system.md` (abbreviated **DS** below). DS1, DS2 and DS4 are merged into `feat/m1a-foundation` (at `cd4bee5`). DS5 is open as PR #20, DS3 is not started, and A4a (Room v1, which holds `app_setting`) is open as PR #19. This plan adds a **DS6 slice** to `feat/m1a-foundation`. It lands **before A5**, so no screen is ever restyled.

Interactive mockup: `docs/plans/2026-10-30-m1a-premium-look-mockups.html`. Open it in a browser. It uses fake data only, and it fetches Google Fonts **in the browser only**; the app never does. A live copy is at https://claude.ai/artifact/NrWC79sjYPkSAUH7ddFcs8. The mockup's controls start at the app defaults: Graphite in light and dark, Figtree, tonal canvas, glow off. Its "Today" palette (the current ink-indigo look) is there **for comparison only** and is not an app option.

---

## 1. Requirement

**Need.** The owner wants the app to look clearly more premium than the DS1–DS4 result, which has a saturated indigo accent, a single flat surface, a system sans and serif on the hero only. The change must land before A5 so it is a token change, not a retrofit across screens.

**Owner decisions (final, 2026-10-30):**

| # | Decision |
|---|---|
| O1 | **Background: "tonal canvas + cards".** Each card (island) is a slightly different tonal step of the canvas hue, **never white**, with a 1px hairline edge. In dark mode it also gets a faint inset top highlight. A **flat** option (today's single surface) stays as a setting. |
| O2 | **Header glow:** a soft radial tint of the palette's wash colour behind the header. It fades out **before the hero row**, so money is never drawn on a tinted area, and its strength is capped. It is a setting, **off by default**. |
| O3 | **Palettes:** four curated palettes generated offline, each with light and dark: **GRAPHITE** (neutral + platinum), **MIDNIGHT** (navy + brass), **IVORY** (warm paper + ink blue) and **AUBERGINE** (deep plum + champagne). The light and dark palettes are chosen independently. |
| O4 | **Defaults:** `lightPalette = GRAPHITE`, `darkPalette = GRAPHITE`, `sansFont = FIGTREE`, `typePairing = HERO_ONLY`, `background = TONAL`, `headerGlow = false`. **Material You dynamic colour stays available but is no longer the default** (this revises UI-8). |
| O5 | **Type pairing setting:** `HERO_ONLY` (default, today's look) or `EDITORIAL` (§3.6). An all-serif pairing was evaluated in the mockup and liked, but the owner deferred it (2026-10-30, second round). |
| O6 | **Sans font setting:** `FIGTREE` (default), `PLUS_JAKARTA_SANS` or `SYSTEM`. All bundled fonts are OFL, with sizes measured. One UI Sans and Circular / Spotify Mix are proprietary and **cannot be bundled**. **Amended by DS6f (2026-10-30):** the original list also had `GOOGLE_SANS_FLEX` (owner: "drop for now"; 3.9 MB) and `DM_SANS` (no tabular figures); see §3.7. |
| O7 | **Header actions are icon-only:** search, filter and the others are 24dp icons with content descriptions and 48dp touch targets, with no text labels. |
| O8 | **All of these are user settings** (AP1): `lightPalette`, `darkPalette`, `sansFont`, `typePairing`, `background` and `headerGlow`, plus the existing theme mode, dynamic colour and display font. An unknown stored value reads as the default. |

**ACs.** New and revised criteria, with test mapping in §7:

| ID | Requirement |
|---|---|
| UI-2 (revised) | The default text font is the bundled **Figtree**. The system font and one other bundled sans font (Plus Jakarta Sans) are options (DS6f: Google Sans Flex and DM Sans were dropped). Custom import (AP2) is unchanged. |
| UI-3 (revised) | The serif sets the hero figure by default (Q10). The type-pairing setting can also extend it to the large amount and the titles (`EDITORIAL`). |
| UI-8 (revised) | The default accent is the curated **Graphite** palette. Material You dynamic colour is an opt-in. |
| UI-11 | Four curated palettes, light and dark chosen independently, all passing the DS contrast rules. The accent never reads as income or expense. |
| UI-12 | Tonal canvas with tonal, never white, islands and a hairline edge (default), or flat. |
| UI-13 | Optional header glow, off by default, capped, never under money. |
| UI-14 | Header actions are icon-only, labelled for TalkBack, with 48dp touch targets. |
| UI-15 | The type pairing (hero only, editorial) with Newsreader's optical size set per role. |
| UI-16 | The sans choice (three options: Figtree, Plus Jakarta Sans, system; DS6f). Every bundled font is OFL with its licence shipped, and money digits are tabular in every bundled font. |
| UI-17 | Every bundled font's size is measured, recorded and pinned by a test, and all of `res/font` together is at most 3 MiB (owner-confirmed 2026-10-30). |
| UI-18 | No visible first-frame flash with any palette or background: the window colour is within 8 tones of the first Compose frame, and exact after it. |

**Done means:**
1. `PftTheme` resolves the chosen palette per mode (or dynamic colour, on opt-in), and the default is Graphite.
2. The surface style (tonal or flat), the header glow and the type pairing come from `ThemePrefs`.
3. The two sans fonts (Figtree, Plus Jakarta Sans) are bundled with their licences and pinned hashes and sizes.
4. Header actions are icon-only.
5. Every palette passes the contrast and hue tests in both modes.
6. The debug catalogue toggles every option.
7. Goldens are re-recorded and reviewed.
8. AP1's additions are specified (§6.3).
9. It applies app-wide through the theme (§3.9), and `ThemeLiteralScanTest` keeps hardcoded colours and fonts out of screens.
10. `./gradlew check` is green after every slice.

**Out of scope:**
- The appearance settings screen and the persistence code (AP1; this plan defines the values and the contract only).
- Custom font import (AP2).
- Italic Newsreader (it is a separate file, not an axis).
- More palettes, or a free accent picker.
- Any change to the money colours.
- Screens (A5 onward).

## 2. Current state

**Theme code (`app/src/main/kotlin/dev/imagh/pft/app/ui/theme/`):**
- `PftTheme.kt`:
  - `:26-36`: `resolveColorScheme(dark, dynamic, context)` chooses dynamic, then `BrandColors`.
  - `:53-58`: resolves dark or light.
  - `:59`: the colour scheme.
  - `:61`: the text family, hard-wired to `FontFamily.Default`.
  - `:62-64`: the typography, remembered.
  - `:65`: provides `LocalMoneyColors` and `LocalPftTypography`.
  - `:77`: `LocalPftTypography` is a `staticCompositionLocalOf`.
  - `:80-104`: the `PftTheme` accessor object. **This is the mechanism to reuse** for surfaces.
- `ThemePrefs.kt:43-53`: `ThemePrefs(mode, dynamicColor = true, displayFont)`. `:46` holds the default this plan flips. `DisplayFont` is at `:22-31`.
- `BrandColors.kt:22-104`: the ink-indigo scheme (seed `#4A55B8`). Its KDoc at `:8-21` records the DS1 generation procedure, which this plan reuses for every palette.
- `PftTypography.kt`:
  - `:42-47`: the hero size, spacing and optical-size constants.
  - `:53-65`: `NewsreaderFamily`, pinned to `wght 400`, `opsz 48`.
  - `:68-76`: `displayFamily`.
  - `:79-109`: `pftType`.
  - `:117-136`: `pftMaterialTypography(text)`, every M3 role in one family.
- `MainActivity.kt`:
  - `:21`: `FLAG_SECURE`, which stays first.
  - `:22`: `enableEdgeToEdge()`.
  - `:24-28`: `setContent { PftTheme { Surface(fillMaxSize) { DevContent() } } }`. The root `Surface` paints `colorScheme.surface`.

**Window resources:**
- `res/values/colors.xml:5` sets `#FCF8FD` and `res/values-night/colors.xml:4` sets `#131316`, both brand `surface`.
- `res/values{,-night}/themes.xml:7,10` use them for `windowBackground` and the splash.

**Header (`ui/common/CollapsingHeaderScaffold.kt`):**
- `:186`: `actions: @Composable RowScope.() -> Unit`, a free slot.
- `:226`: `Scaffold(containerColor = colorScheme.surface)`.
- `:319-322`: the header `.background(colorScheme.surface)`.
- `:337-340`: the actions row.
- `:584-606`: `PeriodStepper` draws text glyphs ‹ › inside `IconButton` (a DS4 deviation, "until DS3's icons").
- `HeaderSamples.kt:34-38,51-54` uses **text** `TextButton("Back")`, `TextButton("Search")` and `TextButton("Filter")` as actions.
- `BottomActionBar.kt:46` paints `colorScheme.surface`.

**Tests to extend:**
- `ColorContrastTest.kt`:
  - `:41-44`: the grey sweep, dark tones `4..24`.
  - `:46`: brand scheme only.
  - `:60-72`: the money sweep over `surfaceRoles()`.
  - `:108-113`: brand tone-ladder guard.
- `ColorSchemeTestSupport.kt:56-70`: `surfaceRoles()`, which has M3 roles only.
- `WindowThemeTest.kt:42-52`: the window background equals the brand `surface`.
- `ThemeResolutionTest.kt:37-60`: brand fallback and ink-indigo pin.
- `PftTypographyTest.kt`:
  - `:78-84`: hero `wght 400`, `opsz 48`.
  - `:86-91`: every M3 role is `FontFamily.Default`.
- `BundledFontTest.kt:28-55`: Newsreader size, hash and OFL.
- 48 references to `BrandColors` or `dynamicColor` across 18 files under `app/src`, not counting `BrandColors.kt` itself. 8 of those files name `BrandColors`; the rest are mostly `ThemePrefs(dynamicColor = false)` in tests and samples.
- 30 goldens in `app/src/test/screenshots/`.

**DS5 (open, PR #20) touches what this plan changes:**
- `GroupedSection.kt:35`: `groupedSectionColor(scheme) = scheme.surfaceContainer`, with dividers in `outlineVariant`.
- `StatusChip` takes its colours from a pure `chipColors(tone, money, scheme)`, and a blank label is rejected.
- DS5 also edits the DS plan (§6.1 and §6.3) and the top of `docs/architecture/design-system.md`. This plan's doc edits avoid those hunks.

**Settings storage:**
- `core/ledger/.../settings/SettingsService.kt:22-72` is key/value text in the `app_setting` table (A4a), audited on change, a no-op when the value is unchanged, and throws `NotInitialised` before first-run setup.
- **Finding:** keys must match `[a-z][a-z0-9_]*` (`:70`), at most 64 characters (`LedgerLimits.kt:22`); values are at most 256 characters (`:25`). **DS §4's AP1 keys (`ui.theme_mode`, `ui.font.display`, ...) contain dots and would throw `InvalidSettingKey`.** §4 below gives valid keys, and the DS plan is annotated.
- New settings are new rows, so **no schema change or migration** is needed.

**Facts verified while planning:**
- **Newsreader, read from the bundled file** (`res/font/newsreader.ttf`, 451,664 B) with a standard-library cmap/GSUB/fvar/hmtx probe:
  - It has U+20B9 ₹ (glyph 559) and U+2212 − (glyph 619).
  - GSUB has `tnum` and `pnum` but **no `lnum`**, so the `lnum` request in `PftTypography.kt:91,99` is a harmless no-op.
  - The default figures are already equal-width (1100 units each).
  - The axes are `wght` 200–800 (default 400) and `opsz` 6–72 (**default 18**).
  - So tabular figures and the ₹ glyph in Newsreader are **confirmed statically**. What was still unconfirmed was how they *render* on the device; DS6f confirmed it on the emulator (§3.7, §13).
- **Hues** (CAM16 / HCT-style hue computed from the hex values; CIELAB hue in brackets):

  | Token | Light | Dark |
  |---|---|---|
  | Money green | ≈158° [154°] | ≈160° [156°] |
  | Money red | ≈26° [36°] | ≈24° [30°] |
  | **Warning amber** | **≈72° [74°]** | **≈86° [85°]** |

  The warning amber is not the ≈40° the brief assumed.
  - Brass (MIDNIGHT dark primary `#D4B572`) is ≈87°, **about 1° from dark warning amber**.
  - Champagne (AUBERGINE dark primary `#D9BE8A`) is ≈85°, also about 1°.
  - MIDNIGHT's light brass `#7D5F26` is ≈81°, 7–9° from amber.
- **Tones and contrast of the mockup tokens** (computed):
  - **Canvases.** Light canvases are tone 90.7–92.3; Graphite is 91.2. Dark canvases are tone **2.7–5.1**, **below the dark sweep's floor of tone 4** (`ColorContrastTest.kt:42`).
  - **Flat surfaces** are tone 95.6–96.6 (light) and 4.4–7.7 (dark).
  - **Islands** have about 1.06–1.11:1 contrast to their canvas, and hairlines about 1.2–1.3:1. Both are decorative, as intended.
  - **Text clears its threshold everywhere:**
    - `on` and `onvar` clear 4.5:1 on every canvas, island and flat surface (lowest: `onvar` on Ivory light canvas, 5.85).
    - Money text clears 4.5:1 on all of them (lowest: positive on Ivory light canvas, 5.60).
  - **Wash (glow) top**, the wash blended at 72% over the canvas:
    - Ivory light is tone 86.5 and Aubergine light 86.9, **just below the light band floor of 87**.
    - Contrast still holds there: `onvar` 5.23 and positive money 5.01 on Ivory light.
- **The mockup differs from the spec in two places.** Neither is adopted:
  1. Its money **container** values (`--warn-c`, `--pos-c` and so on) differ slightly from `MoneyColors`. The app keeps `MoneyColors` unchanged.
  2. Its budget meter is filled with `primary`. In the app, D4's `ProgressMeter` is filled with the **money tone** of the budget state (DS §3.7), never the accent, because brass and champagne sit next to warning amber.
- **Pre-commit:** `.pre-commit-config.yaml:31` runs `check-added-large-files`, which rejects files over 500 KB. Bundled fonts larger than that need a decision (DS6f measured: none of the kept fonts is over it, so no exception is needed; §3.7).
- **The R8 guard** is `mustBeRemoved` in `app/build.gradle.kts:109-115`. New sample files go there.

## 3. Design & decision

### 3.1 Palettes (DS6a, DS6b)

**Shape (no new abstraction):**

```kotlin
/** One curated palette in one mode: the M3 roles plus the tones M3 has no role for. */
@Immutable data class PaletteColors(
    val scheme: ColorScheme,   // all M3 roles (DS6a)
    val canvas: Color,         // tonal background (DS6c)
    val island: Color,         // tonal card fill (DS6c)
    val wash: Color,           // header glow tint (DS6d)
)
/** The palettes a user can pick. AP1 persists the name: never rename or remove an entry. */
enum class ThemePalette { GRAPHITE, MIDNIGHT, IVORY, AUBERGINE }
internal fun ThemePalette.colors(dark: Boolean): PaletteColors
```

- Each palette is one file (`ui/theme/GraphitePalette.kt` and so on), an `object` with `light` and `dark` `PaletteColors`, written in the same style as today's `BrandColors.kt`.
- Each field arrives with the slice that first reads it (DS2's YAGNI precedent).

**Mockup token to role mapping.** The values are the CSS blocks `.phone[data-pal=…]` in the mockup.

| Mockup | App | Role |
|---|---|---|
| `flat` | `scheme.surface` = `scheme.background` | FLAT screen background (today's mapping) |
| `flatlow` | `scheme.surfaceContainer` | FLAT island (DS5's `groupedSectionColor`) |
| `on` / `onvar` | `scheme.onSurface` / `onSurfaceVariant` | text |
| `line` | `scheme.outlineVariant` | hairline: island edge and dividers |
| `primary` | `scheme.primary` | accent for interactive chrome only |
| (derived) | `scheme.onPrimary` | text and icons on `primary` |
| `canvas`, `island` | `PaletteColors.canvas`, `.island` | TONAL |
| `pc` | `PaletteColors.wash` | glow tint (`onpc` is unused: no text sits on a wash fill) |

**Generation procedure.** This extends the DS1 procedure (`BrandColors.kt:8-21`), and the provenance goes in each palette's KDoc.
1. In a scratch directory, build the scheme with `@material/material-color-utilities` 0.3.0, the version DS1 used. It is not a project dependency.
   - The **primary palette** comes from the mockup `primary` of that mode.
   - The **neutral and neutral-variant palettes** come from the canvas hue at the mockup's low chroma.
   - Take the M3 roles at the standard tones, and fill the newer surface roles at the M3 1.3 tone ladder as DS1 did. [Verify the 0.3.0 API for building a scheme from custom palettes; if it cannot, build `TonalPalette`s and read the role tones directly.]
2. **Pin the owner-approved mockup values** over the generated roles for the tokens in the table above. The owner approved what they saw. Each override is listed in the KDoc.
3. `onPrimary` is the generated value: the primary palette's tone 100 in light and tone 20 in dark.
4. Run the tests in §7. If a pinned value fails a test, adjust its **tone**, never its hue, and record the change in the KDoc and the PR.

**Default and fallback.**
- **`BrandColors` (ink indigo) is retired:** deleted in DS6a, not kept as a fallback.
  - With dynamic colour off (now the default), the chosen palette *is* the scheme.
  - An unknown stored palette reads as `GRAPHITE`.
  - So no code path needs a second fallback, and an unused palette would be dead code.
- This supersedes DS Q5 (`#4A55B8`) and DS §3.2's "accent override, later": the curated palettes *are* that override.
- Goldens use the default Graphite, so they stay deterministic.

**Dynamic colour (opt-in).**
- `ThemePrefs.dynamicColor` defaults to `false`. When it is on, the **scheme** is `dynamic{Light,Dark}ColorScheme`, and the palette fields are ignored.
- The tones M3 has no role for are derived from the dynamic scheme by a pure function:

  | Token | Light | Dark |
  |---|---|---|
  | `canvas` | `surfaceContainerHigh` (tone 92) | `surfaceContainerLowest` (tone 4) |
  | `island` | `surfaceContainerLow` (tone 96) | `surfaceContainerLow` (tone 10) |
  | `wash` | `primaryContainer` | `primaryContainer` |

- The money colours are unchanged and independent of every palette and of dynamic colour, as before.

**Accent safety (UI-11).**
- **Hue rule:** each palette's `primary` must be **at least 40° of CIELAB hue** from the money `positive` and `negative` of the same mode (`MIN_ACCENT_HUE_DISTANCE`).
  - The smallest measured distance is MIDNIGHT light brass vs red, at 45°.
  - CIELAB is used because a test can compute it in a few lines with no library.
- **Warning amber is exempt from the hue rule, deliberately.** The owner chose brass and champagne, which sit 1–9° from amber. Hue therefore cannot separate them, so other cues must:
  1. **Labels.** `StatusChip` already rejects a blank label (DS5), so "Unverified" and "80%" are always words.
  2. **Fixed colours.** A warning pill's colours come from `MoneyColors` and are **identical under every palette** (tested). Choosing a palette can never make a pill brass.
  3. **Where the accent may appear.** It is only on interactive chrome (FAB, selected navigation item, buttons, switches, focus). It is never on status or meters: D4's `ProgressMeter` uses the money tone.
  4. **Manual check.** In MIDNIGHT dark and AUBERGINE dark, an Unverified pill next to a selected navigation item and the FAB must read as a status, not an accent (§11).

### 3.2 Surfaces: tonal or flat (DS6c)

```kotlin
enum class BackgroundStyle { TONAL, FLAT }      // persisted by AP1
@Immutable data class PftSurfaces(
    val canvas: Color,          // screen, header, bottom bar background
    val island: Color,          // GroupedSection and card fill
    val islandEdge: Color,      // 1 physical px; Transparent when FLAT
    val islandHighlight: Color, // dark TONAL only: inset top line; else Transparent
    val glow: Color?,           // wash when headerGlow is on (DS6d), else null
)
internal fun pftSurfaces(p: PaletteColors, style: BackgroundStyle, glow: Boolean, dark: Boolean): PftSurfaces
```

| Style | canvas | island | islandEdge | islandHighlight |
|---|---|---|---|---|
| `TONAL` (default) | `p.canvas` | `p.island` | `p.scheme.outlineVariant` | dark: white at 4.5% alpha (mockup `rgba(255,255,255,.045)`), light: transparent |
| `FLAT` | `p.scheme.surface` | `p.scheme.surfaceContainer` | transparent | transparent |

- `FLAT` reproduces today's look exactly, in the chosen palette.
- **Provided** by `LocalPftSurfaces` (a `staticCompositionLocalOf`, like `LocalMoneyColors`) and read as `PftTheme.surfaces`.
  - A static local means changing the palette recomposes the whole tree. That is accepted, because it happens only when the user changes a setting.
- **Consumers, changed in DS6c:**
  - `CollapsingHeaderScaffold` `:226` and `:319-322` use `canvas`.
  - `BottomActionBar` `:46` uses `canvas`.
  - DS5's `GroupedSection` uses `island`, plus `islandEdge` as a border and `islandHighlight`. Use a vertical-gradient border brush or equivalent, as the developer chooses.
  - `MainActivity`'s root `Surface` uses `canvas`.
  - A later `NavigationBar` (A5/C4) uses `island` for its container, per the mockup.

### 3.3 Window and first frame (UI-18, DS6c)

The window colour before Compose draws comes from resources and cannot know the user's palette. The plan is three parts:
1. **Resources follow the defaults.** `pft_window_background` becomes Graphite's `canvas` in `values` and `values-night` (DS6c); DS6a sets it to Graphite's `surface` first.
2. **Every alternative stays close.** For every palette, both `canvas` and `surface` (flat), and the derived dynamic canvas and surface, are **within 8 tones** (`WINDOW_FLASH_MAX_TONE_DELTA`) of the resource colour in the same mode.
   - Measured worst cases: MIDNIGHT light flat is +5.3, and dynamic light flat (`surface`, tone 98) is about +6.8.
   - So a palette change can at most shift the first frame by a barely visible step, never light to dark.
   - Light vs dark against a *forced* mode is AP1's existing `setApplicationNightMode` item (DS §6.2).
3. **Runtime sync.** `SyncWindowBackground(window)` in `ui/theme/WindowBackground.kt` is a composable `SideEffect`. It sets `window.setBackgroundDrawable(ColorDrawable(PftTheme.surfaces.canvas.toArgb()))` whenever the canvas changes. `MainActivity` calls it once inside `PftTheme`.
   - From then on, the window colour is exact: under predictive back, IME gaps and configuration changes.
   - AP1's at-most-500 ms first-frame hold (DS §5.1) means the first Compose frame already uses the stored prefs.
   - **`FLAG_SECURE` stays the first line of `onCreate`**, followed by `enableEdgeToEdge()`. Nothing moves above them.

### 3.4 Header glow (UI-13, DS6d)

- **Pure functions** in `ui/common/HeaderGlow.kt`:
  - `glowAlpha(distance: Float, end: Float): Float` returns `MAX_GLOW_ALPHA` (0.72, the mockup's top stop) at distance 0. It is non-increasing (smooth falloff through about 0.28 at 38% of `end`, as in the mockup) and **exactly 0 for `distance ≥ end`**.
  - `glowEnd(heroTop: Float?, bodyHeight: Float): Float` returns `heroTop − 24dp`, clamped at 0, when a hero exists. Otherwise it returns `0.6 × bodyHeight` (Standard style).
  - The glow's total strength is multiplied by `1 − collapsedFraction`, so the 64dp bar is plain canvas.
- **Drawing:**
  - A `Modifier.drawBehind` on the header draws a `Brush.radialGradient` of `PftTheme.surfaces.glow`, centred at top-centre with radius `glowEnd`. Its stops come from `glowAlpha`.
  - Because it is a circle centred on the top edge, every point at or below `glowEnd`'s depth is untinted. **The hero row is always below it.**
  - `HeaderPolicy` writes the measured hero top into a float state that the draw lambda reads, so a scroll frame redraws but never recomposes. This is the same rule as DS4's layout-phase reads.
- **Contrast:** the title and actions sit on the tint. `onSurface`, `onSurfaceVariant` and (defensively) the money tokens are swept against `wash` composited at `MAX_GLOW_ALPHA` over `canvas`, for every palette, both modes and the dynamic scheme.
- Glow off (the default) means `surfaces.glow == null` and nothing is drawn.

### 3.5 Icon-only header actions (UI-14, DS6e; needs DS3)

- **`HeaderIconButton(symbol: PftSymbol, label: String, onClick)`** goes in `ui/common`. It is M3 `IconButton` (48dp) around an `Icon(painterResource(symbol.res), contentDescription = label)` at 24dp, tinted `onSurfaceVariant`. A blank `label` throws `IllegalArgumentException`.
  - **Why a component:** it is not a restyle wrapper (the DS rule). It enforces a contract the owner set: no text, a mandatory TalkBack label, and a registry symbol. It has more than two named consumers: Home search and filter, list back, budget-detail overflow, and settings back.
- The `actions` slot stays a slot. An overflow `DropdownMenu` needs a `Box` anchor next to its button, so a typed list would not fit. The rule "only `HeaderIconButton`s in `actions`" is written in the scaffold KDoc and `design-system.md`. Samples and catalogue use it, and tests check the samples.
- `PeriodStepper` (`:584-606`) swaps its ‹ › text glyphs for DS3's chevron symbols. That closes the DS4 deviation.
- **Dependency:** DS3's `PftSymbol` and its chrome keys (search, filter, back, overflow, chevrons). DS3 is already a pre-A5 slice.

### 3.6 Type pairing and optical size per role (UI-15, DS6g)

```kotlin
enum class TypePairing { HERO_ONLY, EDITORIAL }   // persisted by AP1
```

The pairing chooses **which roles use the display family**. `displayFont` (unchanged) chooses **what** the display family is. So `displayFont = TEXT` with `EDITORIAL` is all sans, and that is documented.

| Pairing | Display family (Newsreader by default) | Text family (sans) |
|---|---|---|
| `HERO_ONLY` (default) | `amountHero` | everything else (today's look) |
| `EDITORIAL` | `amountHero`, `amountLarge`, `display*`, `headline*`, `titleLarge`, `titleMedium` (screen titles, the bar title, section titles) | `titleSmall`, `body*`, `label*`, `amountRow` |

- `titleSmall` stays sans in `EDITORIAL` because M3 uses it for tab labels, which are controls.
- **Optical size per role.** `newsreader(weight: FontWeight, size: TextUnit): FontFamily` replaces the single `NewsreaderFamily` (`PftTypography.kt:53-65`).
  - It sets `FontVariation.Settings(weight, FontStyle.Normal, FontVariation.opticalSizing(size.coerceIn(6.sp, 72.sp)))`. `EDITORIAL`'s serif roles run from `titleMedium` at 16sp to `displayLarge` at 57sp, so each gets its own cut instead of today's fixed 48.
  - Only what `EDITORIAL` needs is built: the function is called for the serif roles in the table above, and nothing else.
  - The variation applies only to the bundled serif. `SYSTEM_SERIF` and `TEXT` get no variation settings.
- **Hero weight stays 400** (owner, 2026-10-30, after the DS6f device check: "400"). The mockup's 350 is not adopted. For the record, variable weight does render on the device: the same hero amount measured 634, 638, 642 and 660 px wide at `wght` 300, 350, 400 and 500 (opsz 48), so 350 was distinct from 400; the owner chose 400.
- `tnum` stays on all three amount roles in every pairing. Newsreader's default figures are already tabular, and `tnum` keeps that true for other fonts.
- **All serif is deferred.** It was evaluated in the mockup, liked, and deferred by the owner (2026-10-30). It can return later as a third `TypePairing` value **without a migration**, because an unknown stored value reads as the default and a new value is simply added.
- **No body-size gate.** With all serif deferred, no body or label text is ever set in Newsreader, so the earlier gate on its 12–16sp text cut is dropped. DS6f looked at the smallest `EDITORIAL` serif role (`titleMedium`, 16sp). **Result (owner, 2026-10-30): "500 w for title looks good. 48sp is good for title"; asked whether that meant Newsreader or Figtree at w500, "both"; asked what "48sp" referred to, "Hero amount".** So the 16sp serif cut is not weak, and **`titleMedium` stays in the serif list in `EDITORIAL`, at `wght` 500 with `opsz` 16.** The hero at 48sp is confirmed.

### 3.7 Bundled sans fonts (UI-16, UI-17; DS6f spike done, then DS6h, DS6i)

```kotlin
enum class SansFont { FIGTREE, PLUS_JAKARTA_SANS, SYSTEM }  // persisted by AP1; AP2 adds CUSTOM
```

DS6f (2026-10-30) measured the four candidates. **Google Sans Flex and DM Sans are not in the enum.** Both can come back later as new values with no migration, because an unknown stored value reads as the default (§6.3).

- **Shipping:** each family ships **unmodified** from google/fonts at the pinned commit below into `res/font/`, as the **variable** file (`wght` only is used: 400, 500 and 600).
  - Each has an OFL text in `assets/licenses/<family>-OFL.txt`.
  - Each has a row in the `design-system.md` third-party table (source, commit, licence, Reserved Font Name, size, sha256). Those rows are added by the slice that bundles the font (DS6h, DS6i), not by DS6f: nothing is bundled yet.
  - `BundledFontTest` pins each size and hash.
- **Only the chosen family is loaded:** Compose loads a `ResourceFont` when it is first used.
- **`SYSTEM`** is `FontFamily.Default`: Roboto or Google Sans on Pixel, and the One UI font on Samsung [still to verify on the owner's phone, DS §3.3]. Tabular figures there depend on the device font; that is accepted and documented, like custom fonts.
- **Proprietary fonts:** One UI Sans (Samsung) and Circular / Spotify Mix are proprietary and **cannot be bundled**. Figtree and Plus Jakarta Sans cover the Circular feel. Google Sans Flex (OFL, November 2025) is the legitimate Google-Sans-like option but is out for now (below).

**DS6f results.** All files come from `github.com/google/fonts` at the **single pinned commit `6c1dbe27dcf7d817d4b62cec033ba77fa8e2a5ec`** (2026-10-30), directory `ofl/<family>/`. They were read with a standard-library probe (cmap, GSUB, fvar, hmtx), the same way as the Newsreader file in §2. Every licence is SIL OFL 1.1, and **none declares a Reserved Font Name** (the copyright line has no "with Reserved Font Name" clause).

| | Figtree | Plus Jakarta Sans | DM Sans | Google Sans Flex |
|---|---|---|---|---|
| File | `Figtree[wght].ttf` | `PlusJakartaSans[wght].ttf` | `DMSans[opsz,wght].ttf` | `GoogleSansFlex[ROND,opsz,slnt,wdth,wght].ttf` |
| Copyright line | 2022 The Figtree Project Authors | 2020 The Plus Jakarta Sans Project Authors | 2014 The DM Sans Project Authors | 2015 The Google Sans Flex Authors |
| Licence / RFN | OFL 1.1 / none | OFL 1.1 / none | OFL 1.1 / none | OFL 1.1 / none (`TRADEMARKS.md`: the Google marks are not licensed) |
| `fvar` axes | `wght` 300-900, **default 300** | `wght` 200-800, default 400 | `opsz` 9-40, `wght` 100-1000, default 400 | **five axes:** `opsz` 1-144 (default 18), `wdth` 25-151, `wght` 1-1000, `ROND` 0-100, `slnt` -10-0 |
| 400, 500, 600 in range | yes | yes | yes | yes |
| `tnum` (GSUB) | yes; digits proportional by default, `tnum` makes all ten 620/1000 equal | yes; proportional by default, `tnum` gives 600/1000 | **no `tnum`, no `pnum`; default digits proportional (342 to 656)** | yes; proportional by default, `tnum` gives 1290/2000 (also `lnum`, `zero`) |
| `lnum` | no | no | no | yes |
| ₹ U+20B9 / − U+2212 in cmap | yes / yes | yes / yes | yes / yes | yes / yes |
| Variable file, bytes | 62,712 | 176,288 | 240,164 | 3,910,784 |
| Static 400+500+600, bytes (**indicative**) | 120,376 | 289,720 | not measured | 393,000 (wght-only variable instance: 243,440) |
| Chosen form | **variable** | **variable** | n/a | n/a |
| sha256 of the file | `26ad3db9b31ff7dde67a91ff515d022d2f495cd506590699cf264f0bfe6fb714` | `89b3fb38aa0d275d7a731d0d817a4f1622b316b4d7fbdedcf02ee9099ff68bc8` | `8cd08d97e89c24d0aa92edd2f0f4c8ee6195eee9b7c9f154865a58b02f0c1c0d` | `a9ac770798857d5e8ac593ced42b22e40a7c08cec98319f92b0eccc2783a77ba` |
| `OFL.txt` upstream sha256 | `140d37233e7f3ce7313798befa9600893bcceaf41a55fa0fa5ad52f7f657a268` | `995c7199cab65954f545996326755daee7b63cc6b42b06c13da1f9502ab08a99` | `9af36190332437f5ecd09974de43c1f7c77a310a996cdd8ceb25628b458840e1` | `fc13d69f63e36d284b6e383d4d1463d8ad404f0aa065e57cf961252d51063137` |
| Release APK delta (compressed entry) | +35,171 B | +79,125 B | +108,937 B | +1,809,451 B |
| **Decision** | **KEEP** | **KEEP** | **DROP:** fails the hard tabular-figures requirement | **DROP "for now"** (owner): 3.9 MB alone exceeds the 3 MiB budget |

- **Statics are indicative only.** google/fonts has no static files for these families at the pinned commit, so the static sizes come from instancing the variable files with fontTools 4.55.3 in a scratch virtual environment (never in the repo, no dependency added). The shipped files are the unmodified variable ones; the variable file is smaller than three statics for both kept families, so no static swap is needed.
- **Google Sans Flex, revisitable.** The owner's decision is "drop for now". The licence is fine, and the full file is the problem. A wght-only variable instance would be about 243 KB, but it is a modified file, which breaks the "shipped unmodified" rule and needs a recorded, reproducible instancing recipe; it would also need to say how it complies with `TRADEMARKS.md`. Do not ship it without a new owner decision.
- **Rendering checks on the device.** A throwaway debug catalogue page (not committed) showed ₹48,250.40 and −₹540.00 with `tnum` on, at 12, 14, 16, 24 and 48sp, in Figtree, Plus Jakarta Sans, Google Sans Flex, the system font and Newsreader. Owner, 2026-10-30, on ₹, − and the digits in each sans: **"looks good"**; no per-glyph fallback was seen, as the cmap check predicted. Variable weight applies on the device through `FontVariation.Settings(FontWeight(w))` on a `Font` declared per weight.
- **Size rules** (UI-17):
  - **Budget (owner-confirmed 2026-10-30): at most 3 MiB (3,145,728 B) for all of `res/font` together** (`FONT_BUDGET_BYTES = 3 * 1024 * 1024`). The measured totals:

    | Set | `res/font` bytes |
    |---|---|
    | Newsreader + all four candidates | 4,841,612 (over by 1,695,884) |
    | **Newsreader + Figtree + Plus Jakarta Sans (the kept set)** | **690,664 (0.66 MiB), 2.34 MiB free** |

  - **Base release APK, measured at `1f787ef`: 21,241,278 B (21.2 MB), not the 1.39 MB** quoted from the R8 learnings entry. The older figure predates A4b: SQLCipher's native libraries (four ABIs, about 19 MB uncompressed in the APK) are now most of the APK. The kept fonts add about 114 KB (Figtree 35,171 + Plus Jakarta Sans 79,125, compressed).
  - **Pre-commit hook: no exception is needed for the kept set.** `check-added-large-files` rejects files over 500 KB; the largest files are Newsreader 451,664 B (already committed), Plus Jakarta Sans 176,288 B and Figtree 62,712 B, all under it. **Fallback, kept as approved (owner, 2026-10-30):** if a font over 500 KB is ever bundled (Google Sans Flex's 3,910,784 B file would be the case, but it would also break the 3 MiB budget on its own), add a **path-scoped** exception for `app/src/main/res/font/` in `.pre-commit-config.yaml`, backed by `BundledFontTest` (exact size and hash) and `FontBudgetTest` (total). The hook is never bypassed with `--no-verify`.
- **R8 gotcha (found by DS6f): resource shrinking strips unreferenced fonts.** Release builds shrink resources (`isShrinkResources`), so a `res/font` file that no reachable code names as `R.font.<name>` is removed from the APK. The first all-fonts build showed a +0 delta until the throwaway used `tools:keep="@font/*"`. Consequence for DS6h and DS6i: reference every font directly as `R.font.<name>` from main code that the theme reaches (`sansFamily()` and `SansFont`), never by `getIdentifier` or a name string. The recipe in §11 step 4 checks the `res/*.ttf` entries in the release APK. Do **not** ship a `tools:keep` file to hide the problem.

### 3.8 Abstraction bar

- There is **no new interface, factory or wrapper class.** Every new type is a plain `enum` (persisted name) or an `@Immutable data class`, and pure functions resolve them: `ThemePalette.colors`, `pftSurfaces`, `glowAlpha`/`glowEnd`, `newsreader`, `sansFamily`.
- There is **one** new `staticCompositionLocalOf`, `LocalPftSurfaces`, because the surfaces vary with prefs (unlike spacing and motion, which stay constants).
- `HeaderIconButton` is justified in §3.5.
- None of these is an external dependency, and none is swapped.

### 3.9 Scope: app-wide

**The mockup shows only Home, but everything in this plan applies to every screen and component, built now or later.**
- **How it reaches them.** Every option here is a theme-level token: the palette (`MaterialTheme.colorScheme`), `PftTheme.surfaces` (canvas, island, edge, glow), the type roles (`MaterialTheme.typography`, `PftTheme.type`) and the sans font. Screens consume them only through `PftTheme` and the shared components (`CollapsingHeaderScaffold`, `BottomActionBar`, `GroupedSection`, and so on).
- **M3 components follow automatically.** Material 3 takes its colours and fonts from the theme: dialogs, bottom sheets (`PftBottomSheet`, B2), the navigation bar, the FAB, buttons, switches, text fields, date pickers and snackbars.
  - In a curated palette, the `surfaceContainer*` roles that dialogs and sheets paint are tonal steps of the canvas hue, so they match.
  - **One exception:** light `surfaceContainerLowest` is tone 100, which is white. It must not be used as a card, sheet or dialog fill (the owner rejected white cards). This is a review rule.
- **Meters and charts.** D4's `ProgressMeter` (and later M3 charts) fills with money tones on a track from the scheme, never the accent (§3.1).
- **The debug catalogue** gets a toggle for every option (DS6b–DS6i), so every component can be checked under every combination.
- **New screens from A5 onward get all of this for free.** They need no restyle, provided they follow the rule below.

**Rule: screens never hardcode colours or fonts.**
- No `Color(0x…)`, named `Color.*` constant, `FontFamily(…)`, `FontFamily.Serif`/`SansSerif`/`Monospace`/`Default` or `R.font.*` outside `ui/theme/`. Only `Color.Transparent` and `Color.Unspecified` are allowed, because they carry no design value. This extends the existing review rule (`design-system.md` "Rules") to fonts.
- **Check (cheap, added in DS6a):** `ThemeLiteralScanTest`, a plain JUnit 5 test.
  - It reads every `.kt` file under `app/src/main/kotlin` and `app/src/debug/kotlin` except `ui/theme/`, and fails with file and line for each forbidden literal.
  - It also has a self-test: the scanner flags a fixture string and passes a clean one, so the Red step fails for the right reason.
  - The source tree is clean of these literals today; DS5 adds only `Color.Transparent`, which is allowed.
  - **Why a test and not detekt:** detekt runs without type resolution here, and banning a constructor call needs a custom rule module. A text scan is about 30 lines with no new dependency. It is syntactic, so an alias import could slip past; review still covers that.

**Existing components that need a restyle check** (each re-checked in the slice that changes what it reads; goldens cover the pixels):

| Component | What changes for it | Slice |
|---|---|---|
| `MainActivity` root `Surface`, `DevContent` | canvas, window colour sync | DS6c |
| `CollapsingHeaderScaffold` (header, list background) | canvas, glow, icon-only actions, `EDITORIAL` titles | DS6c, DS6d, DS6e, DS6g |
| `PeriodStepper` (inside the scaffold) | chevron symbols | DS6e |
| `HeaderSamples` | icon actions | DS6e |
| `BottomActionBar` | canvas | DS6c |
| `TokenSheet` pages | palette scheme; the money-on-surfaces page adds canvas, island and wash | DS6a, DS6c, DS6d |
| `DesignCatalog`, `MotionSection` | new toggles | DS6b–DS6i |
| `GroupedSection` (DS5) | island fill, hairline edge, dark highlight | DS6c |
| `StatusChip` (DS5) | pill colours stay palette-independent (tested); visible on the island | DS6b, DS6c |
| `MoneyText` (DS5) | `amountLarge` is serif under `EDITORIAL`; figures tabular in every sans | DS6g, DS6h |
| `EmptyState` (DS5) | text roles and sans font only | DS6h |
| `CategoryGlyph` (DS3) | category palette is unchanged; check the badge on the island | DS6c |

## 4. Data model & schema

**There is no DB schema change and no migration.** Settings are rows in the existing `app_setting` table (A4a) through `SettingsService`.

**`ThemePrefs` final shape.** Fields arrive with the slice in brackets.

```kotlin
@Immutable data class ThemePrefs(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false,                        // DS6a: default was true (UI-8 revised)
    val lightPalette: ThemePalette = ThemePalette.GRAPHITE,   // DS6b
    val darkPalette: ThemePalette = ThemePalette.GRAPHITE,    // DS6b
    val background: BackgroundStyle = BackgroundStyle.TONAL,  // DS6c
    val headerGlow: Boolean = false,                          // DS6d
    val displayFont: DisplayFont = DisplayFont.BUNDLED_SERIF, // existing
    val typePairing: TypePairing = TypePairing.HERO_ONLY,     // DS6g
    val sansFont: SansFont = SansFont.FIGTREE,                // DS6h (DS6i adds one entry: PLUS_JAKARTA_SANS)
)
```

**Persistence contract for AP1.** These keys replace DS §4's dotted keys, which `SettingsService` rejects.

| Key | Values (enum `name`, or `true`/`false`) | Default |
|---|---|---|
| `ui_theme_mode` | `SYSTEM`, `LIGHT`, `DARK` | `SYSTEM` |
| `ui_dynamic_color` | `true`, `false` | `false` |
| `ui_palette_light` | `GRAPHITE`, `MIDNIGHT`, `IVORY`, `AUBERGINE` | `GRAPHITE` |
| `ui_palette_dark` | as above | `GRAPHITE` |
| `ui_background` | `TONAL`, `FLAT` | `TONAL` |
| `ui_header_glow` | `true`, `false` | `false` |
| `ui_font_display` | `BUNDLED_SERIF`, `SYSTEM_SERIF`, `TEXT` (AP2 adds `CUSTOM`) | `BUNDLED_SERIF` |
| `ui_type_pairing` | `HERO_ONLY`, `EDITORIAL` | `HERO_ONLY` |
| `ui_font_sans` | `FIGTREE`, `PLUS_JAKARTA_SANS`, `SYSTEM` (AP2 adds `CUSTOM`; this replaces DS's `ui.font.text`; DS6f dropped `GOOGLE_SANS_FLEX` and `DM_SANS`) | `FIGTREE` |

**Rules:**
- An unknown, missing or malformed value reads as the default for that key alone. Matching is exact: `entries.firstOrNull { it.name == v }`, and booleans are exactly `true` or `false`. Reading never throws, so values written by a newer version are tolerated.
- Enum names are persisted, so they are **permanent**: never rename or remove one, only add. AP1 pins the names in a test resource, as DS3 does for symbol keys.
- These values are not sensitive, so plain-text audit (the `SettingsService` contract) is fine.
- Before first-run setup, writes throw `NotInitialised` and reads return defaults. That is expected.

## 5. Flows

**Launch and theme resolution (after DS6; AP1 replaces `ThemePrefs.Default` with stored prefs):**
1. `MainActivity.onCreate` runs `FLAG_SECURE`, then `enableEdgeToEdge()`, both unchanged. The window paints the resource colour, which is Graphite's canvas.
2. `setContent { PftTheme(prefs) { SyncWindowBackground(window); Surface(color = PftTheme.surfaces.canvas) { … } } }`.
3. `PftTheme` resolves the theme in this order:
   - `dark` comes from `mode`.
   - The palette is `if (dark) darkPalette else lightPalette`.
   - The palette colours are `if (dynamicColor) derived(dynamicScheme) else palette.colors(dark)`.
   - The surfaces come from `pftSurfaces(colors, background, headerGlow, dark)`.
   - The typography comes from `sansFamily(sansFont)`, `displayFamily(displayFont)` and `typePairing`.
   - It provides `MaterialTheme(colors.scheme, …)`, `LocalMoneyColors`, `LocalPftTypography` and `LocalPftSurfaces`.
4. `SyncWindowBackground` sets the window colour to `canvas`, and does so again on every change.

**A setting changes (AP1):** `AppearanceViewModel` → `AppearanceSettings` → `SettingsService.set` (audited) → `prefs` flow → `MainActivity` recomposes `PftTheme` → the window colour syncs.

**Header frame with glow on:** scroll → `HeaderPolicy` lays out and stores the hero top → the draw lambda reads it and draws the gradient with `glowAlpha` × (1 − fraction). Composition does not run.

## 6. Implementation steps (TDD-ordered)

**Conventions** (as DS §6):
- Tests go in `app/src/test/kotlin/dev/imagh/pft/app/...`: Robolectric `@Config(sdk = [35])` with `HostedComposeRule`, or JUnit 5 where no Android is needed.
- Goldens use `@GraphicsMode(NATIVE)` and `w393dp-h851dp-xxhdpi`, with fake data.
- Every step ends with a green `./gradlew check`.
- A step that changes pixels re-records with `./gradlew :app:recordRoborazziDebug`, and the PR shows the PNG diffs.
- Any new `*Samples.kt` is added to `mustBeRemoved`.
- **One session per slice, in its own worktree** (memory rule), branched from the current `feat/m1a-foundation`. Slice branches use the learned rule (learnings 2026-10-28): git cannot hold both `feat/m1a-foundation` and `feat/m1a-foundation/<x>`, so the name is **`feat/m1a-foundation-ds6<x>-<slug>`** and the worktree is `.claude/worktrees/ds6<x>-<slug>`.

### 6.1 Slices

**DS6a: Graphite becomes the default scheme; dynamic colour becomes opt-in; `BrandColors` is retired**
- **Red:**
  - `ThemeResolutionTest`:
    - `default prefs turn dynamic colour off`: `ThemePrefs.Default.dynamicColor == false`.
    - `dynamic off resolves the Graphite scheme in each mode`: compare `roles()` maps.
    - `Graphite primary is the owner-approved platinum`: light `#3B4350`, dark `#C9CED8` (mockup values, pinned).
    - Keep the dynamic-on tests.
    - Delete `brand fallback is the owner-approved ink indigo` (`:55-60`), since Q5 is superseded.
  - `ColorContrastTest`:
    - Point it at the Graphite scheme.
    - Extend the dark grey sweep to tones `0..24`.
    - Replace the tone-ladder guard (`:108-113`) with: `every Graphite surface role lies within the swept band`.
    - Add: `onSurface and onSurfaceVariant clear 4.5 on every surface role`.
  - `AccentHueTest` (new, JUnit 5): Graphite `primary` is at least `MIN_ACCENT_HUE_DISTANCE` (40°) of CIELAB hue from `positive` and `negative`, in each mode. A `labHue(Color)` helper goes in `ColorSchemeTestSupport.kt`.
  - `WindowThemeTest` (`:42-52`): the window background equals Graphite `surface` in each mode.
  - `ThemeLiteralScanTest` (new, JUnit 5; §3.9): its self-test fails until the scanner exists; then the tree scan passes.
- **Green:**
  - Generate `GraphitePalette.kt` (§3.1 procedure) with `PaletteColors(scheme)`.
  - `resolveColorScheme` uses the palette.
  - `ThemePrefs.dynamicColor = false`.
  - Delete `BrandColors.kt` and update the 8 files that name it. `ThemePrefs(dynamicColor = false)` stays valid. Also update `app/README.md`, `design-system.md` and `code-structure.md`.
  - `res/values{,-night}/colors.xml` use Graphite `surface`.
  - `ThemeLiteralScanTest`'s scanner (test code only).
  - Re-record all 30 goldens.
- **Refactor:** KDoc (provenance, overrides). `design-system.md` "Theming" and "Colour". `app/README.md` (configuration defaults).

**DS6b: three more palettes, light and dark chosen independently**
- **Red:**
  - `ThemeResolutionTest`:
    - `light and dark palettes are chosen independently`: `ThemePrefs(lightPalette = IVORY, darkPalette = MIDNIGHT)` resolves IVORY light and MIDNIGHT dark.
    - `dynamic on ignores the palette`.
  - `ColorContrastTest` and `AccentHueTest` loop over `ThemePalette.entries × {light, dark}`. They fail until the entries exist.
  - `ChipColorsTest` (with DS5 merged): `chipColors(tone, money, palette.scheme)` is **identical for every palette** and equals the `MoneyColors` container pair for Positive, Negative and Warning. The warning pill can never take the accent.
  - `ThemePrefsTest` (pure): the defaults are both `GRAPHITE`.
- **Green:**
  - `MidnightPalette.kt`, `IvoryPalette.kt` and `AuberginePalette.kt`, generated and pinned per §3.1.
  - The `ThemePalette` enum and `colors(dark)`.
  - `ThemePrefs.lightPalette` and `darkPalette`.
  - `PftTheme` picks the palette by the resolved mode.
  - Catalogue toggles for light palette, dark palette and dynamic colour.
  - One new golden per mode: `palettes-<light|dark>.png`, the four palettes' role swatches side by side.
- **Refactor:** `design-system.md` palette table. `docs/features/appearance.md` (created; see §9).

**DS6c: tonal surfaces, window colour**
- **Red:**
  - `PftSurfacesTest` (JUnit 5):
    - For every palette, both modes and both styles, the §3.2 table holds.
    - The default prefs give `TONAL`.
    - Dark `TONAL` has the 4.5% white highlight; light has none.
    - `FLAT` has a transparent edge.
  - `ColorContrastTest` adds `canvas` and `island` of every palette and mode to the swept surfaces (money, `onSurface`, `onSurfaceVariant`).
  - `WindowFlashToneTest` (JUnit 5): every palette's `canvas` and `surface` is within `WINDOW_FLASH_MAX_TONE_DELTA` (8) tones of the resource colour (Graphite canvas) in the same mode.
  - `ThemeResolutionTest` (Robolectric) adds:
    - The derived dynamic `canvas`, `island` and `surface` keep within that bound.
    - They lie within the swept band.
  - `WindowThemeTest`: the resource window background equals Graphite `canvas`, and `FLAG_SECURE` is still set.
  - `WindowBackgroundTest` (Robolectric, launched activity):
    - With `PftTheme(ThemePrefs(lightPalette = IVORY, background = FLAT)) { SyncWindowBackground(window) }`, the decor view's `ColorDrawable` colour equals IVORY light `surface`.
    - Switching to `TONAL` updates it to IVORY `canvas`.
  - `CollapsingHeaderScaffoldTest` (`@GraphicsMode(NATIVE)`): with TONAL IVORY light, a pixel in the header's empty area and one in the list gutter equal IVORY `canvas`; with FLAT they equal `surface`. Pixels are read with `View.drawToBitmap()`. [Verify that this is reliable under Robolectric native graphics; if not, the goldens carry it and the pure `pftSurfaces` test carries correctness.]
  - `GroupedSectionTest`: the fill is `surfaces.island`, and the edge is drawn only for TONAL. This goes through a pure colours function, as DS5 does.
- **Green:**
  - `BackgroundStyle`, `PftSurfaces`, `pftSurfaces`, `LocalPftSurfaces` and `PftTheme.surfaces`.
  - `canvas` and `island` added to all four palettes.
  - The consumers listed in §3.2.
  - `WindowBackground.kt` and the `MainActivity` call.
  - `res` colours become Graphite `canvas`.
  - Catalogue toggle for background.
  - Re-record the goldens.
- **Refactor:**
  - `design-system.md`: surfaces and window colour.
  - `code-structure.md`: the new `ui/theme` files.

**DS6d: header glow**
- **Red:**
  - `HeaderGlowTest` (JUnit 5, pure):
    - `glowAlpha(0, end) == MAX_GLOW_ALPHA`.
    - It is non-increasing over 200 samples.
    - It is `0` at and beyond `end`.
    - It never exceeds `MAX_GLOW_ALPHA`.
    - `glowEnd(heroTop, h) ≤ heroTop − 24dp`, and is never negative.
    - Standard: `glowEnd(null, h) == 0.6h`.
    - The collapsed-fraction scaling gives `0` when collapsed.
  - `ColorContrastTest` adds `washTop = wash composited at MAX_GLOW_ALPHA over canvas` for every palette and mode. The checks are `onSurface` and `onSurfaceVariant` at 4.5:1 and money at 4.5:1. They use **contrast, not the tone band**, because Ivory and Aubergine light fall to tone 86.5–86.9.
  - `ThemeResolutionTest` adds the derived dynamic wash.
  - `PftSurfacesTest`: `headerGlow = false` (the default) gives `glow == null`.
  - `CollapsingHeaderScaffoldTest`, native pixels, glow on:
    - The header's top-centre pixel is not the canvas.
    - Every pixel on the hero's top edge row, across the band width, **equals the canvas**.
    - When collapsed, the top-centre pixel equals the canvas.
    - Glow off: the top-centre pixel equals the canvas.
- **Green:**
  - `HeaderGlow.kt`: the pure functions and `Modifier.headerGlow`.
  - `HeaderPolicy` stores the hero top.
  - `wash` added to all four palettes.
  - `ThemePrefs.headerGlow`.
  - Catalogue toggle.
  - Two goldens: `header-dashboard-expanded-glow-<light|dark>.png`.
- **Refactor:** `design-system.md` "One-hand layout": the glow rule.

**DS6e: icon-only header actions** (needs DS3 merged)
- **Red:**
  - `HeaderIconButtonTest`:
    - It renders the symbol with `contentDescription == label`.
    - The touch target is at least 48×48dp.
    - A click invokes `onClick`.
    - A blank label throws.
  - `CollapsingHeaderScaffoldTest`:
    - In both samples, every node under the actions row has a content description, and **no `Text` node** exists there.
    - Every clickable action is at least 48dp.
    - The period stepper's buttons are symbols labelled "Previous period" / "Next period".
- **Green:**
  - `ui/common/HeaderIconButton.kt`.
  - `HeaderSamples.kt` uses back, search and filter symbols.
  - `PeriodStepper` uses the chevron symbols.
  - The KDoc rule on `actions`.
  - Re-record the header goldens.
- **Refactor:** `design-system.md` (the icon-only rule; remove the DS4 "text glyphs" note). `code-structure.md`.

**DS6f: font spike (docs only; gates DS6g–DS6i). DONE 2026-10-30; results in §3.7 and §13.**
- **Do:**
  - Download each candidate from google/fonts at one pinned commit into a fresh scratch directory (untrusted-download rules), and read each file as in §2. Scripts stay outside the download directory, and Python runs with `-I`.
  - On a throwaway, unmerged branch: bundle all of them, `assembleRelease`, and record the APK delta per font.
  - On the emulator (a throwaway AVD, because `pft35` is shared), a throwaway catalogue page shows ₹48,250.40 and −₹540.00 at 12, 14, 16, 24 and 48sp in every font, plus Newsreader at `wght` 350 and 400 and at the per-role optical size.
- **Output** (a PR that amends §3.6–§3.7 of this plan):
  - The per-font table: licence and Reserved Font Name, axes, `tnum`, ₹, −, sizes, sha256, commit.
  - The keep or drop decision per font.
  - Variable or static per font.
  - The hook rule.
  - Hero weight 350: yes or no. **Decided: no, 400 stays.**
  - `titleMedium` in `EDITORIAL`: serif or sans. **Decided: serif, `wght` 500.**
  - A learnings entry.
- **No production code.** Red/Green does not apply; this is a measurement step.

**DS6g: type pairing and optical size per role (Newsreader only; no new font file, so DS6f's R8 gotcha does not apply here)**
- **Red:**
  - `PftTypographyTest`:
    - `HERO_ONLY` is unchanged. The hero's `wght` axis stays 400 (DS6f decision).
    - `EDITORIAL`: the §3.6 roles use Newsreader and the rest use the text family.
    - Each Newsreader role's `opsz` equals its font size clamped to 6–72 (for example `titleMedium` 16, `displayLarge` 57, hero 48), and its `wght` axis equals its weight (`titleMedium` is serif at `wght` 500 in `EDITORIAL`, per DS6f; the hero stays 400).
    - `displayFont = TEXT` with `EDITORIAL` gives no Newsreader at all.
    - An unknown pairing value is not representable in `ThemePrefs`; the parse-to-default rule is AP1's test (§6.3).
    - `SYSTEM_SERIF` gives `FontFamily.Serif` with no variation settings.
    - `tnum` is on the three amount roles in every pairing.
  - `CollapsingHeaderScaffoldTest`: the existing "every title is sans" check becomes pairing-aware: sans under `HERO_ONLY`, display under `EDITORIAL`.
  - Goldens: the two type pages under `EDITORIAL`, light and dark at 1x (4 PNGs).
- **Green:**
  - `TypePairing`, `newsreader(weight, size)`, and `pftMaterialTypography(text, display, pairing)` / `pftType(...)`.
  - `ThemePrefs.typePairing`.
  - Catalogue toggle.
- **Refactor:** `design-system.md` typography table (by pairing).

**DS6h: bundled Figtree becomes the default sans**
- **Red:**
  - `PftTypographyTest`: by default every sans role uses the bundled Figtree family; `SansFont.SYSTEM` gives `FontFamily.Default`.
  - `BundledFontTest`:
    - Figtree loads, and its bytes and sha256 match the DS6f record.
    - Its OFL text and copyright line ship.
  - `TabularFiguresTest` (Robolectric native): for Figtree and Newsreader, under each amount style, `₹11,111.11` and `₹00,000.00` measure equal widths, to within 0.5px. [Verify that native graphics applies `tnum`. If not, measure with `android.graphics.Paint` and `fontFeatureSettings = "tnum"` on the loaded `Typeface`.]
  - `FontBudgetTest`: the sum of the `R.font.*` sizes is at most `FONT_BUDGET_BYTES` (3 MiB).
- **Green:**
  - `res/font/figtree*.ttf` and `assets/licenses/figtree-OFL.txt`.
  - `SansFont { FIGTREE, SYSTEM }` and `sansFamily()`.
  - `PftTheme` `:61` uses the chosen family.
  - **No hook exception** (DS6f: Figtree is 62,712 B, under the 500 KB limit); `.pre-commit-config.yaml` is untouched.
  - Re-record every golden (all the text changes).
- **Refactor:** `design-system.md` third-party table. `README.md`.
- **Pick-ups from DS6f (apply in DS6h and DS6i):**
  - **Weights are set explicitly.** Figtree's `wght` axis runs 300-900 with a **default of 300**, so declare one `Font(R.font.figtree, FontWeight(w), variationSettings = FontVariation.Settings(FontWeight(w)))` for each of 400, 500 and 600; that is the form verified on the device. Roles asking for a weight that is not declared (for example bold) fall to the nearest declared one, so declare any weight a role uses.
  - **Digits are proportional by default** in Figtree and Plus Jakarta Sans; only `tnum` makes them equal (620/1000 and 600/1000). `TabularFiguresTest` must apply `tnum` through the amount styles, and plain body text in these fonts keeps proportional digits by design. Neither font has `lnum`, so the `lnum` in `"tnum, lnum"` is a harmless no-op, as with Newsreader.
  - **Reference every font directly as `R.font.<name>`** so release resource shrinking keeps it (§3.7 R8 gotcha), and check the `res/*.ttf` entries in the release APK (§11 step 4).
  - **Pin from the DS6f table:** file name, bytes, sha256, the google/fonts commit, and the upstream `OFL.txt` hash. The shipped OFL text may differ from upstream only by trailing whitespace and a final newline (as for Newsreader).
  - **Resource names** are lowercase with underscores: `figtree.ttf`, `plus_jakarta_sans.ttf`.
  - **Enum and settings:** `SansFont` is `FIGTREE`, `PLUS_JAKARTA_SANS`, `SYSTEM`; AP1's Appearance screen offers three sans options (§6.3).
  - **Budget:** the kept set is 690,664 B of 3,145,728 B; `FontBudgetTest` pins the sum of `R.font.*` file sizes.
  - **Baseline:** the release APK before fonts is 21,241,278 B; expect about +114 KB after Figtree and Plus Jakarta Sans.

**DS6i: Plus Jakarta Sans**
- **Red:** the same `BundledFontTest`, `TabularFiguresTest` and `FontBudgetTest` cases, now over `PLUS_JAKARTA_SANS` (the only sans left; DS6f dropped `GOOGLE_SANS_FLEX` and `DM_SANS`). `PftTypographyTest` checks that each entry maps to its family.
- **Green:**
  - `plus_jakarta_sans.ttf` (176,288 B), its OFL text and the enum entry. Nothing else: the other two candidates were dropped by DS6f.
  - Catalogue toggle.
- **Refactor:** third-party table. `docs/features/appearance.md`.

### 6.2 PR breakdown (incremental — required)

**Target branch:** every slice PR targets the existing base **`feat/m1a-foundation`**, which goes to `main` in A-final (as DS1–DS5 do).

**Why this split:**
- Each PR changes one visible thing that a reviewer can judge from its goldens, or adds options without changing the default look.
- Generated palette constants are split from the look change, so the review of DS6a is "is Graphite right?" and the review of DS6b is "are the three options valid?".
- Fonts are gated by a measurement PR, because licence and size facts decide what ships.

| PR | Branch | Scope | Depends on | Why it's independently reviewable |
|----|--------|-------|------------|-----------------------------------|
| DS6-0 | `docs/m1a-premium-look-plan` | this plan, the mockup, DS plan and architecture notes, learnings | — | docs only |
| DS6a | `feat/m1a-foundation-ds6a-graphite-default` | Graphite scheme, dynamic opt-in, `BrandColors` retired, window resources, goldens | DS6-0 (best after DS5, to re-record goldens once) | one look change: the default colours |
| DS6b | `feat/m1a-foundation-ds6b-palettes` | Midnight, Ivory, Aubergine, enum, light/dark prefs, hue and chip tests | DS6a, DS5 | options only; the default look is unchanged |
| DS6c | `feat/m1a-foundation-ds6c-tonal-surfaces` | `PftSurfaces`, TONAL/FLAT, window sync, consumers | DS6b, DS5 | the background change in isolation |
| DS6d | `feat/m1a-foundation-ds6d-header-glow` | glow functions, drawing, pref | DS6c | one effect, off by default |
| DS6e | `feat/m1a-foundation-ds6e-icon-header-actions` | `HeaderIconButton`, samples, chevrons | DS3, DS4 (parallel with DS6b–d) | header API rule plus a TalkBack contract |
| DS6f | `docs/m1a-ds6f-font-spike` | measurements and decisions (docs); **done** | DS6-0 (parallel) | facts only, gates fonts |
| DS6g | `feat/m1a-foundation-ds6g-type-pairing` | pairing, per-role `opsz`; hero weight stays 400 | DS6f | typography only, Newsreader only |
| DS6h | `feat/m1a-foundation-ds6h-figtree` | Figtree default, `SansFont{FIGTREE, SYSTEM}`, budget tests | DS6g | one font, one default change |
| DS6i | `feat/m1a-foundation-ds6i-more-sans` | Plus Jakarta Sans only (DS6f dropped the other two; the owner may fold this into DS6h) | DS6h | options only |
| A5 (existing) | as M1a | consumes everything | DS5, DS6a–e, A4b | — |

- **Hard gates before A5:** DS6a, DS6c and DS6e. They change what screens pass (surfaces, header actions) or what they look like by default.
- DS6d and DS6g–i change theme internals only, so they **should** also land before A5 (the owner's goal) but do not block it.
- Every PR carries its docs, learnings, security-review section, `graphify update .` and golden diffs.

### 6.3 What AP1 must add (DS §6.2 is annotated to point here)

1. **`AppearanceSettings`:**
   - Uses the §4 keys (underscored) and parses with the §4 rules in a pure `themePrefsFrom(read: (String) -> String?)`.
   - `AppearanceSettingsTest` checks, for **every** key: unset reads as the default; a garbage value reads as the default without throwing; a valid value round-trips with one audit row; a no-op write adds no row.
   - A names test pins the persisted enum names (`ThemePalette`, `BackgroundStyle`, `TypePairing`, `SansFont`, `ThemeMode`, `DisplayFont`).
2. **The Appearance screen** (`GroupedSection` rows), in this order:
   - Theme mode.
   - Light palette and dark palette, each a swatch picker that previews `canvas`, `island` and `primary`.
   - "Use wallpaper colours" (dynamic). When it is on, the palette pickers are disabled with a note.
   - Background (Tonal / Flat).
   - Header glow (switch).
   - Sans font (three options: Figtree, Plus Jakarta Sans, system; each label in its own font).
   - Type pairing (two options, each with a small preview).
   - Display font (existing).
3. **First frame:** the existing ≤500 ms hold uses the stored prefs. `SyncWindowBackground` is already wired. `AppearanceFlowTest` checks that switching the light palette to IVORY repaints the window and the scaffold canvas.
4. **The Licences screen** lists Newsreader, every bundled sans and Material Symbols.
5. **`docs/features/appearance.md`:** the settings, the defaults and what each does.

## 7. Test plan

**Unit tests (JUnit 5, pure):**
- `ColorContrastTest`, looped over every palette and mode. It checks:
  - money text on the M3 surface roles, `canvas`, `island` and the max-strength wash-top, plus greys at light tones 87–100 and dark tones 0–24;
  - `onSurface` and `onSurfaceVariant` on the same set;
  - `onPrimary` on `primary` (4.5:1) and `primary` against `canvas`, `island` and `surface` (3:1);
  - the containers.
- `AccentHueTest` (40° from money green and red).
- `PftSurfacesTest`, `WindowFlashToneTest`, `HeaderGlowTest`, `ThemePrefsTest` and `ChipColorsTest`.
- `ThemeLiteralScanTest` (no hardcoded colours or fonts outside `ui/theme`, §3.9).

**Integration tests (Robolectric, real collaborators):**
- `ThemeResolutionTest`, with the real `dynamic*ColorScheme` and its derived surfaces and wash.
- `WindowThemeTest` and `WindowBackgroundTest` (a launched `MainActivity` or host activity, real window).
- `PftTypographyTest` (real `PftTheme` composition).
- `BundledFontTest` and `TabularFiguresTest` (real font files, native text measurement).
- `FontBudgetTest`.
- `HeaderIconButtonTest` and `CollapsingHeaderScaffoldTest` (real nested scroll, native pixels for canvas and glow).
- `GroupedSectionTest`.

No mocks; the platform shadows are the only fakes.

**Edge and failure cases:**
- An unknown stored palette, style, pairing or sans (AP1).
- `displayFont = TEXT` with `EDITORIAL`.
- Dynamic colour on with any palette selected.
- A forced dark mode on a light system (AP1).
- Font scale 2.0 with the glow on: the hero rises, and `glowEnd` follows the measured hero top.
- A short window (always collapsed, so no glow).
- Brass and champagne next to warning amber.
- A font missing ₹ or − (per-glyph fallback, recorded).

**Screenshot goldens:**
- Re-recorded in DS6a, DS6c, DS6e and DS6h.
- New: palettes (DS6b), glow (DS6d) and type-pairing pages (DS6g).
- Graphite, the default, is used everywhere else.

**AC → test map:**

| AC | Tests |
|---|---|
| UI-1 | goldens and the owner's mockup review, manual pass (§11) |
| UI-2 (revised) | `PftTypographyTest` (Figtree default, `SYSTEM`), `BundledFontTest` |
| UI-3 (revised) | `PftTypographyTest` (pairings), `CollapsingHeaderScaffoldTest` (pairing-aware titles) |
| UI-4 | `PftThemeModeTest`, `WindowThemeTest`, `ColorContrastTest` (both modes) |
| UI-5 | `ColorContrastTest` (money on every palette's surfaces and wash), `ChipColorsTest`, `ThemeResolutionTest` (money independent of palette and dynamic colour) |
| UI-7 | `CollapsingHeaderScaffoldTest` (unchanged layout assertions stay green) |
| UI-8 (revised) | `ThemeResolutionTest` (Graphite default, dynamic opt-in, dynamic ignores the palette) |
| UI-11 | `ThemeResolutionTest` (independent light/dark), `ColorContrastTest`, `AccentHueTest`, `ChipColorsTest`, manual amber check |
| UI-12 | `PftSurfacesTest`, `GroupedSectionTest`, `CollapsingHeaderScaffoldTest` (canvas pixels), goldens; app-wide reach: `ThemeLiteralScanTest` (§3.9) |
| UI-13 | `HeaderGlowTest`, `ColorContrastTest` (wash-top), `CollapsingHeaderScaffoldTest` (hero row equals canvas) |
| UI-14 | `HeaderIconButtonTest`, `CollapsingHeaderScaffoldTest` (no text, labels, 48dp), TalkBack manual |
| UI-15 | `PftTypographyTest` (roles, `opsz`, `wght`) |
| UI-16 | `BundledFontTest` (licence and copyright for each font), `TabularFiguresTest` |
| UI-17 | `BundledFontTest` (size pins), `FontBudgetTest`, DS6f record |
| UI-18 | `WindowFlashToneTest`, `WindowThemeTest`, `WindowBackgroundTest` |
| SEC-2 | `WindowThemeTest` and `ManifestHardeningTest` (`FLAG_SECURE` unchanged) |
| SEC-3 | `ManifestHardeningTest` (no new permission) |

## 8. Security considerations

- **No new permission, network access or runtime dependency.** Fonts are bundled resources; there are no downloadable fonts, and `ManifestHardeningTest` stays green. Palette generation uses material-color-utilities offline in a scratch directory, never as a project dependency (DS1 precedent).
- **Licences:**
  - Every bundled font must be OFL-1.1 (DS6f verifies; a font that is not compatible is dropped).
  - The OFL text ships with each font (OFL §2), and AP1 shows it.
  - Fonts are shipped unmodified, so a declared Reserved Font Name is no obstacle.
  - Proprietary fonts (One UI Sans, Circular, Spotify Mix) are excluded.
- **Provenance:** each font is pinned by sha256 and size in `BundledFontTest`, from a pinned google/fonts commit. Spike downloads go into a fresh scratch directory, are treated as untrusted, and are read with `python3 -I`.
- **Untrusted input:**
  - Stored setting values are parsed by exact enum-name match, and any other value reads as the default, so reading never throws.
  - No setting value is ever used in a path, a query string or a log.
  - Custom font files remain AP2's fail-closed pipeline (unchanged).
- **`FLAG_SECURE`** stays first in `onCreate`. `SyncWindowBackground` runs inside composition, after it.
- **Logs:** no new log events, and setting values are not logged.
- **Privacy:**
  - The goldens and the mockup use fake data only.
  - The mockup HTML loads Google Fonts **in the viewer's browser** when opened. It is a docs artefact, never packaged.

## 9. Documentation updates (all required surfaces)

| Surface | Change | Slice |
|---|---|---|
| KDoc | every new type and function; palette provenance and overrides; the "persisted name, never rename" note on each enum; the `actions` icon-only rule | each |
| `docs/architecture/design-system.md` | a "Planned: DS6" note (DS6-0, appended at the end to avoid DS5's hunks), then: theming (palettes, dynamic opt-in), colour (palette table, accent and amber rule), surfaces, window colour, glow, icon-only actions, typography by pairing, third-party font rows | DS6-0, DS6a–i |
| `docs/architecture/code-structure.md` | new `ui/theme` files (`*Palette.kt`, `PftSurfaces.kt`, `WindowBackground.kt`), `ui/common/HeaderGlow.kt` and `HeaderIconButton.kt`, `res/font` and `assets/licenses` entries | DS6a, c, d, e, h |
| `docs/features/appearance.md` (**new**) | product doc: palettes, background, glow, pairing and fonts, with their defaults (AP1 extends it with the screen) | DS6b, extended by DS6c–i |
| `app/README.md` | public interface (`PftTheme.surfaces`, `HeaderIconButton`), configuration (`ThemePrefs` defaults), assets | each |
| Root `README.md` | module map unchanged; note the font assets only if setup changes (the hook exception) | DS6h |
| `docs/plans/2026-10-29-m1a-design-system.md` | annotated now (§1 UI-2/3/8, §3.1, §3.3, §4 keys, §6.2 AP1, §7 UI-8, §12 Q10) | DS6-0 |
| this plan | DS6f results amend §3.6–3.7; deviations recorded per slice, as in DS | DS6f, each |
| `docs/learnings/README.md` | one entry per slice | each |
| graphify | `graphify update .` after code changes | each code PR |

## 10. Risks & mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Brass and champagne sit about 1–9° from warning amber | a status could read as the accent, or the accent as a warning | labels are mandatory (DS5); pill colours are palette-independent (`ChipColorsTest`); the accent goes only on interactive chrome; `ProgressMeter` uses money tones; manual check in MIDNIGHT and AUBERGINE dark |
| Islands at 1.06–1.11:1 to the canvas vanish on dim or low-quality screens | cards lose their shape | the hairline edge (about 1.2–1.3:1) and the dark top highlight; tones are constants, easy to tune; the owner judges on the emulator and in goldens |
| Re-derived schemes drift from what the owner approved | the look changes in review | pin the mockup values (§3.1); change tone only on a test failure; record it |
| First-frame flash with a non-default palette | a visible jump at launch | resources equal the default canvas; a tone bound of at most 8 (tested); runtime sync; AP1 first-frame hold |
| A font over 500 KB, or a large total | the pre-commit hook blocks it; APK growth | measured first (DS6f); path-scoped exception backed by exact size and hash pins; 3 MiB budget pinned by `FontBudgetTest` |
| A bundled sans lacks ₹ or − | a glyph from another font inside an amount | recorded per font; on-device check; per-glyph fallback accepted, or the font dropped if it looks broken |
| A font lacks tabular figures | amounts jitter and columns misalign | hard requirement in DS6f (it dropped DM Sans); `TabularFiguresTest` |
| Newsreader weight 350 does not render, or its 16sp cut looks weak | hero weight wrong; `EDITORIAL` section titles look thin | **Resolved by DS6f:** 350 does render; the owner kept the hero at 400 and judged the 16sp w500 serif title good |
| Google Sans Flex licence or axes differ from the reports | cannot bundle it, or the wrong axis | **Resolved by DS6f:** the licence is OFL 1.1 with no RFN and it has five axes, but the file is 3.9 MB, so it is dropped "for now" (owner) |
| A bundled font is stripped from the release APK | the font silently falls back to the system one | reference each font as `R.font.<name>` in reachable code; check the `res/*.ttf` entries in the release APK (§3.7, §11) |
| Golden churn across four PRs | review fatigue | goldens re-recorded only in DS6a, c, e and h; behaviour tests carry correctness |
| Merge-order conflicts with DS5 (goldens, `GroupedSection`) and DS3 | rework | DS6b and DS6c wait for DS5, DS6e for DS3; DS6a ideally after DS5 |
| Retiring `BrandColors` supersedes owner decision Q5 | surprise | called out here and in DS §12; the owner decided Graphite is the default |

## 11. Rollout & verification

**Per slice:**
1. Run `./gradlew check` (unit, Robolectric, screenshot verification, lint, detekt, coverage, R8 shrink guard).
2. Review the PNG diffs.
3. Run `./gradlew :app:installDebug` on `pft35` (started with `-gpu host`) and walk the catalogue with every new toggle:
   - **Night mode:** `adb shell cmd uimode night yes`, then `no`. Check every palette in both modes, TONAL vs FLAT, and the glow on and off. Islands must be visible and never white; the hero row must be untinted.
   - **Font scale:** `adb shell settings put system font_scale 2.0`, then `1.0`. Check the hero shrink, that the glow ends above the hero, and the type pairings.
   - **Dynamic colour with red and green wallpapers** (Settings › Wallpaper, a solid red then a solid green image; catalogue dynamic toggle on). The accent follows the wallpaper; the money colours and pills do not change; the canvas and island are derived and the islands are still visible.
   - **Amber check:** MIDNIGHT dark and AUBERGINE dark, with an Unverified pill beside a selected navigation item and the FAB.
   - **TalkBack** (Settings › Accessibility): header actions read "Search", "Filter", "Back", "Previous period"; amounts read with their direction.
   - **Launch flash:** cold-start in each palette (`adb shell am force-stop dev.imagh.pft`, then launch), watching the first frame.
4. **Fonts (DS6f, DS6h, DS6i):**
   - Run `./gradlew :app:assembleRelease`.
   - Record the APK size before and after with `ls -l app/build/outputs/apk/release/`, and the `res/font` entries with `unzip -lv` (R8 renames them to `res/<xx>.ttf`; match them by uncompressed size). A font missing from the list was stripped as unreferenced (§3.7).
   - Install a debug-key-signed **copy** for a smoke test (recipe in learnings, 2026-10-30).
   - Write the numbers into learnings.

**Rollback:**
- Every slice is UI-only and revertable on its own, with no schema change.
- Reverting DS6a needs `BrandColors` back. Reverting later slices leaves AP1's unknown values reading as defaults.

## 12. Open questions

**No blocking question.** The one decision that was open (hero weight) was taken at the DS6f review. One minor owner item is open and not decided here:
- **Fold DS6i into DS6h?** DS6f left DS6i with a single font (Plus Jakarta Sans, 176,288 B). The owner may bundle it in DS6h or keep it as its own small PR (§6.2).

**Decided (owner, 2026-10-30, DS6f device check):**
1. **Hero weight: 400** (owner, verbatim: "400"). The mockup's 350 is not adopted.
2. **`titleMedium` in `EDITORIAL`: serif (Newsreader) at `wght` 500** (owner, verbatim: "500 w for title looks good. 48sp is good for title"; asked whether Newsreader or Figtree at w500, "both"; asked what "48sp" meant, "Hero amount"). The serif was not judged weak, so the plan's default (serif) stands; the hero at 48sp is confirmed.
3. **₹, − and digits in each sans** (owner, verbatim: "looks good"). No per-glyph fallback was seen.
4. **Google Sans Flex: dropped "for now"** (owner, verbatim: "drop for now"). Revisitable later as a new `SansFont` value; the wght-only instance is not shipped.

**Decided (owner, 2026-10-30, second round):**
- **Font size budget:** at most 3 MiB for all of `res/font`, with a path-scoped exception to the 500 KB hook for `app/src/main/res/font/` as a **fallback only**: DS6f measured that the kept fonts need no exception (§3.7).
- **All serif:** deferred. `TypePairing` is `{ HERO_ONLY, EDITORIAL }`; all serif can return later as a third value without a migration (§3.6).
- **Scope:** app-wide, through the theme and the shared components (§3.9).

Decisions recorded here that revise earlier owner decisions (for the owner's awareness, not a question):
- UI-8 (Graphite default; dynamic opt-in).
- UI-2 (Figtree default).
- UI-3 / Q10 (`EDITORIAL` option; hero-only stays the default).
- Q5 (ink indigo retired).

## 13. Deviations from the plan, by slice

**DS6a (Graphite default; dynamic opt-in; `BrandColors` retired)**
- **Base moved.** The slice was briefed on `ca11f41`; `feat/m1a-foundation` had since moved to `df458d2` (PR #27, A4b SQLCipher), and the branch is cut from that.
- **Counts.** The plan said "8 files name `BrandColors`" and "30 goldens". The eight code and resource files that named it were `PftTheme`, `ThemePrefs`, both `colors.xml`, `WindowThemeTest`, `ColorContrastTest`, `PftThemeModeTest` and `ThemeResolutionTest`; the rest were `BrandColors.kt` itself, `app/README.md` and the docs. Comments in `MoneyColors` and four screenshot tests said "brand scheme" without the name and were reworded. There are 48 goldens (DS3 to DS5 added most), all re-recorded.
- **No pinned value needed a tone change.** Every mockup value passed `ColorContrastTest` and `AccentHueTest` as approved. Three values are not mockup tokens but keep the pinned ones coherent, and none changes a hue: `surfaceTint` equals the pinned `primary`; dark `surfaceDim` equals the pinned dark `surface` (tone 5, not the ladder's 6); dark `surfaceContainerLow` is neutral tone 7 (`#151618`, not the ladder's 10) so the dark ladder stays ordered under the pinned `surfaceContainer` (tone 9). They are listed in `GraphitePalette`'s KDoc.
- **Generation API.** material-color-utilities 0.3.0 can build a scheme from custom palettes: `new DynamicScheme({ ..., primaryPalette, secondaryPalette, tertiaryPalette, neutralPalette, neutralVariantPalette })` with `TonalPalette.fromHueAndChroma`; the `Variant` enum is not exported from the package root, so the tonal-spot variant is passed as its value, 2. The surface roles were then read from the neutral palette at the M3 1.3 ladder (the 0.3.0 `MaterialDynamicColors` surface-container roles were not used).
- **The scanner found nothing to move.** `ThemeLiteralScanTest` passes on the tree as it stands (DS3 to DS5 added no colour or font literal outside `ui/theme`), so no literal was moved and nothing is allowlisted.
- **Commit shape.** The palette switch and the 48 re-recorded goldens are one commit, because `check` verifies goldens and they cannot be green apart from it. `ThemePrefs.dynamicColor = false` is its own commit, because every screenshot test already passed `dynamicColor = false`.

**DS6e (icon-only header actions; `HeaderIconButton`)**
- **DS6a had already merged.** The slice was briefed with DS6a still in flight; it merged first (PR #28, merge `1f787ef`) and the branch was cut from that, so the header goldens were recorded on Graphite and `ThemeLiteralScanTest` ran on the tree with no rebase.
- **The 48dp target is `touchBoundsInRoot`, not the node's bounds.** M3's `IconButton` draws 40dp and Compose adds the 48dp minimum as a touch target outside the semantics bounds, so the "at least 48dp" tests read `SemanticsNode.touchBoundsInRoot`. (The DS3 learning already said so.)
- **"Renders the symbol" is a golden, not a unit test.** `captureToImage` times out under Robolectric in this repo, so `HeaderIconButtonTest` checks the semantics (button role, `contentDescription == label`, no text node, 48dp, click, blank label) and the pixels are the 8 re-recorded header goldens. The tint is set explicitly to `onSurfaceVariant` because `Icon` otherwise takes `LocalContentColor`.
- **`PeriodStepper` reuses `HeaderIconButton`** with `CHEVRON_LEFT` / `CHEVRON_RIGHT`; the labels already came from string resources, so it fits with no change. Its row stays `Row(testTag(PERIOD_STEPPER))`.
- **A `HeaderTags.ACTIONS` test tag was added** on the actions row (semantics only) so the test can scope "every node under the actions row".
- **The existing `actions sit in the top row` test** now uses a `HeaderIconButton` instead of an `IconButton` holding text, so no test models the pattern the rule forbids.
- **Release APK delta is 0 bytes.** No release code reaches the scaffold or `HeaderIconButton` yet (only `HeaderSamples`, which R8 removes), so the release APK is byte-identical to the base (SHA-256 equal) and no drawable survives. The first real consumer (A5 and later) is when the symbols start to count; DS3's learning already records the +87 KB worst case.
- **LTR only.** The app is LTR-only, so symbols are not auto-mirrored: `CHEVRON_LEFT` is always "previous" and `BACK` is unmirrored, although the scaffold's swipe already flips under RTL. Revisit (autoMirrored drawables or swapped chevrons) if RTL is ever supported. It is also in the `HeaderPeriod` and `PeriodStepper` KDoc.
- **No new `*Samples.kt`**, so `mustBeRemoved` is unchanged.
- **Commit shape.** The stepper change and its two expanded Dashboard goldens are one commit; the samples change and all 8 header goldens are the next (the Dashboard-expanded pair is therefore recorded twice).

**DS6f (font spike)**
- **Base.** Briefed on `ca11f41`; `feat/m1a-foundation` had moved to `1f787ef` (DS6a, PR #28), and the branch is cut from that.
- **Candidate set shrank from four to two.** DM Sans has no `tnum`/`pnum` and proportional default digits (hard requirement fails). Google Sans Flex passes the licence check (OFL 1.1, no RFN) but is 3,910,784 B, over the whole 3 MiB budget by itself; the owner said "drop for now". Kept: Figtree and Plus Jakarta Sans, both variable. §3.7, §4, O6, UI-2, UI-16, §6.3 and the DS6h/DS6i slices were updated.
- **Where it was checked.** The brief said `pft35`; `pft35` is shared and was not running, so a throwaway AVD (API 35, Pixel 7, 1080x2400 at 420 dpi) was used and deleted afterwards. The owner looked at the emulator window; rendering was also read through `uiautomator` (all 43 catalogue rows present, no crash). `FLAG_SECURE` was not touched, so screenshots are blank.
- **APK baseline correction.** The plan quoted a 1.39 MB release APK; at `1f787ef` it is 21,241,278 B because of SQLCipher's native libraries (A4b).
- **APK delta method.** One all-fonts rebuild with `./gradlew :app:assembleRelease --offline`, then per-entry compressed sizes from `unzip -lv` (not one font at a time). Total delta with all four was 2,033,320 B; the entries sum to 2,032,684 B and the rest is resource-table overhead. R8 strips unreferenced fonts, so the throwaway used `tools:keep="@font/*"`, which is not shipped.
- **Static sizes** are fontTools 4.55.3 instances made in a scratch virtual environment, indicative only; google/fonts has no statics for these families.
- **Hook exception not needed** for the kept set (largest file Newsreader 451,664 B, Plus Jakarta Sans 176,288 B, Figtree 62,712 B); the approved path-scoped shape is kept as a fallback in §3.7.
- **Wording fixes outside the font scope, made while here:** the `TypePairing` options in §6.3 said three; it has two since all-serif was deferred (§3.6). The "five or six axes" expectation for Google Sans Flex was five.
- **Nothing was bundled.** No font, licence file, test, hook change or dependency was added in this slice; `design-system.md`'s third-party table gains rows in DS6h and DS6i.
