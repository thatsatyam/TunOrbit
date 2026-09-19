package com.tunorbit.music.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tunorbit.music.core.model.Song

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onSongClick: (Song, List<Song>) -> Unit,
    onArtistClick: (String) -> Unit
) {
    val songs = viewModel.songs.collectAsState()
    val discoverSongs = viewModel.discoverSongs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSongs()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        HomeHeader(modifier = Modifier.padding(horizontal = 20.dp))

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Play for Me",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Music picked for your mood and taste",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        songs.value.firstOrNull()?.let { song ->
            PlayForMeCard(
                song = song,
                modifier = Modifier.padding(horizontal = 20.dp),
                onSongClick = { s -> onSongClick(s, listOf(s)) },
                onArtistClick = { onArtistClick(song.artistId) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (discoverSongs.value.isNotEmpty()) {
            HomeSection(
                title = "Discover on Audius",
                subtitle = "Trending tracks right now",
                songs = discoverSongs.value,
                onSongClick = { s -> onSongClick(s, discoverSongs.value) },
                onArtistClick = onArtistClick
            )

            Spacer(modifier = Modifier.height(30.dp))
        }

        // We can hook this up to Recently Played eventually, 
        // but for now, we leave Home as is.
        if (songs.value.isNotEmpty()) {
            HomeSection(
                title = "Today's Session",
                subtitle = "A few tracks to get you started",
                songs = songs.value,
                onSongClick = { s -> onSongClick(s, songs.value) },
                onArtistClick = onArtistClick
            )

            Spacer(modifier = Modifier.height(30.dp))

            HomeSection(
                title = "Continue Listening",
                subtitle = "Pick up where you left off",
                songs = songs.value,
                onSongClick = { s -> onSongClick(s, songs.value) },
                onArtistClick = onArtistClick
            )

            Spacer(modifier = Modifier.height(30.dp))

            HomeSection(
                title = "Made for You",
                subtitle = "Based on what you enjoy",
                songs = songs.value,
                onSongClick = { s -> onSongClick(s, songs.value) },
                onArtistClick = onArtistClick
            )

            Spacer(modifier = Modifier.height(30.dp))

            HomeSection(
                title = "Recently Discovered",
                subtitle = "Fresh music worth exploring",
                songs = songs.value,
                onSongClick = { s -> onSongClick(s, songs.value) },
                onArtistClick = onArtistClick
            )
        }
    }
}

@Composable
private fun HomeHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "TunOrbit",
            style = MaterialTheme.typography.headlineLarge
        )

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "♪",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun PlayForMeCard(
    song: Song,
    modifier: Modifier = Modifier,
    onSongClick: (Song) -> Unit,
    onArtistClick: () -> Unit
) {
    Card(
        onClick = { onSongClick(song) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "BECAUSE YOU'VE BEEN INTO LATE-NIGHT VIBES",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (song.artworkUrl != null) {
                        AsyncImage(
                            model = song.artworkUrl,
                            contentDescription = "${song.title} album artwork",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "♪",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = song.artistName,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable { onArtistClick() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = { onSongClick(song) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Play")
            }
        }
    }
}

@Composable
private fun HomeSection(
    title: String,
    subtitle: String,
    songs: List<Song>,
    modifier: Modifier = Modifier,
    onSongClick: (Song) -> Unit,
    onArtistClick: (String) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val availableWidth = maxWidth - 40.dp
        val visibleCards = if (availableWidth >= 340.dp) 3 else 2
        val spacing = 14.dp * (visibleCards - 1).toFloat()
        val cardWidth = (availableWidth - spacing) / visibleCards.toFloat()

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(
                    items = songs,
                    key = { _, song -> song.id }
                ) { index, song ->
                    SongCard(
                        song = song,
                        index = index,
                        sectionTitle = title,
                        cardWidth = cardWidth,
                        onSongClick = onSongClick,
                        onArtistClick = { onArtistClick(song.artistId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SongCard(
    song: Song,
    index: Int,
    sectionTitle: String,
    cardWidth: Dp,
    onSongClick: (Song) -> Unit,
    onArtistClick: () -> Unit
) {
        Column(
        modifier = Modifier
            .width(cardWidth)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSongClick(song) }
            .padding(bottom = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardWidth)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    when (index % 3) {
                        0 -> MaterialTheme.colorScheme.primaryContainer
                        1 -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.tertiaryContainer
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (song.artworkUrl != null) {
                AsyncImage(
                    model = song.artworkUrl,
                    contentDescription = "${song.title} album artwork",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "♪",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = song.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = song.artistName,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable { onArtistClick() }
        )

        if (sectionTitle == "Continue Listening") {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(
                            when (index % 3) {
                                0 -> 0.35f
                                1 -> 0.60f
                                else -> 0.80f
                            }
                        )
                        .height(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}