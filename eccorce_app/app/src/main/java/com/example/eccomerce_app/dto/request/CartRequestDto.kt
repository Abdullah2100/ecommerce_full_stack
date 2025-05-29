package com.example.eccomerce_app.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CartRequestDto(
    val longitude: Double,
    val latitude: Double,
    val totalPrice: Double,
    val items: List<OrderRequestItemsDto>,
)