package com.troweprice.moviesData.remote

import com.troweprice.moviesData.model.GenreData
import com.troweprice.moviesData.model.MovieData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApi {
    @GET("api/genres")
    suspend fun getGenres(): Response<List<GenreData>>

    @GET("api/movies")
    suspend fun getMovies(
        @Query("genre") genre: String?,
        @Query("limit") limit: Int,
        @Query("from") offset: Int
    ): Response<List<MovieData>>
}