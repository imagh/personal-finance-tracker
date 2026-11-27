package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.category.Category
import dev.imagh.pft.ledger.category.CategoryColour
import dev.imagh.pft.ledger.category.CategoryKind
import dev.imagh.pft.ledger.entity.EntityMeta
import dev.imagh.pft.ledger.ids.CategoryId
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.money.Paise
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

private val at = Instant.parse("2026-11-01T00:00:00Z")

private fun cat(
    id: String,
    parent: String? = null,
    archived: Boolean = false,
) = Category(
    meta =
        EntityMeta(
            CategoryId(id),
            at,
            at,
            UserId("u"),
            DeviceId("d"),
            archivedAt = if (archived) at else null,
        ),
    parentId = parent?.let(::CategoryId),
    kind = CategoryKind.EXPENSE,
    name = id,
    colour = CategoryColour.SLATE,
    iconKey = null,
    sortOrder = 0,
    systemKey = null,
)

private fun ids(vararg v: String) = v.map(::CategoryId).toSet()

/** Slice C D5: how a filter is validated, how a parent category expands, and how a line matches it. */
class TransactionFilterTest :
    FunSpec({
        context("validation") {
            test("a from date after its to date is refused") {
                shouldThrow<LedgerError.InvalidRange> {
                    TransactionFilter(from = LocalDate.of(2026, 11, 2), to = LocalDate.of(2026, 11, 1))
                }
            }

            test("from equal to to is one day, and either end alone is fine") {
                val day = LocalDate.of(2026, 11, 1)
                TransactionFilter(from = day, to = day).from shouldBe day
                TransactionFilter(from = day).to shouldBe null
                TransactionFilter(to = day).from shouldBe null
            }

            test("an amountMin above its amountMax is refused, equal is fine") {
                shouldThrow<LedgerError.InvalidRange> {
                    TransactionFilter(amountMin = Paise(101), amountMax = Paise(100))
                }
                TransactionFilter(amountMin = Paise(100), amountMax = Paise(100)).amountMin shouldBe Paise(100)
            }

            test("copy() cannot build a backwards range either") {
                shouldThrow<LedgerError.InvalidRange> {
                    TransactionFilter(amountMin = Paise(1)).copy(amountMax = Paise(0))
                }
            }

            test("a date outside years 1 to 9999 is refused, so the window never overflows") {
                shouldThrow<LedgerError.InvalidRange> { TransactionFilter(to = LocalDate.MAX) }
                shouldThrow<LedgerError.InvalidRange> { TransactionFilter(from = LocalDate.MIN) }
                shouldThrow<LedgerError.InvalidRange> { TransactionFilter(to = LocalDate.of(10_000, 1, 1)) }
                TransactionFilter(from = LocalDate.of(1, 1, 1), to = LocalDate.of(9999, 12, 31)).to shouldBe
                    LocalDate.of(9999, 12, 31)
            }
        }

        context("createdBy") {
            test("createdBy defaults to empty") {
                TransactionFilter().createdBy shouldBe emptySet()
            }

            test("a person is a transaction-level filter: it is not a line filter and does not touch the window") {
                val filter = TransactionFilter(createdBy = setOf(UserId("u1"), UserId("u2")))
                val zone = ZoneId.of("Asia/Kolkata")

                filter.createdBy shouldBe setOf(UserId("u1"), UserId("u2"))
                filter.hasLineFilter shouldBe false
                filter.window(zone) shouldBe TransactionFilter().window(zone)
                filter.window(zone, YearMonth.of(2026, 11)) shouldBe
                    TransactionFilter().window(zone, YearMonth.of(2026, 11))
            }
        }

        context("text") {
            test("the three text filters default to none") {
                val filter = TransactionFilter()

                filter.text shouldBe null
                filter.titleText shouldBe null
                filter.noteText shouldBe null
            }

            test("text is a transaction-level filter: not a line filter, and it does not touch the window") {
                val filter = TransactionFilter(text = "swiggy", titleText = "swi", noteText = "note")
                val zone = ZoneId.of("Asia/Kolkata")

                filter.hasLineFilter shouldBe false
                filter.window(zone) shouldBe TransactionFilter().window(zone)
            }

            test("text of exactly the limit is accepted, one more is refused, in each of the three") {
                val limit = LedgerLimits.MAX_SEARCH_TEXT_LENGTH
                val fits = "a".repeat(limit)
                val over = "a".repeat(limit + 1)

                TransactionFilter(text = fits, titleText = fits, noteText = fits).text shouldBe fits
                shouldThrow<LedgerError.NameTooLong> { TransactionFilter(text = over) }.max shouldBe limit
                shouldThrow<LedgerError.NameTooLong> { TransactionFilter(titleText = over) }.max shouldBe limit
                shouldThrow<LedgerError.NameTooLong> { TransactionFilter(noteText = over) }.max shouldBe limit
            }

            test("copy() is checked too") {
                shouldThrow<LedgerError.NameTooLong> {
                    TransactionFilter().copy(text = "a".repeat(LedgerLimits.MAX_SEARCH_TEXT_LENGTH + 1))
                }
            }

            test("blank or punctuation-only text is kept as given, never read as no filter") {
                TransactionFilter(text = "").text shouldBe ""
                TransactionFilter(titleText = "  ").titleText shouldBe "  "
                TransactionFilter(noteText = "*").noteText shouldBe "*"
            }

            test("the refusal names no text") {
                val error = shouldThrow<LedgerError.NameTooLong> { TransactionFilter(text = "secret".repeat(100)) }

                error.message!!.contains("secret") shouldBe false
            }
        }

        context("categoryScope") {
            val all =
                listOf(
                    cat("food"),
                    cat("groceries", parent = "food"),
                    cat("dining", parent = "food", archived = true),
                    cat("travel"),
                    cat("flights", parent = "travel"),
                )

            test("a parent expands to itself and all its children, archived ones too") {
                val scope = TransactionFilter(categoryIds = ids("food")).categoryScope(all)

                scope shouldBe ids("food", "groceries", "dining")
            }

            test("a child stands for itself only, and parents expand independently") {
                TransactionFilter(categoryIds = ids("groceries")).categoryScope(all) shouldBe ids("groceries")
                TransactionFilter(categoryIds = ids("groceries", "travel")).categoryScope(all) shouldBe
                    ids("groceries", "travel", "flights")
            }

            test("an id that is not in the list stays in the scope, so it matches nothing new") {
                TransactionFilter(categoryIds = ids("gone")).categoryScope(all) shouldBe ids("gone")
            }

            test("no category filter is an empty scope") {
                TransactionFilter().categoryScope(all) shouldBe emptySet()
            }
        }

        context("matchesLine: one line must satisfy the category and the tag filter together") {
            val food = ids("food", "groceries")

            test("with no line filter every line matches") {
                TransactionFilter().hasLineFilter shouldBe false
                TransactionFilter().matchesLine(line("t", category = null), emptySet()) shouldBe true
            }

            test("category and tag filters are line filters") {
                TransactionFilter(categoryIds = ids("food")).hasLineFilter shouldBe true
                TransactionFilter(tagIds = setOf(TagId("trip"))).hasLineFilter shouldBe true
            }

            test("a category matches by scope, so a child line matches its parent's filter") {
                val filter = TransactionFilter(categoryIds = ids("food"))
                filter.matchesLine(line("t", category = "groceries"), food) shouldBe true
                filter.matchesLine(line("t", category = "travel"), food) shouldBe false
                filter.matchesLine(line("t", category = null), food) shouldBe false
            }

            test("tags are any-of") {
                val filter = TransactionFilter(tagIds = setOf(TagId("trip"), TagId("gift")))
                filter.matchesLine(line("t", tags = setOf("gift", "other")), emptySet()) shouldBe true
                filter.matchesLine(line("t", tags = setOf("other")), emptySet()) shouldBe false
                filter.matchesLine(line("t"), emptySet()) shouldBe false
            }

            test("both filters must hold on the same line, not on two different lines") {
                val filter = TransactionFilter(categoryIds = ids("food"), tagIds = setOf(TagId("trip")))
                filter.matchesLine(line("t", category = "food", tags = setOf("trip")), food) shouldBe true
                filter.matchesLine(line("t", category = "food"), food) shouldBe false
                filter.matchesLine(line("t", category = "travel", tags = setOf("trip")), food) shouldBe false
            }
        }
    })
