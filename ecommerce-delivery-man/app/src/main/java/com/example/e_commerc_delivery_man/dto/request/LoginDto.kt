package com.example.e_commerc_delivery_man.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(
    val username: String,
    val password: String,
    val deviceToken: String
)
