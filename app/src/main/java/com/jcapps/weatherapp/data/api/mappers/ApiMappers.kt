package com.jcapps.weatherapp.data.api.mappers

import com.jcapps.weatherapp.data.api.models.WeatherResponse
import com.jcapps.weatherapp.domain.models.Weather

fun WeatherResponse.toDomain(): Weather {
    val weatherInfo = weather.firstOrNull()
    val iconCode = weatherInfo?.icon ?: ""
    val iconUrl = if (iconCode.isNotEmpty()) {
        "https://openweathermap.org/img/wn/$iconCode@2x.png"
    } else {
        ""
    }
    
    return Weather(
        cityId = id,
        cityName = name,
        temperature = main.temp,
        condition = weatherInfo?.main ?: "",
        iconUrl = iconUrl,
        description = weatherInfo?.description ?: "",
        humidity = main.humidity,
        windSpeed = wind.speed,
        timestamp = dt * 1000L
    )
}