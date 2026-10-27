package com.nbs.banking.core.callguard

import android.content.Context
import android.media.AudioManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

fun AudioManager.callStateObserver(context: Context): Flow<CallState> = callbackFlow {
    val executor = ContextCompat.getMainExecutor(context)

    fun getAudioMode(mode: Int): CallState = if (mode == AudioManager.MODE_IN_CALL || mode == AudioManager.MODE_IN_COMMUNICATION) {
        CallState.OFFHOOK
    } else {
        CallState.IDLE
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val listener = AudioManager.OnModeChangedListener { mode ->
            val state = getAudioMode(mode)
            trySend(state)
        }
        trySend(getAudioMode(this@callStateObserver.mode))
        addOnModeChangedListener(executor, listener)
        awaitClose {
            removeOnModeChangedListener(listener)
        }
    } else {
        val pollingJob = launch {
            var lastKnownState: CallState? = null
            while (isActive) {
                val currentState = getAudioMode(this@callStateObserver.mode)
                if (currentState != lastKnownState) {
                    trySend(currentState)
                    lastKnownState = currentState
                }
                delay(500.milliseconds)
            }
        }
        trySend(getAudioMode(this@callStateObserver.mode))
        awaitClose {
            pollingJob.cancel()
        }
    }
}.distinctUntilChanged()
