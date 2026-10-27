package com.nbs.banking.core.notification

import android.content.Intent
import com.nbs.banking.core.analytics.Analytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nbs.banking.core.error.AppException
import com.nbs.banking.core.eventbus.event.NotificationData
import com.nbs.banking.core.eventbus.event.SilentPushEvent
import com.nbs.banking.core.eventbus.event.SilentPushEventHandler
import com.nbs.banking.core.extensions.orTrue
import com.nbs.banking.core.storage.NotificationStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var json: Json

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var analytics: Analytics

    @Inject
    lateinit var silentPushEventBusHandler: SilentPushEventHandler

    @Inject
    lateinit var notificationStorage: NotificationStorage

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.i("onMessageReceived::firebaseMessage: $remoteMessage")
        Timber.i("onMessageReceived::data: ${remoteMessage.data}")

//        if (FlavorType.isDebuggingFlavor()) {
//            analytics.apply {
//                val remoteMessageParams = remoteMessage.data.toMapStringAny().toMutableMap().apply { put("FROM", remoteMessage.from) }
//                trackEvent(EventType.FIREBASE_REMOTE_MESSAGE, remoteMessageParams, providers = arrayOf(AnalyticsIdentifier.CRASHLYTICS))
//                trackNonFatalException(NotificationException(EventType.FIREBASE_REMOTE_MESSAGE.eventName), remoteMessageParams)
//            }
//        }

        val notificationData = remoteMessage.toNotificationData(json)

        Timber.i("onMessageReceived::notificationData: $notificationData")

        notificationData?.let { notification ->
            when {
                notification.isShowNotification.orTrue() -> {
                    showNotification(remoteMessage)
                }

                else -> {
                    Timber.i("onMessageReceived::notification not shown, no topic match or isShowNotification is false")
                }
            }

            if (notification.customActions != null) {
                val customAction = parseCustomActions(notification.customActions, json) ?: return

                CoroutineScope(Dispatchers.IO).launch {
                    silentPushEventBusHandler.publish(
                        SilentPushEvent(
                            message = customAction.actionType.orEmpty(),
                            deeplink = notification.deeplink,
                            notificationData = NotificationData(
                                title = customAction.actionParameters?.get("title").orEmpty(),
                                desc = customAction.actionParameters?.get("desc").orEmpty(),
                                type = customAction.actionParameters?.get("type").orEmpty(),
                            ),
                        ),
                    )
                }
            }
        }
    }

    private fun showNotification(remoteMessage: RemoteMessage) {
        remoteMessage.data.let { data ->
            notificationManager.sendNotification(
                applicationContext,
                data,
            )
        }
    }

    override fun handleIntent(intent: Intent) {
        try {
            val extras = intent.extras
            if (extras != null && !extras.isEmpty) {
                val builder = RemoteMessage.Builder("MyFirebaseMessagingService")

                extras.keySet().forEach { key ->
                    if (!key.startsWith("google.") && !key.startsWith("gcm.")) {
                        extras.getString(key)?.let { value ->
                            builder.addData(key, value)
                        } ?: Timber.w("Value for key '$key' is null or not a String, skipping.")
                    }
                }

                extras.getString("token")?.let { token ->
                    onNewToken(token)
                }

                onMessageReceived(builder.build())
            } else {
                super.handleIntent(intent)
            }
        } catch (e: AppException) {
            Timber.e(e, "Error handling intent")
            super.handleIntent(intent)
        }
    }
}
