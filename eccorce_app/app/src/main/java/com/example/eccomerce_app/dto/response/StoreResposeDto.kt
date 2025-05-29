package com.example.eccomerce_app.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class StoreResposeDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,

    @Serializable(with = UUIDKserialize::class)
    val user_id: UUID,

    val name: String,
    val wallpaper_image: String,
    val small_image: String,
    )
