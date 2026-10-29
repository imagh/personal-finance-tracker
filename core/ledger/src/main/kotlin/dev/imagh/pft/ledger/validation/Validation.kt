package dev.imagh.pft.ledger.validation

import dev.imagh.pft.ledger.entity.LedgerEntity
import dev.imagh.pft.money.Paise

/**
 * The validation helpers every service shares, so each rule is written once (plan §3.4).
 *
 * Each helper returns the validated value, so a service can write
 * `val name = Validation.requireName(cmd.name, LedgerLimits.MAX_NAME_LENGTH)`, and throws the
 * matching [LedgerError] when the value is not acceptable. Services call these at their boundary:
 * everything a user typed is untrusted until it has passed.
 */
object Validation {
    /**
     * [text] trimmed, if it is non-blank and at most [max] characters long once trimmed.
     *
     * @throws LedgerError.BlankName if the text is empty or whitespace.
     * @throws LedgerError.NameTooLong if it is longer than [max].
     */
    fun requireName(
        text: String,
        max: Int,
    ): String {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) throw LedgerError.BlankName()
        if (trimmed.length > max) throw LedgerError.NameTooLong(max)
        return trimmed
    }

    /**
     * [paise] if it is positive and at most [max] (inclusive).
     *
     * @throws LedgerError.AmountNotPositive for zero or a negative amount.
     * @throws LedgerError.AmountTooLarge above [max].
     */
    fun requireAmount(
        paise: Paise,
        max: Paise = LedgerLimits.MAX_AMOUNT,
    ): Paise {
        if (paise.value <= 0) throw LedgerError.AmountNotPositive()
        if (paise > max) throw LedgerError.AmountTooLarge(max)
        return paise
    }

    /**
     * [entity] if it can be referenced from new data: it exists and is neither deleted nor
     * archived. [what] names the kind of record for the error (`"account"`), never its content.
     *
     * @throws LedgerError.NotFound if [entity] is null.
     * @throws LedgerError.Deleted if it is tombstoned (reported first when it is also archived).
     * @throws LedgerError.Archived if it is archived.
     */
    fun <T : LedgerEntity<*, *>> requireLive(
        entity: T?,
        what: String,
    ): T {
        val found = entity ?: throw LedgerError.NotFound(what)
        if (found.meta.isActive) return found
        throw if (found.meta.isDeleted) LedgerError.Deleted(what) else LedgerError.Archived(what)
    }
}
