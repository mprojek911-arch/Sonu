package com.example.ui

import android.annotation.SuppressLint
import android.view.View
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.ApkAnatomyItem
import com.example.model.GenerationState
import com.example.model.GenrePreset
import com.example.model.Track
import com.example.receiver.MusicControlReceiver
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryNeon
import com.example.ui.theme.PrimaryNeonGlow
import com.example.ui.theme.SecondaryPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.sin

@Composable
fun SunoV6MainScreen(viewModel: SunoViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            SunoBottomNavigation(
                selectedTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.GENERATOR -> GeneratorScreen(viewModel)
                AppTab.APK_STRUCTURE -> ApkStructureScreen(viewModel)
                AppTab.WEB_PORTAL -> WebPortalScreen()
                AppTab.TOOLS -> ToolsScreen()
            }
        }
    }
}

@Composable
fun SunoBottomNavigation(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        contentColor = TextPrimary,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .testTag("suno_bottom_navigation")
    ) {
        NavigationBarItem(
            selected = selectedTab == AppTab.GENERATOR,
            onClick = { onTabSelected(AppTab.GENERATOR) },
            icon = { Icon(Icons.Default.GraphicEq, contentDescription = "Studio Generator") },
            label = { Text("Studio V6", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryNeon,
                selectedTextColor = PrimaryNeon,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = DarkSurfaceVariant
            ),
            modifier = Modifier.testTag("tab_generator")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.APK_STRUCTURE,
            onClick = { onTabSelected(AppTab.APK_STRUCTURE) },
            icon = { Icon(Icons.Default.Code, contentDescription = "APK Anatomy") },
            label = { Text("Struktur APK", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SecondaryPink,
                selectedTextColor = SecondaryPink,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = DarkSurfaceVariant
            ),
            modifier = Modifier.testTag("tab_apk_structure")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.WEB_PORTAL,
            onClick = { onTabSelected(AppTab.WEB_PORTAL) },
            icon = { Icon(Icons.Default.Public, contentDescription = "Web Portal") },
            label = { Text("Web Suno", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentCyan,
                selectedTextColor = AccentCyan,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = DarkSurfaceVariant
            ),
            modifier = Modifier.testTag("tab_web_portal")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.TOOLS,
            onClick = { onTabSelected(AppTab.TOOLS) },
            icon = { Icon(Icons.Default.Build, contentDescription = "Tools") },
            label = { Text("Tools", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentEmerald,
                selectedTextColor = AccentEmerald,
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted,
                indicatorColor = DarkSurfaceVariant
            ),
            modifier = Modifier.testTag("tab_tools")
        )
    }
}

@Composable
fun GeneratorScreen(viewModel: SunoViewModel) {
    val presets = viewModel.genrePresets
    val selectedPreset by viewModel.selectedPreset.collectAsState()
    val userPrompt by viewModel.userPrompt.collectAsState()
    val lyricsInput by viewModel.lyricsInput.collectAsState()
    val bpm by viewModel.bpm.collectAsState()
    val mode by viewModel.generationMode.collectAsState()
    val genState by viewModel.generationState.collectAsState()
    val isPlayingAudio by viewModel.isPlayingAudio.collectAsState()
    val playingTrackId by viewModel.currentlyPlayingTrackId.collectAsState()
    val tracks by viewModel.trackHistory.collectAsState()
    val isServiceRunning by viewModel.isServiceRunning.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("generator_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            // Header Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                PrimaryNeon.copy(alpha = 0.25f),
                                DarkBackground
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isPlayingAudio) AccentEmerald else PrimaryNeon)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ADA SYNPHONIA • SUNO V6",
                                    color = PrimaryNeonGlow,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "Ada Synphonia Music Studio",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Version badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                        ) {
                            Text(
                                text = "v6.0-ultra",
                                color = AccentCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    if (isServiceRunning) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.GraphicEq,
                                contentDescription = "Active Daemon",
                                tint = AccentEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Android Foreground Service Aktif (Background Playback)",
                                color = AccentEmerald,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .widthIn(max = 600.dp)
            ) {
                // Mode Selector: Prompt vs Lyrics
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (mode == "PROMPT") PrimaryNeon else Color.Transparent)
                            .clickable { viewModel.setGenerationMode("PROMPT") }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✨ Text-to-Music Prompt",
                            color = if (mode == "PROMPT") TextPrimary else TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (mode == "LYRICS") SecondaryPink else Color.Transparent)
                            .clickable { viewModel.setGenerationMode("LYRICS") }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✍️ Lyrics-to-Song",
                            color = if (mode == "LYRICS") TextPrimary else TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Genre Presets Horizontal List
                Text(
                    text = "Gaya & Genre Musik Suno V6:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    presets.forEach { preset ->
                        val isSelected = selectedPreset.id == preset.id
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryNeon.copy(alpha = 0.2f) else DarkSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) PrimaryNeon else DarkSurfaceBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.selectPreset(preset) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = preset.emoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = preset.name,
                                    color = if (isSelected) PrimaryNeonGlow else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Input Box (Prompt or Lyrics)
                if (mode == "PROMPT") {
                    OutlinedTextField(
                        value = userPrompt,
                        onValueChange = { viewModel.setPrompt(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_prompt"),
                        label = { Text("Deskripsikan musik impianmu (instrumen, vibe, mood)...") },
                        minLines = 3,
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = PrimaryNeon,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedLabelColor = PrimaryNeonGlow,
                            unfocusedLabelColor = TextMuted
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    OutlinedTextField(
                        value = lyricsInput,
                        onValueChange = { viewModel.setLyrics(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_lyrics"),
                        label = { Text("Tulis lirik lagu (dengan tag [Verse], [Chorus])...") },
                        minLines = 4,
                        maxLines = 7,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = SecondaryPink,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedLabelColor = SecondaryPink,
                            unfocusedLabelColor = TextMuted
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tempo / BPM Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tempo (BPM): $bpm BPM",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = when {
                            bpm < 95 -> "Slow & Mellow"
                            bpm < 125 -> "Moderate Groove"
                            else -> "Fast & Energetic"
                        },
                        color = AccentCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Slider(
                    value = bpm.toFloat(),
                    onValueChange = { viewModel.setBpm(it.toInt()) },
                    valueRange = 60f..180f,
                    steps = 24,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryNeon,
                        activeTrackColor = PrimaryNeon,
                        inactiveTrackColor = DarkSurfaceVariant
                    ),
                    modifier = Modifier.testTag("slider_bpm")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Generate Button
                Button(
                    onClick = { viewModel.generateMusic() },
                    enabled = genState !is GenerationState.Generating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_generate_music"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryNeon,
                        disabledContainerColor = DarkSurfaceVariant
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.GraphicEq,
                            contentDescription = "Generate",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (genState is GenerationState.Generating) "Menghasilkan Musik V6..." else "⚡ Generate Suno V6 Music",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Generation Progress Card
                AnimatedVisibility(visible = genState is GenerationState.Generating) {
                    if (genState is GenerationState.Generating) {
                        val state = genState as GenerationState.Generating
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryNeon.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = state.stage,
                                        color = PrimaryNeonGlow,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${(state.progress * 100).toInt()}%",
                                        color = AccentCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { state.progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = PrimaryNeon,
                                    trackColor = DarkSurfaceBorder
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Waveform Audio Visualizer (Active when music is playing)
                AudioWaveformVisualizer(isPlaying = isPlayingAudio)

                Spacer(modifier = Modifier.height(16.dp))

                // Section Header: Track History / Generated Tracks
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Lagu Hasil Sintesis AI:",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${tracks.size} Lagu Tersedia",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Track items
        items(tracks, key = { it.id }) { track ->
            TrackCard(
                track = track,
                isPlaying = isPlayingAudio && playingTrackId == track.id,
                onPlayToggle = { viewModel.playTrack(track) },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            )
        }
    }
}

@Composable
fun AudioWaveformVisualizer(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .testTag("waveform_visualizer"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isPlaying) AccentCyan.copy(alpha = 0.5f) else DarkSurfaceBorder)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val numBars = 32
                val barWidth = size.width / (numBars * 1.5f)
                val spacing = barWidth * 0.5f
                val centerY = size.height / 2f

                for (i in 0 until numBars) {
                    val x = i * (barWidth + spacing) + spacing
                    val amp = if (isPlaying) {
                        (sin((i * 0.35f + phase).toDouble()) * 0.45f + 0.55f).toFloat() * (size.height * 0.75f)
                    } else {
                        (sin(i * 0.2) * 0.15f + 0.2f).toFloat() * (size.height * 0.3f)
                    }

                    val color = when {
                        i < 10 -> PrimaryNeon
                        i < 22 -> SecondaryPink
                        else -> AccentCyan
                    }

                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, centerY - amp / 2),
                        size = Size(barWidth, amp.coerceAtLeast(4f)),
                        cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
                    )
                }
            }

            if (!isPlaying) {
                Text(
                    text = "▶ Tekan Play pada lagu untuk mendengarkan audio sintesis",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkBackground.copy(alpha = 0.85f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TrackCard(
    track: Track,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag("track_card_${track.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPlaying) PrimaryNeon else DarkSurfaceBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isPlaying) PrimaryNeon.copy(alpha = 0.25f) else DarkSurfaceVariant)
                        .border(
                            1.dp,
                            if (isPlaying) PrimaryNeon else DarkSurfaceBorder,
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = if (isPlaying) PrimaryNeonGlow else TextPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onPlayToggle() }
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = track.title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${track.genre} • ${track.bpm} BPM",
                            color = AccentCyan,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${track.durationFormatted}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Quick Play Button
            IconButton(
                onClick = onPlayToggle,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isPlaying) SecondaryPink else DarkSurfaceVariant)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = "Toggle Audio",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: APK STRUCTURE (Aktivitas, Layanan, Konfigurasi Manifes)
// -------------------------------------------------------------
@Composable
fun ApkStructureScreen(viewModel: SunoViewModel) {
    val items = viewModel.apkAnatomyData
    val isServiceRunning by viewModel.isServiceRunning.collectAsState()
    val lastBroadcast by viewModel.lastReceivedBroadcast.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("apk_structure_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                SecondaryPink.copy(alpha = 0.2f),
                                PrimaryNeon.copy(alpha = 0.15f)
                            )
                        )
                    )
                    .border(1.dp, SecondaryPink.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📱", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Arsitektur APK Android",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Aktivitas, Layanan (Service), & Konfigurasi Manifes",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aplikasi ini mendemonstrasikan secara nyata komponen pembentuk APK Android: MainActivity sebagai UI, MusicPlaybackService sebagai Background Daemon, MusicControlReceiver sebagai Broadcast Listener, dan AndroidManifest.xml sebagai deklarasi sentral sistem operasi.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Live Controls for Service and Broadcast
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Uji Coba Komponen Android Live:",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Status Background Service:",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = if (isServiceRunning) "RUNNING (Foreground Media)" else "STOPPED",
                                color = if (isServiceRunning) AccentEmerald else AccentAmber,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleServiceManual() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isServiceRunning) AccentAmber else AccentEmerald
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isServiceRunning) "Hentikan Service" else "Jalankan Service",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Broadcast test button
                    Text(
                        text = "Status Terakhir Broadcast Receiver: $lastBroadcast",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.triggerBroadcastTest(MusicControlReceiver.ACTION_MEDIA_PLAY) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                        ) {
                            Text("Kirim Broadcast Play", fontSize = 11.sp, color = TextPrimary)
                        }
                        Button(
                            onClick = { viewModel.triggerBroadcastTest(MusicControlReceiver.ACTION_MEDIA_PAUSE) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                        ) {
                            Text("Kirim Broadcast Pause", fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // List of APK components
        items(items) { item ->
            ApkComponentCard(item)
        }
    }
}

@Composable
fun ApkComponentCard(item: ApkAnatomyItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("apk_card_${item.componentType}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.title,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = item.componentType,
                        color = PrimaryNeonGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkSurfaceVariant
                ) {
                    Text(
                        text = item.badge,
                        color = AccentCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.description,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Manifest Snippet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkBackground)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "Deklarasi di AndroidManifest.xml:",
                        color = AccentAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.manifestDeclaration,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Sembunyikan Implementasi Kotlin" else "Lihat Kode Kotlin...",
                    color = SecondaryPink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isExpanded) "▲" else "▼",
                    color = SecondaryPink,
                    fontSize = 11.sp
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkBackground)
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = item.codeSnippet,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: SUNO WEB PORTAL (Bundled Offline Assets from suno_v6_com-main)
// -------------------------------------------------------------
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPortalScreen() {
    var viewMode by remember { mutableStateOf("NATIVE") } // "NATIVE" or "WEBVIEW"
    var currentUrl by remember { mutableStateOf("file:///android_asset/web/index.html") }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var hasRendererCrashed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("web_portal_screen")
    ) {
        // Mode Switcher: Native Reader vs Web Browser
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (viewMode == "NATIVE") PrimaryNeon else DarkSurfaceVariant)
                    .clickable { viewMode = "NATIVE" }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📑 Panduan & Ringkasan",
                    color = if (viewMode == "NATIVE") TextPrimary else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (viewMode == "WEBVIEW") AccentCyan else DarkSurfaceVariant)
                    .clickable {
                        viewMode = "WEBVIEW"
                        hasRendererCrashed = false
                    }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🌐 Browser Webview",
                    color = if (viewMode == "WEBVIEW") TextPrimary else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (viewMode == "NATIVE") {
            NativeWebGuideScreen(onOpenWebView = { targetUrl ->
                currentUrl = targetUrl
                viewMode = "WEBVIEW"
                hasRendererCrashed = false
            })
        } else {
            // Quick Navigation Bar for WebView
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = {
                        currentUrl = "file:///android_asset/web/index.html"
                        hasRendererCrashed = false
                        webViewInstance?.loadUrl(currentUrl)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentUrl.endsWith("web/index.html")) PrimaryNeon else DarkSurface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("🏠 Beranda", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        currentUrl = "file:///android_asset/web/free/index.html"
                        hasRendererCrashed = false
                        webViewInstance?.loadUrl(currentUrl)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentUrl.contains("/free/")) PrimaryNeon else DarkSurface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("🎁 Free", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        currentUrl = "file:///android_asset/web/pro/index.html"
                        hasRendererCrashed = false
                        webViewInstance?.loadUrl(currentUrl)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentUrl.contains("/pro/")) PrimaryNeon else DarkSurface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("⭐ Pro", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        currentUrl = "file:///android_asset/web/tools/index.html"
                        hasRendererCrashed = false
                        webViewInstance?.loadUrl(currentUrl)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentUrl.contains("/tools/")) PrimaryNeon else DarkSurface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("🛠️ Tools", fontSize = 11.sp)
                }

                IconButton(
                    onClick = {
                        hasRendererCrashed = false
                        webViewInstance?.reload()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reload", tint = TextPrimary)
                }
            }

            if (hasRendererCrashed) {
                // Safe Fallback if emulator Chromium renderer has crashed
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🛡️", fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Renderer Emulator Direset",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Lingkungan emulator virtual mengalami keterbatasan akselerasi GPU Chromium. Silakan gunakan Mode Panduan Native atau muat ulang dengan perenderan perangkat lunak.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewMode = "NATIVE" },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon)
                                ) {
                                    Text("Buka Panduan Native", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = {
                                        hasRendererCrashed = false
                                        webViewInstance?.loadUrl(currentUrl)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                                ) {
                                    Text("Muat Ulang", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                // Embedded Android WebView with software rendering and onRenderProcessGone protection
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            // Force software rendering to completely avoid Mesa rendernode GPU crash on emulator
                            setLayerType(View.LAYER_TYPE_SOFTWARE, null)

                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                allowFileAccess = true
                                allowContentAccess = true
                                cacheMode = WebSettings.LOAD_DEFAULT
                                mediaPlaybackRequiresUserGesture = true
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onRenderProcessGone(
                                    view: WebView?,
                                    detail: RenderProcessGoneDetail?
                                ): Boolean {
                                    // Returning true is MANDATORY to prevent Android OS from terminating host app!
                                    try {
                                        view?.destroy()
                                    } catch (_: Exception) {}
                                    hasRendererCrashed = true
                                    return true
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    // Handle network/resource errors silently
                                }
                            }

                            loadUrl(currentUrl)
                            webViewInstance = this
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun NativeWebGuideScreen(onOpenWebView: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Hero Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            AccentCyan.copy(alpha = 0.2f),
                            DarkSurface
                        )
                    )
                )
                .border(1.dp, AccentCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎵", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Suno V6 AI Music Platform",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Panduan Lengkap, Alur Kerja & Perbandingan Paket",
                            color = AccentCyan,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Suno V6 adalah generator musik AI berbasis peramban untuk kreasi lagu dari teks prompt dan lirik. Dirancang untuk kreator konten, penulis lagu, dan produksi audio komersial.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Alur Kerja Utama (Workflow)",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "1. Text-to-Music (Prompt Terbimbing)",
                    color = PrimaryNeonGlow,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tentukan instrumen, tempo BPM, suasana hati (mood), dan deskripsi genre untuk menghasilkan track musik lengkap.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "2. Lyrics-to-Song (Struktur Lirik)",
                    color = SecondaryPink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Gunakan penanda [Verse], [Chorus], [Drop], dan [Bridge] untuk memandu struktur aransemen vokal dan melodi vokal AI.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Perbandingan Paket Suno V6",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Free Plan", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "50 kredit/hari", color = AccentCyan, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "• Antrean bersama", color = TextSecondary, fontSize = 11.sp)
                    Text(text = "• Non-komersial", color = TextSecondary, fontSize = 11.sp)
                    Text(text = "• Kualitas standar", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryNeon)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Pro Plan ⭐", color = PrimaryNeonGlow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "2.500 kredit/bln", color = AccentAmber, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "• Antrean prioritas", color = TextSecondary, fontSize = 11.sp)
                    Text(text = "• Hak cipta komersial", color = TextSecondary, fontSize = 11.sp)
                    Text(text = "• Ultra Stereo 96kHz", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions to open WebView
        Text(
            text = "Halaman Dokumen Offline:",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onOpenWebView("file:///android_asset/web/index.html") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Buka Webview", fontSize = 11.sp, color = TextPrimary)
            }

            Button(
                onClick = { onOpenWebView("file:///android_asset/web/tools/index.html") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Buka Tools Web", fontSize = 11.sp, color = TextPrimary)
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: TOOLS & SUITE
// -------------------------------------------------------------
@Composable
fun ToolsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("tools_screen")
    ) {
        Text(
            text = "Suno V6 Audio Tools Suite",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Fitur produksi musik dan pemrosesan audio canggih",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        val toolItems = listOf(
            Triple("🎤 Vocal Stem Separator", "Pisahkan vokal acapella dari instrumental backing track dengan AI model stereo 96kHz.", AccentCyan),
            Triple("🥁 BPM & Key Tap Detector", "Deteksi tempo ketukan dan tangga nada harmonik (Camelot wheel) secara real-time.", PrimaryNeon),
            Triple("🎚️ Song Section Extender", "Perpanjang durasi lagu hingga 10 menit dengan transisi chorus dan solo yang mulus.", SecondaryPink),
            Triple("🎨 AI Album Art Generator", "Buat cover artwork bertema visual futuristik yang sesuai dengan genre dan lirik musik.", AccentEmerald),
            Triple("📻 Mastering Equalizer V6", "Optimasi dynamic range, stereo wideness, dan loudness LUFS untuk Spotify & Apple Music.", AccentAmber)
        )

        toolItems.forEach { (title, desc, color) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = desc,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}
