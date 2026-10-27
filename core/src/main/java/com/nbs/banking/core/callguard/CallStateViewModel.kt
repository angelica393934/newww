package com.nbs.banking.core.callguard

import android.content.Context
import android.media.AudioManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class CallStateViewModel @Inject constructor(
    private val audioManager: AudioManager,
    voipManager: VoipManager,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {
    private fun getCurrentHardwareState(): CallState {
        val mode = audioManager.mode
        return if (mode == AudioManager.MODE_IN_CALL || mode == AudioManager.MODE_IN_COMMUNICATION) {
            CallState.OFFHOOK
        } else {
            CallState.IDLE
        }
    }

    val callState: StateFlow<CallState> =
        combine(
            audioManager.callStateObserver(context),
            voipManager.isVoipActive,
        ) { hardwareState, isVoipCallActive ->
            Pair(hardwareState, isVoipCallActive)
        }.scan(PrivacyLatchState()) { previousState, (hardwareState, isVoipActive) ->
            // 1. Determine if we should hold the app unlocked.
            val shouldHoldShield = if (isVoipActive) {
                // Whitelist is active during VoIP; no need for the latch yet.
                false
            } else {
                // If VoIP just ended but the hardware is still lagging at OFFHOOK,
                // keep holding the latch. For normal cellular calls, this evaluates
                // to false, allowing the shield to trigger immediately.
                (previousState.wasVoipActiveBefore || previousState.isWaitingForHardwareToClear) &&
                    hardwareState != CallState.IDLE
            }

            // 2. Decide what state to expose to the UI.
            val computedCallState = if (isVoipActive || shouldHoldShield) {
                CallState.IDLE // VoIP is live or hardware is lagging -> Keep app unlocked.
            } else {
                hardwareState // Normal state (IDLE) or regular cell call (OFFHOOK) -> Trust hardware.
            }

            // 3. Save current values into memory for the next incoming frame
            PrivacyLatchState(
                finalCallState = computedCallState,
                wasVoipActiveBefore = isVoipActive,
                isWaitingForHardwareToClear = shouldHoldShield,
            )
        }.map { it.finalCallState }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = if (voipManager.isVoipActive.value) CallState.IDLE else getCurrentHardwareState(),
            )
}
