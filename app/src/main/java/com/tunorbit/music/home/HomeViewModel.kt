package com.tunorbit.music.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tunorbit.music.core.model.Song
import com.tunorbit.music.core.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _rawSongs = MutableStateFlow<List<Song>>(emptyList())
    private val _rawDiscoverSongs = MutableStateFlow<List<Song>>(emptyList())
    private val _rawRecommendedSongs = MutableStateFlow<List<Song>>(emptyList())
    
    val songs: StateFlow<List<Song>> = combine(
        _rawSongs,
        musicRepository.observeLikedSongs()
    ) { rawSongs, likedSongs ->
        val likedIds = likedSongs.map { it.id }.toSet()
        rawSongs.map { it.copy(isLiked = likedIds.contains(it.id)) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val discoverSongs: StateFlow<List<Song>> = combine(
        _rawDiscoverSongs,
        musicRepository.observeLikedSongs()
    ) { rawSongs, likedSongs ->
        val likedIds = likedSongs.map { it.id }.toSet()
        rawSongs.map { it.copy(isLiked = likedIds.contains(it.id)) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val recommendedSongs: StateFlow<List<Song>> = combine(
        _rawRecommendedSongs,
        musicRepository.observeLikedSongs()
    ) { rawSongs, likedSongs ->
        val likedIds = likedSongs.map { it.id }.toSet()
        rawSongs.map { it.copy(isLiked = likedIds.contains(it.id)) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadSongs() {
        viewModelScope.launch {
            _isLoading.value = true
            _rawSongs.value = musicRepository.searchSongs("")
            _rawDiscoverSongs.value = musicRepository.getDiscoverSongs()
            _rawRecommendedSongs.value = musicRepository.getRecommendedSongs()
            _isLoading.value = false
        }
    }
}

class HomeViewModelFactory(
    private val musicRepository: MusicRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(musicRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}