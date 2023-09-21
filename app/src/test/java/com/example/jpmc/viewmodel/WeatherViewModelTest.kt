package com.example.jpmc.viewmodel

import com.example.jpmc.data.repositories.WeatherRepo
import com.example.jpmc.testUtil.CoroutinesTestExtension
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

typealias LocalWeather = com.example.jpmc.data.local.entity.Weather


internal class WeatherViewModelTest {

    @RegisterExtension
    private val coroutinesTestExtension = CoroutinesTestExtension()

    private lateinit var viewModel: WeatherViewModel
    private lateinit var repo: WeatherRepo
    private lateinit var weather: LocalWeather

    @BeforeEach
    fun setup() {
        // Initialize common variables used in multiple tests
        weather = LocalWeather("New York", 22.5, 60, 5.5, "Partly Cloudy", "icon_partly_cloudy.png")

        repo = mockk()
        viewModel = WeatherViewModel(repo)
    }

    @Test
    fun getCurrentWeather() = runTest(coroutinesTestExtension.dispatcher) {
        // Given: The repository is set up to return a mock weather data
        coEvery { repo.getCurrentWeather() } returns weather

        // When: The ViewModel's getCurrentWeather() function is called
        viewModel.getCurrentWeather()

        // Then: The ViewModel's state should be updated with the expected weather data
        assertEquals(weather, viewModel.state.weather)
        assertFalse(viewModel.state.loading)
    }

    @Test
    fun fetchAndSaveWeather() = runTest(coroutinesTestExtension.dispatcher) {
        // Given: The repository is set up to fetch and save weather data
        coEvery { repo.fetchAndSaveWeather("New York") } just Runs
        coEvery { repo.getCurrentWeather() } returns weather

        // When: The ViewModel's fetchAndSaveWeather() function is called
        viewModel.fetchAndSaveWeather("New York")

        // Then: The ViewModel's state should be updated with the expected weather data
        assertEquals(weather, viewModel.state.weather)
        assertFalse(viewModel.state.loading)
    }

    @Test
    fun testExceptionHandling() = runTest(coroutinesTestExtension.dispatcher) {
        // Given: The repository is set up to throw a general exception
        val exceptionMessage = WeatherViewModel.GENERAL_ERROR
        val exception = Exception()
        coEvery { repo.getCurrentWeather() } throws exception

        // When: The ViewModel's getCurrentWeather() function is called
        viewModel.getCurrentWeather()

        // Then: The ViewModel's state should show the general error message
        assertEquals(exceptionMessage, viewModel.state.error)
        assertFalse(viewModel.state.loading)
    }

    @Test
    fun testNetworkExceptionHandling() = runTest(coroutinesTestExtension.dispatcher) {
        // Given: The repository is set up to throw a network exception
        val exceptionMessage = WeatherViewModel.NETWORK_ERROR
        val exception = IOException()
        coEvery { repo.getCurrentWeather() } throws exception

        // When: The ViewModel's getCurrentWeather() function is called
        viewModel.getCurrentWeather()

        // Then: The ViewModel's state should show the network error message
        assertEquals(exceptionMessage, viewModel.state.error)
        assertFalse(viewModel.state.loading)
    }

    @Test
    fun testNotFoundExceptionHandling() = runTest(coroutinesTestExtension.dispatcher) {
        // Given: The repository is set up to throw a 404 not found exception
        val exceptionMessage = WeatherViewModel.NOT_FOUND_ERROR
        val exception = HttpException(Response.error<Any>(404, "".toResponseBody()))
        coEvery { repo.getCurrentWeather() } throws exception

        // When: The ViewModel's getCurrentWeather() function is called
        viewModel.getCurrentWeather()

        // Then: The ViewModel's state should show the not found error message
        assertEquals(exceptionMessage, viewModel.state.error)
        assertFalse(viewModel.state.loading)
    }

    @Test
    fun testServerExceptionHandling() = runTest(coroutinesTestExtension.dispatcher) {
        // Given: The repository is set up to throw a 500 server exception
        val exceptionMessage = WeatherViewModel.SERVER_ERROR
        val exception = HttpException(Response.error<Any>(500, "".toResponseBody()))
        coEvery { repo.getCurrentWeather() } throws exception

        // When: The ViewModel's getCurrentWeather() function is called
        viewModel.getCurrentWeather()

        // Then: The ViewModel's state should show the server error message
        assertEquals(exceptionMessage, viewModel.state.error)
        assertFalse(viewModel.state.loading)
    }

    @Test
    fun testAuthorizationExceptionHandling() = runTest(coroutinesTestExtension.dispatcher) {
        // Given: The repository is set up to throw authorization exceptions (401 and 403)
        val exceptionMessage = WeatherViewModel.AUTHORIZATION_ERROR
        val exception401 = HttpException(Response.error<Any>(401, "".toResponseBody()))
        val exception403 = HttpException(Response.error<Any>(403, "".toResponseBody()))
        coEvery { repo.getCurrentWeather() } throws exception401 andThenThrows exception403

        // When: The ViewModel's getCurrentWeather() function is called twice
        viewModel.getCurrentWeather()
        viewModel.getCurrentWeather()

        // Then: The ViewModel's state should show the authorization error message
        assertEquals(exceptionMessage, viewModel.state.error)
        assertFalse(viewModel.state.loading)
    }

}
