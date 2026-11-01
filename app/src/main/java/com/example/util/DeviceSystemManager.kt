package com.example.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

data class DeviceSpecs(
    val deviceName: String,
    val osVersion: String,
    val batteryPct: Int,
    val isCharging: Boolean,
    val networkStatus: String,
    val availableStorage: String,
    val totalStorage: String,
    val availableRam: String,
    val totalRam: String
)

data class SystemToolItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val icon: ImageVector,
    val badge: String? = null,
    val isGooglePlay: Boolean = false,
    val launchAction: (Context) -> Unit
)

data class InstalledAppItem(
    val name: String,
    val packageName: String,
    val icon: Drawable? = null
)

object DeviceSystemManager {

    fun getDeviceSpecs(context: Context): DeviceSpecs {
        val manufacturer = Build.MANUFACTURER.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val model = Build.MODEL
        val deviceName = "$manufacturer $model"
        val osVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

        // Battery
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, ifilter)
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (level >= 0 && scale > 0) (level * 100 / scale) else 100
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        // Network
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNet = connMgr?.activeNetwork
        val caps = connMgr?.getNetworkCapabilities(activeNet)
        val networkStatus = when {
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "Wi-Fi Connected"
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Cellular 4G/5G"
            caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true -> "Online"
            else -> "Offline"
        }

        // Storage
        val stat = StatFs(Environment.getDataDirectory().path)
        val blockSize = stat.blockSizeLong
        val availableBytes = stat.availableBlocksLong * blockSize
        val totalBytes = stat.blockCountLong * blockSize
        val availStorageGB = String.format("%.1f GB", availableBytes / (1024.0 * 1024.0 * 1024.0))
        val totalStorageGB = String.format("%.1f GB", totalBytes / (1024.0 * 1024.0 * 1024.0))

        // RAM
        val actMgr = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actMgr?.getMemoryInfo(memInfo)
        val availRamGB = String.format("%.1f GB", memInfo.availMem / (1024.0 * 1024.0 * 1024.0))
        val totalRamGB = String.format("%.1f GB", memInfo.totalMem / (1024.0 * 1024.0 * 1024.0))

        return DeviceSpecs(
            deviceName = deviceName,
            osVersion = osVersion,
            batteryPct = batteryPct,
            isCharging = isCharging,
            networkStatus = networkStatus,
            availableStorage = availStorageGB,
            totalStorage = totalStorageGB,
            availableRam = availRamGB,
            totalRam = totalRamGB
        )
    }

    fun openGooglePlay(context: Context, targetPackage: String? = null) {
        val uriStr = if (targetPackage != null) {
            "market://details?id=$targetPackage"
        } else {
            "market://apps"
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriStr)).apply {
            setPackage("com.android.vending")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback to browser
            val webUri = if (targetPackage != null) {
                "https://play.google.com/store/apps/details?id=$targetPackage"
            } else {
                "https://play.google.com/store/apps"
            }
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUri)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "Could not open Google Play: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun safeStartIntent(context: Context, intent: Intent, failureMsg: String) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, failureMsg, Toast.LENGTH_SHORT).show()
        }
    }

    fun getSystemTools(context: Context): List<SystemToolItem> {
        return listOf(
            SystemToolItem(
                id = "play_store",
                title = "Google Play Store",
                category = "Google",
                description = "Get & update your favorite apps, games and tools",
                icon = Icons.Default.Shop,
                badge = "Shortcut",
                isGooglePlay = true,
                launchAction = { ctx -> openGooglePlay(ctx) }
            ),
            SystemToolItem(
                id = "camera",
                title = "Camera",
                category = "Media",
                description = "Capture high-resolution photos & video",
                icon = Icons.Default.CameraAlt,
                launchAction = { ctx ->
                    safeStartIntent(ctx, Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA), "Camera app not found")
                }
            ),
            SystemToolItem(
                id = "photos",
                title = "Photos & Gallery",
                category = "Media",
                description = "View on-device photo library and albums",
                icon = Icons.Default.PhotoLibrary,
                launchAction = { ctx ->
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        type = "image/*"
                    }
                    safeStartIntent(ctx, intent, "Gallery app not found")
                }
            ),
            SystemToolItem(
                id = "settings",
                title = "System Settings",
                category = "System",
                description = "Configure OS, apps, accounts & preferences",
                icon = Icons.Default.Settings,
                launchAction = { ctx ->
                    safeStartIntent(ctx, Intent(Settings.ACTION_SETTINGS), "Settings not accessible")
                }
            ),
            SystemToolItem(
                id = "wifi",
                title = "Wi-Fi & Internet",
                category = "System",
                description = "Connect to wireless networks & hotspots",
                icon = Icons.Default.Wifi,
                launchAction = { ctx ->
                    safeStartIntent(ctx, Intent(Settings.ACTION_WIFI_SETTINGS), "Wi-Fi settings not accessible")
                }
            ),
            SystemToolItem(
                id = "bluetooth",
                title = "Bluetooth Devices",
                category = "System",
                description = "Pair headphones, tablets, keyboards & accessories",
                icon = Icons.Default.Bluetooth,
                launchAction = { ctx ->
                    safeStartIntent(ctx, Intent(Settings.ACTION_BLUETOOTH_SETTINGS), "Bluetooth settings not accessible")
                }
            ),
            SystemToolItem(
                id = "battery",
                title = "Battery & Power",
                category = "System",
                description = "Monitor battery usage & power saver modes",
                icon = Icons.Default.BatteryChargingFull,
                launchAction = { ctx ->
                    safeStartIntent(ctx, Intent(Intent.ACTION_POWER_USAGE_SUMMARY), "Battery settings not accessible")
                }
            ),
            SystemToolItem(
                id = "display",
                title = "Display & Theme",
                category = "System",
                description = "Screen brightness, dark mode & font scale",
                icon = Icons.Default.Wallpaper,
                launchAction = { ctx ->
                    safeStartIntent(ctx, Intent(Settings.ACTION_DISPLAY_SETTINGS), "Display settings not accessible")
                }
            ),
            SystemToolItem(
                id = "sound",
                title = "Sound & Volume",
                category = "System",
                description = "Media volume, ringers, alerts & equalizer",
                icon = Icons.Default.VolumeUp,
                launchAction = { ctx ->
                    safeStartIntent(ctx, Intent(Settings.ACTION_SOUND_SETTINGS), "Sound settings not accessible")
                }
            ),
            SystemToolItem(
                id = "browser",
                title = "Web Browser",
                category = "Internet",
                description = "Explore the web, search and browse bookmarks",
                icon = Icons.Default.Language,
                launchAction = { ctx ->
                    safeStartIntent(ctx, Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")), "Browser not found")
                }
            ),
            SystemToolItem(
                id = "calculator",
                title = "Calculator",
                category = "Tools",
                description = "Math, percentages and formulas",
                icon = Icons.Default.Calculate,
                launchAction = { ctx ->
                    val calcIntent = Intent().apply {
                        action = Intent.ACTION_MAIN
                        addCategory(Intent.CATEGORY_APP_CALCULATOR)
                    }
                    safeStartIntent(ctx, calcIntent, "Calculator app not found")
                }
            ),
            SystemToolItem(
                id = "clock",
                title = "Clock & Alarms",
                category = "Tools",
                description = "Alarms, world clock, stopwatch and timer",
                icon = Icons.Default.Schedule,
                launchAction = { ctx ->
                    safeStartIntent(ctx, Intent(AlarmClock.ACTION_SHOW_ALARMS), "Clock app not found")
                }
            ),
            SystemToolItem(
                id = "calendar",
                title = "Calendar",
                category = "Productivity",
                description = "Schedule meetings, reminders & events",
                icon = Icons.Default.CalendarMonth,
                launchAction = { ctx ->
                    val uri = Uri.parse("content://com.android.calendar/time")
                    safeStartIntent(ctx, Intent(Intent.ACTION_VIEW, uri), "Calendar not found")
                }
            ),
            SystemToolItem(
                id = "files",
                title = "Device Files",
                category = "Tools",
                description = "Manage local downloads, docs & storage",
                icon = Icons.Default.Folder,
                launchAction = { ctx ->
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "*/*"
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }
                    safeStartIntent(ctx, intent, "Files browser not found")
                }
            ),
            SystemToolItem(
                id = "music",
                title = "Music & Audio",
                category = "Media",
                description = "Play music, tracks and audio files",
                icon = Icons.Default.MusicNote,
                launchAction = { ctx ->
                    val intent = Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER)
                    safeStartIntent(ctx, intent, "Music player not found")
                }
            ),
            SystemToolItem(
                id = "maps",
                title = "Maps & Navigation",
                category = "Navigation",
                description = "GPS navigation, transit and local spots",
                icon = Icons.Default.Map,
                launchAction = { ctx ->
                    val uri = Uri.parse("geo:0,0?q=")
                    safeStartIntent(ctx, Intent(Intent.ACTION_VIEW, uri), "Maps app not found")
                }
            ),
            SystemToolItem(
                id = "phone",
                title = "Phone",
                category = "Communication",
                description = "Call phone numbers & voice keypad",
                icon = Icons.Default.Call,
                launchAction = { ctx ->
                    safeStartIntent(ctx, Intent(Intent.ACTION_DIAL), "Phone dialer not found")
                }
            ),
            SystemToolItem(
                id = "messages",
                title = "Messages",
                category = "Communication",
                description = "Send and receive text messages",
                icon = Icons.Default.Message,
                launchAction = { ctx ->
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"))
                    safeStartIntent(ctx, intent, "Messaging app not found")
                }
            ),
            SystemToolItem(
                id = "email",
                title = "Email",
                category = "Communication",
                description = "Read & compose emails",
                icon = Icons.Default.Email,
                launchAction = { ctx ->
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
                    safeStartIntent(ctx, intent, "Email app not found")
                }
            ),
            SystemToolItem(
                id = "contacts",
                title = "Contacts",
                category = "Communication",
                description = "Address book, friends and team directory",
                icon = Icons.Default.Contacts,
                launchAction = { ctx ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people"))
                    safeStartIntent(ctx, intent, "Contacts app not found")
                }
            )
        )
    }

    fun getInstalledApps(context: Context): List<InstalledAppItem> {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolved = try {
            pm.queryIntentActivities(mainIntent, 0)
        } catch (_: Exception) {
            emptyList()
        }

        val myPackageName = context.packageName
        return resolved
            .filter { it.activityInfo.packageName != myPackageName }
            .map { resolveInfo ->
                InstalledAppItem(
                    name = resolveInfo.loadLabel(pm).toString(),
                    packageName = resolveInfo.activityInfo.packageName,
                    icon = try { resolveInfo.loadIcon(pm) } catch (_: Exception) { null }
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.name }
    }

    fun launchAppByPackage(context: Context, packageName: String): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        return if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
                true
            } catch (e: Exception) {
                Toast.makeText(context, "Could not launch app: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                false
            }
        } else {
            Toast.makeText(context, "App not available on this device", Toast.LENGTH_SHORT).show()
            false
        }
    }
}
