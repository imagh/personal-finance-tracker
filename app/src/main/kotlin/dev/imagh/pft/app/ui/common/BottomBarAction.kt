package dev.imagh.pft.app.ui.common

/**
 * One action in a [BottomActionBar].
 *
 * @property label the button text (a verb: "Save", "Continue", "Edit").
 * @property enabled a disabled action is greyed out and does not respond to taps.
 * @property onClick called once per tap.
 */
class BottomBarAction(
    val label: String,
    val enabled: Boolean = true,
    val onClick: () -> Unit,
)
