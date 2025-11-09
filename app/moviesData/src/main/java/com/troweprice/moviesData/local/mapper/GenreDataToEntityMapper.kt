package com.troweprice.moviesData.local.mapper

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesData.local.roomdb.GenreEntity
import com.troweprice.moviesData.model.GenreData
import javax.inject.Inject

/**
 * Maps a [GenreData] object from the data layer to a [GenreEntity] for the database.
 */
class GenreDataToEntityMapper @Inject constructor() : IMapper<GenreData, GenreEntity> {

    override fun map(input: GenreData): GenreEntity {
        return GenreEntity(
            name = input.name,
            noOfMovies = input.noOfMovies
        )
    }
}