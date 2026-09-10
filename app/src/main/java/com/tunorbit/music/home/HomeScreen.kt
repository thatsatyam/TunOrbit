package com.tunorbit.music.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import com.tunorbit.music.core.model.Song

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val songs = viewModel.songs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSongs()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        HomeHeader()

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Play for Me",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Music picked for your mood and taste",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        songs.value.firstOrNull()?.let { song ->
            PlayForMeCard(song = song)
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (songs.value.isNotEmpty()) {
            HomeSection(
                title = "Today's Session",
                subtitle = "A few tracks to get you started",
                songs = songs.value
            )

            Spacer(modifier = Modifier.height(30.dp))

            HomeSection(
                title = "Continue Listening",
                subtitle = "Pick up where you left off",
                songs = songs.value
            )

            Spacer(modifier = Modifier.height(30.dp))

            HomeSection(
                title = "Made for You",
                subtitle = "Based on what you enjoy",
                songs = songs.value
            )

            Spacer(modifier = Modifier.height(30.dp))

            HomeSection(
                title = "Recently Discovered",
                subtitle = "Fresh music worth exploring",
                songs = songs.value
            )
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
private fun PlayForMeCard(song: Song) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
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
                    Text(
                        text = "♪",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = song.artistName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    // Real playback will be connected here later.
                },
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
    songs: List<Song>
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val visibleCards = if (maxWidth >= 340.dp) 3 else 2
        val spacing = 14.dp * (visibleCards - 1).toFloat()
        val cardWidth = (maxWidth - spacing) / visibleCards.toFloat()

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
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
                        cardWidth = cardWidth
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
    cardWidth: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier.width(cardWidth)
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
            Text(
                text = "♪",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = song.title,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = song.artistName,
            style = MaterialTheme.typography.bodySmall
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