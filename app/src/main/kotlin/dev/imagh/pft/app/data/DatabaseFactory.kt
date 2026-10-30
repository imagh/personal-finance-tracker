package dev.imagh.pft.app.data

import android.content.Context
import androidx.sqlite.db.SupportSQLiteOpenHelper
import dev.imagh.pft.app.security.DbKeyManager
import dev.imagh.pft.app.security.DbKeyUnavailable
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

/**
 * Opens the encrypted app database, `pft.db` (PRD SEC-1, plan decision 9): gets the key from
 * [DbKeyManager], takes the [PreMigrationBackup], then builds the database with
 * [PftDatabase.create] (still the only builder, so the migrations and the audit-trigger callback
 * are attached) over SQLCipher's open helper, and opens it **now**, so migrations and any wrong-key
 * failure happen here, at a known point, rather than on the first query.
 *
 * Order, which is the fail-closed contract:
 * 1. [DbKeyManager.useKey] runs first. On a fresh install it creates the key before `pft.db` exists,
 *    so the database can only ever be created with a persisted key; if the key cannot be produced it
 *    throws [DbKeyUnavailable] and **nothing else happens**: no database is created over existing
 *    data, no file is deleted, no new key is made.
 * 2. [PreMigrationBackup.backUpIfOlder], so a failed backup stops the open before the database is
 *    touched ([PreMigrationBackupFailed]).
 * 3. The database is opened and the opened schema version recorded.
 *
 * **The key array is not wiped after the open, on purpose.** [DbKeyManager.useKey] zero-fills its
 * array when the block ends, but SQLCipher's `SupportOpenHelperFactory` keeps the very array it was
 * given and uses it again for every connection it opens later (it re-keys each pooled connection, and
 * on first run it creates the file with whatever the array holds when the first connection opens). A
 * wiped array would therefore key later connections with 32 zero bytes. So the factory is handed a
 * **copy** that lives as long as the database does (the process, for the app's one database), and
 * is wiped only if the open fails. Native SQLCipher keeps its derived key in memory for an open
 * database anyway, so this adds one array to a residual that cannot be removed.
 *
 * Opening takes SQLCipher's key derivation plus any migration, so call it off the UI thread once the
 * app has more than a placeholder screen (A5's container).
 *
 * @param name the database file name.
 * @param openHelperFactoryFor builds the open helper from the key; SQLCipher's in the app, and a
 *   framework helper in the JVM tests (Robolectric has no SQLCipher native library).
 */
class DatabaseFactory internal constructor(
    private val context: Context,
    private val name: String,
    private val keys: DbKeyManager,
    private val backup: PreMigrationBackup,
    private val openHelperFactoryFor: (ByteArray) -> SupportSQLiteOpenHelper.Factory,
) {
    /**
     * Returns the open database.
     *
     * @throws DbKeyUnavailable if the key cannot be produced (nothing was created or changed).
     * @throws PreMigrationBackupFailed if a needed backup could not be made (the database was not opened).
     */
    fun open(): PftDatabase =
        keys.useKey { key ->
            val keyForDatabase = key.copyOf()
            var database: PftDatabase? = null
            var opened = false
            try {
                backup.backUpIfOlder()
                database = PftDatabase.create(context, name, openHelperFactoryFor(keyForDatabase))
                database.openHelper.writableDatabase
                backup.recordOpened()
                opened = true
                database
            } finally {
                if (!opened) {
                    database?.close()
                    keyForDatabase.fill(0)
                }
            }
        }

    companion object {
        /** The app's database: `pft.db`, its key, its backups, and SQLCipher's open helper. */
        fun forApp(context: Context): DatabaseFactory =
            DatabaseFactory(
                context = context,
                name = PftDatabase.NAME,
                keys = DbKeyManager.forApp(context),
                backup = PreMigrationBackup.forApp(context),
                openHelperFactoryFor = ::sqlCipherOpenHelperFactory,
            )

        /**
         * SQLCipher's open helper factory over [key]; it keeps that array (see the class doc), so
         * the caller must not wipe it while the database is open. Loads the native library on first
         * use, deferred to here so that a key failure never needs it.
         */
        internal fun sqlCipherOpenHelperFactory(key: ByteArray): SupportSQLiteOpenHelper.Factory {
            System.loadLibrary("sqlcipher")
            return SupportOpenHelperFactory(key)
        }
    }
}
