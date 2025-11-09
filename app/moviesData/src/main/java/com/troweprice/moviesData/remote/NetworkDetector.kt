package com.troweprice.moviesData.remote

// Create a new file, e.g., NetworkDetector.kt, in a shared or utility module.
// Or place it within your :moviesData module if it's only used there.

import android.Manifest
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import javax.inject.Inject

// The interface allows for easy testing with a fake implementation.
interface NetworkDetector {
    fun isConnected(): Boolean
}

// The implementation that uses the Android framework.
class AndroidNetworkDetector @Inject constructor(
    private val connectivityManager: ConnectivityManager
) : NetworkDetector {

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun isConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}