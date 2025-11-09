package com.troweprice.moviesapp.movieslisting.ui

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesapp.movieslisting.ui.mapper.GenreToGenreUiMapper
import com.troweprice.moviesapp.movieslisting.ui.mapper.MovieToMovieUiMapper
import com.troweprice.moviesapp.movieslisting.ui.model.GenreUi
import com.troweprice.moviesapp.movieslisting.ui.model.MovieUi
import com.troweprice.moviesdomain.model.Genre
import com.troweprice.moviesdomain.model.Movie
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
abstract class di {

    @Binds
    @ViewModelScoped
    abstract fun bindMovieToMovieUiMapper(
        impl: MovieToMovieUiMapper
    ): IMapper<Movie, MovieUi>

    @Binds
    @ViewModelScoped
    abstract fun bindGenreToGenreUiMapper(
        impl: GenreToGenreUiMapper
    ): IMapper<Genre, GenreUi>
}
