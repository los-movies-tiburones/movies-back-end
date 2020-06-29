package com.wfh.sharknet.dto

import com.wfh.sharknet.model.ProductionCompany
import com.wfh.sharknet.model.ProductionCountry
import com.wfh.sharknet.model.Rating
import com.wfh.sharknet.model.SpokenLanguage
import com.wfh.sharknet.model.Tag
import java.net.URI

data class MovieDescriptionDTO(
    val id: Int,
    val title: String,
    val year: String,
    val budget: Int,
    val cover: URI,
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
    val tags: Iterable<Tag>,
    val recommendations: Iterable<MovieDTO>
)