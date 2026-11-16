package dev.imagh.pft.app.ui.common

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.account.Account
import dev.imagh.pft.ledger.account.AccountType
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.AccountId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.money.Paise
import java.time.Instant

private fun sampleAccount(
    id: String,
    name: String,
    type: AccountType,
    issuer: String? = null,
    lastFour: String? = null,
) = Account(
    meta = EntityMeta(AccountId(id), Instant.EPOCH, Instant.EPOCH, UserId("sample"), DeviceId("sample")),
    name = name,
    type = type,
    openingBalance = Paise(0),
    maskedIdentifier = lastFour,
    issuer = issuer,
)

/**
 * An `AccountPicker` over four fake accounts, one of each type, in the order `AccountService.active()`
 * gives, with "Everyday" selected. The single source for the preview and the golden. Release builds
 * drop it (R8; see `verifyReleaseShrink`).
 */
@Composable
internal fun AccountPickerSample() {
    val accounts =
        listOf(
            sampleAccount("a1", "Everyday", AccountType.BANK, issuer = "Sample Bank", lastFour = "1234"),
            sampleAccount("a2", "Pocket cash", AccountType.CASH),
            sampleAccount("a3", "Rewards card", AccountType.CREDIT_CARD, issuer = "Sample Card", lastFour = "9876"),
            sampleAccount("a4", "Wallet", AccountType.WALLET),
        )
    AccountPicker(accounts, selected = AccountId("a1"), onPick = {}, onDismiss = {})
}

/** An `AccountPicker` with no accounts. */
@Composable
internal fun AccountPickerEmptySample() {
    AccountPicker(emptyList(), selected = null, onPick = {}, onDismiss = {})
}

@PreviewLightDark
@Composable
internal fun AccountPickerPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { AccountPickerSample() } }
}

@PreviewLightDark
@Composable
internal fun AccountPickerEmptyPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) {
        Surface(color = PftTheme.surfaces.canvas) { AccountPickerEmptySample() }
    }
}
