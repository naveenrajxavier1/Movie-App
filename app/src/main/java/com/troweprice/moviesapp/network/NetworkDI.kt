package com.troweprice.moviesapp.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object NetworkDI {
    @Provides
    @Named("IO")
    fun providesIODispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}
