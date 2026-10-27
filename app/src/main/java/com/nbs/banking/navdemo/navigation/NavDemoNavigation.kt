package com.nbs.banking.navdemo.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.nbs.banking.navdemo.presentation.ScreenA
import com.nbs.banking.navdemo.presentation.ScreenB
import com.nbs.banking.navdemo.presentation.ScreenC
import com.nbs.banking.navdemo.presentation.ScreenD
import com.nbs.banking.navigation.back
import com.nbs.banking.navigation.backToRoot
import com.nbs.banking.navigation.backUntil
import com.nbs.banking.navigation.clearAndGoTo
import com.nbs.banking.navigation.destination.AppNavKey
import com.nbs.banking.navigation.destination.NavDemoADestination
import com.nbs.banking.navigation.destination.NavDemoBDestination
import com.nbs.banking.navigation.destination.NavDemoCDestination
import com.nbs.banking.navigation.destination.NavDemoDDestination
import com.nbs.banking.navigation.goTo
import com.nbs.banking.navigation.rememberFinishActivity

fun EntryProviderScope<AppNavKey>.navDemoNavigation(
    backStack: MutableList<AppNavKey>
) {
    entry<NavDemoADestination> {
        val exitApp = rememberFinishActivity()
        ScreenA(
            onBack = {
                backStack.back(exitApp)
            },
            onNextToB = { backStack.goTo(NavDemoBDestination) },
            onClearAndGoToD = { backStack.clearAndGoTo(NavDemoDDestination) }
        )
    }

    entry<NavDemoBDestination> {
        ScreenB(
            onBack = { backStack.back() },
            onNextToC = { backStack.goTo(NavDemoCDestination(1)) },
            onGoToD = { backStack.goTo(NavDemoDDestination) }
        )
    }

    entry<NavDemoCDestination> { key ->
        ScreenC(
            count = key.count,
            onBack = { backStack.back() },
            onBackToA = { backStack.backToRoot() },
            onOpenNewC = { backStack.goTo(NavDemoCDestination(key.count + 1)) },
            onNextToD = { backStack.goTo(NavDemoDDestination) }
        )
    }

    entry<NavDemoDDestination> {
        val exitApp = rememberFinishActivity()
        ScreenD(
            onBack = { backStack.back() },
            onBackToA = { backStack.backUntil<NavDemoADestination>() },
            onBackToB = { backStack.backUntil<NavDemoBDestination>() },
            onExitApp = exitApp
        )
    }
}
