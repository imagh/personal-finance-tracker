# Design system

Status: **DS1 (colour, `PftTheme`, window themes), DS2 (type, shape, spacing, bundled serif,
screenshot harness, debug catalogue), DS3 (icons, category palette, `CategoryGlyph`, emoji policy), DS4 (motion tokens,
`CollapsingHeaderScaffold`, `BottomActionBar`), DS5 (`MoneyText`, `StatusChip`, `GroupedSection`, `EmptyState`), DS6e
(`HeaderIconButton`, icon-only header actions) and DS6g (the type pairing and per-role optical size) are built.**

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
You, opt-in, or the committed palette the user picked for the resolved mode, Cobalt by default; the default prefs are Cobalt in both modes, Manrope sans, Bricolage Grotesque display font, hero-only pairing, tonal surfaces, K4), applies `MaterialTheme(colorScheme, typography, shapes)` and
provides the extended tokens through `staticCompositionLocalOf`. Read them through the `PftTheme`
object:

| Accessor | Type | Provided how |
|---|---|---|
| `MaterialTheme.colorScheme` | M3 `ColorScheme` | the chosen palette's scheme (Cobalt by default); dynamic when `ThemePrefs.dynamicColor` is on, which ignores the palette |
| `MaterialTheme.typography` | M3 `Typography` | every role in the sans text family; under `EDITORIAL`, `display*`, `headline*`, `titleLarge` and `titleMedium` are in the display family |
| `MaterialTheme.shapes` | M3 `Shapes` | 6/10/16/24/32dp |
| `PftTheme.money` | `MoneyColors` | fixed green/red/amber, never derived from the accent |
| `PftTheme.surfaces` | `PftSurfaces` | the screen canvas, card island, island edge, top highlight and header glow tint (null while the glow is off) for the palette, mode, `BackgroundStyle` and `ThemePrefs.headerGlow` (DS6c, DS6d); see "Surfaces and window colour" and "One-hand layout" |
| `PftTheme.brand` | `BrandColors` | the brand fill, the balance card gradient and the tab bar fill: hand-picked for Cobalt, derived from the scheme for every other palette and for dynamic colour (K1) |
| `PftTheme.type` | `PftTypography` | the three amount styles |
| `PftTheme.shapes` | `PftShapes` | `glyph` (the category badge), `card` 22dp, `island` 18dp, `quickAction` 17dp, `tabBar` (full pill) |
| `PftTheme.spacing` | `PftSpacing` | the 4dp-grid scale |
| `PftTheme.motion` | `PftMotion` | four springs, navigation transitions, press scale; a constant, the same in every theme |

`ThemePrefs` is a plain data class (`mode`, `dynamicColor`, `lightPalette`, `darkPalette`, `background`, `headerGlow`, `displayFont`, `typePairing`, `sansFont`). Until the appearance
settings (AP1) the app always passes the defaults. Tokens are static code constants; per-user
choices will live in the database (CLAUDE.md §3.5).

## Tokens

### Typography (UI-2, UI-3, UI-15, owner decision Q10)

Two settings shape the type. `ThemePrefs.displayFont` chooses **what** the display family is:
`BUNDLED_SERIF` (Newsreader, the default), `BUNDLED_GROTESQUE` (Bricolage Grotesque, K2), `SYSTEM_SERIF` or
`TEXT` ("same as text").
`ThemePrefs.typePairing` chooses **which roles** use it. The text family ("text" in the table) is
`ThemePrefs.sansFont`: the bundled **Figtree** by default, the bundled **Plus Jakarta Sans**, the bundled
**Manrope** (K2), or `SYSTEM` (`FontFamily.Default`); see
"The sans font" below.

| Role | `HERO_ONLY` (default) | `EDITORIAL` | Size / line (sp) | Weight | Features |
|---|---|---|---|---|---|
| `PftTheme.type.amountHero` | display | display | 48/60, +0.01em (34/42.5 for `BUNDLED_GROTESQUE`) | 400 (700 for `BUNDLED_GROTESQUE`) | `tnum`, `lnum` |
| `PftTheme.type.amountLarge` | text | display | 32/40 | 500 | `tnum`, `lnum` |
| `PftTheme.type.amountRow` | text | text | 16/24 | 500 | `tnum` |
| `display*` | text | display | M3 (57, 45, 36) | M3 | |
| `headlineLarge` | text | display | 32/40 | 600 | |
| `headlineMedium`, `headlineSmall` | text | display | M3 (28, 24) | M3 | |
| `titleLarge` | text | display | 22/28 | 500 | |
| `titleMedium` | text | display | 16/24 | 500 | |
| `titleSmall` (tab labels, which are controls) | text | text | M3 | M3 | |
| `body*`, `label*` | text | text | M3 | M3 | |

- **`HERO_ONLY` is the default and is today's look**: the serif is `amountHero` only (owner decision Q10),
  and no golden changed when the pairing arrived.
- **`displayFont = TEXT` with `EDITORIAL` is all sans**: the pairing picks the roles, the font picks what
  they are set in, and "same as text" is the text family. `SYSTEM_SERIF` gives `FontFamily.Serif`.
- **Optical size per role.** Newsreader is a variable font with a weight axis (`wght`) and an
  optical-size axis (`opsz`, 6 to 72). `newsreader(weight, size)` builds a family pinned to the role's
  weight and cut for the role's own size (the size clamped to 6 to 72), so `titleMedium` is `wght` 500 at
  `opsz` 16, `displayLarge` is 400 at 57 and the hero is 400 at 48. It is called only for the serif roles
  of the chosen pairing. The variation settings apply to the bundled Newsreader only; the system serif
  and the text family get none. The hero keeps the 48sp cut at every shrink step.
- **The hero weight is 400** (owner decision after the DS6f device check); the mockup's 350 was not adopted.
- **Bricolage Grotesque (K2, Cobalt plan D2).** `DisplayFont.BUNDLED_GROTESQUE` makes the hero **Bricolage at
  weight 700, 34sp**, line height 1.25x (42.5sp), the same +0.01em, `tnum, lnum`; the size and weight come from one
  private `heroSpec(display)` that every other display font answers with the regular 48sp. The font is one variable
  file with axes `opsz` 12-96 (default 96), `wdth` 75-100 (default 100) and `wght` 200-800 (default 800), so
  `bricolage(weight, size)` pins all three: `wght` to the weight, `opsz` to the size (clamped to 12-96, so the hero
  is `opsz` 34 and keeps that cut at every shrink step, as Newsreader keeps its 48) and `wdth` to 100. Under
  `EDITORIAL` the same function cuts each serif role for its own size and weight. **`HeroSizeSteps` stays the full
  ladder (48 down to 12sp); `heroSizeSteps(heroSize)` is the steps at or below the hero's own size**, which
  `MoneyText` uses, so the 48sp hero keeps its eight steps and the 34sp hero shrinks 34, 28, 24, 20, 16, 12sp and
  never grows to a larger step. Nothing is the default yet: `ThemePrefs.Default` is still Graphite, Figtree and
  Newsreader (K4 flips it).
- **Manrope (K2).** `SansFont.MANROPE` is one family of three `Font(R.font.manrope, ...)` entries for 400, 500 and
  600 pinned on `wght` (its axis is 200-800 with default 200, so an unpinned axis would render everything too light),
  built by the same `sansWeights` helper (each pinned one step heavier, see K9 below). Nothing in Cobalt asks for 700 as a role weight. Its default digits are already tabular, so
  `tnum` is a harmless no-op there, as for Newsreader.
- **Verified from the font files (K2, `FontFileFacts` in `TabularFiguresTest`).** Both Manrope and Bricolage have a
  `tnum` GSUB feature (Bricolage also `lnum`) and their own `cmap` glyphs for the rupee sign U+20B9 and the true minus
  U+2212. `Paint.hasGlyph` cannot prove that: under Robolectric native graphics it also consults the system fallback
  fonts and answered true for a CJK ideograph, so the tests read the `cmap` and `GSUB` tables of the bundled bytes.
- `tnum` stays on all three amount roles in every pairing.
- **The sans font (UI-2, UI-16, DS6h, DS6i).** `SansFont` is `{ FIGTREE, PLUS_JAKARTA_SANS, SYSTEM }` (names
  persisted by AP1; AP2 adds `CUSTOM`, without a migration). `sansFamily(SansFont)` maps `SYSTEM` to
  `FontFamily.Default`, and each bundled font to one family of three `Font(R.font.<name>, ...)` entries
  (`figtree`, `plus_jakarta_sans`), for weights 400, 500 and 600, each with
  `FontVariation.Settings(FontWeight(w), FontStyle.Normal)`; one private helper builds both. Both are variable
  fonts; Figtree's `wght` axis defaults to 300 and Plus Jakarta Sans's (200-800) to 400, but the axis is set
  explicitly for both, so every weight a role uses must be declared; 400, 500 and 600 are all
  the weights any role in `pftMaterialTypography` and `pftType` uses (`PftTypographyTest` checks it), and a new
  weight must be added to the family first. **K9 (owner walk-through, 2026-11-26): the three bundled sans fonts
  read too light and narrow, so each declared weight is pinned one step heavier on `wght`** (400 as 500, 500 as 600,
  600 as 700) by `shiftedSansWght(weight, axisMax)`, capped at the font's heaviest `wght` (Figtree 900, Plus
  Jakarta Sans and Manrope 800, read off the `fvar` tables); roles still ask for 400/500/600 and the width is
  untouched, so the shift is one place. `SYSTEM`, Newsreader and Bricolage (hero 700 of 800) are not shifted.
  `PftTheme` passes the chosen family as the text family to both
  type functions, so both pairings work on top of it: in `EDITORIAL` the serif roles stay Newsreader, and
  `displayFont = TEXT` means the chosen sans (Figtree by default). The digits of Figtree and Plus Jakarta Sans are
  proportional by default; only `tnum` makes them equal, so the three amount roles keep it, while body text digits
  stay proportional by design (neither has `lnum`, so that half of `"tnum, lnum"` is a no-op).
  `TabularFiguresTest` measures `₹11,111.11` against `₹00,000.00` under each amount style in Figtree, Plus
  Jakarta Sans, Manrope, Newsreader and the Bricolage hero (equal within 0.5px), with a no-`tnum` control that
  differs for Figtree, Plus Jakarta Sans and the Bricolage hero, and a `pnum` control for Manrope (its default digits
  are already tabular, so dropping `tnum` cannot make it differ). Fonts are referenced only as `R.font.<name>` from main code, because release resource shrinking
  removes an unreferenced font; `FontBudgetTest` holds all of `res/font` to 3 MiB.
- `TypePairing` is `{ HERO_ONLY, EDITORIAL }`; its names are persisted by AP1. An all-serif value can be
  added later without a migration, because an unknown stored value reads as the default.
- The hero spacing (1.25x line height, +0.01em) comes from the owner (Q1) and is a named constant in
  `PftTypography.kt`. A custom font without `tnum` falls back to proportional digits; that is accepted.
- Pixels: `token-sheet-type-*-light-1x-editorial.png` and the dark twins show the two type pages under
  `EDITORIAL`. The token sheet's two type page titles read "Type scale: display, headline" and
  "Type scale: title, body, label" (DS6h dropped "(sans)", which is wrong under `EDITORIAL`).

### Shapes

| Shape | Radius |
|---|---|
| extraSmall / small / medium / large / extraLarge | 6 / 10 / 16 / 24 / 32dp |
| `PftTheme.shapes.glyph` | 30% of the badge side |
| `PftTheme.shapes.card` | 22dp (the balance card) |
| `PftTheme.shapes.island` | 18dp (`GroupedSection`, day groups) |
| `PftTheme.shapes.quickAction` | 17dp (Home quick actions) |
| `PftTheme.shapes.tabBar` | 50% (a full pill) |

### Spacing

`PftTheme.spacing`: `xxs` 2, `xs` 4, `sm` 8, `md` 12, `lg` 16, `gutter` 20 (screen edge), `xl` 24,
`xxl` 32, `huge` 48 dp.

### Colour

There are five curated palettes (`ThemePalette`, DS6b; Cobalt added by the Cobalt plan, K1), each an `object` in `ui/theme` with a light and a dark
`PaletteColors`, generated offline and pinned to the owner-approved mockup values (each file's KDoc lists the
provenance and every override). **The user picks the light palette and the dark palette independently**
(`ThemePrefs.lightPalette`, `ThemePrefs.darkPalette`, both `GRAPHITE` by default); `PftTheme` uses
`if (dark) darkPalette else lightPalette` by the *resolved* mode, so a forced mode on a system of the other kind still
gets the right one. Dynamic colour ignores both. The `ThemePalette` names are persisted by AP1, so they are permanent:
never rename or remove one, and never depend on the order of the entries.

| Palette | `ThemePalette` | Primary, light | Primary, dark | Look | CIELAB hue distance from money red / green (light, then dark) |
|---|---|---|---|---|---|
| Graphite (default) | `GRAPHITE` | `#3B4350` | `#C9CED8` | grey with a platinum accent | 125.7 / 116.6, 118.3 / 116.3 |
| Midnight | `MIDNIGHT` | `#7D5F26` | `#D4B572` | navy with a brass (light) or champagne (dark) accent | 44.7 / 73.0, 56.1 / 69.3 |
| Ivory | `IVORY` | `#2E3A59` | `#AEB9DA` | warm paper with an ink-navy (light) or periwinkle (dark) accent | 113.6 / 128.7, 110.4 / 124.2 |
| Aubergine | `AUBERGINE` | `#5B2A66` | `#D9BE8A` | deep plum with a champagne accent in dark | 74.8 / 167.5, 55.4 / 70.0 |
| Cobalt | `COBALT` | `#2251FF` | `#9DB1FF` | cobalt blue on a cool white canvas (white islands); periwinkle on a blue-tinted ink in dark | 97.5 / 144.8, 104.2 / 130.3 |

Graphite stays the default until the Cobalt plan's K4 flips it (`docs/plans/2026-11-25-m1a-cobalt-redesign.md`).

#### Cobalt and the brand tokens (Cobalt plan 2026-11-25, K1)

`CobaltPalette` is generated with Graphite's procedure (a CAM16 / HCT run outside the repository, from the mockup
hues) and pinned to the owner-approved mockup (`.cobalt-light`, `.cobalt-dark` in
`docs/plans/2026-11-25-home-redesign-mockup.html`); its KDoc lists the provenance and every override:

| Mockup token | Role | Light | Dark |
|---|---|---|---|
| `canvas` | `canvas` | `#F5F7FB` | `#0B0E17` |
| `island` | `island` | `#FFFFFF` | `#151A27` |
| `on` | `onSurface`, `onBackground` | `#0E1B3D` | `#E8EBF4` |
| `onvar` | `onSurfaceVariant` | `#687187` | `#8E97AD` |
| `line` | `outlineVariant` | `#E9ECF3` | `#222838` |
| `primary` / `icon` | `primary` | `#2251FF` | `#9DB1FF` |
| `primary-soft` | `primaryContainer`; `wash` in dark | `#E7EDFF` | `#1D2647` |

The mockup values that failed WCAG are adjusted, each to the nearest value that passes (owner accepted):

- **Light `onSurfaceVariant` `#687187`** (mockup `#7A8399`, 3.6:1 on the canvas).
- **Dark tab bar `nav` `#151A27`** (mockup `#181D2B` gives the active pill 2.95:1; the island tone gives 3.05:1).
- **Dark `primary` `#9DB1FF`**, the mockup's `--icon`: the mockup's `#2251FF` fails as text on dark. The filled brand
  stays `#2251FF` in `BrandColors.brand`.
- **Faded card text** is `onCard` at 68% alpha (and solid below 24sp), not the mockup's 55%.

Two more adjustments were forced by the pins, because `ColorContrastTest` holds `onSurfaceVariant` to 4.5:1 on every
surface role and on the header glow at its strongest, and the pinned light `#687187` and dark `#8E97AD` only reach that on
a narrow band of surfaces:

- **The M3 surface ladder is compressed** for the roles the mockup does not pin: light `surfaceDim` and
  `surfaceContainerHighest` tone 97, `surfaceContainerHigh` 97.5, `surfaceContainer` and `surface` 98,
  `surfaceContainerLow` 99; dark `surfaceContainerHighest` tone 18 and `surfaceBright` 19 (M3 says 22 and 24). Screens
  read `canvas` and `island` (pinned), so only M3's own filled components (text fields, menus, sheets) show the narrower
  ladder.
- **Light `wash` is white**, not `primary-soft` (`#687187` over `primary-soft` at the glow's 72% is 4.28:1). The header
  glow is opt-in and off by default.

**Brand tokens.** M3 has no role for a filled brand, the balance card's gradient or the floating tab bar, so
`PaletteColors.brand` is a `BrandColors` (`brand`, `onBrand`, `brandSoft`, `onBrandSoft`, `cardStart`, `cardMid`,
`cardEnd`, `onCard`, `cardGhost`, `cardGhost2`, `nav`), read as `PftTheme.brand` (a `staticCompositionLocalOf`, like
`PftTheme.surfaces`). Cobalt pins the mockup values:

| Token | Light | Dark |
|---|---|---|
| `brand`, `onBrand` | `#2251FF`, `#FFFFFF` | `#2251FF`, `#FFFFFF` |
| `brandSoft`, `onBrandSoft` | `#E7EDFF`, `#2251FF` | `#1D2647`, `#9DB1FF` |
| `cardStart` to `cardMid` to `cardEnd` | `#3463FF`, `#2251FF`, `#1B44E0` | `#2D5BFF`, `#2251FF`, `#1A3FCC` |
| `onCard` | `#FFFFFF` | `#FFFFFF` |
| `cardGhost`, `cardGhost2` | `#C7D4FF`, `#E2E9FF` | `#22306A`, `#182148` |
| `nav` | `#FFFFFF` | `#151A27` |

The other four palettes and dynamic colour **derive** theirs (`derivedBrand(scheme, island)`, no hand-picked values):
`brand` and the three card stops are `primary` (a flat card), `onBrand` and `onCard` are `onPrimary`, `brandSoft` and
`cardGhost` are `primaryContainer`, `onBrandSoft` is `onPrimaryContainer`, `cardGhost2` is `outlineVariant`, `nav` is the
island. `PaletteColors.brand` defaults to `derivedBrand`, so only Cobalt passes its own. `ColorContrastTest` checks the
pairs for every palette and mode, and for dynamic colour across five tones either side of the M3 mapping: `onBrand` on
`brand` and `onCard` on each card stop at 4.5:1; faded `onCard` (68%) on each card stop, `brand` on the nav, canvas and
island, and `onBrandSoft` on `brandSoft` at 3:1; `onSurfaceVariant` on the nav at 4.5:1. If a derived pair fails, fix
`derivedBrand`; never exempt a palette.

**Blue never for text.** App-drawn text (titles, body, amounts, captions) is never `primary` or `brand`. M3 draws
`primary` as text in a few controls (`TextButton` labels, a focused field's label); that is accepted as a control-label
exception (`#2251FF` light, `#9DB1FF` dark, both above 4.5:1). Money tokens are unchanged by Cobalt: they are global
(`MoneyColors`) and pass on every Cobalt surface.

**White islands.** Cobalt light is the one palette whose island is white (owner-approved, D10); `GroupedSectionColorsTest`
says so ("never white, except Cobalt light") with Graphite light as the positive control. Light
`surfaceContainerLowest` is still white and still never a card, sheet or dialog fill.

To pick: choose the light palette for daytime use and the dark palette for night, independently (for example Ivory by
day and Midnight by night). Add a palette by adding one `<Name>Palette.kt` (the Graphite procedure), one `ThemePalette`
entry (its brand tokens are derived unless it passes its own), its pins in `ThemePaletteTest`, and re-running the loops in `ColorContrastTest`, `AccentHueTest` and
`ChipColorsTest`, which cover every entry automatically.

**Accent safety.** The accent is for interactive chrome only (FAB, selected navigation item, buttons, switches, focus),
never for status or meters. Hue separates it from money green and red (at least 40 degrees of CIELAB hue,
`AccentHueTest`, every palette). It does **not** separate it from warning amber: brass and champagne sit 1 to 7 degrees
from it, by the owner's choice, so the hue rule exempts amber on purpose. What keeps a warning from reading as an accent
is that a `StatusChip` always carries a word, and that its colours come from `MoneyColors`, identical under every palette
(`ChipColorsTest`). The debug catalogue's Accent page puts an Unverified pill beside a selected navigation item and the
FAB for the manual check in Midnight dark and Aubergine dark.

The default scheme is **Graphite + platinum** (`GraphitePalette`, owner decision 2026-10-30). It was
generated offline with material-color-utilities 0.3.0 from custom tonal palettes, with the
owner-approved mockup values pinned over the generated roles; its KDoc lists the provenance and every
override. The primary is platinum: light `#3B4350`, dark `#C9CED8`. The accent is for interactive
chrome only, and sits at least 40 degrees of CIELAB hue from money green and red (`AccentHueTest`).
Dynamic (Material You) colour is **opt-in** (`ThemePrefs.dynamicColor`, default off); money colours
are the same either way. Screens never hardcode a colour or a font: `ThemeLiteralScanTest` fails on
any `Color(...)`, named `Color.*` (except `Transparent` and `Unspecified`), `FontFamily` or `R.font.*`
outside `ui/theme`. See `MoneyColors` (KDoc) and plan §3.3. `ColorContrastTest` pins every pair at
WCAG 4.5:1, sweeping greys over light tones 87 to 100 and dark tones 0 to 24, and asserts every
Graphite surface role lies inside that band. Light `surfaceContainerLowest` is white: never use it as a
card, sheet or dialog fill. Never draw money text on `surfaceVariant`:
dark `negative` on it is 4.23:1. `ColorContrastTest` pins two things: the sweep excludes
`surfaceVariant` on purpose (a test asserts the ratio is still below 4.5, so the exclusion is not
dropped by accident), and the backgrounds DS5 components draw money on (the island in both styles, every
`chipColors` pill) are asserted to be swept surface roles and never `surfaceVariant`. It cannot stop
a future component choosing `surfaceVariant`: that stays a code-review rule, and a new money
surface should add its colour to this test.

### Surfaces and window colour (DS6c, UI-12, UI-18)

`PftTheme.surfaces` (`PftSurfaces`, from the pure `pftSurfaces(palette, style, dark, glow)`; a
`staticCompositionLocalOf`, so a palette change recomposes the whole tree, which only happens when the user
changes a setting) is the one place a screen gets its background and card colours. `BackgroundStyle` has two
persisted names, permanent like the palettes: `TONAL` (the default) and `FLAT`.

| Style | `canvas` (screen, header, bottom bar) | `island` (`GroupedSection`, cards) | `islandEdge` | `islandHighlight` |
|---|---|---|---|---|
| `TONAL` | `PaletteColors.canvas` | `PaletteColors.island` | `outlineVariant`, 1 physical px | dark: white at 4.5% alpha; light: transparent |
| `FLAT` | `scheme.surface` | `scheme.surfaceContainer` | transparent | transparent |

`PftSurfaces.glow` (DS6d) is the palette's `wash` while `ThemePrefs.headerGlow` is on, else `null`, in either style (the glow itself is described under "One-hand layout").

- The canvas and island of each palette are the owner-approved mockup values (`canvas`, `island`), pinned in the
  palette files; the island is lighter than the canvas in every palette and both modes, and never white except in
  Cobalt light (owner-approved, Cobalt plan D10).
  `FLAT` reproduces the pre-DS6c look exactly.
- **Dynamic colour** has no such roles, so they are derived (`dynamicPaletteColors`): canvas is
  `surfaceContainerHigh` (light, tone 92) or `surfaceContainerLowest` (dark, tone 4); island is
  `surfaceContainerLow` (tone 96 light, 10 dark).
- **Consumers.** `CollapsingHeaderScaffold` (header and list background), `BottomActionBar` and `MainActivity`'s
  root `Surface` paint the canvas. `GroupedSection` fills with the island and, for TONAL, draws the edge as a
  vertical-gradient border from the hairline lit by the highlight (dark) to the plain hairline (`groupedSectionColors`
  is the pure function). A later `NavigationBar` (A5/C4) uses the island for its container. Rows inside an island
  must not paint their own background: use `GroupedSectionDefaults.listItemColors()`.
- **The white-card rule.** Light `surfaceContainerLowest` is tone 100, white. It must never be a card, sheet or
  dialog fill (owner decision: no white cards). `ColorContrastTest` sweeps `canvas` and `island` for every palette
  and mode (money, `onSurface`, `onSurfaceVariant` at 4.5:1; `primary` at 3:1) and asserts the island is never
  `surfaceVariant`; the white-card rule itself stays a review rule.
- **Window colour and the first frame.** Before Compose draws, the window paints `pft_window_background`
  (`res/values` and `values-night`), which equals Cobalt's `canvas`, the default (`#F5F7FB` light, `#0B0E17` dark) (`WindowThemeTest` pins this).
  The resources cannot know the user's palette, so every palette's `canvas` and flat `surface`, and the derived
  dynamic canvas, island and surface, stay within `WINDOW_FLASH_MAX_TONE_DELTA` (8 tones of CIE L*) of it in the
  same mode (`WindowFlashToneTest`, `ThemeResolutionTest`): a palette change can shift the first frame by a barely
  visible step, never light to dark. `SyncWindowBackground(window)` (a `SideEffect` in `ui/theme/WindowBackground.kt`,
  called once inside `PftTheme` from `MainActivity`) then sets the window background to the live canvas, so predictive
  back, IME gaps and configuration changes show the canvas. `FLAG_SECURE` stays the first line of `onCreate`,
  followed by `enableEdgeToEdge()`. A forced light or dark mode against the system's night mode is AP1's
  `setApplicationNightMode` item.
- **The debug catalogue** has a Background (Tonal / Flat) row and a Glow (off / on) row.

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
  25% of the width with a fade, and the outgoing one out toward the start; a pop is the reverse.
  `PftNavHost` (A5f) passes all four as the NavHost's `enter/exit/popEnter/popExit` transitions, for
  every route. **Predictive back (A5f result):** the manifest sets `enableOnBackInvokedCallback="true"`.
  On an API 35 emulator with "Predictive back animations" on, a slow back swipe from the debug
  catalogue back to Home showed the pop transition tracking the finger (Home sliding in from the
  start, the catalogue following the finger out to the end), and a released swipe committed the pop
  and landed on Home with the app still running. No glitch was observed. Verified through
  `uiautomator dump` mid-gesture (node bounds), because `FLAG_SECURE` blanks screencap, so that
  check could not show the *look*. **Owner-approved 2026-11-03:** the owner watched the animation, on the build current at 2026-11-03 (navigation 2.8.9), on a
  visible emulator and approved its look.
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
- **Header glow (UI-13, DS6d).** An optional soft tint behind the header, **off by default**
  (`ThemePrefs.headerGlow`, `PftTheme.surfaces.glow == null`). With it on, `Modifier.headerGlow`
  (`ui/common/HeaderGlow.kt`) draws a radial gradient of `PaletteColors.wash` (the mockup's `pc`;
  with dynamic colour, `primaryContainer`) centred on the header's top-centre point.
  - **Shape.** `glowAlpha(distance, end)`: 72% (`MAX_GLOW_ALPHA`) at the top edge, about 28% at 38% of the
    reach (the mockup's stops), exactly 0 from `end` on, never increasing. Its whole strength is multiplied
    by `1 - collapsedFraction` (`glowStrength`), so the 64dp bar is plain canvas.
  - **Reach.** `glowEnd(heroTop, bodyHeight)` is `heroTop - 24dp` (clamped at 0) for a Dashboard header
    with a hero, and 0.6 x the header height for one without. The gradient is a circle on the top edge,
    so **the hero row is always untinted**: money is never drawn on the glow, and a test checks every
    pixel of the hero's top row equals the canvas.
  - **No recomposition.** `HeaderPolicy` writes the hero's measured top into a float state that only the
    draw lambda reads, and the draw lambda reads `collapsedFraction` too, so a scroll frame redraws the
    header but never recomposes it (the DS4 rule). `HeaderGlowScaffoldTest` counts the recomposer's
    changes across scroll frames.
  - **Contrast.** The title, actions and (defensively) money sit on the tint, so `onSurface`,
    `onSurfaceVariant` and the three money tokens are swept at 4.5:1 against the wash at 72% over the canvas
    (and over the flat surface), for every palette and mode, by contrast and not by the tone band (Ivory and
    Aubergine light fall to tone 86.5 to 86.9 there). The dynamic wash is swept by tone, because
    Robolectric's synthetic system palette puts `primaryContainer` at tone 49 to 60, which no device does.
- **Header layout rule (owner decision Q10).** Under the default `HERO_ONLY` pairing all titles are
  sans and the serif is the hero number only; under `EDITORIAL` the titles (`headlineLarge` on a
  `Standard` header, `titleLarge` on a `Dashboard` header and on the collapsed bar) are in the display
  family and the subtitle stays sans. The title is read-only (no click action), horizontally centred, in the upper band, with an
  optional one-line subtitle under it.

  | Style | Title | Lower band (thumb zone) |
  |---|---|---|
  | `Dashboard` | `titleLarge`, top edge about 12% down the expanded header; at least 24dp clear above the hero | the hero (display font), its caption, and the chevron period stepper at the end of the caption row |
  | `Standard` | `headlineLarge`, centred in the middle of the expanded area | none (a hero is ignored) |

  The top row holds only secondary `actions` (back, search, filter, overflow); add actions go in the
  FAB. **Header actions are icon-only (UI-14, DS6e):** only `HeaderIconButton`s go in `actions`. The
  slot stays a slot because an overflow `DropdownMenu` needs a `Box` anchor around its button, so the
  rule is a convention checked by `CollapsingHeaderScaffoldTest` on both samples (every action is one
  labelled button, at least 48dp, with no text node). On collapse the title cross-fades and scales into the 64dp bar's `titleLarge` title, and the
  subtitle and lower band fade out (three times faster than the collapse). While fully expanded
  only the large title exists in the tree, and while fully collapsed only the bar title, so
  assistive technology never reads the title twice.
- **`HeaderIconButton(symbol: PftSymbol, label: String, onClick)`** (`ui/common`) is the only
  control a header's `actions` row holds: an M3 `IconButton` around a 24dp `Icon` of a registry
  symbol, tinted `onSurfaceVariant`, announced as a button named `label`. A blank `label` throws
  `IllegalArgumentException`, so an unlabelled icon cannot ship. Compose gives it a touch target of at
  least 48dp (`touchBoundsInRoot`; the drawn button is 40dp, and the 40dp semantics bounds do not
  include the 48dp target, so tests read `touchBoundsInRoot`). It is a component and not a restyle
  wrapper because it enforces the owner's contract (no text, mandatory label, registry symbol) and
  has many named consumers: Home search and filter, list and settings back, budget-detail overflow,
  and the period stepper below.
- **Period.** `period: HeaderPeriod(onPrevious, onNext)` (Dashboard only) draws the chevron stepper
  (`CHEVRON_LEFT` / `CHEVRON_RIGHT` through `HeaderIconButton`; "Previous period" / "Next period" for TalkBack) and makes a horizontal swipe of more than 48dp on
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
- **`EditFormScaffold` and `NameField` (A5d).** Every create/edit screen is a `Standard` header plus a
  `BottomActionBar` holding the one primary action (Save, Continue), which is disabled while `saving`.
  An optional back `HeaderIconButton` goes in the action row (48dp target; Setup has none). The
  scaffold adds **no** `imePadding()`: `CollapsingHeaderScaffold` already wraps its bottom bar in one,
  and a second would pad twice. `NameField` is an M3 `OutlinedTextField` (single line, words
  capitalised) with the error as supporting text; it never clips what the user typed, so a length
  error is shown, not enforced. Goldens: `form-light`, `form-dark`, `form-light-2x` (`FormScreenshotTest`);
  the catalogue's Forms page is live.
- **`snackbarHost` slot (B2a).** `CollapsingHeaderScaffold` passes a `snackbarHost: @Composable () -> Unit`
  (empty by default, so no existing screen or golden changes) to M3's `Scaffold`, which places it above
  the `bottomBar`, or above the bottom inset when there is none, and moves it clear of the FAB.
  `CollapsingHeaderScaffoldTest` shows a snackbar through the slot with a 48dp navigation-bar inset
  and checks it ends above it. Used for the delete Undo (`UndoDeleteSnackbar`) and the fixed
  "Couldn't ... Try again." messages of the entity lists.
- **`EntityListScreen` (B2a).** The one screen every entity list is (accounts now; categories and tags
  in B4 and B5; loans and goals later): a `Standard` `CollapsingHeaderScaffold` with a labelled Back,
  the **active** rows in a `GroupedSection`, a collapsed **"Archived (n)"** toggle (a 48dp button that
  says "Collapsed" or "Expanded", with an expand chevron) over a second island, an `EmptyState` when
  there are no rows at all, and the add FAB. A row is an M3 `ListItem` (`EntityRow`: title, optional
  supporting line, optional leading `CategoryGlyph`, optional trailing overflow button that opens a
  menu of exactly its `RowAction`s; `indent` insets a child by the 24dp spacing step). The overflow
  button is labelled "Actions for <title>" and the badge is spoken as the row's glyph label when it has one (a category's and an account's are silent: their text already says it), so
  nothing is read twice. `add` (a `RowAction`) is the FAB's label and callback and the empty state's
  button; null hides both. The FAB sits in the scaffold's slot, so the list ends with a 96dp spacer
  that keeps the last row clear of it. An optional `header` slot (B4a) draws a block as the first list
  item, above the rows and above the empty state; the categories list puts its Expense/Income segmented
  switch there. `EntityRow` and `RowAction` stay plain data: each consumer's
  mapper (`AccountRowMapper`, `CategoryRowMapper`) owns what a row says. Goldens `entity-list-light`, `-dark`,
  `-archived-light` (Archived expanded), `-empty-light`, `-empty-dark` (`EntityListScreenshotTest`) and
  `categories-list-light`, `-dark`, `-archived-light` (`CategoriesScreenshotTest`, 1100dp and 1500dp tall
  so the Archived section is in the picture).
- **`PftBottomSheet` and `AccountPicker` (B2c).** Pickers (account, category, tag, date) open as
  bottom sheets, never as top dropdowns. `PftBottomSheet(title, onDismiss) { close -> ... }` is the only
  place that opts into `ExperimentalMaterial3Api` for `ModalBottomSheet`, so an M3 upgrade touches one
  file. It adds no styling of its own: the sheet is M3's, so it takes `MaterialTheme.shapes.extraLarge`
  (32dp, top corners only) and the theme's sheet container colour (never white `surfaceContainerLowest`),
  with a dimming scrim and the standard slide. It opens at its full content height (`skipPartiallyExpanded`)
  so a short picker is short and a long one scrolls. The title is a `titleLarge` heading. Back, a scrim tap,
  a swipe down and the content's `close` all slide the sheet out and then call `onDismiss` once; the consumer
  removes the sheet from the composition. `AccountPicker` is a stateless list of radio-style `ListItem` rows
  (type badge, name, the list's supporting line): the selected row has `selected` semantics and a primary-
  coloured check, and a tap picks, closes, and ignores further taps. The consumer's ViewModel supplies
  `AccountService.active()`. Goldens `account-picker-{light,dark,empty-light,empty-dark}`
  (`AccountPickerScreenshotTest`; the sheet's dialog window is drawn directly because `captureToImage`
  cannot read it under Robolectric). `CategoryPicker` (B4b) and `TagPicker` (B5b) are separate components
  and share only the sheet.
- **`CategoryPicker`, the symbol grid and the colour swatches (B4b).** Three choice grids, all stateless, all a
  `selectableGroup` of `Role.RadioButton` cells with the chosen one marked by `selected` semantics and a
  2dp primary ring (`selectionRing`: a shape cue, never colour alone), each cell at least 48dp, laid out by
  `TileGrid` (a `Column` of `Row`s sized from the available width, no experimental `FlowRow`). **TalkBack
  rule:** a cell is announced once. `CategoryPicker` cells have visible text, so the badge is decorative
  (`CategoryGlyph(name = null)`) and the cell is read from its text, plus "Under <parent>" for a
  sub-category (also shown, so two same-named sub-categories differ on screen and by ear). `SymbolGrid` and
  `ColourSwatches` cells have no text, so the cell itself carries the spoken name (the key as words, "Local
  cafe", "Amber") and the badge inside is decorative. `CategoryPicker` shows each top-level category and its
  sub-categories as one group starting a new row and keeps the service's order; with `topLevelOnly` it is the
  form's parent field. `SymbolGrid` draws every badge in the category's chosen colour and starts with a "No
  symbol" cell that shows the name's first letter. A swatch is the colour's own badge fill with an outline in
  its content colour and a check mark when chosen. Goldens `category-picker-{light,dark,parent-light,parent-dark}`
  (the sheet window drawn with `drawSheetWindow`), `category-symbol-grid-{light,dark}`,
  `category-swatches-{light,dark}` and the form `category-edit-{new-light,new-dark,editing-light}`.
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

`MoneyText(amount: Paise, semantic: MoneySemantic, modifier, size: MoneySize = Row, animate = false,
onColor = Color.Unspecified, fadeDecimals = false)` is the only way an amount is drawn (plan §5.2).

- **Format.** `PaiseFormat.format` from `:core:money` returns a sign-free `₹1,23,456.78`. `MoneyText`
  prepends the sign (`moneySign`) and never reformats; there is no float anywhere in the path.
  U+2212 (a true minus, not a hyphen) for an expense, a negative balance or a negative `Net`, `+` for
  income or a positive `Net`, nothing for a transfer, a budget figure, a zero `Net` or a non-negative
  balance.
- **Negative amounts: the caller's contract.** Transaction amounts are positive magnitudes and the
  direction is the semantic, so a negative `Paise` is rejected with `IllegalArgumentException` for every
  semantic except `Balance` and `Net` (the two signed ones). A wrong sign would misreport the money, so `MoneyText` fails closed instead
  of drawing it, and that throw happens during composition (it would crash the screen). So **callers must
  pass magnitudes (non-negative `Paise`) for every semantic except `Balance` and `Net`**, and the ViewModel or
  mapper that builds the UI state owns that: it takes `abs` of a signed difference only where the
  semantic says the direction (for example a budget's remaining amount shown with `Budget(OVER)`), and
  validates at that boundary, with a test, rather than relying on the composable's `require`. C4
  (transaction rows and Home totals), C5 (detail) and D4 (budget figures) each have such a mapper.
- **Tone** (`moneyTone(semantic, amount)` → `MoneyTone` → colour; the size never changes it):

  | Semantic | Tone |
  |---|---|
  | `Income` | Positive (green), in totals and in rows |
  | `Expense` | Neutral at every size, in rows, totals and heroes (Cobalt plan D5; it was red in totals before) |
  | `Net` | Neutral whether negative, zero or positive: a signed difference such as a day total or the banner's net |
  | `Transfer` | Neutral |
  | `Balance` | Neutral, except Negative when the amount is below zero (Q3) |
  | `Budget(UNDER / AT_THRESHOLD / OVER)` | Positive / Warning / Negative |

  Red is kept for what is actually wrong: a negative balance and an over-budget figure. Neutral is
  `onSurface` (or `onColor`, below); the others are `PftTheme.money` tokens, never the accent. `BudgetState` is
  the UI's own three-value enum until the budget module (D1) exists; D4 maps core's `BudgetProgress`
  onto it.
- **Spoken.** The semantics description is "Expense, ₹250.00" (direction word first, from string
  resources: Income, Transfer, Net, Balance, Negative balance, Under budget, Budget near its limit,
  Over budget), so colour is never the only cue. A negative `Net` says its sign aloud: "Net, minus
  ₹250.00" (a positive or zero one reads "Net, ₹250.00"; the `+` is only drawn).
- **`onColor`: money on the blue card** (Cobalt plan D4). `onColor` draws a neutral figure in a given
  colour, for a fill the theme does not sweep (the balance card's `onCard` on the brand gradient). It is
  allowed only with a `Neutral` tone and throws `IllegalArgumentException` otherwise, so no red, green
  or amber is ever drawn on the blue. The tone is decided by the data, so a negative `Balance` (red)
  with an `onColor` also throws: the card shows the month's spend, an expense, which is neutral.
- **`fadeDecimals`: the hero's decimals** (hero only; ignored at other sizes). The decimals (the `.`
  and the two paise digits) are drawn at `FADED_DECIMAL_ALPHA` (0.68) of the figure's colour, as a
  colour span, while the hero is at a large-text step (24sp or more). Below that, after the hero has
  shrunk to fit, they are solid, because the faded colour only clears 3:1 (large text), not 4.5:1.
  With `animate` each rolling digit takes the same colour. Spoken text and shown text are unchanged.
- **Sizes.** `Hero` is `amountHero`, `Large` is `amountLarge`, `Row` is `amountRow`.
- **Hero shrink-to-fit.** The hero is one line at the largest of `heroSizeSteps(its own size)` (from the ladder
  `HeroSizeSteps`: 48, 40, 34, 28, 24, 20, 16, 12sp, the plan's four plus four more; a 34sp Bricolage hero uses the
  six at or below 34sp) whose width fits the width it is given, measured with
  a `TextMeasurer` (so it tracks font scale and density) inside `BoxWithConstraints`. The line height
  scales with the size and the optical size stays at the 48sp display cut. The steps go below the plan's
  28sp because the plan's example amount (Rs 1e10) is shorter than the real limit `Paise.MAX_AMOUNT`
  (Rs 1e11, `₹1,00,00,00,00,000.00`): at 200% font scale that needs 16sp on 393dp and 12sp on 320dp
  (Android scales sp to dp non-linearly, so 12sp is still about 24dp tall there). If even 12sp does not
  fit (an extreme figure on a very narrow window) the text wraps rather than being cut off, because a
  truncated amount would misreport the money. The line break may fall right after the sign.
- **Rolling digits** (`animate = true`). Each character is its own `AnimatedContent` slot, keyed from
  the end of the string so the units digit keeps its column when the length changes; a rising digit
  slides in from below, a falling one from above, on `PftMotion.smooth` (`smoothOffset` for the slide). Per-character slots cannot wrap, so a hero that does not fit even at 12sp is drawn as plain wrapping text with no roll (it must never be cut off).
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
`large` shape, filled with `PftTheme.surfaces.island` (never `surfaceVariant`, never white except in Cobalt light; with TONAL a
one-pixel edge, lit along the top in dark mode; see "Surfaces and window colour"), one
physical-pixel `outlineVariant` divider between rows, inset 16dp from the start (N rows give N-1
dividers). A row with `onClick` is clickable (`Role.Button`) and at least 56dp tall. A rows lambda rather than a
composable slot lets the section own the dividers. M3 `ListItem` paints its own `surface` container,
so rows use `GroupedSectionDefaults.listItemColors()` (transparent). `Dp.Hairline` is not used for the
divider because it lays out at zero height, which cannot be measured and relies on an unclipped
canvas stroke.

### `TransactionRow` and `DayGroup` (K5)

`TransactionRow(model: TransactionRowModel)` is the one way a transaction is drawn in a list; `DayGroup(day,
today, rows, total?)` puts a day header over one `GroupedSection` island of them. Both are in `ui/common` with
`TransactionRowSamples.kt` (preview, catalogue page "Transactions", goldens `transaction-row-*` and `day-group-*`).

- **Plain data.** `TransactionRowModel` holds text, a `GlyphSource` and a `Paise` magnitude with a
  `MoneySemantic` (expense, income or transfer only; anything else, or a negative amount, throws), so the block
  knows no entity. The consumer's mapper resolves names.
- **Look (D3, D5).** A 40dp `CategoryGlyph` tile (`PftShapes.glyph`), the title in `bodyLarge` medium `onSurface`
  (set explicitly: an island `Surface` supplies no content colour), the meta "category · account" in `bodySmall`
  `onSurfaceVariant`, the amount as `MoneyText` (an expense is neutral with a minus, an income green with a plus,
  a transfer unsigned and neutral), an optional "Needs review" `StatusChip(Warning)` under the amount and optional
  neutral `StatusChip` tag pills that wrap under the meta. Rows are separated by `GroupedSection`'s hairline dividers;
  there is no card inside the island. The day header is `labelMedium` `onSurfaceVariant` with the net total
  (`MoneyText(Net)`, neutral at either sign) at the end, only when given.
- **One node.** The row sets `clearAndSetSemantics` with one description (title, category, account, tags, the
  amount's "Expense, ₹486.00", then the chip's words), so a screen reader says each once. Plain merging is not
  enough: `MoneyText` and `StatusChip` are merged nodes, and a merged child is not folded into its parent. The tile
  is decorative (`name = null`). With `onClick` it is a `Role.Button` and at least 56dp tall.
- **Large text.** The title wraps, never truncates. Below a font scale of 1.5 the meta line is one line (ellipsis when the amount leaves it too little room; the spoken description carries the whole meta; K9). From a font scale of 1.5 the meta wraps whole too, and the amount sits under the tile and
  text at the full row width. A `Row`-size `MoneyText` does not shrink, so the largest amount (`Paise.MAX_AMOUNT`)
  fits one line at 100% but wraps at 200% on a 320dp window (never cut: the test asserts every character is laid out).
- **Day label.** "Today" or "Yesterday" from the passed-in `today` (the caller owns the clock and zone), else the
  device-locale `yMMMd` date. The header is a heading.

### `BalanceCard` and `QuickActionRow` (K6)

Home's two dashboard blocks, in `ui/home` with `HomeSamples.kt` (preview, catalogue page "Home", goldens
`balance-card-*` and `quick-actions-*`). Plain data in, no entity or ViewModel; nothing renders them on Home until
slice C (C4d) wires the month's totals.

- **`BalanceCard(model: BalanceCardModel)`.** The month's spend as a `MoneyText(Expense, Hero, onColor = onCard,
  fadeDecimals = true)` on the `cardStart`, `cardMid`, `cardEnd` gradient, `PftShapes.card`, with two decorative slabs
  (`cardGhost2`, `cardGhost`) peeking above it. Over the hero: the caller's title and the caption "Spent this month".
  Under it: "In" and "Out" (unsigned `PaiseFormat` figures in `amountRow`: `MoneyText` cannot draw income on the blue),
  side by side when both fit and stacked otherwise. The budget meter and its label are drawn only when
  `BalanceCardModel.budget` is given (hidden until slice D, Cobalt plan D6).
- **Gradient and ring.** The gradient is the mockup's CSS `linear-gradient(135deg, start 0%, mid 55%, end 100%)`:
  stops at 0, 0.55 and 1 on a true 45 degree line through the card centre, `(w + h) / sqrt 2` long (`cardGradientLine`),
  not Compose's corner-to-corner default. Over it, behind the text and clipped by the card, a decorative ring: the
  mockup's 200dp circle with a 28dp border added outside (256dp across, stroke radius 100 to 128), 68dp in from the
  right edge and 88dp down, `onCard` at `RING_ALPHA` (0.08). It is drawn, not a node.
- **Contrast (D4).** Every text is solid `onCard`; hierarchy is size and weight. The ring lightens what is under some
  text, so `ColorContrastTest` pins `onCard` over the ringed mid and end stops, over the ringed gradient at the ring's
  furthest reach toward the start (`RING_MIN_LINE_FRACTION`, 0.28) and the faded decimals over the ringed mid and
  end stops at 3:1, in every palette and mode. The ring never reaches the start corner: ringed, Cobalt light's start
  would be 4.2:1. The meter's fill is `onCard` over a track of `onCard` at `METER_TRACK_ALPHA` (0.22), held to 3:1.
- **Spoken.** The hero says "Expense, ₹48,250.40"; "In" and "Out" are one merged node each (word, then figure); the
  arrows, ghosts and meter bar have no semantics, so the meter's label is said once.
- **Large text.** The hero shrinks as `MoneyText` does. The card is inset by the gutter and has 18dp padding, so on a
  320dp window it holds 244dp: a figure of 100 crore fits one line at 200%, and `Paise.MAX_AMOUNT` wraps whole at the
  smallest step (never cut).
- **`QuickActionRow(actions: List<QuickAction>)`.** Renders exactly the actions given: `QuickAction(label, icon,
  onClick, primary)`. Each is one `Role.Button` named by its label; a 52dp `PftShapes.quickAction` tile (`brand` with
  `onBrand` for the primary one, `brandSoft` with `onBrandSoft` for the rest) over a `bodySmall` medium label in
  `onSurface`. Budgets is not passed until slice D. The actions share equal columns; when the widest label no longer
  fits its share (200% text, a fourth action) the row wraps onto further lines with fewer columns, so a label is never
  broken mid-word.

### `EmptyState`

`EmptyState(message, glyph?, actionLabel?, onAction?)`: centred glyph slot (DS3's `CategoryGlyph` or a
symbol goes here), a `bodyLarge` message in `onSurfaceVariant`, and an optional tonal button. The label
and callback must be given together. It sits in a list's content; it does not fill the screen itself.

## Icons and emoji (DS3, UI-6)

**One icon family: Material Symbols Rounded** (Apache-2.0), weight 400, grade 0, optical size 24, outlined (FILL 0). Filled
variants are for selected navigation items only and are not shipped yet (A5 adds them when the nav bar exists). Not
`material-icons-extended` or `material-icons-core`: detekt `ForbiddenImport` bans `androidx.compose.material.icons.*` in our
sources (M3's own internals may still use it).

- **Registry.** `PftSymbol` (`ui/theme`) is an enum of 92 symbols, `PftSymbol(key, res)`, with `PftSymbol.fromKey(key)` (exact
  match, null if unknown). Each is a vector drawable `res/drawable/sym_<key>.xml` (24dp, viewport 960, path data from the SVG,
  black: tint it with `Icon`), 0.5 to 1.5 KB; all 92 total 73 KB.
- **Keys are permanent.** They are persisted in `category.icon_key` (`sym:<key>`), so like the application ID they are never renamed or
  removed, only added. `app/src/test/resources/pft-symbol-keys.txt` pins every key: removing or renaming one fails
  `PftSymbolTest`, and so does adding one without listing it (so additions are a visible edit). An unknown key (for example from a newer
  version synced in M2) renders the letter fallback and never crashes.
- **`icon_key` grammar** (untrusted input, parsed once by `GlyphSource.fromIconKey`, `ui/common`): `sym:<key>`, key matching
  `[a-z0-9_]{1,40}` and registered; or `emoji:<value>`, 1 to 16 UTF-16 units, well-formed, with no whitespace or control
  characters (invisible format characters such as zero-width space, word joiner, BOM and the bidi overrides are also refused;
  the zero-width joiner and tag characters are allowed, so ZWJ families, flags and subdivision flags work). Anything else, oversized
  input included, is the letter fallback (the name's first letter or digit, upper-cased). Whether a value is really a renderable
  emoji is the B4c `EmojiInputValidator`'s job when an emoji is typed or pasted into the category form.
- **`CategoryGlyph(source, name, colourKey, size, onClick)`** (`ui/common`) draws a symbol, one emoji or a letter on a
  `CategoryPalette` badge (12 hues, light and dark: `PftTheme.palette[colourKey]`; unknown name is `slate`). It is announced as the
  category name (or, with a null `name`, is decorative and silent, for a badge beside a title that already says it: category list rows),
  at a fixed dp size (`GlyphSize`: chip 28, row 40, hero 56), and is a button only when given `onClick`.
- **Emoji** are drawn by the system emoji font, so they differ between phones (accepted; symbols are the consistent option).
  `EmojiCompatInitializer` is removed from the merged manifest (it fetches a font through Google Play services);
  `ManifestHardeningTest` asserts it is absent. Compose is not affected: it uses the platform emoji font (minSdk 33).
- **Emoji input** (B4c, `ui/categories`): the category form's icon section is `IconChoice`, a Symbol | Emoji switch of
  `SingleChoiceSegmentedButtonRow` buttons (the app's existing idiom, announced as radio buttons with a selected state; not tabs,
  which would add a second idiom and a panel promise the form lacks) over `SymbolGrid` or `EmojiTab`. The side opens on Emoji when the
  category's icon is an emoji, else Symbol, then the user's choice stands (it survives recreation), and switching never changes the
  icon. `EmojiTab` is a quick-pick of 24 emoji (`QUICK_PICK_EMOJI`, an explicit ordered list of single code points from Unicode 11 or
  older) as one `selectableGroup` of `Role.RadioButton` cells with **no** description (a visible emoji is read by its Unicode name,
  so one would say it twice), a "Chosen emoji" row (the emoji as a badge in the category colour, named by the emoji itself) and a
  type-or-paste field that is a **sink**: it always shows nothing and hands the text, untouched, to the ViewModel. `EmojiInputValidator`
  (an `object`) accepts text only if core's `CategoryIcon.Emoji.isValid` (run as is, never copied: length first, so a huge paste costs
  nothing), exactly one ICU grapheme cluster, a country flag or an `Extended_Pictographic` first code point followed only by emoji
  parts (VS16, skin tone, tag characters, ZWJ joining pictographs, no dangling ZWJ), and `Paint.hasGlyph` on this device all hold.
  Keycaps are refused, whitespace is refused and never trimmed. The ViewModel keeps only a validated `CategoryIcon.Emoji` or the fixed
  `emojiRejected` flag, shown as one fixed message that never echoes the paste. Goldens `category-emoji-tab-{light,dark}`.
- **Descriptions** (plan Q7): a transaction row's glyph is its sub-category's if set, otherwise its category's; free emoji
  typed in titles render in the system font. There is no per-transaction icon.
- **Not changed in DS3 (on purpose).** DS5's `EmptyState` placeholder glyph stays as it is: its glyph slot is filled by its
  consumers with a `CategoryGlyph`. (DS4's `PeriodStepper` text chevrons were swapped for symbols in DS6e.)

### The curated list

Permanent key (Material Symbols name when different). Chrome keys are semantic; category keys keep the Material name.

| Group | Keys |
|---|---|
| Chrome | `back` (arrow_back), `forward` (arrow_forward), `add`, `close`, `check`, `search`, `filter` (filter_list), `overflow` (more_vert), `chevron_left`, `chevron_right`, `expand_more`, `expand_less`, `edit`, `delete`, `undo`, `lock`, `settings`, `calendar` (calendar_month), `info`, `warning`, `error`, `visibility`, `visibility_off`, `download`, `upload`, `tune`, `sort`, `transfer` (swap_horiz), `tag` (label), `history`, `check_circle`, `south_west` (In, the Income quick action), `north_east` (Out), `more_horiz` (the More tab) |
| Food and drink | `restaurant`, `local_cafe`, `fastfood`, `local_bar`, `bakery_dining`, `local_pizza`, `icecream` |
| Shopping | `shopping_cart`, `shopping_bag`, `grocery`, `checkroom`, `redeem`, `diamond` |
| Transport | `directions_car`, `directions_bus`, `train`, `local_taxi`, `two_wheeler`, `flight`, `local_gas_station` |
| Home and bills | `home`, `bed`, `chair`, `bolt`, `water_drop`, `wifi`, `cleaning_services`, `handyman`, `receipt_long`, `phone_iphone`, `tv` |
| Health | `medication`, `local_hospital`, `fitness_center`, `spa`, `favorite` |
| Fun and travel | `movie`, `sports_esports`, `music_note`, `theater_comedy`, `beach_access`, `luggage` |
| Family and learning | `school`, `menu_book`, `child_care`, `pets` |
| Money and work | `work`, `payments`, `account_balance`, `savings`, `trending_up`, `credit_card`, `account_balance_wallet`, `currency_rupee`, `local_atm`, `receipt`, `subscriptions` |
| Other | `category`, `star`, `flag`, `volunteer_activism` |

DS6e depends on the chrome keys `back`, `search`, `filter`, `overflow`, `chevron_left` and `chevron_right`; `PftSymbolTest` pins them.

### Adding an icon

1. Pick the symbol on https://fonts.google.com/icons (style Rounded, weight 400, grade 0, optical size 24, fill off). The
   file name is the symbol's name: `symbols/web/<name>/materialsymbolsrounded/<name>_24px.svg` in `google/material-design-icons`.
2. Choose a permanent key `[a-z0-9_]{1,40}` and add a line `<key> <name>` to `tools/material-symbols/symbols.txt`. Chrome icons
   get a semantic key; category symbols keep the Material name. **Think before choosing: the key can never change.**
3. Download into a **new empty directory** and convert (the script treats each SVG as untrusted: it accepts only one `<svg>` with one
   `<path>` and the expected `viewBox`, rejects DOCTYPE, scripts, external references and oversized files, and keeps only the path data):
   `python3 -I tools/material-symbols/import_symbols.py --download-dir <new empty dir> --res-dir app/src/main/res/drawable`.
   It re-creates every drawable from the pinned commit (`COMMIT` in the script). `tools/material-symbols/svg-sha256.txt` is a check, not
   just a record: every file is downloaded, validated and compared with its committed hash **before anything is written**. A
   mismatch for an already-listed key aborts with exit code 1 and a message naming the key, and writes no drawable and no hash. A key
   not yet listed has its hash recorded. `git diff` must show only the new drawable and one new hash line. To move to a newer
   upstream commit, change `COMMIT` deliberately and re-run with `--update-hashes`, which accepts changed sources and overwrites their
   hashes; then review every changed drawable and hash.
4. Add the entry to `PftSymbol` (`KEY("key", R.drawable.sym_key)`) and the key to `pft-symbol-keys.txt`.
   **Then classify it.** A *category* symbol (one a category may have) must also be added to `CATEGORY_SYMBOLS`, the explicit
   list directly under the enum in `PftSymbol.kt`, in display order: the category form's symbol grid offers exactly that
   list and nothing else, so a symbol you forget is simply not offered (it fails closed). An *interface* icon (back, add,
   share ...) must **not** go in that list; add it to `interfaceSymbols` in `PftSymbolCategoriesTest` instead.
   `PftSymbolCategoriesTest` fails until every registry symbol is in one list or the other.
5. Run `./gradlew :app:testDebugUnitTest --tests '*PftSymbolTest'`, then re-record the symbol goldens
   (`./gradlew :app:recordRoborazziDebug`) and review the PNG diff. Update the list above.
6. The Apache-2.0 notice (`assets/licenses/material-symbols-APACHE-2.0.txt`) already covers every symbol from this source.
   Icons from any other source need their own licence file and a row in the third-party table.

## Previews, the token sheet and screenshot goldens

`ui/theme/TokenSheet.kt` shows every token on one sheet (type scale, amount styles, shapes,
spacing ruler, money tokens on surfaces) with fake figures only. It is the **single source** for
three consumers, so they cannot drift:

- the Studio preview (`@PreviewLightDark`),
- the debug-only design catalogue (`src/debug/.../DesignCatalog.kt`, the `dev/catalog` route of the nav host, opened from a Home header button),
  which adds toggles for light/dark, the light palette, the dark palette, dynamic colour, hero font and font scale,
  and an Accent page for the amber check,
- the Roborazzi goldens (`TokenSheetScreenshotTest`).

Component goldens (DS4) come from `HeaderScreenshotTest`, using the same `StandardHeaderSample`,
`DashboardHeaderSample` and `BottomActionBarSamples` as the previews and the catalogue:
`header-<standard|dashboard>-<expanded|collapsed>-<light|dark>.png` and
`bottom-action-bar-<light|dark>.png`, full-screen 393x851dp (about 90 to 110 KB each; the collapsed
ones are captured after a real swipe). The debug catalogue has a page for each, plus a motion page.

Content goldens (DS5) come from `ContentScreenshotTest`, captured by the bounds of the `*Samples`
composable on `surface` (393dp wide): `money-text-<light|dark>[-2x].png` (the 2x ones at font scale
2.0), `status-chip-`, `grouped-section-` and `empty-state-<light|dark>.png`, 19 to 170 KB each. The
Dashboard header sample uses a `MoneyText` hero (an expense, neutral since Cobalt K3), so its goldens show the plain text colour, not red.

Palette goldens (DS6b) come from `PaletteScreenshotTest`: `palettes-<light|dark>.png`, the five palettes' role swatches
side by side (`PaletteSwatches`, each column drawn from its own palette's colours, 393dp wide). They
render byte-identically on the debug and release unit-test variants.

Glyph goldens (DS3) come from `CategoryGlyphScreenshotTest`: every `PftSymbol` on a teal, an amber and a violet badge
(`category-glyph-symbols-<teal|amber|violet>-<light|dark>.png`, about 175 KB each) and the emoji, letter and fallback sources
in all 12 hues and three sizes (`category-glyph-sources-<light|dark>.png`, about 100 KB). They render identically on the debug
and release unit-test variants. The emoji in them are drawn by Robolectric's native emoji font, so they check layout, not artwork.

The sheet is split into `TokenSheetPages`, each short enough for one 393x851dp screen at 200% font
scale, so each page is one golden. Goldens are in `app/src/test/screenshots/`:
`token-sheet-<page>-<light|dark>-<1x|2x>.png`.

Screenshot rules:

- Native graphics (`@GraphicsMode(NATIVE)`), qualifiers `w393dp-h851dp-xxhdpi`, light and `+night`,
  font scale 1.0 and 2.0, Graphite (dynamic off) so goldens are deterministic.
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
  `BottomActionBarTest`, `MoneyTextTest`, `StatusChipTest`, `GroupedSectionTest`, `EmptyStateTest`, `PftSymbolTest`, `GlyphSourceTest`,
  `CategoryGlyphTest`), not by pixels.
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
| Figtree variable font `Figtree[wght].ttf` (Erik D. Kennedy and the Figtree Project Authors) | https://github.com/google/fonts/tree/6c1dbe27dcf7d817d4b62cec033ba77fa8e2a5ec/ofl/figtree (google/fonts `ofl/figtree`; upstream project https://github.com/erikdkennedy/figtree) | google/fonts commit `6c1dbe27dcf7d817d4b62cec033ba77fa8e2a5ec` (2026-10-30, the single DS6f pin); `wght` 300-900, default 300 | SIL OFL 1.1, **Reserved Font Name: none** | 62,712 B | `26ad3db9b31ff7dde67a91ff515d022d2f495cd506590699cf264f0bfe6fb714` | `app/src/main/res/font/figtree.ttf`, unmodified; weights 400, 500 and 600 are declared in `sansFamily()` |
| Figtree licence text `OFL.txt` | same directory, same commit | | | 4,388 B upstream | upstream `140d37233e7f3ce7313798befa9600893bcceaf41a55fa0fa5ad52f7f657a268` | `app/src/main/assets/licenses/figtree-OFL.txt` (4,383 B, shipped sha256 `4504180d840bfb94a6445f6145574256aac1c7da5e6d4be55da5b9e710772490`: identical to upstream apart from trailing whitespace trimmed by the repo's pre-commit hooks; the file already ended with a newline) |
| Plus Jakarta Sans variable font `PlusJakartaSans[wght].ttf` (the Plus Jakarta Sans Project Authors) | https://github.com/google/fonts/tree/6c1dbe27dcf7d817d4b62cec033ba77fa8e2a5ec/ofl/plusjakartasans (google/fonts `ofl/plusjakartasans`; upstream project https://github.com/tokotype/PlusJakartaSans) | google/fonts commit `6c1dbe27dcf7d817d4b62cec033ba77fa8e2a5ec` (2026-10-30, the single DS6f pin); `wght` 200-800, default 400 | SIL OFL 1.1, **Reserved Font Name: none** | 176,288 B | `89b3fb38aa0d275d7a731d0d817a4f1622b316b4d7fbdedcf02ee9099ff68bc8` | `app/src/main/res/font/plus_jakarta_sans.ttf`, unmodified; weights 400, 500 and 600 are declared in `sansFamily()` |
| Plus Jakarta Sans licence text `OFL.txt` | same directory, same commit | | | 4,402 B upstream | upstream `995c7199cab65954f545996326755daee7b63cc6b42b06c13da1f9502ab08a99` | `app/src/main/assets/licenses/plus-jakarta-sans-OFL.txt` (4,401 B, shipped sha256 `f9a8e776b4b048227f2f046b8716083eb04b1539d9ad1d4b87e81c1df28f86e3`: identical to upstream apart from one line's trailing space trimmed by the repo's pre-commit hooks; the file already ended with a newline) |
| Manrope variable font `Manrope[wght].ttf` (the Manrope Project Authors) | https://github.com/google/fonts/tree/6c1dbe27dcf7d817d4b62cec033ba77fa8e2a5ec/ofl/manrope (google/fonts `ofl/manrope`; upstream project https://github.com/googlefonts/manrope) | google/fonts commit `6c1dbe27dcf7d817d4b62cec033ba77fa8e2a5ec` (the single DS6f pin, reused in K2); name table Version 4.505; `wght` 200-800, default 200 | SIL OFL 1.1, **Reserved Font Name: none** | 164,700 B | `3ae11c49db0455a3cc33e37d380f20fdb8c7f8b41dc07625c177e3d87a9d6ae6` | `app/src/main/res/font/manrope.ttf`, unmodified; weights 400, 500 and 600 are declared in `sansFamily()` |
| Manrope licence text `OFL.txt` | same directory, same commit | | | 4,387 B upstream | upstream `58172e0c0fac2cda8a37b348164bb55e44b0e69051e557e92b1d3f6910141f7b` | `app/src/main/assets/licenses/manrope-OFL.txt` (4,386 B, shipped sha256 `d6a309fcfb963d329a93baf9df66fa5f3c23a4be2b5f67edf24d6cafecde12ac`: identical to upstream apart from one line's trailing space trimmed by the repo's pre-commit hooks; the copyright line is "Copyright 2018 The Manrope Project Authors") |
| Bricolage Grotesque variable font `BricolageGrotesque[opsz,wdth,wght].ttf` (the Bricolage Grotesque Project Authors) | https://github.com/google/fonts/tree/6c1dbe27dcf7d817d4b62cec033ba77fa8e2a5ec/ofl/bricolagegrotesque (google/fonts `ofl/bricolagegrotesque`; upstream project https://github.com/ateliertriay/bricolage) | google/fonts commit `6c1dbe27dcf7d817d4b62cec033ba77fa8e2a5ec` (K2); name table Version 1.001; `opsz` 12-96 (default 96), `wdth` 75-100 (default 100), `wght` 200-800 (default 800) | SIL OFL 1.1, **Reserved Font Name: none** (checked in the upstream `OFL.txt`; `BundledFontTest` asserts it) | 408,496 B (under the 500 KB pre-commit large-file limit, so no hook exception) | `413e7357809ddd12fd80a96a8a396de0e401638d4acd3cb3e37532f0472ac682` | `app/src/main/res/font/bricolage_grotesque.ttf`, unmodified; used by `bricolage(weight, size)` |
| Bricolage Grotesque licence text `OFL.txt` | same directory, same commit | | | 4,403 B upstream | upstream `4b5a7d8f37f5602621c8a8d7358a6a2e71317e6c231c661e15aef0275d3e07ba` | `app/src/main/assets/licenses/bricolage-grotesque-OFL.txt` (4,402 B, shipped sha256 `46ba5f18ee20ea529f21d96c0ef8637a8314c1a5cfb2aa84018bc8157cbeff41`: identical to upstream apart from one line's trailing space trimmed by the repo's pre-commit hooks; the copyright line is "Copyright 2022 The Bricolage Grotesque Project Authors") |
| Material Symbols Rounded (92 icons, SVG path data only) | https://github.com/google/material-design-icons (`symbols/web/<name>/materialsymbolsrounded/<name>_24px.svg`) | commit `10ecc14790612ca8fd164e3f4ffe9c16fc50d5b7` (2026-10-23) | Apache-2.0 | 73 KB of drawables | per-icon SVG hashes in `tools/material-symbols/svg-sha256.txt`; upstream `LICENSE` 11,357 B, `58d1e17ffe5109a7ae296caafcadfdbe6a7d176f0bc4ab01e12a689b0499d8bd` | `app/src/main/res/drawable/sym_<key>.xml` (path data unchanged, wrapped in a group that shifts the SVG's -960 origin); licence in `app/src/main/assets/licenses/material-symbols-APACHE-2.0.txt` (attribution header, then the upstream text with trailing whitespace trimmed) |

`BundledSymbolsLicenceTest` checks that the Apache-2.0 text and the attribution (source and pinned commit) ship.
`BundledFontTest` pins each font's size and SHA-256 and checks that the OFL text and the copyright line
("Copyright 2020 The Newsreader Project Authors (http://github.com/productiontype/Newsreader)",
"Copyright 2022 The Figtree Project Authors (https://github.com/erikdkennedy/figtree)", and the Plus Jakarta Sans
Project Authors line "2020 The Plus Jakarta Sans Project Authors", "Copyright 2018 The Manrope Project Authors
(https://github.com/googlefonts/manrope)" and "Copyright 2022 The Bricolage Grotesque Project Authors
(https://github.com/ateliertriay/bricolage)") ship, and that neither Manrope nor Bricolage declares a Reserved Font Name.
OFL section 2 requires the licence to accompany every copy; AP1 adds an in-app licences viewer.
No bundled font declares a Reserved Font Name, so subsetting is allowed later if APK size matters; it is not
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

## Planned: premium look (DS6)

`docs/plans/2026-10-30-m1a-premium-look.md` (owner decisions of 2026-10-30) changes several things on this page. Each DS6 slice updates the sections it touches as it lands; until then, the sections above describe what is built.

- **Colour.** `BrandColors` (ink indigo) is retired (done in DS6a: Graphite is the default and dynamic colour is opt-in). DS6b adds the other three curated palettes, with light and dark chosen independently (done; see "Colour"). Money colours are unchanged.
- **Surfaces.** The default is a tonal canvas with tonal islands, never white, with a hairline edge; a flat option remains. The window background follows the canvas (done in DS6c; see "Surfaces and window colour").
- **Header.** An optional glow, off by default, never under money (done in DS6d; see "One-hand layout"). (Icon-only actions through `HeaderIconButton` are done: DS6e.)
- **Typography.** A type-pairing setting: `HERO_ONLY` stays the default, and `EDITORIAL` is an option (done in DS6g; see "Typography"). Bundled Figtree is the default sans (done in DS6h) and Plus Jakarta Sans is the second bundled option (done in DS6i). The third-party table has a row per bundled font, and all bundled fonts together stay within 3 MiB. The DS6f spike (plan §3.7) measured the candidates: only Figtree and Plus Jakarta Sans (both variable) are bundled, 0.66 MiB with Newsreader in total.
- **Scope.** It applies app-wide through the theme and the shared components. Screens never hardcode a colour or font outside `ui/theme`; `ThemeLiteralScanTest` checks this (built in DS6a).
