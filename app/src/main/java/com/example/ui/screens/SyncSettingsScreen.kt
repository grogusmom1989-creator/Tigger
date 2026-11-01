package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.util.NotificationUtil
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiPearl
import com.example.viewmodel.KikiViewModel
import androidx.compose.ui.unit.dp
@Composable
fun SyncSettingsScreen(
    viewModel: KikiViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val settingsViewModel: com.example.viewmodel.SystemSettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return com.example.viewmodel.SystemSettingsViewModel(
                    com.example.data.SettingsManager(context)
                ) as T
            }
        }
    )
    val isBackupEnabled by viewModel.isBackupEnabled.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        com.example.util.NotificationUtil.createNotificationChannel(context)
    }

    Column(modifier = Modifier.fillMaxSize().background(KikiDarkBg).padding(16.dp)) {
        Text("SYNC & SETTINGS", style = MaterialTheme.typography.headlineMedium, color = KikiPearl, modifier = Modifier.padding(bottom = 16.dp))
        
        ListItem(
            headlineContent = { Text("Google Drive Backup", color = KikiPearl) },
            supportingContent = { Text("Backup every 24h on Wi-Fi & Charging") },
            trailingContent = {
                Switch(
                    checked = isBackupEnabled,
                    onCheckedChange = { viewModel.toggleBackup(context, it) }
                )
            },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ListItem(
            headlineContent = { Text("Dark Mode", color = KikiPearl) },
            supportingContent = { Text("System-wide dark theme") },
            trailingContent = {
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { settingsViewModel.toggleDarkMode(it) }
                )
            },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}
