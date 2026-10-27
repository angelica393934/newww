package com.nbs.banking.core.analytics.tools

import android.content.Context
import javax.inject.Inject

class AppsflyerProvider @Inject constructor(private val context: Context) : AnalyticsProvider {
    override val identifier: AnalyticsIdentifier = AnalyticsIdentifier.APPSFLYER

    override fun setAnalyticsId(analyticsId: String) {
        // Not yet Implemented
    }

    override fun setDefaultProperties(properties: Map<String, Any?>) {
        // Doesn't need default properties
    }

    override fun logEvent(event: String, params: Map<String, Any?>) {
//        AppsFlyerLib.getInstance().apply {
//            setCollectIMEI(true)
//            setCollectAndroidID(true)
//            logEvent(
//                context,
//                event,
//                params,
//                object : AppsFlyerRequestListener {
//                    override fun onSuccess() {
//                        // no-op
//                    }
//
//                    override fun onError(p0: Int, p1: String) {
//                        // no-op
//                    }
//                },
//            )
//        }
    }

    override fun logError(throwable: Throwable, params: Map<String, Any?>) {
        // Not yet Implemented
    }
}
