package com.example.e_commerc_delivery_man.dto.response

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
    val address:List<AddressResponseDto>?=null,
    @Serializable(with = UUIDKserialize::class)
    val store_id: UUID,
)
