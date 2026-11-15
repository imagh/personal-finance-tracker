package dev.imagh.pft.app.log

import dev.imagh.pft.app.security.DbKeyUnavailable
import java.time.Duration

/**
 * Something worth a log line, as a typed value that **cannot hold text** (PRD SEC-5, CLAUDE.md
 * §3.7). Every field of every subclass is an `Int`, `Long`, `Boolean`, an enum or a [Duration], so
 * there is nowhere to put an amount, a payee, a message, a name, a path or a token. `LogEventShapeTest`
 * enforces this by reflection, and [SafeLog] accepts nothing but a [LogEvent].
 *
 * Add a subclass only in the change that first emits it, and add a sample to `LogEventShapeTest`
 * (its exhaustive `when` fails to compile until you do).
 */
sealed interface LogEvent {
    /**
     * The line that is written: `<snake_case_name> key=VALUE ...`. The name and keys are fixed
     * lowercase identifiers; values are numbers or enum codes, so the whole line matches
     * `^[a-z_]+( [a-z_]+=[A-Z0-9_]+)*$`.
     */
    fun line(): String

    /** The encrypted database opened and migrated at [schemaVersion] in [took]. */
    data class DbOpened(
        val schemaVersion: Int,
        val took: Duration,
    ) : LogEvent {
        override fun line(): String =
            render("db_opened", "schema" to LogValue(schemaVersion), "ms" to LogValue(took.toMillis()))
    }

    /** The database key cannot be produced, for [reason]; the app fails closed. */
    data class DbKeyUnavailable(
        val reason: DbKeyUnavailable.Reason,
    ) : LogEvent {
        override fun line(): String = render("db_key_unavailable", "reason" to LogValue(reason))
    }

    /**
     * The stored identity is inconsistent (the current-device setting points at a device or user
     * that does not exist); the app fails closed instead of treating the database as new. It carries
     * no fields: nothing about the identity, not even its ids, is logged.
     */
    data object IdentityCorrupt : LogEvent {
        override fun line(): String = render("identity_corrupt")
    }

    /**
     * First-run setup finished: the user, the device and the current-device setting were saved. It
     * carries no fields, so neither the name nor the phone label (nor any id) can reach logcat.
     */
    data object SetupCompleted : LogEvent {
        override fun line(): String = render("setup_completed")
    }

    /**
     * First-run setup could not be saved (an unexpected failure; the user is asked to try again). It
     * carries no fields: not the exception, its message or its class name, and not the names typed.
     */
    data object SetupFailed : LogEvent {
        override fun line(): String = render("setup_failed")
    }

    /**
     * A list action ([action]: archive, unarchive, delete or restore) on a [kind] of record failed
     * unexpectedly; the list is left as it was and the user is asked to try again. Enums only (SEC-5).
     */
    data class EntityActionFailed(
        val kind: EntityKind,
        val action: EntityAction,
    ) : LogEvent {
        override fun line(): String =
            render("entity_action_failed", "kind" to LogValue(kind), "action" to LogValue(action))
    }
}

/**
 * The kind of record an entity log event is about. An entry is added with the screen that first
 * emits an event for it (categories and tags come with their lists, slice B).
 */
enum class EntityKind {
    /** An account. */
    ACCOUNT,
}

/**
 * The list action an [LogEvent.EntityActionFailed] is about. An entry is added only when something
 * emits it (a category's reorder comes with the categories list).
 */
enum class EntityAction {
    /** Hide a record from new entries, keeping it in the archived section. */
    ARCHIVE,

    /** Make an archived record active again. */
    UNARCHIVE,

    /** Delete a record (a tombstone). */
    DELETE,

    /** Undo a delete. */
    RESTORE,
}

/**
 * Renders `name k=v k=v`. Private to this file so only [LogEvent] subclasses can format a line.
 * Values are numbers or enum codes, never text: the type [Any] is not accepted, so a `String` does
 * not compile.
 */
private fun render(
    name: String,
    vararg fields: Pair<String, LogValue>,
): String = (listOf(name) + fields.map { (key, value) -> "$key=${value.text}" }).joinToString(" ")

/** A log value: a number or an enum code, the only things a line may carry. */
@JvmInline
private value class LogValue private constructor(
    val text: String,
) {
    constructor(number: Number) : this(number.toString())
    constructor(code: Enum<*>) : this(code.name)
}
