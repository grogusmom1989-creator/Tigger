package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Centralized Real Audio & Music Engine for the entire app.
 * Powers:
 *  - 1D Hit Jukebox & Lyric Blender (OneDirectionHubScreen)
 *  - Universal Flow Music & Google Apps Importer (FlowMusicGoogleImporterSection)
 *  - KikiVerse Music Player (KikiVerseScreen)
 *  - Smartwatch Concert Jukebox & Wear OS Voice Tile (SmartWatchStudioScreen / WearOsCompanionManager)
 *  - 1D Iconic Soundboard (OneDirectionSoundboard)
 *  - Pastel Sparkle Doorways Gentle Voice Catchphrases (OneDirectionDoorwaysScreen)
 *
 * Uses:
 *  1. Android MediaPlayer for real imported local / Google Drive audio files (MP3/WAV/FLAC/M4A/OGG).
 *  2. Real-time polyphonic 44.1kHz 16-bit PCM AudioTrack synthesizer (Lead Melody + Chord Pad + Bassline + Drum Beat)
 *     with distinct song arrangements for every 1D hit and Flow Music stem.
 *  3. Android TextToSpeech (British/Irish locale preference) + harmonic chime cadence for all voice catchphrases.
 */
object MusicAudioEngine {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _activeTrackId = MutableStateFlow("track_wmyb")
    val activeTrackId: StateFlow<String> = _activeTrackId.asStateFlow()

    private val _activeTrackTitle = MutableStateFlow("What Makes You Beautiful")
    val activeTrackTitle: StateFlow<String> = _activeTrackTitle.asStateFlow()

    private val _activeSourceLabel = MutableStateFlow("1D Jukebox • Up All Night")
    val activeSourceLabel: StateFlow<String> = _activeSourceLabel.asStateFlow()

    private val _activeCatchphrase = MutableStateFlow<String?>(null)
    val activeCatchphrase: StateFlow<String?> = _activeCatchphrase.asStateFlow()

    private var synthJob: Job? = null
    private var catchphraseJob: Job? = null
    private var activeAudioTrack: AudioTrack? = null
    private var mediaPlayer: MediaPlayer? = null

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private data class SongPattern(
        val bpm: Int,
        val melodyFreqs: DoubleArray,
        val bassFreqs: DoubleArray,
        val padHarmonicRatio: Double = 1.25,
        val hasPopDrums: Boolean = true
    )

    /**
     * Plays or switches to the specified track with real audible output.
     */
    fun playTrack(
        context: Context,
        trackId: String,
        title: String,
        sourceLabel: String = "Flow Music & 1D Jukebox",
        localUriString: String? = null
    ) {
        stopInternal()

        _activeTrackId.value = trackId
        _activeTrackTitle.value = title
        _activeSourceLabel.value = sourceLabel
        _isPlaying.value = true

        // 1. If a real local or Google Drive file URI was provided, play via MediaPlayer first
        if (!localUriString.isNullOrBlank()) {
            try {
                val mp = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setDataSource(context.applicationContext, Uri.parse(localUriString))
                    isLooping = true
                    setOnPreparedListener { player ->
                        player.start()
                    }
                    setOnErrorListener { _, _, _ ->
                        startPolyphonicSynthLoop(trackId, title)
                        true
                    }
                    prepareAsync()
                }
                mediaPlayer = mp
                return
            } catch (_: Exception) {
                // Fall back to polyphonic synth loop if URI is virtual or inaccessible
            }
        }

        // 2. Real-time polyphonic AudioTrack music arrangement loop
        startPolyphonicSynthLoop(trackId, title)
    }

    /**
     * Toggles play/pause for a given track.
     */
    fun toggleTrack(
        context: Context,
        trackId: String,
        title: String,
        sourceLabel: String = "Flow Music & 1D Jukebox",
        localUriString: String? = null
    ) {
        if (_isPlaying.value && (_activeTrackId.value == trackId || _activeTrackTitle.value.equals(title, ignoreCase = true))) {
            pause()
        } else {
            playTrack(context, trackId, title, sourceLabel, localUriString)
        }
    }

    /**
     * Pauses current music playback.
     */
    fun pause() {
        _isPlaying.value = false
        stopInternal()
    }

    /**
     * Stops all music playback.
     */
    fun stop() {
        _isPlaying.value = false
        stopInternal()
    }

    private fun stopInternal() {
        synthJob?.cancel()
        synthJob = null
        try {
            activeAudioTrack?.pause()
            activeAudioTrack?.flush()
            activeAudioTrack?.release()
        } catch (_: Exception) {
        }
        activeAudioTrack = null

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
        mediaPlayer = null
    }

    /**
     * Plays a spoken catchphrase + member musical signature jingle for Soundboard, Doorways & Watch Tile.
     */
    fun playCatchphrase(
        context: Context,
        speakerName: String,
        phrase: String
    ) {
        _activeCatchphrase.value = "$speakerName: \"$phrase\""

        // 1. Trigger Android TextToSpeech for real spoken voice
        ensureTtsInitialized(context.applicationContext) {
            try {
                // Adjust pitch/rate slightly per member for personality
                val lowerSpeaker = speakerName.lowercase()
                val pitch = when {
                    lowerSpeaker.contains("harry") -> 0.92f
                    lowerSpeaker.contains("zayn") -> 1.08f
                    lowerSpeaker.contains("louis") -> 1.05f
                    lowerSpeaker.contains("niall") -> 1.02f
                    lowerSpeaker.contains("liam") -> 0.96f
                    else -> 1.0f
                }
                tts?.setPitch(pitch)
                tts?.setSpeechRate(1.02f)
                tts?.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, "catchphrase_${System.currentTimeMillis()}")
            } catch (_: Exception) {
            }
        }

        // 2. Always play an expressive musical vocal-synth cadence via AudioTrack so sound is immediate and guaranteed
        catchphraseJob?.cancel()
        catchphraseJob = scope.launch {
            try {
                val sampleRate = 44100
                val notes = getMemberSignatureNotes(speakerName, phrase)
                val noteDurationSec = 0.16
                val samplesPerNote = (sampleRate * noteDurationSec).toInt()
                val totalSamples = samplesPerNote * notes.size
                val pcm = ShortArray(totalSamples)

                for (nIdx in notes.indices) {
                    val freq = notes[nIdx]
                    val startSample = nIdx * samplesPerNote
                    for (i in 0 until samplesPerNote) {
                        val t = i.toDouble() / sampleRate
                        val progress = i.toDouble() / samplesPerNote
                        val attack = (progress * 10.0).coerceAtMost(1.0)
                        val decay = exp(-2.8 * progress)
                        val env = attack * decay

                        val vibrato = 1.0 + 0.004 * sin(2.0 * PI * 6.0 * t)
                        val wave = 0.55 * sin(2.0 * PI * freq * vibrato * t) +
                            0.28 * sin(2.0 * PI * freq * 1.5 * t) +
                            0.17 * sin(2.0 * PI * freq * 2.0 * t)

                        pcm[startSample + i] = (wave * env * 22000).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                            .toShort()
                    }
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(pcm.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcm, 0, pcm.size)
                track.play()

                kotlinx.coroutines.delay((totalSamples * 1000L / sampleRate) + 180L)
                track.stop()
                track.release()
            } catch (_: Exception) {
            }
        }
    }

    private fun ensureTtsInitialized(appContext: Context, onReady: () -> Unit) {
        if (tts != null && isTtsReady) {
            onReady()
            return
        }
        if (tts == null) {
            try {
                tts = TextToSpeech(appContext) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        isTtsReady = true
                        val ukResult = tts?.setLanguage(Locale.UK)
                        if (ukResult == TextToSpeech.LANG_MISSING_DATA || ukResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                            tts?.setLanguage(Locale.US)
                        }
                        onReady()
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun startPolyphonicSynthLoop(trackId: String, title: String) {
        synthJob?.cancel()
        synthJob = scope.launch {
            val sampleRate = 44100
            val pattern = getSongPattern(trackId, title)
            val stepSec = 60.0 / pattern.bpm / 2.0 // Eighth-note steps
            val samplesPerStep = (sampleRate * stepSec).toInt().coerceAtLeast(2048)

            val minBuf = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(samplesPerStep * 2)

            val audioTrack = try {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBuf * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            } catch (_: Exception) {
                null
            } ?: return@launch

            activeAudioTrack = audioTrack
            try {
                audioTrack.play()
                val stepBuffer = ShortArray(samplesPerStep)
                var stepIndex = 0

                while (isActive && _isPlaying.value) {
                    val leadFreq = pattern.melodyFreqs[stepIndex % pattern.melodyFreqs.size]
                    val bassFreq = pattern.bassFreqs[(stepIndex / 2) % pattern.bassFreqs.size]
                    val isKickStep = pattern.hasPopDrums && (stepIndex % 4 == 0)
                    val isSnareStep = pattern.hasPopDrums && (stepIndex % 4 == 2)
                    val isHatStep = pattern.hasPopDrums && (stepIndex % 2 == 1)

                    for (i in 0 until samplesPerStep) {
                        val t = i.toDouble() / sampleRate
                        val progress = i.toDouble() / samplesPerStep

                        // Lead synth envelope with gentle vibrato
                        val attack = (progress * 14.0).coerceAtMost(1.0)
                        val decay = exp(-2.2 * progress)
                        val leadEnv = attack * decay
                        val vibrato = 1.0 + 0.003 * sin(2.0 * PI * 5.5 * t)

                        val leadSignal = if (leadFreq > 0.0) {
                            (0.45 * sin(2.0 * PI * leadFreq * vibrato * t) +
                                0.22 * sin(2.0 * PI * leadFreq * pattern.padHarmonicRatio * t) +
                                0.14 * sin(2.0 * PI * leadFreq * 2.0 * t)) * leadEnv
                        } else {
                            0.0
                        }

                        // Warm sub-bass signal
                        val bassEnv = exp(-1.6 * progress)
                        val bassSignal = 0.30 * sin(2.0 * PI * bassFreq * t) * bassEnv

                        // Pop percussion synthesis
                        var drumSignal = 0.0
                        if (isKickStep && progress < 0.35) {
                            val kickFreq = 130.0 * exp(-12.0 * progress) + 42.0
                            drumSignal += 0.35 * sin(2.0 * PI * kickFreq * t) * exp(-8.0 * progress)
                        }
                        if (isSnareStep && progress < 0.25) {
                            val pseudoNoise = sin(2.0 * PI * 1730.0 * t * (1.0 + sin(2.0 * PI * 410.0 * t)))
                            drumSignal += 0.18 * pseudoNoise * exp(-14.0 * progress)
                        }
                        if (isHatStep && progress < 0.12) {
                            val hatWave = sin(2.0 * PI * 5400.0 * t + sin(2.0 * PI * 3100.0 * t))
                            drumSignal += 0.08 * hatWave * exp(-25.0 * progress)
                        }

                        val mixed = (leadSignal + bassSignal + drumSignal).coerceIn(-0.95, 0.95)
                        stepBuffer[i] = (mixed * 24500).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                            .toShort()
                    }

                    val written = audioTrack.write(stepBuffer, 0, stepBuffer.size)
                    if (written < 0) break
                    stepIndex++
                }
            } catch (_: Exception) {
            } finally {
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {
                }
            }
        }
    }

    /**
     * Returns distinct musical arrangements (BPM, lead melody notes in Hz, bassline in Hz)
     * for every 1D track, Flow Music stem, and KikiVerse song.
     */
    private fun getSongPattern(trackId: String, title: String): SongPattern {
        val key = "$trackId ${title.lowercase()}"
        return when {
            // What Makes You Beautiful: Upbeat E Major guitar riff & chorus hook (125 BPM)
            key.contains("wmyb") || key.contains("beautiful") -> SongPattern(
                bpm = 126,
                melodyFreqs = doubleArrayOf(
                    329.63, 329.63, 370.00, 415.30, 440.00, 493.88, 440.00, 370.00, // E4 E4 F#4 G#4 A4 B4 A4 F#4
                    659.25, 587.33, 493.88, 440.00, 415.30, 440.00, 493.88, 329.63  // E5 D#5 B4 A4 G#4 A4 B4 E4
                ),
                bassFreqs = doubleArrayOf(164.81, 164.81, 220.00, 246.94) // E3 -> A3 -> B3
            )

            // Night Changes: Dreamy acoustic/synth arpeggio in Ab / G Major (120 BPM)
            key.contains("night_changes") || key.contains("night changes") -> SongPattern(
                bpm = 112,
                melodyFreqs = doubleArrayOf(
                    392.00, 440.00, 493.88, 587.33, 523.25, 493.88, 440.00, 392.00, // G4 A4 B4 D5 C5 B4 A4 G4
                    329.63, 392.00, 493.88, 587.33, 659.25, 587.33, 493.88, 440.00  // E4 G4 B4 D5 E5 D5 B4 A4
                ),
                bassFreqs = doubleArrayOf(196.00, 130.81, 164.81, 146.83), // G3 -> C3 -> E3 -> D3
                padHarmonicRatio = 1.5,
                hasPopDrums = false
            )

            // Story of My Life: Folk-pop driving acoustic pluck in F/C Major (121 BPM)
            key.contains("story") -> SongPattern(
                bpm = 121,
                melodyFreqs = doubleArrayOf(
                    440.00, 523.25, 587.33, 659.25, 587.33, 523.25, 440.00, 392.00,
                    349.23, 440.00, 523.25, 587.33, 523.25, 440.00, 392.00, 349.23
                ),
                bassFreqs = doubleArrayOf(220.00, 174.61, 130.81, 196.00) // Am -> F -> C -> G
            )

            // Best Song Ever: High-energy glam-pop dance anthem (118 BPM)
            key.contains("best_song") || key.contains("best song") -> SongPattern(
                bpm = 128,
                melodyFreqs = doubleArrayOf(
                    554.37, 554.37, 659.25, 554.37, 493.88, 440.00, 493.88, 554.37,
                    659.25, 739.99, 659.25, 554.37, 493.88, 440.00, 370.00, 440.00
                ),
                bassFreqs = doubleArrayOf(185.00, 220.00, 246.94, 246.94)
            )

            // Drag Me Down: Synth-rock stadium groove in C minor (138 BPM)
            key.contains("drag_me_down") || key.contains("drag me down") -> SongPattern(
                bpm = 136,
                melodyFreqs = doubleArrayOf(
                    523.25, 466.16, 392.00, 466.16, 523.25, 587.33, 622.25, 587.33,
                    523.25, 466.16, 392.00, 349.23, 392.00, 466.16, 523.25, 392.00
                ),
                bassFreqs = doubleArrayOf(130.81, 155.56, 174.61, 196.00) // Cm -> Eb -> F -> G
            )

            // Steal My Girl: Piano stadium pop (77/154 BPM)
            key.contains("steal_my_girl") || key.contains("steal my girl") -> SongPattern(
                bpm = 132,
                melodyFreqs = doubleArrayOf(
                    466.16, 587.33, 698.46, 622.25, 587.33, 523.25, 466.16, 392.00,
                    466.16, 587.33, 783.99, 698.46, 587.33, 523.25, 466.16, 587.33
                ),
                bassFreqs = doubleArrayOf(116.54, 155.56, 196.00, 174.61) // Bb -> Eb -> Gm -> F
            )

            // Little Things: Gentle fingerpicked acoustic ballad
            key.contains("little_things") || key.contains("little things") -> SongPattern(
                bpm = 104,
                melodyFreqs = doubleArrayOf(
                    392.00, 493.88, 587.33, 659.25, 587.33, 493.88, 440.00, 392.00,
                    329.63, 392.00, 440.00, 493.88, 440.00, 392.00, 329.63, 293.66
                ),
                bassFreqs = doubleArrayOf(196.00, 146.83, 164.81, 130.81),
                hasPopDrums = false
            )

            // History: Clap-along acoustic anthem
            key.contains("history") -> SongPattern(
                bpm = 118,
                melodyFreqs = doubleArrayOf(
                    440.00, 493.88, 554.37, 659.25, 554.37, 493.88, 440.00, 554.37,
                    659.25, 739.99, 659.25, 554.37, 493.88, 440.00, 493.88, 440.00
                ),
                bassFreqs = doubleArrayOf(220.00, 164.81, 185.00, 146.83)
            )

            // Flow Music / Imported Stems: Shimmering futuristic synthwave & vocal harmony
            else -> SongPattern(
                bpm = 124,
                melodyFreqs = doubleArrayOf(
                    523.25, 659.25, 783.99, 987.77, 783.99, 659.25, 587.33, 783.99,
                    587.33, 698.46, 880.00, 1046.50, 880.00, 698.46, 659.25, 523.25
                ),
                bassFreqs = doubleArrayOf(130.81, 164.81, 196.00, 220.00),
                padHarmonicRatio = 1.5
            )
        }
    }

    private fun getMemberSignatureNotes(speakerName: String, phrase: String): DoubleArray {
        val lower = speakerName.lowercase()
        val seed = (phrase.hashCode() and 0x7FFFFFFF) % 4
        return when {
            lower.contains("zayn") -> doubleArrayOf(392.00, 493.88, 587.33, 783.99, 659.25 + seed * 15)
            lower.contains("louis") -> doubleArrayOf(329.63, 440.00, 554.37, 659.25, 587.33 + seed * 15)
            lower.contains("niall") -> doubleArrayOf(293.66, 369.99, 440.00, 587.33, 659.25 + seed * 15)
            lower.contains("liam") -> doubleArrayOf(261.63, 329.63, 392.00, 523.25, 659.25 + seed * 15)
            lower.contains("harry") -> doubleArrayOf(349.23, 440.00, 523.25, 698.46, 880.00 + seed * 15)
            else -> doubleArrayOf(440.00, 554.37, 659.25, 880.00, 1046.50)
        }
    }
}
