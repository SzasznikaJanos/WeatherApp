package com.jcapps.weatherapp.data.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("main")
    val main: Main,
    @SerialName("weather")
    val weather: List<WeatherInfo>,
    @SerialName("wind")
    val wind: Wind,
    @SerialName("dt")
    val dt: Long
)

@Serializable
data class Main(
    @SerialName("temp")
    val temp: Double,
    @SerialName("humidity")
    val humidity: Int
)

@Serializable
data class WeatherInfo(
    @SerialName("main")
    val main: String,
    @SerialName("description")
    val description: String,
    @SerialName("icon")
    val icon: String
)

@Serializable
data class Wind(
    @SerialName("speed")
    val speed: Double
)