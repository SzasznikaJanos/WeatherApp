package com.jcapps.weatherapp.data.local.mappers

import com.jcapps.weatherapp.data.local.entities.WeatherEntity
import com.jcapps.weatherapp.domain.models.Weather

fun WeatherEntity.toDomain(): Weather {
    return Weather(
        cityId = cityId,
        cityName = cityName,
        temperature = temperature,
        condition = condition,
        iconUrl = iconUrl,
        description = description,
        humidity = humidity,
        windSpeed = windSpeed,
        timestamp = timestamp
    )
}

fun Weather.toEntity(): WeatherEntity {
    return WeatherEntity(
        cityId = cityId,
        cityName = cityName,
        temperature = temperature,
        condition = condition,
        iconUrl = iconUrl,
        description = description,
        humidity = humidity,
        windSpeed = windSpeed,
        timestamp = timestamp
    )
}