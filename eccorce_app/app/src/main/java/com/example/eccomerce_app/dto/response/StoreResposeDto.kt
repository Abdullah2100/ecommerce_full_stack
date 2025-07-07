package com.example.e_commercompose.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class StoreResposeDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,

    @Serializable(with = UUIDKserialize::class)
    val userId: UUID,

    val name: String,
    val wallpaperImage: String,
    val smallImage: String,
    val longitude: Double,
    val latitude: Double,
)
