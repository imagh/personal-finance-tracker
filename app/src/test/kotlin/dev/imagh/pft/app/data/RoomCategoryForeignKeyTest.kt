package dev.imagh.pft.app.data

import android.database.sqlite.SQLiteConstraintException
import dev.imagh.pft.app.data.category.CategoryMapper
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant

/**
 * The [Verify] item of plan §4: does the database enforce `category.parent_id -> category(id)`? This
 * test pins the answer so a change in Room's or SQLite's behaviour is noticed. Whatever the answer,
 * `CategoryService` validates the parent inside the write transaction, which is the guard.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class RoomCategoryForeignKeyTest {
    private val database = TestDatabase.create()
    private val dao = database.categoryDao()
    private val at = Instant.parse("2026-11-01T10:00:00Z")

    @After
    fun closeDatabase() = database.close()

    private fun row(
        id: String,
        parent: String?,
    ) = CategoryMapper.toRow(
        Category(
            meta = EntityMeta(CategoryId(id), at, at, UserId("user-1"), DeviceId("device-1")),
            parentId = parent?.let(::CategoryId),
            kind = CategoryKind.EXPENSE,
            name = id,
            colour = CategoryColour.SLATE,
            iconKey = null,
            sortOrder = 0,
            systemKey = null,
        ),
    )

    @Test
    fun `a row whose parent exists is accepted`() =
        runSuspending {
            dao.insert(row("parent", null))

            dao.insert(row("child", "parent"))

            dao.byId("child")!!.parentId shouldBe "parent"
        }

    @Test
    fun `a row whose parent does not exist is refused by the database`() =
        runSuspending {
            shouldThrow<SQLiteConstraintException> { dao.insert(row("orphan", "no-such-parent")) }

            dao.byId("orphan") shouldBe null
        }
}
