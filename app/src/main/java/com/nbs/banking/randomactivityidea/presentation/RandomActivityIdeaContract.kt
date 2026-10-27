package com.nbs.banking.randomactivityidea.presentation

import com.nbs.banking.randomactivityidea.domain.model.RandomActivityIdea
import com.nbs.banking.core.state.ScreenState

sealed interface RandomActivityIdeaIntent {
    data object FetchRandomActivity : RandomActivityIdeaIntent
}

sealed interface RandomActivityIdeaEvent {
    data object ActivityLoading : RandomActivityIdeaEvent
    data class ActivityLoaded(val randomActivityIdea: RandomActivityIdea) : RandomActivityIdeaEvent
    data class ActivityError(val error: Throwable) : RandomActivityIdeaEvent
}

data class RandomActivityIdeaState(
    val activityState: ScreenState<RandomActivityIdea> = ScreenState.default()
) {
    fun loading() = copy(activityState = ScreenState.loading())
    fun success(data: RandomActivityIdea) = copy(activityState = ScreenState.success(data))
    fun error(error: Throwable) = copy(activityState = ScreenState.error(error, error.message.orEmpty()))
}

sealed interface RandomActivityIdeaEffect {
    data class ShowSnackBar(val message: String) : RandomActivityIdeaEffect
}
