package com.example.jpmc.viewmodel.state

import com.example.jpmc.data.local.entity.Weather

data class WeatherState(
    val weather: Weather? = null,
    val error: String? = null,
    val loading: Boolean = false
)
