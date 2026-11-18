package dev.imagh.pft.app.ui.common

import androidx.test.core.app.ApplicationProvider
import dev.imagh.pft.app.log.EntityAction
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The fixed "Couldn't ... Try again." text a list shows when an action fails unexpectedly, one per
 * [EntityAction] and shared by every entity list. Each says what failed and carries nothing else
 * (no name, no exception text).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class EntityActionTextTest {
    private fun text(action: EntityAction): String =
        ApplicationProvider.getApplicationContext<android.app.Application>().getString(action.failureRes())

    @Test
    fun `every action has its own fixed message`() {
        EntityAction.entries.associateWith(::text) shouldBe
            mapOf(
                EntityAction.ARCHIVE to "Couldn't archive. Try again.",
                EntityAction.UNARCHIVE to "Couldn't unarchive. Try again.",
                EntityAction.DELETE to "Couldn't delete. Try again.",
                EntityAction.RESTORE to "Couldn't undo. Try again.",
                EntityAction.REORDER to "Couldn't move. Try again.",
            )
    }
}
