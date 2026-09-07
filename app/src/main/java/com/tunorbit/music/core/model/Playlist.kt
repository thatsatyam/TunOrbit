package com.tunorbit.music.core.model

/**
 * A user-created playlist inside TunOrbit.
 *
 * The actual song order is stored separately so playlist
 * metadata stays lightweight and easy to update.
 */
data class Playlist(
    val id: String,
    val name: String,
    val description: String? = null,
    val artworkUrl: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)