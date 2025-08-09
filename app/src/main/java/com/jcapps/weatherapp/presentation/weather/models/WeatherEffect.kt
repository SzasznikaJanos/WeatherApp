package com.jcapps.weatherapp.presentation.weather.models

import com.jcapps.weatherapp.domain.models.DomainError

sealed class WeatherEffect {

    data class FailureSnackbar(val failure: DomainError) : WeatherEffect()

}