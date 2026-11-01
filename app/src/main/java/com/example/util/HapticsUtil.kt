package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Haptic feedback utility providing tactile responses for the 1D World sparkle collection,
 * concert jukebox, and smartwatch interactions using the Android VibrationEffect API.
 */
object HapticsUtil {

    private fun getVibrator(context: Context): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Subtle tactile tick for tapping sparkle items or browsing the collection.
     */
    fun performSparkleTick(context: Context) {
        val vibrator = getVibrator(context) ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(15L)
            }
        } catch (e: Exception) {
            // Silently fallback if device lacks vibrator hardware
        }
    }

    /**
     * Crisp tactile click for selecting a band member doorway or opening sparkle details.
     */
    fun performSparkleClick(context: Context) {
        val vibrator = getVibrator(context) ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(25L)
            }
        } catch (e: Exception) {
            // Silently fallback
        }
    }

    /**
     * Dual-pulse burst effect for collecting new sparkles or claiming daily wrist bonuses.
     */
    fun performSparkleBurst(context: Context) {
        val vibrator = getVibrator(context) ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 30, 40, 30)
                val amplitudes = intArrayOf(0, 160, 0, 220)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 30, 40, 30), -1)
            }
        } catch (e: Exception) {
            // Silently fallback
        }
    }

    /**
     * Subtle rhythmic beat for music playback controls on the concert jukebox.
     */
    fun performJukeboxBeat(context: Context) {
        val vibrator = getVibrator(context) ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(35L)
            }
        } catch (e: Exception) {
            // Silently fallback
        }
    }
}
