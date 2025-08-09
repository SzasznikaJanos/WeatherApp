package com.jcapps.weatherapp.data.repository

import com.jcapps.weatherapp.data.source.local.WeatherLocalDataSource
import com.jcapps.weatherapp.data.source.remote.WeatherRemoteDataSource
import com.jcapps.weatherapp.domain.models.DomainError
import com.jcapps.weatherapp.domain.models.Either
import com.jcapps.weatherapp.domain.models.Weather
import com.jcapps.weatherapp.domain.models.onSuccess
import com.jcapps.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource,
) : WeatherRepository {

    override suspend fun fetchWeatherByCityName(city: String): Either<DomainError, Weather> =
        remoteDataSource
            .getCurrentWeather(city)
            .onSuccess { localDataSource.updateWeather(this) }

    override suspend fun fetchWeatherByCityId(cityId: Int): Either<DomainError, Weather> =
        remoteDataSource
            .getCurrentWeatherByCityId(cityId)
            .onSuccess { localDataSource.updateWeather(this) }


    override fun observeWeatherByCityId(cityId: Int): Flow<Weather?> =
        localDataSource
            .observeCityWeather(cityId)
            .distinctUntilChanged()
}

