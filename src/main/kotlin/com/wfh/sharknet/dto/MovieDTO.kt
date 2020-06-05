package com.wfh.sharknet.dto

data class MovieDTO(
    val id: Int,
    val title: String,
    val year: String,
    val budget: Int,
    val cover: String,
    val averageRating: Float,
    val genres: Set<String>
)