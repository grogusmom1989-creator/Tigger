package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AIHubService
import com.example.data.AIHubServices
import com.example.ui.theme.*
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.grid.GridItemSpan
import com.example.data.ServiceCategory

@Composable
fun AIHubMenuScreen(
    onServiceSelected: (String) -> Unit
) {
    val groupedServices = remember { AIHubServices.services.groupBy { it.category } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KikiDarkBg)
            .padding(16.dp)
    ) {
        Text(
            text = "KIKI HUB",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                color = KikiPearl
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groupedServices.forEach { (category, services) ->
                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = category.name.replace("_", " "),
                        style = MaterialTheme.typography.titleSmall,
                        color = KikiLilac,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(services) { service ->
                    AIHubServiceCard(service = service, onClick = { onServiceSelected(service.id) })
                }
            }
        }
    }
}

@Composable
fun AIHubServiceCard(
    service: AIHubService,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = service.icon,
                contentDescription = null,
                tint = KikiLilac,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = service.name,
                style = MaterialTheme.typography.titleMedium,
                color = KikiPearl,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
