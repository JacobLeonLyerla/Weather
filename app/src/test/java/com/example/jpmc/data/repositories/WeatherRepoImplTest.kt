package com.example.jpmc.data.repositories

import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.example.jpmc.data.local.dao.WeatherDao
import com.example.jpmc.data.remote.response.Clouds
import com.example.jpmc.data.remote.response.Coord
import com.example.jpmc.data.remote.response.Main
import com.example.jpmc.data.remote.response.Sys
import com.example.jpmc.data.remote.response.WeatherResponse
import com.example.jpmc.data.remote.response.Wind
import com.example.jpmc.data.remote.services.WeatherService
import com.example.jpmc.testUtil.CoroutinesTestExtension
import com.example.jpmc.utils.DateUtility
import com.example.jpmc.utils.DateUtility.isDataStale
import com.example.jpmc.utils.loction.LocationTracker
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

typealias RemoteWeather = com.example.jpmc.data.remote.response.Weather
typealias LocalWeather = com.example.jpmc.data.local.entity.Weather

internal class WeatherRepoImplTest {

    // Define properties for mocked objects and extensions
    @RegisterExtension
    private val coroutinesTestExtension = CoroutinesTestExtension()

    // Mock dependencies
    private val service: WeatherService = mockk()
    private val locationTracker: LocationTracker = mockk()
    private val weatherDao: WeatherDao = mockk {
        coEvery { deleteAll() } just Runs
        coEvery { replace(any()) } just Runs
    }
    private val geocoder: Geocoder = mockk()
    private val scope: CoroutineScope = TestScope(coroutinesTestExtension.dispatcher)

    private val repo = WeatherRepoImpl(
        service = service,
        locationTracker = locationTracker,
        weatherDao = weatherDao,
        geocoder = geocoder,
        repoScope = scope
    )

    private lateinit var mockWeather: LocalWeather
    private lateinit var mockWeatherResponse: WeatherResponse
    private lateinit var mockAddress: Address
    private lateinit var mockLocation: Location

    @BeforeEach
    fun setup() {

        // this is used for mocking Java classes
        mockkStatic(DateUtility::class)

        mockLocation = mockk()
        every { mockLocation.latitude } returns LATITUDE
        every { mockLocation.longitude } returns LONGITUDE

        mockAddress = mockk()
        every { mockAddress.locality } returns "New York"
        mockWeather =
            LocalWeather("New York", 22.5, 60, 5.5, "Partly Cloudy", "icon_partly_cloudy.png")
        mockWeatherResponse = WeatherResponse(
            coord = Coord(lon = -74.0060, lat = 40.7128),
            weather = listOf(
                RemoteWeather(
                    id = 800,
                    main = "Clear",
                    description = "clear sky",
                    icon = "01d"
                )
            ),
            base = "stations",
            main = Main(
                temp = 22.5,
                feelsLike = 20.0,
                tempMin = 20.0,
                tempMax = 25.0,
                pressure = 1012,
                humidity = 60
            ),
            visibility = 10000,
            wind = Wind(speed = 5.5, deg = 90),
            rain = null,
            clouds = Clouds(all = 0),
            dt = 1634234400,
            sys = Sys(
                type = 1,
                id = 4929,
                country = "US",
                sunrise = 1634217600,
                sunset = 1634260800
            ),
            timezone = -14400,
            id = 5128581,
            name = "New York",
            cod = 200
        )
    }

    @Test
    fun `test fetching weather when data is locally available and not stale`() =
        runTest(coroutinesTestExtension.dispatcher) {
            // Given: local weather data is available and not stale
            coEvery { weatherDao.currentWeather } returns flowOf(mockWeather)
            every { isDataStale(any()) } returns false

            // When: fetching current weather
            val result = repo.getCurrentWeather()

            // Then: should return local weather data
            assertEquals(mockWeather, result)
        }

    @Test
    fun `test fetching weather when data is locally available but stale`() =
        runTest(coroutinesTestExtension.dispatcher) {
            // Given: local weather data is available but stale
            coEvery { weatherDao.currentWeather } returns flowOf(mockWeather)
            every { isDataStale(any()) } returns true
            coEvery { service.getWeatherByCity(any()) } returns mockWeatherResponse

            // When: fetching current weather
            val result = repo.getCurrentWeather()

            // Then: should return updated weather data
            assertEquals(mockWeather, result)
        }

    @Test
    fun `test fetching weather when data is not locally available but location is provided`() =
        runTest(coroutinesTestExtension.dispatcher) {
            // Given: No local weather data but location is available
            coEvery { weatherDao.currentWeather } returns flowOf(null) andThen flowOf(mockWeather)
            coEvery { locationTracker.getCurrentLocation() } returns mockLocation
            every { geocoder.getFromLocation(any(), any(), any()) } returns listOf(mockAddress)
            coEvery { service.getWeatherByCity(any()) } returns mockWeatherResponse

            // When: fetching current weather
            val result = repo.getCurrentWeather()

            // Then: should return weather data based on the provided location
            assertEquals(mockWeather, result)
        }

    @Test
    fun `test fetchAndSaveWeather method with network error`() =
        runTest(coroutinesTestExtension.dispatcher) {
            // Given: a network error while fetching weather data
            coEvery { service.getWeatherByCity(any()) } throws IOException("Network Error")

            // When: fetching and saving current weather
            val result = runCatching {
                repo.fetchAndSaveWeather("New York")
            }

            // Then: should catch the IOException
            assertTrue(result.isFailure)
            assertEquals("Network Error", result.exceptionOrNull()?.message)
        }

    @Test
    fun `test fetching weather when geocoder returns empty list`() =
        runTest(coroutinesTestExtension.dispatcher) {
            // Given: No local weather data and geocoder returns an empty list
            coEvery { weatherDao.currentWeather } returns flowOf(null)
            coEvery { locationTracker.getCurrentLocation() } returns mockLocation
            every { geocoder.getFromLocation(any(), any(), any()) } returns listOf()

            // When: fetching current weather
            val result = repo.getCurrentWeather()

            // Then: should return null as it cannot determine the location
            assertEquals(null, result)
        }

    @Test
    fun `test multiple calls to getCurrentWeather`() =
        runTest(coroutinesTestExtension.dispatcher) {
            // Given: local weather data is available and not stale
            coEvery { weatherDao.currentWeather } returns flowOf(mockWeather)
            every { isDataStale(any()) } returns false

            // When: fetching current weather multiple times
            val result1 = repo.getCurrentWeather()
            val result2 = repo.getCurrentWeather()

            // Then: should return the same local weather data for both calls
            assertEquals(mockWeather, result1)
            assertEquals(mockWeather, result2)
        }

    companion object {
        const val LATITUDE = 40.7128
        const val LONGITUDE = -74.0060
    }
}
