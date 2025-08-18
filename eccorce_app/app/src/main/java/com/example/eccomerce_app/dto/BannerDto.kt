package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.io.File
import java.util.UUID

@Serializable
data class BannerDto(
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    val image: String,
    @Serializable(with = UUIDKserialize::class)
    val storeId: UUID,
)


data class CreateBannerDto(
    val Image: File,
    @Serializable(with = UUIDKserialize::class)
    val StoreId: UUID
)