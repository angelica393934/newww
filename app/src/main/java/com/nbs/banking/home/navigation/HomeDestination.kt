package com.nbs.banking.home.navigation

import com.nbs.banking.navigation.destination.NavigationDestination
import kotlinx.serialization.Serializable

@Serializable
data class DetailNavigationDestination(
    val id: String
) : NavigationDestination{
    companion object{
        const val RESULT_KEY = "DetailNavigationDestinationResultKey"
    }
}