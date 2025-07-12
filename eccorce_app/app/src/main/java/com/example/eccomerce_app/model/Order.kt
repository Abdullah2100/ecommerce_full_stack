package com.example.e_commercompose.model

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Order(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val longitude: Double,
    val latitude: Double,
    val totalPrice: Double,
    val deliveryFee: Double,
    val user_phone: String,
    val status:Int,
    val order_items:List<OrderItem>


)
