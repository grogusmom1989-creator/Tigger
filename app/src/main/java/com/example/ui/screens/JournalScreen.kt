package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.viewmodel.KikiViewModel

@Composable
fun JournalScreen(viewModel: KikiViewModel) {
    val entries by viewModel.journalEntries.collectAsState()
    var content by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Daily Beauty Journal", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Log entry") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = mood, onValueChange = { mood = it }, label = { Text("Mood") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { viewModel.addJournalEntry(content, mood) }) {
            Text("Save Entry")
        }
        LazyColumn {
            items(entries) { entry ->
                Text(text = "${entry.date}: ${entry.content}")
            }
        }
    }
}
