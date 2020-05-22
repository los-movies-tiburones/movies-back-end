package com.wfh.sharknet.service

import com.wfh.sharknet.model.Movie
import com.wfh.sharknet.repository.MovieRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

interface MovieService {
    fun findByTitle(title: String, page: Int, size: Long): Flux<Movie>
    fun findAll(genres: List<String>, field: String, page: Int, size: Long): Flux<Movie>
}

@Service
class MovieServiceImp constructor(private val movieRepository: MovieRepository) : MovieService {

    override fun findByTitle(title: String, page: Int, size: Long): Flux<Movie> =
        movieRepository
            .findByTitleContainsIgnoreCase(title)
            .skip(page * size)
            .take(size)

    override fun findAll(genres: List<String>, field: String, page: Int, size: Long): Flux<Movie> {
        val fieldSort =
            if (field.length > 2 && field.startsWith("-"))
                Sort.Order.desc(field.substring(1))
            else
                Sort.Order.asc(field)

        return movieRepository.findAll(Sort.by(fieldSort))
                .filter { movie -> genres.all { movie.genres.contains(it) } }
                .skip(page * size)
                .take(size)
    }
}