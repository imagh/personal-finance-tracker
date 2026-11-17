package dev.imagh.pft.app.ui.common

/** How a form's save ended, as far as the form is concerned. */
internal enum class SaveOutcome {
    /** The write went through. */
    SAVED,

    /** The record was deleted (or is gone) since the form loaded: a stale screen, not an error. */
    VANISHED,

    /** The write failed unexpectedly; the form says "try again" and keeps what was typed. */
    FAILED,
}
