package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiTextMuted

@Composable
fun SecurityDashboardScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KikiDarkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("SECURITY DASHBOARD", style = MaterialTheme.typography.headlineSmall, color = KikiPearl, modifier = Modifier.padding(bottom = 16.dp))

        SecurityCard(
            title = "Anti-Tamper Status",
            description = "System integrity check passed.",
            icon = Icons.Default.Security,
            status = "SECURE"
        )
        SecurityCard(
            title = "Suspicious Activity",
            description = "No unauthorized access detected in last 24h.",
            icon = Icons.Default.Warning,
            status = "CLEAN"
        )
        
        Button(
            onClick = { /* Handle review */ },
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
            Icon(Icons.Default.Lock, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("One-Tap Permission Review")
        }
    }
}

@Composable
fun SecurityCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, status: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = KikiLilac, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = KikiPearl)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = KikiTextMuted)
                Text(status, style = MaterialTheme.typography.labelSmall, color = if (status == "SECURE") Color.Green else Color.Yellow)
            }
        }
    }
}
