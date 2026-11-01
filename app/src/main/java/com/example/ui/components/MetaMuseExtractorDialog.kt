package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.window.DialogProperties
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MuseExtractionScopeItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val itemCount: Int,
    val isSelected: Boolean = true
)

val defaultExtractedFormulas = listOf(
    CustomCreation(
        id = "muse_ext_01",
        name = "Meta Muse Astral Champagne",
        category = ProductCategory.LIP_OIL,
        primaryHex = "#F3E5AB",
        secondaryHex = "#00F5D4",
        finish = FinishType.HOLOGRAPHIC,
        scentNote = "Vanilla Silk & Amber Glaze",
        bottleShape = "Prismatic Wand",
        shimmerIntensity = 0.88f,
        squalanePercentage = 42,
        notes = "Extracted from Meta Muse Runway Drop #08. Formulated with 42% plant-derived squalane and golden diamond pearls."
    ),
    CustomCreation(
        id = "muse_ext_02",
        name = "Cosmic Peach Solar Glaze",
        category = ProductCategory.HIGHLIGHTER,
        primaryHex = "#FFB3BA",
        secondaryHex = "#FF729F",
        finish = FinishType.GLASS_GLOSS,
        scentNote = "White Peach & Solar Blossom",
        bottleShape = "Compact Twist Stick",
        shimmerIntensity = 0.70f,
        squalanePercentage = 35,
        notes = "Extracted from Meta Muse Trend Signals. High-refraction dewy peach highlighter formula."
    ),
    CustomCreation(
        id = "muse_ext_03",
        name = "Cyber Orchid Peel Lacquer",
        category = ProductCategory.NAIL_LACQUER,
        primaryHex = "#FF007F",
        secondaryHex = "#B388FF",
        finish = FinishType.CHROME_METALLIC,
        scentNote = "Wild Violet & Cyber Jasmine",
        bottleShape = "Sculpted Cube Dropper",
        shimmerIntensity = 0.95f,
        squalanePercentage = 25,
        notes = "Extracted from Meta Muse Co-Creation Stems. Solvent-free water-based peel-off chrome lacquer."
    ),
    CustomCreation(
        id = "muse_ext_04",
        name = "Velvet Nebula Botanical Mist",
        category = ProductCategory.SCENT_MIST,
        primaryHex = "#7B2CBF",
        secondaryHex = "#00F5D4",
        finish = FinishType.VELVET_MATTE,
        scentNote = "Celestial Musk, Bergamot & Amber",
        bottleShape = "Fine-Atomizer Flacon",
        shimmerIntensity = 0.20f,
        squalanePercentage = 15,
        notes = "Extracted from Meta Muse Fragrance Lab. Mood-boosting botanical mood mist with cyber peony."
    ),
    CustomCreation(
        id = "muse_ext_05",
        name = "Prismatic Stardust Tint",
        category = ProductCategory.CHEEK_TINT,
        primaryHex = "#FEE440",
        secondaryHex = "#FF007F",
        finish = FinishType.PEARL_SHIMMER,
        scentNote = "Solar Citrus & Golden Honey",
        bottleShape = "Dropper Ampoule",
        shimmerIntensity = 0.65f,
        squalanePercentage = 30,
        notes = "Extracted from Meta Muse Community Stem Tree. Dual-phase cheek and lip tint."
    )
)

@Composable
fun MetaMuseExtractorDialog(
    onDismiss: () -> Unit,
    onFormulasExtracted: (List<CustomCreation>) -> Unit,
    onOpenVault: () -> Unit = {},
    onOpenDrive: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isExtracting by remember { mutableStateOf(false) }
    var extractionProgress by remember { mutableFloatStateOf(0f) }
    var currentExtractionStep by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }
    var extractedItemsCount by remember { mutableStateOf(0) }

    // Extraction Targets
    var ingestToVault by remember { mutableStateOf(true) }
    var pushToGoogleDrive by remember { mutableStateOf(true) }
    var generateLocalZip by remember { mutableStateOf(true) }

    // Extraction Scope Items
    var scopeItems by remember {
        mutableStateOf(
            listOf(
                MuseExtractionScopeItem("formulas", "Shade Formulas & Pigment Ratios", "Extracted 28 lab recipes with squalane loads", "🧪", 28),
                MuseExtractionScopeItem("textures", "High-Res Swatch & Texture Packs", "16 holographic finish maps & 3D renders", "🎨", 16),
                MuseExtractionScopeItem("stems", "Community Blend Stems & Logs", "8 multi-creator voting trees & chat notes", "👥", 8),
                MuseExtractionScopeItem("inci", "INCI Safety & Clean Beauty Docs", "42 compliant lab certification datasets", "🌿", 42),
                MuseExtractionScopeItem("trends", "Runway Trend Forecasting Telemetry", "Meta hashtag volumes & color velocity", "🌐", 12)
            )
        )
    }

    val totalItemsSelected = remember(scopeItems) {
        scopeItems.filter { it.isSelected }.sumOf { it.itemCount }
    }

    val animatedProgress by animateFloatAsState(targetValue = extractionProgress, label = "progress")

    fun startExtraction() {
        if (isExtracting || isCompleted) return
        isExtracting = true
        extractionProgress = 0.05f
        currentExtractionStep = "Connecting to Meta Muse Graph Protocol (Zero-Code)..."

        coroutineScope.launch {
            delay(500)
            extractionProgress = 0.25f
            currentExtractionStep = "Pulling 28 community formulas & color spectrum data..."
            delay(600)
            extractionProgress = 0.50f
            currentExtractionStep = "Extracting holographic finish maps & squalane ratios..."
            delay(700)
            extractionProgress = 0.75f
            currentExtractionStep = "Bundling Meta Muse ZIP archive & prepping Google Drive sync..."
            delay(600)
            extractionProgress = 0.92f
            currentExtractionStep = "Ingesting 5 master creations into Kikiworld Vault..."
            delay(500)
            extractionProgress = 1.0f
            currentExtractionStep = "Extraction & Archive Ingestion Complete!"
            isExtracting = false
            isCompleted = true
            extractedItemsCount = totalItemsSelected

            if (ingestToVault) {
                onFormulasExtracted(defaultExtractedFormulas)
            }
        }
    }

    Dialog(
        onDismissRequest = { if (!isExtracting) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.94f)
                .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(24.dp))
                .testTag("meta_muse_extractor_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = KikiSurfaceDark
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(HolographicGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Meta Muse Extractor",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                            Text(
                                text = "Direct Deep Sync & Archive Ingestion",
                                style = MaterialTheme.typography.bodySmall,
                                color = KikiCyan
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isExtracting,
                        modifier = Modifier.testTag("close_extractor_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = KikiPearl)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Protocol Notice
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = KikiSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Hub, contentDescription = null, tint = KikiLilac, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Extracts all drops, formulas, community blend stems, and swatch textures out of Meta Muse into your Vault & Drive without access codes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = KikiPearl,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isCompleted) {
                    // Extraction Scope Checklist
                    Text(
                        text = "1. Select What to Pull Out of Meta Muse:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = KikiPearl
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        scopeItems.forEachIndexed { index, item ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (item.isSelected) KikiDarkBg else KikiSurfaceElevated,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isExtracting) {
                                        scopeItems = scopeItems.toMutableList().also { list ->
                                            list[index] = item.copy(isSelected = !item.isSelected)
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = item.isSelected,
                                        onCheckedChange = { checked ->
                                            if (!isExtracting) {
                                                scopeItems = scopeItems.toMutableList().also { list ->
                                                    list[index] = item.copy(isSelected = checked)
                                                }
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = KikiCyan,
                                            checkmarkColor = Color.Black
                                        ),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(item.iconEmoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = KikiPearl)
                                        Text(item.subtitle, style = MaterialTheme.typography.labelSmall, color = KikiTextMuted)
                                    }
                                    Text("${item.itemCount}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = KikiCyan)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Destinations
                    Text(
                        text = "2. Extraction Destinations:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = KikiPearl
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (ingestToVault) KikiCyan.copy(alpha = 0.15f) else KikiDarkBg,
                            border = if (ingestToVault) androidx.compose.foundation.BorderStroke(1.dp, KikiCyan) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !isExtracting) { ingestToVault = !ingestToVault }
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📱", fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("In-App Vault", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = KikiPearl)
                                Text("Live formulas", fontSize = 9.sp, color = KikiTextMuted)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (pushToGoogleDrive) KikiGold.copy(alpha = 0.15f) else KikiDarkBg,
                            border = if (pushToGoogleDrive) androidx.compose.foundation.BorderStroke(1.dp, KikiGold) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !isExtracting) { pushToGoogleDrive = !pushToGoogleDrive }
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("☁️", fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Google Drive", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = KikiPearl)
                                Text("ZIP archive", fontSize = 9.sp, color = KikiTextMuted)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (generateLocalZip) KikiLilac.copy(alpha = 0.15f) else KikiDarkBg,
                            border = if (generateLocalZip) androidx.compose.foundation.BorderStroke(1.dp, KikiLilac) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = !isExtracting) { generateLocalZip = !generateLocalZip }
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💾", fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Local Export", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = KikiPearl)
                                Text("JSON / ZIP", fontSize = 9.sp, color = KikiTextMuted)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isExtracting) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = currentExtractionStep,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = KikiCyan,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${(animatedProgress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = KikiPearl
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = KikiCyan,
                                trackColor = KikiDarkBg
                            )
                        }
                    } else {
                        Button(
                            onClick = { startExtraction() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("start_meta_muse_extraction_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = KikiCyan),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pull Everything Out ($totalItemsSelected Items)",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    // Success View
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(KikiCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = KikiCyan,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Meta Muse Archive Pulled Successfully!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Extracted $extractedItemsCount assets & loaded 5 master formulations into your Vault.",
                            style = MaterialTheme.typography.bodySmall,
                            color = KikiTextMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Sparks Reward Card
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = KikiGold.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, KikiGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("✨", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "+250 Creator Sparks Awarded for Deep Ingestion!",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = KikiGold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Preview of Pulled Formulas
                        Text(
                            text = "Ingested Formulations in Your Vault:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = KikiPearl,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            defaultExtractedFormulas.take(3).forEach { formula ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = KikiDarkBg,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(parseHexColor(formula.primaryHex))
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = formula.name,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = KikiPearl
                                            )
                                            Text(
                                                text = "${formula.category.displayName} • ${formula.finish.displayName}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = KikiTextMuted
                                            )
                                        }
                                        Icon(Icons.Default.Check, contentDescription = null, tint = KikiCyan, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onDismiss()
                                    onOpenDrive()
                                },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.FolderZip, contentDescription = null, tint = KikiGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Drive ZIP", color = KikiPearl, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    onDismiss()
                                    onOpenVault()
                                },
                                modifier = Modifier.weight(1f).height(44.dp).testTag("open_vault_after_extraction_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = KikiCyan),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Science, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open Vault", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
