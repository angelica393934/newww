package com.nbs.banking.membership.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.nbs.banking.membership.presentation.LoginScreen
import com.nbs.banking.membership.presentation.VerificationSuccessScreen
import com.nbs.banking.navigation.destination.AppNavKey
import com.nbs.banking.navigation.clearAndGoTo
import com.nbs.banking.navigation.destination.DashboardDestination
import com.nbs.banking.navigation.destination.LoginDestination
import com.nbs.banking.navigation.goTo

fun EntryProviderScope<AppNavKey>.membershipNavigation(
    backStack: MutableList<AppNavKey>
){
    entry<LoginDestination>{
        LoginScreen(
            onLoginClick = {
                backStack.goTo(VerificationSuccessNavigationDestination)
            }
        )
    }
    entry<VerificationSuccessNavigationDestination>{
        VerificationSuccessScreen(
            onContinueClick = {
                backStack.clearAndGoTo(DashboardDestination)
            }
        )
    }
}
