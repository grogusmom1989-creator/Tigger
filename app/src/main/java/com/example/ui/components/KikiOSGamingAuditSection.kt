package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted
import com.example.util.HapticsUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class DeviceAuditCategory {
    ALL,
    CERTIFIED,
    GAMING_FAILED
}

data class KikiOsDeviceProfile(
    val id: String,
    val deviceName: String,
    val manufacturer: String,
    val iconEmoji: String,
    val isGamingDevice: Boolean,
    val canRunKikiOs: Boolean,
    val statusBadge: String,
    val statusColorHex: String,
    val hardwareSpecs: String,
    val funnyFailureReason: String,
    val thermalTempC: Int,
    val tdpWatts: Int,
    val fanRpm: Int,
    val squalaneTolerance: String
)

val defaultKikiOsDeviceProfiles = listOf(
    // 1. Steam Deck OLED
    KikiOsDeviceProfile(
        id = "dev_steam_deck",
        deviceName = "Valve Steam Deck OLED",
        manufacturer = "Valve (SteamOS / Proton GE)",
        iconEmoji = "🎮",
        isGamingDevice = true,
        canRunKikiOs = false,
        statusBadge = "FAILED • CAN'T RUN IT LOL",
        statusColorHex = "#FF3366",
        hardwareSpecs = "AMD Van Gogh 6nm APU • 16GB LPDDR5",
        funnyFailureReason = "Proton GE 9.4 Kernel Panic: Linux kernel crashed trying to emulate 35% squalane emulsion. RDNA 2 shaders melted into rose-gold slag during What Makes You Beautiful chorus.",
        thermalTempC = 104,
        tdpWatts = 45,
        fanRpm = 11200,
        squalaneTolerance = "0% (Hydrophobic Linux Kernel)"
    ),

    // 2. Nintendo Switch OLED
    KikiOsDeviceProfile(
        id = "dev_switch_oled",
        deviceName = "Nintendo Switch OLED",
        manufacturer = "Nintendo (Horizon OS)",
        iconEmoji = "🕹️",
        isGamingDevice = true,
        canRunKikiOs = false,
        statusBadge = "MELTED • CAN'T RUN IT LOL",
        statusColorHex = "#E50914",
        hardwareSpecs = "Nvidia Tegra X1 (2015) • 4GB LPDDR4",
        funnyFailureReason = "Tegra X1 clocked at 2015 frequencies choked on 6 arched pastel doorways and 37 Louis Tomlinson co-written songs. Joy-Cons detached in sheer panic and flew across the room.",
        thermalTempC = 99,
        tdpWatts = 18,
        fanRpm = 9400,
        squalaneTolerance = "0% (Plastic Joy-Con drift)"
    ),

    // 3. ASUS ROG Ally Z1 Extreme
    KikiOsDeviceProfile(
        id = "dev_rog_ally",
        deviceName = "ASUS ROG Ally Z1 Extreme",
        manufacturer = "ASUS Republic of Gamers (Windows 11)",
        iconEmoji = "🔥",
        isGamingDevice = true,
        canRunKikiOs = false,
        statusBadge = "OVERHEAT • CAN'T RUN IT LOL",
        statusColorHex = "#FF9900",
        hardwareSpecs = "AMD Ryzen Z1 Extreme • 8C/16T • 30W Turbo",
        funnyFailureReason = "Armoury Crate fans screamed at 8500 RPM. Windows Game Bar killed the process for excessive glam: Zayn's You & I octave-soaring falsetto waveform caused catastrophic thermal throttling to 0.2 GHz.",
        thermalTempC = 108,
        tdpWatts = 54,
        fanRpm = 8600,
        squalaneTolerance = "1% (Armoury Crate fan blew thermal paste away)"
    ),

    // 4. PlayStation Portal
    KikiOsDeviceProfile(
        id = "dev_ps_portal",
        deviceName = "PlayStation Portal",
        manufacturer = "Sony Interactive Entertainment",
        iconEmoji = "⚡",
        isGamingDevice = true,
        canRunKikiOs = false,
        statusBadge = "REJECTED • CAN'T RUN IT LOL",
        statusColorHex = "#9A4BFF",
        hardwareSpecs = "Snapdragon 662 • DualSense Haptics",
        funnyFailureReason = "Remote Play stream severed: DualSense haptic actuators burnt out attempting to simulate 90,000 Wembley Stadium Directioners stomping their feet in unison.",
        thermalTempC = 88,
        tdpWatts = 12,
        fanRpm = 0,
        squalaneTolerance = "0% (Wi-Fi dropped to 0 kbps)"
    ),

    // 5. Custom RTX 4090 Battlestation
    KikiOsDeviceProfile(
        id = "dev_rtx_4090",
        deviceName = "Custom RTX 4090 Gaming Rig",
        manufacturer = "NVIDIA & Custom Waterblock",
        iconEmoji = "🖥️",
        isGamingDevice = true,
        canRunKikiOs = false,
        statusBadge = "12VHPWR BLOWN • CAN'T RUN IT LOL",
        statusColorHex = "#00F5D4",
        hardwareSpecs = "Intel Core i9-14900KS • RTX 4090 24GB",
        funnyFailureReason = "DirectGlow 12 API error: NVIDIA DLSS 3.5 Frame Generation generated 500 frames of Harry's curls and melted the 12VHPWR power cable into pastel candlewax. Requires 2000W nuclear PSU.",
        thermalTempC = 102,
        tdpWatts = 650,
        fanRpm = 12000,
        squalaneTolerance = "Requires Sub-Zero Liquid Nitrogen Loop"
    ),

    // 6. Samsung Galaxy S25 Ultra (Certified)
    KikiOsDeviceProfile(
        id = "dev_galaxy_s25",
        deviceName = "Samsung Galaxy S25 Ultra",
        manufacturer = "Samsung Electronics (One UI 7)",
        iconEmoji = "📱",
        isGamingDevice = false,
        canRunKikiOs = true,
        statusBadge = "KIKIOS CERTIFIED ✓ 100%",
        statusColorHex = "#00E676",
        hardwareSpecs = "Snapdragon 8 Elite • 16GB RAM • Titanium Frame",
        funnyFailureReason = "Runs flawlessly! 120Hz LTPO display handles squalane shimmers and 1D starlight particles with zero battery drain. Knox certified.",
        thermalTempC = 34,
        tdpWatts = 8,
        fanRpm = 0,
        squalaneTolerance = "100% (Native Squalane Vapor Chamber)"
    ),

    // 7. Samsung Galaxy Watch 7 / Ultra (Certified)
    KikiOsDeviceProfile(
        id = "dev_galaxy_watch",
        deviceName = "Samsung Galaxy Watch 7 (Wear OS)",
        manufacturer = "Samsung & Google (Wear OS 5)",
        iconEmoji = "⌚",
        isGamingDevice = false,
        canRunKikiOs = true,
        statusBadge = "KIKIOS WRIST CERTIFIED ✓",
        statusColorHex = "#00E676",
        hardwareSpecs = "Exynos W1000 3nm • 454x454 Circular OLED",
        funnyFailureReason = "Fully supported! Real-time Bluetooth DataLayer sync, rotating concert jukebox, and haptic wrist sparkle mining.",
        thermalTempC = 29,
        tdpWatts = 2,
        fanRpm = 0,
        squalaneTolerance = "100% (IP68 Water & Squalane Resistant)"
    ),

    // 8. Windows 11 Desktop Pro (Certified)
    KikiOsDeviceProfile(
        id = "dev_win11_desktop",
        deviceName = "Windows 11 Desktop Windowing System",
        manufacturer = "Microsoft (Windows 11 Pro 24H2)",
        iconEmoji = "🪟",
        isGamingDevice = false,
        canRunKikiOs = true,
        statusBadge = "KIKIOS DESKTOP PRO ✓",
        statusColorHex = "#00D2FF",
        hardwareSpecs = "Universal x86_64 / ARM64 • Fluent Acrylic UI",
        funnyFailureReason = "Runs perfectly in windowed mode with NavigationRail, snap layouts, Room SQLite offline persistence, and resizable panes.",
        thermalTempC = 38,
        tdpWatts = 25,
        fanRpm = 1800,
        squalaneTolerance = "100% (Windows Subsystem for KikiOS)"
    ),

    // 9. Apple iPad Pro M4 (Certified)
    KikiOsDeviceProfile(
        id = "dev_ipad_m4",
        deviceName = "Apple iPad Pro M4",
        manufacturer = "Apple Inc. (iPadOS 18)",
        iconEmoji = "🍎",
        isGamingDevice = false,
        canRunKikiOs = true,
        statusBadge = "KIKIOS STANDALONE ✓",
        statusColorHex = "#FEE440",
        hardwareSpecs = "Apple M4 10-Core • Ultra Retina Tandem OLED",
        funnyFailureReason = "Runs in fullscreen Safari Standalone mode with Apple Touch Icons and smooth touch gestures.",
        thermalTempC = 31,
        tdpWatts = 15,
        fanRpm = 0,
        squalaneTolerance = "100% (Liquid Glass Retina)"
    ),

    // 10. Samsung Galaxy Z Fold 6 (Certified)
    KikiOsDeviceProfile(
        id = "dev_z_fold_6",
        deviceName = "Samsung Galaxy Z Fold 6",
        manufacturer = "Samsung Electronics (Foldable Posture)",
        iconEmoji = "📖",
        isGamingDevice = false,
        canRunKikiOs = true,
        statusBadge = "KIKIOS FOLDABLE POSTURE ✓",
        statusColorHex = "#B388FF",
        hardwareSpecs = "7.6\" Dynamic AMOLED 2X • Flex Hinge",
        funnyFailureReason = "Dual-screen bliss: 1D Doorways on the left flex panel, Lab Blender on the right! S-Pen supported.",
        thermalTempC = 33,
        tdpWatts = 9,
        fanRpm = 0,
        squalaneTolerance = "100% (Hinge Squalane Lubricant)"
    )
)

/**
 * Interactive KikiOS Hardware Compatibility & Gaming Audit Component.
 * Showcases that every mainstream device gets KikiOS, but gaming devices fail with hilarious diagnostics.
 */
@Composable
fun KikiOSGamingAuditSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(DeviceAuditCategory.ALL) }
    var selectedDeviceToBypass by remember { mutableStateOf<KikiOsDeviceProfile?>(null) }
    var showBypassSimDialog by remember { mutableStateOf(false) }

    val filteredProfiles = remember(selectedCategory) {
        when (selectedCategory) {
            DeviceAuditCategory.ALL -> defaultKikiOsDeviceProfiles
            DeviceAuditCategory.CERTIFIED -> defaultKikiOsDeviceProfiles.filter { it.canRunKikiOs }
            DeviceAuditCategory.GAMING_FAILED -> defaultKikiOsDeviceProfiles.filter { !it.canRunKikiOs }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                Brush.horizontalGradient(listOf(Color(0xFF00E676), Color(0xFFFF3366))),
                RoundedCornerShape(24.dp)
            )
            .testTag("kikios_hardware_audit_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171422))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
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
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF28223B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤖", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "KikiOS Universal Hardware Audit",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Every device gets KikiOS... gaming devices can't run it lol! 🎮❌",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFFF3366),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scoreboard Summary Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF00E676).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("5 / 5 Supported", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
                        Text("Phones, PC & Watches ✓", fontSize = 9.sp, color = KikiTextMuted)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFF3366).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF3366).copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("0 / 5 Gaming Rigs", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF3366))
                        Text("Melted & Overheated lol ❌", fontSize = 9.sp, color = KikiTextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedCategory == DeviceAuditCategory.ALL) Color.White else KikiSurfaceElevated,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedCategory = DeviceAuditCategory.ALL }
                        .testTag("filter_all_devices")
                ) {
                    Text(
                        text = "All Devices (10)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedCategory == DeviceAuditCategory.ALL) Color.Black else KikiPearl,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedCategory == DeviceAuditCategory.GAMING_FAILED) Color(0xFFFF3366) else KikiSurfaceElevated,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedCategory = DeviceAuditCategory.GAMING_FAILED }
                        .testTag("filter_gaming_failed")
                ) {
                    Text(
                        text = "Gaming (Can't Run ❌)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedCategory == DeviceAuditCategory.GAMING_FAILED) Color.Black else Color(0xFFFF3366),
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedCategory == DeviceAuditCategory.CERTIFIED) Color(0xFF00E676) else KikiSurfaceElevated,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedCategory = DeviceAuditCategory.CERTIFIED }
                        .testTag("filter_certified_devices")
                ) {
                    Text(
                        text = "Certified (5 ✓)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedCategory == DeviceAuditCategory.CERTIFIED) Color.Black else Color(0xFF00E676),
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Device Cards List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredProfiles.forEach { profile ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (profile.canRunKikiOs) Color(0xFF1E2623) else Color(0xFF281822),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (profile.canRunKikiOs) Color(0xFF00E676).copy(alpha = 0.5f) else Color(0xFFFF3366).copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("device_card_${profile.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(profile.iconEmoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = profile.deviceName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = profile.manufacturer,
                                            fontSize = 9.5.sp,
                                            color = KikiTextMuted
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (profile.canRunKikiOs) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFFFF3366).copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (profile.canRunKikiOs) Color(0xFF00E676) else Color(0xFFFF3366)
                                    )
                                ) {
                                    Text(
                                        text = profile.statusBadge,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (profile.canRunKikiOs) Color(0xFF00E676) else Color(0xFFFF3366),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Funny Diagnostic Message
                            Text(
                                text = profile.funnyFailureReason,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = if (profile.canRunKikiOs) Color(0xFFD1F2D9) else Color(0xFFFFD1DC),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Specs & Telemetry Pill Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DeviceThermostat,
                                        contentDescription = null,
                                        tint = if (profile.thermalTempC > 80) Color(0xFFFF3366) else Color(0xFF00E676),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${profile.thermalTempC}°C",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (profile.thermalTempC > 80) Color(0xFFFF3366) else Color(0xFF00E676)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (profile.fanRpm > 0) "${profile.fanRpm} RPM" else "Fanless",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Memory,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${profile.tdpWatts}W TDP",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                if (profile.isGamingDevice) {
                                    Button(
                                        onClick = {
                                            selectedDeviceToBypass = profile
                                            showBypassSimDialog = true
                                            HapticsUtil.performSparkleClick(context)
                                        },
                                        modifier = Modifier.height(28.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3366)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Force Run lol", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Force Run / Crash Simulation Dialog
    if (showBypassSimDialog && selectedDeviceToBypass != null) {
        val device = selectedDeviceToBypass!!
        var simProgress by remember { mutableFloatStateOf(0f) }
        var simStageText by remember { mutableStateOf("Initializing Squalane Kernel Injector...") }
        var isSimCompleted by remember { mutableStateOf(false) }

        LaunchedEffect(device.id) {
            simProgress = 0f
            isSimCompleted = false
            simStageText = "Probing ${device.deviceName} architecture..."
            delay(600L)
            simProgress = 0.25f
            simStageText = "Injecting 35% Squalane Botanical Driver into GPU..."
            delay(700L)
            simProgress = 0.6f
            simStageText = "Rendering 1D Starlight particle shaders... Fans at ${device.fanRpm} RPM!"
            delay(800L)
            simProgress = 0.85f
            simStageText = "Playing Liam's Steal My Girl opening harmony... APU OVERHEATED!"
            delay(800L)
            simProgress = 1.0f
            simStageText = "💥 GPU CRASHED! Told you gaming devices can't run it lol!"
            isSimCompleted = true
            HapticsUtil.performSparkleBurst(context)
        }

        Dialog(onDismissRequest = { showBypassSimDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color(0xFFFF3366), RoundedCornerShape(22.dp))
                    .testTag("gaming_device_bypass_dialog"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1420))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isSimCompleted) "💥 MELTDOWN! 💥" else "⚡ FORCE INSTALLING KIKIOS ⚡",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color(0xFFFF3366)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Target: ${device.deviceName}",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { simProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (isSimCompleted) Color(0xFFFF3366) else Color(0xFF00F5D4),
                        trackColor = Color(0xFF332035)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Real-time Telemetry Terminal Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0C0911),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "> $simStageText",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSimCompleted) Color(0xFFFF3366) else Color(0xFF00F5D4)
                            )

                            if (isSimCompleted) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "[KERNEL ERROR 0xLOL_GAMING_DEVICE]\n\"${device.funnyFailureReason}\"\n\n👉 Recommendation: Use your Phone, Smartwatch, or Windows 11 PC instead!",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    lineHeight = 13.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showBypassSimDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("close_bypass_dialog_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3366)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isSimCompleted) "Understood, gaming devices can't run it lol! 😂" else "Cancel Override",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
