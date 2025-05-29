package com.example.eccomerce_app.Dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResultDto(
    val accessToken: String="",
    val refreshToken: String = ""
)