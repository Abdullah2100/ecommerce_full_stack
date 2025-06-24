package com.example.e_commercompose.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderVarientResponseDto(
    val varient_name:String,
    val product_varient_name:String
)
