package com.arjun.streamy.ui.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arjun.streamy.exoplayer.MusicService
import com.arjun.streamy.exoplayer.MusicServiceConnection
import com.arjun.streamy.exoplayer.currentPlaybackPosition
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SongViewModel @ViewModelInject constructor(musicServiceConnection: MusicServiceConnection) :
    ViewModel() {

    private val playbackState = musicServiceConnection.playbackState

    private val _currentSongDuration = MutableStateFlow(0L)
    val currentSongDuration = _currentSongDuration.asStateFlow()

    private val _currentPlayerPosition = MutableStateFlow(0L)
    val currentPlayerPosition = _currentSongDuration.asStateFlow()

    init {
        updateCurrentPlayerPosition()
    }

    private fun updateCurrentPlayerPosition() {
        viewModelScope.launch {
            while (true) {
                playbackState.value?.currentPlaybackPosition?.let { position ->
                    if (currentPlayerPosition.value != position) {
                        _currentPlayerPosition.value = position
                        _currentSongDuration.value = MusicService.currentSongDuration
                    }
                }

                delay(1000)
            }
        }
    }

}