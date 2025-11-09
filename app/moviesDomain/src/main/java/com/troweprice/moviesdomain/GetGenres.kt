package com.troweprice.moviesdomain

import javax.inject.Inject

class GetGenres @Inject constructor(private val moviesRepository: IMoviesRepository) {
    suspend operator fun invoke(): GenreResult {
        return moviesRepository.getGenres()
    }
}