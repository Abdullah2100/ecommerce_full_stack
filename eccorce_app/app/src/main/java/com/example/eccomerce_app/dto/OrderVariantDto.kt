package com.example.eccomerce_app.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderVariantDto(
    val variantName:String,
    val productVariantName:String
)