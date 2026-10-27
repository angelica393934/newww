package com.nbs.banking.navigation.destination

import androidx.navigation3.runtime.NavKey

sealed interface AppNavKey : NavKey

/**
 * NavigationDestination is a destination that can be navigated from inner-module
 * Inner Module is a module that can be navigated only from the same module
 * For example, AccountActivationDestination could be navigated only from the Dashboard module
 */
interface NavigationDestination: AppNavKey

/**
 * LaunchableDestination is a destination that can be launched from inter-module
 * Inter Module is a module that can be launched from another module
 * For example, LaunchableQrisSxcannerDestination could be launched from the Membership module
 */
interface LaunchableDestination: AppNavKey