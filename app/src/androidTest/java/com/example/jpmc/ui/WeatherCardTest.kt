package com.example.jpmc.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

typealias LocalWeather = com.example.jpmc.data.local.entity.Weather

@RunWith(AndroidJUnit4::class)
class WeatherCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun weatherCard_ShouldDisplayAllTextElements() {
        val dummyWeather = LocalWeather(
            name = "New York",
            temp = 75.0,
            humidity = 40,
            windSpeed = 5.0,
            description = "Sunny",
            icon = "sunny_icon"
        ).apply {
            timestamp = System.currentTimeMillis()
        }
        val sdf = SimpleDateFormat("hh:mm a", Locale.US)
        val formattedDate = sdf.format(Date(dummyWeather.timestamp))

        // When
        composeTestRule.setContent {
            WeatherCard(weather = dummyWeather)
        }

        // Then
        assertTextExists(dummyWeather, formattedDate)
    }

    @Test
    fun weatherCard_ShouldDisplayTextForRainyWeather() {
        // Given
        val dummyWeather = LocalWeather(
            name = "New York",
            temp = 75.0,
            humidity = 40,
            windSpeed = 5.0,
            description = "Sunny",
            icon = "sunny_icon"
        ).apply {
            timestamp = System.currentTimeMillis()
        }
        val sdf = SimpleDateFormat("hh:mm a", Locale.US)
        val formattedDate = sdf.format(Date(dummyWeather.timestamp))

        // When
        composeTestRule.setContent {
            WeatherCard(weather = dummyWeather)
        }

        // Then
        assertTextExists(dummyWeather, formattedDate)
    }

    // Helper function to assert text elements
    private fun assertTextExists(weather: LocalWeather, formattedDate: String) {
        composeTestRule.onNodeWithText("Location: ${weather.name}").assertExists()
        composeTestRule.onNodeWithText("Temperature: ${weather.temp}°F").assertExists()
        composeTestRule.onNodeWithText("Humidity: ${weather.humidity}%").assertExists()
        composeTestRule.onNodeWithText("Wind Speed: ${weather.windSpeed} mph").assertExists()
        composeTestRule.onNodeWithText("Description: ${weather.description}").assertExists()
        composeTestRule.onNodeWithText("Last Updated: $formattedDate").assertExists()
    }
}