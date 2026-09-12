package com.tunorbit.music.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.tunorbit.music.core.model.Song
import com.tunorbit.music.core.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class PlayerViewModel(
    private val player: Player,
    private val musicRepository: MusicRepository
) : ViewModel() {
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    init {
        player.addListener(object : Player.Listener {
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
        })
        
        viewModelScope.launch {
            while(true) {
                if (player.isPlaying) {
                    _currentPosition.value = player.currentPosition
                }
                delay(1000)
            }
        }
    }

    fun playSong(song: Song) {
        if (_currentSong.value?.id == song.id) {
            // Already playing this song, just resume
            if (!player.isPlaying) {
                player.play()
                _isPlaying.value = true
            }
            return
        }

        _currentSong.value = song
        val mediaItem = MediaItem.fromUri(song.mediaUrl)
        player.setMediaItem(mediaItem)
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
                _currentSong.value = song.copy(isLiked = !song.isLiked)
            }
        }
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        _currentPosition.value = positionMs
    }

    // In a real app we would manage a playlist. For now, we simulate skip next/prev.
    fun skipToNext() {
        // TODO: implement real playlist logic
    }

    fun skipToPrevious() {
        // TODO: implement real playlist logic
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
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
