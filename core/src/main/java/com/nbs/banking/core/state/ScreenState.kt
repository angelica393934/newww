package com.nbs.banking.core.state

sealed class ScreenState<out T> {
    data object Default : ScreenState<Nothing>()

    data object Loading : ScreenState<Nothing>()

    data class Success<out T>(val data: T) : ScreenState<T>()

    data class Error(
        val throwable: Throwable,
        val message: String,
    ) : ScreenState<Nothing>()

    data object Offline: ScreenState<Nothing>()

    companion object {
        fun <T> default(): ScreenState<T> = Default

        fun <T> loading(): ScreenState<T> = Loading

        fun <T> success(data: T): ScreenState<T> = Success(data = data)

        fun <T> error(error: Throwable, message: String): ScreenState<T> = Error(throwable = error, message = message)

        fun <T> offline(): ScreenState<T> = Offline
    }
}
