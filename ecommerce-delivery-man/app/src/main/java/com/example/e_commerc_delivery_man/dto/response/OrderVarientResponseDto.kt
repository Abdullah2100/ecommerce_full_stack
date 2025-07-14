package com.example.eccomerce_app.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderVarientResponseDto(
    val varient_name:String,
    val product_varient_name:String
)
