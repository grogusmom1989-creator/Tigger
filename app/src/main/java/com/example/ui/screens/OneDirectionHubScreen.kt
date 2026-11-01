package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.ui.platform.LocalContext
import com.example.data.room.SparkleItemEntity
import com.example.ui.components.MemberSparkleDonutChart
import com.example.ui.components.MemberSparkleSlice
import com.example.util.HapticsUtil
import com.example.util.ShareUtil
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CustomCreation
import com.example.data.FinishType
import com.example.data.ProductCategory
import com.example.ui.components.OneDirectionCrimson
import com.example.ui.components.OneDirectionRed
import com.example.ui.components.Windows11InstallerDialog
import com.example.ui.components.parseHexColor
import com.example.ui.theme.HolographicGradient
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

// Data Classes for Rich One Direction Content
data class OneDirectionMemberShade(
    val id: String,
    val memberName: String,
    val roleTitle: String,
    val avatarEmoji: String,
    val formulaName: String,
    val category: ProductCategory,
    val primaryHex: String,
    val secondaryHex: String,
    val finish: FinishType,
    val squalanePercentage: Int,
    val scentNote: String,
    val eraSong: String,
    val description: String
)

val defaultOneDirectionMembers = listOf(
    OneDirectionMemberShade(
        id = "1d_harry",
        memberName = "Harry Styles",
        roleTitle = "Watermelon Sugar • Golden Vocal",
        avatarEmoji = "👑",
        formulaName = "Styles Velvet Rose Glaze",
        category = ProductCategory.LIP_OIL,
        primaryHex = "#FF4071",
        secondaryHex = "#FEE440",
        finish = FinishType.GLASS_GLOSS,
        squalanePercentage = 35,
        scentNote = "Vanilla Silk & Fresh Guava",
        eraSong = "What Makes You Beautiful",
        description = "Juicy sheer rose balm with golden micro-pearls and 35% botanical squalane hydration."
    ),
    OneDirectionMemberShade(
        id = "1d_niall",
        memberName = "Niall Horan",
        roleTitle = "Irish Gold • Acoustic Soul",
        avatarEmoji = "🎸",
        formulaName = "Niall Irish Gold Solar Beam",
        category = ProductCategory.HIGHLIGHTER,
        primaryHex = "#FFD166",
        secondaryHex = "#00F5D4",
        finish = FinishType.PEARL_SHIMMER,
        squalanePercentage = 25,
        scentNote = "Japanese Yuzu & Green Tea",
        eraSong = "Live While We're Young",
        description = "Warm champagne solar stick infused with light-refracting emerald and cyan micro-shimmer."
    ),
    OneDirectionMemberShade(
        id = "1d_zayn",
        memberName = "Zayn Malik",
        roleTitle = "High Notes • Pillowtalk Velvet",
        avatarEmoji = "⚡",
        formulaName = "Zayn Midnight Plum Chrome",
        category = ProductCategory.NAIL_LACQUER,
        primaryHex = "#2E1A47",
        secondaryHex = "#B388FF",
        finish = FinishType.CHROME_METALLIC,
        squalanePercentage = 20,
        scentNote = "Celestial Musk & White Amber",
        eraSong = "Midnight Memories",
        description = "Solvent-free peelable lacquer in shifting obsidian violet and liquid chrome."
    ),
    OneDirectionMemberShade(
        id = "1d_liam",
        memberName = "Liam Payne",
        roleTitle = "Classic 1D • Harmonies Lead",
        avatarEmoji = "🎤",
        formulaName = "Liam Iconic 1D Red Holo",
        category = ProductCategory.LIP_OIL,
        primaryHex = "#E50914",
        secondaryHex = "#FF007F",
        finish = FinishType.HOLOGRAPHIC,
        squalanePercentage = 30,
        scentNote = "Cold Metallic Rose & Amber",
        eraSong = "Story of My Life",
        description = "Vibrant tribute crimson red lip oil with holographic glaze and moisture barrier."
    ),
    OneDirectionMemberShade(
        id = "1d_louis",
        memberName = "Louis Tomlinson",
        roleTitle = "Walls • Faith in the Future",
        avatarEmoji = "✨",
        formulaName = "Louis Sunlit Coral Tint",
        category = ProductCategory.CHEEK_TINT,
        primaryHex = "#FF9248",
        secondaryHex = "#FF729F",
        finish = FinishType.VELVET_MATTE,
        squalanePercentage = 20,
        scentNote = "Solar Peach & Wild Jasmine",
        eraSong = "Night Changes",
        description = "Weightless water-jelly flush for cheeks and lips with velvety soft-focus bloom."
    )
)

data class OneDirectionAlbumEra(
    val title: String,
    val year: String,
    val keyTrack: String,
    val colorHex: String,
    val iconEmoji: String
)

val oneDirectionAlbumEras = listOf(
    OneDirectionAlbumEra("Up All Night", "2011", "What Makes You Beautiful", "#FF007F", "💿"),
    OneDirectionAlbumEra("Take Me Home", "2012", "Live While We're Young", "#00F5D4", "🇬🇧"),
    OneDirectionAlbumEra("Midnight Memories", "2013", "Story of My Life", "#E50914", "🎸"),
    OneDirectionAlbumEra("FOUR", "2014", "Night Changes", "#FEE440", "🌟"),
    OneDirectionAlbumEra("Made in the A.M.", "2015", "History", "#B388FF", "☕")
)

data class OneDirectionTrack(
    val id: String,
    val title: String,
    val album: String,
    val year: String,
    val iconicLyric: String,
    val formulaTitle: String,
    val primaryHex: String,
    val secondaryHex: String,
    val finish: FinishType,
    val squalane: Int,
    val scent: String
)

val oneDirectionTracks = listOf(
    OneDirectionTrack(
        id = "track_wmyb",
        title = "What Makes You Beautiful",
        album = "Up All Night",
        year = "2011",
        iconicLyric = "Baby you light up my world like nobody else",
        formulaTitle = "Solar Glow Euphoria",
        primaryHex = "#FF007F",
        secondaryHex = "#FEE440",
        finish = FinishType.GLASS_GLOSS,
        squalane = 30,
        scent = "Golden Sand & Wild Strawberries"
    ),
    OneDirectionTrack(
        id = "track_night_changes",
        title = "Night Changes",
        album = "FOUR",
        year = "2014",
        iconicLyric = "We're only getting older baby, and I've been thinking about it lately",
        formulaTitle = "Starlight Twilight Tint",
        primaryHex = "#2E1A47",
        secondaryHex = "#FF729F",
        finish = FinishType.PEARL_SHIMMER,
        squalane = 25,
        scent = "Midnight Jasmine & Cedar"
    ),
    OneDirectionTrack(
        id = "track_story_of_my_life",
        title = "Story of My Life",
        album = "Midnight Memories",
        year = "2013",
        iconicLyric = "Story of my life, I take her home, I drive all night to keep her warm",
        formulaTitle = "Warm Amber Keepsake",
        primaryHex = "#FF9248",
        secondaryHex = "#E50914",
        finish = FinishType.VELVET_MATTE,
        squalane = 20,
        scent = "Smoked Amber & Honey"
    ),
    OneDirectionTrack(
        id = "track_best_song_ever",
        title = "Best Song Ever",
        album = "Midnight Memories",
        year = "2013",
        iconicLyric = "And we danced all night to the best song ever",
        formulaTitle = "Glam Rock Holo Lacquer",
        primaryHex = "#00F5D4",
        secondaryHex = "#FF007F",
        finish = FinishType.HOLOGRAPHIC,
        squalane = 15,
        scent = "Electric Pop Rock & Citrus"
    ),
    OneDirectionTrack(
        id = "track_drag_me_down",
        title = "Drag Me Down",
        album = "Made in the A.M.",
        year = "2015",
        iconicLyric = "With your love, nobody can drag me down",
        formulaTitle = "Cosmic Obsidian Shield",
        primaryHex = "#14121E",
        secondaryHex = "#00F5D4",
        finish = FinishType.CHROME_METALLIC,
        squalane = 35,
        scent = "NASA Clean Ozone & Mint"
    ),
    OneDirectionTrack(
        id = "track_steal_my_girl",
        title = "Steal My Girl",
        album = "FOUR",
        year = "2014",
        iconicLyric = "Everybody wanna steal my girl, everybody wanna take her heart away",
        formulaTitle = "Golden Hour Monarch",
        primaryHex = "#FFD166",
        secondaryHex = "#FF4071",
        finish = FinishType.PEARL_SHIMMER,
        squalane = 20,
        scent = "Desert Rose & Solar Citrus"
    ),
    OneDirectionTrack(
        id = "track_little_things",
        title = "Little Things",
        album = "Take Me Home",
        year = "2012",
        iconicLyric = "I'm in love with you and all these little things",
        formulaTitle = "British Tea & Honey Balm",
        primaryHex = "#F3E5AB",
        secondaryHex = "#FFB3BA",
        finish = FinishType.GLASS_GLOSS,
        squalane = 40,
        scent = "Earl Grey & Lavender Squalane"
    ),
    OneDirectionTrack(
        id = "track_history",
        title = "History",
        album = "Made in the A.M.",
        year = "2015",
        iconicLyric = "You and me got a whole lot of history, we could be the greatest team",
        formulaTitle = "Nostalgic Lavender Chrome",
        primaryHex = "#B388FF",
        secondaryHex = "#E50914",
        finish = FinishType.HOLOGRAPHIC,
        squalane = 25,
        scent = "Vanilla Orchid & Cotton"
    )
)

data class OneDirectionTour(
    val name: String,
    val years: String,
    val showsCount: Int,
    val grossingStat: String,
    val keyHighlight: String,
    val stadiumFormula: String,
    val colorHex: String
)

val oneDirectionTours = listOf(
    OneDirectionTour(
        name = "Up All Night Tour",
        years = "2011 - 2012",
        showsCount = 54,
        grossingStat = "Sold out globally in 12 minutes",
        keyHighlight = "Debut headline tour across UK, Oceania & North America",
        stadiumFormula = "Stage Sparkle Pink Lacquer",
        colorHex = "#FF007F"
    ),
    OneDirectionTour(
        name = "Take Me Home Tour",
        years = "2013",
        showsCount = 123,
        grossingStat = "$114+ Million Worldwide",
        keyHighlight = "Massive arena tour; filmed for 'This Is Us' movie",
        stadiumFormula = "London Telephone Red Gloss",
        colorHex = "#E50914"
    ),
    OneDirectionTour(
        name = "Where We Are Tour",
        years = "2014",
        showsCount = 69,
        grossingStat = "$290.2M (Highest-grossing tour of 2014)",
        keyHighlight = "3.4 million attendees across global mega-stadiums (San Siro & Wembley)",
        stadiumFormula = "Stadium Floodlight Gold Glaze",
        colorHex = "#FEE440"
    ),
    OneDirectionTour(
        name = "On the Road Again Tour",
        years = "2015",
        showsCount = 80,
        grossingStat = "$208+ Million",
        keyHighlight = "Final historic stadium tour playing across Asia, Africa & the Americas",
        stadiumFormula = "Midnight Celestial Indigo Chrome",
        colorHex = "#7B2CBF"
    )
)

data class OneDirectionTrivia(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

val oneDirectionTriviaQuestions = listOf(
    OneDirectionTrivia(
        question = "Which member came up with the name 'One Direction'?",
        options = listOf("Niall Horan", "Harry Styles", "Liam Payne", "Louis Tomlinson"),
        correctIndex = 1,
        explanation = "Harry Styles suggested 'One Direction' during the X Factor rehearsals, thinking it sounded great when read out!"
    ),
    OneDirectionTrivia(
        question = "What was One Direction's debut smash single released in September 2011?",
        options = listOf("Gotta Be You", "What Makes You Beautiful", "Live While We're Young", "One Thing"),
        correctIndex = 1,
        explanation = "'What Makes You Beautiful' debuted at #1 on the UK Singles Chart and became a multi-platinum global hit."
    ),
    OneDirectionTrivia(
        question = "Who hit the iconic soaring falsetto high note in 'Best Song Ever'?",
        options = listOf("Harry Styles", "Zayn Malik", "Liam Payne", "Niall Horan"),
        correctIndex = 1,
        explanation = "Zayn Malik delivered the legendary high note during the final chorus bridge of 'Best Song Ever'!"
    ),
    OneDirectionTrivia(
        question = "Which superstar singer-songwriter co-wrote 'Little Things'?",
        options = listOf("Ed Sheeran", "Bruno Mars", "Shawn Mendes", "Taylor Swift"),
        correctIndex = 0,
        explanation = "Ed Sheeran wrote 'Little Things' alongside Fiona Bevan and gave it to the band for 'Take Me Home'!"
    ),
    OneDirectionTrivia(
        question = "What stadium tour in 2014 became the highest-grossing tour of the entire year?",
        options = listOf("Up All Night Tour", "Take Me Home Tour", "Where We Are Tour", "On the Road Again Tour"),
        correctIndex = 2,
        explanation = "The 'Where We Are Tour' grossed $290.2 million across 69 massive sold-out stadium concerts!"
    )
)

@Composable
fun OneDirectionHubScreen(
    viewModel: KikiViewModel,
    modifier: Modifier = Modifier
) {
    val wallpaperActive by viewModel.oneDirectionWallpaperActive.collectAsState()
    val wallpaperDimming by viewModel.oneDirectionWallpaperDimming.collectAsState()
    val selectedPresetId by viewModel.selectedWallpaperPresetId.collectAsState()
    val customWallpaperUri by viewModel.customWallpaperUri.collectAsState()
    var showWindowsInstallerDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            viewModel.setCustomWallpaperUri(it.toString())
        }
    }

    val context = LocalContext.current
    val allSparkles by viewModel.allSparkles.collectAsState()
    val collectedSparkles by viewModel.collectedSparkles.collectAsState()
    var inspectingSparkleItem by remember { mutableStateOf<SparkleItemEntity?>(null) }
    var selectedMemberFilter by remember { mutableStateOf("All") }

    val memberSparkleSlices = remember(collectedSparkles) {
        val memberMap = listOf(
            Triple("Band", "Band Together", OneDirectionRed),
            Triple("Liam", "Liam Payne", Color(0xFF64B5F6)),
            Triple("Harry", "Harry Styles", Color(0xFFFF80AB)),
            Triple("Louis", "Louis Tomlinson", Color(0xFF69F0AE)),
            Triple("Niall", "Niall Horan", Color(0xFFFFD54F)),
            Triple("Zayn", "Zayn Malik", Color(0xFFB388FF))
        )

        memberMap.map { (key, name, color) ->
            val memberItems = collectedSparkles.filter { it.memberName == key }
            val emoji = when (key) {
                "Band" -> "👑"
                "Liam" -> "🎤"
                "Harry" -> "🌸"
                "Louis" -> "🥕"
                "Niall" -> "🎸"
                "Zayn" -> "🌙"
                else -> "✨"
            }
            MemberSparkleSlice(
                memberName = key,
                displayName = name,
                count = memberItems.size,
                points = memberItems.sumOf { it.sparkleValue },
                color = color,
                iconEmoji = emoji
            )
        }
    }

    // Jukebox State
    var currentTrackIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    val currentTrack = oneDirectionTracks[currentTrackIndex]

    // Trivia State
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var triviaScore by remember { mutableIntStateOf(0) }
    var isTriviaCompleted by remember { mutableStateOf(false) }

    // Photocard Maker State
    var selectedCardMember by remember { mutableStateOf(defaultOneDirectionMembers[0]) }
    var selectedCardSticker by remember { mutableStateOf("★ DIRECTIONER FOREVER ★") }
    var cardSavedMessage by remember { mutableStateOf<String?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "musicBars")
    val barHeight1 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(400), RepeatMode.Reverse), label = "b1"
    )
    val barHeight2 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(550), RepeatMode.Reverse), label = "b2"
    )
    val barHeight3 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse), label = "b3"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("one_direction_hub_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 0. Dedicated Pastel Sparkle Doorways Portal
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.5.dp,
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Color(0xFFFFD1DC), Color(0xFFE8D7FF), Color(0xFFD0F0FD))
                        ),
                        RoundedCornerShape(24.dp)
                    )
                    .clickable {
                        viewModel.selectTab(com.example.viewmodel.KikiNavTab.ONE_DIRECTION_DOORWAYS)
                    }
                    .testTag("doorways_portal_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A2B))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFFFFD1DC).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD1DC).copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "✨ PASTEL SPARKLE SANCTUARY ✨",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD1DC),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text("👑 6 Doorways", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE8D7FF))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "One Direction Member & Band Doorways",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Step through the magical arched doorways for the band together, Liam, Harry, Louis, Niall, and Zayn. Pure memories, gentle voices, and pastel sparkle tributes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.selectTab(com.example.viewmodel.KikiNavTab.ONE_DIRECTION_DOORWAYS)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("open_doorways_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1DC)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🚪 Open 1D Doorways Sanctuary",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 0B. Member Sparkle Breakdown Donut Chart Dashboard
        item {
            MemberSparkleDonutChart(
                slices = memberSparkleSlices,
                onSliceSelected = { slice ->
                    selectedMemberFilter = slice?.memberName ?: "All"
                }
            )
        }

        // 0C. Room-Cached 1D Sparkle Collection Vault (Offline Persistent Storage)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFFFD1DC).copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                    .testTag("sparkle_collection_vault_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A2B))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Title & Offline Room Persistence Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("✨", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "1D Sparkle Collection Vault",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00E676))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Offline Room Database Cache Active (SQLite)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E676)
                                )
                            }
                        }

                        // Social Share Milestone Button
                        IconButton(
                            onClick = {
                                HapticsUtil.performSparkleClick(context)
                                val topMember = memberSparkleSlices.maxByOrNull { it.count }?.displayName ?: "One Direction"
                                ShareUtil.shareCollectionMilestone(
                                    context = context,
                                    collectedCount = collectedSparkles.size,
                                    totalCount = allSparkles.size,
                                    totalPoints = collectedSparkles.sumOf { it.sparkleValue },
                                    topMember = topMember
                                )
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFFFD1DC).copy(alpha = 0.2f))
                                .testTag("share_milestone_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Collection Milestone",
                                tint = Color(0xFFFFD1DC),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Member Filter Chips
                    val filterOptions = listOf("All", "Band", "Liam", "Harry", "Louis", "Niall", "Zayn")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filterOptions) { filter ->
                            val isSelected = selectedMemberFilter == filter
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFFFFD1DC) else Color(0xFF282338),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) Color(0xFFFFD1DC) else Color(0x33FFFFFF)
                                ),
                                modifier = Modifier
                                    .clickable {
                                        selectedMemberFilter = filter
                                        HapticsUtil.performSparkleTick(context)
                                    }
                                    .testTag("filter_chip_$filter")
                            ) {
                                Text(
                                    text = filter,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.Black else Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Filtered Sparkle Items
                    val displayedSparkles = remember(allSparkles, selectedMemberFilter) {
                        if (selectedMemberFilter == "All") {
                            allSparkles
                        } else {
                            allSparkles.filter { it.memberName == selectedMemberFilter }
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        displayedSparkles.forEach { item ->
                            val isSelectedForInspect = inspectingSparkleItem?.id == item.id
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelectedForInspect) Color(0xFF332A4A) else if (item.isCollected) Color(0xFF231E33) else Color(0xFF191624),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelectedForInspect) 1.5.dp else 1.dp,
                                    if (isSelectedForInspect) Color(0xFFFFD1DC) else if (item.isCollected) Color(0x44FFFFFF) else Color(0x22FFFFFF)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        HapticsUtil.performSparkleClick(context)
                                        inspectingSparkleItem = if (isSelectedForInspect) null else item
                                    }
                                    .testTag("sparkle_item_${item.id}")
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
                                        // Emoji Icon Badge
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    try {
                                                        Color(android.graphics.Color.parseColor(item.colorHex)).copy(alpha = 0.25f)
                                                    } catch (e: Exception) {
                                                        Color(0xFF2E293D)
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(item.iconEmoji, fontSize = 20.sp)
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = item.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = Color(0xFF2D2740)
                                                ) {
                                                    Text(
                                                        text = item.memberName,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFFFD1DC),
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = item.rarity,
                                                    fontSize = 10.sp,
                                                    color = KikiGold
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "• ${item.sparkleValue} Sparks",
                                                    fontSize = 10.sp,
                                                    color = Color.White.copy(alpha = 0.6f)
                                                )
                                            }
                                        }
                                    }

                                    // Action / Collected Indicator
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                HapticsUtil.performSparkleTick(context)
                                                ShareUtil.shareSparkleItem(context, item)
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Share",
                                                tint = Color.White.copy(alpha = 0.7f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                HapticsUtil.performSparkleBurst(context)
                                                viewModel.toggleSparkleCollected(item.id, item.isCollected)
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (item.isCollected) Icons.Default.CheckCircle else Icons.Default.Lock,
                                                contentDescription = if (item.isCollected) "Collected" else "Locked",
                                                tint = if (item.isCollected) Color(0xFF00E676) else Color.White.copy(alpha = 0.4f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Shared Element / Animated Detail Pane
                    AnimatedVisibility(
                        visible = inspectingSparkleItem != null,
                        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
                        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
                    ) {
                        inspectingSparkleItem?.let { item ->
                            Spacer(modifier = Modifier.height(14.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.5.dp, Color(0xFFFFD1DC), RoundedCornerShape(20.dp))
                                    .testTag("inspect_sparkle_card"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF28223B))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(item.iconEmoji, fontSize = 26.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = item.title,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 15.sp,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "${item.memberName} • ${item.era}",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFFFFD1DC)
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = { inspectingSparkleItem = null },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Close",
                                                tint = Color.White.copy(alpha = 0.7f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "\"${item.description}\"",
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                HapticsUtil.performSparkleClick(context)
                                                ShareUtil.shareSparkleItem(context, item)
                                            },
                                            modifier = Modifier.weight(1f).height(38.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1DC)),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Share Item", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = {
                                                HapticsUtil.performSparkleBurst(context)
                                                viewModel.toggleSparkleCollected(item.id, item.isCollected)
                                                inspectingSparkleItem = inspectingSparkleItem?.copy(isCollected = !item.isCollected)
                                            },
                                            modifier = Modifier.weight(1f).height(38.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (item.isCollected) Color(0xFF00E676) else Color(0xFF4A4260)
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = if (item.isCollected) "✓ Collected (+${item.sparkleValue})" else "+ Add to Vault",
                                                color = if (item.isCollected) Color.Black else Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1. One Direction Hero Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, OneDirectionRed, RoundedCornerShape(24.dp))
                    .testTag("one_direction_hero_banner"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    OneDirectionRed.copy(alpha = 0.28f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(OneDirectionRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "1D",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 26.sp,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "ONE DIRECTION",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.2.sp
                                        ),
                                        color = KikiPearl
                                    )
                                    Text(
                                        text = "Official 1D Tribute Hub • 5 Album Eras",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = KikiCyan
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = OneDirectionRed.copy(alpha = 0.25f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, OneDirectionRed)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("❤️", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "1D ERA",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Celebrate Harry, Niall, Zayn, Liam, and Louis! Enjoy the interactive 1D Jukebox, album shade synthesizers, Directioner trivia, and stadium tour archives.",
                            style = MaterialTheme.typography.bodySmall,
                            color = KikiTextMuted,
                            lineHeight = 19.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Wallpaper Controls
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = KikiSurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Wallpaper, contentDescription = null, tint = OneDirectionRed, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "1D Band Wallpaper",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = KikiPearl
                                            )
                                            Text(
                                                text = if (wallpaperActive) "Visible behind all screens" else "Cosmic dark background active",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = KikiTextMuted
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = wallpaperActive,
                                        onCheckedChange = { viewModel.toggleOneDirectionWallpaper() },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = OneDirectionRed
                                        ),
                                        modifier = Modifier.testTag("toggle_1d_wallpaper_switch")
                                    )
                                }

                                if (wallpaperActive) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Wallpaper Contrast / Dimming", style = MaterialTheme.typography.labelSmall, color = KikiTextMuted)
                                        Text("${(wallpaperDimming * 100).toInt()}%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = KikiCyan)
                                    }
                                    Slider(
                                        value = wallpaperDimming,
                                        onValueChange = { viewModel.setOneDirectionWallpaperDimming(it) },
                                        valueRange = 0.1f..0.7f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = OneDirectionRed,
                                            activeTrackColor = OneDirectionRed,
                                            inactiveTrackColor = KikiDarkBg
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "1D Photo Wallpaper Presets",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = KikiPearl
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(com.example.viewmodel.default1DWallpaperPresets) { preset ->
                                            val isSelected = selectedPresetId == preset.id && customWallpaperUri == null
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (isSelected) OneDirectionRed.copy(alpha = 0.25f) else KikiDarkBg,
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    if (isSelected) OneDirectionRed else KikiSurfaceBorder
                                                ),
                                                modifier = Modifier
                                                    .width(140.dp)
                                                    .clickable { viewModel.selectWallpaperPreset(preset.id) }
                                            ) {
                                                Column(modifier = Modifier.padding(8.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = preset.era,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = KikiGold
                                                        )
                                                        if (isSelected) {
                                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OneDirectionRed, modifier = Modifier.size(12.dp))
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = preset.name,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = KikiPearl,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                photoPickerLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp)
                                                .testTag("pick_1d_wallpaper_photo_btn"),
                                            colors = ButtonDefaults.buttonColors(containerColor = OneDirectionRed),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Pick 1D Photo from Device", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }

                                        if (customWallpaperUri != null) {
                                            OutlinedButton(
                                                onClick = { viewModel.clearCustomWallpaper() },
                                                modifier = Modifier.height(38.dp),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(Icons.Default.RestartAlt, contentDescription = null, tint = KikiPearl, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Reset", color = KikiPearl, fontSize = 11.sp)
                                            }
                                        }
                                    }

                                    if (customWallpaperUri != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "✨ Custom 1D Photo Wallpaper Active from Device Gallery",
                                            fontSize = 10.sp,
                                            color = Color(0xFF00E676),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Interactive 1D Hit Jukebox & Lyric Blender
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, parseHexColor(currentTrack.primaryHex), RoundedCornerShape(22.dp))
                    .testTag("one_direction_jukebox"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(OneDirectionRed.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Headphones, contentDescription = null, tint = OneDirectionRed, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "1D Jukebox & Lyric Blender",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KikiPearl
                                )
                                Text(
                                    text = "Track ${currentTrackIndex + 1} of ${oneDirectionTracks.size} • ${currentTrack.album} (${currentTrack.year})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = KikiCyan
                                )
                            }
                        }

                        // Equalizer Visualizer Bars
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(20.dp)
                        ) {
                            Box(modifier = Modifier.width(4.dp).height((20 * if (isPlaying) barHeight1 else 0.3f).dp).background(OneDirectionRed, RoundedCornerShape(2.dp)))
                            Box(modifier = Modifier.width(4.dp).height((20 * if (isPlaying) barHeight2 else 0.5f).dp).background(KikiCyan, RoundedCornerShape(2.dp)))
                            Box(modifier = Modifier.width(4.dp).height((20 * if (isPlaying) barHeight3 else 0.2f).dp).background(KikiGold, RoundedCornerShape(2.dp)))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Current Song Banner
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = KikiDarkBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, parseHexColor(currentTrack.primaryHex).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "♫ ${currentTrack.title}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = KikiPearl
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"${currentTrack.iconicLyric}\"",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = KikiGold,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Jukebox Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else oneDirectionTracks.size - 1
                            }
                        ) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Track", tint = KikiPearl)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(OneDirectionRed)
                                .clickable { isPlaying = !isPlaying },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        IconButton(
                            onClick = {
                                currentTrackIndex = (currentTrackIndex + 1) % oneDirectionTracks.size
                            }
                        ) {
                            Icon(Icons.Default.SkipNext, contentDescription = "Next Track", tint = KikiPearl)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Blend from Song Button
                    Button(
                        onClick = {
                            viewModel.loadOneDirectionFormula(
                                name = currentTrack.formulaTitle,
                                primaryHex = currentTrack.primaryHex,
                                secondaryHex = currentTrack.secondaryHex,
                                finish = currentTrack.finish,
                                squalane = currentTrack.squalane,
                                scent = currentTrack.scent,
                                notes = "Inspired by 1D song '${currentTrack.title}' (${currentTrack.album}): \"${currentTrack.iconicLyric}\""
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("blend_song_formula_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = parseHexColor(currentTrack.primaryHex)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Blend '${currentTrack.formulaTitle}' in Lab",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 2b. Iconic Catchphrases Soundboard
        item {
            com.example.ui.components.OneDirectionSoundboard(
                onQuotePlayed = { quote ->
                    viewModel.awardSparks(10, "Heard ${quote.member}: \"${quote.quote}\"")
                }
            )
        }

        // 3. Directioner Trivia Quiz
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KikiGold, RoundedCornerShape(22.dp))
                    .testTag("directioner_trivia_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏆", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Directioner Trivia Challenge",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KikiPearl
                                )
                                Text(
                                    text = "Question ${currentQuestionIndex + 1} of ${oneDirectionTriviaQuestions.size} • Earn 100 Sparks",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = KikiGold
                                )
                            }
                        }

                        Surface(shape = RoundedCornerShape(8.dp), color = KikiSurfaceElevated) {
                            Text(
                                text = "Score: $triviaScore",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = KikiGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val activeQ = oneDirectionTriviaQuestions[currentQuestionIndex]

                    Text(
                        text = activeQ.question,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = KikiPearl
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Options
                    activeQ.options.forEachIndexed { optIdx, optText ->
                        val isSelected = selectedAnswerIndex == optIdx
                        val isCorrect = optIdx == activeQ.correctIndex
                        val btnColor by animateColorAsState(
                            targetValue = when {
                                selectedAnswerIndex == null -> KikiDarkBg
                                isSelected && isCorrect -> Color(0xFF00E676)
                                isSelected && !isCorrect -> Color(0xFFFF5252)
                                optIdx == activeQ.correctIndex -> Color(0xFF00E676).copy(alpha = 0.4f)
                                else -> KikiDarkBg
                            }, label = "btnColor"
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = btnColor,
                            border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(enabled = selectedAnswerIndex == null) {
                                    selectedAnswerIndex = optIdx
                                    if (optIdx == activeQ.correctIndex) {
                                        triviaScore += 20
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${('A' + optIdx)}. $optText",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else KikiPearl
                                )
                            }
                        }
                    }

                    // Explanation & Next Button
                    if (selectedAnswerIndex != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = activeQ.explanation,
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiCyan
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (currentQuestionIndex < oneDirectionTriviaQuestions.size - 1) {
                                    currentQuestionIndex++
                                    selectedAnswerIndex = null
                                } else {
                                    isTriviaCompleted = true
                                    viewModel.awardSparks(100, "1D Trivia Master Quiz")
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OneDirectionRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (currentQuestionIndex < oneDirectionTriviaQuestions.size - 1) "Next Question →" else "Claim 100 Sparks Reward 🎉",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isTriviaCompleted) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF00E676).copy(alpha = 0.2f), modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "🎉 Congratulations! You achieved $triviaScore/100 and earned 100 Sparks!",
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF00E676)
                            )
                        }
                    }
                }
            }
        }

        // 4. Band Members Signature Formulas
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Band Member Tribute Formulations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = KikiPearl
                )
                Text(
                    text = "5 Signature Blends",
                    style = MaterialTheme.typography.labelSmall,
                    color = OneDirectionRed
                )
            }
        }

        items(defaultOneDirectionMembers) { member ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(18.dp))
                    .testTag("member_card_${member.id}"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(member.avatarEmoji, fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = member.memberName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KikiPearl
                                )
                                Text(
                                    text = member.roleTitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OneDirectionRed
                                )
                            }
                        }

                        // Shade Preview Swatch
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(member.primaryHex))
                                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(member.secondaryHex))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = member.formulaName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = KikiCyan
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = member.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = KikiTextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = KikiSurfaceElevated,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Finish: ${member.finish.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiPearl,
                                modifier = Modifier.padding(6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = KikiSurfaceElevated,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Squalane: ${member.squalanePercentage}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiLilac,
                                modifier = Modifier.padding(6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.loadOneDirectionFormula(
                                name = member.formulaName,
                                primaryHex = member.primaryHex,
                                secondaryHex = member.secondaryHex,
                                finish = member.finish,
                                squalane = member.squalanePercentage,
                                scent = member.scentNote,
                                notes = "Inspired by ${member.memberName} (${member.eraSong})"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("blend_${member.id}_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OneDirectionRed)
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Blend ${member.memberName.substringBefore(" ")}'s Shade in Lab",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 5. Directioner Photocard Creator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OneDirectionRed, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📸", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "1D Directioner Photocard Studio",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                            Text(
                                text = "Design and save custom 1D member photocards to your Vault",
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Member Selection Row
                    Text("Select Band Member:", style = MaterialTheme.typography.labelSmall, color = KikiTextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(defaultOneDirectionMembers) { m ->
                            val isSel = selectedCardMember.id == m.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) OneDirectionRed else KikiSurfaceElevated,
                                modifier = Modifier.clickable { selectedCardMember = m }
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(m.avatarEmoji, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(m.memberName.substringBefore(" "), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Card Preview Frame
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        parseHexColor(selectedCardMember.primaryHex).copy(alpha = 0.4f),
                                        Color(0xFF0F0E17)
                                    )
                                )
                            )
                            .border(2.dp, OneDirectionRed, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(selectedCardMember.avatarEmoji, fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = selectedCardMember.memberName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )
                            Text(
                                text = selectedCardMember.roleTitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiGold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = OneDirectionRed
                            ) {
                                Text(
                                    text = selectedCardSticker,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val creation = CustomCreation(
                                id = "1d_card_${System.currentTimeMillis()}",
                                name = "1D Photocard: ${selectedCardMember.memberName}",
                                category = selectedCardMember.category,
                                primaryHex = selectedCardMember.primaryHex,
                                secondaryHex = selectedCardMember.secondaryHex,
                                finish = selectedCardMember.finish,
                                scentNote = selectedCardMember.scentNote,
                                bottleShape = "1D Stencil Card",
                                shimmerIntensity = 0.85f,
                                squalanePercentage = selectedCardMember.squalanePercentage,
                                notes = "Collector photocard dedicated to ${selectedCardMember.memberName} (${selectedCardMember.eraSong}). Sticker: '$selectedCardSticker'",
                                createdAtMillis = System.currentTimeMillis()
                            )
                            viewModel.saveCustomCreation(creation)
                            cardSavedMessage = "Saved ${selectedCardMember.memberName}'s Photocard to your Vault!"
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OneDirectionRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Directioner Photocard to Vault", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    cardSavedMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "✨ $msg", style = MaterialTheme.typography.labelSmall, color = Color(0xFF00E676))
                    }
                }
            }
        }

        // 6. 1D World Tour Archive
        item {
            Text(
                text = "1D Stadium World Tour Archive",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = KikiPearl
            )
        }

        items(oneDirectionTours) { tour ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, parseHexColor(tour.colorHex).copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Stadium, contentDescription = null, tint = parseHexColor(tour.colorHex), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(tour.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = KikiPearl)
                        }
                        Text(tour.years, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = KikiGold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(tour.keyHighlight, style = MaterialTheme.typography.bodySmall, color = KikiTextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• ${tour.showsCount} Shows • ${tour.grossingStat}", style = MaterialTheme.typography.labelSmall, color = KikiCyan)

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.loadOneDirectionFormula(
                                name = tour.stadiumFormula,
                                primaryHex = tour.colorHex,
                                secondaryHex = "#FFFFFF",
                                finish = FinishType.HOLOGRAPHIC,
                                squalane = 25,
                                scent = "Stadium Concert Atmosphere & Rose",
                                notes = "Commemorative stadium blend from the ${tour.name} (${tour.years})"
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Blend '${tour.stadiumFormula}' in Lab", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = parseHexColor(tour.colorHex))
                    }
                }
            }
        }

        // 7. Album Eras Row
        item {
            Text(
                text = "One Direction Album Eras",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = KikiPearl
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(oneDirectionAlbumEras) { era ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = KikiSurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, parseHexColor(era.colorHex)),
                        modifier = Modifier.width(180.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(era.iconEmoji, fontSize = 20.sp)
                                Text(era.year, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = KikiGold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(era.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = KikiPearl)
                            Text(era.keyTrack, style = MaterialTheme.typography.labelSmall, color = KikiTextMuted, maxLines = 1)
                        }
                    }
                }
            }
        }

        // 8. Windows 11 PC Installer Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF0078D4), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0078D4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🪟", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Install on Windows 11 PC (Home & Pro)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                            Text(
                                text = "Run full-screen on your PC monitor with keyboard & mouse",
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showWindowsInstallerDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("open_windows_11_installer_from_1d_hub"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0078D4)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.DesktopWindows, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Windows 11 PC Installation Center", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // 9. Samsung Galaxy Store Submission Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF7B2CBF), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF7B2CBF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌌", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Samsung Galaxy Store Submission",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                            Text(
                                text = "Submit 1D World for Galaxy S24, Z Fold & Flip foldables",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFB388FF)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.selectTab(com.example.viewmodel.KikiNavTab.MULTI_STORE) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("open_samsung_submission_from_1d_hub"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B2CBF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Samsung Galaxy Store Submission Portal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showWindowsInstallerDialog) {
        Windows11InstallerDialog(
            onDismiss = { showWindowsInstallerDialog = false },
            onSwitchToWindowsDesktopMode = {
                showWindowsInstallerDialog = false
                viewModel.setLayoutMode(com.example.viewmodel.AppLayoutMode.WINDOWS_DESKTOP)
            }
        )
    }
}
