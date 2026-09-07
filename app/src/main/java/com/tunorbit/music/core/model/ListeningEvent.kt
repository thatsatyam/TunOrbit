package com.tunorbit.music.core.model

/**
 * Records one listening interaction so TunOrbit can learn
 * from the user's real behavior over time.
 */
data class ListeningEvent(
    val id: String,
    val songId: String,
    val timestamp: Long,
    val playedDurationMs: Long,
    val completionPercentage: Float,
    val action: ListeningAction,
    val context: ListeningContext? = null
)

/**
 * The user's direct or inferred action during a listening event.
 */
enum class ListeningAction {
    PLAYED,
    COMPLETED,
    SKIPPED,
    LIKED,
    DISLIKED,
    REPLAYED,
    ADDED_TO_PLAYLIST
}

/**
 * Optional context in which the song was played.
 *
 * Context is intentionally nullable because TunOrbit should
 * learn naturally without forcing users to choose a mood first.
 */
enum class ListeningContext {
    MORNING,
    AFTERNOON,
    EVENING,
    NIGHT,
    STUDY,
    WORKOUT,
    TRAVEL,
    RELAX,
    SLEEP
}