package com.jcapps.weatherapp.di

import com.jcapps.weatherapp.arch.DispatcherProvider
import com.jcapps.weatherapp.data.api.WeatherApiService
import com.jcapps.weatherapp.data.local.dao.WeatherDao
import com.jcapps.weatherapp.data.source.local.WeatherLocalDataSource
import com.jcapps.weatherapp.data.source.local.WeatherLocalDataSourceImpl
import com.jcapps.weatherapp.data.source.remote.WeatherRemoteDataSource
import com.jcapps.weatherapp.data.source.remote.WeatherRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideWeatherRemoteDataSource(
        apiService: WeatherApiService,
        dispatcherProvider: DispatcherProvider,
    ): WeatherRemoteDataSource = WeatherRemoteDataSourceImpl(apiService, dispatcherProvider)

    @Provides
    @Singleton
    fun provideWeatherLocalDataSource(
        weatherDao: WeatherDao,
    ): WeatherLocalDataSource = WeatherLocalDataSourceImpl(weatherDao)
}