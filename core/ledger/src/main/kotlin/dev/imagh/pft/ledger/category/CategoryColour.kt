package dev.imagh.pft.ledger.category

/**
 * The 12 colours a category can have, by their **permanent** persisted names (`category.colour`).
 * The names and their order are the same as the app's `CategoryPalette.keys` (which draws them);
 * a test in `app` keeps the two lists equal. Never rename or reorder: stored data and synced
 * devices refer to the keys.
 *
 * @property key the persisted name, lower-case.
 */
enum class CategoryColour(
    val key: String,
) {
    ROSE("rose"),
    CORAL("coral"),
    AMBER("amber"),
    OLIVE("olive"),
    EMERALD("emerald"),
    TEAL("teal"),
    SKY("sky"),
    BLUE("blue"),
    INDIGO("indigo"),
    VIOLET("violet"),
    MAGENTA("magenta"),
    SLATE("slate"),
    ;

    companion object {
        /**
         * The colour stored as [text]. A stored value is untrusted (a corrupt row, or one written by
         * a newer version and synced in M2), so only an exact [key] match counts: unknown, wrong-case
         * or null becomes [SLATE], the neutral fallback, and this never throws.
         */
        fun fromStored(text: String?): CategoryColour = entries.firstOrNull { it.key == text } ?: SLATE
    }
}
