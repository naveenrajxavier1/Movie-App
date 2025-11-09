package com.troweprice.moviesData.local.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object for the genres table.
 */
@Dao
interface GenreDao {

    /**
     * Fetches all genres from the genres table, ordered by name.
     * @return A Flow that emits a list of all genres.
     */
    @Query("SELECT * FROM genres ORDER BY name ASC")
    fun getAllGenres(): List<GenreEntity>

    /**
     * Returns the total number of movies across all genres.
     * @return The count of movies.
     */
    @Query("SELECT SUM(movie_count) FROM genres")
    suspend fun getTotalMoviesCountByGenre(): Int

    /**
     * Inserts a list of genres. If a genre already exists, it will be replaced.
     * @param genres The list of genres to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(genres: List<GenreEntity>)
}