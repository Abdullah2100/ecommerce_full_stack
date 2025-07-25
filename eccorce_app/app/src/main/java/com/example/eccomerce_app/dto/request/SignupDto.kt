package com.example.e_commercompose.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SignupDto(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val deviceToken: String
)
