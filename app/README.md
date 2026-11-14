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
  the exception, its message or its class name), both emitted by `SetupViewModel`.
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
| `PftTheme.type` | `PftTypography`: `amountHero` (48/60, display font, `tnum`+`lnum`), `amountLarge` (32/40; sans, or the display font under `EDITORIAL`; `tnum`+`lnum`), `amountRow` (16/24, always sans, `tnum`) |
| `PftTheme.shapes`, `MaterialTheme.shapes` | `PftShapes.glyph` (category badge, 30% corners); M3 shapes are 6/10/16/24/32dp |
| `PftTheme.spacing` | `PftSpacing`: `xxs` 2, `xs` 4, `sm` 8, `md` 12, `lg` 16, `gutter` 20, `xl` 24, `xxl` 32, `huge` 48 dp |
| `MaterialTheme.typography` | The M3 scale: **by default every role is in the sans text family** (`sansFamily(ThemePrefs.sansFont)`: the bundled Figtree or Plus Jakarta Sans, or `FontFamily.Default` for `SansFont.SYSTEM`); under `TypePairing.EDITORIAL`, `display*`, `headline*`, `titleLarge` and `titleMedium` are in the display font, each cut for its own size and weight (`titleSmall`, `body*`, `label*` stay sans). `headlineLarge` 32/40 semibold and `titleLarge` 22/28 medium are the header titles. |
| `PftTheme.motion` | `PftMotion`: springs `snappy` (0.30 s, bounce 0.10), `smooth` (0.45 s, 0), `gentle` (0.60 s, 0), `bouncy` (0.50 s, 0.25) as `SpringSpec<Float>`; `navEnter`/`navExit`/`navPopEnter`/`navPopExit` (25% slide + fade on `smooth`); `PRESSED_SCALE` 0.97; `smoothOffset` is `smooth` over `IntOffset` for slide transitions (rolling digits). A constant, not a CompositionLocal |
| `iosSpring<T>(durationMillis, bounce)` | Converts a SwiftUI-style spring to a `SpringSpec<T>` (`stiffness = (2π/duration)²`, `dampingRatio = 1 − bounce`); throws `IllegalArgumentException` for bounce outside `[0, 1)` or duration ≤ 0 |
| `Modifier.pressScale()` | Scales to 0.97 with `snappy` while pressed; observes pointers only (does not consume). Put it before `clickable` |
| `PftSymbol` (`key`, `res`), `PftSymbol.fromKey(key)` | The icon registry: 92 Material Symbols Rounded vector drawables (`res/drawable/sym_<key>.xml`; chrome: `back`, `forward`, `add`, `close`, `check`, `search`, `filter`, `overflow`, `chevron_left`, `chevron_right`, `expand_more`, `expand_less`, `edit`, `delete`, `undo`, `lock`, `settings`, `calendar`, ...; the rest are category symbols). **Keys are permanent** (persisted in `category.icon_key`): add, never rename or remove; `src/test/resources/pft-symbol-keys.txt` pins them. `fromKey` is exact-match and null for anything unknown. Draw one with `Icon(painterResource(symbol.res), contentDescription = ..., tint = ...)`: the drawable is black. `androidx.compose.material.icons.*` is banned in app code (detekt `ForbiddenImport`). The full list and the how-to are in `docs/architecture/design-system.md` |
| `PftTheme.palette`, `CategoryPalette[key]`, `categoryPalette(dark)`, `CategoryColors(container, content)` | The 12 named category hues (`rose, coral, amber, olive, emerald, teal, sky, blue, indigo, violet, magenta, slate`), light and dark, for glyph badges only (never money). An unknown or null name gives `slate`. Content is at least 4.5:1 on container |
| `GlyphSize { Chip 28, Row 40, Hero 56 }` | The badge sides, fixed dp |
| `ThemePrefs(mode: ThemeMode, dynamicColor: Boolean, lightPalette: ThemePalette, darkPalette: ThemePalette, background: BackgroundStyle, headerGlow: Boolean, displayFont: DisplayFont, typePairing: TypePairing, sansFont: SansFont)`, `ThemePrefs.Default` | The user's appearance. `Default` is `SYSTEM`, dynamic colour **off** (opt-in), `GRAPHITE` for both `lightPalette` and `darkPalette`, `TONAL` background, header glow **off** (`headerGlow = false`, DS6d), bundled serif, `HERO_ONLY` type pairing (DS6g), `FIGTREE` sans (DS6h) |
| `ThemePalette { GRAPHITE, MIDNIGHT, IVORY, AUBERGINE }` | The palettes a user can pick, light and dark independently. **The names are persisted by AP1: never rename or remove one, never depend on the order.** `ThemePalette.colors(dark)` (internal) is its `PaletteColors` |
| `DisplayFont { BUNDLED_SERIF, SYSTEM_SERIF, TEXT }` | What the display family is (the roles that use it are chosen by `TypePairing`); `TEXT` is "same as text". Only the bundled serif gets variation settings |
| `TypePairing { HERO_ONLY, EDITORIAL }` | Which roles use the display family (UI-15, DS6g): `HERO_ONLY` (default) is the hero only, today's look (owner decision Q10); `EDITORIAL` adds `amountLarge`, `display*`, `headline*`, `titleLarge` and `titleMedium`. `displayFont = TEXT` with `EDITORIAL` is all sans. **The names are persisted by AP1: never rename or remove one**; a third value (all serif) can be added later without a migration. `ThemePrefsTest` pins the names |
| `SansFont { FIGTREE, PLUS_JAKARTA_SANS, SYSTEM }` | The sans text family (UI-2, UI-16, DS6h, DS6i): `FIGTREE` (the default) is the bundled Figtree, `PLUS_JAKARTA_SANS` the bundled Plus Jakarta Sans, `SYSTEM` the platform default. **The names are persisted by AP1: never rename or remove one**; AP2 adds `CUSTOM`, without a migration. `ThemePrefsTest` pins the names and the default. `ThemePrefs.sansFont` carries it |
| `sansFamily(SansFont)` (internal) | `FIGTREE` and `PLUS_JAKARTA_SANS` are each one `FontFamily` of three `Font(R.font.figtree, ...)` / `Font(R.font.plus_jakarta_sans, ...)`, weights 400, 500 and 600, each with its own `wght` axis (Figtree's default is 300 and Plus Jakarta Sans's 400; the axis is set explicitly and every weight a role uses must be declared; `PftTypographyTest` checks this). `SYSTEM` is `FontFamily.Default`. Reference a font only as `R.font.<name>` here: release shrinking strips a font no reachable code names |
| `newsreader(weight, size)` (internal) | A `FontFamily` of the bundled Newsreader pinned to `weight` on `wght` and cut for `size` on `opsz` (clamped to 6 to 72). Built only for the serif roles of the chosen pairing; replaces the single `NewsreaderFamily` (hero: weight 400, 48sp) |
| `ThemeMode { SYSTEM, LIGHT, DARK }` | `SYSTEM` follows the system night mode |
| `resolveColorScheme(prefs, dark, context)` | Dynamic (Material You) scheme when `prefs.dynamicColor` (opt-in; the palette fields are then ignored), else the scheme of `prefs.darkPalette` in dark or `prefs.lightPalette` in light |
| `GraphitePalette.light` / `.dark` (`PaletteColors(scheme)`, internal) | The default scheme, Graphite + platinum: generated offline with material-color-utilities 0.3.0 from custom tonal palettes, the owner-approved mockup values pinned over the generated roles (provenance and every override are in its KDoc), committed as constants. Used whenever dynamic colour is off and Graphite is chosen, and in screenshot tests. It replaces the ink-indigo `BrandColors`, which is deleted. Canvas, island and wash join `PaletteColors` in DS6c and DS6d |
| `MidnightPalette`, `IvoryPalette`, `AuberginePalette` (`.light` / `.dark`, internal) | The other three curated palettes (DS6b), generated and pinned the same way as Graphite; each KDoc records its provenance and every override |
| `MoneyColors`, `moneyColors(dark)` | Fixed green/red/amber tokens for amounts and status. **Never derived from the accent**; the same in the dynamic and the Graphite scheme. Positive balances are neutral by owner decision (`MoneyTone` in `ui/common` maps meaning to these tokens) |

The bundled serif is Newsreader (`res/font/newsreader.ttf`, OFL-1.1, unmodified upstream; licence in
`assets/licenses/newsreader-OFL.txt`; provenance and hash in `docs/architecture/design-system.md`).
The default sans is Figtree (`res/font/figtree.ttf`, OFL-1.1, unmodified upstream, no Reserved Font Name;
licence in `assets/licenses/figtree-OFL.txt`; provenance and hash in the same table). The second sans option is
Plus Jakarta Sans (`res/font/plus_jakarta_sans.ttf`, OFL-1.1, unmodified upstream, no Reserved Font Name; licence in
`assets/licenses/plus-jakarta-sans-OFL.txt`). `BundledFontTest` pins all three, and `FontBudgetTest` keeps all of
`res/font` (690,664 B for the three) within 3 MiB.
`TokenSheet` (internal) shows every token; it feeds the Studio preview, the debug catalogue and the
screenshot goldens.

Rules: never draw money text on `surfaceVariant`; use `background`, `surface` or a
`surfaceContainer*` step (dark `negative` on `surfaceVariant` is 4.23:1, below 4.5). Do not wrap an M3 component just to restyle it (style comes from the theme); raw colour
values and design `dp`/`sp` live only in `ui/theme` (detekt's `MagicNumber` is relaxed there for
that reason). Colour is never the only cue: every amount carries a sign and every status a word.

### `ui/common` (shared components, DS3, DS4, DS5 and A5d)

| Symbol | What it is |
|---|---|
| `CollapsingHeaderScaffold(title, style, subtitle, hero, heroCaption, period, actions, bottomBar, floatingActionButton) { LazyListScope }` | The screen frame: M3 `TopAppBarState` + `exitUntilCollapsedScrollBehavior` (snap on `PftTheme.motion.smooth`), expanded height `clamp(0.38 x window, 200dp, 360dp)`, 64dp collapsed bar, collapsed-only under 560dp of window height, expanded while the list fits. Owns the `LazyColumn`. Edge-to-edge (`safeDrawing`); `bottomBar` is `imePadding()`-aware |
| `HeaderStyle { Dashboard, Standard }` | `Dashboard`: `titleLarge` title about 12% down plus the hero, caption and period stepper in the lower (thumb) band. `Standard`: `headlineLarge` title centred in the middle, no hero. All titles are sans, centred and read-only (Q10) |
| `HeaderIconButton(symbol: PftSymbol, label: String, onClick)` | The only control a header's `actions` may hold (UI-14): an M3 `IconButton` around a 24dp registry symbol tinted `onSurfaceVariant`, announced as a button named `label` (a blank label throws `IllegalArgumentException`), touch target at least 48dp, never text. `actions` stays a slot (an overflow `DropdownMenu` needs a `Box` anchor); the rule is in the scaffold KDoc and `CollapsingHeaderScaffoldTest` checks it on the samples |
| `glowAlpha(distance, end)`, `Density.glowEnd(heroTop, bodyHeight)`, `glowStrength(collapsedFraction)`, `MAX_GLOW_ALPHA` (DS6d, `ui/common/HeaderGlow.kt`, internal) | The pure rules of the header glow: 72% at the top edge, about 28% at 38% of the reach, exactly 0 from the end on; the reach is 24dp above the hero (0.6 x the header without one); the strength is `1 - collapsedFraction` |
| `Modifier.headerGlow(glow: Color?, heroTop: () -> Float?, collapsedFraction: () -> Float)` (internal) | Draws the glow behind the header: a radial gradient of `glow` from the top-centre, stops from `glowAlpha`. A null `glow` adds nothing. Both lambdas are read in the draw phase only, so a scroll frame redraws but never recomposes. `CollapsingHeaderScaffold` applies it with `PftTheme.surfaces.glow`; screens do not call it |
| `HeaderPeriod(onPrevious, onNext)` | Dashboard only: draws the chevron stepper (symbols, labelled "Previous period" / "Next period") and makes a sideways swipe of more than 48dp on the expanded header step the period |
| `BottomActionBar(primary, modifier, secondary)`, `BottomBarAction(label, enabled, onClick)` | One full-width primary button, or an outlined secondary plus the primary on the end side; at least 56dp tall; pads by `safeDrawing` (so it clears the IME and navigation bar) |
| `MoneyText(amount: Paise, semantic: MoneySemantic, modifier, size: MoneySize = Row, animate = false)` | One amount: `PaiseFormat.format` (sign-free, from `:core:money`) with a U+2212 or `+` prepended here, never reformatted. Tone from `moneyTone` mapped to a `PftTheme.money` token (neutral is `onSurface`); the spoken description names the direction first ("Expense, ₹250.00"). A negative amount is rejected (`IllegalArgumentException`, thrown in composition) for every semantic except `Balance`: callers pass magnitudes. `size = Hero` steps the figure down through `HeroSizeSteps` (48, 40, 34, 28, 24, 20, 16, 12sp) to the largest that fits one line; if even 12sp does not fit it wraps rather than cutting digits. `animate` rolls the digits (`PftMotion.smooth`); a hero too wide for even 12sp is plain wrapping text, without the roll |
| `MoneySemantic { Income, Expense, Transfer, Balance, Budget(state) }`, `BudgetState { UNDER, AT_THRESHOLD, OVER }`, `MoneySize { Hero, Large, Row }` | What an amount means and which `PftTheme.type.amount*` style it uses. `Row` is the only size where an expense is neutral (Q2); `Balance` is the only semantic that may be negative |
| `MoneyTone { Neutral, Positive, Negative, Warning }`, `moneyTone(semantic, amount, inRow)` | The pure mapping: income Positive; expense Negative in totals and Neutral in rows (Q2); balance Neutral unless negative (Q3); budget Positive, Warning or Negative by state |
| `StatusChip(label, tone: MoneyTone, modifier)` | A labelled pill (Unverified, Overdue, Paused, Over, 80%). The label is required (blank is rejected): colour is never the only cue. Colours are the money container tokens, or `surfaceContainerHighest` for Neutral (`chipColors`) |
| `GroupedSection(modifier) { row(onClick?) { ... } }`, `GroupedSectionDefaults.listItemColors()` | An inset (20dp gutter), rounded (`large`) island of rows filled with `PftTheme.surfaces.island` (TONAL adds a one-pixel edge, lit along the top in dark mode), with a one-pixel divider, inset 16dp from the start, between rows only (N rows, N-1 dividers). A row with `onClick` is clickable (`Role.Button`) and at least 56dp tall. Give M3 `ListItem`s `listItemColors()` (transparent container) |
| `CategoryGlyph(source: GlyphSource, name, colourKey, modifier, size = Row, onClick = null)` | The category/account badge: a soft-square tile in a palette colour holding a symbol, one emoji or a letter. One image node announced as `name` (never the key); fixed dp size; emoji clipped to one line; with `onClick` it is a button (48dp touch target, press scale) |
| `GlyphSource { Symbol(PftSymbol), Emoji(text), Letter(letter) }` (`Letter` holds one code point as a string, with a `Char` constructor), `GlyphSource.fromIconKey(iconKey, name)`, `GlyphSource.letterFor(name)` | The UI model of `category.icon_key`. `fromIconKey` is the **boundary parser** (untrusted input), but it does **not own the grammar** (B3a): it delegates to core's `CategoryIcon.parse` (`sym:<key>` with key `[a-z0-9_]{1,40}`, or `emoji:<value>` of 1 to 16 UTF-16 units, well-formed, no whitespace, control or invisible format characters, ZWJ and tag characters allowed; core also enforces it when an icon is written) and adds the one check that is the app's: a symbol key must **exist** in the registry (`PftSymbol.fromKey`). Anything else (null, no prefix, oversized, malformed, a well-formed key that is not registered) is `Letter` of the name's first letter or digit by code point (a combining mark or unpaired surrogate never counts, a letter outside the basic plane does), upper-cased (`?` if none); it never throws. `Emoji` itself rejects an invalid value (`IllegalArgumentException`, via core's `CategoryIcon.Emoji.isValid`) |
| `EmptyState(message, modifier, glyph, actionLabel, onAction)` | A centred glyph slot, message and optional tonal-button action; the label and callback must be given together |
| `FieldState(text, error)` with `edit(text)` and `check(rule)`, `FieldCheck(value, state)`, `FieldError { Required, TooLong(max), InvalidLastFour, Duplicate }` with `FieldError.message()` (the one internal `@Composable` that turns an error into its string), `LedgerError.toFieldError()` (A5d, B1b, B3a; `FieldState.kt`, `FieldError.kt`, `LedgerErrorMapping.kt`) | A form field's state as plain data a ViewModel holds. `text` is exactly what was typed and is never trimmed or clipped; `edit` replaces it and clears the error. `check` runs one of **core's own validators** (for example `{ Validation.requireName(it, MAX_NAME_LENGTH) }`) and returns the validated value, or null plus the state with the `FieldError`; it catches only `LedgerError`, and rethrows one that is not about a field. `toFieldError()` and `message()` are exhaustive `when`s (no `else`): a new `LedgerError` case fails compilation until mapped, and a new `FieldError` until it has its string, and non-field errors (`NotInitialised`, `IdentityCorrupt`, the category rules `DepthExceeded`, `KindMismatch`, `HasChildren`, `InvalidOrder`, `InvalidIcon`, `SystemCategoryProtected`, ...) map to null, to be shown as a form-level message. `DuplicateName` maps to `FieldError.Duplicate` ("Already in use", for a category or tag name) |
| `NameField(label, state, onTextChange, modifier, imeAction)` (A5d) | The one name input: a single-line M3 `OutlinedTextField`, words capitalised, error text under it (`FieldError.message()`) and M3 error semantics. **Never truncates or alters the typed text**: 120 typed characters stay 120 and length is reported as `TooLong`, never enforced by clipping. Fills the width between the gutters and adds its own vertical rhythm |
| `EditFormScaffold(title, onPrimary, modifier, subtitle, primaryLabel, saving, onBack) { LazyListScope }` (A5d) | The create/edit frame: a `Standard` `CollapsingHeaderScaffold` with the primary action ("Save" by default, or the caller's, such as "Continue") in a `BottomActionBar`. `saving = true` disables it (a double-tap guard; the ViewModel keeps its own). `onBack` adds a labelled back `HeaderIconButton` (48dp target); null means no Back. **Adds no `imePadding()`**: the scaffold already lifts its bottom bar above the keyboard. Named consumers: Setup (A5e), B2 account edit, B3/B5 forms, D/E/F/G edit screens |

The hero slot is set in `PftTheme.type.amountHero` by default (the serif is the number only). Actions
go in the top row (back, search, filter, overflow; secondary only), add actions in the FAB. Both
components are shared (named consumers in `docs/architecture/code-structure.md`); do not wrap them
to restyle. `HeaderSamples.kt` holds the fake-data samples that feed the previews, the debug
catalogue and `HeaderScreenshotTest`; DS5 adds one file per component (`MoneyTextSamples.kt`,
`StatusChipSamples.kt`, `GroupedSectionSamples.kt`, `EmptyStateSamples.kt`; DS3 adds `CategoryGlyphSamples.kt`; A5d adds `FormSamples.kt`, a live two-field form) for the same three
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
| `PftDatabase` (`VERSION = 2`), `Migrations.ALL` (`MIGRATION_1_2`) | The Room database `pft.db` (`user_profile`, `device`, `app_setting`, `audit_entry`, `account`) with exported schemas in `app/schemas/`; the ordered list of migrations. **`PftDatabase.create(context, name, openHelperFactory)` is the only way to build it** (production and tests; `internal`, so only the app module, in production `DatabaseFactory`, can call it): it registers `Migrations.ALL` and the audit-trigger callback; `DatabaseFactory` only passes SQLCipher's open-helper factory. `name = null` is in-memory |
| `AccountEntity`, `AccountDao`, `AccountMapper`, `RoomAccountRepository` (B1c, `data/account/`) | Table `account`, and core's `AccountRepository` over it: `RoomEntityRepository` plus `allLive()` (`deleted_at IS NULL`, no order; the service sorts). `AccountMapper` always writes `currency = 'INR'` and **fails closed on read** (`IllegalStateException` naming the column, never the value) for another currency or an unknown `type`. No `CHECK` and no index (Room cannot declare a CHECK; see `data-model.md`) |
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
| `AppServices(database, clock, auditLog = RoomAuditLog(..))` | The production object graph over an **open** `PftDatabase`: `ids`, `runner`, `identityRepository`, `accountRepository`, `accounts` (`AccountService`), `settingsRepository`, `recorder`, `identity`, `settings`, `writer`. Does no I/O. `RoomTestKit` builds exactly this, so Room integration tests run the production wiring. The clock must be a whole-millisecond clock (`EpochMillis.clock`); `AppContainer` guarantees that |
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
| `SetupViewModel(identity, defaultDeviceLabel)` + `SetupViewModel.factory(services, defaultDeviceLabel)` | `onNameChange`, `onDeviceLabelChange`, `submit()`. `submit()` checks both fields through `FieldState.check { Validation.requireName(it, LedgerLimits.MAX_NAME_LENGTH) }` (core's rule, no copy here), saves nothing unless both pass, then calls `IdentityService.initialise` on the main dispatcher and logs `setup_completed`. It sets `saving` before launching and ignores calls while `saving` or `done`, so a double tap saves once. `AlreadyInitialised` counts as done. **Any other `Exception` is caught** (a `CancellationException` is rethrown): `saving` resets, `saveFailed` is set, both typed fields are kept exactly as typed, and one field-less `setup_failed` is logged (the exception is dropped on purpose; the catch binds `_`, so detekt's `TooGenericExceptionCaught` needs no suppression). `saveFailed` clears when either field is edited or Continue is tapped again. Retry is safe because `IdentityService.initialise` is one transaction (user, device, setting, three audit rows), so a failed attempt leaves nothing behind. The phone-name prefill is `defaultDeviceLabel` trimmed and cut to 100 |
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
| `Routes` (`SETUP = "setup"`, `HOME = "home"`) | Plain string routes (plan D3): Navigation 2.8's type-safe routes need the kotlinx-serialization compiler plugin, which the project does not use. No arguments, no deep links. The debug catalogue's route (`dev/catalog`) is private to `src/debug/.../DevEntry.kt`, so release has no trace of it |
| `PftNavHost(services, hasIdentity, modifier)` | The `NavHost`. Starts on `HOME` when `hasIdentity`, on `SETUP` otherwise. Setup's `onDone` navigates to `HOME` with `popUpTo(SETUP) { inclusive = true }`, so Back on Home leaves the app. All four transitions are `PftMotion.navEnter/navExit/navPopEnter/navPopExit`. It also calls `devRoutes()` (debug adds the catalogue; release adds nothing) |
| `HomeUiState(userName, deviceLabel, loaded)`, `HomeViewModel(identity)` + `HomeViewModel.factory(services)` | Reads `identity.requireIdentity()` on `viewModelScope` and holds the user and phone name. Home is only reachable once an identity exists, so a missing or inconsistent one here is a bug and is not caught |
| `HomeScreen(state, modifier, actions)` | Stateless: a `CollapsingHeaderScaffold` titled "Home", subtitle "<name> · <phone>" once loaded, one `EmptyState` ("Accounts and transactions arrive in the next update"). `actions` is the header's action slot (only `HeaderIconButton`s, UI-14) |
| `HomeRoute(services, modifier, actions)` | The only place that touches `HomeViewModel` |
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

### Window theme

`res/values/themes.xml` and `res/values-night/themes.xml` (`Theme.Pft`) paint Graphite's canvas
behind the first frame (`pft_window_background`, which must equal `GraphitePalette.*.canvas`; `WindowThemeTest` fails on drift), keep
the system bars transparent and colour the launch splash, so a dark launch does not flash a light
window. `MainActivity` then calls `enableEdgeToEdge()` (after the `FLAG_SECURE` line). The resource
colour is Graphite's canvas whatever the user picked, because the palette is not known before Compose
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
  (schema v2: `user_profile`, `device`, `app_setting`, `audit_entry`, `account`; see
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
`SYSTEM` + dynamic colour off + Graphite for both light and dark + tonal background + no header glow + bundled serif + hero-only type; the settings screen and its `app_setting` storage arrive in AP1, so today
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
scale on 320dp, rolling digits), `StatusChipTest`, `GroupedSectionTest`, `EmptyStateTest` and
`ContentScreenshotTest` (goldens for the four components, `MoneyText` also at 200%). Text fit is asserted with
line metrics (`TextLayoutSupport.kt`), not `hasVisualOverflow`, which is unreliable in Compose 1.7.

A5e adds `SetupViewModelTest` (Robolectric, real Room through `AppServices`; the main looper is idled by `testing/awaitMain`, which also lets the ViewModel's `viewModelScope` run: blank and over-long names, the phone-name error only, a valid save with its three audit rows and the `setup_completed` line, the double-submit guard (a mutation test showed the guard is what keeps a changed second submit out), prefill), `SetupScreenTest` (copy, Continue in the bottom bar with a 48dp `touchBoundsInRoot` target, disabled while saving, error text, typing), `SetupRouteTest` (the real route over a real database) and `SetupScreenshotTest` (six `setup-expanded-*` goldens: light, dark, light at 200% font, light with the name in error, light while saving, light after a failed save; fake data only). Under Compose rules, wait for a ViewModel result with `awaitMain { compose.waitForIdle(); condition }`: `compose.waitUntil` alone did not let the Main-dispatched save finish.

A5f adds `SetupFlowTest` (the M1a acceptance test: the real `AppContainer` over a real Room database, through `PftRoot`; a fresh install shows Setup, Continue lands on "Akash · Pixel 8" with one user, one device, `current_device_id` and three audit rows persisted, system back on Home finishes the activity, a blank name stays on Setup with nothing saved, and when an identity appears while Setup is open Home shows the **existing** one, not what was typed), `LaunchRoutingTest` (an initialised database opens on Home with the stored identity, Setup is not shown (sampled at each poll as Home appears, and at the end), nothing new is written), `HomeViewModelTest`, `HomeScreenTest`, `PftRootTest` (Home or Setup for a ready database), `DevRouteTest` (`src/testDebug`: the "Design catalogue" action opens the catalogue and back returns to Home), `ReleaseHasNoCatalogueTest` (the new `src/testRelease`: no such action, and the catalogue class is absent) and two `ManifestHardeningTest` cases (the predictive-back flag; the launcher is the only intent filter). Mutation checks: removing `popUpTo` failed the back test, and starting always on Setup failed `LaunchRoutingTest`; a release `DevHeaderActions` that drew the button failed `ReleaseHasNoCatalogueTest`. No golden changed.

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
then `migrateTo(version, migrations)`, which also fails unless the audit triggers survived; B1c adds
`migrate1To2` (the table's columns, the seeded rows, a row insert) and the one-step list check.
`RoomAccountIntegrationTest` (every field round-trips through `AccountService`, each change with its
audit entry; a failing audit write leaves no row) and `RoomAccountMapperTest` (INR, unknown type, no
value in the message) cover accounts. The instrumented `EncryptedMigrationTest` (`pft35`, not in
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
enforced and repaired on the encrypted file, a damaged file is never deleted, and a backup taken
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
