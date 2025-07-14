package com.example.e_commerc_delivery_man.Dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResultDto(
    val accessToken: String="",
    val refreshToken: String = ""
)