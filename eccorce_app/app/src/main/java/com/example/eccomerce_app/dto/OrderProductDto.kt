package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OrderProductDto(
    @Serializable(with = UUIDKserialize::class)
    val Id: UUID,
    @Serializable(with = UUIDKserialize::class)
    val StoreId: UUID,
    val Name: String,
    val Thmbnail: String
)