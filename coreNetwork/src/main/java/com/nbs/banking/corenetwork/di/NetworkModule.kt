package com.nbs.banking.corenetwork.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.nbs.banking.corenetwork.helper.CertificateManager
import com.nbs.banking.corenetwork.helper.ssl.DynamicSSLProvider
import com.nbs.banking.corenetwork.helper.ssl.DynamicSSLProviderImpl
import com.nbs.banking.corenetwork.interceptor.HeaderInterceptor
import com.nbs.banking.corenetwork.interceptor.ResponseInterceptor
import com.nbs.banking.corenetwork.interceptor.SessionInterceptor
import com.nbs.banking.core.BuildConfig
import com.nbs.banking.corenetwork.core.OkHttpClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    private const val CHUCKER_MAX_CONTENT_LENGTH = 250000L

    @Provides
    @Singleton
    fun provideOkHttpClient(
        headerInterceptor: HeaderInterceptor,
        sessionInterceptor: SessionInterceptor,
        responseInterceptor: ResponseInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
    ): OkHttpClient {
        val interceptors = buildList {
            add(headerInterceptor)
            add(sessionInterceptor)
            add(responseInterceptor)
            if (BuildConfig.DEBUG) {
                add(chuckerInterceptor)
            }
        }

        return OkHttpClientFactory.create(
            interceptors = interceptors,
            showDebugLog = BuildConfig.DEBUG,
        )
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        json: Json,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit
        .Builder()
        .baseUrl("https://bored-api.appbrewery.com/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()

    @Singleton
    fun provideHeaderInterceptor(): HeaderInterceptor = HeaderInterceptor()

    @Singleton
    fun provideResponseInterceptor(): ResponseInterceptor = ResponseInterceptor()


    @Singleton
    fun provideSessionInterceptor(): SessionInterceptor = SessionInterceptor()

    @Provides
    fun providesChuckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor =
        ChuckerInterceptor
            .Builder(context)
            .collector(ChuckerCollector(context))
            .maxContentLength(CHUCKER_MAX_CONTENT_LENGTH)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()

    @Provides
    fun provideCertificatePinning(@ApplicationContext context: Context): CertificateManager =
        CertificateManager(context)

    @Provides
    fun provideDynamicSSLProvider(implementation: DynamicSSLProviderImpl): DynamicSSLProvider =
        implementation
}
