package com.tunorbit.music.core.repository

import com.tunorbit.music.core.data.source.MusicDataSource
import com.tunorbit.music.core.model.Album
import com.tunorbit.music.core.model.Artist
import com.tunorbit.music.core.model.Playlist
import com.tunorbit.music.core.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class DefaultMusicRepository(
    private val localDataSource: MusicDataSource,
    private val remoteDataSource: MusicDataSource
) : MusicRepository {

    override suspend fun searchSongs(query: String): List<Song> {
        return try {
            val results = remoteDataSource.searchSongs(query)
            if (results.isNotEmpty()) {
                results.forEach { localDataSource.cacheSong(it) }
                results
            } else localDataSource.searchSongs(query)
        } catch (ignore: Exception) {
            localDataSource.searchSongs(query)
        }
    }

    override suspend fun searchArtists(query: String): List<Artist> {
        return localDataSource.searchArtists(query)
    }

    override suspend fun getSongsByArtist(artistId: String): List<Song> {
        return try {
            val results = remoteDataSource.getSongsByArtist(artistId)
            if (results.isNotEmpty()) {
                results.forEach { localDataSource.cacheSong(it) }
                results
            } else localDataSource.getSongsByArtist(artistId)
        } catch (ignore: Exception) {
            localDataSource.getSongsByArtist(artistId)
        }
    }

    override suspend fun getArtist(artistId: String): Artist? {
        return try {
            remoteDataSource.getArtist(artistId) ?: localDataSource.getArtist(artistId)
        } catch (ignore: Exception) {
            localDataSource.getArtist(artistId)
        }
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
        } catch (ignore: Exception) {
            localDataSource.getAlbum(albumId)
        }
    }

    override suspend fun getSongsByAlbum(albumId: String): List<Song> {
        return try {
            val results = remoteDataSource.getSongsByAlbum(albumId)
            if (results.isNotEmpty()) {
                results.forEach { localDataSource.cacheSong(it) }
                results
            } else localDataSource.getSongsByAlbum(albumId)
        } catch (ignore: Exception) {
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
            if (results.isNotEmpty()) {
                results.forEach { localDataSource.cacheSong(it) }
                results
            } else localDataSource.getDiscoverSongs()
        } catch (ignore: Exception) {
            localDataSource.getDiscoverSongs()
        }
    }

    override suspend fun getRecommendedSongs(): List<Song> {
        return try {
            val recent = localDataSource.observeRecentSongs().first()
            val liked = localDataSource.observeLikedSongs().first()
            val historySongs = (recent + liked).distinctBy { it.id }
            val historyIds = historySongs.map { it.id }.toSet()

            // 1. Score artists by frequency of engagement
            val artistFrequencies = historySongs.groupingBy { it.artistId }
                .eachCount()
                .filterKeys { it.isNotBlank() }

            // 2. Select top 5 artists deterministically (frequency desc, then alphabetically)
            val topArtists = artistFrequencies.entries
                .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
                .take(5)
                .map { it.key }

            // 3. Fetch songs for these artists and exclude exact history matches
            val recommendationsByArtist = mutableMapOf<String, List<Song>>()
            for (artistId in topArtists) {
                val artistSongs = remoteDataSource.getSongsByArtist(artistId)
                recommendationsByArtist[artistId] = artistSongs
                    .filter { !historyIds.contains(it.id) }
                    .distinctBy { it.id }
            }

            // 4. Interleave results round-robin for variety without random shuffle
            val finalRecs = mutableListOf<Song>()
            val maxSongsPerArtist = recommendationsByArtist.values.maxOfOrNull { it.size } ?: 0

            for (i in 0 until maxSongsPerArtist) {
                for (artistId in topArtists) {
                    val songs = recommendationsByArtist[artistId]
                    if (songs != null && i < songs.size) {
                        val song = songs[i]
                        if (finalRecs.none { it.id == song.id }) {
                            finalRecs.add(song)
                        }
                    }
                    if (finalRecs.size >= 15) break
                }
                if (finalRecs.size >= 15) break
            }

            if (finalRecs.isNotEmpty()) {
                finalRecs.forEach { localDataSource.cacheSong(it) }
                finalRecs
            } else {
                val fallback = remoteDataSource.getDiscoverSongs()
                if (fallback.isNotEmpty()) {
                    fallback.forEach { localDataSource.cacheSong(it) }
                    fallback
                } else {
                    localDataSource.getDiscoverSongs()
                }
            }
        } catch (ignore: Exception) {
            localDataSource.getDiscoverSongs()
        }
    }

    override suspend fun cacheSong(song: Song) {
        localDataSource.cacheSong(song)
    }
}