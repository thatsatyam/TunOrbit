package com.tunorbit.music.core.data.source

import android.content.SharedPreferences
import com.tunorbit.music.core.model.Album
import com.tunorbit.music.core.model.Artist
import com.tunorbit.music.core.model.Playlist
import com.tunorbit.music.core.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class FakeMusicDataSource(private val prefs: SharedPreferences) : MusicDataSource {

    private val _likedSongIds = MutableStateFlow<Set<String>>(
        prefs.getStringSet("liked_songs", emptySet())?.toSet() ?: emptySet()
    )

    private val _recentSongIds = MutableStateFlow<List<String>>(
        prefs.getString("recent_songs", "")?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    )

    private val _playlists = MutableStateFlow<List<Playlist>>(loadPlaylistsFromPrefs())

    private fun loadPlaylistsFromPrefs(): List<Playlist> {
        val ids = prefs.getStringSet("playlist_ids", emptySet()) ?: emptySet()
        return ids.mapNotNull { id ->
            val name = prefs.getString("playlist_${id}_name", null) ?: return@mapNotNull null
            val songsStr = prefs.getString("playlist_${id}_songs", "") ?: ""
            val songIds = songsStr.split(",").filter { it.isNotBlank() }
            Playlist(id, name, songIds)
        }
    }

    private fun savePlaylistsToPrefs(playlists: List<Playlist>) {
        val editor = prefs.edit()
        editor.putStringSet("playlist_ids", playlists.map { it.id }.toSet())
        playlists.forEach { p ->
            editor.putString("playlist_${p.id}_name", p.name)
            editor.putString("playlist_${p.id}_songs", p.songIds.joinToString(","))
        }
        editor.apply()
    }

    private val baseSongs = listOf(
        Song(
            id = "song_001",
            title = "Blinding Lights",
            artistId = "artist_001",
            artistName = "The Weeknd",
            albumId = "album_001",
            albumName = "After Hours",
            durationMs = 200040L,
            artworkUrl = "https://picsum.photos/seed/tunorbit1/300/300",
            language = "English",
            releaseYear = 2020,
            mediaUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        ),
        Song(
            id = "song_002",
            title = "Starboy",
            artistId = "artist_001",
            artistName = "The Weeknd",
            albumId = "album_002",
            albumName = "Starboy",
            durationMs = 230453L,
            artworkUrl = null,
            language = "English",
            releaseYear = 2016,
            mediaUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
        ),
        Song(
            id = "song_003",
            title = "Shape of You",
            artistId = "artist_002",
            artistName = "Ed Sheeran",
            albumId = "album_003",
            albumName = "÷",
            durationMs = 233713L,
            artworkUrl = "https://picsum.photos/seed/tunorbit3/300/300",
            language = "English",
            releaseYear = 2017,
            mediaUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
        )
    )

    override suspend fun searchSongs(query: String): List<Song> {
        val likedIds = _likedSongIds.value
        val results = if (query.isBlank()) {
            baseSongs
        } else {
            baseSongs.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.artistName.contains(query, ignoreCase = true)
            }
        }
        return results.map { it.copy(isLiked = likedIds.contains(it.id)) }
    }

    override suspend fun searchArtists(query: String): List<Artist> {
        return emptyList()
    }

    override suspend fun getSongsByArtist(artistId: String): List<Song> {
        return emptyList()
    }

    override suspend fun getArtist(artistId: String): Artist? {
        return null
    }

    override fun observeLikedSongs(): Flow<List<Song>> {
        return _likedSongIds.map { ids ->
            baseSongs.filter { ids.contains(it.id) }.map { it.copy(isLiked = true) }
        }
    }

    override suspend fun toggleLike(songId: String) {
        val current = _likedSongIds.value.toMutableSet()
        if (current.contains(songId)) {
            current.remove(songId)
        } else {
            current.add(songId)
        }
        prefs.edit().putStringSet("liked_songs", current).apply()
        _likedSongIds.value = current
    }

    override suspend fun addRecentSong(songId: String) {
        val current = _recentSongIds.value.toMutableList()
        current.remove(songId)
        current.add(0, songId)
        val trimmed = current.take(20)
        prefs.edit().putString("recent_songs", trimmed.joinToString(",")).apply()
        _recentSongIds.value = trimmed
    }

    override fun observeRecentSongs(): Flow<List<Song>> {
        return combine(_recentSongIds, _likedSongIds) { recentIds, likedIds ->
            recentIds.mapNotNull { id -> baseSongs.find { it.id == id } }
               .map { song -> song.copy(isLiked = likedIds.contains(song.id)) }
        }
    }

    override fun observePlaylists(): Flow<List<Playlist>> = _playlists.asStateFlow()

    override suspend fun createPlaylist(name: String) {
        val newId = "playlist_${System.currentTimeMillis()}"
        val newPlaylist = Playlist(newId, name)
        val current = _playlists.value.toMutableList()
        current.add(0, newPlaylist)
        _playlists.value = current
        savePlaylistsToPrefs(current)
    }

    override suspend fun deletePlaylist(playlistId: String) {
        val current = _playlists.value.filter { it.id != playlistId }
        _playlists.value = current
        savePlaylistsToPrefs(current)
        prefs.edit().remove("playlist_${playlistId}_name").remove("playlist_${playlistId}_songs").apply()
    }

    override suspend fun addSongToPlaylist(playlistId: String, songId: String) {
        val current = _playlists.value.toMutableList()
        val index = current.indexOfFirst { it.id == playlistId }
        if (index != -1) {
            val p = current[index]
            if (!p.songIds.contains(songId)) {
                current[index] = p.copy(songIds = p.songIds + songId)
                _playlists.value = current
                savePlaylistsToPrefs(current)
            }
        }
    }

    override suspend fun removeSongFromPlaylist(playlistId: String, songId: String) {
        val current = _playlists.value.toMutableList()
        val index = current.indexOfFirst { it.id == playlistId }
        if (index != -1) {
            val p = current[index]
            current[index] = p.copy(songIds = p.songIds.filter { it != songId })
            _playlists.value = current
            savePlaylistsToPrefs(current)
        }
    }

    override fun observePlaylistSongs(playlistId: String): Flow<List<Song>> {
        return combine(_playlists, _likedSongIds) { playlists, likedIds ->
            val p = playlists.find { it.id == playlistId }
            p?.songIds?.mapNotNull { id -> baseSongs.find { it.id == id } }
                ?.map { song -> song.copy(isLiked = likedIds.contains(song.id)) }
                ?: emptyList()
        }
    }

    override suspend fun getAllSongs(): List<Song> {
        val liked = _likedSongIds.value
        return baseSongs.map { it.copy(isLiked = liked.contains(it.id)) }
    }

    override suspend fun getAlbums(): List<Album> {
        return emptyList() // Will be implemented with data later
    }

    override suspend fun getArtists(): List<Artist> {
        return emptyList() // Will be implemented with data later
    }
}