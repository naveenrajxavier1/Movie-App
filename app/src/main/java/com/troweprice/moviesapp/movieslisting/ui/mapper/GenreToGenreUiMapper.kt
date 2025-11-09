package com.troweprice.moviesapp.movieslisting.ui.mapper

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesdomain.model.Genre
import com.troweprice.moviesapp.movieslisting.ui.model.GenreUi
import javax.inject.Inject

/**
 * Maps a [Genre] domain object to a [GenreUi] presentation object.
 *
 * This mapper transforms the genre data into a display-ready format,
 * for instance, creating a formatted string like "Action (120 movies)".
 */
class GenreToGenreUiMapper @Inject constructor() : IMapper<Genre, GenreUi> {

    override fun map(input: Genre): GenreUi {
        return GenreUi(
            name = input.name,
            displayText = createDisplayText(input.name, input.movieCount), isSelected = false
        )
    }

    /**
     * Creates a formatted string for display purposes.
     */
    private fun createDisplayText(name: String, movieCount: Int): String {
        return "$name ($movieCount movies)"
    }
}