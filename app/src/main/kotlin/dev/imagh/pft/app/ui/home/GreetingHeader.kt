package dev.imagh.pft.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.imagh.pft.R
import dev.imagh.pft.app.ui.theme.PftTheme

private val AVATAR_SIZE = 44.dp

/**
 * Home's header (plan D9): a round avatar with the user's initials, then "Good morning / afternoon /
 * evening" over the user's name, and the caller's [actions] at the end of the row (the debug build's
 * design-catalogue button; release passes none). There is no bell and no gear.
 *
 * The avatar is decorative: its initials only repeat the name beside it, so it is cleared from
 * semantics and TalkBack says the greeting and the name once. Insets and the gutter are the caller's
 * (the Home list pads for the status bar); the header adds only its own row spacing.
 *
 * @param greeting which part of the day to greet for.
 * @param name the user's name, shown as given (it is the user's own text, never logged).
 * @param actions extra header actions (only `HeaderIconButton`s, UI-14), drawn after the name.
 */
@Composable
fun GreetingHeader(
    greeting: Greeting,
    name: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier.fillMaxWidth().padding(horizontal = PftTheme.spacing.gutter, vertical = PftTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PftTheme.spacing.md),
    ) {
        Avatar(initialsOf(name))
        Column(Modifier.weight(1f, fill = false)) {
            Text(
                stringResource(greeting.textRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                name,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        actions()
    }
}

/** The round `brandSoft` avatar, decorative (see [GreetingHeader]). */
@Composable
private fun Avatar(initials: String) {
    val brand = PftTheme.brand
    Box(
        Modifier
            .clearAndSetSemantics {}
            .size(AVATAR_SIZE)
            .clip(CircleShape)
            .background(brand.brandSoft),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            initials,
            color = brand.onBrandSoft,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

private val Greeting.textRes: Int
    get() =
        when (this) {
            Greeting.MORNING -> R.string.home_greeting_morning
            Greeting.AFTERNOON -> R.string.home_greeting_afternoon
            Greeting.EVENING -> R.string.home_greeting_evening
        }
