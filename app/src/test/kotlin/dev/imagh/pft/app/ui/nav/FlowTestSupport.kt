package dev.imagh.pft.app.ui.nav

import dev.imagh.pft.app.data.PftDatabase

/** The number of rows in [table], read straight from the database (the persisted outcome, not a mock). */
internal fun PftDatabase.rowCount(table: String): Long =
    openHelper.readableDatabase.query("SELECT COUNT(*) FROM $table").use { cursor ->
        cursor.moveToFirst()
        cursor.getLong(0)
    }
