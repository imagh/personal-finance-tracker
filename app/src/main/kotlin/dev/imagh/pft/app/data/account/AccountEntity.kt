package dev.imagh.pft.app.data.account

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import dev.imagh.pft.app.data.common.MetaColumns

/**
 * Table `account` (schema v2, plan §4): one row per account, the Room twin of core's `Account`.
 * Money is signed integer paise; there is no stored balance (INT-6).
 *
 * `currency` is always `INR` and is written and checked by [AccountMapper] only; it is the one column
 * outside the audit trail (a reviewed allowlist entry in the audit-columns test), because it is not
 * part of the model. There is deliberately no `CHECK` constraint on it: Room cannot declare one, so
 * it would exist only in migrated databases (plan §2). The mapper holds the invariant instead.
 *
 * The meta columns are embedded, and [Entity.primaryKeys] names `id` because Room does not count a
 * key inside an embedded class.
 */
@Entity(tableName = "account", primaryKeys = ["id"])
data class AccountEntity(
    @Embedded val meta: MetaColumns,
    @ColumnInfo(name = "name") val name: String,
    /** The `AccountType` name. */
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "currency") val currency: String,
    /** Signed paise. */
    @ColumnInfo(name = "opening_balance") val openingBalance: Long,
    @ColumnInfo(name = "masked_identifier") val maskedIdentifier: String?,
    @ColumnInfo(name = "issuer") val issuer: String?,
)
