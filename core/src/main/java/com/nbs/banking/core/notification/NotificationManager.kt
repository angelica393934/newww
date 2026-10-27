package com.nbs.banking.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.nbs.banking.core.notification.NotificationBuilder.Companion.IMAGE_URL
import com.nbs.banking.core.utils.CommonUtils
import java.util.Date
import javax.inject.Inject

class NotificationManager @Inject constructor(
    private val notificationBuilder: NotificationBuilder,
) {
    fun sendNotification(context: Context, remoteData: Map<String?, String?>) {
        val notificationId = Date().time.toInt()
        val builder = notificationBuilder.createCommonNotification(
            remoteData,
        )

        val notificationManager = NotificationManagerCompat.from(context)
        val vipSoundUri: Uri = "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/raw/winxp".toUri()
        val promoSoundUri: Uri = "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/raw/flying".toUri()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            var channelId = "channelId"
            var channelName = "channelName"
            var soundUri: Uri? = null

            if (remoteData["notif"] == "promo") {
                channelId = "Promo"
                channelName = "Promo"
                soundUri = promoSoundUri
            } else if (remoteData["notif"] == "vip") {
                channelId = "vip"
                channelName = "vip"
                soundUri = vipSoundUri
            }

            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "description"
                soundUri?.let {
                    setSound(it, audioAttributes)
                }
            }
            notificationManager.createNotificationChannel(notificationChannel)
            builder.setChannelId(channelId)
        }

        if (remoteData[IMAGE_URL] != null) {
            CommonUtils.loadImageForNotification(
                context,
                remoteData[IMAGE_URL].toString(),
                builder,
                onSuccess = {
                    buildNotification(
                        context,
                        builder,
                        notificationId,
                        notificationManager,
                    )
                },
                onError = {
                    buildNotification(
                        context,
                        builder,
                        notificationId,
                        notificationManager,
                    )
                },
            )
        } else {
            buildNotification(context, builder, notificationId, notificationManager)
        }
    }

    private fun buildNotification(
        context: Context,
        builder: NotificationCompat.Builder,
        notificationId: Int,
        notificationManager: NotificationManagerCompat,
    ) {
        notificationManager.cancel(notificationId)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notificationManager.notify(notificationId, builder.build())
    }
}
