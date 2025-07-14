package com.example.e_commerc_delivery_man.model

import java.util.UUID

data class CartModel(
    val totalPrice: Double,
    val longit: Double,
    val latitu: Double,
    val userId: UUID,
    val cartProducts:List<CardProductModel>
)