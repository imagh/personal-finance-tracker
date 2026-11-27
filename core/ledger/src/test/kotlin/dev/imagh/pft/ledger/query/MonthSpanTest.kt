package dev.imagh.pft.ledger.query

import dev.imagh.pft.ledger.validation.LedgerError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

private val kolkata = ZoneId.of("Asia/Kolkata")

private fun ym(
    year: Int,
    month: Int,
) = YearMonth.of(year, month)

private fun span(
    from: YearMonth,
    to: YearMonth,
) = MonthSpan(from, to)

/** Slice C C3c: the month strip's range is a pure core rule; the UI never computes it. */
class MonthSpanTest :
    FunSpec({
        context("a span") {
            test("a first month after the last is refused") {
                shouldThrow<LedgerError.InvalidRange> { MonthSpan(ym(2026, 11), ym(2026, 10)) }
            }

            test("one month is a span of itself") {
                MonthSpan(ym(2026, 11), ym(2026, 11)) shouldBe MonthSpan(ym(2026, 11), ym(2026, 11))
            }
        }

        context("the strip rule min(first, now)..max(last, now)") {
            val entries = span(ym(2026, 3), ym(2026, 8))

            test("now inside the entries leaves the span") {
                MonthSpan.strip(entries, ym(2026, 5)) shouldBe entries
            }

            test("now after the last entry extends the end") {
                MonthSpan.strip(entries, ym(2026, 11)) shouldBe span(ym(2026, 3), ym(2026, 11))
            }

            test("now before the first entry extends the start") {
                MonthSpan.strip(entries, ym(2025, 12)) shouldBe span(ym(2025, 12), ym(2026, 8))
            }

            test("now at either end changes nothing") {
                MonthSpan.strip(entries, ym(2026, 3)) shouldBe entries
                MonthSpan.strip(entries, ym(2026, 8)) shouldBe entries
            }

            test("with no entries the strip is the current month alone") {
                MonthSpan.strip(null, ym(2026, 11)) shouldBe span(ym(2026, 11), ym(2026, 11))
            }

            test("a span including a month widens by that month only") {
                entries.including(ym(2027, 1)) shouldBe span(ym(2026, 3), ym(2027, 1))
                entries.including(ym(2025, 1)) shouldBe span(ym(2025, 1), ym(2026, 8))
            }
        }

        context("a span from two instants") {
            fun ms(iso: String) = Instant.parse(iso).toEpochMilli()

            test("months are read in the zone: 23:30 UTC on 31 October is already 1 November in Kolkata") {
                val s = MonthSpan.ofInstants(ms("2026-10-31T23:30:00Z"), ms("2026-12-31T20:00:00Z"), kolkata)

                s shouldBe span(ym(2026, 11), ym(2027, 1)) // 20:00Z on 31 Dec is 01:30 on 1 Jan in Kolkata
            }

            test("the same instant is one month") {
                val t = ms("2026-11-15T10:00:00Z")

                MonthSpan.ofInstants(t, t, kolkata) shouldBe span(ym(2026, 11), ym(2026, 11))
            }

            test("the first month is read from the first instant and the last from the last") {
                MonthSpan.ofInstants(ms("2025-01-10T00:00:00Z"), ms("2026-06-10T00:00:00Z"), kolkata) shouldBe
                    span(ym(2025, 1), ym(2026, 6))
            }
        }
    })
