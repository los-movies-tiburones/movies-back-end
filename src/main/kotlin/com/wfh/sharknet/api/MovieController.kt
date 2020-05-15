package com.wfh.sharknet.api

import com.wfh.sharknet.model.Movie
import com.wfh.sharknet.service.MovieService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("movies")
class MovieController constructor(private val movieService: MovieService) {

    @GetMapping(params = ["title"])
    fun findByTitle(
        @RequestParam title: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Long
    ): Flux<Movie> = movieService.findByTitle(title, page, size)

    @GetMapping
    fun findAll(
        @RequestParam(defaultValue = "") genres: List<String>,
        @RequestParam(defaultValue = "") sort: Array<String>,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Long
    ): Flux<Movie> = movieService.findAll(genres, sort, page, size)
}