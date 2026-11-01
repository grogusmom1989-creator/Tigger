package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KikiPearl
import com.example.viewmodel.KikiViewModel
import com.google.firebase.auth.auth
import com.google.firebase.Firebase

@Composable
fun ProfileScreen(
    viewModel: KikiViewModel,
    onSignOut: () -> Unit
) {
    var username by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var bio by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Edit Profile", style = MaterialTheme.typography.headlineMedium, color = KikiPearl)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { 
            // In a real app, get userId from auth
            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            viewModel.updateProfile(userId, username, bio, null, emptyList()) 
        }) {
            Text("Save Profile")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSignOut) {
            Text("Sign Out")
        }
    }
}
