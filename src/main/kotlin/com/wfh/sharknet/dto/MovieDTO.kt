package com.wfh.sharknet.dto

import java.net.URI

data class MovieDTO(
    val id: Int,
    val title: String,
    val year: String,
    val budget: Int,
    val cover: URI,
    val averageRating: Float,
    val genres: Set<String>
)