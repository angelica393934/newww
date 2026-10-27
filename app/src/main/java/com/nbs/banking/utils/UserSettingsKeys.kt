package com.nbs.banking.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nbs.banking.core.datastore.objectPreferencesKey
import com.nbs.banking.core.encryption.encryptedKey
import com.nbs.banking.datastoreexample.domain.model.SecureData
import com.nbs.banking.datastoreexample.domain.model.UserProfile

object UserSettingsKeys {
    val USERNAME = stringPreferencesKey("username")
    val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    val NOTIFICATION_COUNT = intPreferencesKey("notification_count")
    val LAST_LOGIN_TIME = longPreferencesKey("last_login_time")
    val FONT_SCALE = floatPreferencesKey("font_scale")
    val ACCOUNT_BALANCE = doublePreferencesKey("account_balance")
    val USER_PROFILE = objectPreferencesKey("user_profile", UserProfile.serializer())
    val SECURE_DATA = encryptedKey<SecureData>("secure_data")
}