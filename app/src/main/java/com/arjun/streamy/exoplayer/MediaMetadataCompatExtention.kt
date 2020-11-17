package com.arjun.streamy.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.arjun.streamy.data.entities.Song

fun MediaMetadataCompat.toSong(): Song? {
    return description?.let {
        Song(
            id = it.mediaId?.toInt() ?: -1,
            title = it.title.toString(),
            artist = it.subtitle.toString(),
            albumArt = it.mediaUri.toString(),
            icon = it.iconUri.toString()
        )
    }
}