package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@Composable
fun TimeDisplay(
    modifier: Modifier = Modifier
) {
    var timeString by remember { mutableStateOf("12:00:00 PM") }
    var dateString by remember { mutableStateOf("Monday, November 1, 2026") }
    var offsetString by remember { mutableStateOf("EDT (UTC-4) • Eastern Time") }
    var zoneBadge by remember { mutableStateOf("ET") }

    LaunchedEffect(Unit) {
        val easternZone = ZoneId.of("America/New_York")
        val deviceZone = try {
            ZoneId.systemDefault()
        } catch (e: Exception) {
            easternZone
        }

        val timeFormatter = DateTimeFormatter.ofPattern("h:mm:ss a")
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")

        while (true) {
            try {
                val nowInstant = Instant.now()
                val easternZdt = nowInstant.atZone(easternZone)
                val deviceZdt = nowInstant.atZone(deviceZone)

                timeString = easternZdt.format(timeFormatter)
                dateString = easternZdt.format(dateFormatter)

                val isDst = easternZone.rules.isDaylightSavings(nowInstant)
                zoneBadge = if (isDst) "EDT" else "EST"

                // Calculate difference in offset hours relative to the device timezone
                val deviceOffsetSec = deviceZdt.offset.totalSeconds
                val easternOffsetSec = easternZdt.offset.totalSeconds
                val diffHours = (easternOffsetSec - deviceOffsetSec) / 3600
                val diffMinutes = abs((easternOffsetSec - deviceOffsetSec) % 3600) / 60

                val offsetDiffDesc = when {
                    diffHours == 0 && diffMinutes == 0 -> "Synced with device timezone"
                    diffHours > 0 -> "+${diffHours}h from device (${deviceZone.id.substringAfterLast('/')})"
                    else -> "${diffHours}h from device (${deviceZone.id.substringAfterLast('/')})"
                }

                val utcOffsetFormatted = easternZdt.offset.id
                offsetString = "Eastern Time ($utcOffsetFormatted) • $offsetDiffDesc"
            } catch (e: Exception) {
                timeString = "7:16:00 AM"
                offsetString = "Eastern Time (ET)"
            }
            delay(1000L)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, KikiGold.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
            .testTag("time_display_et"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
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
                            .background(KikiGold.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Clock",
                            tint = KikiGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Eastern Time (ET)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiTextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = KikiGold.copy(alpha = 0.22f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KikiGold.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = zoneBadge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = KikiGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = KikiSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = timeString,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        ),
                        color = KikiGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = offsetString,
                        style = MaterialTheme.typography.labelSmall,
                        color = KikiCyan,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
