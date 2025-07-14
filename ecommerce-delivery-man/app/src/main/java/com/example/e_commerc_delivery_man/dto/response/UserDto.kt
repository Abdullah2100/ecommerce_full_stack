package com.example.e_commerc_delivery_man.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    @Serializable(with = UUIDKserialize::class)
    val userId: UUID,
    val isAvaliable: Boolean = true,
    val thumbnail: String? = null,
    val address: AddressResponseDto,
    val analys: DeliveryAnalysDto? = null
)
