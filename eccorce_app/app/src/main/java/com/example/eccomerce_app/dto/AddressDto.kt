package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AddressDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val longitude: Double,
    val latitude: Double,
    val title: String?,
    val isCurrent: Boolean?=false
)


@Serializable
data class CreateAddressDto(
    val Longitude: Double,
    val Latitude: Double,
    val Title: String?
)

@Serializable
data class UpdateAddressDto(
    @Serializable(with = UUIDKserialize::class)
    val Id: UUID?,
    val Longitude: Double?,
    val Latitude: Double?,
    val Title: String?
)

