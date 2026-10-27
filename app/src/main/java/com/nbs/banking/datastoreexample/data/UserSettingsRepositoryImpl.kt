package com.nbs.banking.datastoreexample.data

import com.nbs.banking.core.datastore.PreferenceDataStore
import com.nbs.banking.core.datastore.SecuredPreferenceDataStore
import com.nbs.banking.utils.UserSettingsKeys
import com.nbs.banking.datastoreexample.domain.model.SecureData
import com.nbs.banking.datastoreexample.domain.model.UserProfile
import com.nbs.banking.datastoreexample.domain.model.UserSettings
import com.nbs.banking.datastoreexample.domain.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class UserSettingsRepositoryImpl @Inject constructor(
    private val preferenceDataStore: PreferenceDataStore,
    private val securedPreferenceDataStore: SecuredPreferenceDataStore,
) : UserSettingsRepository {

    override fun observeUserSettings(): Flow<UserSettings> {
        val usernameFlow = preferenceDataStore.observe(UserSettingsKeys.USERNAME, "Guest")
        val isDarkModeFlow = preferenceDataStore.observe(UserSettingsKeys.IS_DARK_MODE, false)
        val notifCountFlow = preferenceDataStore.observe(UserSettingsKeys.NOTIFICATION_COUNT, 0)
        val lastLoginFlow = preferenceDataStore.observe(UserSettingsKeys.LAST_LOGIN_TIME, 0L)
        val fontScaleFlow = preferenceDataStore.observe(UserSettingsKeys.FONT_SCALE, 1.0f)
        val balanceFlow = preferenceDataStore.observe(UserSettingsKeys.ACCOUNT_BALANCE, 0.0)
        val profileFlow =
            preferenceDataStore.observeObject(UserSettingsKeys.USER_PROFILE, UserProfile())
        val secureDataFlow =
            securedPreferenceDataStore.observe(UserSettingsKeys.SECURE_DATA, SecureData())

        val firstGroup = combine(usernameFlow, isDarkModeFlow, notifCountFlow) { u, d, n ->
            Triple(u, d, n)
        }
        val secondGroup = combine(lastLoginFlow, fontScaleFlow, balanceFlow) { l, f, b ->
            Triple(l, f, b)
        }
        val thirdGroup = combine(profileFlow, secureDataFlow) { profile, secure ->
            Pair(profile, secure)
        }

        return combine(firstGroup, secondGroup, thirdGroup) { g1, g2, g3 ->
            UserSettings(
                username = g1.first,
                isDarkMode = g1.second,
                notificationCount = g1.third,
                lastLoginTime = g2.first,
                fontScale = g2.second,
                balance = g2.third,
                profile = g3.first,
                secureData = g3.second
            )
        }
    }

    override fun getUserSettingsEager(): UserSettings {
        return UserSettings(
            username = preferenceDataStore.getEager(UserSettingsKeys.USERNAME, "Guest"),
            isDarkMode = preferenceDataStore.getEager(UserSettingsKeys.IS_DARK_MODE, false),
            notificationCount = preferenceDataStore.getEager(
                UserSettingsKeys.NOTIFICATION_COUNT,
                0
            ),
            lastLoginTime = preferenceDataStore.getEager(UserSettingsKeys.LAST_LOGIN_TIME, 0L),
            fontScale = preferenceDataStore.getEager(UserSettingsKeys.FONT_SCALE, 1.0f),
            balance = preferenceDataStore.getEager(UserSettingsKeys.ACCOUNT_BALANCE, 0.0),
            profile = preferenceDataStore.getObjectEager(
                UserSettingsKeys.USER_PROFILE,
                UserProfile()
            ),
            secureData = securedPreferenceDataStore.getEager(
                UserSettingsKeys.SECURE_DATA,
                SecureData()
            )
        )
    }

    override suspend fun getUserSettings(): UserSettings {
        return UserSettings(
            username = preferenceDataStore.get(UserSettingsKeys.USERNAME, "Guest"),
            isDarkMode = preferenceDataStore.get(UserSettingsKeys.IS_DARK_MODE, false),
            notificationCount = preferenceDataStore.get(
                UserSettingsKeys.NOTIFICATION_COUNT,
                0
            ),
            lastLoginTime = preferenceDataStore.get(UserSettingsKeys.LAST_LOGIN_TIME, 0L),
            fontScale = preferenceDataStore.get(UserSettingsKeys.FONT_SCALE, 1.0f),
            balance = preferenceDataStore.get(UserSettingsKeys.ACCOUNT_BALANCE, 0.0),
            profile = preferenceDataStore.getObject(
                UserSettingsKeys.USER_PROFILE,
                UserProfile()
            ),
            secureData = securedPreferenceDataStore.get(UserSettingsKeys.SECURE_DATA, SecureData())
        )
    }

    override suspend fun saveUsername(username: String) {
        preferenceDataStore.save(UserSettingsKeys.USERNAME, username)
    }

    override suspend fun saveDarkMode(isDarkMode: Boolean) {
        preferenceDataStore.save(UserSettingsKeys.IS_DARK_MODE, isDarkMode)
    }

    override suspend fun saveNotificationCount(count: Int) {
        preferenceDataStore.save(UserSettingsKeys.NOTIFICATION_COUNT, count)
    }

    override suspend fun saveLastLoginTime(time: Long) {
        preferenceDataStore.save(UserSettingsKeys.LAST_LOGIN_TIME, time)
    }

    override suspend fun saveFontScale(scale: Float) {
        preferenceDataStore.save(UserSettingsKeys.FONT_SCALE, scale)
    }

    override suspend fun saveAccountBalance(balance: Double) {
        preferenceDataStore.save(UserSettingsKeys.ACCOUNT_BALANCE, balance)
    }

    override suspend fun saveUserProfile(profile: UserProfile) {
        preferenceDataStore.saveObject(
            UserSettingsKeys.USER_PROFILE,
            profile
        )
    }

    override suspend fun saveSecureData(secureData: SecureData) {
        securedPreferenceDataStore.save(UserSettingsKeys.SECURE_DATA, secureData)
    }

    override suspend fun removeUserSettings() {
        preferenceDataStore.remove(
            UserSettingsKeys.USERNAME,
            UserSettingsKeys.IS_DARK_MODE,
            UserSettingsKeys.NOTIFICATION_COUNT,
            UserSettingsKeys.LAST_LOGIN_TIME,
            UserSettingsKeys.FONT_SCALE,
            UserSettingsKeys.ACCOUNT_BALANCE,
            UserSettingsKeys.USER_PROFILE.key
        )
        securedPreferenceDataStore.remove(
            UserSettingsKeys.SECURE_DATA
        )
    }
}
