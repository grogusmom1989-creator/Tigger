package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.KikiDarkBg

val OneDirectionRed = Color(0xFFE50914)
val OneDirectionCrimson = Color(0xFFB81D24)
val OneDirectionStageDark = Color(0xFF0A0910)

@Composable
fun OneDirectionWallpaper(
    dimmingAlpha: Float = 0.35f,
    customImageUri: String? = null,
    presetImageUrl: String? = null,
    modifier: Modifier = Modifier
) {
    val activePhoto = customImageUri ?: presetImageUrl

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OneDirectionStageDark)
    ) {
        if (!activePhoto.isNullOrBlank()) {
            // Render 1D Photo Wallpaper via Coil AsyncImage
            AsyncImage(
                model = activePhoto,
                contentDescription = "One Direction Photo Wallpaper",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Vignette Gradient & Stage Color Glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                OneDirectionRed.copy(alpha = 0.20f),
                                Color.Transparent,
                                Color(0xFF0A0910).copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Dimming Scrim Layer for App Text Readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(KikiDarkBg.copy(alpha = dimmingAlpha))
            )

            // Corner Stencil 1D Badge Watermark
            Surface(
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 10.dp, bottomEnd = 10.dp),
                color = OneDirectionRed.copy(alpha = 0.80f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = "1D",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        } else {
            // Fallback High-Res Stage Canvas with Stencil 1D Logo and Silhouettes
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // 1. Concert Stage Lighting Cones
                val spotlightLeft = Path().apply {
                    moveTo(w * 0.15f, 0f)
                    lineTo(0f, h * 0.75f)
                    lineTo(w * 0.45f, h * 0.75f)
                    close()
                }
                drawPath(
                    path = spotlightLeft,
                    brush = Brush.verticalGradient(
                        colors = listOf(OneDirectionRed.copy(alpha = 0.35f), Color.Transparent)
                    )
                )

                val spotlightRight = Path().apply {
                    moveTo(w * 0.85f, 0f)
                    lineTo(w * 0.55f, h * 0.75f)
                    lineTo(w, h * 0.75f)
                    close()
                }
                drawPath(
                    path = spotlightRight,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF00F5D4).copy(alpha = 0.20f), Color.Transparent)
                    )
                )

                // Center glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(OneDirectionRed.copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(w * 0.5f, h * 0.28f),
                        radius = w * 0.55f
                    ),
                    radius = w * 0.55f,
                    center = Offset(w * 0.5f, h * 0.28f)
                )

                // 2. ONE DIRECTION text header drawn via native Canvas Paint
                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = w * 0.09f
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        textAlign = Paint.Align.CENTER
                        letterSpacing = 0.12f
                        isAntiAlias = true
                        setShadowLayer(16f, 0f, 4f, android.graphics.Color.parseColor("#E50914"))
                    }
                    drawText("ONE DIRECTION", w / 2f, h * 0.15f, textPaint)

                    // 3. Iconic 1D Stencil Logo in Center
                    val logoPaint = Paint().apply {
                        color = android.graphics.Color.parseColor("#E50914")
                        textSize = w * 0.24f
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        textAlign = Paint.Align.CENTER
                        letterSpacing = 0.05f
                        isAntiAlias = true
                        setShadowLayer(24f, 0f, 0f, android.graphics.Color.parseColor("#FF0055"))
                    }
                    drawText("1D", w / 2f, h * 0.30f, logoPaint)
                }

                // 4. Stylized Band Silhouettes (Harry, Niall, Zayn, Liam, Louis)
                val memberXs = listOf(w * 0.18f, w * 0.34f, w * 0.50f, w * 0.66f, w * 0.82f)
                val memberHeights = listOf(h * 0.28f, h * 0.29f, h * 0.31f, h * 0.30f, h * 0.28f)
                val memberBaseY = h * 0.88f

                memberXs.forEachIndexed { i, cx ->
                    val mH = memberHeights[i]
                    val mW = w * 0.14f

                    // Body silhouette
                    drawRoundRect(
                        color = Color.Black.copy(alpha = 0.85f),
                        topLeft = Offset(cx - mW / 2f, memberBaseY - mH * 0.75f),
                        size = Size(mW, mH * 0.75f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                    )

                    // Head silhouette
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.90f),
                        radius = mW * 0.38f,
                        center = Offset(cx, memberBaseY - mH * 0.85f)
                    )

                    // Silhouette edge highlight (Concert back-light)
                    drawCircle(
                        color = if (i % 2 == 0) OneDirectionRed.copy(alpha = 0.5f) else Color(0xFF00F5D4).copy(alpha = 0.4f),
                        radius = mW * 0.40f,
                        center = Offset(cx, memberBaseY - mH * 0.85f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
                    )
                }

                // 5. Stage Floor with subtle reflection
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xFF050508).copy(alpha = 0.95f)),
                        startY = h * 0.82f,
                        endY = h
                    ),
                    topLeft = Offset(0f, h * 0.82f),
                    size = Size(w, h * 0.18f)
                )

                // 6. Frosted Dimming Overlay for app card legibility
                drawRect(
                    color = KikiDarkBg.copy(alpha = dimmingAlpha),
                    topLeft = Offset(0f, 0f),
                    size = Size(w, h)
                )
            }
        }
    }
}
