package com.example.e_commercompose.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CartRequestDto(
    val longitude: Double,
    val latitude: Double,
    val totalPrice: Double,
    val items: List<OrderRequestItemsDto>,
)