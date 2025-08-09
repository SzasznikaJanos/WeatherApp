package com.jcapps.weatherapp.data.source.remote

import com.jcapps.weatherapp.arch.DispatcherProvider
import com.jcapps.weatherapp.data.api.WeatherApiService
import com.jcapps.weatherapp.data.api.mappers.toDomain
import com.jcapps.weatherapp.domain.models.DomainError
import com.jcapps.weatherapp.domain.models.Either
import com.jcapps.weatherapp.domain.models.Weather
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class WeatherRemoteDataSourceImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val dispatcherProvider: DispatcherProvider,
) : WeatherRemoteDataSource {

    override suspend fun getCurrentWeather(cityName: String): Either<DomainError, Weather> =
        withContext(dispatcherProvider.io) {
            apiService.getCurrentWeather(cityName)
                .mapCatching { it.toDomain() }
                .fold(
                    onSuccess = { Either.Success(it) },
                    onFailure = { Either.Error(it.toDomainError()) }
                )
        }

    override suspend fun getCurrentWeatherByCityId(cityId: Int): Either<DomainError, Weather> =
        withContext(dispatcherProvider.io) {
            apiService.getCurrentWeatherByCityId(cityId)
                .mapCatching { it.toDomain() }
                .fold(
                    onSuccess = { Either.Success(it) },
                    onFailure = { Either.Error(it.toDomainError()) }
                )
        }

    private fun Throwable.toDomainError(): DomainError {
        return when (this) {
            is UnknownHostException, is ConnectException -> DomainError.NetworkError
            is SocketTimeoutException -> DomainError.TimeoutError
            is HttpException -> {
                when (code()) {
                    401 -> DomainError.ApiKeyError
                    404 -> DomainError.CityNotFound
                    in 500..599 -> DomainError.ServerError(code(), "Server error occurred")
                    else -> DomainError.UnknownError(this)
                }
            }

            else -> DomainError.UnknownError(this)
        }
    }
}