package dev.imagh.pft.app.data

/**
 * Room's eight full-text content-sync triggers (slice C D6), as they are named in `sqlite_master`: before update,
 * before delete, after update and after insert, on each of the two content tables. They exist beside the audit
 * triggers, so a test that lists **every** trigger expects `AuditTriggers.TRIGGER_NAMES + FtsSyncTriggers.NAMES`.
 * A copy of the JVM tests' `AuditedTables.ftsSyncTriggers`, which this source set cannot see.
 */
object FtsSyncTriggers {
    val NAMES: Set<String> =
        listOf("transaction_fts", "transaction_line_fts")
            .flatMap { index ->
                listOf("BEFORE_UPDATE", "BEFORE_DELETE", "AFTER_UPDATE", "AFTER_INSERT")
                    .map { "room_fts_content_sync_${index}_$it" }
            }.toSet()
}
