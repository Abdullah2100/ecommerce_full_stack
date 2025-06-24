package com.example.e_commercompose.dto.request

import com.example.hotel_mobile.Modle.NetworkCallHandler
import kotlinx.serialization.Serializable

@Serializable
data class ReseatPasswordRequestDto(
    val email: String,
    val otp: String,
    val password: String
)
