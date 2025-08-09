package com.jcapps.weatherapp.data.api

import com.jcapps.weatherapp.data.api.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") cityName: String,
        @Query("units") units: String = "metric"
    ): Result<WeatherResponse>
    
    @GET("weather")
    suspend fun getCurrentWeatherByCityId(
        @Query("id") cityId: Int,
        @Query("units") units: String = "metric"
    ): Result<WeatherResponse>
}