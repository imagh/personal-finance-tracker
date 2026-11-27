package dev.imagh.pft.ledger.query

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

private val ist = ZoneId.of("Asia/Kolkata")

private fun ms(iso: String) = Instant.parse(iso).toEpochMilli()

private fun TimeWindow.contains(iso: String) = ms(iso) >= startMs && ms(iso) < endMs

/** Slice C D5 (decision 5): the month window is half-open, in the caller's zone, computed in core. */
class TimeWindowTest :
    FunSpec({
        val october = YearMonth.of(2026, 10)

        test("a month is [start of the month, start of the next month) in the zone, as epoch ms") {
            val window = TransactionFilter().window(ist, october)

            window shouldBe TimeWindow(ms("2026-09-30T18:30:00Z"), ms("2026-10-31T18:30:00Z"))
        }

        test("23:30 IST on the last day is in the month and 00:10 IST the next day is not") {
            val window = TransactionFilter().window(ist, october)

            window.contains("2026-10-31T18:00:00Z") shouldBe true // 2026-10-31 23:30 IST
            window.contains("2026-10-31T18:40:00Z") shouldBe false // 2026-11-01 00:10 IST
            window.contains("2026-09-30T18:30:00Z") shouldBe true // the first instant, inclusive
            window.contains("2026-09-30T18:29:59Z") shouldBe false
        }

        test("the next month starts where this one ends, so no instant is in both or neither") {
            val endOfOctober = TransactionFilter().window(ist, october).endMs
            val startOfNovember = TransactionFilter().window(ist, YearMonth.of(2026, 11)).startMs

            endOfOctober shouldBe startOfNovember
        }

        test("a month follows the zone's own clock, not 30 days or 24-hour days") {
            val newYork = ZoneId.of("America/New_York")
            val window = TransactionFilter().window(newYork, YearMonth.of(2026, 3))

            window.startMs shouldBe ms("2026-03-01T05:00:00Z")
            window.endMs shouldBe ms("2026-04-01T04:00:00Z") // daylight saving began on 8 March
        }

        test("from and to narrow the month: from is the start of its day, to includes its whole day") {
            val filter = TransactionFilter(from = LocalDate.of(2026, 10, 10), to = LocalDate.of(2026, 10, 20))

            filter.window(ist, october) shouldBe TimeWindow(ms("2026-10-09T18:30:00Z"), ms("2026-10-20T18:30:00Z"))
        }

        test("a range wider than the month does not widen it") {
            val filter = TransactionFilter(from = LocalDate.of(2026, 9, 1), to = LocalDate.of(2026, 12, 31))

            filter.window(ist, october) shouldBe TransactionFilter().window(ist, october)
        }

        test("a range outside the month leaves an empty window") {
            val window = TransactionFilter(from = LocalDate.of(2026, 11, 5)).window(ist, october)

            (window.startMs >= window.endMs) shouldBe true
        }

        test("without a month, only from and to bound the window, and no bound is unbounded") {
            TransactionFilter().window(ist) shouldBe TimeWindow(Long.MIN_VALUE, Long.MAX_VALUE)
            TransactionFilter(from = LocalDate.of(2026, 10, 10)).window(ist) shouldBe
                TimeWindow(ms("2026-10-09T18:30:00Z"), Long.MAX_VALUE)
            TransactionFilter(to = LocalDate.of(2026, 10, 10)).window(ist) shouldBe
                TimeWindow(Long.MIN_VALUE, ms("2026-10-10T18:30:00Z"))
        }

        test("an extreme month is not clamped: its window fails loudly with an ArithmeticException") {
            shouldThrow<ArithmeticException> { TransactionFilter().window(ist, YearMonth.of(999_999_999, 6)) }
        }
    })
