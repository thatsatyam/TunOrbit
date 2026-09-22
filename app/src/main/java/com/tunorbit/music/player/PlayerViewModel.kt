package com.tunorbit.music.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import com.tunorbit.music.core.model.Song
import com.tunorbit.music.core.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class PlayerViewModel(
    private val player: Player,
    private val musicRepository: MusicRepository
) : ViewModel() {
    private val _currentSong = MutableStateFlow<Song?>(null)
    
    val currentSong: StateFlow<Song?> = combine(
        _currentSong,
        musicRepository.observeLikedSongs()
    ) { current, likedSongs ->
        if (current == null) return@combine null
        current.copy(isLiked = likedSongs.any { it.id == current.id })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private var currentQueue: List<Song> = emptyList()

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            _currentPosition.value = player.currentPosition
        }
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            mediaItem?.mediaId?.let { id ->
                val song = currentQueue.find { it.id == id }
                if (song != null) {
                    _currentSong.value = song
                    if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                        viewModelScope.launch {
                            musicRepository.addRecentSong(song.id)
                        }
                    }
                }
            }
        }
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                _isPlaying.value = false
                _currentPosition.value = player.duration.coerceAtLeast(0L)
            }
        }
        override fun onPlayerError(error: PlaybackException) {
            _isPlaying.value = false
        }
    }

    init {
        player.addListener(playerListener)
        
        // Sync initial state in case player is already active (e.g. Activity recreation)
        _isPlaying.value = player.isPlaying
        _currentPosition.value = player.currentPosition
        if (player.currentMediaItem != null && _currentSong.value == null) {
            val id = player.currentMediaItem?.mediaId
            if (id != null) {
                // Try to find the song in the queue, or at least keep the ID
                _currentSong.value = currentQueue.find { it.id == id } 
            }
        }
        
        viewModelScope.launch {
            while(true) {
                if (player.isPlaying) {
                    _currentPosition.value = player.currentPosition
                }
                delay(1000)
            }
        }
    }

    fun playQueue(queue: List<Song>, startIndex: Int) {
        val song = queue.getOrNull(startIndex) ?: return
        
        if (_currentSong.value?.id == song.id && currentQueue.size == queue.size && currentQueue.firstOrNull()?.id == queue.firstOrNull()?.id) {
            if (!player.isPlaying) {
                player.play()
                _isPlaying.value = true
            }
            return
        }

        viewModelScope.launch {
            musicRepository.addRecentSong(song.id)
        }

        currentQueue = queue
        _currentSong.value = song
        
        player.clearMediaItems()
        val mediaItems = queue.map { 
            MediaItem.Builder()
                .setUri(it.mediaUrl)
                .setMediaId(it.id)
                .build()
        }
        player.setMediaItems(mediaItems, startIndex, 0L)
        player.prepare()
        player.play()
        _isPlaying.value = true
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
        } else {
            player.play()
            _isPlaying.value = true
        }
    }

    fun toggleLike() {
        _currentSong.value?.let { song ->
            viewModelScope.launch {
                musicRepository.toggleLike(song.id)
            }
        }
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        _currentPosition.value = positionMs
    }

    fun skipToNext() {
        if (player.hasNextMediaItem()) {
            player.seekToNext()
        }
    }

    fun skipToPrevious() {
        if (player.hasPreviousMediaItem()) {
            player.seekToPrevious()
        } else {
            player.seekTo(0L)
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.removeListener(playerListener)
        // We do NOT release the player here. 
        // The player lifecycle is tied to the AppContainer and MediaSessionService, 
        // allowing it to outlive this ViewModel for background playback.
    }
}

class PlayerViewModelFactory(
    private val player: Player,
    private val musicRepository: MusicRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerViewModel(player, musicRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
