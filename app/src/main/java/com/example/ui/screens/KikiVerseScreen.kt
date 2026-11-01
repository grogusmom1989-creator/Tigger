package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.viewmodel.KikiViewModel
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiPearl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KikiVerseScreen(viewModel: KikiViewModel, onNavigateBack: () -> Unit) {
    val blogEntries by viewModel.blogEntries.collectAsState()
    val musicTracks by viewModel.musicTracks.collectAsState()
    val shrinePages by viewModel.shrinePages.collectAsState()
    val galleryItems by viewModel.galleryItems.collectAsState()

    Scaffold(
        containerColor = KikiDarkBg,
        topBar = {
            TopAppBar(
                title = { Text("KikiVerse", color = KikiPearl) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = KikiPearl) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Text("Blog Entries", style = MaterialTheme.typography.titleLarge, color = KikiPearl) }
            items(blogEntries) { entry ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text(entry.title) },
                        supportingContent = { Text(entry.content) }
                    )
                }
            }
            
            item { Text("Music Tracks", style = MaterialTheme.typography.titleLarge, color = KikiPearl) }
            items(musicTracks) { track ->
                val context = androidx.compose.ui.platform.LocalContext.current
                val isPlaying by com.example.util.MusicAudioEngine.isPlaying.collectAsState()
                val activeTrackId by com.example.util.MusicAudioEngine.activeTrackId.collectAsState()
                val isThisPlaying = isPlaying && activeTrackId == track.id
                Card(
                    onClick = {
                        com.example.util.MusicAudioEngine.toggleTrack(
                            context = context,
                            trackId = track.id,
                            title = track.title,
                            sourceLabel = track.artist
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text(track.title) },
                        supportingContent = { Text(track.artist) },
                        trailingContent = {
                            Text(if (isThisPlaying) "🔊 Playing" else "▶️ Play")
                        }
                    )
                }
            }

            item { Text("Shrines", style = MaterialTheme.typography.titleLarge, color = KikiPearl) }
            items(shrinePages) { shrine ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text(shrine.title) },
                        supportingContent = { Text(shrine.description) }
                    )
                }
            }

            item { Text("Gallery", style = MaterialTheme.typography.titleLarge, color = KikiPearl) }
            items(galleryItems) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text(item.title) }
                    )
                }
            }
        }
    }
}
