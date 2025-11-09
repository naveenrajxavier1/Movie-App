package com.troweprice.moviesdomain

import com.troweprice.moviesdomain.model.Genre
import com.troweprice.moviesdomain.model.Movie

sealed interface MoviesResult {
    data class Success(val movies: List<Movie>, val isLastPage: Boolean) : MoviesResult
    sealed class MoviesError : MoviesResult {
        data class GenericError(val error: String) : MoviesError()
        data object NoInternetException : MoviesError()
    }
}

sealed interface GenreResult {
    data class Success(val genres: List<Genre>) : GenreResult
    sealed class GenreError : GenreResult {
        data class GenericError(val error: String) : GenreError()
        data object NoInternetException : GenreError()
    }
}
