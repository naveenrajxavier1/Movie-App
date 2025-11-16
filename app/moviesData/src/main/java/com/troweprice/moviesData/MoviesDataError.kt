package com.troweprice.moviesData

import java.lang.RuntimeException

/**
 * Please extend the class for new errors
 */
sealed class MoviesDataError : RuntimeException()
data class GenericError(val errorMessage: String) : MoviesDataError()
data class HttpError(val code: Int, val errorMessage: String) : MoviesDataError()
data object NoInternetConnection : MoviesDataError() {
    private fun readResolve(): Any = NoInternetConnection
}