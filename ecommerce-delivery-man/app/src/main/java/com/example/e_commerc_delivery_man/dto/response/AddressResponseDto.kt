package com.example.e_commerc_delivery_man.dto.response

import android.location.Location
import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AddressResponseDto(
    val longitude: Double,
    val latitude: Double,
)
