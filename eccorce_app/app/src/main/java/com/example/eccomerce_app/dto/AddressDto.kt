package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AddressDto(
    @Serializable(with = UUIDKserialize::class)
    val Id: UUID,
    val Lngitude: Double,
    val Latitude: Double,
    val Title: String?,
    val IsCurrent: Boolean?=false
)


@Serializable
data class CreateAddressDto(
    val Longitude: Double,
    val Latitude: Double,
    val Title: String
)

@Serializable
data class UpdateAddressDto(
    @Serializable(with = UUIDKserialize::class)
    val Id: UUID?,
    val Longitude: Double?,
    val Latitude: Double?,
    val Title: String?
)

