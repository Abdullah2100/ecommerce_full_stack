package com.example.eccomerce_app.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class VerificationRequestDto(
    val email:String,
    val otp: String
)
