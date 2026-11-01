package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesome
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.PastelButtercup
import com.example.ui.screens.PastelLavender
import com.example.ui.screens.PastelMint
import com.example.ui.screens.PastelPink
import com.example.ui.screens.PastelSky
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted
import com.example.util.HapticsUtil
import kotlin.math.atan2

data class MemberSparkleSlice(
    val memberName: String,
    val displayName: String,
    val count: Int,
    val points: Int,
    val color: Color,
    val iconEmoji: String
)

/**
 * Native Jetpack Compose Donut Chart visualizing the breakdown of collected '1D World' sparkle items
 * across band members (Liam, Harry, Louis, Niall, Zayn, and Band Together) with interactive slice selection,
 * tactile haptics, and responsive layout.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MemberSparkleDonutChart(
    slices: List<MemberSparkleSlice>,
    modifier: Modifier = Modifier,
    onSliceSelected: ((MemberSparkleSlice?) -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedMemberName by remember { mutableStateOf<String?>(null) }
    var animationTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animationTrigger = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationTrigger) 1f else 0f,
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "donutChartAnimation"
    )

    val totalItems = slices.sumOf { it.count }.coerceAtLeast(1)
    val totalPoints = slices.sumOf { it.points }
    val selectedSlice = slices.find { it.memberName == selectedMemberName }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("member_sparkle_donut_chart_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(OneDirectionRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PastelPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Sparkle Collection Breakdown",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Text(
                            text = "Item distribution by 1D band member",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiTextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = KikiSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, KikiSurfaceBorder)
                ) {
                    Text(
                        text = "$totalPoints Sparks",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = KikiGold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Donut Chart Graphic & Center Summary
            Box(
                modifier = Modifier.size(210.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(190.dp)
                        .pointerInput(slices) {
                            detectTapGestures { offset ->
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val touchAngleRad = atan2(offset.y - center.y, offset.x - center.x)
                                var touchAngleDeg = Math.toDegrees(touchAngleRad.toDouble()).toFloat()
                                if (touchAngleDeg < 0) touchAngleDeg += 360f

                                // Donut start angle is -90 (top)
                                val adjustedAngle = (touchAngleDeg + 90f) % 360f

                                var currentAngle = 0f
                                for (slice in slices) {
                                    val sweep = (slice.count.toFloat() / totalItems.toFloat()) * 360f
                                    if (adjustedAngle >= currentAngle && adjustedAngle < currentAngle + sweep) {
                                        selectedMemberName = if (selectedMemberName == slice.memberName) null else slice.memberName
                                        HapticsUtil.performSparkleTick(context)
                                        onSliceSelected?.invoke(selectedSlice)
                                        break
                                    }
                                    currentAngle += sweep
                                }
                            }
                        }
                        .testTag("donut_chart_canvas")
                ) {
                    val strokeWidth = 26.dp.toPx()
                    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                    val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

                    // Background track ring
                    drawArc(
                        color = Color(0xFF231E33),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )

                    var startAngle = -90f
                    slices.forEach { slice ->
                        val sweepAngle = ((slice.count.toFloat() / totalItems.toFloat()) * 360f) * animatedProgress
                        val isSelected = selectedMemberName == slice.memberName
                        val effectiveStroke = if (isSelected) strokeWidth * 1.25f else strokeWidth
                        val effectiveColor = if (selectedMemberName == null || isSelected) {
                            slice.color
                        } else {
                            slice.color.copy(alpha = 0.35f)
                        }

                        if (sweepAngle > 0.5f) {
                            drawArc(
                                color = effectiveColor,
                                startAngle = startAngle,
                                sweepAngle = (sweepAngle - 2f).coerceAtLeast(1f),
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = effectiveStroke, cap = StrokeCap.Round)
                            )
                        }
                        startAngle += sweepAngle
                    }
                }

                // Inner Cutout Content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            selectedMemberName = null
                            HapticsUtil.performSparkleClick(context)
                            onSliceSelected?.invoke(null)
                        }
                        .padding(12.dp)
                ) {
                    if (selectedSlice != null) {
                        Text(
                            text = selectedSlice.iconEmoji,
                            fontSize = 24.sp
                        )
                        Text(
                            text = selectedSlice.displayName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = selectedSlice.color
                        )
                        Text(
                            text = "${selectedSlice.count} items (${(selectedSlice.count * 100) / totalItems}%)",
                            fontSize = 11.sp,
                            color = KikiPearl
                        )
                    } else {
                        Text(
                            text = "✨",
                            fontSize = 24.sp
                        )
                        Text(
                            text = "$totalItems Items",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = KikiPearl
                        )
                        Text(
                            text = "Tap to Inspect",
                            fontSize = 10.sp,
                            color = KikiTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Interactive Legend Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                slices.forEach { slice ->
                    val isSelected = selectedMemberName == slice.memberName
                    val percent = if (totalItems > 0) (slice.count * 100) / totalItems else 0

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) slice.color.copy(alpha = 0.25f) else KikiSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) slice.color else KikiSurfaceBorder
                        ),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clickable {
                                selectedMemberName = if (isSelected) null else slice.memberName
                                HapticsUtil.performSparkleTick(context)
                                onSliceSelected?.invoke(if (isSelected) null else slice)
                            }
                            .testTag("donut_legend_${slice.memberName.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(slice.color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${slice.iconEmoji} ${slice.displayName}",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) slice.color else KikiPearl
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "(${slice.count} • $percent%)",
                                fontSize = 10.sp,
                                color = KikiTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
