package com.wfh.sharknet.mapper

import com.wfh.sharknet.dto.MovieDTO
import com.wfh.sharknet.dto.MovieDescriptionDTO
import com.wfh.sharknet.model.Movie

fun Movie.toMovieDTO(): MovieDTO =
    MovieDTO(
        id = this.id,
        title = this.title,
        year = this.year,
        budget = this.budget,
        cover = this.cover,
        averageRating = this.averageRating,
        genres = this.genres
    )

fun Movie.toMovieDescriptionDTO(recommendations: Iterable<MovieDTO>): MovieDescriptionDTO =
    MovieDescriptionDTO(
        id = this.id,
        title = this.title,
        year = this.year,
        budget = this.budget,
        cover = this.cover,
        tmdbId = this.tmdbId,
        averageRating = this.averageRating,
        overview = this.overview,
        runtime = this.runtime,
        genres = this.genres,
        ratings = this.ratings,
        ratingSize = this.ratingSize,
        spokenLanguages = this.spokenLanguages,
        productionCountries = this.productionCountries,
        productionCompanies = this.productionCompanies,
        tags = this.tags,
        recommendations = recommendations
    )