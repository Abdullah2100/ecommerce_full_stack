package com.example.e_commerc_delivery_man.model

import com.example.e_commerc_delivery_man.dto.response.AddressResponseDto
import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

data class Delivery(
    val id: UUID,
    val userId: UUID,
    val isAvailable: Boolean,
    val thumbnail: String? = null,
    val address: Address,
    val user: UserModel
)
