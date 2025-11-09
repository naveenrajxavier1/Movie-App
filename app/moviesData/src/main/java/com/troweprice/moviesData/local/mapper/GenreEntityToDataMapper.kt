package com.troweprice.moviesData.local.mapper

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesData.local.roomdb.GenreEntity
import com.troweprice.moviesData.model.GenreData
import javax.inject.Inject

/**
 * Maps a [GenreEntity] from the database to a [GenreData] object for the data layer.
 */
class GenreEntityToDataMapper @Inject constructor() : IMapper<GenreEntity, GenreData> {

    override fun map(input: GenreEntity): GenreData {
        return GenreData(
            name = input.name,
            noOfMovies = input.noOfMovies
        )
    }
}