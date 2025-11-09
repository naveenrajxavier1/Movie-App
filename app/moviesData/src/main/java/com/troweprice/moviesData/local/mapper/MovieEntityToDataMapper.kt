package com.troweprice.moviesData.local.mapper

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesData.local.roomdb.MovieEntity
import com.troweprice.moviesData.model.MovieData
import javax.inject.Inject

/**
 * Maps a [MovieEntity] from the database to a [MovieData] object for the data layer.
 */
class MovieEntityToDataMapper @Inject constructor() : IMapper<MovieEntity, MovieData> {

    override fun map(input: MovieEntity): MovieData {
        return MovieData(
            id = input.id,
            genres = input.genres,
            releaseDate = input.releaseDate,
            title = input.title,
            tagline = input.tagline,
            overview = input.overview,
            url = input.url
        )
    }
}