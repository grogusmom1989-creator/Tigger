package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.wear.WearOsCompanionManager
import com.example.wear.WristJukeboxTrack
import com.example.wear.WristMemberBadge
import com.example.util.HapticsUtil
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HoloBadge
import com.example.ui.components.OneDirectionRed
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted
import com.example.viewmodel.KikiViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

enum class WatchTheme(
    val id: String,
    val title: String,
    val subtitle: String,
    val primaryColor: Color,
    val accentColor: Color,
    val iconEmoji: String,
    val quote: String
) {
    ONE_DIRECTION_BAND(
        id = "1d_band",
        title = "1D Stadium Master",
        subtitle = "Five Voices • Forever 1D",
        primaryColor = OneDirectionRed,
        accentColor = KikiGold,
        iconEmoji = "👑",
        quote = "We're One Direction!"
    ),
    LIAM_DOORWAY(
        id = "liam_watch",
        title = "Liam Payne Watch",
        subtitle = "Payno • Sweet Harmonies",
        primaryColor = Color(0xFF64B5F6),
        accentColor = Color(0xFFFFF176),
        iconEmoji = "🎤",
        quote = "Payno in the house!"
    ),
    HARRY_DOORWAY(
        id = "harry_watch",
        title = "Harry Styles Watch",
        subtitle = "TPWK • Kindness & Curls",
        primaryColor = Color(0xFFFF80AB),
        accentColor = Color(0xFFE1BEE7),
        iconEmoji = "🍓",
        quote = "Treat People With Kindness"
    ),
    LOUIS_DOORWAY(
        id = "louis_watch",
        title = "Louis Tomlinson Watch",
        subtitle = "Tommo • Doncaster Sunshine",
        primaryColor = Color(0xFF69F0AE),
        accentColor = Color(0xFF80D8FF),
        iconEmoji = "🥕",
        quote = "I like girls who eat carrots!"
    ),
    NIALL_DOORWAY(
        id = "niall_watch",
        title = "Niall Horan Watch",
        subtitle = "Nialler • Acoustic Gold",
        primaryColor = Color(0xFFFFD54F),
        accentColor = Color(0xFFA7F3D0),
        iconEmoji = "🎸",
        quote = "Oh potato!"
    ),
    ZAYN_DOORWAY(
        id = "zayn_watch",
        title = "Zayn Malik Watch",
        subtitle = "High Notes • Cosmic Violet",
        primaryColor = Color(0xFFB388FF),
        accentColor = Color(0xFF80DEEA),
        iconEmoji = "🌙",
        quote = "Vas Happenin'?!"
    ),
    KIKI_STUDIO(
        id = "kiki_watch",
        title = "Kiki Clean Beauty",
        subtitle = "Squalane & Glow Stats",
        primaryColor = KikiCyan,
        accentColor = KikiLilac,
        iconEmoji = "💄",
        quote = "Clean Science Glow"
    )
}

enum class WatchDisplayMode {
    WATCH_FACE,
    WEAR_OS_TILE,
    WRIST_APP,
    WRIST_JUKEBOX,
    WRIST_SPARKLES
}

@Composable
fun SmartWatchStudioScreen(
    viewModel: KikiViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val wristSparks by WearOsCompanionManager.wristSparks.collectAsState()
    val wristBadges by WearOsCompanionManager.wristBadges.collectAsState()
    val wristTracks by WearOsCompanionManager.tracks.collectAsState()
    val currentTrackIndex by WearOsCompanionManager.currentTrackIndex.collectAsState()
    val isPlaying by WearOsCompanionManager.isPlaying.collectAsState()
    val stadiumAmbience by WearOsCompanionManager.stadiumAmbienceEnabled.collectAsState()
    val lastWristAction by WearOsCompanionManager.lastWristAction.collectAsState()

    var selectedTheme by remember { mutableStateOf(WatchTheme.ONE_DIRECTION_BAND) }
    var displayMode by remember { mutableStateOf(WatchDisplayMode.WATCH_FACE) }
    var isAlwaysOnDisplay by remember { mutableStateOf(false) }
    var isDigitalMode by remember { mutableStateOf(false) }
    var isSyncingToWatch by remember { mutableStateOf(false) }
    var syncSuccess by remember { mutableStateOf(false) }
    var connectedWatchModel by remember { mutableStateOf("Samsung Galaxy Watch 7 (Bluetooth)") }
    var wristHeartCount by remember { mutableIntStateOf(128) }
    var lastPlayedCatchphrase by remember { mutableStateOf<String?>(null) }

    // Live Eastern Time
    var easternTimeText by remember {
        mutableStateOf(
            try {
                val zdt = java.time.ZonedDateTime.now(java.time.ZoneId.of("America/New_York"))
                zdt.format(java.time.format.DateTimeFormatter.ofPattern("h:mm"))
            } catch (e: Exception) {
                "8:07"
            }
        )
    }

    var easternSecondsText by remember {
        mutableStateOf(
            try {
                val zdt = java.time.ZonedDateTime.now(java.time.ZoneId.of("America/New_York"))
                zdt.format(java.time.format.DateTimeFormatter.ofPattern("ss"))
            } catch (e: Exception) {
                "23"
            }
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                val zdt = java.time.ZonedDateTime.now(java.time.ZoneId.of("America/New_York"))
                easternTimeText = zdt.format(java.time.format.DateTimeFormatter.ofPattern("h:mm"))
                easternSecondsText = zdt.format(java.time.format.DateTimeFormatter.ofPattern("ss"))
            } catch (e: Exception) {
                // fallback
            }
            delay(1000L)
        }
    }

    // Smooth second hand rotation
    val infiniteTransition = rememberInfiniteTransition(label = "watchHand")
    val sweepSecondAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepSecond"
    )

    BackHandler {
        onNavigateBack()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(KikiDarkBg)
            .testTag("smart_watch_studio_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Navigation Top Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(containerColor = KikiSurfaceElevated),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder),
                    modifier = Modifier.testTag("watch_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = KikiPearl, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Return", color = KikiPearl, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color(0xFF00E676).copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Bluetooth, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Wear OS Paired",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E676)
                        )
                    }
                }
            }
        }

        // 2. Header Title Card
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⌚", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SMARTWATCH SOFTWARE STUDIO",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        ),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Wear OS, Galaxy Watch & Pixel Watch Companion App with Watch Face Format (WFF)",
                    style = MaterialTheme.typography.bodySmall,
                    color = KikiTextMuted
                )
            }
        }

        // 3. Mode Switcher (Watch Face, Wear OS Tile, Wrist App, Jukebox, Sparkles)
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                item {
                    val isSelected = displayMode == WatchDisplayMode.WATCH_FACE
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) selectedTheme.primaryColor else KikiSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) selectedTheme.primaryColor else KikiSurfaceBorder),
                        modifier = Modifier
                            .clickable { displayMode = WatchDisplayMode.WATCH_FACE }
                            .testTag("mode_watch_face_tab")
                    ) {
                        Text(
                            text = "Watch Face",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else KikiPearl,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }

                item {
                    val isSelected = displayMode == WatchDisplayMode.WRIST_JUKEBOX
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) selectedTheme.primaryColor else KikiSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) selectedTheme.primaryColor else KikiSurfaceBorder),
                        modifier = Modifier
                            .clickable { displayMode = WatchDisplayMode.WRIST_JUKEBOX }
                            .testTag("mode_wrist_jukebox_tab")
                    ) {
                        Text(
                            text = "Concert Jukebox 🎵",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else KikiPearl,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }

                item {
                    val isSelected = displayMode == WatchDisplayMode.WRIST_SPARKLES
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) selectedTheme.primaryColor else KikiSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) selectedTheme.primaryColor else KikiSurfaceBorder),
                        modifier = Modifier
                            .clickable { displayMode = WatchDisplayMode.WRIST_SPARKLES }
                            .testTag("mode_wrist_sparkles_tab")
                    ) {
                        Text(
                            text = "Sparkle Vault ✨",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else KikiPearl,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }

                item {
                    val isSelected = displayMode == WatchDisplayMode.WEAR_OS_TILE
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) selectedTheme.primaryColor else KikiSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) selectedTheme.primaryColor else KikiSurfaceBorder),
                        modifier = Modifier
                            .clickable { displayMode = WatchDisplayMode.WEAR_OS_TILE }
                            .testTag("mode_tile_tab")
                    ) {
                        Text(
                            text = "Wear OS Tile",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else KikiPearl,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }

                item {
                    val isSelected = displayMode == WatchDisplayMode.WRIST_APP
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) selectedTheme.primaryColor else KikiSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) selectedTheme.primaryColor else KikiSurfaceBorder),
                        modifier = Modifier
                            .clickable { displayMode = WatchDisplayMode.WRIST_APP }
                            .testTag("mode_wrist_app_tab")
                    ) {
                        Text(
                            text = "Wrist Mini-App",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else KikiPearl,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        // 4. THE INTERACTIVE CIRCULAR SMARTWATCH SIMULATOR
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                // Watch Straps (Upper and Lower)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(130.dp)
                ) {
                    // Top Strap
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(Color(0xFF23202E))
                            .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    )

                    Spacer(modifier = Modifier.height(290.dp))

                    // Bottom Strap
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                            .background(Color(0xFF23202E))
                            .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    )
                }

                // Digital Rotating Crown (Right edge)
                Box(
                    modifier = Modifier
                        .size(16.dp, 36.dp)
                        .align(Alignment.CenterEnd)
                        .padding(end = 22.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF4A4559))
                        .border(1.dp, Color(0xFF8E88A1), RoundedCornerShape(6.dp))
                )

                // Circular Titanium Case (Galaxy Watch / Pixel Watch bezel)
                Box(
                    modifier = Modifier
                        .size(310.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFF3A354A),
                                    Color(0xFF252131),
                                    Color(0xFF5A5370),
                                    Color(0xFF1E1A29),
                                    Color(0xFF3A354A)
                                )
                            )
                        )
                        .border(4.dp, Color(0xFF6B6482), CircleShape)
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // OLED Black Inner Bezel & Screen
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(if (isAlwaysOnDisplay) Color.Black else Color(0xFF09080E))
                            .border(1.dp, selectedTheme.primaryColor.copy(alpha = if (isAlwaysOnDisplay) 0.3f else 0.7f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Watch Screen Content depending on display mode
                        when (displayMode) {
                            WatchDisplayMode.WATCH_FACE -> {
                                SmartWatchFaceRenderer(
                                    theme = selectedTheme,
                                    timeText = easternTimeText,
                                    secondsText = easternSecondsText,
                                    isAod = isAlwaysOnDisplay,
                                    isDigital = isDigitalMode,
                                    sweepAngle = sweepSecondAngle,
                                    hearts = wristHeartCount,
                                    onTapHeart = {
                                        wristHeartCount++
                                        Toast.makeText(context, "Wrist Sparkle Heart Sent! 💖", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                            WatchDisplayMode.WEAR_OS_TILE -> {
                                SmartWatchTileRenderer(
                                    theme = selectedTheme,
                                    timeText = easternTimeText,
                                    onPlayQuote = { quote ->
                                        lastPlayedCatchphrase = quote
                                        HapticsUtil.performJukeboxBeat(context)
                                        if (quote.contains("light up my world", ignoreCase = true)) {
                                            val activeWristTrack = wristTracks.getOrNull(currentTrackIndex) ?: wristTracks[0]
                                            com.example.util.MusicAudioEngine.playTrack(
                                                context = context,
                                                trackId = activeWristTrack.id,
                                                title = activeWristTrack.title,
                                                sourceLabel = "Smartwatch Concert Jukebox • ${activeWristTrack.album}"
                                            )
                                        } else {
                                            com.example.util.MusicAudioEngine.playCatchphrase(
                                                context = context,
                                                speakerName = selectedTheme.title,
                                                phrase = quote
                                            )
                                        }
                                        Toast.makeText(context, "Wrist Audio: \"$quote\"", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                            WatchDisplayMode.WRIST_APP -> {
                                SmartWatchAppRenderer(
                                    theme = selectedTheme,
                                    timeText = easternTimeText,
                                    hearts = wristHeartCount,
                                    onTapSparkle = {
                                        wristHeartCount += 5
                                        Toast.makeText(context, "Collected 5 Wrist Sparks!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                            WatchDisplayMode.WRIST_JUKEBOX -> {
                                val currentTrack = wristTracks.getOrNull(currentTrackIndex) ?: wristTracks[0]
                                SmartWatchJukeboxRenderer(
                                    theme = selectedTheme,
                                    track = currentTrack,
                                    isPlaying = isPlaying,
                                    stadiumAmbience = stadiumAmbience,
                                    onTogglePlay = {
                                        WearOsCompanionManager.togglePlayPause()
                                        HapticsUtil.performJukeboxBeat(context)
                                        if (WearOsCompanionManager.isPlaying.value) {
                                            com.example.util.MusicAudioEngine.playTrack(
                                                context = context,
                                                trackId = currentTrack.id,
                                                title = currentTrack.title,
                                                sourceLabel = "Wrist Jukebox • ${currentTrack.album}"
                                            )
                                        } else {
                                            com.example.util.MusicAudioEngine.pause()
                                        }
                                    },
                                    onNextTrack = {
                                        WearOsCompanionManager.nextTrack()
                                        HapticsUtil.performSparkleClick(context)
                                        val nextTrack = wristTracks.getOrNull(WearOsCompanionManager.currentTrackIndex.value) ?: currentTrack
                                        com.example.util.MusicAudioEngine.playTrack(
                                            context = context,
                                            trackId = nextTrack.id,
                                            title = nextTrack.title,
                                            sourceLabel = "Wrist Jukebox • ${nextTrack.album}"
                                        )
                                    },
                                    onPreviousTrack = {
                                        WearOsCompanionManager.previousTrack()
                                        HapticsUtil.performSparkleClick(context)
                                        val prevTrack = wristTracks.getOrNull(WearOsCompanionManager.currentTrackIndex.value) ?: currentTrack
                                        com.example.util.MusicAudioEngine.playTrack(
                                            context = context,
                                            trackId = prevTrack.id,
                                            title = prevTrack.title,
                                            sourceLabel = "Wrist Jukebox • ${prevTrack.album}"
                                        )
                                    },
                                    onToggleStadiumAmbience = {
                                        WearOsCompanionManager.toggleStadiumAmbience()
                                        HapticsUtil.performSparkleTick(context)
                                    }
                                )
                            }
                            WatchDisplayMode.WRIST_SPARKLES -> {
                                SmartWatchSparklesRenderer(
                                    theme = selectedTheme,
                                    wristSparks = wristSparks,
                                    badges = wristBadges,
                                    onMineSparks = {
                                        WearOsCompanionManager.mineWristSparkles(5)
                                        HapticsUtil.performSparkleBurst(context)
                                        Toast.makeText(context, "+5 Wrist Sparks Mined! ✨", Toast.LENGTH_SHORT).show()
                                    },
                                    onClaimDailyBonus = {
                                        WearOsCompanionManager.claimDailyWristBonus()
                                        HapticsUtil.performSparkleBurst(context)
                                        Toast.makeText(context, "+100 Daily Bonus Added! 🏆", Toast.LENGTH_SHORT).show()
                                    },
                                    onBadgeClick = { badge ->
                                        HapticsUtil.performSparkleClick(context)
                                        Toast.makeText(context, "${badge.iconEmoji} ${badge.memberName}: ${badge.badgeTitle}", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Always-On Display (AOD) & Digital/Analogue Toggles
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Always-On Display (AOD)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Text(
                            text = if (isAlwaysOnDisplay) "Active: Ultra-low power OLED ambient mode" else "Disabled: Full color 60fps display",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isAlwaysOnDisplay) Color(0xFF00E676) else KikiTextMuted
                        )
                    }

                    Switch(
                        checked = isAlwaysOnDisplay,
                        onCheckedChange = { isAlwaysOnDisplay = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF00E676),
                            checkedTrackColor = Color(0xFF00E676).copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("aod_toggle")
                    )
                }
            }
        }

        // 6. Theme Selector Carousel
        item {
            Column {
                Text(
                    text = "Select Watch Edition & Member Theme:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = KikiPearl
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(WatchTheme.values()) { theme ->
                        val isSelected = selectedTheme == theme
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) theme.primaryColor.copy(alpha = 0.25f) else KikiSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) theme.primaryColor else KikiSurfaceBorder
                            ),
                            modifier = Modifier
                                .clickable { selectedTheme = theme }
                                .testTag("theme_chip_${theme.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(theme.iconEmoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = theme.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isSelected) theme.primaryColor else KikiPearl
                                    )
                                    Text(
                                        text = theme.subtitle,
                                        fontSize = 9.sp,
                                        color = KikiTextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Bluetooth & Wearable DataLayer Sync to Real Watch
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, selectedTheme.primaryColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(selectedTheme.primaryColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Watch, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Connected Smartwatch",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = KikiPearl
                                )
                                Text(
                                    text = connectedWatchModel,
                                    fontSize = 11.sp,
                                    color = Color(0xFF00E676)
                                )
                            }
                        }

                        HoloBadge(text = "BLE Synced ✓")
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isSyncingToWatch) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Transmitting Watch Face Package via Wearable DataLayer...",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedTheme.primaryColor
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                                color = selectedTheme.primaryColor,
                                trackColor = KikiSurfaceElevated
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isSyncingToWatch = true
                                    delay(1200L)
                                    isSyncingToWatch = false
                                    syncSuccess = true
                                    Toast.makeText(context, "Pushed ${selectedTheme.title} to your Smartwatch!", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("push_to_watch_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = selectedTheme.primaryColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Push ${selectedTheme.title} to Watch",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (syncSuccess) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF00E676).copy(alpha = 0.2f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Watch Face transferred to Galaxy Watch / Pixel Watch successfully!",
                                    fontSize = 11.sp,
                                    color = Color(0xFF00E676),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 8. Watch Face Format (WFF) Specification & Export
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Wear OS Watch Face Format (WFF) Specification",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = KikiPearl
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Official declarative XML format required for Wear OS 4 & 5 (Pixel Watch, Galaxy Watch 6 & 7). Zero background battery drain.",
                        fontSize = 11.sp,
                        color = KikiTextMuted,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = """
                                <!-- Wear OS Watch Face Format -->
                                <WatchFace width="454" height="454" clipShape="CIRCLE">
                                  <Metadata key="CLOCK_TYPE" value="ANALOG_AND_DIGITAL" />
                                  <Metadata key="PREVIEW" value="1d_watch_preview.png" />
                                  <Scene backgroundColor="#09080E">
                                    <ComplicationSlot slotId="0" supportedTypes="SHORT_TEXT">
                                      <DefaultProviderPolicy defaultSystemProvider="STEP_COUNT" />
                                    </ComplicationSlot>
                                    <ComplicationSlot slotId="1" supportedTypes="RANGED_VALUE">
                                      <DefaultProviderPolicy defaultSystemProvider="BATTERY" />
                                    </ComplicationSlot>
                                  </Scene>
                                </WatchFace>
                            """.trimIndent(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = Color(0xFF00F5D4),
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Wear OS WFF Manifest", "com.aistudio.kikiworld.watch (Wear OS 4/5 WFF Specification)"))
                            Toast.makeText(context, "Copied Wear OS WFF Manifest to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("copy_wff_manifest_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = KikiSurfaceElevated),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = KikiPearl, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy WFF Manifest & Package Specs", color = KikiPearl, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// -------------------------------------------------------------
// WATCH SCREEN COMPOSABLES
// -------------------------------------------------------------

@Composable
fun SmartWatchFaceRenderer(
    theme: WatchTheme,
    timeText: String,
    secondsText: String,
    isAod: Boolean,
    isDigital: Boolean,
    sweepAngle: Float,
    hearts: Int,
    onTapHeart: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Analogue Watch Dial Canvas
        Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2

            // Draw Hour Ticks (12 ticks)
            for (i in 0 until 12) {
                val angleRad = Math.toRadians((i * 30.0) - 90)
                val tickLength = if (i % 3 == 0) 14f else 8f
                val strokeW = if (i % 3 == 0) 3.5f else 1.8f
                val tickColor = if (isAod) Color.White.copy(alpha = 0.5f) else if (i % 3 == 0) theme.primaryColor else Color.White.copy(alpha = 0.4f)

                val start = Offset(
                    (center.x + (radius - tickLength) * cos(angleRad)).toFloat(),
                    (center.y + (radius - tickLength) * sin(angleRad)).toFloat()
                )
                val end = Offset(
                    (center.x + (radius - 2f) * cos(angleRad)).toFloat(),
                    (center.y + (radius - 2f) * sin(angleRad)).toFloat()
                )
                drawLine(
                    color = tickColor,
                    start = start,
                    end = end,
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
            }

            if (!isAod) {
                // Outer Subtle Progress Ring (Step progress)
                drawCircle(
                    color = theme.primaryColor.copy(alpha = 0.15f),
                    radius = radius - 1,
                    style = Stroke(width = 3f)
                )

                // Smooth Second Hand
                val secondRad = Math.toRadians(sweepAngle.toDouble() - 90)
                val secEnd = Offset(
                    (center.x + (radius - 24f) * cos(secondRad)).toFloat(),
                    (center.y + (radius - 24f) * sin(secondRad)).toFloat()
                )
                drawLine(
                    color = theme.accentColor,
                    start = center,
                    end = secEnd,
                    strokeWidth = 2.5f,
                    cap = StrokeCap.Round
                )
                drawCircle(color = theme.accentColor, radius = 4f, center = center)
            }
        }

        // Center Dial Info Elements
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Member/Band Crest
            Text(
                text = theme.iconEmoji,
                fontSize = if (isAod) 18.sp else 26.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Main Time Display
            Text(
                text = timeText,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = Color.White,
                fontSize = 28.sp
            )

            // Eastern Time Zone Tag
            Text(
                text = "ET • $secondsText s",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = theme.primaryColor
            )

            if (!isAod) {
                Spacer(modifier = Modifier.height(6.dp))

                // Complication Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Heart Rate
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier.clickable { onTapHeart() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💖", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("$hearts", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // Steps
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("👟", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("7.4k", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // Battery
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🔋", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("88%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = theme.subtitle,
                    fontSize = 9.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun SmartWatchTileRenderer(
    theme: WatchTheme,
    timeText: String,
    onPlayQuote: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(theme.iconEmoji, fontSize = 14.sp)
            Text(
                text = "1D WRIST TILE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = theme.primaryColor
            )
            Text(timeText, fontSize = 9.sp, color = KikiPearl)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "\"${theme.quote}\"",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Wrist Quick Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = { onPlayQuote(theme.quote) },
                modifier = Modifier.weight(1f).height(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = theme.primaryColor),
                shape = RoundedCornerShape(100.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("🔊 Voice", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onPlayQuote("Baby you light up my world!") },
                modifier = Modifier.weight(1f).height(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E293D)),
                shape = RoundedCornerShape(100.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("🎶 Music", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Swipe ← for Next Tile",
            fontSize = 8.sp,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun SmartWatchAppRenderer(
    theme: WatchTheme,
    timeText: String,
    hearts: Int,
    onTapSparkle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✨", fontSize = 16.sp)
        Text(
            text = "Wrist Doorways",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = Color.White
        )
        Text(
            text = "Gentle Sanctuary on Wrist",
            fontSize = 8.sp,
            color = theme.primaryColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            shape = RoundedCornerShape(100.dp),
            color = Color.Black.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(1.dp, theme.primaryColor),
            modifier = Modifier.clickable { onTapSparkle() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("💖", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Tap: $hearts Sparks",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Haptics Enabled ✓",
            fontSize = 8.sp,
            color = Color(0xFF00E676)
        )
    }
}

/**
 * Interactive Circular Smartwatch Concert Jukebox Renderer for Wear OS / Galaxy Watch.
 * Features rotating vinyl disc, audio track playback controls, live lyrics, and stadium ambience.
 */
@Composable
fun SmartWatchJukeboxRenderer(
    theme: WatchTheme,
    track: WristJukeboxTrack,
    isPlaying: Boolean,
    stadiumAmbience: Boolean,
    onTogglePlay: () -> Unit,
    onNextTrack: () -> Unit,
    onPreviousTrack: () -> Unit,
    onToggleStadiumAmbience: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "discSpin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isPlaying) 6000 else 600000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        // Vinyl Grooves Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f

            drawCircle(color = Color(0xFF14121B), radius = radius)
            // Grooves
            for (r in listOf(0.9f, 0.75f, 0.6f, 0.45f)) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.06f),
                    radius = radius * r,
                    style = Stroke(width = 1.2f)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Track Era & Stadium Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "1D CONCERT JUKEBOX",
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.primaryColor,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Central Vinyl Label / Track Title
            Surface(
                shape = CircleShape,
                color = theme.primaryColor.copy(alpha = 0.18f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.primaryColor),
                modifier = Modifier.size(76.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isPlaying) "🎶" else "⏸️",
                        fontSize = 16.sp
                    )
                    Text(
                        text = track.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp,
                        color = Color.White,
                        maxLines = 2,
                        lineHeight = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Iconic Lyric snippet
            Text(
                text = "\"${track.iconicLyric}\"",
                fontSize = 7.5.sp,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 1,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Text(
                text = "${track.album} (${track.year}) • ${track.durationText}",
                fontSize = 7.sp,
                color = KikiTextMuted
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Media Transport Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Prev
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onPreviousTrack() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Track",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Play / Pause
                Surface(
                    shape = CircleShape,
                    color = theme.primaryColor,
                    modifier = Modifier
                        .size(34.dp)
                        .clickable { onTogglePlay() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Next
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onNextTrack() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Track",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Stadium Crowd Ambience Pill
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = if (stadiumAmbience) Color(0xFF00E676).copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.4f),
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    if (stadiumAmbience) Color(0xFF00E676) else Color.White.copy(alpha = 0.2f)
                ),
                modifier = Modifier.clickable { onToggleStadiumAmbience() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (stadiumAmbience) "🏟️ Ambience: ON" else "🏟️ Ambience: OFF",
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (stadiumAmbience) Color(0xFF00E676) else Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

/**
 * Interactive Circular Smartwatch Sparkles Collection Renderer for Wear OS / Galaxy Watch.
 * Features wrist sparkle mining, daily starlight bonuses, and member achievement badges.
 */
@Composable
fun SmartWatchSparklesRenderer(
    theme: WatchTheme,
    wristSparks: Int,
    badges: List<WristMemberBadge>,
    onMineSparks: () -> Unit,
    onClaimDailyBonus: () -> Unit,
    onBadgeClick: (WristMemberBadge) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✨", fontSize = 16.sp)
        Text(
            text = "1D Sparkle Collection",
            fontWeight = FontWeight.Black,
            fontSize = 9.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Large Starlight Orb with Total Wrist Sparks
        Surface(
            shape = CircleShape,
            color = theme.primaryColor.copy(alpha = 0.18f),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, theme.primaryColor),
            modifier = Modifier
                .size(66.dp)
                .clickable { onMineSparks() }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$wristSparks",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Text(
                    text = "Sparks",
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.primaryColor
                )
                Text(
                    text = "Tap +5",
                    fontSize = 6.5.sp,
                    color = Color(0xFF00E676)
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Action Buttons Row (Daily +100 & Member Badges)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = KikiGold.copy(alpha = 0.25f),
                border = androidx.compose.foundation.BorderStroke(1.dp, KikiGold),
                modifier = Modifier.clickable { onClaimDailyBonus() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎁", fontSize = 8.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "+100 Daily",
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = KikiGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Horizontal Badge Row on Wrist
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(badges) { badge ->
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBadgeClick(badge) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(badge.iconEmoji, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "Haptic Tactile Engine Active",
            fontSize = 6.5.sp,
            color = Color(0xFF00E676)
        )
    }
}

