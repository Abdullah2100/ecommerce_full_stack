package com.example.e_commercompose.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(
    val username: String,
    val password: String,
    val deviceToken: String
)
