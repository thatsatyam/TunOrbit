package com.tunorbit.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import com.tunorbit.music.home.HomeScreen
import com.tunorbit.music.home.HomeViewModel
import com.tunorbit.music.home.HomeViewModelFactory
import com.tunorbit.music.ui.theme.TunOrbitTheme

class MainActivity : ComponentActivity() {

    private val appContainer = AppContainer()

    private val homeViewModel by lazy {
        HomeViewModelFactory(appContainer.musicRepository)
            .create(HomeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TunOrbitTheme {
                HomeScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding(),
                    viewModel = homeViewModel
                )
            }
        }
    }
}