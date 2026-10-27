package com.nbs.banking.dashboard.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.nbs.banking.dashboard.presentation.DashboardScreen
import com.nbs.banking.longlist.navigation.LongListDestination
import com.nbs.banking.longlist.navigation.TransactionListDestination
import com.nbs.banking.navigation.destination.AppNavKey
import com.nbs.banking.navigation.destination.DashboardDestination
import com.nbs.banking.navigation.destination.HomeMviDestination
import com.nbs.banking.navigation.destination.NavDemoADestination
import com.nbs.banking.navigation.destination.NoteListDestination
import com.nbs.banking.navigation.destination.RandomActivityIdeaDestination
import com.nbs.banking.navigation.destination.UserSettingsDestination
import com.nbs.banking.navigation.goTo

fun EntryProviderScope<AppNavKey>.dashboardNavigation(
    backStack: MutableList<AppNavKey>
) {
    entry<DashboardDestination> {
        DashboardScreen(
            onNavigateHomeMvi = { backStack.goTo(HomeMviDestination) },
            onNavigateUserSettings = { backStack.goTo(UserSettingsDestination) },
            onNavigateNavDemo = { backStack.goTo(NavDemoADestination) },
            onNavigateNote = { backStack.goTo(NoteListDestination) },
            onNavigateActivity = { backStack.goTo(RandomActivityIdeaDestination) },
            onNavigateLongList = { backStack.goTo(LongListDestination) },
            onNavigateTransactionList = { backStack.goTo(TransactionListDestination) }
        )
    }
}
