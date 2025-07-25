package com.example.e_commerc_delivery_man.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DeliveryInfoDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    @Serializable(with = UUIDKserialize::class)
    val userId: UUID,
    val isAvaliable: Boolean,
    val thumbnail: String? = null,
    val address: AddressResponseDto,
    val analys: DeliveryAnalysDto,
    val user: UserDto
)
