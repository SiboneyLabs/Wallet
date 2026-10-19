package com.tainoapp.wallet.ads

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LaunchAdSchedulerTest {

    private fun TestScope.scheduler() =
        LaunchAdScheduler(delayMs = 60_000, retryIntervalMs = 1_000, elapsedRealtime = { currentTime })

    @Test
    fun doesNotShowBeforeDelay() = runTest {
        var shown = 0
        val job = launch { scheduler().run({ true }, { true }, { shown++; true }) }
        advanceTimeBy(59_999)
        runCurrent()
        assertEquals(0, shown)
        job.cancel()
    }

    @Test
    fun showsOnceAfterDelay() = runTest {
        var shown = 0
        val scheduler = scheduler()
        launch { scheduler.run({ true }, { true }, { shown++; true }) }
        advanceTimeBy(60_001)
        runCurrent()
        assertEquals(1, shown)
        assertTrue(scheduler.isFinished)

        // Running again (new foreground period) must not show a second ad.
        launch { scheduler.run({ true }, { true }, { shown++; true }) }
        advanceTimeBy(120_000)
        runCurrent()
        assertEquals(1, shown)
    }

    @Test
    fun skipsSessionWhenAdNotLoadedAtDelayEnd() = runTest {
        var shown = 0
        val scheduler = scheduler()
        launch { scheduler.run({ false }, { true }, { shown++; true }) }
        advanceTimeBy(61_000)
        runCurrent()
        assertEquals(0, shown)
        assertTrue(scheduler.isFinished)
    }

    @Test
    fun backgroundingPausesCountdown() = runTest {
        var shown = 0
        val scheduler = scheduler()
        val first = launch { scheduler.run({ true }, { true }, { shown++; true }) }
        advanceTimeBy(40_000)
        first.cancel()
        runCurrent()

        // 100 seconds in the background must not count.
        advanceTimeBy(100_000)
        launch { scheduler.run({ true }, { true }, { shown++; true }) }
        advanceTimeBy(19_000)
        runCurrent()
        assertEquals(0, shown)
        advanceTimeBy(2_000)
        runCurrent()
        assertEquals(1, shown)
    }

    @Test
    fun waitsWhileSomethingIsInTheWay() = runTest {
        var shown = 0
        var blocked = true
        val scheduler = scheduler()
        launch { scheduler.run({ true }, { !blocked }, { shown++; true }) }
        advanceTimeBy(70_000)
        runCurrent()
        assertEquals(0, shown)
        assertFalse(scheduler.isFinished)

        blocked = false
        advanceTimeBy(1_500)
        runCurrent()
        assertEquals(1, shown)
    }
}
