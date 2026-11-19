package dev.imagh.pft.ledger.transaction

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditEntry
import dev.imagh.pft.ledger.audit.AuditSource
import dev.imagh.pft.ledger.audit.FieldChange
import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryDraft
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.ids.NameBasedId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.TransactionLineId
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.money.Paise
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Duration

/**
 * Itemising a transaction and reconciling its lines (slice C C1b, D3; IT §3.3 rules 3 to 8): several
 * lines on create and edit, the foreign-id and reference rules, the position and audit shape, the
 * auto-captured facts, and the fail-closed re-check of the sum.
 */
class TransactionItemisationTest :
    FunSpec({
        /** A started fixture with one saved Rs 2,000 expense on Food and two more expense categories. */
        class Scene(
            val f: TransactionFixture,
            val saved: TransactionWithLines,
            val stationery: Category,
            val utilities: Category,
        ) {
            val id = saved.transaction.meta.id
            val line1 =
                saved.lines
                    .single()
                    .meta.id

            /** [amount] paise in [category]; [id] names the line it edits, null adds one. */
            fun item(
                amount: Long,
                category: Category,
                id: TransactionLineId? = null,
                tagIds: Set<TagId> = emptySet(),
            ) = LineDraft(id, Paise(amount), category.meta.id, tagIds)

            /** Rs 1,200 on Food (line 1 kept), Rs 300 on Stationery and Rs 500 on Utilities: Rs 2,000. */
            fun split() = listOf(item(120_000, f.food, line1), item(30_000, stationery), item(50_000, utilities))

            /** The saved transaction's own draft with these [lines]. */
            fun withLines(lines: List<LineDraft>) = f.draftOf(saved).copy(lines = lines)

            suspend fun itemise() = f.service.edit(id, withLines(split()))

            /** The audit entries written after the first [before] ones. */
            fun written(before: Int) = f.trail.drop(before)

            /** The live lines as the service reads them: by position. */
            suspend fun live() = f.service.get(id)!!.lines
        }

        suspend fun scene(): Scene {
            val f = TransactionFixture().start()
            val expense = CategoryKind.EXPENSE
            val stationery = f.kit.categories.create(null, expense, CategoryDraft("Stationery", CategoryColour.CORAL))
            val utilities = f.kit.categories.create(null, expense, CategoryDraft("Utilities", CategoryColour.SLATE))
            val saved = f.service.create(f.draft(amount = 200_000))
            f.kit.clock.advance(Duration.ofMinutes(5))
            return Scene(f, saved, stationery, utilities)
        }

        fun List<AuditEntry>.ofType(type: String) = filter { it.entityType == type }

        context("itemise") {
            test("one line becomes three: one line update, two line creates, no transaction entry") {
                val s = scene()
                val before = s.f.trail.size

                val result = s.itemise()

                val written = s.written(before)
                written shouldHaveSize 3
                written.ofType("ledger_transaction") shouldHaveSize 0
                val updates = written.ofType("transaction_line").filter { it.action == AuditAction.UPDATE }
                updates.single().entityId shouldBe s.line1.value
                updates.single().changes shouldBe mapOf("amount" to FieldChange("200000", "120000"))
                val creates = written.ofType("transaction_line").filter { it.action == AuditAction.CREATE }
                creates.map { it.entityId } shouldBe result.lines.drop(1).map { it.meta.id.value }
                written.forEach { it.source shouldBe AuditSource.MANUAL }
                result.lines.map { it.position } shouldBe listOf(1, 2, 3)
                result.lines.map { it.amount.value } shouldBe listOf(120_000L, 30_000L, 50_000L)
                s.live().sumOf { it.amount.value } shouldBe 200_000L
            }

            test("lines added by an edit get random time-ordered ids, not the deterministic first-line ids") {
                val s = scene()

                val added =
                    s
                        .itemise()
                        .lines
                        .drop(1)
                        .map { it.meta.id.value }

                added.forEachIndexed { index, id ->
                    id shouldNotBe NameBasedId.of("transaction-line", s.id.value, (index + 2).toString())
                    // The version digit of a Uuid7 (the 15th character) is 7.
                    id[14] shouldBe '7'
                }
                added.toSet() shouldHaveSize 2
            }

            test("saving the itemised transaction again as it is writes nothing") {
                val s = scene()
                val itemised = s.itemise()
                val before = s.f.trail.size

                val again = s.f.service.edit(s.id, s.f.draftOf(itemised))

                again shouldBe itemised
                s.f.trail shouldHaveSize before
            }

            test("a line's tags audit as the sorted ids joined by a comma") {
                val s = scene()
                val work =
                    s.f.kit.tags
                        .create("Work")
                val tags = setOf(s.f.travel.meta.id, work.meta.id)
                val before = s.f.trail.size
                val lines = listOf(s.item(120_000, s.f.food, s.line1), s.item(80_000, s.stationery, tagIds = tags))

                val result = s.f.service.edit(s.id, s.withLines(lines))

                val created = s.written(before).ofType("transaction_line").single { it.action == AuditAction.CREATE }
                created.entityId shouldBe
                    result.lines[1]
                        .meta.id.value
                created.changes["tag_ids"] shouldBe FieldChange(null, tags.map { it.value }.sorted().joinToString(","))
            }
        }

        context("reordering and removing items") {
            test("reordering changes only the positions, renumbered 1..n in draft order") {
                val s = scene()
                val itemised = s.itemise()
                val (a, b, c) = itemised.lines.map { it.meta.id }
                val before = s.f.trail.size
                val draft = s.f.draftOf(itemised)

                val result = s.f.service.edit(s.id, draft.copy(lines = draft.lines.reversed()))

                result.lines.map { it.meta.id } shouldBe listOf(c, b, a)
                result.lines.map { it.position } shouldBe listOf(1, 2, 3)
                s.live().map { it.meta.id } shouldBe listOf(c, b, a)
                // The middle line keeps position 2, so only two lines change, and only their position.
                s.written(before).associate { it.entityId to it.changes } shouldBe
                    mapOf(
                        a.value to mapOf("position" to FieldChange("1", "3")),
                        c.value to mapOf("position" to FieldChange("3", "1")),
                    )
            }

            test("a draft line without an id replaces the only line: the old one is tombstoned, a new one added") {
                val s = scene()
                val before = s.f.trail.size
                val replacement = listOf(s.item(200_000, s.stationery))

                val result = s.f.service.edit(s.id, s.withLines(replacement))

                val line = result.lines.single()
                line.meta.id shouldNotBe s.line1
                line.position shouldBe 1
                s.live().map { it.meta.id } shouldBe listOf(line.meta.id)
                val written = s.written(before)
                written shouldHaveSize 2
                written.map { it.entityId to it.action }.toSet() shouldBe
                    setOf(line.meta.id.value to AuditAction.CREATE, s.line1.value to AuditAction.DELETE)
            }

            test("a removed item is tombstoned and audited, the rest renumbered, and it stays in the store") {
                val s = scene()
                val (a, b, c) = s.itemise().lines.map { it.meta.id }
                val before = s.f.trail.size
                val without = listOf(s.item(150_000, s.f.food, a), s.item(50_000, s.utilities, c))

                val result = s.f.service.edit(s.id, s.withLines(without))

                result.lines.map { it.meta.id to it.position } shouldBe listOf(a to 1, c to 2)
                s.live().map { it.meta.id } shouldBe listOf(a, c)
                val stored = s.f.storedLines().associateBy { it.meta.id }
                stored.keys shouldBe setOf(a, b, c)
                stored.getValue(b).meta.deletedAt shouldBe
                    s.f.kit.clock
                        .instant()
                s.written(before) shouldHaveSize 3
                val entries = s.written(before).ofType("transaction_line").associateBy { it.entityId }
                entries.getValue(b.value).action shouldBe AuditAction.DELETE
                entries.getValue(c.value).changes shouldBe mapOf("position" to FieldChange("3", "2"))
                entries.getValue(a.value).changes shouldBe mapOf("amount" to FieldChange("120000", "150000"))
            }
        }

        context("the sum") {
            test("items that do not add up are refused") {
                val s = scene()
                val short = listOf(s.item(120_000, s.f.food, s.line1), s.item(30_000, s.stationery))

                s.f.shouldRefuse<LedgerError.BreakdownMismatch> { s.f.service.edit(s.id, s.withLines(short)) }
            }

            test("a changed amount with unchanged lines is refused") {
                val s = scene()
                val itemised = s.itemise()

                s.f.shouldRefuse<LedgerError.BreakdownMismatch> {
                    s.f.service.edit(s.id, s.f.draftOf(itemised).copy(amount = Paise(250_000)))
                }
            }

            test("a changed amount with re-balanced lines is saved, the transaction audit showing the amount") {
                val s = scene()
                val itemised = s.itemise()
                val before = s.f.trail.size
                val draft = s.f.draftOf(itemised)
                val first = draft.lines.first().copy(amount = Paise(170_000))

                val rebalanced = draft.copy(amount = Paise(250_000), lines = listOf(first) + draft.lines.drop(1))

                val result = s.f.service.edit(s.id, rebalanced)

                result.transaction.amount shouldBe Paise(250_000)
                s.live().sumOf { it.amount.value } shouldBe 250_000L
                val written = s.written(before)
                written shouldHaveSize 2
                written.ofType("ledger_transaction").single().changes shouldBe
                    mapOf("amount" to FieldChange("200000", "250000"))
                written.ofType("transaction_line").single().changes shouldBe
                    mapOf("amount" to FieldChange("120000", "170000"))
            }
        }

        context("line ids belong to the transaction (IT §3.3 rule 4)") {
            test("another transaction's line id is refused after the transaction write, and nothing is kept") {
                val s = scene()
                val other = s.f.service.create(s.f.draft(amount = 50_000, title = "Other"))
                val foreign =
                    other.lines
                        .single()
                        .meta.id
                val lines = listOf(s.item(150_000, s.f.food, s.line1), s.item(50_000, s.utilities, foreign))

                s.f
                    .shouldRefuse<LedgerError.NotFound> {
                        s.f.service.edit(s.id, s.withLines(lines).copy(title = "Changed"))
                    }.what shouldBe "transaction_line"
                s.f.service.get(other.transaction.meta.id) shouldBe other
            }

            test("a removed item's id cannot be named again: it is a tombstoned line") {
                val s = scene()
                val (a, b, c) = s.itemise().lines.map { it.meta.id }
                val removed = listOf(s.item(150_000, s.f.food, a), s.item(50_000, s.utilities, c))
                s.f.service.edit(s.id, s.withLines(removed))
                val naming = listOf(s.item(150_000, s.f.food, a), s.item(50_000, s.utilities, b))

                s.f
                    .shouldRefuse<LedgerError.NotFound> { s.f.service.edit(s.id, s.withLines(naming)) }
                    .what shouldBe "transaction_line"
            }

            test("a new transaction's lines name no ids") {
                val s = scene()

                s.f
                    .shouldRefuse<LedgerError.NotFound> {
                        s.f.service.create(s.f.draft(amount = 200_000).copy(lines = s.split()))
                    }.what shouldBe "transaction_line"
            }
        }

        context("references are checked only where they change, on every line") {
            test("an untouched archived category stays while another item is added") {
                val s = scene()
                s.f.kit.categories
                    .archive(s.f.food.meta.id)
                val lines = listOf(s.item(150_000, s.f.food, s.line1), s.item(50_000, s.utilities))

                val result = s.f.service.edit(s.id, s.withLines(lines))

                result.lines.map { it.categoryId } shouldBe listOf(s.f.food.meta.id, s.utilities.meta.id)
                s.live() shouldBe result.lines
            }

            test("a new item on an archived category, or with a deleted tag, is refused") {
                val s = scene()
                s.f.kit.categories
                    .archive(s.stationery.meta.id)
                val gone =
                    s.f.kit.tags
                        .create("Gone")
                s.f.kit.tags
                    .delete(gone.meta.id)
                val kept = s.item(150_000, s.f.food, s.line1)
                val onArchived = listOf(kept, s.item(50_000, s.stationery))
                val withDeletedTag = listOf(kept, s.item(50_000, s.utilities, tagIds = setOf(gone.meta.id)))

                s.f
                    .shouldRefuse<LedgerError.Archived> { s.f.service.edit(s.id, s.withLines(onArchived)) }
                    .what shouldBe "category"
                s.f
                    .shouldRefuse<LedgerError.Deleted> { s.f.service.edit(s.id, s.withLines(withDeletedTag)) }
                    .what shouldBe "tag"
            }

            test("changing an existing item to an archived category is refused") {
                val s = scene()
                val itemised = s.itemise()
                s.f.kit.categories
                    .archive(s.stationery.meta.id)
                val draft = s.f.draftOf(itemised)
                val moved = draft.lines.map { it.copy(categoryId = s.stationery.meta.id) }

                s.f
                    .shouldRefuse<LedgerError.Archived> { s.f.service.edit(s.id, draft.copy(lines = moved)) }
                    .what shouldBe "category"
            }

            test("the kind of every item is checked, not only the changed ones") {
                val s = scene()
                val draft = s.f.draftOf(s.itemise())
                val first = draft.lines.first().copy(categoryId = s.f.salary.meta.id)

                val asIncome = draft.copy(type = TransactionType.INCOME, lines = listOf(first) + draft.lines.drop(1))

                s.f.shouldRefuse<LedgerError.KindMismatch> { s.f.service.edit(s.id, asIncome) }
            }
        }

        context("income") {
            test("an income is itemised with income categories, and an expense category on it is refused") {
                val f = TransactionFixture().start()
                val income = CategoryKind.INCOME
                val bonus = f.kit.categories.create(null, income, CategoryDraft("Bonus", CategoryColour.SLATE))
                val pay = f.draft(TransactionType.INCOME, amount = 100_000, categoryId = f.salary.meta.id)
                val saved = f.service.create(pay)
                val base = f.draftOf(saved)
                val lineId =
                    saved.lines
                        .single()
                        .meta.id
                val salaryLine = LineDraft(lineId, Paise(70_000), f.salary.meta.id)

                val result =
                    f.service.edit(
                        saved.transaction.meta.id,
                        base.copy(lines = listOf(salaryLine, LineDraft(null, Paise(30_000), bonus.meta.id))),
                    )

                result.lines.map { it.categoryId } shouldBe listOf(f.salary.meta.id, bonus.meta.id)
                f.shouldRefuse<LedgerError.KindMismatch> {
                    val expense = LineDraft(null, Paise(30_000), f.food.meta.id)
                    f.service.edit(saved.transaction.meta.id, base.copy(lines = listOf(salaryLine, expense)))
                }
            }
        }

        context("create with several lines") {
            test("each line gets the deterministic id of its position and is audited and attributed") {
                val f = TransactionFixture().start()
                val lines =
                    listOf(
                        LineDraft(amount = Paise(60_000), categoryId = f.food.meta.id),
                        LineDraft(amount = Paise(30_000), categoryId = null, note = "Cab"),
                        LineDraft(
                            amount = Paise(10_000),
                            categoryId = f.food.meta.id,
                            tagIds = setOf(f.travel.meta.id),
                        ),
                    )

                val saved = f.service.create(f.draft(amount = 100_000).copy(lines = lines))

                val txn = saved.transaction.meta.id
                saved.lines.map { it.position } shouldBe listOf(1, 2, 3)
                saved.lines.map { it.meta.id.value } shouldBe
                    listOf("1", "2", "3").map { NameBasedId.of("transaction-line", txn.value, it) }
                saved.lines.map { it.note } shouldBe listOf(null, "Cab", null)
                f.service.get(txn) shouldBe saved
                f.trail.ofType("ledger_transaction").map { it.action } shouldBe listOf(AuditAction.CREATE)
                val creates = f.trail.ofType("transaction_line")
                creates.map { it.action } shouldBe List(3) { AuditAction.CREATE }
                creates.map { it.entityId } shouldBe saved.lines.map { it.meta.id.value }
                creates.forEach {
                    it.userId shouldBe f.me.user.id
                    it.deviceId shouldBe f.me.device.id
                }
            }

            test("items that do not add up are refused on create, writing nothing") {
                val f = TransactionFixture().start()
                val lines = listOf(60_000L, 30_000L).map { LineDraft(amount = Paise(it), categoryId = f.food.meta.id) }

                f.shouldRefuse<LedgerError.BreakdownMismatch> {
                    f.service.create(f.draft(amount = 100_000).copy(lines = lines))
                }
            }
        }

        context("auto-captured payment facts") {
            test("itemising keeps every payment fact exactly and writes no transaction entry") {
                val f = TransactionFixture().start()
                val seeded = f.seed()
                val food = f.food.meta.id
                val before = f.trail.size
                val draft = f.draftOf(seeded)
                val lines =
                    listOf(
                        draft.lines.single().copy(amount = Paise(1_000), categoryId = food),
                        LineDraft(null, Paise(1_500), food),
                    )

                val result = f.service.edit(seeded.transaction.meta.id, draft.copy(lines = lines))

                result.transaction shouldBe seeded.transaction
                f.storedTransactions() shouldBe listOf(seeded.transaction)
                result.transaction.state shouldBe TxnState.UNVERIFIED
                result.transaction.source shouldBe TxnSource.SMS
                result.transaction.bankRef shouldBe "UTR1"
                result.transaction.sourceMessageId shouldBe "msg-1"
                result.transaction.accountFragment shouldBe "1234"
                val written = f.trail.drop(before)
                written shouldHaveSize 2
                written.ofType("ledger_transaction") shouldHaveSize 0
                written.forEach { it.source shouldBe AuditSource.MANUAL }
            }
        }

        context("defence in depth (IT §3.3 rule 5)") {
            /**
             * A line store that loses its [dropAt]th insert and stores an extra copy of its [doubleAt]th
             * (1 is the first; 0 never) and passes every other call on.
             */
            class FaultyInserts(
                private val real: TransactionLineRepository,
                private val dropAt: Int = 0,
                private val doubleAt: Int = 0,
            ) : TransactionLineRepository by real {
                private var inserts = 0

                override suspend fun insert(entity: TransactionLine) {
                    inserts++
                    if (inserts != dropAt) real.insert(entity)
                    if (inserts == doubleAt) {
                        val copyId = TransactionLineId("${entity.meta.id.value}-copy")
                        real.insert(entity.copy(meta = entity.meta.copy(id = copyId)))
                    }
                }
            }

            test("a lost insert makes the edit fail closed, and everything rolls back") {
                val s = scene()
                val failing = s.f.serviceOver(FaultyInserts(s.f.kit.transactionLineRepository, dropAt = 2))

                // Lines 2 and 3 are inserted and the second insert is lost: the live lines sum to Rs 1,500.
                s.f
                    .shouldRefuse<IllegalStateException> { failing.edit(s.id, s.withLines(s.split())) }
                    .message shouldBe BALANCE_MESSAGE
            }

            test("an extra stored line makes the edit fail closed too: the sum must match exactly") {
                val s = scene()
                val failing = s.f.serviceOver(FaultyInserts(s.f.kit.transactionLineRepository, doubleAt = 2))

                // Line 3 is stored twice: the live lines sum to Rs 2,500.
                s.f
                    .shouldRefuse<IllegalStateException> { failing.edit(s.id, s.withLines(s.split())) }
                    .message shouldBe BALANCE_MESSAGE
            }

            test("the same edit over a store with no fault is saved, so only the fault failed the others") {
                val s = scene()
                val control = s.f.serviceOver(FaultyInserts(s.f.kit.transactionLineRepository))

                val saved = control.edit(s.id, s.withLines(s.split()))

                saved.lines.map { it.amount.value } shouldBe listOf(120_000L, 30_000L, 50_000L)
                s.live() shouldBe saved.lines
            }

            test("a lost insert fails a create closed as well") {
                val f = TransactionFixture().start()
                val failing = f.serviceOver(FaultyInserts(f.kit.transactionLineRepository, dropAt = 1))

                f
                    .shouldRefuse<IllegalStateException> { failing.create(f.draft()) }
                    .message shouldBe BALANCE_MESSAGE
            }
        }
    })

/** The fixed text of `TransactionLines.requireBalanced`'s refusal, so a test ties a failure to that check. */
private const val BALANCE_MESSAGE = "The items of a transaction must add up to its amount"
