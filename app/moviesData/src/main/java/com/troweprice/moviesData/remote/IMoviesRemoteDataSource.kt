package com.troweprice.moviesData.remote

import com.troweprice.moviesData.model.GenreData
import com.troweprice.moviesData.model.MovieData

interface IMoviesRemoteDataSource {
    suspend fun getGenres(): Result<List<GenreData>>
    suspend fun getMovies(genre: String?, limit: Int, offset: Int): Result<List<MovieData>>
}
