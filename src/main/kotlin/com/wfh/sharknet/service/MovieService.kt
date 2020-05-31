package com.wfh.sharknet.service

import com.wfh.sharknet.model.Movie
import com.wfh.sharknet.repository.MovieRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

interface MovieService {
    fun findByTitle(title: String, pageRequest: PageRequest): Flux<Movie>
    fun findAll(genres: List<String>, field: String, pageRequest: PageRequest): Flux<Movie>
    fun findById(id: Int): Mono<Movie>
    fun findTopRating(pageRequest: PageRequest): Flux<Movie>
    fun findAllGenres(): Mono<List<String>>
    fun topTenMoviesPerGenres(genre: String): Flux<Movie>
}

@Service
class MovieServiceImp constructor(private val movieRepository: MovieRepository) : MovieService {
    
    override fun findByTitle(title: String, pageRequest: PageRequest): Flux<Movie> =
        movieRepository
            .findByTitleContainsIgnoreCase(title)
            .skip((pageRequest.pageNumber * pageRequest.pageSize).toLong())
            .take(pageRequest.pageSize.toLong())
    
    override fun findAll(genres: List<String>, field: String, pageRequest: PageRequest): Flux<Movie> {
        val fieldSort =
            if (field.length > 2 && field.startsWith("-"))
                Sort.Order.desc(field.substring(1))
            else
                Sort.Order.asc(field)
        
        return movieRepository.findAll(Sort.by(fieldSort))
            .filter { movie -> genres.all { movie.genres.contains(it) } }
            .skip((pageRequest.pageNumber * pageRequest.pageSize).toLong())
            .take(pageRequest.pageSize.toLong())
    }
    
    override fun findById(id: Int): Mono<Movie> =
        movieRepository
            .existsById(id)
            .flatMap { isPresent ->
                if (isPresent) movieRepository.findById(id)
                else Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND))
            }
    
    @Cacheable(value = ["MovieServiceImp.findTopRating"], condition = "(#pageRequest.pageNumber + 1) * #pageRequest.pageSize <= 100")
    override fun findTopRating(pageRequest: PageRequest): Flux<Movie> =
        movieRepository
            .findAll(Sort.by(Sort.Order.desc("averageRating")))
            .skip((pageRequest.pageNumber * pageRequest.pageSize).toLong())
            .take(pageRequest.pageSize.toLong())
            .cache()
    
    
    @Cacheable("MovieServiceImp.findAllGenres")
    override fun findAllGenres(): Mono<List<String>> =
        movieRepository.findAll()
            .flatMap { it.genres.toFlux() }
            .distinct()
            .collectSortedList()
            .cache()
    
    @Cacheable("MovieServiceImp.topTenMoviesPerGenres")
    override fun topTenMoviesPerGenres(genre: String): Flux<Movie> {
        return movieRepository
            .findAll(Sort.by(Sort.Order.desc("averageRating")))
            .filter { it.genres.contains(genre) }
            .take(10)
            .cache()
    }
}