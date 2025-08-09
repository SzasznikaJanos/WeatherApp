package com.jcapps.weatherapp.data.repository

import app.cash.turbine.test
import com.jcapps.weatherapp.data.source.local.WeatherLocalDataSource
import com.jcapps.weatherapp.data.source.remote.WeatherRemoteDataSource
import com.jcapps.weatherapp.domain.models.DomainError
import com.jcapps.weatherapp.domain.models.Either
import com.jcapps.weatherapp.domain.models.Weather
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class WeatherRepositoryImplTest {

    private val testWeather = Weather(
        cityId = 12345,
        cityName = "London",
        temperature = 20.0,
        condition = "Clear",
        iconUrl = "https://openweathermap.org/img/wn/01d@2x.png",
        description = "clear sky",
        humidity = 65,
        windSpeed = 5.5,
        timestamp = 1647896400000L
    )

    private val remoteDataSource = mockk<WeatherRemoteDataSource>()
    private val localDataSource = mockk<WeatherLocalDataSource>(relaxed = true)

    private val subject = WeatherRepositoryImpl(remoteDataSource, localDataSource)

    @Test
    fun `fetchWeatherByCityName when remote success should return weather and save locally`() =
        runTest {
            coEvery { remoteDataSource.getCurrentWeather("London") } returns Either.Success(
                testWeather
            )

            val result = subject.fetchWeatherByCityName("London")

            result shouldBe Either.Success(testWeather)
            coVerify(exactly = 1) { remoteDataSource.getCurrentWeather("London") }
            coVerify(exactly = 1) { localDataSource.updateWeather(testWeather) }
        }

    @Test
    fun `fetchWeatherByCityName when remote error should return error without saving locally`() =
        runTest {
            val error = DomainError.NetworkError
            coEvery { remoteDataSource.getCurrentWeather("London") } returns Either.Error(error)

            val result = subject.fetchWeatherByCityName("London")

            result shouldBe Either.Error(error)
            coVerify(exactly = 1) { remoteDataSource.getCurrentWeather("London") }
            coVerify(exactly = 0) { localDataSource.updateWeather(any()) }
        }

    @Test
    fun `fetchWeatherByCityName when remote success but local save fails should throw exception`() =
        runTest {
            coEvery { remoteDataSource.getCurrentWeather("London") } returns Either.Success(
                testWeather
            )
            coEvery { localDataSource.updateWeather(testWeather) } throws RuntimeException("Database error")

            shouldThrow<RuntimeException> {
                subject.fetchWeatherByCityName("London")
            }

            coVerify(exactly = 1) { remoteDataSource.getCurrentWeather("London") }
            coVerify(exactly = 1) { localDataSource.updateWeather(testWeather) }
        }

    @Test
    fun `fetchWeatherByCityName when city not found should return CityNotFound error`() = runTest {
        val error = DomainError.CityNotFound
        coEvery { remoteDataSource.getCurrentWeather("InvalidCity") } returns Either.Error(error)

        val result = subject.fetchWeatherByCityName("InvalidCity")

        result shouldBe Either.Error(error)
        coVerify(exactly = 1) { remoteDataSource.getCurrentWeather("InvalidCity") }
        coVerify(exactly = 0) { localDataSource.updateWeather(any()) }
    }

    @Test
    fun `fetchWeatherByCityName when API key error should return ApiKeyError`() = runTest {
        val error = DomainError.ApiKeyError
        coEvery { remoteDataSource.getCurrentWeather("London") } returns Either.Error(error)

        val result = subject.fetchWeatherByCityName("London")

        result shouldBe Either.Error(error)
        coVerify(exactly = 1) { remoteDataSource.getCurrentWeather("London") }
        coVerify(exactly = 0) { localDataSource.updateWeather(any()) }
    }

    @Test
    fun `fetchWeatherByCityName when server error should return ServerError`() = runTest {
        val error = DomainError.ServerError(500, "Internal Server Error")
        coEvery { remoteDataSource.getCurrentWeather("London") } returns Either.Error(error)

        val result = subject.fetchWeatherByCityName("London")

        result shouldBe Either.Error(error)
        coVerify(exactly = 1) { remoteDataSource.getCurrentWeather("London") }
        coVerify(exactly = 0) { localDataSource.updateWeather(any()) }
    }

    @Test
    fun `fetchWeatherByCityId when remote success should return weather and save locally`() =
        runTest {
            coEvery { remoteDataSource.getCurrentWeatherByCityId(12345) } returns Either.Success(
                testWeather
            )

            val result = subject.fetchWeatherByCityId(12345)

            result shouldBe Either.Success(testWeather)
            coVerify(exactly = 1) { remoteDataSource.getCurrentWeatherByCityId(12345) }
            coVerify(exactly = 1) { localDataSource.updateWeather(testWeather) }
        }

    @Test
    fun `fetchWeatherByCityId when remote error should return error without saving locally`() =
        runTest {
            val error = DomainError.NetworkError
            coEvery { remoteDataSource.getCurrentWeatherByCityId(12345) } returns Either.Error(error)

            val result = subject.fetchWeatherByCityId(12345)

            result shouldBe Either.Error(error)
            coVerify(exactly = 1) { remoteDataSource.getCurrentWeatherByCityId(12345) }
            coVerify(exactly = 0) { localDataSource.updateWeather(any()) }
        }

    @Test
    fun `fetchWeatherByCityId when remote success but local save fails should throw exception`() =
        runTest {
            coEvery { remoteDataSource.getCurrentWeatherByCityId(12345) } returns Either.Success(
                testWeather
            )
            coEvery { localDataSource.updateWeather(testWeather) } throws RuntimeException("Database error")

            try {
                subject.fetchWeatherByCityId(12345)
                io.kotest.assertions.fail("Expected RuntimeException to be thrown")
            } catch (e: RuntimeException) {
                e.message shouldBe "Database error"
            }

            coVerify(exactly = 1) { remoteDataSource.getCurrentWeatherByCityId(12345) }
            coVerify(exactly = 1) { localDataSource.updateWeather(testWeather) }
        }

    @Test
    fun `fetchWeatherByCityId when unknown error should return UnknownError`() = runTest {
        val throwable = RuntimeException("Unexpected error")
        val error = DomainError.UnknownError(throwable)
        coEvery { remoteDataSource.getCurrentWeatherByCityId(12345) } returns Either.Error(error)

        val result = subject.fetchWeatherByCityId(12345)

        result shouldBe Either.Error(error)
        coVerify(exactly = 1) { remoteDataSource.getCurrentWeatherByCityId(12345) }
        coVerify(exactly = 0) { localDataSource.updateWeather(any()) }
    }

    @Test
    fun `observeWeatherByCityId when valid city id should return flow from local data source`() =
        runTest {
            val weatherFlow = flowOf(testWeather)
            coEvery { localDataSource.observeCityWeather(12345) } returns weatherFlow

            val result = subject.observeWeatherByCityId(12345)

            result.test {
                awaitItem() shouldBe testWeather
                awaitComplete()
            }

        }

    @Test
    fun `observeWeatherByCityId when null weather should emit null`() = runTest {
        val weatherFlow = flowOf(null)
        coEvery { localDataSource.observeCityWeather(99999) } returns weatherFlow

        subject.observeWeatherByCityId(99999).test {
            awaitItem() shouldBe null
            awaitComplete()
        }
    }

    @Test
    fun `observeWeatherByCityId when multiple emissions should emit all distinct values`() =
        runTest {
            val weather1 = testWeather
            val weather2 = testWeather.copy(temperature = 25.0)
            val weatherFlow = flowOf(
                weather1,
                weather1,
                weather2
            ) // middle duplicate should be filtered by distinctUntilChanged
            coEvery { localDataSource.observeCityWeather(12345) } returns weatherFlow

            subject.observeWeatherByCityId(12345).test {
                awaitItem() shouldBe weather1
                awaitItem() shouldBe weather2
                awaitComplete()
            }
        }
}