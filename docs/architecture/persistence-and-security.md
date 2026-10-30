# Persistence and security

How the app database is encrypted, where its key lives, what happens when the key is lost, and how
a migration is protected. Built in slice A4b (plan `docs/plans/2026-10-28-m1a-core-ledger.md`,
decision 9; PRD SEC-1, INT-2, INT-5). Tables and conventions are in [data-model.md](data-model.md);
the app-lock model joins this file in slice H1.

## The pieces

```
Application.startup (lazy)                       dev.imagh.pft.app.PftApplication
  DatabaseStartup.open(DatabaseFactory.forApp)   data/DatabaseStartup, data/DatabaseFactory
    DbKeyManager.useKey { key -> ... }           security/DbKeyManager (wraps/unwraps the key)
      PreMigrationBackup.backUpIfOlder()         data/PreMigrationBackup
      PftDatabase.create(ctx, "pft.db", SQLCipher factory(copy of key))   data/PftDatabase
      database.openHelper.writableDatabase       migrations + audit triggers run here
      PreMigrationBackup.recordOpened()
  Ready(db)  ->  the app          KeyUnavailable(reason)  ->  KeyUnavailableScreen
```

## SQLCipher

`pft.db` is a Room database over SQLCipher 4.7.2 (`net.zetetic:sqlcipher-android`, which bundles
its own SQLite). Room talks to it through `SupportOpenHelperFactory`, so the DAOs, migrations and
triggers are unchanged. The key is 32 random bytes handed to SQLCipher as its passphrase. The file
has no readable header and none of its content is visible; it runs in WAL mode, so `pft.db-wal` and
`pft.db-shm` sit next to it (the log is encrypted too).

The Room 2.7.1 and sqlcipher-android 4.7.2 pairing is proven on the emulator by the instrumented
`EncryptedDatabaseTest` (see the app README for how to run it): the file does not start with
`SQLite format 3\0`, a wrong key and an all-zero key do not open it, and a fresh `DbKeyManager`
plus a fresh `PftDatabase` reopen it with the managed key.

**Bundled SQLite.** sqlcipher-android 4.7.2 carries SQLite **3.49.1** (SQLCipher community 4.7.0).
OWASP dependency-check cannot see inside the native library, so its report says nothing about the
engine. SQLite before 3.50.2 is affected by CVE-2025-6965 (and 3.49.1 by a few later SQLite CVEs,
for example in FTS5), all of which need an attacker able to run crafted SQL. The app only runs its
own static, parameterised statements (message text is bound as a parameter, never concatenated),
so they are not reachable today; the pin still lags. sqlcipher-android 4.10.0 bundles SQLite 3.50.4.
Moving up is an owner decision (it also needs the Room pairing test re-run); see the A4b entry in
`docs/learnings/README.md`.

## The key and the KEK

- The **database key** is 32 bytes from `SecureRandom`, created on first run.
- It is stored only **wrapped**: AES-256-GCM under a **key-encryption key (KEK)** that lives in the
  Android Keystore under the alias `pft_db_kek` (hardware-backed where the device has it). The raw
  key exists in memory only while the database is open.
- The wrapped key is the file `noBackupFilesDir/pft_db_key.bin`. `noBackupFilesDir` is excluded
  from cloud backup and device transfer by definition (and the manifest sets `allowBackup=false`
  and data-extraction rules that exclude every domain).
- The KEK is **not user-authentication-bound** (`setUserAuthenticationRequired(false)`) and does not
  require an unlocked device. This is deliberate: M1b's SMS receiver must write transactions while
  the phone is locked and the app lock is on. App lock (H1) is a UI gate; at-rest protection comes
  from SQLCipher plus the hardware-backed KEK. The trade-off: anyone who can run code as the app
  can get the key, which is the same as for any app-private secret; an attacker with only the file
  (a copied data partition, a backup) gets nothing, because the KEK never leaves the Keystore.

### Wrapped-key file format, version 1

```
offset  size  field
0       4     magic "PFTK"
4       1     format version (1)
5       1     IV length (12)
6       12    AES-GCM IV, chosen by the cipher
18      48    AES-GCM ciphertext of the 32-byte key, followed by the 16-byte tag
```

Exactly 66 bytes. The first 6 bytes are also the GCM additional authenticated data, so an edited
header fails authentication. Decoding never guesses: any other length, magic, version or IV length
is "malformed". A later format gets version 2 and a migration; version 1 is never silently
reinterpreted.

The file is written to a temp file, synced, renamed over the final name (atomic) and the directory
is then synced.

## Fail closed

The decision is made inside `DbKeyManager` from what is on disk, never from a caller's flag.

| On disk | What happens |
|---|---|
| no key file, no `pft.db` | First run: create the KEK, create and wrap the key, write the file, read it back, hand it out |
| key file, valid, KEK present | Unwrap and hand out |
| key file, wrong length/magic/version | `DbKeyUnavailable(KEY_FILE_MALFORMED)` |
| key file, KEK missing from the Keystore (reinstall, factory reset, restored from backup) | `KEK_MISSING` |
| key file, KEK present but the Keystore fails or the KEK was invalidated | `KEK_UNUSABLE` |
| key file edited, or wrapped by a different KEK | `KEY_FILE_AUTH_FAILED` (GCM tag mismatch) |
| `pft.db` exists, key file does not | `KEY_FILE_MISSING` (a new key would orphan the data) |
| key file cannot be read or written | `KEY_FILE_IO` |

On any of these the app shows `KeyUnavailableScreen` (plain explanation, a warning not to clear
storage or uninstall, and the fixed reason code). It **never** generates a replacement key,
creates a new database over existing data, or deletes a file to recover. There is no "reset"
button. `DbKeyUnavailable` carries a fixed reason and a class name only: no path, and the
underlying exception is not chained, because Keystore and I/O messages name paths and providers.

**Residual risk: a power cut before the directory sync.** On first run the key file is renamed into
place and the directory is then fsynced. If power is lost between the rename and the directory
sync, the file's directory entry may not survive. By then no database exists yet (the key is created
before `pft.db`), so the next start simply creates a fresh key and database. The only way the
entry can be lost *after* `pft.db` exists is a failure of the storage itself; in that case the
rule above applies and the app ends in `KEY_FILE_MISSING`, never in a new key over existing data.

**Open item for M1b: credential-encrypted storage.** `noBackupFilesDir` (like `getDatabasePath`)
is credential-encrypted storage: it is unreadable after a reboot until the user first unlocks the
phone (Direct Boot). The SMS receiver must not assume it can open the database before that first
unlock; M1b has to either wait for `ACTION_USER_UNLOCKED` or queue to device-protected storage. Not
needed in M1a (the only entry point is the launcher activity).

## The key array and SQLCipher (a trap)

`DbKeyManager.useKey` zero-fills its array when the block ends. SQLCipher's
`SupportOpenHelperFactory(byte[])` does **not** copy that array: it keeps the reference, hands it
to the open helper, and the helper uses it **lazily**, at the first open and again for every
connection it opens afterwards (each pooled reader in WAL mode is re-keyed with it). Passing the
managed array straight in would therefore key later connections, and on a first run the creation of
the database file itself, with 32 zero bytes: a database "encrypted" with a publicly known key.

`DatabaseFactory` therefore:
1. opens the database **eagerly**, inside the key block, so migrations and key failures happen at a
   known point;
2. gives SQLCipher its **own copy** of the key, which lives as long as the database (the process,
   for the app's one database) and is wiped only if the open fails.

Wiping that copy after a successful open was tried and breaks every later connection with
`SQLiteNotADatabaseException: file is not a database`; `EncryptedDatabaseTest.laterConnectionsAreKeyedToo`
guards the assumption. Native SQLCipher keeps its derived key in memory for an open database anyway,
so this adds one array to a residual that cannot be removed. The all-zero-key test shows that a
database created through `DatabaseFactory` does not open with 32 zero bytes.

## Pre-migration backup (INT-5)

Before Room runs a migration, `PreMigrationBackup` copies the **still-encrypted** database file to
`noBackupFilesDir/backups/pre-migration-v{n}.db`, where `n` is the **old** schema version (the
version the copy holds). It keeps the newest three. The last-opened schema version is recorded in
`noBackupFilesDir/pft_schema_version` (a single integer) after each successful open.

- **Why a file and not a setting.** The version must be known before the database opens, and a
  SQLCipher file cannot be read without opening it, so it cannot be in the database (CLAUDE.md §3.5
  puts runtime-varying values in the database, but this one is needed before the database exists).
  It is non-secret; if it is lost or damaged the effect is an extra backup (`v0`, "unknown"), never
  a missing one.
- **WAL.** The database is not open at backup time and SQLCipher cannot checkpoint without the
  key, so `pft.db-wal` is copied alongside as `pre-migration-v{n}.db-wal`, written first; the `.db`
  rename is the commit point. To restore, put both files back under the database's name and open
  with the key; SQLCipher replays the log. On the emulator a force-stopped app really leaves tens of
  KB of committed data in the `-wal` file, so a copy of the `.db` alone would be stale.
- **Never overwritten, never the key.** An existing backup for a version is kept (a crash after a
  migration committed but before the version was recorded must not replace the true copy). Only the
  database and its log are read: the wrapped key file is never copied.
- **Private.** The directory is `rwx------` and the files `rw-------`, set explicitly and checked on
  the emulator. It is under `noBackupFilesDir`, outside backup and device transfer. A backup is as
  encrypted as the original and useless without the wrapped key and the Keystore KEK.
- **Fail closed.** If a needed backup cannot be made, `PreMigrationBackupFailed` ends the start
  before the database is opened (and migrated).
- **When nothing is copied.** Fresh install, equal versions, or a stored version newer than the app
  (Room refuses a downgrade by itself).

Migrations on an encrypted database get their instrumented test with the first real migration (B1,
`migrate1To2` against `SupportOpenHelperFactory`): `Migrations.ALL` is empty at schema v1.

## Audit triggers

`audit_entry` is append-only: `BEFORE UPDATE`/`BEFORE DELETE` triggers abort with a fixed message
(`AuditTriggers`). `PftDatabase.create` attaches a callback that re-installs them on every open,
so a lost or replaced trigger is repaired. On the encrypted file this is covered by
`EncryptedDatabaseTest` (installed, enforced for UPDATE and DELETE, repaired after a drop and
reopen). Tamper-resistance against someone holding the key is out of scope.

## What logs may contain

Nothing in this layer logs key material, amounts, payees, message text or account identifiers.
`DbKeyUnavailable` and `PreMigrationBackupFailed` carry fixed codes. SQLCipher itself logs one line
per connection, `Database keying operation returned:<code>` (the status, never the key), at INFO.

## Corruption handling

SQLCipher's `DefaultDatabaseErrorHandler` deletes a corrupt plain SQLite file, but returns without
deleting when the library has a codec (`hasCodec()`, true for SQLCipher). `EncryptedDatabaseTest`
damages the header and asserts the open fails and the file is left in place, so "never delete files
to recover" holds. A damaged or unopenable database is not turned into a screen yet (it ends the
start); that and a user-facing restore from `backups/` are later work.
