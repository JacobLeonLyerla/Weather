package com.example.jpmc.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jpmc.viewmodel.WeatherViewModel

@Composable
fun WeatherContent(viewModel: WeatherViewModel = hiltViewModel()) {
    val state = viewModel.state

    if (state.weather != null) {
        WeatherCard(state.weather)
    } else if (!state.loading) {
        Text(
            text = "Please enter a valid city",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}
