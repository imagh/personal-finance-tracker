# Data protection: encrypted storage and the "can't open your data" screen

Status: built in M1a slice A4b. PRD: SEC-1 (encryption at rest), INT-5 (a copy before a database
migration). Technical design: [persistence-and-security.md](../architecture/persistence-and-security.md).

## What it does for you

- **Your data is encrypted on the phone.** Everything the app stores (accounts, transactions, the
  audit trail) is in one database file that is unreadable without a key. The key is created the
  first time the app starts and kept in the phone's secure hardware (the Android Keystore). You
  never see it, type it or manage it, and it is not tied to your screen lock, so automatic logging
  of bank messages can keep working while the phone is locked (M1b).
- **Nothing leaves the phone.** The database and its key are excluded from Android's cloud backup
  and phone-to-phone transfer. The app asks for no permissions and has no network access.
- **A safety copy before an upgrade changes the database.** When a new version of the app needs to
  change how data is stored, it first makes a copy of the (still encrypted) database, and keeps the
  last three. The copies are private to the app and encrypted like the original.

## The "Your data can't be opened" screen

If the app cannot get the key that unlocks the database, it stops and shows this screen instead of
the app. This can happen if the phone's secure hardware was reset, the app's key file was
damaged or removed, or after restoring the app's files onto a different phone.

What it shows: a short explanation, a warning, and a code such as `Code: KEK_MISSING`.

What it guarantees:
- **Your data has not been changed or deleted.** The app never creates a new, empty database over
  existing data, never generates a replacement key, and never deletes anything to "recover".
- **There is no reset button**, on purpose. A reset would destroy data that might still be
  recoverable.

What to do: do **not** clear the app's storage or uninstall it (that erases the data for good).
Restart the phone and try again. If it keeps happening, note the code and tell the owner.

| Code | Meaning |
|---|---|
| `KEK_MISSING` | The phone has no key for this app any more (for example after a factory reset or a reinstall) |
| `KEK_UNUSABLE` | The phone's secure hardware refused or failed, or the key was invalidated |
| `KEY_FILE_MALFORMED` | The app's key file is damaged or in an unknown format |
| `KEY_FILE_AUTH_FAILED` | The key file was altered, or was made by a different phone key |
| `KEY_FILE_MISSING` | The database exists but its key file does not |
| `KEY_FILE_IO` | The key file could not be read or written |

The screen shows only this code, never file paths or key details, and (like every screen) it cannot
be screenshotted.

Development devices only: an emulator or test phone that ran a build from before this slice has a
plain, unencrypted database and no key file, so it shows `KEY_FILE_MISSING`. Uninstall the app or clear
its data once before installing this version (see the local setup guide). Nothing like this applies to
real users; there are none with an older database.

## Not built yet

- A restore from the pre-migration copies in the UI (they exist for manual recovery; none are made
  until the first schema change, B1).
- A screen for a database that exists but cannot be opened for another reason, and for a failed
  backup (the app stops without touching the data).
- An encrypted export and import of your data (planned), which is the real answer to "I lost the key".
