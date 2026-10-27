package com.nbs.banking.core.analytics.tools

import com.google.firebase.analytics.FirebaseAnalytics
import com.nbs.banking.core.extensions.toBundle
import javax.inject.Inject

class FirebaseAnalyticsProvider @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsProvider {
    override val identifier: AnalyticsIdentifier = AnalyticsIdentifier.FIREBASE_ANALYTICS

    override fun setAnalyticsId(analyticsId: String) {
        firebaseAnalytics.setUserId(analyticsId)
    }

    override fun setDefaultProperties(properties: Map<String, Any?>) {
        firebaseAnalytics.setDefaultEventParameters(properties.toBundle())
    }

    override fun logEvent(event: String, params: Map<String, Any?>) {
//        if (FeatureFlag.isEnabled(FlagKey.LOG_FIREBASE_ANALYTICS)) {
//            firebaseAnalytics.logEvent(event, params.toBundle())
//        }
    }

    override fun logError(throwable: Throwable, params: Map<String, Any?>) {
        // Not yet implemented
    }
}
