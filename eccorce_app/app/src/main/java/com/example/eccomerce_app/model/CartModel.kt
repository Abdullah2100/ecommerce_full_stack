package com.example.e_commercompose.model

import java.util.UUID

data class CartModel(
    val totalPrice: Double,
    val longitude: Double,
    val latitude: Double,
    val userId: UUID,
    val cartProducts:List<CardProductModel>
)