package com.jcapps.weatherapp.domain.interactors

import com.jcapps.weatherapp.arch.Interactors
import com.jcapps.weatherapp.data.local.preferences.WeatherPreferences
import com.jcapps.weatherapp.domain.models.onFailure
import com.jcapps.weatherapp.domain.models.onSuccess
import com.jcapps.weatherapp.domain.repository.WeatherRepository
import com.jcapps.weatherapp.presentation.weather.models.WeatherAction
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult.Failure
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult.Loading
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult.RefreshFailed
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult.ShowWeather
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class WeatherInteractor @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val weatherPreferences: WeatherPreferences,
) : Interactors<WeatherAction, WeatherResult> {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun initResults(): Flow<WeatherResult> = weatherPreferences
        .lastSearchedCityId
        .flatMapLatest { searchedCityId ->
            if (searchedCityId == null) flowOf(WeatherResult.NoCachedCity)
            else weatherRepository
                .observeWeatherByCityId(searchedCityId)
                .filterNotNull()
                .map { ShowWeather(it) }
                .onStart<WeatherResult> {
                    emit(Loading)
                    weatherRepository.fetchWeatherByCityId(searchedCityId)
                        .onFailure { emit(Failure(this)) }
                }
        }


    override fun actionToResult(action: WeatherAction): Flow<WeatherResult> = when (action) {
        is WeatherAction.SearchCity -> flow {
            emit(Loading)
            weatherRepository.fetchWeatherByCityName(action.cityName)
                .onFailure { emit(Failure(this)) }
                .onSuccess { weatherPreferences.changeCityId(cityId) }
        }

        is WeatherAction.RefreshWeather -> flow {
            emit(WeatherResult.Refreshing)
            weatherRepository.fetchWeatherByCityName(action.cityName)
                .onFailure { emit(RefreshFailed(this)) }
            emit(WeatherResult.RefreshDone)
        }
    }
}