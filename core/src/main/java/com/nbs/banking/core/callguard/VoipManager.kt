package com.nbs.banking.core.callguard

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class VoipManager @Inject constructor() {
    private val _isVoipActive = MutableStateFlow(false)
    val isVoipActive: StateFlow<Boolean> = _isVoipActive.asStateFlow()

    fun setVoipActive(isActive: Boolean) {
        _isVoipActive.update { isActive }
    }
}
