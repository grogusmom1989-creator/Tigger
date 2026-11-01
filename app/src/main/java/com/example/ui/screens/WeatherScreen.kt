package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiPearl
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.viewmodel.KikiViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WeatherScreen(viewModel: KikiViewModel = viewModel()) {
    val weather by viewModel.weather.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().background(KikiDarkBg).padding(16.dp)) {
        Text("KIKIWEATHER AI", style = MaterialTheme.typography.headlineMedium, color = KikiPearl)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (weather != null) {
            Text("City: ${weather?.name}", color = KikiPearl)
            Text("Report: ${weather?.weather?.get(0)?.description}", color = KikiPearl)
        } else {
            Button(onClick = { viewModel.fetchWeather("Kikiworld") }) {
                Text("Fetch Magical Weather")
            }
        }
    }
}
