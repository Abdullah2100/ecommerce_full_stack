package com.example.e_commerc_delivery_man.dto.response

import android.location.Location
import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AddressResponseDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val longitude: Double,
    val latitude: Double,
    val title: String?,
    val isCurrent: Boolean?=false
)
