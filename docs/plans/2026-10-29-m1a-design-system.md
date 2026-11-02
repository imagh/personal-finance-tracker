# Plan: M1a design system and visual direction (lands before A5)

| Field       | Value |
|-------------|-------|
| Date        | 2026-10-29 |
| Author      | architect |
| Status      | Approved 2026-10-29 (decisions in §12); §6.3 applied to the M1a plan. Amended 2026-10-29 after the owner's mockup review: serif for hero numbers only, header title layout, Budgets list + detail (§12 Q10–Q11). Annotated 2026-10-30 by the premium-look plan `2026-10-30-m1a-premium-look.md` (DS6): curated palettes with Graphite as the default, tonal surfaces, type-pairing and sans options, and valid AP1 setting keys. Its notes are marked "DS6" |
| Related ACs | Owner UI requirements UI-1..UI-10 (defined in §1); PRD L-2 (recolour/re-icon), L-6 (state visible), B-2 (threshold flag), B-3 (unverified visibly marked), SEC-2 (`FLAG_SECURE`), SEC-3 (no permissions), SEC-5 (no sensitive logs), NFR responsiveness |
| Plan owner  | akashg (repo owner) |

Foundation: the approved M1a plan `docs/plans/2026-10-28-m1a-core-ledger.md` (abbreviated **M1a** below), in particular §3.3 (dependency pins), §3.4 (reuse by layer) and steps A5, B2–B4, C4, C5 and D4. This plan inserts a **design-system slice (DS1–DS5)** into `feat/m1a-foundation` between A4b and A5. It then adds a small **appearance slice (AP1–AP2)** after A-final, and amends the UI steps of M1a that consume it (§6.3).

Mockups for the owner to react to are in §5.5 (wireframes), and a styled HTML mockup is at `docs/plans/2026-10-29-m1a-design-system-mockups.html`. Open it in a browser and use the toggle for light and dark.

---

## 1. Requirement

**Need.** A5 is the first screen. If no design system exists before it, every screen from A5 to H invents its own colours, spacing, type and motion. The owner would then be retrofitting a look across 15+ screens. The owner wants the visual direction settled first, as reusable components.

**Owner requirements (IDs used for test mapping):**

| ID | Requirement |
|---|---|
| UI-1 | The look is like Cashew, but more premium, clean and sleek. |
| UI-2 | The system default font, plus an option to load a custom font. There is no `INTERNET` permission, so no downloadable fonts. **Revised 2026-10-30 (DS6):** the default text font is the bundled Figtree. The system font and three other bundled OFL sans fonts are options, and custom import is unchanged. |
| UI-3 | A modern serif for dashboards and reports; sans-serif for everyday screens. Refined at the mockup review (Q10): the serif sets only the dashboard and report **figures** (the hero number); every title, on every screen, is sans. **Revised 2026-10-30 (DS6):** this stays the default (`HERO_ONLY`); a type-pairing setting adds `EDITORIAL` (serif hero, large amount and titles). An all-serif option was evaluated and deferred. |
| UI-4 | Light and dark mode. |
| UI-5 | Green and red money colours (income/expense, under/over budget, positive/negative balance), accessible in both themes. |
| UI-6 | Emoji and icons for categories, sub-categories and descriptions, without `material-icons-extended`. |
| UI-7 | One-hand use in the style of Samsung One UI: content starts mid-screen under a large collapsing header, and interactive elements are within reach. |
| UI-8 | The accent comes from the system (Material You dynamic colour). An override comes later. **Revised 2026-10-30 (DS6):** the default is the curated Graphite palette, with dynamic colour as an opt-in. Four curated palettes are the override. |
| UI-9 | Every UI element is a reusable, extensible component (M1a §3.4 and the abstraction bar). |
| UI-10 | Smooth, iOS-like animation: springs and continuity (shared-element) transitions, with no janky defaults. |

**Done means:**
1. `PftTheme` wraps the app and provides colour, type, shape, spacing and motion tokens in light and dark, with dynamic colour on by default.
2. The one-hand scaffold and the content primitives that A5, B2 and C4 need are built, previewed and screenshot-tested.
3. Every money and brand colour pair passes the WCAG contrast tests in both themes.
4. The serif font is bundled with its licence.
5. The icon set is curated and registered under permanent keys.
6. A debug-only design catalogue shows all of the above on the emulator.
7. The consuming M1a steps are amended to use these pieces.
8. `./gradlew check`, which now includes screenshot verification, is green.

After A-final, the appearance slice adds a settings screen for theme mode, dynamic colour, font choice and custom-font import.

**Out of scope:**
- A custom accent override (UI-8 "later"; the approach is noted in §3.2).
- Charts and insights (M3).
- A per-transaction icon override (Q7).
- An emoji-picker library.
- Tablet and foldable layouts. Landscape works but is not designed.
- Localisation.
- Home-screen widgets, Lottie or illustrated empty states.
- Baseline profiles (only if a measurement shows jank, §10).
- The screens themselves, which stay in their M1a steps.

## 2. Current state

- **The app shell is bare.**
  - `app/src/main/kotlin/dev/imagh/pft/app/MainActivity.kt:21-25` renders `MaterialTheme { Surface(...) {} }` with default (baseline purple) colours. `FLAG_SECURE` is set first, at `:19`, and must stay first.
  - `app/src/main/res/values/themes.xml:3` uses `@android:style/Theme.Material.Light.NoActionBar`, and there is no `values-night`. A dark-mode launch therefore flashes a light window before Compose draws.
  - There is no edge-to-edge setup.
- **Dependencies.**
  - `app/build.gradle.kts:51-55` has only `compose-ui`, `material3` and `activity-compose`. There is no `ui-tooling-preview` (so no `@Preview`) and no `ui-test-junit4`.
  - The pins are in `gradle/libs.versions.toml:8` (`composeBom = "2024.10.01"`).
  - **Correction:** the lockfile resolves `androidx.compose.material3:material3:1.3.1`, not the 1.3.0 that M1a §3.3 and `docs/TECHNICAL.md:161` state. Compose UI and animation resolve to 1.7.5. Fix the docs (§9).
- **Transitive artifacts already present** (`app/gradle.lockfile`):
  - `material-icons-core:1.7.5`. This is the original Material Icons family, not Material Symbols.
  - `emoji2:1.3.0` and `startup-runtime:1.1.1`.
  - `compose.animation:1.7.5`, which includes `SharedTransitionLayout` (experimental) and `Modifier.animateItem`.
- **Merged manifest.** `app/build/intermediates/merged_manifest/release/.../AndroidManifest.xml:29-42` has `androidx.startup.InitializationProvider` (`exported=false`) registering `EmojiCompatInitializer`, `ProcessLifecycleInitializer` and `ProfileInstallerInitializer`. EmojiCompat's default config asks a font provider (Google Play services) for a downloadable emoji font. This plan removes that initializer (§3.6).
- **Tests to extend.** `app/src/test/kotlin/dev/imagh/pft/app/ManifestHardeningTest.kt:104-136` covers no permissions, only the launcher exported, and `FLAG_SECURE`. Robolectric runs at `@Config(sdk = [35])` on JUnit Vintage (`app/build.gradle.kts:41-48`).
- **Lint.** `config/detekt/detekt.yml:1-12` has only two overrides. Detekt runs **without type resolution** (M1a §3.3), so only syntactic rules such as `ForbiddenImport` work.
  - **Finding, outside this plan's scope:** M1a §8 (`2026-10-28-m1a-core-ledger.md:380`) relies on detekt `ForbiddenMethodCall` to ban `android.util.Log`. That rule needs type resolution and will silently do nothing. A5 should use `ForbiddenImport` for `android.util.Log` instead [Verify against the detekt 1.23.7 rule docs].
- **Pre-commit.** `.pre-commit-config.yaml:31` runs `check-added-large-files` (default 500 KB). The bundled font (451,664 bytes) is under it, and screenshot goldens must stay under it too.
- **M1a building blocks this plan reshapes** (`2026-10-28-m1a-core-ledger.md:110-113`): `UiState`, `FormState`, `EditFormScaffold` and `NameField` (A5); `EntityListScreen`, `UndoDeleteSnackbar`, `AmountField`, `MoneyText` and `AccountPicker` (B2); `CategoryPicker` (B4), `TagPicker` (B5) and `DateField` (C4); `AuditHistoryList` (C5).
  - The `category` table already has `colour` and `icon_key` columns (`:128`), so B3 is where the icon and colour contract (§4) lands.
  - M1a §3.3 (`:87`) rejects `material-icons-extended` and says "use core icons". This plan amends that to curated Material Symbols drawables (§3.6).
- **`:core:money` is a stub** (`core/money/README.md`), and A2 is still pending. `MoneyText` must use A2's `PaiseFormat` and must not reformat money itself, so DS5 depends on A2.
- **Branch reality vs the brief.** The brief says A1 is merged, but `feat/m1a-foundation` and `origin/feat/m1a-foundation` are both at `8bde3b6` (the plan commit). A1's commits exist only on `feat/m1a-foundation-a1-gradle-skeleton` (HEAD `41f95c4`). DS1 must branch from the base **after** the A1 PR is merged (Q9).
- **Platform pieces to reuse, not rebuild:**
  - **Header scroll:** M3 `TopAppBarState` and `TopAppBarDefaults.exitUntilCollapsedScrollBehavior(...)`, which provide the nested-scroll, snap and fling logic for the collapsing header.
  - **Colour:** `dynamicLightColorScheme`/`dynamicDarkColorScheme`. Contrast tests use `Color.luminance()`, which implements the WCAG 2.0 relative-luminance formula.
  - **Activity:** `enableEdgeToEdge()` (activity 1.9.3). `UiModeManager.setApplicationNightMode` (API 31) gives a per-app dark mode that also themes the window and splash.
  - **Files and glyphs:** `ActivityResultContracts.OpenDocument` (Storage Access Framework, no permission). `android.graphics.fonts.Font.Builder` (API 29) throws on a malformed font. `Paint.hasGlyph` (API 23) checks emoji renderability, and ICU `BreakIterator` counts grapheme clusters.
  - **Compose:** `Modifier.animateItem()` for list changes.

## 3. Design & decision

### 3.1 Visual direction: "Cashew, but premium"

Keep what makes Cashew good: emoji/icon categories on coloured badges, a month-based home, budget bars and Material You. Make it calmer and more deliberate:

| Principle | What it means in tokens/components |
|---|---|
| **Quiet surfaces, one accent** | Neutral tonal surfaces. The dynamic accent is used only for interactive chrome (FAB, selected nav item, primary button, focus, switches). **Money is never drawn in the accent colour.** |
| **Type carries hierarchy, not boxes** | Large serif numerals on dashboards, generous whitespace, inset grouped "islands" (iOS/One UI style) instead of shadowed cards. Shadows only on the FAB and modal sheets; everything else uses tonal elevation. |
| **Colour where it signals** | Category colour appears only in its glyph badge. Money green/red appears only on amounts, progress meters and status, always paired with a sign or label (§3.3). |
| **Reach first** | Content starts about 38% down the screen. Primary actions sit at the bottom (bottom action bar, FAB, bottom sheets). The top holds only secondary actions. |
| **Continuity** | Springs everywhere. The FAB grows into the add screen, a row's glyph and amount fly into the detail screen, and digits roll. Nothing pops in from nowhere. |

**DS6 amendment (2026-10-30, owner):**
- "Quiet surfaces, one accent" now means a **tonal canvas with tonal islands**: each island is a slightly different tonal step of the canvas hue, **never white**, with a 1px hairline edge (plus a faint inset top highlight in dark mode). A flat option remains.
- The accent is a muted metal from a curated palette (Graphite by default) rather than the wallpaper. It is still only on interactive chrome.
- An optional header glow is off by default.
- Header actions are icon-only.
- See `2026-10-30-m1a-premium-look.md` §3.

### 3.2 Theming architecture (no new abstraction)

All code is in `:app` (Compose is Android-only; `core` stays pure). There are two packages:

- `app/src/main/kotlin/dev/imagh/pft/app/ui/theme/` holds the **tokens and `PftTheme`**.
- `app/src/main/kotlin/dev/imagh/pft/app/ui/common/` holds the **reusable components**. This is the package M1a §3.4 already names, so no rename is needed.

`PftTheme(prefs: ThemePrefs = ThemePrefs.Default, content)` does the following:
1. Resolves dark or light from `prefs.mode` (`SYSTEM` follows `isSystemInDarkTheme()`).
2. Resolves the colour scheme: dynamic (`dynamicLight/DarkColorScheme(context)`) when `prefs.dynamicColor`, otherwise the committed brand fallback.
3. Builds the M3 `Typography` from `prefs.displayFont` and `prefs.textFont`.
4. Calls `MaterialTheme(colorScheme, typography, shapes)`.
5. Provides the extended tokens through `staticCompositionLocalOf`: `LocalMoneyColors`, `LocalCategoryPalette`, `LocalSpacing`, `LocalMotion` and `LocalPftType`. These are read via `object PftTheme { val money; val palette; val spacing; val motion; val type }`.

This is the Compose-idiomatic "extended theme" mechanism. It is not an interface, factory or wrapper. `ThemePrefs` is a plain data class, and until AP1 the app always passes the defaults.

**Rules (reviewer-enforced, written into `docs/architecture/design-system.md`):**
- **Do not wrap an M3 component just to restyle it.** Style comes from the theme. Use `Button`, `TextField`, `ListItem`, `Switch`, `NavigationBar`, `SingleChoiceSegmentedButtonRow` and `Snackbar` directly.
- A component goes in `ui/common` only when this plan or M1a names **two or more consumers** (M1a §3.4 rule). Otherwise it lives in its screen's package and is promoted when the second consumer arrives.
- No raw colour (`Color(0x…)`) or raw `dp`/`sp` design value outside `ui/theme`. Layout arithmetic is the exception. Without type resolution detekt cannot enforce this, so it is a code-review checklist item.
- Experimental APIs (`ModalBottomSheet`, `SharedTransitionLayout`) are opted into in **one file each** (`ui/common/PftBottomSheet.kt`, `ui/nav/SharedElements.kt`), so an API change on upgrade touches one place.

**Accent override (later, not built):** when needed, offer a curated set of about 8 accent presets whose full schemes are generated offline and committed as constants. This needs no colour-utilities library and matches how the brand fallback is produced. A `ThemePrefs.accent` field is added then, not now (YAGNI).

### 3.3 Tokens

> **DS6 amendment (2026-10-30):**
> - The ink-indigo brand scheme below is **retired** by DS6a and replaced by four curated palettes: GRAPHITE (the default), MIDNIGHT, IVORY and AUBERGINE. Light and dark are chosen independently.
> - Each palette is generated with the procedure below, then pinned to the owner-approved mockup values.
> - Dynamic colour becomes opt-in (off by default).
> - Money colours are unchanged.
> - The accent must be at least 40° of CIELAB hue from money green and red. Warning amber is exempt, because brass and champagne sit 1–9° from it; the warning pill is distinguished by its label and its palette-independent colours instead.
> - Details: `2026-10-30-m1a-premium-look.md` §3.1.

**Colour: brand fallback scheme** (used when dynamic colour is off, and always in screenshot tests so goldens are deterministic).
- **Seed:** "ink indigo" `#4A55B8`. Its hue is chosen to be far from money green (~150°) and red (~25°), so the accent can never read as income or expense.
- **Generation:** generated once, offline, with the Material Color Utilities "tonal spot" scheme (as built, `@material/material-color-utilities` 0.3.0 with the newer surface roles filled by hand; see "DS1 deviations" in §6.1). All light and dark roles are committed as constants in `ui/theme/BrandColors.kt`, with a header comment recording the seed, tool and date. No runtime library is used.
- **Checked (§7):** primary on surface is 6.08:1 (light) and 10.88:1 (dark). onPrimary on primary is 6.40:1 and 7.85:1.

**Colour: money tokens** (`MoneyColors`, the same in dynamic and brand schemes, **never derived from the accent**).

| Token | Light | Dark | Use |
|---|---|---|---|
| `positive` | `#13653C` | `#6FD99E` | income, under budget (positive balances stay neutral, Q3) |
| `negative` | `#A3261D` | `#FF8F85` | expense totals, over budget, negative balance |
| `warning` | `#734900` | `#F2C14E` | budget at or above its alert threshold (B-2) |
| `positiveContainer` / `onPositiveContainer` | `#DDF4E5` / `#0B4A2A` | `#123C27` / `#B5EFCB` | income chip, "under" pill |
| `negativeContainer` / `onNegativeContainer` | `#FCE3E0` / `#7A1B14` | `#4A1712` / `#FFD3CD` | over-budget pill |
| `warningContainer` / `onWarningContainer` | `#FCEFD2` / `#5C3A00` | `#3F2E05` / `#F8DFA2` | threshold pill |

**Contrast, computed for this plan** (WCAG 2.x). Dynamic surfaces are low-chroma, so they are tested as greys at the extreme tones each surface role can take:

| Pair | Light, from surface (98) to surfaceDim (87) | Dark, from lowest (4) to surfaceBright (24) |
|---|---|---|
| `positive` on surfaces | 6.75 → 5.08 | 11.12 → 6.65 |
| `negative` on surfaces | 6.99 → 5.27 | 8.75 → 5.24 |
| `warning` on surfaces | 7.44 → 5.60 | 11.50 → 6.88 |
| on-container on container | 8.63–8.95 | 9.51–10.86 |

So every money text token clears 4.5:1 on every surface either scheme can produce, except `surfaceVariant`, which money text must not use (see "DS1 deviations" in §6.1). `ColorContrastTest` (DS1) pins this.

**Colour is never the only cue (WCAG 1.4.1).** The light positive and negative tokens are almost equal in luminance: their ratio to each other is 1.04:1, and 1.27:1 in dark. A red/green colour-blind user cannot tell them apart by lightness. So:
- every signed amount carries a sign glyph (`+`, or a true minus `−` U+2212);
- status pills carry words ("Over", "80%", "Unverified");
- `MoneyText` exposes a spoken direction ("Expense", "Income") in its semantics.

**Colour: category palette.** 12 named hues (`rose, coral, amber, olive, emerald, teal, sky, blue, indigo, violet, magenta, slate`). Each has a container and content colour per theme: light container at about tone 90 with content at about tone 30, and dark container at about tone 30 with content at about tone 90. The palette is used only for glyph badges.
- **Contrast:** content on container is at least 3:1 for symbols (WCAG 1.4.11, non-text) and at least 4.5:1 for the letter fallback.
- **Storage:** the **key** is what is stored in the DB (§4), not a hex value. That lets each theme render its own accessible tones.

> **DS6 amendment (2026-10-30):**
> - The text font defaults to the bundled **Figtree**. Google Sans Flex, Plus Jakarta Sans, DM Sans and System are options, subject to the DS6f licence and size spike.
> - A **type-pairing** setting chooses which roles use the display font: `HERO_ONLY` (default; the table below) or `EDITORIAL`. An all-serif option was evaluated and deferred by the owner.
> - Newsreader's optical size is set per serif role (the size clamped to 6–72), not pinned at 48.
> - The bundled fonts together are capped at 3 MiB (owner-confirmed).
> - **DS6f outcome (2026-10-30):** only Figtree and Plus Jakarta Sans (plus System) are bundled options; Google Sans Flex and DM Sans were dropped. See premium-look plan §3.7.
> - Read from the bundled file: Newsreader has ₹, −, `tnum` and `pnum`, but no `lnum`, and its default figures are already tabular.
> - Details: `2026-10-30-m1a-premium-look.md` §3.6–3.7.

**Typography (UI-2, UI-3).** There are two font slots:
- **Display:** serif, for dashboard and report **figures** only (`amountHero`).
- **Text:** sans, for everything else, including every title (Q10).

| Role | Family | Size / line (sp) | Weight | Features | Used for |
|---|---|---|---|---|---|
| `PftTheme.type.amountHero` | display (Newsreader, opsz axis auto) | 48/60, shrinks in steps of 48 → 40 → 34 → 28 to fit | 400 | `tnum`, `lnum` | Home month total, budget detail, account and loan totals; M3 report figures |
| M3 `headlineLarge` | text | 32/40 | 600 | | the large title of a `Standard` header ("Budgets", "Accounts", form titles) |
| M3 `titleLarge` | text | 22/28 | 500 | | the title of a `Dashboard` header ("October 2026", "🛒 Groceries") and of the collapsed 64dp bar |
| M3 `display*`, other `headline*`, `title*`, `body*`, `label*` | text | M3 defaults | M3 | | everything else (no header uses `display*`) |
| `PftTheme.type.amountLarge` | text | 32/40 | 500 | `tnum`, `lnum` | amount on add/edit and detail |
| `PftTheme.type.amountRow` | text | 16/24 | 500 | `tnum` | list rows |

- **Display spacing (owner, Q1).** Newsreader is set looser than its defaults: `amountHero` line height 1.25× the size (the table above) and letter spacing **+0.01em**. These are named tokens (`DisplaySpacing`), so DS2 tunes the final values on the debug catalogue screen, on the owner's phone, before goldens are recorded. Sans roles keep M3 spacing.
- **Why titles are sans (owner, Q10).** In the mockups a serif title stacked on a serif number was too heavy, and a sans title directly above a serif number clashed. Keeping the serif to the number alone, with the title in sans and well separated from it (§3.4), reads cleanest.
- **Text font:** defaults to `FontFamily.Default`, which is the device's system font: Roboto or Google Sans on Pixel, and the One UI font on Samsung [Verify on the owner's phone that the One UI font setting reaches `FontFamily.Default`]. It can be set to a custom file (AP2).
- **Display font** (sets `amountHero` only)**:** defaults to the **bundled Newsreader**. Other options are System serif (`FontFamily.Serif`), "Same as text" and a custom file.
- **Why Newsreader.** It is the only candidate checked that has all four things a money font needs. These were verified from the font files during planning:

| Font | Licence | Tabular figures | ₹ U+20B9 | − U+2212 | Variable | Size |
|---|---|---|---|---|---|---|
| **Newsreader** (Production Type) | OFL-1.1, **no Reserved Font Name** | yes (`tnum`, `pnum`) | yes | yes | opsz 6–72, wght 200–800 | 451,664 B |
| Fraunces | OFL-1.1 | **no** | yes | yes | yes | 360,440 B |
| Instrument Serif | OFL-1.1 | **no** | **no** | yes | no (400 only) | 70,012 B |

- **Font scale:** supported up to 200%. Hero amounts shrink to fit (tested); everything else wraps.
- **Custom fonts:** a custom font without `tnum` falls back to proportional digits. That is acceptable and documented: rolling digits may shift slightly.

**Shape.** M3 `Shapes`:

| Shape | Corner radius |
|---|---|
| extraSmall | 6dp |
| small | 10dp |
| medium | 16dp |
| large | 24dp (grouped islands, cards) |
| extraLarge | 32dp (sheets) |

Plus `PftTheme.shapes.glyph = RoundedCornerShape(30%)` for the soft-square category badge.

**Spacing.** A 4dp grid in `PftSpacing`:

| Token | Value |
|---|---|
| `xxs` | 2 |
| `xs` | 4 |
| `sm` | 8 |
| `md` | 12 |
| `lg` | 16 |
| `gutter` | 20 (screen edge) |
| `xl` | 24 |
| `xxl` | 32 |
| `huge` | 48 |

Sizes:
- Touch targets are at least 48dp (M3 default, tested).
- Rows are 64dp (two-line) or 56dp (one-line).
- Glyphs are 40dp in rows, 56dp in the detail hero and 28dp in chips.

**Motion (UI-10).** Springs are defined the way SwiftUI defines them, as a perceptual (duration, bounce) pair, and converted exactly to Compose. With mass 1: `stiffness = (2π / duration)²` and `dampingRatio = 1 − bounce`. The conversion is a pure function, `iosSpring(durationMillis, bounce)`, and is unit-tested.

| Token | (duration, bounce) | Compose stiffness, ζ | Used for |
|---|---|---|---|
| `snappy` | 0.30 s, 0.10 | ≈ 438.6, 0.90 | press feedback, chips, keypad digits, toggles |
| `smooth` | 0.45 s, 0 | ≈ 194.9, 1.00 | screen transitions, header snap, progress fill, rolling digits |
| `gentle` | 0.60 s, 0 | ≈ 109.7, 1.00 | container transforms (FAB → add screen, row → detail) |
| `bouncy` | 0.50 s, 0.25 | ≈ 157.9, 0.75 | rare celebration only (goal reached, G2) |

- **Navigation transitions** (`PftMotion.navEnter/navExit/navPopEnter/navPopExit`):
  - Push: slide in 25% of the width with a fade, using `smooth`.
  - Pop: the reverse.
  - Predictive back: enabled via `android:enableOnBackInvokedCallback="true"`, with navigation-compose 2.8.4's predictive-back support [Verify in A5].
- **Press feedback:** `Modifier.pressScale()` scales to 0.97 with `snappy`. It goes on rows, cards, glyph tiles and keypad keys, and the M3 ripple is kept.
- **Reduced motion:** Compose honours the system animator-duration scale ("Remove animations" in accessibility settings), so animations jump to their end state. **Rule: no logic waits on an animation to finish.** For example, a delete is committed on the state change, not on the end of the collapse animation.

### 3.4 One-hand layout (UI-7): `CollapsingHeaderScaffold`

- **Heights:**
  - Expanded: `clamp(0.38 × window height, 200dp, 360dp)`.
  - Collapsed: a 64dp bar.
  - If the window is shorter than 560dp (landscape, split screen), the header starts and stays collapsed.
- **Mechanics:** it reuses M3's `TopAppBarState` and `exitUntilCollapsedScrollBehavior(snapAnimationSpec = PftTheme.motion.smooth)`. The scaffold only sets `state.heightOffsetLimit` and lays out by `collapsedFraction`. No custom nested-scroll code is needed, and M3 already provides snap-to-nearest and fling.
  - If the content cannot scroll, the header stays expanded (One UI behaviour, M3 `canScroll`).
- **Header layout rule (owner, Q10).** The expanded header has two bands:
  - **Upper band: the title.** It is read-only, so it may sit out of thumb reach. It is sans, horizontally **centred**, with an optional one-line `subtitle` caption under it. It never sits directly on top of the hero number: there is clear space between them.
  - **Lower band (thumb zone): the hero and everything tappable.** The serif hero number, its caption, and the `period` control (‹ › stepper on the end of the caption row) sit at the bottom of the header, just above the content.
  - The only things in the top row are secondary `actions` (back, search, filter, overflow).
- **Two styles:**
  - `HeaderStyle.Dashboard`: sans `titleLarge` title (with optional `subtitle`) high in the upper band, about 12% of the expanded height from its top, plus a `hero` slot and an optional `period` slot in the lower band. Swiping the header horizontally also steps the period. Used by Home, Budget detail, and later account, loan and goal overviews.
  - `HeaderStyle.Standard`: sans `headlineLarge` title (with optional `subtitle`, for example "October · 3 days left · 4 active") centred in the middle of the expanded area. No hero. Used by lists (including the Budgets tab), settings and forms.
  - **Collapse:** the title cross-fades and scales into the 64dp bar's title, so the screen name stays visible; the hero, caption and `period` fade out with the lower band.
- **Slots:** `title`, `subtitle` (optional), `hero` (optional, Dashboard only), `period` (optional, Dashboard only: the `PeriodSwitcher`), `actions` (secondary only: back, search, filter, overflow), `bottomBar` (`NavigationBar` or `BottomActionBar`), `floatingActionButton`, `content` (`LazyListScope`). Add actions go in the FAB, not in the header.
- **Insets:** edge-to-edge, with `WindowInsets.safeDrawing`. The bottom bar is `imePadding()`-aware, so the Save button rides above the keyboard.
- **`BottomActionBar`:** one primary button (full width) or a primary plus a secondary button side by side, inside the thumb zone. Used by every form (Save), setup (Continue) and detail (Edit/Delete).
- **Pickers** (account, category, tag, date) open as bottom sheets (`PftBottomSheet`, built in B2), never as top dropdowns.

### 3.5 Fonts: bundling, licensing, custom import

- **Bundled.**
  - The upstream `Newsreader[opsz,wght].ttf` is shipped **unmodified** as `app/src/main/res/font/newsreader.ttf`: Google Fonts `ofl/newsreader`, sha256 `8a08d13f8a6c0d51be379a60af84f945f65369a67e509ee3c3bdcc421254d7c1`, re-verified at commit.
  - Its OFL text and copyright line ("Copyright 2020 The Newsreader Project Authors (http://github.com/productiontype/Newsreader)") go in `app/src/main/assets/licenses/newsreader-OFL.txt`. OFL §2 requires the licence to accompany every copy; AP1 adds an in-app "Licences" viewer so it is easy for the user to see.
  - **Subsetting:** no Reserved Font Name is declared, so subsetting (an OFL "Modified Version") is allowed later if APK size matters. It is not done now, so the shipped file's provenance stays checkable by hash.
  - The source, licence, hash and size are recorded in the third-party assets table in `docs/architecture/design-system.md`.
- **Custom font (AP2).** The pipeline fails closed: on any failure the previous font and setting are kept.
  1. **Pick:** SAF `OpenDocument`, which needs no permission. The MIME filter is `font/ttf`, `font/otf`, `font/sfnt`, `application/x-font-ttf`, `application/x-font-otf`, plus `application/octet-stream`, because many file managers mislabel fonts.
  2. **Copy with a cap:** read through `ContentResolver.openInputStream` with a byte cap (`AppearanceLimits.MAX_FONT_BYTES` = 8 MiB, typed static config) into a temp file under `noBackupFilesDir/fonts/`.
  3. **Check the magic number:** the first 4 bytes must be `00010000`, `OTTO` or `true`. Reject `ttcf`, `wOFF` and `wOF2`.
  4. **Parse:** `android.graphics.fonts.Font.Builder(file).build()` parses the file and throws if it is malformed.
  5. **Commit:** atomically rename to the fixed name `custom-text.font` or `custom-display.font`.
  6. **Persist:** store the slot choice in settings.
  - The SAF display name is **never** used in a path.
  - The URI permission is not persisted. The app keeps its own copy, so a moved or deleted source file does not break the font.
  - At startup, a missing or unloadable custom file falls back to the default and emits `SafeLog` `FontFallback(slot)` (an enum, no names).

### 3.6 Icons and emoji (UI-6)

- **One icon family: Material Symbols Rounded** (Apache-2.0). Weight 400, grade 0, optical size 24, outlined; filled variants only for selected navigation items.
  - **Curation:** about 60 category symbols and about 30 chrome symbols (back, add, search, filter, calendar, chevrons, edit, delete, undo, lock, settings, and so on).
  - **Format:** committed as vector drawables `app/src/main/res/drawable/sym_<key>.xml`, at roughly 0.5–1.5 KB each. They are imported once from the official `google/material-design-icons` SVGs via Android Studio's Vector Asset tool; the how-to goes in `docs/architecture/design-system.md`.
  - **Licence:** the Apache-2.0 notice goes in `assets/licenses/material-symbols-APACHE-2.0.txt`.
  - **Not `material-icons-extended`** (several MB before R8, rejected in M1a §3.3), and **not `material-icons-core`** either, even though it is already on the classpath. It is the older Material Icons family, and mixing two families looks cheap. Detekt `ForbiddenImport` bans `androidx.compose.material.icons.*` in app code. M3's internal components may still use it.
- **Registry with permanent keys.** `enum class PftSymbol(val key: String, @DrawableRes val res: Int)` lives in `ui/theme/PftSymbol.kt`.
  - **Keys are persisted** in `category.icon_key`, so like the app ID they are **permanent**: never renamed or removed, only added.
  - A pinned key list, `app/src/test/resources/pft-symbol-keys.txt`, makes removal a failing test.
  - An unknown key, for example from a newer version synced in M2, renders the letter fallback and never crashes.
- **Emoji are rendered by the system emoji font** (Noto Color Emoji on Pixel, Samsung's set on One UI). minSdk 33 covers Unicode 14+.
  - **Decision:** remove `EmojiCompatInitializer` from the merged manifest. Its default config asks Google Play services' font provider for a downloadable emoji font. Removing it means no cross-app font fetch and deterministic rendering. `ManifestHardeningTest` asserts it is absent.
  - Emoji can look different on the two phones (different makers' emoji sets). That is accepted; symbols are the consistent option.
- **Emoji input for categories (built in B4, specified here):**
  - The user types or pastes one emoji with the keyboard's emoji panel, or taps one of a static quick-pick row of about 24 finance emoji. There is no picker library (`emoji2-emojipicker` is View/RecyclerView-based).
  - The input is checked at the UI boundary by `EmojiInputValidator`. It must be exactly one grapheme cluster (ICU `BreakIterator`), contain an emoji code point (ICU `UProperty.EXTENDED_PICTOGRAPHIC` or a regional-indicator pair) and render on this device (`Paint.hasGlyph`).
- **"Descriptions"** (default, Q7):
  - A transaction row's leading glyph is its sub-category's glyph if set, otherwise its category's.
  - Titles and descriptions accept any emoji typed in, rendered with the system font. Compose truncates at grapheme boundaries.
  - There is no per-transaction icon.

### 3.7 Component inventory (UI-9)

Each shared component has at least two named consumers (M1a §3.4). "Built in" is the PR that creates it: DS PRs build what A5, B2 and C4 need first, and the rest is built with its first consumer, to this spec.

| Component | Package | What it is | Named consumers | Built in |
|---|---|---|---|---|
| `PftTheme`, `ThemePrefs`, `BrandColors`, `MoneyColors` | `ui/theme` | theme entry and colour tokens | every screen | DS1 |
| `PftTypography`, `PftShapes`, `PftSpacing` | `ui/theme` | type, shape and spacing tokens | every screen | DS2 |
| `PftSymbol`, `CategoryPalette` | `ui/theme` | icon registry, category colours | `CategoryGlyph`, chrome icons everywhere | DS3 |
| `CategoryGlyph` (symbol, emoji or letter fallback on a palette badge) | `ui/common` | the category/account badge | B2 (account type), B4, C4 rows, `CategoryPicker`, D4, E4 | DS3 |
| `PftMotion` (springs, nav transitions, `pressScale`) | `ui/theme` | motion tokens | every screen | DS4 |
| `CollapsingHeaderScaffold` | `ui/common` | One UI-style screen frame | A5 setup, `EditFormScaffold`, B2 `EntityListScreen`, C4 Home, D4, AP1/H1 settings | DS4 |
| `BottomActionBar` | `ui/common` | thumb-zone primary actions | A5 `EditFormScaffold` (all forms), setup, C5 detail | DS4 |
| `MoneyText` (+ `MoneyTone`) | `ui/common` | formatted, signed, toned, tabular amount; hero shrink-to-fit; optional rolling digits | B2, C4, C5, D4, F3, G2 | DS5 (moved from B2) |
| `StatusChip` | `ui/common` | labelled state pill (Unverified, Overdue, Paused, Over) | C4, C5, D4, E4 | DS5 |
| `GroupedSection` | `ui/common` | inset rounded island with hairline dividers | C4 accounts strip, C5 detail, D4 detail, AP1/H1 settings, H2 diagnostics | DS5 |
| `EmptyState` | `ui/common` | glyph, message and action | B2 `EntityListScreen` (all lists), C4 empty month, C5 no results | DS5 |
| `EditFormScaffold`, `NameField`, `UiState`, `FormState` | `ui/common` | form plumbing **on** `CollapsingHeaderScaffold` + `BottomActionBar` | as M1a | A5 (unchanged owner) |
| `EntityListScreen`, `UndoDeleteSnackbar`, `AmountField`, `AccountPicker` | `ui/common` | as M1a | as M1a | B2 |
| `PftBottomSheet` | `ui/common` | the one `ModalBottomSheet` opt-in, sheet shape and motion | `AccountPicker` (B2), `CategoryPicker` (B4), `TagPicker` (B5), `DateField` (C4) | B2 |
| `EmojiInputValidator` | `ui/categories` | one-emoji validation | B4 only, so it stays local | B4 |
| `TransactionRow` | `ui/common` | glyph, title, subtitle, `MoneyText`, `StatusChip` | C4 list, C5 search, D4 budget detail, F3 loan EMIs | C4 |
| `PeriodSwitcher` | `ui/common` | the Dashboard `period` slot: ‹ › stepper beside the caption plus a horizontal swipe on the header, with a directional slide of the title and hero | C4 month, D4 budget detail (past periods) | C4 |
| `AmountKeypad` | `ui/transactions` | big-key numeric pad producing text for `Paise.parse` | C4 only, so it stays local | C4 |
| `SharedElements` (keys and the `SharedTransitionLayout` opt-in) | `ui/nav` | FAB → add, row → detail continuity | C4, C5 | C4 |
| `ProgressMeter` | `ui/common` | verified fill + **hatched** unverified segment (B-3, not colour alone) + threshold tick + tone | D4, F3, G2 | D4 |
| `AuditHistoryList` | `ui/common` | as M1a, styled as a `GroupedSection` | as M1a | C5 |

**Deliberately not components** (use M3 directly with theme styling): `Button`, `TextButton`, `TextField`, `ListItem`, `Switch`, `NavigationBar`, `SingleChoiceSegmentedButtonRow`, `Snackbar`, `FloatingActionButton`, `DatePicker`.

**Preview and screenshot single source.** Each component file has `@PreviewLightDark internal` sample composables using fake data only. The screenshot tests and the debug catalogue call those same functions, so nothing is written twice.

**Debug design catalogue.** A debug build shows a scrollable catalogue of tokens and components, with live light/dark and font toggles. It is the on-device verification tool:
- **Location:** `app/src/debug/kotlin/dev/imagh/pft/app/DevEntry.kt` defines `DevContent()`. `app/src/release/.../DevEntry.kt` defines it as an empty `Surface`.
- **Lifecycle:** `MainActivity` shows `DevContent()` until A5 replaces it with `PftNavHost`. From then on, the catalogue is a debug-only route.
- **Scope:** it contains no mocked screens, only real components, so none of it is throwaway.

### 3.8 New dependencies (pinned, locked, checksum-verified; follow `build-logic/README.md` "Refreshing pins")

| Dependency | Pin | Config | Why |
|---|---|---|---|
| `androidx.compose.ui:ui-tooling-preview` | via BOM 2024.10.01 | `implementation` (annotations only, tiny) | `@Preview`, `@PreviewLightDark` |
| `androidx.compose.ui:ui-tooling` | via BOM | `debugImplementation` | Studio preview rendering, never in release |
| `androidx.compose.ui:ui-test-junit4` | via BOM | `testImplementation` | Compose tests (M1a already lists it for A5; it arrives in DS1 instead). `ui-test-manifest` is **not** used: see "DS1 deviations" in §6.1 |
| `io.github.takahirom.roborazzi` `roborazzi`, `roborazzi-compose`, `roborazzi-junit-rule` and Gradle plugin | **1.36.0** (2024-12-09) | `testImplementation` and plugin | JVM screenshot tests on the Robolectric we already use. 1.36.0 is the release built against **Robolectric 4.14.1 and Compose 1.7.5**, our exact pins [Verify it compiles under Kotlin 2.1.0 and AGP 8.7.3] |
| Robolectric native graphics runtime | via Robolectric 4.14.1 | test | `@GraphicsMode(NATIVE)` for pixel rendering [Verify that it resolves through Gradle (checksummed), not a runtime download] |
| Bundled asset: Newsreader | upstream file, sha256 above | `res/font` | §3.5 |
| Bundled assets: Material Symbols (about 90 vector drawables) | upstream SVG → XML | `res/drawable` | §3.6 |

**Rejected:**
- `material-color-utilities` and MDC `com.google.android.material` (`harmonize`): runtime colour maths is not needed; presets come later.
- `emoji2-emojipicker`: View-based.
- `androidx.core:core-splashscreen`: the platform splash API on 31+ suffices.
- Downloadable Google Fonts: these need network.
- Lottie, Coil, Accompanist and blur libraries: not needed.

Release footprint: about 450 KB of font, about 90 KB of icons, and no new runtime library.

## 4. Data model & schema

**DS1–DS5: no schema change.**

**Contract handed to B3** (M1a v3 `category` table, `2026-10-28-m1a-core-ledger.md:128`). Validation lives in `core/ledger`, and rendering lives in `app`.

| Column | Stored text | Core type (B3) | Core validation | App rendering |
|---|---|---|---|---|
| `colour` | a palette key, for example `teal` | `enum class CategoryColour` (12 names, no colour values) | must be a known name. On read, an unknown value maps to `slate` (fail-safe for M2 sync) | `CategoryPalette[colour]` per theme |
| `icon_key` | `sym:<key>` or `emoji:<text>` | `sealed interface CategoryIcon { Symbol(key); Emoji(text) }` | `sym:` key matches `[a-z0-9_]{1,40}`. `emoji:` text is 1–16 UTF-16 units with no whitespace or control characters. The "is it really a renderable emoji" check is the app's `EmojiInputValidator` (needs ICU and `Paint`) | `PftSymbol.fromKey` or the system emoji. Unknown → letter fallback |

`DefaultCategories` (B3) assigns each seeded category a `PftSymbol` key and a palette colour from DS3's curated list. The list goes in `docs/features/categories-and-tags.md`.

**AP1 settings** go in the existing `app_setting` table (M1a v1) through `SettingsService`. They are runtime and per-user, so they live in the DB with an audit trail (CLAUDE.md §3.5).

| Key | Values | Default |
|---|---|---|
| `ui.theme_mode` | `SYSTEM`, `LIGHT`, `DARK` | `SYSTEM` |
| `ui.dynamic_color` | `true`, `false` | `true` |
| `ui.font.display` | `BUNDLED_SERIF`, `SYSTEM_SERIF`, `TEXT`, `CUSTOM` | `BUNDLED_SERIF` |
| `ui.font.text` | `SYSTEM`, `CUSTOM` | `SYSTEM` |

- An unknown or missing value reads as the default. It never throws, so settings written by a newer version are tolerated.
- Every change is audited.
- **DS6 correction (2026-10-30):** `SettingsService` accepts only keys matching `[a-z][a-z0-9_]*` (`SettingsService.kt:70`), so the dotted keys above would throw `InvalidSettingKey`. AP1 uses underscored keys (`ui_theme_mode`, ...), the `ui_font_sans` default changes to `FIGTREE`, and `ui_dynamic_color` defaults to `false`. The full key table, with the DS6 keys added, is in `2026-10-30-m1a-premium-look.md` §4.
- **AP2 files:** `noBackupFilesDir/fonts/custom-text.font` and `custom-display.font`, which are not in the DB. Backup is off anyway.

## 5. Flows

### 5.1 Launch and theme resolution
1. `MainActivity.onCreate` sets `FLAG_SECURE` (unchanged, first), then calls `enableEdgeToEdge()`.
2. The window theme (`values` / `values-night`) already matches the platform's per-app night mode, so there is no light flash.
3. `setContent { PftTheme(prefs) { … } }`. Until AP1, `prefs = ThemePrefs.Default`.
4. **From AP1:** `AppearanceSettings.prefs: StateFlow<ThemePrefs?>` is loaded off the main thread. The first frame is held with an `OnPreDrawListener` for at most 500 ms, after which the defaults are used (fail-safe).
5. A theme-mode change writes the setting (audited) and calls `UiModeManager.setApplicationNightMode`, so the window, the splash and `isSystemInDarkTheme()` all agree on the next launch.

### 5.2 Rendering an amount
1. The ViewModel exposes `Paise` plus a `MoneySemantic` (`Income`, `Expense`, `Transfer`, `Balance(signed)`, or `Budget(state)`, where `state` comes from core `BudgetProgress`).
2. `MoneyText` formats with A2's `PaiseFormat.format`; there is no UI re-implementation.
3. It prefixes the sign glyph, maps `MoneySemantic` → `MoneyTone` → a `MoneyColors` token, applies the `tnum` style and sets the semantics ("Expense, ₹250.00").
4. Budget state (under, threshold, over) is decided in core. The UI only maps it.

### 5.3 Collapsing header
1. The user drags the content.
2. M3's nested-scroll connection consumes the delta into `heightOffset` until the header is collapsed.
3. The scaffold lays out the title and hero by `collapsedFraction`: the centred title moves into the 64dp bar, and the lower band (hero, caption, `period`) fades out.
4. On release, the M3 snap spec (`PftMotion.smooth`) settles to the nearer end.
5. A horizontal swipe on an expanded Dashboard header that has a `period` steps to the previous or next period, the same as ‹ ›. Vertical drags stay with step 2.

### 5.4 Custom font import (AP2)
1. Settings › Appearance › Text font › "Import font…" opens the SAF picker.
2. The URI goes to `CustomFontStore.import(uri, slot)`, which checks the size cap, then the magic number, parses with `Font.Builder`, renames atomically and writes the setting (audited).
3. `PftTheme` recomposes with the new `FontFamily`.
4. On any failure: a "Couldn't use this font" message is shown, temp files are deleted, the previous state is kept, and `SafeLog` gets `FontImportRejected(reason enum)`.

### 5.5 Mockups (wireframes; the styled version is in the HTML file)

Fake data only. Legend: `[serif]` means the display font, `( )` is a glyph badge, and a dashed line marks where content starts, about 38% down.

**Home: month list (dashboard, C4)**
```
┌──────────────────────────────────────┐
│ 9:41                        ▾ ⌕  ⋮   │  secondary actions only
│             October 2026             │  title: sans titleLarge, centred, read-only
│                                      │  clear space
│                                      │
│  −₹48,250.40        [serif hero]     │  MoneyText hero, negative tone
│  spent this month          (‹) (›)   │  caption + PeriodSwitcher (also swipe)
│  ( + ₹1,20,000 in ) ( net +₹71,749 ) │  positive containers
├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─┤  ← content starts ~38%
│ Today · Wed 29          −₹1,040.00   │
│ ╭──────────────────────────────────╮ │  GroupedSection island
│ │ (🍔) Swiggy order        −₹540.00│ │  TransactionRow
│ │      Food › Delivery · HDFC      │ │
│ │ (⛽) Fuel                −₹500.00│ │
│ │      Transport  [Unverified]     │ │  StatusChip, labelled
│ ╰──────────────────────────────────╯ │
│ Tue 28                 +₹1,20,000.00 │
│ ╭──────────────────────────────────╮ │
│ │ (💼) Salary         +₹1,20,000.00│ │  positive tone
│ │ (⇄) Card bill          ₹12,000.00│ │  transfer: neutral, no sign
│ ╰──────────────────────────────────╯ │
│                               ( + )  │  FAB → add (container transform)
│  ⌂ Home   ◔ Budgets   ↻ Upcoming  ⋯  │  NavigationBar (Q6)
└──────────────────────────────────────┘
```

**Add transaction (everyday, C4).** The FAB grows into this screen, and every control is in the lower two-thirds.
```
┌──────────────────────────────────────┐
│ ✕                                    │
│     [ Expense | Income | Transfer ]  │  segmented
│                                      │
│              ₹ 250                   │  amountLarge (sans, tnum), digits roll
│          Swiggy order_               │  title field (inline)
├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─┤
│ ((🍔) Food › Delivery)  ((🏦) HDFC)  │  chips → PftBottomSheet pickers
│ ((📅) Today)  (# tags)               │
│ ┌──────┬──────┬──────┐               │
│ │  1   │  2   │  3   │               │  AmountKeypad (pressScale + haptic)
│ │  4   │  5   │  6   │               │
│ │  7   │  8   │  9   │               │
│ │  .   │  0   │  ⌫   │               │
│ └──────┴──────┴──────┘               │
│ [            Save ₹250.00          ] │  BottomActionBar (primary)
└──────────────────────────────────────┘
```

**Budgets tab: list (standard, D4).** One card per active budget; tap a card for its detail.
```
┌──────────────────────────────────────┐
│ 9:41                               ⋮ │
│                                      │
│               Budgets                │  title: sans headlineLarge, centred
│   October · 3 days left · 4 active   │  subtitle
│                                      │
├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─┤
│ ╭──────────────────────────────────╮ │
│ │ (Σ) All spending ₹48,250/₹60,000›│ │  BudgetCard
│ │     Monthly · resets 1 Nov       │ │
│ │ ████████████████▒▒▒│░░░░         │ │  ProgressMeter: verified, ▒ unverified, │ threshold
│ │ [80% · alert]  ₹11,750 left      │ │  warning tone + label
│ │ (🛒) Groceries    ₹6,500/₹8,000 ›│ │
│ │ ████████████████▒░░│░  [81% ⚠]   │ │
│ │ (🍽) Dining out   ₹4,200/₹3,000 ›│ │
│ │ ██████████████████████ [Over]    │ │  negative tone + "Over by ₹1,200"
│ │ (🚕) Transport      ₹900/₹2,500 ›│ │
│ │ ███████░░░░░░░░│░░░ [₹1,600 left]│ │  positive tone
│ ╰──────────────────────────────────╯ │
│                               ( + )  │  FAB → new budget
│  ⌂ Home   ◔ Budgets   ↻ Upcoming  ⋯  │
└──────────────────────────────────────┘
```

**Budget detail (dashboard, D4).** ‹ › (or a swipe on the header) steps through past periods.
```
┌──────────────────────────────────────┐
│ ←                                  ⋮ │
│             (🛒) Groceries           │  title: sans titleLarge, centred
│                October               │  subtitle: the period shown
│                                      │
│  ₹6,500 of ₹8,000   [serif hero]     │  MoneyText hero
│  spent · ₹1,500 left        (‹) (›)  │  caption + PeriodSwitcher
│  ████████████████▒░░│░               │  ProgressMeter
│  [81% · alert]  ▒ ₹560 unverified    │
├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─┤
│ ╭──────────────────────────────────╮ │
│ │ Period      Monthly, from the 1st│ │  GroupedSection: facts
│ │ Covers     (🛒) Groceries, (🥦) …│ │
│ │ Daily pace          ₹500/day left│ │  remaining ÷ days left (core)
│ ╰──────────────────────────────────╯ │
│  Past months                         │
│ ╭──────────────────────────────────╮ │
│ │ September        ▬▬▬▬▬▬▬   ₹7,200│ │  spent vs limit per period
│ │ August           ▬▬▬▬▬▬▬▬  ₹8,600│ │  over: negative tone
│ ╰──────────────────────────────────╯ │
│  This month · 14 transactions        │
│ ╭──────────────────────────────────╮ │
│ │ (🛒) BigBasket         −₹1,240.00│ │  TransactionRow → transaction detail
│ ╰──────────────────────────────────╯ │
│ [ Archive ]       [  Edit budget   ] │  BottomActionBar
└──────────────────────────────────────┘
```

**Transaction detail (everyday, C5).** The glyph and amount fly in from the row as shared elements.
```
┌──────────────────────────────────────┐
│ ←                                 ⋮  │
│                                      │
│              ( 🍔 )                  │  56dp glyph (shared element)
│             −₹540.00                 │  amountLarge, negative (shared)
│           Swiggy order               │
│       Food › Delivery  [Verified]    │
├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─┤
│ ╭──────────────────────────────────╮ │
│ │ Account          HDFC Savings  › │ │  GroupedSection
│ │ Date         Wed 29 Oct, 1:05 pm │ │
│ │ Tags                 #work #lunch│ │
│ │ Source        Manual · by Akash  │ │
│ ╰──────────────────────────────────╯ │
│  History                             │
│ ╭──────────────────────────────────╮ │  AuditHistoryList
│ │ Amount ₹500.00 → ₹540.00         │ │
│ │ by Akash on Pixel · 29 Oct 1:10pm│ │
│ │ Created by Akash on Pixel        │ │
│ ╰──────────────────────────────────╯ │
│ [ Delete ]          [     Edit     ] │  BottomActionBar (secondary + primary)
└──────────────────────────────────────┘
```

## 6. Implementation steps (TDD-ordered)

**Conventions:**
- Production code goes in `app/src/main/kotlin/dev/imagh/pft/app/ui/{theme,common}/`.
- JVM tests go in `app/src/test/kotlin/dev/imagh/pft/app/ui/...`. Use Robolectric `@Config(sdk = [35])` on JUnit Vintage like `ManifestHardeningTest`, and plain JUnit 5 where no Android is needed.
- Screenshot tests use Roborazzi with `@GraphicsMode(NATIVE)` and the qualifiers `w393dp-h851dp-xxhdpi`, in light and `+night`, at font scales 1.0 and 2.0 where text matters. Goldens are committed in `app/src/test/screenshots/` with fake data only.
- Every step ends with a green `./gradlew check`.

### 6.1 Design-system slice: PRs into `feat/m1a-foundation`

**DS1: colour tokens, `PftTheme`, window themes**
- **Red:**
  - `ui/theme/ColorContrastTest` (JUnit 5, no Robolectric; contrast via `Color.luminance()`). For light and dark:
    - `positive`, `negative` and `warning` are at least 4.5:1 against the brand surface roles (not `surfaceVariant`, see "DS1 deviations") and against greys at the M3 surface tone limits (light tones 87–100, dark 4–24).
    - Every on-container/container pair is at least 4.5:1.
    - Brand `onPrimary`/`primary` and `onSurfaceVariant`/`surface` are at least 4.5:1, and `primary`/`surface` is at least 3:1.
    - It fails today because no tokens exist.
  - `ThemeResolutionTest` (Robolectric):
    - `resolveColorScheme(dark = false, dynamic = true, ctx)` equals `dynamicLightColorScheme(ctx)`, and `dynamic = false` gives `BrandColors.light`. The same holds for dark.
    - `moneyColors(dark)` is identical whether dynamic is on or off, so it is never derived from the accent.
  - `PftThemeModeTest` (Robolectric Compose): with the `+night` qualifier, `ThemeMode.SYSTEM` yields the dark `surface`, and `LIGHT` overrides it to light; the reverse also holds.
  - `WindowThemeTest` (Robolectric): with `+night`, the launched `MainActivity`'s window background colour is the dark surface (no light flash). The status and navigation bars are transparent (edge-to-edge). `FLAG_SECURE` is still set.
- **Green:**
  - Add `ui-test-junit4` to the catalog and `app/build.gradle.kts`, then refresh the locks and verification metadata. (`ui-test-manifest` was dropped; see "DS1 deviations" below.)
  - `ui/theme/{BrandColors,MoneyColors,ThemePrefs,PftTheme}.kt`.
  - `res/values/themes.xml`: keep the parent and add `windowBackground`, `statusBarColor`/`navigationBarColor` transparent, and `windowSplashScreenBackground`. Add `res/values-night/themes.xml` with parent `@android:style/Theme.Material.NoActionBar`.
  - `MainActivity`: `enableEdgeToEdge()` after the `FLAG_SECURE` line, and `PftTheme { Surface { DevContent() } }`.
  - Add the `DevContent` debug/release pair, which is empty until DS2.
- **Refactor:** KDoc on every public token. `app/README.md` (public interface: `ui/theme`).

**DS1 deviations (2026-10-29)** (all accepted in code review):
- **`ui-test-manifest` dropped.** As `debugImplementation` it adds an exported `androidx.activity.ComponentActivity` to the merged debug manifest, so `ManifestHardeningTest` ("only the launcher activity is exported") went red on debug. It is also absent from the release unit-test task, where `createComposeRule()` then cannot resolve its host activity. Replaced by a test-only `HostedComposeRule` that registers `ComponentActivity` with Robolectric's package manager; every later Compose JVM test in `:app` uses it. Instrumented tests (A4b) need their own answer.
- **Money-text contrast sweep excludes `surfaceVariant`.** §3.3 says "every brand surface role", but dark `negative` on `surfaceVariant` is 4.23:1. `surfaceVariant` is a tone-30 fill (dark) for input tracks and outlines, outside the stated dark surface range of tones 4-24, and its text colour is `onSurfaceVariant` (tested). The sweep covers `background`, `surface`, `surfaceDim`/`surfaceBright`, the five `surfaceContainer*` steps and greys at light tones 87-100 and dark tones 4-24. The rule for authors, written in the `MoneyColors` KDoc and `app/README.md`: never draw money text on `surfaceVariant`.
- **Brand scheme tool.** Generated with `@material/material-color-utilities` 0.3.0 (the engine behind Material Theme Builder, run once offline from a scratch directory; not a project dependency). That version emits the older surface roles, so `surfaceDim`, `surfaceBright` and `surfaceContainer*` were filled by hand from the same neutral palette at the current M3 1.3 tone ladder. Provenance is in the `BrandColors` KDoc.
- **MagicNumber** is excluded for `**/ui/theme/**` in `config/detekt/detekt.yml` (design tokens are named literals).
- **Edge-to-edge** is verified through bar icon appearance only: `setDecorFitsSystemWindows` has no getter under Robolectric and the cutout mode is not applied on API 30+. Layout behind the bars is an emulator check.

**DS2: type, shape, spacing, bundled serif, screenshot harness, catalogue**
- **Red:**
  - `PftTypographyTest` (Robolectric Compose):
    - With the defaults, `amountHero` uses the Newsreader family and every M3 role (`display*`, `headline*`, `title*`, `body*`, `label*`) uses `FontFamily.Default` (Q10: titles are sans).
    - `ThemePrefs(displayFont = TEXT)` makes `amountHero` use the text family.
    - `amountHero`, `amountLarge` and `amountRow` all carry `tnum`.
  - `BundledFontTest` (Robolectric):
    - `resources.getFont(R.font.newsreader)` loads.
    - `assets/licenses/newsreader-OFL.txt` exists and contains "SIL OPEN FONT LICENSE Version 1.1" and the Newsreader copyright line.
  - `TokenSheetScreenshotTest` (Roborazzi) renders the type scale, shapes, spacing ruler and the money tokens on surfaces, in light/dark × font scale 1.0/2.0. It fails because there are no goldens yet.
- **Green:**
  - Add `ui-tooling-preview` (impl), `ui-tooling` (debugImpl) and Roborazzi 1.36.0 (test libraries and plugin) to the catalog. Set `roborazzi.test.verify=true` and an output dir of `src/test/screenshots` in `gradle.properties`/`app/build.gradle.kts`, so `check` verifies against the goldens. Refresh the locks and checksums.
  - `ui/theme/{PftTypography,PftShapes,PftSpacing}.kt` and `res/font/newsreader.ttf` (hash-checked).
  - `assets/licenses/newsreader-OFL.txt`.
  - `ui/theme/TokenSheet.kt` (`@PreviewLightDark internal`).
  - The debug `DesignCatalog` showing the token sheet.
  - Record the goldens with `./gradlew :app:recordRoborazziDebug` and review the PNGs in the PR.
- **Refactor:** create `docs/architecture/design-system.md` (principles, tokens, theming, third-party assets table). Learnings: the Roborazzi and native-graphics setup.

**DS2 deviations (2026-10-29):**
- **`ui-tooling` adds an exported `PreviewActivity`** to the debug manifest, which `ManifestHardeningTest` rejected (the same trap as DS1's `ui-test-manifest`). `src/debug/AndroidManifest.xml` removes it with `tools:node="remove"`. Studio's "run preview on device" no longer works; static previews and the screenshot tests do not need it.
- **`ThemePrefs` gains only `displayFont`** (`DisplayFont { BUNDLED_SERIF, SYSTEM_SERIF, TEXT }`). `textFont` and `DisplayFont.CUSTOM` need a loaded `FontFamily` and arrive with custom-font import (AP2), so there is no field that cannot be exercised yet (YAGNI). `PftTheme` passes `FontFamily.Default` as the text family.
- **Type token class is `PftTypography`** (not `PftType`: detekt `MatchingDeclarationName` wants the file name). `PftTheme.type` returns it. `PftTheme.shapes` returns the `PftShapes` object (`glyph`; the M3 `Shapes` are `PftShapes.material`, applied to `MaterialTheme`). `PftTheme.spacing` is a plain constant, not a CompositionLocal: the scale is a fixed grid, so a local would be single-value indirection (§3.2 lists `LocalSpacing`; it is dropped until something needs to vary it).
- **Newsreader optical size.** Plan text says "opsz axis auto"; Compose has no automatic mapping, so the hero is pinned to weight 400 and `FontVariation.opticalSizing(48.sp)` (the display cut). The DS5 shrink steps keep the 48sp cut (closed in DS5, see "DS5 deviations").
- **Token sheet is split into pages** (`TokenSheetPages`, five pages) and each page is a golden, captured by node bounds: a single sheet does not fit one 393x851dp screen at 200% font scale. That gives 20 PNGs (5 pages x light/dark x 1.0/2.0, 43 to 111 KB each, 1.5 MB total), all under the 500 KB large-file hook. The sheet's hero and large samples are short ("₹82,450") so they do not wrap at 200%; the shrink-to-fit behaviour is DS5's.
- **Extra tests beyond §6.1:** `PftShapesTest`, `PftSpacingTest` (the radii and the scale read back through the real theme) and `DesignCatalogTest` in a `src/testDebug` source set (the catalogue exists only in debug). `BundledFontTest` also pins the font's size and SHA-256, which is the "hash-check" for provenance.
- **Lock refresh.** Roborazzi's Gradle plugin runtime brings `junit:junit:4.13.2` and `hamcrest-core:1.3` into `buildscript-gradle.lockfile` (both already checksum-verified for the app's tests). `--write-verification-metadata` alone failed the lock check for them on a fresh `GRADLE_USER_HOME`, so they were added with `--update-locks`, and the plugin artifacts were verified first with `--dependency-verification=lenient` plus `--write-verification-metadata`. See `docs/learnings/README.md`.
- **Not done in this slice:** tuning the hero's letter spacing and line height on the owner's phone (§3.3 "Display spacing"; the values are named constants in `PftTypography.kt`), and the emulator walk-through of §11. Re-record the goldens after any tuning.
- **`TokenSheet` lives in `main`** (as §6.1 says), so it is compiled into release but unreferenced there; the debug `DesignCatalog` that shows it does not ship.

**DS3: icons, palette, `CategoryGlyph`, emoji policy** (can run in parallel with DS4)
- **Red:**
  - `PftSymbolTest` (Robolectric):
    - Keys are unique and match `[a-z0-9_]{1,40}`.
    - Every key in `src/test/resources/pft-symbol-keys.txt` still exists (persisted keys are permanent).
    - Every symbol's drawable inflates.
    - `fromKey("nope")` is `null`.
  - `CategoryPaletteContrastTest` (JUnit 5): for each of the 12 keys and both themes, content on container is at least 3:1 (symbol) and at least 4.5:1 (letter fallback).
  - `CategoryGlyphTest` (Robolectric Compose):
    - A symbol source renders its drawable with the given content description (the category name, not the key).
    - An emoji source renders the emoji text.
    - A `null` symbol renders the first letter of the name, upper-cased.
    - When clickable, the touch target is at least 48dp.
  - `ManifestHardeningTest`: add `emoji compat initializer is not registered` (the merged manifest has no `androidx.emoji2.text.EmojiCompatInitializer` meta-data).
  - `CategoryGlyphScreenshotTest`: a grid of all symbols × 3 palette keys, plus emoji and letter fallbacks, in light and dark.
- **Green:**
  - About 90 `res/drawable/sym_*.xml`.
  - `ui/theme/{PftSymbol,CategoryPalette}.kt` and `ui/common/{CategoryGlyph,GlyphSource}.kt`. `GlyphSource` is a UI model: `Symbol(PftSymbol)`, `Emoji(String)` or `Letter(Char)`.
  - `AndroidManifest.xml`: inside `InitializationProvider` (`tools:node="merge"`), add `<meta-data android:name="androidx.emoji2.text.EmojiCompatInitializer" tools:node="remove"/>`.
  - `config/detekt/detekt.yml`: `style.ForbiddenImport.imports: ['androidx.compose.material.icons.*']`, with the reason as a comment.
  - `assets/licenses/material-symbols-APACHE-2.0.txt`.
  - Add a glyph section to the catalogue.
- **Refactor:** `design-system.md` "Adding an icon" how-to and the curated list. `app/README.md` manifest-posture table gets an EmojiCompat row.

**DS3 deviations (2026-10-30):**
- **The curated list is 92 symbols** (31 chrome, 61 category), named in `tools/material-symbols/symbols.txt` (the plan gives a count, not names) and tabulated in `design-system.md`. Chrome keys are semantic (`back`, `search`, `filter`, `overflow`, `chevron_left`, `chevron_right`, `calendar`, `transfer`, `tag`, ...), which is what DS6e needs. `local_grocery_store` does not exist in Material Symbols at the pinned commit, so the key is `grocery`.
- **Imported by a script, not Android Studio's Vector Asset tool.** `tools/material-symbols/import_symbols.py` (stdlib, unit-tested) downloads the official web SVGs from `google/material-design-icons` at a pinned commit (`10ecc14790612ca8fd164e3f4ffe9c16fc50d5b7`), treats each as untrusted (exactly one `<path>`, expected `viewBox`, path-command characters only, no DOCTYPE or entities, 16 KB cap) and keeps only the path data. It makes the import reproducible and puts the "Adding an icon" steps in one command. Each drawable wraps the unchanged path data in a `<group translateY="960">` (the SVG's y axis runs from -960 to 0) instead of rewriting coordinates; there is no `tint` attribute (Compose's `Icon` tints). Per-icon SVG hashes are in `svg-sha256.txt` and are a real check (review fix): the script fetches and validates every file and compares it with its committed hash before writing anything, aborts with exit code 1 on a mismatch for a listed key, records the hash of a key not yet listed, and overwrites a hash only with `--update-hashes`.
- **FILL 0 only.** The "filled variants for selected navigation items" are not shipped: A5 builds the nav bar and adds them (new keys) then.
- **Palette contrast lives in `ColorContrastTest`**, not a separate `CategoryPaletteContrastTest`, because it shares that file's helpers. It asserts content on container is at least 4.5:1 for all 12 keys in both themes (the plan's 3:1 for a symbol is implied). The palette is provided by `PftTheme` as `PftTheme.palette` (a `CompositionLocal`, as §3.2 says) and follows the resolved mode, ignoring dynamic colour (`PftThemeModeTest`). Colours were generated in CIELAB (container tone 90 and content tone 30 in light, swapped in dark).
- **API.** `CategoryGlyph(source, name, colourKey, modifier, size: GlyphSize = Row, onClick = null)`; `GlyphSize { Chip 28, Row 40, Hero 56 }` is in `ui/theme` (the plan's §3.3 sizes as one named place). `colourKey` is the stored palette name (null or unknown is `slate`). The badge is one image node announced as `name`; the symbol, emoji or letter inside is decoration.
- **`GlyphSource.fromIconKey(iconKey, name)` was added**, a raw-string parser for the stored `icon_key`. The plan puts validation in core (B3 `CategoryIcon`) and a `CategoryIcon -> GlyphSource` mapper in B4; but the value is untrusted when the UI reads it (corrupt row, newer synced version), so the UI boundary parses and falls back to `Letter` on its own, and never throws. B3's core `CategoryIcon` should enforce the same grammar at write time; B4's mapper can then build `GlyphSource` directly or call `fromIconKey` on the stored string. `Emoji` cannot hold an invalid value. `Letter` holds one code point as a `String` (with a `Char` constructor), not the plan's `Char`, so a first letter outside the basic plane (found in review) is drawn instead of skipped; `letterFor` scans by code point.
- **The emoji grammar is slightly stricter than "no whitespace or control characters"**: invisible format characters (zero-width space, word joiner, BOM, soft hyphen, LRM/RLM, bidi embeddings, overrides and isolates) and unpaired surrogates are refused too, since they can hide or reorder neighbouring text. The zero-width joiner and the tag characters stay allowed (ZWJ families, flags, subdivision flags such as England's, 14 units). B3 and B4's validator should match.
- **Touch target.** A clickable `CategoryGlyph` relies on Compose's built-in 48dp minimum touch bounds (`ViewConfiguration.minimumTouchTargetSize`), which `CategoryGlyphTest` asserts at every size, rather than also adding `minimumInteractiveComponentSize()` (which would change layout without changing the touch target).
- **Manifest.** The merged manifest had no `InitializationProvider` entry of our own, so one was added (`tools:node="merge"`, `exported="false"`, `tools:ignore="MissingClass"` because lint cannot see `androidx.startup` as a direct dependency) holding the `tools:node="remove"` meta-data. `ManifestHardeningTest` was seen failing first, with a message listing the three initializers registered at that point (`EmojiCompatInitializer`, `ProcessLifecycleInitializer`, `ProfileInstallerInitializer`). Only `EmojiCompatInitializer` is removed; the lifecycle and profile-installer initializers stay registered, and the test passes after.
- **Goldens.** 8 PNGs, 100 to 180 KB each, identical on the debug and release unit-test variants: the whole registry on teal, amber and violet (one grid per colour, light and dark, since 92 x 3 tiles do not fit one 851dp screen) and one sources page (three sizes, emoji, letters in all 12 hues, fallbacks). The plan's catalogue "glyph section" is the Glyphs page.
- **Added tests beyond the plan:** `GlyphSourceTest` (the `icon_key` boundary), `CategoryPaletteTest`, `BundledSymbolsLicenceTest`, the importer's Python tests and a `DesignCatalogTest` case.
- **DS4 and DS5 placeholders were not touched.** The premium-look plan (§3.5) explicitly assigns `PeriodStepper`'s chevron swap to DS6e, which needs the `chevron_left` and `chevron_right` keys defined here; DS3's own Red/Green list does not scope it, and the DS5 `EmptyState` glyph is a slot its consumers fill with a `CategoryGlyph`. Follow-ups: DS6e swaps the stepper chevrons and removes the "text glyphs" note (**done in DS6e**); B2, B4 and C4 pass `CategoryGlyph` into `EmptyState` where a glyph helps.
- **Release footprint.** The plan's budget is "about 90 KB of icons" (§3.8). The unsigned release APK was 1,397,696 B at the base commit (`3da6c4f`) and is 1,402,033 B with DS3 (+4,337 B, the licence asset): R8 and resource shrinking remove all 92 drawables while nothing reachable from `MainActivity` references `PftSymbol`. Measured worst case, with a throwaway reference to every `PftSymbol` from the release `DevContent` so all drawables are kept: 1,484,590 B (+86,894 B, about 87 KB), inside the budget. `:app:verifyReleaseShrink` is green with `CategoryGlyphSamples` and its `ComposableSingletons` twin in `mustBeRemoved`.
- **No lock or verification-metadata change** (vector drawables need no dependency).
- **Not verified:** the glyphs and emoji on a device or the emulator (emoji artwork differs by phone), and TalkBack reading of the badge (asserted through semantics only).

**DS4: motion tokens and the one-hand layout**
- **Red:**
  - `PftMotionTest` (JUnit 5, pure):
    - `iosSpring(450, 0.0)` gives stiffness 194.9 ± 0.5 and ζ 1.0, and `iosSpring(500, 0.25)` gives ζ 0.75.
    - It rejects bounce < 0 or ≥ 1, and duration ≤ 0, with `IllegalArgumentException`.
    - The four tokens equal their table values (§3.3).
  - `CollapsingHeaderScaffoldTest` (Robolectric Compose, 393×851dp, `mainClock` controlled):
    - Expanded: the first content item's top is at least 35% of the window height, and the large title is displayed.
    - After `swipeUp` of 400dp, the header is 64dp and the small title is displayed.
    - Dragging 30% of the collapse range and releasing settles back to expanded. Dragging 70% settles collapsed (snap).
    - Content shorter than the viewport keeps the header expanded after a swipe.
    - At 393×500dp the header starts collapsed.
    - With `HeaderStyle.Dashboard` the hero uses the display family and the title uses the text family.
    - Header layout (Q10), expanded, both styles: the title is horizontally centred (its centre within 1dp of the window centre) and has no click action. Dashboard: the title's bottom is above the hero's top with at least 24dp between them, and the `period` stepper's bottom is in the lower half of the header. Collapsed: the bar shows the title, and the hero is not displayed.
    - A horizontal swipe on a Dashboard header with a `period` invokes `onPrevious`/`onNext` once; a vertical drag does not.
  - `BottomActionBarTest`:
    - The primary button's bottom is within 120dp of the window bottom, and its height is at least 48dp.
    - With two actions, the primary is on the end (right) side.
    - A disabled primary is not clickable.
  - `PressScaleTest`: while pressed, the scale settles to 0.97 after the clock advances, and on release it returns to 1.0. This asserts the graphics-layer scale through semantics or a test tag on a `Modifier.graphicsLayer` reader.
  - Screenshots: Standard and Dashboard styles, expanded and collapsed, light and dark.
- **Green:** `ui/theme/PftMotion.kt` (tokens, `iosSpring`, nav transition specs, `pressScale`), and `ui/common/{CollapsingHeaderScaffold,BottomActionBar}.kt`. Add both to the catalogue.
- **Refactor:** `design-system.md` motion and one-hand sections. `code-structure.md` shared-blocks rows.
- **Release guard (added 2026-10-30):** any `@Preview` or sample code DS4 adds (`ui/common/HeaderSamples`) is added to `mustBeRemoved` in `:app:verifyReleaseShrink` (`app/build.gradle.kts`), so R8 is shown to drop it.

**DS4 deviations (2026-10-29):**
- **`heroCaption` and `HeaderPeriod` added to the slot list.** §3.4 lists `hero` and a `period` slot (the `PeriodSwitcher`, built in C4) but the Q10 rule also needs a caption row with the stepper at its end, and the test spec needs `onPrevious`/`onNext` on the scaffold. So the scaffold takes `heroCaption: String?` and `period: HeaderPeriod(onPrevious, onNext)`, draws the ‹ › stepper itself (text glyphs until DS3's icons; **closed in DS6e**: it now uses `HeaderIconButton` with the chevron symbols) and handles the swipe. C4's `PeriodSwitcher` builds on that (the directional slide) rather than replacing it.
- **`BottomActionBar` takes `BottomBarAction(label, enabled, onClick)` values** (primary, optional secondary) instead of loose label/callback pairs.
- **Motion is a plain constant, not a CompositionLocal.** `PftTheme.motion` returns the `PftMotion` object, like `spacing` and `shapes`: the tokens never vary, so `LocalMotion` (§3.2) would be single-value indirection. The springs are `SpringSpec<Float>`; other types use `iosSpring<T>`.
- **`pressScale()` is a `composed` modifier** (a modifier-factory `@Composable` trips Compose lint), drives an `Animatable` from the pointer coroutine, and never consumes events. A composed block does not recompose on state it reads, so the press state is not read there.
- **The scaffold owns the `LazyColumn`** (`content: LazyListScope.() -> Unit`, as §3.4 says) because `canScroll` needs its scroll state; it passes `Scaffold`'s padding as `contentPadding`, so rows scroll under the opaque header.
- **`canScroll` also stays true while the header is partly collapsed**, so a list that fits only when collapsed can still be pulled back open.
- **Screenshot set:** Standard and Dashboard, expanded and collapsed, light and dark (8 PNGs), plus `BottomActionBar` light and dark (2), 10 in total, 89 to 108 KB each. Font scale 2.0 is not captured for these.
- **detekt `LongParameterList` is ignored for `@Composable` functions** (Compose slots are defaulted parameters); the rule still applies to everything else.
- **Review fixes (2026-10-29).** `bottomBar` is nullable: M3's `Scaffold` pads the list and FAB by the bottom inset only when the bottom-bar slot emits no placeables, and the always-present `imePadding` wrapper had defeated that (found in review, confirmed against `Scaffold.kt` 1.3.1; the new inset test failed first with the last row under a 48dp bar). A `period` or caption without a `hero` is inert (no stepper, no swipe). `LongParameterList` is relaxed with `ignoreDefaultParameters` rather than for every `@Composable`.
- **§7 font scale 2.0 for the header: met** for the Dashboard header with a subtitle on 393x851dp (gap 67dp, asserted). **Closed in DS5 (2026-10-30):** a long hero shrinks to fit at 2.0x inside the scaffold (`CollapsingHeaderScaffoldTest`: the real maximum `Paise.MAX_AMOUNT` on 393dp and 320dp windows, shown in full and inside the gutters, title still 24dp above it). Still not covered: a short expandable window at 2.0x.
- **Below 560dp the Dashboard hero is hidden** (the header is always collapsed), as §3.4 specifies; recorded in `design-system.md` so screens plan for it.
- **Not verified:** the §11 jank check (deferred to C4, where scrolling a real list first makes the number meaningful) and status-bar/cutout insets on a device. Navigation-bar and IME insets are tested under Robolectric.

**DS5: content primitives** (needs A2 merged: `:app` gains `implementation(project(":core:money"))`)
- **Red:**
  - `MoneyToneTest` (JUnit 5, pure):
    - `Income` → Positive, `Expense` → Negative in totals and Neutral in rows (Q2 default), `Transfer` → Neutral.
    - `Balance`: > 0 → Neutral, < 0 → Negative, 0 → Neutral (Q3).
    - `Budget`: `UNDER` → Positive, `AT_THRESHOLD` → Warning, `OVER` → Negative.
  - `MoneyTextTest` (Robolectric Compose):
    - `Paise(12345678)` as `Expense` displays exactly `−₹1,23,456.78`. The body is `PaiseFormat.format`'s output, prefixed with U+2212.
    - `Income` displays `+`, and `Transfer` has no sign.
    - The semantics content description starts with "Expense" or "Income".
    - The hero variant with the maximum amount (`₹10,00,00,00,000.00`, the `LedgerLimits` max) at width 320dp and font scale 2.0 has no `hasVisualOverflow` and uses a size from the step list.
    - With `animate = true`, changing the value ends on the new text after the clock advances.
  - `StatusChipTest`: every status renders a text label; colour is never the only cue.
  - `GroupedSectionTest`: N rows give N−1 inset dividers, and a clickable row is at least 56dp tall.
  - `EmptyStateTest`: shows the message, and the action invokes its callback.
  - Screenshots for each, in light and dark, with `MoneyText` also at font scale 2.0.
- **Green:** `ui/common/{MoneyText,MoneyTone,StatusChip,GroupedSection,EmptyState}.kt`, plus catalogue sections.
- **Refactor:**
  - Remove `MoneyText` from B2's list in M1a §3.4 (doc amendment, §9).
  - Add the `ui/common` blocks to `code-structure.md`.
  - `app/README.md` public interface.

**DS5 deviations (2026-10-30):**
- **Hero steps extended to 48, 40, 34, 28, 24, 20, 16, 12sp.** The plan's example amount (`₹10,00,00,00,000.00`, Rs 1e10) is not the limit: `Paise.MAX_AMOUNT` is Rs 1e11 (`₹1,00,00,00,00,000.00`, one digit group longer; A2 learnings). At 200% font scale that needs 16sp on 393dp and 12sp on 320dp, so the 28sp floor was not enough. The hero-overflow test uses the real maximum. If even 12sp does not fit the hero wraps rather than clipping (`HeroSizeSteps` in `PftTypography.kt`). The optical size stays at the 48sp cut for every step; the DS2 "revisit if it looks off" note is closed (the 2x golden shows the smaller steps reading fine).
- **Fit is asserted with line metrics, not `hasVisualOverflow`.** A probe showed `TextLayoutResult.hasVisualOverflow` is true for any wrap-content `Text` narrower than its maximum and false for a `maxLines = 1` text that is really cut (the amount broke after its sign and showed only "−"). The tests use `isShownInFull()` (`TextLayoutSupport.kt`). The 320dp/2.0x check is in `MoneyTextTest` and `CollapsingHeaderScaffoldTest`.
- **API shape.** `MoneyText(amount, semantic, modifier, size: MoneySize = Row, animate = false)`; `MoneySize { Hero, Large, Row }` picks the amount style and decides "row" for Q2. `MoneySemantic` is `Income | Expense | Transfer | Balance | Budget(state)` (`Balance` is an object: the sign comes from the signed amount, so the plan's `Balance(signed)` payload was redundant). `moneyTone(semantic, amount, inRow)` is the pure mapping `MoneyToneTest` covers.
- **`BudgetState { UNDER, AT_THRESHOLD, OVER }` is the UI's own enum** because core's `BudgetProgress` does not exist until D1; D4 maps it.
- **A negative amount is rejected** (`IllegalArgumentException`) for every semantic except `Balance`, rather than shown with a sign the semantic contradicts. The ledger's amounts are positive, so this is a bug trap.
- **`StatusChip(label, tone: MoneyTone)`** reuses `MoneyTone` instead of a second four-value enum; a blank label is rejected (colour must not be the only cue). Colours come from a pure `chipColors` so `ColorContrastTest` sweeps them; neutral is `surfaceContainerHighest`/`onSurface`. The pill colour itself is checked through that mapping and the goldens (`captureToImage` times out under `HostedComposeRule`).
- **`GroupedSection { row(onClick?) { ... } }`** (a rows lambda, so the section owns the dividers), plus `GroupedSectionDefaults.listItemColors()` (a transparent container, because `ListItem` paints `surface`). The divider is one physical pixel, not `Dp.Hairline`, which lays out at zero height and cannot be measured. The island is inset by the 20dp gutter itself so every consumer aligns.
- **`EmptyState(message, glyph?, actionLabel?, onAction?)`.** The glyph is a slot until DS3's `CategoryGlyph` exists; label and callback are required together.
- **`PftMotion.smoothOffset`** (the `smooth` spring over `IntOffset`) is now public for the rolling-digit slide; it was a private field. Rolling digits are one `AnimatedContent` per character, keyed from the end.
- **Money off `surfaceVariant` is pinned for what exists, not for the future.** `ColorContrastTest` asserts the sweep's exclusion (dark `negative` on `surfaceVariant` is still below 4.5:1) and that the island and every chip pill are swept surface roles. It cannot stop a later component choosing `surfaceVariant`; that stays a review rule, and a new money surface should add its colour to the test.
- **Caller contract for amounts (review, 2026-10-30).** `MoneyText` throws `IllegalArgumentException` in composition for a negative `Paise` with any semantic except `Balance`, so **callers must pass magnitudes (non-negative `Paise`) for every semantic except `Balance`**, validated in the ViewModel or mapper (C4, C5, D4), not left to the composable. Recorded in `design-system.md`.
- **Review fixes (2026-10-30).** An animated hero that cannot fit even at 12sp now falls back to plain wrapping text (rolling digits cannot wrap and had been clipping). Clickable `GroupedSection` rows carry `Role.Button`.
- **Samples and goldens.** One `*Samples.kt` per component (`MoneyTextSamples`, `StatusChipSamples`, `GroupedSectionSamples`, `EmptyStateSamples`), each added with its `ComposableSingletons$` twin to `mustBeRemoved`. 12 new goldens (19 to 170 KB; `MoneyText` also at 2.0x), by `ContentScreenshotTest`. The Dashboard header sample now uses a `MoneyText` hero, so `header-dashboard-expanded-{light,dark}.png` were re-recorded (the figure is the money red). The catalogue gained Money, Chips, Sections and Empty pages (section chips wrap in rows of three).
- **No lock or verification-metadata change** for `implementation(project(":core:money"))`: the module has no external dependencies.
- **Not verified:** the catalogue and the animation on a device or emulator (rolling digits are asserted mid-roll and at their end state under Robolectric only), and the §11 jank check.

### 6.2 Appearance slice: base `feat/m1a-appearance` off `main`, after A-final (Q4)

**AP1: appearance settings and licences**
- **Known limitation from DS1, AP1 must address:** plain `enableEdgeToEdge()`, the window theme and the splash follow the *system* night mode. When the user forces `DARK` on a light system (or the reverse), the bar icons and the pre-Compose window colour would be wrong. AP1 handles this with `UiModeManager.setApplicationNightMode` (§5.1) and/or explicit `SystemBarStyle`s, and `AppearanceFlowTest` should cover a forced mode against the opposite system mode.
- **Red:**
  - `AppearanceSettingsTest` (Robolectric, real in-memory Room via M1a's `TestDatabase`, through `SettingsService`):
    - Unset keys read as `ThemePrefs.Default`.
    - `setThemeMode(DARK)` persists, appends an audit entry, and sets `UiModeManager.applicationNightMode` to `MODE_NIGHT_YES`.
    - A stored `"PURPLE"` reads as `SYSTEM` without throwing.
    - A no-op set writes no audit row.
  - `AppearanceFlowTest` (Robolectric Compose, through the nav host):
    - Settings › Appearance › Dark makes the surface dark.
    - Turning dynamic colour off makes `primary` equal the brand primary.
    - Display font "Same as text" makes `amountHero` use the text family.
    - Licences › Newsreader shows the OFL text.
- **Green:**
  - `app/.../settings/AppearanceSettings.kt`: typed keys over `SettingsService`, with `prefs: StateFlow<ThemePrefs?>`.
  - `ui/settings/{SettingsScreen,AppearanceScreen,AppearanceViewModel,LicencesScreen}.kt`, built from `CollapsingHeaderScaffold`, `GroupedSection` and M3 `ListItem`/`Switch`/`RadioButton`.
  - `MainActivity` collects the prefs and holds the first frame for at most 500 ms.
- **Refactor:** `docs/features/appearance.md`.
- **DS6 additions (2026-10-30):**
  - AP1 also persists `ui_palette_light`, `ui_palette_dark`, `ui_background`, `ui_header_glow`, `ui_type_pairing` and `ui_font_sans`, with the underscored keys, defaults and parse rules in `2026-10-30-m1a-premium-look.md` §4.
  - It pins the persisted enum names in a test resource.
  - It adds the Appearance rows, the font licences and the window-colour flow test listed in that plan's §6.3.
  - `SyncWindowBackground` is already wired by DS6c.

**AP2: custom font import**
- **Red:**
  - `FontFileCheckTest` (JUnit 5, pure):
    - Accepts the headers `00 01 00 00`, `OTTO` and `true`.
    - Rejects `ttcf`, `wOFF`, `wOF2`, an empty input, fewer than 4 bytes, PNG bytes, and a declared size over `MAX_FONT_BYTES`.
  - `CustomFontStoreTest` (Robolectric; a content URI served via `ShadowContentResolver.registerInputStream`, fed a copy of the bundled Newsreader as the valid fixture):
    - A valid import lands at the fixed path, `load(TEXT)` returns a `FontFamily`, and the setting is `CUSTOM` (audited).
    - A file with valid magic but a corrupt body is rejected: the previous file and setting are unchanged, and no temp file is left.
    - A stream longer than the cap is rejected without reading past the cap.
    - `clear(TEXT)` deletes the file and resets to `SYSTEM`.
    - If the file was deleted behind the app's back, startup falls back to the default and the result is `FontFallback`.
  - `CustomFontFlowTest`: "Import font…" through a test `ActivityResultRegistry` makes a rendered `body` sample use the custom family. A rejected file shows the error and leaves the font unchanged.
- **Green:** `app/.../appearance/{FontFileCheck,CustomFontStore,AppearanceLimits}.kt` and the `AppearanceScreen` import/clear rows. Add the `LogEvent.FontImportRejected(reason: enum)` and `FontFallback(slot: enum)` events.
- **Refactor:** feature doc (custom fonts, limits, what happens on failure). `app/README.md` configuration (`AppearanceLimits`).

### 6.3 Amendments to M1a steps (consumption; this plan edits M1a when approved)

| M1a step | Change |
|---|---|
| **A5** | **Navigation:** `PftNavHost` uses `PftMotion` nav transitions, and the manifest sets `enableOnBackInvokedCallback="true"`. `MainActivity` swaps `DevContent()` for the nav host, and the catalogue becomes a debug-only route. **Forms:** `EditFormScaffold` = `CollapsingHeaderScaffold(Standard)` + `BottomActionBar(Save)` + `imePadding`, and `SetupScreen` is its first consumer. `SetupFlowTest` additionally asserts that Continue is in the bottom bar. **Lint:** `SafeLog` uses a `ForbiddenImport` of `android.util.Log`, not `ForbiddenMethodCall` (§2 finding). |
| **B2** | `EntityListScreen` = `CollapsingHeaderScaffold` + M3 `ListItem` + `EmptyState`. `MoneyText` comes from DS5. **New here:** `PftBottomSheet` (first consumer `AccountPicker`). The account type badge is a `CategoryGlyph` with a symbol. |
| **B3** | `CategoryColour` enum and `CategoryIcon` (§4) in core, with validation tests in `CategoryServiceTest`. `DefaultCategories` assigns symbols and colours. |
| **B4** | Category edit has a symbol grid (`PftSymbol`), an emoji tab (quick-pick plus keyboard, `EmojiInputValidator` + `EmojiInputValidatorTest`: one grapheme, ZWJ family OK, flag OK, two emoji rejected, letter rejected, zero-width space rejected) and palette swatches. `CategoryPicker` is a `PftBottomSheet` grid of `CategoryGlyph`. A shared `CategoryIcon → GlyphSource` mapper goes in `ui/common` (consumers: C4, D4, E4). |
| **C4** | Home uses `HeaderStyle.Dashboard`: the month ("October 2026") as the centred sans title, a hero `MoneyText(animate = true)`, and the `PeriodSwitcher` in the `period` slot. **New here:** `PeriodSwitcher` and `TransactionRow` (shared), plus `AmountKeypad` (local). The FAB → add container transform via `SharedElements` (`gentle`), `Modifier.animateItem()` on insert and delete, and the `StatusChip` Unverified. **Amount contract:** the list and totals mapper passes magnitudes (non-negative `Paise`) to `MoneyText` for `Income`/`Expense`/`Transfer`, which throws in composition otherwise; only an account `Balance` may be negative. |
| **C5** | List → detail shared elements (glyph and amount). The detail is on `GroupedSection` + `AuditHistoryList` + `BottomActionBar(Edit, Delete)`. **Amount contract:** the detail mapper passes the stored positive amount with its direction as the `MoneySemantic` (non-negative `Paise`; `MoneyText` throws otherwise). |
| **D4** | **New here:** `ProgressMeter` (shared). Its test asserts that the unverified segment is drawn with a distinct pattern (screenshot) and labelled in semantics (B-3). **Two screens (Q11):** the Budgets tab is a `Standard` list of budget cards (spent of limit, `ProgressMeter`, status pill; FAB → new budget), and `BudgetDetailScreen` is `Dashboard` style (title = budget, subtitle = period, hero = spent of limit, `PeriodSwitcher` through past periods; facts, daily pace, past periods and this period's `TransactionRow`s; `BottomActionBar(Edit, Archive)`). **Amount contract:** the budget mapper passes non-negative `Paise` with `Budget(state)`, for example the overspend as a magnitude with `OVER`, never a negative remainder (`MoneyText` throws otherwise). |
| **E4 / F3 / G2** | E4 uses `StatusChip` for Overdue/Paused/Cancelled. F3 and G2 use `ProgressMeter`. G2 completion uses `bouncy`, which reduced motion skips. |
| **H1 / H3** | H1: the lock screen uses `PftTheme`, and the timeout row joins AP1's `SettingsScreen`. H3: motion is judged on a minified release build (§11), and jank numbers go in learnings. |

### PR breakdown (incremental — required)

**Target branches:**
- DS1–DS5 are slices of the existing **`feat/m1a-foundation`** base. The owner wants them before A5, and A5 is in that base. Slice branches follow the learned naming rule (`docs/learnings/README.md`, 2026-10-28): `feat/m1a-foundation-ds1-colour-theme` and so on.
- AP1–AP2 get their own base, **`feat/m1a-appearance`**, off `main` after A-final, with a final AP → `main` PR.

| PR | Scope (steps) | Target | Depends on | Why it's independently reviewable |
|----|---------------|--------|------------|-----------------------------------|
| DS1 | colour tokens, `PftTheme`, day/night window, edge-to-edge, compose-test deps | `feat/m1a-foundation` | A1 merged into the base (Q9) | one concern: colour and theme resolution, proven by contrast and resolution tests |
| DS2 | type, shape, spacing, Newsreader and licence, preview and Roborazzi harness, catalogue skeleton | `feat/m1a-foundation` | DS1 | static tokens plus the visual test harness, first goldens |
| DS3 | Material Symbols drawables, `PftSymbol`, palette, `CategoryGlyph`, EmojiCompat removal, icon import ban | `feat/m1a-foundation` | DS2 (parallel with DS4) | icon and emoji policy in isolation |
| DS4 | `PftMotion`, `CollapsingHeaderScaffold`, `BottomActionBar` | `feat/m1a-foundation` | DS2 (parallel with DS3) | the one-hand layout and motion, behaviour-tested |
| DS5 | `MoneyText`/`MoneyTone`, `StatusChip`, `GroupedSection`, `EmptyState` | `feat/m1a-foundation` | DS4, A2 | content primitives on top of A2's formatter |
| A5 (existing, amended) | `SafeLog`, `AppContainer`, setup, consuming DS | `feat/m1a-foundation` | DS5, A4b | as M1a |
| A-final | foundation → `main` | `main` | A1–A5, DS1–DS5 | as M1a |
| AP1 | appearance settings, licences screen | `feat/m1a-appearance` | A-final | settings plus one screen |
| AP2 | custom font import | `feat/m1a-appearance` | AP1 | untrusted-file handling isolated for focused security review |
| AP-final | appearance → `main` | `main` | AP1–AP2 | one capability |

Each PR carries its own docs, learnings entry, security-review section and a `graphify update .` (TECHNICAL.md §5, §2). Every PR with visual changes includes its golden PNG diffs for review.

## 7. Test plan

**Unit tests** (plain JVM, fast, no Android): colour contrast (money, brand, palette, both themes, extreme surface tones), `iosSpring` maths and token values, `MoneyTone` mapping, `FontFileCheck` magic and size rules.

**Integration tests** (Robolectric, real collaborators):
- Theme resolution with real `dynamic*ColorScheme(context)`.
- Theme mode against the `+night` configuration.
- The window background in night mode, and `FLAG_SECURE` still on.
- Header collapse and snap through real nested scroll, with the clock controlled.
- `BottomActionBar` reach.
- `MoneyText` with the real `PaiseFormat` from `:core:money`, plus overflow at 200% font.
- Bundled font loading.
- Symbol drawables inflating.
- The merged manifest (EmojiCompat absent).
- AP1 settings through real `SettingsService` and in-memory Room, with audit rows asserted.
- AP2 through a real `ContentResolver` stream, real files and real `Font.Builder` parsing.

No mocks. The only fakes are Robolectric's platform shadows and a test `ActivityResultRegistry` at the SAF boundary.

**Screenshot tests** (Roborazzi goldens, verified in `check`): the token sheet, glyph grid, scaffold states, `MoneyText`, chips, sections and empty state, in light and dark, at font scales 1.0 and 2.0 where text matters. Goldens are review aids for visual regressions. **Correctness is carried by the behaviour tests above, not by pixels.**

**Manual, on device** (release-like build, §11): spring feel at 60/120 Hz, predictive back, dynamic colour across wallpapers (including a red and a green wallpaper), the One UI system font, a TalkBack pass over the catalogue, and "Remove animations".

**Edge and failure cases:**
- Maximum amount at 200% font.
- An unknown symbol key or palette key.
- Settings values written by a newer version.
- A non-scrollable list under the header.
- A short window.
- A corrupt, oversized, collection or WOFF font file.
- A custom font file deleted behind the app's back.
- A dynamic accent that is itself green or red.

**AC → test map**

| AC | Tests |
|---|---|
| UI-1 | Screenshot goldens (owner reviews them in the DS PRs), `docs/architecture/design-system.md` principles, manual pass |
| UI-2 | `PftTypographyTest` (default family), `CustomFontStoreTest`, `CustomFontFlowTest`, `FontFileCheckTest` |
| UI-3 | `PftTypographyTest` (`amountHero` = serif, every M3 role = text), `CollapsingHeaderScaffoldTest` (Dashboard hero uses display, title uses text; title centred and separated from the hero) |
| UI-4 | `PftThemeModeTest`, `WindowThemeTest`, `AppearanceSettingsTest` (UiModeManager), light/dark goldens |
| UI-5 | `ColorContrastTest`, `MoneyToneTest`, `MoneyTextTest` (sign and semantics), `StatusChipTest`, D4 `ProgressMeter` test |
| UI-6 | `PftSymbolTest`, `CategoryGlyphTest`, `CategoryPaletteContrastTest`, B4 `EmojiInputValidatorTest`, `ManifestHardeningTest` (EmojiCompat) |
| UI-7 | `CollapsingHeaderScaffoldTest` (content ≥ 35% down, snap, short-window), `BottomActionBarTest` (reach, 48dp) |
| UI-8 | `ThemeResolutionTest` (dynamic default, brand fallback, money independent of the accent). **Revised by DS6:** Graphite is the default, dynamic colour is opt-in and ignores the palette, and money is independent of both (premium-look plan §7) |
| UI-9 | Reviewer reuse check (M1a §8), the §3.7 inventory, `code-structure.md` table |
| UI-10 | `PftMotionTest`, `PressScaleTest`, `MoneyTextTest` (animated end state), `CollapsingHeaderScaffoldTest` (snap), manual release-build check |
| B-3 | D4 `ProgressMeter` test (hatched segment, labelled), `StatusChipTest` |
| SEC-2 | `WindowThemeTest` and `ManifestHardeningTest` (`FLAG_SECURE` unchanged) |
| SEC-3 | `ManifestHardeningTest` (SAF adds no permission) |
| SEC-5 | `SafeLogTest` additions for the `FontImportRejected` and `FontFallback` events (enum-only fields) |

## 8. Security considerations

- **No new permissions and no network.**
  - SAF `OpenDocument` needs no permission, and `ManifestHardeningTest` keeps proving the manifest requests none.
  - There are no downloadable fonts, and the EmojiCompat font-provider initializer is removed.
  - Roborazzi and `ui-tooling` are test/debug-only, so they are off the release runtime classpath that the OWASP scan gates.
- **Untrusted input: font files (AP2).** A font is parsed by native platform code (minikin/FreeType), which is real attack surface. Mitigations:
  - Import is user-initiated only.
  - The byte cap is enforced while streaming.
  - The magic number is checked.
  - The file is parsed with `Font.Builder` before use.
  - It is copied to a fixed private path; there is no name-derived path, so no traversal.
  - The URI grant is not persisted.
  - On failure it fails closed to the previous font.
  - The residual risk (a malicious but well-formed font) is accepted and recorded in the AP2 PR security review.
- **Untrusted input: emoji text (B4).** The validator bounds it to one grapheme. Core bounds the stored length and rejects control and whitespace characters. Unknown `icon_key`/`colour` values from future sync render a fallback and never throw.
- **Screen capture and privacy.**
  - `FLAG_SECURE` is untouched and is still set before `enableEdgeToEdge()` and content.
  - The catalogue is debug-only (`src/debug`). The release `DevContent` is an empty surface.
  - Goldens, previews and mockups use **fake data only**; the reviewer rejects real names, amounts or account numbers in PNGs.
- **Logs:** only enum-typed `SafeLog` events (`FontImportRejected(reason)`, `FontFallback(slot)`). File names, URIs and font names are never logged.
- **Licences:** OFL-1.1 (Newsreader) and Apache-2.0 (Material Symbols) notices ship in `assets/licenses/` and are viewable in-app (AP1). There are no copyleft-incompatible assets.
- **Dependencies:** pinned in `libs.versions.toml`, locked and checksum-verified (append-mode refresh per `build-logic/README.md`). Run `dependencyCheckAggregate` in each PR security step.
- **Integrity invariants** (CLAUDE.md §3.8) are untouched. `MoneyText` only formats through `:core:money`, so there is no float in money paths; spring and contrast maths use floats in UI-only code, outside the money modules.
- Run `/security-review` and `/review-changes` before every PR. AP2 especially needs it.

## 9. Documentation updates (all required surfaces)

| Surface | Created/updated in |
|---|---|
| KDoc on every public token, component and parameter (including "do not wrap M3" and two-consumer notes where relevant) | every PR |
| `app/README.md`: public interface (`ui/theme`, `ui/common` blocks), configuration (`ThemePrefs` defaults; `AppearanceLimits` in AP2), dependencies (Roborazzi, tooling), how to run (`recordRoborazziDebug`, `verifyRoborazziDebug`; goldens live in `src/test/screenshots`), manifest posture (EmojiCompat removed) | DS1–DS5, AP1, AP2 |
| **New** `docs/architecture/design-system.md`: principles (§3.1), theming architecture, token tables, motion conversion, one-hand rules, icon/emoji policy and the permanent-key rule, component inventory, "adding a component" and "adding an icon" how-tos, third-party assets table (source, licence, sha256, size) | DS2, extended DS3–DS5 |
| `docs/architecture/overview.md`: a "UI layer" paragraph linking design-system.md | DS1 |
| `docs/architecture/code-structure.md`: `ui/theme`, `ui/common`, `res/font`, `assets/licenses`, `src/test/screenshots`, the `src/debug` catalogue; shared-building-block rows for every DS component | DS1–DS5 |
| **New** `docs/features/appearance.md` (product: light/dark/system, dynamic colour, fonts, custom font import and its limits, licences) | AP1, AP2 |
| `docs/features/categories-and-tags.md`: the default category icons and colours, emoji rules | B3/B4 (contract from §4) |
| `docs/plans/2026-10-28-m1a-core-ledger.md`: amend §3.3 (Material 3 resolves to 1.3.1; the new deps; "use core icons" → curated Material Symbols), the §3.4 table (`MoneyText` moves to DS5; new rows), steps per §6.3, the PR table (DS rows), and §8 (`ForbiddenImport` for `Log`) | a docs commit in DS1, after this plan is approved |
| `docs/TECHNICAL.md:161`: "Material 3 1.3.0" → "1.3.1 (resolved by BOM 2024.10.01)" | DS1 |
| `docs/learnings/README.md`: the Roborazzi/native-graphics setup and the golden re-record rule; material3 1.3.1 via the BOM; EmojiCompat's provider fetch; detekt rules that need type resolution are silently inert | DS1, DS2, DS3, and each PR |
| Root `README.md`: the screenshot record/verify commands in "Test" | DS2 |
| `.github/pull_request_template.md`: one checkbox, "Goldens contain fake data only; visual diffs reviewed" | DS2 |
| graphify: `graphify update .` after code changes; `/graphify . --update` for the new docs (owner's call on token cost, TECHNICAL.md §2 rule 2) | each PR / DS5 |

## 10. Risks & mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| A dynamic accent that is itself green or red (from the wallpaper) reads as income or expense | money semantics blur | money tokens are never derived from the accent; accent only on chrome; every amount carries a sign or label; the later accent override; manual check with red and green wallpapers |
| Red/green colour blindness (positive/negative lightness ratio 1.04:1) | income and expense indistinguishable | signs, words and semantics on every money element (`MoneyTextTest`, `StatusChipTest`); hatched pattern for unverified spend |
| Robolectric native-graphics goldens differ across machines or OS versions | false red `check` | record on one machine; small Roborazzi change threshold [Verify value]; documented re-record procedure; behaviour tests, not pixels, carry correctness |
| Experimental Compose APIs (`ModalBottomSheet`, `SharedTransitionLayout`) change on upgrade | breakage spread across screens | each is opted into in exactly one file (§3.2) |
| Motion judged on a debug build (Compose debug builds are much slower) | wrong "janky" verdicts | judge motion only on a minified release-like build (§11); baseline profiles only if frame stats show jank |
| Custom font is malicious or crashes the native parser | app crash or exploit | §8 pipeline; user-initiated; fail closed; startup fallback if the file can't load |
| +~0.5 MB APK from the font | size | acceptable for sideload; subsetting is allowed (no Reserved Font Name) if it ever matters |
| One UI system font does not apply to `FontFamily.Default` | the owner's preferred font is ignored | [Verify] on the phone with the A-final release build; the custom-font import (AP2) is the fallback |
| A tall header hides rows on small or landscape screens | less content visible | 0.38 fraction capped at 360dp; collapsed-only under 560dp of height; snap |
| Design-system creep (wrappers around everything) | abstraction hell | "do not wrap M3 to restyle" rule; the two-consumer rule; the §3.7 inventory is the whole list; reviewer reuse check |
| Plan built against a base branch that lacks A1 (§2) | rebase churn | confirm A1 is merged into `feat/m1a-foundation` before DS1 (Q9) |
| Goldens or mockups accidentally containing real data | privacy leak in the repo | fake-data rule; PR template checkbox; reviewer check |

## 11. Rollout & verification

**Per DS PR:**
1. Run `./gradlew check`: unit, Robolectric and screenshot verification, lint, detekt, coverage.
2. When a visual change is intended, run `./gradlew :app:recordRoborazziDebug` and review every changed PNG in the PR diff.
3. Run `./gradlew :app:installDebug` on `pft35` (started with `-gpu host`) and walk the catalogue:
   - Dark mode: `adb shell cmd uimode night yes`, then `no`.
   - Font scale: `adb shell settings put system font_scale 2.0`, then `1.0`.
   - Dynamic colour: change the wallpaper (including a red and a green one), then confirm money colours stay fixed and the accent changes.
   - "Remove animations": `adb shell settings put global animator_duration_scale 0`, then confirm nothing breaks and states land correctly.
   - TalkBack: amounts are announced with their direction.

**Motion check (DS4, then C4/C5):**
1. Build a minified variant and install it on `pft35`. `assembleRelease` is now minified directly (R8 was enabled early on 2026-10-30; M1a H3 amendment), so no separate variant is needed. Its APK is unsigned: sign a copy with the debug keystore for a one-off local install (recipe in learnings, 2026-10-30), never in the build.
2. Exercise header collapse, list scroll and (from C4) FAB → add and row → detail.
3. Read `adb shell dumpsys gfxinfo dev.imagh.pft framestats`, aiming for under 1% janky frames on the emulator, and record the numbers in learnings.

The owner's phone gets only release-signed builds (PRD decision 31). The first on-phone look is the A-final release APK, which shows the real setup screen; the full feel comes at C-final. Until then the owner reacts to the HTML mockup and the PR goldens.

**Rollback:** every DS and AP PR is UI-only and revertable on its own. There is no schema change. AP settings keys that a rolled-back build doesn't know are simply ignored, because unknown keys read as defaults.

## 12. Open questions

**Resolved 2026-10-29.** The owner answered every question; the right-hand column is now the decision.

| # | Question | Decision |
|---|---|---|
| Q1 | Serif for dashboards and reports: **Newsreader** (OFL, tabular figures, ₹, variable optical size, 452 KB), or another? Fraunces has no tabular figures and Instrument Serif has no ₹ glyph, so both would mis-set amounts. | Newsreader, with extra letter and line spacing (§3.3 "Display spacing") |
| Q2 | Expense amounts in **list rows**: neutral text with a `−` sign (calmer and more premium; red is kept for totals, over-budget and negative balances), or red on every expense row? | Neutral in rows, red in totals |
| Q3 | Positive account balances: green as stated, or neutral, with only negative balances in red? (Cash and bank balances are almost always positive, so green everywhere is loud.) | **Neutral** positive balances; only negative balances are red |
| Q4 | When do appearance settings and custom-font import ship: their own small slice right after A-final (recommended), or folded into slice H? | Own slice after A-final |
| Q5 | Brand fallback accent (used when dynamic colour is off, and in goldens): ink indigo `#4A55B8`? | `#4A55B8`. **Superseded 2026-10-30 (DS6):** ink indigo is retired, and the curated Graphite palette is the default and the goldens' scheme |
| Q6 | Bottom navigation: **Home · Budgets · Upcoming · More**, with a bottom-right FAB for add (mockups). Accounts, categories, tags, loans, goals and settings live under More. | As proposed |
| Q7 | "Emoji for descriptions": category glyph on each row plus free emoji typed in titles (no schema change), or a per-transaction icon override (adds a column in C1)? | Category glyph + typed emoji |
| Q8 | Remove the EmojiCompat initializer, so emoji come from the system font only and nothing asks Play services for a font? | Remove |
| Q9 | The brief says A1 is merged, but `feat/m1a-foundation` is still at `8bde3b6` without A1. Merge the A1 PR into the base before DS1 branches? | Done: PR #9 merged into `feat/m1a-foundation` (2026-10-29) |
| Q10 | (Mockup review, 2026-10-29.) Serif titles, sans titles, or a mix? Where does the title sit relative to the serif hero? | Serif for the hero number only; **all titles sans**. The read-only title is centred in the upper band with clear space above the hero; the hero, caption and the ‹ › period stepper (plus a header swipe) sit low in the thumb zone (§3.4 "Header layout rule"). **Revisited 2026-10-30 (owner, DS6):** hero-only serif stays the **default** (`HERO_ONLY`). `EDITORIAL` (serif hero, large amount, display, headline and title roles) becomes a user option, with Newsreader's optical size set per role. All serif was evaluated in the mockup and deferred; it could return later as a third value without a migration. The header layout rule is unchanged. |
| Q11 | (Mockup review, 2026-10-29.) Budgets as one dashboard, or a list plus a detail page? | **List + detail.** The Budgets tab lists budgets (Standard header); a budget detail page shows everything about one budget (Dashboard header). Add stays in the FAB. M1a D2/D4 amended. |
