package com.tunorbit.music.core.data.source

import com.tunorbit.music.BuildConfig
import com.tunorbit.music.core.model.Album
import com.tunorbit.music.core.model.Artist
import com.tunorbit.music.core.model.Playlist
import com.tunorbit.music.core.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class RemoteMusicDataSource : MusicDataSource {

    private val baseUrl = "https://api.audius.co/v1"

    override suspend fun searchSongs(query: String): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()

        val results = mutableListOf<Song>()
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val appName = URLEncoder.encode(BuildConfig.AUDIUS_APP_NAME, "UTF-8")
            val urlString = "$baseUrl/tracks/search?query=$encodedQuery&app_name=$appName"

            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val responseString = reader.use { it.readText() }
                val jsonObject = JSONObject(responseString)
                
                if (jsonObject.has("data")) {
                    val dataArray = jsonObject.getJSONArray("data")
                    for (i in 0 until dataArray.length()) {
                        val trackObj = dataArray.getJSONObject(i)
                        
                        val id = trackObj.optString("id")
                        val title = trackObj.optString("title")
                        val durationSec = trackObj.optInt("duration", 0)
                        
                        // Parse user
                        var artistId = ""
                        var artistName = "Unknown Artist"
                        if (trackObj.has("user")) {
                            val userObj = trackObj.getJSONObject("user")
                            artistId = userObj.optString("id")
                            artistName = userObj.optString("name", "Unknown Artist")
                        }
                        
                        // Parse artwork
                        var artworkUrl: String? = null
                        if (trackObj.has("artwork")) {
                            val artworkObj = trackObj.optJSONObject("artwork")
                            if (artworkObj != null) {
                                artworkUrl = artworkObj.optString("480x480", "")
                                if (artworkUrl.isEmpty()) {
                                    artworkUrl = artworkObj.optString("150x150", "")
                                }
                                if (artworkUrl.isEmpty()) {
                                    artworkUrl = null
                                }
                            }
                        }
                        
                        if (id.isNotBlank() && title.isNotBlank()) {
                            val streamUrl = "$baseUrl/tracks/$id/stream?app_name=$appName"
                            
                            results.add(
                                Song(
                                    id = id,
                                    title = title,
                                    artistId = artistId,
                                    artistName = artistName,
                                    durationMs = durationSec * 1000L,
                                    artworkUrl = artworkUrl,
                                    mediaUrl = streamUrl
                                )
                            )
                        }
                    }
                }
            }
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
            // Return empty list on failure so app doesn't crash
        }
        
        results
    }

    override suspend fun getDiscoverSongs(): List<Song> = withContext(Dispatchers.IO) {
        val results = mutableListOf<Song>()
        try {
            val appName = URLEncoder.encode(BuildConfig.AUDIUS_APP_NAME, "UTF-8")
            val urlString = "$baseUrl/tracks/trending?app_name=$appName&time=week"

            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val responseString = reader.use { it.readText() }
                val jsonObject = JSONObject(responseString)
                
                if (jsonObject.has("data")) {
                    val dataArray = jsonObject.getJSONArray("data")
                    for (i in 0 until dataArray.length().coerceAtMost(15)) {
                        val trackObj = dataArray.getJSONObject(i)
                        
                        val id = trackObj.optString("id")
                        val title = trackObj.optString("title")
                        val durationSec = trackObj.optInt("duration", 0)
                        
                        // Parse user
                        var artistId = ""
                        var artistName = "Unknown Artist"
                        if (trackObj.has("user")) {
                            val userObj = trackObj.getJSONObject("user")
                            artistId = userObj.optString("id")
                            artistName = userObj.optString("name", "Unknown Artist")
                        }
                        
                        // Parse artwork
                        var artworkUrl: String? = null
                        if (trackObj.has("artwork")) {
                            val artworkObj = trackObj.optJSONObject("artwork")
                            if (artworkObj != null) {
                                artworkUrl = artworkObj.optString("480x480", "")
                                if (artworkUrl.isEmpty()) {
                                    artworkUrl = artworkObj.optString("150x150", "")
                                }
                                if (artworkUrl.isEmpty()) {
                                    artworkUrl = null
                                }
                            }
                        }
                        
                        if (id.isNotBlank() && title.isNotBlank()) {
                            val streamUrl = "$baseUrl/tracks/$id/stream?app_name=$appName"
                            
                            results.add(
                                Song(
                                    id = id,
                                    title = title,
                                    artistId = artistId,
                                    artistName = artistName,
                                    durationMs = durationSec * 1000L,
                                    artworkUrl = artworkUrl,
                                    mediaUrl = streamUrl
                                )
                            )
                        }
                    }
                }
            }
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        results
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

    override fun observeRecentSearches(): Flow<List<String>> {
        return emptyFlow()
    }

    override suspend fun addRecentSearch(query: String) {}

    override suspend fun clearRecentSearches() {}
}