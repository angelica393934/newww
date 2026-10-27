package com.nbs.banking.core.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.nbs.banking.core.base.BaseNotificationBuilder
import javax.inject.Inject
import timber.log.Timber
import androidx.core.net.toUri

class NotificationBuilder @Inject constructor(context: Context) : BaseNotificationBuilder(context) {
    companion object {
        const val MAIN_ACTIVITY = ""
        const val DEEPLINK = "deeplink"
        const val IMAGE_URL = "image"
        const val TITLE = "title"
        const val BODY = "body"
    }

    fun createCommonNotification(remoteData: Map<String?, String?>): NotificationCompat.Builder {
        try {
            val deeplink = remoteData[DEEPLINK].orEmpty()
            Timber.d("Remote message: $remoteData")
            Timber.d("Deeplink: $deeplink")

            val intent = Intent(context, Class.forName(MAIN_ACTIVITY)).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                data = deeplink.toUri()
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE,
            )

            return getBaseNotificationBuilder(remoteData).setContentIntent(pendingIntent)
        } catch (e: Exception) {
            Timber.e(e)
            return getBaseNotificationBuilder(remoteData)
        }
    }
}
