package com.tunorbit.music.core.data.source

import com.tunorbit.music.core.model.Album
import com.tunorbit.music.core.model.Artist
import com.tunorbit.music.core.model.Playlist
import com.tunorbit.music.core.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class RemoteMusicDataSource : MusicDataSource {

    override suspend fun searchSongs(query: String): List<Song> {
        TODO("Implement song search")
    }

    override suspend fun searchArtists(query: String): List<Artist> {
        TODO("Implement artist search")
    }

    override suspend fun getSongsByArtist(artistId: String): List<Song> {
        TODO("Implement artist songs")
    }

    override suspend fun getArtist(artistId: String): Artist? {
        TODO("Implement artist lookup")
    }

    override suspend fun toggleLike(songId: String) {
        // No-op for remote for now
    }

    override fun observeLikedSongs(): Flow<List<Song>> {
        return emptyFlow()
    }

    override suspend fun addRecentSong(songId: String) {
        // No-op for remote for now
    }

    override fun observeRecentSongs(): Flow<List<Song>> {
        return emptyFlow()
    }

    override fun observePlaylists(): Flow<List<Playlist>> {
        return emptyFlow()
    }

    override suspend fun createPlaylist(name: String) {}

    override suspend fun deletePlaylist(playlistId: String) {}

    override suspend fun addSongToPlaylist(playlistId: String, songId: String) {}

    override suspend fun removeSongFromPlaylist(playlistId: String, songId: String) {}

    override fun observePlaylistSongs(playlistId: String): Flow<List<Song>> {
        return emptyFlow()
    }

    override suspend fun getAllSongs(): List<Song> {
        return emptyList()
    }

    override suspend fun getAlbums(): List<Album> {
        return emptyList()
    }

    override suspend fun getArtists(): List<Artist> {
        return emptyList()
    }
}