package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ReikiGuideScreen(
    onNavigateBack: () -> Unit
) {
    var isSessionActive by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(180) } // 3 minute session

    LaunchedEffect(isSessionActive) {
        if (isSessionActive) {
            timeLeft = 180
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            isSessionActive = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KikiDarkBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isSessionActive) "Healing in Progress" else "Reiki Guide",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                color = KikiPearl
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (!isSessionActive) {
            Image(
                painter = painterResource(id = R.drawable.reiki_guide_asset_1793402962454),
                contentDescription = "Reiki Energy",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            // Energy Visualization
            val infiniteTransition = rememberInfiniteTransition(label = "energy")
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse), label = "scale"
            )
            Canvas(modifier = Modifier.size(200.dp).padding(16.dp)) {
                drawCircle(color = KikiLilac.copy(alpha = 0.3f), radius = size.minDimension / 2 * scale)
                drawCircle(color = KikiCyan.copy(alpha = 0.5f), radius = size.minDimension / 3 * scale)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isSessionActive) "${timeLeft / 60}:${(timeLeft % 60).toString().padStart(2, '0')}" else "Align Your Energy",
            style = MaterialTheme.typography.displaySmall,
            color = KikiPearl,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isSessionActive) "Focus on your intention and breathe deeply." else "Welcome to your personal Reiki guide. Take a deep breath, ground yourself, and let the healing flow through your intentions.",
            style = MaterialTheme.typography.bodyLarge,
            color = KikiTextMuted,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { isSessionActive = !isSessionActive },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isSessionActive) KikiTextMuted else KikiLilac),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isSessionActive) "End Session" else "Begin Session", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
