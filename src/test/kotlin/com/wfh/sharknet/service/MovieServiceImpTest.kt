package com.wfh.sharknet.service

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.wfh.sharknet.dto.MovieDTO
import com.wfh.sharknet.model.Movie
import com.wfh.sharknet.repository.MovieRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.server.ResponseStatusException
import java.util.Optional
import java.util.UUID.randomUUID
import kotlin.random.Random

internal class MovieServiceImpTest {
    
    private val movieRepository: MovieRepository = mock(MovieRepository::class.java)
    
    @Test
    fun `it should show movies by title`() {
        val movies: List<MovieDTO> = listOf()
        val pagination = PageRequest.of(0, 10)
        val title = "Action"
        
        Mockito.`when`(movieRepository.findByTitleContainsIgnoreCase(anyString(), any())).thenReturn(movies)
        
        val movieService = MovieServiceImp(movieRepository)
        val expected = movieService.findByTitle(title, pagination)
        
        assert(movies == expected)
    }
    
    @Test
    fun `it should find 100 TopRating`() {
        val movies: List<MovieDTO> = arrayListOf()
        val pagination = PageRequest.of(0, 10)
        
        Mockito.`when`(movieRepository.findByRatingSizeGreaterThanOrderByAverageRatingDesc(eq(10), any())).thenReturn(movies)
        
        val movieService = MovieServiceImp(movieRepository)
        val expected = movieService.findTopRating(pagination)
        
        assert(movies == expected)
    }
    
    
    @Test
    fun `it should find a movie by id`() {
        val movie: Optional<Movie> = Optional.empty()
        val id = Random.nextInt(0, 100)
        
        Mockito.`when`(movieRepository.findById(eq(id))).thenReturn(movie)
        val movieService = MovieServiceImp(movieRepository)
        
        assertThrows<ResponseStatusException> { movieService.findById(id) }
    }
    
    
    @Test
    fun `it should show all movies with filters`() {
        val movies = mock(Page::class.java) as Page<*>?
        val randomField = randomUUID().toString()
        val pagination = PageRequest.of(0, 10, Sort.by(randomField))
        Mockito.`when`(movieRepository.findAll(eq(pagination))).thenReturn(movies as Page<Movie>?)
        
        val movieService = MovieServiceImp(movieRepository)
        assertThrows<ResponseStatusException> { movieService.findAll(arrayOf(), randomField, pagination) }
    }
    
    @Test
    fun `it should find top ten movies per genres`() {
        val genres: Set<String> = setOf("Action", "Drama")
        val actual: Map<String, List<MovieDTO>?> = mapOf( "Action" to listOf() )
        
        val movieService = MovieServiceImp(movieRepository)
        ReflectionTestUtils.setField(movieService, "topMovies", actual)
    
        val expected = movieService.findTopTenMoviesPerGenres(genres)
        assert(actual == expected)
    }
    
}
