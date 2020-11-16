package com.arjun.streamy.data.entities

data class Song(
    val id: Int = -1,
    val title: String = "",
    val artist: String = "",
    val songUrl: String = "",
    val albumArt: String = "",
    val icon: String = ""
)