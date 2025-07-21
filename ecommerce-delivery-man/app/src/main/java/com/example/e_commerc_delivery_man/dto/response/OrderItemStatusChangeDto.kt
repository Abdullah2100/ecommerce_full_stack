package com.example.e_commerc_delivery_man.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable()
data class OrderItemStatusChangeDto(
    @Serializable(with = UUIDKserialize::class)
    val orderId: UUID,
    @Serializable(with = UUIDKserialize::class)
    val orderItemId: UUID,
    val status: String
)
