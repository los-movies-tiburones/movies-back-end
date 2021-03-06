package com.wfh.sharknet.service

import com.wfh.sharknet.dto.MovieDTO
import com.wfh.sharknet.dto.MovieDescriptionDTO
import com.wfh.sharknet.mapper.toMovieDTO
import com.wfh.sharknet.mapper.toMovieDescriptionDTO
import com.wfh.sharknet.model.Rating
import com.wfh.sharknet.repository.MovieRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import javax.annotation.PostConstruct

interface MovieService {
    fun findByTitle(title: String, pageRequest: PageRequest): List<MovieDTO>
    fun findAll(genres: Array<String>, field: String, pageRequest: PageRequest): List<MovieDTO>
    fun findById(id: Int): MovieDescriptionDTO
    fun findTopRating(pageRequest: PageRequest): List<MovieDTO>
    fun findAllGenres(): List<String>
    fun findTopTenMoviesPerGenres(genres: Set<String>): Map<String, List<MovieDTO>?>
    fun saveRating(rating: Rating, movieId: Int)
}

@Service
class MovieServiceImp constructor(private val movieRepository: MovieRepository): MovieService {
    private val log = LoggerFactory.getLogger(javaClass)
    
    private lateinit var genres: List<String>
    private lateinit var topMovies: Map<String, List<MovieDTO>>
    
    @PostConstruct
    fun loadCache() {
        log.info("Loading cache...")
        topMovies = movieRepository.findAll()
            .filter { it.ratingSize > 10 }
            .flatMap { movie -> movie.genres.map { it to movie.toMovieDTO() } }
            .groupBy({ it.first }, { it.second })
            .mapValues { entry ->
                entry
                    .value
                    .sortedByDescending { it.averageRating }
                    .take(10)
            }
        genres = topMovies.keys.toList().sorted()
        log.info("Cache loaded")
    }

    override fun findByTitle(title: String, pageRequest: PageRequest): List<MovieDTO> =
        movieRepository.findByTitleContainsIgnoreCase(title, pageRequest)

    override fun findAll(genres: Array<String>, field: String, pageRequest: PageRequest): List<MovieDTO> {
        val fieldSort =
            if (field.length > 2 && field.startsWith("-"))
                Sort.Order.desc(field.substring(1))
            else
                Sort.Order.asc(field)
        
        val movies = if (genres.isEmpty()) {
            movieRepository
                .findAll(PageRequest.of(pageRequest.pageNumber, pageRequest.pageSize, Sort.by(fieldSort)))
                .toList()
                .map { it.toMovieDTO() }
        } else {
            movieRepository.findAnyOfTheseGenres(
                genres,
                PageRequest.of(pageRequest.pageNumber, pageRequest.pageSize, Sort.by(fieldSort))
            )
        }
        
        if (movies.isEmpty()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        } else {
            return movies
        }
    }

    override fun findById(id: Int): MovieDescriptionDTO {
        val movie = movieRepository.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        val recommendations = if (movie.genres.isEmpty()) {
            findTopRating(PageRequest.of(0, 5))
        } else {
            findTopTenMoviesPerGenres(movie.genres)
                .flatMap { it.value?.asIterable() ?: emptyList() }
                .distinctBy { it.id }
                .filter { it.id != movie.id }
                .sortedByDescending { it.averageRating }
                .take(10)
        }
        
        return movie.toMovieDescriptionDTO(recommendations)
    }
    
    
    @Cacheable(value = ["MovieServiceImp.findTopRating"], condition = "(#pageRequest.pageNumber + 1) * #pageRequest.pageSize <= 100")
    override fun findTopRating(pageRequest: PageRequest): List<MovieDTO> =
        movieRepository.findByRatingSizeGreaterThanOrderByAverageRatingDesc(10, pageRequest)
    
    @Cacheable("MovieServiceImp.findAllGenres")
    override fun findAllGenres(): List<String> = genres
    
    @Cacheable(value = ["MovieServiceImp.findTopTenMoviesPerGenres"])
    override fun findTopTenMoviesPerGenres(genres: Set<String>): Map<String, List<MovieDTO>?> =
        topMovies.keys.filter { genres.contains(it) }
            .map { it to topMovies[it] }
            .toMap()
    
    override fun saveRating(rating: Rating, movieId: Int) {
        val movie = movieRepository
            .findById(movieId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        val currentRating = movie.ratings.find { it.userIdString == rating.userIdString }
        if (currentRating != null) {
            currentRating.value = rating.value
            currentRating.timeStamp = Instant.now().epochSecond.toString()
        } else {
            movie.ratings.add(rating)
            movie.ratingSize = movie.ratings.size.toShort()
            movie.averageRating = movie.ratings.map { it.value }.average().toFloat()
        }
        movie.ratings.sortWith(Comparator { a, b -> a.timeStamp.toInt() - b.timeStamp.toInt() })
        movieRepository.save(movie)
    }
}