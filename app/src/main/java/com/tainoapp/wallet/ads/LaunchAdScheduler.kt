package com.tainoapp.wallet.ads

import kotlinx.coroutines.delay

/**
 * Decides when the once-per-session ad may be shown: after [delayMs] of total foreground time.
 * Pure Kotlin so the gating logic can be unit tested. Run [run] inside repeatOnLifecycle(STARTED):
 * cancellation pauses the countdown and the remaining time carries over to the next run.
 */
class LaunchAdScheduler(
    private val delayMs: Long,
    private val retryIntervalMs: Long,
    private val elapsedRealtime: () -> Long
) {
    private var remainingMs = delayMs

    var isFinished = false
        private set

    /**
     * [isReady]: an ad is loaded. If not when the delay ends, the session gets no ad.
     * [canShowNow]: nothing is in the way right now; checked repeatedly until true.
     * [tryShow]: attempts to show the ad and returns whether it was shown.
     */
    suspend fun run(
        isReady: () -> Boolean,
        canShowNow: () -> Boolean,
        tryShow: () -> Boolean
    ) {
        if (isFinished) return
        if (remainingMs > 0) {
            val startedAt = elapsedRealtime()
            try {
                delay(remainingMs)
            } finally {
                remainingMs = (remainingMs - (elapsedRealtime() - startedAt)).coerceAtLeast(0L)
            }
        }
        if (!isReady()) {
            isFinished = true
            return
        }
        while (true) {
            if (canShowNow() && tryShow()) {
                isFinished = true
                return
            }
            delay(retryIntervalMs)
        }
    }
}
