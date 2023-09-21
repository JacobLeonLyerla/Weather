package com.example.jpmc.data.repositories

import com.example.jpmc.data.local.entity.Weather

interface WeatherRepo {
    suspend fun getCurrentWeather(): Weather?
    suspend fun fetchAndSaveWeather(city: String)
}