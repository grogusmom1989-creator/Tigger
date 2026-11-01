package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Real-time procedural audio synthesizer using Android's [AudioTrack].
 * Provides:
 * 1. Cosmetic Recipe Sound Effects (Chime, Fizz, Pop, Cosmic Harp, Prism Drop)
 * 2. Real-time Audio Shimmer Filter for Lab Melody Loops
 * 3. 1D Era-Specific Ambient Soundscapes
 * 4. 'Guess the Era' Melody Quiz Clips
 */
object SynthAudioEngine {
    private const val SAMPLE_RATE = 22050
    private var loopJob: Job? = null
    private var ambientJob: Job? = null

    /**
     * Plays a short procedural sound effect for KikiWorld Lab's Sound Design menu.
     */
    fun playRecipeSoundEffect(effectId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val durationSec = 0.65f
                val numSamples = (SAMPLE_RATE * durationSec).toInt()
                val samples = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toFloat() / SAMPLE_RATE
                    val env = exp(-4.5 * t).toFloat()
                    val raw = when (effectId) {
                        "sfx_chime" -> {
                            // Crystal harmonic chime (C6 + G6 + E7)
                            0.5f * sin(2.0 * PI * 1046.5 * t).toFloat() +
                                0.3f * sin(2.0 * PI * 1567.98 * t).toFloat() +
                                0.2f * sin(2.0 * PI * 2637.02 * t).toFloat()
                        }
                        "sfx_fizz" -> {
                            // Effervescent sparkle fizz (high-pass modulated noise + bell)
                            val noise = (Random.nextFloat() * 2f - 1f) * 0.55f
                            val sparkleTone = sin(2.0 * PI * (1800.0 + (i % 700)) * t).toFloat() * 0.45f
                            noise + sparkleTone
                        }
                        "sfx_pop" -> {
                            // Lip-gloss bubble pop (rapid pitch sweep upward + sub thump)
                            val freq = 220.0 + 980.0 * (1.0 - exp(-18.0 * t))
                            val popEnv = exp(-12.0 * t).toFloat()
                            sin(2.0 * PI * freq * t).toFloat() * popEnv * 1.4f
                        }
                        "sfx_harp" -> {
                            // Cosmic arpeggio glissando
                            val step = (t * 12).toInt().coerceIn(0, 5)
                            val notes = doubleArrayOf(523.25, 659.25, 783.99, 987.77, 1174.66, 1567.98)
                            val f = notes[step]
                            0.6f * sin(2.0 * PI * f * t).toFloat() +
                                0.3f * sin(2.0 * PI * f * 2.0 * t).toFloat()
                        }
                        "sfx_prism" -> {
                            // Holographic prism shimmer
                            val mod = sin(2.0 * PI * 14.0 * t)
                            0.55f * sin(2.0 * PI * (880.0 + 120.0 * mod) * t).toFloat() +
                                0.35f * sin(2.0 * PI * 1760.0 * t).toFloat()
                        }
                        else -> {
                            sin(2.0 * PI * 880.0 * t).toFloat()
                        }
                    }
                    val clamped = (raw * env).coerceIn(-1f, 1f)
                    samples[i] = (clamped * Short.MAX_VALUE * 0.55f).toInt().toShort()
                }
                playPcmBuffer(samples)
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Plays a short melody clip for the 'Guess the Era' quiz in 1D World Hub.
     */
    fun playEraMysteryClip(eraId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val notes = when (eraId) {
                    "Up All Night" -> doubleArrayOf(523.25, 587.33, 659.25, 783.99, 659.25, 783.99, 1046.50)
                    "Take Me Home" -> doubleArrayOf(440.0, 554.37, 659.25, 880.0, 783.99, 659.25, 554.37)
                    "Midnight Memories" -> doubleArrayOf(329.63, 392.00, 440.00, 493.88, 587.33, 493.88, 440.00)
                    "FOUR" -> doubleArrayOf(392.00, 493.88, 587.33, 783.99, 659.25, 587.33, 493.88)
                    "Made in the A.M." -> doubleArrayOf(349.23, 440.00, 523.25, 698.46, 659.25, 523.25, 440.00)
                    else -> doubleArrayOf(440.0, 554.37, 659.25, 880.0)
                }
                val noteDurationSec = 0.22f
                val totalSamples = (SAMPLE_RATE * noteDurationSec * notes.size).toInt()
                val samples = ShortArray(totalSamples)
                val samplesPerNote = (SAMPLE_RATE * noteDurationSec).toInt()

                for (nIdx in notes.indices) {
                    val freq = notes[nIdx]
                    val startSample = nIdx * samplesPerNote
                    for (j in 0 until samplesPerNote) {
                        val idx = startSample + j
                        if (idx >= totalSamples) break
                        val t = j.toFloat() / SAMPLE_RATE
                        val env = exp(-5.0 * t).toFloat()
                        val wave = 0.6f * sin(2.0 * PI * freq * t).toFloat() +
                            0.25f * sin(2.0 * PI * freq * 2.0 * t).toFloat() +
                            0.15f * sin(2.0 * PI * freq * 3.0 * t).toFloat()
                        samples[idx] = ((wave * env).coerceIn(-1f, 1f) * Short.MAX_VALUE * 0.5f).toInt().toShort()
                    }
                }
                playPcmBuffer(samples)
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Starts or updates the KikiWorld Lab melody loop with real-time high-frequency Shimmer Filter.
     */
    fun startShimmerMelodyLoop(
        shimmerIntensity: Float,
        harmonicSparkle: Float,
        loopPatternIndex: Int
    ) {
        stopShimmerMelodyLoop()
        loopJob = CoroutineScope(Dispatchers.Default).launch {
            val patterns = listOf(
                doubleArrayOf(523.25, 659.25, 783.99, 987.77, 1046.50, 987.77, 783.99, 659.25),
                doubleArrayOf(440.00, 523.25, 659.25, 880.00, 783.99, 659.25, 587.33, 523.25),
                doubleArrayOf(587.33, 739.99, 880.00, 1174.66, 1046.50, 880.00, 739.99, 659.25)
            )
            val activeNotes = patterns[loopPatternIndex.coerceIn(0, patterns.lastIndex)]
            var step = 0
            while (isActive) {
                val baseFreq = activeNotes[step % activeNotes.size]
                val stepSamples = (SAMPLE_RATE * 0.24f).toInt()
                val buffer = ShortArray(stepSamples)
                for (i in 0 until stepSamples) {
                    val t = i.toFloat() / SAMPLE_RATE
                    val env = exp(-4.2 * t).toFloat()
                    // Base warm synth
                    val fundamental = sin(2.0 * PI * baseFreq * t).toFloat() * 0.45f
                    // High-frequency Shimmer Filter (adds +1 octave, +2 octave & crystalline air harmonics)
                    val octaveUp = sin(2.0 * PI * (baseFreq * 2.0) * t).toFloat() * (0.3f * shimmerIntensity)
                    val fifthHarmonic = sin(2.0 * PI * (baseFreq * 3.0) * t).toFloat() * (0.22f * shimmerIntensity)
                    val airSparkle = sin(2.0 * PI * (baseFreq * 4.0 + 12.0 * sin(2.0 * PI * 6.0 * t)) * t).toFloat() *
                        (0.25f * shimmerIntensity * harmonicSparkle)

                    val combined = (fundamental + octaveUp + fifthHarmonic + airSparkle) * env
                    buffer[i] = (combined.coerceIn(-1f, 1f) * Short.MAX_VALUE * 0.5f).toInt().toShort()
                }
                playPcmBuffer(buffer)
                step++
                delay(20L)
            }
        }
    }

    fun stopShimmerMelodyLoop() {
        loopJob?.cancel()
        loopJob = null
    }

    /**
     * Plays a gentle era-specific ambient soundscape pulse when toggled in 1D World Hub.
     */
    fun playEraAmbientPreview(eraTitle: String) {
        ambientJob?.cancel()
        ambientJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                val durationSec = 1.1f
                val numSamples = (SAMPLE_RATE * durationSec).toInt()
                val samples = ShortArray(numSamples)
                val rootFreq = when {
                    eraTitle.contains("Up All Night") -> 392.0 // Warm seaside G4 chord + ocean breeze
                    eraTitle.contains("Take Me Home") -> 440.0 // Cozy London A4 + soft raindrop chime
                    eraTitle.contains("Midnight") -> 329.63 // Electric E4 evening city hum
                    eraTitle.contains("FOUR") -> 523.25 // Starlight C5 acoustic glow
                    else -> 349.23 // Made in the A.M. F4 vinyl warmth
                }
                for (i in 0 until numSamples) {
                    val t = i.toFloat() / SAMPLE_RATE
                    val env = kotlin.math.sin(PI * (i.toDouble() / numSamples)).toFloat()
                    val pad = 0.4f * sin(2.0 * PI * rootFreq * t).toFloat() +
                        0.3f * sin(2.0 * PI * (rootFreq * 1.25) * t).toFloat() +
                        0.2f * sin(2.0 * PI * (rootFreq * 1.5) * t).toFloat()
                    val breeze = (Random.nextFloat() * 2f - 1f) * 0.06f
                    samples[i] = (((pad + breeze) * env).coerceIn(-1f, 1f) * Short.MAX_VALUE * 0.4f).toInt().toShort()
                }
                playPcmBuffer(samples)
            } catch (_: Exception) {
            }
        }
    }

    private fun playPcmBuffer(samples: ShortArray) {
        var audioTrack: AudioTrack? = null
        try {
            val byteCount = samples.size * 2
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(byteCount.coerceAtLeast(2048))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()
            val sleepMs = (samples.size * 1000L / SAMPLE_RATE) + 40L
            Thread.sleep(sleepMs)
        } catch (_: Exception) {
        } finally {
            try {
                audioTrack?.stop()
                audioTrack?.release()
            } catch (_: Exception) {
            }
        }
    }
}
