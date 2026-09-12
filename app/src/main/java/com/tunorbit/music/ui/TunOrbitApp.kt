package com.tunorbit.music.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tunorbit.music.home.HomeScreen
import com.tunorbit.music.home.HomeViewModel
import com.tunorbit.music.library.LibraryScreen
import com.tunorbit.music.library.LibraryViewModel
import com.tunorbit.music.player.MiniPlayer
import com.tunorbit.music.player.PlayerScreen
import com.tunorbit.music.player.PlayerViewModel
import com.tunorbit.music.search.SearchScreen
import com.tunorbit.music.search.SearchViewModel

sealed class TopLevelDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : TopLevelDestination("home", "Home", Icons.Default.Home)
    object Search : TopLevelDestination("search", "Search", Icons.Default.Search)
    object Library : TopLevelDestination("library", "Library", Icons.Default.Favorite)
}

val destinations = listOf(
    TopLevelDestination.Home,
    TopLevelDestination.Search,
    TopLevelDestination.Library
)

@Composable
fun TunOrbitApp(
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel,
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentSong by playerViewModel.currentSong.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute != "player") {
                Column {
                    currentSong?.let { song ->
                        MiniPlayer(
                            song = song,
                            isPlaying = isPlaying,
                            onPlayPauseClick = playerViewModel::togglePlayPause,
                            onLikeClick = playerViewModel::toggleLike,
                            onPlayerClick = {
                                navController.navigate("player") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    destinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.title
                                )
                            },
                            label = { Text(destination.title) },
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    // Pop up to the start destination to avoid building up a large back stack
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(TopLevelDestination.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onSongClick = { song ->
                        playerViewModel.playSong(song)
                        navController.navigate("player") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(TopLevelDestination.Search.route) {
                SearchScreen(
                    viewModel = searchViewModel,
                    onSongClick = { song ->
                        playerViewModel.playSong(song)
                        navController.navigate("player") {
                            launchSingleTop = true
                        }
                    },
                    onBackClick = { navController.popBackStack(TopLevelDestination.Home.route, inclusive = false) }
                )
            }
            composable(TopLevelDestination.Library.route) {
                LibraryScreen(
                    viewModel = libraryViewModel,
                    onSongClick = { song ->
                        playerViewModel.playSong(song)
                        navController.navigate("player") {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("player") {
                PlayerScreen(
                    viewModel = playerViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
