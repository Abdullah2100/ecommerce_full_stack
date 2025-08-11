package com.example.eccomerce_app.dto

import com.example.hotel_mobile.services.kSerializeChanger.UUIDKserialize
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SubCategoryDto(
    val Name: String,
    @Serializable(with = UUIDKserialize::class)
    val Id: UUID,
    @Serializable(with = UUIDKserialize::class)
    val CategoryId: UUID,
)


@Serializable
data class UpdateSubCategoryDto(
    val Name: String,
    @Serializable(with = UUIDKserialize::class)
    val Id: UUID,
    @Serializable(with = UUIDKserialize::class)
    val CateogyId: UUID
)

@Serializable
data class CreateSubCategoryDto(
    val Name: String,
    @Serializable(with = UUIDKserialize::class)
    val CateogyId: UUID
)

