package com.troweprice.moviesdomain

interface IMoviesRepository {
    suspend fun getGenres(): GenreResult
    suspend fun getMovies(
        genre: String?,
        limit: Int?,
        isFreshLoading: Boolean = false
    ): MoviesResult
}