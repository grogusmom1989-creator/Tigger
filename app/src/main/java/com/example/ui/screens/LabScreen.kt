package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.LinearScale
import com.example.ui.components.OneDimensionalModeCanvas
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FinishType
import com.example.data.ProductCategory
import com.example.ui.components.HoloBadge
import com.example.ui.components.InteractiveLiquidSwatch
import com.example.ui.components.parseHexColor
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
import com.example.viewmodel.LabEditorState
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Build

private val CuratedPigments = listOf(
    "#FF729F" to "Rose Glaze",
    "#D67BFF" to "Cyber Lilac",
    "#5CE5E7" to "Neon Aqua",
    "#FF9248" to "Solar Peach",
    "#FFD166" to "Champagne",
    "#8A4FFF" to "Deep Amethyst",
    "#C5CAD6" to "Liquid Chrome",
    "#2E1A47" to "Obsidian Plum",
    "#E0F7FA" to "Ice Diamond",
    "#FF4071" to "Coral Bloom"
)

private val ScentOptions = listOf(
    "Vanilla Squalane",
    "Japanese Yuzu",
    "Wild Coconut Milk",
    "Cold Metallic Rose",
    "Ambergris & Fig",
    "Unscented Clean"
)

private val BottleShapes = listOf(
    "Prism Wand",
    "Refillable Dropper",
    "Chrome Capsule",
    "Pocket Vaporizer"
)


@Composable
fun LabScreen(
    state: LabEditorState,
    onCategoryChanged: (ProductCategory) -> Unit,
    onNameChanged: (String) -> Unit,
    onPrimaryHexChanged: (String) -> Unit,
    onSecondaryHexChanged: (String) -> Unit,
    onFinishChanged: (FinishType) -> Unit,
    onScentChanged: (String) -> Unit,
    onBottleChanged: (String) -> Unit,
    onShimmerChanged: (Float) -> Unit,
    onSqualaneChanged: (Int) -> Unit,
    onNotesChanged: (String) -> Unit,
    onSaveFormula: () -> Unit,
    onOrderSampleClicked: () -> Unit = {},
    onOpenGlossary: () -> Unit = {},
    onAiGenerateShade: (String) -> Unit = {},
    onMetaMuseConsult: (String) -> Unit = {},
    onSubmitToChallenge: (String) -> Unit = {},
    onAnalyzeImage: (android.graphics.Bitmap) -> Unit = {},
    challenges: List<com.example.data.Challenge> = emptyList(),
    onOpenDrive: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var previewMode by remember { mutableStateOf(0) }
    var showBeforeAfter by remember { mutableStateOf(false) }
    var aiPromptInput by remember { mutableStateOf("") }
    var metaMuseInput by remember { mutableStateOf("") }
    var selectedChallengeId by remember { mutableStateOf("") }

    val photoPicker = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                android.graphics.ImageDecoder.decodeBitmap(
                    android.graphics.ImageDecoder.createSource(context.contentResolver, it)
                )
            } else {
                android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            }
            onAnalyzeImage(bitmap)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(KikiDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Lab Studio Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BEAUTY LAB STUDIO",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = KikiPearl
                    )
                    HoloBadge(text = "CO-CREATION 2.0")
                }
                Text(
                    text = "Blend bespoke shades, textures, and active botanical infusions.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KikiTextMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onOpenGlossary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("open_ingredient_glossary_btn"),
                    shape = RoundedCornerShape(100.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KikiCyan.copy(alpha = 0.7f))
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Science,
                        contentDescription = null,
                        tint = KikiCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "📚 Explore Clean Ingredient Glossary",
                        color = KikiCyan,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Gemini AI Shade Copilot Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KikiLilac.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = KikiLilac,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gemini AI Shade Copilot",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = KikiLilac.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "AI Powered",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = KikiLilac,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = aiPromptInput,
                        onValueChange = { aiPromptInput = it },
                        placeholder = { Text("Describe your dream shade (e.g., 'Cyberpunk holographic neon violet gloss')...", color = KikiTextMuted, fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_copilot_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KikiLilac,
                            unfocusedBorderColor = KikiSurfaceBorder,
                            focusedTextColor = KikiPearl,
                            unfocusedTextColor = KikiPearl,
                            cursorColor = KikiLilac
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(listOf("Neon Sunset", "Prismatic Ice", "Velvet Plum", "Golden Champagne")) { promptTag ->
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        aiPromptInput = promptTag
                                        onAiGenerateShade(promptTag)
                                    },
                                    label = { Text(promptTag, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = KikiSurfaceElevated,
                                        labelColor = KikiPearl
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (aiPromptInput.isNotBlank()) {
                                    onAiGenerateShade(aiPromptInput)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = KikiLilac),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("ai_copilot_submit")
                        ) {
                            Text("Blend", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Meta Muse AI Trend Studio Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KikiCyan.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = KikiCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Meta Muse AI Trend Studio",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = KikiCyan.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Avant-Garde",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = KikiCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = metaMuseInput,
                        onValueChange = { metaMuseInput = it },
                        placeholder = { Text("Consult Meta Muse for runway trends (e.g., 'Met Gala Cyberpunk')...", color = KikiTextMuted, fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("meta_muse_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KikiCyan,
                            unfocusedBorderColor = KikiSurfaceBorder,
                            focusedTextColor = KikiPearl,
                            unfocusedTextColor = KikiPearl,
                            cursorColor = KikiCyan
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(listOf("Met Gala Editorial", "Cyber Matrix", "Y2K Chrome", "Elven Fairy")) { trendTag ->
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        metaMuseInput = trendTag
                                        onMetaMuseConsult(trendTag)
                                    },
                                    label = { Text(trendTag, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = KikiSurfaceElevated,
                                        labelColor = KikiPearl
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (metaMuseInput.isNotBlank()) {
                                    onMetaMuseConsult(metaMuseInput)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = KikiCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("meta_muse_submit")
                        ) {
                            Text("Consult", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Preview Mode Toggle: 3D Swatch vs Virtual Try-On
        item {
            TabRow(
                selectedTabIndex = previewMode,
                containerColor = KikiSurfaceDark,
                contentColor = KikiLilac,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[previewMode]),
                        color = KikiLilac
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(14.dp))
            ) {
                Tab(
                    selected = previewMode == 0,
                    onClick = { previewMode = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("3D Swatch Studio", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = previewMode == 1,
                    onClick = { previewMode = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Virtual Mirror", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = previewMode == 2,
                    onClick = { previewMode = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LinearScale, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("1D Mode", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }

        // Category Selector Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ProductCategory.values()) { category ->
                    val isSelected = state.category == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryChanged(category) },
                        label = {
                            Text(
                                text = "${category.icon} ${category.displayName}",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KikiLilac,
                            selectedLabelColor = Color.Black,
                            containerColor = KikiSurfaceDark,
                            labelColor = KikiPearl
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) KikiLilac else KikiSurfaceBorder
                        )
                    )
                }
            }
        }

        // Live Swatch, Virtual Try-On, or 1D Mode Canvas
        item {
            when (previewMode) {
                0 -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    InteractiveLiquidSwatch(
                        category = state.category,
                        primaryHex = state.primaryHex,
                        secondaryHex = state.secondaryHex,
                        finish = state.finish,
                        shimmerIntensity = state.shimmerIntensity,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Live Swatch Simulator • ${state.finish.displayName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = KikiCyan
                    )
                }
            }
            1 -> {
                // Virtual Mirror Try-On Display
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HoloBadge(text = "LIVE COSMETIC AR MIRROR", containerColor = KikiSurfaceElevated)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (showBeforeAfter) "Split View" else "Full Overlay",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = KikiTextMuted
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = showBeforeAfter,
                                    onCheckedChange = { showBeforeAfter = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = KikiLilac,
                                        checkedTrackColor = KikiSurfaceElevated
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // AR Portrait Canvas
                        val primaryColor = parseHexColor(state.primaryHex)
                        val secondaryColor = parseHexColor(state.secondaryHex)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF1E1B29)),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                val cx = w / 2f
                                val cy = h / 2f

                                // Stylized face/skin contour
                                drawCircle(
                                    color = Color(0xFFE2C2B3),
                                    radius = h * 0.45f,
                                    center = Offset(cx, cy)
                                )

                                // Soft blush highlight
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(primaryColor.copy(alpha = 0.35f), Color.Transparent),
                                        center = Offset(cx - w * 0.22f, cy - 10f),
                                        radius = 60f
                                    ),
                                    radius = 60f,
                                    center = Offset(cx - w * 0.22f, cy - 10f)
                                )
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(primaryColor.copy(alpha = 0.35f), Color.Transparent),
                                        center = Offset(cx + w * 0.22f, cy - 10f),
                                        radius = 60f
                                    ),
                                    radius = 60f,
                                    center = Offset(cx + w * 0.22f, cy - 10f)
                                )

                                // Lip path
                                val lipW = w * 0.35f
                                val lipH = h * 0.16f
                                val lipLeft = cx - lipW / 2f
                                val lipTop = cy + h * 0.12f

                                val lipPath = Path().apply {
                                    moveTo(lipLeft, lipTop + lipH * 0.4f)
                                    quadraticTo(cx - 20f, lipTop, cx, lipTop + lipH * 0.25f)
                                    quadraticTo(cx + 20f, lipTop, lipLeft + lipW, lipTop + lipH * 0.4f)
                                    quadraticTo(cx, lipTop + lipH * 1.1f, lipLeft, lipTop + lipH * 0.4f)
                                    close()
                                }

                                if (showBeforeAfter) {
                                    // Natural lip on left, cosmetic on right
                                    drawPath(lipPath, color = Color(0xFFC78283))
                                    // Clip right half
                                    drawRect(
                                        brush = Brush.linearGradient(listOf(primaryColor, secondaryColor)),
                                        topLeft = Offset(cx, lipTop),
                                        size = androidx.compose.ui.geometry.Size(lipW / 2f, lipH)
                                    )
                                    // Split line
                                    drawLine(
                                        color = Color.White,
                                        start = Offset(cx, lipTop - 10f),
                                        end = Offset(cx, lipTop + lipH + 10f),
                                        strokeWidth = 3f
                                    )
                                } else {
                                    // Full cosmetic swatch overlay
                                    drawPath(
                                        path = lipPath,
                                        brush = Brush.verticalGradient(
                                            colors = listOf(primaryColor, secondaryColor),
                                            startY = lipTop,
                                            endY = lipTop + lipH
                                        )
                                    )
                                    // Gloss sheen
                                    drawLine(
                                        color = Color.White.copy(alpha = 0.7f),
                                        start = Offset(cx - 30f, lipTop + lipH * 0.65f),
                                        end = Offset(cx + 30f, lipTop + lipH * 0.65f),
                                        strokeWidth = 6f
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Cosmetic filter mapped to facial landmarks • Zero cloud credits used",
                            style = MaterialTheme.typography.labelSmall,
                            color = KikiCyan
                        )
                    }
                }
            }
            else -> {
                OneDimensionalModeCanvas(
                    category = state.category,
                    primaryHex = state.primaryHex,
                    secondaryHex = state.secondaryHex,
                    finish = state.finish,
                    shimmerIntensity = state.shimmerIntensity,
                    squalanePercentage = state.squalanePercentage,
                    onPrimaryColorChanged = { onPrimaryHexChanged(it) }
                )
            }
        }
    }

        // Formula Name Input
        item {
            OutlinedTextField(
                value = state.formulaName,
                onValueChange = onNameChanged,
                label = { Text("Formula / Shade Name", color = KikiTextMuted) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_formula_name"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KikiLilac,
                    unfocusedBorderColor = KikiSurfaceBorder,
                    focusedTextColor = KikiPearl,
                    unfocusedTextColor = KikiPearl,
                    focusedContainerColor = KikiSurfaceDark,
                    unfocusedContainerColor = KikiSurfaceDark
                ),
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Base & Shift Pigments
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Base Pigment",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Text(
                            text = state.primaryHex,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiLilac
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(CuratedPigments) { (hex, _) ->
                            val isSelected = state.primaryHex.equals(hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(hex))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onPrimaryHexChanged(hex) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Duochrome Flash Shift",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Text(
                            text = state.secondaryHex,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(CuratedPigments.reversed()) { (hex, _) ->
                            val isSelected = state.secondaryHex.equals(hex, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(hex))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onSecondaryHexChanged(hex) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Finish Selector
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Surface Texture & Finish",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = KikiPearl
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FinishType.values().forEach { finish ->
                            val isSelected = state.finish == finish
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onFinishChanged(finish) }
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) KikiLilac else KikiSurfaceBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                color = if (isSelected) KikiSurfaceElevated else KikiDarkBg
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = finish.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) KikiLilac else KikiTextMuted,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Shimmer & Botanical Squalane Sliders
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Micro-Mica Shimmer Intensity",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = KikiPearl
                        )
                        Text(
                            text = "${(state.shimmerIntensity * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiGold
                        )
                    }

                    Slider(
                        value = state.shimmerIntensity,
                        onValueChange = onShimmerChanged,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = KikiGold,
                            activeTrackColor = KikiLilac,
                            inactiveTrackColor = KikiSurfaceBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Botanical Squalane & Peptide Hydration",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = KikiPearl
                        )
                        Text(
                            text = "${state.squalanePercentage}%",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiCyan
                        )
                    }

                    Slider(
                        value = state.squalanePercentage.toFloat(),
                        onValueChange = { onSqualaneChanged(it.toInt()) },
                        valueRange = 5f..30f,
                        steps = 24,
                        colors = SliderDefaults.colors(
                            thumbColor = KikiCyan,
                            activeTrackColor = KikiCyan,
                            inactiveTrackColor = KikiSurfaceBorder
                        )
                    )
                }
            }
        }

        // Scent Note & Bottle Shape
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = KikiSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Aroma / Fragrance Note",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = KikiPearl
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ScentOptions) { scent ->
                            val isSelected = state.scentNote == scent
                            FilterChip(
                                selected = isSelected,
                                onClick = { onScentChanged(scent) },
                                label = { Text(scent) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = KikiRose,
                                    selectedLabelColor = Color.Black,
                                    containerColor = KikiSurfaceElevated,
                                    labelColor = KikiPearl
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Packaging Hardware",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = KikiPearl
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(BottleShapes) { bottle ->
                            val isSelected = state.bottleShape == bottle
                            FilterChip(
                                selected = isSelected,
                                onClick = { onBottleChanged(bottle) },
                                label = { Text(bottle) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = KikiCyan,
                                    selectedLabelColor = Color.Black,
                                    containerColor = KikiSurfaceElevated,
                                    labelColor = KikiPearl
                                )
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons: Save & Order Sample Prototype
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onSaveFormula,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_formula_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = KikiLilac),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Formula to Beauty Deck (+150 Sparks)",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                if (challenges.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { /* Handle submission logic here */ },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, KikiLilac)
                    ) {
                        Text("Submit to Challenge", color = KikiLilac)
                    }
                }

                OutlinedButton(
                    onClick = { photoPicker.launch(androidx.activity.result.PickVisualMediaRequest(androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KikiCyan)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = KikiCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze Inspiration Photo", color = KikiCyan)
                }

                OutlinedButton(
                    onClick = onOpenDrive,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("pull_from_drive_lab_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KikiLilac)
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = KikiLilac)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pull Everything from Google Drive", color = KikiLilac, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onOrderSampleClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("order_sample_button"),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, KikiCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = KikiCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Order 5ml Prototype Sample & Laser Engraving",
                        color = KikiCyan,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}
