package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.io.File
import java.util.UUID

@Serializable
data class StoreDto(
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

@Serializable
data class StoreStatusDto(
    @Serializable(with = UUIDKserialize::class)
    val StoreId: UUID,
    val Status: Boolean
)

data class CreateStoreDto(
    val name: String?="سوق سعيد",
    val wallpaperImage: File?=null,
    val smallImage: File?=null,
    val longitude: Double?=null,
    val latitude: Double?=null
)
