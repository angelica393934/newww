package com.nbs.banking.core.di

import android.app.Application
import android.content.Context
import android.media.AudioManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nbs.banking.core.notification.NotificationBuilder
import com.nbs.banking.core.notification.NotificationManager
import com.nbs.banking.core.storage.InMemoryStorage
import com.nbs.banking.core.utils.CommonUtils
import com.nbs.banking.core.utils.ContextProvider
import com.nbs.banking.core.utils.JsonLoader
import com.nbs.banking.core.utils.LocationManager
import com.nbs.banking.core.utils.LocationTracker
import com.nbs.banking.core.utils.PhoneBookManager
import com.nbs.banking.core.utils.StatusBarTheme
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideAppCoroutineScope(): CoroutineScope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
            encodeDefaults = false
            useAlternativeNames = false
            prettyPrint = true
        }

    @Provides
    @Singleton
    fun providesFusedLocationProviderClient(application: Application): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    @Provides
    @Singleton
    fun providesLocationTracker(
        fusedLocationProviderClient: FusedLocationProviderClient,
        @ApplicationContext context: Context
    ): LocationTracker =
        LocationManager(
            fusedLocationProviderClient = fusedLocationProviderClient,
            context = context,
        )

    @Provides
    fun provideLocationManager(@ApplicationContext context: Context): android.location.LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager

    @Singleton
    @Provides
    fun provideNotificationBuilder(@ApplicationContext context: Context): NotificationBuilder =
        NotificationBuilder(context)

    @Singleton
    @Provides
    fun provideNotificationManager(notificationBuilder: NotificationBuilder): NotificationManager =
        NotificationManager(notificationBuilder)

    @Singleton
    @Provides
    fun provideCommonUtils(): CommonUtils = CommonUtils

    @Singleton
    @Provides
    fun provideContextProvider(@ApplicationContext context: Context): ContextProvider =
        ContextProvider(context)

//    @Provides
//    @Singleton
//    fun provideLanguageManager(
//        contextProvider: ContextProvider,
//        deviceStorage: DeviceStorage,
//        appScope: CoroutineScope,
//        dataStoreHandler: DataStoreHandler,
//    ): LanguageManager = LanguageManager(contextProvider, deviceStorage, appScope, dataStoreHandler)

    @Provides
    @Singleton
    fun provideInMemoryStore(): InMemoryStorage = InMemoryStorage

    @Provides
    @Singleton
    fun provideJsonLoader(@ApplicationContext context: Context): JsonLoader = JsonLoader(context)

    @Provides
    @Singleton
    fun providePhoneBookManager(@ApplicationContext context: Context): PhoneBookManager =
        PhoneBookManager(context.contentResolver)

    @Provides
    @Singleton
    fun provideStatusBarTheme(): StatusBarTheme = StatusBarTheme()


    @Provides
    @Singleton
    fun provideAudioManager(@ApplicationContext context: Context): AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
}
