package com.troweprice.moviesdomain

import javax.inject.Inject

private const val MOVIES_PAGE_COUNT = 10

class GetMovies @Inject constructor(private val moviesRepository: IMoviesRepository) {
    suspend operator fun invoke(isFreshLoading: Boolean, genre: String? = null): MoviesResult {
        return moviesRepository.getMovies(
            genre = genre,
            limit = MOVIES_PAGE_COUNT,
            isFreshLoading = isFreshLoading
        )
    }
}