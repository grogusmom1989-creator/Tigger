package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.viewmodel.KikiViewModel
import kotlinx.coroutines.launch

@Composable
fun AiCompanionScreen(viewModel: KikiViewModel) {
    val messages by viewModel.aiCompanionMessages.collectAsState()
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("KikiWorld AI Companion", style = MaterialTheme.typography.headlineMedium)
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { msg ->
                Text(text = msg, modifier = Modifier.padding(8.dp))
            }
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                label = { Text("Ask Kiki...") }
            )
            Button(onClick = {
                if (message.isNotBlank()) {
                    viewModel.sendAiMessage(message)
                    message = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}
