package com.troweprice.moviesData.di

import android.content.Context
import androidx.room.Room
import com.troweprice.commonlib.IMapper
import com.troweprice.moviesData.MoviesDataError
import com.troweprice.moviesData.MoviesRepository
import com.troweprice.moviesData.local.IMoviesLocalDataSource
import com.troweprice.moviesData.local.MoviesLocalDataSource
import com.troweprice.moviesData.local.roomdb.AppDatabase
import com.troweprice.moviesData.local.roomdb.GenreDao
import com.troweprice.moviesData.local.roomdb.MovieDao
import com.troweprice.moviesData.mapper.GenreDataErrorMapper
import com.troweprice.moviesData.mapper.GenreDataToGenreMapper
import com.troweprice.moviesData.mapper.MovieDataToMovieMapper
import com.troweprice.moviesData.mapper.MoviesDataErrorMapper
import com.troweprice.moviesData.model.GenreData
import com.troweprice.moviesData.model.MovieData
import com.troweprice.moviesData.remote.IMoviesRemoteDataSource
import com.troweprice.moviesData.remote.MoviesRemoteDataSource
import com.troweprice.moviesData.remote.mapper.NetworkExceptionToErrorMapper
import com.troweprice.moviesdomain.GenreResult
import com.troweprice.moviesdomain.IMoviesRepository
import com.troweprice.moviesdomain.MoviesResult
import com.troweprice.moviesdomain.model.Genre
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object DI {
    @Provides
    fun provideMovieRepository(moviesRepository: MoviesRepository): IMoviesRepository =
        moviesRepository

    @Provides
    fun provideMovieRemoteDataSource(moviesRemoteDataSource: MoviesRemoteDataSource): IMoviesRemoteDataSource =
        moviesRemoteDataSource

    @Provides
    fun provideMoviesLocalDataSource(moviesLocalDataSource: MoviesLocalDataSource): IMoviesLocalDataSource =
        moviesLocalDataSource

    @Provides
    fun getDataBase(@ApplicationContext applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(
            context = applicationContext,
            AppDatabase::class.java, "MoviesDB"
        ).build()
    }

    @Provides
    fun provideMovieDao(appDatabase: AppDatabase): MovieDao {
        return appDatabase.movieDao()
    }

    @Provides
    fun provideGenreDao(appDatabase: AppDatabase): GenreDao {
        return appDatabase.genreDao()
    }

    @Provides
    fun providesGenreDataErrorMapper(impl: GenreDataErrorMapper): IMapper<Throwable, GenreResult.GenreError> =
        impl

    @Provides
    fun providesNetworkExceptionToErrorMapper(impl: NetworkExceptionToErrorMapper): IMapper<Exception, MoviesDataError> =
        impl

    @Provides
    fun providesMoviesDataErrorMapper(impl: MoviesDataErrorMapper): IMapper<Throwable, MoviesResult.MoviesError> =
        impl

    @Provides
    fun providesGenreDataToGenreMapper(impl: GenreDataToGenreMapper): IMapper<GenreData, Genre> =
        impl

    @Provides
    fun providesMovieDataToMovieMapper(impl: MovieDataToMovieMapper): IMapper<MovieData, com.troweprice.moviesdomain.model.Movie> =
        impl
}