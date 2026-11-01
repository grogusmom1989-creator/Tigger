package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiRose
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted
import com.example.util.HapticsUtil
import com.example.util.SynthAudioEngine
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

data class HolographicBadgeReward(
    val id: String,
    val title: String,
    val subtitle: String,
    val badgeCode: String,
    val primaryHex: String,
    val secondaryHex: String,
    val soundEffectLabel: String = "Crystal Chime",
    val sparksAwarded: Int = 150
)

data class CosmeticSoundEffect(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val frequencyProfile: String,
    val description: String,
    val accentHex: String,
    val isPreInstalled: Boolean = false
)

val defaultCosmeticSoundEffects = listOf(
    CosmeticSoundEffect(
        id = "sfx_chime",
        name = "Crystal Chime",
        iconEmoji = "🎐",
        frequencyProfile = "1046Hz • Harmonic C6 Bell",
        description = "Prismatic glass wind-chime resonance when opening or swatching a holographic glaze.",
        accentHex = "#5CE5E7",
        isPreInstalled = true
    ),
    CosmeticSoundEffect(
        id = "sfx_fizz",
        name = "Sparkle Fizz",
        iconEmoji = "🫧",
        frequencyProfile = "1800Hz • Effervescent Air",
        description = "Crisp botanical squalane micro-bubble fizz inspired by Japanese Yuzu soda.",
        accentHex = "#FFD166",
        isPreInstalled = true
    ),
    CosmeticSoundEffect(
        id = "sfx_pop",
        name = "Velvet Pop",
        iconEmoji = "💄",
        frequencyProfile = "220Hz–1.2kHz • Gloss Wand Pop",
        description = "Satisfying vacuum-seal lip oil applicator pop with warm sub-bass bloom.",
        accentHex = "#FF729F",
        isPreInstalled = false
    ),
    CosmeticSoundEffect(
        id = "sfx_harp",
        name = "Cosmic Harp",
        iconEmoji = "✨",
        frequencyProfile = "523Hz–1568Hz • Starlight Gliss",
        description = "Ascending 6-note celestial harp arpeggio for high-shimmer highlighter sticks.",
        accentHex = "#D67BFF",
        isPreInstalled = false
    ),
    CosmeticSoundEffect(
        id = "sfx_prism",
        name = "Prism Drop",
        iconEmoji = "💧",
        frequencyProfile = "880Hz • Liquid Chrome Mod",
        description = "Iridescent liquid-metal droplet ripple with duochrome frequency modulation.",
        accentHex = "#00F5D4",
        isPreInstalled = false
    )
)

data class StarConstellationNode(
    val id: String,
    val name: String,
    val latinTitle: String,
    val starDustReward: Int,
    val spaceFact: String,
    val bioscienceLink: String,
    val unlockedIngredient: String,
    val colorHex: String,
    val normalizedPoints: List<Pair<Float, Float>>
)

val defaultBioscienceConstellations = listOf(
    StarConstellationNode(
        id = "const_cassiopeia",
        name = "Cassiopeia",
        latinTitle = "The Queen's W-Cluster",
        starDustReward = 75,
        spaceFact = "Located 550 light-years away, Cassiopeia's iconic 'W' asterism contains some of the most luminous supergiant stars in the Milky Way.",
        bioscienceLink = "Inspires our 5-point Molecular Squalane Lattice that locks hydration across the skin barrier.",
        unlockedIngredient = "Cryo-Distilled Starlight Squalane",
        colorHex = "#FF729F",
        normalizedPoints = listOf(0.12f to 0.25f, 0.20f to 0.38f, 0.28f to 0.28f, 0.36f to 0.40f, 0.44f to 0.22f)
    ),
    StarConstellationNode(
        id = "const_lyra",
        name = "Lyra & Vega",
        latinTitle = "The Celestial Harp",
        starDustReward = 90,
        spaceFact = "Vega in Lyra was the northern pole star around 12,000 BCE and spins so rapidly that its equator bulges outward by 23%.",
        bioscienceLink = "Models acoustic high-frequency micro-emulsion dispersion for weightless glass-gloss polymers.",
        unlockedIngredient = "Vega Prismatic Silica Pearls",
        colorHex = "#5CE5E7",
        normalizedPoints = listOf(0.62f to 0.20f, 0.72f to 0.28f, 0.82f to 0.25f, 0.78f to 0.42f, 0.66f to 0.38f)
    ),
    StarConstellationNode(
        id = "const_pleiades",
        name = "Pleiades (M45)",
        latinTitle = "The Seven Sisters Nebula",
        starDustReward = 100,
        spaceFact = "The Pleiades reflection nebula glows electric blue because interstellar dust scatters blue wavelengths from hot B-type stars.",
        bioscienceLink = "Rayleigh light-scattering principles power our non-nano Blue-Shift Duochrome Mica pigments.",
        unlockedIngredient = "Nebula Blue-Shift Bio-Mica",
        colorHex = "#D67BFF",
        normalizedPoints = listOf(0.18f to 0.65f, 0.25f to 0.60f, 0.32f to 0.64f, 0.28f to 0.76f, 0.20f to 0.78f)
    ),
    StarConstellationNode(
        id = "const_cygnus",
        name = "Cygnus X-1",
        latinTitle = "The Northern Cross Swan",
        starDustReward = 85,
        spaceFact = "Deneb, the tail star of Cygnus, is roughly 200,000 times brighter than our Sun and marks the top of the Summer Triangle.",
        bioscienceLink = "High-energy photon protection inspires our Extremophile Astaxanthin & Ectoin UV-shield complex.",
        unlockedIngredient = "Solar-Shield Extremophile Ectoin",
        colorHex = "#FFD166",
        normalizedPoints = listOf(0.55f to 0.62f, 0.68f to 0.70f, 0.80f to 0.78f, 0.74f to 0.56f, 0.60f to 0.84f)
    )
)

/**
 * 1. Holographic Badge Overlay Animation triggered when completing a Daily Challenge
 * or discovering/bookmarking a new Lab Recipe.
 */
@Composable
fun HolographicBadgeOverlay(
    badge: HolographicBadgeReward?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = badge != null,
        enter = fadeIn(tween(280)) + scaleIn(initialScale = 0.82f, animationSpec = tween(320)),
        exit = fadeOut(tween(220)) + scaleOut(targetScale = 0.88f, animationSpec = tween(220)),
        modifier = modifier
    ) {
        badge?.let { reward ->
            val infiniteTransition = rememberInfiniteTransition(label = "holoOverlay")
            val rotationDeg by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
                label = "holoRot"
            )
            val shimmerShift by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
                label = "holoShift"
            )

            val primaryCol = parseHexColor(reward.primaryHex)
            val secondaryCol = parseHexColor(reward.secondaryHex)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.84f))
                    .clickable { onDismiss() }
                    .testTag("holographic_badge_overlay"),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .border(
                            2.dp,
                            Brush.linearGradient(
                                colors = listOf(KikiCyan, KikiLilac, KikiGold, primaryCol)
                            ),
                            RoundedCornerShape(28.dp)
                        )
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF171324))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50.dp),
                                color = KikiCyan.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, KikiCyan)
                            ) {
                                Text(
                                    text = "✨ HOLOGRAPHIC BADGE UNLOCKED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = KikiCyan,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("dismiss_holo_badge_btn")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = KikiPearl)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Animated Rotating Holographic Prismatic Crest
                        Box(
                            modifier = Modifier.size(168.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val cx = size.width / 2f
                                val cy = size.height / 2f
                                val outerR = size.minDimension * 0.46f

                                rotate(rotationDeg) {
                                    // Prismatic starburst rays
                                    for (i in 0 until 12) {
                                        val angle = Math.toRadians((i * 30).toDouble())
                                        val xEnd = cx + (outerR * 1.08f * cos(angle)).toFloat()
                                        val yEnd = cy + (outerR * 1.08f * sin(angle)).toFloat()
                                        drawLine(
                                            color = if (i % 2 == 0) KikiCyan.copy(alpha = 0.6f * shimmerShift)
                                            else KikiLilac.copy(alpha = 0.6f * shimmerShift),
                                            start = Offset(cx, cy),
                                            end = Offset(xEnd, yEnd),
                                            strokeWidth = 4f
                                        )
                                    }

                                    // Octagonal foil seal ring
                                    val path = Path()
                                    for (i in 0..8) {
                                        val a = Math.toRadians((i * 45).toDouble())
                                        val px = cx + (outerR * cos(a)).toFloat()
                                        val py = cy + (outerR * sin(a)).toFloat()
                                        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                                    }
                                    drawPath(
                                        path = path,
                                        brush = Brush.sweepGradient(
                                            listOf(primaryCol, KikiCyan, KikiGold, secondaryCol, KikiLilac, primaryCol)
                                        ),
                                        style = Stroke(width = 8f)
                                    )
                                }

                                // Inner holographic core
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            primaryCol.copy(alpha = 0.9f),
                                            secondaryCol.copy(alpha = 0.75f),
                                            Color(0xFF1E1930)
                                        ),
                                        center = Offset(cx, cy),
                                        radius = outerR * 0.78f
                                    ),
                                    radius = outerR * 0.78f,
                                    center = Offset(cx, cy)
                                )

                                // Foil specular highlight ring
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.45f * shimmerShift),
                                    radius = outerR * 0.62f,
                                    center = Offset(cx, cy),
                                    style = Stroke(width = 2.5f)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🧪", fontSize = 34.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = reward.badgeCode,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = reward.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = KikiPearl,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = reward.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = KikiCyan,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = KikiSurfaceElevated
                            ) {
                                Text(
                                    text = "🔊 SFX: ${reward.soundEffectLabel}",
                                    fontSize = 11.sp,
                                    color = KikiGold,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF00E676).copy(alpha = 0.18f)
                            ) {
                                Text(
                                    text = "+${reward.sparksAwarded} Sparks",
                                    fontSize = 11.sp,
                                    color = Color(0xFF00E676),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("collect_holo_badge_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = KikiLilac),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Collect Holographic Badge to Vault",
                                color = Color.Black,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2. Sound Design Menu in KikiWorld Lab:
 * Allows users to download/unlock and assign unique sound effects (Chime, Fizz, Pop, Cosmic Harp, Prism Drop)
 * to their custom cosmetic shade recipes, with real-time synthesized playback.
 */
@Composable
fun SoundDesignStudioCard(
    selectedSoundId: String,
    downloadedSoundIds: Set<String>,
    onDownloadAndAssignSound: (CosmeticSoundEffect) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, KikiGold.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
            .testTag("sound_design_menu_card"),
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
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = KikiGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Sonic Shade Sound Design Menu",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Text(
                            text = "Download & assign sensory audio signatures to your recipe",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiTextMuted
                        )
                    }
                }
                HoloBadge(text = "AUDIO FX")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                defaultCosmeticSoundEffects.forEach { sfx ->
                    val isDownloaded = sfx.isPreInstalled || downloadedSoundIds.contains(sfx.id)
                    val isAssigned = selectedSoundId == sfx.id
                    val accent = parseHexColor(sfx.accentHex)

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isAssigned) accent.copy(alpha = 0.16f) else KikiSurfaceElevated,
                        border = BorderStroke(
                            width = if (isAssigned) 1.5.dp else 1.dp,
                            color = if (isAssigned) accent else KikiSurfaceBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                HapticsUtil.performSparkleClick(context)
                                SynthAudioEngine.playRecipeSoundEffect(sfx.id)
                                onDownloadAndAssignSound(sfx)
                            }
                            .testTag("sound_effect_item_${sfx.id}")
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
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(accent.copy(alpha = 0.22f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(sfx.iconEmoji, fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = sfx.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = KikiPearl
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = sfx.frequencyProfile,
                                            fontSize = 10.sp,
                                            color = accent
                                        )
                                    }
                                    Text(
                                        text = sfx.description,
                                        fontSize = 11.sp,
                                        color = KikiTextMuted,
                                        maxLines = 2
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = when {
                                    isAssigned -> accent
                                    isDownloaded -> KikiDarkBg
                                    else -> KikiLilac.copy(alpha = 0.25f)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when {
                                            isAssigned -> Icons.Default.CheckCircle
                                            isDownloaded -> Icons.Default.PlayArrow
                                            else -> Icons.Default.CloudDownload
                                        },
                                        contentDescription = null,
                                        tint = if (isAssigned) Color.Black else KikiPearl,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = when {
                                            isAssigned -> "Assigned"
                                            isDownloaded -> "Assign"
                                            else -> "Download"
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAssigned) Color.Black else KikiPearl
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

/**
 * 3. Advanced Audio Mixing Panel with Real-Time 'Shimmer / Sparkle' High-Frequency Filter.
 */
@Composable
fun AudioShimmerMixerCard(
    shimmerIntensity: Float,
    onShimmerFilterChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var isLoopPlaying by remember { mutableStateOf(false) }
    var harmonicSparkle by remember { mutableFloatStateOf(0.8f) }
    var selectedLoopPattern by remember { mutableIntStateOf(0) }
    val loopNames = listOf("Starlight Arp", "Cyber Glaze Synth", "Tokyo Dew Loop")

    DisposableEffect(Unit) {
        onDispose {
            SynthAudioEngine.stopShimmerMelodyLoop()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "shimmerSpec")
    val animPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, KikiCyan.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
            .testTag("audio_shimmer_mixer_card"),
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
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = KikiCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Real-Time Audio Shimmer Filter",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Text(
                            text = "High-frequency harmonic sparkle processor for Lab melody loops",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiTextMuted
                        )
                    }
                }

                Button(
                    onClick = {
                        isLoopPlaying = !isLoopPlaying
                        if (isLoopPlaying) {
                            SynthAudioEngine.startShimmerMelodyLoop(
                                shimmerIntensity = shimmerIntensity,
                                harmonicSparkle = harmonicSparkle,
                                loopPatternIndex = selectedLoopPattern
                            )
                        } else {
                            SynthAudioEngine.stopShimmerMelodyLoop()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLoopPlaying) KikiRose else KikiCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("toggle_shimmer_audio_loop_btn")
                ) {
                    Icon(
                        imageVector = if (isLoopPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isLoopPlaying) "Stop Loop" else "Play Shimmer",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Melody Loop Selector
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(loopNames.indices.toList()) { idx ->
                    val isSel = selectedLoopPattern == idx
                    FilterChip(
                        selected = isSel,
                        onClick = {
                            selectedLoopPattern = idx
                            if (isLoopPlaying) {
                                SynthAudioEngine.startShimmerMelodyLoop(
                                    shimmerIntensity = shimmerIntensity,
                                    harmonicSparkle = harmonicSparkle,
                                    loopPatternIndex = selectedLoopPattern
                                )
                            }
                        },
                        label = {
                            Text(
                                text = "♫ ${loopNames[idx]}",
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KikiCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = KikiSurfaceElevated,
                            labelColor = KikiPearl
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Real-Time High-Frequency Spectrum Visualizer Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(KikiDarkBg)
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val barCount = 24
                    val spacing = size.width / barCount
                    for (i in 0 until barCount) {
                        val isHighFreqBand = i >= 12
                        val baseAmp = if (isLoopPlaying) {
                            0.35f + 0.35f * kotlin.math.abs(sin(animPhase + i * 0.45f))
                        } else {
                            0.18f
                        }
                        val shimmerBoost = if (isHighFreqBand) shimmerIntensity * harmonicSparkle * 0.55f else 0.1f
                        val normalizedH = (baseAmp + shimmerBoost).coerceIn(0.12f, 1f)
                        val barH = size.height * normalizedH
                        val barColor = when {
                            i >= 18 -> KikiGold
                            i >= 12 -> KikiCyan
                            else -> KikiLilac
                        }
                        drawLine(
                            color = barColor,
                            start = Offset(i * spacing + spacing / 2f, size.height),
                            end = Offset(i * spacing + spacing / 2f, size.height - barH),
                            strokeWidth = (spacing * 0.58f).coerceAtLeast(4f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "High-Shelf Sparkle Filter (+2 Octave Air)",
                    fontSize = 11.sp,
                    color = KikiPearl,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${(shimmerIntensity * 100).toInt()}% Shimmer",
                    fontSize = 11.sp,
                    color = KikiCyan,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = shimmerIntensity,
                onValueChange = {
                    onShimmerFilterChanged(it)
                    if (isLoopPlaying) {
                        SynthAudioEngine.startShimmerMelodyLoop(
                            shimmerIntensity = it,
                            harmonicSparkle = harmonicSparkle,
                            loopPatternIndex = selectedLoopPattern
                        )
                    }
                },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = KikiCyan,
                    activeTrackColor = KikiCyan,
                    inactiveTrackColor = KikiSurfaceBorder
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Harmonic Crystalline Resonance",
                    fontSize = 11.sp,
                    color = KikiPearl,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${(harmonicSparkle * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = KikiGold,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = harmonicSparkle,
                onValueChange = {
                    harmonicSparkle = it
                    if (isLoopPlaying) {
                        SynthAudioEngine.startShimmerMelodyLoop(
                            shimmerIntensity = shimmerIntensity,
                            harmonicSparkle = it,
                            loopPatternIndex = selectedLoopPattern
                        )
                    }
                },
                valueRange = 0.1f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = KikiGold,
                    activeTrackColor = KikiGold,
                    inactiveTrackColor = KikiSurfaceBorder
                )
            )
        }
    }
}

/**
 * 4. Interactive Star Map in the Bioscience Skills Hub:
 * Users tap on constellations to learn fun facts about space & astro-botanical bioscience,
 * unlocking 'Star Dust' currency to be used for cosmetic formulas.
 */
@Composable
fun BioscienceStarMapCard(
    starDustBalance: Int,
    unlockedConstellations: Set<String>,
    onConstellationTapped: (StarConstellationNode) -> Unit,
    onInfuseStarDustIntoFormula: (StarConstellationNode) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeConstellation by remember { mutableStateOf(defaultBioscienceConstellations.first()) }

    val infiniteTransition = rememberInfiniteTransition(label = "starTwinkle")
    val twinkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "twinkle"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, KikiLilac.copy(alpha = 0.65f), RoundedCornerShape(22.dp))
            .testTag("bioscience_star_map_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141122))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔭", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Bioscience Skills Hub • Astro Star Map",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = KikiPearl
                        )
                    }
                    Text(
                        text = "Tap constellations to learn space bioscience & harvest Star Dust",
                        style = MaterialTheme.typography.labelSmall,
                        color = KikiCyan
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = KikiGold.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, KikiGold)
                ) {
                    Text(
                        text = "✨ $starDustBalance Star Dust",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = KikiGold,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("star_dust_balance_badge")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Starfield Constellation Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF231B3F), Color(0xFF0B0914))
                        )
                    )
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(16.dp))
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            val w = size.width.toFloat()
                            val h = size.height.toFloat()
                            // Find closest constellation star
                            val tapped = defaultBioscienceConstellations.minByOrNull { node ->
                                node.normalizedPoints.minOf { (nx, ny) ->
                                    hypot(tapOffset.x - nx * w, tapOffset.y - ny * h)
                                }
                            }
                            if (tapped != null) {
                                HapticsUtil.performSparkleClick(context)
                                activeConstellation = tapped
                                onConstellationTapped(tapped)
                            }
                        }
                    }
                    .testTag("interactive_star_map_canvas")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Background cosmic dust stars
                    for (i in 0 until 36) {
                        val sx = ((i * 73 + 19) % 100) / 100f * w
                        val sy = ((i * 41 + 37) % 100) / 100f * h
                        drawCircle(
                            color = Color.White.copy(alpha = if (i % 2 == 0) 0.35f * twinkleAlpha else 0.25f),
                            radius = if (i % 5 == 0) 2.5f else 1.5f,
                            center = Offset(sx, sy)
                        )
                    }

                    // Draw each constellation
                    defaultBioscienceConstellations.forEach { constellation ->
                        val isSelected = activeConstellation.id == constellation.id
                        val isUnlocked = unlockedConstellations.contains(constellation.id)
                        val nodeColor = parseHexColor(constellation.colorHex)

                        val pts = constellation.normalizedPoints.map { (nx, ny) ->
                            Offset(nx * w, ny * h)
                        }

                        // Connecting lines
                        for (idx in 0 until pts.size - 1) {
                            drawLine(
                                color = if (isSelected) nodeColor else nodeColor.copy(alpha = 0.45f),
                                start = pts[idx],
                                end = pts[idx + 1],
                                strokeWidth = if (isSelected) 3.5f else 1.8f
                            )
                        }

                        // Star nodes
                        pts.forEachIndexed { idx, pt ->
                            if (isSelected || isUnlocked) {
                                drawCircle(
                                    color = nodeColor.copy(alpha = 0.28f * twinkleAlpha),
                                    radius = if (idx == 0) 14f else 10f,
                                    center = pt
                                )
                            }
                            drawCircle(
                                color = if (isSelected) Color.White else nodeColor,
                                radius = if (idx == 0) 6f else 4.5f,
                                center = pt
                            )
                        }
                    }
                }

                // Quick-select chips overlaid at bottom of star map
                LazyRow(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(defaultBioscienceConstellations) { node ->
                        val isSelected = activeConstellation.id == node.id
                        val isUnlocked = unlockedConstellations.contains(node.id)
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = if (isSelected) parseHexColor(node.colorHex) else Color(0xCC1B162E),
                            border = BorderStroke(1.dp, parseHexColor(node.colorHex)),
                            modifier = Modifier
                                .clickable {
                                    HapticsUtil.performSparkleClick(context)
                                    activeConstellation = node
                                    onConstellationTapped(node)
                                }
                                .testTag("constellation_chip_${node.id}")
                        ) {
                            Text(
                                text = "${if (isUnlocked) "★" else "☆"} ${node.name}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selected Constellation Bioscience & Space Fact Details
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = KikiSurfaceElevated,
                border = BorderStroke(1.dp, parseHexColor(activeConstellation.colorHex).copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${activeConstellation.name} • ${activeConstellation.latinTitle}",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = KikiPearl
                            )
                            Text(
                                text = "Astro-Botanical Active: ${activeConstellation.unlockedIngredient}",
                                fontSize = 11.sp,
                                color = parseHexColor(activeConstellation.colorHex),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = KikiGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "+${activeConstellation.starDustReward} Star Dust",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = KikiGold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "🌌 Space Fact: ${activeConstellation.spaceFact}",
                        fontSize = 11.sp,
                        color = KikiPearl.copy(alpha = 0.9f),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "🧬 Bioscience Skill: ${activeConstellation.bioscienceLink}",
                        fontSize = 11.sp,
                        color = KikiCyan,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onInfuseStarDustIntoFormula(activeConstellation) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("infuse_star_dust_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = parseHexColor(activeConstellation.colorHex)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Infuse '${activeConstellation.unlockedIngredient}' (50 Star Dust)",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
