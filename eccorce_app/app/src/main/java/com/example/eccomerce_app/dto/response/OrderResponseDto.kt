package com.example.e_commercompose.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderResponseDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val longitude: Double,
    val latitude: Double,
    val totalPrice: Double,
    val deliveryFee: Double,
    val userPhone: String,
    val status:Int,
    val orderItems:List<OrderItemResponseDto>


)
