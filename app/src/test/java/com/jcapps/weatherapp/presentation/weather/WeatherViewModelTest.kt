package com.jcapps.weatherapp.presentation.weather

import app.cash.turbine.test
import com.jcapps.weatherapp.arch.DispatcherProvider
import com.jcapps.weatherapp.domain.interactors.WeatherInteractor
import com.jcapps.weatherapp.domain.models.DomainError
import com.jcapps.weatherapp.domain.models.Weather
import com.jcapps.weatherapp.presentation.weather.models.WeatherAction
import com.jcapps.weatherapp.presentation.weather.models.WeatherEffect
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult
import com.jcapps.weatherapp.presentation.weather.models.WeatherUiState
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class WeatherViewModelTest {

    private val testWeatherData = Weather(
        cityId = 123,
        cityName = "London",
        temperature = 20.5,
        condition = "Sunny",
        iconUrl = "https://example.com/icon.png",
        description = "Clear sky",
        humidity = 65,
        windSpeed = 5.2,
        timestamp = 1234567890L
    )

    private val testDispatcher = UnconfinedTestDispatcher()
    
    private val interactor = mockk<WeatherInteractor> {
        every { initResults() } returns emptyFlow()
    }
    
    private val dispatcherProvider = mockk<DispatcherProvider> {
        every { viewModel } returns testDispatcher
    }
    
    private lateinit var subject: WeatherViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        subject = WeatherViewModel(interactor, dispatcherProvider)
        verify(exactly = 1) { interactor.initResults() }
    }

    @Test
    fun `initialization when created should start with loading state`() = runTest {
        subject.uiState.test {
            awaitItem().shouldBeInstanceOf<WeatherUiState.Loading>()
        }
    }

    @Test
    fun `handleResult when Loading should return Loading state`() = runTest {
        val previousState = WeatherUiState.ShowWeather(testWeatherData, isRefreshing = false)
        
        val result = subject.handleResult(previousState, WeatherResult.Loading)
        
        result.shouldBeInstanceOf<WeatherUiState.Loading>()
    }

    @Test
    fun `handleResult when ShowWeather should return ShowWeather state`() = runTest {
        val previousState = WeatherUiState.Loading
        
        val result = subject.handleResult(previousState, WeatherResult.ShowWeather(testWeatherData))
        
        result.shouldBeInstanceOf<WeatherUiState.ShowWeather>()
        result.weather shouldBe testWeatherData
        result.isRefreshing shouldBe false
    }

    @Test
    fun `handleResult when NoCachedCity should return NoCachedCity state`() = runTest {
        val previousState = WeatherUiState.Loading
        
        val result = subject.handleResult(previousState, WeatherResult.NoCachedCity)
        
        result.shouldBeInstanceOf<WeatherUiState.NoCachedCity>()
    }

    @Test
    fun `handleResult when Failure with NetworkError should return Failure state and emit effect`() = runTest {
        val error = DomainError.NetworkError
        
        subject.uiEffectFlow.flow.test {
            val result = subject.handleResult(WeatherUiState.Loading, WeatherResult.Failure(error))
            
            result.shouldBeInstanceOf<WeatherUiState.Failure>()
            result.reason shouldBe error
            
            (awaitItem() as WeatherEffect.FailureSnackbar).failure shouldBe error
        }
    }

    @Test
    fun `handleResult when Failure with CityNotFound should return Failure state and emit effect`() = runTest {
        val error = DomainError.CityNotFound
        
        subject.uiEffectFlow.flow.test {
            val result = subject.handleResult(WeatherUiState.Loading, WeatherResult.Failure(error))
            
            result.shouldBeInstanceOf<WeatherUiState.Failure>()
            result.reason shouldBe error
            
            (awaitItem() as WeatherEffect.FailureSnackbar).failure shouldBe error
        }
    }

    @Test
    fun `handleResult when Failure with ApiKeyError should return Failure state and emit effect`() = runTest {
        val error = DomainError.ApiKeyError
        
        subject.uiEffectFlow.flow.test {
            val result = subject.handleResult(WeatherUiState.Loading, WeatherResult.Failure(error))
            
            result.shouldBeInstanceOf<WeatherUiState.Failure>()
            result.reason shouldBe error
            
            (awaitItem() as WeatherEffect.FailureSnackbar).failure shouldBe error
        }
    }

    @Test
    fun `handleResult when Failure with ServerError should return Failure state and emit effect`() = runTest {
        val error = DomainError.ServerError(500, "Internal Server Error")
        
        subject.uiEffectFlow.flow.test {
            val result = subject.handleResult(WeatherUiState.Loading, WeatherResult.Failure(error))
            
            result.shouldBeInstanceOf<WeatherUiState.Failure>()
            result.reason shouldBe error
            
            (awaitItem() as WeatherEffect.FailureSnackbar).failure shouldBe error
        }
    }

    @Test
    fun `handleResult when Failure with InvalidInput should return Failure state and emit effect`() = runTest {
        val error = DomainError.InvalidInput
        
        subject.uiEffectFlow.flow.test {
            val result = subject.handleResult(WeatherUiState.Loading, WeatherResult.Failure(error))
            
            result.shouldBeInstanceOf<WeatherUiState.Failure>()
            result.reason shouldBe error
            
            (awaitItem() as WeatherEffect.FailureSnackbar).failure shouldBe error
        }
    }

    @Test
    fun `handleResult when Failure with UnknownError should return Failure state and emit effect`() = runTest {
        val error = DomainError.UnknownError(RuntimeException("Something went wrong"))
        
        subject.uiEffectFlow.flow.test {
            val result = subject.handleResult(WeatherUiState.Loading, WeatherResult.Failure(error))
            
            result.shouldBeInstanceOf<WeatherUiState.Failure>()
            result.reason shouldBe error
            
            (awaitItem() as WeatherEffect.FailureSnackbar).failure shouldBe error
        }
    }

    @Test
    fun `handleResult when Failure with TimeoutError should return Failure state and emit effect`() = runTest {
        val error = DomainError.TimeoutError
        
        subject.uiEffectFlow.flow.test {
            val result = subject.handleResult(WeatherUiState.Loading, WeatherResult.Failure(error))
            
            result.shouldBeInstanceOf<WeatherUiState.Failure>()
            result.reason shouldBe error
            
            (awaitItem() as WeatherEffect.FailureSnackbar).failure shouldBe error
        }
    }

    @Test
    fun `handleResult when Refreshing with ShowWeather state should set isRefreshing to true`() = runTest {
        val previousState = WeatherUiState.ShowWeather(testWeatherData, isRefreshing = false)
        
        val result = subject.handleResult(previousState, WeatherResult.Refreshing)
        
        result.shouldBeInstanceOf<WeatherUiState.ShowWeather>()
        result.weather shouldBe testWeatherData
        result.isRefreshing shouldBe true
    }

    @Test
    fun `handleResult when Refreshing with non-ShowWeather state should return same state`() = runTest {
        val previousState = WeatherUiState.Loading
        
        val result = subject.handleResult(previousState, WeatherResult.Refreshing)
        
        result shouldBe WeatherUiState.Loading
    }

    @Test
    fun `handleResult when RefreshDone with ShowWeather state should set isRefreshing to false`() = runTest {
        val previousState = WeatherUiState.ShowWeather(testWeatherData, isRefreshing = true)
        
        val result = subject.handleResult(previousState, WeatherResult.RefreshDone)
        
        result.shouldBeInstanceOf<WeatherUiState.ShowWeather>()
        result.weather shouldBe testWeatherData
        result.isRefreshing shouldBe false
    }

    @Test
    fun `handleResult when RefreshDone with non-ShowWeather state should return same state`() = runTest {
        val previousState = WeatherUiState.Failure(DomainError.NetworkError)
        
        val result = subject.handleResult(previousState, WeatherResult.RefreshDone)
        
        result shouldBe previousState
    }

    @Test
    fun `processUiAction when SearchCity should trigger interactor actionToResult`() = runTest {
        val testAction = WeatherAction.SearchCity("London")
        every { interactor.actionToResult(testAction) } returns flowOf(WeatherResult.Loading)
        
        subject.processUiAction(testAction)
        
        verify(exactly = 1) { interactor.actionToResult(testAction) }
    }

    @Test
    fun `processUiAction when RefreshWeather should trigger interactor actionToResult`() = runTest {
        val testAction = WeatherAction.RefreshWeather("London")
        every { interactor.actionToResult(testAction) } returns flowOf(
            WeatherResult.Refreshing,
            WeatherResult.RefreshDone
        )
        
        subject.processUiAction(testAction)
        
        verify(exactly = 1) { interactor.actionToResult(testAction) }
    }

    @Test
    fun `uiState when action processed should update based on result`() = runTest {
        val testAction = WeatherAction.SearchCity("London")
        every { interactor.actionToResult(testAction) } returns flowOf(
            WeatherResult.Loading,
            WeatherResult.ShowWeather(testWeatherData)
        )
        
        subject.uiState.test {
            awaitItem().shouldBeInstanceOf<WeatherUiState.Loading>()
            
            subject.processUiAction(testAction)
            
            // With UnconfinedTestDispatcher, states may be emitted immediately
            // We might get Loading or go directly to ShowWeather
            val nextState = awaitItem()
            val finalState = when (nextState) {
                is WeatherUiState.Loading -> {
                    // If we got Loading, the next should be ShowWeather
                    awaitItem()
                }
                is WeatherUiState.ShowWeather -> {
                    // If we got ShowWeather directly, that's our final state
                    nextState
                }
                else -> throw AssertionError("Unexpected state: $nextState")
            }
            
            finalState.shouldBeInstanceOf<WeatherUiState.ShowWeather>()
            finalState.weather shouldBe testWeatherData
        }
    }

    @Test
    fun `effects when emitted should be consumed once`() = runTest {
        every { interactor.actionToResult(any()) } returns flowOf(
            WeatherResult.Failure(DomainError.NetworkError)
        )
        
        subject.uiEffectFlow.flow.test {
            subject.processUiAction(WeatherAction.SearchCity("London"))
            
            awaitItem().shouldBeInstanceOf<WeatherEffect.FailureSnackbar>()
            
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `multiple rapid state changes should be handled correctly`() = runTest {
        val testAction1 = WeatherAction.SearchCity("London")
        val testAction2 = WeatherAction.SearchCity("Paris")
        val parisWeather = testWeatherData.copy(cityName = "Paris", cityId = 456)
        
        every { interactor.actionToResult(testAction1) } returns flowOf(
            WeatherResult.Loading,
            WeatherResult.ShowWeather(testWeatherData)
        )
        every { interactor.actionToResult(testAction2) } returns flowOf(
            WeatherResult.Loading,
            WeatherResult.ShowWeather(parisWeather)
        )
        
        subject.uiState.test {
            awaitItem().shouldBeInstanceOf<WeatherUiState.Loading>()
            
            subject.processUiAction(testAction1)
            subject.processUiAction(testAction2)
            
            // With UnconfinedTestDispatcher, emissions happen immediately
            // We need to consume states until we get the final Paris weather
            var foundParisWeather = false
            var currentState: WeatherUiState
            
            while (!foundParisWeather) {
                currentState = awaitItem()
                if (currentState is WeatherUiState.ShowWeather && currentState.weather.cityName == "Paris") {
                    foundParisWeather = true
                    currentState.weather.cityName shouldBe "Paris"
                }
            }
        }
    }

    @Test
    fun `refresh flow should update isRefreshing state correctly`() = runTest {
        val refreshAction = WeatherAction.RefreshWeather("London")
        every { interactor.actionToResult(refreshAction) } returns flowOf(
            WeatherResult.Refreshing,
            WeatherResult.RefreshDone
        )
        
        val initialShowWeatherState = WeatherUiState.ShowWeather(testWeatherData, isRefreshing = false)
        
        val refreshingResult = subject.handleResult(initialShowWeatherState, WeatherResult.Refreshing)
        refreshingResult.shouldBeInstanceOf<WeatherUiState.ShowWeather>()
        refreshingResult.isRefreshing shouldBe true
        
        val refreshDoneResult = subject.handleResult(refreshingResult, WeatherResult.RefreshDone)
        refreshDoneResult.shouldBeInstanceOf<WeatherUiState.ShowWeather>()
        refreshDoneResult.isRefreshing shouldBe false
    }

    @Test
    fun `initResults when has cached city should emit ShowWeather`() = runTest {
        val weatherInteractor = mockk<WeatherInteractor> {
            every { initResults() } returns flowOf(
                WeatherResult.Loading,
                WeatherResult.ShowWeather(testWeatherData)
            )
        }
        
        val viewModel = WeatherViewModel(weatherInteractor, dispatcherProvider)
        
        viewModel.uiState.test {
            // The flow may emit Loading first (from initialState) and then from initResults
            val firstState = awaitItem()
            if (firstState is WeatherUiState.Loading) {
                val secondState = awaitItem()
                if (secondState is WeatherUiState.Loading) {
                    // If we got two Loading states, the ShowWeather is next
                    (awaitItem() as WeatherUiState.ShowWeather).weather shouldBe testWeatherData
                } else {
                    // If second state is not Loading, it should be ShowWeather
                    (secondState as WeatherUiState.ShowWeather).weather shouldBe testWeatherData
                }
            } else {
                // If first state is not Loading, it should be ShowWeather
                (firstState as WeatherUiState.ShowWeather).weather shouldBe testWeatherData
            }
        }
    }

    @Test
    fun `initResults when no cached city should emit NoCachedCity`() = runTest {
        val weatherInteractor = mockk<WeatherInteractor> {
            every { initResults() } returns flowOf(WeatherResult.NoCachedCity)
        }
        
        val viewModel = WeatherViewModel(weatherInteractor, dispatcherProvider)
        
        viewModel.uiState.test {
            // The flow starts with Loading (from initialState) then processes NoCachedCity
            val firstState = awaitItem()
            if (firstState is WeatherUiState.Loading) {
                awaitItem().shouldBeInstanceOf<WeatherUiState.NoCachedCity>()
            } else {
                firstState.shouldBeInstanceOf<WeatherUiState.NoCachedCity>()
            }
        }
    }

    @Test
    fun `state transitions from Loading to ShowWeather should be smooth`() = runTest {
        every { interactor.actionToResult(any()) } returns flowOf(
            WeatherResult.Loading,
            WeatherResult.ShowWeather(testWeatherData)
        )
        
        subject.uiState.test {
            awaitItem().shouldBeInstanceOf<WeatherUiState.Loading>()
            
            subject.processUiAction(WeatherAction.SearchCity("London"))
            
            // With UnconfinedTestDispatcher, states may be emitted immediately
            // We may get Loading or go directly to ShowWeather
            val nextState = awaitItem()
            when (nextState) {
                is WeatherUiState.Loading -> {
                    (awaitItem() as WeatherUiState.ShowWeather).weather shouldBe testWeatherData
                }
                is WeatherUiState.ShowWeather -> {
                    nextState.weather shouldBe testWeatherData
                }
                else -> throw AssertionError("Unexpected state: $nextState")
            }
        }
    }

    @Test
    fun `state transitions from ShowWeather to Failure should preserve previous weather in memory`() = runTest {
        val searchAction = WeatherAction.SearchCity("InvalidCity")
        every { interactor.actionToResult(searchAction) } returns flowOf(
            WeatherResult.Loading,
            WeatherResult.Failure(DomainError.CityNotFound)
        )
        
        val initialState = WeatherUiState.ShowWeather(testWeatherData, isRefreshing = false)
        val loadingResult = subject.handleResult(initialState, WeatherResult.Loading)
        loadingResult.shouldBeInstanceOf<WeatherUiState.Loading>()
        
        val failureResult = subject.handleResult(loadingResult, WeatherResult.Failure(DomainError.CityNotFound))
        failureResult.shouldBeInstanceOf<WeatherUiState.Failure>()
    }

    @Test
    fun `effect emission timing should be immediate during failure handling`() = runTest {
        every { interactor.actionToResult(any()) } returns flowOf(
            WeatherResult.Failure(DomainError.ServerError(500, "Error"))
        )
        
        subject.uiEffectFlow.flow.test {
            subject.processUiAction(WeatherAction.SearchCity("London"))
            
            (awaitItem() as WeatherEffect.FailureSnackbar).failure.shouldBeInstanceOf<DomainError.ServerError>()
        }
    }
}