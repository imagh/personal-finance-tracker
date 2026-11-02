# First run and profiles

Status: **Built** (plan A5, finished in A5f). PRD: L-8 (who is using this device). Plan:
[2026-11-01-m1a-a5-first-run-setup.md](../plans/2026-11-01-m1a-a5-first-run-setup.md).

## What it does for you

- **The first time you open the app it asks two things:** your name, and a name for this phone. The
  phone's name starts as the phone's model (for example "Pixel 8") and you can change it. Your name
  starts empty.
- **Why it asks.** Every entry you add is marked with who added it and from which phone, so a
  shared household ledger always shows whose entry it is. That mark is set once, here.
- **Continue checks both names.** A name cannot be empty or only spaces, and cannot be longer than
  100 characters. If one is wrong, the message appears under that field only ("Required" or
  "At most 100 characters") and nothing is saved. The app never shortens what you typed: if it is
  too long, you shorten it. Spaces around a name are dropped when it is saved.
- **Tapping Continue twice saves once.** While it saves, Continue is greyed out.
- **Then you land on Home.** Home is a placeholder for now: its header says "Home" and, underneath,
  who is using this phone ("Akash · Pixel 8"), and it says that accounts and transactions arrive in
  the next update. This is how you can see the mark that every later entry will carry.
- **Every later launch goes straight to Home.** You are never asked again. Pressing Back (or the
  back gesture) on Home leaves the app: Setup is not behind it, so it can never be reached by going
  back.
- **There is no way back from Setup**, because nothing is behind it.
- **Your names stay on the phone.** They are stored in the encrypted database, never sent anywhere
  and never written to the app's logs. The log notes only that setup was completed.
- **Each phone has its own profile** until sync arrives (M1c, M2).
- **If the stored profile is inconsistent**, the app does not set up a new one. It shows the "Your
  data can't be opened" screen with the code `IDENTITY_CORRUPT` and changes nothing (see
  [data-protection.md](data-protection.md)).

## Behaviour details

- The profile (one user, one device, and the "this is the current device" setting) is saved in one
  step, together with its audit entries (three in all).
- If a profile already exists when Continue is tapped (an unlikely race, for example a second
  start of setup), the screen treats setup as done instead of showing an error, because a profile
  exists either way. What you typed is **not** saved in that case: Home shows the profile that was
  already there.
- Rotating the phone on Setup keeps what you typed. If the app is killed mid-setup, the typed text
  is lost and you type the two short fields again.
- In a debug build only, Home has an extra header button, "Design catalogue", for checking the
  look of the app. Release builds do not contain it.

## Where it lives

`app/.../ui/setup/` (`SetupViewModel`, `SetupScreen`, `SetupRoute`), `app/.../ui/nav/` (`PftNavHost`,
`Routes`: Setup on a fresh install, Home otherwise) and `app/.../ui/home/` (`HomeViewModel`,
`HomeScreen`, `HomeRoute`). Validation is core's `Validation.requireName`; saving is core's
`IdentityService.initialise`. See `app/README.md`. The test that proves the flow end to end is
`SetupFlowTest`.
