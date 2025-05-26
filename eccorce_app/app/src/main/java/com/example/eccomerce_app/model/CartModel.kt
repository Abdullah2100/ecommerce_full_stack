package com.example.eccomerce_app.model

import java.util.UUID

data class CartModel(
    val totalPrice: Double?=null,
    val longit: Double?=null,
    val latitu: Double?=null,
    val userId: UUID?=null,
    var cartProducts:List<CardProductModel> = emptyList<CardProductModel>()
)