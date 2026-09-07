package com.tunorbit.music.core.model

/**
 * Canonical representation of an album inside TunOrbit.
 */
data class Album(
    val id: String,
    val title: String,
    val artistId: String,
    val artistName: String,
    val artworkUrl: String? = null,
    val releaseYear: Int? = null,
    val trackCount: Int? = null
)