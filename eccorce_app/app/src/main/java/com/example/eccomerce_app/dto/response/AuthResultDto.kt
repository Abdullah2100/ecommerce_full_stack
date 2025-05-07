package com.example.eccomerce_app.Dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResultDto(
    var accessToken: String="",
    var refreshToken: String = ""
)