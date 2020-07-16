package com.wfh.sharknet.service

import arrow.core.Either
import arrow.core.extensions.set.foldable.find
import com.wfh.sharknet.dto.MovieDTO
import com.wfh.sharknet.dto.MovieDescriptionDTO
import com.wfh.sharknet.mapper.toMovieDTO
import com.wfh.sharknet.mapper.toMovieDescriptionDTO
import com.wfh.sharknet.model.MovieFavorite
import com.wfh.sharknet.model.Rating
import com.wfh.sharknet.model.Review
import com.wfh.sharknet.repository.ApplicationUserRepository
import com.wfh.sharknet.repository.MovieRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.LocalDateTime
import javax.annotation.PostConstruct

interface MovieService {
    fun findByTitle(title: String, pageRequest: PageRequest): List<MovieDTO>
    fun findAll(genres: Array<String>, field: String, pageRequest: PageRequest): List<MovieDTO>
    fun findById(id: Int, username: String): Either<Throwable, MovieDescriptionDTO>
    fun findTopRating(pageRequest: PageRequest): List<MovieDTO>
    fun findAllGenres(): List<String>
    fun findTopTenMoviesPerGenres(genres: Set<String>): Map<String, List<MovieDTO>?>
    fun saveRating(rating: Rating, movieId: Int)
    fun saveReview(id: Int, review: Review): Either<Throwable, Review>
    fun findFavorites(username: String): List<MovieDTO>
    fun saveFavorite(id: Int, username: String): Either<Throwable, MovieFavorite>
    fun deleteFavorite(id: Int, username: String): Either<Throwable, Boolean>
}

@Service
class MovieServiceImp constructor(
    private val movieRepository: MovieRepository,
    private val restTemplate: RestTemplate,
    private val applicationUserRepository: ApplicationUserRepository
): MovieService {
    private val log = LoggerFactory.getLogger(javaClass)
    
    @Value("\${system.recommendations.movie.related}")
    private lateinit var urlMovieRecommendations: String
    
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

    override fun findById(id: Int, username: String): Either<ResponseStatusException, MovieDescriptionDTO> {
        val optionMovie = movieRepository.findById(id)
        return if (!optionMovie.isPresent) {
            Either.left(ResponseStatusException(HttpStatus.NOT_FOUND))
        } else {
            val movie = optionMovie.get()
            val recommendations: List<MovieDTO> = if (movie.genres.isEmpty()) {
                findTopRating(PageRequest.of(0, 5))
            } else {
                restTemplate
                    .getForObject<List<String>>(urlMovieRecommendations, movie.title)
                    .map { movieRepository.findFirstByTitle(it).get() }
            }
            val isInMyList = applicationUserRepository.findByUsername(username)
                ?.moviesFavorites?.any { it.id == movie.id }
                
            val movieReturned = movie.toMovieDescriptionDTO(recommendations).copy(isInMyList = isInMyList ?: false)
            Either.right(movieReturned)
        }
        
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
    
    override fun saveReview(id: Int, review: Review): Either<ResponseStatusException, Review> {
        val optionMovie = movieRepository.findById(id)
        return if (!optionMovie.isPresent) {
            Either.left(ResponseStatusException(HttpStatus.NOT_FOUND))
        } else {
            val movie = optionMovie.get()
            val currentReview = movie.reviews.find { it.username == review.username }
            if (currentReview != null) {
                currentReview.date = LocalDateTime.now()
                currentReview.text = review.text
            } else {
                movie.reviews.add(review)
            }
            movieRepository.save(movie)
            Either.right(review)
        }
    }
    
    override fun findFavorites(username: String): List<MovieDTO> {
        val user = applicationUserRepository.findByUsername(username)
        return user?.moviesFavorites?.map {
            movieRepository
                .findById(it.id)
                .get()
                .toMovieDTO()
        } ?: throw UsernameNotFoundException(username)
    }
    
    override fun saveFavorite(id: Int, username: String): Either<Throwable, MovieFavorite> {
        val movie = movieRepository.findById(id)
        val optionMovie = movieRepository.findById(id)
        return if (!optionMovie.isPresent) {
            Either.left(ResponseStatusException(HttpStatus.NOT_FOUND))
        } else {
            val user = applicationUserRepository.findByUsername(username)
            return if (user != null) {
                val movieFavoriteCurrent = user.moviesFavorites.find { it.id == id }
                return if (movieFavoriteCurrent.isEmpty()) {
                    val movieFavorite = MovieFavorite(
                        id = id,
                        added = LocalDateTime.now()
                    )
                    val newSet = user.moviesFavorites.toMutableSet()
                    newSet.add(movieFavorite)
                    val newUser = user.copy(
                        moviesFavorites = newSet.toSet()
                    )
                    applicationUserRepository.save(newUser)
                    Either.right(movieFavorite)
                } else {
                    Either.left(ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "movie is already added"))
                }
            } else {
                Either.left(UsernameNotFoundException(username))
            }
        }
    }
    
    override fun deleteFavorite(id: Int, username: String): Either<Throwable, Boolean> {
        val movie = movieRepository.findById(id)
        val optionMovie = movieRepository.findById(id)
        return if (!optionMovie.isPresent) {
            Either.left(ResponseStatusException(HttpStatus.NOT_FOUND))
        } else {
            val user = applicationUserRepository.findByUsername(username)
            return if (user != null) {
                val newSet = user.moviesFavorites.toMutableSet()
                val removed = newSet.removeIf { it.id == id }
                return if (removed) {
                    val newUser = user.copy(
                        moviesFavorites = newSet.toSet()
                    )
                    applicationUserRepository.save(newUser)
                    Either.right(removed)
                } else {
                    Either.left(ResponseStatusException(HttpStatus.NOT_FOUND))
                }
            } else {
                Either.left(UsernameNotFoundException(username))
            }
        }
    }
}