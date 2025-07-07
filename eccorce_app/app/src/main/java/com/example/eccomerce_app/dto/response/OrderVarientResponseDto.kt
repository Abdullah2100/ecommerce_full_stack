package com.example.e_commercompose.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderVarientResponseDto(
    val varientName:String,
    val productVarientName:String
)
