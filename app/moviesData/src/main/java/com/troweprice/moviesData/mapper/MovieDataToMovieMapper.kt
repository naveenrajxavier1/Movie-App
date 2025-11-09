package com.troweprice.moviesData.mapper

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesData.model.MovieData
import com.troweprice.moviesdomain.model.Movie
import javax.inject.Inject

class MovieDataToMovieMapper @Inject constructor() : IMapper<MovieData, Movie> {
    override fun map(input: MovieData): Movie = Movie(
        id = input.id,
        genres = input.genres ?: emptyList(),
        releaseDate = input.releaseDate.orEmpty(),
        title = input.title.orEmpty(),
        overview = input.overview.orEmpty(),
        url = input.url.orEmpty()
    )
}
