package com.nbs.banking.home.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.result.LocalResultEventBus
import androidx.navigation3.runtime.result.ResultEffect
import com.nbs.banking.navigation.destination.AppNavKey
import com.nbs.banking.navigation.destination.HomeDestination
import com.nbs.banking.navigation.destination.HomeMviDestination
import com.nbs.banking.home.presentation.home.mvvm.HomeViewModel
import com.nbs.banking.home.presentation.home.mvvm.DetailScreen
import com.nbs.banking.home.presentation.home.mvi.HomeEffect
import com.nbs.banking.home.presentation.home.mvi.HomeMviScreen
import com.nbs.banking.home.presentation.home.mvi.HomeMviViewModel
import com.nbs.banking.home.presentation.home.mvvm.HomeScreen
import com.nbs.banking.home.presentation.home.mvvm.HomeScreenUiState
import com.nbs.banking.navigation.back
import com.nbs.banking.navigation.goTo

fun EntryProviderScope<AppNavKey>.appNavigation(
    backStack: MutableList<AppNavKey>
) {
    entry<HomeDestination> {
/*        val resultBus = LocalResultEventBus.current
//               val detailResult by resultBus.conflateAsState<Detail?>(
//                    resultKey = "test",
//                    defaultValue = null
                )*/
        val viewModel: HomeViewModel = hiltViewModel()
        val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
        val homeContent by viewModel.homeContentState.collectAsStateWithLifecycle()
        val status by viewModel.statusState.collectAsStateWithLifecycle()

        ResultEffect<DetailNavigationDestination>(resultKey = DetailNavigationDestination.RESULT_KEY) {
            viewModel.result = it.id
            snackbarHostState.showSnackbar("result = ${it.id}")
        }

        val uiState = HomeScreenUiState(
            result = viewModel.result,
            status = status,
            homeContent = homeContent,
            unreadCount = viewModel.unreadCount,
            bannerActionResult = viewModel.bannerActionResult,
        )

        HomeScreen(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            checkStatus = viewModel::checkStatus,
            onReloadData = viewModel::loadData,
            onDismissBanner = viewModel::dismissBanner
        ) { backStack.goTo(DetailNavigationDestination("123")) }
    }
    entry<DetailNavigationDestination> {
        val resultBus = LocalResultEventBus.current
        DetailScreen(it.id) {
            resultBus.sendResult(
                resultKey = DetailNavigationDestination.RESULT_KEY,
                result = it
            )
            backStack.back()
        }
    }
    entry<HomeMviDestination> {
        val snackbarHostState = remember { SnackbarHostState() }
        val viewModel: HomeMviViewModel = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is HomeEffect.ShowSnackBar -> {
                        snackbarHostState.showSnackbar(
                            message = effect.message
                        )
                    }
                }
            }
        }
        HomeMviScreen(
            state,
            snackbarHostState,
            { backStack.goTo(DetailNavigationDestination("123")) },
            viewModel::onIntent
        )
    }
}