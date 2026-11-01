package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CommunityFormula
import com.example.data.DropCampaign
import com.example.data.FinishType
import com.example.data.ProductCategory
import com.example.data.VoteOption
import com.example.ui.theme.HolographicGradient
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiRose
import com.example.ui.theme.KikiSilver
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted

fun parseHexColor(hex: String, defaultColor: Color = Color(0xFFD67BFF)): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = clean.toLong(16)
        if (clean.length == 6) {
            Color(0xFF000000 or colorInt)
        } else if (clean.length == 8) {
            Color(colorInt)
        } else {
            defaultColor
        }
    } catch (e: Exception) {
        defaultColor
    }
}

@Composable
fun HoloBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = KikiSurfaceElevated,
    textColor: Color = KikiPearl,
    icon: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .border(1.dp, Brush.horizontalGradient(listOf(KikiLilac.copy(alpha = 0.6f), KikiCyan.copy(alpha = 0.6f))), RoundedCornerShape(100.dp)),
        shape = RoundedCornerShape(100.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon?.invoke()
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                color = textColor
            )
        }
    }
}

@Composable
fun SparkBalancePill(
    balance: Int,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(1.dp, KikiGold.copy(alpha = 0.7f), RoundedCornerShape(100.dp)),
        shape = RoundedCornerShape(100.dp),
        color = KikiSurfaceDark
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Sparks",
                tint = KikiGold,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "$balance",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = KikiPearl
            )
            Text(
                text = "SPARKS",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                color = KikiGold
            )
        }
    }
}

@Composable
fun SwatchPill(
    primaryHex: String,
    secondaryHex: String,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    val color1 = parseHexColor(primaryHex)
    val color2 = parseHexColor(secondaryHex)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(color1, color2)
                )
            )
            .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape)
    )
}

@Composable
fun InteractiveLiquidSwatch(
    category: ProductCategory,
    primaryHex: String,
    secondaryHex: String,
    finish: FinishType,
    shimmerIntensity: Float,
    modifier: Modifier = Modifier
) {
    val baseColor1 = parseHexColor(primaryHex)
    val baseColor2 = parseHexColor(secondaryHex)

    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(KikiSurfaceDark)
            .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(24.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f

            when (category) {
                ProductCategory.NAIL_LACQUER -> {
                    // Draw sleek nail silhouette with liquid polish reflection
                    val nailWidth = width * 0.38f
                    val nailHeight = height * 0.82f
                    val left = centerX - nailWidth / 2f
                    val top = centerY - nailHeight / 2f

                    val nailPath = Path().apply {
                        moveTo(left + nailWidth * 0.1f, top)
                        // Cuticle curve
                        quadraticTo(centerX, top - 15f, left + nailWidth * 0.9f, top)
                        // Right edge
                        lineTo(left + nailWidth, top + nailHeight * 0.7f)
                        // Tip curve
                        quadraticTo(centerX, top + nailHeight + 10f, left, top + nailHeight * 0.7f)
                        close()
                    }

                    // Nail fill with cosmetic gradient
                    drawPath(
                        path = nailPath,
                        brush = Brush.linearGradient(
                            colors = listOf(baseColor1, baseColor2, baseColor1.copy(alpha = 0.9f)),
                            start = Offset(left, top),
                            end = Offset(left + nailWidth, top + nailHeight)
                        )
                    )

                    // Finish overlay & shimmer highlight
                    if (finish == FinishType.HOLOGRAPHIC || finish == FinishType.CHROME_METALLIC || finish == FinishType.PEARL_SHIMMER) {
                        drawPath(
                            path = nailPath,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.6f * shimmerIntensity),
                                    KikiCyan.copy(alpha = 0.4f * shimmerIntensity),
                                    Color.Transparent
                                ),
                                start = Offset(shimmerOffset, top),
                                end = Offset(shimmerOffset + 120f, top + nailHeight)
                            )
                        )
                    }

                    // Glass sheen reflection curve
                    drawPath(
                        path = Path().apply {
                            moveTo(left + nailWidth * 0.22f, top + 20f)
                            quadraticTo(left + nailWidth * 0.25f, top + nailHeight * 0.5f, left + nailWidth * 0.28f, top + nailHeight * 0.75f)
                        },
                        color = Color.White.copy(alpha = 0.45f),
                        style = Stroke(width = 8f)
                    )
                }

                ProductCategory.LIP_OIL -> {
                    // Draw lush lip silhouette with dewy plumping reflection
                    val lipWidth = width * 0.65f
                    val lipHeight = height * 0.52f
                    val left = centerX - lipWidth / 2f
                    val top = centerY - lipHeight / 2f

                    val upperLip = Path().apply {
                        moveTo(left, top + lipHeight * 0.45f)
                        quadraticTo(left + lipWidth * 0.25f, top + lipHeight * 0.1f, centerX - 15f, top + lipHeight * 0.25f)
                        quadraticTo(centerX, top + lipHeight * 0.1f, centerX + 15f, top + lipHeight * 0.25f)
                        quadraticTo(left + lipWidth * 0.75f, top + lipHeight * 0.1f, left + lipWidth, top + lipHeight * 0.45f)
                        quadraticTo(centerX, top + lipHeight * 0.55f, left, top + lipHeight * 0.45f)
                        close()
                    }

                    val lowerLip = Path().apply {
                        moveTo(left, top + lipHeight * 0.45f)
                        quadraticTo(centerX, top + lipHeight * 0.55f, left + lipWidth, top + lipHeight * 0.45f)
                        quadraticTo(centerX, top + lipHeight + 20f, left, top + lipHeight * 0.45f)
                        close()
                    }

                    val lipBrush = Brush.verticalGradient(
                        colors = listOf(baseColor1, baseColor2),
                        startY = top,
                        endY = top + lipHeight
                    )

                    drawPath(upperLip, lipBrush)
                    drawPath(lowerLip, lipBrush)

                    // Mirror gloss wet glare
                    drawPath(
                        path = Path().apply {
                            moveTo(centerX - 40f, top + lipHeight * 0.65f)
                            quadraticTo(centerX, top + lipHeight * 0.75f, centerX + 40f, top + lipHeight * 0.65f)
                        },
                        color = Color.White.copy(alpha = 0.75f),
                        style = Stroke(width = 10f)
                    )

                    // Shimmer sweep
                    drawPath(
                        path = lowerLip,
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.5f * shimmerIntensity), Color.Transparent),
                            startX = shimmerOffset,
                            endX = shimmerOffset + 140f
                        )
                    )
                }

                else -> {
                    // Prismatic Orb & Swatch Droplet
                    val radius = height * 0.38f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                baseColor1,
                                baseColor2,
                                baseColor1.copy(alpha = 0.4f)
                            ),
                            center = Offset(centerX - 20f, centerY - 20f),
                            radius = radius * 1.2f
                        ),
                        radius = radius,
                        center = Offset(centerX, centerY)
                    )

                    // Highlight ring
                    drawCircle(
                        color = Color.White.copy(alpha = 0.35f),
                        radius = radius * 0.85f,
                        center = Offset(centerX - 25f, centerY - 25f),
                        style = Stroke(width = 4f)
                    )

                    // Holographic beam
                    drawCircle(
                        brush = Brush.linearGradient(
                            colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.6f * shimmerIntensity), Color.Transparent),
                            start = Offset(shimmerOffset, centerY - radius),
                            end = Offset(shimmerOffset + 80f, centerY + radius)
                        ),
                        radius = radius,
                        center = Offset(centerX, centerY)
                    )
                }
            }
        }
    }
}

@Composable
fun VotingOptionCard(
    option: VoteOption,
    totalVotesInCampaign: Int,
    isUserVote: Boolean,
    onVote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = if (totalVotesInCampaign > 0) {
        ((option.voteCount.toFloat() / totalVotesInCampaign.toFloat()) * 100).toInt()
    } else 0

    val animatedPercent by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "voteBar"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isUserVote) 1.5.dp else 1.dp,
                color = if (isUserVote) KikiLilac else KikiSurfaceBorder,
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUserVote) KikiSurfaceElevated else KikiSurfaceDark
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color swatch
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(parseHexColor(option.hexColor))
                        .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        if (isUserVote) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Your vote",
                                tint = KikiLilac,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = option.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = KikiTextMuted,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = if (isUserVote) KikiLilac else KikiPearl
                    )
                    Text(
                        text = "${option.voteCount} votes",
                        style = MaterialTheme.typography.labelSmall,
                        color = KikiTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Percentage bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(KikiSurfaceBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedPercent)
                        .height(8.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(
                            if (isUserVote) HolographicGradient else Brush.horizontalGradient(
                                listOf(parseHexColor(option.hexColor), KikiLilac)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HoloBadge(
                    text = option.finish.displayName,
                    containerColor = KikiDarkBg.copy(alpha = 0.6f)
                )

                Button(
                    onClick = onVote,
                    enabled = !isUserVote,
                    modifier = Modifier
                        .testTag("vote_btn_${option.id}")
                        .height(38.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUserVote) KikiSurfaceElevated else KikiLilac,
                        contentColor = if (isUserVote) KikiTextMuted else Color.Black
                    ),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Icon(
                        imageVector = if (isUserVote) Icons.Default.CheckCircle else Icons.Default.HowToVote,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isUserVote) "Voted" else "Vote (+50)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun DropCampaignCard(
    campaign: DropCampaign,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HoloBadge(
                    text = campaign.dropNumber,
                    containerColor = KikiSurfaceElevated
                )
                Text(
                    text = "⏳ ${campaign.hoursRemaining}h remaining",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = KikiCyan
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = campaign.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = KikiPearl
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = campaign.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = KikiTextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Swatch row previews
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                campaign.options.forEach { opt ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(parseHexColor(opt.hexColor))
                            .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${campaign.totalVotes} Co-Creators",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = KikiGold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_drop_${campaign.id}"),
                colors = ButtonDefaults.buttonColors(containerColor = KikiSurfaceElevated),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (campaign.userVotedOptionId != null) "View Drop Spec & Votes" else "Cast Vote (+50 Sparks)",
                    color = KikiLilac,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CommunityFormulaCard(
    formula: CommunityFormula,
    onRemix: () -> Unit,
    onToggleLike: () -> Unit,
    onOpenDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenDetail)
            .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SwatchPill(
                    primaryHex = formula.hexColor,
                    secondaryHex = formula.secondaryHex,
                    size = 38.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formula.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = KikiPearl,
                        maxLines = 1
                    )
                    Text(
                        text = "by ${formula.creatorHandle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = KikiTextMuted
                    )
                }

                IconButton(
                    onClick = onToggleLike,
                    modifier = Modifier.testTag("like_${formula.id}")
                ) {
                    Icon(
                        imageVector = if (formula.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (formula.isLiked) KikiRose else KikiTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HoloBadge(text = formula.category.displayName)
                HoloBadge(text = formula.finish.displayName)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "❤️ ${formula.upvotes}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = KikiPearl
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRemix,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("remix_${formula.id}"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, KikiLilac.copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = KikiLilac,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Remix in Beauty Lab",
                    color = KikiLilac,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
