package com.wfh.sharknet.api

import com.wfh.sharknet.model.Movie
import com.wfh.sharknet.service.MovieService
import org.springframework.data.domain.PageRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.constraints.Max
import javax.validation.constraints.Positive
import javax.validation.constraints.PositiveOrZero
import javax.validation.constraints.Size

@Validated
@RestController
@RequestMapping("movies")
class MovieController constructor(private val movieService: MovieService) {
    
    @GetMapping(params = ["title"])
    fun findByTitle(
        @RequestParam title: String,
        @RequestParam(defaultValue = "0") @PositiveOrZero page: Int,
        @RequestParam(defaultValue = "10") @Positive @Max(100) size: Int
    ): Flux<Movie> = movieService.findByTitle(title, PageRequest.of(page, size))
    
    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "") @Size(max = 10) genres: List<String>,
        @RequestParam(defaultValue = "\"\"") sort: String,
        @RequestParam(defaultValue = "0") @PositiveOrZero page: Int,
        @RequestParam(defaultValue = "10") @Positive @Max(100) size: Int
    ): Flux<Movie> = movieService.findAll(genres, sort, PageRequest.of(page, size))
    
    @GetMapping("{id}")
    fun findById(@PathVariable id: Int): Mono<Movie> = movieService.findById(id)
    
    @GetMapping("genres")
    fun findAllGenres(): Mono<List<String>> = movieService.findAllGenres()
    
    @GetMapping("top-rating")
    fun findByTopRating(
        @RequestParam(defaultValue = "0") @PositiveOrZero page: Int,
        @RequestParam(defaultValue = "10") @Positive @Max(100) size: Int
    ): Flux<Movie> = movieService.findTopRating(PageRequest.of(page, size))
    
    @GetMapping("top-rating", params = ["genre"])
    fun topTenMoviesPerGenres(@RequestParam genre: String): Flux<Movie> =
        movieService.topTenMoviesPerGenres(genre)
    
}