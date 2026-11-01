package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.viewmodel.KikiViewModel

@Composable
fun TrendPredictorScreen(viewModel: KikiViewModel) {
    val prediction by viewModel.trendPrediction.collectAsState()
    var season by remember { mutableStateOf("Summer") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Seasonal Trend Predictor", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = season, onValueChange = { season = it }, label = { Text("Season") })
        Button(onClick = { viewModel.generateSeasonalTrend(season) }) {
            Text("Predict Trends")
        }
        prediction?.let {
            Text(it, modifier = Modifier.padding(top = 16.dp))
        }
    }
}
