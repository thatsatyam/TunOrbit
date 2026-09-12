package com.tunorbit.music.core.repository

import com.tunorbit.music.core.model.Artist
import com.tunorbit.music.core.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    suspend fun searchSongs(query: String): List<Song>

    suspend fun searchArtists(query: String): List<Artist>

    suspend fun getSongsByArtist(artistId: String): List<Song>

    suspend fun getArtist(artistId: String): Artist?

    suspend fun toggleLike(songId: String)

    fun observeLikedSongs(): Flow<List<Song>>
}