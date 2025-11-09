package com.troweprice.moviesData.local

import com.troweprice.moviesData.model.GenreData
import com.troweprice.moviesData.model.MovieData

/**
 * Interface defining the contract for local movie data operations.
 */
interface IMoviesLocalDataSource {

    /**
     * Retrieves a stream of all movies stored in the local database.
     * @return A Flow that emits a list of `MovieData` objects.
     */
    suspend fun getAllMovies(): List<MovieData>

    /**
     * Caches a list of movies by inserting them into the local database.
     * @param movies The list of `MovieData` objects to be saved.
     */
    suspend fun cacheMovies(movies: List<MovieData>)

    /**
     * Clears all movies from the local database.
     */
    suspend fun clearMovies()

    /**
     * Gets the total count of cached movies.
     * @return An integer representing the number of movies in the database.
     */
    suspend fun getCachedMoviesCount(): Int

    suspend fun getMoviesTotalCountByGenre(): Int
    
    suspend fun getAllGenres(): List<GenreData>

    suspend fun cacheGenres(genres: List<GenreData>)
}