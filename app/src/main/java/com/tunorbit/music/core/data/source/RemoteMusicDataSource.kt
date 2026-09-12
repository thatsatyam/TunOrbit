package com.tunorbit.music.core.data.source

import com.tunorbit.music.core.model.Artist
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
}