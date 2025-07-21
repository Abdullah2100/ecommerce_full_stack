package com.example.eccomerce_app.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderVarientResponseDto(
    val varientName:String,
    val productVarientName:String
)
