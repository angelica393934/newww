package com.nbs.banking.longlist.navigation

import com.nbs.banking.navigation.destination.NavigationDestination
import kotlinx.serialization.Serializable

@Serializable
data object LongListDestination : NavigationDestination

@Serializable
object TransactionListDestination : NavigationDestination