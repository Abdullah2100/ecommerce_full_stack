package com.example.eccomerce_app.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID
@Serializable
data class BannerResponseDto(
    @Serializable(with= UUIDKserialize::class)
    val id: UUID,
    val image:String,
    @Serializable(with= UUIDKserialize::class)
    val store_id: UUID,
)
