package com.tunorbit.music.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tunorbit.music.core.model.Album
import com.tunorbit.music.core.model.Song
import com.tunorbit.music.core.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlbumViewModel(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _album = MutableStateFlow<Album?>(null)
    val album: StateFlow<Album?> = _album.asStateFlow()

    private val _rawSongs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = combine(
        _rawSongs,
        musicRepository.observeLikedSongs()
    ) { raw, liked ->
        val likedIds = liked.map { it.id }.toSet()
        raw.map { it.copy(isLiked = likedIds.contains(it.id)) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadAlbum(albumId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _album.value = musicRepository.getAlbum(albumId)
            _rawSongs.value = musicRepository.getSongsByAlbum(albumId)
            _isLoading.value = false
        }
    }
}

class AlbumViewModelFactory(
    private val musicRepository: MusicRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlbumViewModel(musicRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
