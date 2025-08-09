package com.jcapps.weatherapp.data.source.local

import com.jcapps.weatherapp.data.local.dao.WeatherDao
import com.jcapps.weatherapp.data.local.mappers.toDomain
import com.jcapps.weatherapp.data.local.mappers.toEntity
import com.jcapps.weatherapp.domain.models.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherLocalDataSourceImpl @Inject constructor(
    private val weatherDao: WeatherDao,
) : WeatherLocalDataSource {

    override suspend fun updateWeather(weather: Weather) = weatherDao.upsert(weather.toEntity())

    override fun observeCityWeather(cityId: Int): Flow<Weather?> =
        weatherDao
            .observeCityWeather(cityId)
            .map { it?.toDomain() }
}