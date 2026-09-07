package com.tunorbit.music

import com.tunorbit.music.core.data.source.FakeMusicDataSource
import com.tunorbit.music.core.data.source.MusicDataSource
import com.tunorbit.music.core.repository.DefaultMusicRepository
import com.tunorbit.music.core.repository.MusicRepository

class AppContainer {

    private val musicDataSource: MusicDataSource = FakeMusicDataSource()

    val musicRepository: MusicRepository =
        DefaultMusicRepository(musicDataSource)
}