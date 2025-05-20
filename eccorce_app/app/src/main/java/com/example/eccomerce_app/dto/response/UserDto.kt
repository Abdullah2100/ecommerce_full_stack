package com.example.eccomerce_app.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserDto(
    @Serializable(with = UUIDKserialize::class)
    var id: UUID,
    var name:String,
    var phone: String,
    var email: String,
    var thumbnail:String,
    var address:List<AddressResponseDto>?=null,
    @Serializable(with = UUIDKserialize::class)
    var store_id: UUID,
)
