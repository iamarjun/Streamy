package com.arjun.streamy.ui

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.arjun.streamy.data.entities.Song
import com.arjun.streamy.exoplayer.MusicServiceConnection
import com.arjun.streamy.exoplayer.isPlayEnabled
import com.arjun.streamy.exoplayer.isPlaying
import com.arjun.streamy.exoplayer.isPrepared
import com.arjun.streamy.util.Constants.MEDIA_ROOT_ID
import com.arjun.streamy.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel @ViewModelInject constructor(private val musicServiceConnection: MusicServiceConnection) :
    ViewModel() {

    private val _mediaItems = MutableStateFlow<Resource<List<Song>>>(Resource.Loading)
    val mediaItems: StateFlow<Resource<List<Song>>>
        get() = _mediaItems

    val isConnected: StateFlow<Resource<Boolean>>
        get() = musicServiceConnection.isConnected

    val networkError: StateFlow<Resource<Boolean>>
        get() = musicServiceConnection.networkError

    val playbackState: StateFlow<PlaybackStateCompat?>
        get() = musicServiceConnection.playbackState

    val currentPlayingSong: StateFlow<MediaMetadataCompat?>
        get() = musicServiceConnection.currentPlayingSong


    init {
        musicServiceConnection.subscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)

                    val items = children.map {
                        Song(
                            id = it.mediaId?.toInt() ?: -1,
                            title = it.description.title.toString(),
                            artist = it.description.subtitle.toString(),
                            songUrl = it.description.mediaUri.toString(),
                            albumArt = it.description.mediaUri.toString(),
                            icon = it.description.iconUri.toString()
                        )
                    }

                    _mediaItems.value = Resource.Success(items)
                }
            })
    }

    fun skipToNextSong() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun skipToPreviousSong() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(pos: Long) {
        musicServiceConnection.transportControls.seekTo(pos)
    }

    fun playOrToggle(mediaItem: Song, toggle: Boolean = false) {
        val isPrepared = playbackState.value?.isPrepared ?: false

        if (isPrepared &&
            mediaItem.id.toString() == currentPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)
        ) {
            playbackState.value?.let {
                when {
                    it.isPlaying -> if (toggle) musicServiceConnection.transportControls.pause()
                    it.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }

        } else {
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.id.toString(), null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(
            MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})
    }

}