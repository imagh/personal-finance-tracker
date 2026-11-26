# `:app` — Android application

## Purpose

The thin Android layer of Personal Finance Tracker: Compose UI, the single `MainActivity`, the
Room/SQLCipher implementations of the `core` repository interfaces, Keystore key handling and safe
logging (`SafeLog`, A5a; only `SafeLog` may log). App lock arrives in a later slice. It contains no business logic; that lives in `core/<module>`
(CLAUDE.md §3.1).

Status: **A1 skeleton plus DS1, DS2, DS3, DS4 and DS5 (theming, icons and category glyphs, motion, one-hand layout, content primitives), A4a (the Room database v1 and its core adapters), A4b (SQLCipher, the Keystore-wrapped key, the pre-migration backup), A5a (safe logging: `SafeLog`, typed `LogEvent`s, the detekt and source-scan guards, the database-open events) A5b (the `AppContainer` that opens the database off the main thread, `AppServices`, `LaunchViewModel`, `PftRoot`, the generalised fail-closed screen), A5c to A5e (the navigation and lifecycle pins, the shared form blocks, the Setup ViewModel and screen) and A5f (the navigation host, the Home placeholder, the debug-only catalogue route, predictive back).** The app launches a `FLAG_SECURE`, edge-to-edge
window inside `PftTheme`.
On start `MainActivity` shows `PftRoot`, which asks `PftApplication.container` to open the **encrypted**
database off the main thread (see *Startup*, below). If the key cannot be produced, or the stored
identity is inconsistent, `PftRoot` shows `DataUnavailableScreen` instead (fail closed). Otherwise it
shows `PftNavHost`: **the app opens to Setup on a fresh install** (name and phone name, saved as the
identity) and to the Home placeholder on every later launch; Back on Home leaves the app (plan
`docs/plans/2026-11-01-m1a-a5-first-run-setup.md`, product doc `docs/features/first-run-and-profiles.md`). Real Home content arrives with C4 (plan
`docs/plans/2026-10-28-m1a-core-ledger.md`); the rest of the design system (the DS6 premium-look slices) later (plan
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
| Emoji font | `androidx.emoji2.text.EmojiCompatInitializer` removed from the merged `androidx.startup.InitializationProvider` (`tools:node="remove"`) | Its default config asks Google Play services for a downloadable emoji font, a cross-app fetch; emoji are drawn by the system emoji font instead, so rendering is deterministic and nothing is fetched. The provider itself is declared `exported="false"` |

Three library-contributed manifest entries are removed with `tools:node="remove"` because they would
break the table above: `androidx.profileinstaller.ProfileInstallReceiver` (an exported receiver)
the `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` permission from `androidx.core` (only needed
below API 33; minSdk is 33), and the EmojiCompat initializer (last table row). If a library upgrade adds a new entry, `ManifestHardeningTest` fails.

`androidx.compose.ui:ui-test-manifest` is deliberately not a dependency: it adds an exported
`ComponentActivity` to the debug manifest. Compose tests use `HostedComposeRule`, which registers
that activity with Robolectric instead. Likewise `ui-tooling` (debug only) registers an exported
`androidx.compose.ui.tooling.PreviewActivity`; `src/debug/AndroidManifest.xml` removes it
(`tools:node="remove"`), so Studio's "run preview on device" is unavailable but static previews and
screenshot tests work. `ManifestHardeningTest` runs against both variants.

## Public interface

Entry points: `dev.imagh.pft.app.PftApplication` (owns the `AppContainer`) and `dev.imagh.pft.app.MainActivity`. Reusable building blocks (plan §3.4) are listed here
as they land (`data/common`, `ui/common`).

### `log` (safe logging, A5a)

All logging goes through one object (PRD SEC-5, CLAUDE.md §3.7). Policy:

- `SafeLog.event(LogEvent)` is the only way to log. It writes one INFO line under the tag `pft`,
  `e.line()`, in debug **and** release builds (the events are non-sensitive, and logcat is readable
  only over adb).
- `LogEvent` is a sealed interface whose subclasses hold only `Int`, `Long`, `Boolean`, enums or
  `java.time.Duration`, so a message, an amount, a payee, a name, a path or a token cannot be passed.
  Lines look like `db_opened schema=1 ms=12` and match `^[a-z_]+( [a-z_]+=[A-Z0-9_]+)*$`.
  `LogEventShapeTest` enforces the field types by reflection; its exhaustive `when` makes a new
  subclass without a sample fail to compile. The rendered line of every sample is also checked
  against the pattern, and `render` accepts only numbers and enums.
- Events are added **only by the change that first emits them**. Today: `DbOpened(schemaVersion, took)`
  and `DbKeyUnavailable(reason)`, both emitted by `DatabaseStartup.open`, and `IdentityCorrupt`
  (`identity_corrupt`, no fields), emitted by `LaunchViewModel`, and `SetupCompleted`
  (`setup_completed`, no fields: no name, label or id) and `SetupFailed` (`setup_failed`, no fields: not
  the exception, its message or its class name), both emitted by `SetupViewModel`. B2a adds
  `EntityActionFailed(kind, action)` (`entity_action_failed kind=ACCOUNT action=ARCHIVE`), emitted by
  `AccountsViewModel` when an archive, unarchive, delete or undo fails unexpectedly. It carries
  enums only: `EntityKind { ACCOUNT }` and `EntityAction { ARCHIVE, UNARCHIVE, DELETE, RESTORE }`; an
  entry is added with the screen that first uses it (categories and tags, reorder). B2b adds
  `EntitySaveFailed(kind)` (`entity_save_failed kind=ACCOUNT`), emitted by `AccountEditViewModel` when
  saving the form fails unexpectedly; enum only, never a name, the last four digits or an amount.
- Guards: detekt `ForbiddenImport` bans `import android.util.Log` everywhere, with one file-level
  `@Suppress("ForbiddenImport")` in `SafeLog.kt` (and one in the instrumented `EncryptedDatabaseTest`,
  which prints its own timing); `LoggingScanTest` scans `app/src/{main,debug,release}` and
  `core/*/src/main` for `println`, `print`, `System.out/err`, `printStackTrace`, any `android.util.Log`
  reference (calls and `Log::d`), `java.util.logging`, `System.getLogger` and `Timber` (its shared scanner is the test helper `testing/SourceScan`).
- Never log from a `catch`: there is no way to pass the exception, on purpose.

### `ui/theme` (design-system tokens, DS1, DS2, DS3, DS4)

| Symbol | What it is |
|---|---|
| `PftTheme(prefs: ThemePrefs = ThemePrefs.Default) { ... }` | Wraps the UI. Resolves dark/light from `prefs.mode`, picks the colour scheme, applies `MaterialTheme` and provides the extended tokens |
| `PftTheme.money` | `MoneyColors` for the current theme (read inside `PftTheme`) |
| `PftTheme.surfaces` | `PftSurfaces(canvas, island, islandEdge, islandHighlight, glow)` (DS6c, DS6d): the screen/header/bottom-bar background, the card fill, its 1 px edge, the dark top highlight and the header glow tint (the palette's `wash`, or `null` while `ThemePrefs.headerGlow` is off), from the palette, the mode, `ThemePrefs.background` and `ThemePrefs.headerGlow`. Screens take their background and card colours from here, never `surface`/`surfaceContainer` directly |
| `BackgroundStyle { TONAL, FLAT }` | How surfaces are painted. The names are persisted by AP1 (`ui_background`): never rename or remove one. `TONAL` is the default; `FLAT` is the pre-DS6c look |
| `SyncWindowBackground(window)` | Keeps the window background equal to the live canvas (a `SideEffect`); `MainActivity` calls it once inside `PftTheme` |
| `PftTheme.brand` | `BrandColors(brand, onBrand, brandSoft, onBrandSoft, cardStart, cardMid, cardEnd, onCard, cardGhost, cardGhost2, nav)` (Cobalt plan K1): the filled-brand tokens M3 has no role for (a brand fill, the balance card gradient, the floating tab bar). Hand-picked for Cobalt, `derivedBrand(scheme, island)` for every other palette and for dynamic colour (`PaletteColors.brand` defaults to it). App-drawn text is never `brand` |
| `PftTheme.type` | `PftTypography`: `amountHero` (48/60, display font, `tnum`+`lnum`; bold 34/42.5 for `BUNDLED_GROTESQUE`), `amountLarge` (32/40; sans, or the display font under `EDITORIAL`; `tnum`+`lnum`), `amountRow` (16/24, always sans, `tnum`) |
| `PftTheme.shapes`, `MaterialTheme.shapes` | `PftShapes.glyph` (category badge, 30% corners), `card` 22dp, `island` 18dp (`GroupedSection` clips to it), `quickAction` 17dp, `tabBar` (50%, a full pill); M3 shapes are 6/10/16/24/32dp |
| `PftTheme.spacing` | `PftSpacing`: `xxs` 2, `xs` 4, `sm` 8, `md` 12, `lg` 16, `gutter` 20, `xl` 24, `xxl` 32, `huge` 48 dp |
| `MaterialTheme.typography` | The M3 scale: **by default every role is in the sans text family** (`sansFamily(ThemePrefs.sansFont)`: the bundled Figtree, Plus Jakarta Sans or Manrope, or `FontFamily.Default` for `SansFont.SYSTEM`); under `TypePairing.EDITORIAL`, `display*`, `headline*`, `titleLarge` and `titleMedium` are in the display font, each cut for its own size and weight (`titleSmall`, `body*`, `label*` stay sans). `headlineLarge` 32/40 semibold and `titleLarge` 22/28 medium are the header titles. |
| `PftTheme.motion` | `PftMotion`: springs `snappy` (0.30 s, bounce 0.10), `smooth` (0.45 s, 0), `gentle` (0.60 s, 0), `bouncy` (0.50 s, 0.25) as `SpringSpec<Float>`; `navEnter`/`navExit`/`navPopEnter`/`navPopExit` (25% slide + fade on `smooth`); `PRESSED_SCALE` 0.97; `smoothOffset` is `smooth` over `IntOffset` for slide transitions (rolling digits). A constant, not a CompositionLocal |
| `iosSpring<T>(durationMillis, bounce)` | Converts a SwiftUI-style spring to a `SpringSpec<T>` (`stiffness = (2π/duration)²`, `dampingRatio = 1 − bounce`); throws `IllegalArgumentException` for bounce outside `[0, 1)` or duration ≤ 0 |
| `Modifier.pressScale()` | Scales to 0.97 with `snappy` while pressed; observes pointers only (does not consume). Put it before `clickable` |
| `PftSymbol` (`key`, `res`), `PftSymbol.fromKey(key)` | The icon registry: 92 Material Symbols Rounded vector drawables (`res/drawable/sym_<key>.xml`; chrome: `back`, `forward`, `add`, `close`, `check`, `search`, `filter`, `overflow`, `chevron_left`, `chevron_right`, `expand_more`, `expand_less`, `edit`, `delete`, `undo`, `lock`, `settings`, `calendar`, ...; the rest are category symbols). **Keys are permanent** (persisted in `category.icon_key`): add, never rename or remove; `src/test/resources/pft-symbol-keys.txt` pins them. `fromKey` is exact-match and null for anything unknown. Draw one with `Icon(painterResource(symbol.res), contentDescription = ..., tint = ...)`: the drawable is black. `androidx.compose.material.icons.*` is banned in app code (detekt `ForbiddenImport`). The full list and the how-to are in `docs/architecture/design-system.md` |
| `PftTheme.palette`, `CategoryPalette[key]`, `categoryPalette(dark)`, `CategoryColors(container, content)` | The 12 named category hues (`rose, coral, amber, olive, emerald, teal, sky, blue, indigo, violet, magenta, slate`), light and dark, for glyph badges only (never money). An unknown or null name gives `slate`. Content is at least 4.5:1 on container |
| `GlyphSize { Chip 28, Row 40, Hero 56 }` | The badge sides, fixed dp |
| `ThemePrefs(mode: ThemeMode, dynamicColor: Boolean, lightPalette: ThemePalette, darkPalette: ThemePalette, background: BackgroundStyle, headerGlow: Boolean, displayFont: DisplayFont, typePairing: TypePairing, sansFont: SansFont)`, `ThemePrefs.Default` | The user's appearance. `Default` is `SYSTEM`, dynamic colour **off** (opt-in), `GRAPHITE` for both `lightPalette` and `darkPalette`, `TONAL` background, header glow **off** (`headerGlow = false`, DS6d), bundled serif, `HERO_ONLY` type pairing (DS6g), `FIGTREE` sans (DS6h) |
| `ThemePalette { GRAPHITE, MIDNIGHT, IVORY, AUBERGINE, COBALT }` | The palettes a user can pick, light and dark independently. **The names are persisted by AP1: never rename or remove one, never depend on the order.** `ThemePalette.colors(dark)` (internal) is its `PaletteColors` |
| `DisplayFont { BUNDLED_SERIF, BUNDLED_GROTESQUE, SYSTEM_SERIF, TEXT }` | What the display family is (the roles that use it are chosen by `TypePairing`); `TEXT` is "same as text". Only the two bundled fonts get variation settings. `BUNDLED_GROTESQUE` (Bricolage Grotesque, K2) makes the hero bold 34sp (the others are regular 48sp); the names are persisted, so it was added, not renamed |
| `TypePairing { HERO_ONLY, EDITORIAL }` | Which roles use the display family (UI-15, DS6g): `HERO_ONLY` (default) is the hero only, today's look (owner decision Q10); `EDITORIAL` adds `amountLarge`, `display*`, `headline*`, `titleLarge` and `titleMedium`. `displayFont = TEXT` with `EDITORIAL` is all sans. **The names are persisted by AP1: never rename or remove one**; a third value (all serif) can be added later without a migration. `ThemePrefsTest` pins the names |
| `SansFont { FIGTREE, PLUS_JAKARTA_SANS, MANROPE, SYSTEM }` | The sans text family (UI-2, UI-16, DS6h, DS6i, K2): `FIGTREE` is the bundled Figtree, `PLUS_JAKARTA_SANS` the bundled Plus Jakarta Sans, `MANROPE` (the default since K4) the bundled Manrope, `SYSTEM` the platform default. **The names are persisted by AP1: never rename or remove one**; AP2 adds `CUSTOM`, without a migration. `ThemePrefsTest` pins the names and the default. `ThemePrefs.sansFont` carries it |
| `sansFamily(SansFont)` (internal) | `FIGTREE`, `PLUS_JAKARTA_SANS` and `MANROPE` are each one `FontFamily` of three `Font(R.font.figtree, ...)` / `Font(R.font.plus_jakarta_sans, ...)` / `Font(R.font.manrope, ...)`, weights 400, 500 and 600, each with its own `wght` axis (Figtree's default is 300, Plus Jakarta Sans's 400 and Manrope's 200; the axis is set explicitly and every weight a role uses must be declared; `PftTypographyTest` checks this). `SYSTEM` is `FontFamily.Default`. Reference a font only as `R.font.<name>` here: release shrinking strips a font no reachable code names |
| `bricolage(weight, size)` (internal) | The same shape for the bundled Bricolage Grotesque (`R.font.bricolage_grotesque`): `wght` pinned to `weight`, `opsz` to `size` (clamped to 12 to 96) and `wdth` to 100. Used by `displayFamily(BUNDLED_GROTESQUE, ...)` |
| `heroSizeSteps(heroSize)` (internal) | The `HeroSizeSteps` (48, 40, 34, 28, 24, 20, 16, 12sp) at or below `heroSize`: all eight for the 48sp hero, 34 down to 12sp for the Bricolage hero. `MoneyText` fits the hero through these |
| `newsreader(weight, size)` (internal) | A `FontFamily` of the bundled Newsreader pinned to `weight` on `wght` and cut for `size` on `opsz` (clamped to 6 to 72). Built only for the serif roles of the chosen pairing; replaces the single `NewsreaderFamily` (hero: weight 400, 48sp) |
| `ThemeMode { SYSTEM, LIGHT, DARK }` | `SYSTEM` follows the system night mode |
| `resolveColorScheme(prefs, dark, context)` | Dynamic (Material You) scheme when `prefs.dynamicColor` (opt-in; the palette fields are then ignored), else the scheme of `prefs.darkPalette` in dark or `prefs.lightPalette` in light |
| `GraphitePalette.light` / `.dark` (`PaletteColors(scheme)`, internal) | The default scheme, Graphite + platinum: generated offline with material-color-utilities 0.3.0 from custom tonal palettes, the owner-approved mockup values pinned over the generated roles (provenance and every override are in its KDoc), committed as constants. Used whenever dynamic colour is off and Graphite is chosen, and in screenshot tests. It replaces the ink-indigo `BrandColors`, which is deleted. Canvas, island and wash join `PaletteColors` in DS6c and DS6d |
| `MidnightPalette`, `IvoryPalette`, `AuberginePalette` (`.light` / `.dark`, internal) | Three more curated palettes (DS6b), generated and pinned the same way as Graphite; each KDoc records its provenance and every override |
| `CobaltPalette` (`.light` / `.dark`, internal) | The Cobalt palette (Cobalt plan K1): generated with Graphite's procedure and pinned to the owner-approved mockup, with its own `BrandColors`. Its KDoc records the provenance, the mockup values adjusted for WCAG, the compressed surface ladder and the white light `wash`. Cobalt light is the one palette with a white island (D10). The default palette since K4 |
| `BrandColors`, `derivedBrand(scheme, island)` | The brand token set (public data class) and the pure function that derives it from a scheme for every palette that does not pick its own; `ColorContrastTest` sweeps it |
| `MoneyColors`, `moneyColors(dark)` | Fixed green/red/amber tokens for amounts and status. **Never derived from the accent**; the same in the dynamic and the Graphite scheme. Positive balances are neutral by owner decision (`MoneyTone` in `ui/common` maps meaning to these tokens) |

The bundled serif is Newsreader (`res/font/newsreader.ttf`, OFL-1.1, unmodified upstream; licence in
`assets/licenses/newsreader-OFL.txt`; provenance and hash in `docs/architecture/design-system.md`).
The default sans is Figtree (`res/font/figtree.ttf`, OFL-1.1, unmodified upstream, no Reserved Font Name;
licence in `assets/licenses/figtree-OFL.txt`; provenance and hash in the same table). The second sans option is
Plus Jakarta Sans (`res/font/plus_jakarta_sans.ttf`, OFL-1.1, unmodified upstream, no Reserved Font Name; licence in
`assets/licenses/plus-jakarta-sans-OFL.txt`). `BundledFontTest` pins all five, and `FontBudgetTest` keeps all of
`res/font` (1,263,860 B for the five) within 3 MiB.
`TokenSheet` (internal) shows every token; it feeds the Studio preview, the debug catalogue and the
screenshot goldens.

Rules: never draw money text on `surfaceVariant`; use `background`, `surface` or a
`surfaceContainer*` step (dark `negative` on `surfaceVariant` is 4.23:1, below 4.5). Do not wrap an M3 component just to restyle it (style comes from the theme); raw colour
values and design `dp`/`sp` live only in `ui/theme` (detekt's `MagicNumber` is relaxed there for
that reason). Colour is never the only cue: every amount carries a sign and every status a word.

### `ui/common` (shared components, DS3, DS4, DS5 and A5d)

| Symbol | What it is |
|---|---|
| `CollapsingHeaderScaffold(title, style, subtitle, hero, heroCaption, period, actions, bottomBar, floatingActionButton, snackbarHost) { LazyListScope }` | The screen frame: M3 `TopAppBarState` + `exitUntilCollapsedScrollBehavior` (snap on `PftTheme.motion.smooth`), expanded height `clamp(0.38 x window, 200dp, 360dp)`, 64dp collapsed bar, collapsed-only under 560dp of window height, expanded while the list fits. Owns the `LazyColumn`. Edge-to-edge (`safeDrawing`); `bottomBar` is `imePadding()`-aware |
| `HeaderStyle { Dashboard, Standard }` | `Dashboard`: `titleLarge` title about 12% down plus the hero, caption and period stepper in the lower (thumb) band. `Standard`: `headlineLarge` title centred in the middle, no hero. All titles are sans, centred and read-only (Q10) |
| `HeaderIconButton(symbol: PftSymbol, label: String, onClick)` | The only control a header's `actions` may hold (UI-14): an M3 `IconButton` around a 24dp registry symbol tinted `onSurfaceVariant`, announced as a button named `label` (a blank label throws `IllegalArgumentException`), touch target at least 48dp, never text. `actions` stays a slot (an overflow `DropdownMenu` needs a `Box` anchor); the rule is in the scaffold KDoc and `CollapsingHeaderScaffoldTest` checks it on the samples |
| `glowAlpha(distance, end)`, `Density.glowEnd(heroTop, bodyHeight)`, `glowStrength(collapsedFraction)`, `MAX_GLOW_ALPHA` (DS6d, `ui/common/HeaderGlow.kt`, internal) | The pure rules of the header glow: 72% at the top edge, about 28% at 38% of the reach, exactly 0 from the end on; the reach is 24dp above the hero (0.6 x the header without one); the strength is `1 - collapsedFraction` |
| `Modifier.headerGlow(glow: Color?, heroTop: () -> Float?, collapsedFraction: () -> Float)` (internal) | Draws the glow behind the header: a radial gradient of `glow` from the top-centre, stops from `glowAlpha`. A null `glow` adds nothing. Both lambdas are read in the draw phase only, so a scroll frame redraws but never recomposes. `CollapsingHeaderScaffold` applies it with `PftTheme.surfaces.glow`; screens do not call it |
| `HeaderPeriod(onPrevious, onNext)` | Dashboard only: draws the chevron stepper (symbols, labelled "Previous period" / "Next period") and makes a sideways swipe of more than 48dp on the expanded header step the period |
| `BottomActionBar(primary, modifier, secondary)`, `BottomBarAction(label, enabled, onClick)` | One full-width primary button, or an outlined secondary plus the primary on the end side; at least 56dp tall; pads by `safeDrawing` (so it clears the IME and navigation bar) |
| `MoneyText(amount: Paise, semantic: MoneySemantic, modifier, size: MoneySize = Row, animate = false, onColor = Color.Unspecified, fadeDecimals = false)` | One amount: `PaiseFormat.format` (sign-free, from `:core:money`) with a U+2212 or `+` prepended here, never reformatted. Tone from `moneyTone` mapped to a `PftTheme.money` token (neutral is `onSurface`); the spoken description names the direction first ("Expense, ₹250.00"). A negative amount is rejected (`IllegalArgumentException`, thrown in composition) for every semantic except `Balance` and `Net`: callers pass magnitudes. `onColor` draws a neutral figure in a given colour (the balance card's `onCard`) and throws for any other tone; `fadeDecimals` (hero only) draws the decimals at 0.68 alpha while the hero is at 24sp or more, solid below. `size = Hero` steps the figure down through `HeroSizeSteps` (48, 40, 34, 28, 24, 20, 16, 12sp) to the largest that fits one line; if even 12sp does not fit it wraps rather than cutting digits. `animate` rolls the digits (`PftMotion.smooth`); a hero too wide for even 12sp is plain wrapping text, without the roll |
| `MoneySemantic { Income, Expense, Net, Transfer, Balance, Budget(state) }`, `BudgetState { UNDER, AT_THRESHOLD, OVER }`, `MoneySize { Hero, Large, Row }` | What an amount means and which `PftTheme.type.amount*` style it uses. The size never changes the tone; `Balance` and `Net` are the only semantics that may be negative |
| `MoneyTone { Neutral, Positive, Negative, Warning }`, `moneyTone(semantic, amount)` | The pure mapping: income Positive; expense, net and transfer Neutral at every size; balance Neutral unless negative (Q3); budget Positive, Warning or Negative by state |
| `StatusChip(label, tone: MoneyTone, modifier)` | A labelled pill (Unverified, Overdue, Paused, Over, 80%). The label is required (blank is rejected): colour is never the only cue. Colours are the money container tokens, or `surfaceContainerHighest` for Neutral (`chipColors`) |
| `GroupedSection(modifier) { row(onClick?) { ... } }`, `GroupedSectionDefaults.listItemColors()` | An inset (20dp gutter), rounded (`large`) island of rows filled with `PftTheme.surfaces.island` (TONAL adds a one-pixel edge, lit along the top in dark mode), with a one-pixel divider, inset 16dp from the start, between rows only (N rows, N-1 dividers). A row with `onClick` is clickable (`Role.Button`) and at least 56dp tall. Give M3 `ListItem`s `listItemColors()` (transparent container) |
| `TransactionRow(model: TransactionRowModel, modifier)`, `TransactionRowModel(title, glyph, colourKey, category?, account, amount, semantic, needsReview, tags, onClick?)` (K5) | One transaction as plain data (no entity): a decorative `CategoryGlyph` tile (`name = null`), the title (`onSurface`), a "category · account" meta line (`onSurfaceVariant`; a transfer has no category), optional tag chips (neutral `StatusChip` pills that wrap), the amount as a `MoneyText` (expense neutral with a minus, income green with a plus, transfer unsigned) and, with `needsReview`, a "Needs review" `StatusChip`. The model rejects any semantic but expense, income and transfer, and a negative amount. The row is **one node**: `clearAndSetSemantics` with one description, "title, category, account, tags, Expense, ₹486.00, Needs review" (merging is not enough: `MoneyText` and `StatusChip` are merged nodes themselves and are not folded into a parent). With `onClick` it is a `Role.Button` at least 56dp tall. Title and meta wrap and never truncate; from a font scale of 1.5 the amount moves under the tile and text and takes the full row width. It draws no background: it sits in a `GroupedSection` row. Named consumers: Home recent (C4d), Transactions tab (C4a), search (C5b), budget detail (D4), loan detail (F3) |
| `DayGroup(day, today, rows, modifier, total: Paise? = null)` (K5) | A day header over one `GroupedSection` island of `TransactionRow`s. The header is "Today" or "Yesterday" relative to the **passed-in** `today` (the caller owns the clock and zone; the block never reads the time), else the date in the device locale (`DateFormat.getBestDateTimePattern("yMMMd")`); it is a heading. The optional `total` is a `MoneyText(Net)` (signed, neutral, spoken "Net"), shown only when given. An empty `rows` list throws `IllegalArgumentException` |
| `CategoryGlyph(source: GlyphSource, name: String?, colourKey, modifier, size = Row, onClick = null)` | The category/account badge: a soft-square tile in a palette colour holding a symbol, one emoji or a letter. One image node announced as `name` (never the key), or **decorative with a null `name`** (nothing is spoken and it is not an image; for a badge beside a title that already says the name, as in a category row; a tappable badge requires a name); fixed dp size; emoji clipped to one line; with `onClick` it is a button (48dp touch target, press scale) |
| `GlyphSource { Symbol(PftSymbol), Emoji(text), Letter(letter) }` (`Letter` holds one code point as a string, with a `Char` constructor), `GlyphSource.fromIconKey(iconKey, name)`, `GlyphSource.letterFor(name)` | The UI model of `category.icon_key`. `fromIconKey` is the **boundary parser** (untrusted input), but it does **not own the grammar** (B3a): it delegates to core's `CategoryIcon.parse` (`sym:<key>` with key `[a-z0-9_]{1,40}`, or `emoji:<value>` of 1 to 16 UTF-16 units, well-formed, no whitespace, control or invisible format characters, ZWJ and tag characters allowed; core also enforces it when an icon is written) and adds the one check that is the app's: a symbol key must **exist** in the registry (`PftSymbol.fromKey`). Anything else (null, no prefix, oversized, malformed, a well-formed key that is not registered) is `Letter` of the name's first letter or digit by code point (a combining mark or unpaired surrogate never counts, a letter outside the basic plane does), upper-cased (`?` if none); it never throws. `Emoji` itself rejects an invalid value (`IllegalArgumentException`, via core's `CategoryIcon.Emoji.isValid`) |
| `CategoryIcon?.toGlyphSource(name)` (internal, `CategoryIconGlyph.kt`, B4a) | The one mapping from core's `CategoryIcon` to a `GlyphSource` (a symbol that exists in the registry, a valid emoji, else the name's letter; never throws). `GlyphSource.fromIconKey` is this over `CategoryIcon.parse`. Consumers: the categories list, `CategoryPicker` (B4b), C4/D4/E4 rows |
| `EntityAction.failureRes()` (internal, `EntityActionText.kt`, B4a) | The fixed "Couldn't ... Try again." string resource for each `EntityAction`, shared by the accounts, categories and tags lists (B5b) |
| `EmptyState(message, modifier, glyph, actionLabel, onAction)` | A centred glyph slot, message and optional tonal-button action; the label and callback must be given together |
| `FieldState(text, error)` with `edit(text)` and `check(rule)`, `FieldCheck(value, state)`, `FieldError { Required, TooLong(max), InvalidLastFour, InvalidAmount, Duplicate }` with `FieldError.message()` (the one internal `@Composable` that turns an error into its string), `LedgerError.toFieldError()` (A5d, B1b, B3a; `FieldState.kt`, `FieldError.kt`, `LedgerErrorMapping.kt`) | A form field's state as plain data a ViewModel holds. `text` is exactly what was typed and is never trimmed or clipped; `edit` replaces it and clears the error. `check` runs one of **core's own validators** (for example `{ Validation.requireName(it, MAX_NAME_LENGTH) }`) and returns the validated value, or null plus the state with the `FieldError`; it catches only `LedgerError`, and rethrows one that is not about a field. `toFieldError()` and `message()` are exhaustive `when`s (no `else`): a new `LedgerError` case fails compilation until mapped, and a new `FieldError` until it has its string, and non-field errors (`NotInitialised`, `IdentityCorrupt`, the category rules `DepthExceeded`, `KindMismatch`, `HasChildren`, `InvalidOrder`, `InvalidIcon`, `SystemCategoryProtected`, ...) map to null, to be shown as a form-level message. `DuplicateName` maps to `FieldError.Duplicate` ("Already in use", for a category or tag name) |
| `NameField(label, state, onTextChange, modifier, imeAction)` (A5d) | The one name input: a single-line M3 `OutlinedTextField`, words capitalised, error text under it (`FieldError.message()`) and M3 error semantics. **Never truncates or alters the typed text**: 120 typed characters stay 120 and length is reported as `TooLong`, never enforced by clipping. Fills the width between the gutters and adds its own vertical rhythm |
| `AmountField(label, state, onTextChange, modifier, imeAction)` and `FieldState.checkAmount(): FieldCheck<Paise>` (B2b) | The one amount input: a single-line M3 `OutlinedTextField` with a decimal number keyboard and a "₹" prefix (drawn, not part of the text). Like `NameField` it **never alters what is typed**. `checkAmount()` trims and reads the text with core's `PaiseParser` (Indian or Western grouping, at most two decimals, up to `Paise.MAX_AMOUNT`): blank gives `FieldError.Required`, anything the parser refuses (a sign, `1.234`, letters) gives `FieldError.InvalidAmount`. **Magnitude only**: no sign is accepted, so a screen that needs a signed value (the account balance) applies the sign itself, and zero is valid (a caller wanting a positive amount checks that). Named consumers: account balance (B2b); transaction, item, budget and loan amounts later |
| `EntityListScreen(title, active: List<EntityRow>, archived: List<EntityRow>, emptyMessage, onBack, modifier, add: RowAction?, snackbarHost, loaded = true, header: (@Composable () -> Unit)? = null)` (B2a, B4a) | The one list screen every entity list is (plan D11): a `CollapsingHeaderScaffold` with a labelled Back, the **active** rows in a `GroupedSection`, a collapsed **"Archived (n)"** header (a 48dp button that says "Collapsed"/"Expanded") over a second island, an `EmptyState` when there is nothing at all (only once `loaded`: while `loaded` is false neither rows nor the empty state are drawn, so the empty message never flashes before the first read), a labelled `+` FAB, and a snackbar host. `add` is both the FAB and the empty state's button; **null hides both** (accounts pass null until B2b). A row is an M3 `ListItem` with an optional `CategoryGlyph`, a title, a supporting line and, when it has actions, a trailing overflow button (labelled "Actions for <title>") that opens a menu of exactly those actions. Named consumers: accounts, categories, tags (loans and goals later). No generic list ViewModel or base class: each consumer maps its entities to rows **`header`** (B4a) is an optional block drawn as the first list item, above the rows: it shows before the first load and above the empty state (the categories list puts its Expense/Income switch there). Null adds nothing. |
| `EntityRow(key, title, supporting, glyph: RowGlyph?, indent, onClick, actions: List<RowAction>)`, `RowGlyph(source, colourKey, label)`, `RowAction(label, onClick)` (B2a) | Plain immutable data: everything a row shows and can do, already resolved to text. The glyph's `label` is what TalkBack says for the badge (the account type), so the title is not announced twice; a null `label` makes the badge decorative (category rows). `indent` insets a child row by one spacing step (24dp). `key` is the entity id as text and keeps a row's open menu with its entity when the list reorders |
| `UndoDeleteSnackbar(offer, message, host, onUndo, onDismissed)` (B2a) | The effect (it draws nothing itself) that shows "<message>" with **Undo** on a `SnackbarHostState` for 10 s while an `offer` is on. Undo reports `onUndo`, timeout or swipe reports `onDismissed`; a new offer replaces the snackbar and a null offer removes it, both without reporting. The list's ViewModel owns the delete and the undo, so nothing travels across navigation |
| `PftBottomSheet(title, onDismiss, modifier) { close -> ... }` (B2c) | The one bottom sheet every picker opens in, and the only file that opts into `ExperimentalMaterial3Api` for `ModalBottomSheet`. Compose it only while it is open (`if (open) PftBottomSheet(...)`). The sheet takes the theme's container colour and extra-large (32dp, top corners only) shape, opens at its full content height (no half-open state) and shows `title` as a heading. Back, a scrim tap, a swipe down and the content's `close` all slide it out and then call `onDismiss` once. Named consumers: `AccountPicker`, `CategoryPicker` (B4b), `TagPicker` (B5b), the C4 date picker |
| `AccountPicker(accounts: List<Account>, selected: AccountId?, onPick, onDismiss, modifier)` (B2c) | Stateless sheet that asks "Choose account". The consumer's ViewModel loads `AccountService.active()` (live, not archived, sorted by name ignoring case) and passes it, so only active accounts are offered, in the service's order; there is no ViewModel in the component. A row is a radio-style `ListItem`: the type badge (`AccountRowMapper.badge`, silent: the supporting line speaks the type), the name and the list's supporting line ("Bank · HDFC · ••1234"). `selected` has `selected` semantics and a check mark (an id not in the list marks nothing). A tap calls `onPick(id)` once, then closes the sheet and calls `onDismiss`; taps while it closes are ignored. No accounts: "No accounts yet. Add one from More.". Logs nothing. The consumer is C4's account chip; no screen uses it yet |
| `CategoryPicker(categories: List<Category>, selected: CategoryId?, onPick, onDismiss, modifier, topLevelOnly, title)` (B4b) | Stateless sheet that asks "Choose category" (or the `title` given: the form's parent field says "Choose parent category"). The consumer's ViewModel loads `CategoryService.pickable(kind)` and passes it, so only pickable categories are offered (an archived one and the children of an archived parent are absent) **in the service's order, never re-sorted**. A grid of `CategoryGlyph` badges with the name under each; each top-level category and its sub-categories form one group that starts on a new row, and a sub-category's cell has a second line "Under <parent>", so two "Misc" cells are told apart on screen and by ear. `topLevelOnly` leaves the sub-categories out (the parent field). **TalkBack:** the cells are one `selectableGroup`, each a `Role.RadioButton` selectable; the badge is decorative (`name = null`: no description, not an image), so a cell speaks its name once from its text, then "Under <parent>"; the chosen cell is `selected` and ringed; a `selected` id that is not offered marks nothing. Each cell is at least 48dp. A tap calls `onPick(id)` once, then closes the sheet and calls `onDismiss` (a `picked` flag ignores taps while it slides out). Empty: "No categories to choose from yet". Logs nothing. Consumers: the category form's parent field (B4b), C4b's item editor, D budgets; shares only `PftBottomSheet` with `AccountPicker` |
| `TagPicker(tags: TagLists, selected: Set<TagId>, onDone: (Set<TagId>) -> Unit, onDismiss, modifier)` (B5b) | Stateless **multi-select** sheet that asks "Choose tags". The consumer's ViewModel passes `TagService.lists()` and the item's current tag ids. Offered: every **active** tag in core's order (by name ignoring case), never re-sorted. **The selected-archived rule:** an archived tag that is already in `selected` is still drawn (after the active ones), selected and with an "Archived" line, so an edit (C4) cannot silently drop it and the user can deselect it on purpose; an archived tag not selected is never offered; a selected id that is no tag any more is not drawn but is handed back untouched. A grid of cells (neutral `PftSymbol.TAG` badge over the name; tags have no colour or icon), built on `TileGrid` and `selectionRing`. **TalkBack:** cells are one `selectableGroup`, each a `Role.Checkbox` selectable (announced as checked); the badge is decorative (`name = null`), so a cell speaks its name once, plus "Archived"; the title is a heading. A tap toggles a cell and the sheet stays open; **Done** calls `onDone(chosen)` once (an empty set is valid), then closes the sheet and calls `onDismiss` (a `finished` flag ignores a second tap while it slides out). Back, the scrim and a swipe down call `onDismiss` only. Cells and Done are at least 48dp. Nothing to offer: "No tags yet. Add one from More." and no Done. Logs nothing. Shares only `PftBottomSheet`, `TileGrid` and `selectionRing` with the other pickers. The consumer is C4b's item editor; no screen uses it yet |
| `TileGrid(items, tileWidth, modifier) { item -> }`, `Modifier.selectionRing(selected, shape)` (B4b, internal) | The two layout and look pieces the choice grids share (`CategoryPicker`, `TagPicker`, the form's `SymbolGrid` and `ColourSwatches`): rows of equal-width tiles, as many as fit `tileWidth`, as a plain `Column` of `Row`s (no experimental `FlowRow`, usable inside a scrolling item); and the primary-coloured 2dp ring that marks the chosen tile by shape, not colour alone. Neither adds semantics: each grid makes its tiles selectable and the container a `selectableGroup` |
| `launchSave(kind, onOutcome, refused, write)` and `SaveOutcome { SAVED, VANISHED, FAILED }` (B4b, internal, `LaunchSave.kt`) | The save shape of the entity forms (plan D12), a plain `CoroutineScope` extension (no base class): `write` returning is `SAVED`; `LedgerError.NotFound` / `Deleted` is `VANISHED` (a stale screen, not logged); anything else is `FAILED` and logs one `entity_save_failed kind=...` (enums only; the exception text is dropped); a cancellation is rethrown, never reported. `refused` lets a form show a ledger rule itself (the category form shows `DuplicateName` under the name field): if it returns true, nothing is reported or logged. Used by `AccountEditViewModel` and `CategoryEditViewModel`; `SetupViewModel` keeps its own (it treats `AlreadyInitialised` as done, logs its own event and has no vanished case) |
| `EditFormScaffold(title, onPrimary, modifier, subtitle, primaryLabel, saving, onBack) { LazyListScope }` (A5d) | The create/edit frame: a `Standard` `CollapsingHeaderScaffold` with the primary action ("Save" by default, or the caller's, such as "Continue") in a `BottomActionBar`. `saving = true` disables it (a double-tap guard; the ViewModel keeps its own). `onBack` adds a labelled back `HeaderIconButton` (48dp target); null means no Back. **Adds no `imePadding()`**: the scaffold already lifts its bottom bar above the keyboard. Named consumers: Setup (A5e), B2 account edit, B3/B5 forms, D/E/F/G edit screens |

The hero slot is set in `PftTheme.type.amountHero` by default (the serif is the number only). Actions
go in the top row (back, search, filter, overflow; secondary only), add actions in the FAB. Both
components are shared (named consumers in `docs/architecture/code-structure.md`); do not wrap them
to restyle. `HeaderSamples.kt` holds the fake-data samples that feed the previews, the debug
catalogue and `HeaderScreenshotTest`; DS5 adds one file per component (`MoneyTextSamples.kt`,
`StatusChipSamples.kt`, `GroupedSectionSamples.kt`, `EmptyStateSamples.kt`; K5 adds `TransactionRowSamples.kt` (row and day-group samples), goldens `transaction-row-*` and `day-group-*`; DS3 adds `CategoryGlyphSamples.kt`; A5d adds `FormSamples.kt`, a live two-field form; B2a adds `EntityListSamples.kt`, a full list screen and its empty twin, goldens `entity-list-*`; B2c adds `AccountPickerSamples.kt`, goldens `account-picker-*`; B4b adds `CategoryPickerSamples.kt`, goldens `category-picker-*`; B5b adds `TagPickerSamples.kt`, goldens `tag-picker-*`, and `ui/tags/TagsSamples.kt`, goldens `tags-list-*`) for the same three
consumers and `ContentScreenshotTest`. Each `*Samples` file is listed in `mustBeRemoved` of
`verifyReleaseShrink`, so R8 is shown to drop it from the release APK.

Money text never sits on `surfaceVariant`: `GroupedSection` uses the island and `StatusChip` a `surfaceContainer`
step, and `ColorContrastTest` sweeps both (and the canvas and island of every palette) and pins that the sweep still excludes `surfaceVariant`. The test cannot stop
a future component using it: that is a review rule, and a new money surface should add its colour to the test.

**Icons and emoji (DS3).** All icons come from `PftSymbol`; emoji are drawn by the system emoji font (they can look different
on different phones: accepted, symbols are the consistent option). The pre-DS3 `PeriodStepper` text chevrons in
`CollapsingHeaderScaffold` and the placeholder glyph in `EmptyStateSamples` are unchanged on purpose: DS6e swaps the chevrons for
`PftSymbol.CHEVRON_LEFT`/`CHEVRON_RIGHT`, and consumers pass a `CategoryGlyph` to `EmptyState`.

**Amount contract.** `MoneyText` throws in composition for a negative `Paise` with any semantic except `Balance`, so
callers (the C4, C5 and D4 ViewModels or mappers) must pass magnitudes (non-negative `Paise`) and validate there.

### `data` (Room persistence, A4a)

Package `dev.imagh.pft.app.data`. The Room implementations of the repository interfaces `core`
defines (the only swap boundaries in M1a); tables and conventions are in
[`docs/architecture/data-model.md`](../docs/architecture/data-model.md).

| Symbol | What it is |
|---|---|
| `PftDatabase` (`VERSION = 4`), `Migrations.ALL` (`MIGRATION_1_2`, `MIGRATION_2_3`, `MIGRATION_3_4`) | The Room database `pft.db` (`user_profile`, `device`, `app_setting`, `audit_entry`, `account`, `category`, `tag`) with exported schemas in `app/schemas/`; the ordered list of migrations. **`PftDatabase.create(context, name, openHelperFactory)` is the only way to build it** (production and tests; `internal`, so only the app module, in production `DatabaseFactory`, can call it): it registers `Migrations.ALL` and the audit-trigger callback; `DatabaseFactory` only passes SQLCipher's open-helper factory. `name = null` is in-memory |
| `AccountEntity`, `AccountDao`, `AccountMapper`, `RoomAccountRepository` (B1c, `data/account/`) | Table `account`, and core's `AccountRepository` over it: `RoomEntityRepository` plus `allLive()` (`deleted_at IS NULL`, no order; the service sorts). `AccountMapper` always writes `currency = 'INR'` and **fails closed on read** (`IllegalStateException` naming the column, never the value) for another currency or an unknown `type`. No `CHECK` and no index (Room cannot declare a CHECK; see `data-model.md`) |
| `CategoryEntity`, `CategoryDao`, `CategoryMapper`, `RoomCategoryRepository` (B3b, `data/category/`) | Table `category` (schema v3), and core's `CategoryRepository` over it: `RoomEntityRepository` plus `allLive()` (`deleted_at IS NULL`, no order; the service sorts) and `bySystemKey(key)` (also `deleted_at IS NULL`); bound parameters only, no `@RawQuery`. The entity has a self-referencing foreign key on `parent_id` (**enforced**, see `data-model.md`), an index on it and a **unique** index on `system_key`; no `CHECK`, no partial index. `CategoryMapper` stores `colour` as the palette **key** (`coral`) and reads it with `CategoryColour.fromStored`, so an unknown or wrong-case colour reads as slate; a stored `icon_key` that fails `CategoryIcon.parse` reads as no icon; an unknown `kind` or `system_key` **fails closed** (`IllegalStateException` naming the column, never the value), like `account.type`. `MIGRATION_2_3` is the SQL copied from `schemas/.../3.json`; it seeds nothing |
| `TagEntity`, `TagDao`, `TagMapper`, `RoomTagRepository` (B5a, `data/tag/`) | Table `tag` (schema v4), and core's `TagRepository` over it: `RoomEntityRepository` plus `allLive()` (`deleted_at IS NULL`, no order; the service sorts); bound parameters only. The entity is a name and the shared columns, with **no index and no key**: "unique among live tags, ignoring case" is `TagService`'s rule, because Room cannot declare a partial or `NOCASE` unique index (see `data-model.md`). `TagMapper` has no branch: a name is text and nothing about it is interpreted on read. `MIGRATION_3_4` is the one `CREATE TABLE` copied from `schemas/.../4.json`; it seeds nothing |
| `AuditTriggers` | The `BEFORE UPDATE`/`BEFORE DELETE` triggers that make `audit_entry` append-only (Room does not model triggers). Its callback re-installs them on **every open** (dropping a same-named trigger first), so a database that lost or had its triggers replaced is repaired; `PftDatabaseTest` covers it |
| `RoomTransactionRunner(db)` | core `TransactionRunner` over `withTransaction`. A failure at any nesting depth rolls back the outermost block, even when an outer block swallows it (same as core's fake) |
| `RoomAuditLog(dao)`, `AuditDao`, `AuditEntryMapper` | core `AuditLog`: insert and select only. `changes_json` is the pinned JSON shape written with `org.json` |
| `RoomIdentityRepository`, `RoomAppSettingsRepository` | core `IdentityRepository` and `AppSettingsRepository` over plain DAOs |
| `MetaColumns` + `MetaMapper` | The `@Embedded` shared columns (`id`, `created_at`, `updated_at`, `created_by_user_id`, `created_on_device_id`, `deleted_at`, `archived_at`) and the one mapping to `EntityMeta`. Entities declare `primaryKeys = ["id"]` themselves |
| `BaseDao<E>`, `EntityMapper<T, E>`, `RoomEntityRepository<T, E, Id>` | What a new entity composes (B1 onwards): its DAO extends `BaseDao` and overrides `byId` with an `@Query`; its mapper implements `EntityMapper`; its repository extends `RoomEntityRepository(dao, "<auditType>", mapper)` and adds only its own queries |

Adding an entity table (B1 onwards): the Room entity with `@Embedded MetaColumns` and explicit
snake_case `@ColumnInfo` names, DAO, mapper, repository, a `Migration` registered in `Migrations.ALL`
with `PftDatabase.VERSION` bumped, a `migrateNToN+1` test, and a line in `AuditedTables` (test
support). `RoomAuditColumnsTest` fails if a column is neither audited nor on the reviewed allowlist.

### `security` and the encrypted open (A4b)

Design, key file format and the fail-closed table: [`docs/architecture/persistence-and-security.md`](../docs/architecture/persistence-and-security.md).

| Symbol | What it is |
|---|---|
| `DbKeyManager.forApp(context)`, `useKey { key -> ... }` | The SQLCipher key: 32 random bytes, stored only wrapped (AES-256-GCM, Keystore KEK `pft_db_kek`) in `noBackupFilesDir/pft_db_key.bin`. `useKey` wipes the key after the block. Fails closed with `DbKeyUnavailable(reason)` and never creates a key over an existing key file or an existing `pft.db`. The KEK is not user-auth-bound, because M1b's SMS receiver writes while locked |
| `DatabaseFactory.forApp(context).open()` | Opens `pft.db` through SQLCipher: key first (before the file exists), then the pre-migration backup, then `PftDatabase.create` over SQLCipher's open helper, opened eagerly. SQLCipher keeps and re-uses the key array it is given, so it is handed a **copy** that lives as long as the database (wiping it would key later connections with zeros). Throws `DbKeyUnavailable` or `PreMigrationBackupFailed`; creates and deletes nothing in either case |
| `PreMigrationBackup`, `PreMigrationBackupFailed` | Copies the still-encrypted database (and its `-wal`) to `noBackupFilesDir/backups/pre-migration-v{n}.db` (n = the old schema version) before a migration, keeps the newest 3, never copies the key, `rwx------`/`rw-------`. A failed `.db` copy deletes the `-wal` copy already written, and pruning deletes any `-wal` whose `.db` is absent. Records the last-opened version in `noBackupFilesDir/pft_schema_version` |
| `DatabaseStartup` (`Ready(database)`, `KeyUnavailable(reason)`), `DatabaseStartup.open(factory)` | How opening ended; only `DbKeyUnavailable` becomes a result. Logs `db_opened schema=<n> ms=<n>` or `db_key_unavailable reason=<code>` through `SafeLog` (nothing else) |
| `ui/startup/DataUnavailableScreen(cause)`, `DataUnavailableCause` (`KeyUnavailable(reason)`, `IdentityCorrupt`) | The fail-closed screen (A4b's `KeyUnavailableScreen`, generalised in A5b): the shared title, a warning not to clear storage or uninstall, and `Code: <code>`. The cause fixes the explanation string and the code (an enum name or a constant), so the screen cannot be given user content or exception text. No path, key, name or exception detail; no reset action |

### `di` and startup (A5b)

Package `dev.imagh.pft.app.di`, plus `ui/LaunchViewModel` and `ui/PftRoot`. Concrete classes, no DI framework.

| Symbol | What it is |
|---|---|
| `AppServices(database, clock, auditLog = RoomAuditLog(..))` | The production object graph over an **open** `PftDatabase`: `ids`, `runner`, `identityRepository`, `accountRepository`, `accounts` (`AccountService`), `categoryRepository`, `categories` (`CategoryService`), `tagRepository`, `tags` (`TagService`), `settingsRepository`, `recorder`, `identity`, `settings`, `writer`, `firstRun` (`FirstRunService`: the identity plus the default categories in one transaction). Does no I/O. `RoomTestKit` builds exactly this, so Room integration tests run the production wiring. The clock must be a whole-millisecond clock (`EpochMillis.clock`); `AppContainer` guarantees that |
| `AppContainer(openDatabase, baseClock, io)`, `AppContainer.forApp(context)` | Owns *when* the database opens. `suspend fun open(): AppStart` opens it once, on `io` (`Dispatchers.IO`), never on the caller's thread and without blocking it; every caller, concurrent or later, shares the one result. The open belongs to the container, so a cancelled caller does not abort it. A key failure is the value `AppStart.KeyUnavailable`; any other failure is rethrown to every caller and never cached as `Ready` (the container does not retry). `openDatabase: () -> DatabaseStartup` is a function, a test seam for a slow or failing open, not an interface |
| `AppStart` (`Ready(services)`, `KeyUnavailable(reason)`) | How `AppContainer.open()` ended |
| `LaunchViewModel(container, io)` + `LaunchState` (`Opening`, `KeyUnavailable(reason)`, `IdentityCorrupt`, `Ready(services, hasIdentity)`) | Maps `AppStart` plus `identity.currentIdentity()` to the launch state. `LedgerError.IdentityCorrupt` becomes `LaunchState.IdentityCorrupt` (one `identity_corrupt` log line), never "no identity". Its work runs on an injected IO dispatcher so Robolectric tests can wait for the state from the main thread. Anything unexpected is not caught: it ends the start |
| `PftRoot(container)` | The root composable: `Opening` draws nothing (the window background already equals the canvas, so no flash), the two fail-closed states draw `DataUnavailableScreen`, `Ready` draws `PftNavHost(services, hasIdentity)` |

**Startup.** `MainActivity.onCreate` sets `FLAG_SECURE`, enables edge-to-edge and calls `setContent`
before any database work. `PftRoot` obtains `LaunchViewModel` (state `Opening`), which calls
`container.open()` on IO: key, backup, SQLCipher open, `db_opened` log, then the `AppServices`.
M1b's SMS receiver will call the same `container.open()`. `PftApplication.startup`, which opened the
database lazily on the calling (main) thread, is gone. `PftApplication` and its `container` are `open`
only so `MainActivityOpeningTest` can substitute a held-open opener.

### `ui/setup` and the UiState convention (A5e)

Package `dev.imagh.pft.app.ui.setup`. One ViewModel, one screen, no new interfaces. Shown by `PftNavHost`
when no identity exists (A5f).

| Symbol | What it is |
|---|---|
| `SetupUiState(name, deviceLabel, saving, done, saveFailed)` | Everything the screen shows: two `FieldState`s, `saving` (Continue disabled), `done` (an identity exists; the route leaves) and `saveFailed` (a fixed form-level "Couldn't save. Try again." message) |
| `SetupViewModel(firstRun, defaultDeviceLabel)` + `SetupViewModel.factory(services, defaultDeviceLabel)` | `onNameChange`, `onDeviceLabelChange`, `submit()`. `submit()` checks both fields through `FieldState.check { Validation.requireName(it, LedgerLimits.MAX_NAME_LENGTH) }` (core's rule, no copy here), saves nothing unless both pass, then calls `FirstRunService.initialise` (the identity and the 31 default categories, one transaction) on the main dispatcher and logs `setup_completed`. It sets `saving` before launching and ignores calls while `saving` or `done`, so a double tap saves once. `AlreadyInitialised` counts as done. **Any other `Exception` is caught** (a `CancellationException` is rethrown): `saving` resets, `saveFailed` is set, both typed fields are kept exactly as typed, and one field-less `setup_failed` is logged (the exception is dropped on purpose; the catch binds `_`, so detekt's `TooGenericExceptionCaught` needs no suppression). `saveFailed` clears when either field is edited or Continue is tapped again. Retry is safe because `FirstRunService.initialise` is one transaction (user, device, setting, 31 categories, 34 audit rows), so a failed attempt leaves nothing behind. The phone-name prefill is `defaultDeviceLabel` trimmed and cut to 100 |
| `SetupScreen(state, onNameChange, onLabelChange, onContinue, modifier)` | Stateless: an `EditFormScaffold` (title "Welcome", subtitle "Set up this phone", Continue in the bottom bar, no Back), one explanatory line, two `NameField`s ("Your name", "This phone's name") and, when `saveFailed`, a fixed error-coloured "Couldn't save. Try again." line under them, a polite live region so TalkBack reads it when it appears. Rendered from fake state by the tests and goldens |
| `SetupRoute(services, onDone, modifier)` | The only place that touches the ViewModel: builds it with `Build.MODEL` as the default phone name, collects its state and calls `onDone` when `done` becomes true |

**UiState convention (plan D4).** It is a convention, not a type: one immutable `XxxUiState` data
class per screen, exposed as a `StateFlow` and collected with `collectAsStateWithLifecycle`; a one-shot
outcome such as "setup finished" is a state field the screen reacts to, not a `Channel`; a
`LedgerError` is mapped in the ViewModel, never in a composable. A generic `UiState<T>` waits for a
second consumer with a Loading/Content/Error shape (B2 may introduce one for its lists).

### `ui/nav` and `ui/home` (A5f)

Packages `dev.imagh.pft.app.ui.nav` and `dev.imagh.pft.app.ui.home`. Concrete, no new interfaces.

| Symbol | What it is |
|---|---|
| `Routes` (`SETUP = "setup"`, `HOME = "home"`, `MORE = "more"`, `ACCOUNTS = "accounts"`, `TAGS = "tags"` (B5b), `ACCOUNT_EDIT = "accounts/edit?id={id}"` and `accountEdit(id?)` to build it; B4b adds `CATEGORY_EDIT = "categories/edit?id={id}&parent={parent}&kind={kind}"` and `categoryEdit(id?, parent?, kind?)`, which leaves out the arguments not given and encodes each; B5b adds `TAG_EDIT = "tags/edit?id={id}"` and `tagEdit(id?)`, in `ui/nav/TagRoutes.kt` with the list and form destinations and a `tag_not_found` flag on the tags entry) | Plain string routes (plan D3): Navigation 2.8's type-safe routes need the kotlinx-serialization compiler plugin, which the project does not use. The only argument is an entity **id** (never a name or an amount), optional (none: create), URI-encoded when built and treated as untrusted on arrival (plan D13). If the account is unknown the form pops and leaves a flag on the accounts entry's saved state, which the list turns into a "That account no longer exists" snackbar. No deep links. The debug catalogue's route (`dev/catalog`) is private to `src/debug/.../DevEntry.kt`, so release has no trace of it |
| `PftNavHost(services, hasIdentity, modifier)` | The `NavHost`. Starts on `HOME` when `hasIdentity`, on `SETUP` otherwise. Setup's `onDone` navigates to `HOME` with `popUpTo(SETUP) { inclusive = true }`, so Back on Home leaves the app. All four transitions are `PftMotion.navEnter/navExit/navPopEnter/navPopExit`. It also calls `devRoutes()` (debug adds the catalogue; release adds nothing). B2a adds `MORE` and `ACCOUNTS` as plain pushes: Home's More button opens More, More's Accounts row opens the list, and Back pops one screen at a time (Accounts to More to Home) |
| `HomeUiState(userName, deviceLabel, loaded)`, `HomeViewModel(identity)` + `HomeViewModel.factory(services)` | Reads `identity.requireIdentity()` on `viewModelScope` and holds the user and phone name. Home is only reachable once an identity exists, so a missing or inconsistent one here is a bug and is not caught |
| `HomeScreen(state, onOpenMore, modifier, actions)` | Stateless: a `CollapsingHeaderScaffold` titled "Home", subtitle "<name> · <phone>" once loaded, one `EmptyState` ("Open More to manage your accounts. Transactions arrive in the next update"). The header ends with a `HeaderIconButton(OVERFLOW, "More")` that calls `onOpenMore` (plan D9; C4 moves it into the bottom bar); `actions` is the caller's extra header actions, drawn before it (only `HeaderIconButton`s, UI-14) |
| `HomeRoute(services, onOpenMore, modifier, actions)` | The only place that touches `HomeViewModel` |
| `devRoutes()` and `RowScope.DevHeaderActions(navigate)` in `src/debug` and `src/release` | Debug: the `dev/catalog` route (`DesignCatalog()`) and a "Design catalogue" `HeaderIconButton` (`PftSymbol.SETTINGS`) on Home that opens it. Release: both are empty |

**Predictive back.** The manifest sets `android:enableOnBackInvokedCallback="true"` on `<application>`
(`ManifestHardeningTest` pins it). On API 35 with the developer option "Predictive back animations"
(`enable_back_animation`), a slow back swipe from the catalogue showed Home sliding in from the start
while the catalogue followed the finger out to the end, with `PftMotion`'s pop transition, and a
released swipe popped to Home. No glitch was seen. Back on Home finishes the activity. The owner
watched the animation on a visible emulator and approved its look on 2026-11-03. The result is
recorded in `docs/architecture/design-system.md` and the learnings.

**Lint.** Navigation's own lint checks (`WrongStartDestinationType`, `WrongNavigateRouteType`,
`WrongPopBackStackRouteType`, `TypeSafeDestinationMissingAnnotation`, `ComposableDestinationInComposeScope`,
`ComposableNavGraphInComposeScope`, `UnrememberedGetBackStackEntry`, and the deep-link ones) are all on.
With navigation 2.8.4 and AGP 8.7.3's lint 31.7.3 the detectors threw `NoClassDefFoundError`
(`androidx/navigation/lint/UtilKt`) on `PftNavHost.kt`, and one crashing detector aborts the whole run,
so A5f had disabled seven of them. Navigation 2.8.9 (`navigationCompose` in `gradle/libs.versions.toml`)
does not crash, although its lint jars are byte-identical to 2.8.4's. Only `AndroidGradlePluginVersion`,
`GradleDependency` and `NewerVersionAvailable` are disabled now, because upgrades are deliberate. A newer
lint (`android.experimental.lint.version=8.13.2`) also fixed it but needs 45 extra components (85
artifacts) with known CVEs, so it was not adopted; see the learnings entry of 2026-11-02 (issue #46).
Revisit it when the lint version moves with the next AGP upgrade (`docs/TECHNICAL.md` section 7.1), and
only once issue #47 lets the vulnerability scan cover that classpath.

### `ui/more` and `ui/accounts` (B2a, B2b)

Packages `dev.imagh.pft.app.ui.more` and `dev.imagh.pft.app.ui.accounts`. Concrete, no new interfaces. B2b adds the account form (`AccountEdit*`) and wires the list's add button and row tap to it.

| Symbol | What it is |
|---|---|
| `MoreScreen(onBack, onOpenAccounts, onOpenCategories, onOpenTags, modifier)` | Stateless (nothing to load, so no ViewModel or route wrapper): a `CollapsingHeaderScaffold` titled "More" with Back and one `GroupedSection` with the rows **Accounts**, **Categories** (B4a) and **Tags** (B5b); C4 reuses the screen as the bottom bar's More tab |
| `AccountsUiState(active, archived, loaded, undo: AccountId?, failedAction: EntityAction?)`, `AccountsViewModel(accounts)` + `AccountsViewModel.factory(services)` | The accounts list's state and actions: `reload()`, `archive(id)`, `unarchive(id)`, `delete(id)`, `undo()`, `dismissUndo()`, `failureShown()`. Lists are read from `AccountService.lists()` on construction, after every action and on `reload()` (plan D2: no `Flow`). Actions and reads run one at a time (a `Mutex`). A `LedgerError` (the account was deleted elsewhere) just refreshes the list; any other exception leaves the list as it was, sets `failedAction` and logs one `entity_action_failed`; cancellation is rethrown. A successful delete sets `undo` to that account (only the latest delete is offered) |
| `AccountsScreen(state, actions: AccountsActions, snackbars, onBack, modifier)`, `AccountsActions(archive, unarchive, delete, add, edit)` | Stateless: `EntityListScreen` over account rows. Active rows offer Archive and Delete, archived rows Unarchive and Delete. The add FAB (and the empty state's "Add account" button) calls `add`; tapping a row, active or archived, calls `edit(id)` |
| `AccountsRoute(services, onBack, onAdd, onEdit, notFound, onNotFoundShown, modifier)` | The only place that touches `AccountsViewModel`. When `notFound` is set (the edit form was opened for an account that no longer exists) it shows a fixed "That account no longer exists" snackbar and then calls `onNotFoundShown`. `LifecycleResumeEffect` reloads the lists whenever the screen resumes (so an edit made elsewhere shows on return) and ends the undo offer when it pauses; `UndoDeleteSnackbar` shows "Account deleted" with Undo; a failure shows a fixed "Couldn't archive. Try again." snackbar (never exception text) and is then cleared |
| `AccountRowMapper.badge(type)`, `AccountRowMapper.supporting(typeLabel, issuer, lastFour)`, `Account.toRow(typeLabel, onClick, actions)` (internal) | The only account-specific drawing logic: Bank `account_balance`/blue, Credit card `credit_card`/violet, Cash `payments`/emerald, Wallet `account_balance_wallet`/teal; the second line is "<type> · <issuer> · ••<last four>" with missing parts left out. The badge is silent (`RowGlyph.label = null`): the supporting line already speaks the type |
| `AccountEditUiState(editing, loaded, notFound, name, type, lastFour, issuer, amount, saving, saved, saveFailed)`, `AccountEditViewModel(accounts, accountId: String?)` + `AccountEditViewModel.factory(services, accountId)` (B2b) | The form's state and actions: `onNameChange`, `onTypeChange`, `onLastFourChange`, `onIssuerChange`, `onAmountChange`, `submit()`. It copies `SetupViewModel`'s save shape (plan D12): every field is checked with core's own rule (`Validation.requireName`, `AccountRules.requireMaskedIdentifier`, `Validation.requireOptionalName`, `checkAmount`), **every invalid field shows its own error at once** and nothing is saved; a `saving` guard ignores a second submit; cancellation is rethrown; any other failure resets `saving`, sets `saveFailed`, keeps the typed text and logs one `entity_save_failed kind=ACCOUNT`. **The balance sign follows the type (Q3):** the amount is typed positive; a credit card stores it negated ("Amount owed"), bank, cash and wallet as entered ("Current balance"); switching the type flips the stored sign, not the text. **Edit mode** (`accountId` non-null) reads `AccountService.all()` and prefills (a negative card balance shows as "5,000.00"); an unknown, deleted or malformed id (the argument is untrusted, compared as text) or an account deleted before Save ends in `notFound`, with nothing logged; `loaded` stays false until the read finishes, so nothing flashes. An untouched balance (type and amount text as prefilled) is saved as stored, so renaming an account whose sign does not follow its type cannot flip it; an edit that changes nothing writes no audit entry |
| `AccountEditScreen(state, actions: AccountEditActions, modifier)`, `AccountEditActions(...)`, `AccountEditRoute(services, accountId, onDone, onNotFound, onBack, modifier)` (B2b) | Stateless form on `EditFormScaffold`: `NameField`, the four types as M3 single-choice segmented buttons (equal height, no check mark), a number-keyboard last-four field, the issuer `NameField`, `AmountField` (label "Amount owed" for a credit card, else "Current balance"), and a polite live-region "Couldn't save. Try again." line after a failure. No fields are drawn before an edit has loaded or after not-found, and Save is disabled then and while saving. `AccountEditRoute` is the only place that touches the ViewModel and calls `onDone` once on save and `onNotFound` once on not-found |

### `ui/tags` (B5b)

Package `dev.imagh.pft.app.ui.tags`. Concrete, no new interfaces. A tag is only a name, so these are the plainest of the three entity screens and copy the accounts shape (plan D2, D11, D12, D13); no generic list ViewModel, and no Move up/Move down because tags have no order.

| Symbol | What it is |
|---|---|
| `TagsUiState(active, archived, loaded, undo: TagId?, failedAction: EntityAction?)`, `TagsViewModel(tags)` + `TagsViewModel.factory(services)` | The list's state and actions: `reload()`, `archive(id)`, `unarchive(id)`, `delete(id)`, `undo()`, `dismissUndo()`, `failureShown()`. Lists are read from `TagService.lists()` on construction, after every action and on `reload()`; actions and reads run one at a time (a `Mutex`). A `LedgerError` (tag gone, or an undo whose name was taken meanwhile) is an answer: the list refreshes, nothing is shown or logged. Any other exception sets `failedAction` (a fixed message) and logs one enum-only `entity_action_failed kind=TAG action=...`; a cancellation is rethrown. A successful delete offers an undo of that tag only |
| `TagsScreen(state, actions: TagsActions, snackbars, onBack, modifier)`, `TagsActions(archive, unarchive, delete, add, edit)`, `TagsNavigation(back, add, edit)`, `TagsRoute(services, navigation, notFound, onNotFoundShown, modifier)` | Stateless `EntityListScreen` over tag rows (name, a **silent** `PftSymbol.TAG` badge: `RowGlyph.label = null`, since the title already says the name). Active rows offer Archive and Delete, archived rows Unarchive and Delete; `loaded` is passed so the empty text never flashes. `TagsRoute` is the only place that touches the ViewModel: reload on resume, the "Tag deleted" Undo snackbar (`UndoDeleteSnackbar`), the fixed failure snackbar (`failureRes`) and the "That tag no longer exists" message |
| `TagEditUiState(editing, loaded, notFound, name, saving, saved, saveFailed)`, `TagEditViewModel(tags, tagId: String?)` + `TagEditViewModel.factory(services, tagId)` | The form's state and actions: `onNameChange`, `submit()`. Validation is core's (`Validation.requireName`). The save is `launchSave(EntityKind.TAG, ::finished, ::showRefusal)`: a `DuplicateName` from core (ignoring case, archived counted, checked inside the write transaction) shows "Already in use" under the name field, keeps the text and logs nothing. The id argument is untrusted: compared as text against the stored tags, so an unknown, deleted or malformed id ends in `notFound` and never crashes. One save at a time; nothing after saved or not-found |
| `TagEditScreen(state, actions: TagEditActions, onBack, modifier)`, `TagEditRoute(services, tagId, onDone, onNotFound, onBack, modifier)` | Stateless form on `EditFormScaffold`: one `NameField` (Done on the keyboard) and a polite live-region "Couldn't save. Try again." line after a failure. Nothing is drawn before an edit has loaded or after not-found; Save is disabled then and while saving |
| `sampleTag`, `sampleTagLists()`, `sampleTagsState()`, `TagsSample()` (internal, `TagsSamples.kt`) | Fake data for the preview and the goldens; release builds drop it |

Tests (`app/src/test/.../ui/tags/`, `ui/common/`, `ui/nav/`): `TagsViewModelTest` and `TagEditViewModelTest` (over real in-memory Room through `RoomTestKit`, with a failing `AuditLog`), `TagsScreenTest`, `TagPickerTest` (over `TagService.lists()`), `TagsFlowTest` (create "Travel", rename to "Trips", archive; Undo; duplicate; not found), `MoreScreenTest`, and the goldens `tags-list-{light,dark,archived-light}`, `tag-edit-{new-light,new-dark,duplicate-light}`, `tag-picker-{light,dark,empty-light}`. `EntityKind.TAG` joins the log enum with this PR (`LogEventTest`).

### `ui/categories` (B4a, B4b)

Package `dev.imagh.pft.app.ui.categories`. Concrete, no new interfaces and no generic list ViewModel.

| Symbol | What it is |
|---|---|
| `CategoriesUiState(kind, active, archived, loaded, undo: CategoryId?, notice: CategoriesNotice?)`, `CategoriesViewModel(categories)` + `CategoriesViewModel.factory(services)` | The list's state and actions: `selectKind(kind)`, `choose(id, CategoryAction)` (move up/down, archive, unarchive, delete), `undo()`, `dismissUndo()`, `noticeShown()`, `reload()`. Reads `CategoryService.lists(kind)` on construction, after every action, on a kind change and on `reload()` (plan D2: no `Flow`); actions and reads run one at a time (a `Mutex`) and an action publishes its message together with the refreshed list. **Moving** re-reads the lists, swaps the category with its neighbour among the *active* siblings in the full sibling order (archived ones stay put) and passes that exact order to `CategoryService.reorder`, which breaks `sort_order` ties itself. **Errors are not copied from `AccountsViewModel`:** `LedgerError.HasChildren` becomes `CategoriesNotice.DeleteBlocked` (nothing written, nothing logged); any other exception (a `LedgerError` for a category gone elsewhere included) becomes `CategoriesNotice.Failed(action)`, logs `entity_action_failed kind=CATEGORY action=...` (enums only) and refreshes the list; a cancellation is rethrown |
| `CategoryItem(category, indent, parentName, actions: List<CategoryAction>)`, `CategoryAction { MOVE_UP, MOVE_DOWN, ARCHIVE, UNARCHIVE, DELETE }`, `CategoryLists.toItems()` (internal) | The row rules, decided in one place and tested through the ViewModel: Move up unless first and Move down unless last among the active siblings of one parent; Archive and Delete never for a system category (Loan EMI); an archived row offers Unarchive and Delete; a child hidden only by its archived parent offers Delete alone; an archived child of an active parent carries `parentName` so the row can say "Under <parent>" |
| `CategoriesScreen(state, actions: CategoriesActions, snackbars, onBack, modifier)`, `CategoriesActions(choose, selectKind, add, edit)`, `CategoriesNavigation(back, add, edit)` | Stateless: an Expense/Income `SingleChoiceSegmentedButtonRow` (selected state announced) as `EntityListScreen`'s `header`, over category rows. B4b: the add FAB (and the empty state's "Add category") calls `add(kind on show)`; tapping a row, active or archived, calls `edit(id)` |
| `CategoriesRoute(services, navigation, notFound, onNotFoundShown, modifier)` | The only place that touches `CategoriesViewModel`. Reloads on resume, ends the undo offer on pause, shows "Category deleted" with Undo, and shows the fixed "Delete its sub-categories first" or "Couldn't ... Try again." notice, then clears it. When `notFound` is set (the form was opened for a category that no longer exists) it shows a fixed "That category no longer exists" snackbar and then calls `onNotFoundShown` |
| `CategoryItem.toRow(actionLabel, parentLine, choose, edit)` (internal, `CategoryRowMapper.kt`) | The only category-specific drawing logic: name, a badge from `CategoryIcon?.toGlyphSource` in the category's colour key (spoken as the name), the indent, the parent line, the menu labelled by the caller, and the row tap (`edit(id)`) |
| `CategoryEditUiState(editing, loaded, notFound, kind, name, colour, icon, parent, parentOptions, saving, saved, saveFailed, emojiRejected)`, `CategoryEditViewModel(categories, categoryId, parentId, kindName)` + `CategoryEditViewModel.factory(services, categoryId, parentId, kindName)` (B4b) | The form's state and actions: `onNameChange`, `onColourChange`, `onIconChange(CategoryIcon?)` (a symbol, none, or a quick-pick emoji), `onEmojiInput(text)` (B4c), `onParentChange(CategoryId?)`, `submit()`. **Create:** the route's `kind` (an enum name; none is Expense) and `parent` (an id) are untrusted text; an unknown kind, or a parent that is not one of the live top-level categories of that kind in `CategoryService.pickable`, ends in `notFound`. Those categories are `parentOptions`, which the form's picker shows; a new category starts slate with no symbol. **Edit:** the id is compared as text with every live category's id (both kinds, archived included); a missing, deleted or malformed id ends in `notFound`. Name, colour and symbol are editable; **kind and parent are fixed once created** (owner Q7): `onParentChange` does nothing when editing, and an emoji icon is kept until another icon is chosen. **`onEmojiInput(text)` (B4c)** treats typed or pasted text as untrusted: `EmojiInputValidator.validate` decides; an accepted emoji becomes `icon` (and clears `emojiRejected`), a rejected one sets only `emojiRejected` (a fixed error flag) and keeps the previous icon, so the state never holds candidate text and nothing is logged; choosing any icon clears the flag. The name is checked with `Validation.requireName`; `DuplicateName` (same name, ignoring case, among live siblings) is refused by core in the write transaction and shown under the name field. The save is `launchSave` (a `saving` guard, `VANISHED` becomes `notFound` with nothing logged, any other failure sets `saveFailed`, keeps the text and logs one `entity_save_failed kind=CATEGORY`, a cancellation is rethrown); `loaded` stays false until the read finishes |
| `CategoryEditScreen(state, actions: CategoryEditActions, onBack, modifier)`, `CategoryEditActions(onNameChange, onColourChange, onIconChange, onEmojiInput, onParentChange, save)`, `CategoryEditRoute(services, categoryId, parentId, kindName, onDone, onNotFound, onBack, modifier)` (B4b) | Stateless form on `EditFormScaffold`: `NameField`, a **Type** row (Expense or Income) and a **Parent category** row ("None (top level)" or the parent's name), `ColourSwatches` and the icon choice (`IconChoice`, B4c: a Symbol | Emoji switch of segmented buttons over `SymbolGrid` or `EmojiTab`), and a polite live-region "Couldn't save. Try again." line after a failure. When creating, the parent row is a button that opens `CategoryPicker(topLevelOnly = true)` over `parentOptions`, and a "Make top level" button clears a chosen parent; when editing, both rows are text with "Can't be changed after the category is created" and have no click action. Nothing is drawn before the form has loaded or after not-found; Save is disabled then and while saving. `CategoryEditRoute` is the only place that touches the ViewModel and calls `onDone` once on save and `onNotFound` once on not-found |
| `SymbolGrid(selected: CategoryIcon?, colour, name, onSelect, modifier)` (B4b) | The form's symbol choices: a "No symbol" cell (the name's first letter, live as the name is typed; selects `null`) then exactly the symbols of `CATEGORY_SYMBOLS` (the explicit list under the `PftSymbol` enum, in display order; a symbol added to the registry later is not offered until it is listed, so it fails closed; `PftSymbolCategoriesTest` checks no duplicates, every default category's symbol, no interface symbol, and that every registry symbol is classified), each drawn in the chosen colour. One `selectableGroup` of `Role.RadioButton` cells with no visible text, so each is announced by a spoken name (`PftSymbol.spokenName()`, the key as words: "Local gas station"; "No symbol"), the badge inside is decorative, the chosen cell is `selected` and ringed, and each is a 48dp target. An emoji (or a symbol not offered) selects nothing |
| `ColourSwatches(selected: CategoryColour, onSelect, modifier)` (B4b) | The 12 palette colours as round swatches in the palette's order: the colour's own badge fill with an outline in its content colour for the current theme; the chosen one has a check mark and a ring. One `selectableGroup` of `Role.RadioButton` cells announced by name ("Amber", `CategoryColour.spokenName()`), each a 48dp target |
| `PftSymbol.spokenName()`, `CategoryColour.spokenName()` (internal, `SpokenName.kt`) | What a screen reader says for a symbol or colour with no visible text: the permanent key with `_` as spaces and the first letter capitalised. The keys are English by construction, so no string per symbol; nothing here is user text |
| `CategoriesSamples.kt` (`sampleCategoriesState`, `CategoriesSample`) | Fake data for the preview, `CategoriesScreenTest` and the goldens; in `verifyReleaseShrink`'s `mustBeRemoved` (R8 drops it) |

**B4c (emoji icons).**

| Item | Role |
|---|---|
| `EmojiInputValidator` (`object`, `validate(text): CategoryIcon.Emoji?`) | The UI-boundary check for typed or pasted emoji text (untrusted). Accepts only exactly one emoji that renders on this device: core's `CategoryIcon.Emoji.isValid` run as is (length bound first, so a huge paste is refused cheaply; no copied grammar), one ICU `BreakIterator` grapheme cluster, a country flag or an `Extended_Pictographic` first code point followed only by emoji parts (VS16, skin tone, tags, ZWJ with pictographs, no dangling ZWJ), and `Paint.hasGlyph`. Keycaps are refused; whitespace is refused, never trimmed. `Paint.hasGlyph` answers under Robolectric `@GraphicsMode(NATIVE)` (true for a cart and a ZWJ family, false for unassigned code points and reserved pictographs), so the render check is not injected |
| `EmojiTab(selected, colour, rejected, onSelect, onInput, modifier)`, `QUICK_PICK_EMOJI` (internal) | The emoji side of the form: a fixed ordered quick-pick of 24 emoji as one `selectableGroup` of `Role.RadioButton` cells (no description: the visible emoji is read by its Unicode name), a "Chosen emoji" row, and a type-or-paste field that is a sink (always empty; hands the text to `onInput` unchanged) with a fixed error when `rejected` |
| `IconChoice(state, actions, modifier)` (internal) | The Symbol \| Emoji switch (segmented buttons, selected state announced) over `SymbolGrid` or `EmojiTab`. Opens on Emoji when the icon is an emoji; the choice survives recreation; switching never changes the icon |

B4c adds `EmojiInputValidatorTest`, `EmojiTabTest`, `CategoryEditEmojiViewModelTest`, switch cases in `CategoryEditScreenTest`,
two emoji flow tests and one edit-reopen test in `CategoriesFlowTest`, and goldens `category-emoji-tab-{light,dark}`;
`category-edit-{new-light,new-dark,editing-light}` were re-recorded for the switch.

B4b adds `CategoryEditViewModelTest` (over real in-memory Room with a failing `AuditLog`: create, the parent
choices, sub-categories, the duplicate-name error, edit and what it leaves alone, untrusted arguments, the
failure shape), `CategoryEditScreenTest`, `SymbolGridTest`, `ColourSwatchesTest`, `CategoryEditScreenshotTest`
(goldens `category-edit-{new-light,new-dark,editing-light}`, `category-symbol-grid-{light,dark}`,
`category-swatches-{light,dark}`), `CategoriesRouteTest` (reload on resume, the add and row-tap callbacks, the
not-found message), `CategoryRoutesTest` (the route strings), `CategoryPickerTest` and
`CategoryPickerScreenshotTest` (goldens `category-picker-{light,dark,parent-light,parent-dark}`, in `ui/common`),
`LaunchSaveTest`, and the create, edit, duplicate and not-found paths in `CategoriesFlowTest`.

Tests: `CategoriesViewModelTest` (listing, kinds, menus, moves including an archived sibling and a
`sort_order` tie, archive) and `CategoriesActionsTest` (delete, undo, the refused parent delete, failures
and logging), both over real in-memory Room; `CategoryRowMapperTest`, `CategoriesScreenTest`,
`CategoriesScreenshotTest` (goldens `categories-list-{light,dark,archived-light}`), `CategoriesFlowTest`
(Home to More to Categories through `PftRoot`), `CategoryIconGlyphTest`, `EntityActionTextTest`,
`CategoryColourPaletteTest` (core's `CategoryColour` and the app's `CategoryPalette` list the same 12
names).

Tests: `EntityListScreenTest`, `UndoDeleteSnackbarTest`, `EntityListScreenshotTest` (goldens
`entity-list-{light,dark,archived-light,empty-light,empty-dark}`), `AccountRowMapperTest`,
`AccountsViewModelTest` (over real in-memory Room, with a failing `AuditLog` for the failure paths),
`AccountsRouteTest` (the resume reload and the pause behaviour, driven through a hand-made
`LifecycleRegistry`; the add and edit callbacks; the not-found message), `MoreScreenTest` and `AccountsFlowTest` (Home to More to Accounts through
`PftRoot`, and the create, edit and not-found paths of the form). B2b adds `AmountFieldTest` (the rule, the field, the decimal keyboard read from the `EditorInfo` the platform asks for), `AccountEditViewModelTest` (over real in-memory Room, with a failing `AuditLog`), `AccountEditScreenTest` and `AccountEditScreenshotTest` (goldens `account-edit-{empty-light,empty-dark,errors-light,credit-card-light,credit-card-dark}`). B2c adds `PftBottomSheetTest` (the title as a heading, Back, `close` reporting one dismissal even if called twice, the top-corner radius read from the drawn pixels), `AccountPickerTest` (over real in-memory Room through `AccountService.active()`: only active accounts, sorted, badges, `onPick` and dismissal, double tap, one radio group, selection, empty) and `AccountPickerScreenshotTest` (goldens `account-picker-{light,dark,empty-light,empty-dark}`). The sheet's dialog window cannot be read by `captureToImage` under Robolectric (it times out), so the tests draw its compose view directly (`drawSheetWindow` in `src/test`).

### Window theme

`res/values/themes.xml` and `res/values-night/themes.xml` (`Theme.Pft`) paint Graphite's canvas
behind the first frame (`pft_window_background`, which must equal `CobaltPalette.*.canvas`; `WindowThemeTest` fails on drift), keep
the system bars transparent and colour the launch splash, so a dark launch does not flash a light
window. `MainActivity` then calls `enableEdgeToEdge()` (after the `FLAG_SECURE` line). The resource
colour is Cobalt's canvas whatever the user picked, because the palette is not known before Compose
runs; every palette's canvas and flat surface, and the derived dynamic ones, stay within 8 tones of it
(`WindowFlashToneTest`, `ThemeResolutionTest`), and `SyncWindowBackground` makes the window colour exact
once Compose is running.

**Known limitation, AP1 must address:** plain `enableEdgeToEdge()`, the window theme
(`values`/`values-night`) and the splash follow the *system* night mode. Once AP1 lets the user
force `DARK` on a light system (or the reverse), the status/navigation bar icons and the
pre-Compose window colour would be wrong. AP1 fixes this with `UiModeManager.setApplicationNightMode`
(design plan §5.1) and/or passing explicit `SystemBarStyle`s to `enableEdgeToEdge()`.

### Debug and release source sets

In `src/debug`, `devRoutes()` adds the `dev/catalog` route to `PftNavHost` and `DevHeaderActions` puts a "Design catalogue" button in Home's header. The route shows `DesignCatalog`: the token sheet, a motion page (press target, one spring track per token), the bottom bar, both header styles and a page each for money text, chips, sections, the empty state and the category glyphs (sources plus every symbol), with toggles for light/dark, the light palette, the dark palette, dynamic colour, hero font, sans font (Figtree, Plus Jakarta Sans or system) and font scale, plus an Accent page for the manual amber check and a Palettes page (use it
to check tokens and components on the emulator or phone; it grows with each DS slice). `src/release` holds empty twins of both (`ReleaseHasNoCatalogueTest` proves Home has no such action and the catalogue class is not on the release classpath), so nothing from the catalogue ships. `TokenSheet` itself lives in `main`
(`ui/theme`) so the screenshot tests can render it on both unit-test variants; it is unreferenced by
release code and R8 drops it (checked by `verifyReleaseShrink`, below).

## Data it owns

- `pft.db` (with `pft.db-wal` and `pft.db-shm`) in the app's database directory: the Room database
  (schema v4: `user_profile`, `device`, `app_setting`, `audit_entry`, `account`, `category`, `tag`; see
  [`docs/architecture/data-model.md`](../docs/architecture/data-model.md)), **encrypted with SQLCipher**.
  `AppContainer` (held by `PftApplication`) opens it and builds the services on it.
- `noBackupFilesDir/pft_db_key.bin`: the wrapped database key (66 bytes, format v1). The KEK
  `pft_db_kek` lives in the Android Keystore, not on disk. Never copy, log or export the file; it is
  useless without the Keystore entry, and both names are permanent (renaming either orphans the data).
- `noBackupFilesDir/backups/pre-migration-v{n}.db` (+ `-wal`): up to three safety copies taken
  before migrations, still encrypted, private to the app.
- `noBackupFilesDir/pft_schema_version`: one integer, the schema version of the last successful open.

Times are stored as epoch milliseconds **exactly**: every
time goes through `EpochMillis.of`, which refuses a finer instant (`IllegalArgumentException`) instead of
rounding it. `AppContainer` builds the `Clock` it gives core with `EpochMillis.clock()` (the A5
acceptance criterion; `AppContainerTest` and `AppServicesTest`), so every instant core produces is storable.

## Configuration

Static, build-time: `applicationId`/`namespace` `dev.imagh.pft` (**permanent**: changing it or the
release signing key installs as a new app with an empty database; plan §12 Q4), minSdk 33,
target/compile SDK 35, JDK 17. Pinned versions are in `gradle/libs.versions.toml`.
Runtime appearance (`ThemePrefs`: mode, dynamic colour, light palette, dark palette, background, header glow, display font, type pairing) is a plain value that defaults to
`SYSTEM` + dynamic colour off + Cobalt for both light and dark + tonal background + no header glow + Manrope sans + Bricolage Grotesque display font + hero-only type (the Cobalt look, K4); the settings screen and its `app_setting` storage arrive in AP1, so today
nothing reads or writes a setting. Colours, like all design tokens, are static code constants, and screens never hardcode them: `ThemeLiteralScanTest` (over the shared `SourceScan`) fails on any `Color(...)`, named `Color.*` (except `Transparent` and `Unspecified`), `FontFamily` or `R.font.*` outside `ui/theme`.

## Dependencies

Compose BOM 2024.10.01 (ui 1.7.5, material3 1.3.1), `androidx.activity:activity-compose` 1.9.3.
`:core:ledger` (and `:core:money` through it). Room 2.7.1 (`room-runtime`; `room-ktx` is not needed, since
2.7 `withTransaction` lives in the runtime) with its compiler through KSP 2.1.0-1.0.29, which pulls in
`androidx.sqlite` 2.5.0 and kotlinx-coroutines. Room's compiler, KSP and the SQL verifier
(`sqlite-jdbc`) run at build time only and are not shipped.
`net.zetetic:sqlcipher-android` 4.7.2 (A4b; runtime): the encryption layer, with its own SQLite
(3.49.1) and a native library for four ABIs (it makes the release APK about 21 MB; an ABI filter is an
H3 option). Pinned, locked and checksummed like every dependency.
`androidx.compose.ui:ui-tooling-preview` (annotations only, via the BOM; the only DS2 addition to
the release runtime classpath), `androidx.compose.ui:ui-tooling` (debug only).
Icons: 92 vector drawables derived from Material Symbols (Apache-2.0, `assets/licenses/material-symbols-APACHE-2.0.txt`);
no icon library is a dependency (`material-icons-core` is on the classpath through Compose but banned by detekt).
Project: `:core:money` (`implementation`, DS5): `MoneyText` formats through `PaiseFormat`. It has no
external dependencies, so the lockfiles and verification metadata did not change.

A5c (pins only; A5b uses `lifecycle-viewmodel-compose`, `lifecycle-runtime-compose` and coroutines; A5f uses navigation): `androidx.navigation:navigation-compose` 2.8.4, moved to 2.8.9 for the lint fix (nav host; brings
`navigation-runtime`/`-common` and, at runtime only, `kotlinx-serialization-core` 1.6.3: the serialization
*compiler plugin* is not applied anywhere), `androidx.lifecycle:lifecycle-viewmodel-compose` and
`lifecycle-runtime-compose` 2.8.7 (this moves the whole lifecycle family 2.8.3 to 2.8.7), and
`kotlinx-coroutines-android` declared explicitly at 1.7.3, the version resolution already selected
(declared, not bumped; `:core:*` still has no coroutines dependency).

Test only: `:core:ledger` test fixtures (in-memory fakes, `TestClock`, Kotest assertions), `room-testing`
(`MigrationTestHelper`), JUnit 5 Jupiter, JUnit Vintage + JUnit 4 (to run Robolectric), Robolectric 4.14.1,
`androidx.test:core`, `androidx.compose.ui:ui-test-junit4` (via the BOM), Roborazzi 1.36.0
(`roborazzi`, `roborazzi-compose`, `roborazzi-junit-rule` and its Gradle plugin; screenshot tests).
Instrumented tests only: `androidx.test:runner` 1.6.2 and JUnit 4.13.2.
No network, analytics or crash SDKs.

## How to run and test

```bash
./gradlew :app:testDebugUnitTest      # Robolectric JVM tests (no emulator needed)
./gradlew :app:assembleDebug          # debug APK (debug key)
./gradlew :app:lint                   # Android lint (also part of `check`)
./gradlew :app:recordRoborazziDebug   # re-record the screenshot goldens (deliberate visual changes only)
./gradlew :app:verifyRoborazziDebug   # compare against the goldens (what `check` does via the unit tests)
./gradlew :app:assembleRelease        # minified (R8) release APK, UNSIGNED: no release signing until H3
./gradlew :app:verifyReleaseShrink    # R8 guard (part of `check`): MainActivity, PftDatabase_Impl and SQLCipher's SQLiteConnection kept, preview code removed
ANDROID_SERIAL=emulator-5554 ./gradlew :app:connectedDebugAndroidTest   # instrumented tests on the pft35 emulator (not part of `check`)
./gradlew :app:testDebugUnitTest --tests '*Room*'   # the Room integration tests only
```

**Release build (R8).** `buildTypes.release` has `isMinifyEnabled` and `isShrinkResources` on
(owner decision 2026-10-30, so sample and preview code does not ship), with
`proguard-android-optimize.txt` plus `app/proguard-rules.pro`. Compose, activity-compose,
room-runtime and sqlcipher-android carry their own consumer rules (Room's keeps every `RoomDatabase`
subclass and its constructor, which is how `PftDatabase_Impl` is found by name; SQLCipher's keeps
`net.zetetic.**` with its natives and JNI-read fields), and AGP keeps the manifest's `MainActivity`.
The one rule in `proguard-rules.pro` is a conservative blanket for SQLCipher's JNI surface, with its
reason in the file (H3 narrows it). Add a rule only for something R8 reports or a smoke test shows
broken, with a comment saying why. There is deliberately **no release `signingConfig`** (H3 adds it from
`ANDROID_KEYSTORE_*` and must fail closed), so `assembleRelease` yields `app-release-unsigned.apk`.
The release APK is about 21 MB with R8 since SQLCipher's native libraries (four ABIs) joined it
(1.4 MB before; the app code itself was about 1.4 MB). A5f, which wires the first screens, measured
21,415,742 B before and 22,290,811 B after (+875,069 B): `classes.dex` +785,168 B (navigation-compose,
`navigation-runtime`/`-common`, `kotlinx-serialization-core`, the ViewModel plumbing and the new
Setup, Home and form code, now reachable), the 92 `PftSymbol` drawables +51,492 B (DS3 predicted about
87 KB), `resources.arsc` +28,272 B and the baseline profile +1.4 KB. The dex growth, not the icons,
is the surprise: the plan expected about +87 KB.

`verifyReleaseShrink` (registered in `app/build.gradle.kts`, wired into `check`) runs
`assembleRelease`, reads `build/outputs/mapping/release/mapping.txt` and fails with a clear message
if minification is off, `dev.imagh.pft.app.MainActivity`, `dev.imagh.pft.app.data.PftDatabase_Impl`
(Room creates it by reflection; nothing references it by name) or
`net.zetetic.database.sqlcipher.SQLiteConnection` (SQLCipher's natives register on it by name through JNI) is gone or renamed, or any
`dev.imagh.pft.app.ui.theme.TokenSheet*` class is still present. When a slice adds preview or sample
code, add its compiled class prefix to `mustBeRemoved`. To smoke-test a release build on `pft35`,
sign a copy of the APK with the debug keystore, outside the build (recipe in `docs/learnings`).

**Screenshot goldens.** `TokenSheetScreenshotTest` (Roborazzi, `@GraphicsMode(NATIVE)`, qualifiers
`w393dp-h851dp-xxhdpi`, light and `+night`, font scale 1.0 and 2.0, Graphite) renders the token
sheet page by page. The goldens are committed in `app/src/test/screenshots/` (fake data only);
`roborazzi.test.verify=true` in `gradle.properties` makes `./gradlew check` compare against them,
on both the debug and release unit-test variants. After an intended visual change run
`:app:recordRoborazziDebug`, review every changed PNG and commit them with the code. Failing actual
and diff images land in `app/build/outputs/roborazzi/`.

Design-token tests: `ColorContrastTest` (JUnit 5, WCAG contrast of every colour pair, for every palette in light and dark), `AccentHueTest` and `ChipColorsTest` (every palette), `ThemePaletteTest` (the pinned mockup values), `ThemePrefsTest`, `PaletteScreenshotTest` (the `palettes-<light|dark>` goldens),
`ThemeResolutionTest`, `PftThemeModeTest` (needs the `+night` qualifier), `WindowThemeTest`, `WindowBackgroundTest`, `WindowFlashToneTest`, `PftSurfacesTest` (DS6c; the surfaces table, the flash bound and the window sync), `GroupedSectionColorsTest`, `BottomActionBarSurfaceTest`,
`PftTypographyTest`, `PftShapesTest`, `PftSpacingTest`, `BundledFontTest` (font hash and OFL text) and
`TokenSheetScreenshotTest` (all Robolectric). DS4 adds `PftMotionTest` and `HeaderHeightTest` (JUnit 5, pure),
`NavTransitionTest`, `PressScaleTest`, `CollapsingHeaderScaffoldTest` (real nested scroll, clock stepped by hand),
`BottomActionBarTest` and `HeaderScreenshotTest` (goldens for both header styles, expanded and collapsed, and the bar); `DesignCatalogTest` lives in `src/testDebug` because
the catalogue is debug-only. DS5 adds `MoneyToneTest` (JUnit 5, pure), `MoneyTextTest` (real `PaiseFormat`:
signs, spoken direction, tone colours, hero shrink at the real maximum `Paise.MAX_AMOUNT` including 200% font
scale on 320dp, rolling digits), `StatusChipTest`, `GroupedSectionTest`, `EmptyStateTest`, K5's `TransactionRowTest` and `DayGroupTest`, and
`ContentScreenshotTest` (goldens for the components, `MoneyText` also at 200%). Text fit is asserted with
line metrics (`TextLayoutSupport.kt`), not `hasVisualOverflow`, which is unreliable in Compose 1.7.

A5e adds `SetupViewModelTest` (Robolectric, real Room through `AppServices`; the main looper is idled by `testing/awaitMain`, which also lets the ViewModel's `viewModelScope` run: blank and over-long names, the phone-name error only, a valid save with its three audit rows and the `setup_completed` line, the double-submit guard (a mutation test showed the guard is what keeps a changed second submit out), prefill), `SetupScreenTest` (copy, Continue in the bottom bar with a 48dp `touchBoundsInRoot` target, disabled while saving, error text, typing), `SetupRouteTest` (the real route over a real database) and `SetupScreenshotTest` (six `setup-expanded-*` goldens: light, dark, light at 200% font, light with the name in error, light while saving, light after a failed save; fake data only). Under Compose rules, wait for a ViewModel result with `awaitMain { compose.waitForIdle(); condition }`: `compose.waitUntil` alone did not let the Main-dispatched save finish.

A5f adds `SetupFlowTest` (the M1a acceptance test: the real `AppContainer` over a real Room database, through `PftRoot`; a fresh install shows Setup, Continue lands on "Akash · Pixel 8" with one user, one device, `current_device_id` and three audit rows persisted, system back on Home finishes the activity, a blank name stays on Setup with nothing saved, and when an identity appears while Setup is open Home shows the **existing** one, not what was typed), `LaunchRoutingTest` (an initialised database opens on Home with the stored identity, Setup is not shown (sampled at each poll as Home appears, and at the end), nothing new is written), `HomeViewModelTest`, `HomeScreenTest`, `PftRootTest` (Home or Setup for a ready database), `DevRouteTest` (`src/testDebug`: the "Design catalogue" action opens the catalogue and back returns to Home), `ReleaseHasNoCatalogueTest` (the new `src/testRelease`: no such action, and the catalogue class is absent) and two `ManifestHardeningTest` cases (the predictive-back flag; the launcher is the only intent filter). Mutation checks: removing `popUpTo` failed the back test, and starting always on Setup failed `LaunchRoutingTest`; a release `DevHeaderActions` that drew the button failed `ReleaseHasNoCatalogueTest`. No golden changed.

B3b changes the setup tests from three audit rows to 34 (the identity plus one CREATE for each of the 31 default categories): `SetupViewModelTest` and `SetupFlowTest` check the 31 rows, their deterministic ids, the `LOAN_EMI` row by `bySystemKey`, that they are attributed to the new user and device, that an identity that already existed gets none, and that a failure after the identity and six defaults were written (the audit log fails on the tenth append) leaves no user, device, setting, category or audit row and a retry then seeds exactly once.
A5f follow-up (the save-failure fix) adds to `SetupViewModelTest` the failure cases: the state after an unexpected failure (fields kept untrimmed, `saveFailed`, `saving` reset, only `setup_failed` logged, nothing persisted), the error clearing on editing either field, the error clearing at once on a retry, a retry ending with exactly one user, one device and three audit rows, and a cancellation not reported as a failure. The failure is injected through the existing `AuditLog` boundary: `AppServices(database, clock, auditLog)` takes a test double that throws on the first append (after the user, device and setting rows are written inside the transaction, so Room rolls them back) and delegates to the real `RoomAuditLog` afterwards. `SetupScreenTest` checks the message and its live region, and that Continue is enabled.

A5d adds the form-block tests: `FieldStateTest` (JUnit 5, pure, through core's real `Validation`: errors, edit-clears-error, the exhaustive mapping), `FieldErrorMessageTest` (B1b: each error kind renders its own string), `NameFieldTest` (label, error text and error semantics, no truncation at 120 or 5,000 characters), `EditFormScaffoldTest` (primary in the bottom bar and outside the list, disabled while saving, Back absent or present with a 48dp `touchBoundsInRoot`) and `FormScreenshotTest` (`form-light`, `form-dark`, `form-light-2x` goldens). The debug catalogue has a Forms page, covered by `DesignCatalogTest`. `DesignCatalogTest` also asserts every page chip fits the window width on one line.

DS3 adds `PftSymbolTest` (permanent keys, drawables inflate), `GlyphSourceTest` (JUnit 5, the `icon_key` boundary: the app-level behaviour, that is delegation, the registry check and the letter fallback; the grammar cases moved to core's `CategoryIconTest` in B3a),
`CategoryPaletteTest`, palette contrast in `ColorContrastTest`, `CategoryGlyphTest`, `BundledSymbolsLicenceTest` and
`CategoryGlyphScreenshotTest` (8 goldens: symbol grids on teal, amber and violet, and the sources page, light and dark),
and `ManifestHardeningTest` checks the EmojiCompat initializer is gone. The importer has its own tests:
`python3 -I -m unittest discover -s tools/material-symbols`.

**Room tests (A4a).** They run on a real in-memory `PftDatabase` (`TestDatabase.create()`, Robolectric's
native SQLite) with `runSuspending { }` around suspend calls, and never mock the DAO. `AuditTriggersTest`
(raw SQL: the database itself refuses UPDATE/DELETE on `audit_entry`), `AuditDaoTest` and
`AuditEntryMapperTest`, `RoomIdentityRepositoryTest`, `RoomAppSettingsRepositoryTest`,
`RoomTransactionRunnerTest` (commit, rollback, nesting, swallowed inner failure),
`IdentityRoomIntegrationTest` and `RoomAuditedWriterIntegrationTest` (core services over Room via
`RoomTestKit`, including injected audit-write failures), `MetaMapperTest` and `RoomEntityRepositoryTest`
(the shared blocks, through the throwaway `SampleThing` entity and `TestDatabase.createWithSample()`),
`RoomAuditColumnsTest` (every column audited or allowlisted, plus tests that the check catches a miss).
`MigrationTest` (in `src/testDebug`, because Robolectric serves the tested variant's merged assets and
the exported schemas are debug-variant assets only) uses `MigrationTestSupport`: `createV1 { seed }`,
then `migrateTo(version, migrations)`, which also fails unless the audit triggers survived; `createAt(version, seed)` builds any
older version (`createV1` is `createAt(1, ...)`). B1c adds `migrate1To2` (the table's columns, the seeded rows, a row insert)
and B3b `migrate2To3` (from a v2 database with an account row: the columns, both indexes, the key, the account row), an
insert test on the migrated table, and the step list check (`1 to 2`, `2 to 3`, `3 to 4`); `migrate3To4` starts from a v3 database with an account row and a top-level category row (the category key is enforced, so a child would need its parent first).
`RoomAccountIntegrationTest` (every field round-trips through `AccountService`, each change with its
audit entry; a failing audit write leaves no row) and `RoomAccountMapperTest` (INR, unknown type, no
value in the message) cover accounts. `RoomCategoryIntegrationTest` (create, sub-category, reorder, archive and `pickable`, delete and
restore, `bySystemKey`, seeding, the unique `system_key`, every field round-tripping, each change with its audit entry, a failing
audit write leaving nothing), `RoomCategoryMapperTest` (colour key, fallbacks, fail-closed kind and system key) and
`RoomCategoryForeignKeyTest` (the dangling-parent insert is refused) cover categories. `RoomTagIntegrationTest` (create, rename, the name rule read from stored rows including archived and restored tags, lifecycle, sorted lists, quote characters and non-ASCII names round-tripping, each change with its audit entry, a failing audit write leaving no row or the old name) covers tags; there is no `RoomTagMapperTest` because the mapper has no branch to test (the integration test round-trips the name). The instrumented `EncryptedMigrationTest` (`pft35`, not in
`check`) migrates an encrypted v1 file to `PftDatabase.VERSION` through `DatabaseFactory`; run it with
`./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=dev.imagh.pft.app.data.EncryptedMigrationTest`.

Robolectric tests use the JUnit 4 runner and execute on the JUnit Vintage engine, because
Robolectric does not run on Jupiter. Robolectric downloads its `android-all` SDK 35 jar from Maven
Central on first use (cached in `~/.m2`); this download is outside Gradle's dependency
verification (see `docs/learnings/README.md`).

**Instrumented tests (A4b).** `src/androidTest` holds only what the JVM cannot run: the Android
Keystore (`DbKeyManagerTest`) and SQLCipher's native library (`EncryptedDatabaseTest`). They are not
part of `check`; run them on the `pft35` emulator (boot recipe in `docs/guides/local-setup.md` and
`docs/learnings`) with `ANDROID_SERIAL=emulator-5554 ./gradlew :app:connectedDebugAndroidTest`. The
runner is `androidx.test:runner` 1.6.2 (same AndroidX Test release as `androidx.test:core` 1.6.1)
plus JUnit 4. They run inside the app's process, so they use their own database name, Keystore alias
and a subdirectory of `noBackupFilesDir`, never the app's real `pft.db` or key.
`EncryptedDatabaseTest` proves the Room 2.7.1 + SQLCipher 4.7.2 pairing: the file is not plain
SQLite, a wrong key and an all-zero key do not open it, a fresh manager and `PftDatabase` reopen it
with the managed key, extra pooled connections are keyed, the audit triggers are installed,
enforced and repaired on the encrypted file, foreign keys are enforced on it (B3b), a damaged file is never deleted, and a backup taken
while rows are only in the `-wal` restores with it, encrypted and mode `0600`.
The JVM tests of the key logic are `DbKeyManagerJvmTest` (real AES-GCM with an in-memory KEK) and
`DbKeyManagerWiringTest` (Robolectric). `PreMigrationBackupTest` (plain files) covers the backup
rules; `DatabaseFactoryTest` (Robolectric, a framework open helper in SQLCipher's place) pins the
order of events and that the key array handed over stays intact; `DatabaseStartupTest`,
`DataUnavailableScreenTest` and `MainActivityStartupTest` (Robolectric has no Keystore, so it
exercises the real fail-closed path end to end, asynchronously) cover the screen and that nothing is created;
`DatabaseStartupTest` also asserts that each outcome logs exactly one `pft` line, matching the
pattern above, with no path, file name or exception text. A5a adds `LogEventTest`, `LogEventShapeTest`,
`SafeLogTest` (Robolectric, `ShadowLog`) and `LoggingScanTest`.

A5b adds `AppServicesTest` (production wiring on a sub-millisecond base clock: written equals read),
`AppContainerTest` (opens off the main thread, a slow open does not block the caller, concurrent
callers open once and share the result, a cancelled caller does not abort the open, key unavailable
fails closed, a throwing open propagates and is never cached as Ready, millisecond clock),
`LaunchViewModelTest` (the four launch states over real Room), `PftRootTest` and, in the debug tests, `PftRootReadyTest`, and
`MainActivityOpeningTest` (with the held-open `SlowOpeningApplication`: the first frame is composed,
`FLAG_SECURE` and text-free before the open finishes). `RoomTestKit` now delegates to `AppServices`
with its public surface unchanged. Waiting for a ViewModel from a Robolectric test: use IO-dispatched
ViewModel work (as `LaunchViewModel`) and a plain blocking wait or `compose.waitUntil`, never
`runBlocking` on the main thread against a main-dispatched coroutine.
