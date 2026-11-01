package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceDark

@Composable
fun AIChatComponent() {
    var message by remember { mutableStateOf("Hello! I am your AI Companion. How can I help you today?") }
    var showChat by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        if (showChat) {
            Card(colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(message, color = KikiPearl)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { message = "You are doing great! Keep it up!" }) {
                        Text("Encourage me!")
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { showChat = !showChat },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Companion")
        }
    }
}
