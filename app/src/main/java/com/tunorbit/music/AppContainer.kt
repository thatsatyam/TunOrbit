package com.tunorbit.music

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.tunorbit.music.core.data.source.FakeMusicDataSource
import com.tunorbit.music.core.data.source.MusicDataSource
import com.tunorbit.music.core.repository.DefaultMusicRepository
import com.tunorbit.music.core.repository.MusicRepository

class AppContainer(private val context: Context) {

    val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context).build()
    }

    private val sharedPreferences by lazy {
        context.getSharedPreferences("tunorbit_prefs", Context.MODE_PRIVATE)
    }

    private val musicDataSource: MusicDataSource by lazy { 
        FakeMusicDataSource(sharedPreferences) 
    }

    val musicRepository: MusicRepository by lazy {
        DefaultMusicRepository(musicDataSource)
    }
}