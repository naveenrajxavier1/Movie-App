package com.troweprice.moviesdomain

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val MOVIES_PAGE_COUNT = 10

class GetMoviesTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var moviesRepository: IMoviesRepository

    private lateinit var getMovies: GetMovies

    @Before
    fun setUp() {
        getMovies = GetMovies(moviesRepository)
    }

    @Test
    fun `invoke() Given genre and fresh loading Then returns MoviesResult from repository`() =
        runTest {
            // GIVEN
            val genre = "Action"
            val isFreshLoading = true
            val expectedResult: MoviesResult = mockk() // A mock result for verification

            // Set up the mock repository to return our expected result when called with the correct arguments.
            coEvery {
                moviesRepository.getMovies(
                    genre = genre,
                    limit = MOVIES_PAGE_COUNT,
                    isFreshLoading = isFreshLoading
                )
            } returns expectedResult

            // WHEN
            val actualResult = getMovies(isFreshLoading = isFreshLoading, genre = genre)

            // THEN
            coVerify(exactly = 1) {
                moviesRepository.getMovies(
                    genre = genre,
                    limit = MOVIES_PAGE_COUNT,
                    isFreshLoading = isFreshLoading
                )
            }

            // VERIFY
            assertEquals(expectedResult, actualResult)
        }

    @Test
    fun `invoke() Given without genre and not fresh loading Then returns MoviesResult from repository`() =
        runTest {
            // GIVEN
            val isFreshLoading = false
            val expectedResult: MoviesResult = mockk()
            coEvery {
                moviesRepository.getMovies(
                    genre = null,
                    limit = MOVIES_PAGE_COUNT,
                    isFreshLoading = isFreshLoading
                )
            } returns expectedResult

            // TEST
            val actualResult =
                getMovies(isFreshLoading = isFreshLoading) // genre is defaulted to null

            // VERIFY
            coVerify(exactly = 1) {
                moviesRepository.getMovies(
                    genre = null,
                    limit = MOVIES_PAGE_COUNT,
                    isFreshLoading = isFreshLoading
                )
            }
            assertEquals(expectedResult, actualResult)
        }
}