package com.troweprice.moviesData.local

import com.troweprice.moviesData.local.mapper.GenreDataToEntityMapper
import com.troweprice.moviesData.local.mapper.GenreEntityToDataMapper
import com.troweprice.moviesData.local.mapper.MovieDataToEntityMapper
import com.troweprice.moviesData.local.mapper.MovieEntityToDataMapper
import com.troweprice.moviesData.local.roomdb.GenreDao
import com.troweprice.moviesData.local.roomdb.MovieDao
import com.troweprice.moviesData.model.GenreData
import com.troweprice.moviesData.model.MovieData
import javax.inject.Inject

class MoviesLocalDataSource @Inject constructor(
    private val movieDao: MovieDao,
    private val genreDao: GenreDao,
    private val movieEntityToDataMapper: MovieEntityToDataMapper,
    private val movieDataToEntityMapper: MovieDataToEntityMapper,
    private val genreEntityToDataMapper: GenreEntityToDataMapper,
    private val genreDataToEntityMapper: GenreDataToEntityMapper
) : IMoviesLocalDataSource {

    override suspend fun getAllMovies(): List<MovieData> {
        // Get the Flow of entities, then map the list to MovieData
        return movieDao.getAllMovies().map {
            movieEntityToDataMapper.map(it)
        }
    }

    override suspend fun cacheMovies(movies: List<MovieData>) {
        // Convert the list of MovieData to a list of entities before saving
        val entities = movies.map { movieDataToEntityMapper.map(it) }
        movieDao.insertAll(entities)
    }

    override suspend fun clearMovies() {
        movieDao.clearAll()
    }

    /**
     * Returns the total count of movies by calling the corresponding DAO method.
     */
    override suspend fun getCachedMoviesCount(): Int {
        return movieDao.getMoviesCount()
    }

    override suspend fun getAllGenres(): List<GenreData> {
        return genreDao.getAllGenres().map { entity -> genreEntityToDataMapper.map(entity) }
    }

    override suspend fun cacheGenres(genres: List<GenreData>) {
        val entities = genres.map { data -> genreDataToEntityMapper.map(data) }
        genreDao.insertAll(entities)
    }

    override suspend fun getMoviesTotalCountByGenre(): Int {
        return genreDao.getTotalMoviesCountByGenre()
    }
}
