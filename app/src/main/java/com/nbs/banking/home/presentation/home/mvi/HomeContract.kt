package com.nbs.banking.home.presentation.home.mvi

import com.nbs.banking.core.state.ScreenState
import com.nbs.banking.home.domain.dummyhome.model.HomeContent

sealed interface HomeIntent {
    data object LoadHomeContent : HomeIntent
    data object CheckStatus : HomeIntent
    data class DismissBanner(val bannerId: String) : HomeIntent
}

sealed interface HomeEvent {
    data object HomeContentLoading : HomeEvent
    data class HomeContentLoaded(val homeContent: HomeContent) : HomeEvent
    data class HomeContentErgit statusror(val error: Throwable) : HomeEvent

    data object StatusLoading : HomeEvent
    data class StatusLoaded(val status: Boolean) : HomeEvent
    data class StatusError(val error: Throwable) : HomeEvent

    data class UnreadCountUpdated(val count: Int) : HomeEvent
    data class BannerDismissed(val message: String) : HomeEvent
}

data class HomeState(
    val homeContent: ScreenState<HomeContent> = ScreenState.default(),
    val status: ScreenState<Boolean> = ScreenState.default(),
    val unreadCount: Int = 0,
    val bannerActionResult: String? = null,
) {
    fun contentLoading() = copy(homeContent = ScreenState.loading())
    fun contentSuccess(data: HomeContent) = copy(homeContent = ScreenState.success(data))
    fun contentError(throwable: Throwable) =
        copy(homeContent = ScreenState.error(throwable, throwable.message.orEmpty()))

    fun statusLoading() = copy(status = ScreenState.loading())
    fun statusSuccess(data: Boolean) = copy(status = ScreenState.success(data))
    fun statusError(throwable: Throwable) =
        copy(status = ScreenState.error(throwable, throwable.message.orEmpty()))
}

sealed interface HomeEffect {
    data class ShowSnackBar(val message: String) : HomeEffect
}