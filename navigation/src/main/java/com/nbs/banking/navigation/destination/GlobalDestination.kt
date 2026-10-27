package com.nbs.banking.navigation.destination

import kotlinx.serialization.Serializable

@Serializable
data object SplashDestination : LaunchableDestination

@Serializable
data object HomeDestination : LaunchableDestination

@Serializable
data object HomeMviDestination : LaunchableDestination

@Serializable
data object LoginDestination : LaunchableDestination

@Serializable
data object NoteListDestination : LaunchableDestination

@Serializable
data object RandomActivityIdeaDestination : LaunchableDestination

@Serializable
data object NavDemoADestination : LaunchableDestination

@Serializable
data object UserSettingsDestination : LaunchableDestination

@Serializable
data object DashboardDestination : LaunchableDestination
