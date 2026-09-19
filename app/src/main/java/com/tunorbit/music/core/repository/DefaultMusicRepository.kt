package com.tunorbit.music.core.repository

import com.tunorbit.music.core.data.source.MusicDataSource
import com.tunorbit.music.core.model.Album
import com.tunorbit.music.core.model.Artist
import com.tunorbit.music.core.model.Playlist
import com.tunorbit.music.core.model.Song
import kotlinx.coroutines.flow.Flow

class DefaultMusicRepository(
    private val localDataSource: MusicDataSource,
    private val remoteDataSource: MusicDataSource
) : MusicRepository {

    override suspend fun searchSongs(query: String): List<Song> {
        return try {
            val results = remoteDataSource.searchSongs(query)
            if (results.isNotEmpty()) results else localDataSource.searchSongs(query)
        } catch (e: Exception) {
            localDataSource.searchSongs(query)
        }
    }

    override suspend fun searchArtists(query: String): List<Artist> {
        return localDataSource.searchArtists(query)
    }

    override suspend fun getSongsByArtist(artistId: String): List<Song> {
        return localDataSource.getSongsByArtist(artistId)
    }

    override suspend fun getArtist(artistId: String): Artist? {
        return localDataSource.getArtist(artistId)
    }

    override suspend fun toggleLike(songId: String) {
        localDataSource.toggleLike(songId)
    }

    override fun observeLikedSongs(): Flow<List<Song>> {
        return localDataSource.observeLikedSongs()
    }

    override suspend fun addRecentSong(songId: String) {
        localDataSource.addRecentSong(songId)
    }

    override fun observeRecentSongs(): Flow<List<Song>> {
        return localDataSource.observeRecentSongs()
    }

    override fun observePlaylists(): Flow<List<Playlist>> {
        return localDataSource.observePlaylists()
    }

    override suspend fun createPlaylist(name: String) {
        localDataSource.createPlaylist(name)
    }

    override suspend fun deletePlaylist(playlistId: String) {
        localDataSource.deletePlaylist(playlistId)
    }

    override suspend fun addSongToPlaylist(playlistId: String, songId: String) {
        localDataSource.addSongToPlaylist(playlistId, songId)
    }

    override suspend fun removeSongFromPlaylist(playlistId: String, songId: String) {
        localDataSource.removeSongFromPlaylist(playlistId, songId)
    }

    override fun observePlaylistSongs(playlistId: String): Flow<List<Song>> {
        return localDataSource.observePlaylistSongs(playlistId)
    }

    override suspend fun getAllSongs(): List<Song> {
        return localDataSource.getAllSongs()
    }

    override suspend fun getAlbums(): List<Album> {
        return localDataSource.getAlbums()
    }

    override suspend fun getAlbum(albumId: String): Album? {
        return try {
            remoteDataSource.getAlbum(albumId) ?: localDataSource.getAlbum(albumId)
        } catch (e: Exception) {
            localDataSource.getAlbum(albumId)
        }
    }

    override suspend fun getSongsByAlbum(albumId: String): List<Song> {
        return try {
            val results = remoteDataSource.getSongsByAlbum(albumId)
            if (results.isNotEmpty()) results else localDataSource.getSongsByAlbum(albumId)
        } catch (e: Exception) {
            localDataSource.getSongsByAlbum(albumId)
        }
    }

    override suspend fun getArtists(): List<Artist> {
        return localDataSource.getArtists()
    }

    override fun observeRecentSearches(): Flow<List<String>> {
        return localDataSource.observeRecentSearches()
    }

    override suspend fun addRecentSearch(query: String) {
        localDataSource.addRecentSearch(query)
    }

    override suspend fun clearRecentSearches() {
        localDataSource.clearRecentSearches()
    }

    override suspend fun getDiscoverSongs(): List<Song> {
        return try {
            val results = remoteDataSource.getDiscoverSongs()
            if (results.isNotEmpty()) results else localDataSource.getDiscoverSongs()
        } catch (e: Exception) {
            localDataSource.getDiscoverSongs()
        }
    }
}