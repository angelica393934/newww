package com.nbs.banking.navigation.deeplink

import com.nbs.banking.navigation.BuildConfig

object DeeplinkConfig {
    private const val SCHEME = BuildConfig.DEEP_LINK_SCHEME
    private const val HOST = BuildConfig.DEEP_LINK_HOST
    const val DEEPLINK_URI = "$SCHEME://$HOST/"
}