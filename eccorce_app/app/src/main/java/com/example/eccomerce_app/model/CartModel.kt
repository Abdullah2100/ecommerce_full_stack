package com.example.eccomerce_app.model

import java.util.UUID

data class CartModel(
    val totalPrice: Double,
    val longit: Double,
    val latitu: Double,
    val userId: UUID,
    val cartProducts:List<CardProductModel>
)