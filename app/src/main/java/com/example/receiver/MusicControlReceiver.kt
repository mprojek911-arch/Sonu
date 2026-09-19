package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.service.MusicPlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MusicControlReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_MEDIA_PLAY = "com.example.ACTION_MEDIA_PLAY"
        const val ACTION_MEDIA_PAUSE = "com.example.ACTION_MEDIA_PAUSE"
        const val ACTION_MEDIA_NEXT = "com.example.ACTION_MEDIA_NEXT"

        private val _lastReceivedAction = MutableStateFlow("None (Listening for broadcasts)")
        val lastReceivedAction: StateFlow<String> = _lastReceivedAction
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        _lastReceivedAction.value = action

        when (action) {
            ACTION_MEDIA_PLAY -> {
                context?.let {
                    MusicPlaybackService.start(it, "Broadcast Triggered Track", "AI Synth")
                    Toast.makeText(it, "Broadcast: Media Play Started", Toast.LENGTH_SHORT).show()
                }
            }
            ACTION_MEDIA_PAUSE -> {
                context?.let {
                    MusicPlaybackService.stop(it)
                    Toast.makeText(it, "Broadcast: Media Play Paused", Toast.LENGTH_SHORT).show()
                }
            }
            ACTION_MEDIA_NEXT -> {
                context?.let {
                    Toast.makeText(it, "Broadcast: Next Track Requested", Toast.LENGTH_SHORT).show()
                }
            }
            Intent.ACTION_HEADSET_PLUG -> {
                val state = intent.getIntExtra("state", -1)
                val msg = if (state == 1) "Headset Plugged In" else "Headset Unplugged"
                _lastReceivedAction.value = "System: $msg"
            }
        }
    }
}
