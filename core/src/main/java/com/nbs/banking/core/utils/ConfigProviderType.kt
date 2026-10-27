package com.nbs.banking.core.utils

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteConfigProvider

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class FeatureFlagProvider
