package com.example.eccomerce_app.dto.request

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AddressRequestUpdateDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID?,
    val longitude: Double?,
    val latitude: Double?,
    val title: String?
)
