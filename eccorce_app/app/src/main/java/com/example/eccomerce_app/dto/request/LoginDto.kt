package com.example.eccomerce_app.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(
    var username: String,
    var password: String
)
