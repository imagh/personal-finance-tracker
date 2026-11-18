package dev.imagh.pft.app.ui.tags

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.ledger.tag.Tag
import dev.imagh.pft.ledger.tag.TagLists
import java.time.Instant

private val SAMPLE_TIME = Instant.parse("2026-01-01T00:00:00Z")

/** A fake tag for samples and tests; fake data only. */
internal fun sampleTag(
    id: String,
    name: String,
    archived: Boolean = false,
) = Tag(
    meta =
        EntityMeta(
            id = TagId(id),
            createdAt = SAMPLE_TIME,
            updatedAt = SAMPLE_TIME,
            createdByUserId = UserId("sample-user"),
            createdOnDeviceId = DeviceId("sample-device"),
            archivedAt = if (archived) SAMPLE_TIME else null,
        ),
    name = name,
)

/**
 * Fake tags in the order `TagService.lists` gives them (by name ignoring case, so "groceries" sits between
 * "Bills" and "Travel"): four active and one archived.
 */
internal fun sampleTagLists() =
    TagLists(
        active =
            listOf(
                sampleTag("bills", "Bills"),
                sampleTag("groceries", "groceries"),
                sampleTag("reimbursable", "Reimbursable"),
                sampleTag("travel", "Travel"),
            ),
        archived = listOf(sampleTag("old-trip", "Old trip", archived = true)),
    )

/** The state of the tags list over [sampleTagLists]. The single source for the preview and the goldens. */
internal fun sampleTagsState(): TagsUiState {
    val lists = sampleTagLists()
    return TagsUiState(active = lists.active, archived = lists.archived, loaded = true)
}

/** The tags list over [sampleTagsState]. Release builds drop it (R8; see `verifyReleaseShrink`). */
@Composable
internal fun TagsSample() {
    val snackbars = remember { SnackbarHostState() }
    TagsScreen(
        state = sampleTagsState(),
        actions = TagsActions(archive = {}, unarchive = {}, delete = {}, add = {}, edit = {}),
        snackbars = snackbars,
        onBack = {},
    )
}

@PreviewLightDark
@Composable
internal fun TagsPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { TagsSample() } }
}
