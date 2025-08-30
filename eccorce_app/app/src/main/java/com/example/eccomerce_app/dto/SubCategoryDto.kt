package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SubCategoryDto(
    val name: String,
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    @Serializable(with = UUIDKserialize::class)
    val storeId: UUID,
    @Serializable(with = UUIDKserialize::class)
    val categoryId: UUID,
)


@Serializable
data class UpdateSubCategoryDto(
    val name: String,
    @Serializable(with = UUIDKserialize::class)
    val id: UUID,
    @Serializable(with = UUIDKserialize::class)
    val categoryId: UUID
)

@Serializable
data class CreateSubCategoryDto(
    val Name: String,
    @Serializable(with = UUIDKserialize::class)
    val CategoryId: UUID
)

