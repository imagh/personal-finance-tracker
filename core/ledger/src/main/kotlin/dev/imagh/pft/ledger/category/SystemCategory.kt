package dev.imagh.pft.ledger.category

/**
 * The categories the app itself relies on, identified by a permanent key stored in
 * `category.system_key`. A system category can be renamed, recoloured and re-iconed but never
 * archived or deleted (`LedgerError.SystemCategoryProtected`), so the feature that needs it can
 * always find it.
 */
enum class SystemCategory {
    /** Where loan instalments go, so loans (slice F) can book an EMI without asking. */
    LOAN_EMI,
}
