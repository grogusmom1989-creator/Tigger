package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.viewmodel.SocialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(viewModel: SocialViewModel) {
    val friendships by viewModel.friendships.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Social Hub") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Friends", style = MaterialTheme.typography.titleLarge)
            LazyColumn {
                items(friendships.filter { it.status == "accepted" }) { friendship ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text("Friend: ${friendship.id}", modifier = Modifier.padding(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Friend Requests", style = MaterialTheme.typography.titleLarge)
            LazyColumn {
                items(friendships.filter { it.status == "pending" }) { friendship ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text("Request from: ${friendship.requestSender}")
                        Button(onClick = { viewModel.acceptFriendRequest(friendship.id) }) {
                            Text("Accept")
                        }
                    }
                }
            }
        }
    }
}
