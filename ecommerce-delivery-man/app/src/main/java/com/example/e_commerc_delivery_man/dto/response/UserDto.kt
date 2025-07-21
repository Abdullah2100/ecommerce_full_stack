package com.example.e_commerc_delivery_man.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserDto(
    val name: String,
    val phone: String,
    val email: String,
    val thumbnail: String?=null,
)
