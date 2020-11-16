package com.arjun.streamy.exoplayer.callbacks

import android.widget.Toast
import com.arjun.streamy.exoplayer.MusicService
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player

class MusicPlayerEventListener(private val musicService: MusicService) : Player.EventListener {
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        if (playbackState == Player.STATE_READY && !playWhenReady)
            musicService.stopForeground(false)
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "Something went wrong", Toast.LENGTH_LONG).show()
    }
}