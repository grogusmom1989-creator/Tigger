package com.example.ui.screens

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceDark
import androidx.compose.ui.viewinterop.AndroidView

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import com.example.data.AIHubServices
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebPortalScreen(
    selectedServiceId: String?,
    isSyncing: Boolean = false,
    onSyncTriggered: () -> Unit = {},
    onServiceSelected: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (selectedServiceId == null) {
        AIHubMenuScreen(onServiceSelected = onServiceSelected)
    } else {
        val selectedService = com.example.data.AIHubServices.services.find { it.id == selectedServiceId }
        
        if (selectedServiceId == "games_portal") {
            com.example.ui.screens.AppLauncherScreen(
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
                onNavigateBack = onNavigateBack
            )
            return
        }

        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = KikiDarkBg,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = selectedService?.name ?: "AI Portal",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = KikiPearl)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = KikiSurfaceDark
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(KikiDarkBg)
            ) {
                if (selectedService != null) {
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.loadWithOverviewMode = true
                                settings.useWideViewPort = true
                                webViewClient = WebViewClient()
                                loadUrl(selectedService.url)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
