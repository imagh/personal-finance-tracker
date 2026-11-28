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
    /**
     * The nested graph that holds every screen once an identity exists: the tab graphs, and (from
     * slice C) the screens several tabs reach. The root graph starts on [SETUP] or here.
     */
    const val MAIN = "main"

    /** The Home tab's graph: [HOME] and the debug design catalogue. */
    const val HOME_TAB = "home_tab"

    /** The Transactions tab's graph: [TRANSACTIONS] (later the screens only this tab reaches). */
    const val TRANSACTIONS_TAB = "transactions_tab"

    /** The Accounts tab's graph: [ACCOUNTS] and the account form. */
    const val ACCOUNTS_TAB = "accounts_tab"

    /** The More tab's graph: [MORE], the categories and tags lists and their forms. */
    const val MORE_TAB = "more_tab"

    /** First-run setup: shown only while no identity exists; popped off the back stack on completion. */
    const val SETUP = "setup"

    /** Home, the Home tab's root: where every later launch starts, and where Back leaves the app. */
    const val HOME = "home"

    /** More, the More tab's root: the entry point to the lists that are not tabs of their own. */
    const val MORE = "more"

    /** The month list with its month strip and totals, the Transactions tab's root. */
    const val TRANSACTIONS = "transactions"

    /** The accounts list, the Accounts tab's root. */
    const val ACCOUNTS = "accounts"

    /** The categories list, opened from More. */
    const val CATEGORIES = "categories"

    /** The tags list, opened from More. */
    const val TAGS = "tags"

    /** The argument name of an entity's id in the edit routes. */
    const val ARG_ID = "id"

    /** The argument name of a new sub-category's parent id in the category form's route. */
    const val ARG_PARENT = "parent"

    /** The argument name of a new category's kind (an enum name) in the category form's route. */
    const val ARG_KIND = "kind"

    /** The account form: create when no id is given, edit when `id` is an account's id. Build it with [accountEdit]. */
    const val ACCOUNT_EDIT = "$ACCOUNT_EDIT_BASE?$ARG_ID={$ARG_ID}"

    /** The tag form: create when no id is given, edit when `id` is a tag's id. Build it with [tagEdit]. */
    const val TAG_EDIT = "$TAG_EDIT_BASE?$ARG_ID={$ARG_ID}"

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

    /** The route that opens the tag form: for a new tag when [id] is null, else for tag [id]. */
    fun tagEdit(id: String? = null): String =
        if (id == null) TAG_EDIT_BASE else "$TAG_EDIT_BASE?$ARG_ID=${Uri.encode(id)}"
}

/** The account form's path without its argument; the one place it is written. */
private const val ACCOUNT_EDIT_BASE = "accounts/edit"

/** The category form's path without its arguments; the one place it is written. */
private const val CATEGORY_EDIT_BASE = "categories/edit"

/** The tag form's path without its argument; the one place it is written. */
private const val TAG_EDIT_BASE = "tags/edit"
