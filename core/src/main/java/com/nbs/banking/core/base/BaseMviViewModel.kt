package com.nbs.banking.core.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

abstract class BaseMviViewModel<Intent : Any, Event : Any, State : Any, Effect : Any>(
    initialState: State
) : ViewModel() {

    val state: StateFlow<State>
        field = MutableStateFlow(initialState)

    private val effectChannel = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = effectChannel.receiveAsFlow()

    abstract fun onIntent(intent: Intent)

    protected fun sendEvent(event: Event) {
        state.update { currentState ->
            reduce(currentState, event)
        }
    }

    protected fun sendEffect(effect: Effect) {
        effectChannel.trySend(effect)
    }

    abstract fun reduce(
        currentState: State,
        event: Event
    ): State
}