package com.jcapps.weatherapp.data.api.interceptors

import com.jcapps.weatherapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyInterceptor @Inject constructor() : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalHttpUrl = originalRequest.url
        
        if (BuildConfig.WEATHER_API_KEY.isEmpty()) {
            throw IllegalStateException("Weather API key is not configured. Please add WEATHER_API_KEY to your gradle.properties file.")
        }
        
        val urlWithApiKey = originalHttpUrl.newBuilder()
            .addQueryParameter("appid", BuildConfig.WEATHER_API_KEY)
            .build()
        
        val requestWithApiKey = originalRequest.newBuilder()
            .url(urlWithApiKey)
            .build()
        
        return chain.proceed(requestWithApiKey)
    }
}