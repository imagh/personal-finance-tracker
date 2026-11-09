package dev.imagh.pft.app.ui.common

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.text.TextLayoutResult
import org.junit.Assert.assertTrue

/** The node's single `TextLayoutResult`, read through semantics. */
internal fun SemanticsNodeInteraction.layout(): TextLayoutResult {
    val results = mutableListOf<TextLayoutResult>()
    fetchSemanticsNode()
        .config
        .getOrNull(SemanticsActions.GetTextLayoutResult)
        ?.action
        ?.invoke(results)
    return results.single()
}

/**
 * True when the whole text is on one line and that line is not wider than the laid-out text box:
 * nothing was wrapped, truncated or clipped.
 *
 * This is used instead of `TextLayoutResult.hasVisualOverflow`, which is unreliable in Compose
 * 1.7: it is true for any wrap-content `Text` narrower than its maximum width (its `size` is the
 * wrapped width but `multiParagraph.width` is the constraint), and false when a `maxLines = 1`
 * text really is cut off. Both were reproduced in a probe (see docs/learnings/README.md).
 */
internal fun TextLayoutResult.isShownInFull(): Boolean =
    lineCount == 1 &&
        !isLineEllipsized(0) &&
        getLineEnd(0, visibleEnd = false) == layoutInput.text.length &&
        getLineRight(0) <= size.width + HALF_PIXEL

/** Asserts [isShownInFull], with the text and [context] in the failure message. */
internal fun TextLayoutResult.assertShownInFull(context: String = "") {
    assertTrue(
        "'${layoutInput.text}' is wrapped, cut or wider than its box $context: " +
            "lines=$lineCount lineEnd=${getLineEnd(0)} lineRight=${getLineRight(0)} width=${size.width}",
        isShownInFull(),
    )
}

private const val HALF_PIXEL = 0.5f
