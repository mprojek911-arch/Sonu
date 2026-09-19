package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

object ToneSynthGenerator {
    private var currentJob: Job? = null
    private var audioTrack: AudioTrack? = null
    var isPlaying = false
        private set

    private const val SAMPLE_RATE = 44100

    fun playTrackMelody(
        baseFreq: Float = 440f,
        bpm: Int = 120,
        scope: CoroutineScope,
        onPlayingChanged: (Boolean) -> Unit
    ) {
        stop()

        val minBufSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

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
            .setBufferSizeInBytes(minBufSize.coerceAtLeast(8192))
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()
        isPlaying = true
        onPlayingChanged(true)

        // Musical scale intervals (pentatonic / minor progression)
        val intervals = listOf(1.0f, 1.189f, 1.334f, 1.498f, 1.781f, 2.0f, 1.781f, 1.498f)
        val noteDurationMs = (60_000L / bpm.coerceAtLeast(60)) / 2

        currentJob = scope.launch(Dispatchers.Default) {
            try {
                var step = 0
                while (isActive) {
                    val freq = baseFreq * intervals[step % intervals.size]
                    val numSamples = (SAMPLE_RATE * (noteDurationMs / 1000f)).toInt()
                    val buffer = ShortArray(numSamples)

                    for (i in 0 until numSamples) {
                        val t = i.toDouble() / SAMPLE_RATE
                        // Add fundamental + soft second harmonic for warm synth timbre
                        val sampleVal = (sin(2.0 * Math.PI * freq * t) * 0.7 +
                                sin(4.0 * Math.PI * freq * t) * 0.3)
                        // Envelope ADSR (gentle fade in and out)
                        val env = when {
                            i < 200 -> i / 200.0
                            i > numSamples - 300 -> (numSamples - i) / 300.0
                            else -> 1.0
                        }
                        buffer[i] = (sampleVal * env * Short.MAX_VALUE * 0.5).toInt().toShort()
                    }

                    audioTrack?.write(buffer, 0, buffer.size)
                    step++
                    delay(10)
                }
            } catch (_: Exception) {
            } finally {
                stop()
                onPlayingChanged(false)
            }
        }
    }

    fun stop() {
        currentJob?.cancel()
        currentJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {
        }
        audioTrack = null
        isPlaying = false
    }
}
