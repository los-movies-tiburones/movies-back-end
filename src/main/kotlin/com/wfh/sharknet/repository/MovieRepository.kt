package com.wfh.sharknet.repository

import com.wfh.sharknet.dto.MovieDTO
import com.wfh.sharknet.model.Movie
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import java.util.*


interface MovieRepository: PagingAndSortingRepository<Movie, Int> {
    
    fun findFirstByTitle(title: String): Optional<MovieDTO>
    
    fun findByTitleContainsIgnoreCase(title: String, pageable: Pageable): List<MovieDTO>
    
    fun findByRatingSizeGreaterThanOrderByAverageRatingDesc(size: Int, pageable: Pageable): List<MovieDTO>
    
    @Query(value = "{ 'genres' : {\$all : [?0] }}")
    fun findAnyOfTheseGenres(@Param("genres") genres: Array<String>, pageable: Pageable): List<MovieDTO>
    
}