package dev.imagh.pft.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.imagh.pft.app.ui.common.MoneyTone
import dev.imagh.pft.app.ui.common.StatusChip
import dev.imagh.pft.app.ui.theme.PftSymbol
import dev.imagh.pft.app.ui.theme.PftTheme

/**
 * The catalogue's accent page: the manual amber check of plan §3.1 "Accent safety" and §11. The
 * brass and champagne accents sit within a few degrees of warning amber, so hue cannot tell them
 * apart; this page puts an `Unverified` warning pill beside a selected navigation item and the FAB
 * (the places the accent may appear) so the owner can judge, in Midnight dark and Aubergine dark,
 * that the pill reads as a status and the others as accent. Fake labels only. Debug builds only.
 */
@Composable
internal fun AccentCheckSection() {
    val spacing = PftTheme.spacing
    var selected by rememberSaveable { mutableIntStateOf(0) }
    Column(Modifier.padding(spacing.lg), verticalArrangement = Arrangement.spacedBy(spacing.lg)) {
        Text(
            "Amber check: the pill is a status, the navigation item and the FAB are the accent.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm), verticalAlignment = Alignment.CenterVertically) {
            StatusChip("Unverified", MoneyTone.Warning)
            StatusChip("80%", MoneyTone.Warning)
        }
        Box(Modifier.fillMaxWidth().height(96.dp)) {
            FloatingActionButton(onClick = {}, modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(painterResource(PftSymbol.ADD.res), contentDescription = "Add")
            }
        }
        NavigationBar {
            listOf(
                "Budgets" to PftSymbol.TAG,
                "Accounts" to PftSymbol.TRANSFER,
                "Settings" to PftSymbol.SETTINGS,
            ).forEachIndexed { index, (label, symbol) ->
                NavigationBarItem(
                    selected = selected == index,
                    onClick = { selected = index },
                    icon = { Icon(painterResource(symbol.res), contentDescription = null) },
                    label = { Text(label) },
                )
            }
        }
    }
}
