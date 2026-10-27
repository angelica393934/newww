package com.nbs.banking.core.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nbs.banking.core.exceptions.ApiException
import com.nbs.banking.core.state.ScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

//region MVVM
suspend fun <T> Flow<T>.startFlowAsState(
    stateFlow: MutableStateFlow<ScreenState<T>>,
) = this
    .onStart { stateFlow.update { _ -> ScreenState.loading() } }
    .catchException(stateFlow)
    .collect { stateFlow.update { _ -> ScreenState.success(it) } }

suspend fun <T> startSuspendAsState(
    stateFlow: MutableStateFlow<ScreenState<T>>,
    block: suspend () -> T,
) {
    stateFlow.update { ScreenState.loading() }
    try {
        val result = block()
        stateFlow.update { ScreenState.success(result) }
    } catch (error: Throwable) {
        if (error.isCancellation()) throw error
        Timber.tag("exception").e(error.toString())
        stateFlow.update { error.toUIStateError() }
    }
}

suspend fun <T> Flow<T>.startFlow(
    onError: (Throwable) -> Unit = {},
    block: (T) -> Unit,
) = this
    .catch { throwable ->
        if (throwable.isCancellation()) throw throwable
        Timber.tag("exception").e(throwable.toString())
        onError(throwable)
    }
    .collect { block(it) }

suspend fun <T> startSuspend(
    onError: (Throwable) -> Unit = {},
    block: suspend () -> T,
) =
    try {
        block()
    } catch (error: Throwable) {
        if (error.isCancellation()) throw error
        Timber.tag("exception").e(error.toString())
        onError(error)
        null
    }

inline fun <T> asUIState(
    crossinline block: () -> Flow<T>
): Flow<ScreenState<T>> = block()
    .map<T, ScreenState<T>> { ScreenState.success(it) }
    .onStart { emit(ScreenState.loading()) }
    .catchAsUIState()

fun <T> ViewModel.collectAsUIState(
    scope: CoroutineScope = this.viewModelScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(5000),
    initialValue: ScreenState<T> = ScreenState.default(),
    block: () -> Flow<T>,
): StateFlow<ScreenState<T>> = asUIState(block)
    .stateIn(
        scope = scope,
        started = started,
        initialValue = initialValue
    )

fun <T> Flow<T>.catchException(stateFlow: MutableStateFlow<ScreenState<T>>): Flow<T> =
    catch { throwable ->
        if (throwable.isCancellation()) {
            throw throwable
        }
        Timber.tag("exception").e(throwable.toString())
        stateFlow.update { throwable.toUIStateError() }
    }

fun <T> Flow<ScreenState<T>>.catchAsUIState(): Flow<ScreenState<T>> = catch { throwable ->
    if (throwable.isCancellation()) {
        throw throwable
    }
    Timber.tag("exception").e(throwable.toString())
    emit(throwable.toUIStateError())
}

//TODO: IMPROVE THIS
fun <T> Throwable.toUIStateError(): ScreenState<T> = when (this) {
    is ApiException -> ScreenState.error(this, this.message.orEmpty())
    else -> ScreenState.error(this, this.message.orEmpty())
}
//endregion

//region MVI
suspend fun <T> Flow<T>.startMviFlow(
    onLoading: () -> Unit = {},
    onError: (Throwable) -> Unit = {},
    onSuccess: (T) -> Unit,
) = this
    .onStart { onLoading() }
    .catchMviException { error -> onError(error) }
    .collect { onSuccess(it) }

suspend fun <T> startMviSuspend(
    onLoading: () -> Unit = {},
    onError: (Throwable) -> Unit = {},
    onSuccess: (T) -> Unit = {},
    block: suspend () -> T,
) {
    onLoading()
    try {
        val result = block()
        onSuccess(result)
    } catch (error: Throwable) {
        if (error.isCancellation()) throw error
        Timber.tag("exception").e(error.toString())
        onError(error)
    }
}

fun <T> Flow<T>.catchMviException(
    onError: (Throwable) -> Unit
): Flow<T> = catch { error ->
    Timber.tag("exception").e(error.toString())
    if (error.isCancellation()) throw error
    onError(error)
}
//endregion

fun Throwable.isCancellation(): Boolean =
    this is CancellationException