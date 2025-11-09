package com.troweprice.moviesData.local.mapper

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesData.local.roomdb.MovieEntity
import com.troweprice.moviesData.model.MovieData
import javax.inject.Inject

/**
 * Maps a [MovieData] object from the data layer to a [MovieEntity] for the database.
 */
class MovieDataToEntityMapper @Inject constructor() : IMapper<MovieData, MovieEntity> {

    override fun map(input: MovieData): MovieEntity {
        return MovieEntity(
            id = input.id,
            genres = input.genres ?: emptyList(),
            releaseDate = input.releaseDate.orEmpty(),
            title = input.title.orEmpty(),
            tagline = input.tagline.orEmpty(),
            overview = input.overview.orEmpty(),
            url = input.url.orEmpty()
        )
    }
}