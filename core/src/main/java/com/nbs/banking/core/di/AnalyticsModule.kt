package com.nbs.banking.core.di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.nbs.banking.core.analytics.Analytics
import com.nbs.banking.core.analytics.tools.AppsflyerProvider
import com.nbs.banking.core.analytics.tools.CrashlyticsProvider
import com.nbs.banking.core.analytics.tools.FirebaseAnalyticsProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    @Singleton
    fun providesFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics

    @Provides
    @Singleton
    fun providesFirebaseCrashlytics(): FirebaseCrashlytics = Firebase.crashlytics

    @Provides
    @Singleton
    fun providesCrashlyticsTools(firebaseCrashlytics: FirebaseCrashlytics, json: Json): CrashlyticsProvider =
        CrashlyticsProvider(firebaseCrashlytics, json)

    @Provides
    @Singleton
    fun providesFirebaseAnalyticsTool(firebaseAnalytics: FirebaseAnalytics): FirebaseAnalyticsProvider =
        FirebaseAnalyticsProvider(firebaseAnalytics)

    @Provides
    @Singleton
    fun provideAppsflyerAnalyticsTool(@ApplicationContext context: Context): AppsflyerProvider =
        AppsflyerProvider(context)

    @Provides
    @Singleton
    fun providesAnalytics(
        firebaseAnalyticsProvider: FirebaseAnalyticsProvider,
        crashlyticsProvider: CrashlyticsProvider,
        appsflyerProvider: AppsflyerProvider,
    ): Analytics = Analytics(
        analyticsProviders = listOf(
            firebaseAnalyticsProvider,
            crashlyticsProvider,
            appsflyerProvider
        ),
    )
}
