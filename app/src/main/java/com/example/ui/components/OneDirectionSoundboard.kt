package com.example.ui.components

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted

data class BandQuoteItem(
    val id: String,
    val member: String,
    val quote: String,
    val context: String,
    val year: String,
    val memberEmoji: String,
    val colorHex: String
)

val iconicBandQuotes = listOf(
    BandQuoteItem(
        id = "quote_vas_happenin",
        member = "Zayn Malik",
        quote = "Vas Happenin'?!",
        context = "The definitive One Direction catchphrase coined by Zayn during the 2010 X Factor video diaries.",
        year = "2010",
        memberEmoji = "⚡",
        colorHex = "#7B2CBF"
    ),
    BandQuoteItem(
        id = "quote_carrots",
        member = "Louis Tomlinson",
        quote = "I like girls who eat carrots!",
        context = "Louis' legendary answer during Week 2 video diary that instantly became a global Directioner meme.",
        year = "2010",
        memberEmoji = "🥕",
        colorHex = "#FF5722"
    ),
    BandQuoteItem(
        id = "quote_simple_effective",
        member = "Harry Styles",
        quote = "Simple, but effective.",
        context = "Harry describing his baking technique in the Holmes Chapel bakery before entering X Factor.",
        year = "2010",
        memberEmoji = "👑",
        colorHex = "#FF4071"
    ),
    BandQuoteItem(
        id = "quote_chonce",
        member = "Niall Horan",
        quote = "We took a chonce!",
        context = "Niall's beloved Irish pronunciation of 'chance' during the Take Me Home album interviews.",
        year = "2012",
        memberEmoji = "🎸",
        colorHex = "#00F5D4"
    ),
    BandQuoteItem(
        id = "quote_jimmy",
        member = "Louis Tomlinson",
        quote = "No! Jimmy protested!",
        context = "Louis and Kevin the Pigeon recounting their hilarious dramatic stories on tour.",
        year = "2011",
        memberEmoji = "🐦",
        colorHex = "#E50914"
    ),
    BandQuoteItem(
        id = "quote_london",
        member = "Harry Styles",
        quote = "London, you are incredible!",
        context = "Harry speaking to 80,000 screaming fans at Wembley Stadium during Where We Are Tour.",
        year = "2014",
        memberEmoji = "🎤",
        colorHex = "#FEE440"
    ),
    BandQuoteItem(
        id = "quote_best_fans",
        member = "Liam Payne",
        quote = "We have the best fans in the whole world.",
        context = "Liam accepting the Global Success Brit Award on behalf of One Direction.",
        year = "2013",
        memberEmoji = "🏆",
        colorHex = "#FFD166"
    )
)

@Composable
fun OneDirectionSoundboard(
    onQuotePlayed: (BandQuoteItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeQuote by remember { mutableStateOf(iconicBandQuotes[0]) }
    var isPlayingQuote by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "soundwave")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse),
        label = "wave"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, parseHexColor(activeQuote.colorHex).copy(alpha = 0.6f), RoundedCornerShape(22.dp))
            .testTag("one_direction_soundboard_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
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
                            .background(parseHexColor(activeQuote.colorHex).copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = parseHexColor(activeQuote.colorHex), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "1D Iconic Soundboard",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Text(
                            text = "Memorable quotes & video diary catchphrases",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiCyan
                        )
                    }
                }

                HoloBadge(text = activeQuote.year)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Active Playing Quote Banner (Tap to replay audio)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = KikiDarkBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, parseHexColor(activeQuote.colorHex)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isPlayingQuote = true
                        com.example.util.HapticsUtil.performJukeboxBeat(context)
                        com.example.util.MusicAudioEngine.playCatchphrase(
                            context = context,
                            speakerName = activeQuote.member,
                            phrase = activeQuote.quote
                        )
                        onQuotePlayed(activeQuote)
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(activeQuote.memberEmoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = activeQuote.member,
                                fontWeight = FontWeight.Bold,
                                color = parseHexColor(activeQuote.colorHex),
                                fontSize = 14.sp
                            )
                        }

                        // Animated Audio Waveform
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(18.dp)
                        ) {
                            Box(modifier = Modifier.width(3.dp).height((18 * (if (isPlayingQuote) waveScale else 0.4f)).dp).background(parseHexColor(activeQuote.colorHex), RoundedCornerShape(2.dp)))
                            Box(modifier = Modifier.width(3.dp).height((18 * (if (isPlayingQuote) 1f - waveScale + 0.3f else 0.6f)).dp).background(KikiCyan, RoundedCornerShape(2.dp)))
                            Box(modifier = Modifier.width(3.dp).height((18 * (if (isPlayingQuote) waveScale * 0.8f else 0.3f)).dp).background(KikiGold, RoundedCornerShape(2.dp)))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "\"${activeQuote.quote}\"",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = activeQuote.context,
                        style = MaterialTheme.typography.bodySmall,
                        color = KikiTextMuted,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Tap to replay quotes:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = KikiPearl
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Carousel of Quote Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(iconicBandQuotes) { quoteItem ->
                    val isSelected = activeQuote.id == quoteItem.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) parseHexColor(quoteItem.colorHex).copy(alpha = 0.25f) else KikiSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) parseHexColor(quoteItem.colorHex) else KikiSurfaceBorder
                        ),
                        modifier = Modifier
                            .clickable {
                                activeQuote = quoteItem
                                isPlayingQuote = true
                                com.example.util.HapticsUtil.performJukeboxBeat(context)
                                com.example.util.MusicAudioEngine.playCatchphrase(
                                    context = context,
                                    speakerName = quoteItem.member,
                                    phrase = quoteItem.quote
                                )
                                onQuotePlayed(quoteItem)
                            }
                            .testTag("quote_button_${quoteItem.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(quoteItem.memberEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = quoteItem.quote,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else KikiPearl
                                )
                                Text(
                                    text = quoteItem.member,
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
}
