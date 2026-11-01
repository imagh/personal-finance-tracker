package dev.imagh.pft.app.ui.startup

import androidx.annotation.StringRes
import dev.imagh.pft.R
import dev.imagh.pft.app.security.DbKeyUnavailable

/**
 * Why [DataUnavailableScreen] is shown. Each cause fixes both what the screen says and the code it
 * shows, and the code can only come from an enum name or a constant here, so the screen cannot be
 * handed user content or exception text: "fixed strings and a fixed code only" is structural, not a
 * convention.
 */
sealed interface DataUnavailableCause {
    /** The fixed code the owner can quote. */
    val code: String

    /** The explanation paragraph for this cause. */
    val explanation: Int
        @StringRes get

    /** The database key cannot be produced; the code is the [reason]'s own name. */
    data class KeyUnavailable(
        val reason: DbKeyUnavailable.Reason,
    ) : DataUnavailableCause {
        override val code: String get() = reason.name
        override val explanation: Int get() = R.string.key_unavailable_explanation
    }

    /** The stored identity is inconsistent (`LedgerError.IdentityCorrupt`). */
    data object IdentityCorrupt : DataUnavailableCause {
        override val code: String get() = "IDENTITY_CORRUPT"
        override val explanation: Int get() = R.string.identity_corrupt_explanation
    }
}
