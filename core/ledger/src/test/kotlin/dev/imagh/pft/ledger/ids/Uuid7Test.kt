package dev.imagh.pft.ledger.ids

import dev.imagh.pft.ledger.fakes.TestClock
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeSorted
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll
import java.time.Duration
import java.time.Instant
import java.util.Random

private fun clockAt(millis: Long) = TestClock(Instant.ofEpochMilli(millis))

/** The 48-bit millisecond timestamp encoded in the first 12 hex digits of a UUIDv7. */
private fun timestampOf(uuid: String): Long = uuid.replace("-", "").substring(0, 12).toLong(16)

class Uuid7Test :
    FunSpec({
        test("has the 8-4-4-4-12 text form, lower-case hex, 36 chars") {
            val id = Uuid7(clockAt(1_700_000_000_000)).next()
            id.length shouldBe 36
            id shouldMatch Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
        }

        test("version nibble is 7 and the variant bits are 0b10") {
            val ids = Uuid7(clockAt(1_700_000_000_000))
            repeat(1_000) {
                val id = ids.next()
                id[14] shouldBe '7'
                (id[19] in "89ab") shouldBe true
            }
        }

        test("encodes the clock's milliseconds in the leading 48 bits") {
            val millis = 1_700_000_123_456
            timestampOf(Uuid7(clockAt(millis)).next()) shouldBe millis
        }

        test("IDs from increasing milliseconds sort lexicographically") {
            val clock = clockAt(1_700_000_000_000)
            val ids = Uuid7(clock)
            val generated =
                List(500) {
                    clock.advance(Duration.ofMillis(1))
                    ids.next()
                }
            generated.shouldBeSorted()
            generated.toSet().size shouldBe generated.size
        }

        test("IDs from the same millisecond still sort in creation order") {
            // More than the 12-bit counter can hold in one millisecond, so the rollover is covered.
            val ids = Uuid7(clockAt(1_700_000_000_000))
            val generated = List(10_000) { ids.next() }
            generated.shouldBeSorted()
        }

        test("10,000 IDs are unique") {
            val ids = Uuid7(clockAt(1_700_000_000_000))
            List(10_000) { ids.next() }.toSet().size shouldBe 10_000
        }

        test("a clock that goes backwards never produces a smaller ID") {
            val clock = clockAt(1_700_000_000_000)
            val ids = Uuid7(clock)
            val before = ids.next()
            clock.advance(Duration.ofSeconds(-5))
            val after = ids.next()
            (after > before) shouldBe true
        }

        test("the default constructor uses the system clock and SecureRandom") {
            val before = System.currentTimeMillis()
            val id = Uuid7().next()
            val after = System.currentTimeMillis()
            (timestampOf(id) in before..after) shouldBe true
        }

        test("two generators with different randomness do not collide") {
            val clock = clockAt(1_700_000_000_000)
            val a = Uuid7(clock, Random(1)).next()
            val b = Uuid7(clock, Random(2)).next()
            (a == b) shouldBe false
        }

        test("any representable timestamp keeps version and variant (property)") {
            checkAll(Arb.long(0L..0xFFFF_FFFF_FFFEL)) { millis ->
                val id = Uuid7(clockAt(millis)).next()
                id[14] shouldBe '7'
                (id[19] in "89ab") shouldBe true
                timestampOf(id) shouldBe millis
            }
        }

        test("rejects a clock outside the 48-bit range") {
            shouldThrow<IllegalArgumentException> {
                Uuid7(clockAt(-1)).next()
            }
            shouldThrow<IllegalArgumentException> {
                Uuid7(clockAt(0x1_0000_0000_0000L)).next()
            }
        }
    })
