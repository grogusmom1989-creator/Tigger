package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiPearl
import com.example.viewmodel.KikiViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LinkHubScreen(viewModel: KikiViewModel = viewModel()) {
    val feed by viewModel.socialFeed.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(KikiDarkBg).padding(16.dp)) {
        Text("KikiLink Hub", style = MaterialTheme.typography.headlineMedium, color = KikiPearl)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(feed) { item ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(item.authorName, style = MaterialTheme.typography.titleMedium, color = KikiPearl)
                        Text(item.content, style = MaterialTheme.typography.bodyMedium, color = KikiPearl)
                    }
                }
            }
        }
    }
}
