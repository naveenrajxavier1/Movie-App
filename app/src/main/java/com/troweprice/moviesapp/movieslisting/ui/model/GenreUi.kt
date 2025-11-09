package com.troweprice.moviesapp.movieslisting.ui.model

/**
 * Represents the UI model for a genre, formatted for display.
 *
 * @param name The name of the genre.
 * @param displayText A formatted string combining the name and the number of movies,
 *e.g., "Action (120 movies)".
 */
data class GenreUi(
    val name: String,
    val displayText: String,
    val isSelected: Boolean,
)