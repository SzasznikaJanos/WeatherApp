package com.jcapps.weatherapp.domain.interactors

import app.cash.turbine.test
import com.jcapps.weatherapp.data.local.preferences.WeatherPreferences
import com.jcapps.weatherapp.domain.models.DomainError
import com.jcapps.weatherapp.domain.models.Either
import com.jcapps.weatherapp.domain.models.Weather
import com.jcapps.weatherapp.domain.repository.WeatherRepository
import com.jcapps.weatherapp.presentation.weather.models.WeatherAction
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult.Failure
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult.Loading
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult.NoCachedCity
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult.RefreshDone
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult.RefreshFailed
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult.Refreshing
import com.jcapps.weatherapp.presentation.weather.models.WeatherResult.ShowWeather
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class WeatherInteractorTest {

    private val testWeather = Weather(
        cityId = 123,
        cityName = "London",
        temperature = 20.0,
        condition = "Clear",
        iconUrl = "https://openweathermap.org/img/w/01d.png",
        description = "Clear sky",
        humidity = 65,
        windSpeed = 5.5,
        timestamp = 1647896400000L
    )

    private val testDispatcher = StandardTestDispatcher()

    private val repository = mockk<WeatherRepository>(relaxed = true)
    private val weatherPreferences = mockk<WeatherPreferences>(relaxed = true) {
        every { lastSearchedCityId } returns flowOf(null)
    }

    private val subject = WeatherInteractor(repository, weatherPreferences)

    @Test
    fun `initResults when no cached city should return NoCachedCity`() =
        runTest(testDispatcher) {
            every { weatherPreferences.lastSearchedCityId } returns flowOf(null)

            subject.initResults().test {
                awaitItem() shouldBe NoCachedCity
                awaitComplete()
            }

            coVerify(exactly = 0) { repository.fetchWeatherByCityId(any()) }
            coVerify(exactly = 0) { repository.fetchWeatherByCityName(any()) }
        }

    @Test
    fun `initResults when cached city should return Loading then ShowWeather`() =
        runTest(testDispatcher) {
            val cachedCityId = 123
            every { weatherPreferences.lastSearchedCityId } returns flowOf(cachedCityId)
            every { repository.observeWeatherByCityId(cachedCityId) } returns flowOf(testWeather)
            coEvery { repository.fetchWeatherByCityId(cachedCityId) } returns Either.Success(
                testWeather
            )

            subject.initResults().test {
                awaitItem() shouldBe Loading
                awaitItem() shouldBe ShowWeather(testWeather)
                awaitComplete()
            }

            coVerify(exactly = 1) { repository.fetchWeatherByCityId(cachedCityId) }
        }

    @Test
    fun `actionToResult when SearchCity with valid city name should return Loading then call repository`() =
        runTest(testDispatcher) {
            val cityName = "London"
            coEvery { repository.fetchWeatherByCityName(cityName) } returns Either.Success(
                testWeather
            )
            coEvery { weatherPreferences.changeCityId(testWeather.cityId) } returns Unit

            subject.actionToResult(WeatherAction.SearchCity(cityName)).test {
                awaitItem() shouldBe Loading
                awaitComplete()
            }

            coVerify(exactly = 1) { repository.fetchWeatherByCityName(cityName) }
            coVerify(exactly = 1) { weatherPreferences.changeCityId(testWeather.cityId) }
        }

    @Test
    fun `actionToResult when SearchCity with repository error should return Loading then Failure`() =
        runTest(testDispatcher) {
            val cityName = "InvalidCity"
            val error = DomainError.CityNotFound
            coEvery { repository.fetchWeatherByCityName(cityName) } returns Either.Error(error)

            subject.actionToResult(WeatherAction.SearchCity(cityName)).test {
                awaitItem() shouldBe Loading
                awaitItem() shouldBe Failure(error)
                awaitComplete()
            }

            coVerify(exactly = 1) { repository.fetchWeatherByCityName(cityName) }
            coVerify(exactly = 0) { weatherPreferences.changeCityId(any()) }
        }

    @Test
    fun `actionToResult when RefreshWeather with valid city name should return Refreshing then RefreshDone`() =
        runTest(testDispatcher) {
            val cityName = "London"
            coEvery { repository.fetchWeatherByCityName(cityName) } returns Either.Success(
                testWeather
            )

            subject.actionToResult(WeatherAction.RefreshWeather(cityName)).test {
                awaitItem() shouldBe Refreshing
                awaitItem() shouldBe RefreshDone
                awaitComplete()
            }

            coVerify(exactly = 1) { repository.fetchWeatherByCityName(cityName) }
        }

    @Test
    fun `actionToResult when RefreshWeather with repository error should return Refreshing then RefreshDone`() =
        runTest(testDispatcher) {
            val cityName = "InvalidCity"
            val error = DomainError.NetworkError
            coEvery { repository.fetchWeatherByCityName(cityName) } returns Either.Error(error)

            subject.actionToResult(WeatherAction.RefreshWeather(cityName)).test {
                awaitItem() shouldBe Refreshing
                awaitItem() shouldBe RefreshFailed(error)
                awaitItem() shouldBe RefreshDone
                awaitComplete()
            }

            coVerify(exactly = 1) { repository.fetchWeatherByCityName(cityName) }
        }

    @Test
    fun `actionToResult when SearchCity with network error should not update preferences`() =
        runTest(testDispatcher) {
            val cityName = "London"
            val error = DomainError.NetworkError
            coEvery { repository.fetchWeatherByCityName(cityName) } returns Either.Error(error)

            subject.actionToResult(WeatherAction.SearchCity(cityName)).test {
                awaitItem() shouldBe Loading
                awaitItem() shouldBe Failure(error)
                awaitComplete()
            }

            coVerify(exactly = 1) { repository.fetchWeatherByCityName(cityName) }
            coVerify(exactly = 0) { weatherPreferences.changeCityId(any()) }
        }

    @Test
    fun `initResults when cached city and null weather should only emit Loading`() =
        runTest(testDispatcher) {
            val cachedCityId = 123
            every { weatherPreferences.lastSearchedCityId } returns flowOf(cachedCityId)
            every { repository.observeWeatherByCityId(cachedCityId) } returns flowOf(null)
            coEvery { repository.fetchWeatherByCityId(cachedCityId) } returns Either.Success(
                testWeather
            )

            subject.initResults().test {
                awaitItem() shouldBe Loading
                awaitComplete()
            }

            coVerify(exactly = 1) { repository.fetchWeatherByCityId(cachedCityId) }
        }
}