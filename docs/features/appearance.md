# Appearance: colour palettes, dynamic colour, background and type

Status: palettes and dynamic colour are built in the M1a premium-look slice DS6b, the background
in DS6c, the header glow in DS6d, the type pairing in DS6g, the sans fonts in DS6h and DS6i and Manrope and Bricolage Grotesque
as choices in K2 of the Cobalt redesign; the settings screen that lets you change them arrives in AP1,
so until then the app always uses the defaults below (the debug catalogue can already try every
combination).
PRD: UI-11, UI-12, UI-13, UI-15, UI-16, UI-2, UI-8, UI-18.
Technical design: [design-system.md](../architecture/design-system.md) and the plan
[2026-10-30-m1a-premium-look.md](../plans/2026-10-30-m1a-premium-look.md).

## What it does for you

- **You choose the colour mood of the app.** There are five palettes: **Graphite** (a grey with a
  platinum accent), **Midnight** (navy with a brass accent by day and champagne by night),
  **Ivory** (warm paper with an ink-navy accent by day and a soft periwinkle by night),
  **Aubergine** (deep plum, with a champagne accent at night) and **Cobalt** (a vivid cobalt blue
  on a cool white background by day, a periwinkle blue on a blue-tinted ink at night). Cobalt is
  the one the app starts with.
- **Light and dark are chosen separately.** You can use Ivory in the daytime and Midnight at
  night. The palette used is the one for the mode you are in: with the app set to follow the phone,
  that is the phone's light or dark setting; if you force light or dark, it is that mode's palette.
- **Dynamic colour is optional and off by default.** If you turn it on, the app takes its colours
  from your phone's wallpaper (Material You) and ignores the palette you chose. Turning it off
  brings your palette back.
- **The background is tonal or flat.** With **Tonal** (the default) the screen is a tinted
  background and your lists and settings sit on lighter, softly rounded panels ("islands") with a
  hairline edge, so the panels read as raised without shadows. In dark mode a faint light line
  runs along the top of each panel. The panels are never white, except in Cobalt by day, where they are white on a cool,
  slightly blue background. With **Flat** the screen and the
  panels are the plain surface colours, as before. Both follow the palette you picked (or the
  wallpaper colours, with dynamic colour on).
- **The header glow is optional and off by default.** Turn it on and the top of each screen gets a
  soft tint of the palette's accent family behind the title (the "wash"), strongest at the very top
  and fading out well before the big figure on a home-style screen, so an amount is never drawn on a
  tinted area. It is capped, so the title and buttons stay readable, and it fades away as you scroll
  the header down to the small bar. Screens without a big figure get the tint over the top 60% of
  the header. With dynamic colour the tint comes from your wallpaper colours.
- **The type pairing is hero only or editorial.** With **Hero only** (the default) the big
  figure is set in a serif and everything else is in the plain sans, which is how the app has always
  looked. With **Editorial** the serif also sets the titles (screen titles, the title on the small
  bar, section titles) and the amount on the add and detail screens, so a screen reads like a
  printed page. Tab labels, body text, labels and the amounts in list rows stay sans, so anything you
  tap or scan quickly stays plain. Each serif size gets the cut of the font made for that size, so a
  small title is sturdier and a large heading finer. The digits stay tabular, so columns of amounts
  line up. If you pick "Same as text" as the figure font, Editorial is all sans.
- **The text font is Figtree by default, with four choices.** Every piece of text that is not in
  the serif (body text, labels, tab labels, list rows and, unless you choose Editorial, all titles)
  is set in **Figtree**, a friendly geometric sans that ships inside the app, so the app looks the
  same on every phone. You can switch to **Plus Jakarta Sans**, a second sans that also ships inside
  the app (a little wider and more modern), to **Manrope**, a third bundled sans (clean and slightly
  rounded, the Cobalt look's text font), or to **System font**, which is whatever your phone uses
  (Roboto on a Pixel, the maker's font on other phones). Amounts keep their digits the same width in
  Figtree, in Plus Jakarta Sans, in Manrope and in the serif, so columns of amounts line up; in the system font
  that depends on the phone's font. Ordinary text digits (a date, a count) keep their natural widths.
  All the bundled fonts are free software under the SIL Open Font License, and their licence texts ship
  with the app.
- **The figure font can be Bricolage Grotesque.** Besides the bundled serif, the system serif and
  "Same as text", the big figure can be set in **Bricolage Grotesque**, a bold display sans bundled with
  the app. In it the big figure is bold and a little smaller (34sp rather than the serif's 48sp), and it
  shrinks to fit a long amount the same way, from 34sp down. Its digits are tabular and lining, and the
  rupee sign and the true minus come from the font itself. With **Editorial** the same font also sets
  the titles. Bricolage Grotesque is the default figure font (the Cobalt look); the serif stays a choice.
- **The window matches the screen.** The bar behind the app (what shows briefly while the app
  opens, or during a back swipe) uses the same background colour as the screen, so there is no
  flash of a different colour. The colour it shows before the app has drawn is the default
  palette's (Cobalt's); the app repaints it to the chosen palette as soon as it has drawn.
- **Money always looks the same.** Income, over-budget and warning colours (green, red and amber) are
  fixed. They do not change with the palette or with dynamic colour, so an amount never changes
  meaning because you changed the look.

## Defaults

| Setting | Default |
|---|---|
| Light palette | Cobalt |
| Dark palette | Cobalt |
| Dynamic colour | off |
| Background | Tonal |
| Header glow | off |
| Type pairing | Hero only |
| Text font | Manrope |
| Figure font | Bricolage Grotesque |

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

Money is coloured by what it means, not by how much of it there is. **Spending is never red**: an
expense is drawn in the ordinary text colour in a list row, a total and the big figure on Home, and
the minus sign and the word "Expense" say what it is. Income is green. Red is kept for the two things
that are actually wrong: an account whose balance is below zero, and a budget that is over its limit;
amber marks a budget close to its limit. A net figure (a day's total, the month's net) is also the
plain text colour, with a `+` or a minus sign, since a loss is not an alarm. On the blue balance card
the month's spend is white, and its pence fade a little only while the figure is large; once a long
amount has shrunk to a small size they are solid so they stay easy to read.

## Behind the scenes

- The palette names (`GRAPHITE`, `MIDNIGHT`, `IVORY`, `AUBERGINE`) are what will be stored when the
  settings screen arrives, so they are permanent: a name is never renamed or removed, only added
  to.
- The background choice is stored as `TONAL` or `FLAT` (permanent names, like the palettes) when the
  settings screen arrives.
- The type pairing is stored as `HERO_ONLY` or `EDITORIAL` (permanent names) when the settings
  screen arrives. A third value (all serif) can be added later without a migration. Tests check each
  serif role's weight and optical size straight from the font settings, that the other roles stay
  sans, and that the system serif and "Same as text" get no variation settings.
- The text font is stored as `FIGTREE`, `PLUS_JAKARTA_SANS`, `MANROPE` or `SYSTEM` (permanent names; `MANROPE`
  was added in K2) and the figure font as `BUNDLED_SERIF`, `BUNDLED_GROTESQUE`, `SYSTEM_SERIF` or `TEXT`
  (`BUNDLED_GROTESQUE` added in K2), when the settings screen arrives; an imported font is added later without a migration. Figtree is one variable
  font file (62,712 bytes, checked by hash) with the weights 400, 500 and 600 declared (each drawn one step
  heavier, at 500, 600 and 700, because the bundled sans fonts read too light and narrow on a phone, K9; the
  system sans, the serif and Bricolage Grotesque are not shifted), because its own
  default weight is lighter than any text style asks for; Plus Jakarta Sans is the same (176,288
  bytes, checked by hash, default weight 400 but the three weights are still declared); Manrope is the same
  (164,700 bytes, default weight 200, so its three weights matter most) and Bricolage Grotesque is one file
  (408,496 bytes) pinned on three axes: weight, optical size (the figure's size) and width 100. Neither
  font declares a Reserved Font Name (a test asserts it for Bricolage). Tests check each declared weight's axis, that
  the system font choice gives the platform default, that the serif roles stay Newsreader, that amounts
  measure equal widths for different digits (and that the same check fails without tabular figures, or with
  proportional ones for Manrope, whose default digits are already tabular), that both new fonts have the `tnum`
  feature and their own glyphs for the rupee sign and the true minus (read from the font files), that the
  Bricolage figure is bold at 34sp and shrinks only through the steps at or below 34sp, that the defaults did
  not change, and that all five bundled fonts (1,263,860 bytes) stay under 3 MiB.
- The header glow is stored as `true` or `false` when the settings screen arrives. Its tint colours
  are the values the owner approved in the mockup (`pc`); tests check that the title, the buttons and
  the money colours stay at least 4.5:1 against the tint at its strongest in every palette, and that
  the pixels on the big figure's top edge are plain background.
- Every pair of colours is checked for contrast in each palette (text at 4.5:1, the accent
  against the background at 3:1) so each palette stays readable.
- The background and panel colours of each palette are the values the owner approved in the
  mockup; with dynamic colour they are derived from the wallpaper scheme (two surface steps), and
  tests check they stay readable and close to the default window colour.
- The colours were generated once with Google's Material colour tools and the exact values the
  owner approved in the mockup are pinned in place (each palette's source file records them).
