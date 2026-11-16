package dev.imagh.pft.app.ui.common

import androidx.annotation.StringRes
import dev.imagh.pft.R
import dev.imagh.pft.app.log.EntityAction

/**
 * The fixed "Couldn't ... Try again." message a list shows in a snackbar when this action fails
 * unexpectedly. It names the action and nothing else: never a record's name and never exception
 * text (SEC-5). Shared by the accounts and categories lists (and tags, B5b).
 */
@StringRes
internal fun EntityAction.failureRes(): Int =
    when (this) {
        EntityAction.ARCHIVE -> R.string.entity_failed_archive
        EntityAction.UNARCHIVE -> R.string.entity_failed_unarchive
        EntityAction.DELETE -> R.string.entity_failed_delete
        EntityAction.RESTORE -> R.string.entity_failed_restore
        EntityAction.REORDER -> R.string.entity_failed_reorder
    }
