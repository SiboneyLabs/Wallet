package com.tainoapp.wallet.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.tainoapp.wallet.BuildConfig

/**
 * Loads a single interstitial ad and shows it at most once per app session.
 * Held by the Application, so the "shown" flag and the countdown survive activity recreation
 * (rotation) and only reset when the process dies; that is what counts as one session.
 * All methods must be called from the main thread.
 */
class InterstitialAdManager(
    context: Context,
    private val adUnitId: String = BuildConfig.ADMOB_INTERSTITIAL_ID,
    private val elapsedRealtime: () -> Long = SystemClock::elapsedRealtime
) {
    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())

    private var initializationStarted = false
    private var isLoading = false
    private var interstitialAd: InterstitialAd? = null
    private var showing = false

    private val scheduler = LaunchAdScheduler(LAUNCH_DELAY_MS, RETRY_INTERVAL_MS, elapsedRealtime)

    /** Set by the UI while ads must not appear (for example, on the app lock screen). */
    @Volatile
    var isSuppressed = false

    var hasShownThisSession = false
        private set

    val isReady: Boolean
        get() = interstitialAd != null

    /** Initializes the Mobile Ads SDK once and preloads the ad. Call only when consent allows ads. */
    fun initializeAndLoad() {
        if (!ADS_ENABLED || initializationStarted) return
        initializationStarted = true
        Thread {
            MobileAds.initialize(appContext) { mainHandler.post { load() } }
        }.start()
    }

    private fun load() {
        if (isLoading || interstitialAd != null || hasShownThisSession) return
        isLoading = true
        InterstitialAd.load(
            appContext,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    isLoading = false
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoading = false
                    interstitialAd = null
                }
            }
        )
    }

    /**
     * Shows the ad once after [LAUNCH_DELAY_MS] of total foreground time. Run it inside
     * repeatOnLifecycle(STARTED) so backgrounding pauses the countdown.
     */
    suspend fun showAfterLaunchDelay(activity: Activity, canShowNow: () -> Boolean) {
        if (!ADS_ENABLED) return
        scheduler.run(
            isReady = { isReady },
            canShowNow = { !isSuppressed && canShowNow() },
            tryShow = { tryShow(activity) }
        )
    }

    /** Shows the ad if it is loaded and none was shown this session. Returns true if shown. */
    fun tryShow(activity: Activity): Boolean {
        val ad = interstitialAd
        if (ad == null || hasShownThisSession || showing) return false
        if (activity.isFinishing || activity.isDestroyed) return false
        showing = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                hasShownThisSession = true
            }

            override fun onAdDismissedFullScreenContent() {
                clear()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                clear()
            }
        }
        ad.show(activity)
        return true
    }

    private fun clear() {
        interstitialAd = null
        showing = false
    }

    companion object {
        /** Kill switch: set to false to disable all ad loading and showing. */
        const val ADS_ENABLED = true
        const val LAUNCH_DELAY_MS = 60_000L
        private const val RETRY_INTERVAL_MS = 1_000L
    }
}
