package com.tunorbit.music.core.model

/**
 * Canonical representation of an artist inside TunOrbit.
 *
 * The recommendation engine works with this model without
 * needing to know which music source supplied the artist.
 */
data class Artist(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val genre: String? = null
)