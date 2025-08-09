package com.jcapps.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.jcapps.weatherapp.presentation.weather.WeatherScreen
import com.jcapps.weatherapp.presentation.weather.WeatherViewModel
import com.jcapps.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                val viewModel: WeatherViewModel = hiltViewModel()
                val viewState by viewModel.uiState.collectAsState()
                
                WeatherScreen(
                    viewState = viewState,
                    processAction = viewModel::processUiAction,
                    effectFlow = viewModel.uiEffectFlow
                )
            }
        }
    }
}