package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.ui.theme.HolographicGradient
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
import kotlinx.coroutines.delay

data class SparkleCard(
    val id: Int,
    val symbol: String,
    val name: String,
    val color: Color
)

@Composable
fun SparkleMatchGame(
    onGameCompleted: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val symbols = remember {
        listOf(
            Triple("⭐", "Star", KikiGold),
            Triple("👑", "Crown", Color(0xFFFEE440)),
            Triple("💖", "Heart", Color(0xFFFF85A1)),
            Triple("✨", "Sparkle", KikiCyan),
            Triple("🌸", "Blossom", Color(0xFFFFB3BA)),
            Triple("💎", "Gem", Color(0xFFB388FF))
        )
    }

    val cards = remember {
        mutableStateListOf<SparkleCard>().apply {
            val list = mutableListOf<SparkleCard>()
            var idCounter = 0
            symbols.forEach { (sym, name, col) ->
                list.add(SparkleCard(idCounter++, sym, name, col))
                list.add(SparkleCard(idCounter++, sym, name, col))
            }
            list.shuffle()
            addAll(list)
        }
    }

    val flippedIndices = remember { mutableStateListOf<Int>() }
    val matchedIndices = remember { mutableStateListOf<Int>() }
    var moveCount by remember { mutableIntStateOf(0) }
    var isChecking by remember { mutableStateOf(false) }
    var isGameWon by remember { mutableStateOf(false) }

    fun resetGame() {
        cards.clear()
        val list = mutableListOf<SparkleCard>()
        var idCounter = 0
        symbols.forEach { (sym, name, col) ->
            list.add(SparkleCard(idCounter++, sym, name, col))
            list.add(SparkleCard(idCounter++, sym, name, col))
        }
        list.shuffle()
        cards.addAll(list)

        flippedIndices.clear()
        matchedIndices.clear()
        moveCount = 0
        isChecking = false
        isGameWon = false
    }

    LaunchedEffect(flippedIndices.size) {
        if (flippedIndices.size == 2) {
            isChecking = true
            moveCount++
            val first = cards[flippedIndices[0]]
            val second = cards[flippedIndices[1]]

            if (first.symbol == second.symbol) {
                delay(400)
                matchedIndices.add(flippedIndices[0])
                matchedIndices.add(flippedIndices[1])
                flippedIndices.clear()
                isChecking = false

                if (matchedIndices.size == cards.size) {
                    isGameWon = true
                    onGameCompleted(moveCount)
                }
            } else {
                delay(900)
                flippedIndices.clear()
                isChecking = false
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFFF85A1).copy(alpha = 0.5f), RoundedCornerShape(22.dp))
            .testTag("sparkle_match_game"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF85A1).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✨", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Sparkle Match Game",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Text(
                            text = "Calm & gentle • Match stars, crowns & hearts",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFF85A1)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = KikiSurfaceElevated
                ) {
                    Text(
                        text = "Pairs: ${matchedIndices.size / 2} / ${symbols.size}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = KikiGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Game Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                itemsIndexed(cards) { index, card ->
                    val isFlipped = index in flippedIndices || index in matchedIndices
                    val isMatched = index in matchedIndices

                    val cardBg by animateColorAsState(
                        targetValue = when {
                            isMatched -> card.color.copy(alpha = 0.25f)
                            isFlipped -> KikiSurfaceElevated
                            else -> Color(0xFF1E1A2B)
                        },
                        animationSpec = tween(300),
                        label = "cardBg"
                    )

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(cardBg)
                            .border(
                                width = if (isMatched) 1.5.dp else 1.dp,
                                color = if (isMatched) card.color else KikiSurfaceBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable(enabled = !isFlipped && !isChecking) {
                                if (flippedIndices.size < 2 && index !in flippedIndices) {
                                    flippedIndices.add(index)
                                }
                            }
                            .testTag("match_card_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isFlipped) {
                            Text(
                                text = card.symbol,
                                fontSize = 28.sp
                            )
                        } else {
                            Text(
                                text = "✨",
                                fontSize = 16.sp,
                                color = KikiTextMuted.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer Status & Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gentle taps: $moveCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = KikiTextMuted
                )

                OutlinedButton(
                    onClick = { resetGame() },
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Shuffle", modifier = Modifier.size(14.dp), tint = KikiPearl)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Shuffle", style = MaterialTheme.typography.labelSmall, color = KikiPearl)
                }
            }

            // Peaceful Victory Banner
            if (isGameWon) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF00E676).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💖", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Sweet Gentle Magic! ✨",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF00E676)
                            )
                            Text(
                                text = "You found every matching star, crown, and heart in $moveCount gentle taps.",
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiPearl
                            )
                        }
                    }
                }
            }
        }
    }
}
