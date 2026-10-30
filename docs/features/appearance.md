# Appearance: colour palettes, dynamic colour and background

Status: palettes and dynamic colour are built in the M1a premium-look slice DS6b and the background
in DS6c; the settings screen that lets you change them arrives in AP1, so until then the app always
uses the defaults below (the debug catalogue can already try every combination). The header glow,
type pairing and fonts (DS6d to DS6i) will be added to this page as they land. PRD: UI-11, UI-12,
UI-8, UI-18.
Technical design: [design-system.md](../architecture/design-system.md) and the plan
[2026-10-30-m1a-premium-look.md](../plans/2026-10-30-m1a-premium-look.md).

## What it does for you

- **You choose the colour mood of the app.** There are four palettes: **Graphite** (a grey with a
  platinum accent), **Midnight** (navy with a brass accent by day and champagne by night),
  **Ivory** (warm paper with an ink-navy accent by day and a soft periwinkle by night) and
  **Aubergine** (deep plum, with a champagne accent at night).
- **Light and dark are chosen separately.** You can use Ivory in the daytime and Midnight at
  night. The palette used is the one for the mode you are in: with the app set to follow the phone,
  that is the phone's light or dark setting; if you force light or dark, it is that mode's palette.
- **Dynamic colour is optional and off by default.** If you turn it on, the app takes its colours
  from your phone's wallpaper (Material You) and ignores the palette you chose. Turning it off
  brings your palette back.
- **The background is tonal or flat.** With **Tonal** (the default) the screen is a tinted
  background and your lists and settings sit on lighter, softly rounded panels ("islands") with a
  hairline edge, so the panels read as raised without shadows. In dark mode a faint light line
  runs along the top of each panel. The panels are never white. With **Flat** the screen and the
  panels are the plain surface colours, as before. Both follow the palette you picked (or the
  wallpaper colours, with dynamic colour on).
- **The window matches the screen.** The bar behind the app (what shows briefly while the app
  opens, or during a back swipe) uses the same background colour as the screen, so there is no
  flash of a different colour. The colour it shows before the app has drawn is the default
  palette's; every other palette is within a few shades of it.
- **Money always looks the same.** Spending, income and warning colours (green, red and amber) are
  fixed. They do not change with the palette or with dynamic colour, so an amount never changes
  meaning because you changed the look.

## Defaults

| Setting | Default |
|---|---|
| Light palette | Graphite |
| Dark palette | Graphite |
| Dynamic colour | off |
| Background | Tonal |

Once the settings are stored (AP1), a value the app does not recognise (for example one saved by a
newer version) will be read as the default for that setting alone, so a choice can never leave the
app unreadable.

## How the accent is kept from looking like a status

The accent colour appears only on things you can tap: the add button, the selected tab, buttons,
switches and the focus ring. It is never used for a status or a progress meter. Brass and
champagne are close in colour to the warning amber, so a status such as **Unverified** or **80%**
is always shown as a pill with a word on it, and the pill's colours are the same under every
palette. The accent also stays well away from the green and red of money (at least 40 degrees
of hue, checked by a test for every palette in light and dark).

## Behind the scenes

- The palette names (`GRAPHITE`, `MIDNIGHT`, `IVORY`, `AUBERGINE`) are what will be stored when the
  settings screen arrives, so they are permanent: a name is never renamed or removed, only added
  to.
- The background choice is stored as `TONAL` or `FLAT` (permanent names, like the palettes) when the
  settings screen arrives.
- Every pair of colours is checked for contrast in each palette (text at 4.5:1, the accent
  against the background at 3:1) so each palette stays readable.
- The background and panel colours of each palette are the values the owner approved in the
  mockup; with dynamic colour they are derived from the wallpaper scheme (two surface steps), and
  tests check they stay readable and close to the default window colour.
- The colours were generated once with Google's Material colour tools and the exact values the
  owner approved in the mockup are pinned in place (each palette's source file records them).
