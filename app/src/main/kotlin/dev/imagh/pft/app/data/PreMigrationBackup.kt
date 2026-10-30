package dev.imagh.pft.app.data

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.PosixFilePermissions

/**
 * The backup could not be made, so the database must not be opened (and migrated). Fail closed:
 * migrating without the safety copy is never the fallback. Like `DbKeyUnavailable`, it carries a
 * fixed message and the failure's class name only, never the cause (an I/O exception's message
 * names a path).
 */
class PreMigrationBackupFailed(
    causeType: String?,
) : Exception("Pre-migration backup failed" + if (causeType == null) "" else " ($causeType)")

/**
 * A safety copy of the **still-encrypted** database file, taken before Room runs a migration
 * (PRD INT-5, plan decision 9).
 *
 * **When.** [backUpIfOlder] runs before the database is opened, so before any migration. It copies
 * when the schema version recorded by the last successful open ([lastOpenedVersion]) is older than
 * [currentVersion]; equal (nothing to migrate), newer (a downgrade, which Room refuses by itself) or
 * no database yet (a fresh install) copy nothing. [recordOpened] stores the current version once the
 * database has opened, so a migration that failed is backed up again on the next start.
 *
 * **Name.** `backups/pre-migration-v{n}.db`, where `n` is the **old** schema version, the one the
 * copy holds (`pre-migration-v1.db` is the database as it was at version 1, before the migration to
 * 2). `v0` means "version unknown": a database exists but no version was ever recorded (for
 * example a store from before this file existed, or an unreadable record), which is treated as the
 * oldest possible, never as up to date. A backup that already exists for a version is kept, never
 * replaced: if the app died after a migration committed but before [recordOpened], the database
 * already holds migrated data and must not overwrite the true copy.
 *
 * **Retention.** The newest [KEEP] versions; older ones are deleted after a new copy succeeds.
 *
 * **Consistency (WAL).** The database runs in write-ahead-log mode, so committed rows can sit in
 * `pft.db-wal` until a checkpoint. The database is not open at backup time (and SQLCipher cannot
 * checkpoint without the key), so the `-wal` file is copied next to the copy as
 * `pre-migration-v{n}.db-wal` and written first; the `.db` file's rename is the commit point, so a
 * backup is complete exactly when its `.db` exists. To restore, put both files back under the
 * database's name and let SQLCipher open them with the key; it replays the log.
 *
 * **Secrecy.** The copy is as encrypted as the original (same key), so the backup is useless
 * without the wrapped key, which is **never copied**: only the database and its log are read. The
 * directory is `rwx------` and the files `rw-------`, set explicitly rather than left to the umask.
 * It lives under `noBackupFilesDir`, which backup and device transfer never include.
 *
 * **Why the version is a file, not a setting.** It must be known *before* the database opens, which
 * is when migrations run, and a SQLCipher file cannot be read without opening it, so it cannot live
 * in the database (CLAUDE.md §3.5 sends runtime-varying values to the database, but this one is
 * needed before the database exists). It is one non-secret integer in [versionFile]; if it is lost
 * or damaged the effect is an extra backup, never a missing one.
 *
 * @param databaseFile `pft.db`; its `-wal` sibling is found next to it.
 * @param backupDir where the copies go.
 * @param versionFile where the last-opened schema version is recorded.
 * @param currentVersion the schema version this build opens, [PftDatabase.VERSION].
 */
class PreMigrationBackup(
    private val databaseFile: File,
    private val backupDir: File,
    private val versionFile: File,
    private val currentVersion: Int,
) {
    /**
     * Copies the database if it is about to be migrated, and prunes old copies.
     *
     * @return the new copy, or null when nothing was copied (see the class doc).
     * @throws PreMigrationBackupFailed if a copy was needed and could not be made; no partial copy
     *   is left that looks complete.
     */
    fun backUpIfOlder(): File? {
        val stored = lastOpenedVersion() ?: UNKNOWN_VERSION
        val target = File(backupDir, backupName(stored))
        // Nothing to protect (fresh install), nothing to migrate, or the true copy already exists.
        if (!databaseFile.exists() || stored >= currentVersion || target.exists()) return null
        try {
            createPrivateDirectory()
            val walCopy = File(backupDir, backupName(stored) + WAL_SUFFIX)
            val wal = File(databaseFile.path + WAL_SUFFIX)
            if (wal.length() > 0) copyPrivately(wal, walCopy) else walCopy.delete()
            copyPrivately(databaseFile, target)
            prune()
        } catch (e: IOException) {
            failed(e)
        }
        return target
    }

    /** Records that the database opened (and migrated) at [currentVersion]. */
    fun recordOpened() {
        versionFile.parentFile?.mkdirs()
        val temp = File(versionFile.path + TEMP_SUFFIX)
        FileOutputStream(temp).use { out ->
            out.write("$currentVersion\n".toByteArray())
            out.fd.sync()
        }
        Files.move(temp.toPath(), versionFile.toPath(), StandardCopyOption.ATOMIC_MOVE)
    }

    /** The schema version of the last successful open, or null if none is recorded or it is unreadable. */
    fun lastOpenedVersion(): Int? {
        if (!versionFile.isFile || versionFile.length() > MAX_VERSION_FILE_BYTES) return null
        val version = versionFile.readText().trim().toIntOrNull()
        return version?.takeIf { it >= 1 }
    }

    /** Throws [PreMigrationBackupFailed], naming only the failure's class (never its message). */
    private fun failed(cause: IOException): Nothing = throw PreMigrationBackupFailed(cause.javaClass.simpleName)

    private fun createPrivateDirectory() {
        Files.createDirectories(backupDir.toPath(), PosixFilePermissions.asFileAttribute(DIR_PERMISSIONS))
        // An existing directory keeps whatever mode it had; tighten it.
        Files.setPosixFilePermissions(backupDir.toPath(), DIR_PERMISSIONS)
    }

    /** Copies [source] to [target] through a private temp file, so [target] is complete or absent. */
    private fun copyPrivately(
        source: File,
        target: File,
    ) {
        val temp = File(backupDir, target.name + TEMP_SUFFIX)
        try {
            temp.delete()
            Files.createFile(temp.toPath(), PosixFilePermissions.asFileAttribute(FILE_PERMISSIONS))
            FileInputStream(source).use { input ->
                FileOutputStream(temp).use { out ->
                    input.copyTo(out)
                    out.fd.sync()
                }
            }
            Files.move(temp.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE)
        } catch (e: IOException) {
            temp.delete()
            throw e
        }
    }

    /** Keeps the newest [KEEP] backups (highest versions) and deletes the rest with their logs. */
    private fun prune() {
        val versions =
            backupDir
                .list()
                .orEmpty()
                .mapNotNull {
                    NAME
                        .matchEntire(it)
                        ?.groupValues
                        ?.get(1)
                        ?.toIntOrNull()
                }.sortedDescending()
        versions.drop(KEEP).forEach { version ->
            File(backupDir, backupName(version)).delete()
            File(backupDir, backupName(version) + WAL_SUFFIX).delete()
        }
    }

    companion object {
        /** How many backups are kept (INT-5). */
        const val KEEP = 3

        /** The directory under `noBackupFilesDir` that holds the copies. */
        const val DIR_NAME = "backups"

        /** The file in `noBackupFilesDir` that records the last-opened schema version. */
        const val VERSION_FILE_NAME = "pft_schema_version"

        private const val UNKNOWN_VERSION = 0
        private const val WAL_SUFFIX = "-wal"
        private const val TEMP_SUFFIX = ".tmp"
        private const val MAX_VERSION_FILE_BYTES = 32L
        private val NAME = Regex("""pre-migration-v(\d+)\.db""")
        private val DIR_PERMISSIONS = PosixFilePermissions.fromString("rwx------")
        private val FILE_PERMISSIONS = PosixFilePermissions.fromString("rw-------")

        /** The backup's file name for a database that was at schema [version]. */
        fun backupName(version: Int): String = "pre-migration-v$version.db"

        /** The app's backup: `pft.db`, copies in `noBackupFilesDir/`[DIR_NAME], at [PftDatabase.VERSION]. */
        fun forApp(context: Context): PreMigrationBackup =
            PreMigrationBackup(
                databaseFile = context.getDatabasePath(PftDatabase.NAME),
                backupDir = File(context.noBackupFilesDir, DIR_NAME),
                versionFile = File(context.noBackupFilesDir, VERSION_FILE_NAME),
                currentVersion = PftDatabase.VERSION,
            )
    }
}
