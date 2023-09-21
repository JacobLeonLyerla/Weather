package com.example.jpmc.data.remote.services

import com.example.jpmc.BuildConfig
import com.example.jpmc.data.remote.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * WeatherService Interface
 *
 * ## Adding OpenWeather API Key
 * This service uses the OpenWeather API. To interact with this API, you'll need to add your OpenWeather API key.
 *
 * 1. Open the `local.properties` file located in your project root directory.
 * 2. Add your OpenWeather API key like so:
 *
 * ```
 * key=Your-OpenWeather-Key-Here
 * ```
 *
 * Make sure to replace `Your-OpenWeather-Key-Here` with your actual OpenWeather API key.
 *
 */

interface WeatherService {

    @GET("data/2.5/weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String = BuildConfig.KEY,
        @Query("units") units: String = "imperial"
    ): WeatherResponse

}