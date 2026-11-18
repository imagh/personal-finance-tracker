package dev.imagh.pft.app.ui.common

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import dev.imagh.pft.app.ui.tags.sampleTagLists
import dev.imagh.pft.app.ui.theme.PftTheme
import dev.imagh.pft.app.ui.theme.ThemePrefs
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.tag.TagLists

/**
 * A `TagPicker` over fake tags with "groceries" and the archived "Old trip" already selected, so the golden
 * shows a ringed cell, an unringed one and the archived-but-chosen cell with its "Archived" line. The single
 * source for the preview and the golden. Release builds drop it (R8; see `verifyReleaseShrink`).
 */
@Composable
internal fun TagPickerSample() {
    TagPicker(
        sampleTagLists(),
        selected = setOf(TagId("groceries"), TagId("old-trip")),
        onDone = {},
        onDismiss = {},
    )
}

/** A `TagPicker` with no tags at all: the empty sheet. */
@Composable
internal fun TagPickerEmptySample() {
    TagPicker(TagLists(emptyList(), emptyList()), selected = emptySet(), onDone = {}, onDismiss = {})
}

@PreviewLightDark
@Composable
internal fun TagPickerPreview() {
    PftTheme(ThemePrefs(dynamicColor = false)) { Surface(color = PftTheme.surfaces.canvas) { TagPickerSample() } }
}
