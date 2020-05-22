package com.wfh.sharknet.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Movie (
    @Id
    val id: String,
    val title: String,
    val year: String,
    val budget: Int,
    val cover: String,
    val tmdbId: String,
    val genres: Set<String>
)