package com.troweprice.moviesData

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesData.local.IMoviesLocalDataSource
import com.troweprice.moviesData.model.GenreData
import com.troweprice.moviesData.model.MovieData
import com.troweprice.moviesData.remote.IMoviesRemoteDataSource
import com.troweprice.moviesdomain.GenreResult
import com.troweprice.moviesdomain.MoviesResult
import com.troweprice.moviesdomain.model.Genre
import com.troweprice.moviesdomain.model.Movie
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

const val DEFAULT_MOVIES_PAGE_COUNT = 500

class MoviesRepository @Inject constructor(
    private val moviesRemoteRepository: IMoviesRemoteDataSource,
    private val moviesLocalRepository: IMoviesLocalDataSource,
    private val genreDataToGenreMapper: IMapper<GenreData, Genre>,
    private val movieDataToMovieMapper: IMapper<MovieData, Movie>,
    private val moviesDataErrorMapper: IMapper<Throwable, MoviesResult.MoviesError>,
    private val genreDataErrorMapper: IMapper<Throwable, GenreResult.GenreError>,
    @Named("IO") private val coroutineDispatcher: CoroutineDispatcher
) : com.troweprice.moviesdomain.IMoviesRepository {

    override suspend fun getGenres(): GenreResult {
        return withContext(coroutineDispatcher) {
            try {
                moviesRemoteRepository.getGenres().fold(onSuccess = { genres ->
                    return@withContext GenreResult.Success(genres.map {
                        genreDataToGenreMapper.map(it)
                    })
                }, onFailure = {
                    return@withContext genreDataErrorMapper.map(it)
                })

            } catch (e: Exception) {
                return@withContext genreDataErrorMapper.map(e)
            }
        }
    }

    override suspend fun getMovies(
        genre: String?,
        limit: Int?,
        isFreshLoading: Boolean
    ): MoviesResult {
        return withContext(coroutineDispatcher) {
            try {
                val requestedPageSize = limit ?: DEFAULT_MOVIES_PAGE_COUNT

                if (isFreshLoading) {
                    moviesLocalRepository.clearMovies()
                }

                val offset =
                    if (isFreshLoading) 0 else moviesLocalRepository.getCachedMoviesCount() + 1
               // Log.v("TestMovies", "pagination offset $offset and $isFreshLoading")
                val remoteMovies = moviesRemoteRepository.getMovies(
                    genre = genre,
                    limit = requestedPageSize,
                    offset = offset
                ).getOrThrow()

                moviesLocalRepository.cacheMovies(remoteMovies)

                val mappedMovies =
                    moviesLocalRepository.getAllMovies().map { movieDataToMovieMapper.map(it) }

                return@withContext MoviesResult.Success(
                    movies = mappedMovies,
                    isLastPage = remoteMovies.size < requestedPageSize
                )
            } catch (e: Exception) {
                return@withContext moviesDataErrorMapper.map(e)
            }
        }
    }
}