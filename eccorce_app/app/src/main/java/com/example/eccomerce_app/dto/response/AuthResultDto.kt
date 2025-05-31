package com.example.e_commercompose.Dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResultDto(
    val accessToken: String="",
    val refreshToken: String = ""
)