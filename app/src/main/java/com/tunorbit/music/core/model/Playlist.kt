package com.tunorbit.music.core.model

data class Playlist(
    val id: String,
    val name: String,
    val songIds: List<String> = emptyList()
)
