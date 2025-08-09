package com.jcapps.weatherapp.data.source.local

import com.jcapps.weatherapp.domain.models.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    suspend fun updateWeather(weather: Weather)
    fun observeCityWeather(cityId: Int): Flow<Weather?>
}