package com.troweprice.moviesData.remote

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesData.HttpError
import com.troweprice.moviesData.MoviesDataError
import com.troweprice.moviesData.model.GenreData
import com.troweprice.moviesData.model.MovieData
import retrofit2.Response
import javax.inject.Inject

class MoviesRemoteDataSource @Inject constructor(
    private val moviesApi: MoviesApi,
    private val moviesDataErrorMapper: IMapper<Exception, MoviesDataError>
) :
    IMoviesRemoteDataSource {
    override suspend fun getGenres(): Result<List<GenreData>> {
        return moviesApi.getGenres().convertToResult()
    }

    override suspend fun getMovies(genre: String?, limit: Int, offset: Int): Result<List<MovieData>> {
        return moviesApi.getMovies(genre = genre, limit = limit, offset = offset).convertToResult()
    }

    private fun <T> Response<T>.convertToResult(): Result<T> {
        return runCatching {
            if (isSuccessful) {
                val body = body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(HttpError(code(), "Response body was null"))
                }
            } else {
                val errorBodyString = errorBody()?.string() ?: "Generic Error"
                Result.failure(HttpError(code(), errorBodyString))
            }
        }.getOrElse {
            Result.failure(moviesDataErrorMapper.map(it as Exception))
        }
    }
}

