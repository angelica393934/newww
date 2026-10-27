package com.nbs.banking.randomactivityidea.presentation

import androidx.lifecycle.viewModelScope
import com.nbs.banking.randomactivityidea.domain.usecase.GetRandomActivityUseCase
import com.nbs.banking.core.base.BaseMviViewModel
import com.nbs.banking.core.extensions.startMviSuspend
import com.nbs.banking.core.state.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RandomActivityIdeaViewModel @Inject constructor(
    private val getRandomActivityUseCase: GetRandomActivityUseCase
) : BaseMviViewModel<RandomActivityIdeaIntent, RandomActivityIdeaEvent, RandomActivityIdeaState, RandomActivityIdeaEffect>(
    RandomActivityIdeaState(activityState = ScreenState.default())
) {

    override fun onIntent(intent: RandomActivityIdeaIntent) {
        when (intent) {
            RandomActivityIdeaIntent.FetchRandomActivity -> fetchRandomActivity()
        }
    }

    override fun reduce(
        currentState: RandomActivityIdeaState,
        event: RandomActivityIdeaEvent
    ): RandomActivityIdeaState =
        when (event) {
            RandomActivityIdeaEvent.ActivityLoading -> currentState.loading()
            is RandomActivityIdeaEvent.ActivityLoaded -> currentState.success(event.randomActivityIdea)
            is RandomActivityIdeaEvent.ActivityError -> currentState.error(event.error)
        }

    private fun fetchRandomActivity() {
        viewModelScope.launch {
            startMviSuspend(
                onLoading = { sendEvent(RandomActivityIdeaEvent.ActivityLoading) },
                onError = { error ->
                    sendEvent(RandomActivityIdeaEvent.ActivityError(error))
                    sendEffect(RandomActivityIdeaEffect.ShowSnackBar(error.message.orEmpty()))
                },
                onSuccess = { activity ->
                    sendEvent(RandomActivityIdeaEvent.ActivityLoaded(activity))
                }
            ) {
                getRandomActivityUseCase()
            }
        }
    }
}
