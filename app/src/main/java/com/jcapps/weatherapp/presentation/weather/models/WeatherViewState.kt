package com.jcapps.weatherapp.presentation.weather.models

import com.jcapps.weatherapp.domain.models.DomainError
import com.jcapps.weatherapp.domain.models.Weather


sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class ShowWeather(
        val weather: Weather,
        val isRefreshing: Boolean = false,
    ) : WeatherUiState

    data object NoCachedCity : WeatherUiState

    data class Failure(val reason: DomainError) : WeatherUiState
}