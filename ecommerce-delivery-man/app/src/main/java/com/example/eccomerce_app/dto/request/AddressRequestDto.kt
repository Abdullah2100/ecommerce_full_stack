package com.example.e_commerc_delivery_man.dto.request

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AddressRequestDto(
    val longitude: Double,
    val latitude: Double,
    val title: String
)
