package com.example.eccomerce_app.dto.response

import com.example.e_commerc_delivery_man.dto.AddressDto
import com.example.e_commerc_delivery_man.dto.AddressWithTitleDto
import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderItemResponseDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val price: Double,
    val quanity:Int,
    val address: List<AddressWithTitleDto>?=null,
    val product: OrderProductResponseDto,
    val productVarient:List<OrderVarientResponseDto>?=null,
    val orderItemStatus: String
)

@Serializable()
data class OrderItemStatusChangeDto(
    @Serializable(with = UUIDKserialize::class)
    val orderId: UUID,
    @Serializable(with = UUIDKserialize::class)
    val orderItemId: UUID,
    val status: String
)

