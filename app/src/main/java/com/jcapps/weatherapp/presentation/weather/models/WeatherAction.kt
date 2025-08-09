package com.jcapps.weatherapp.presentation.weather.models

sealed interface WeatherAction {
    data class SearchCity(val cityName: String) : WeatherAction

    data class RefreshWeather(val cityName: String) : WeatherAction

}