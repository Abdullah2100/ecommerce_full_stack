package com.example.e_commerc_delivery_man.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthDto(
    val Username: String,
    val Password: String,
    val DeviceToken: String
)

@Serializable
data class AuthResultDto(
    val accessToken: String="",
    val refreshToken: String = ""
)
