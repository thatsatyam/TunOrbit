package com.tunorbit.music.core.model

/**
 * Canonical representation of a song inside TunOrbit.
 *
 * The recommendation engine works with this model without
 * needing to know which music source supplied the song.
 */
data class Song(
    val id: String,
    val title: String,
    val artistId: String,
    val artistName: String,
    val albumId: String? = null,
    val albumName: String? = null,
    val durationMs: Long = 0L,
    val artworkUrl: String? = null,
    val language: String? = null,
    val releaseYear: Int? = null
)