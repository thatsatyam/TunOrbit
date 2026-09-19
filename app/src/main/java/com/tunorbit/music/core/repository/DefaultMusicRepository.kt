package com.tunorbit.music.core.repository

import com.tunorbit.music.core.data.source.MusicDataSource
import com.tunorbit.music.core.model.Album
import com.tunorbit.music.core.model.Artist
import com.tunorbit.music.core.model.Playlist
import com.tunorbit.music.core.model.Song
import kotlinx.coroutines.flow.Flow

class DefaultMusicRepository(
    private val dataSource: MusicDataSource
) : MusicRepository {

    override suspend fun searchSongs(query: String): List<Song> {
        return dataSource.searchSongs(query)
    }

    override suspend fun searchArtists(query: String): List<Artist> {
        return dataSource.searchArtists(query)
    }

    override suspend fun getSongsByArtist(artistId: String): List<Song> {
        return dataSource.getSongsByArtist(artistId)
    }

    override suspend fun getArtist(artistId: String): Artist? {
        return dataSource.getArtist(artistId)
    }

    override suspend fun toggleLike(songId: String) {
        dataSource.toggleLike(songId)
    }

    override fun observeLikedSongs(): Flow<List<Song>> {
        return dataSource.observeLikedSongs()
    }

    override suspend fun addRecentSong(songId: String) {
        dataSource.addRecentSong(songId)
    }

    override fun observeRecentSongs(): Flow<List<Song>> {
        return dataSource.observeRecentSongs()
    }

    override fun observePlaylists(): Flow<List<Playlist>> {
        return dataSource.observePlaylists()
    }

    override suspend fun createPlaylist(name: String) {
        dataSource.createPlaylist(name)
    }

    override suspend fun deletePlaylist(playlistId: String) {
        dataSource.deletePlaylist(playlistId)
    }

    override suspend fun addSongToPlaylist(playlistId: String, songId: String) {
        dataSource.addSongToPlaylist(playlistId, songId)
    }

    override suspend fun removeSongFromPlaylist(playlistId: String, songId: String) {
        dataSource.removeSongFromPlaylist(playlistId, songId)
    }

    override fun observePlaylistSongs(playlistId: String): Flow<List<Song>> {
        return dataSource.observePlaylistSongs(playlistId)
    }

    override suspend fun getAllSongs(): List<Song> {
        return dataSource.getAllSongs()
    }

    override suspend fun getAlbums(): List<Album> {
        return dataSource.getAlbums()
    }

    override suspend fun getArtists(): List<Artist> {
        return dataSource.getArtists()
    }
}