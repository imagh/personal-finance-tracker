package dev.imagh.pft.app.data

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermissions

/**
 * [PreMigrationBackup] (INT-5): a copy of the still-encrypted database before a migration runs.
 * The files are opaque bytes to it, so plain byte arrays stand in for the encrypted database; that
 * the copy of a real SQLCipher file stays encrypted and readable is the instrumented
 * `EncryptedDatabaseTest`'s job.
 */
class PreMigrationBackupTest {
    @get:Rule
    val temp = TemporaryFolder()

    private val database by lazy { File(temp.root, "databases/pft.db").also { it.parentFile!!.mkdirs() } }
    private val wal by lazy { File(database.path + "-wal") }
    private val backups by lazy { File(temp.root, "no_backup/backups") }
    private val versionFile by lazy { File(temp.root, "no_backup/pft_schema_version") }

    private fun backup(currentVersion: Int) = PreMigrationBackup(database, backups, versionFile, currentVersion)

    private fun backupNames(): List<String> = backups.list().orEmpty().toList()

    private fun withMarker(version: Int) {
        versionFile.parentFile!!.mkdirs()
        versionFile.writeText("$version\n")
    }

    @Test
    fun `an older stored version copies the database to pre-migration-v(old) byte for byte`() {
        database.writeBytes(byteArrayOf(9, 8, 7, 6))
        withMarker(1)

        val copy = backup(currentVersion = 2).backUpIfOlder()

        copy.shouldNotBeNull().name shouldBe "pre-migration-v1.db"
        copy.readBytes() shouldBe byteArrayOf(9, 8, 7, 6)
        backupNames() shouldBe listOf("pre-migration-v1.db")
    }

    @Test
    fun `the same version copies nothing and creates no directory`() {
        database.writeBytes(byteArrayOf(1))
        withMarker(2)

        backup(currentVersion = 2).backUpIfOlder().shouldBeNull()

        backups.exists() shouldBe false
    }

    @Test
    fun `a stored version newer than the app copies nothing, because Room refuses a downgrade by itself`() {
        database.writeBytes(byteArrayOf(1))
        withMarker(3)

        backup(currentVersion = 2).backUpIfOlder().shouldBeNull()

        backups.exists() shouldBe false
    }

    @Test
    fun `a fresh install has nothing to protect`() {
        backup(currentVersion = 2).backUpIfOlder().shouldBeNull()

        backups.exists() shouldBe false
    }

    @Test
    fun `a database with no recorded version is backed up as v0, because its version is unknown`() {
        database.writeBytes(byteArrayOf(5))

        backup(currentVersion = 1).backUpIfOlder().shouldNotBeNull().name shouldBe "pre-migration-v0.db"
    }

    @Test
    fun `an unreadable version record is treated as unknown, never as up to date`() {
        database.writeBytes(byteArrayOf(5))
        versionFile.parentFile!!.mkdirs()
        versionFile.writeText("not a number")

        backup(currentVersion = 2).lastOpenedVersion().shouldBeNull()
        backup(currentVersion = 2).backUpIfOlder().shouldNotBeNull().name shouldBe "pre-migration-v0.db"
    }

    @Test
    fun `the write-ahead log is copied with the database, because committed rows may only be in it`() {
        database.writeBytes(byteArrayOf(1, 1))
        wal.writeBytes(byteArrayOf(2, 2, 2))
        withMarker(1)

        backup(currentVersion = 2).backUpIfOlder()

        backupNames() shouldContainExactlyInAnyOrder listOf("pre-migration-v1.db", "pre-migration-v1.db-wal")
        File(backups, "pre-migration-v1.db-wal").readBytes() shouldBe byteArrayOf(2, 2, 2)
    }

    @Test
    fun `an empty write-ahead log is not copied`() {
        database.writeBytes(byteArrayOf(1))
        wal.writeBytes(ByteArray(0))
        withMarker(1)

        backup(currentVersion = 2).backUpIfOlder()

        backupNames() shouldBe listOf("pre-migration-v1.db")
    }

    @Test
    fun `only the newest three backups are kept, with their write-ahead logs`() {
        database.writeBytes(byteArrayOf(1))
        wal.writeBytes(byteArrayOf(2))
        for (old in 1..4) {
            withMarker(old)
            backup(currentVersion = old + 1).backUpIfOlder()
        }

        backupNames() shouldContainExactlyInAnyOrder
            listOf(2, 3, 4).flatMap { listOf("pre-migration-v$it.db", "pre-migration-v$it.db-wal") }
    }

    @Test
    fun `a backup that already exists for that version is kept, never overwritten`() {
        database.writeBytes(byteArrayOf(1))
        withMarker(1)
        backup(currentVersion = 2).backUpIfOlder()
        // The migration committed but the app died before recording the new version: the database
        // now holds migrated data, and the next start must not replace the true v1 copy with it.
        database.writeBytes(byteArrayOf(7, 7))

        backup(currentVersion = 2).backUpIfOlder().shouldBeNull()

        File(backups, "pre-migration-v1.db").readBytes() shouldBe byteArrayOf(1)
    }

    @Test
    fun `the key file is never copied, and no backup contains its bytes`() {
        val keyFile = File(temp.root, "no_backup/pft_db_key.bin")
        keyFile.parentFile!!.mkdirs()
        keyFile.writeBytes("WRAPPED-KEY-BYTES".toByteArray())
        database.writeBytes(byteArrayOf(1))
        wal.writeBytes(byteArrayOf(2))
        withMarker(1)

        backup(currentVersion = 2).backUpIfOlder()

        backupNames().none { it.contains("key") } shouldBe true
        backups.listFiles().orEmpty().none { it.readBytes().toString(Charsets.ISO_8859_1).contains("WRAPPED") } shouldBe
            true
    }

    @Test
    fun `backups are private to the app, not readable by group or others`() {
        database.writeBytes(byteArrayOf(1))
        wal.writeBytes(byteArrayOf(2))
        withMarker(1)

        backup(currentVersion = 2).backUpIfOlder()

        PosixFilePermissions.toString(Files.getPosixFilePermissions(backups.toPath())) shouldBe "rwx------"
        backups.listFiles().orEmpty().forEach {
            PosixFilePermissions.toString(Files.getPosixFilePermissions(it.toPath())) shouldBe "rw-------"
        }
    }

    @Test
    fun `a looser existing directory is tightened`() {
        database.writeBytes(byteArrayOf(1))
        withMarker(1)
        backups.mkdirs()
        Files.setPosixFilePermissions(backups.toPath(), PosixFilePermissions.fromString("rwxr-xr-x"))

        backup(currentVersion = 2).backUpIfOlder()

        PosixFilePermissions.toString(Files.getPosixFilePermissions(backups.toPath())) shouldBe "rwx------"
    }

    @Test
    fun `a leftover temp file from a crashed copy is replaced, and no temp file remains`() {
        database.writeBytes(byteArrayOf(1))
        withMarker(1)
        backups.mkdirs()
        File(backups, "pre-migration-v1.db.tmp").writeBytes(byteArrayOf(99, 99, 99, 99, 99))

        backup(currentVersion = 2).backUpIfOlder()

        backupNames() shouldBe listOf("pre-migration-v1.db")
        File(backups, "pre-migration-v1.db").readBytes() shouldBe byteArrayOf(1)
    }

    @Test
    fun `a backup that cannot be made fails closed, with no partial backup and no path in the message`() {
        database.writeBytes(byteArrayOf(1))
        withMarker(1)
        backups.parentFile!!.mkdirs()
        backups.writeBytes(byteArrayOf(0)) // a plain file where the directory must go

        val failure = shouldThrow<PreMigrationBackupFailed> { backup(currentVersion = 2).backUpIfOlder() }

        failure.message.orEmpty() shouldNotContain temp.root.path
        failure.cause.shouldBeNull()
        backup(currentVersion = 2).lastOpenedVersion() shouldBe 1
    }

    @Test
    fun `a database that cannot be read fails closed and leaves no backup that looks complete`() {
        database.mkdirs() // a directory where the file should be: copying it fails
        withMarker(1)

        shouldThrow<PreMigrationBackupFailed> { backup(currentVersion = 2).backUpIfOlder() }

        backupNames().filter { it.endsWith(".db") } shouldBe emptyList()
    }

    @Test
    fun `recordOpened stores the current version, and a later start sees it`() {
        backup(currentVersion = 3).lastOpenedVersion().shouldBeNull()

        backup(currentVersion = 3).recordOpened()

        backup(currentVersion = 5).lastOpenedVersion() shouldBe 3
        versionFile.readText().trim() shouldBe "3"
    }

    @Test
    fun `after recordOpened the same version is not backed up again`() {
        database.writeBytes(byteArrayOf(1))
        withMarker(1)
        backup(currentVersion = 2).backUpIfOlder()
        backup(currentVersion = 2).recordOpened()

        backup(currentVersion = 2).backUpIfOlder().shouldBeNull()
    }
}
