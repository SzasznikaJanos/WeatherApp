package com.jcapps.weatherapp.presentation.weather.models

import com.jcapps.weatherapp.domain.models.DomainError
import com.jcapps.weatherapp.domain.models.Weather

sealed interface WeatherResult {
    data object Loading : WeatherResult
    data class ShowWeather(val weather: Weather) : WeatherResult
    data class Failure(val error: DomainError) : WeatherResult

    
    data object Refreshing : WeatherResult
    data object RefreshDone : WeatherResult
    data class RefreshFailed(val error: DomainError) : WeatherResult
    data object NoCachedCity : WeatherResult
}