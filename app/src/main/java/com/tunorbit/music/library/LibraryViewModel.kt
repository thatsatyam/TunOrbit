package com.tunorbit.music.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tunorbit.music.core.model.Song
import com.tunorbit.music.core.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _recentSongs = MutableStateFlow<List<Song>>(emptyList())
    val recentSongs: StateFlow<List<Song>> = _recentSongs.asStateFlow()

    val likedSongs: StateFlow<List<Song>> = musicRepository.observeLikedSongs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadLibraryData()
    }

    private fun loadLibraryData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // In a real app, these would be separate repository calls (e.g., getLikedSongs(), getRecentSongs())
                val allSongs = musicRepository.searchSongs("")
                
                if (allSongs.isNotEmpty()) {
                    _recentSongs.value = allSongs.reversed().take(2)
                }
            } catch (e: Exception) {
                // Handled gracefully by remaining empty
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class LibraryViewModelFactory(
    private val musicRepository: MusicRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LibraryViewModel(musicRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
