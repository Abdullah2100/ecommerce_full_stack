package com.example.eccomerce_app.model

import java.util.UUID

data class Order(
    val id: UUID,
    val longitude: Double,
    val latitude: Double,
    val user_phone: String,
    val status:String,
    val userPhone: String,
    var name: String,
    val totalPrice: Double,
    val realPrice: Double?=null,
    val deliveryFee: Double,
    val orderItems:List<OrderItem>
)
