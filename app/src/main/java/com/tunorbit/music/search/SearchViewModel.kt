package com.tunorbit.music.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tunorbit.music.core.model.Song
import com.tunorbit.music.core.repository.MusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _rawSearchResults = MutableStateFlow<List<Song>>(emptyList())

    val searchResults: StateFlow<List<Song>> = combine(
        _rawSearchResults,
        musicRepository.observeLikedSongs()
    ) { results, likedSongs ->
        val likedIds = likedSongs.map { it.id }.toSet()
        results.map { it.copy(isLiked = likedIds.contains(it.id)) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.isBlank()) {
            _rawSearchResults.value = emptyList()
            _isLoading.value = false
            return
        }

        searchJob = viewModelScope.launch {
            _isLoading.value = true
            delay(300) // Debounce rapid typing
            try {
                _rawSearchResults.value = musicRepository.searchSongs(query)
            } catch (e: Exception) {
                _rawSearchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class SearchViewModelFactory(
    private val musicRepository: MusicRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(musicRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
