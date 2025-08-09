package com.jcapps.weatherapp.presentation.weather

import com.jcapps.weatherapp.arch.DispatcherProvider
import com.jcapps.weatherapp.arch.FlowViewModel
import com.jcapps.weatherapp.domain.interactors.WeatherInteractor
import com.jcapps.weatherapp.presentation.weather.models.WeatherAction
import com.jcapps.weatherapp.presentation.weather.models.WeatherEffect
import com.jcapps.weatherapp.presentation.weather.models.WeatherEffect.FailureSnackbar
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult.RefreshDone
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult.RefreshFailed
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult.Refreshing
import com.jcapps.weatherapp.presentation.weather.models.WeatherUiState
import com.jcapps.weatherapp.presentation.weather.models.WeatherUiState.Failure
import com.jcapps.weatherapp.presentation.weather.models.WeatherUiState.Loading
import com.jcapps.weatherapp.presentation.weather.models.WeatherUiState.NoCachedCity
import com.jcapps.weatherapp.presentation.weather.models.WeatherUiState.ShowWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    interactors: WeatherInteractor,
    dispatcherProvider: DispatcherProvider,
) : FlowViewModel<WeatherUiState, WeatherAction, WeatherResult, WeatherEffect>(
    flowContext = dispatcherProvider.viewModel,
    interactors = interactors,
    initialState = Loading
) {

    override suspend fun handleResult(
        previous: WeatherUiState,
        result: WeatherResult,
    ): WeatherUiState = when (result) {

        is WeatherResult.Loading -> Loading
        is WeatherResult.ShowWeather -> ShowWeather(result.weather)
        is WeatherResult.Failure -> Failure(result.error).also {
            emitEffect(FailureSnackbar(it.reason))
        }
        WeatherResult.NoCachedCity -> NoCachedCity
        RefreshDone -> previous.updateRefreshingState(false)
        Refreshing -> previous.updateRefreshingState(true)
        is RefreshFailed -> previous.also {
            emitEffect(FailureSnackbar(result.error))
        }
    }
}

private fun WeatherUiState.updateRefreshingState(isRefreshing: Boolean): WeatherUiState =
    when (this) {
        is ShowWeather -> copy(isRefreshing = isRefreshing)
        else -> this
    }