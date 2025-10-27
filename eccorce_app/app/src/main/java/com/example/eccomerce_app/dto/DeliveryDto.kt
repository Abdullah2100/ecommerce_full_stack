package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DeliveryDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    @Serializable(with = UUIDKserialize::class)
    val userId: UUID,
    val isAvailable: Boolean,
    val thumbnail: String? = null,
    val address: AddressDto?=null,
    val user:DeliveryUserInfoDto
)
@Serializable
data class DeliveryUserInfoDto(
    val name: String,
    val phone: String,
    val email: String,
    val thumbnail: String?=null,
)

