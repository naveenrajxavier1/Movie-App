package com.troweprice.moviesData.di

import android.content.Context
import android.net.ConnectivityManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.troweprice.moviesData.model.GenreData
import com.troweprice.moviesData.model.GenreDeserializer
import com.troweprice.moviesData.remote.AndroidNetworkDetector
import com.troweprice.moviesData.remote.MoviesApi
import com.troweprice.moviesData.remote.NetworkConnectionInterceptor
import com.troweprice.moviesData.remote.NetworkDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

const val BASEURL = "https://movies-app-backend.replit.app/"

@InstallIn(SingletonComponent::class)
@Module
object NetworkDI {
    @Provides
    @Singleton
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    @Singleton
    fun provideNetworkDetector(connectivityManager: ConnectivityManager): NetworkDetector {
        return AndroidNetworkDetector(connectivityManager) // Assuming you have this class
    }

    @Provides
    @Singleton
    fun providesRetrofit(client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASEURL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun providesGson(): Gson = GsonBuilder()
        .registerTypeAdapter(
            GenreData::
            class.java, GenreDeserializer()
        )
        .create()

    @Provides
    @Singleton
    fun providesOkkHttpClient(networkConnectionInterceptor: NetworkConnectionInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(networkConnectionInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .build()

    }

    @Provides
    @Singleton
    fun providesMoviesApiService(retrofit: Retrofit): MoviesApi {
        return retrofit.create(MoviesApi::class.java)
    }
}