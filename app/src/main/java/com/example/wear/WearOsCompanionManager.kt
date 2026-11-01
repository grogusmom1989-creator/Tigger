package com.example.wear

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WristMemberBadge(
    val id: String,
    val memberName: String,
    val iconEmoji: String,
    val badgeTitle: String,
    val colorHex: String,
    val unlocked: Boolean = true
)

data class WristJukeboxTrack(
    val id: String,
    val title: String,
    val album: String,
    val year: String,
    val durationText: String,
    val iconicLyric: String,
    val themeColorHex: String
)

data class WearOsWatchInfo(
    val modelName: String = "Samsung Galaxy Watch 7 (44mm)",
    val osVersion: String = "Wear OS 5.0 (One UI Watch 6.0)",
    val connectionType: String = "Bluetooth Low Energy (BLE) & Wi-Fi",
    val batteryPercent: Int = 88,
    val isConnected: Boolean = true,
    val lastSyncTimestamp: String = "Just now"
)

val defaultWristTracks = listOf(
    WristJukeboxTrack("w_wmyb", "What Makes You Beautiful", "Up All Night", "2011", "3:18", "Baby you light up my world like nobody else", "#FF007F"),
    WristJukeboxTrack("w_story", "Story of My Life", "Midnight Memories", "2013", "4:05", "I drive all night to keep her warm and time is frozen", "#FF9248"),
    WristJukeboxTrack("w_night", "Night Changes", "FOUR", "2014", "3:46", "We're only getting older baby, and I've been thinking about it lately", "#9A4BFF"),
    WristJukeboxTrack("w_best", "Best Song Ever", "Midnight Memories", "2013", "3:20", "And we danced all night to the best song ever", "#00F5D4"),
    WristJukeboxTrack("w_drag", "Drag Me Down", "Made in the A.M.", "2015", "3:12", "With your love, nobody can drag me down", "#14121E"),
    WristJukeboxTrack("w_steal", "Steal My Girl", "FOUR", "2014", "3:48", "Everybody wanna steal my girl, everybody wanna take her heart away", "#FEE440"),
    WristJukeboxTrack("w_history", "History", "Made in the A.M.", "2015", "3:07", "You and me got a whole lot of history!", "#B388FF")
)

val defaultWristBadges = listOf(
    WristMemberBadge("b_band", "One Direction", "👑", "Wembley Stadium Legend", "#E50914"),
    WristMemberBadge("b_liam", "Liam Payne", "🎤", "Golden Harmony Angel", "#64B5F6"),
    WristMemberBadge("b_harry", "Harry Styles", "🍓", "TPWK Kindness Heart", "#FF80AB"),
    WristMemberBadge("b_louis", "Louis Tomlinson", "🥕", "Doncaster Songwriting King", "#69F0AE"),
    WristMemberBadge("b_niall", "Niall Horan", "🎸", "Irish Gold Acoustic Soul", "#FFD54F"),
    WristMemberBadge("b_zayn", "Zayn Malik", "🌙", "Cosmic High Note Master", "#B388FF")
)

object WearOsCompanionManager {
    private val _watchInfo = MutableStateFlow(WearOsWatchInfo())
    val watchInfo: StateFlow<WearOsWatchInfo> = _watchInfo.asStateFlow()

    private val _wristSparks = MutableStateFlow(1450)
    val wristSparks: StateFlow<Int> = _wristSparks.asStateFlow()

    private val _wristBadges = MutableStateFlow(defaultWristBadges)
    val wristBadges: StateFlow<List<WristMemberBadge>> = _wristBadges.asStateFlow()

    private val _tracks = MutableStateFlow(defaultWristTracks)
    val tracks: StateFlow<List<WristJukeboxTrack>> = _tracks.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(0)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _wristVolume = MutableStateFlow(0.8f)
    val wristVolume: StateFlow<Float> = _wristVolume.asStateFlow()

    private val _stadiumAmbienceEnabled = MutableStateFlow(true)
    val stadiumAmbienceEnabled: StateFlow<Boolean> = _stadiumAmbienceEnabled.asStateFlow()

    private val _lastWristAction = MutableStateFlow("Wrist Companion Initialized")
    val lastWristAction: StateFlow<String> = _lastWristAction.asStateFlow()

    fun togglePlayPause() {
        _isPlaying.update { !it }
        _lastWristAction.value = if (_isPlaying.value) "Playing on Wrist: ${_tracks.value[_currentTrackIndex.value].title}" else "Paused Jukebox on Wrist"
    }

    fun nextTrack() {
        _currentTrackIndex.update { (it + 1) % _tracks.value.size }
        _isPlaying.value = true
        _lastWristAction.value = "Skipped to: ${_tracks.value[_currentTrackIndex.value].title}"
    }

    fun previousTrack() {
        _currentTrackIndex.update {
            if (it == 0) _tracks.value.size - 1 else it - 1
        }
        _isPlaying.value = true
        _lastWristAction.value = "Returned to: ${_tracks.value[_currentTrackIndex.value].title}"
    }

    fun setVolume(vol: Float) {
        _wristVolume.value = vol.coerceIn(0f, 1f)
    }

    fun toggleStadiumAmbience() {
        _stadiumAmbienceEnabled.update { !it }
        _lastWristAction.value = if (_stadiumAmbienceEnabled.value) "Stadium Crowd Ambience Active 🏟️" else "Stadium Ambience Muted"
    }

    fun mineWristSparkles(amount: Int = 5) {
        _wristSparks.update { it + amount }
        _lastWristAction.value = "+$amount Wrist Sparks Collected! ✨"
    }

    fun claimDailyWristBonus(): Boolean {
        _wristSparks.update { it + 100 }
        _lastWristAction.value = "Claimed +100 Daily Wrist Sparkle Bonus! 🎉"
        return true
    }

    fun syncWithPhoneSparks(phoneSparks: Int) {
        _wristSparks.value = phoneSparks
        _watchInfo.update { it.copy(lastSyncTimestamp = "Just now") }
        _lastWristAction.value = "Synced $phoneSparks Sparks with Phone DataLayer"
    }
}
