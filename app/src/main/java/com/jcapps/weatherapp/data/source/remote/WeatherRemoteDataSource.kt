package com.jcapps.weatherapp.data.source.remote

import com.jcapps.weatherapp.domain.models.DomainError
import com.jcapps.weatherapp.domain.models.Either
import com.jcapps.weatherapp.domain.models.Weather

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(cityName: String): Either<DomainError, Weather>
    suspend fun getCurrentWeatherByCityId(cityId: Int): Either<DomainError, Weather>
}