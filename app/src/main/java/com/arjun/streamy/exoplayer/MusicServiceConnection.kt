package com.arjun.streamy.exoplayer

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.arjun.streamy.util.Constants.NETWORK_ERROR
import com.arjun.streamy.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

class MusicServiceConnection(context: Context) {

    private val _isConnected = MutableStateFlow<Resource<Boolean>>(Resource.Loading)
    val isConnected: StateFlow<Resource<Boolean>>
        get() = _isConnected

    private val _networkError = MutableStateFlow<Resource<Boolean>>(Resource.Loading)
    val networkError: StateFlow<Resource<Boolean>>
        get() = _networkError

    private val _playbackState = MutableStateFlow<PlaybackStateCompat?>(null)
    val playbackState: StateFlow<PlaybackStateCompat?>
        get() = _playbackState

    private val _currentPlayingSong = MutableStateFlow<MediaMetadataCompat?>(null)
    val currentPlayingSong: StateFlow<MediaMetadataCompat?>
        get() = _currentPlayingSong


    lateinit var mediaController: MediaControllerCompat

    val transportControls
        get() = mediaController.transportControls

    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(context, MusicService::class.java),
        mediaBrowserConnectionCallback,
        null
    ).apply {
        connect()
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.value = state
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            _currentPlayingSong.value = metadata
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)

            when (event) {

                NETWORK_ERROR -> _networkError.value =
                    Resource.Error(IOException("Couldn't connect to the server. Please check your internet connection."))
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }

    private inner class MediaBrowserConnectionCallback(private val context: Context) :
        MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            _isConnected.value = Resource.Success(true)
        }

        override fun onConnectionSuspended() {
            _isConnected.value = Resource.Error(IOException("Connection was suspended"))
        }

        override fun onConnectionFailed() {
            _isConnected.value =
                Resource.Error(IOException("Couldn't connect to the media browser"))
        }
    }
}