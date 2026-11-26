package dev.imagh.pft.app.ui.home

import io.kotest.matchers.shouldBe
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

/** [greetingAt]: the time-of-day word comes from the local hour of an injected clock and zone. */
class GreetingTest {
    private val utc: ZoneId = ZoneId.of("UTC")

    private fun at(
        time: String,
        zone: ZoneId = utc,
    ): Greeting = greetingAt(Clock.fixed(Instant.parse("2026-11-26T${time}Z"), zone))

    @Test
    fun `05_00 is the first minute of morning and 04_59 is still evening`() {
        at("04:59:59") shouldBe Greeting.EVENING
        at("05:00:00") shouldBe Greeting.MORNING
    }

    @Test
    fun `11_59 is morning and 12_00 is afternoon`() {
        at("11:59:59") shouldBe Greeting.MORNING
        at("12:00:00") shouldBe Greeting.AFTERNOON
    }

    @Test
    fun `16_59 is afternoon and 17_00 is evening`() {
        at("16:59:59") shouldBe Greeting.AFTERNOON
        at("17:00:00") shouldBe Greeting.EVENING
    }

    @Test
    fun `midnight and late night are evening`() {
        at("00:00:00") shouldBe Greeting.EVENING
        at("23:59:59") shouldBe Greeting.EVENING
    }

    @Test
    fun `the clock's zone decides the hour, not UTC`() {
        // 03:00 UTC is 08:30 in Kolkata (morning) and 22:00 the evening before in New York.
        at("03:00:00", ZoneId.of("Asia/Kolkata")) shouldBe Greeting.MORNING
        at("03:00:00", ZoneId.of("America/New_York")) shouldBe Greeting.EVENING
    }
}
