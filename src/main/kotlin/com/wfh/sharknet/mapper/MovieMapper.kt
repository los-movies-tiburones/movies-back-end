package com.wfh.sharknet.mapper

import com.wfh.sharknet.dto.MovieDTO
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