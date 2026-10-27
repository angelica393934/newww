package com.nbs.banking.navigation.deeplink

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import com.nbs.banking.navigation.destination.AppNavKey
import kotlinx.coroutines.flow.first
import timber.log.Timber

/**
 * Opens deep links on a back stack. Holds everything that is fixed for the lifetime
 * of the stack, so the composable only needs the link itself.
 *
 * @param backStack must be snapshot-backed (NavBackStack / mutableStateListOf)
 * @param policy decides which links may be opened
 * @param push adds one key to the stack (your goTo helper)
 */
class DeepLinkNavigator(
    private val backStack: MutableList<AppNavKey>,
    private val policy: DeepLinkPolicy,
    private val push: (AppNavKey) -> Unit,
) {
    /** Suspends until the app is ready for [keys], then opens them. */
    suspend fun open(keys: List<AppNavKey>) {
        val needsLogin = policy.isPostLogin(keys.last())

        // WAIT:
        // - post-login link: no gate page anywhere in the stack (a general page can sit
        //   on top of Login, so the whole stack is checked, not just the top).
        // - other links: only wait for the splash to leave the top.
        // top != null is required: clear() leaves the stack empty for a moment, and
        // null !is splash would end the wait too early.
        snapshotFlow { backStack.toList() }
            .first { stack ->
                val top = stack.lastOrNull()
                top != null && if (needsLogin) {
                    stack.none(policy::isPreLogin)
                } else {
                    !policy.isSplash(top)
                }
            }

        // OPEN: nothing below suspends, so the stack is never observed half-built.
        // If the first key is already in the stack, pop to it and push only the rest.
        val root = backStack.indexOfLast { it == keys.first() }
        if (root >= 0) {
            while (backStack.size > root + 1) backStack.removeAt(backStack.lastIndex)
            keys.drop(1).forEach(push)
        } else {
            keys.forEach(push)
        }
    }
}

/** Opens a pending deep link, then marks it handled. Restarts only when the link changes. */
@Composable
fun DeepLinkEffect(
    deeplinkDestination: List<AppNavKey>?,
    navigator: DeepLinkNavigator,
    onDeepLinkConsumed: () -> Unit,
) {
    val currentOnConsumed by rememberUpdatedState(onDeepLinkConsumed)

    LaunchedEffect(deeplinkDestination) {
        Timber.tag("App").d("DeeplinkDestination: $deeplinkDestination")
        val keys = deeplinkDestination?.takeIf { it.isNotEmpty() } ?: return@LaunchedEffect
        navigator.open(keys)
        currentOnConsumed()
    }
}