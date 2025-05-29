package com.example.eccomerce_app.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(
    val username: String,
    val password: String
)
