package com.jdefey.upsplash.data

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val modifiedRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer " + Networking.token)
            .build()
        return chain.proceed(modifiedRequest)
    }
}
