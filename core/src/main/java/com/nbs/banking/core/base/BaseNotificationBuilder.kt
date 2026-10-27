package com.nbs.banking.core.base

import android.content.Context
import androidx.core.app.NotificationCompat

abstract class BaseNotificationBuilder(protected val context: Context) {
    protected fun getBaseNotificationBuilder(remoteData: Map<String?, String?>): NotificationCompat.Builder {
//        val notificationColor = context.getColor(R.color.notification_color)
        return NotificationCompat
//            .Builder(context, context.getString(R.string.notification_channel_id))
            .Builder(context, "channelId")
//            .setSmallIcon(R.drawable.ic_notification)
//            .setColor(notificationColor)
//            .setContentTitle(remoteData[TITLE]?.htmlToString())
//            .setContentText(remoteData[BODY]?.htmlToString())
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
    }
}
