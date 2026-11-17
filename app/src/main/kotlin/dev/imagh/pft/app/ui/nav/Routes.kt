package dev.imagh.pft.app.ui.nav

import android.net.Uri
import dev.imagh.pft.ledger.category.CategoryKind

/**
 * The navigation routes, as plain strings (plan decision D3): Navigation 2.8's type-safe routes need
 * the kotlinx-serialization compiler plugin, which the project does not use. A route carries an
 * argument only where an entity is edited, and then only its **id** (never a name or an amount): the
 * receiving ViewModel treats it as untrusted text (plan D13). The debug-only design catalogue route
 * lives in `src/debug` and is not listed here.
 */
object Routes {
    /** First-run setup: shown only while no identity exists; popped off the back stack on completion. */
    const val SETUP = "setup"

    /** Home: where every later launch starts, and where Back leaves the app. */
    const val HOME = "home"

    /** More: the entry point to the entity lists, opened from Home's header (plan D9). */
    const val MORE = "more"

    /** The accounts list, opened from More. */
    const val ACCOUNTS = "accounts"

    /** The categories list, opened from More. */
    const val CATEGORIES = "categories"

    /** The argument name of an entity's id in the edit routes. */
    const val ARG_ID = "id"

    /** The argument name of a new sub-category's parent id in the category form's route. */
    const val ARG_PARENT = "parent"

    /** The argument name of a new category's kind (an enum name) in the category form's route. */
    const val ARG_KIND = "kind"

    /** The account form: create when no id is given, edit when `id` is an account's id. Build it with [accountEdit]. */
    const val ACCOUNT_EDIT = "$ACCOUNT_EDIT_BASE?$ARG_ID={$ARG_ID}"

    /**
     * The category form: create when no `id` is given (with the new category's `kind`, and a `parent` for a
     * sub-category), edit when `id` is a category's id. Build it with [categoryEdit].
     */
    const val CATEGORY_EDIT = "$CATEGORY_EDIT_BASE?$ARG_ID={$ARG_ID}&$ARG_PARENT={$ARG_PARENT}&$ARG_KIND={$ARG_KIND}"

    /**
     * The route that opens the category form, with only the arguments given, each encoded: for category
     * [id], or for a new category of [kind] (Expense when omitted) under [parent].
     */
    fun categoryEdit(
        id: String? = null,
        parent: String? = null,
        kind: CategoryKind? = null,
    ): String {
        val arguments =
            listOfNotNull(id?.let { ARG_ID to it }, parent?.let { ARG_PARENT to it }, kind?.let { ARG_KIND to it.name })
        if (arguments.isEmpty()) return CATEGORY_EDIT_BASE
        return arguments.joinToString("&", "$CATEGORY_EDIT_BASE?") { (name, value) -> "$name=${Uri.encode(value)}" }
    }

    /** The route that opens the account form: for a new account when [id] is null, else for account [id]. */
    fun accountEdit(id: String? = null): String =
        if (id == null) ACCOUNT_EDIT_BASE else "$ACCOUNT_EDIT_BASE?$ARG_ID=${Uri.encode(id)}"
}

/** The account form's path without its argument; the one place it is written. */
private const val ACCOUNT_EDIT_BASE = "accounts/edit"

/** The category form's path without its arguments; the one place it is written. */
private const val CATEGORY_EDIT_BASE = "categories/edit"
