package com.tunorbit.music.core.data.source

import android.content.SharedPreferences
import com.tunorbit.music.core.model.Artist
import com.tunorbit.music.core.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeMusicDataSource(private val prefs: SharedPreferences) : MusicDataSource {

    private val _likedSongIds = MutableStateFlow<Set<String>>(
        prefs.getStringSet("liked_songs", emptySet())?.toSet() ?: emptySet()
    )

    private val baseSongs = listOf(
        Song(
            id = "song_001",
            title = "Blinding Lights",
            artistId = "artist_001",
            artistName = "The Weeknd",
            albumId = "album_001",
            albumName = "After Hours",
            durationMs = 200040L,
            artworkUrl = null,
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
            artworkUrl = null,
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
}