package com.example.jpmc.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.jpmc.data.local.entity.Weather
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeatherCard(weather: Weather, modifier: Modifier = Modifier) {

    val formattedDate = remember(weather.timestamp) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.US)
        sdf.format(Date(weather.timestamp))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Coil will automatically handle image caches as per the requirement of this project
            Image(
                painter = rememberAsyncImagePainter(
                    "https://openweathermap.org/img/w/${weather.icon}.png"
                ),
                contentDescription = weather.description,
                modifier = modifier
                    .height(150.dp)
                    .clip(MaterialTheme.shapes.large),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Location: ${weather.name}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Temperature: ${weather.temp}°F")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Humidity: ${weather.humidity}%")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Wind Speed: ${weather.windSpeed} mph")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Description: ${weather.description}")
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Last Updated: $formattedDate",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
