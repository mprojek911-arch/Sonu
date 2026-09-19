package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MusicPlaybackService : Service() {

    companion object {
        const val CHANNEL_ID = "suno_playback_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "com.example.service.ACTION_START"
        const val ACTION_STOP = "com.example.service.ACTION_STOP"
        const val ACTION_TOGGLE_PLAY = "com.example.service.ACTION_TOGGLE_PLAY"

        const val EXTRA_TRACK_TITLE = "extra_track_title"
        const val EXTRA_TRACK_GENRE = "extra_track_genre"

        private val _isServiceRunning = MutableStateFlow(false)
        val isServiceRunning: StateFlow<Boolean> = _isServiceRunning

        private val _currentTrackTitle = MutableStateFlow("No track playing")
        val currentTrackTitle: StateFlow<String> = _currentTrackTitle

        fun start(context: Context, trackTitle: String, genre: String) {
            val intent = Intent(context, MusicPlaybackService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_TRACK_TITLE, trackTitle)
                putExtra(EXTRA_TRACK_GENRE, genre)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, MusicPlaybackService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val title = intent.getStringExtra(EXTRA_TRACK_TITLE) ?: "Suno V6 Track"
                val genre = intent.getStringExtra(EXTRA_TRACK_GENRE) ?: "AI Synth"
                _currentTrackTitle.value = title
                _isServiceRunning.value = true

                val notification = buildNotification(title, genre)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                    )
                } else {
                    startForeground(NOTIFICATION_ID, notification)
                }
            }
            ACTION_STOP -> {
                _isServiceRunning.value = false
                _currentTrackTitle.value = "Stopped"
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Suno V6 Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows playback controls for Suno V6 generated music"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(trackTitle: String, genre: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingOpenApp = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, MusicPlaybackService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingStop = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Suno V6 • $trackTitle")
            .setContentText("Genre: $genre | Ultra Stereo Fidelity")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingOpenApp)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_media_pause, "Stop", pendingStop)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        _isServiceRunning.value = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
