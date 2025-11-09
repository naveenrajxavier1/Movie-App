package com.troweprice.moviesData.mapper

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesData.NoInternetConnection
import com.troweprice.moviesdomain.GenreResult
import javax.inject.Inject

class GenreDataErrorMapper @Inject constructor() : IMapper<Throwable, GenreResult.GenreError> {
    override fun map(input: Throwable): GenreResult.GenreError {
        return when (input) {
            is NoInternetConnection -> GenreResult.GenreError.NoInternetException
            else -> GenreResult.GenreError.GenericError(input.message?: "Generic Error")

        }
    }
}