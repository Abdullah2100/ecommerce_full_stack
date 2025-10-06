package com.example.e_commerc_delivery_man.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AddressDto(
    val longitude: Double,
    val latitude: Double,
)

@Serializable
data class AddressWithTitleDto(
    val longitude: Double,
    val latitude: Double,
    val title: String?=null
)

@Serializable
data class CreateAddressDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID?,
    val longitude: Double?,
    val latitude: Double?,
    val title: String?
)

