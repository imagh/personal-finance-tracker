package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.ledger.validation.Validation
import dev.imagh.pft.money.Paise

/**
 * A list of line drafts that has passed the pure rules for splitting [total] (IT §3.3 rule 1), with
 * each note trimmed and a blank one made null. Only [of] builds one, so holding a `Breakdown` means
 * the lines add up. It does not look anything up: the categories and tags are checked by
 * [TransactionService], against the store.
 *
 * @property total the transaction's amount the lines split.
 * @property lines the checked lines, in the order given (that order becomes their positions).
 */
class Breakdown private constructor(
    val total: Paise,
    val lines: List<LineDraft>,
) {
    /** Builds a [Breakdown]. */
    companion object {
        /**
         * [drafts] checked as a split of [total], in this order: the count, each line's amount and
         * note, distinct ids, then the sum. The sum is exact ([Paise.plus] uses `addExact`; 50 lines of
         * the largest amount are far below `Long.MAX_VALUE`).
         *
         * @throws LedgerError.TooManyLines above [LedgerLimits.MAX_LINES_PER_TRANSACTION] lines.
         * @throws LedgerError.AmountNotPositive / [LedgerError.AmountTooLarge] for a line's amount.
         * @throws LedgerError.NameTooLong for a note above [LedgerLimits.MAX_LINE_NOTE_LENGTH]
         *   characters once trimmed.
         * @throws LedgerError.DuplicateLine if two drafts name the same line id (new lines have none).
         * @throws LedgerError.BreakdownMismatch if the amounts do not add up exactly to [total],
         *   including an empty list.
         */
        fun of(
            total: Paise,
            drafts: List<LineDraft>,
        ): Breakdown {
            if (drafts.size > LedgerLimits.MAX_LINES_PER_TRANSACTION) {
                throw LedgerError.TooManyLines(LedgerLimits.MAX_LINES_PER_TRANSACTION)
            }
            val lines = drafts.map { it.checked() }
            requireDistinctIds(lines)
            val sum = lines.fold(Paise.ZERO) { acc, line -> acc + line.amount }
            if (sum != total) throw LedgerError.BreakdownMismatch()
            return Breakdown(total, lines)
        }

        /** This line with its amount checked and its note trimmed (blank becomes null) and bounded. */
        private fun LineDraft.checked(): LineDraft {
            Validation.requireAmount(amount)
            return copy(note = Validation.requireOptionalName(note, LedgerLimits.MAX_LINE_NOTE_LENGTH))
        }

        /** Refuses two drafts that name the same line; new lines (no id) never clash. */
        private fun requireDistinctIds(lines: List<LineDraft>) {
            val ids = lines.mapNotNull { it.id }
            if (ids.size != ids.toSet().size) throw LedgerError.DuplicateLine()
        }
    }
}
