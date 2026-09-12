package com.tunorbit.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import com.tunorbit.music.home.HomeViewModel
import com.tunorbit.music.home.HomeViewModelFactory
import com.tunorbit.music.library.LibraryViewModel
import com.tunorbit.music.library.LibraryViewModelFactory
import com.tunorbit.music.player.PlayerViewModel
import com.tunorbit.music.player.PlayerViewModelFactory
import com.tunorbit.music.search.SearchViewModel
import com.tunorbit.music.search.SearchViewModelFactory
import com.tunorbit.music.ui.TunOrbitApp
import com.tunorbit.music.ui.theme.TunOrbitTheme

class MainActivity : ComponentActivity() {

    private val appContainer by lazy { AppContainer(applicationContext) }

    private val homeViewModel by lazy {
        HomeViewModelFactory(appContainer.musicRepository)
            .create(HomeViewModel::class.java)
    }

    private val searchViewModel by lazy {
        SearchViewModelFactory(appContainer.musicRepository)
            .create(SearchViewModel::class.java)
    }

    private val libraryViewModel by lazy {
        LibraryViewModelFactory(appContainer.musicRepository)
            .create(LibraryViewModel::class.java)
    }

    private val playerViewModel by lazy {
        PlayerViewModelFactory(appContainer.exoPlayer, appContainer.musicRepository)
            .create(PlayerViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TunOrbitTheme {
                TunOrbitApp(
                    homeViewModel = homeViewModel,
                    searchViewModel = searchViewModel,
                    libraryViewModel = libraryViewModel,
                    playerViewModel = playerViewModel
                )
            }
        }
    }
}