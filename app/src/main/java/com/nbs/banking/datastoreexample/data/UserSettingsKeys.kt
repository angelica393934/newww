package com.nbs.banking.datastoreexample.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserSettingsKeys {
    val USERNAME = stringPreferencesKey("username")
    val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    val NOTIFICATION_COUNT = intPreferencesKey("notification_count")
    val LAST_LOGIN_TIME = longPreferencesKey("last_login_time")
    val FONT_SCALE = floatPreferencesKey("font_scale")
    val ACCOUNT_BALANCE = doublePreferencesKey("account_balance")
}
