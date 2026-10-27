package com.nbs.banking.datastoreexample.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val username: String = "Guest",
    val isDarkMode: Boolean = false,
    val notificationCount: Int = 0,
    val lastLoginTime: Long = 0L,
    val fontScale: Float = 1.0f,
    val balance: Double = 0.0,
    val profile: UserProfile = UserProfile(),
    val secureData: SecureData = SecureData()
)

@Serializable
data class UserProfile(
    val nickname: String = "JohnDoe",
    val role: String = "VIP"
)

@Serializable
data class SecureData(
    val secureToken: String = "",
    val secretPin: String = ""
)