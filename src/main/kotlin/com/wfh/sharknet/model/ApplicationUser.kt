package com.wfh.sharknet.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Document
data class ApplicationUser(
    @Id
    val id: String = "",
    @field:NotBlank
    @Indexed(unique = true)
    @field:Size(min = 4, max = 100)
    val username: String,
    @field:NotBlank
    val password: String
)