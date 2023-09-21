package com.example.jpmc.viewmodel

import androidx.annotation.OpenForTesting
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jpmc.data.repositories.WeatherRepo
import com.example.jpmc.viewmodel.state.WeatherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OpenForTesting
@HiltViewModel
open class WeatherViewModel @Inject constructor(
    private val weatherRepo: WeatherRepo
) : ViewModel() {

    var state by mutableStateOf(WeatherState())
        private set

    // A coroutine exception handler to manage various types of errors and update the ViewModel's state accordingly.
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        val message = when (exception) {
            is IOException -> NETWORK_ERROR
            is HttpException -> {
                when (exception.code()) {
                    404 -> NOT_FOUND_ERROR
                    500 -> SERVER_ERROR
                    401, 403 -> AUTHORIZATION_ERROR
                    else -> GENERAL_ERROR
                }
            }

            else -> GENERAL_ERROR
        }

        state = state.copy(weather = null, error = message, loading = false)
    }

    // Represents the current active job, which allows canceling an ongoing operation to start a new one.
    private var currentJob: Job? = null

    // Fetches the current weather. Updates the ViewModel's state with new data or any errors that occur during fetching.
    fun getCurrentWeather() {
        currentJob?.cancel()
        currentJob = viewModelScope.launch(exceptionHandler) {
            state = state.copy(loading = true)
            val weather = weatherRepo.getCurrentWeather()
            state = state.copy(weather = weather, loading = false)


        }

    }

    // Fetches and saves weather data for a given city name, and then refreshes the ViewModel's state.
    fun fetchAndSaveWeather(city: String) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch(exceptionHandler) {
            state = state.copy(loading = true)
            weatherRepo.fetchAndSaveWeather(city)
            getCurrentWeather() // Refresh current weather after updating
        }
    }

    // Static strings for various types of errors.
    companion object {
        const val NETWORK_ERROR = "Network Error. Please check your internet connection."
        const val NOT_FOUND_ERROR = "The data you are looking for could not be found."
        const val SERVER_ERROR = "Our servers are currently down. Please try again later."
        const val AUTHORIZATION_ERROR = "You're not authorized to access this content."
        const val GENERAL_ERROR = "An unexpected error occurred. Please try again later."
    }
}
