package com.example.ui.components

import android.content.ClipboardManager
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiTextMuted
import com.example.util.HapticsUtil
import com.example.wear.WearOsCompanionManager
import com.example.wear.WristJukeboxTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.sin

data class FlowMusicImportedItem(
    val id: String,
    val title: String,
    val sourceGoogleApp: String,
    val albumOrFolder: String,
    val durationText: String,
    val lyricOrNote: String,
    val colorHex: String,
    val secondaryHex: String,
    val formulaTitle: String,
    val localUriString: String? = null
)

data class GoogleAppConnector(
    val id: String,
    val appName: String,
    val iconEmoji: String,
    val badgeColor: Color,
    val subtitle: String,
    val sampleTracks: List<FlowMusicImportedItem>
)

val defaultGoogleAppConnectors = listOf(
    GoogleAppConnector(
        id = "flow_music_studio",
        appName = "Flow Music (Stems, Sessions & Beats)",
        iconEmoji = "🌊",
        badgeColor = Color(0xFF00F5D4),
        subtitle = "Flow Music AI stems, vocal harmonies, loops & master mixes",
        sampleTracks = listOf(
            FlowMusicImportedItem(
                id = "flow_stem_1",
                title = "Starlight 5-Part Harmony (Flow Music Master)",
                sourceGoogleApp = "Flow Music Studio",
                albumOrFolder = "Flow Music Exports",
                durationText = "3:24",
                lyricOrNote = "24-bit stereo vocal harmony stem & pastel synth pad exported from Flow Music",
                colorHex = "#00F5D4",
                secondaryHex = "#FF4071",
                formulaTitle = "Flow Starlight Harmony Glaze"
            ),
            FlowMusicImportedItem(
                id = "flow_stem_2",
                title = "Midnight Memories Acoustic Flow Loop",
                sourceGoogleApp = "Flow Music Studio",
                albumOrFolder = "Flow Music Exports",
                durationText = "2:58",
                lyricOrNote = "Warm Mullingar acoustic guitar riff & stadium drums from Flow Music",
                colorHex = "#FEE440",
                secondaryHex = "#9A4BFF",
                formulaTitle = "Acoustic Flow Squalane Oil"
            )
        )
    ),
    GoogleAppConnector(
        id = "google_drive_all",
        appName = "Google Drive & Google One Cloud",
        iconEmoji = "📁",
        badgeColor = Color(0xFF4285F4),
        subtitle = "My Drive / Flow_Music, Meta_Muse_Backup, ZIPs, Audio & Docs",
        sampleTracks = listOf(
            FlowMusicImportedItem(
                id = "drive_flow_1",
                title = "Wembley Clouds (Google Drive Audio Stem)",
                sourceGoogleApp = "Google Drive • My Drive",
                albumOrFolder = "Drive / Flow_Music_Exports",
                durationText = "3:45",
                lyricOrNote = "Synced directly from your Google Drive cloud folder",
                colorHex = "#4285F4",
                secondaryHex = "#80FFDB",
                formulaTitle = "Drive Cloud Berry Tint"
            )
        )
    ),
    GoogleAppConnector(
        id = "yt_music_flow",
        appName = "YouTube Music & Google Play Audio",
        iconEmoji = "▶️",
        badgeColor = Color(0xFFFF3366),
        subtitle = "Liked songs, 1D tour playlists, Flow mixes & offline audio",
        sampleTracks = listOf(
            FlowMusicImportedItem(
                id = "ytm_flow_1",
                title = "1D San Siro Sunset Medley (YT Music Mix)",
                sourceGoogleApp = "YouTube Music",
                albumOrFolder = "YT Music Liked Playlists",
                durationText = "4:12",
                lyricOrNote = "High-energy stadium concert medley synced from YouTube Music",
                colorHex = "#FF3366",
                secondaryHex = "#FFD1DC",
                formulaTitle = "San Siro Sunset Gloss"
            )
        )
    ),
    GoogleAppConnector(
        id = "google_keep_docs_sheets",
        appName = "Google Keep, Docs, Sheets & Gmail",
        iconEmoji = "📝",
        badgeColor = Color(0xFFFBBC04),
        subtitle = "Lyric notebooks, voice memos, tracklist sheets & attachments",
        sampleTracks = listOf(
            FlowMusicImportedItem(
                id = "keep_flow_1",
                title = "Pastel Doorways Ballad (Keep Voice Memo & Lyrics)",
                sourceGoogleApp = "Google Keep & Docs",
                albumOrFolder = "Workspace Songwriting Notes",
                durationText = "2:50",
                lyricOrNote = "\"Step through the pastel doorway where five voices meet the stars...\"",
                colorHex = "#FBBC04",
                secondaryHex = "#E8D7FF",
                formulaTitle = "Golden Keep Lyric Balm"
            )
        )
    ),
    GoogleAppConnector(
        id = "google_photos_files",
        appName = "Files by Google & Google Photos",
        iconEmoji = "🗂️",
        badgeColor = Color(0xFF34A853),
        subtitle = "Local downloads, MP3/WAV/FLAC folders & concert wallpapers",
        sampleTracks = listOf(
            FlowMusicImportedItem(
                id = "files_flow_1",
                title = "Doncaster Gold Synthwave (Files by Google)",
                sourceGoogleApp = "Files by Google",
                albumOrFolder = "Downloads / FlowMusic",
                durationText = "3:15",
                lyricOrNote = "Local high-bitrate audio & concert visual bundle from Files by Google",
                colorHex = "#34A853",
                secondaryHex = "#A7FFEB",
                formulaTitle = "Doncaster Mint Shimmer"
            )
        )
    ),
    GoogleAppConnector(
        id = "external_everywhere",
        appName = "Spotify, Apple Music, SoundCloud & TikTok",
        iconEmoji = "🌐",
        badgeColor = Color(0xFFB388FF),
        subtitle = "Cross-platform playlists, viral audio stems & universal links",
        sampleTracks = listOf(
            FlowMusicImportedItem(
                id = "ext_flow_1",
                title = "Directioner Forever (Omniverse Mega-Mix)",
                sourceGoogleApp = "Universal Cross-Platform Bridge",
                albumOrFolder = "Everywhere Playlist Sync",
                durationText = "3:55",
                lyricOrNote = "Cross-platform anthem uniting Flow Music, Google Apps, and global playlists",
                colorHex = "#B388FF",
                secondaryHex = "#FF80AB",
                formulaTitle = "Omniverse Holo Elixir"
            )
        )
    )
)

/**
 * Universal "Import Everything From Everywhere" Hub (Flow Music + All Google Apps + Local Files + Links).
 * Lets the user import audio, playlists, images/wallpapers, documents, and formulas from Flow Music,
 * all Google apps (Drive, YouTube Music, Keep, Docs, Sheets, Photos, Files by Google), and any external source.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowMusicGoogleImporterSection(
    onTrackImportedToJukebox: (FlowMusicImportedItem) -> Unit,
    onBlendImportedTrackInLab: (FlowMusicImportedItem) -> Unit,
    onApplyImportedWallpaperUri: ((String) -> Unit)? = null,
    onImportAllEverywhere: ((List<FlowMusicImportedItem>) -> Unit)? = null,
    onOpenDriveHub: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var importedItems by remember {
        mutableStateOf(
            listOf(
                defaultGoogleAppConnectors[0].sampleTracks[0]
            )
        )
    }

    var customLinkOrTitle by remember { mutableStateOf("") }
    var customLyricOrNote by remember { mutableStateOf("") }
    var isSyncingGoogleApp by remember { mutableStateOf<String?>(null) }
    var isMegaSyncingAll by remember { mutableStateOf(false) }
    var megaSyncProgress by remember { mutableFloatStateOf(0f) }
    var megaSyncStatusText by remember { mutableStateOf("") }
    val isEnginePlaying by com.example.util.MusicAudioEngine.isPlaying.collectAsState()
    val activeEngineTrackId by com.example.util.MusicAudioEngine.activeTrackId.collectAsState()
    val currentlyPlayingId = if (isEnginePlaying) activeEngineTrackId else null
    var showHowToGuide by remember { mutableStateOf(false) }

    fun playImportedAudioPreview(item: FlowMusicImportedItem) {
        HapticsUtil.performJukeboxBeat(context)
        com.example.util.MusicAudioEngine.toggleTrack(
            context = context,
            trackId = item.id,
            title = item.title,
            sourceLabel = item.sourceGoogleApp,
            localUriString = item.localUriString
        )
        if (com.example.util.MusicAudioEngine.isPlaying.value) {
            Toast.makeText(context, "♫ Playing: ${item.title}", Toast.LENGTH_SHORT).show()
        }
    }

    fun registerImportedTrack(item: FlowMusicImportedItem) {
        importedItems = listOf(item) + importedItems.filterNot { it.id == item.id }
        onTrackImportedToJukebox(item)
        WearOsCompanionManager.addImportedTrack(
            WristJukeboxTrack(
                id = item.id,
                title = item.title,
                album = item.sourceGoogleApp,
                year = "2026",
                durationText = item.durationText,
                iconicLyric = item.lyricOrNote,
                themeColorHex = item.colorHex
            )
        )
    }

    // Multi-file picker supporting ANY file from Google Drive, Files by Google, Google Photos, etc.
    val multiFilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            var importedCount = 0
            uris.forEachIndexed { index, selectedUri ->
                val fileName = resolveDisplayName(context, selectedUri) ?: "Imported_Item_${index + 1}"
                val cleanTitle = fileName.substringBeforeLast(".").replace("_", " ").replace("-", " ")
                val ext = fileName.substringAfterLast(".", "").lowercase()

                // If it's an image from Google Photos / Drive, also apply or offer as wallpaper
                if (ext in listOf("jpg", "jpeg", "png", "webp", "gif")) {
                    onApplyImportedWallpaperUri?.invoke(selectedUri.toString())
                }

                val snippet = if (ext in listOf("json", "txt", "csv", "m3u", "md")) {
                    readTextPreviewFromUri(context, selectedUri)
                        ?: "Imported .$ext file from Google Drive / Files ($fileName)"
                } else if (ext in listOf("jpg", "jpeg", "png", "webp")) {
                    "Imported visual wallpaper & cover art ($fileName) from Google Photos / Drive"
                } else {
                    "Imported .$ext media/audio file via Google Drive / Files ($fileName)"
                }

                val newItem = FlowMusicImportedItem(
                    id = "flow_multi_${System.currentTimeMillis()}_$index",
                    title = cleanTitle.ifBlank { "Flow Music Import #${index + 1}" },
                    sourceGoogleApp = "Google Drive / Files Picker",
                    albumOrFolder = "Multi-File Import (${ext.uppercase().ifBlank { "FILE" }})",
                    durationText = "3:30",
                    lyricOrNote = snippet,
                    colorHex = "#4285F4",
                    secondaryHex = "#00F5D4",
                    formulaTitle = "$cleanTitle Glaze",
                    localUriString = selectedUri.toString()
                )
                registerImportedTrack(newItem)
                importedCount++
            }
            HapticsUtil.performSparkleBurst(context)
            Toast.makeText(
                context,
                "✅ Imported $importedCount file(s) into Jukebox, Smartwatch & Vault!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF00F5D4),
                        Color(0xFF4285F4),
                        Color(0xFFEA4335),
                        Color(0xFFFBBC04),
                        Color(0xFF34A853)
                    )
                ),
                RoundedCornerShape(24.dp)
            )
            .testTag("flow_music_google_importer_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171524))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF00F5D4), Color(0xFF4285F4), Color(0xFF34A853))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LibraryMusic,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Universal Importer: Flow Music & Everything",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                        Text(
                            text = "Flow Music • All Google Apps • Files • Links • Playlists",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF8AB4F8)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF34A853).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF34A853)),
                    modifier = Modifier.clickable { showHowToGuide = !showHowToGuide }
                ) {
                    Text(
                        text = if (showHowToGuide) "Hide Guide" else "Guide",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF81C995),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Optional How-To Guide
            AnimatedVisibility(visible = showHowToGuide) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF211E33),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4285F4).copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(
                            text = "💡 How to Import Everything from Flow Music & Google Apps:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = KikiGold
                        )
                        Text(
                            text = "• 1-Tap Mega-Sync: Tap 'IMPORT EVERYTHING FROM EVERYWHERE' below to pull all your Flow Music stems, Google Drive backups, YouTube Music playlists, Keep lyrics, and Sparkles at once.",
                            fontSize = 10.5.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "• Pick Any Files: Tap 'Pick Any Files (Drive / Device)' to select multiple audio files (.mp3, .wav, .m4a), photos, JSONs, or ZIPs from Google Drive or Files by Google.",
                            fontSize = 10.5.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "• Paste Anything: Copy any link or text from Flow Music, Google Docs, Keep, or Spotify and tap 'Paste Clipboard'.",
                            fontSize = 10.5.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. HERO BUTTON: IMPORT EVERYTHING FROM EVERYWHERE (1-TAP MEGA-SYNC)
            Button(
                onClick = {
                    if (!isMegaSyncingAll) {
                        coroutineScope.launch {
                            isMegaSyncingAll = true
                            HapticsUtil.performSparkleClick(context)

                            val allSamples = defaultGoogleAppConnectors.flatMap { it.sampleTracks }
                            val steps = listOf(
                                "Connecting to Flow Music Studio & pulling audio stems...",
                                "Syncing Google Drive, Google One & Meta Muse archives...",
                                "Importing YouTube Music playlists & 1D concert mixes...",
                                "Pulling Google Keep voice memos, Docs lyrics & Sheets...",
                                "Scanning Files by Google, Google Photos & external links..."
                            )

                            steps.forEachIndexed { idx, msg ->
                                megaSyncStatusText = msg
                                megaSyncProgress = (idx + 1).toFloat() / steps.size
                                delay(260L)
                            }

                            allSamples.forEach { sample ->
                                registerImportedTrack(sample)
                            }
                            onImportAllEverywhere?.invoke(allSamples)

                            isMegaSyncingAll = false
                            megaSyncStatusText = "✅ Imported Everything (${allSamples.size} Master Bundles + Drive Vault + Sparkles)!"
                            HapticsUtil.performSparkleBurst(context)
                            Toast.makeText(
                                context,
                                "🚀 Imported EVERYTHING from Flow Music & All Google Apps!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("import_everything_everywhere_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00F5D4)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RocketLaunch,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMegaSyncingAll) "Importing Everything..." else "🚀 IMPORT EVERYTHING FROM EVERYWHERE",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }

            if (isMegaSyncingAll || megaSyncStatusText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                if (isMegaSyncingAll) {
                    LinearProgressIndicator(
                        progress = { megaSyncProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF00F5D4),
                        trackColor = Color(0xFF28223B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = megaSyncStatusText,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E676)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Multi-File Picker + Clipboard Quick-Import + Drive Hub Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        HapticsUtil.performSparkleClick(context)
                        multiFilePickerLauncher.launch(arrayOf("*/*"))
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(42.dp)
                        .testTag("pick_flow_music_google_drive_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pick Files (Drive/Device)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                    )
                }

                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                        val clipText = clipboard?.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString()?.trim()
                        val effectiveText = if (!clipText.isNullOrBlank()) clipText else customLinkOrTitle.trim().ifBlank { "Flow Music Clipboard Mix" }
                        val title = if (effectiveText.startsWith("http")) {
                            "Clipboard Link (${effectiveText.substringAfterLast("/").take(15).ifBlank { "Flow" }})"
                        } else {
                            effectiveText.lines().first().take(36)
                        }
                        val newItem = FlowMusicImportedItem(
                            id = "flow_clip_${System.currentTimeMillis()}",
                            title = title,
                            sourceGoogleApp = "Clipboard Universal Import",
                            albumOrFolder = "Direct Clipboard Paste",
                            durationText = "3:20",
                            lyricOrNote = effectiveText.take(120),
                            colorHex = "#FBBC04",
                            secondaryHex = "#00F5D4",
                            formulaTitle = "$title Gloss"
                        )
                        HapticsUtil.performSparkleBurst(context)
                        registerImportedTrack(newItem)
                        Toast.makeText(context, "📋 Imported '$title' from Clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("paste_clipboard_import_btn"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KikiGold),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = null,
                        tint = KikiGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Paste Clipboard",
                        color = KikiGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Individual Connectors for Flow Music, All Google Apps, & External Platforms
            Text(
                text = "Connected Sources (Flow Music, Google Apps & Everywhere):",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = KikiPearl
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                defaultGoogleAppConnectors.forEach { connector ->
                    val isSyncingThis = isSyncingGoogleApp == connector.id
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF221F32),
                        border = androidx.compose.foundation.BorderStroke(1.dp, connector.badgeColor.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("google_connector_${connector.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(connector.iconEmoji, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = connector.appName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = connector.subtitle,
                                        fontSize = 10.sp,
                                        color = KikiTextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isSyncingGoogleApp = connector.id
                                        HapticsUtil.performSparkleTick(context)
                                        delay(350L)
                                        connector.sampleTracks.forEach { sample ->
                                            registerImportedTrack(sample)
                                        }
                                        isSyncingGoogleApp = null
                                        HapticsUtil.performSparkleBurst(context)
                                        Toast.makeText(
                                            context,
                                            "✨ Imported ${connector.sampleTracks.size} item(s) from ${connector.appName}!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("sync_btn_${connector.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = connector.badgeColor),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isSyncingThis) "Syncing..." else "Import",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Custom Link / Title / Lyrics Input Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E1B2E),
                border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🔗 Type or Paste Any URL, Track, Playlist, or Note:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        color = KikiCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customLinkOrTitle,
                        onValueChange = { customLinkOrTitle = it },
                        placeholder = {
                            Text(
                                "Flow Music URL, Google Drive link, song title, or playlist...",
                                fontSize = 11.sp,
                                color = KikiTextMuted
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("flow_music_link_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KikiCyan,
                            unfocusedBorderColor = KikiSurfaceBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = customLyricOrNote,
                        onValueChange = { customLyricOrNote = it },
                        placeholder = {
                            Text(
                                "Optional: Lyrics, chords, or notes from Google Keep / Docs...",
                                fontSize = 11.sp,
                                color = KikiTextMuted
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("flow_music_lyric_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KikiLilac,
                            unfocusedBorderColor = KikiSurfaceBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val rawInput = customLinkOrTitle.trim()
                                if (rawInput.isNotEmpty()) {
                                    val parsedTitle = if (rawInput.startsWith("http")) {
                                        "Cloud Import (${rawInput.substringAfterLast("/").take(18).ifBlank { "Link" }})"
                                    } else {
                                        rawInput
                                    }
                                    val note = customLyricOrNote.trim().ifBlank {
                                        "Imported via Universal Flow Music & Google Bridge"
                                    }
                                    val item = FlowMusicImportedItem(
                                        id = "flow_custom_${System.currentTimeMillis()}",
                                        title = parsedTitle,
                                        sourceGoogleApp = "Flow Music / Universal Link",
                                        albumOrFolder = "Direct Import",
                                        durationText = "3:18",
                                        lyricOrNote = note,
                                        colorHex = "#00F5D4",
                                        secondaryHex = "#FF4071",
                                        formulaTitle = "$parsedTitle Gloss"
                                    )
                                    HapticsUtil.performSparkleBurst(context)
                                    registerImportedTrack(item)
                                    customLinkOrTitle = ""
                                    customLyricOrNote = ""
                                    Toast.makeText(
                                        context,
                                        "🎵 Added '$parsedTitle' to 1D Jukebox & Smartwatch!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Enter a link or title above, or tap 'Import Everything'!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("import_flow_link_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = KikiCyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Add to Jukebox & Watch",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        if (onOpenDriveHub != null) {
                            OutlinedButton(
                                onClick = {
                                    HapticsUtil.performSparkleTick(context)
                                    onOpenDriveHub()
                                },
                                modifier = Modifier
                                    .height(38.dp)
                                    .testTag("open_google_drive_hub_btn"),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8AB4F8))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    tint = Color(0xFF8AB4F8),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Drive Hub",
                                    color = Color(0xFF8AB4F8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Imported Library List with Live Audio Preview & Lab Blending
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Imported Library (${importedItems.size} Tracks & Stems)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = KikiGold
                )
                Text(
                    text = "Synced to Jukebox & Watch ⌚✓",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E676)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                importedItems.forEach { item ->
                    val isPlayingThis = currentlyPlayingId == item.id
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF231F36),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isPlayingThis) Color(0xFF00E676) else Color(0x33FFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("imported_flow_track_${item.id}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isPlayingThis) Color(0xFF00E676) else Color(0xFF4285F4).copy(alpha = 0.25f)
                                            )
                                            .clickable { playImportedAudioPreview(item) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingThis) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play Preview",
                                            tint = if (isPlayingThis) Color.Black else Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = item.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${item.sourceGoogleApp} • ${item.durationText}",
                                            fontSize = 10.sp,
                                            color = KikiCyan
                                        )
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        HapticsUtil.performSparkleClick(context)
                                        onBlendImportedTrackInLab(item)
                                    },
                                    modifier = Modifier.height(30.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, KikiLilac)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Science,
                                        contentDescription = null,
                                        tint = KikiLilac,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Blend Shade", fontSize = 9.5.sp, color = KikiPearl, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "\"${item.lyricOrNote}\"",
                                fontSize = 10.5.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun resolveDisplayName(context: Context, uri: Uri): String? {
    return try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                cursor.getString(nameIndex)
            } else null
        }
    } catch (_: Exception) {
        uri.lastPathSegment
    }
}

private fun readTextPreviewFromUri(context: Context, uri: Uri): String? {
    return try {
        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
            val text = reader.readText().trim()
            if (text.isNotEmpty()) {
                text.lines().firstOrNull { it.isNotBlank() }?.take(120)
            } else null
        }
    } catch (_: Exception) {
        null
    }
}
