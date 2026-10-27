package com.nbs.banking.navigation

import android.app.Activity
import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.nbs.banking.navigation.destination.AppNavKey

@MainThread
fun MutableList<AppNavKey>.goTo(
    key: AppNavKey
) {
    add(key)
}

@MainThread
fun MutableList<AppNavKey>.goThrough(
    vararg keys: AppNavKey
) {
    addAll(keys)
}


@MainThread
fun MutableList<AppNavKey>.back(onExit: () -> Unit = {}) {
    if (size > 1) {
        removeLastOrNull()
    } else {
        onExit()
    }
}

@MainThread
fun MutableList<AppNavKey>.backToRoot() {
    if (size > 1) subList(1, size).clear()
}

@MainThread
fun MutableList<AppNavKey>.backTo(destination: AppNavKey) {
    val index = lastIndexOf(destination)
    if (index in 0..<lastIndex) {
        subList(index + 1, size).clear()
    }
}

@MainThread
inline fun <reified T : AppNavKey> MutableList<AppNavKey>.backUntil() {
    val index = indexOfLast { it is T }
    if (index in 0..<lastIndex) {
        subList(index + 1, size).clear()
    }
}

@MainThread
fun MutableList<AppNavKey>.clearAndGoTo(key: AppNavKey) {
    if (isNotEmpty()) set(0, key)
    subList(1, size).clear()
}

@Composable
fun rememberFinishActivity(): () -> Unit {
    val context = LocalContext.current

    return remember(context) {
        {
            (context as? Activity)?.finish()
        }
    }
}
