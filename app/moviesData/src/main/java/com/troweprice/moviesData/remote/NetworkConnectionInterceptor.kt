package com.troweprice.moviesData.remote

import com.troweprice.moviesData.remote.NetworkDetector // Make sure to import your detector
import com.troweprice.moviesData.NoInternetConnection
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class NetworkConnectionInterceptor @Inject constructor(
    private val networkDetector: NetworkDetector
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Before proceeding with the request, check for connectivity.
        if (!networkDetector.isConnected()) {
            // If there is no connection, throw our custom exception.
            // This will stop the request from ever being made.
            throw NoConnectivityException()
        }
        return chain.proceed(chain.request())
    }
}

class NoConnectivityException : IOException("No Internet Connection")