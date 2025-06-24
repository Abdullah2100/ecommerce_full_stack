package com.example.e_commercompose.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class VerificationRequestDto(
    val email:String,
    val otp: String
)
