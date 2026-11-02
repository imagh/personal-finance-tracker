package dev.imagh.pft.app.ui.theme

import androidx.annotation.DrawableRes
import dev.imagh.pft.R

/**
 * The icon registry: every symbol the app draws, as a Material Symbols Rounded vector drawable
 * (`res/drawable/sym_<key>.xml`; weight 400, grade 0, optical size 24, outlined). Chrome icons
 * (back, search, ...) and category icons come from here and nowhere else, so there is one icon
 * family. `androidx.compose.material.icons.*` is banned in app code (detekt `ForbiddenImport`).
 *
 * **Keys are permanent.** [key] is persisted in `category.icon_key` (as `sym:<key>`), so, like the
 * application ID, it is never renamed or removed, only added. `pft-symbol-keys.txt` (test
 * resources) pins every key, so removing or renaming one fails `PftSymbolTest`. An unknown key,
 * for example from a newer version synced in M2, is not an error: [fromKey] returns null and the
 * caller draws the letter fallback.
 *
 * To add one, see "Adding an icon" in `docs/architecture/design-system.md`.
 *
 * @property key the persisted key, matching `[a-z0-9_]{1,40}`.
 * @property res the vector drawable. Paint it tinted (`Icon(painterResource(res), ...)`): the
 *   drawable itself is black.
 */
enum class PftSymbol(
    val key: String,
    @DrawableRes val res: Int,
) {
    // Chrome
    BACK("back", R.drawable.sym_back),
    FORWARD("forward", R.drawable.sym_forward),
    ADD("add", R.drawable.sym_add),
    CLOSE("close", R.drawable.sym_close),
    CHECK("check", R.drawable.sym_check),
    SEARCH("search", R.drawable.sym_search),
    FILTER("filter", R.drawable.sym_filter),
    OVERFLOW("overflow", R.drawable.sym_overflow),
    CHEVRON_LEFT("chevron_left", R.drawable.sym_chevron_left),
    CHEVRON_RIGHT("chevron_right", R.drawable.sym_chevron_right),
    EXPAND_MORE("expand_more", R.drawable.sym_expand_more),
    EXPAND_LESS("expand_less", R.drawable.sym_expand_less),
    EDIT("edit", R.drawable.sym_edit),
    DELETE("delete", R.drawable.sym_delete),
    UNDO("undo", R.drawable.sym_undo),
    LOCK("lock", R.drawable.sym_lock),
    SETTINGS("settings", R.drawable.sym_settings),
    CALENDAR("calendar", R.drawable.sym_calendar),
    INFO("info", R.drawable.sym_info),
    WARNING("warning", R.drawable.sym_warning),
    ERROR("error", R.drawable.sym_error),
    VISIBILITY("visibility", R.drawable.sym_visibility),
    VISIBILITY_OFF("visibility_off", R.drawable.sym_visibility_off),
    DOWNLOAD("download", R.drawable.sym_download),
    UPLOAD("upload", R.drawable.sym_upload),
    TUNE("tune", R.drawable.sym_tune),
    SORT("sort", R.drawable.sym_sort),
    TRANSFER("transfer", R.drawable.sym_transfer),
    TAG("tag", R.drawable.sym_tag),
    HISTORY("history", R.drawable.sym_history),
    CHECK_CIRCLE("check_circle", R.drawable.sym_check_circle),

    // Food and drink
    RESTAURANT("restaurant", R.drawable.sym_restaurant),
    LOCAL_CAFE("local_cafe", R.drawable.sym_local_cafe),
    FASTFOOD("fastfood", R.drawable.sym_fastfood),
    LOCAL_BAR("local_bar", R.drawable.sym_local_bar),
    BAKERY_DINING("bakery_dining", R.drawable.sym_bakery_dining),
    LOCAL_PIZZA("local_pizza", R.drawable.sym_local_pizza),
    ICECREAM("icecream", R.drawable.sym_icecream),

    // Shopping
    SHOPPING_CART("shopping_cart", R.drawable.sym_shopping_cart),
    SHOPPING_BAG("shopping_bag", R.drawable.sym_shopping_bag),
    GROCERY("grocery", R.drawable.sym_grocery),
    CHECKROOM("checkroom", R.drawable.sym_checkroom),
    REDEEM("redeem", R.drawable.sym_redeem),
    DIAMOND("diamond", R.drawable.sym_diamond),

    // Transport
    DIRECTIONS_CAR("directions_car", R.drawable.sym_directions_car),
    DIRECTIONS_BUS("directions_bus", R.drawable.sym_directions_bus),
    TRAIN("train", R.drawable.sym_train),
    LOCAL_TAXI("local_taxi", R.drawable.sym_local_taxi),
    TWO_WHEELER("two_wheeler", R.drawable.sym_two_wheeler),
    FLIGHT("flight", R.drawable.sym_flight),
    LOCAL_GAS_STATION("local_gas_station", R.drawable.sym_local_gas_station),

    // Home and bills
    HOME("home", R.drawable.sym_home),
    BED("bed", R.drawable.sym_bed),
    CHAIR("chair", R.drawable.sym_chair),
    BOLT("bolt", R.drawable.sym_bolt),
    WATER_DROP("water_drop", R.drawable.sym_water_drop),
    WIFI("wifi", R.drawable.sym_wifi),
    CLEANING_SERVICES("cleaning_services", R.drawable.sym_cleaning_services),
    HANDYMAN("handyman", R.drawable.sym_handyman),
    RECEIPT_LONG("receipt_long", R.drawable.sym_receipt_long),
    PHONE_IPHONE("phone_iphone", R.drawable.sym_phone_iphone),
    TV("tv", R.drawable.sym_tv),

    // Health
    MEDICATION("medication", R.drawable.sym_medication),
    LOCAL_HOSPITAL("local_hospital", R.drawable.sym_local_hospital),
    FITNESS_CENTER("fitness_center", R.drawable.sym_fitness_center),
    SPA("spa", R.drawable.sym_spa),
    FAVORITE("favorite", R.drawable.sym_favorite),

    // Fun and travel
    MOVIE("movie", R.drawable.sym_movie),
    SPORTS_ESPORTS("sports_esports", R.drawable.sym_sports_esports),
    MUSIC_NOTE("music_note", R.drawable.sym_music_note),
    THEATER_COMEDY("theater_comedy", R.drawable.sym_theater_comedy),
    BEACH_ACCESS("beach_access", R.drawable.sym_beach_access),
    LUGGAGE("luggage", R.drawable.sym_luggage),

    // Family and learning
    SCHOOL("school", R.drawable.sym_school),
    MENU_BOOK("menu_book", R.drawable.sym_menu_book),
    CHILD_CARE("child_care", R.drawable.sym_child_care),
    PETS("pets", R.drawable.sym_pets),

    // Money and work
    WORK("work", R.drawable.sym_work),
    PAYMENTS("payments", R.drawable.sym_payments),
    ACCOUNT_BALANCE("account_balance", R.drawable.sym_account_balance),
    SAVINGS("savings", R.drawable.sym_savings),
    TRENDING_UP("trending_up", R.drawable.sym_trending_up),
    CREDIT_CARD("credit_card", R.drawable.sym_credit_card),
    ACCOUNT_BALANCE_WALLET("account_balance_wallet", R.drawable.sym_account_balance_wallet),
    CURRENCY_RUPEE("currency_rupee", R.drawable.sym_currency_rupee),
    LOCAL_ATM("local_atm", R.drawable.sym_local_atm),
    RECEIPT("receipt", R.drawable.sym_receipt),
    SUBSCRIPTIONS("subscriptions", R.drawable.sym_subscriptions),

    // Other
    CATEGORY("category", R.drawable.sym_category),
    STAR("star", R.drawable.sym_star),
    FLAG("flag", R.drawable.sym_flag),
    VOLUNTEER_ACTIVISM("volunteer_activism", R.drawable.sym_volunteer_activism),
    ;

    companion object {
        private val byKey: Map<String, PftSymbol> = entries.associateBy { it.key }

        /** The symbol persisted under [key], or null for an unknown or malformed key (exact match only). */
        fun fromKey(key: String): PftSymbol? = byKey[key]
    }
}
