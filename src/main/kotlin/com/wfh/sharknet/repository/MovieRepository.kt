package com.wfh.sharknet.repository

import com.wfh.sharknet.model.Movie
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Flux

interface MovieRepository: ReactiveSortingRepository<Movie, Int> {
    fun findByTitleContainsIgnoreCase(title: String): Flux<Movie>
}