package com.example.eccomerce_app.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SignupDto(
    var name: String,
    var phone: String,
    var email: String,
    var password: String
)
