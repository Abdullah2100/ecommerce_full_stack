package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class StoreDto(
    @Serializable(with = UUIDKserialize::class)
    val Id: UUID,
    @Serializable(with = UUIDKserialize::class)
    val UserId: UUID,
    val Name: String,
    val WallpaperImage: String,
    val SmallImage: String,
    val Longitude: Double,
    val Latitude: Double,
)

@Serializable
data class StoreStatusDto(
    @Serializable(with = UUIDKserialize::class)
    val StoreId: UUID,
    val Status: Boolean
)
