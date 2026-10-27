package com.nbs.banking.core.di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.nbs.banking.core.config.provider.ConfigProvider
import com.nbs.banking.core.config.provider.FirebaseConfigProvider
import com.nbs.banking.core.config.provider.LocalConfigProvider
import com.nbs.banking.core.utils.FeatureFlagProvider
import com.nbs.banking.core.utils.RemoteConfigProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {
    private const val SIXTY_MILLIS = 60000L

    @Provides
    @Singleton
    fun provideFirebaseConfig(json: Json): FirebaseConfigProvider =
        FirebaseConfigProvider(Firebase.remoteConfig, json)

    @Provides
    @Singleton
    fun provideLocalConfig(context: Context, json: Json): LocalConfigProvider = LocalConfigProvider(context, json)

//    // TODO: Replace with real credentials
//    @Provides
//    @Singleton
//    fun provideUnleashConfig(context: Context): UnleashConfigProvider = UnleashConfigProvider(
//        unleash = DefaultUnleash(
//            androidContext = context,
//            unleashConfig = UnleashConfig
//                .newBuilder(appName = "test-android-app")
//                .proxyUrl("https://unleash.danamon.co.id/api/frontend")
//                .clientKey("default:development.unleash-insecure-frontend-api-token")
//                .pollingStrategy
//                .interval(SIXTY_MILLIS)
//                .localStorageConfig
//                .enabled(true)
//                .metricsStrategy
//                .interval(SIXTY_MILLIS)
//                .httpClient(
//                    OkHttpClient
//                        .Builder()
//                        .addInterceptor(ChuckerInterceptor(context))
//                        .build(),
//                ).build(),
//        ),
//    )

    @Provides
    @FeatureFlagProvider
    @Singleton
    fun provideFeatureFlagConfigProvider(
        firebaseConfigProvider: FirebaseConfigProvider,
        localConfigProvider: LocalConfigProvider
    ): ConfigProvider = localConfigProvider
//        FlavorType.provideByFlavor(
//        dev = { localConfigProvider },
//        staging = { firebaseConfigProvider },
//        prod = { firebaseConfigProvider },
//    )

    @Provides
    @RemoteConfigProvider
    @Singleton
    fun provideRemoteConfigConfigProvider(
        firebaseConfigProvider: FirebaseConfigProvider,
        localConfigProvider: LocalConfigProvider
    ): ConfigProvider = localConfigProvider
//        FlavorType.provideByFlavor(
//        dev = { localConfigProvider },
//        staging = { firebaseConfigProvider },
//        prod = { firebaseConfigProvider },
//    )
}
