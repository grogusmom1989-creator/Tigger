package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BlendRoom
import com.example.data.CustomCreation
import com.example.data.FinishType
import com.example.data.ProductCategory
import com.example.ui.theme.HolographicGradient
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiMagenta
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted

@Composable
fun CollaborativeBlendDialog(
    onDismiss: () -> Unit,
    onSaveSharedFormula: (CustomCreation) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeRoom by remember {
        mutableStateOf<BlendRoom?>(
            BlendRoom(
                roomId = "room_muse_77",
                roomCode = "MUSE-77",
                roomName = "Meta Muse Co-Creation Lab",
                hostCreator = "SirenAlchemist (You)",
                activeParticipantCount = 3,
                activeShadeHex = "#00F5D4",
                finishName = "Holo Glaze",
                squalanePercent = 25,
                shimmerIntensity = 0.85f,
                lastAction = "Meta Muse suggested +10% Prismatic Shimmer"
            )
        )
    }

    var shadeHex by remember { mutableStateOf("#00F5D4") }
    var secondaryHex by remember { mutableStateOf("#B388FF") }
    var shimmerIntensity by remember { mutableStateOf(0.85f) }
    var squalanePercent by remember { mutableStateOf(25) }
    var formulaName by remember { mutableStateOf("Astral Muse Duo-Glaze") }
    var liveActionLog by remember { mutableStateOf(listOf("Meta Muse joined session", "Host adjusted shade to Cyber Cyan", "Connected via Google Drive Bridge")) }
    var showJoinCodeBox by remember { mutableStateOf(false) }
    var enteredCode by remember { mutableStateOf("") }

    val presetShades = listOf("#00F5D4", "#B388FF", "#FF729F", "#FEE440", "#7B2CBF", "#FF007F")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = KikiSurfaceDark,
            modifier = modifier
                .fillMaxWidth()
                .border(1.dp, KikiCyan.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .testTag("collaborative_blend_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(HolographicGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Live Blend Session",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                            Text(
                                text = "Room: ${activeRoom?.roomCode ?: "Open"} • 3 Active Blenders",
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiCyan
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_blend_dialog")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = KikiTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Zero Code info card / Room Code banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = KikiSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Hub, contentDescription = null, tint = KikiLilac, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Meta Muse Bridge: Live (No Code Required)",
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiPearl
                            )
                        }

                        Text(
                            text = "Room Code: MUSE-77",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Shared Formula Preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(parseHexColor(shadeHex))
                            .border(3.dp, parseHexColor(secondaryHex), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✨", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = formulaName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Text(
                            text = "Finish: Holo Glaze • ${squalanePercent}% Squalane",
                            style = MaterialTheme.typography.bodySmall,
                            color = KikiLilac
                        )
                        Text(
                            text = "Shimmer: ${(shimmerIntensity * 100).toInt()}% • Duo-Tone Base",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Live Hue Selector
                Text(
                    text = "Collaborative Hue Swatches:",
                    style = MaterialTheme.typography.labelMedium,
                    color = KikiPearl
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    presetShades.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(parseHexColor(hex))
                                .border(
                                    if (shadeHex == hex) 3.dp else 1.dp,
                                    if (shadeHex == hex) Color.White else KikiSurfaceBorder,
                                    CircleShape
                                )
                                .clickable {
                                    shadeHex = hex
                                    liveActionLog = listOf("You changed shade to $hex") + liveActionLog.take(2)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (shadeHex == hex) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Shimmer Slider
                Text(
                    text = "Prismatic Shimmer: ${(shimmerIntensity * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = KikiPearl
                )
                Slider(
                    value = shimmerIntensity,
                    onValueChange = {
                        shimmerIntensity = it
                        liveActionLog = listOf("You adjusted shimmer to ${(it * 100).toInt()}%") + liveActionLog.take(2)
                    },
                    valueRange = 0.1f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = KikiCyan,
                        activeTrackColor = KikiCyan,
                        inactiveTrackColor = KikiSurfaceElevated
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("blend_shimmer_slider")
                )

                // Recent Live Events Log
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = KikiDarkBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Live Activity Stream:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = KikiGold
                        )
                        liveActionLog.take(2).forEach { log ->
                            Text(
                                text = "• $log",
                                fontSize = 11.sp,
                                color = KikiTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Button(
                    onClick = {
                        val sharedCreation = CustomCreation(
                            id = "shared_${System.currentTimeMillis()}",
                            name = formulaName,
                            category = ProductCategory.LIP_OIL,
                            primaryHex = shadeHex,
                            secondaryHex = secondaryHex,
                            finish = FinishType.HOLOGRAPHIC,
                            scentNote = "Cyber Vanilla & Stardust",
                            bottleShape = "Prismatic Vial",
                            shimmerIntensity = shimmerIntensity,
                            squalanePercentage = squalanePercent,
                            notes = "Co-created in live Blend Session with Meta Muse & collaborative community."
                        )
                        onSaveSharedFormula(sharedCreation)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_shared_formula_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = KikiCyan),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save to Vault & Drive (.zip)", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
