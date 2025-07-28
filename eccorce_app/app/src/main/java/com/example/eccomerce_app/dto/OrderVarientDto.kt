package com.example.eccomerce_app.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderVarientDto(
    val VarientName:String,
    val ProductVarientName:String
)