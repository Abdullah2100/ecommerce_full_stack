package com.example.e_commerc_delivery_man.dto

import com.example.e_commerc_delivery_man.dto.UserDto
import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.io.File
import java.util.UUID

@Serializable
data class DeliveryDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    @Serializable(with = UUIDKserialize::class)
    val userId: UUID,
    val isAvailable: Boolean,
    val thumbnail: String? = null,
    val address: AddressDto,
    val user: UserDto,
    val analys:DeliveryAnalysDto?=null
)

@Serializable()
data class DeliveryAnalysDto(
    val dayFee: Double? = null,
    val weekFee: Double? = null,
    val monthFee: Double? = null,
    val dayOrder: Int? = null,
    val weekOrder: Int? = null
)


