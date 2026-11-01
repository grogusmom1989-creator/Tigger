package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FinishType
import com.example.data.ProductCategory
import com.example.ui.theme.HolographicGradient
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiMagenta
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted
import kotlin.math.roundToInt

@Composable
fun OneDimensionalModeCanvas(
    category: ProductCategory,
    primaryHex: String,
    secondaryHex: String,
    finish: FinishType,
    shimmerIntensity: Float,
    squalanePercentage: Int,
    onPrimaryColorChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = remember(primaryHex) { parseHexColor(primaryHex) }
    val secondaryColor = remember(secondaryHex) { parseHexColor(secondaryHex) }

    // 1D Spectrum Wavelength points
    val spectrumHexList = remember {
        listOf(
            "#FF007F", // 700nm deep magenta
            "#FF416C", // 650nm red-rose
            "#FFB3BA", // 620nm soft peach
            "#FEE440", // 580nm solar gold
            "#00F5D4", // 510nm cyan
            "#7B2CBF", // 440nm royal violet
            "#B388FF", // 400nm lavender spark
            "#F3E5AB"  // 380nm champagne pearl
        )
    }

    var normalizedPosition by remember { mutableFloatStateOf(0.45f) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(24.dp))
            .testTag("one_dimensional_mode_canvas"),
        shape = RoundedCornerShape(24.dp),
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(HolographicGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LinearScale,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "1D Spectral Mode",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Text(
                            text = "Linear Wavelength & Refraction Track",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiCyan
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = KikiSurfaceElevated
                ) {
                    Text(
                        text = "${(380 + normalizedPosition * 320).roundToInt()} nm",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = KikiGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1D Linear Spectrogram Ribbon (Scrubbable / Interactive)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F0E17))
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(16.dp))
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val newNorm = (offset.x / size.width).coerceIn(0f, 1f)
                            normalizedPosition = newNorm
                            val targetIndex = ((spectrumHexList.size - 1) * newNorm).roundToInt().coerceIn(0, spectrumHexList.size - 1)
                            onPrimaryColorChanged(spectrumHexList[targetIndex])
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val newNorm = (change.position.x / size.width).coerceIn(0f, 1f)
                            normalizedPosition = newNorm
                            val targetIndex = ((spectrumHexList.size - 1) * newNorm).roundToInt().coerceIn(0, spectrumHexList.size - 1)
                            onPrimaryColorChanged(spectrumHexList[targetIndex])
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Background 1D linear chromatic gradient
                    val spectrumColors = spectrumHexList.map { parseHexColor(it) }
                    drawRoundRect(
                        brush = Brush.horizontalGradient(spectrumColors),
                        topLeft = Offset(0f, 0f),
                        size = Size(w, h),
                        cornerRadius = CornerRadius(16f, 16f)
                    )

                    // Overlay shimmer frequency lines
                    val lineCount = 30
                    val shimmerAlpha = (0.2f + (shimmerIntensity * 0.4f)).coerceIn(0.1f, 0.7f)
                    for (i in 0..lineCount) {
                        val x = (w / lineCount) * i
                        drawLine(
                            color = Color.White.copy(alpha = shimmerAlpha * (if (i % 2 == 0) 0.6f else 0.2f)),
                            start = Offset(x, 0f),
                            end = Offset(x, h),
                            strokeWidth = 1.5f
                        )
                    }

                    // Scrubber Indicator at normalizedPosition
                    val scrubX = w * normalizedPosition
                    // Halo circle
                    drawCircle(
                        color = Color.White,
                        radius = 18f,
                        center = Offset(scrubX, h / 2f)
                    )
                    drawCircle(
                        color = primaryColor,
                        radius = 14f,
                        center = Offset(scrubX, h / 2f)
                    )
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.4f),
                        radius = 6f,
                        center = Offset(scrubX, h / 2f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("380 nm (UV Edge)", style = MaterialTheme.typography.labelSmall, color = KikiTextMuted)
                Text("Scrub along 1D spectrum to blend", style = MaterialTheme.typography.labelSmall, color = KikiPearl)
                Text("700 nm (IR Edge)", style = MaterialTheme.typography.labelSmall, color = KikiTextMuted)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1D Spectral Telemetry Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = KikiSurfaceElevated,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Refraction Index",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiTextMuted,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${String.format("%.2f", 1.33f + (shimmerIntensity * 0.25f))} η",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiCyan
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = KikiSurfaceElevated,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Viscosity (Squalane)",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiTextMuted,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$squalanePercentage cP",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiLilac
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = KikiSurfaceElevated,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Finish Reflectance",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiTextMuted,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = finish.displayName,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                    }
                }
            }
        }
    }
}
