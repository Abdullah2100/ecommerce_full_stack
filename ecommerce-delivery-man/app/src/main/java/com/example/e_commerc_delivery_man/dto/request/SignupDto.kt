package com.example.e_commerc_delivery_man.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SignupDto(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val deviceToken: String
)
