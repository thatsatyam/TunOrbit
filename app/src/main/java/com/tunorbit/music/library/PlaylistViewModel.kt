package com.tunorbit.music.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tunorbit.music.core.model.Playlist
import com.tunorbit.music.core.model.Song
import com.tunorbit.music.core.repository.MusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _playlistId = MutableStateFlow<String?>(null)

    val playlist: StateFlow<Playlist?> = combine(_playlistId, musicRepository.observePlaylists()) { id, playlists ->
        playlists.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    val allSongs: StateFlow<List<Song>> = _allSongs.asStateFlow()

    private var job: Job? = null

    fun loadPlaylist(id: String) {
        _playlistId.value = id
        job?.cancel()
        job = viewModelScope.launch {
            musicRepository.observePlaylistSongs(id).collect {
                _songs.value = it
            }
        }
        viewModelScope.launch {
            _allSongs.value = musicRepository.getAllSongs()
        }
    }

    fun deletePlaylist() {
        _playlistId.value?.let { id ->
            viewModelScope.launch { musicRepository.deletePlaylist(id) }
        }
    }

    fun addSong(songId: String) {
        _playlistId.value?.let { id ->
            viewModelScope.launch { 
                musicRepository.addSongToPlaylist(id, songId) 
                _allSongs.value = musicRepository.getAllSongs() // refresh state to hide added song
            }
        }
    }

    fun removeSong(songId: String) {
        _playlistId.value?.let { id ->
            viewModelScope.launch { musicRepository.removeSongFromPlaylist(id, songId) }
        }
    }
}

class PlaylistViewModelFactory(
    private val musicRepository: MusicRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaylistViewModel(musicRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
