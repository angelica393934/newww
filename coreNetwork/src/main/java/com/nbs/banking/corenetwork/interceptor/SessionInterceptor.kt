package com.nbs.banking.corenetwork.interceptor

import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class SessionInterceptor @Inject constructor(
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        return chain.proceed(originalRequest)
    }
}
