package com.jcapps.weatherapp.domain.repository

import com.jcapps.weatherapp.domain.models.DomainError
import com.jcapps.weatherapp.domain.models.Either
import com.jcapps.weatherapp.domain.models.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun fetchWeatherByCityName(city: String): Either<DomainError, Weather>
    suspend fun fetchWeatherByCityId(cityId: Int): Either<DomainError, Weather>

    fun observeWeatherByCityId(cityId: Int): Flow<Weather?>
}