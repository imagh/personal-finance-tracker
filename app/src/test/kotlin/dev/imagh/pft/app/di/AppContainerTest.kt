package dev.imagh.pft.app.di

import android.os.Looper
import dev.imagh.pft.app.data.DatabaseStartup
import dev.imagh.pft.app.data.PftDatabase
import dev.imagh.pft.app.security.DbKeyUnavailable.Reason
import dev.imagh.pft.app.testing.TestDatabase
import dev.imagh.pft.app.testing.runSuspending
import dev.imagh.pft.ledger.fakes.TestClock
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * [AppContainer] opens the database once, off the caller's thread, without blocking the caller, and
 * fails closed. The test thread is Robolectric's main thread, so "not the test thread" is "not the
 * main thread". Slow and failing opens are driven through the container's `openDatabase` seam.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class AppContainerTest {
    private val databases = CopyOnWriteArrayList<PftDatabase>()

    /** A fresh in-memory database, tracked so [closeDatabases] closes it. */
    private fun newDatabase(): PftDatabase = TestDatabase.create().also { databases += it }

    @After
    fun closeDatabases() = databases.forEach { it.close() }

    private fun container(
        baseClock: java.time.Clock = TestClock(),
        openDatabase: () -> DatabaseStartup,
    ) = AppContainer(openDatabase, baseClock)

    /** Waits up to ten seconds, so a regression fails the test instead of hanging the build. */
    private fun CountDownLatch.awaitOrFail() = await(10, TimeUnit.SECONDS) shouldBe true

    @Test
    fun `the database opens off the calling thread`() =
        runSuspending {
            val openedOn = CopyOnWriteArrayList<Thread>()
            val container =
                container {
                    openedOn += Thread.currentThread()
                    DatabaseStartup.Ready(newDatabase())
                }

            val start = container.open()

            start::class shouldBe AppStart.Ready::class
            openedOn.size shouldBe 1
            openedOn.single() shouldNotBe Thread.currentThread()
            openedOn.single() shouldNotBe Looper.getMainLooper().thread
        }

    @Test
    fun `a slow open does not block the caller`() =
        runSuspending {
            val opening = CountDownLatch(1)
            val release = CountDownLatch(1)
            val container =
                container {
                    opening.countDown()
                    release.awaitOrFail()
                    DatabaseStartup.Ready(newDatabase())
                }

            // The caller runs on this very thread: if open() blocked it, yield() would not return
            // until the opener finished (and the opener only finishes when released below).
            val caller = async { container.open() }
            yield()
            opening.awaitOrFail()
            caller.isCompleted shouldBe false

            release.countDown()
            caller.await()::class shouldBe AppStart.Ready::class
        }

    @Test
    fun `concurrent callers open the database once and share the one result`() =
        runSuspending {
            val opens = AtomicInteger()
            val gate = CountDownLatch(1)
            val container =
                container {
                    opens.incrementAndGet()
                    gate.awaitOrFail()
                    DatabaseStartup.Ready(newDatabase())
                }
            val callers = 8
            val started = CountDownLatch(callers)

            val results =
                (1..callers).map {
                    async(Dispatchers.Default) {
                        started.countDown()
                        container.open()
                    }
                }
            started.awaitOrFail()
            gate.countDown()
            val all = results.awaitAll()

            opens.get() shouldBe 1
            val first = all.first() as AppStart.Ready
            all.forEach { (it as AppStart.Ready).services shouldBeSameInstanceAs first.services }
            // A later call, after the open finished, still shares it and still does not reopen.
            (container.open() as AppStart.Ready).services shouldBeSameInstanceAs first.services
            opens.get() shouldBe 1
        }

    @Test
    fun `a caller that gives up does not cancel the open for the others`() =
        runSuspending {
            val opens = AtomicInteger()
            val opening = CountDownLatch(1)
            val release = CountDownLatch(1)
            val container =
                container {
                    opens.incrementAndGet()
                    opening.countDown()
                    release.awaitOrFail()
                    DatabaseStartup.Ready(newDatabase())
                }

            val impatient = launch { container.open() }
            yield()
            opening.awaitOrFail()
            impatient.cancelAndJoin()
            release.countDown()

            container.open()::class shouldBe AppStart.Ready::class
            opens.get() shouldBe 1
        }

    @Test
    fun `a key that cannot be produced fails closed and builds no services`() =
        runSuspending {
            val container = container { DatabaseStartup.KeyUnavailable(Reason.KEK_MISSING) }

            container.open() shouldBe AppStart.KeyUnavailable(Reason.KEK_MISSING)
            container.open() shouldBe AppStart.KeyUnavailable(Reason.KEK_MISSING)
        }

    @Test
    fun `an open that throws propagates to every caller and is never cached as Ready`() =
        runSuspending {
            val opens = AtomicInteger()
            val container =
                container {
                    opens.incrementAndGet()
                    error("backup failed")
                }

            shouldThrow<IllegalStateException> { container.open() }
            shouldThrow<IllegalStateException> { container.open() }
            opens.get() shouldBe 1
        }

    @Test
    fun `services are built on a millisecond clock even when the base clock is finer`() =
        runSuspending {
            val sub = TestClock(Instant.parse("2026-11-01T10:00:00.123456789Z"))
            val container = container(baseClock = sub) { DatabaseStartup.Ready(newDatabase()) }
            val services = (container.open() as AppStart.Ready).services

            val created = services.identity.initialise("Akash", "Pixel")

            services.identity.currentIdentity() shouldBe created
            val times =
                services.database.openHelper.readableDatabase.query("SELECT at FROM audit_entry").use { cursor ->
                    buildList { while (cursor.moveToNext()) add(cursor.getLong(0)) }
                }
            times shouldBe List(3) { Instant.parse("2026-11-01T10:00:00.123Z").toEpochMilli() }
        }
}
