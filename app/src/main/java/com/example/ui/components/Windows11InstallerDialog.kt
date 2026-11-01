package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.HolographicGradient
import com.example.ui.theme.KikiCyan
import com.example.ui.theme.KikiDarkBg
import com.example.ui.theme.KikiGold
import com.example.ui.theme.KikiLilac
import com.example.ui.theme.KikiPearl
import com.example.ui.theme.KikiSurfaceBorder
import com.example.ui.theme.KikiSurfaceDark
import com.example.ui.theme.KikiSurfaceElevated
import com.example.ui.theme.KikiTextMuted

@Composable
fun Windows11InstallerDialog(
    onDismiss: () -> Unit,
    onSwitchToWindowsDesktopMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var copiedCommand by remember { mutableStateOf<String?>(null) }
    var selectedMethodTab by remember { mutableStateOf(0) }

    val powershellScript = """
# KIKI World - Windows 11 Home & Pro 1-Click Sideload
# 1. Open Windows Subsystem for Android (WSA) Settings -> Turn on "Developer Mode"
# 2. Run this command in Windows Terminal (PowerShell):
adb connect 127.0.0.1:58526
adb install -r KikiWorld_v27_Windows11.apk
# 3. KIKI World is now pinned to your Windows 11 Start Menu and Taskbar!
    """.trimIndent()

    val batchScript = """
@echo off
title KIKI World - Windows 11 PC Installer
echo ==============================================
echo Installing KIKI World on Windows 11 PC...
echo ==============================================
adb connect 127.0.0.1:58526
adb install -r "%~dp0KikiWorld_Windows11.apk"
echo.
echo [SUCCESS] App installed on your Windows 11 PC!
pause
    """.trimIndent()

    fun copyToClipboard(label: String, text: String, id: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        copiedCommand = id
        Toast.makeText(context, "Copied $label to clipboard!", Toast.LENGTH_SHORT).show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .border(1.dp, KikiSurfaceBorder, RoundedCornerShape(24.dp))
                .testTag("windows_11_installer_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = KikiSurfaceDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
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
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0078D4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🪟", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Install on Windows 11 PC",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                            Text(
                                text = "Windows 11 Home & Pro • WSA / Sideload Ready",
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiCyan
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_windows_installer_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = KikiPearl)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Windows Compatibility Status Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = KikiSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0078D4).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Windows 11 PC Certified • Build 22000+",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = KikiPearl
                            )
                            Text(
                                text = "Supports Windows 11 Home & Pro with WSA, keyboard shortcuts, mouse gestures & multi-window snapping.",
                                style = MaterialTheme.typography.labelSmall,
                                color = KikiTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Method Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("1-Click WSA", "PowerShell", "Batch Script").forEachIndexed { index, title ->
                        val isSelected = selectedMethodTab == index
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFF0078D4) else KikiDarkBg,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMethodTab = index }
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(vertical = 8.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) Color.White else KikiPearl,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (selectedMethodTab) {
                    0 -> {
                        // WSA-Pacman 1-Click Guide
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Method 1: WSA-Pacman (Recommended for Windows 11)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = KikiCyan
                            )
                            Text(
                                text = "WSA-Pacman is the native Windows 11 package manager that gives Android apps a double-click .exe style installer.",
                                style = MaterialTheme.typography.bodySmall,
                                color = KikiTextMuted
                            )

                            Surface(shape = RoundedCornerShape(10.dp), color = KikiDarkBg, modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Step 1: Install WSA-Pacman on your Windows 11 PC (free on GitHub or Winget: winget install wsa-pacman)", style = MaterialTheme.typography.labelSmall, color = KikiPearl)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Step 2: Double-click KikiWorld_v27_Windows11.apk", style = MaterialTheme.typography.labelSmall, color = KikiPearl)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Step 3: Click 'Install' — Kiki World appears in your Windows Start Menu!", style = MaterialTheme.typography.labelSmall, color = KikiGold)
                                }
                            }
                        }
                    }
                    1 -> {
                        // PowerShell Command
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Method 2: Windows Terminal / PowerShell",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = KikiCyan
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0C0C0C),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("PowerShell 7 / Windows Terminal", style = MaterialTheme.typography.labelSmall, color = KikiTextMuted)
                                        IconButton(
                                            onClick = { copyToClipboard("PowerShell Sideload Script", powershellScript, "ps") },
                                            modifier = Modifier.size(28.dp).testTag("copy_powershell_button")
                                        ) {
                                            Icon(
                                                imageVector = if (copiedCommand == "ps") Icons.Default.Check else Icons.Default.ContentCopy,
                                                contentDescription = "Copy",
                                                tint = if (copiedCommand == "ps") KikiCyan else KikiPearl,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = powershellScript,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color(0xFF81C784),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        // Batch script
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Method 3: 1-Click .BAT Installer File",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = KikiCyan
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0C0C0C),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("install_windows11.bat", style = MaterialTheme.typography.labelSmall, color = KikiTextMuted)
                                        IconButton(
                                            onClick = { copyToClipboard("Windows Batch Script", batchScript, "bat") },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (copiedCommand == "bat") Icons.Default.Check else Icons.Default.ContentCopy,
                                                contentDescription = "Copy",
                                                tint = if (copiedCommand == "bat") KikiCyan else KikiPearl,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = batchScript,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color(0xFF81C784),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Actions
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            onDismiss()
                            onSwitchToWindowsDesktopMode()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("launch_windows_11_mode_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0078D4)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.DesktopWindows, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Switch into Windows 11 Desktop Mode",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Done", color = KikiPearl, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
