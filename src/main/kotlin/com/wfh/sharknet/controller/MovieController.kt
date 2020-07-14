package com.wfh.sharknet.controller

import arrow.core.getOrHandle
import com.wfh.sharknet.dto.MovieDTO
import com.wfh.sharknet.dto.MovieDescriptionDTO
import com.wfh.sharknet.model.MovieFavorite
import com.wfh.sharknet.model.Rating
import com.wfh.sharknet.model.Review
import com.wfh.sharknet.service.MovieService
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.validation.constraints.Max
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Positive
import javax.validation.constraints.PositiveOrZero
import javax.validation.constraints.Size

@Validated
@RestController
@RequestMapping("movies")
class MovieController constructor(private val movieService: MovieService) {
    
    @GetMapping(params = ["title"])
    @ApiOperation(value = "Find movies by title")
    fun findByTitle(
        @RequestParam title: String,
        @RequestParam(defaultValue = "0") @PositiveOrZero page: Int,
        @RequestParam(defaultValue = "10") @Positive @Max(1000) size: Int
    ): List<MovieDTO> = movieService.findByTitle(title, PageRequest.of(page, size))
    
    @GetMapping
    @ApiOperation(value = "Find movie all with optional filters")
    fun findAll(
        @RequestParam(defaultValue = "") @Size(max = 10) genres: Array<String>,
        @RequestParam(defaultValue = "\"\"") sort: String,
        @RequestParam(defaultValue = "0") @PositiveOrZero page: Int,
        @RequestParam(defaultValue = "10") @Positive @Max(1000) size: Int
    ): List<MovieDTO> = movieService.findAll(genres, sort, PageRequest.of(page, size))
    
    @GetMapping("{id}")
    @ApiOperation(value = "Find movie by id")
    fun findById(@PathVariable id: Int): MovieDescriptionDTO =
        movieService.findById(id).getOrHandle { throw it }
        
    @GetMapping("genres")
    @ApiOperation(value = "Find all genres available")
    fun findAllGenres(): List<String> = movieService.findAllGenres()
    
    @GetMapping("top-rating")
    @ApiOperation(value = "Find top 100 movies by average rating descending", notes = "Movies with less or equal 10 are not included")
    fun findByTopRating(
        @RequestParam(defaultValue = "0") @PositiveOrZero page: Int,
        @RequestParam(defaultValue = "10") @Positive @Max(100) size: Int
    ): List<MovieDTO> = movieService.findTopRating(PageRequest.of(page, size))
    
    @GetMapping("top-rating", params = ["genres"])
    @ApiOperation(value = "Find top 10 movies by genre and average rating descending", notes = "Only shows the first 10 movies per genre")
    fun findTopTenMoviesPerGenres(@RequestParam genres: Set<String>): Map<String, List<MovieDTO>?> =
        movieService.findTopTenMoviesPerGenres(genres)
    
    @GetMapping("recommendations")
    @ApiOperation(value = "Top 5 movies recommended")
    fun findRecommendations(authentication: Authentication): List<MovieDTO> =
        movieService.findTopRating(PageRequest.of(0, 5))
        
    @PostMapping("{id}/ratings")
    @ApiOperation(value = "Create new rating")
    fun saveRating(
        @PathVariable id: Int,
        @RequestBody @PositiveOrZero @Max(5) rating: Float,
        authentication: Authentication
    ) = movieService.saveRating(
            Rating(
                userIdString = authentication.principal.toString(),
                value = rating
            ),
            id
        )
    
    @PostMapping("{id}/reviews")
    @ApiOperation(value = "Create new review")
    fun saveReview(
        @PathVariable id: Int,
        @RequestBody @NotEmpty rating: String,
        authentication: Authentication
    ):Review = movieService.saveReview(
        id,
        Review(
            username = authentication.principal.toString(),
            text = rating,
            date = LocalDateTime.now()
        ))
        .getOrHandle { throw it }

    @GetMapping("/favorites")
    @ApiOperation(value = "Find all favorite movies")
    fun findFavorites(
        authentication: Authentication
    ): Set<MovieFavorite> = movieService.findFavorites(authentication.principal.toString())
    
    @PostMapping("{id}/favorites")
    @ApiOperation(value = "Find all favorite movies")
    fun saveFavorite(
        @PathVariable id: Int,
        authentication: Authentication
    ): MovieFavorite =
        movieService
            .saveFavorite(id, authentication.principal.toString())
            .getOrHandle { throw it }
    
    @DeleteMapping("{id}/favorites")
    @ApiOperation(value = "Find all favorite movies")
    fun findFavorites3(
        @PathVariable id: Int,
        authentication: Authentication
    ): Boolean =
        movieService
            .deleteFavorite(id, authentication.principal.toString())
            .getOrHandle { throw it }
}
    