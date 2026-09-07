package com.tunorbit.music.core.repository

import com.tunorbit.music.core.data.source.MusicDataSource
import com.tunorbit.music.core.model.Artist
import com.tunorbit.music.core.model.Song

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
}