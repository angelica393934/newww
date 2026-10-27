package com.nbs.banking.home.presentation.home.mvi

import androidx.lifecycle.viewModelScope
import com.nbs.banking.core.base.BaseMviViewModel
import com.nbs.banking.core.extensions.startMviFlow
import com.nbs.banking.core.extensions.startMviSuspend
import com.nbs.banking.home.domain.dummyhome.usecase.CheckHomeStatusUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.DismissBannerUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.FetchHomeContentUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.ObserveUnreadCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeMviViewModel @Inject constructor(
    private val fetchHomeContentUseCase: FetchHomeContentUseCase,
    private val checkHomeStatusUseCase: CheckHomeStatusUseCase,
    private val observeUnreadCountUseCase: ObserveUnreadCountUseCase,
    private val dismissBannerUseCase: DismissBannerUseCase,
) : BaseMviViewModel<HomeIntent, HomeEvent, HomeState, HomeEffect>(HomeState()) {

    init {
        onIntent(HomeIntent.LoadHomeContent)
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadHomeContent -> loadData()
            HomeIntent.CheckStatus -> checkStatus()
            is HomeIntent.DismissBanner -> dismissBanner(intent.bannerId)
        }
    }

    override fun reduce(
        currentState: HomeState,
        event: HomeEvent
    ): HomeState =
        when (event) {
            HomeEvent.HomeContentLoading -> currentState.contentLoading()
            is HomeEvent.HomeContentLoaded -> currentState.contentSuccess(event.homeContent)
            is HomeEvent.HomeContentError -> currentState.contentError(event.error)

            HomeEvent.StatusLoading -> currentState.statusLoading()
            is HomeEvent.StatusLoaded -> currentState.statusSuccess(event.status)
            is HomeEvent.StatusError -> currentState.statusError(event.error)

            is HomeEvent.UnreadCountUpdated -> currentState.copy(unreadCount = event.count)
            is HomeEvent.BannerDismissed -> currentState.copy(bannerActionResult = event.message)
        }

    private fun loadData() {
        // Case 1: Flow -> ScreenState
        viewModelScope.launch {
            fetchHomeContentUseCase().onEach { delay(1000.milliseconds) }
                .startMviFlow(
                    onLoading = {
                        sendEvent(HomeEvent.HomeContentLoading)
                    },
                    onError = { error ->
                        sendEvent(HomeEvent.HomeContentError(error))
                        sendEffect(HomeEffect.ShowSnackBar(message = error.message.orEmpty()))
                    },
                    onSuccess = { homeContent ->
                        sendEvent(HomeEvent.HomeContentLoaded(homeContent))
                        sendEffect(HomeEffect.ShowSnackBar(message = "Home content loaded successfully"))
                    }
                )
        }

        // Case 3: Flow -> Direct Value
        viewModelScope.launch {
            observeUnreadCountUseCase().onEach { delay(500.milliseconds) }
                .startMviFlow(
                    onError = { Timber.e("Failed to sync badge: $it") }
                ) { count ->
                    sendEvent(HomeEvent.UnreadCountUpdated(count))
                }
        }
    }

    // Case 2: Suspend -> ScreenState
    private fun checkStatus() {
        viewModelScope.launch {
            startMviSuspend(
                onLoading = {
                    sendEvent(HomeEvent.StatusLoading)
                },
                onError = { error ->
                    sendEvent(HomeEvent.StatusError(error))
                    sendEffect(HomeEffect.ShowSnackBar(message = error.message.orEmpty()))
                },
                onSuccess = { status ->
                    sendEvent(HomeEvent.StatusLoaded(status))
                }
            ) {
                delay(2000.milliseconds)
                checkHomeStatusUseCase()
            }
        }
    }

    // Case 4: Suspend -> Direct Action
    private fun dismissBanner(bannerId: String) {
        viewModelScope.launch {
            startMviSuspend(
                onError = { error ->
                    sendEffect(HomeEffect.ShowSnackBar(message = "Failed to dismiss: ${error.message}"))
                },
                onSuccess = { isSuccess ->
                    if (isSuccess) {
                        val message = "Banner $bannerId dismissed!"
                        sendEvent(HomeEvent.BannerDismissed(message))
                        sendEffect(HomeEffect.ShowSnackBar(message = message))
                    }
                }
            ) {
                delay(2500.milliseconds)
                dismissBannerUseCase(bannerId)
            }
        }
    }
}