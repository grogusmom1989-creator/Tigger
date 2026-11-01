package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiPearl

@Composable
fun NewsScreen() {
    Column(modifier = Modifier.fillMaxSize().background(KikiDarkBg).padding(16.dp)) {
        Text("NEWS", style = MaterialTheme.typography.headlineMedium, color = KikiPearl)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Configure API key in Secrets panel to load latest news.", color = KikiPearl)
    }
}
