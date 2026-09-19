package com.example.model

data class Track(
    val id: String,
    val title: String,
    val genre: String,
    val bpm: Int,
    val durationFormatted: String = "2:15",
    val prompt: String,
    val lyrics: String = "",
    val styleTags: List<String> = emptyList(),
    val baseFreq: Float = 440f, // For audio synthesizer
    val scaleType: String = "minor"
)

data class GenrePreset(
    val id: String,
    val name: String,
    val emoji: String,
    val prompt: String,
    val bpm: Int,
    val styleTags: List<String>,
    val baseFreq: Float
)

data class ApkAnatomyItem(
    val title: String,
    val componentType: String,
    val badge: String,
    val description: String,
    val manifestDeclaration: String,
    val codeSnippet: String,
    val liveStatus: String
)

sealed class GenerationState {
    object Idle : GenerationState()
    data class Generating(val progress: Float, val stage: String) : GenerationState()
    data class Completed(val track: Track) : GenerationState()
}
