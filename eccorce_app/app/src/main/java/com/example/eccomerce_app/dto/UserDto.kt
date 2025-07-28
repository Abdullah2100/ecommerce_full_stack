package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserDto(
    @Serializable(with = UUIDKserialize::class)
    val Id: UUID,
    val Name:String,
    val Phone: String,
    val Email: String,
    val Thumbnail:String,
    val Address:List<AddressDto>?=null,
    @Serializable(with = UUIDKserialize::class)
    val StoreId: UUID?,
)