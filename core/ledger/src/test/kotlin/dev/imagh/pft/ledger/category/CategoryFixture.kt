package dev.imagh.pft.ledger.category

import dev.imagh.pft.ledger.fakes.LedgerTestKit
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/** The wiring every category service test starts from: the kit's fakes and a few shorthand writers. */
internal class CategoryFixture {
    val kit = LedgerTestKit()
    val service = CategoryService(kit.store, kit.writer, kit.categoryRepository, kit.ids)
    val trail get() = kit.store.auditLog.entries

    fun draft(
        name: String = "Groceries",
        colour: CategoryColour = CategoryColour.CORAL,
        icon: CategoryIcon? = CategoryIcon.Symbol("grocery"),
    ) = CategoryDraft(name, colour, icon)

    /** The stored rows themselves (tombstones included), not the service's view. */
    fun stored() = kit.categoryRepository.all()

    suspend fun top(
        name: String = "Food",
        kind: CategoryKind = CategoryKind.EXPENSE,
    ): Category = service.create(null, kind, draft(name))

    /** A system category, written the way the seeding writes one (nothing else can create it). */
    suspend fun system(name: String = "Loan EMI"): Category =
        kit.writer.create(kit.categoryRepository, CategoryId(kit.ids.next())) { meta ->
            Category(meta, null, CategoryKind.EXPENSE, name, CategoryColour.SLATE, null, 0, SystemCategory.LOAN_EMI)
        }

    suspend fun archive(category: Category) = kit.writer.archive(kit.categoryRepository, category.meta.id)

    suspend fun delete(category: Category) = kit.writer.delete(kit.categoryRepository, category.meta.id)

    suspend fun sub(
        parent: Category,
        name: String = "Groceries",
    ): Category = service.create(parent.meta.id, parent.kind, draft(name))
}

/**
 * Runs [block], which must be refused with an [E], and checks the refusal changed nothing: no row
 * written (tombstones and all) and no audit entry appended. Returns the error for further checks.
 */
internal suspend inline fun <reified E : LedgerError> CategoryFixture.shouldRefuse(block: () -> Any?): E {
    val rows = stored()
    val trailSize = trail.size
    val error = shouldThrow<E> { block() }
    stored() shouldBe rows
    trail shouldHaveSize trailSize
    return error
}
