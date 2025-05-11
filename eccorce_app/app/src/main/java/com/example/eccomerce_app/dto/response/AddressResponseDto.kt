package com.example.eccomerce_app.dto.response

import android.location.Location
import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AddressResponseDto(
    @Serializable(with = UUIDKserialize::class)
    var id: UUID,
    var longitude: Double,
    var latitude: Double,
    var title: String,
    var isCurrnt: Boolean?=false
)
