package com.wfh.sharknet.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.net.URI
import java.time.Instant
import java.time.LocalDateTime

@Document
data class Movie(
    @Id
    val id: Int,
    val title: String,
    val year: String,
    val budget: Int,
    val cover: URI,
    val tmdbId: Int,
    val videoId: String?,
    var averageRating: Float,
    val overview: String,
    val runtime: Short,
    val genres: Set<String>,
    val ratings: MutableList<Rating>,
    var ratingSize: Short,
    val spokenLanguages: Iterable<SpokenLanguage>,
    val productionCountries: Iterable<ProductionCountry>,
    val productionCompanies: Iterable<ProductionCompany>,
    val tags: Iterable<Tag>,
    val reviews: MutableList<Review> = mutableListOf<Review>()
)

data class Review(
    val username: String,
    var text: String,
    var date: LocalDateTime
)

data class SpokenLanguage(
    val ISO_639_1: String,
    val name: String
)

data class Rating(
    val userId: Int? = null,
    val userIdString: String?,
    var value: Float,
    var timeStamp: String = Instant.now().epochSecond.toString()
)

data class ProductionCountry(
    val ISO_3166_1: String,
    val name: String
)

data class Tag(
    val userId: String,
    val value: String,
    val timeStamp: String
)

data class ProductionCompany(
    val name: String,
    val originCountry: String
)