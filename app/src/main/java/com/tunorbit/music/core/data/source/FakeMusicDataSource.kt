package com.tunorbit.music.core.data.source

import com.tunorbit.music.core.model.Artist
import com.tunorbit.music.core.model.Song

class FakeMusicDataSource : MusicDataSource {
    private val songs = listOf(
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
            releaseYear = 2020
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
            releaseYear = 2016
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
            releaseYear = 2017
        )
    )

    override suspend fun searchSongs(query: String): List<Song> {
        if (query.isBlank()) {
            return songs
        }

        return songs.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.artistName.contains(query, ignoreCase = true)
        }
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
}