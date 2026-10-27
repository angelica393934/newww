package com.nbs.banking.corenetwork.core

import okhttp3.Authenticator
import okhttp3.CertificatePinner
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpClientFactory {

    private const val DEFAULT_MAX_REQUEST = 10

    fun create(
        interceptors: List<Interceptor> = emptyList(),
        authenticator: Authenticator? = null,
        certificatePinner: CertificatePinner? = null,
        readTimeOut: Int = 60,
        connectTimeOut: Int = 60,
        showDebugLog: Boolean,
        maxRequest: Int = DEFAULT_MAX_REQUEST
    ): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(readTimeOut.toLong(), TimeUnit.SECONDS)
        .connectTimeout(connectTimeOut.toLong(), TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .apply {
            interceptors.forEach(::addInterceptor)
            authenticator?.let(::authenticator)
            certificatePinner?.let(::certificatePinner)
            if (showDebugLog) {
                addInterceptor(
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
                )
            }
            dispatcher(
                Dispatcher().apply {
                    this.maxRequests = maxRequest
                    maxRequestsPerHost = maxRequest
                }
            )
        }.build()
}