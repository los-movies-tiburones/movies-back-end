package com.wfh.sharknet.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.sql.Timestamp

@Document
data class Movie(
    @Id
    val id: Int,
    val title: String,
    val year: String,
    val budget: Int,
    val cover: String,
    val tmdbId: Int,
    val averageRating: Float,
    val overview: String,
    val runtime: Short,
    val genres: Set<String>,
    val ratings: List<Rating>,
    val ratingSize: Short,
    val spokenLanguages: Iterable<SpokenLanguage>,
    val productionCountries: Iterable<ProductionCountry>,
    val productionCompanies: Iterable<ProductionCompany>,
    val tags: Iterable<Tag>
)

data class SpokenLanguage(
    val ISO_639_1: String,
    val name: String
)

data class Rating(
    val userId: Int,
    val value: Float,
    val timeStamp: String
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