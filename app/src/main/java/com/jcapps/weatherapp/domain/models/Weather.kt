package com.jcapps.weatherapp.domain.models

import java.io.Serializable

data class Weather(
    val cityId: Int,
    val cityName: String,
    val temperature: Double,
    val condition: String,
    val iconUrl: String,
    val description: String,
    val humidity: Int,
    val windSpeed: Double,
    val timestamp: Long
) : Serializable