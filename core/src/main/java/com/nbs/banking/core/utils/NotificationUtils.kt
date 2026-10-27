package com.nbs.banking.core.utils

import timber.log.Timber
import androidx.core.net.toUri

object NotificationUtils {
    fun parseDeeplink(url: String): NotificationDeeplinkData? = try {
        val uri = url.toUri()
        val linkScreen = uri.scheme + "://" + uri.authority + uri.path
        val auth = uri.getQueryParameter("auth")?.toBoolean() ?: true

        val id = when {
            uri.getQueryParameter("transactionId") != null -> uri.getQueryParameter("transactionId")
            uri.getQueryParameter("referenceId") != null -> uri.getQueryParameter("referenceId")
            uri.getQueryParameter("type") != null -> uri.getQueryParameter("type")
            else -> null
        }

        NotificationDeeplinkData(linkScreen, auth, id)
    } catch (e: UnsupportedOperationException) {
        Timber.e(e)
        null
    } catch (e: NullPointerException) {
        Timber.e(e)
        null
    }
}

data class NotificationDeeplinkData(
    val linkScreen: String = "",
    val auth: Boolean = true,
    val id: String? = "",
)
