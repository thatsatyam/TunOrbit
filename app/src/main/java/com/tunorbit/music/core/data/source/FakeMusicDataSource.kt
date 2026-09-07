package com.tunorbit.music.core.data.source

import com.tunorbit.music.core.model.Artist
import com.tunorbit.music.core.model.Song

class FakeMusicDataSource : MusicDataSource {

    override suspend fun searchSongs(query: String): List<Song> {
        return emptyList()
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