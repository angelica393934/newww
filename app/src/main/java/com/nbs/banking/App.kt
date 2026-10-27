package com.nbs.banking

import android.widget.Toast
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.result.rememberResultEventBusNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nbs.banking.randomactivityidea.navigation.boredNavigation
import com.nbs.banking.membership.navigation.membershipNavigation
import com.nbs.banking.navdemo.navigation.navDemoNavigation
import com.nbs.banking.datastoreexample.navigation.dataStoreExampleNavigation
import com.nbs.banking.home.navigation.appNavigation
import com.nbs.banking.dashboard.navigation.dashboardNavigation
import com.nbs.banking.longlist.navigation.longListNavigation
import com.nbs.banking.navigation.back
import com.nbs.banking.navigation.deeplink.DeepLinkEffect
import com.nbs.banking.navigation.deeplink.DeepLinkNavigator
import com.nbs.banking.navigation.destination.AppNavKey
import com.nbs.banking.navigation.destination.SplashDestination
import com.nbs.banking.navigation.goTo
import com.nbs.banking.navigation.utils.CustomNavEntryDecorator
import com.nbs.banking.navigation.utils.rememberAppNavBackStack
import com.nbs.banking.navigation.utils.slideInFromLeft
import com.nbs.banking.navigation.utils.slideOutToRight
import com.nbs.banking.note.navigation.noteNavigation
import com.nbs.banking.splash.navigation.splashNavigation
import com.nbs.banking.utils.deeplink.appDeepLinkPolicy

@Composable
fun App(
    deeplinkDestination: List<AppNavKey>?,
    onDeepLinkConsumed: () -> Unit
) {
    val backStack = rememberAppNavBackStack(SplashDestination)
    val context = LocalContext.current
    val deepLinkNavigator = remember(backStack) {
        DeepLinkNavigator(
            backStack = backStack,
            policy = appDeepLinkPolicy,
            push = { backStack.goTo(it) },
        )
    }

    DeepLinkEffect(
        deeplinkDestination = deeplinkDestination,
        navigator = deepLinkNavigator,
        onDeepLinkConsumed = onDeepLinkConsumed
    )

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.back() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
            rememberResultEventBusNavEntryDecorator(),
            remember { CustomNavEntryDecorator() }
        ),
        entryProvider = entryProvider(fallback = { key ->
            NavEntry(
                key = key,
                metadata = NavDisplay.transitionSpec { EnterTransition.None togetherWith ExitTransition.None } +
                        NavDisplay.popTransitionSpec { EnterTransition.None togetherWith ExitTransition.None }
            ) {
                LaunchedEffect(Unit) {
                    backStack.back()
                    if (BuildConfig.DEBUG) {
                        Toast.makeText(context, "$key doesn't exist", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }) {
            splashNavigation(backStack)
            appNavigation(backStack)
            membershipNavigation(backStack)
            dashboardNavigation(backStack)
            longListNavigation(backStack)
            noteNavigation(backStack)
            boredNavigation(backStack)
            navDemoNavigation(backStack)
            dataStoreExampleNavigation(backStack)
        },

        transitionSpec = {
            slideInFromLeft() togetherWith slideOutToRight()
        },
        popTransitionSpec = {
            slideInFromLeft() togetherWith slideOutToRight()
        },
        predictivePopTransitionSpec = {
            slideInFromLeft() togetherWith slideOutToRight()
        },
    )
}
