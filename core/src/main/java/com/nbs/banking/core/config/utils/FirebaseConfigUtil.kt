package com.nbs.banking.core.config.utils

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException

inline fun FirebaseRemoteConfig.fetchAndActivate(crossinline onComplete: (isUpdated: Boolean) -> Unit, crossinline onFailure: (exception: Exception) -> Unit) {
    fetchAndActivate()
        .addOnSuccessListener { result ->
            onComplete(result)
        }.addOnFailureListener { exception ->
            onFailure(exception)
            FirebaseCrashlytics.getInstance().recordException(exception)
        }
}

inline fun FirebaseRemoteConfig.addOnConfigUpdateListener(crossinline onUpdate: (ConfigUpdate) -> Unit, crossinline onError: (FirebaseRemoteConfigException) -> Unit) {
    addOnConfigUpdateListener(
        object : ConfigUpdateListener {
            override fun onUpdate(update: ConfigUpdate) {
                onUpdate(update)
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                onError(error)
            }
        },
    )
}
