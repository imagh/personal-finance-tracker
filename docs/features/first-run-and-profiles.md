# First run and profiles

Status: **DRAFT.** The setup screen and its ViewModel are built (plan A5e) but nothing opens them yet;
the navigation that shows Setup on the first launch, and the Home it lands on, arrive in A5f, which
also finalises this document. Treat everything below as the intended behaviour.
PRD: L-8 (who is using this device). Plan:
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
- **There is no way back from this screen**, because nothing is behind it.
- **Your names stay on the phone.** They are stored in the encrypted database, never sent anywhere
  and never written to the app's logs. The log notes only that setup was completed.
- **Each phone has its own profile** until sync arrives (M1c, M2).

## Behaviour details

- The profile (one user, one device, and the "this is the current device" setting) is saved in one
  step, together with its audit entries (three in all).
- If a profile already exists when Continue is tapped (an unlikely race), the screen treats setup
  as done instead of showing an error, because a profile exists either way.
- Typed text is not kept if the app is killed mid-setup; you type the two short fields again.

## Not yet (A5f)

- Opening to Setup on a fresh install and to Home afterwards; the placeholder Home that shows
  "<name> · <phone>"; back from Home leaving the app instead of returning to Setup.

## Where it lives

`app/.../ui/setup/` (`SetupViewModel`, `SetupScreen`, `SetupRoute`); validation is core's
`Validation.requireName`; saving is core's `IdentityService.initialise`. See `app/README.md`.
