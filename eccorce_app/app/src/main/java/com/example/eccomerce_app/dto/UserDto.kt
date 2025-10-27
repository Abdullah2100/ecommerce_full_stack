package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
data class UserDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val name:String,
    val phone: String,
    val email: String,
    val thumbnail:String,
    val address: List<AddressDto>?=null,
    @Serializable(with = UUIDKserialize::class)
    val storeId: UUID?=null,
)
