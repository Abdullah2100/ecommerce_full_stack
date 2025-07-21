package com.example.eccomerce_app.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderResponseDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val longitude: Double,
    val latitude: Double,
    val userPhone: String,
    var name: String,
    val status:Int,
    val totalPrice: Double,
    val realPrice: Double,
    val deliveryFee: Double,
    val orderItems:List<OrderItemResponseDto>


)
