package com.example.e_commercompose.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderItemResponseDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val price: Double,
    val quanity:Int,
    val product: OrderProductResponseDto,
    val productVarient:List<OrderVarientResponseDto>,
    val orderItemStatus: String

)
