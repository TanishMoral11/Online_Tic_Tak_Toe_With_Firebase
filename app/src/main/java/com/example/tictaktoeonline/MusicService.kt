package com.example.tictaktoeonline

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private var resumePosition = 0

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.background_sound)
        mediaPlayer.isLooping = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.seekTo(resumePosition)
            mediaPlayer.start()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            if (mediaPlayer.isPlaying) {
                resumePosition = mediaPlayer.currentPosition
                mediaPlayer.pause()
            }
            mediaPlayer.release()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
