package com.troweprice.moviesData

import com.troweprice.commonlib.IMapper
import com.troweprice.moviesData.local.IMoviesLocalDataSource
import com.troweprice.moviesData.model.GenreData
import com.troweprice.moviesData.model.MovieData
import com.troweprice.moviesData.remote.IMoviesRemoteDataSource
import com.troweprice.moviesdomain.GenreResult
import com.troweprice.moviesdomain.IMoviesRepository
import com.troweprice.moviesdomain.MoviesResult
import com.troweprice.moviesdomain.model.Genre
import com.troweprice.moviesdomain.model.Movie
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class MoviesRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    // SUT (System Under Test)
    private lateinit var moviesRepository: IMoviesRepository

    @MockK
    private lateinit var moviesRemoteDataSource: IMoviesRemoteDataSource

    @MockK
    private lateinit var moviesLocalDataSource: IMoviesLocalDataSource

    @MockK
    private lateinit var genreDataToGenreMapper: IMapper<GenreData, Genre>

    @MockK
    private lateinit var movieDataToMovieMapper: IMapper<MovieData, Movie>

    @MockK
    private lateinit var moviesDataErrorMapper: IMapper<Throwable, MoviesResult.MoviesError>

    @MockK
    private lateinit var genreDataErrorMapper: IMapper<Throwable, GenreResult.GenreError>

    private val testDispatcher = StandardTestDispatcher()

    // Mock data for tests
    private val mockMovieData = mockk<MovieData>()
    private val mockMovie = mockk<Movie>()
    private val mockGenreData = mockk<GenreData>()
    private val mockGenre = mockk<Genre>()

    @Before
    fun setUp() {
        every { movieDataToMovieMapper.map(any()) } returns mockMovie
        every { genreDataToGenreMapper.map(any()) } returns mockGenre

        // Create the repository instance for each test
        moviesRepository = MoviesRepository(
            moviesRemoteRepository = moviesRemoteDataSource,
            moviesLocalRepository = moviesLocalDataSource,
            genreDataToGenreMapper = genreDataToGenreMapper,
            movieDataToMovieMapper = movieDataToMovieMapper,
            moviesDataErrorMapper = moviesDataErrorMapper,
            genreDataErrorMapper = genreDataErrorMapper,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `getMovies on fresh load - success - clears cache, fetches and caches new data`() =
        runTest(testDispatcher) {
            // GIVEN: A fresh load is requested
            val isFreshLoading = true
            val genre = "Action"
            val limit = 10
            val movieDataMockList = buildList {
                repeat(limit) {
                    add(mockMovieData)
                }
            }
            val expectedMoviesList = movieDataMockList.map {
                movieDataToMovieMapper.map(it)
            }

            val remoteMovies: List<MovieData> = movieDataMockList

            // Mock remote data source behavior
            coEvery { moviesRemoteDataSource.getGenres() } returns Result.success(emptyList())
            coEvery { moviesRemoteDataSource.getMovies(genre, limit, 0) } returns Result.success(
                remoteMovies
            )

            // Mock local data source behavior
            coEvery { moviesLocalDataSource.cacheGenres(any()) } just runs
            coEvery { moviesLocalDataSource.clearMovies() } just runs
            coEvery { moviesLocalDataSource.cacheMovies(remoteMovies) } just runs
            coEvery { moviesLocalDataSource.getAllMovies() } returns remoteMovies

            // WHEN: getMovies is called
            val result = moviesRepository.getMovies(genre, limit, isFreshLoading)

            // TEST: Verify the logic for a fresh load
            coVerifyOrder {
                moviesRemoteDataSource.getGenres()
                moviesLocalDataSource.cacheGenres(any())
                moviesLocalDataSource.clearMovies()
                moviesRemoteDataSource.getMovies(genre, limit, 0)
                moviesLocalDataSource.cacheMovies(remoteMovies)
                moviesLocalDataSource.getAllMovies()
            }

            // VERIFY: Assert the final result is Success
            assertTrue(result is MoviesResult.Success)
            assertEquals(expectedMoviesList, (result as MoviesResult.Success).movies)
            assertFalse(
                "Last page should be false when remote movies equal limit",
                result.isLastPage
            )
        }

    @Test
    fun `getMovies on non-fresh load (pagination) - success - fetches with correct offset`() =
        runTest(testDispatcher) {
            // GIVEN: A non-fresh load (pagination)
            val isFreshLoading = false
            val genre = "Action"
            val limit = 10
            val initialCachedCount = 10
            val remoteMovies = listOf(mockMovieData)

            // Mock remote data source behavior (offset should be cached count + 1)
            coEvery {
                moviesRemoteDataSource.getMovies(
                    genre,
                    limit,
                    initialCachedCount + 1
                )
            } returns Result.success(remoteMovies)

            // Mock local data source behavior
            coEvery { moviesLocalDataSource.getCachedMoviesCount() } returns initialCachedCount
            coEvery { moviesLocalDataSource.cacheMovies(remoteMovies) } just runs
            coEvery { moviesLocalDataSource.getAllMovies() } returns remoteMovies

            // TEST: getMovies is called for pagination
            val result = moviesRepository.getMovies(genre, limit, isFreshLoading)

            // VERIFY: Verify the logic for pagination
            coVerify { moviesLocalDataSource.getCachedMoviesCount() }
            coVerify { moviesRemoteDataSource.getMovies(genre, limit, initialCachedCount + 1) }
            coVerify(exactly = 0) { moviesLocalDataSource.clearMovies() } // Ensure cache clearing was NOT called

            assertTrue(result is MoviesResult.Success)
        }

    @Test
    fun `getMovies on fresh load - remote returns fewer items than limit - isLastPage is true`() =
        runTest(testDispatcher) {
            // GIVEN: A fresh load where the remote source has no more data
            val isFreshLoading = true
            val limit = 10
            val remoteMovies = listOf(mockk<MovieData>(), mockk(), mockk()) // 3 items < limit

            coEvery { moviesRemoteDataSource.getGenres() } returns Result.success(emptyList())
            coEvery { moviesRemoteDataSource.getMovies(any(), limit, 0) } returns Result.success(
                remoteMovies
            )
            coEvery { moviesLocalDataSource.cacheGenres(any()) } just runs
            coEvery { moviesLocalDataSource.clearMovies() } just runs
            coEvery { moviesLocalDataSource.cacheMovies(any()) } just runs
            coEvery { moviesLocalDataSource.getAllMovies() } returns remoteMovies

            // TEST: getMovies is called
            val result = moviesRepository.getMovies(
                genre = null,
                limit = limit,
                isFreshLoading = isFreshLoading
            )

            // VERIFY: Assert the final result is Success and isLastPage is true
            assertTrue(result is MoviesResult.Success)
            assertTrue(
                "Last page should be true when remote returns fewer movies than the limit",
                (result as MoviesResult.Success).isLastPage
            )
        }

    @Test
    fun `getMovies when remote fetch fails - returns mapped error`() = runTest(testDispatcher) {
        // GIVEN: The remote data source will throw an exception
        val exception = IOException("Network Error")
        val expectedError = MoviesResult.MoviesError.NoInternetException

        coEvery { moviesRemoteDataSource.getGenres() } returns Result.failure(exception)
        every { moviesDataErrorMapper.map(exception) } returns expectedError

        // TEST: getMovies is called and the remote source fails
        val result = moviesRepository.getMovies(genre = null, limit = 10, isFreshLoading = true)

        // VERIFY: Verify that the error mapper was called and the correct error is returned
        verify { moviesDataErrorMapper.map(exception) }
        assertEquals(expectedError, result)
        coVerify(exactly = 0) { moviesLocalDataSource.cacheMovies(any()) } // Ensure caching was not attempted
    }

    // --- getGenres Tests ---

    @Test
    fun `getGenres - success - fetches from remote and returns mapped genres`() =
        runTest(testDispatcher) {
            // GIVEN
            val remoteGenres = listOf(mockGenreData)
            coEvery { moviesRemoteDataSource.getGenres() } returns Result.success(remoteGenres)
            every { genreDataToGenreMapper.map(mockGenreData) } returns mockGenre

            // TEST
            val result = moviesRepository.getGenres()

            // VERIFY
            coVerify { moviesRemoteDataSource.getGenres() }
            verify { genreDataToGenreMapper.map(mockGenreData) }
            assertTrue(result is GenreResult.Success)
            assertEquals(listOf(mockGenre), (result as GenreResult.Success).genres)
        }

    @Test
    fun `getGenres when remote fetch fails - returns mapped error`() = runTest(testDispatcher) {
        // GIVEN
        val exception = IOException("Network Error")
        val expectedError = GenreResult.GenreError.GenericError("Mapped Error")
        coEvery { moviesRemoteDataSource.getGenres() } returns Result.failure(exception)
        every { genreDataErrorMapper.map(exception) } returns expectedError

        // TEST
        val result = moviesRepository.getGenres()

        // VERIFY
        verify { genreDataErrorMapper.map(exception) }
        assertEquals(expectedError, result)
    }
}