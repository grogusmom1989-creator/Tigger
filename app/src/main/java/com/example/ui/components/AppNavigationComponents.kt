package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiTextMuted
import com.example.viewmodel.KikiNavTab

@Composable
fun AppNavigationBar(currentTab: KikiNavTab, onTabSelected: (KikiNavTab) -> Unit) {
    NavigationBar(
        containerColor = KikiSurfaceDark,
        tonalElevation = 8.dp,
        modifier = Modifier.border(0.5.dp, KikiSurfaceBorder)
    ) {
        KikiNavTab.values().forEach { tab ->
            NavigationBarItem(
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { /* Simplified for brevity, use existing logic */ },
                label = { Text(tab.title) }
            )
        }
    }
}
