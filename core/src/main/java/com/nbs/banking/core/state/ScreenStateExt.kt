package com.nbs.banking.core.state

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.nbs.banking.core.state.ScreenState.Default
import com.nbs.banking.core.state.ScreenState.Error
import com.nbs.banking.core.state.ScreenState.Loading
import com.nbs.banking.core.state.ScreenState.Offline
import com.nbs.banking.core.state.ScreenState.Success
import kotlinx.coroutines.CoroutineScope

/**
 * Observes the state of a UIState and triggers the appropriate composable function based on the state.
 *
 * @param onLoading A composable function to be invoked when the state is Loading.
 * @param onSuccess A composable function to be invoked when the state is Success.
 * @param onError A composable function to be invoked when the state is Error.
 * @param onDefault A composable function to be invoked when the state is Default.
 */
@Composable
inline fun <T> ScreenState<T>.Render(
    onLoading: @Composable () -> Unit = {},
    onSuccess: @Composable (T) -> Unit = {},
    onError: @Composable (throwable: Throwable, message: String) -> Unit = { _, _ -> },
    onOffline: @Composable () -> Unit = {},
    onDefault: @Composable () -> Unit = {},
) {
    when (this) {
        is Default -> onDefault()
        is Error -> onError(this.throwable, this.message)
        is Loading -> onLoading()
        is Success -> onSuccess(this.data)
        is Offline -> onOffline()
    }
}

/**
 * Observes the state of a UIState and triggers the appropriate composable function based on the state.
 *
 * @param onLoading A suspend function to be invoked when the state is Loading.
 * @param onSuccess A suspend function to be invoked when the state is Success.
 * @param onError A suspend function to be invoked when the state is Error.
 * @param onDefault A suspend function to be invoked when the state is Default.
 */
@Composable
fun <T> ScreenState<T>.ObserveSideEffect(
    onSuccess: suspend CoroutineScope.(T) -> Unit = {},
    onLoading: suspend CoroutineScope.() -> Unit = {},
    onError: suspend CoroutineScope.(throwable: Throwable, message: String) -> Unit = { _, _ -> },
    onOffline: suspend CoroutineScope.() -> Unit = {},
    onDefault: suspend CoroutineScope.() -> Unit = {},
) {
    LaunchedEffect(this) {
        when (val state = this@ObserveSideEffect) {
            is Default -> onDefault()
            is Error -> onError(state.throwable, state.message)
            is Loading -> onLoading()
            is Success -> onSuccess(state.data)
            is Offline -> onOffline()
        }
    }
}

fun <T> ScreenState<T>.isLoading() = this is Loading

fun <T> ScreenState<T>.isSuccess() = this is Success

fun <T> ScreenState<T>.isError() = this is Error

fun <T> ScreenState<T>.isDefault() = this is Default

fun <T> ScreenState<T>.isOffline() = this is Offline


@Composable
inline fun <T : Any> LazyPagingItems<T>.Render(
    onLoading: @Composable () -> Unit = {},
    onError: @Composable (throwable: Throwable, message: String) -> Unit = { _, _ -> },
    onEmpty: @Composable () -> Unit = {},
    onSuccess: @Composable (LazyPagingItems<T>) -> Unit = {},
) {
    val refresh = loadState.refresh
    when {
        itemCount == 0 && refresh is LoadState.Loading -> onLoading()
        itemCount == 0 && refresh is LoadState.Error -> onError(
            refresh.error,
            refresh.error.localizedMessage ?: "Failed to load items"
        )

        itemCount == 0 && refresh is LoadState.NotLoading -> onEmpty()
        else -> onSuccess(this)
    }
}

fun <T : Any> LazyListScope.RenderAppend(
    items: LazyPagingItems<T>,
    onLoading: @Composable () -> Unit = {},
    onError: @Composable (throwable: Throwable, message: String) -> Unit = { _, _ -> },
) {
    when (val append = items.loadState.append) {
        is LoadState.Loading -> item { onLoading() }
        is LoadState.Error -> item {
            onError(append.error, append.error.localizedMessage ?: "Failed to load more items")
        }
        is LoadState.NotLoading -> Unit
    }
}

@Composable
fun <T : Any> PagingLoadStateEffect(
    items: LazyPagingItems<T>,
    onInitialError: suspend (Throwable) -> Unit = {},
    onRefreshError: suspend (error: Throwable) -> Unit = {},
    onAppendError: suspend (error: Throwable) -> Unit = {},
) {
    val refresh = items.loadState.refresh
    val append = items.loadState.append
    val currentOnInitialError by rememberUpdatedState(onInitialError)
    val currentOnRefreshError by rememberUpdatedState(onRefreshError)
    val currentOnAppendError by rememberUpdatedState(onAppendError)

    LaunchedEffect(refresh) {
        if (refresh is LoadState.Error) {
            if (items.itemCount == 0) currentOnInitialError(refresh.error)
            else currentOnRefreshError(refresh.error)
        }
    }
    LaunchedEffect(append) {
        if (append is LoadState.Error) currentOnAppendError(append.error)
    }
}