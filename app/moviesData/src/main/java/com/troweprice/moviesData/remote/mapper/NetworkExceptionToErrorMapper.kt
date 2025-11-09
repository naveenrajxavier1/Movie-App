package com.troweprice.moviesData.remote.mapper

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesData.GenericError
import com.troweprice.moviesData.MoviesDataError
import com.troweprice.moviesData.NoInternetConnection
import com.troweprice.moviesData.remote.NoConnectivityException
import java.io.IOException
import javax.inject.Inject

class NetworkExceptionToErrorMapper @Inject constructor() : IMapper<Exception, MoviesDataError> {
    override fun map(input: Exception): MoviesDataError {
        return when (input) {
            is NoConnectivityException -> NoInternetConnection
            is IOException -> NoInternetConnection
            else -> GenericError(input.message ?: "An unexpected error occurred")
        }
    }
}

