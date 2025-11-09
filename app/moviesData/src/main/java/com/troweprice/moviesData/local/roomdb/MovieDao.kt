package com.troweprice.moviesData.local.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object for the movies table.
 */
@Dao
interface MovieDao {

    /**
     * Fetches all movies from the movies table, ordered by title.
     *
     * @return A Flow that emits a list of all movies in the database.
     *         The Flow will automatically update and emit a new list
     *         whenever the data in the movies table changes.
     */
    @Query("SELECT * FROM movies ORDER BY title ASC")
    fun getAllMovies(): List<MovieEntity>

    /**
     * Inserts a list of movies into the database. If a movie already exists,
     * it will be replaced.
     *
     * @param movies The list of movies to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    /**
     * Returns the total number of movies in the table.
     * @return The count of movies.
     */
    @Query("SELECT COUNT(id) FROM movies")
    suspend fun getMoviesCount(): Int

    /**
     * Deletes all movies from the movies table.
     */
    @Query("DELETE FROM movies")
    suspend fun clearAll()
}
