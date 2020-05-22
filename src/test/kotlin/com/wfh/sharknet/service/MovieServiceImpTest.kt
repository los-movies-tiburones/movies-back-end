package com.wfh.sharknet.service

import com.wfh.sharknet.model.Movie
import com.wfh.sharknet.repository.MovieRepository
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.mock
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import java.util.UUID.randomUUID
import kotlin.random.Random
import org.springframework.data.domain.Sort


internal class MovieServiceImpTest {

    private val movieRepository: MovieRepository = mock(MovieRepository::class.java)

    @Test
    fun `it should show movies by title`() {
        val moviesFlux: Flux<Movie> = Flux.empty()
        Mockito.`when`(movieRepository.findByTitleContainsIgnoreCase(anyString())).thenReturn(moviesFlux)

        val movieService = MovieServiceImp(movieRepository)
        val expected = movieService.findByTitle(anyString(), 0, 10)
        assert(moviesFlux.toIterable().toList() == expected.toIterable().toList())
    }

    @Test
    fun `it should show movies with filters`() {
        val movies = generateMovies()
        val page = 0
        val size: Long = 10
        Mockito.`when`(movieRepository.findAll(any(Sort::class.java))).thenReturn(Flux.fromIterable(movies))

        val movieService = MovieServiceImp(movieRepository)
        val expected = movieService.findAll(listOf(), randomUUID().toString(), page, size).toIterable().toList()
        val actual = movies
                .toFlux()
                .skip(page * size)
                .take(size)
                .toIterable().toList()

        assert(actual == expected)
    }


    private fun generateMovies(): Iterable<Movie> {
        return (1..Random.nextInt(0, 10)).map {
            Movie(
                    id = randomUUID().toString(),
                    title = randomUUID().toString(),
                    year = randomUUID().toString(),
                    budget = Random.nextInt(),
                    cover = randomUUID().toString(),
                    tmdbId = randomUUID().toString(),
                    genres = setOf()
            )
        } .asIterable()
    }

}
