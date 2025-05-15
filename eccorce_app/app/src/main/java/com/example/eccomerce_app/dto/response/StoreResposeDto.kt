package com.example.eccomerce_app.dto.response

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class StoreResposeDto(
    @Serializable(with = UUIDKserialize::class)
    var id: UUID,

    @Serializable(with = UUIDKserialize::class)
    var user_id: UUID,

    var name: String,
    var wallpaper_image: String,
    var small_image: String,

    )
