package com.troweprice.moviesapp.movieslisting.ui.mapper

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesapp.movieslisting.ui.model.MovieUi
import com.troweprice.moviesdomain.model.Movie
import javax.inject.Inject

/**
 * Maps a [Movie] domain object to a [MovieUi] presentation object.
 *
 * This mapper performs the following transformations:
 * - Extracts the year from the `releaseDate` string.
 * - Joins the list of `genres` into a single, comma-separated string.
 */
class MovieToMovieUiMapper @Inject constructor() : IMapper<Movie, MovieUi> {

    override fun map(input: Movie): MovieUi {
        return MovieUi(
            id = input.id,
            title = input.title,
            overview = input.overview,
            url = input.url,
            genres = input.genres.joinToString(separator = ", "),
            releaseYear = getYearFromDate(input.releaseDate)
        )
    }

    /**
     * Safely extracts the year from a date string (e.g., "YYYY-MM-DD").
     * Returns the original string or a placeholder if the format is unexpected.
     */
    private fun getYearFromDate(dateString: String): String {
        return try {
            // Assuming the date format is "YYYY-MM-DD"
            if (dateString.contains("-")) {
                dateString.substringBefore("-")
            } else {
                dateString // Return as-is if format is not as expected
            }
        } catch (e: Exception) {
            // In case of any unexpected errors (e.g., IndexOutOfBoundsException)
            "N/A"
        }
    }
}