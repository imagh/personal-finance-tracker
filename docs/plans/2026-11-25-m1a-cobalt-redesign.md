# Plan: M1a Cobalt redesign — look, shared components, tab bar and the Home dashboard

| Field       | Value |
|-------------|-------|
| Date        | 2026-11-25 |
| Author      | architect |
| Status      | Approved (2026-11-25; every §12 default accepted) |
| Related ACs | PRD UI-2, UI-3, UI-5, UI-7, UI-11, UI-14, UI-17 (design-system requirements); L-7 and L-8 through the Transactions tab (slice C) |
| Plan owner  | akashg (repo owner) |

Visual reference: [`2026-11-25-home-redesign-mockup.html`](2026-11-25-home-redesign-mockup.html), the two "Proposed" phones (`.cobalt-light`, `.cobalt-dark`). Builds on the design-system plan ([`2026-10-29-m1a-design-system.md`](2026-10-29-m1a-design-system.md)) and the premium-look plan ([`2026-10-30-m1a-premium-look.md`](2026-10-30-m1a-premium-look.md)), whose decisions stand except where §3 reverses them. The slice C plan ([`2026-11-18-m1a-slice-c-transactions.md`](2026-11-18-m1a-slice-c-transactions.md)) carries an "Amendment 2026-11-25" that schedules the Transactions tab, the Home dashboard, filters, sort and search on top of this plan.

Section map for briefs: decisions are **§3 D1–D13**, steps are **§6 K1–K8**, the combined PR order is **§6 PR breakdown**.

---

## 1. Requirement

**Need.** The owner approved a new look ("Cobalt") and a new navigation model. Today the app is Graphite with a Newsreader serif hero, a collapsing header on every screen and no tab bar (Home → More → lists, `app/src/main/kotlin/dev/imagh/pft/app/ui/nav/PftNavHost.kt:27-29`). Cobalt brings a brand blue, Manrope and Bricolage Grotesque, a balance card, quick actions, day-grouped transaction rows and a floating pill tab bar.

**Done means:**
1. **Look.** Cobalt is the default palette in light and dark; Manrope is the default UI font and Bricolage Grotesque the default hero font; both are bundled and licensed. Graphite, Midnight, Ivory and Aubergine stay selectable (persisted names).
2. **Contrast.** Every new text/fill pair, including money on the blue balance card, is pinned by `ColorContrastTest` across `ThemePalette.entries` and the dynamic sweep.
3. **Money colour.** Expenses are the default text colour everywhere (rows, day totals, hero, banner); income stays `positive`; red is kept for negative balances and over-budget only (D5).
4. **Components.** `TransactionRow` + `DayGroup` (shared), `BalanceCard`, `QuickActionRow`, `GreetingHeader` and `PftTabBar` exist with fake-data samples, catalogue entries and goldens. Existing components are restyled through tokens (table in D3).
5. **Navigation.** A floating tab bar on tab roots, per-tab back stacks, hidden on Setup, forms and detail screens. On `main` after this slice: **Home · Accounts · More**; slice C C4a inserts **Transactions** (D8).
6. **Home.** The Home header becomes the greeting header in this slice; the dashboard body (balance card, quick actions, recent list) is wired with real data in slice C C4d (D9).
7. **No dead ends.** No control leads nowhere: destinations that do not exist yet are hidden (D6).
8. **Gates.** `./gradlew check` green; every golden that moved is re-recorded and reviewed; release APK measured before and after the fonts.

**Out of scope.**
- Transactions tab, filters, sort, search, the Home dashboard's data: slice C (amendment).
- Budgets tab and the budget meter on the balance card: slice D (D4).
- Insights tab: M3. Notifications/review inbox (the bell): M1b. Appearance settings screen (palette/font pickers): AP1 (unchanged).
- Hide-amounts toggle (the mockup's eye icon): not in the PRD; roadmap R-9 (proposed, §9).

## 2. Current state

**Theme (`app/src/main/kotlin/dev/imagh/pft/app/ui/theme/`).**
- `ThemePalette` (`ThemePalette.kt:12-24`) is persisted by name, additive only; `colors(dark)` is an exhaustive `when` (`:30-36`), so a new entry without colours does not compile.
- `PaletteColors(scheme, canvas, island, wash)` (`GraphitePalette.kt:24-30`); Graphite's KDoc (`:32-82`) is the provenance procedure to copy for Cobalt.
- `ThemePrefs` defaults: Graphite both modes, Figtree, `BUNDLED_SERIF`, `HERO_ONLY` (`ThemePrefs.kt:98-117`). `SansFont` (`:62-74`) and `DisplayFont` (`:22-31`) are persisted by name. `MainActivity.kt:33` calls `PftTheme {}` with the defaults (no stored prefs exist until AP1), and 60+ screenshot tests call `PftTheme(ThemePrefs(dynamicColor = false))`, so **flipping the defaults moves almost every golden**.
- `PftTheme` provides money, category palette, type and surfaces (`PftTheme.kt:107-112`); dynamic colour derives canvas/island/wash in `dynamicPaletteColors` (`:61-70`).
- Type: `sansFamily`/`sansWeights` pin 400/500/600 (`PftTypography.kt:77-98`); `displayFamily` (`:137-147`); `pftType` fixes the hero at 48sp weight 400 (`:154-190`); `HeroSizeSteps` start at 48sp (`:60-61`).
- `MoneyColors` are global, never per palette (`MoneyColors.kt:7-37`).
- Shapes: M3 large is 24dp, `glyph` is 30% corners (`PftShapes.kt:14-30`).

**Money display.** `moneyTone` makes an expense neutral in rows and red elsewhere (`ui/common/MoneyTone.kt:69-76`); `MoneyText(amount, semantic, modifier, size, animate)` (`ui/common/MoneyText.kt:85-95`) picks the colour from the tone.

**Tests that pin the look.** `ColorContrastTest` loops `ThemePalette.entries` (`ColorContrastTest.kt:29`) over surfaces, money, chips, islands, glow and accent (`:62-257`); `AccentHueTest` keeps every primary 40° of CIELAB hue from money red/green (`AccentHueTest.kt:20-35`); `GroupedSectionColorsTest.kt:80-81` asserts the island is never white; `FontBudgetTest.kt:42-48` pins exactly three fonts; `BundledFontTest` pins each font's size, SHA-256 and OFL text; `TabularFiguresTest` and `PftTypographyTest` pin `tnum` and families; `ThemeLiteralScanTest` forbids colour/font literals outside `ui/theme`.

**Navigation and Home.** `PftNavHost` (`ui/nav/PftNavHost.kt:51-95`): start is `SETUP` or `HOME` (`:60`), Setup pops itself (`:71`), More is a push from Home's header (`:78`), Accounts/Categories/Tags are pushes from More (`:85-87`). `HomeScreen` is a placeholder on `CollapsingHeaderScaffold` with a "<name> · <phone>" subtitle (`ui/home/HomeScreen.kt:31-55`); `HomeViewModel` reads `IdentityService.requireIdentity()` (`ui/home/HomeViewModel.kt:36-56`). `MoreScreen` has Back and an Accounts row (`ui/more/MoreScreen.kt:40-54`); `EntityListScreen` always draws Back (`ui/common/EntityListScreen.kt:83,96`).

**Symbols.** `PftSymbol` already has HOME, ADD, TRANSFER, RECEIPT_LONG, ACCOUNT_BALANCE_WALLET, SEARCH, FILTER, SORT, SETTINGS, OVERFLOW, CLOSE, VISIBILITY. Missing: `more_horiz` (More tab), `south_west`/`north_east` (In/Out, Income quick action). Import via `tools/material-symbols` only (digest "Compose UI").

**Computed contrast (WCAG, from the mockup values; script results, 2026-11-25).**

| Pair | Ratio | Verdict |
|---|---|---|
| light `#7A8399` (secondary) on canvas `#F5F7FB` / white | 3.54 / 3.79 | **fails 4.5** → D1 tone adjust |
| light `#687187` on canvas / white | 4.55 / 4.88 | passes |
| white on card lightest stop `#3463FF` (light) / `#2D5BFF` (dark) | 4.80 / 5.18 | passes |
| decimals at 55% white on `#3463FF` | 2.51 | **fails even 3.0** → D4 |
| card caption 78%, meter label 80%, In/Out 92% white on `#3463FF` | 3.55 / 3.65 / 4.33 | **fail 4.5** → D4 |
| "2 people" tag (white on 18% white over card) | 3.56 | fails → dropped (D6) |
| dark `#2251FF` on canvas / island / nav `#181D2B` | 3.39 / 3.05 / 2.95 | text fails; nav fails 3.0 → D1 |
| light `#2251FF` on canvas / white; white on `#2251FF` | 5.31 / 5.69; 5.69 | passes |
| dark `#9DB1FF` on canvas / `#1D2647` | 9.33 / 7.16 | passes |

## 3. Design & decision

**D1 — The Cobalt palette (owner decision 2026-11-25; tone adjustments are the architect's, owner to eyeball).**
- New `ThemePalette.COBALT` and `CobaltPalette.kt`, generated with Graphite's procedure (`GraphitePalette.kt:37-71`) from the mockup hues, then pinned to the mockup values:

  | Mockup token | Role | Light | Dark |
  |---|---|---|---|
  | `canvas` | `canvas` | #F5F7FB | #0B0E17 |
  | `island` | `island` | #FFFFFF (D10) | #151A27 |
  | `on` | `onSurface`, `onBackground` | #0E1B3D | #E8EBF4 |
  | `onvar` | `onSurfaceVariant` | **#687187** (mockup #7A8399 fails 4.5:1) | #8E97AD |
  | `line` | `outlineVariant` | #E9ECF3 | #222838 |
  | `primary` / `icon` | `primary` | #2251FF | **#9DB1FF** (the mockup `--icon`; #2251FF fails as text on dark) |
  | `primary-soft` | `primaryContainer`, `wash` | #E7EDFF | #1D2647 |
- **"Blue never for text".** App-drawn text (titles, body, amounts, captions) never uses `primary` or `brand`. M3 draws `primary` as text in a few controls (TextButton labels, a focused field's label); in light that is #2251FF at 5.31:1, in dark #9DB1FF; we accept that as a control-label exception rather than fight M3 (§12 Q1).
- **Brand tokens.** M3 has no role for "filled brand", the card gradient or the floating bar. Add one `@Immutable data class BrandColors(brand, onBrand, brandSoft, onBrandSoft, cardStart, cardMid, cardEnd, onCard, cardGhost, cardGhost2, nav)` as a field of `PaletteColors`, provided as `PftTheme.brand` (same pattern as `PftTheme.surfaces`). Cobalt pins the mockup: light `brand #2251FF`, `onBrand #FFFFFF`, `brandSoft #E7EDFF`, `onBrandSoft #2251FF`, card `#3463FF → #2251FF → #1B44E0`, `onCard #FFFFFF`, ghosts `#C7D4FF`/`#E2E9FF`, `nav #FFFFFF`; dark `brand #2251FF`, `onBrand #FFFFFF`, `brandSoft #1D2647`, `onBrandSoft #9DB1FF`, card `#2D5BFF → #2251FF → #1A3FCC`, ghosts `#22306A`/`#182148`, **`nav #151A27`** (mockup #181D2B gives the active pill 2.95:1; the island tone gives 3.05:1).
- The four existing palettes and dynamic colour get **derived** brand tokens from one pure `derivedBrand(scheme, island)`: `brand = primary`, `onBrand = onPrimary`, `brandSoft = primaryContainer`, `onBrandSoft = onPrimaryContainer`, card = a flat `primary` (three equal stops) with `onCard = onPrimary`, ghosts = `primaryContainer` and `outlineVariant`, `nav = island`. No new hand-picked values for them.
- **Money tokens unchanged.** The mockup's `--positive #13803F`/`#5CD68C` and `--warn #9A5B00` are not adopted: `MoneyColors` are global by design (`MoneyColors.kt:7-10`) and the existing values pass on every Cobalt surface (positive 6.63:1 on the light canvas, 10.0:1 on the dark island).
- `AccentHueTest` covers Cobalt automatically (it loops entries); #2251FF and #9DB1FF sit near 290–300° CIELAB hue, far from money red (~40°) and green (~140°) — the test confirms it.

**D2 — Fonts (owner decision).**
- Bundle **Manrope** (variable `wght`) as `res/font/manrope.ttf` and **Bricolage Grotesque** (variable `opsz,wdth,wght`) as `res/font/bricolage_grotesque.ttf`, unmodified, fetched from the pinned google/fonts commit into an empty scratch directory (digest "Fonts"). OFL texts in `assets/licenses/manrope-OFL.txt` and `bricolage-grotesque-OFL.txt`.
- New persisted entries (additive): `SansFont.MANROPE`, `DisplayFont.BUNDLED_GROTESQUE`. Manrope uses `sansWeights` (400/500/600); nothing in Cobalt needs 700 (the avatar initials use 600).
- The hero for `BUNDLED_GROTESQUE` is Bricolage at **weight 700, 34sp** (mockup), `opsz` pinned to the size, `wdth` 100, line height 1.25x, `tnum, lnum`. `pftType` takes the hero size and weight from the display font; `HeroSizeSteps` become "the steps at or below the hero's own size" so a 34sp hero shrinks 34 → 28 → … → 12sp.
- Newsreader stays bundled: `BUNDLED_SERIF` is a persisted option. It is no longer the default.
- **[Verify] in K2 Red, from the files:** both fonts have a `tnum` GSUB feature, and both have glyphs for `₹` (U+20B9) and `−` (U+2212) (`Paint.hasGlyph` under `@GraphicsMode(NATIVE)`). If Bricolage lacks `tnum` or `₹`, stop and ask the owner (fallback: hero in Manrope 700, adding 700 to `sansWeights`). Record sizes; the budget stays 3 MiB (today 690,664 B). A file over the 500 KB pre-commit limit needs the path-scoped exception backed by `BundledFontTest`'s size and hash (`FontBudgetTest.kt:14-20`).

**D3 — Components: restyle or replace (decided by the effort to match the mockup).**

| Component | Decision | What changes |
|---|---|---|
| `PftShapes` | restyle | add `card` 22dp, `island` 18dp, `tabBar` full pill, `quickAction` 17dp; `glyph` (30%) already gives the mockup's 12dp on a 40dp tile |
| `PftSpacing` | keep | the 4dp grid covers the mockup (18dp gutters map to `gutter` 20dp) |
| `PftSurfaces` | keep | Cobalt flows through `canvas`/`island`/`islandEdge` |
| `GroupedSection` | restyle | radius → `PftShapes.island` (18dp); colours via tokens; the "never white" test becomes "never white except Cobalt light" (D10) |
| `EntityRow` / `EntityListScreen` | restyle + small API | tokens only; `onBack` becomes nullable so a tab root draws no Back; FAB clears the tab bar |
| `CategoryGlyph` | keep | already the tinted soft-square tile; `CategoryPalette` is persisted by key and stays |
| `StatusChip` | keep | already the pill on money containers; the mockup "Needs review" pill matches `MoneyTone.Warning` |
| `HeaderIconButton` | keep | still the only header action (UI-14); the greeting header's bell/gear are hidden (D6), so no bordered variant is needed |
| `CollapsingHeaderScaffold` | keep | every screen except Home keeps it (Transactions tab uses `Standard` with Search and overflow); Home leaves it (D9) |
| `BottomActionBar` | keep | forms only; restyled through tokens |
| `MoneyText` | extend | `Net` semantic, `fadeDecimals`, `onColor` for the card (D4, D5) |
| `TransactionRow` + `DayGroup` | **new, shared** (`ui/common`) | tile, title, "category · account" meta, amount, optional `StatusChip`, tag/account mini-chips; `DayGroup` = day header (date, optional total) + island. Consumers: Home recent (C4d), Transactions tab (C4a), search results (C5b), budget detail (D4), loan detail (F3) |
| `BalanceCard`, `QuickActionRow`, `GreetingHeader` | **new, local** (`ui/home`) | one consumer (Home); stay local until a second appears |
| `PftTabBar` | **new** (`ui/nav`) | floating pill bar (D8) |
| `MonthStrip`, `TotalsBanner`, `FilterSheet`, `SortSheet` | new, local (`ui/transactions`) | slice C amendment |

**D4 — Money on the blue card (reverses premium-look O2, owner decision).**
- O2 said money is never drawn on a tinted area (`2026-10-30-m1a-premium-look.md:26`). The balance card draws the month's spend in `onCard` (white) on the brand gradient. The header-glow rule itself is unchanged.
- **Contrast rules for the card** (pinned in `ColorContrastTest` for every palette, both modes, against the **lightest** gradient stop):
  - all card text is solid `onCard` at ≥ 4.5:1 — no alpha-faded captions, labels or In/Out (the mockup's 78–92% fails); hierarchy comes from size and weight;
  - faded decimals are `onCard` at **alpha 0.68** (3.06:1 on `#3463FF`, WCAG large text 3:1) and fade **only while the hero is at a large-text step** (≥ 24sp; below that they are solid);
  - the budget meter fill vs. its track ≥ 3:1 (non-text, D4 of slice D wires it).
- `MoneyText` gains `onColor: Color = Color.Unspecified` ("draw on a fill the theme sweeps separately"): allowed only with a `Neutral` tone (throws otherwise, so no red/green on blue); and `fadeDecimals: Boolean = false` (hero only). Spoken text is unchanged.
- The card's meaning is never colour-only: "Spent this month", "In", "Out" are words.

**D5 — Money colours (owner decision; what changes).**
- **Before:** an expense is neutral in rows, red in totals and heroes (`MoneyTone.kt:76`; digest "Colour meaning"; design-system plan §3.7).
- **After:** `MoneySemantic.Expense → Neutral` everywhere. Income stays `Positive`. Red (`Negative`) is kept for a **negative account balance** and **over budget**. Amber for a budget at threshold.
- **New `MoneySemantic.Net`**: a signed figure (`+`/`−` sign, may be negative) that is always `Neutral` — day totals and the banner's net. `Balance` keeps its red-when-negative rule.
- `moneyTone`'s `inRow` parameter no longer changes any outcome; remove it in K3 (one caller, `MoneyText.kt:95`).
- `MoneyColors` values, `AccentHueTest` and the money rows of `ColorContrastTest` are unchanged; `MoneyToneTest` and the KDocs of `MoneySemantic.Expense`, `MoneyTone.Negative` and `MoneySize` change; the digest line "Colour meaning" is rewritten in K3.

**D6 — Not-yet-built destinations are hidden, not disabled.** A disabled control is a dead end with no explanation; the codebase rule is "`add` is nullable, never a dead control" (digest "Lists"). Each hidden item appears in the PR that builds its destination:

| Mockup element | M1a until | Appears in |
|---|---|---|
| Quick action Expense / Income / Transfer | Home dashboard lands after the add form | slice C C4d (with C4b's `type` preset) |
| Quick action Budgets | hidden; the row shows three buttons | slice D D4 |
| Budget meter and "x% of budget · n days left" | hidden | slice D D4 |
| Tab Transactions | not in the bar on `main` | slice C C4a (inserted at position 2) |
| Tab Insights | not in M1a | M3 |
| Bell with badge | hidden | M1b (review inbox for unverified captures) |
| Gear | dropped: settings live in the More tab | — |
| Eye (hide amounts) | dropped | roadmap R-9 if the owner wants it |
| "2 people" pill | dropped (fails contrast; one user per phone until R-1/M1c) | — |
| Day totals on Home's recent list | dropped: Home shows the latest 5 entries, so a day may be partial and its total would misreport the day | Day totals stay on the Transactions tab, where days are complete |

**D7 — Cobalt is the new default; the other palettes stay (owner answer 1).** `ThemePrefs.Default` becomes `lightPalette = darkPalette = COBALT`, `sansFont = MANROPE`, `displayFont = BUNDLED_GROTESQUE`, `typePairing = HERO_ONLY`, `background = TONAL`. Graphite/Midnight/Ivory/Aubergine, Figtree/Plus Jakarta Sans and Newsreader stay as persisted options for AP1. The flip is its own PR (K4) so the pixel change is reviewed apart from the code (digest "Git": pixels-neutral first, then flip).

**D8 — Tab bar and navigation (owner answer 2; reverses slice C Q2/D7).**
- **Tab set.** M1a end state: **Home · Transactions · Budgets · Accounts · More** (five, the M3 maximum). Order is fixed now so later slices insert, never move: Transactions arrives at position 2 in C4a, Budgets at position 3 in D4. On `main` after this slice: Home · Accounts · More. Upcoming (E4) lives inside Home/Transactions, not as a tab (decided in E's plan). Insights (M3) will need a slot: the expected move is More → behind the Home avatar; decided in M3, recorded here so nobody adds a sixth tab.
- **More** holds Categories, Tags and (later) settings, appearance and diagnostics. It loses its Back button and the Accounts row; its header subtitle shows "<name> · <phone>" (the attribution line Home loses).
- **Graph.** Root `NavHost(start = if (hasIdentity) MAIN else SETUP)`; `MAIN` is a nested graph whose start is the Home tab graph; each tab is a nested graph (`home_tab`, `accounts_tab`, `more_tab`, later `transactions_tab`, `budgets_tab`) holding its root and its own forms (`ACCOUNT_EDIT` in accounts, `CATEGORIES`/`TAGS` and their forms in more). Screens reached from more than one tab (transaction edit/detail, search; slice C) are direct children of `MAIN`. String routes stay (Navigation 2.8.9, digest "Lists and navigation").
- **Behaviour.**
  - Tap a tab: `navigate(tab.graph) { popUpTo(MAIN start) { saveState = true }; launchSingleTop = true; restoreState = true }` — each tab keeps its own stack and list state.
  - Tap the active tab: pop to its root.
  - Back on a non-Home tab root goes to Home; Back on Home leaves the app (as today).
  - Setup → `navigate(MAIN) { popUpTo(SETUP) { inclusive = true } }`; Back after Setup leaves the app, never returns to Setup (`LaunchRoutingTest`, `SetupFlowTest`).
  - The bar shows only when the current destination is a tab root; it is hidden on Setup, forms, detail and search. `DataUnavailableScreen` is outside the host (`PftRoot`) and unaffected.
  - The "no longer exists" flags across pops (`savedStateHandle`) are unchanged.
  - No deep links (unchanged).
- **The bar.** `PftTabBar(tabs, selected, onSelect)`: a floating pill (62dp, `PftShapes.tabBar`, inset by the gutter and the navigation-bar inset, fill `brand.nav`, a soft shadow), with a canvas fade behind it. The active tab is a `brand` pill with icon + label in `onBrand`; inactive tabs are icon-only in `onSurfaceVariant`. Every item is `Role.Tab` with `selected` semantics and its label as the spoken name, inside `selectableGroup`, with a ≥ 48dp target. Tab-root lists add bottom padding equal to the bar's height (read from a `LocalTabBarClearance` set by the host) so the last row and the FAB clear it.

**D9 — Home becomes a dashboard (owner answer 4).**
- Home leaves `CollapsingHeaderScaffold` for a plain `LazyColumn`: `GreetingHeader` (avatar initials, "Good morning/afternoon/evening" by the local hour, the user's name; the avatar is decorative), `BalanceCard`, `QuickActionRow`, a "Recent transactions" section (latest 5 entries in `DayGroup`s without day totals, D6) with "See all" switching to the Transactions tab.
- The debug catalogue action stays in the header (debug only, `DevHeaderActions`).
- This slice ships the header (K8) and the card/actions as catalogue-rendered components (K6); slice C C4d wires the data (`TransactionQueries.month` totals and `recent(5)`).
- `HeaderStyle.Dashboard` and `HeaderPeriod` stay in the codebase for the budget detail (slice D); the month stepping moves to the Transactions tab's month strip.

**D10 — White islands in Cobalt light (reverses the "never white cards" rule for Cobalt only).** The approved mockup has white islands on `#F5F7FB`. `PftSurfaces.island` KDoc (`PftSurfaces.kt:27-28`), `GroupedSection` KDoc and `GroupedSectionColorsTest.kt:80-81` change to "never white, except Cobalt light, where the owner approved it (2026-11-25)". Other palettes keep the rule. §12 Q2 asks the owner to confirm.

**D11 — Sequencing (owner answer 5: confirmed with one correction).** C1–C3 are core/data and unaffected, so they proceed in parallel. Cobalt (K1–K8) lands on `main` before C4a so `TransactionRow`, the tab bar and Home are built once. Correction: slice C needs `main` merged into `feat/m1a-transactions` after K-final (a T0 merge PR, "C-sync") before C4a, and C3 gains a small **C3c** (sort, person filter, `recent`, month span) for the Transactions tab and Home (slice C amendment).

**D12 — Abstraction check.** No new interface. New data: `BrandColors` (tokens, like `PftSurfaces`). New shared block: `TransactionRow`/`DayGroup` (five named consumers). Everything else is local to its screen. `derivedBrand` is a pure function, not a strategy. Rejected: a theme-provider interface, a generic "card" component, a navigation abstraction over Navigation-Compose.

**D13 — Goldens.** Per PR, in §6. Rule: let `check` fail, read each `_compare.png`, confirm the diff is exactly the intended change, then re-record only those (digest "Goldens"). K4 moves nearly every golden; it carries no code change besides the defaults and the shape tokens, so the review is visual only.

## 4. Data model & schema

No schema change. No new settings key (prefs are still the compile-time `ThemePrefs.Default` until AP1). Persisted enum names added, never renamed: `ThemePalette.COBALT`, `SansFont.MANROPE`, `DisplayFont.BUNDLED_GROTESQUE`.

## 5. Flows

1. **Theme.** `MainActivity` → `PftTheme(ThemePrefs.Default)` → `resolvePaletteColors` → `CobaltPalette.light|dark` (or `derivedBrand` for the others/dynamic) → `LocalPftBrand`, `LocalPftSurfaces`, `MaterialTheme` → components read `PftTheme.brand`/`surfaces`/`type`.
2. **Launch.** `PftRoot` → `PftNavHost(hasIdentity)` → `SETUP` (fresh) or `MAIN/home_tab/HOME`.
3. **Tab switch.** `PftTabBar.onSelect(tab)` → `navController.navigate(tab.graph)` with save/restore → the tab's saved stack reappears; the bar hides when a form is pushed and returns on pop.
4. **Home (after C4d).** `HomeRoute` → `HomeViewModel` → `IdentityService.requireIdentity()` + `TransactionQueries.month(thisMonth, zone, none)` totals + `recent(5)` → `HomeUiState` → `GreetingHeader`, `BalanceCard`, `QuickActionRow`, `DayGroup`s. Quick action → `TRANSACTION_EDIT?type=…`; See all → Transactions tab.

## 6. Implementation steps (TDD-ordered)

Each step is one PR into `feat/m1a-cobalt` unless noted. Run tests in the background with `tools/agent/check-retry.sh` (digest "Build").

### Step K1 — Cobalt palette and brand tokens. Uses D1, D10
- **Red:**
  - `ThemePaletteTest`: `COBALT` resolves light/dark to the pinned table values (D1); every entry has `BrandColors`.
  - `ColorContrastTest` (extend, loops entries + dynamic sweep): `onBrand` on `brand` ≥ 4.5; `onCard` on each card stop ≥ 4.5; faded `onCard` (alpha 0.68) on the lightest stop ≥ 3.0; `brand` on `nav`, canvas and island ≥ 3.0; `onBrandSoft` on `brandSoft` ≥ 3.0 (icon); `onSurfaceVariant` on `nav` ≥ 4.5. Prove it bites: set Cobalt light `onSurfaceVariant` back to #7A8399 and dark `nav` to #181D2B and watch both fail.
  - `DerivedBrandTest`: `derivedBrand` maps the scheme roles as D1 says.
  - `GroupedSectionColorsTest`: "never white except Cobalt light" (positive control: Graphite light still not white).
- **Green:** `ThemePalette.COBALT` (+ `colors()` branch, `displayName` "Cobalt"), `CobaltPalette.kt` with the provenance KDoc, `BrandColors` in `PaletteColors`, `derivedBrand` for the four palettes and `dynamicPaletteColors`, `LocalPftBrand` + `PftTheme.brand`, `PaletteSwatches` shows Cobalt.
- **Refactor:** `design-system.md` "Colour" (Cobalt table, brand tokens, the two tone adjustments); `appearance.md` "What it does for you" (a fifth palette).
- **Goldens:** `palettes-light.png`, `palettes-dark.png` only.

### Step K2 — Manrope and Bricolage Grotesque, as options. Uses D2
- **Red:**
  - `BundledFontTest`: each new font loads, matches its pinned size and SHA-256, and its OFL text and copyright ship in `assets/licenses/` (Bricolage: check whether the OFL declares a Reserved Font Name and assert it).
  - `FontBudgetTest`: five fonts, names pinned, total ≤ 3 MiB.
  - `TabularFiguresTest` (`@GraphicsMode(NATIVE)`): Manrope and Bricolage amounts of the same shape measure equal under `tnum`, with a no-`tnum` negative control; `₹` and `−` have glyphs ([Verify], D2).
  - `PftTypographyTest`: `SansFont.MANROPE` puts every sans role in Manrope at 400/500/600 pinned on `wght`; `BUNDLED_GROTESQUE` makes the hero Bricolage at 700, 34sp, `opsz` 34, `tnum, lnum`; the hero steps start at 34sp; defaults are unchanged (Graphite/Figtree/Newsreader).
- **Green:** fonts + licences, `SansFont.MANROPE`, `DisplayFont.BUNDLED_GROTESQUE`, `bricolage(weight, size)` beside `newsreader`, hero size/weight per display font, steps filtered to ≤ the hero size.
- **Refactor:** `design-system.md` "Typography" and "Third-party assets" (provenance: pinned commit, sizes, hashes); `appearance.md` (two new choices).
- **Goldens:** none (`git diff --stat app/src/test/screenshots` empty proves it). Measure `assembleRelease` per-entry sizes before/after (digest "APK size").

### Step K3 — Money colour and the card's money. Uses D4, D5
- **Red:**
  - `MoneyToneTest`: Expense is Neutral at every size; `Net` is Neutral for negative, zero and positive; Balance negative is still Negative; Budget OVER is still Negative.
  - `MoneyTextTest`: `Net` signs (`+`, U+2212, none at zero) and speaks "Net, minus ₹…"; `onColor` with a non-Neutral tone throws; `fadeDecimals` draws the decimals at 0.68 alpha at 34sp and solid at 20sp (`TextLayoutResult` span colours), and TalkBack text is unchanged.
- **Green:** `MoneySemantic.Net`, `moneyTone` without `inRow`, `moneySign` allows negative `Net`, `MoneyText(onColor, fadeDecimals)`.
- **Refactor:** KDocs named in D5; `design-system.md` "MoneyText"; digest "Colour meaning" line; `docs/features/appearance.md` "How the accent is kept from looking like a status".
- **Goldens:** `money-text-*`, `token-sheet-money-*`, any `header-dashboard-*` whose sample hero is an expense — confirm each diff is only red → neutral.

### Step K4 — Make Cobalt the default look. Uses D7, D3 (shapes)
- **Red:** `PftThemeModeTest`/`ThemeResolutionTest`: `ThemePrefs.Default` is Cobalt/Manrope/Bricolage/HERO_ONLY/TONAL; an unknown stored name still reads as the default (existing tests, new expected values). `PftShapesTest`: `card` 22dp, `island` 18dp, `quickAction` 17dp, `tabBar` 50%. `GroupedSectionTest`: the island clips to `PftShapes.island`.
- **Green:** the defaults and shape tokens; `GroupedSection` radius.
- **Refactor:** `appearance.md` "Defaults"; `design-system.md` "Theming", "Shapes"; `app/README.md` `ui/theme`.
- **Goldens:** re-record all moved PNGs (expect ~100). Split commits by what moves: defaults first, shapes second (digest "Goldens").

### Step K5 — `TransactionRow` and `DayGroup` (shared). Uses D3, D5
- **Red:** `TransactionRowTest`: title, "category · account" meta, amount via `MoneyText` (Expense neutral, Income positive, Transfer unsigned); the tile is decorative (`CategoryGlyph(name = null)`); an optional `StatusChip` ("Needs review"); tag and account mini-chips are text, not colour; the whole row is one clickable node whose spoken text reads title, meta, amount, chip; ≥ 48dp target; 200% font scale wraps the title, never clips the amount (`isShownInFull`). `DayGroupTest`: header "Today"/"Yesterday"/date; total shown only when given (`Net`); rows in one island with hairline dividers.
- **Green:** `ui/common/TransactionRow.kt` (plain data `TransactionRowModel`, no entity), `ui/common/DayGroup.kt`, samples, catalogue entry.
- **Refactor:** `app/README.md` `ui/common`; `design-system.md` "Content primitives".
- **Goldens:** new `transaction-row-{light,dark}`, `day-group-{light,dark}`.

### Step K6 — `BalanceCard` and `QuickActionRow` (Home-local). Uses D4, D6, D9
- **Red:** `BalanceCardTest`: hero is the month spend via `MoneyText(Expense, Hero, onColor = onCard, fadeDecimals = true)`; "In"/"Out" rows are words + amounts; no meter when no budget is given; ghosts are decorative (no semantics); `Paise.MAX_AMOUNT` at 200% on a 320dp window fits (`isShownInFull`). `QuickActionRowTest`: renders exactly the actions given (three without Budgets), each a button labelled by its text with a ≥ 48dp target, the primary one filled with `brand`.
- **Green:** `ui/home/BalanceCard.kt`, `ui/home/QuickActionRow.kt`, samples, catalogue entries; `south_west`/`north_east` symbols via `tools/material-symbols` (`PftSymbolCategoriesTest` lists interface icons).
- **Goldens:** new `balance-card-{light,dark}`, `quick-actions-{light,dark}`.

### Step K7 — Tab bar and the navigation graph. Uses D8
- **Red:**
  - `PftTabBarTest`: one `Role.Tab` per tab with its label, the active one `selected` and showing its label, inactive icon-only but spoken; targets ≥ 48dp; fits at 200% on 320dp.
  - `ui/nav/TabNavigationFlowTest` (real in-memory Room, through `PftNavHost`): the bar shows on Home/Accounts/More and hides on the account form and Setup; Accounts → open form → switch to More → back to Accounts restores the form; re-tapping Accounts pops to its root; Back on More goes to Home; Back on Home finishes the activity.
  - Update `LaunchRoutingTest`, `SetupFlowTest` (Setup → Home, Back leaves the app, Setup never returns), `AccountsFlowTest`, `CategoriesFlowTest`, `TagsFlowTest` (entry via tabs/More; "no longer exists" still crosses the pop).
  - `EntityListScreenTest`: no Back when `onBack` is null; the FAB sits above the bar's clearance.
- **Green:** `ui/nav/PftTabBar.kt`, `ui/nav/Tabs.kt` (tab order constant), nested graphs in `PftNavHost`, `Routes` graph names, `MoreScreen` without Back/Accounts and with the attribution subtitle, `EntityListScreen(onBack: (() -> Unit)?)`, `more_horiz` symbol.
- **Refactor:** `PftNavHost` KDoc rewritten (tabs, back, setup); `app/README.md` "`ui/nav` and `ui/home`", "`ui/more` and `ui/accounts`"; `docs/architecture/overview.md` (navigation model); `docs/features/first-run-and-profiles.md` (where the attribution line now shows).
- **Goldens:** new `tab-bar-{light,dark}`; `entity-list-*` only if the Back button is in the sample.

### Step K8 — Home greeting header. Uses D9, D6
- **Red:** `GreetingTest` (pure): 05:00–11:59 morning, 12:00–16:59 afternoon, else evening, from an injected clock and zone. `InitialsTest` (pure): "Akash Gupta" → "AG", one word → one letter, Devanagari and emoji names take whole grapheme clusters (`BreakIterator`), blank → none. `HomeScreenTest`: the greeting and name show once loaded, nothing flashes before; no bell or gear; the debug action is present in debug and absent in release (`src/testRelease`).
- **Green:** `ui/home/GreetingHeader.kt`, `HomeScreen` on a `LazyColumn` with the header and the existing placeholder line, `HomeUiState` without `deviceLabel` (moved to More in K7).
- **Refactor:** `app/README.md` `ui/home`; `docs/features/first-run-and-profiles.md`.
- **Goldens:** new `home-header-{light,dark}`.

### Step K-final — Integrate
`feat/m1a-cobalt` → `main`: emulator walk-through of light and dark against the mockup (screenshots via `uiautomator dump` since `FLAG_SECURE`), APK size note, `graphify update .`, learnings. Then "C-sync": merge `main` into `feat/m1a-transactions` (T0).

### PR breakdown (incremental — required)

**Branches.** K0 (this plan + the slice C amendment) is a docs branch → `main`. Then base `feat/m1a-cobalt` from `main`; dev branches `feat/m1a-cobalt-<slug>`; K-final merges the base → `main`. Slice C continues on `feat/m1a-transactions` in parallel and takes `main` in C-sync.

| PR | Scope (steps) | Target | Depends on | Tier (CLAUDE.md §8) | Why it's independently reviewable |
|----|---------------|--------|------------|------|-----|
| K0 | this plan, slice C amendment, PRD §12 decisions 37–38 and roadmap R-7..R-9, learnings | `main` | — | T0 docs | the decisions to review |
| K1 | Cobalt palette, brand tokens, contrast tests | `feat/m1a-cobalt` | K0 | T2 tokens + accessibility thresholds | tokens only, 2 goldens |
| K2 | Manrope + Bricolage as options | base | K0 (parallel to K1) | T2 third-party assets, provenance, font facts to verify | pixels-neutral |
| K3 | money tone, `Net`, card money | base | K1 | T2 money display contract | one rule change, few goldens |
| K4 | default flip + shape tokens | base | K1, K2, K3 | T1 mechanical, visual review | pixels only |
| K5 | `TransactionRow`, `DayGroup` | base | K4 | T2 new shared component, a11y | one component |
| K6 | `BalanceCard`, `QuickActionRow` | base | K4 | T2 money on a tinted fill | two local components |
| K7 | tab bar, nested graphs, More/Accounts roots | base | K4 | T2 (`high`) navigation and back behaviour across every flow | one model change |
| K8 | greeting header | base | K7 | T1 follows the header pattern | one header |
| K-final | `feat/m1a-cobalt` → `main` | `main` | K1–K8 | T0 merge | one look |

**Combined order with slice C** (C ids per the slice C amendment):
1. K0 → `main`.
2. In parallel: slice C core C1d → C2 → C3a → C3c → C3b on `feat/m1a-transactions`; Cobalt K1 ‖ K2 → K3 → K4 → K5 ‖ K6 → K7 → K8 → K-final on `feat/m1a-cobalt`.
3. C-sync (`main` → `feat/m1a-transactions`, T0).
4. C4a Transactions tab → C4b add/edit + presets → C4d Home dashboard + account balances → C4c items → C5a detail → C5b search → C5c filters → C5d sort → C6 polish → C-final.

## 7. Test plan

- **Unit (JVM, no Android):** `DerivedBrandTest`, `MoneyToneTest`, `GreetingTest`, `InitialsTest`, `ThemePaletteTest`, `AccentHueTest` (unchanged, now covers Cobalt).
- **Robolectric/Compose:** `ColorContrastTest` (new card, brand, nav pairs; bites proved by mutating two values), `BundledFontTest`, `FontBudgetTest`, `TabularFiguresTest` (native graphics), `PftTypographyTest`, `MoneyTextTest`, `TransactionRowTest`, `DayGroupTest`, `BalanceCardTest`, `QuickActionRowTest`, `PftTabBarTest`, `EntityListScreenTest`, `HomeScreenTest`.
- **Integration (real in-memory Room through `PftNavHost`):** `TabNavigationFlowTest`, updated `LaunchRoutingTest`, `SetupFlowTest`, `AccountsFlowTest`, `CategoriesFlowTest`, `TagsFlowTest`.
- **Edge cases:** `Paise.MAX_AMOUNT` on the card at 200% on 320dp; decimals solid below 24sp; empty name initials; tab bar at 200% font; Back at every tab root; Setup never reachable after completion; unknown persisted enum names read as the default.
- **AC mapping:** Done-means 1 → K1/K2/K4 tests; 2 → `ColorContrastTest`; 3 → `MoneyToneTest`/`MoneyTextTest`; 4 → K5/K6 tests + goldens; 5 → `TabNavigationFlowTest`; 6 → `HomeScreenTest`; 7 → `QuickActionRowTest` (no Budgets), `HomeScreenTest` (no bell/gear); 8 → `check` + APK note.

## 8. Security considerations

- **No network fonts.** Fonts are bundled; the mockup's Google Fonts link is for the browser only. Fetch from the pinned google/fonts commit into an empty scratch directory; record SHA-256 in `BundledFontTest` and `design-system.md`; licences ship in `assets/licenses/`.
- **No new dependency, permission, exported component or scope.** The manifest is untouched (`ManifestHardeningTest` unchanged).
- **Logs.** No new log event. The greeting shows the user's own name on screen only, never logged.
- **Navigation.** No deep links are added; nav arguments remain untrusted ids validated in ViewModels (unchanged).
- **`FLAG_SECURE`/app lock** behaviour unchanged; the tab bar lives inside the same activity.

## 9. Documentation updates (all required surfaces)

| PR | Surfaces |
|---|---|
| K0 | this plan; slice C amendment; PRD §12 decisions 37 (Cobalt look, D1–D5, D10) and 38 (tab bar now, reversing slice C Q2); PRD §6.2 roadmap R-7 (Transactions list settings sheet: totals banner mode, pinned account filters), R-8 (Insights tab slot decision, M3), R-9 (hide-amounts toggle, proposed); `docs/learnings/README.md` entry + `DIGEST.md` if a standing rule changes |
| K1 | KDoc (`CobaltPalette`, `BrandColors`, `derivedBrand`); `design-system.md` Colour; `appearance.md`; `app/README.md` `ui/theme` |
| K2 | KDoc (`sansFamily`, `bricolage`, `pftType`); `design-system.md` Typography + Third-party assets; `appearance.md`; root `README.md` licences line if it lists fonts |
| K3 | KDoc (`MoneySemantic`, `MoneyTone`, `MoneyText`, `MoneySize`); `design-system.md` MoneyText; `DIGEST.md` "Colour meaning" |
| K4 | `appearance.md` Defaults; `design-system.md` Theming/Shapes; `app/README.md` `ui/theme` |
| K5 | KDoc; `app/README.md` `ui/common`; `design-system.md` Content primitives |
| K6 | KDoc; `app/README.md` `ui/home` |
| K7 | `PftNavHost` KDoc; `app/README.md` `ui/nav`, `ui/more`, `ui/accounts`; `docs/architecture/overview.md` (navigation); `docs/architecture/code-structure.md` (`ui/nav/PftTabBar.kt`, `Tabs.kt`); `first-run-and-profiles.md` |
| K8 | KDoc; `app/README.md` `ui/home`; `first-run-and-profiles.md` |
| every PR | `docs/learnings/README.md` entry; `graphify update .` when structure changes |

## 10. Risks & mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Bricolage lacks `tnum` or `₹` | hero digits jitter or `₹` falls back to a system glyph | [Verify] in K2 Red; stop and ask; fallback Manrope 700 hero |
| Bricolage file > 500 KB | pre-commit large-file hook blocks | path-scoped exception backed by `BundledFontTest` size + hash (existing pattern) |
| K4 re-records ~100 goldens | real regressions hide in a big visual diff | K4 contains no behaviour change; reviewer walks `_compare.png` by screen; split commits (defaults, shapes) |
| Navigation restructure breaks back/launch | user stuck or returned to Setup | `TabNavigationFlowTest` + existing flow tests through the real host; predictive back checked on `pft35` |
| Slice C diverges while Cobalt is in flight | merge conflicts in C-sync | Cobalt touches `ui/` only; slice C until C3b touches core/data and three list screens' notices |
| Tone adjustments look off to the owner | rework | §12 Q3; adjustments are two values, cheap to change before K4 |
| A derived palette (Midnight, Ivory, Aubergine, dynamic) fails a new card/brand pair | K1 red on a palette nobody uses by default | fix inside `derivedBrand` (e.g. card from `primaryContainer`/`onPrimaryContainer` for that case), never by exempting the palette from the loop |
| White islands contradict a standing rule | confusion in later reviews | D10 scopes the exception to Cobalt light in KDoc, test and digest |

## 11. Rollout & verification

1. Per PR: `bash tools/agent/check-retry.sh <worktree> <tag>` in the background; read every `_compare.png` that moved.
2. K2: `assembleRelease` per-entry sizes before/after (`unzip -lv`), noted in the PR.
3. K-final on `pft35`: fresh install → Setup → Home (greeting) → tabs (Accounts, More) → open the account form (bar hidden) → switch tab and back (form restored) → Back to Home → Back exits. Repeat in dark mode. Compare with the mockup side by side.
4. Release smoke (digest "APK size"): signed copy, `am start`, no `FATAL EXCEPTION`.
5. **Rollback:** each K PR reverts cleanly; reverting K4 alone restores the Graphite look with all new tokens in place.

## 12. Open questions

Owner answers of 2026-11-25 settled the palette default (D7), the tab bar (D8), Home (D9), sequencing (D11) and restyle-vs-replace (D3). The owner accepted every default below on 2026-11-25 ("all yes"):

| # | Question | Default |
|---|---|---|
| Q1 | Accept M3's control labels (TextButton, focused field label) in `primary` (#2251FF light, #9DB1FF dark) as the one exception to "blue never for text"? | **Yes.** Fighting M3 here costs a wrapper per control; both values pass 4.5:1 |
| Q2 | Confirm white islands in Cobalt light (D10), reversing "never white cards" for Cobalt only? | **Yes**, as approved in the mockup |
| Q3 | Accept the tone adjustments: light secondary #7A8399 → #687187, dark tab bar #181D2B → #151A27, faded decimals 55% → 68% (and solid below 24sp), all card text solid white? | **Yes.** Each is the nearest value that passes WCAG AA |
| Q4 | Final M1a tab set Home · Transactions · Budgets · Accounts · More, with Insights (M3) expected to displace More to the Home avatar? | **Yes** |
| Q5 | Drop the bell, gear, eye and "2 people" pill for M1a (D6), with hide-amounts recorded as roadmap R-9 (proposed)? | **Yes** |
