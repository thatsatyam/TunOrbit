package com.tunorbit.music.core.data.source

import com.tunorbit.music.core.model.Album
import com.tunorbit.music.core.model.Artist
import com.tunorbit.music.core.model.Playlist
import com.tunorbit.music.core.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicDataSource {

    suspend fun searchSongs(query: String): List<Song>

    suspend fun searchArtists(query: String): List<Artist>

    suspend fun getSongsByArtist(artistId: String): List<Song>

    suspend fun getArtist(artistId: String): Artist?

    suspend fun toggleLike(songId: String)

    fun observeLikedSongs(): Flow<List<Song>>

    suspend fun addRecentSong(songId: String)

    fun observeRecentSongs(): Flow<List<Song>>

    fun observePlaylists(): Flow<List<Playlist>>

    suspend fun createPlaylist(name: String)

    suspend fun deletePlaylist(playlistId: String)

    suspend fun addSongToPlaylist(playlistId: String, songId: String)

    suspend fun removeSongFromPlaylist(playlistId: String, songId: String)

    fun observePlaylistSongs(playlistId: String): Flow<List<Song>>

    suspend fun getAllSongs(): List<Song>
    
    suspend fun getAlbums(): List<Album>
    
    suspend fun getArtists(): List<Artist>

    fun observeRecentSearches(): Flow<List<String>>

    suspend fun addRecentSearch(query: String)

    suspend fun clearRecentSearches()

    suspend fun getDiscoverSongs(): List<Song>
}