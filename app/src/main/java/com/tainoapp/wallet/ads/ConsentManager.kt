package com.tainoapp.wallet.ads

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

/**
 * Wraps Google's User Messaging Platform (UMP). Ads must only be initialized and requested
 * once [canRequestAds] is true.
 */
class ConsentManager(context: Context) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context.applicationContext)

    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    val isPrivacyOptionsRequired: Boolean
        get() = consentInformation.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    /**
     * Refreshes consent info and shows the consent form if one is required.
     * [onCanRequestAds] is invoked at most once, as soon as ads may be requested; that can be
     * immediately when consent was already gathered in a previous session.
     */
    fun gatherConsent(activity: Activity, onCanRequestAds: () -> Unit) {
        var notified = false
        fun notifyIfAllowed() {
            if (!notified && consentInformation.canRequestAds()) {
                notified = true
                onCanRequestAds()
            }
        }

        consentInformation.requestConsentInfoUpdate(
            activity,
            ConsentRequestParameters.Builder().build(),
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    notifyIfAllowed()
                }
            },
            { notifyIfAllowed() }
        )
        notifyIfAllowed()
    }

    fun showPrivacyOptionsForm(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { }
    }
}
