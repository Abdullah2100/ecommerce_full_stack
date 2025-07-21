package com.example.e_commerc_delivery_man.Dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderUpdateEvent(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    @Serializable(with = UUIDKserialize::class)
    val deliveryId: UUID,
)