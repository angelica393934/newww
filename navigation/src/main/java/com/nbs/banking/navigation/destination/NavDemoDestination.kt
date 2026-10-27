package com.nbs.banking.navigation.destination

import kotlinx.serialization.Serializable


@Serializable
data object NavDemoBDestination : LaunchableDestination

@Serializable
data class NavDemoCDestination(val count: Int = 1) : LaunchableDestination

@Serializable
data object NavDemoDDestination : LaunchableDestination
