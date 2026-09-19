package com.example.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.ToneSynthGenerator
import com.example.model.ApkAnatomyItem
import com.example.model.GenerationState
import com.example.model.GenrePreset
import com.example.model.Track
import com.example.receiver.MusicControlReceiver
import com.example.service.MusicPlaybackService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppTab {
    GENERATOR,
    APK_STRUCTURE,
    WEB_PORTAL,
    TOOLS
}

class SunoViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentTab = MutableStateFlow(AppTab.GENERATOR)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    val genrePresets = listOf(
        GenrePreset(
            id = "synthwave",
            name = "Synthwave",
            emoji = "🌆",
            prompt = "Retro 80s analog synthwave, warm neon pads, driving bassline, arpeggiated lead",
            bpm = 124,
            styleTags = listOf("Electronic", "Retro", "Arp", "Stereo 96kHz"),
            baseFreq = 330f
        ),
        GenrePreset(
            id = "lofi",
            name = "Lo-Fi Beats",
            emoji = "☕",
            prompt = "Cozy lo-fi chillhop beats, vinyl crackle, mellow Rhodes electric piano, jazzy drums",
            bpm = 85,
            styleTags = listOf("Chill", "Coffee", "Jazz", "Warm Tape"),
            baseFreq = 261.63f
        ),
        GenrePreset(
            id = "cinematic",
            name = "Cinematic",
            emoji = "🎬",
            prompt = "Epic cinematic film trailer, colossal brass, rising strings, thunderous taiko percussion",
            bpm = 110,
            styleTags = listOf("Orchestral", "Epic", "Trailer", "Hybrid"),
            baseFreq = 220f
        ),
        GenrePreset(
            id = "afrobeats",
            name = "Afrobeats",
            emoji = "🌴",
            prompt = "Uplifting summer Afrobeats, log drum groove, smooth guitars, joyful melody",
            bpm = 105,
            styleTags = listOf("Dance", "Acoustic", "Groove", "Modern"),
            baseFreq = 392f
        ),
        GenrePreset(
            id = "rock",
            name = "Cyber Rock",
            emoji = "🎸",
            prompt = "Heavy futuristic industrial rock, distorted synths, driving electric guitar riffs",
            bpm = 140,
            styleTags = listOf("Heavy", "Industrial", "Distortion", "Cyber"),
            baseFreq = 293.66f
        )
    )

    private val _selectedPreset = MutableStateFlow(genrePresets[0])
    val selectedPreset: StateFlow<GenrePreset> = _selectedPreset.asStateFlow()

    private val _userPrompt = MutableStateFlow(genrePresets[0].prompt)
    val userPrompt: StateFlow<String> = _userPrompt.asStateFlow()

    private val _lyricsInput = MutableStateFlow(
        """[Verse 1]
Neon lights reflect upon the midnight rain,
Synthesizers humming down the digital lane.

[Chorus]
Take me through the waveform, into the sound,
Where human thoughts and AI rhythm are found!"""
    )
    val lyricsInput: StateFlow<String> = _lyricsInput.asStateFlow()

    private val _bpm = MutableStateFlow(124)
    val bpm: StateFlow<Int> = _bpm.asStateFlow()

    private val _generationMode = MutableStateFlow("PROMPT") // "PROMPT" or "LYRICS"
    val generationMode: StateFlow<String> = _generationMode.asStateFlow()

    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()

    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio: StateFlow<Boolean> = _isPlayingAudio.asStateFlow()

    private val _currentlyPlayingTrackId = MutableStateFlow<String?>(null)
    val currentlyPlayingTrackId: StateFlow<String?> = _currentlyPlayingTrackId.asStateFlow()

    private val _trackHistory = MutableStateFlow<List<Track>>(
        listOf(
            Track(
                id = "suno-001",
                title = "Midnight Horizon (V6 Ultra)",
                genre = "Synthwave",
                bpm = 124,
                durationFormatted = "2:34",
                prompt = "Retro 80s analog synthwave, warm neon pads",
                styleTags = listOf("V6 Model", "Stereo", "124 BPM"),
                baseFreq = 330f
            ),
            Track(
                id = "suno-002",
                title = "Rainy Day Study Chill",
                genre = "Lo-Fi Beats",
                bpm = 85,
                durationFormatted = "3:10",
                prompt = "Cozy lo-fi chillhop beats with vinyl crackle",
                styleTags = listOf("V6 Lo-Fi", "Mellow", "Tape Warmth"),
                baseFreq = 261.63f
            ),
            Track(
                id = "suno-003",
                title = "Chronicles of Olympus",
                genre = "Cinematic",
                bpm = 110,
                durationFormatted = "2:52",
                prompt = "Epic cinematic film trailer, colossal brass",
                styleTags = listOf("Orchestral", "Cinematic", "96kHz"),
                baseFreq = 220f
            )
        )
    )
    val trackHistory: StateFlow<List<Track>> = _trackHistory.asStateFlow()

    // Service state from background service
    val isServiceRunning: StateFlow<Boolean> = MusicPlaybackService.isServiceRunning
    val currentTrackTitle: StateFlow<String> = MusicPlaybackService.currentTrackTitle
    val lastReceivedBroadcast: StateFlow<String> = MusicControlReceiver.lastReceivedAction

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun selectPreset(preset: GenrePreset) {
        _selectedPreset.value = preset
        _userPrompt.value = preset.prompt
        _bpm.value = preset.bpm
    }

    fun setPrompt(text: String) {
        _userPrompt.value = text
    }

    fun setLyrics(text: String) {
        _lyricsInput.value = text
    }

    fun setBpm(newBpm: Int) {
        _bpm.value = newBpm
    }

    fun setGenerationMode(mode: String) {
        _generationMode.value = mode
    }

    fun generateMusic() {
        if (_generationState.value is GenerationState.Generating) return

        viewModelScope.launch {
            val stages = listOf(
                "Analyzing musical prompt & semantics...",
                "Synthesizing melodic progression & chord stems...",
                "Arranging drums, bass, and stereo spatialization...",
                "Applying Suno V6 Ultra-Fidelity mastering pass..."
            )

            for (i in stages.indices) {
                _generationState.value = GenerationState.Generating(
                    progress = (i + 1) / (stages.size.toFloat()),
                    stage = stages[i]
                )
                delay(650)
            }

            val preset = _selectedPreset.value
            val newTrack = Track(
                id = "suno-${System.currentTimeMillis() % 10000}",
                title = if (_generationMode.value == "LYRICS") "AI Song: " + _lyricsInput.value.lines().firstOrNull { it.isNotBlank() && !it.startsWith("[") }?.take(22) ?: "AI Masterpiece" else "AI Symphony in ${preset.name}",
                genre = preset.name,
                bpm = _bpm.value,
                durationFormatted = "2:45",
                prompt = _userPrompt.value,
                lyrics = if (_generationMode.value == "LYRICS") _lyricsInput.value else "",
                styleTags = listOf("Suno V6.0", "${_bpm.value} BPM", preset.name),
                baseFreq = preset.baseFreq
            )

            _trackHistory.value = listOf(newTrack) + _trackHistory.value
            _generationState.value = GenerationState.Completed(newTrack)

            // Auto-play preview
            playTrack(newTrack)
        }
    }

    fun playTrack(track: Track) {
        if (_currentlyPlayingTrackId.value == track.id && _isPlayingAudio.value) {
            stopAudio()
            return
        }

        ToneSynthGenerator.playTrackMelody(
            baseFreq = track.baseFreq,
            bpm = track.bpm,
            scope = viewModelScope
        ) { playing ->
            _isPlayingAudio.value = playing
            if (!playing && _currentlyPlayingTrackId.value == track.id) {
                _currentlyPlayingTrackId.value = null
            }
        }

        _currentlyPlayingTrackId.value = track.id
        _isPlayingAudio.value = true

        // Also start Android Foreground Service to showcase APK Service component
        MusicPlaybackService.start(
            getApplication(),
            track.title,
            track.genre
        )
    }

    fun stopAudio() {
        ToneSynthGenerator.stop()
        _isPlayingAudio.value = false
        _currentlyPlayingTrackId.value = null
        MusicPlaybackService.stop(getApplication())
    }

    fun triggerBroadcastTest(action: String) {
        val intent = Intent(action).apply {
            setPackage(getApplication<Application>().packageName)
        }
        getApplication<Application>().sendBroadcast(intent)
    }

    fun toggleServiceManual() {
        val context = getApplication<Application>()
        if (isServiceRunning.value) {
            MusicPlaybackService.stop(context)
        } else {
            MusicPlaybackService.start(context, "Suno V6 Manual Background Daemon", "Media Service")
        }
    }

    override fun onCleared() {
        stopAudio()
        super.onCleared()
    }

    val apkAnatomyData = listOf(
        ApkAnatomyItem(
            title = "MainActivity (Aktivitas)",
            componentType = "Activity",
            badge = "UI / Entry Point",
            description = "Komponen utama antarmuka pengguna (UI) yang dirender menggunakan Jetpack Compose. Menangani siklus hidup aktivitas (onCreate, onStart, onResume) dan interaksi pengguna secara langsung.",
            manifestDeclaration = """<activity
    android:name=".MainActivity"
    android:exported="true"
    android:theme="@style/Theme.SunoV6">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>""",
            codeSnippet = """class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SunoTheme { SunoV6MainScreen() }
        }
    }
}""",
            liveStatus = "Active (Running Foreground)"
        ),
        ApkAnatomyItem(
            title = "MusicPlaybackService (Layanan)",
            componentType = "Foreground Service",
            badge = "Background Audio",
            description = "Komponen layanan Android (Service) yang menjalankan pemutaran audio di latar belakang tanpa terganggu saat aplikasi diminimalkan. Menggunakan Foreground Service bertipe mediaPlayback lengkap dengan Notification Channel.",
            manifestDeclaration = """<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />

<service
    android:name=".service.MusicPlaybackService"
    android:enabled="true"
    android:exported="false"
    android:foregroundServiceType="mediaPlayback" />""",
            codeSnippet = """class MusicPlaybackService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        return START_NOT_STICKY
    }
}""",
            liveStatus = "Ready / Dynamic Toggleable"
        ),
        ApkAnatomyItem(
            title = "MusicControlReceiver (Penerima Siaran)",
            componentType = "BroadcastReceiver",
            badge = "Event Listener",
            description = "Merespons siaran sistem (seperti headset dicabut/dipasang) atau Intent siaran kustom dari kontrol audio luar untuk mengontrol pemutaran musik secara asynchronous.",
            manifestDeclaration = """<receiver
    android:name=".receiver.MusicControlReceiver"
    android:enabled="true"
    android:exported="false" />""",
            codeSnippet = """class MusicControlReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_MEDIA_PLAY -> MusicPlaybackService.start(...)
            ACTION_MEDIA_PAUSE -> MusicPlaybackService.stop(...)
        }
    }
}""",
            liveStatus = "Registered & Listening"
        ),
        ApkAnatomyItem(
            title = "AndroidManifest.xml (Konfigurasi Manifes)",
            componentType = "Manifest Configuration",
            badge = "Application Blueprint",
            description = "File konfigurasi utama yang mendefinisikan identitas paket aplikasi, izin keamanan (Permissions), daftar seluruh Aktivitas, Layanan, Penerima Siaran, Provider, serta tema visual dan konfigurasi cadangan.",
            manifestDeclaration = """<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    ...
</manifest>""",
            codeSnippet = """// Android OS membaca file ini saat instalasi APK:
1. Validasi Izin & Keamanan Sistem
2. Registrasi Peluncur (Launcher Icon)
3. Alokasi Tipe Layanan Latar Belakang""",
            liveStatus = "Configured (API 36 / Min 24)"
        )
    )
}
