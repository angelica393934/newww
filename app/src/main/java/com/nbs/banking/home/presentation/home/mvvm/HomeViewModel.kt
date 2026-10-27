package com.nbs.banking.home.presentation.home.mvvm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbs.banking.core.extensions.collectAsUIState
import com.nbs.banking.core.extensions.startFlow
import com.nbs.banking.core.extensions.startFlowAsState
import com.nbs.banking.core.extensions.startSuspend
import com.nbs.banking.core.extensions.startSuspendAsState
import com.nbs.banking.core.state.ScreenState
import com.nbs.banking.home.domain.dummyhome.model.HomeContent
import com.nbs.banking.home.domain.dummyhome.usecase.CheckHomeStatusUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.DismissBannerUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.FetchHomeContentUseCase
import com.nbs.banking.home.domain.dummyhome.usecase.ObserveUnreadCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fetchHomeContentUseCase: FetchHomeContentUseCase,
    private val checkHomeStatusUseCase: CheckHomeStatusUseCase,
    private val observeUnreadCountUseCase: ObserveUnreadCountUseCase,
    private val dismissBannerUseCase: DismissBannerUseCase,
) : ViewModel() {
    var result by mutableStateOf<String?>(null)

    val homeContentState: StateFlow<ScreenState<HomeContent>>
        field = MutableStateFlow<ScreenState<HomeContent>>(ScreenState.default())

    val statusState: StateFlow<ScreenState<Boolean>>
        field = MutableStateFlow<ScreenState<Boolean>>(ScreenState.default())

    var unreadCount by mutableIntStateOf(0)
        private set

    var bannerActionResult by mutableStateOf<String?>(null)
        private set

    init {
        loadData()
    }

    fun loadData() {
        // Case 1: Flow -> ScreenState
        viewModelScope.launch {
            fetchHomeContentUseCase().onEach { delay(1000.milliseconds) }
                .startFlowAsState(homeContentState)
        }

        // Case 3: Flow -> Direct
        viewModelScope.launch {
            observeUnreadCountUseCase().onEach { delay(500.milliseconds) }.startFlow(
                onError = { Timber.e("Failed to sync badge: $it") }
            ) { count ->
                unreadCount = count
            }
        }
    }

    // Case 2: Suspend -> ScreenState
    fun checkStatus() {
        viewModelScope.launch {
            startSuspendAsState(statusState) {
                delay(2000.milliseconds)
                checkHomeStatusUseCase()
            }
        }
    }

    // Case 4: Suspend -> Direct
    fun dismissBanner(bannerId: String) {
        viewModelScope.launch {
            val isSuccess = startSuspend(
                onError = { Timber.e("Failed to dismiss banner: $it") }
            ) {
                delay(2500.milliseconds)
                dismissBannerUseCase(bannerId)
            }

            if (isSuccess == true) {
                bannerActionResult = "Banner $bannerId dismissed!"
            }
        }
    }

    val state: StateFlow<ScreenState<HomeContent>> = collectAsUIState { fetchHomeContentUseCase() }
}