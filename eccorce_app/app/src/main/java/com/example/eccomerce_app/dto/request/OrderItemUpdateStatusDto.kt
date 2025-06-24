package com.example.e_commercompose.dto.request

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderItemUpdateStatusDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val status:Int
)
