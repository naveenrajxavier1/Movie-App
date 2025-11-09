package com.troweprice.moviesData.mapper

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesData.NoInternetConnection
import com.troweprice.moviesdomain.MoviesResult
import javax.inject.Inject

class MoviesDataErrorMapper @Inject constructor() : IMapper<Throwable, MoviesResult.MoviesError> {
    override fun map(input: Throwable): MoviesResult.MoviesError {
        return when (input) {
            is NoInternetConnection -> MoviesResult.MoviesError.NoInternetException
            else -> MoviesResult.MoviesError.GenericError(input.message ?: "Generic Error")
        }
    }
}