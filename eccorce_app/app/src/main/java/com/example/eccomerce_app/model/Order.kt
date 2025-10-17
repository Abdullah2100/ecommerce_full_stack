package com.example.e_commercompose.model

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

data class Order(
    val id: UUID,
    val longitude: Double,
    val latitude: Double,
    val totalPrice: Double,
    val deliveryFee: Double,
    val userPhone: String,
    val status:String,
    val orderItems:List<OrderItem>


)
