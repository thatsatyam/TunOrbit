package com.tunorbit.music

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import com.tunorbit.music.core.data.source.FakeMusicDataSource
import com.tunorbit.music.core.data.source.MusicDataSource
import com.tunorbit.music.core.data.source.RemoteMusicDataSource
import com.tunorbit.music.core.repository.DefaultMusicRepository
import com.tunorbit.music.core.repository.MusicRepository

class AppContainer(private val context: Context) {

    val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(), 
                true
            )
            .build()
    }

    private val sharedPreferences by lazy {
        context.getSharedPreferences("tunorbit_prefs", Context.MODE_PRIVATE)
    }

    private val localMusicDataSource: MusicDataSource by lazy { 
        FakeMusicDataSource(sharedPreferences) 
    }

    private val remoteMusicDataSource: MusicDataSource by lazy {
        RemoteMusicDataSource()
    }

    val musicRepository: MusicRepository by lazy {
        DefaultMusicRepository(localMusicDataSource, remoteMusicDataSource)
    }
}