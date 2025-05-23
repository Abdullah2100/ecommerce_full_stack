package com.example.eccomerce_app.dto.request

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class LocationRequestDto(
    @Serializable(with = UUIDKserialize::class)
    var id: UUID?=null,
    var longitude: Double,
    var latitude: Double,
    var title: String
)
