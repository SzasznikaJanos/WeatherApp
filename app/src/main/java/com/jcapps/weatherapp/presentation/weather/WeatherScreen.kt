package com.jcapps.weatherapp.presentation.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jcapps.weatherapp.R
import com.jcapps.weatherapp.arch.EffectFlow
import com.jcapps.weatherapp.arch.WatchEffectFlow
import com.jcapps.weatherapp.domain.models.DomainError
import com.jcapps.weatherapp.domain.models.Weather
import com.jcapps.weatherapp.presentation.components.ErrorCard
import com.jcapps.weatherapp.presentation.components.LoadingIndicator
import com.jcapps.weatherapp.presentation.components.SearchBar
import com.jcapps.weatherapp.presentation.components.WeatherCard
import com.jcapps.weatherapp.presentation.weather.models.WeatherAction
import com.jcapps.weatherapp.presentation.weather.models.WeatherEffect
import com.jcapps.weatherapp.presentation.weather.models.WeatherUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewState: WeatherUiState,
    processAction: (WeatherAction) -> Unit,
    effectFlow: EffectFlow<WeatherEffect>,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Pre-create error messages to avoid calling stringResource in LaunchedEffect
    val networkError = stringResource(R.string.error_network)
    val apiKeyError = stringResource(R.string.error_api_key)
    val cityNotFoundError = stringResource(R.string.error_city_not_found)
    val invalidInputError = stringResource(R.string.error_invalid_input)
    val timeoutError = stringResource(R.string.error_timeout)
    val unknownError = stringResource(R.string.error_unknown)
    val serverErrorFormat = stringResource(R.string.error_server, "")

    WatchEffectFlow(effectFlow) { effect ->
        when (effect) {
            is WeatherEffect.FailureSnackbar -> {
                val message = when (effect.failure) {
                    DomainError.NetworkError -> networkError
                    DomainError.ApiKeyError -> apiKeyError
                    DomainError.CityNotFound -> cityNotFoundError
                    DomainError.InvalidInput -> invalidInputError
                    DomainError.TimeoutError -> timeoutError
                    is DomainError.ServerError -> serverErrorFormat.replace("%s", effect.failure.message)
                    is DomainError.UnknownError -> unknownError
                }
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            SearchBar(
                onSearchCity = { cityName ->
                    keyboardController?.hide()
                    processAction(WeatherAction.SearchCity(cityName))
                },
                enabled = viewState !is WeatherUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            when (viewState) {
                WeatherUiState.Loading -> LoadingIndicator()
                is WeatherUiState.Failure -> ErrorCard()
                is WeatherUiState.ShowWeather -> WeatherContent(
                    viewState.weather, viewState.isRefreshing, processAction
                )

                is WeatherUiState.NoCachedCity -> Unit // We just want to show the search bar
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherContent(
    weather: Weather,
    isRefreshing: Boolean,
    processAction: (WeatherAction) -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { processAction(WeatherAction.RefreshWeather(weather.cityName)) },
        modifier = Modifier.testTag("pull_to_refresh")
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            WeatherCard(weather = weather)
        }
    }
}

